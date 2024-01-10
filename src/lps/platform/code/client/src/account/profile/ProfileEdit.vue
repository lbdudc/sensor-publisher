/*% if (feature.UM_UserProfile) { %*/
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
          <v-col cols="12" md="6" class="text-right">
            <v-btn @click="$router.back()">
              <v-icon>mdi-arrow-left</v-icon>
              <span class="d-none d-sm-block">
                {{ $t("account.cancel") }}
              </span>
            </v-btn>
            <v-btn class="success ml-3" @click="save">
              <v-icon>save</v-icon>
              <span class="d-none d-sm-block">
                {{ $t("account.save") }}
              </span>
            </v-btn>
          </v-col>
        </v-row>
      </v-card-title>
      <v-card-text>
        <v-form v-model="validForm">
          <v-row>
            <v-col cols="12" sm="6">
              <v-text-field
                v-model="profile.firstName"
                prepend-inner-icon="notes"
                :label="$t('account.firstName')"
              ></v-text-field>
            </v-col>
            <v-col cols="12" sm="6" md="6">
              <v-text-field
                v-model="profile.lastName"
                prepend-inner-icon="notes"
                :label="$t('account.lastName')"
              ></v-text-field>
            </v-col>
            <v-col cols="12" sm="6" md="6">
              <v-text-field
                v-model="profile.email"
                prepend-inner-icon="email"
                :label="$t('account.email')"
                :rules="emailRules"
              ></v-text-field>
            </v-col>
            <v-col cols="12" sm="6" md="6">
              <v-select
                v-model="profile.langKey"
                prepend-inner-icon="language"
                :items="languages"
                :label="$t('account.language')"
                :item-text="item => $t(item.label)"
                item-value="key"
                :menu-props="{ offsetY: true }"
              ></v-select>
            </v-col>
          </v-row>
        </v-form>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script>
import auth from "@/common/auth";
import RepositoryFactory from "@/repositories/RepositoryFactory";
const AccountRepository = RepositoryFactory.get("AccountRepository");

export default {
  name: "ProfileEdit",
  data() {
    return {
      user: auth.getUser(),
      validForm: true,
      profile: {
        firstName: "",
        lastName: "",
        email: "",
        langKey: ""
      },
      emailRules: [
        v => !!v || this.$t("account.emailRequired"),
        v => /.+@.+/.test(v) || this.$t("account.emailValid")
      ],
      languages: [
  /*%
    data.basicData.languages.forEach(function(lang) {
  %*/
        {
          key: "/*%= lang.toLocaleUpperCase() %*/",
          label: "languages./*%= normalize(lang) %*/"
        },
  /*%
    });
  %*/
      ]
    };
  },
  mounted() {
    auth.isAuthenticationChecked().then(() => Object.assign(this.profile, this.user));
  },
  methods: {
    save() {
      if (this.validForm) {
        AccountRepository.updateUser(this.profile).then(() => {
          this.$notify({
            title: "Saved",
            text: this.$t("account.changesSaved"),
            type: "success"
          });
          this.$router.back();
        });
      } else {
        this.$notify({
          title: "Error",
          text: this.$t("formError"),
          type: "error"
        });
      }
    }
  }
};
</script>
/*% } %*/
