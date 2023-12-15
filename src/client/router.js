import { createRouter, createWebHistory } from "vue-router";

import Home from "./pages/Home.vue";
import DSLEditor from "./components/editor/Editor.vue";

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
    props: (route) => ({ query: route.query.text }),
  },
];

const history = createWebHistory();

const router = createRouter({
  history,
  routes,
});

export default router;
