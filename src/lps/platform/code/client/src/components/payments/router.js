/*% if (feature.T_Payments) { %*/
import PaymentView from "./PaymentView";
/*% if (feature.T_P_RedSys) { %*/
import TpvOk from "./TpvOk";
import TpvKo from "./TpvKo";

/*% } %*/
const routes = [
  {
    path: "/payments",
    name: "Payments",
    meta: {
      label: "menu.payments"
    },
    component: PaymentView
  },
  /*% if (feature.T_P_RedSys) { %*/
  {
    path: "/payments/tpv-ok",
    name: "TPV OK",
    component: TpvOk
  },
  {
    path: "/payments/tpv-ko",
    name: "TPV KO",
    component: TpvKo
  }
  /*% } %*/
];

export default routes;
/*% } %*/
