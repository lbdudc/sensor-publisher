import Vue from "vue";
import vuetify from "./plugins/vuetify";
import "./plugins/filters";
import i18n from "./plugins/i18n";
/*% if (feature.MapViewer) { %*/
import "./plugins/leaflet";
/*% } %*/
/*% if (feature.GUI_SP_Management) { %*/
import "./plugins/ckeditor";
/*% } %*/

import "./App.scss";
import "./custom.scss";
import App from "./App.vue";
import router from "./router";
import "./registerServiceWorker";
/*% if (feature.MapViewer) { %*/
import "./plugins/leaflet";
/*% } %*/
import Notifications from "vue-notification";
Vue.use(Notifications);
/*% if (feature.T_P_PayPal) { %*/
import LoadScript from "vue-plugin-load-script";
Vue.use(LoadScript);
/*% } %*/
/*% if (feature.T_P_RedSys) { %*/
import VueCryptojs from "vue-cryptojs";
Vue.use(VueCryptojs);
/*% } %*/

Vue.config.productionTip = false;

/*% if (feature.MV_T_Export) { %*/
import VueHtml2Canvas from "vue-html2canvas";
import BabelPolyfill from "babel-polyfill";

Vue.use(VueHtml2Canvas);
Vue.use(BabelPolyfill);
/*% } %*/

import tabTitleGenerator from "./components/tabTitleGenerator";
Vue.mixin(tabTitleGenerator);

import logger from "@/plugins/logger";
Vue.prototype.$logger = logger;

Vue.$router = router;

new Vue({
  router,
  vuetify,
  i18n,
  render: (h) => h(App),
}).$mount("#app");
