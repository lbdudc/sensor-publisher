/*% if (feature.T_P_RedSys) { %*/
<template>
  <v-container>
    <v-row>
      <v-col cols="12" class="text-center">
        <v-card color="transparent" flat>
          <v-card-title>
            <v-col class="display-3 text-center">
              {{ $t("payment.redsys.error") }}
            </v-col>
          </v-card-title>
          <v-card-text>
            <v-row v-if="redsysData">
              <v-col cols="6" class="text-right">
                {{ $t("payment.prop.amount") }}:
              </v-col>
              <v-col cols="6" class="text-left">
                {{ amount }} {{ currency }}</v-col
              >
            </v-row>
            <v-row>
              <v-col>
                <v-icon color="error" x-large> mdi-close-circle </v-icon>
              </v-col>
            </v-row>
            <v-row>
              <v-col>
                <v-btn color="primary" :to="{ name: 'Home' }">
                  {{ $t("payment.redsys.home") }}
                </v-btn>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  name: "TpvKo",
  data() {
    return {
      currency: "€",
      redsysData: null,
      routeName: null,
    };
  },
  computed: {
    amount() {
      const price = this.redsysData.Ds_Amount;
      let amount_main = price.substring(0, price.length - 2);
      let amount_dec = price.substring(price.length - 2, price.length);
      return `${amount_main}.${amount_dec}`;
    },
  },
  mounted() {
    this.$nextTick(async () => {
      if (this.$route.query.Ds_MerchantParameters) {
        const merchantBase64 = this.$CryptoJS.enc.Base64.parse(
          this.$route.query.Ds_MerchantParameters
        );
        this.redsysData = JSON.parse(
          this.$CryptoJS.enc.Utf8.stringify(merchantBase64)
        );
      }
    });
  },
};
</script>
/*% } %*/
