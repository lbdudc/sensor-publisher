/*% if (feature.DM_DS_Address || feature.DM_A_G_Batch) { %*/
import { HTTP } from "@/common/http-common";
import Logger from "js-logger";
const logger = Logger.get("logger");
const RESOURCE_NAME = "geolocation";

export default {
  async getCountries() {
    try {
      return (await HTTP.get(`${RESOURCE_NAME}/countries`)).data;
    } catch (err) {
      logger.error("Error fetching countries");
      throw err;
    }
  },

  async getSubdivisions(
    countryCode,
    sbd1Code = null,
    sbd2Code = null,
    sbd3Code = null,
    sbd4Code = null
  ) {
    let sbdCodes = [sbd1Code, sbd2Code, sbd3Code, sbd4Code];
    let url = `${RESOURCE_NAME}/id/${countryCode}`; // countryCode is mandatory

    // iterate over subdivisions codes to create url
    for (const sbdCode of sbdCodes) {
      if (sbdCode) url = url.concat(`/${sbdCode}`);
      else break; // at first subdivision code null we stop iteration
    }
    try {
      return (await HTTP.get(url)).data;
    } catch (err) {
      logger.error("Error getting subdivisions", url);
      throw err;
    }
  },
  async getName(id) {
    try {
      return (await HTTP.get(`${RESOURCE_NAME}/name/${id}`)).data;
    } catch (err) {
      logger.error("Error getting name for id " + id);
      throw err;
    }
  },
};
/*% } %*/
