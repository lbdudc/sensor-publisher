/*% if (feature.MWM_PM_Employees) { %*/
<template>
  <v-container v-if="!isLoading">
    <v-card>
      <v-card-title>
        <v-row align="center" justify="space-between" no-gutters>
          <v-col class="d-none d-md-block">
            <span class="headline no-split-words">
              {{ $t($route.meta.label) }}
            </span>
          </v-col>
          <v-col class="text-right">
            <v-btn @click="back()">
              <v-icon>mdi-arrow-left</v-icon>
              <span class="d-none d-sm-block"> {{ $t("cancel") }} </span>
            </v-btn>
            <v-btn class="success ml-2" @click="save(employee, $route.params.backPrevious)" :disabled="!validForm">
              <v-icon>save</v-icon>
              <span class="d-none d-sm-block"> {{ $t("save") }} </span>
            </v-btn>
          </v-col>
        </v-row>
      </v-card-title>
      <v-card-text>
        <v-form v-model="validForm">
          <v-row dense>
            <v-col cols="12" md="6">
              <v-text-field
                dense
                v-model="employee.fullName"
                type="text"
                :label="$t('staff_crud.fullName')"
                :rules="[v => !!v || $t('staff_crud.required')]"
              ></v-text-field>

              <v-text-field
                dense
                v-model="employee.email"
                type="email"
                :label="$t('staff_crud.email')"
                :rules="rules.email"
              ></v-text-field>

              <number-field
                v-model="employee.phone"
                type="Long"
                :label="$t('staff_crud.phoneNumber')"
              ></number-field>
            </v-col>

            <v-col cols="12" md="6">
              <v-row no-gutters>
                <v-col cols="12" class="text-center">
                  <b> {{ $t("staff_crud.location") }}: </b>
                </v-col>

                <v-col>
                  <map-field
                    v-bind:entity="employee"
                    geomType="Point"
                    :editable="true"
                    propName="location"
                    entityName="employee"
                  ></map-field>
                </v-col>
              </v-row>
            </v-col>
          </v-row>
        </v-form>
      </v-card-text>
    </v-card>
  </v-container>
  <loading-page v-else></loading-page>
</template>

<script>
import checkInvalidID from "@/common/checkInvalidID";
import MapField from "@/components/map-field/MapField.vue";
import NumberField from "@/components/number-field/NumberField.vue";
import LoadingPage from "@/components/loading-page/LoadingPage.vue";
import RepositoryFactory from "@/repositories/RepositoryFactory";

const StaffCrudRepository = RepositoryFactory.get("StaffCrudRepository");

export default {
  name: "EmployeeForm",
  components: { MapField, NumberField, LoadingPage },
  data() {
    return {
      employee: {},
      validForm: true,
      loading: false,
      rules: {
        email: [
          v => !v || /.+@.+\..+/.test(v) || this.$t("staff_crud.emailValid")
        ]
      }
    };
  },
  computed: {
    isLoading() {
      return this.loading;
    }
  },
  beforeRouteUpdate(to, from, next) {
    // si se accede a la misma ruta con diferentes parámetros, se cargará el nuevo objeto
    if (to.params.id) this._fetchData(to.params.id);
    next();
  },
  created() {
    if (this.$route.params.id) this._fetchData(this.$route.params.id);
  },
  methods: {
    _fetchData(id) {
      this.loading = true;
      return StaffCrudRepository.getEmployee(id)
        .then((res) => (this.employee = res))
        .catch((err) => checkInvalidID(err))
        .finally(() => (this.loading = false));
    },
    back() {
      this.$router.push({ name: "EmployeeList", params: { backAction: true } });
    },
    save() {
      if (!this.validForm) {
        this.$notify({
          type: "error",
          text: this.$t("staff_crud.formFailures"),
        });
      } else  {
        this.loading = true;
        StaffCrudRepository.saveEmployee(this.employee)
          .then(() => {
            if (this.$route.params.id) {
              this.$router.push({
                name: "EmployeeDetail",
                params: {
                  id: this.employee.id,
                  backAction: this.$route.params.backPrevious,
                },
              })
            } else {
              this.$router.push({
                name: "EmployeeList",
                params: { backAction: true },
              })
            }
          })
          .finally(() => (this.loading = false));
      }
    },
  },
};
</script>
/*% } %*/
