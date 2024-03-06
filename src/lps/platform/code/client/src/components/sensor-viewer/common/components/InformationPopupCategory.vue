/*% if (feature.SensorViewer && feature.SV_Popup) { %*/
/*%
  const dimensions = [];
  data.dataWarehouse.sensors.forEach(function(sensor) {
    const dims = sensor.dimensions;
    dims
      .filter(dim => dim.type === "CATEGORICAL")
      .forEach(dim => {
        dimensions.push(dim);
      });
  });
  var hasCategoricalDims = dimensions.length > 0;
  if (hasCategoricalDims) { %*/
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
                      {{
                        $t(
                          `aggregation.property.items.${prop.key.toLowerCase()}`
                        )
                      }}
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

      <!-- RANGED POPUP -->
      <v-row v-if="hasCategoryRanges">
        <v-row
          class="mb-2 mx-0"
          justify="center"
          v-if="parsedFeature.length && temporalAggSelected !== 'NONE' && store"
        >
          <aggregation-histogram-ranged
            :layer="layer"
            :propertyAggLevel="propertySelected"
            :propertyAggItems="propertyAggItems"
            :store="store"
            :histogramGetData="histogramGetData"
            :temporalAggLevel="temporalAggSelected"
            :categoryAggSelected="categoryAggSelected"
            :categories="getCategoryRanges()"
            :hasCategoryRanges="hasCategoryRanges"
            :categoryFilterSelected="categoryFilterSelected"
            :propertyUnits="propertyUnits"
            :title="$t('histogram.label') + ' ' + featureName"
          ></aggregation-histogram-ranged>
        </v-row>
        <v-row
          v-else-if="filteredMeasurements.length > 0"
          class="mb-2"
          justify="center"
        >
          <detail-popup-ranged
            :layer="layer"
            :propertyAggLevel="propertySelected"
            :propertyAggItems="propertyAggItems"
            :store="store"
            :categories="getCategoryRanges()"
            :histogramGetData="histogramGetData"
            :categoryAggSelected="categoryAggSelected"
            :temporalAggLevel="temporalAggSelected"
            :propertyUnits="propertyUnits"
            :title="$t('measurement_popup.title') + ' Sensor ' + featureName"
          ></detail-popup-ranged>
        </v-row>
      </v-row>
      <!-- NOT RANGED POPUP -->
      <v-row v-else>
        <v-row
          class="mb-2 mx-0"
          justify="center"
          v-if="parsedFeature.length && temporalAggSelected !== 'NONE' && store"
        >
          <aggregation-histogram
            :layer="layer"
            :propertyAggLevel="propertySelected"
            :propertyAggItems="propertyAggItems"
            :store="store"
            :histogramGetData="histogramGetData"
            :temporalAggLevel="temporalAggSelected"
            :categoryAggSelected="categoryAggSelected"
            :categories="getCategoryRanges()"
            :hasCategoryRanges="hasCategoryRanges"
            :categoryFilterSelected="categoryFilterSelected"
            :propertyUnits="propertyUnits"
            :title="$t('histogram.label') + ' ' + featureName"
          ></aggregation-histogram>
        </v-row>
        <v-row
          v-else-if="filteredMeasurements.length > 0"
          class="mb-2"
          justify="center"
        >
          <detail-popup
            :layer="layer"
            :propertyAggLevel="propertySelected"
            :propertyAggItems="propertyAggItems"
            :store="store"
            :histogramGetData="histogramGetData"
            :categoryAggSelected="categoryAggSelected"
            :temporalAggLevel="temporalAggSelected"
            :propertyUnits="propertyUnits"
            :title="$t('measurement_popup.title') + ' Sensor ' + featureName"
          ></detail-popup>
        </v-row>
      </v-row>
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
import AggregationHistogram from "@/components/sensor-viewer/common/components/popup/TemporalHistogramCategory.vue";
import AggregationHistogramRanged from "@/components/sensor-viewer/common/components/popup/ranged/TemporalHistogramRanged.vue";
import DetailPopup from "@/components/sensor-viewer/common/components/popup/DetailPopup.vue";
import DetailPopupRanged from "@/components/sensor-viewer/common/components/popup/ranged/DetailRangedPopup.vue";

export default {
  name: "InformationPopup",
  components: {
    DetailPopup,
    DetailPopupRanged,
    AggregationHistogram,
    AggregationHistogramRanged,
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
    categoryFilterSelected: {
      required: true,
    },
    categoryAggSelected: {
      required: true,
    },
    sensorSpec: {
      type: Object,
      required: false,
      default: () => {},
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
    hasCategoryRanges() {
      return this.isRangedCategory();
    },
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
      if (this.isRangedCategory()) {
        const { from, to } = this.getCategoryFromToValues();
        // Try to parse the category value to a number, and also take into account that
        // from and to values can be null
        const dataFilter =
          this?.layer?.feature?.properties?.measurements.filter((el) => {
            if (isNaN(el.id)) return false;
            // Check if the value is between the from and to values, casting them to numbers
            return (
              (from == null || Number(el.id) >= Number(from)) &&
              (to == null || Number(el.id) <= Number(to))
            );
          });
        // Calculate the average of the values
        const avg = dataFilter.reduce((acc, el) => {
          return acc + Number(el.data[this.propertySelected.toLowerCase()]);
        }, 0);

        if (dataFilter.length == 0 || isNaN(avg)) return [];
        const avgValue = avg / dataFilter.length;
        return [
          {
            key: this.propertySelected.toLowerCase(),
            value: avgValue,
          },
        ];

        const result = this.layer?.feature?.properties?.measurements
          .filter((el) => {
            return el.id;
          })
          .map((el) => {
            return {
              key: this.propertySelected.toLowerCase(),
              value: el.data[this.propertySelected.toLowerCase()],
            };
          });

        return result.length == 0 ? [] : result;
      }
      const calc = this.categoryFilterSelected.toLowerCase();
      const dataFilter = this.layer?.feature?.properties?.measurements
        .filter((el) => el.id == calc)
        .map((el) => {
          return {
            key: this.propertySelected.toLowerCase(),
            value: el.data[this.propertySelected.toLowerCase()],
          };
        });
      return dataFilter.length == 0 ? [] : dataFilter;
    },
    filteredMeasurements() {
      const { from, to } = this.getCategoryFromToValues();
      // Try to parse the category value to a number, and also take into account that
      // from and to values can be null
      const dataFilter = this?.layer?.feature?.properties?.measurements.filter(
        (el) => {
          if (isNaN(el.id)) return false;
          // Check if the value is between the from and to values, casting them to numbers
          return (
            (from == null || Number(el.id) >= Number(from)) &&
            (to == null || Number(el.id) <= Number(to))
          );
        }
      );
      return dataFilter;
    },
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
    },
     isRangedCategory() {
      const calcCat = this.sensorSpec?.store?.category.find(
        (cat) =>
          cat.value.toLowerCase() ===
          this.store["categoryAggregation"].toLowerCase()
      );
      return calcCat?.categories ? true : false;
    },
    getCategoryRanges() {
      const calcCat = this.sensorSpec?.store?.category.find(
        (cat) =>
          cat.value.toLowerCase() ===
          this.store["categoryAggregation"].toLowerCase()
      );
      return calcCat?.categories ? calcCat.categories : [];
    },
    getCategoryFromToValues() {
      const catFrom = this.store["categoryFrom"];
      const catTo = this.store["categoryTo"];
      return {
        from:
          catFrom == "Infinity" || catFrom == "-Infinity"
            ? null
            : this.store["categoryFrom"],
        to: catTo == "Infinity" || catTo == "-Infinity" ? null : catTo,
      };
    },
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
/*% } %*/
