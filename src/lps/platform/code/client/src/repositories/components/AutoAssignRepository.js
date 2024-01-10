/*% if (feature.DM_A_G_Batch) { %*/
import { HTTP } from "@/common/http-common";
import Logger from "js-logger";
const logger = Logger.get("logger");
const RESOURCE_NAME = "geolocation/autoassign";

export default {
  async autoassignLocations(body) {
    try {
      return (await HTTP.post(RESOURCE_NAME, body)).data;
    } catch (err) {
      logger.error("Error autoassign locations", this.form);
      throw err;
    }
  },
};
/*% } %*/
