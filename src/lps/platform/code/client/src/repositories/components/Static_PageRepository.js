/*% if (feature.GUI_StaticPages) { %*/
import { HTTP } from "@/common/http-common";
import Logger from "js-logger";

const logger = Logger.get("logger");
const RESOURCE_NAME = "entities/statics";

export default {
  async getAll(options) {
    return (await HTTP.get(RESOURCE_NAME, options)).data;
  },

  async get(id) {
    return (await HTTP.get(`${RESOURCE_NAME}/${id}`)).data;
  },

  async getTranslation(id, language) {
    return (await HTTP.get(`${RESOURCE_NAME}/${id}/${language}`)).data;
  },
  /*% if (feature.GUI_SP_Management) { %*/

  async save(static_page) {
    if (static_page.id) {
      try {
        return (
          await HTTP.put(`${RESOURCE_NAME}/${static_page.id}`, static_page)
        ).data;
      } catch (err) {
        logger.error("Error updating static page", static_page);
        throw err;
      }
    } else {
      try {
        return (await HTTP.post(`${RESOURCE_NAME}`, static_page)).data;
      } catch (err) {
        logger.error("Error saving static page", static_page);
        throw err;
      }
    }
  },

  async delete(id) {
    try {
      return (await HTTP.delete(`${RESOURCE_NAME}/${id}`));
    } catch (err) {
      logger.error("Error deleting static page with id " + id);
      throw err;
    }
  },
  /*% } %*/
};
/*% } %*/
