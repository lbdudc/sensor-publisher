/*% if (feature.UM_AccountActivation) { %*/
<template>
  <v-card v-if="resolved" class="form">
    <v-card-title class="justify-center mt-8" v-bind:class="styleCard">
      <h3>
        {{ $t('account.userActivation') }}
      </h3>
    </v-card-title>
    <v-card-text class="mt-6">
      <v-row align="center" justify="center">
        <div>
          <v-icon v-if="success" color="success" x-large>done</v-icon>
          <v-icon v-if="!success" color="error" x-large>warning</v-icon>
          <span v-bind:class="styleText">{{ response }}</span>
        </div>
      </v-row>
      <v-row v-if="success" justify="center" class="mt-6">
        <span
          >{{ $t('account.nowYouCan') }} <router-link to="login">{{ $t('account.login') }}</router-link> {{ $t('account.normally') }}
        </span>
      </v-row>
    </v-card-text>
  </v-card>
</template>

<script>
import RepositoryFactory from "@/repositories/RepositoryFactory";
const AccountRepository = RepositoryFactory.get("AccountRepository");

export default {
  data() {
    return {
      response: "",
      resolved: null,
      success: false,
      cardStyle: {
        color: "red"
      }
    };
  },
  created() {
    this.resolved = false;
    this.key = this.$route.query.key;
  },
  computed: {
    styleCard() {
      if (this.success) {
        return "success white--text";
      } else return "error darken-1 white--text";
    },
    styleText() {
      if (this.success) {
        return "green--text";
      } else return "red--text";
    }
  },
  mounted() {
    AccountRepository.activateUser(this.$route.query.key)
      .then(resp => {
        if (!resp.isAxiosError) {
          this.success = true;
          this.response = this.$t("account.accountActivated");
        }
      })
      .catch(err => {
        this.response = err;
      })
      .finally((this.resolved = true));
  }
};
</script>

<style scoped>
.form {
  width: 70%;
  text-align: center;
  margin: 0 auto;
}

@media only screen and (max-width: 600px) {
  .form {
    width: 90%;
  }
}
</style>
/*% } %*/
