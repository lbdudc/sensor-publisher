/*% if (feature.GUI_StaticPages) { %*/
import Static_PageList from "../Static_PageList";
  /*% if (feature.GUI_SP_Management) { %*/
import Static_PageForm from "../Static_PageForm";
  /*% } %*/
import Static_PageDetail from "../Static_PageDetail";
import Static_FullPageDetail from "@/components/static_editor/Static_FullPageDetail";

const routes = [
  {
    path: "/statics",
    name: "Static_PageList",
    component: Static_PageList,
    meta: {
      /*% if (feature.UserManagement) { %*/
      userIsLogged: true,
      /*% } %*/
      label: "static_editor.staticPageList"
    }
  },
  /*% if (feature.GUI_SP_Management) { %*/
  {
    path: "/statics/:id/edit",
    name: "Static_PageForm",
    component: Static_PageForm,
    meta: {
      /*% if (feature.UserManagement) { %*/
      userIsLogged: true,
      /*% } %*/
      label: "static_editor.staticPageForm"
    }
  },
  {
    path: "/statics/new",
    name: "Static_PageCreate",
    component: Static_PageForm,
    meta: {
      /*% if (feature.UserManagement) { %*/
      userIsLogged: true,
      /*% } %*/
      label: "static_editor.staticPageForm"
    }
  },
  /*% } %*/
  {
    path: "/statics/:id/detail",
    name: "Static_PageDetail",
    component: Static_PageDetail,
    meta: {
      userIsLogged: true,

      label: "static_editor.staticPageDetail",
    }
  },
  {
    path: "/statics/:id(\\d+)",
    name: "Static_FullPageDetail",
    component: Static_FullPageDetail,
    meta: {
      public: true,
      label: "static_editor.staticPageDetail"
    },
    props: route => {
      return {
        static_page_id: route.params.id ? route.params.id : null
      };
    }
  }
];

export default routes;
/*% } %*/
