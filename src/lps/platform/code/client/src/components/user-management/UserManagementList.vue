/*% if (feature.UM_UserCRUD) { %*/
<template>
  <v-container>
    <v-card>
      <v-card-title>
        <v-row align="center" no-gutters>
          <v-col cols="6" class="d-none d-md-block">
            <span class="headline no-split-words">
              {{ $t($route.meta.label) }}
            </span>
          </v-col>
          <v-col cols="12" md="6" class="text-center text-md-right">
            <debounced-text-field
              v-model="search"
              append-icon="search"
              class="d-md-inline-block"
              dense
              hide-details
              :label="$t('user_management.search')"
              @input="onSearchChange"
            ></debounced-text-field>
              /*% if (feature.UM_R_ByAdmin) { %*/
              <v-btn
                class="ml-3"
                color="success"
                dark
                :to="{ name: 'UserManagementFormNew' }"
              >
                <v-icon>add</v-icon>
                <span class="d-none d-sm-block">
                  {{ $t("user_management.new") }}
                </span>
              </v-btn>
              /*% } %*/
            </v-col>
          </v-row>
      </v-card-title>
      <v-card-text class="mt-5">
        <v-data-table
          :headers="headers"
          :items="users"
          :server-items-length="totalItems"
          :options="pagination"
          @update:options="onPaginationChange"
          :loading="loading"
        >
          <template v-slot:[`item.created_date`]="{ item }">
            <span>{{ item.created_date | dateFormat }} </span>
          </template>
          /*% if (feature.MWM_EmployeeAuthentication) { %*/
          <template v-slot:[`item.employee`]="{ item }">
            <router-link v-if="item.employee" :to="`/staff/employees/${item.employee.id}`">
              {{ item.employee.fullName }}
            </router-link>
          </template>
          /*% } %*/
          <template v-slot:[`item.action`]="{ item }">
            <v-btn icon color="orange" @click="updateUser(item)">
              <v-icon>edit</v-icon>
            </v-btn>
            <v-btn icon color="red" @click="selectDeleteUser(item)">
              <v-icon>delete</v-icon>
            </v-btn>
            /*% if (feature.UM_UP_ByAdmin) { %*/
            <v-btn icon color="primary" @click="showResetpasswordDialog(item)">
              <v-icon>mdi-lock-reset</v-icon>
            </v-btn>
            /*% } %*/
          </template>
          /*% if (feature.UM_AccountActivation) { %*/
          <template v-slot:[`item.activated`]="{ item }">
            <v-switch
              @change="activate(item)"
              v-model="item.activated"
              color="green"
            >
            </v-switch>
          </template>
          /*% } %*/
          <template v-slot:[`item.authorities`]="{ item }">
            <span>{{ item.authorities | rolesFormat }} </span>
          </template>
          <template v-slot:[`item.createdDate`]="{ item }">
            <span>{{ item.createdDate | zonedDateFormat }}</span>
          </template>
        </v-data-table>
      </v-card-text>
    </v-card>

    /*% if (feature.UM_UP_ByAdmin) { %*/
    <!-- Dialogo de confirmacion de reseteo de contraseña -->
    <modal-dialog
      @cancel="resetPasswordDialog = false"
      @submit="resetUserPassword()"
      :dialog="resetPasswordDialog"
      titleClass="primary white--text"
      titleIcon="mdi-lock-reset"
      submitClass="success"
      :submitText="$t('user_management.continue')"
      :title="$t('user_management.resetUserPassword')"
      :content="$t('user_management.confirmation')"
    ></modal-dialog>
    /*% } %*/

    <!-- Dialogo para la eliminacion de usuarios -->
    <delete-dialog
      :dialog="deleteDialog"
      @cancel="deleteDialog = false"
      @submit="deleteUser()"
    ></delete-dialog>
  </v-container>
</template>

<script>
import DeleteDialog from "@/components/modal_dialog/DeleteDialog";
import ModalDialog from "@/components/modal_dialog/ModalDialog";
import UserManagementRepository from "@/repositories/components/UserManagementRepository";
import DebouncedTextField from "@/components/debouncing-inputs/DebouncedTextField";

export default {
  components: { DebouncedTextField, DeleteDialog, ModalDialog },
  data() {
    return {
      users: [],
      search: this.$route.query.search || undefined,
      loading: false,
      deleteDialog: false,
      resetPasswordDialog: false,
      selectedUserResetPassword: false,
      selectedUserDelete: false,
      pagination: {
        page: (this.$route.query.page && parseInt(this.$route.query.page)) || 1,
        itemsPerPage:
          (this.$route.query.pageSize &&
            parseInt(this.$route.query.pageSize)) ||
          10,
      },
      totalItems: null,
    };
  },
  computed: {
    headers() {
      return [
        { text:  this.$t("user_management.login"), value: "login" },
        { text:  this.$t("user_management.firstName"), value: "firstName" },
        { text:  this.$t("user_management.lastName"), value: "lastName" },
        { text:  this.$t("user_management.email"), value: "email" },
        /*% if (feature.UM_AccountActivation) { %*/
        { text: this.$t("user_management.activated"), value: "activated", align: "start" },
        /*% } %*/
        { text:  this.$t("user_management.authorities"), value: "authorities" },
        { text: this.$t("user_management.createdDate"), value: "createdDate", align: "start" },
        /*% if (feature.MWM_EmployeeAuthentication) { %*/
        { text: this.$t("user_management.employee"), value: "employee"},
        /*% } %*/
        { text: "", sortable: false, value: "action" }
      ];
    }
  },
  created() {
    this._fetchData();
  },
  methods: {
    _fetchData() {
      this.loading = true;
      const options = {
        params: {
          page: this.pagination.page - 1,
          size: this.pagination.itemsPerPage,
          search: this.search,
        },
      };
      UserManagementRepository.getAll(options)
        .then((response) => {
          this.users = response.content;
          this.totalItems = response.totalElements;
        })
        .finally(() => (this.loading = false));
    },
    deleteUser() {
      UserManagementRepository.delete(this.selectedUserDelete).then(() => {
        this.users.splice(this.users.indexOf(this.selectedUserDelete), 1);
        this.selectedUserDelete = null;
        this.deleteDialog = false;
      });
    },
    selectDeleteUser(entity) {
      this.selectedUserDelete = entity;
      this.deleteDialog = true;
    },
    /*% if (feature.UM_UP_ByAdmin) { %*/
    showResetpasswordDialog(entity) {
      this.selectedUserResetPassword = entity;
      this.resetPasswordDialog = true;
    },
    /*% } %*/
    updateUser(user) {
      this.$router.push({
        name: "UsermanagementForm",
        params: { login: user.login }
      });
    },
    /*% if (feature.UM_AccountActivation) { %*/
    activate(user) {
      UserManagementRepository.save(user);
    },
    /*% } %*/
    /*% if (feature.UM_UP_ByAdmin) { %*/
    resetUserPassword() {
      UserManagementRepository.resetUserPassword(
        this.selectedUserResetPassword.email
      )
        .then(() =>
          this.$notify({
            title: "Reset Password",
            text: this.$t("user_management.emailSent"),
            type: "success",
          })
        )
        .finally(() => (this.resetPasswordDialog = false));
    },
    /*% } %*/
    redirect(query) {
      if (JSON.stringify(this.$route.query) !== JSON.stringify(query)) {
        this.$router.replace({
          name: this.$route.name,
          query: query,
        });
        this._fetchData();
      }
    },
    onPaginationChange(pagination = this.pagination) {
      this.pagination = pagination;
      let query = JSON.parse(JSON.stringify(this.$route.query));
      query.page = this.pagination.page.toString();
      query.pageSize = this.pagination.itemsPerPage.toString();
      query.search = this.search !== "" ? this.search : undefined;
      this.redirect(query);
    },
    onSearchChange() {
      if (this.pagination.page !== 1) {
        this.pagination.page = 1;
      } else {
        this.onPaginationChange();
      }
    },
  }
};
</script>
<style scoped>
.text-field {
  width: 71%;
  display: inline-block;
}
</style>
/*% } %*/
