/*% if (feature.UM_UserCRUD) { %*/
<template>
  <v-container>
    <v-card>
      <v-card-title>
        <v-row align="center" no-gutters>
          <v-col cols="5" class="d-none d-md-block">
            <span class="headline no-split-words">
              {{ $t($route.meta.label) }}
            </span>
          </v-col>
          <v-col class="text-right">
            <v-btn @click="$router.back()">
              <v-icon>mdi-arrow-left</v-icon>
              <span class="d-none d-sm-block"> {{ $t("cancel") }} </span>
            </v-btn>
            <v-btn class="success ml-2" @click="save()" :disabled="!validForm">
              <v-icon>save</v-icon>
              <span class="d-none d-sm-block">
                {{ $t("user_management.save") }}
              </span>
            </v-btn>
          </v-col>
        </v-row>
      </v-card-title>
      <v-card-text>
        <v-form v-model="validForm">
          <v-row align="center" justify="center">
            <v-col cols="12" sm="6">
              <v-row>
                <v-text-field
                  v-model="user.login"
                  autocomplete="username"
                  autofocus
                  name="login"
                  prepend-inner-icon="person"
                  type="text"
                  :label="$t('user_management.login')"
                  :rules="[v => !!v || $t('user_management.usernameRequired')]"
                  required
                ></v-text-field>
              </v-row>
              <v-row>
                <v-text-field
                  v-model="user.firstName"
                  autocomplete="given-name"
                  name="firstname"
                  prepend-inner-icon="notes"
                  type="text"
                  :label="$t('user_management.firstName')"
                  :rules="[v => !!v || $t('user_management.firstNameRequired')]"
                  required
                ></v-text-field>
              </v-row>
              <v-row>
                <v-text-field
                  v-model="user.lastName"
                  autocomplete="family-name"
                  name="lastname"
                  prepend-inner-icon="notes"
                  type="text"
                  :label="$t('user_management.lastName')"
                  :rules="[v => !!v || $t('user_management.lastNameRequired')]"
                  required
                ></v-text-field>
              </v-row>
              <v-row>
                <v-text-field
                  v-model="user.email"
                  autocomplete="email"
                  name="email"
                  prepend-inner-icon="email"
                  type="email"
                  :label="$t('user_management.email')"
                  :rules="emailRules"
                  required
                ></v-text-field>
              </v-row>
              <v-row>
                <v-select
                  :items="authoritiesDisp"
                  v-model="user.authorities"
                  :label="$t('user_management.authorities')"
                  :menu-props="{ offsetY: true }"
                  multiple
                  chips
                  :rules="authorityRules"
                ></v-select>
              </v-row>
              /*% if (feature.MWM_EmployeeAuthentication) { %*/
              <v-row>
                <autocomplete
                  :debouncing="300"
                  :items="employees"
                  :search-input.sync="employeeSearch"
                  v-model="user.employee"
                  no-filter
                  :label="$t('user_management.employee')"
                  item-text="fullName"
                  return-object
                ></autocomplete>
              </v-row>
              /*% } %*/
            </v-col>
          </v-row>
        </v-form>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script>
/*% if (feature.MWM_EmployeeAuthentication) { %*/
import Autocomplete from "@/components/debouncing-inputs/Autocomplete";
/*% } %*/
import UserManagementRepository from "@/repositories/components/UserManagementRepository";

export default {
  name: "UserManagementForm",
  data() {
    return {
      user: {},
      validForm: true,
      /*% if (feature.MWM_EmployeeAuthentication) { %*/
      employeeSearch: "",
      employees: [],
      oldEmployee: null,
      /*% } %*/
      authoritiesDisp: ["ROLE_USER", "ROLE_ADMIN"],
      authorityRules: [
        v => {
          if (v.length == 0) {
            return this.$t("user_management.authoritiesRequired");
          } else return true;
        }
      ],
      emailRules: [
        v => !!v || this.$t("user_management.emailRequired"),
        v => /.+@.+/.test(v) || this.$t("user_management.emailValid")
      ]
    };
  },
  /*% if (feature.MWM_EmployeeAuthentication) { %*/
  components: { Autocomplete },
  watch: {
    employeeSearch: function(newEmployee) {
      UserManagementRepository.getEmployeesWithoutUserAssociated({
        params: {
          fullName: newEmployee && newEmployee != "" ? newEmployee : undefined,
          unfilteredEmployee: this.oldEmployee ? this.oldEmployee.id : undefined
        }
      }).then(data => {
        this.employees = data;
      });
    }
  },
  /*% } %*/
  created() {
    if (this.$route.params.login) {
      UserManagementRepository.get(this.$route.params.login).then(
        response => (this.user = response)
      );
    }
    /*% if (feature.MWM_EmployeeAuthentication) { %*/
    if (this.user && this.user.employee) {
      this.oldEmployee = this.user.employee;
    }
    /*% } %*/
  },
  methods: {
    save() {
      if (this.validForm) {
        // Si es el caso es un usuario ya creado, hay que actualizar
        if (this.$route.params.login) {
          UserManagementRepository.save(this.user).then(() => {
            this.$notify({
              title: "Success",
              text: this.$t("user_management.userUpdated"),
              type: "success",
            });
            this.$router.push({
              name: "UserManagementList",
            });
          });
        } else {
          // Si no es el caso hay que crear un usuario nuevo
          UserManagementRepository.save(this.user).then(() => {
            this.$router.push({
              name: "UserManagementList",
            });
          });
        }
      } else {
        this.$notify({
          title: "Error",
          text: this.$t("user_management.formBadFilled"),
          type: "error",
        });
      }
    }
  }
};
</script>
/*% } %*/
