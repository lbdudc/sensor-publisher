/*% if (feature.MWM_TA_SensorDataCollector) { %*/
<template>
  <v-container class="pt-0">
    <v-container class="pa-0" ref="map" id="map"></v-container>
  </v-container>
</template>

<script>
import devCheck from "@/common/device-check";
import initMap from "@/common/initMap";
export default {
  name: "SensorDataMap",
  props: ["eventData"],
  data() {
    return {
      map: null,
      mapData: [],
      dataLayer: null,
      baseLayer: null
    };
  },
  beforeMount() {
    this.mapData = this.formatEventData();
  },
  mounted() {
    setTimeout(() => this.loadMap(), 100); //TO FIX: 100 ms for render parent container
  },
  beforeUpdate() {
    this.mapData = this.formatEventData();
  },
  updated() {
    setTimeout(() => this.loadMap(), 100);
  },
  methods: {
    formatEventData() {
      let features = [];
      this.eventData.locations.forEach(location => {
        features.push({
          id: location.id,
          type: "Feature",
          geometry: location.position,
          properties: {
            displayString: this.timestampArrayToEventTime(location.time)
          }
        });
      });
      return {
        type: "FeatureCollection",
        features: features
      };
    },
    loadMap() {
      if (devCheck.isMobile) {
        this.$refs.map.classList.add("map-mobile");
      } else {
        this.$refs.map.classList.add("map-desktop");
      }

      this.map = initMap("map", "sensorData", [
        [41.508742458803326, -4.087890625],
        [44.508742458803326, -12.087890625]
      ]);

      const layerManagerOptions = {
        center: true,
        color: false,
        style: false,
        sorting: false,
        opacity: false,
        stylesLegend: false,
        select: false
      };

      var layerManager = new MV.LayerManager(this.map, layerManagerOptions);
      this.map.addControl(layerManager);

      const dataLayerStyle = new MV.GeoJSONLayerStyle("data_style", {
        color: this.eventData.color,
        opacity: 0.55
      });

      this.dataLayer = new MV.GeoJSONLayer(
        this.mapData,
        {
          id: this.eventData.name,
          label: this.eventData.name,
          baseLayer: false,
          popup: function(e) {
            return e.feature.properties.displayString;
          }
        },
        [dataLayerStyle],
        dataLayerStyle
      );

      this.map.addLayer(this.dataLayer);

      this.map.centerView("visible");
    },
    timestampArrayToEventTime(timestampArray) {
      //Formatter of timestamp array sent from server to hh:mm format
      return (
        ("0" + timestampArray[3].toString()).slice(-2) +
        ":" +
        ("0" + timestampArray[4].toString()).slice(-2)
      );
    }
  }
};
</script>

<style scoped>
.map-desktop {
  width: 100%;
  height: 550px;
  z-index: 1;
}
.map-mobile {
  width: 100%;
  height: 400px;
  z-index: 1;
}
</style>
/*% } %*/
