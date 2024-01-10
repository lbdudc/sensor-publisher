/*% if (feature.MWM_PersonnelManager) { %*/
import { HTTP } from "@/common/http-common";
import Logger from "js-logger";

const logger = Logger.get("logger");
  /*% if (feature.MWM_PM_Clients) { %*/
const CLIENTS_RESOURCE_NAME = "staff_crud/clients";
  /*% } %*/
  /*% if (feature.MWM_PM_Employees) { %*/
const EMPLOYEES_RESOURCE_NAME = "staff_crud/employees";
  /*% } %*/

export default {
  /*% if (feature.MWM_PM_Clients) { %*/
  async getAllClients(options = {}) {
    try {
      return (await HTTP.get(CLIENTS_RESOURCE_NAME, options)).data;
    } catch (err) {
      logger.error("Error fetching all clients", options);
      throw err;
    }
  },

  async getClient(id) {
    try {
      return (await HTTP.get(`${CLIENTS_RESOURCE_NAME}/${id}`)).data;
    } catch (err) {
      logger.error("Error fetching client with id " + id);
      throw err;
    }
  },

  async saveClient(client) {
    if (client.id) {
      try {
        return (await HTTP.put(`${CLIENTS_RESOURCE_NAME}/${client.id}`, client))
          .data;
      } catch (err) {
        logger.error("Error updating client", client);
        throw err;
      }
    } else {
      try {
        return (await HTTP.post(`${CLIENTS_RESOURCE_NAME}`, client)).data;
      } catch (err) {
        logger.error("Error saving client", client);
        throw err;
      }
    }
  },

  async activateClient(id) {
    try {
      return (await HTTP.put(`${CLIENTS_RESOURCE_NAME}/${id}/active`));
    } catch (err) {
      logger.error("Error activating client with id " + id);
      throw err;
    }
  },

  async deleteClient(id) {
    try {
      return (await HTTP.delete(`${CLIENTS_RESOURCE_NAME}/${id}/active`));
    } catch (err) {
      logger.error("Error deleting client with id " + id);
      throw err;
    }
  },
  /*% } %*/
  /*% if (feature.MWM_PM_Employees) { %*/

  async getAllEmployees(options = {}) {
    try {
      return (await HTTP.get(EMPLOYEES_RESOURCE_NAME, options)).data;
    } catch (err) {
      logger.error("Error fetching all employees");
      throw err;
    }
  },

  async getEmployee(id) {
    try {
      return (await HTTP.get(`${EMPLOYEES_RESOURCE_NAME}/${id}`)).data;
    } catch (err) {
      logger.error("Error fetching employee with id " + id);
      throw err;
    }
  },

  async saveEmployee(employee) {
    if (employee.id) {
      try {
        return (await HTTP.put(
          `${EMPLOYEES_RESOURCE_NAME}/${employee.id}`,
          employee
        )).data;
      } catch (err) {
        logger.error("Error updating employee", employee);
        throw err;
      }
    } else {
      try {
        return (await HTTP.post(`${EMPLOYEES_RESOURCE_NAME}`, employee)).data;
      } catch (err) {
        logger.error("Error updating employee", employee);
        throw err;
      }
    }
  },

  async activateEmployee(id) {
    try {
      return (await HTTP.put(`${EMPLOYEES_RESOURCE_NAME}/${id}/active`));
    } catch (err) {
      logger.error("Error activating employee with id " + id);
      throw err;
    }
  },

  async deleteEmployee(id) {
    try {
      return (await HTTP.delete(`${EMPLOYEES_RESOURCE_NAME}/${id}/active`));
    } catch (err) {
      logger.error("Error deleting employee with id " + id);
      throw err;
    }
  },
  /*% } %*/
};
/*% } %*/
