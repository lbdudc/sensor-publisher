/*% if (feature.MWM_VisitSchedule) { %*/
import { HTTP } from "@/common/http-common";
import Logger from "js-logger";
const logger = Logger.get("logger");
const RESOURCE_NAME = "planned_event_crud";
const EVENTS_RESOURCE_NAME = `${RESOURCE_NAME}/events`;

export default {
  async getAll(options = {}) {
    try {
      return (await HTTP.get(EVENTS_RESOURCE_NAME, options)).data;
    } catch (err) {
      logger.error("Error fetching events", options);
      throw err;
    }
  },

  async get(id) {
    try {
      return (await HTTP.get(`${EVENTS_RESOURCE_NAME}/${id}`)).data;
    } catch (err) {
      logger.error("Error fetching event with id " + id);
      throw err;
    }
  },
  /*% if (feature.MVM_VT_OnlyDay || feature.MVM_VT_WithTime) { %*/

  async getCalendarEvents(options = {}) {
    try {
      return (await HTTP.get(`${EVENTS_RESOURCE_NAME}/calendar`, options)).data;
    } catch (err) {
      logger.error("Error getting calendar events", options);
      throw err;
    }
  },

  /*% } %*/
  async getClientsByParams(options = {}) {
    try {
      return (await HTTP.get(`${RESOURCE_NAME}/clients`, options)).data;
    } catch (err) {
      logger.error("Error getting clients", options);
      throw err;
    }
  },

  async getEmployeesByParams(options = {}) {
    try {
      return (await HTTP.get(`${RESOURCE_NAME}/employees`, options)).data;
    } catch (err) {
      logger.error("Error getting employees", options);
      throw err;
    }
  },

  async save(event) {
    if (event.id) {
      try {
        return (await HTTP.put(`${EVENTS_RESOURCE_NAME}/${event.id}`, event))
          .data;
      } catch (err) {
        logger.error("Error updating event", event);
        throw err;
      }
    } else {
      try {
        return (await HTTP.post(`${EVENTS_RESOURCE_NAME}`, event)).data;
      } catch (err) {
        logger.error("Error creating event", event);
        throw err;
      }
    }
  },

  async delete(id) {
    try {
      return (await HTTP.delete(`${EVENTS_RESOURCE_NAME}/${id}`));
    } catch (err) {
      logger.error("Error deleting event with id " + id);
      throw err;
    }
  },
};
/*% } %*/
