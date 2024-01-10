/*% if (feature.T_Payments) { %*/
<template>
  <v-container>
    <v-row>
      <v-col cols="12" class="text-center text-h6">
        {{ $t("payment.form.title") }}
      </v-col>
    </v-row>
    <v-form ref="form" v-model="validForm">
      <v-row align="center" justify="center">
        <v-col cols="12" md="6">
          <v-row>
            <v-text-field
              v-model="payment.fullName"
              :label="$t('payment.prop.name')"
            ></v-text-field>
          </v-row>
          <v-row>
            <number-field
              v-model="payment.amount"
              prefix="€"
              type="Double"
              :label="$t('payment.prop.amount')"
              :rules="[
                (v) => !!v || $t('payment.form.validation.amount_required'),
                (v) =>
                  parseInt(v) >= minAmount ||
                  $t('payment.form.validation.amount_min', {
                    amount: minAmount,
                    currency: currency,
                  }),
                (v) =>
                  parseInt(v) <= maxAmount ||
                  $t('payment.form.validation.amount_max', {
                    amount: maxAmount,
                    currency: currency,
                  }),
              ]"
            ></number-field>
          </v-row>
          <v-row>
            <v-select
              v-model="payment.type"
              :items="paymentMethods"
              :label="$t('payment.prop.type')"
              class="required"
              :rules="[
                (v) => !!v || $t('payment.form.validation.method_required'),
              ]"
            >
              <template v-slot:selection="data">
                <v-row no-gutters>
                  <v-list-item-icon class="selectable-lang">
                    <img :src="data.item.icon" width="35" height="20" />
                  </v-list-item-icon>
                  <v-list-item-content class="selectable-lang">
                    <v-list-item-title>{{
                      $t(data.item.name)
                    }}</v-list-item-title>
                  </v-list-item-content>
                </v-row>
              </template>

              <template v-slot:item="data">
                <v-list-item-icon>
                  <img :src="data.item.icon" width="26" height="30" />
                </v-list-item-icon>
                <v-list-item-content>
                  <v-list-item-title>{{
                    $t(data.item.name)
                  }}</v-list-item-title>
                </v-list-item-content>
              </template>
            </v-select>
          </v-row>
        </v-col>
      </v-row>
    </v-form>
    <v-col cols="12" class="text-center pt-0">
  /*% if (feature.T_P_RedSys) { %*/
      <redsys-button
        v-if="payment.type === 'CREDIT_CARD'"
        :amount="Number(payment.amount)"
        :disabled="!validForm"
      >
      </redsys-button>
  /*% } %*/
  /*% if (feature.T_P_PayPal) { %*/
      <paypal-button
        v-if="payment.type === 'PAYPAL'"
        :amount="Number(payment.amount)"
        :disabled="!validForm"
        :name="payment.fullName"
        @completed="onCompleted"
      ></paypal-button>
  /*% } %*/
    </v-col>
  </v-container>
</template>

<script>
import NumberField from "@/components/number-field/NumberField.vue";
/*% if (feature.T_P_PayPal) { %*/
import PayPalButton from "./PayPalButton.vue";
/*% } %*/
/*% if (feature.T_P_RedSys) { %*/
import RedSysButton from "./RedSysButton.vue";
/*% } %*/

export default {
  name: "PaymentDialog",
  components: {
    "number-field": NumberField,
    /*% if (feature.T_P_PayPal) { %*/
    "paypal-button": PayPalButton,
    /*% } %*/
    /*% if (feature.T_P_RedSys) { %*/
    "redsys-button": RedSysButton,
    /*% } %*/
  },
  data() {
    return {
      payment: {
        amount: null,
        fullName: "",
        type: null,
      },
      loading: false,
      currency: "€",
      maxAmount: 500,
      minAmount: 5,
      validForm: false,
      paymentMethods: [
      /*% if (feature.T_P_RedSys) { %*/
        {
          icon: require("@/assets/payment_methods/credit_card.svg"),
          name: "payment.methods.CREDIT_CARD",
          value: "CREDIT_CARD",
        },
      /*% } %*/
      /*% if (feature.T_P_PayPal) { %*/
        {
          icon: require("@/assets/payment_methods/paypal.svg"),
          name: "payment.methods.PAYPAL",
          value: "PAYPAL",
        },
      /*% } %*/
      ],
    };
  },
  /*% if (feature.T_P_PayPal) { %*/
  methods: {
    onCompleted() {
      this.$notify({
        text: this.$t("payment.messages.successfull_payment.text"),
        title: this.$t("payment.messages.successfull_payment.title"),
        type: "success",
      });
      this.$router.push({ name: "Home" });
    },
  },
  /*% } %*/
};
</script>
/*% } %*/
