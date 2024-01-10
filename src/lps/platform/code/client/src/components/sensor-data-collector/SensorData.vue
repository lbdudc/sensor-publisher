/*% if (feature.MWM_TA_SensorDataCollector) { %*/
<template>
  <v-container>
    <v-card>
      <v-card-title>
        <v-col cols="12" md="6" class="d-none d-md-block pl-0">
          <span class="headline no-split-words">
            {{ $t($route.meta.label) }}
          </span>
        </v-col>
        <v-col cols="12" md="6" class="text-right">
          <v-btn @click="sendRequest">
            {{ $t("sensorDataCollector.search") }}
          </v-btn>
          /*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
          <v-btn v-if="processIsRunning" class="primary ml-3" disabled>
            {{ $t("sensorDataCollector.transform.running.button") }}
          </v-btn>
          <v-tooltip v-else bottom>
            <template v-slot:activator="{ on }">
              <v-btn
                @click="callTransformationProcess"
                class="primary ml-3"
                dark
                v-on="on"
              >{{ $t("sensorDataCollector.transform.button") }}</v-btn
              >
            </template>
            <span>
              {{ $t("sensorDataCollector.transform.information") }}
            </span>
          </v-tooltip>
          /*% } %*/
        </v-col>
        <v-row>
          <v-col cols="12" md="6" class="px-0">
            <date-hour-picker
              :datePickerProp="pickers.datePicker"
              :timePickerProp="pickers.timePicker"
              :additionalTimePickerProp="pickers.additionalTimePicker"
              @update-time="saveDate"
            ></date-hour-picker>
          </v-col>
          <v-col cols="12" md="6">
            <v-autocomplete
              class="mt-0 pt-0"
              :items="employees"
              v-model="form.employee"
              item-text="fullName"
              item-value="id"
              :label="$t('sensorDataCollector.employee')"
              :error="employeesError"
              clearable
              @change="redirectOnEmployee"
            >
            </v-autocomplete>
          </v-col>
        </v-row>
      </v-card-title>
      <v-card-text>
        <v-row>
          <v-col>
            <v-sheet height="64">
              <v-toolbar flat color="primary" dark>
                <v-btn outlined class="mr-4" @click="setToday">
                  {{ $t("sensorDataCollector.today") }}
                </v-btn>
                <v-btn fab text small @click="prev">
                  <v-icon small>mdi-chevron-left</v-icon>
                </v-btn>
                <v-btn fab text small @click="next">
                  <v-icon small>mdi-chevron-right</v-icon>
                </v-btn>
                <v-toolbar-title>{{ title }}</v-toolbar-title>
                <v-spacer></v-spacer>
                <v-menu bottom right>
                  <template v-slot:activator="{ on }">
                    <v-btn outlined v-on="on">
                      <span>{{ calendarView }}</span>
                      <v-icon right>mdi-menu-down</v-icon>
                    </v-btn>
                  </template>
                  <v-list>
                    <v-list-item
                      @click="changeCategories($t('sensorDataCollector.all'))"
                    >
                      <v-list-item-title>{{
                          $t("sensorDataCollector.all")
                        }}</v-list-item-title>
                    </v-list-item>
                    <v-divider></v-divider>
                    <v-list-item
                      @click="
                        changeCategories($t('sensorDataCollector.activities'))
                      "
                    >
                      <v-list-item-title>{{
                          $t("sensorDataCollector.activities")
                        }}</v-list-item-title>
                    </v-list-item>
                    <v-list-item
                      @click="
                        changeCategories($t('sensorDataCollector.locations'))
                      "
                    >
                      <v-list-item-title>{{
                          $t("sensorDataCollector.locations")
                        }}</v-list-item-title>
                    </v-list-item>
                    <v-list-item
                      @click="
                        changeCategories($t('sensorDataCollector.segments'))
                      "
                    >
                      <v-list-item-title>{{
                          $t("sensorDataCollector.segments")
                        }}</v-list-item-title>
                    </v-list-item>
                  </v-list>
                </v-menu>
              </v-toolbar>
            </v-sheet>
            <v-sheet height="600">
              <v-calendar
                id="calendar"
                ref="calendar"
                v-model="focus"
                color="primary"
                :events="events"
                :categories="categoriesDisp"
                category-hide-dynamic
                category-show-all
                :event-category="getCategory"
                :event-color="getEventColor"
                :event-margin-bottom="3"
                event-overlap-mode="column"
                :interval-format="intervalFormat"
                :interval-height="100"
                :interval-minutes="30"
                :interval-count="48"
                :now="today"
                :type="type"
                @click:event="showPopup"
                @change="updateRange"
                :locale="localeRoot"
              >
                <template v-slot:category="{ category }">
                  <div class="v-calendar-category__category">
                    {{ getCategoryText(category) }}
                  </div>
                </template>
              </v-calendar>
              <v-menu
                v-model="selected"
                :close-on-content-click="false"
                :activator="selectedElement"
                offset-x
              >
                <v-card v-if="selected">
                  <v-col class="text-right pa-0">
                    <v-btn x-small text @click="closePopup">
                      <v-icon x-small>mdi-close</v-icon>
                    </v-btn>
                  </v-col>
                  <component
                    :is="popupComponent"
                    :eventData="selectedEvent"
                  ></component>
                </v-card>
              </v-menu>
            </v-sheet>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script>
import { localDateToVCalendarString } from "@/common/conversion-utils";
import DateHourPicker from "@/components/calendar/DateAndHourPicker";
import SensorDataMap from "./components/SensorDataMap";
import SegmentList from "./components/SegmentList";
import i18n from "@/plugins/i18n";
import { localDateToISOString } from "@/common/conversion-utils";
import RepositoryFactory from "@/repositories/RepositoryFactory";
const SensorDataCollectorRepository = RepositoryFactory.get("SensorDataCollectorRepository");
const classes = {
  location: "Location",
  segment: "Segment",
  activity: "Activity"
};

export default {
  name: "SensorData",
  data() {
    return {
      type: "category",
      focus: new Date().toISOString().slice(0, 10),
      start: null,
      end: null,
      form: {
        employee: null,
        startDate: null,
        startTime: null,
        endTime: null
      },
      pickers: {
        datePicker: {
          data: null,
          mandatory: true
        },
        timePicker: {
          data: null,
          mandatory: true
        },
        additionalTimePicker: {
          data: null,
          mandatory: true
        }
      },
      calendarView: this.$t("sensorDataCollector.all"),
      categories: [classes.segment, classes.location, classes.activity],
      categoriesDisp: [classes.segment, classes.location, classes.activity],
      employees: [],
      events: [],
      popupComponent: null,
      today: new Date().toISOString().slice(0, 10),
      employeesError: false,
      colors: {
        segment: "cyan",
        activity: "green",
        location: "orange"
      },
      selectedEvent: {},
      selectedElement: null,
      selected: false,
      runningProcess: false
    };
  },
  components: {
    "date-hour-picker": DateHourPicker,
    "sensor-data-map": SensorDataMap,
    "segment-list": SegmentList
  },
  computed: {
    processIsRunning() {
      return this.runningProcess;
    },
    localeRoot() {
      return i18n.locale;
    },
    title() {
      const { start, end } = this;
      if (!start || !end) {
        return "";
      }

      const monthFormatter = this.$refs.calendar.getFormatter({
        timeZone: "UTC",
        month: "long"
      });

      const startMonth = this.$vuetify.breakpoint.xs
        ? monthFormatter(start).slice(0, 3)
        : monthFormatter(start);
      const startYear = start.year;
      const startDay = this.$i18n.locale === 'en' ? (start.day + nth(start.day)) : start.day;

      switch (this.type) {
        case "month":
          return `${startMonth} ${startYear}`;
        case "week":
          return this.$t("event_calendar.title.week.format", {
            year: startYear,
            month: startMonth,
            day: startDay
          });
        case "category":
        case "day":
          return this.$t("event_calendar.title.day.format", {
            month: startMonth,
            day: startDay,
            weekDay: this.$t('event_calendar.weekdays_name')[start.weekday]
          });
      }
      return "";
    }
  },
  beforeMount() {
    let date = this.$route.query.date
      ? this.$route.query.date.split("-").map(e => parseInt(e))
      : null;
    this.form.employee = this.$route.query.id
      ? parseInt(this.$route.query.id)
      : null;
    this.form.startDate = this.$route.query.date ? date : null;
    this.focus = this.$route.query.date
      ? new Date(
          new Date(date[0], date[1] - 1, date[2]).getTime() -
            new Date().getTimezoneOffset() * 60000
        )
          .toISOString()
          .slice(0, 10)
      : new Date().toISOString().slice(0, 10);
    this.form.startTime = this.$route.query.start
      ? this.$route.query.start.split(":").map(e => parseInt(e))
      : null;
    this.form.endTime = this.$route.query.end
      ? this.$route.query.end.split(":").map(e => parseInt(e))
      : null;

    this.updatePickers();
    this.sendRequest();
  },
  mounted() {
    SensorDataCollectorRepository.getEmployees()
      .then(data => {
        this.employees = data;
        this.checkEmployee();
      });
  },
  methods: {
    redirect() {
      const query = {
        id: this.form.employee ? this.form.employee.toString() : undefined,
        date: this.form.startDate ? this.form.startDate.map(el =>
          (el < 10) ? ("0"+ el.toString()) : el.toString()).join("-") : undefined,
        start: this.form.startTime ? this.form.startTime.map(el =>
          ("0"+ el.toString()).slice(-2)).join(":") : undefined,
        end: this.form.endTime ? this.form.endTime.map(el =>
          ("0"+ el.toString()).slice(-2)).join(":") : undefined
      };
      //Check date times
      if (parseFloat(query.start) > parseFloat(query.end)) {
        this.$notify({
          text: this.$t("sensorDataCollector.startDateError"),
          type: "error"
        });
      }
      if (JSON.stringify(query) !== JSON.stringify(this.$route.query)) {
        this.$router.replace({
          name: "SensorData",
          query: query
        });
      }
    },
    next() {
      this.$refs.calendar.next();
    },
    prev() {
      this.$refs.calendar.prev();
    },
    setToday() {
      this.focus = this.today;
    },
    intervalFormat(interval) {
      return interval.time;
    },
    updatePickers() {
      if (this.form.startDate) {
        this.$set(this.pickers.datePicker, "data", this.form.startDate);
        this.$set(
          this.pickers.datePicker,
          "innerData",
          this.form.startDate.join("-")
        );
        this.$set(
          this.pickers.datePicker,
          "innerDateFormatted",
          this.form.startDate.map(el =>
            (el < 10) ? ("0"+ el.toString()) : el.toString()).reverse().join("/")
        );
      }
      if (this.form.startTime) {
        this.$set(this.pickers.timePicker, "data", this.form.startTime.map(el => ("0" + el.toString()).slice(-2)));
      }
      if (this.form.endTime) {
        this.$set(this.pickers.additionalTimePicker, "data", this.form.endTime.map(el => ("0" + el.toString()).slice(-2)));
      }
    },
    updateRange({ start, end }) {
      this.start = start;
      this.end = end;

      this.form.startDate = [start.year, start.month, start.day];

      this.updatePickers();
      this.redirect();
    },
    showPopup({ nativeEvent, event }) {
      const open = () => {
        this.selectedEvent = event;
        this.popupComponent =
          (event.class === 'Location'
            ? "sensor-data-map"
            : event.class === 'Segment'
              ? "segment-list"
              : null);
        this.selectedElement = nativeEvent.target;
        setTimeout(() => (this.selected = true), 10);
      };

      if (this.selected) {
        this.selected = false;
        this.popupComponent = null;
        setTimeout(open, 10);
      } else {
        open();
      }

      nativeEvent.stopPropagation();
    },
    closePopup() {
      this.selected = false;
      this.popupComponent = null;
    },
    saveDate(data) {
      this.focus = localDateToVCalendarString(data.date);
      this.form.startDate = data.date;
      this.form.startTime = data.time;
      this.form.endTime = data.additionalTime;
    },
    checkEmployee() {
      if (!this.form.employee) {
        this.$notify({
          text: this.$t("sensorDataCollector.selectOneEmployee"),
          type: "error"
        });
        this.employeesError = true;
      } else {
        this.employeesError = false;
        this.$notify({
          clean: true
        });
      }
    },
    redirectOnEmployee() {
      this.checkEmployee();
      this.redirect();
    },
    getEventColor(event) {
      return event.color;
    },
    getCategory(event) {
      return event.class;
    },
    getCategoryText(category) {
      switch (category) {
        case classes.location:
          return this.$t("sensorDataCollector.locations");
        case classes.segment:
          return this.$t("sensorDataCollector.segments");
        case classes.activity:
          return this.$t("sensorDataCollector.activities");
      }
    },
    changeCategories(category) {
      this.calendarView = category;
      this.categoriesDisp = [];
      if (category === this.$t("sensorDataCollector.all"))
        this.categoriesDisp = JSON.parse(JSON.stringify(this.categories));
      else
        this.categoriesDisp.push(category);
    },
    sendRequest() {
      //Send request only if all mandatory fields are filled
      this.events = [];
      if (this.form.employee && this.form.startTime && this.form.endTime) {

        let options = {
          params: {
            start: localDateToISOString(
              [
                ...this.form.startDate,
                ...this.form.startTime,
                0 //seconds
              ]
            ),
            end: localDateToISOString(
              [
                ...this.form.startDate,
                ...this.form.endTime,
                0
              ]
            ),
            groupsOf: 15 //Fetch locations in groups of 15 minutes
          }
        };

        //Get and adapt locations, segments and activities to event format
        SensorDataCollectorRepository.getLocations(this.form.employee, options)
          .then(data => this.transformLocations(data));

        options = {
          params: {
            start: localDateToISOString(
              [
                ...this.form.startDate,
                ...this.form.startTime,
                0
              ]
            ),
            end: localDateToISOString(
              [
                ...this.form.startDate,
                ...this.form.endTime,
                0
              ]
            ),
            groupsOf: 15 //Fetch segments in groups of 15 minutes
          }
        };

        SensorDataCollectorRepository.getSegments(this.form.employee, options)
          .then(data => this.transformSegments(data));

        options = {
          params: {
            employee: this.form.employee,
            start: localDateToISOString(
              [
                ...this.form.startDate,
                ...this.form.startTime,
                0
              ]
            ),
            end: localDateToISOString(
              [
                ...this.form.startDate,
                ...this.form.endTime,
                0
              ]
            )
          }
        };

        SensorDataCollectorRepository.getActivities(options)
          .then(data => this.transformActivities(data));
      }
    },
    transformLocations(locations) {
      locations.forEach(group => {
        if (group.locations.length > 0) {
          let locationsEvent = {};
          locationsEvent.name =
            this.$t("sensorDataCollector.locations") +
            ": " +
            group.locations.length;
          locationsEvent.start = localDateToVCalendarString(group.initTime);
          locationsEvent.end = localDateToVCalendarString(group.endTime);
          locationsEvent.color = this.colors.location;
          locationsEvent.class = classes.location;
          locationsEvent.locations = group.locations;
          this.events.push(locationsEvent);
        }
      });
    },
    transformSegments(segments) {
      segments.forEach(group => {
        if (group.segments.length > 0) {
          let segmentsEvent = {};
          if (group.segments.length === 1) {
            segmentsEvent.name = group.segments[0].tag.name;
          } else {
            segmentsEvent.name =
              this.$t("sensorDataCollector.segments") +
              ": " +
              group.segments.length;
          }
          segmentsEvent.start = localDateToVCalendarString(group.initTime);
          segmentsEvent.end = localDateToVCalendarString(group.endTime);
          segmentsEvent.color = this.colors.segment;
          segmentsEvent.class = classes.segment;
          segmentsEvent.segments = group.segments;
          this.events.push(segmentsEvent);
        }
      });
    },
    transformActivities(activities) {
      activities.forEach(activity => {
        //Bug fixing on less than one minute events
        let ts_initMillis = this.dateTimeArraysToTimestamp(
          [activity.ts_init[0], activity.ts_init[1], activity.ts_init[2]],
          [activity.ts_init[3], activity.ts_init[4]]
        );
        let ts_endMillis = this.dateTimeArraysToTimestamp(
          [activity.ts_end[0], activity.ts_end[1], activity.ts_end[2]],
          [activity.ts_end[3], activity.ts_end[4]]
        );

        const formattedStart = this.millisToISODate(ts_initMillis);
        let formattedEnd = this.millisToISODate(ts_endMillis);
        if (formattedStart === formattedEnd) {
          formattedEnd = this.millisToISODate(ts_endMillis + 60000); //Plus 1 minute
        }
        activity.color = this.colors.activity;
        activity.name = activity.category.label;
        activity.start = formattedStart;
        activity.end = formattedEnd;
        activity.class = classes.activity;
        this.events.push(activity);
      });
    },
    /*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
    callTransformationProcess() {
      this.runningProcess = true;
      SensorDataCollectorRepository.callAnnotateProcess()
        .then(() =>
          this.$notify({
            text: this.$t("sensorDataCollector.transform.call.success"),
            type: "success"
          })
        )
        .finally(() => (this.runningProcess = false));
    },
    /*% } %*/
    millisToISODate(millis) {
      const date = new Date(millis);
      const year = date.getFullYear();
      const month = date.getMonth() + 1;
      const day = date.getDate();
      const hour = ("0" + date.getHours()).slice(-2);
      const min = ("0" + date.getMinutes()).slice(-2);
      return year + "-" + month + "-" + day + " " + hour + ":" + min;
    },
    dateTimeArraysToTimestamp(dateArray, timeArray) {
      const date = new Date(
        dateArray[0],
        dateArray[1] - 1,
        dateArray[2],
        timeArray[0],
        timeArray[1]
      );
      return date.getTime();
    }
  }
};
</script>
/*% } %*/
