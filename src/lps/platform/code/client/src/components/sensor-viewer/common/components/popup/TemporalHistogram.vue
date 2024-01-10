/*% if (feature.SensorViewer) { %*/
<template>
  <div class="pa-0 ma-0">
    <v-dialog v-model="dialog" fullscreen transition="dialog-bottom-transition">
      <template v-slot:activator="{ on, attrs }">
        <v-btn
          block
          color="primary"
          dark
          v-bind="attrs"
          v-on="on"
          :loading="!data"
        >
          {{ $t("histogram.label") }}
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

        <v-container>
          <v-form ref="form">
            <v-row
              no-gutters
              justify="space-around"
              align="center"
              class="mb-4 mt-6"
            >
              <v-col cols="6">
                <v-select
                  v-model="propertyAggSelected"
                  :label="this.$t('aggregation.property.label')"
                  :items="propertyAggItems"
                  :item-text="(el) => this.$t(el.label)"
                  outlined
                  dense
                  :item-value="(el) => el.value.toLowerCase()"
                  @change="resetZoom"
                >
                </v-select>
              </v-col>
              <v-col cols="6">
                <v-select
                  v-model="chartFilterOperation"
                  :label="this.$t('aggregation.operational.label')"
                  :items="chartOperationItems"
                  :item-text="
                    (item) => $t(`aggregation.operational.items.${item.value}`)
                  "
                  outlined
                  multiple
                  dense
                  @change="resetZoom"
                ></v-select>
              </v-col>
              <v-col cols="6">
                <v-select
                  :label="this.$t('histogram.selector-label.from')"
                  v-model="chartFilterFrom"
                  :items="chartLabelItems"
                  clearable
                  outlined
                  dense
                  prepend-inner-icon="mdi-calendar-arrow-right"
                  item-text="id"
                  item-value="value"
                  :rules="chartFilterFromRules"
                  @change="validateForm"
                  @click:clear="validateForm"
                ></v-select>
              </v-col>
              <v-col cols="6">
                <v-select
                  :label="this.$t('histogram.selector-label.to')"
                  v-model="chartFilterTo"
                  :items="chartLabelItems"
                  clearable
                  outlined
                  dense
                  prepend-inner-icon="mdi-calendar-arrow-left"
                  item-text="id"
                  item-value="value"
                  :rules="chartFilterToRules"
                  @change="validateForm"
                  @click:clear="validateForm"
                ></v-select>
              </v-col>
            </v-row>
          </v-form>
        </v-container>
        <v-container id="popup-container" v-if="propertyAggSelected">
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
import { TIME_INTERVALS } from "@/components/sensor-viewer/common/utils/const.js";

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

const CALCS = [
  {
    value: "max",
  },
  {
    value: "min",
  },
  {
    value: "avg",
  },
];

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
    // Retrieve the histogram data making a request to the API
    await this.retrieveHistogramData();
    this.propertyAggSelected = this.propertyAggLevel.toLowerCase();
    this.calculateInitialFilterValues();
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
      chartFilterOperation: null,
      chartOperationItems: CALCS,

      // Chart Filters and Rules
      chartFilterFrom: null,

      chartFilterTo: null,
      dialog: false,
      chartFilterFromRules: [
        (v) => {
          if (this.chartFilterTo) {
            return v < this.chartFilterTo || "From must be < To";
          }
          return true;
        },
      ],
      chartFilterToRules: [
        (v) => {
          if (this.chartFilterFrom) {
            return v > this.chartFilterFrom || "To must be > From";
          }
          return true;
        },
      ],
    };
  },
  computed: {
    chartOptions() {
      return {
        responsive: "true",
        pan: {
          enabled: true,
          mode: "xy",
          modifierKey: "ctrl",
        },
        zoom: {
          mode: "xy",
          drag: {
            enabled: true,
            borderColor: "rgb(54, 162, 235)",
            borderWidth: 1,
            backgroundColor: "rgba(54, 162, 235, 0.3)",
          },
        },
        plugins: {
          legend: {
            position: "bottom",
            onClick: null,
          },
          tooltip: {
            callbacks: {
              label: (context) => {
                return `${context.dataset.label}: ${
                  context.formattedValue
                } ${this.$t(this.propertyUnits[this.propertyAggSelected])}`;
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
                return (
                  value +
                  " " +
                  this.$t(this.propertyUnits[this.propertyAggSelected])
                );
              },
            },
          },
        },
      };
    },
    chartLabelItems() {
      if (!this.data) return [];
      const items = [],
        chartLabels = this.getChartLabels(this.data);
      chartLabels.forEach((label) =>
        items.push({
          id: this.parseDateWithAggregation(label, this.temporalAggLevel),
          value: label,
        })
      );
      return items;
    },
    chartDataCalc() {
      let calcDatasets = [];

      this.chartFilterOperation?.forEach((calc, index) => {
        const order = calc !== "avg" ? 0 : 1;
        calcDatasets.push({
          label: this.$t(`aggregation.operational.items.${calc}`),
          type: "line",
          backgroundColor: CALC_COLOR[index],
          pointBackgroundColor: CALC_COLOR[index],
          borderColor: CALC_COLOR[index],
          order: order,
          data: this.getChartDataWithProperty(
            this.data,
            this.propertyAggSelected,
            calc
          ),
        });
      });
      return {
        labels: this.getChartLabelsForHistogram(this.data),
        datasets: calcDatasets,
      };
    },
  },
  methods: {
    retrieveHistogramData() {
      const options = this.store;
      return this.histogramGetData(this.layer.feature.id, options).then(
        (res) => (this.data = res.data)
      );
    },
    resetZoom() {
      this.$refs.lineChart.getCurrentChart().resetZoom();
    },
    getChartLabels(data) {
      return data.histogram.map((element) => Object.keys(element)[0]);
    },
    getChartLabelsForHistogram(data) {
      return data.histogram
        .filter((label) => {
          const labelKey = Object.keys(label)[0];
          if (this.chartFilterFrom != null && this.chartFilterTo != null) {
            if (
              new Date(labelKey) >= new Date(this.chartFilterFrom) &&
              new Date(labelKey) <= new Date(this.chartFilterTo)
            ) {
              return label;
            }
          } else return label;
        })
        .map((element) =>
          this.parseDateWithAggregation(
            Object.keys(element)[0],
            this.temporalAggLevel
          )
        );
    },
    getChartDataWithProperty(data, property, calc) {
      return data.histogram
        .filter((label) => {
          const labelKey = Object.keys(label)[0];
          if (this.chartFilterFrom != null && this.chartFilterTo != null) {
            if (
              new Date(labelKey) >= new Date(this.chartFilterFrom) &&
              new Date(labelKey) <= new Date(this.chartFilterTo)
            )
              return label;
          } else return label;
        })
        .map((element) => {
          return Object.values(element)[0]
            .map((el) => {
              return el.map((prop) => {
                if (Object.keys(prop)[0].includes(property + "_" + calc)) {
                  return prop[property + "_" + calc];
                } else return "-1";
              });
            })
            .flat()
            .filter((el) => el !== "-1");
        })
        .flat();
    },
    parseDateWithAggregation(date, aggLevel) {
      let dateParsed = new Date(date);
      let year = dateParsed.getFullYear();
      let month = dateParsed.getMonth() + 1;
      let day = dateParsed.getDate().toString().padStart(2, "0");
      let hours = dateParsed.getHours().toString().padStart(2, "0");
      let minutes = dateParsed.getMinutes().toString().padStart(2, "0");

      switch (aggLevel) {
        case TIME_INTERVALS.DAY:
          return `${hours}:${minutes}`;
        case TIME_INTERVALS.MONTH:
          return `${day}/${month}`;
        case TIME_INTERVALS.YEAR:
          return `${month}/${year}`;
        default:
          return `${day}/${month}/${year}`;
      }
    },
    calculateInitialFilterValues() {
      if (this.data) {
        const labels = this.getChartLabels(this.data);
        this.chartFilterFrom = labels[0];
        this.chartFilterTo = labels[labels.length - 1];
        this.chartFilterOperation = CALCS.map((i) => i.value);
      }
    },
    validateForm() {
      this.$refs.form.validate();
      this.resetZoom();
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
