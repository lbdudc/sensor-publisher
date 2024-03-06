/*% if (feature.MWM_PM_Employees) { %*/
<template>
  <v-container>
    <v-card v-if="!isLoading">
      <v-card-title>
        <v-row align="center" justify="space-between" no-gutters>
          <v-col class="d-none d-md-block">
            <span class="headline no-split-words">
              {{ $t($route.meta.label) }}
            </span>
          </v-col>
          <v-col class="text-right">
            <v-btn @click="back">
              <v-icon>arrow_back</v-icon>
              <span class="d-none d-sm-block"> {{ $t("back") }} </span>
            </v-btn>

            <v-btn
              /*% if (feature.MWM_EmployeeAuthentication) { %*/
              v-if="isAdmin"
              /*% } %*/
              class="warning ml-2" @click="edit(employee)">
              <v-icon>edit</v-icon>
              <span class="d-none d-sm-block"> {{ $t("edit") }} </span>
            </v-btn>

            <v-btn
              /*% if (feature.MWM_EmployeeAuthentication) { %*/
              v-if="isAdmin"
              /*% } %*/
              :color="employee.active ? 'error' : 'success'"
              class="ml-2" @click="changeEmployeeState">
              <v-icon>{{
                employee.active ? "visibility_off" : "visibility"
                }}</v-icon>
              <span class="d-none d-sm-block">
                  {{
                    employee.active
                      ? $t("staff_crud.deactivate")
                      : $t("staff_crud.activate")
                  }}
              </span>
            </v-btn>
          </v-col>
        </v-row>
      </v-card-title>
      <v-card-text>
        <v-row align="center" no-gutters>
          <v-col cols="12" md="6">
            <v-row align="center" dense>
              <v-col cols="3" class="text-right">
                <b> {{ $t("staff_crud.fullName") }}: </b>
              </v-col>
              <v-col cols="9">
                {{ employee.fullName }}
              </v-col>

              <v-col cols="3" class="text-right">
                <b> {{ $t("staff_crud.email") }}: </b>
              </v-col>
              <v-col cols="9">
                {{ employee.email }}
              </v-col>

              <v-col cols="3" class="text-right">
                <b> {{ $t("staff_crud.phoneNumber") }}: </b>
              </v-col>
              <v-col cols="9">
                {{ employee.phone }}
              </v-col>
            </v-row>
          </v-col>

          <v-col cols="12" md="6">
            <v-row align="center" no-gutters>
              <v-col cols="12" class="text-center">
                <b> {{ $t("staff_crud.location") }}: </b>
              </v-col>
            </v-row>

            <v-col cols="12">
              <map-field
                v-bind:entity="employee"
                :editable="false"
                propName="location"
                entityName="employee"
              ></map-field>
            </v-col>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>

    <loading-page v-if="isLoading"></loading-page>
  </v-container>
</template>

<script>
import checkInvalidID from "@/common/checkInvalidID";
import MapField from "@/components/map-field/MapField.vue";
import LoadingPage from "@/components/loading-page/LoadingPage.vue";
/*% if (feature.MWM_EmployeeAuthentication) { %*/
import { mapAuthGetter } from "@/common/mapAuthGetter";
/*% } %*/
import RepositoryFactory from "@/repositories/RepositoryFactory";

const StaffCrudRepository = RepositoryFactory.get("StaffCrudRepository");

export default {
  name: "EmployeeDetail",
  components: { MapField, LoadingPage },
  data() {
    return {
      loading: true,
      employee: null,
    }
  },
  computed: {
    /*% if (feature.MWM_EmployeeAuthentication) { %*/
    ...mapAuthGetter(["isAdmin"]),
  /*% } %*/
    isLoading() {
      return this.loading;
    }
  },
  beforeRouteEnter(to, from, next) {
    next(that => {
      /*% if (feature.MWM_EmployeeAuthentication) { %*/
      // Si el usuario no es administrador, sólo puede acceder a los datos de los empleados activos
      that._fetchData(to.params.id);
      /*% } else { %*/
      that._fetchData(to.params.id)
      next()
      /*% } %*/
    });
  },
  beforeRouteUpdate(to, from, next) {
    this._fetchData(to.params.id);
    next();
  },
  methods: {
    _fetchData(id) {
      this.loading = true;
      return StaffCrudRepository.getEmployee(id)
        .then((res) => (this.employee = res))
        .catch((err) => checkInvalidID(err))
        .finally(() => (this.loading = false));
    },
    edit() {
      const selection = window.getSelection().toString();
      if (selection.length === 0) {
        this.$router.push({
          name: "EmployeeForm",
          params: { id: this.employee.id, backPrevious: true },
        });
      }
    },
    back() {
      this.$router.push({ name: "EmployeeList", params: { backAction: true } });
    },
    changeEmployeeState() {
      const method = this.employee.active
        ? StaffCrudRepository.deleteEmployee
        : StaffCrudRepository.activateEmployee;
      method(this.employee.id)
        .then(() => this._fetchData(this.employee.id));
    },
  },
};
</script>
/*% } %*/
