/*% if (feature.UM_UserCRUD) { %*/
import { HTTP } from "@/common/http-common";
import Logger from "js-logger";

const logger = Logger.get("logger");
const RESOURCE_NAME = "user-management/users";
/*% if (feature.MWM_EmployeeAuthentication) { %*/
const EMPLOYEES_RESOURCE_NAME = "user-management/employees";
/*% } %*/

export default {
  async getAll(options) {
    try {
      return (await HTTP.get(RESOURCE_NAME, options)).data;
    } catch (err) {
      logger.error("Error fetching all users");
      throw err;
    }
  },

  async get(login) {
    try {
      return (await HTTP.get(`${RESOURCE_NAME}/${login}`)).data;
    } catch (err) {
      logger.error("Error fetching user with login " + login);
      throw err;
    }
  },

  async save(user) {
    if (user.id) {
      try {
        return (await HTTP.put(RESOURCE_NAME, user)).data;
      } catch (err) {
        logger.error("Error updating user", user);
        throw err;
      }
    } else {
      try {
        return (await HTTP.post(RESOURCE_NAME, user)).data;
      } catch (err) {
        logger.error("Error saving user", user);
        throw err;
      }
    }
  },

  async delete(user) {
    try {
      return (await HTTP.delete(`${RESOURCE_NAME}/${user.login}`));
    } catch (err) {
      logger.error("Error deleting user", user);
      throw err;
    }
  },
  /*% if (feature.MWM_EmployeeAuthentication) { %*/

  async getEmployees(options = {}) {
    try {
      return (await HTTP.get(EMPLOYEES_RESOURCE_NAME, options)).data;
    } catch (err) {
      logger.error("Error fetching all users that are employees", options);
      throw err;
    }
  },

  async getEmployeesWithoutUserAssociated(options = {}) {
    try {
      return (
        await HTTP.get(
          `${EMPLOYEES_RESOURCE_NAME}/without-user-associated`,
          options
        )
      ).data;
    } catch (err) {
      logger.error(
        "Error fetching all employees without user associated",
        options
      );
      throw err;
    }
  },
  /*% } %*/
  /*% if (feature.UM_UP_ByAdmin) { %*/

  async resetUserPassword(mail) {
    try {
      return (
        await HTTP.post("account/reset_password/init", { email: mail })
      ).data;
    } catch (err) {
      logger.error("Error resetting user password", mail);
      throw err;
    }
  },
  /*% } %*/
};
/*% } %*/
