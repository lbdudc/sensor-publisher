/*% if (feature.UM_R_Anonymous) { %*/
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
              <v-row>
                <v-text-field
                  v-model="user.login"
                  autocomplete="username"
                  autofocus
                  name="login"
                  prepend-inner-icon="person"
                  type="text"
                  :disabled="isLoading"
                  :label="$t('account.login')"
                  :rules="[v => !!v || $t('account.usernameRequired')]"
                  required
                ></v-text-field>
              </v-row>
              <v-row>
                <v-text-field
                  v-model="user.firstName"
                  autocomplete="given-name"
                  name="firstname"
                  prepend-inner-icon="notes"
                  type="text"
                  :disabled="isLoading"
                  :label="$t('account.firstName')"
                  :rules="[v => !!v || $t('account.firstNameRequired')]"
                  required
                ></v-text-field>
              </v-row>
              <v-row>
                <v-text-field
                  v-model="user.lastName"
                  autocomplete="family-name"
                  name="lastname"
                  prepend-inner-icon="notes"
                  type="text"
                  :disabled="isLoading"
                  :label="$t('account.lastName')"
                  :rules="[v => !!v || $t('account.lastNameRequired')]"
                  required
                ></v-text-field>
              </v-row>
              <v-row>
                <v-text-field
                  v-model="user.email"
                  autocomplete="username"
                  name="email"
                  prepend-inner-icon="email"
                  type="email"
                  :disabled="isLoading"
                  :label="$t('account.emailAddress')"
                  :rules="emailRules"
                  required
                ></v-text-field>
              </v-row>
              <v-row>
                <v-text-field
                  v-model="user.password"
                  autocomplete="new-password"
                  name="password"
                  prepend-inner-icon="vpn_key"
                  :disabled="isLoading"
                  :label="$t('account.password.name')"
                  :rules="[
                    (v) => !!v || $t('account.passwordRequired'),
                    betweenMinAndMax,
                  ]"
                  :type="hidePassword1 ? 'password' : 'text'"
                  required
                >
                  <template v-slot:append>
                    <v-icon
                      v-if="hidePassword1"
                      tabindex="-1"
                      @click="hidePassword1 = !hidePassword1"
                    >
                      visibility
                    </v-icon>
                    <v-icon
                      v-else
                      tabindex="-1"
                      @click="hidePassword1 = !hidePassword1"
                    >
                      visibility_off
                    </v-icon>
                  </template>
                </v-text-field>
              </v-row>
              <v-row>
                <v-text-field
                  v-model="user.password2"
                  autocomplete="new-password"
                  name="password2"
                  prepend-inner-icon="vpn_key"
                  :disabled="isLoading"
                  :label="$t('account.repeatPassword')"
                  :rules="[
                    (v) => !!v || $t('account.passwordRequired'),
                    passwordConfirmationRule,
                  ]"
                  :type="hidePassword2 ? 'password' : 'text'"
                  @keydown.enter="register"
                  required
                >
                  <template v-slot:append>
                    <v-icon
                      v-if="hidePassword2"
                      tabindex="-1"
                      @click="hidePassword2 = !hidePassword2"
                    >
                      visibility
                    </v-icon>
                    <v-icon
                      v-else
                      tabindex="-1"
                      @click="hidePassword2 = !hidePassword2"
                    >
                      visibility_off
                    </v-icon>
                  </template>
                </v-text-field>
              </v-row>
              <v-row justify="center">
                <v-btn
                  class="primary mt-4"
                  :disabled="isLoading"
                  :loading="isLoading"
                  @click="register">
                  {{ $t("account.register.name") }}
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
      hidePassword1: true,
      hidePassword2: true,
      loading: false,
      emailRules: [
        v => !!v || this.$t("account.emailRequired"),
        v => /.+@.+/.test(v) || this.$t("account.emailValid")
      ],
      user: {
        login: "",
        firstName: "",
        email: "",
        lastName: "",
        password: "",
        password2: "",
        langKey: "en"
      }
    };
  },
  methods: {
    register() {
      if (this.validForm) {
        this.loading = true;
        AccountRepository.register(this.user).then(() => {
          this.$notify({
            title: "Registered",
            text: this.$t("account.successfullRegistered"),
            type: "success"
          });
          this.$router.push({ name: "Login" });
        }).finally(() => this.loading = false);
      } else {
        this.$notify({
          title: "Error",
          text: this.$t("account.formError"),
          type: "error"
        });
      }
    }
  },
  computed: {
    passwordConfirmationRule() {
      return (
        this.user.password === this.user.password2 ||
        this.$t("account.passwordMatch")
      );
    },
    betweenMinAndMax() {
      return () =>
        (this.user.password.length > 4 && this.user.password.length < 100) ||
        this.$t("account.passwordMoreChars");
    },
    isLoading() {
      return this.loading;
    }
  }
};
</script>
/*% } %*/
