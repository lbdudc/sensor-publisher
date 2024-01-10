/*% if (feature.DM_DS_GTFS) { %*/
import { HTTP } from "@/common/http-common";
import Logger from "js-logger";

const logger = Logger.get("logger");
const RESOURCE_NAME = "gtfs/route";

export default {
  async getGeom(geomName, options = {}) {
    try {
      const response = (await HTTP.get(`${RESOURCE_NAME}/geom/${geomName}`, options)).data;
      return response || [];
    } catch (err) {
      logger.error("Error getting geometry " + geomName, options);
      throw err;
    }
  },
};
/*% } %*/
