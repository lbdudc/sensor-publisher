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
          <v-col class="text-right">
            <v-btn @click="back">
              <v-icon>mdi-arrow-left</v-icon>
              <span class="d-none d-sm-block"> {{ $t("cancel") }} </span>
            </v-btn>

            <v-btn
              class="success ml-2"
              @click="save(event, $route.params.backPrevious)"
              :disabled="!validForm"
            >
              <v-icon>save</v-icon>
              <span class="d-none d-sm-block"> {{ $t("save") }} </span>
            </v-btn>
          </v-col>
        </v-row>
      </v-card-title>
      <v-card-text>
        <v-form ref="form" v-model="validForm">
          <v-row dense>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="event.description"
                type="text"
                :label="$t('planned_event_crud.description')"
                :rules="[
                  (v) =>
                    !!v ||
                    $t('planned_event_crud.validation.description_required'),
                ]"
              ></v-text-field>
              <v-text-field
                v-model="event.address"
                type="text"
                :label="$t('planned_event_crud.address')"
              ></v-text-field>
              <v-select
                v-model="event.state"
                :items="statesDisp"
                :label="$t('planned_event_crud.state')"
                :menu-props="{ offsetY: true }"
                :rules="stateSelectRules"
                item-text="label"
                item-value="value"
              ></v-select>
              <autocomplete
                v-model="event.client"
                :debouncing="300"
                :items="clients"
                :label="$t('planned_event_crud.client')"
                :rules="clientSelectRules"
                :search-input.sync="clientSearch"
                item-text="fullName"
                no-filter
                return-object
              ></autocomplete>
              <autocomplete
                v-model="event.employee"
                :debouncing="300"
                :items="employees"
                :label="$t('planned_event_crud.employee')"
                :rules="employeeSelectRules"
                :search-input.sync="employeeSearch"
                item-text="fullName"
                no-filter
                return-object
              ></autocomplete>
            </v-col>
            <v-col cols="12" md="6">
              <v-row no-gutters>
                <v-col cols="12" class="text-center">
                  <b> {{ $t("planned_event_crud.location") }}: </b>
                </v-col>
                <v-col class="ml-4">
                  <map-field
                    v-bind:entity="event"
                    geomType="Point"
                    :editable="true"
                    propName="geom"
                    entityName="event"
                  ></map-field>
                </v-col>
              </v-row>
              <v-row>
                <v-col cols="12" /*% if (feature.MVM_VT_OnlyDay) { %*/md="9"/*% } %*/>
                  <dateAndHourPicker
                    class="mt-1"
                    :datePickerProp="pickers.datePicker"
                    /*% if (feature.MVM_VT_WithTime) { %*/
                    :timePickerProp="pickers.timePicker"
                    :additionalTimePickerProp="pickers.additionalTimePicker"
                    /*% } %*/
                    @update-time="updateTime"
                  ></dateAndHourPicker>
                </v-col>
                /*% if (feature.MVM_VT_OnlyDay) { %*/
                <v-col
                  cols="12"
                  md="3"
                  class="mt-auto mb-auto"
                >
                  <v-btn @click="toSortEvents" small color="primary">
                    {{ $t("planned_event_crud.sort_events") }}
                  </v-btn>
                </v-col>
                /*% } %*/
              </v-row>
            </v-col>
          </v-row>
        </v-form>
      </v-card-text>
    </v-card>
    <loading-page v-if="isLoading"></loading-page>
  </v-container>
</template>

<script>
import checkInvalidID from "@/common/checkInvalidID";
import DateAndHourPicker from "@/components/calendar/DateAndHourPicker";
import MapField from "@/components/map-field/MapField.vue";
import LoadingPage from "@/components/loading-page/LoadingPage.vue";
import Autocomplete from "@/components/debouncing-inputs/Autocomplete.vue";

import RepositoryFactory from "@/repositories/RepositoryFactory";
const EventsRepository = RepositoryFactory.get("EventsRepository");

export default {
  name: "EventForm",
  components: { MapField, DateAndHourPicker, LoadingPage, Autocomplete },
  data() {
    return {
      event: {},
      employees: [],
      clients: [],
      validForm: true,
      loading: false,
      loadingClients: false,
      loadingEmployees: false,
      clientSearch: "",
      employeeSearch: "",
      typesDisp: [
        { text: "Dia", value: "DAY" },
        { text: "Dia / hora", value: "DAYHOUR" },
        { text: "Dia / hora opcional", value: "DAYHOUROPT" },
      ],
      pickers: {
        datePicker: {
          data: null,
          mandatory: true,
        },
        /*% if (feature.MVM_VT_WithTime) { %*/
        timePicker: {
          data: null,
          mandatory: true
        },
        additionalTimePicker: {
          data: null,
          mandatory: true
        }
        /*% } %*/
      }
    };
  },
  computed: {
    isLoading() {
      return this.loading;
    },
    stateSelectRules() {
      return [
        (v) => {
          if (
            v === undefined ||
            (v.constructor === Object && Object.entries(v).length === 0)
          ) {
            return this.$t("planned_event_crud.validation.state_required");
          } else return true;
        },
      ];
    },
    employeeSelectRules() {
      return [
        (v) => {
          if (
            v === undefined ||
            (v.constructor === Object && Object.entries(v).length === 0)
          ) {
            return this.$t("planned_event_crud.validation.employee_required");
          } else return true;
        },
      ];
    },
    clientSelectRules() {
      return [
        (v) => {
          if (
            v === undefined ||
            (v.constructor === Object && Object.entries(v).length === 0)
          ) {
            return this.$t("planned_event_crud.validation.client_required");
          } else return true;
        },
      ];
    },
    statesDisp() {
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
  },
  watch: {
    employeeSearch() {
      this._fetchEmployees();
    },
    clientSearch() {
      this._fetchClients();
    },
  },
  beforeRouteUpdate(to, from, next) {
    // si se accede a la misma ruta con diferentes parámetros, se cargará el nuevo objeto
    if (to.params.id) this._fetchData(to.params.id);
    next();
  },
  created() {
    if (this.$route.params.id) this._fetchData(this.$route.params.id);
    this._fetchEmployees();
    this._fetchClients();
  },
  methods: {
    _fetchData(id) {
      this.loading = true;
      EventsRepository.get(id)
        .then((response) => {
          this.event = response;
          this.pickers.datePicker.data = response.date;
          /*% if (feature.MVM_VT_WithTime) { %*/
          this.pickers.timePicker.data = response.startTime.map(el =>
            ("0" + el).slice(-2)
          );
          this.pickers.additionalTimePicker.data = response.endTime.map(el =>
            ("0" + el).slice(-2)
          );
          /*% } %*/
        })
      .catch(err => checkInvalidID(err))
      .finally(() => (this.loading = false));
    },
    _fetchEmployees() {
      this.loadingEmployees = true;
      let options;
      if (this.employeeSearch) {
        options = {
          params: { filters: "fullName:" + this.employeeSearch },
        };
      }
      return EventsRepository.getEmployeesByParams(options)
        .then((res) => (this.employees = res.content))
        .finally(() => (this.loadingEmployees = false));
    },
    _fetchClients() {
      this.loadingClients = true;
      let options;
      if (this.clientSearch) {
        options = {
          params: { filters: "fullName:" + this.clientSearch },
        };
      }
      return EventsRepository.getClientsByParams(options)
        .then((res) => (this.clients = res.content))
        .finally(() => (this.loadingClients = false));
    },
    back() {
      this.$router.push({
        name: "EventList",
        params: { backAction: true }
      });
    },
    save() {
      this.$refs.form.validate();
      if (!this.validForm) {
        this.$notify({
          type: "error",
          text: this.$t("planned_event_crud.formErrors"),
        });
        return;
      }
      this.loading = true;
      const nextRoute = this.$route.params.id
        ? {
          name: "EventDetail",
          params: {
            id: this.event.id,
            backAction: this.$route.params.backPrevious,
          },
        }
        : {
          name: "EventList",
          params: { backAction: true },
        };
      EventsRepository.save(this.event)
        .then(() => this.$router.push(nextRoute))
        .finally(() => (this.loading = false));
    },
    /*% if (feature.MVM_VT_OnlyDay) { %*/
    toSortEvents() {
      this.$router.push({
        name: "EventCalendar",
        query: {
          employees: this.event.employee.id.toString(),
          dayView: this.formatLocalDateToString(this.event.date),
          typeView: "day"
        }
      });
    },
    formatLocalDateToString(localDate) {
      const date = new Date(localDate);
      date.setDate(localDate[2] + 1);
      return date.toISOString().slice(0, 10);
    },
    /*% } %*/
    updateTime(data) {
      /*% if (feature.MVM_VT_WithTime) { %*/
      this.event.startTime = data.time;
      this.event.endTime = data.additionalTime;
      /*% } %*/
      this.event.date = data.date;
    }
  }
};
</script>
/*% } %*/
