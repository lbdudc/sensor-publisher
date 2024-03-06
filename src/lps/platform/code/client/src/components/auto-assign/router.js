/*% if (feature.DM_A_G_Batch) { %*/
import AutoAssign from "./AutoAssign.vue";

const routes = [
  {
    path: "/auto-assign",
    name: "AutoAssign",
    meta: {
      label: "autoassign.title"
    },
    component: AutoAssign
  }
]

export default routes;
/*% } %*/
