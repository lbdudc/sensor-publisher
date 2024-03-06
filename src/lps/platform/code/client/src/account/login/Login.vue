/*% if (feature.UserManagement) { %*/
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
            /*% if (feature.UM_R_Anonymous) { %*/
            <v-btn class="success" :to="{ name: 'Register' }">
              {{ $t("account.register.name") }}
            </v-btn>
            /*% } %*/
            /*% if (feature.UM_A_RecoverPass) { %*/
            <v-btn class="ml-3 mt-4 mt-sm-0" :to="{ name: 'ResetPasswordRequest' }"
            >{{ $t("account.forgotPassword") }}
            </v-btn>
            /*% } %*/
          </v-col>
        </v-row>
      </v-card-title>
      <v-card-text>
        <v-form v-model="validForm">
          <v-row align="center" justify="center">
            <v-col cols="12" sm="6">
              <v-row>
                <v-text-field
                  v-model="username"
                  autocomplete="username"
                  autofocus
                  name="username"
                  type="text"
                  prepend-inner-icon="person"
                  :label="$t('account.username')"
                  :rules="[v => !!v || $t('account.emailRequired')]"
                  required
                ></v-text-field>
              </v-row>
              <v-row>
                <v-text-field
                  v-model="password"
                  autocomplete="password"
                  name="password"
                  prepend-inner-icon="vpn_key"
                  :label="$t('account.password.name')"
                  :rules="[v => !!v || $t('account.passwordRequired')]"
                  :type="hidePassword ? 'password' : 'text'"
                  @keydown.enter="userLogin()"
                  required
                >
                  <template v-slot:append>
                    <v-icon
                      v-if="hidePassword"
                      tabindex="-1"
                      @click="hidePassword = !hidePassword"
                    >
                      visibility
                    </v-icon>
                    <v-icon
                      v-else
                      tabindex="-1"
                      @click="hidePassword = !hidePassword"
                    >
                      visibility_off
                    </v-icon>
                  </template>
                </v-text-field>
              </v-row>
              /*% if (feature.UM_A_RememberPass) { %*/
              <v-row>
                <v-checkbox
                  v-model="remember_me"
                  :label="$t('account.rememberMe')"
                ></v-checkbox>
              </v-row>
              /*% } %*/
              <v-row justify="center">
                <v-btn class="primary" @click="userLogin">
                  {{ $t("account.logIn") }}
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
import auth from "@/common/auth";

export default {
  data() {
    return {
      validForm: true,
      username: "",
      password: "",
      hidePassword: true,
      /*% if (feature.UM_A_RememberPass) { %*/
      remember_me: false
      /*% } %*/
    };
  },
  methods: {
    userLogin() {
      if (this.validForm) {
        auth
          .login({
            login: this.username,
            password: this.password,
            /*% if (feature.UM_A_RememberPass) { %*/
            remember_me: this.remember_me
            /*% } %*/
          })
          .then(() => {
            this.$router.push({ name: "Home", params: { backAction: true } });
          });
      }
    }
  }
};
</script>
/*% } %*/
