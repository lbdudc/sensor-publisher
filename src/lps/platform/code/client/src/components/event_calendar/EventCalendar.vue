/*% if (feature.MWM_VisitSchedule && feature.MVM_VT_OnlyDay) { %*/
<template>
  <v-container>
    <v-card>
      <v-card-title>
        <v-row dense>
          <v-col cols="12" class="d-none d-md-block">
            <span class="headline no-split-words">
              {{ $t($route.meta.label) }}
            </span>
          </v-col>
          <v-col cols="12" md="6">
            <autocomplete
              :debouncing="300"
              :items="employees"
              v-model="selectedEmployees"
              item-text="fullName"
              item-value="id"
              :label="$t('event_calendar.employee.plural')"
              multiple
              no-filter
              :search-input.sync="employeeSearch"
              :loading="employeesLoading"
            >
            </autocomplete>
          </v-col>
          <v-col cols="12" md="6">
            <v-text-field
              v-model="search"
              append-icon="search"
              :label="$t('event_calendar.searchVisits')"
              single-line
              @input="updateRoute"
            ></v-text-field>
          </v-col>
        </v-row>
      </v-card-title>
      <v-card-text>
        <v-row no-gutters>
          <v-col>
            <v-sheet>
              <v-toolbar flat class="primary" dark>
                <v-row align="center" no-gutters>
                  <v-col
                    class="pl-sm-4 text-sm-right"
                    cols="1"
                    order="1"
                    order-sm="2"
                  >
                    <v-btn icon :small="isXsScreen" @click="prev">
                      <v-icon small>mdi-chevron-left</v-icon>
                    </v-btn>
                  </v-col>
                  <v-col cols="2" sm="1" order="2" order-sm="1">
                    <v-btn
                      class="px-0 ml-sm-2"
                      outlined
                      :small="isXsScreen"
                      @click="setToday"
                    >
                      {{ $t("event_calendar.today") }}
                    </v-btn>
                  </v-col>
                  <v-col
                    cols="5"
                    sm="5"
                    class="text-center text-sm-left"
                    order="4"
                  >
                    <v-toolbar-title class="date-title">
                      {{ title }}
                    </v-toolbar-title>
                  </v-col>
                  <v-col
                    cols="3"
                    sm="4"
                    class="text-right pr-sm-2"
                    order="4"
                    order-sm="5"
                  >
                    <v-menu bottom right>
                      <template v-slot:activator="{ on }">
                        <v-btn
                          class="px-1"
                          :small="isXsScreen"
                          outlined
                          v-on="on"
                        >
                          <span>{{ typeToLabel[type] }}</span>
                          <v-icon right>mdi-menu-down</v-icon>
                        </v-btn>
                      </template>
                      <v-list>
                        <v-list-item @click="changeView('day')">
                          <v-list-item-title>{{
                              $t("event_calendar.day")
                            }}</v-list-item-title>
                        </v-list-item>
                        <v-list-item @click="changeView('week')">
                          <v-list-item-title>{{
                              $t("event_calendar.week")
                            }}</v-list-item-title>
                        </v-list-item>
                        <v-list-item @click="changeView('month')">
                          <v-list-item-title>{{
                              $t("event_calendar.month")
                            }}</v-list-item-title>
                        </v-list-item>
                      </v-list>
                    </v-menu>
                  </v-col>
                  <v-col
                    cols="1"
                    class="text-right text-sm-left"
                    order="5"
                    order-sm="3"
                  >
                    <v-btn icon :small="isXsScreen" @click="next">
                      <v-icon small>mdi-chevron-right</v-icon>
                    </v-btn>
                  </v-col>
                </v-row>
              </v-toolbar>
            </v-sheet>
            <v-sheet height="600">
              <v-calendar
                id="calendar"
                ref="calendar"
                v-model="focus"
                color="primary"
                :events="filteredEvents"
                :event-color="getEventColor"
                :event-margin-bottom="evtMarginBottom"
                :event-height="evtHeight"
                :now="today"
                :type="type"
                :locale="localeRoot"
                :weekdays="$t('event_calendar.weekdays')"
                @mousedown:event="startDrag"
                @mouseup:day="stopDrag"
                @mousemove:event="doDrag"
                @click:event="showEvent"
                @click:more="viewDay"
                @click:date="viewDay"
                @change="updateRange"
              >
              </v-calendar>
              <v-menu
                v-model="selectedOpen"
                :close-on-content-click="false"
                :activator="selectedElement"
                offset-x
              >
                <v-card color="grey lighten-4" min-width="350px" flat>
                  <v-toolbar :color="selectedEvent.color" dark>
                    <v-col cols="10">
                      <v-toolbar-title
                        v-html="selectedEvent.name"
                      ></v-toolbar-title>
                    </v-col>
                    <v-col v-if="selectedEvent.id" cols="2" class="text-right">
                      <v-icon @click="toEventEdition(selectedEvent)">
                        mdi-pencil
                      </v-icon>
                    </v-col>
                  </v-toolbar>
                  <v-card-text>
                    <v-row v-if="selectedEvent.details">
                      <v-col>
                        <b>{{ $t("event_calendar.description") }}:</b>
                      </v-col>
                      <v-col v-if="selectedEvent.details">
                        <span v-html="selectedEvent.details"></span>
                      </v-col>
                    </v-row>
                    <v-row v-if="selectedEvent.order">
                      <v-col>
                        <b>{{ $t("event_calendar.order") }}:</b>
                      </v-col>
                      <v-col>
                        <span v-html="selectedEvent.order"></span>
                      </v-col>
                    </v-row>
                    <v-row v-if="selectedEvent.state">
                      <v-col>
                        <b>{{ $t("event_calendar.state") }}:</b>
                      </v-col>
                      <v-col>
                        <span v-html="selectedEvent.state"></span>
                      </v-col>
                    </v-row>
                    <v-row v-if="selectedEvent.client">
                      <v-col>
                        <b>{{ $t("event_calendar.client") }}:</b>
                      </v-col>
                      <v-col>
                        <router-link
                          :to="`/staff/clients/${selectedEvent.client.id}`"
                        >
                          {{ selectedEvent.client.fullName }}</router-link
                        >
                      </v-col>
                    </v-row>
                    <v-row v-if="selectedEvent.employee">
                      <v-col>
                        <b>{{ $t("event_calendar.employee.singular") }}:</b>
                      </v-col>
                      <v-col>
                        <router-link
                          :to="`/staff/employees/${selectedEvent.employee.id}`"
                        >
                          {{ selectedEvent.employee.fullName }}</router-link
                        >
                      </v-col>
                    </v-row>
                    <v-row v-else>
                      <v-col class="text-center">
                        <v-btn
                          @click="toDayView(selectedEvent.start)"
                          color="primary"
                        >
                          {{ $t("event_calendar.to_day_view") }}</v-btn
                        >
                      </v-col>
                    </v-row>
                  </v-card-text>
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
import i18n from "@/plugins/i18n";
import generateRandomColor from "@/common/generateRandomColor";
import Autocomplete from "@/components/debouncing-inputs/Autocomplete.vue";
import RepositoryFactory from "@/repositories/RepositoryFactory";

const EventsRepository = RepositoryFactory.get("EventsRepository");

export default {
  components: { Autocomplete },
  data() {
    return {
      startMillis: null,
      endMillis: null,
      type: this.$route.query.typeView || "month",
      today: new Date().toISOString().slice(0, 10),
      focus: this.$route.query.dayView || new Date().toISOString().slice(0, 10),
      selectedOpen: false,
      selectedEvent: {},
      selectedElement: null,
      start: null,
      end: null,
      selectedEmployees:
        this.$route.query.employees?.split(",").map((emp) => parseInt(emp)) ||
        [],
      search: this.$route.query.search || "",
      employeesColor: [],
      typeToLabel: {
        month: this.$t("event_calendar.month"),
        week: this.$t("event_calendar.week"),
        day: this.$t("event_calendar.day"),
      },
      employeeSearch: null,
      dragging: false,
      dragged: false,
      selectedDragging: null,
      events: [],
      employees: [],
      eventsLoading: true,
      employeesLoading: true,
    };
  },
  computed: {
    localeRoot() {
      return i18n.locale;
    },
    isXsScreen() {
      return this.$vuetify.breakpoint.xs;
    },
    title() {
      const { start, end } = this;
      if (!start || !end) {
        return "";
      }

      const monthFormatter = this.$refs.calendar.getFormatter({
        timeZone: "UTC",
        month: "long",
      });

      const startMonth = this.$vuetify.breakpoint.xs
        ? monthFormatter(start).slice(0, 3)
        : monthFormatter(start);
      const startYear = start.year;
      const startDay =
        this.$i18n.locale === "en" ? start.day + nth(start.day) : start.day;

      switch (this.type) {
        case "month":
          return `${startMonth} ${startYear}`;
        case "week":
          return this.$t("event_calendar.title.week.format", {
            year: startYear,
            month: startMonth,
            day: startDay,
          });
        case "category":
        case "day":
          return this.$t("event_calendar.title.day.format", {
            month: startMonth,
            day: startDay,
            weekDay: this.$t("event_calendar.weekdays_name")[start.weekday],
          });
      }
      return "";
    },
    evtHeight() {
      if (this.type === "week" || this.type === "day") return 40;
      return 20;
    },
    evtMarginBottom() {
      if (this.type === "week" || this.type === "day") return 15;
      return 3;
    },
    filteredEvents() {
      return this.events
        .filter((el) => {
          var strToSearch = this.search.toLowerCase();
          // Search by name, description, client label or employee label
          var regex = new RegExp(strToSearch, "gi");
          if (el.name.match(regex) || el.details.match(regex)) return el;
        })
        .sort(function (a, b) {
          return (
            (a.employee && b.employee
              ? a.employee.fullName.localeCompare(b.employee.fullName)
              : 0) || a.order - b.order
          );
        });
    },
  },
  watch: {
    employeeSearch() {
      this.getEmployees();
    },
    selectedEmployees() {
      this.updateRoute();
      this.getEvents();
    },
  },
  created() {
    let day = this.$route.query.dayView
      ? new Date(this.$route.query.dayView)
      : new Date();

    this.updateDatesRange(day, this.$route.query.typeView);

    this.getEmployees();
    this.getEvents();
  },
  mounted() {
    this.$refs.calendar.checkChange();
    this.setDayButtonWidth();
  },
  methods: {
    getEmployees() {
      this.employeesLoading = true;
      const options = {
        params: {
          filters: this.employeeSearch
            ? `fullName:${this.employeeSearch}`
            : undefined,
        },
      };
      return EventsRepository.getEmployeesByParams(options)
        .then((res) => (this.employees = res.content))
        .finally(() => (this.employeesLoading = false));
    },
    getEvents() {
      this.eventsLoading = true;
      const options = {
        params: {
          employees: this.selectedEmployees?.toString(),
          start: this.startMillis,
          end: this.endMillis,
          view: this.type,
        },
      };
      return EventsRepository.getCalendarEvents(options)
        .then((res) => {
          this.events = res.content.map((el) => {
            return {
              id: el.id,
              name:
                el.client && el.employee
                  ? el.client.fullName + " (" + el.employee.fullName + ")"
                  : this.$t("event_calendar.clustered_event.title", {
                    date: localDateToVCalendarString(el.date),
                    count: el.description,
                  }),
              details: el.client ? el.description : null,
              order: el.eventOrder,
              start: localDateToVCalendarString(el.date),
              end: localDateToVCalendarString(el.date),
              client: el.client,
              employee: el.employee,
              state: el.state,
              geom: el.geom,
              realtime: el.realtime,
              route: el.route,
              color: el.employee
                ? this.getColorForEmployee(el.employee)
                : "blue",
            };
          });
        })
        .finally(() => (this.eventsLoading = false));
    },
    startDrag(evt) {
      event.preventDefault();
      this.dragging = true;
      this.selectedDragging = evt.event;
    },
    doDrag() {
      if (this.dragging) {
        this.dragged = true;
        document.getElementById("calendar").style.cursor = "move";
      }
    },
    stopDrag(evt) {
      if (this.dragged) {
        // If its a move from same day
        if (evt.date === this.selectedDragging.start) {
          this.changeDayOrder(event, evt.date, this.selectedDragging);
        }
        // If its a move from a day to another day
        else {
          const dayFrom = this.selectedDragging.start;

          // Change event
          this.selectedDragging.end = evt.date;
          this.selectedDragging.start = evt.date;

          // Reorder the 2 days
          this.changeDayOrder(event, evt.date, this.selectedDragging);
          this.changeDayOrder(event, dayFrom, this.selectedDragging);
        }
      }
      this.dragged = false;
      this.dragging = false;
      document.getElementById("calendar").style.cursor = "default";
      this.selectedDragging = null;
    },
    changeDayOrder(event, date, element) {
      // Get event names of the day
      let dayEvents = this.filteredEvents
        .filter((el) => el.start === date)
        .map((el) => el.name);

      // Get div elements of the events
      let htmlEvents = document.getElementsByClassName("pl-1");
      htmlEvents = Array.prototype.filter.call(htmlEvents, (el) =>
        dayEvents.includes(el.innerText)
      );

      // Set new positions
      for (let index = 0; index < htmlEvents.length; index++) {
        if (htmlEvents[index].innerHTML === element.name) {
          htmlEvents[index].newY = event.clientY;
        } else {
          htmlEvents[index].newY = htmlEvents[index].getBoundingClientRect().y;
        }
      }

      // Get the events of the day that correspond to the same employee as that of the changed event
      const employeeEvents = this.filteredEvents.filter(
        (el) => el.start === date && el.employee.id === element.employee.id
      );
      // And we sort and filter htmlEvents again to keep only the events of that employee
      htmlEvents = htmlEvents
        .sort((a, b) => a.newY - b.newY)
        .filter((el) => employeeEvents.find((e) => e.name === el.innerHTML));

      // Set new events with order
      const newEvents = employeeEvents.map((el) => {
        el.order = htmlEvents.findIndex((e) => e.innerHTML === el.name) + 1;
        return el;
      });

      let promises = [];
      // Persist changes
      newEvents.forEach((el) => {
        const eventToSave = {
          id: el.id,
          description: el.details,
          client: el.client,
          employee: el.employee,
          eventOrder: el.order,
          date: el.start.split("-").map((item) => parseInt(item)),
          state: el.state,
          geom: el.geom,
          route: el.route,
          realtime: el.realtime,
        };
        // Update Data
        promises.push(
          EventsRepository.save(eventToSave)
        );
      });

      Promise.all(promises).then(() => this.getEvents());
    },
    updateSize(size) {
      this.eventsPage.pageSize = size;
    },
    toEventEdition(event) {
      this.$router.push({
        name: "EventForm",
        params: {
          id: event.id,
          backPrevious: true,
        },
      });
    },
    getEventColor(event) {
      return event.color;
    },
    getColorForEmployee(employee) {
      const employeeColor = this.employeesColor.find(
        (el) => el.id === employee.id
      );
      if (employeeColor) {
        return employeeColor.color;
      } else {
        var color = generateRandomColor();
        while (this.employeesColor.find((el) => el.color === color)) {
          color = generateRandomColor();
        }
        this.employeesColor.push({
          id: employee.id,
          color: color,
        });
        return color;
      }
    },
    updateRoute() {
      this.$router.replace({
        name: "EventCalendar",
        query: {
          employees:
            this.selectedEmployees.length === 0
              ? undefined
              : this.selectedEmployees.join(","),
          search: this.search === "" ? undefined : this.search,
          dayView: this.focus,
          typeView: this.type,
        },
      });
    },
    redirect() {
      let query = JSON.parse(JSON.stringify(this.$route.query));
      query.dayView = this.focus;
      query.typeView = this.type;

      if (JSON.stringify(this.$route.query) !== JSON.stringify(query)) {
        this.$router.replace({
          name: this.$route.name,
          query: query,
        });
      }
    },
    nth(d) {
      return d > 3 && d < 21
        ? "th"
        : ["th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"][d % 10];
    },
    updateRange({ start, end }) {
      // You could load events from an outside source (like database) now that we have the start and end dates on the calendar
      this.start = start;
      this.end = end;
    },
    intervalFormat(interval) {
      return interval.time;
    },
    showEvent({ nativeEvent, event }) {
      const open = () => {
        this.selectedEvent = event;
        this.selectedElement = nativeEvent.target;
        setTimeout(() => (this.selectedOpen = true), 10);
      };

      if (this.selectedOpen) {
        this.selectedOpen = false;
        setTimeout(open, 10);
      } else {
        open();
      }

      nativeEvent.stopPropagation();
    },
    updateDatesRange(focus, viewType) {
      if (viewType === "week") {
        this.startMillis = new Date(
          focus.getFullYear(),
          focus.getMonth(),
          focus.getDate() - 6
        ).getTime();
        this.endMillis = new Date(
          focus.getFullYear(),
          focus.getMonth(),
          focus.getDate() + 6
        ).getTime();
      } else if (viewType === "day") {
        this.startMillis = new Date(
          focus.getFullYear(),
          focus.getMonth(),
          focus.getDate() - 1
        ).getTime();
        this.endMillis = new Date(
          focus.getFullYear(),
          focus.getMonth(),
          focus.getDate() + 1
        ).getTime();
      } else {
        this.startMillis = new Date(
          focus.getFullYear(),
          focus.getMonth(),
          -6
        ).getTime();
        this.endMillis = new Date(
          focus.getFullYear(),
          focus.getMonth() + 1,
          6
        ).getTime();
      }
    },
    viewDay({ date }) {
      this.focus = date;
      this.type = "day";
      this.updateDatesRange(new Date(date), "day");
      this.redirect();
    },
    toDayView(date) {
      this.viewDay({ date });
      this.selectedOpen = false;
    },
    setDayButtonWidth() {
      // Fixes button width issue on small screens
      setTimeout(() => {
        if (this.$vuetify.breakpoint.xs) {
          let dayButtonElements = document.getElementsByClassName(
            "v-btn v-btn--depressed v-btn--fab v-btn--round theme--light v-size--default"
          );
          Array.prototype.forEach.call(dayButtonElements, (dayButton) => {
            if (this.type === "week") {
              dayButton.style.width = "fit-content";
            } else if (this.type === "day") {
              dayButton.style.width = "56px";
            }
          });
        }
      }, 0);
    },
    changeView(type) {
      this.type = type;
      this.updateDatesRange(new Date(this.focus), type);
      this.redirect();
      this.setDayButtonWidth();
    },
    next() {
      this.$refs.calendar.next();
      this.updateDatesRange(new Date(this.focus), this.type);
      this.redirect();
    },
    prev() {
      this.$refs.calendar.prev();
      this.updateDatesRange(new Date(this.focus), this.type);
      this.redirect();
    },
    setToday() {
      this.focus = this.today;
      this.updateDatesRange(new Date(this.today), this.type);
      this.redirect();
    },
  },
};
</script>
<style lang="css" scoped>
.v-calendar-daily >>> .v-calendar-daily__head {
  height: 100%;
  overflow-y: auto;
  margin-right: 0 !important;
}
.v-menu__content {
  min-width: 350px !important;
}
>>> .v-toolbar__content {
  padding: 0px;
}
.v-application--is-ltr .v-btn__content .v-icon--right {
  margin-left: 0px !important;
}
@media (max-width: 600px) {
  .date-title {
    font-size: large;
    white-space: pre-line;
    margin-bottom: 20px;
  }
}
</style>
/*% } %*/
