/*% if (feature.UM_A_RememberPass || feature.UM_UP_ByAdmin || feature.UM_A_RecoverPass) { %*/
<template>
  <v-container>
    <v-card>
      <v-card-title class="pb-0 pt-0 pt-md-4">
        <v-row align="center" no-gutters>
          <v-col cols="5" class="d-none d-md-block">
            <span class="headline no-split-words">
              {{ $t($route.meta.label) }}
            </span>
          </v-col>
        </v-row>
      </v-card-title>
      <v-card-text>
        <v-form v-model="validForm">
          <v-row align="center" justify="center">
            <v-col cols="12" sm="6">
              <v-row justify="center" class="text-center">
                <span>
                  {{ $t("account.enterEmail") }}
                </span>
              </v-row>
              <v-row>
                <v-text-field
                  v-model="email"
                  name="email"
                  prepend-inner-icon="email"
                  type="email"
                  :label="$t('account.emailAddress')"
                  :rules="emailRules"
                  @keydown.enter="emailAddress"
                  required
                ></v-text-field>
              </v-row>
              <v-row justify="center">
                <v-btn class="primary mt-4" @click="emailAddress">
                  {{ $t("account.resetPassword") }}
                </v-btn>
              </v-row>
            </v-col>
          </v-row>
        </v-form>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script>
import RepositoryFactory from "@/repositories/RepositoryFactory";
const AccountRepository = RepositoryFactory.get("AccountRepository");

export default {
  data() {
    return {
      validForm: true,
      email: "",
      emailRules: [
        v => !!v || this.$t("account.emailRequired"),
        v => /.+@.+/.test(v) || this.$t("account.emailValid")
      ]
    };
  },
  methods: {
    emailAddress() {
      if (this.validForm) {
        AccountRepository.resetPasswordInit(this.email)
          .then(this.$router.replace({ name: "Login" }));
      }
    }
  }
};
</script>
/*% } %*/
