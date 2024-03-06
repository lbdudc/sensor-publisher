/*% if (feature.T_Payments) { %*/
import { HTTP } from "@/common/http-common";
import Logger from "js-logger";
const logger = Logger.get("logger");
const RESOURCE_NAME = "payments";

export default {
  /*% if (feature.T_P_RedSys) { %*/
  async createRedSysOrder(payment) {
    try {
      return (await HTTP.post(`${RESOURCE_NAME}/redsys`, payment)).data;
    } catch (err) {
      logger.error("Error creating order ", payment);
      throw err;
    }
  },
  /*% } %*/
  /*% if (feature.T_P_PayPal) { %*/
  async createOrder(payment) {
    try {
      return (await HTTP.post(RESOURCE_NAME, payment)).data;
    } catch (err) {
      logger.error("Error creating order ", payment);
      throw err;
    }
  },
  /*% } %*/
  async captureOrder(reference, type, data = {}) {
    try {
      return (await HTTP.post(`${RESOURCE_NAME}/capture-order/${reference}`, data, {
        params: {
          type: type
        }
      })).data;
    } catch (err) {
      logger.error("Error capturing order ", reference);
      throw err;
    }
  },
};
/*% } %*/
