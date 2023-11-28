import "./scss/main";
import Vue from "vue";
import vuetify from "./plugins/vuetify";
import i18n from "./plugins/i18n";

import App from "./App.vue";
import router from "./router";

Vue.config.productionTip = false;

new Vue({
  router,
  vuetify,
  i18n,
  render: (h) => h(App),
}).$mount("#app");
