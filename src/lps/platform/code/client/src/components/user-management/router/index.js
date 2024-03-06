/*% if (feature.UM_UserCRUD) { %*/
import UserManagementList from "../UserManagementList";
import UserManagementForm from "../UserManagementForm";

const routes = [
  {
    path: "/user-management/users",
    name: "UserManagementList",
    component: UserManagementList,
    meta: {
      authority: "ROLE_ADMIN",
      label: "menu.userManagement"
    }
  },
  {
    path: "/user-management/users/new",
    name: "UserManagementFormNew",
    component: UserManagementForm,
    meta: {
      authority: "ROLE_ADMIN",
      label: "user_management.userManagementForm"
    }
  },
  {
    path: "/user-management/:login/edit",
    name: "UsermanagementForm",
    component: UserManagementForm,
    meta: {
      authority: "ROLE_ADMIN",
      label: "user_management.userManagementForm"
    }
  }
];

export default routes;
/*% } %*/
