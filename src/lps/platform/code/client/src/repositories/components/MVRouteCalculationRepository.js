/*% if (feature.DM_A_C_RouteCalculation) { %*/
import Logger from "js-logger";

const logger = Logger.get("logger");
    /*% if (feature.DM_A_C_RC_GraphHopper) { %*/
    import axios from "axios";
    import properties from "@/properties";
    /*% } %*/
    /*% if (feature.DM_A_C_RC_pgRouting) { %*/
    import { HTTP } from "@/common/http-common";
    const PGROUTING_RESOURCE_NAME = "/component/connectivity/routeCalculation";
    /*% } %*/
    export default {
    /*% if (feature.DM_A_C_RC_pgRouting) { %*/
      async getPgrRoute(options = {}) {
        try {
          return (await HTTP.get(PGROUTING_RESOURCE_NAME, options)).data;
        } catch (err) {
          logger.error("Error getting Pgr route", options);
          throw err;
        }
      },
    /*% } %*/
    /*% if (feature.DM_A_C_RC_GraphHopper) { %*/
      async getGhRoute(options = {}) {
        try {
          return (await axios.get(
            properties.GRAPHHOPPER_URL + "/route?" + options
          )).data;
        } catch (err) {
          logger.error("Error getting Gh route", options);
          throw err;
        }
      }
    /*% } %*/
    };
/*% } %*/
