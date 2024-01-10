/*% if (feature.UserManagement) { %*/
import { HTTP } from "@/common/http-common";
import Logger from "js-logger";

const logger = Logger.get("logger");
const RESOURCE_NAME = "account";
const AUTHENTICATION_RESOURCE_NAME = "authenticate";
/*% if (feature.UM_A_RememberPass || feature.UM_UP_ByAdmin || feature.UM_UpdatePassword  || feature.UM_R_ByAdmin ) { %*/
const RESET_PASSWORD_RESOURCE_NAME = `${RESOURCE_NAME}/reset_password`;
/*% } %*/
/*% if (feature.UM_R_Anonymous) { %*/
const REGISTER_RESOURCE_NAME = "register";
/*% } %*/
/*% if (feature.UM_AccountActivation) { %*/
const ACTIVATE_RESOURCE_NAME = "activate";
/*% } %*/
/*% if (feature.UM_UserProfile) { %*/
const CHANGE_PASSWORD_RESOURCE_NAME = `${RESOURCE_NAME}/change_password`;
/*% } %*/

export default {
  async getUser() {
    return (await HTTP.get(RESOURCE_NAME)).data;
  },

  async login(credentials) {
    return (
      await HTTP.post(AUTHENTICATION_RESOURCE_NAME, credentials/*% if (feature.UM_A_RememberPass) { %*/, {
      params: { remember_me: credentials.remember_me }
    }/*% } %*/)
    ).data;
  },

  async isAuthenticated() {
    return (await HTTP.get(AUTHENTICATION_RESOURCE_NAME)).data;
  },
  /*% if (feature.UM_A_RememberPass || feature.UM_UP_ByAdmin ) { %*/
  async resetPasswordInit(mail) {
    try {
      return (await HTTP.post(`${RESET_PASSWORD_RESOURCE_NAME}/init`, {
        email: mail,
      })).data;
    } catch(err) {
      logger.error("Error starting reset password", mail);
      throw err;
    }
  },
  /*% } %*/
  /*% if (feature.UM_UpdatePassword  || feature.UM_R_ByAdmin) { %*/

  async resetPasswordFinish(keyAndPassword) {
    return (await HTTP.post(
      `${RESET_PASSWORD_RESOURCE_NAME}/finish`,
      keyAndPassword
    )).data;
  },
  /*% } %*/
  /*% if (feature.UM_R_Anonymous) { %*/

  async register(user) {
    return (await HTTP.post(REGISTER_RESOURCE_NAME, user)).data;
  },
  /*% } %*/
  /*% if (feature.UM_AccountActivation) { %*/

  async activateUser(key) {
    return (await HTTP.get(
      ACTIVATE_RESOURCE_NAME,
      { params: { key: key } }
    )).data;
  },
  /*% } %*/
  /*% if (feature.UM_UserProfile) { %*/
  async changePassword(password) {
    return (await HTTP.post(CHANGE_PASSWORD_RESOURCE_NAME, { password })).data;
  },

  async updateUser(user) {
    return (await HTTP.post(RESOURCE_NAME, user)).data;
  },
  /*% } %*/

  async changeLanguage(login, newLang) {
    return (
      await HTTP.put(`${RESOURCE_NAME}/${login}/language`, newLang, {
        headers: { "Content-Type": "text/plain" },
      })
    ).data;
  },
};
/*% } %*/
