/*% if (feature.SensorViewer && feature.SV_Popup) { %*/
<template>
  <v-container
    v-if="layer"
    :class="
      parsedFeature.length > 1 ? 'ma-0 pa-0 big-popup' : 'ma-0 pa-0 small-popup'
    "
    fluid
  >
    <v-col v-if="!loading" class="px-0 py-0">
      <v-row class="popup-header ml-4 mr-4">
        <span v-if="featureName" class="text-title">
          Sensor: {{ featureName }}
        </span>
      </v-row>
      <v-row class="ml-1 mt-5 mb-2 ma-0 pa-0">
        <v-simple-table style="width: 100%" v-if="parsedFeature.length">
          <template v-slot:default>
            <tbody>
              <tr>
                <td
                  v-for="prop in parsedFeature"
                  :key="prop.key"
                  style="text-align: center"
                >
                /*%
                  const measurements = [];
                  data.dataWarehouse.sensors.forEach(function(sensor) {
                    const props = sensor.measureData;
                    props.forEach(prop => {
                      if (!measurements.find(m => m.name === prop.name)) {
                        measurements.push(prop);
                      }
                    });
                  });
                %*/
                /*% measurements.forEach(function(sensor) { %*/
                  <v-icon
                    color="primary"
                    v-if="prop.key.toLowerCase() == '/*%= sensor.name.toLowerCase() %*/'"
                  >
                  /*% if (sensor.icon) { %*/
                    mdi-/*%= sensor.icon %*/
                  /*% } else { %*/
                    network_check
                  /*% } %*/
                  </v-icon>
                /*% }); %*/
                  <br />
                  <small style="white-space: nowrap">
                    <b>
                      {{ $t(
                        `aggregation.property.items.${prop.key
                          .split("_avg")[0]
                          .toLowerCase()}`
                      ) }}
                     </b
                    ></small
                  >
                  <br />
                  {{ prop.value | popupFormat(prop.key, propertyUnits) }}
                </td>
              </tr>
            </tbody>
          </template>
        </v-simple-table>

        <span v-else class="result mt-4">
          {{ $t("map-views.legend.no-data") }}
        </span>
      </v-row>

      /*% if (feature.SV_P_Histogram) { %*/
      <v-row
        class="mb-2"
        justify="center"
        v-if="parsedFeature.length && temporalAggSelected !== 'NONE' && store"
      >
        <histogram
          :layer="layer"
          :propertyAggLevel="propertySelected"
          :propertyAggItems="propertyAggItems"
          :store="store"
          :histogramGetData="histogramGetData"
          :temporalAggLevel="temporalAggSelected"
          :propertyUnits="propertyUnits"
          :title="$t('histogram.label') + ' ' + featureName"
        ></histogram>
      </v-row>
        /*% } %*/
    </v-col>
    <v-col v-else>
      <v-row justify="center">
        <v-progress-circular
          color="primary"
          indeterminate
          size="25"
        ></v-progress-circular>
        <td class="mt-2">{{ $t("map-views.loading") }}</td>
      </v-row>
    </v-col>
  </v-container>
</template>

<script>
/*% if (feature.SV_P_Histogram) { %*/
import Histogram from "@/components/sensor-viewer/common/components/popup/TemporalHistogram.vue";
/*% } %*/

export default {
  name: "InformationPopup",
  components: {
    /*% if (feature.SV_P_Histogram) { %*/
    Histogram
    /*% } %*/
  },
  props: {
    form: {
      required: true,
    },
    layer: {
      required: true,
    },
    loading: {
      required: true,
    },
    propertySelected: {
      required: false,
    },
    propertyAggItems: {
      required: false,
      default: () => [],
    },
    temporalAggSelected: {
      required: false,
    },
    store: {
      required: true,
      default: null,
    },
    histogramGetData: {
      type: Function,
      required: false,
      default: () => {},
    },
    propertyUnits: {
      type: Object,
      required: false,
      default: () => {},
    },
  },
  computed: {
    featureName() {
      if (
        this.layer?.feature?.properties?.name &&
        this.layer.feature.properties.name !== "null"
      ) {
        return this.layer.feature.properties.name;
      } else if (
        this.layer?.feature?.properties?.displayString &&
        this.layer.feature.properties.displayString !== "null"
      ) {
        return this.layer.feature.properties.displayString;
      }
      return "";
    },
    parsedFeature() {
      if (this.layer?.feature.properties.data == null) return [];
      const finalList = [];
      Object.keys(this.layer.feature.properties.data).forEach((el) => {
        finalList.push({
          key: el,
          value: this.layer.feature.properties.data[el],
        });
      });

      return finalList.sort((a, b) => a.key.localeCompare(b.key));
    }
  },
  watch: {
    layer() {
      if (this.layer) {
        this.layer
          .getPopup()
          .addOneTimeEventListener("remove", () => this.$emit("close"));
      }
    },
  },
  updated() {
    if (this.layer) {
      this.customPopupUpdate();
      this.layer.updatePopup();
    }
  },
  methods: {
    customPopupUpdate() {
      if (this.layer._latlngs) {
        const geometry = this.layer.feature.geometry,
          latLngs = this.layer.getLatLngs();
        if (geometry.type === "Polygon" && latLngs[0].length === 4) {
          const lat =
              latLngs[0][0].lat + (latLngs[0][1].lat - latLngs[0][0].lat) / 2,
            lng =
              latLngs[0][1].lng + (latLngs[0][2].lng - latLngs[0][1].lng) / 2,
            newLatLng = new L.LatLng(lat, lng);
          this.layer._popup.setLatLng(newLatLng);
        }
      }
    }
  },
};
</script>

<style scoped>
.big-popup {
  width: auto;
  height: auto;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}
.small-popup {
  display: flex;
  align-items: center;
  justify-content: center;
}
.popup-header {
  display: grid;
}
.result {
  overflow: hidden;
  white-space: nowrap;
  width: 100%;
  text-align: center;
  font-size: 15px;
}
.text-title {
  text-align: left;
  margin-top: 15px;
  font-weight: bold;
  font-size: 15px;
  display: inline-flex;
}
.table-header {
  font-weight: bolder;
  text-decoration: underline;
}
</style>
<style>
.leaflet-popup-content {
  width: auto !important;
}
</style>
/*% } %*/
