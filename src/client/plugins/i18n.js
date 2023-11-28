import Vue from "vue";
import VueI18n from "vue-i18n";
import esLocale from "../locale/es";
import enLocale from "../locale/en";
import featuresEs from "../locale/feature-label_es";
import featuresEn from "../locale/feature-label_en";

Vue.use(VueI18n);

const messages = {
  en: {
    ...enLocale,
    ...featuresEn,
  },
  es: {
    ...esLocale,
    ...featuresEs,
  },
};

const i18n = new VueI18n({
  locale: "es", // set locale
  fallbackLocale: "en", // set fallback locale
  messages, // set locale messages
});

export default i18n;
