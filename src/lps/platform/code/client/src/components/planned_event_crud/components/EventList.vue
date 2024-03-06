/*% if (feature.MWM_VisitSchedule) { %*/
<template>
  <v-container>
    <v-card v-if="!isLoading">
      <v-card-title>
        <v-row align="center" justify="space-between" no-gutters>
          <v-col cols="7" class="d-none d-md-block">
            <span class="headline no-split-words">
              {{ $t($route.meta.label) }}
            </span>
          </v-col>
          <v-col cols="12" md="5" class="text-right">
            <v-btn color="success" :to="{ name: 'EventCreate' }">
              <v-icon>add</v-icon>
              <span class="d-none d-sm-block">
                {{ $t("planned_event_crud.new") }}
              </span>
            </v-btn>
          </v-col>
        </v-row>
      </v-card-title>
      <v-card-text>
        <v-row align="center" justify="space-between">
          <v-col cols="12" md="4">
            <v-select
              v-model="filters.selectedState"
              :items="states"
              :label="$t('planned_event_crud.state')"
              :menu-props="{ offsetY: true }"
              @change="redirectOnFilterChange"
              item-text="label"
              item-value="value"
              clearable
            ></v-select>
          </v-col>
          <v-col class="pt-0" cols="12" md="4">
            <date-and-hour-picker
              class="mt-sm-4"
              :datePickerProp="fromDatePicker"
              @update-time="setStartDate"
            >
            </date-and-hour-picker>
          </v-col>
          <v-col class="pt-0" cols="12" md="4">
            <date-and-hour-picker
              class="mt-sm-4"
              :datePickerProp="toDatePicker"
              @update-time="setEndDate"
            >
            </date-and-hour-picker>
          </v-col>
          <v-col class="pt-0 pt-sm-3" cols="12" md="4">
            <debounced-text-field
              append-icon="search"
              v-model="filters.search"
              :label="$t('planned_event_crud.description')"
              @input="redirectOnFilterChange"
              dense
              hide-details
              single-line
            ></debounced-text-field>
          </v-col>
          <v-col cols="12" md="4">
            <autocomplete
              v-model="clientsSelected"
              :debouncing="300"
              :items="clientsItems"
              :label="$t('planned_event_crud.client')"
              :search-input.sync="clientSearch"
              @change="redirectOnFilterChange"
              abreviate
              item-text="fullName"
              multiple
              no-filter
              return-object
              solo
            >
            </autocomplete>
          </v-col>
          <v-col class="pt-0 pt-sm-3" cols="12" md="4">
            <autocomplete
              v-model="employeesSelected"
              :debouncing="300"
              :items="employeesItems"
              :label="$t('planned_event_crud.employee')"
              :search-input.sync="employeeSearch"
              @change="redirectOnFilterChange"
              abreviate
              item-text="fullName"
              multiple
              no-filter
              return-object
              solo
            >
            </autocomplete>
          </v-col>
        </v-row>
        <v-data-table
          dense
          :footer-props="tableFooterProps"
          :headers="headers"
          :items="events"
          :options="eventsPagination"
          :server-items-length="eventsTotalItems"
          @click:row="eventDetail"
          @update:options="(options) => redirectOnTableChange(options)"
        >
          <template v-slot:[`item.date`]="{ item }">
            <span>
              {{ item.date | dateFormat }}
            </span>
          </template>
          /*% if (feature.MVM_VT_WithTime) { %*/
          <template v-slot:[`item.startTime`]="{ item }">
            <span>
              {{ item.startTime | timeFormat }}
            </span>
          </template>
          <template v-slot:[`item.endTime`]="{ item }">
            <span>
              {{ item.endTime | timeFormat }}
            </span>
          </template>
          /*% } %*/
          <template v-slot:[`item.action`]="{ item }">
            <v-icon color="primary" @click.stop="eventDetail(item)">
              description
            </v-icon>
            <v-icon color="error" @click.stop="showDeleteDialog(item)">
              delete
            </v-icon>
            <v-icon color="warning" @click.stop="eventUpdate(item)">
              edit
            </v-icon>
          </template>
          <template v-slot:[`item.state`]="{item}">
            <span>
              {{ $t("planned_event_crud.state_values." + item.state) }}
            </span>
          </template>
          <template v-slot:[`item.client`]="{item}">
            <router-link v-if="item.client && item.client.active" :to="`/staff/clients/${item.client.id}`">
              {{ item.client.fullName }}
            </router-link>
            <span v-else>
              {{ item.client.fullName }}
            </span>
          </template>
          <template v-slot:[`item.employee`]="{item}">
            <router-link v-if="item.employee && item.employee.active" :to="`/staff/employees/${item.employee.id}`">
              {{ item.employee.fullName }}
            </router-link>
            <span v-else>
              {{ item.employee.fullName }}
            </span>
          </template>
        </v-data-table>
      </v-card-text>
    </v-card>

    <loading-page v-if="isLoading"></loading-page>

    <delete-dialog
      :dialog="dialog"
      @cancel="dialog = false"
      @submit="deleteEvent()"
    ></delete-dialog>
  </v-container>
</template>


<script>
import Autocomplete from "@/components/debouncing-inputs/Autocomplete.vue";
import DateAndHourPicker from "@/components/calendar/DateAndHourPicker.vue";
import DebouncedTextField from "@/components/debouncing-inputs/DebouncedTextField.vue";
import DeleteDialog from "@/components/modal_dialog/DeleteDialog";
import { formatDateInverse } from "@/common/conversion-utils";
import LoadingPage from "@/components/loading-page/LoadingPage.vue";

import defaultPaginationSettings from "@/common/default-pagination-settings";
import {
  generateSort,
  parseStringToSortBy,
  parseStringToSortDesc,
} from "@/common/pagination-utils";
import tableFooterProps from "@/common/table-footer-props";

import RepositoryFactory from "@/repositories/RepositoryFactory";
const EventsRepository = RepositoryFactory.get("EventsRepository");

export default {
  name: "EventList",
  components: {
    DeleteDialog,
    DateAndHourPicker,
    Autocomplete,
    DebouncedTextField,
    LoadingPage,
  },
  data() {
    return {
      dialog: false,
      loading: false,
      selected: null,
      events: [],
      eventsPagination: {
        page:
          parseInt(this.$route.query.page) || defaultPaginationSettings.page,
        itemsPerPage:
          parseInt(this.$route.query.pageSize) ||
          defaultPaginationSettings.itemsPerPage,
        sortBy: parseStringToSortBy(this.$route.query.sort),
        sortDesc: parseStringToSortDesc(this.$route.query.sort),
      },
      eventsTotalItems: 0,
      filters: {
        search: this.$route.query.description,
        selectedState: this.$route.query.state,
        startDateString: null,
        endDateString: null,
      },
      clientsItems: [],
      clientsSelected: [],
      clientSearch: null,
      employeesItems: [],
      employeesSelected: [],
      employeeSearch: null,
      fromDatePicker: {
        data: null,
        label: this.$t("planned_event_crud.from"),
        mandatory: false,
      },
      toDatePicker: {
        data: null,
        label: this.$t("planned_event_crud.to"),
        mandatory: false,
      },
      tableFooterProps,
    };
  },
  computed: {
    headers() {
      return [
        {
          text: this.$t("planned_event_crud.description"),
          value: "description",
        },
        {
          text: this.$t("planned_event_crud.date"),
          value: "date",
        },
        /*% if (feature.MVM_VT_WithTime) { %*/
        {
          text: this.$t("planned_event_crud.start"),
          value: "startTime"
        },
        {
          text: this.$t("planned_event_crud.end"),
          value: "endTime"
        },
        /*% } %*/
        {
          text: this.$t("planned_event_crud.state"),
          value: "state",
        },
        {
          text: this.$t("planned_event_crud.client"),
          value: "client",
        },
        {
          text: this.$t("planned_event_crud.employee"),
          value: "employee",
        },
        {
          text: this.$t("planned_event_crud.actions"),
          sortable: false,
          value: "action",
        },
      ];
    },
    states() {
      return [
        {
          label: this.$t("planned_event_crud.state_values.PENDING"),
          value: "PENDING",
        },
        {
          label: this.$t("planned_event_crud.state_values.DONE"),
          value: "DONE",
        },
      ];
    },
    isLoading() {
      return this.loading;
    },
  },
  watch: {
    employeeSearch() {
      this.fetchEmployees();
    },
    clientSearch() {
      this.fetchClients();
    },
  },
  created() {
    this.filters.startDateString = this.$route.query.start;
    this.fromDatePicker.data = this.$route.query.start
      ?.split("-")
      .map((i) => parseInt(i));

    this.filters.endDateString = this.$route.query.end;
    this.toDatePicker.data = this.$route.query.end
      ?.split("-")
      .map((i) => parseInt(i));

    const clientPromise = this.fetchClients().then(() => {
      if (this.$route.query.clients) {
        const clientsIdsFromURL = this.$route.query.clients.split(",");
        this.clientsSelected = this.clientsItems.filter(
          (c) => clientsIdsFromURL.findIndex((i) => i === c.id) !== -1
        );
      }
    });
    const employeePromise = this.fetchEmployees().then(() => {
      if (this.$route.query.employees) {
        const employeesIdsFromURL = this.$route.query.employees.split(",");
        this.employeesSelected = this.employeesItems.filter(
          (e) => employeesIdsFromURL.findIndex((i) => i === e.id) !== -1
        );
      }
    });
    Promise.all([clientPromise, employeePromise]).then(() => {
      this.fetchEvents();
    });
  },
  methods: {
    fetchEmployees() {
      let options;
      if (this.employeeSearch) {
        options = {
          params: { filters: "fullName:" + this.employeeSearch },
        };
      }
      return EventsRepository.getEmployeesByParams(options).then(
        (res) => (this.employeesItems = res.content)
      );
    },
    fetchClients() {
      let options;
      if (this.clientSearch) {
        options = {
          params: { filters: "fullName:" + this.clientSearch },
        };
      }
      return EventsRepository.getClientsByParams(options).then(
        (res) => (this.clientsItems = res.content)
      );
    },
    fetchEvents() {
      this.loading = true;
      const sortMapping = {
        client: "client.fullName",
        employee: "employee.fullName",
      };
      const options = {
        params: {
          page: this.eventsPagination.page - 1,
          sort: generateSort(this.eventsPagination, sortMapping),
          size: this.eventsPagination.itemsPerPage,
          description: this.filters.search,
          employee: this.employeesSelected.map((e) => e.id),
          client: this.clientsSelected.map((e) => e.id),
          start: this.filters.startDateString,
          end: this.filters.endDateString,
          state: this.filters.selectedState,
        },
      };
      return EventsRepository.getAll(options)
        .then((res) => {
          this.events = res.content;
          this.eventsTotalItems = res.totalElements;
        })
        .finally(() => (this.loading = false));
    },
    eventDetail(entity) {
      const selection = window.getSelection().toString();
      if (selection.length === 0) {
        this.$router.push({
          name: "EventDetail",
          params: { id: entity.id, backPrevious: true },
        });
      }
    },
    eventUpdate(entity) {
      const selection = window.getSelection().toString();
      if (selection.length === 0) {
        this.$router.push({
          name: "EventForm",
          params: { id: entity.id, backPrevious: true },
        });
      }
    },
    redirect(query) {
      if (JSON.stringify(this.$route.query) !== JSON.stringify(query)) {
        this.$router
          .replace({
            name: "EventList",
            query: query,
          })
          .then(() => this.fetchEvents());
      }
    },
    redirectOnTableChange(pagination = this.eventsPagination) {
      this.eventsPagination = pagination;
      let query = JSON.parse(JSON.stringify(this.$route.query));
      query.page = this.eventsPagination.page.toString();
      query.pageSize = this.eventsPagination.itemsPerPage.toString();
      query.sort = generateSort(this.eventsPagination);
      this.changeQueryFilters(query);
      this.redirect(query);
    },
    redirectOnFilterChange() {
      if (this.eventsPagination.page !== 1) {
        this.eventsPagination.page = 1;
      } else {
        this.redirectOnTableChange();
      }
    },
    changeQueryFilters(query) {
      query.state = this.filters.selectedState || undefined;
      query.start = this.filters.startDateString || undefined;
      query.end = this.filters.endDateString || undefined;
      query.description = this.filters.search || undefined;
      query.clients = this.clientsSelected?.map((c) => c.id).join(",");
      query.employees = this.employeesSelected?.map((e) => e.id).join(",");
    },
    setStartDate(data) {
      this.filters.startDateString = data.date
        ? formatDateInverse(new Date(data.date))
        : null;
      this.redirectOnFilterChange();
    },
    setEndDate(data) {
      this.filters.endDateString = data.date
        ? formatDateInverse(new Date(data.date))
        : null;
      this.redirectOnFilterChange();
    },
    showDeleteDialog(entity) {
      this.selected = entity;
      this.dialog = true;
    },
    closeDeleteDialog() {
      this.selected = null;
      this.dialog = false;
    },
    deleteEvent() {
      EventsRepository.delete(this.selected.id).then(() => {
        this.closeDeleteDialog();
        this.fetchEvents();
      });
    },
  },
};
</script>
/*% } %*/
