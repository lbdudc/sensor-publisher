/*% if (feature.MWM_TA_SensorDataCollector) { %*/
import SensorData from "./SensorData";

const routes = [
  {
    path: "/sensor-data-collector/employees",
    name: "SensorData",
    meta: {
      label: "menu.sensorData"
    },
    component: SensorData
  }
];

export default routes;

/*% } %*/
