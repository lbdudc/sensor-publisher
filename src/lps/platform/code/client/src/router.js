import Vue from "vue";
import Router from "vue-router";
import NotFound from "@/views/NotFound.vue";
/*% if (!feature.GUI_StaticPages || !data.statics || data.statics.find(s => s.id == 'Home') == undefined) { %*/
import Home from "./views/Home.vue";
/*% } %*/
/*% if (feature.UserManagement) { %*/
import Login from "./account/login/Login.vue";
import Unauthorized from "./views/Unauthorized";
import Account_Router from "../src/account/router";
import auth from "@/common/auth";
import store from "@/common/store";
const user = store.state.user;
/*% } %*/
/*% if (feature.DM_DI_DF_Shapefile) { %*/
import ShapefileImport from "./components/shapefile-import/router";
/*% } %*/
/*% if (feature.MapViewer) { %*/
import MapViewer from "@/components/map-viewer/MapViewer.vue";
/*% } %*/
/*% if (feature.DM_A_G_Batch) { %*/
import AutoAssignRouter from "@/components/auto-assign/router";
/*% } %*/
/*% if (feature.GUI_StaticPages) { %*/
import Static_PagesRouter from "./components/static_editor/router";
  /*% if (data.statics && data.statics.find(s => s.id == 'Home') != undefined) { %*/
  import Static_FullPageDetail from "@/components/static_editor/Static_FullPageDetail";
  /*% } %*/
/*% } %*/
/*% if (feature.MWM_PM_Clients) { %*/
import StaffClientRouter from "./components/staff_crud/client/router";
/*% } %*/
/*% if (feature.MWM_PM_Employees) { %*/
import StaffEmployeeRouter from "./components/staff_crud/employee/router";
/*% } %*/
/*% if (feature.MWM_VisitSchedule) { %*/
import PlannedEventRouter from "./components/planned_event_crud/router/EventRouter";
import EventCalendarRouter from "./components/event_calendar/eventCalendarRouter.js";
/*% } %*/
/*% if (feature.UM_UserCRUD) { %*/
import User_ManagementRouter from "./components/user-management/router";
/*% } %*/
/*% if (feature.MWM_TrajectoryExploitation) { %*/
import TrajectoryRouter from "./components/trajectory-exploitation/router";
/*% } %*/
/*% if (feature.MWM_TA_SensorDataCollector) { %*/
import SensorDataRouter from "./components/sensor-data-collector/sensorDataRouter";
/*% } %*/
/*% if (feature.T_Payments) { %*/
import PaymentRouter from "./components/payments/router";
/*% } %*/

/*% data.dataModel.entities
      .filter(function(context) {
        return !context.abstract;
      })
      .forEach(function(entity) { %*/
import /*%= normalize(entity.name) %*/Router from "./modules//*%= normalize(entity.name) %*//router";
      /*% }); %*/

Vue.use(Router);

const routes = [
  {
    path: "/",
    name: "Home",
/*% if (feature.GUI_StaticPages && data.statics && data.statics.find(s => s.id == 'Home') != undefined) { %*/
    component: Static_FullPageDetail,
    props: {
      static_page_id: "Home"
    },
/*% } else { %*/
    component: Home,
/*% } %*/
    meta: {
      /*% if (feature.UserManagement) { %*/
      public: true,
      /*% } %*/
      label: "menu.home"
    }

  },
  /*% if (feature.MapViewer) { %*/
  {
    path: "/map-viewer/:id?",
    name: "MapViewer",
    meta: {
      label: "menu.mapViewer"
    },
    component: MapViewer
  },
  /*% } %*/
  {
    path: "/about",
    name: "about",
    meta: {
      label: "menu.about"
    },
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import("./views/About.vue")
  },
  /*% if (feature.UserManagement) { %*/
  {
    path: "/account/login",
    name: "Login",
    component: Login,
    meta: {
      public: true,
      isLoginPage: true,
      label: "menu.logIn"
    }
  },
  {
    path: "/unauthorized",
    name: "Unauthorized",
    component: Unauthorized,
    meta: {
      public: true,
      label: "errors.unauthorized.title",
    },
  },
  /*% } %*/
  {
    path: "*",
    name: "NotFound",
    component: NotFound,
    props: true,
    meta: {
      /*% if (feature.UserManagement) { %*/
      public: true,
      /*% } %*/
      label: "page_not_found.title"
    }
  }
];

const router = new Router({
  mode: "history",
  base: process.env.BASE_URL,
  routes: routes
    /*% if (feature.GUI_StaticPages) { %*/.concat(Static_PagesRouter)/*% } %*/
    /*% if (feature.DM_A_G_Batch) { %*/.concat(AutoAssignRouter)/*% } %*/
    /*% if (feature.MWM_PM_Clients) { %*/.concat(StaffClientRouter)/*% } %*/
    /*% if (feature.MWM_PM_Employees) { %*/.concat(StaffEmployeeRouter)/*% } %*/
    /*% if (feature.DM_DI_DF_Shapefile) { %*/.concat(ShapefileImport)/*% } %*/
    /*% if (feature.UM_UserCRUD) { %*/.concat(User_ManagementRouter)/*% } %*/
    /*% if (feature.UserManagement) { %*/.concat(Account_Router)/*% } %*/
    /*% if (feature.MWM_VisitSchedule) { %*/.concat(PlannedEventRouter, EventCalendarRouter)/*% } %*/
  /*% if (feature.MWM_TrajectoryExploitation) { %*/.concat(TrajectoryRouter)/*% } %*/
    /*% if (feature.MWM_TA_SensorDataCollector) { %*/.concat(SensorDataRouter)/*% } %*/
    /*% if (feature.T_Payments) { %*/.concat(PaymentRouter)/*% } %*/
    .concat(/*%= data.dataModel.entities
                    .filter(function(context) {
                      return !context.abstract;
                    })
                    .map(function(entity) {
                      return normalize(entity.name) + 'Router';
                    })
                    .join(", ")
                    %*/
    )
});

const backPreviousStack = [];

const checkRedirectToPrevious = (to, from, next) => {
  // if backPrevious prop, add from route to stack and go next
  if (to.params.backPrevious) {
    if (to.params.backAction) {
      // eslint-disable-next-line
      console.warn(
        "Set route with backAction and backPrevious flags is not allowed"
      );
    } else {
      backPreviousStack.push(from);
    }
    next();
  } else if (to.params.backAction) {
    // if backAction prop (should only be present when route from back button)
    // get last item from stack if not empty
    let length = backPreviousStack.length;
    if (length < 1) next();
    else {
      let stackTop = {};
      Object.assign(stackTop, backPreviousStack[length - 1]);
      // check if redirecting to stack top route and if so,
      // remove it from the stack
      if (to.fullPath == stackTop.fullPath) {
        backPreviousStack.pop();
        next();
      } else {
        // call router.push() will trigger again this function
        // so is necessary set backAction and backPrevious params
        // in order to reach this block again
        stackTop.params.backAction = true;
        stackTop.params.backPrevious = false;
        router.push(stackTop); // calling router.push() instead of next() to prevent console error
      }
    }
  } else {
    // clean stack if no backPrevious neither backAction props are present and route next
    /*% if (feature.UserManagement) { %*/
    // only when the next page is not the login page
    if (to.meta.isLoginPage)
    /*% } %*/
      backPreviousStack.splice(0, backPreviousStack.length);
    next();
  }
};
/*% if (feature.UserManagement) { %*/
function checkAuthentication(to, from, next) {
  auth.isAuthenticationChecked().finally(() => {
    // por defecto las rutas restringen el acceso a usuario autenticados
    const requiresAuth = !to.meta.public;
    const requiredAuthority = to.meta.authority;
    const userIsLogged = user.logged;
    const loggedUserAuthorities = user.authorities;

    if (requiresAuth) {
      if (userIsLogged) {
        if (
          requiredAuthority &&
          loggedUserAuthorities.indexOf(requiredAuthority) == -1
        ) {
          // usuario logueado pero sin permisos
          Vue.notify({
            title: "Unauthorized",
            text: "Access is not allowed for the current user. Try to log again.",
            type: "error"
          });
          auth.logout().then(() => next("/account/login"));
        } else {
          // usuario logueado y con permisos adecuados
          next();
        }
      } else {
        // usuario no está logueado
        Vue.notify({
          title: "Unauthorized",
          text: "This page requires authentication",
          type: "error"
        });
        backPreviousStack.push(to);
        next("/account/login");
      }
    } else {
      // página pública
      if (userIsLogged && to.meta.isLoginPage) {
        // si estamos logueados no hace falta volver a mostrar el login
        next({ name: "Home", replace: true });
      } else {
        next();
      }
    }
  });
};

router.beforeEach(checkAuthentication);
/*% } %*/
router.beforeEach(checkRedirectToPrevious);

export default router;
