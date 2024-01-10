/*% if (feature.MWM_PM_Clients) { %*/
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
              dense
              class="text-field"
              v-model="search"
              append-icon="search"
              :label="$t('staff_crud.search')"
              @input="redirectOnFilterChange"
              single-line
              hide-details
            ></debounced-text-field>
            <v-btn class="ml-3" color="success" :to="{ name: 'ClientCreate' }">
              <v-icon>add</v-icon>
              <span class="d-none d-sm-block"> {{ $t("new") }} </span>
            </v-btn>
          </v-col>
        </v-row>
      </v-card-title>
      <v-card-text>
        <v-data-table
          dense
          :footer-props="tableFooterProps"
          :headers="headers"
          :items="clients"
          :item-class="setRowClass"
          :loading="loading"
          :options.sync="clientsPage"
          :search="search"
          :server-items-length="totalClients"
          @click:row="clientDetail"
          @update:options="redirectOnTableChange"
        >
          <template v-slot:[`item.action`]="{ item }">
            <v-icon color="primary" @click.stop="clientDetail(item)">
              description
            </v-icon>
            <v-icon
              /*% if (feature.MWM_EmployeeAuthentication) { %*/
              v-if="isAdmin"
              /*% } %*/
              :color="item.active ? 'success' : 'error'"
              @click.stop="changeItemState(item)">
              {{ item.active ? "visibility_off" : "visibility" }}
            </v-icon>
            <v-icon
              /*% if (feature.MWM_EmployeeAuthentication) { %*/
              v-if="isAdmin"
              /*% } %*/
              color="warning"
              @click.stop="clientUpdate(item)">
              edit
            </v-icon>
          </template>
        </v-data-table>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script>
/*% if (feature.MWM_EmployeeAuthentication) { %*/
import {mapAuthGetter} from "@/common/mapAuthGetter";
/*% } %*/
import DebouncedTextField from "@/components/debouncing-inputs/DebouncedTextField.vue";
import tableFooterProps from "@/common/table-footer-props";
import defaultPaginationSettings from "@/common/default-pagination-settings";
import {
  generateSort,
  parseStringToSortBy,
  parseStringToSortDesc,
} from "@/common/pagination-utils";
import RepositoryFactory from "@/repositories/RepositoryFactory";

const StaffCrudRepository = RepositoryFactory.get("StaffCrudRepository");

export default {
  name: "ClientList",
  components: { DebouncedTextField },
  data() {
    return {
      search: "",
      loading: false,
      clients: [],
      totalClients: 0,
      clientsPage: {
        page:
          parseInt(this.$route.query.page) || defaultPaginationSettings.page,
        itemsPerPage:
          parseInt(this.$route.query.pageSize) ||
          defaultPaginationSettings.itemsPerPage,
        sortBy: parseStringToSortBy(this.$route.query.sort),
        sortDesc: parseStringToSortDesc(this.$route.query.sort),
      },
      tableFooterProps,
    };
  },
  computed: {
    /*% if (feature.MWM_EmployeeAuthentication) { %*/
    ...mapAuthGetter(["isAdmin"]),
    /*% } %*/
    headers() {
      return [
        {
          text: this.$t("staff_crud.fullName"),
          value: "fullName"
        },
        {
          text: this.$t("staff_crud.actions"),
          sortable: false,
          value: "action"
        }
      ];
    }
  },
  created() {
    this.getClients();
  },
  methods: {
    getClients() {
      this.loading = true;
      const options = {
        params: {
          page: this.clientsPage.page - 1,
          search: this.search,
          sort: this.$route.query.sort,
          size: this.clientsPage.itemsPerPage,
        },
      };
      StaffCrudRepository.getAllClients(options)
        .then((data) => {
          this.clients = data.content;
          this.totalClients = data.totalElements;
        })
        .finally(() => (this.loading = false));
    },
    clientDetail(entity) {
      const selection = window.getSelection().toString();
      if (selection.length === 0) {
        this.$router.push({
          name: "ClientDetail",
          params: { id: entity.id, backPrevious: true },
        });
      }
    },
    clientUpdate(entity) {
      const selection = window.getSelection().toString();
      if (selection.length === 0) {
        this.$router.push({
          name: "ClientForm",
          params: { id: entity.id, backPrevious: true }
        });
      }
    },
    changeItemState(item) {
      const promise = item.active
        ? StaffCrudRepository.deleteClient(item.id)
        : StaffCrudRepository.activateClient(item.id);
      // Change local object
      promise.then(() => (item.active = !item.active));
    },
    redirect(query) {
      if (JSON.stringify(this.$route.query) !== JSON.stringify(query)) {
        this.$router
          .replace({
            name: 'ClientList',
            query: query
          })
          .then(() => this.getClients());
      }
    },
    redirectOnTableChange(pagination = this.clientsPage) {
      this.clientsPage = pagination;
      let query = JSON.parse(JSON.stringify(this.$route.query));
      query.fullName = this.search || undefined;
      query.page = this.clientsPage.page.toString();
      query.pageSize = this.clientsPage.itemsPerPage.toString();
      query.sort = generateSort(this.clientsPage);
      this.redirect(query);
    },
    redirectOnFilterChange() {
      if (this.clientsPage.page !== 1) {
        this.clientsPage.page = 1;
      } else {
        this.redirectOnTableChange();
      }
    },
    setRowClass(row) {
      /*% if (feature.MWM_EmployeeAuthentication) { %*/
      if (this.isAdmin) {
        return row.active ? "" : "userInactive";
      } else {
        return "";
      }
      /*% } else { %*/
      return row.active ? "" : "userInactive";
      /*% } %*/
    },
    clientPageFilter() {
      let filter = "fullName:" + (this.search ? this.search : "");
      /*% if (feature.MWM_EmployeeAuthentication) { %*/
      if (!this.isAdmin) {
        filter += ",active:true";
      }
      /*% } %*/
      return filter;
    }
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
