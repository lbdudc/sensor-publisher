/*% if (feature.T_P_RedSys) { %*/
<template>
  <form
    ref="redsys_form"
    action="https://sis-t.redsys.es:25443/sis/realizarPago"
    method="post"
    id="redsys_form"
    name="redsys_form"
  >
    <input
      type="text"
      name="Ds_SignatureVersion"
      v-model="Ds_SignatureVersion"
      hidden
    />
    <input
      type="text"
      name="Ds_MerchantParameters"
      v-model="Ds_MerchantParameters"
      hidden
    />
    <input type="text" name="Ds_Signature" v-model="Ds_Signature" hidden />

    <v-btn
      :loading="loading"
      @click.prevent="chargeWallet()"
      class="mx-4 success"
      :disabled="disabled"
      >{{ $t("payment.name") }}</v-btn
    >
  </form>
</template>
<script>
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
    }
  },
  data() {
    return {
      Ds_MerchantParameters: null,
      Ds_Signature: null,
      Ds_SignatureVersion: null,
      loading: false,
    };
  },
  methods: {
    chargeWallet() {
      let charge = {
        amount: this.amount,
        type: "CREDIT_CARD",
      };
      this.loading = true;
      return PaymentRepository.createRedSysOrder(charge)
        .then((data) => {
          this.Ds_MerchantParameters = data.Ds_MerchantParameters;
          this.Ds_Signature = data.Ds_Signature;
          this.Ds_SignatureVersion = data.Ds_SignatureVersion;
          // NOTE: Exec submit action AFTER data is set on form
          this.$nextTick(() => this.$refs.redsys_form.submit());
        })
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
