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
%*/
/*% if (hasCategoricalDims) { %*/
<template>
  <div class="pa-0 ma-0">
    <v-dialog v-model="dialog" fullscreen transition="dialog-bottom-transition">
      <template v-slot:activator="{ on, attrs }">
        <v-btn block color="primary" dark v-bind="attrs" v-on="on">
          {{ $t("measurement_popup.title") }}
          <v-icon class="ml-1"> mdi-chart-histogram </v-icon>
        </v-btn>
      </template>
      <v-card flex class="pa-0 ma-0">
        <v-toolbar dark color="primary">
          <v-btn icon dark @click="dialog = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
          <v-toolbar-title v-if="title">{{ title }}</v-toolbar-title>
        </v-toolbar>

        <v-container id="popup-container">
          <v-data-table dense :headers="calcHeaders" :items="calcItems">
            <template v-slot:[`item.check`]="{ item }">
              <v-simple-checkbox
                v-model="item.check"
                color="primary"
                @input="selectRow(item)"
              ></v-simple-checkbox>
            </template>
          </v-data-table>
        </v-container>

        <v-container id="popup-container">
          <div class="zoom-container">
            <div class="zoom-reset-btn">
              <v-btn icon @click="resetZoom">
                <v-icon>mdi-magnify-minus-outline</v-icon>
              </v-btn>
            </div>
            <v-row no-gutters>
              <v-col cols="12">
                <LineChartGenerator
                  class="chart-container"
                  ref="lineChart"
                  :chart-options="chartOptions"
                  :chart-data="chartDataCalc"
                  :chart-id="chartId"
                  :dataset-id-key="datasetIdKey"
                  :plugins="plugins"
                  :css-classes="cssClasses"
                  :styles="styles"
                  :width="width"
                  :height="height"
                />
              </v-col>
            </v-row>
          </div>
        </v-container>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import { Bar as LineChartGenerator } from "vue-chartjs/legacy";
import zoomPlugin from "chartjs-plugin-zoom";

import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  BarElement,
  CategoryScale,
  LinearScale,
  LineController,
  LineElement,
  PointElement,
  ScatterController,
} from "chart.js";

ChartJS.register(
  Title,
  Tooltip,
  Legend,
  BarElement,
  LinearScale,
  CategoryScale,
  LineController,
  LineElement,
  PointElement,
  ScatterController,
  zoomPlugin
);

const CALC_COLOR = [
  "rgba(106, 76, 147, 1)",
  "rgba(25, 130, 196, 1)",
  "rgba(138, 201, 38, 1)",
  "rgba(255, 202, 58, 1)",
  "rgba(255, 89, 94, 1)",
];

export default {
  components: {
    LineChartGenerator,
  },
  props: {
    store: {
      type: Object,
      required: true,
    },
    layer: {
      type: Object,
      required: true,
    },
    temporalAggLevel: {
      type: String,
      required: false,
      default: "day",
    },
    propertyAggLevel: {
      type: String,
      required: false,
      default: "INTENSIDAD",
    },
    propertyAggItems: {
      required: false,
      default: () => [],
    },
    title: {
      type: String,
      required: false,
      default: null,
    },
    chartId: {
      type: String,
      default: "lineChart",
    },
    chartData: {
      type: Object,
      default: () => ({}),
    },
    datasetIdKey: {
      type: String,
      default: "label",
    },
    width: {
      type: Number,
      default: 400,
    },
    height: {
      type: Number,
      default: 400,
    },
    cssClasses: {
      default: "",
      type: String,
    },
    styles: {
      type: Object,
      default: () => {},
    },
    plugins: {
      type: Object,
      default: () => {},
    },
    categoryAggSelected: {
      type: String,
      required: true,
    },
    categories: {
      type: Array,
      required: true,
    },
    histogramGetData: {
      type: Function,
      required: false,
    },
    propertyUnits: {
      type: Object,
      required: true,
    },
  },
  async mounted() {
    this.layer.updatePopup();
  },
  data() {
    return {
      labels: null,
      data: null,
      // Object with {id: "property", value: [arrayWithProps]}
      datasets: {},
      propertyAggSelected: null,

      // Operation filter
      chartFilterOperation: [],
      chartOperationItems: this.CALCS,

      dialog: false,
    };
  },
  computed: {
    CALCS() {
      return this.categories.map((el) => {
        return {
          key: el.label,
          value: el.from == "Infinity" || el.from == "-Infinity" ? 0 : el.from,
        };
      });
    },
    calcHeaders() {
      const calcCategories = this.CALCS.map((el) => {
        return {
          text: `${el.key}`,
          value: el.key.toLowerCase(),
        };
      });

      calcCategories.unshift({
        text: this.$t("aggregation.property.label"),
        value: this.categoryAggSelected.toLowerCase(),
      });

      calcCategories.push({
        text: "check",
        value: "check",
      });

      return calcCategories;
    },
    calcItems() {
      const measurements = this.layer.feature.properties.measurements;
      const result = [];

      // Initialize an object to store sum and count for each property in each range
      const propertyData = {};

      // Initialize propertyData object for each property and range
      Object.keys(measurements[0].data).forEach((property) => {
        propertyData[property] = {};
        this.categories.forEach((range) => {
          propertyData[property][range.label] = {
            sum: 0,
            count: 0,
          };
        });
      });

      measurements.forEach((measurement) => {
        const depth = parseFloat(measurement.id);

        // Update propertyData object for each property and range
        Object.keys(measurement.data).forEach((property) => {
          this.categories.forEach((range) => {
            const from =
              range.from === -Infinity ? Number.NEGATIVE_INFINITY : range.from;
            const to =
              range.to === Infinity ? Number.POSITIVE_INFINITY : range.to;

            if (depth >= from && depth <= to) {
              propertyData[property][range.label].sum += parseFloat(
                measurement.data[property]
              );
              propertyData[property][range.label].count++;
            }
          });
        });
      });

      // Calculate average values and construct the result array
      Object.keys(propertyData).forEach((property) => {
        const propertyRow = {
          property: property,
        };

        this.categories.forEach((range) => {
          const depthKey = range.label;
          const sum = propertyData[property][depthKey].sum;
          const count = propertyData[property][depthKey].count;
          // If its NaN, set it to null
          if (isNaN(sum)) propertyRow[depthKey] = null;
          else
            propertyRow[depthKey] =
              count !== 0 ? (sum / count).toFixed(4) : null;
        });

        result.push(propertyRow);
      });

      return result.map((el) => {
        return {
          depth: el.property,
          ...el,
        };
      });
    },
    chartOptions() {
      return {
        responsive: "true",
        pan: {
          enabled: true,
          mode: "xy",
          modifierKey: "ctrl",
        },
        zoom: {
          wheel: {
            enabled: true,
          },
          pinch: {
            enabled: true,
          },
          mode: "xy",
        },
        plugins: {
          legend: {
            position: "bottom",
            onClick: null,
          },
          tooltip: {
            callbacks: {
              label: (context) => {
                return `${context.dataset.label}: ${context.formattedValue}`;
              },
            },
          },
          zoom: {
            zoom: {
              mode: "x",
              drag: {
                enabled: true,
                borderColor: "rgb(54, 162, 235)",
                borderWidth: 1,
                backgroundColor: "rgba(54, 162, 235, 0.3)",
              },
            },
          },
        },
        animation: true,
        maintainAspectRatio: false,
        scales: {
          x: {
            ticks: {
              autoSkip: false,
            },
          },
          y: {
            ticks: {
              callback: (value, index, ticks) => {
                return value;
              },
            },
          },
        },
      };
    },
    chartDataCalc() {
      let calcDatasets = [];
      // CON EL VALOR NONE, CAMBIAR EL POPUP PARA QUE SOLO MUESTRE 1 VALOR DE CADA PROPIEDAD, Y PONERLE UNIDADES
      this.chartFilterOperation?.forEach((calc) => {
        calcDatasets.push({
          label: calc.label,
          type: calc.type || "line",
          backgroundColor: calc.backgroundColor || "rgba(58, 134, 255, 1)",
          pointBackgroundColor:
            calc.pointBackgroundColor || "rgba(58, 134, 255, 1)",
          borderColor: calc.borderColor || "rgba(58, 134, 255, 1)",
          order: calc.order || 1,
          data: calc.data || [],
        });
      });

      return {
        labels: this.getChartLabelsForHistogram(calcDatasets),
        datasets: this.getChartDatasetsWithData(calcDatasets),
      };
    },
  },
  methods: {
    selectRow(element) {
      const items = this.calcItems;

      // Calculate if the row should be checked or not
      if (element.check) {
        // If the row is checked, add it to the dataset of the chart
        this.addDatasetToChart(element[this.categoryAggSelected.toLowerCase()]);
      } else {
        // If the row is unchecked, remove it from the dataset of the chart
        this.removeDatasetFromChart(
          element[this.categoryAggSelected.toLowerCase()]
        );
      }
    },
    addDatasetToChart(category) {
      const categoryMeasurements =
        this.layer?.feature?.properties?.measurements.map((el) => {
          return {
            key: el.id,
            value: el.data[category],
          };
        });

      const color = CALC_COLOR[this.chartFilterOperation.length % 5];

      this.chartFilterOperation.push({
        label: category,
        type: "line",
        backgroundColor: color,
        pointBackgroundColor: color,
        borderColor: color,
        order: 1,
        data: categoryMeasurements,
      });
    },
    removeDatasetFromChart(category) {
      this.chartFilterOperation = this.chartFilterOperation.filter(
        (el) => el.label !== category
      );
    },
    getChartDatasetsWithData(data) {
      const datasets = [];
      data.forEach((dataset) => {
        const calcData = [];
        dataset.data.forEach((element) => {
          // parse the value to a number if it is posible
          calcData.push({
            x: isNaN(element.key) ? element.key : parseFloat(element.key),
            y: isNaN(element.value) ? element.value : parseFloat(element.value),
          });
        });
        datasets.push({
          ...dataset,
          data: calcData,
        });
      });

      return datasets;
    },
    getChartLabelsForHistogram(data) {
      const values = [];
      data.forEach((element) => {
        element.data
          .filter((el) => el.value !== null)
          .map((el) => (isNaN(el.key) ? el.key : parseFloat(el.key)))
          .forEach((el) => {
            if (!values.includes(el)) values.push(el);
          });
      });
      return values;
    },
    resetZoom() {
      this.$refs.lineChart.getCurrentChart().resetZoom();
    },
  },
};
</script>

<style lang="css">
#popup-container {
  min-width: 80vw;
  overflow: hidden;
}

.chart-container {
  max-height: 70vh;
}
.zoom-container {
  position: relative;
  width: 100%;
  height: 100%;
}
.zoom-reset-btn {
  position: absolute;
  top: 0;
  right: 0;
  margin: 0.5rem;
}
</style>
/*% } %*/
/*% } %*/
