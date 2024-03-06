/*% if (feature.MV_LayerManagement || feature.MV_LM_ExternalLayer || feature.MV_T_ViewMapAsList || feature.MV_T_InformationMode) { %*/
<template>
  <div v-if="map" class="map-controls">
    <div class="column">
      <v-btn
        color="white"
        v-if="$vuetify.breakpoint.smAndDown"
        @click="showButtons"
        max-height="25"
        max-width="20"
      >
        <div v-if="!showBtns && $vuetify.breakpoint.smAndDown">
          <v-icon>mdi-wrench-outline</v-icon>
          <v-icon>mdi-chevron-down</v-icon>
        </div>
        <div v-else>
          <v-icon>mdi-wrench-outline</v-icon>
          <v-icon>mdi-chevron-up</v-icon>
        </div>
      </v-btn>
    </div>

    <div v-show="showBtns || !$vuetify.breakpoint.smAndDown">
      /*% if (feature.MV_LayerManagement) { %*/
      <div class="column">
        <layer-manager
          :loadingMap="loadingMap"
          :baseLayers="baseLayers"
          :overlays="overlays"
          :map="map"
          @wms-legend-selected="wmsLegendSelected"
        ></layer-manager>
      </div>
      /*% } %*/

      /*% if (feature.MV_LM_ExternalLayer) { %*/
      <div class="column">
        <v-tooltip left open-delay="200" color="var(--appColor)">
          <template v-slot:activator="{ on, attrs }">
            <v-btn
              color="white"
              @click="buildNewLayerControl"
              v-bind="attrs"
              v-on="on"
            >
              <v-icon>mdi-layers-plus</v-icon>
            </v-btn>
          </template>
          <span>{{ $t("addNewLayer.addNewLayerTitle") }}</span>
        </v-tooltip>
      </div>
      /*% } %*/

      /*% if (feature.MV_LM_BaseLayerSelector) { %*/
      <div class="column">
        <v-tooltip left open-delay="200" color="var(--appColor)">
          <template v-slot:activator="{ on, attrs }">
            <v-btn
              color="white"
              @click="buildChangeBaseLayerControl"
              v-bind="attrs"
              v-on="on"
            >
              <v-icon>mdi-layers-edit</v-icon>
            </v-btn>
          </template>
          <span>{{ $t("changeBaseLayer.changeBaseLayerTitle") }}</span>
        </v-tooltip>
      </div>
      /*% } %*/

      /*% if (feature.MV_T_ViewMapAsList) { %*/
      <div class="column">
        <v-tooltip left open-delay="200" color="var(--appColor)">
          <template v-slot:activator="{ on, attrs }">
            <v-btn
              color="white"
              @click="buildShowListControl"
              v-bind="attrs"
              v-on="on"
            >
              <v-icon>mdi-format-list-bulleted</v-icon>
            </v-btn>
          </template>
          <span>{{ $t("mapViewer.listLayerElements") }}</span>
        </v-tooltip>
      </div>
      /*% } %*/

      /*% if (feature.MV_T_InformationMode) { %*/
      <div class="column">
        <v-tooltip left open-delay="200" color="var(--appColor)">
          <template v-slot:activator="{ on, attrs }">
            <v-btn
              :class="WMSInfoControlBtn ? null : 'btn-selected'"
              @click="buildWMSInfoControl"
              v-bind="attrs"
              v-on="on"
            >
              <v-icon v-if="WMSInfoControlBtn" color="red"
              >mdi-alert-circle</v-icon
              >
              <v-icon v-else color="green">mdi-alert-circle</v-icon>
            </v-btn>
          </template>
          <span>{{ $t("mapViewer.wmsDetail.title") }}</span>
        </v-tooltip>
      </div>
      /*% } %*/
    </div>
  </div>
</template>

<script>
/*% if (feature.MV_LayerManagement) { %*/
import LayerManager from "./LayerManager.vue";
/*% } %*/

export default {
  name: "RightMapControls",
  components: {
    /*% if (feature.MV_LayerManagement) { %*/
    LayerManager,
    /*% } %*/
  },
  props: {
    /*% if (feature.MV_LM_ExternalLayer) { %*/
    buildNewLayerControl: {
      type: Function,
      default: () => {},
    },
    /*% } %*/
    /*% if (feature.MV_LM_BaseLayerSelector) { %*/
    buildChangeBaseLayerControl: {
      type: Function,
      default: () => {},
    },
    /*% } %*/
    /*% if (feature.MV_T_ViewMapAsList) { %*/
    buildShowListControl: {
      type: Function,
      default: () => {},
    },
    /*% } %*/
    /*% if (feature.MV_T_InformationMode) { %*/
    buildWMSInfoControl: {
      type: Function,
      default: () => {},
    },
    WMSInfoControlBtn: {
      type: Boolean,
      default: false,
    },
    /*% } %*/
    baseLayers: {
      type: Array,
      default: () => [],
    },
    overlays: {
      type: Array,
      default: () => [],
    },
    map: {
      type: Object,
      default: () => {},
    },
    loadingMap: {
      type: Boolean,
      required: false,
    },
  },
  data() {
    return {
      showBtns: false,
    };
  },
  methods: {
    wmsLegendSelected(layer) {
      this.$emit("wms-legend-selected", layer);
    },

    showButtons() {
      this.showBtns = !this.showBtns;
    },
  },
};
</script>

<style scoped>
.map-controls {
  position: absolute;
  z-index: 1000;
  top: 4px;
  right: 0px;
  margin-right: 0px;
  padding: 0;
}
.column {
  margin-top: 0.6em;
  padding-right: 8px;
}

/* we will explain what these classes do next! */
.v-enter-active,
.v-leave-active {
  transition: opacity 0.4s ease;
}

.v-enter-from,
.v-leave-to {
  opacity: 0;
}

.btn-selected {
  border: 2px solid #1976d2;
}

.arrow-icon {
  font-size: 24px;
}
</style>
/*% } %*/
