/*% if (feature.MWM_PM_Clients) { %*/
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
            <v-btn @click="back()">
              <v-icon>arrow_back</v-icon>
              <span class="d-none d-sm-block"> {{ $t("back") }} </span>
            </v-btn>

            <v-btn
            /*% if (feature.MWM_EmployeeAuthentication) { %*/
            v-if="isAdmin"
            /*% } %*/
            class="warning ml-2" @click="edit(client)">
              <v-icon>edit</v-icon>
              <span class="d-none d-sm-block"> {{ $t("edit") }} </span>
            </v-btn>

            <v-btn
            /*% if (feature.MWM_EmployeeAuthentication) { %*/
            v-if="isAdmin"
            /*% } %*/
            :color="client.active ? 'error' : 'success'"
            class="ml-2" @click="changeClientState">
              <v-icon>{{
                client.active ? "visibility_off" : "visibility"
                }}</v-icon>
              <span class="d-none d-sm-block">
                  {{
                    client.active
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
                {{ client.fullName }}
              </v-col>

              <v-col cols="3" class="text-right">
                <b> {{ $t("staff_crud.email") }}: </b>
              </v-col>
              <v-col cols="9">
                {{ client.email }}
              </v-col>

              <v-col cols="3" class="text-right">
                <b> {{ $t("staff_crud.phoneNumber") }}: </b>
              </v-col>
              <v-col cols="9">
                {{ client.phone }}
              </v-col>

              <v-col cols="3" class="text-right">
                <b> {{ $t("staff_crud.address") }}: </b>
              </v-col>
              <v-col cols="9">
                {{ client.address }}
              </v-col>
            </v-row>
          </v-col>

          <v-col cols="12" md="6">
            <v-row align="center" no-gutters>
              <v-col cols="12" class="text-center">
                <b>{{ $t("staff_crud.location") }}:</b>
              </v-col>

              <v-col>
                <map-field
                  v-bind:entity="client"
                  :editable="false"
                  propName="location"
                  entityName="client"
                ></map-field>
              </v-col>
            </v-row>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>

    <loading-page v-if="isLoading"></loading-page>
  </v-container>
</template>

<script>
/*% if (feature.MWM_EmployeeAuthentication) { %*/
import { mapAuthGetter } from "@/common/mapAuthGetter";
import auth from "@/common/auth";
/*% } %*/
import checkInvalidID from "@/common/checkInvalidID";
import MapField from "@/components/map-field/MapField.vue";
import LoadingPage from "@/components/loading-page/LoadingPage.vue";
import RepositoryFactory from "@/repositories/RepositoryFactory";
const StaffCrudRepository = RepositoryFactory.get("StaffCrudRepository");

export default {
  name: "ClientDetail",
  components: { MapField, LoadingPage },
  data() {
    return {
      client: {},
      loading: false
    }
  },
  computed: {
    /*% if (feature.MWM_EmployeeAuthentication) { %*/
    ...mapAuthGetter(["isAdmin", "isLogged"]),
    /*% } %*/
    isLoading() {
      return this.loading;
    }
  },
  beforeRouteEnter(to, from, next) {
    next(that => {
      /*% if (feature.MWM_EmployeeAuthentication) { %*/
      // Si el usuario no es administrador, sólo puede acceder a los datos de los clientes activos
      const isAuthenticationChecked = auth.isAuthenticationChecked();
      isAuthenticationChecked.finally(async () => {
        await that._fetchData(to.params.id);
        (that.client && that.client.active) || (that.isLogged && that.isAdmin)
          ? next()
          : next({ name: "ClientList", replace: true });
      });
      /*% } else { %*/
      that._fetchData(to.params.id);
      next();
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
      return StaffCrudRepository.getClient(id)
        .then(data => this.client = data)
        .catch(err => checkInvalidID(err))
        .finally(() => (this.loading = false));
    },
    edit() {
      const selection = window.getSelection().toString();
      if (selection.length === 0) {
        this.$router.push({
          name: "ClientForm",
          params: { id: this.client.id, backPrevious: true }
        });
      }
    },
    back() {
      this.$router.push({ name: "ClientList", params: { backAction: true } });
    },
    changeClientState() {
      const method = this.client.active
        ? StaffCrudRepository.deleteClient
        : StaffCrudRepository.activateClient;
      method(this.client.id)
        .then(() => this.client.active = !this.client.active);
    }
  }
};
</script>
/*% } %*/
