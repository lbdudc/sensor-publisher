import { createApp } from "vue";
import "./style.css";
import App from "./App.vue";
import "material-design-icons-iconfont/dist/material-design-icons.css";
import editorWorker from "monaco-editor/esm/vs/editor/editor.worker?worker";
import jsonWorker from "monaco-editor/esm/vs/language/json/json.worker?worker";
// Vuetify
import "vuetify/styles";
import vuetify from "./plugins/vuetify";
// Router
import router from "./router";

self.MonacoEnvironment = {
  getWorker(_, label) {
    if (label === "json") {
      return jsonWorker();
    }
    return editorWorker();
  },
};

createApp(App).use(router).use(vuetify).mount("#app");
