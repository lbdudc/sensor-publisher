/*% if (feature.T_P_RedSys) { %*/
<template>
  <v-container>
    <v-row>
      <v-col cols="12" class="text-center">
        <v-card color="transparent" flat>
          <v-card-title>
            <v-col class="display-3 text-center">
              {{ $t("payment.redsys.success") }}
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
                <v-icon color="success" x-large> mdi-check-circle </v-icon>
              </v-col>
            </v-row>
            <v-row>
              <v-col cols="12">
                {{
                  $t("payment.redsys.redirection_notification", {
                    val: countDown,
                  })
                }}
              </v-col>
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
import RepositoryFactory from "@/repositories/RepositoryFactory";

const PaymentRepository = RepositoryFactory.get("PaymentRepository");
export default {
  name: "TpvOk",
  data() {
    return {
      countDown: 10,
      currency: "€",
      redsysData: null,
      routeName: null,
      exited: false,
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
      this.countDownTimer();
      if (this.$route.query.Ds_MerchantParameters) {
        this.commitCharge();
        const merchantBase64 = this.$CryptoJS.enc.Base64.parse(
          this.$route.query.Ds_MerchantParameters
        );
        this.redsysData = JSON.parse(
          this.$CryptoJS.enc.Utf8.stringify(merchantBase64)
        );
      }
    });
  },
  beforeDestroy() {
    this.exited = true;
  },
  methods: {
    commitCharge() {
      let requestData = {
        Ds_MerchantParameters: this.$route.query.Ds_MerchantParameters,
        Ds_Signature: this.$route.query.Ds_Signature,
        Ds_SignatureVersion: this.$route.query.Ds_SignatureVersion,
      };
      return PaymentRepository.captureOrder(
        this.$route.query.reference,
        "CREDIT_CARD",
        requestData
      );
    },
    countDownTimer() {
      if (!this.exited) {
        if (this.countDown > 0) {
          setTimeout(() => {
            this.countDown -= 1;
            this.countDownTimer();
          }, 1000);
        } else {
          this.$router.push({ name: "Home" });
        }
      }
    },
  },
};
</script>
/*% } %*/
