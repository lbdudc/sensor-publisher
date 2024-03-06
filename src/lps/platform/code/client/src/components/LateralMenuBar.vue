/*% if ( !feature.GUI_M_Custom && (feature.GUI_M_Top || feature.GUI_M_Left) ) { %*/
<template>
  <!-- Menú lateral -->
  <v-navigation-drawer v-model="localDrawer" app>
  /*% if (feature.UserManagement) { %*/
    <template v-slot:prepend>
      <v-list-item two-line v-if="isLogged">
        <v-list-item-avatar>
          <v-icon size="50">account_circle</v-icon>
        </v-list-item-avatar>
        <v-list-item-content>
          <v-list-item-title>{{ user.login }}</v-list-item-title>
          <v-list-item-subtitle>{{ loggedAs }}</v-list-item-subtitle>
        </v-list-item-content>
      </v-list-item>
    </template>

    <v-divider v-if="isLogged" />
  /*% } %*/

    <v-list dense>
  /*% if (feature.UserManagement) { %*/
      <v-list-item to="/account/login" v-if="!isLogged">
        <v-list-item-icon>
          <v-icon>mdi-login</v-icon>
        </v-list-item-icon>
        <v-list-item-content>
          <v-list-item-title>{{ $t('menu.logIn') }}</v-list-item-title>
        </v-list-item-content>
      </v-list-item>

      /*% if (feature.UM_UserProfile) { %*/
      <v-list-item v-if="isLogged" :to="{ name: 'Profile' }">
        <v-list-item-icon>
          <v-icon>account_circle</v-icon>
        </v-list-item-icon>
        <v-list-item-content>
          <v-list-item-title>{{ $t('menu.myProfile') }}</v-list-item-title>
        </v-list-item-content>
      </v-list-item>

      <v-divider v-if="isLogged" />
      /*% } %*/
  /*% } %*/

      <v-list-group no-action>
        <template v-slot:activator>
          <v-list-item-icon>
            <v-icon>mdi-web</v-icon>
          </v-list-item-icon>
          <v-list-item-content>
            <v-list-item-title>{{ $t('menu.language') }}</v-list-item-title>
          </v-list-item-content>
        </template>
  /*%
    data.basicData.languages.forEach(function(lang) {
  %*/
        <v-list-item
          @click="changeLocale('/*%= lang.toLocaleUpperCase() %*/')"
        >
          <v-list-item-title>{{ $t('languages./*%= normalize(lang) %*/') }}</v-list-item-title>
        </v-list-item>
  /*%
    });
  %*/
      </v-list-group>

    /*% if (feature.UM_UserCRUD) { %*/
      <v-list-item v-if="isAdmin" :to="{ name: 'UserManagementList' }" exact>
        <v-list-item-icon>
          <v-icon>mdi-account-group</v-icon>
        </v-list-item-icon>
        <v-list-item-content>
          <v-list-item-title>{{ $t('menu.userManagement') }}</v-list-item-title>
        </v-list-item-content>
      </v-list-item>
    /*% } %*/
  /*% if (data.lists.length > 0) { %*/
      <v-list-group no-action>
        <template v-slot:activator>
          <v-list-item-icon>
            <v-icon>list</v-icon>
          </v-list-item-icon>
          <v-list-item-content>
            <v-list-item-title>{{ $t('menu.lists') }}</v-list-item-title>
          </v-list-item-content>
        </template>
        /*% data.lists.forEach(function(list) { %*/
        <v-list-item :to="{ name: '/*%= list.id %*/' }">
          <v-list-item-content>
            <v-list-item-title>{{ $t('t_/*%= normalize(list.entity) %*/.headers./*%= normalize(list.id) %*/') }}</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
        /*% }); %*/
      </v-list-group>
  /*% } %*/
  /*% if (feature.MWM_PersonnelManager) { %*/
      <v-list-group no-action>
        <template v-slot:activator>
          <v-list-item-icon>
            <v-icon>supervised_user_circle</v-icon>
          </v-list-item-icon>
          <v-list-item-content>
            <v-list-item-title>{{ $t('menu.management') }}</v-list-item-title>
          </v-list-item-content>
        </template>
        /*% if (feature.MWM_PM_Clients) { %*/
        <v-list-item :to="{ name: 'ClientList' }">
          <v-list-item-content>
            <v-list-item-title>{{ $t('menu.clientList') }}</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
        /*% } %*/
        /*% if (feature.MWM_PM_Employees) { %*/
        <v-list-item :to="{ name: 'EmployeeList' }">
          <v-list-item-content>
            <v-list-item-title>{{ $t('menu.employeeList') }}</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
        /*% } %*/
      </v-list-group>
  /*% } %*/
  /*% if (feature.MWM_VisitSchedule) { %*/
      <v-list-group no-action>
        <template v-slot:activator>
          <v-list-item-icon>
            <v-icon>event_note</v-icon>
          </v-list-item-icon>
          <v-list-item-content>
            <v-list-item-title>{{ $t('menu.planning') }}</v-list-item-title>
          </v-list-item-content>
        </template>
        <v-list-item :to="{ name: 'EventList' }" exact>
          <v-list-item-icon>
            <v-icon>list</v-icon>
          </v-list-item-icon>
          <v-list-item-content>
            <v-list-item-title>{{ $t('menu.plannedVisits') }}</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
        <v-list-item :to="{ name: 'EventCalendar' }" exact>
          <v-list-item-icon>
            <v-icon>event</v-icon>
          </v-list-item-icon>
          <v-list-item-content>
            <v-list-item-title>{{ $t('menu.visitCalendar') }}</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
      </v-list-group>
  /*% } %*/
  /*% if (feature.MWM_TrajectoryExploitation) { %*/
      <v-list-group no-action>
        <template v-slot:activator>
          <v-list-item-icon>
            <v-icon>person</v-icon>
          </v-list-item-icon>
          <v-list-item-content>
            <v-list-item-title>{{ $t('menu.mobilityAnalysis') }}</v-list-item-title>
          </v-list-item-content>
        </template>
        /*% if (feature.MWM_TE_Realtime) { %*/
        <v-list-item :to="{ name: 'Realtime' }" exact>
          <v-list-item-icon>
            <v-icon>schedule</v-icon>
          </v-list-item-icon>
          <v-list-item-content>
            <v-list-item-title>{{ $t('menu.realtime') }}</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
        /*% } %*/
        /*% if (feature.MWM_TE_Planning) { %*/
        <v-list-item :to="{ name: 'Planning' }" exact>
          <v-list-item-icon>
            <v-icon>update</v-icon>
          </v-list-item-icon>
          <v-list-item-content>
            <v-list-item-title>{{ $t('menu.planning') }}</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
        /*% } %*/
        /*% if (feature.MWM_TE_ActivitiesRecord) { %*/
        <v-list-item :to="{ name: 'Record' }" exact>
          <v-list-item-icon>
            <v-icon>history</v-icon>
          </v-list-item-icon>
          <v-list-item-content>
            <v-list-item-title>{{ $t('menu.activityHistory') }}</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
        /*% } %*/
        /*% if (feature.MWM_TE_VisitsRecord) { %*/
        <v-list-item :to="{ name: 'VisitsRecord' }" exact>
          <v-list-item-icon>
            <v-icon>history</v-icon>
          </v-list-item-icon>
          <v-list-item-content>
            <v-list-item-title>{{ $t('menu.visits_record') }}</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
        /*% } %*/
        /*% if (feature.MWM_TE_Statistics) { %*/
        <v-list-item :to="{ name: 'Activities Statistics' }" exact>
          <v-list-item-icon>
            <v-icon>timeline</v-icon>
          </v-list-item-icon>
          <v-list-item-content>
            <v-list-item-title>{{ $t('menu.activitiesStatistics') }}</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
        /*% } %*/
        /*% if (feature.MWM_TE_QueryView) { %*/
        <v-list-item :to="{ name: 'MobilityQuery' }">
          <v-list-item-icon>
            <v-icon>not_listed_location</v-icon>
          </v-list-item-icon>
          <v-list-item-title>
            {{ $t("menu.mobilityQuery") }}
          </v-list-item-title>
        </v-list-item>
        /*% } %*/
      </v-list-group>
  /*% } %*/
  /*% if (feature.MV_MM_UniqueMapViewer || feature.MV_MM_MMV_MapSelectorInMapViewer || feature.MV_MM_MMV_MapSelectorInMenuElement || feature.GUI_StaticPages || feature.MWM_TA_SensorDataCollector || feature.DM_DI_DF_Shapefile || feature.DM_A_G_Batch) { %*/
      <v-list-group no-action>
        <template v-slot:activator>
          <v-list-item-icon>
            <v-icon>category</v-icon>
          </v-list-item-icon>
          <v-list-item-content>
            <v-list-item-title>{{ $t('menu.components') }}</v-list-item-title>
          </v-list-item-content>
        </template>
        /*% if (feature.MV_MM_UniqueMapViewer || (feature.MV_MM_MMV_MapSelectorInMapViewer && !feature.MV_MM_MMV_MapSelectorInMenuElement)) { %*/
        <v-list-item :to="{ name: 'MapViewer'/*% if (feature.MV_MM_UniqueMapViewer) { %*/, params: { id: 'default' } /*% } %*/ }">
          <v-list-item-icon>
            <v-icon>mdi-map</v-icon>
          </v-list-item-icon>
          <v-list-item-content>
            {{ $t('menu.mapViewer') }}
          </v-list-item-content>
        </v-list-item>
        /*% } %*/
        /*% if (feature.MV_MM_MMV_MapSelectorInMenuElement) { %*/
        <v-list-item>
          <v-list-item-content>
            <v-list-group no-action sub-group>
              <template v-slot:activator>
                <v-list-item-title>{{ $t("menu.maps") }}</v-list-item-title>
              </template>
              /*% data.mapViewer.maps.forEach(function(map) { %*/
              <v-list-item :to="{ name: 'MapViewer', params: {id: '/*%= map.name %*/'} }">
                {{ $t("mapViewer.map-label./*%= map.name.replace('.', '-') %*/") }}
              </v-list-item>
              /*% }); %*/
            </v-list-group>
          </v-list-item-content>
        </v-list-item>
        /*% } %*/
      /*% if (feature.GUI_StaticPages) { %*/
        <v-list-item :to="{ name: 'Static_PageList' }" exact>
          <v-list-item-icon>
            <v-icon>mdi-file-document-box-multiple</v-icon>
          </v-list-item-icon>
          <v-list-item-content>
            <v-list-item-title>{{ $t('menu.staticPages') }}</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
      /*% } %*/
      /*% if (feature.MWM_TA_SensorDataCollector) { %*/
        <v-list-item :to="{ name: 'SensorData' }" exact>
          <v-list-item-icon>
            <v-icon>mdi-motion-sensor</v-icon>
          </v-list-item-icon>
          <v-list-item-content>
            <v-list-item-title>{{ $t('menu.sensorData') }}</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
      /*% } %*/
      /*% if (feature.DM_DI_DF_Shapefile) { %*/
        <v-list-item :to="{ name: 'Shapefile' }" exact>
          <v-list-item-icon>
            <v-icon>mdi-file-upload</v-icon>
          </v-list-item-icon>
          <v-list-item-content>
            <v-list-item-title>{{ $t('menu.shapefile') }}</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
      /*% } %*/
      /*% if (feature.DM_A_G_Batch) { %*/
        <v-list-item :to="{ name: 'AutoAssign' }" exact>
          <v-list-item-icon>
            <v-icon>mdi-earth</v-icon>
          </v-list-item-icon>
          <v-list-item-content>
            <v-list-item-title>{{ $t('menu.autoassign') }}</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
      /*% } %*/
        /*% if (feature.T_Payments) { %*/
        <v-list-item :to="{ name: 'Payments' }" exact>
          <v-list-item-icon>
            <v-icon>mdi-cash</v-icon>
          </v-list-item-icon>
          <v-list-item-content>
            <v-list-item-title>{{ $t('menu.payments') }}</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
      /*% } %*/
      </v-list-group>
  /*% } %*/
    </v-list>
  /*% if (feature.UserManagement) { %*/
    <template v-slot:append v-if="isLogged">
      <div class="pa-2">
        <v-btn block @click="logout()">{{ $t('menu.logout') }}</v-btn>
      </div>
    </template>
  /*% } %*/
  </v-navigation-drawer>
</template>

<script>
/*% if (feature.UserManagement) { %*/
import auth from "@/common/auth";
import {mapAuthGetter} from "@/common/mapAuthGetter";
/*% } %*/

export default {
  name: "LateralMenuBar",
  props: {
    drawer: {
      type: Boolean,
      required: true
    }
  },
  /*% if (feature.UserManagement) { %*/
  data() {
    return {
      user: auth.getUser()
    };
  },
  /*% } %*/
  computed: {
  /*% if (feature.UserManagement) { %*/
    ...mapAuthGetter(["isAdmin", "isLogged",/*% if (feature.MWM_EmployeeAuthentication) { %*/ "isEmployee"/*% } %*/]),
    loggedAs() {
      return this.isLogged
        ? this.$t("menu.loggedAs") +
        (this.isAdmin
          ? this.$t("menu.admin")
          /*% if (feature.MWM_EmployeeAuthentication) { %*/
          : this.isEmployee
            ? this.$t("menu.employee")
          /*% } %*/
          : this.$t("menu.registeredUser"))
        : "";
    },
  /*% } %*/
    localDrawer: {
      get() {
        return this.drawer;
      },
      set(val) {
        this.$emit("drawer-changed", val);
      }
    }
  },
  methods: {
    changeLocale(locale) {
      this.$i18n.locale = locale;
      /*% if (feature.UserManagement) { %*/
      auth.changeUserLang(locale);
      /*% } %*/
    },
  /*% if (feature.UserManagement) { %*/
    logout() {
      auth.logout()
        .then(
          this.$router.push({ name: "Home" })
        .catch(err => {
          //Do nothing if navigating to current route, otherwise throw error
          if (!err.name === 'NavigationDuplicated') {
            throw err;
          }
        }));
    },
  /*% } %*/
  }
};
</script>
<style scoped>
.v-list-item__title,
.v-list-item__subtitle {
  white-space: normal;
}
</style>
/*% } %*/
