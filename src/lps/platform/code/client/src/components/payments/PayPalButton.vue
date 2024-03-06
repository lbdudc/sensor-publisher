/*% if (feature.T_P_PayPal) { %*/
<template>
  <div class="button">
    <v-progress-circular
      v-show="loading"
      indeterminate
      color="primary"
    ></v-progress-circular>
    <div id="paypal-container" :class="disabled ? 'v-btn--disabled' : ''" v-show="!loading"></div>
  </div>
</template>
<script>
import properties from "@/properties";
import RepositoryFactory from "@/repositories/RepositoryFactory";

const PaymentRepository = RepositoryFactory.get("PaymentRepository");
export default {
  props: {
    amount: {
      type: Number,
      required: false,
      default: 0,
    },
    disabled: {
      type: Boolean,
      required: false,
      default: false,
    },
    name: {
      type: String,
      required: false,
      default: "",
    },
  },
  data() {
    return {
      loading: false,
    };
  },
  mounted() {
    this.loading = true;
    this.$loadScript(
      `https://www.paypal.com/sdk/js?client-id=${properties.PAYPAL_CLIENT_ID}&currency=EUR&disable-funding=credit,card,sofort`
    )
      .then(() => {
        this.renderPaypalButton();
      })
      .catch(() => {
        this.$notify({
          title: this.$t("payment.error.paypal_error.title"),
          text: this.$t(
            "payment.error.paypal_error.load_script"
          ),
          type: "error",
        });
      })
      .finally(() => (this.loading = false));
  },
  methods: {
    renderPaypalButton() {
      window.paypal
        .Buttons({
          createOrder: async () => {
            return this.doCreateOrder();
          },
          onApprove: async (data) => {
            return this.doApproveOrder(data.orderID);
          },
          onCancel: () => {
            this.$notify({
              title: this.$t(
                "payment.error.paypal_error.title"
              ),
              text: this.$t("payment.error.paypal_error.abort"),
              type: "warning",
            });
          },
          onError: () => {
            this.$notify({
              title: this.$t(
                "payment.error.paypal_error.title"
              ),
              text: this.$t("payment.error.paypal_error.error"),
              type: "error",
            });
          },
        })
        .render("#paypal-container");
    },
    doCreateOrder() {
      let payment = {
        amount: this.amount,
        fullName: this.name,
        type: "PAYPAL",
      };
      return PaymentRepository.createOrder(payment, this.name)
        .then((data) => {
          return data.reference;
        })
        .catch(() => this.$log.debug("Error creating PayPal order"));
    },
    doApproveOrder(reference) {
      this.loading = true;
      return PaymentRepository.captureOrder(reference, "PAYPAL")
        .then((response) => {
          this.$emit("completed", response);
          return response;
        })
        .catch(() => this.$log.debug("Error capturing order " + reference))
        .finally(() => (this.loading = false));
    },
  },
};
</script>
<style scoped>
.button {
  max-width: 200px;
  margin: auto;
}
</style>
/*% } %*/
