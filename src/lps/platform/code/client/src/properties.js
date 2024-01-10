export default {
  /*% if (feature.MV_MS_GeoServer) { %*/GEOSERVER_URL: process.env.VUE_APP_GEOSERVER_URL,/*% } %*/
  /*% if (feature.DM_A_C_RC_GraphHopper) { %*/GRAPHHOPPER_URL: process.env.VUE_APP_GRAPHHOPPER_URL,/*% } %*/
  /*% if (feature.DM_DS_Address) { %*/GOOGLE_API_KEY: process.env.VUE_APP_GOOGLE_API_KEY,/*% } %*/
  /*% if (feature.T_P_PayPal) { %*/PAYPAL_CLIENT_ID: process.env.VUE_APP_PAYPAL_CLIENT_ID,/*% } %*/
  SERVER_URL: process.env.VUE_APP_SERVER_URL,
  APP_NAME: "/*%= data.basicData.name %*/",
}
