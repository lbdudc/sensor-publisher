import { createRouter, createWebHistory } from "vue-router";

import Home from "./pages/Home.vue";
import DSLEditor from "./components/editor/Editor.vue";

const base = import.meta.env.BASE_URL;

const routes = [
  {
    path: base,
    name: "home",
    component: Home,
  },
  {
    path: `${base}dsl-editor`,
    name: "dsl-editor",
    component: DSLEditor,
    props: (route) => (
      { query: route.query.text }, { query: route.query.example }
    ),
  },
];

const history = createWebHistory();

const router = createRouter({
  history,
  routes,
});

export default router;
