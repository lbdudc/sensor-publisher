/*% if (feature.MWM_PM_Employees) { %*/
<template>
  <v-container v-if="pagination">
    <v-card>
      <v-card-title>
        <v-row align="center" no-gutters>
          <v-col cols="6" class="d-none d-md-block">
            <span class="headline no-split-words">{{ $t($route.meta.label) }}</span>
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
            <v-btn class="ml-3" color="success" :to="{ name: 'EmployeeCreate' }">
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
          :items="employees"
          :item-class="setRowClass"
          :loading="loading"
          :options.sync="pagination"
          :search="search"
          :server-items-length="totalEmployees"
          @click:row="employeeDetail"
          @update:options="redirectOnTableChange"
        >
          <template v-slot:[`item.action`]="{ item }">
            /*% if (feature.MWM_VisitSchedule) { %*/
            <v-icon color="primary" @click.stop="showEventCalendar(item)">
              event
            </v-icon>
            /*% } %*/
            <v-icon color="primary" @click.stop="employeeDetail(item)">
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
              @click.stop="employeeUpdate(item)">
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
import { mapAuthGetter } from "@/common/mapAuthGetter";
/*% } %*/
import DebouncedTextField from "@/components/debouncing-inputs/DebouncedTextField.vue";
import defaultPaginationSettings from "@/common/default-pagination-settings";
import {
  generateSort,
  parseStringToSortBy,
  parseStringToSortDesc,
} from "@/common/pagination-utils";
import tableFooterProps from "@/common/table-footer-props";
import RepositoryFactory from "@/repositories/RepositoryFactory";

const StaffCrudRepository = RepositoryFactory.get("StaffCrudRepository");

export default {
  name: "EmployeeList",
  components: { DebouncedTextField },
  data() {
    return {
      search: this.$route.query.fullName,
      employees: [],
      loading: true,
      pagination: {
        page:
          parseInt(this.$route.query.page) || defaultPaginationSettings.page,
        itemsPerPage:
          parseInt(this.$route.query.pageSize) ||
          defaultPaginationSettings.itemsPerPage,
        sortBy: parseStringToSortBy(this.$route.query.sort),
        sortDesc: parseStringToSortDesc(this.$route.query.sort),
      },
      tableFooterProps,
      totalEmployees: 0,
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
    //Refresh data every time component is accessed
    this.getEmployees();
  },
  methods: {
    getEmployees() {
      this.loading = true;
      const options = {
        params: {
          page: this.pagination.page - 1,
          size: this.pagination.itemsPerPage,
          sort: this.$route.query.sort,
          filters: this.employeePageFilter(),
        },
      };
      StaffCrudRepository.getAllEmployees(options)
        .then((data) => {
          this.employees = data.content;
          this.totalEmployees = data.totalElements;
        })
        .finally(() => (this.loading = false));
    },
    changeItemState(item) {
      const method = item.active
        ? StaffCrudRepository.deleteEmployee
        : StaffCrudRepository.activateEmployee;
      method(item.id).then(() => this.getEmployees());
    },
    employeeDetail(entity) {
      const selection = window.getSelection().toString();
      if (selection.length === 0) {
        this.$router.push({
          name: "EmployeeDetail",
          params: { id: entity.id, backPrevious: true }
        });
      }
    },
    employeeUpdate(entity) {
      const selection = window.getSelection().toString();
      if (selection.length === 0) {
        this.$router.push({
          name: "EmployeeForm",
          params: { id: entity.id, backPrevious: true }
        });
      }
    },
    redirect(query) {
      if (JSON.stringify(this.$route.query) !== JSON.stringify(query)) {
        this.$router
          .replace({
            name: "EmployeeList",
            query: query,
          })
          .then(() => this.getEmployees());
      }
    },
    redirectOnTableChange(pagination = this.pagination) {
      this.pagination = pagination;
      let query = JSON.parse(JSON.stringify(this.$route.query));
      query.fullName = this.search || undefined;
      query.page = this.pagination.page.toString();
      query.pageSize = this.pagination.itemsPerPage.toString();
      query.sort = generateSort(this.pagination);
      this.redirect(query);
    },
    redirectOnFilterChange() {
      if (this.pagination.page !== 1) {
        this.pagination.page = 1;
      } else {
        this.redirectOnTableChange();
      }
    },
    setRowClass(row) {
      return row.active ? "" : "userInactive";
    },
    employeePageFilter() {
      return "fullName:" + (this.search ? this.search : "");
    }/*% if (feature.MWM_VisitSchedule) { %*/,
    showEventCalendar(item) {
      this.$router.push({
        name: "EventCalendar",
        query: { employees: [item.id].toString() }
      });
    }
    /*% } %*/
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
