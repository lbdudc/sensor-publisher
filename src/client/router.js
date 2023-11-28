import Vue from "vue";
import VueRouter from "vue-router";

// import engineStore from "./services/engineStore";

Vue.use(VueRouter);

const routes = [
  {
    path: "*",
    redirect: "/",
  },
  {
    path: "/",
    name: "Home",
    component: () => import("./views/Home.vue"),
  },
  {
    path: "/editor",
    name: "Editor",
    component: () => import("./components/dsl/Editor.vue"),
  },
];

const router = new VueRouter({
  mode: "history",
  base: process.env.BASE_URL,
  routes,
});

// router.beforeEach((to, from, next) => {
//   // engineStore.init.finally(() => {
//   // });
// });

export default router;
