/*% if (feature.MWM_VisitSchedule) { %*/
<template>
  <v-container>
    <v-card v-if="!isLoading">
      <v-card-title>
        <v-row align="center" justify="space-between" no-gutters>
          <v-col cols="6" class="d-none d-md-block">
            <span class="headline no-split-words">
              {{ $t($route.meta.label) }}
            </span>
          </v-col>
          <v-col class="text-right">
            <v-btn @click="back">
              <v-icon class="material-icons">arrow_back</v-icon>
              <span class="d-none d-sm-block"> {{ $t("back") }} </span>
            </v-btn>

            <v-btn class="warning ml-2" @click="edit">
              <v-icon class="material-icons details-close-button">edit</v-icon>
              <span class="d-none d-sm-block"> {{ $t("edit") }} </span>
            </v-btn>

            <v-btn class="error ml-2" @click="showDeleteDialog">
              <v-icon class="material-icons details-close-button"
              >delete</v-icon
              >
              <span class="d-none d-sm-block"> {{ $t("remove") }} </span>
            </v-btn>
          </v-col>
        </v-row>
      </v-card-title>
      <v-card-text>
        <v-row align="center" no-gutters>
          <v-col cols="12" md="6">
            <v-row align="center" dense>
              <v-col cols="3" class="text-right">
                <b> {{ $t("planned_event_crud.description") }}: </b>
              </v-col>
              <v-col cols="9">
                {{ event.description }}
              </v-col>

              <v-col cols="3" class="text-right">
                <b> {{ $t("planned_event_crud.address") }}: </b>
              </v-col>
              <v-col cols="9">
                {{ event.address }}
              </v-col>

              <v-col cols="3" class="text-right">
                <b> {{ $t("planned_event_crud.date") }}: </b>
              </v-col>
              <v-col cols="9">
                <span>
                  {{ event.date | dateFormat }}
                </span>
              </v-col>

              /*% if (feature.MVM_VT_WithTime) { %*/
              <v-col cols="3" class="text-right">
                <b> {{ $t('planned_event_crud.start') }}: </b>
              </v-col>
              <v-col cols="9">
                <span>
                  {{ event.startTime | timeFormat }}
                </span>
              </v-col>
              <v-col cols="3" class="text-right">
                <b> {{ $t('planned_event_crud.end') }}: </b>
              </v-col>
              <v-col cols="9">
                <span>
                  {{ event.endTime | timeFormat }}
                </span>
              </v-col>
              /*% } %*/

              <v-col cols="3" class="text-right">
                <b> {{ $t("planned_event_crud.state") }}: </b>
              </v-col>
              <v-col cols="9">
                <span v-if="event.state">
                  {{ $t("planned_event_crud.state_values." + event.state) }}
                </span>
              </v-col>

              <v-col cols="3" class="text-right">
                <b> {{ $t("planned_event_crud.client") }}: </b>
              </v-col>
              <v-col cols="9">
                <router-link
                  v-if="event.client && event.client.active"
                  :to="{
                    name: 'ClientDetail',
                    params: { id: event.client.id, backPrevious: true },
                  }"
                >
                  {{ event.client.fullName }}
                </router-link>
                <span v-if="event.client && !event.client.active">
                  {{ event.client.fullName }}
                </span>
              </v-col>

              <v-col cols="3" class="text-right">
                <b> {{ $t("planned_event_crud.employee") }}: </b>
              </v-col>
              <v-col cols="9">
                <router-link
                  v-if="event.employee && event.employee.active"
                  :to="{
                    name: 'EmployeeDetail',
                    params: { id: event.employee.id, backPrevious: true },
                  }"
                >
                  {{ event.employee.fullName }}
                </router-link>
                <span v-if="event.client && !event.client.active">
                  {{ event.employee.fullName }}
                </span>
              </v-col>
            </v-row>
          </v-col>

          <v-col cols="12" md="6">
            <v-row align="center" no-gutters>
              <v-col cols="12" class="text-center">
                <b> {{ $t("planned_event_crud.location") }}: </b>
              </v-col>
              <v-col>
                <map-field
                  v-bind:entity="event"
                  :editable="false"
                  propName="geom"
                  entityName="event"
                ></map-field>
              </v-col>
            </v-row>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>

    <loading-page v-if="isLoading"></loading-page>

    <delete-dialog
      :dialog="dialog"
      @cancel="dialog = false"
      @submit="deleteEvent"
    ></delete-dialog>
  </v-container>
</template>

<script>
import DeleteDialog from "@/components/modal_dialog/DeleteDialog";
import MapField from "@/components/map-field/MapField.vue";
import LoadingPage from "@/components/loading-page/LoadingPage.vue";
import checkInvalidID from "@/common/checkInvalidID";

import RepositoryFactory from "@/repositories/RepositoryFactory";
const EventsRepository = RepositoryFactory.get("EventsRepository");

export default {
  name: "EventDetail",
  components: { DeleteDialog, MapField, LoadingPage },
  data() {
    return {
      dialog: false,
      event: {},
      loading: true,
    };
  },
  computed: {
    isLoading() {
      return this.loading;
    },
  },
  beforeRouteUpdate(to, from, next) {
    this._fetchData(to.params.id);
    next();
  },
  created() {
    this._fetchData(this.$route.params.id);
  },
  methods: {
    _fetchData(id) {
      this.loading = true;
      return EventsRepository.get(id)
        .then((res) => (this.event = res))
        .catch(err => checkInvalidID(err))
        .finally(() => (this.loading = false));
    },
    edit() {
      this.$router.push({
        name: "EventForm",
        params: { id: this.event.id, backPrevious: true },
      });
    },
    back() {
      this.$router.push({
        name: "EventList",
        params: { backAction: true }
      });
    },
    deleteEvent() {
      this.loading = true;
      EventsRepository.delete(this.event.id)
        .then(() => {
          this.dialog = false;
          this.$router.push({ name: "EventList" });
        })
        .finally(() => (this.loading = false));
    },
    showDeleteDialog() {
      this.dialog = true;
    },
  },
};
</script>
/*% } %*/
