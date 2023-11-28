import { createRouter, createWebHistory } from "vue-router";

import Home from "./pages/Home.vue";
import DSLEditor from "./pages/dsl-editor.vue";

const routes = [
  {
    path: "/",
    name: "home",
    component: Home,
  },
  {
    path: "/dsl-editor",
    name: "dsl-editor",
    component: DSLEditor,
  },
];

const history = createWebHistory();

const router = createRouter({
  history,
  routes,
});

export default router;
