/*% if (feature.MWM_TA_SensorDataCollector) { %*/
import { HTTP } from "@/common/http-common";
import Logger from "js-logger";

const logger = Logger.get("logger");
const RESOURCE_NAME = "sensor-data-collector";
const EMPLOYEES_RESOURCE_NAME = `${RESOURCE_NAME}/employees`;

export default {
  /*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
  //This method requires to return a promise
  callAnnotateProcess() {
    try {
      return HTTP.post(`${RESOURCE_NAME}/annotate`);
    } catch (err) {
      logger.error("Error annotating process");
      throw err;
    }
  },

  /*% } %*/
  async getEmployees() {
    try {
      return (await HTTP.get(`${EMPLOYEES_RESOURCE_NAME}`)).data;
    } catch (err) {
      logger.error("Error getting employees");
      throw err;
    }
  },

  async getActivities(options) {
    try {
      return (await HTTP.get(`${RESOURCE_NAME}/activities`, options)).data;
    } catch (err) {
      logger.error("Error fetching activities", options);
      throw err;
    }
  },

  async getLocations(employee, options) {
    try {
      return (await HTTP.get(
        `${EMPLOYEES_RESOURCE_NAME}/${employee}/location`,
        options
      )).data;
    } catch (err) {
      logger.error("Error getting locations", employee, options);
      throw err;
    }
  },

  async getSegments(employee, options) {
    try {
      return (await HTTP.get(
        `${EMPLOYEES_RESOURCE_NAME}/${employee}/segment`,
        options
      )).data;
    } catch (err) {
      logger.error("Error getting segments", employee, options);
      throw err;
    }
  },
};
/*% } %*/
