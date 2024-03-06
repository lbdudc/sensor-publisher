/*% if (feature.UM_A_RememberPass || feature.UM_UP_ByAdmin || feature.UM_A_RecoverPass ) { %*/
import ResetPasswordRequest from "../password/reset/ResetPasswordRequest.vue";
/*% } %*/
/*% if (feature.UM_UpdatePassword  || (feature.UM_UserCRUD)  ) { %*/
import ResetPasswordFinish from "../password/reset/ResetPasswordFinish.vue";
/*% } %*/
/*% if (feature.UM_UserProfile) { %*/
import Profile from "../profile/Profile.vue";
import ProfileEdit from "../profile/ProfileEdit.vue";
/*% } %*/
/*% if (feature.UM_AccountActivation) { %*/
import Activate from "../activate/Activate.vue";
/*% } %*/
/*% if (feature.UM_R_Anonymous) { %*/
import Register from "../register/Register.vue";
/*% } %*/

/*% if (feature.UM_UP_ByUser) { %*/
import ChangePassword from "@/account/profile/ChangePassword";
/*% } %*/

const routes = [
  /*% if (feature.UM_A_RememberPass || feature.UM_UP_ByAdmin || feature.UM_A_RecoverPass ) { %*/
  {
    path: "/account/reset_password",
    name: "ResetPasswordRequest",
    component: ResetPasswordRequest,
    meta: {
      public: true,
      label: "account.resetPassword"
    }
  },
  /*% } %*/
  /*% if (feature.UM_UpdatePassword ) { %*/
  {
    path: "/account/reset/finish",
    name: "ResetPasswordFinish",
    component: ResetPasswordFinish,
    meta: {
      public: true,
      label: "account.resetPassword"
    }
  },
  /*% } %*/
  /*% if (feature.UM_UserCRUD) { %*/
  {
    path: "/account/set/finish",
    name: "UserCreatedByAdminNewPassword",
    component: ResetPasswordFinish,
    meta: {
      public: true,
      label: "account.resetPassword"
    }
  },
  /*% } %*/
  /*% if (feature.UM_R_Anonymous) { %*/
  {
    path: "/account/register",
    name: "Register",
    component: Register,
    meta: {
      public: true,
      label: "account.register.name"
    }
  },
  /*% } %*/
  /*% if (feature.UM_AccountActivation) { %*/
  {
    path: "/account/activate",
    name: "Activate",
    component: Activate,
    meta: {
      public: true,
      label: "account.userActivation"
    }
  },
  /*% } %*/
  /*% if (feature.UM_UserProfile) { %*/
  {
    path: "/account/profile",
    name: "Profile",
    component: Profile,
    meta: {
      public: false,
      userIsLogged: true,
      label: "account.yourProfile"
    }
  },
  {
    path: "/account/edit",
    name: "EditProfile",
    component: ProfileEdit,
    meta: {
      public: false,
      userIsLogged: true,
      label: "account.editProfile"
    }
  },
  /*% } %*/
  /*% if (feature.UM_UP_ByUser) { %*/
  {
    path: "/account/change_password",
    name: "ChangePassword",
    component: ChangePassword,
    meta: {
      public: false,
      userIsLogged: true,
      label: "account.changePassword"
    }
  }
  /*% } %*/
];

export default routes;
