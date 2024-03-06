/*% if (feature.UM_UserProfile) { %*/
<template>
  <v-container>
    <v-card>
      <v-card-title>
        <v-row align="center" no-gutters>
          <v-col cols="5" class="d-none d-md-block">
            <span class="headline no-split-words">
              {{ $t($route.meta.label) }}
            </span>
          </v-col>
          <v-col cols="12" md="7" class="text-center text-sm-right">
            <v-btn class="warning" :to="{ name: 'EditProfile' }">
              <v-icon>edit</v-icon>
              {{ $t("account.editProfile") }}
            </v-btn>
            /*% if (feature.UM_UP_ByUser) { %*/
            <v-btn class="primary ml-3 mt-4 mt-sm-0" :to="{ name: 'ChangePassword' }">
              <v-icon>vpn_key</v-icon>
              {{ $t("account.changePassword") }}
            </v-btn>
            /*% } %*/
          </v-col>
        </v-row>
      </v-card-title>
      <v-card-text>
        <v-row align="center" dense>
          <v-col cols="4" sm="2" lg="1" xl="1" class="text-right">
            {{ $t("account.login") }}:
          </v-col>
          <v-col cols="8" sm="4" lg="3" xl="2">
            <b>{{ user.login }}</b>
          </v-col>
          <v-col cols="4" sm="2" lg="1" xl="1" class="text-right">
            {{ $t("account.email") }}:
          </v-col>
          <v-col cols="8" sm="4" lg="3" xl="2">
            <b>{{ user.email }}</b>
          </v-col>
          <v-col cols="4" sm="2" lg="1" xl="1" class="text-right">
            {{ $t("account.firstName") }}:
          </v-col>
          <v-col cols="8" sm="4" lg="3" xl="2">
            <b>{{ user.firstName }}</b>
          </v-col>
          <v-col cols="4" sm="2" lg="1" xl="1" class="text-right">
            {{ $t("account.lastName") }}:
          </v-col>
          <v-col cols="8" sm="4" lg="3" xl="2">
            <b>{{ user.lastName }}</b>
          </v-col>
          <v-col cols="4" sm="2" lg="1" xl="1" class="text-right">
            {{ $t("account.language") }}:
          </v-col>
          <v-col cols="8" sm="4" lg="3" xl="2">
            <b>{{ user.langKey }}</b>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script>
import auth from "@/common/auth";

export default {
  name: "Profile",
  mounted() {
    auth.isAuthenticationChecked();
  },
  data() {
    return {
      user: auth.getUser()
    };
  }
};
</script>
/*% } %*/
