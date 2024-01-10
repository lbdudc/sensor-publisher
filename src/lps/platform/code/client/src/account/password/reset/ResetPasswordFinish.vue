/*% if (feature.UM_UpdatePassword  || (feature.UM_UserCRUD)) { %*/
<template>
  <v-container>
    <v-card>
      <v-card-title class="pb-0 pt-0 pt-md-4">
        <v-row align="center" no-gutters>
          <v-col cols="6" class="d-none d-md-block">
            <span class="headline no-split-words">
              {{ $t($route.meta.label) }}
            </span>
          </v-col>
        </v-row>
      </v-card-title>
      <v-card-text v-if="!resolved">
        <v-form v-model="validForm">
          <v-row align="center" justify="center">
            <v-col cols="12" sm="6">
              <v-row justify="center">
                <span>
                  {{ $t("account.enterPassword") }}
                </span>
              </v-row>
              <v-row>
                <v-text-field
                  class="form-control"
                  v-model="user.password"
                  name="password"
                  prepend-inner-icon="vpn_key"
                  :append-icon="hidePassword1 ? 'visibility' : 'visibility_off'"
                  :label="$t('account.password.name')"
                  :rules="[
                    v => !!v || $t('account.passwordRequired'),
                    betweenMinAndMax
                  ]"
                  :type="hidePassword1 ? 'password' : 'text'"
                  @click:append="() => (hidePassword1 = !hidePassword1)"
                  required
                ></v-text-field>
              </v-row>
              <v-row>
                <v-text-field
                  class="form-control"
                  v-model="user.password2"
                  name="password2"
                  prepend-inner-icon="vpn_key"
                  :append-icon="hidePassword2 ? 'visibility' : 'visibility_off'"
                  :label="$t('account.repeatPassword')"
                  :rules="[
                    v => !!v || $t('account.passwordRequired'),
                    passwordConfirmationRule
                  ]"
                  :type="hidePassword2 ? 'password' : 'text'"
                  @click:append="() => (hidePassword2 = !hidePassword2)"
                  @keypress.enter="submitNewPassword"
                  required
                ></v-text-field>
              </v-row>
              <v-row justify="center">
                <v-btn class="primary mt-4" @click="submitNewPassword">
                  {{ $t("account.resetPassword") }}
                </v-btn>
              </v-row>
            </v-col>
          </v-row>
        </v-form>
      </v-card-text>
      <v-card-text class="mt-6" v-if="resolved">
        <v-row align="center" justify="center">
          <div>
            <v-icon v-if="success" color="success" x-large>done</v-icon>
            <v-icon v-if="!success" color="error" x-large>warning</v-icon>
            <span v-bind:class="styleText">{{ response }}</span>
          </div>
        </v-row>
        <v-row v-if="success" justify="center" class="mt-6">
          <span
            >{{ $t('account.nowYouCan') }} <router-link :to="{ name: 'Login' }">{{ $t('account.logIn') }}</router-link> {{ $t('account.normally') }}
          </span>
        </v-row>
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
      user: {
        password: "",
        password2: ""
      },
      validForm: true,
      hidePassword1: true,
      hidePassword2: true,
      resolved: false,
      response: "",
      success: false,
      key: ""
    };
  },
  created() {
    this.resolved = false;
    this.key = this.$route.query.key;
  },
  computed: {
    passwordConfirmationRule() {
      return () =>
        this.user.password === this.user.password2 ||
        this.$t("account.passwordMatch");
    },
    betweenMinAndMax() {
      return () =>
        (this.user.password.length > 4 && this.user.password.length < 100) ||
        this.$t("account.passwordMoreChars");
    }
  },
  methods: {
    submitNewPassword() {
      if (this.validForm) {
        AccountRepository.resetPasswordFinish({
            key: this.key,
            newPassword: this.user.password
          })
          .then(res => {
            if (!res.isAxiosError) {
              this.success = true;
              this.response = this.$t("account.passwordChanged");
            }
          })
          .finally((this.resolved = true));
      }
    }
  }
};
</script>
/*% } %*/
