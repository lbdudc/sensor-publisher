/*% if (feature.MV_LayerManagement) { %*/
<template>
  <v-tooltip left open-delay="200" color="var(--appColor)">
    <template v-slot:activator="{ on: onMenu, attrs }">
      <v-menu
        v-if="map"
        offset-y
        :close-on-content-click="false"
        v-model="isMenuOpen"
        transition="slide-y-transition"
        class="menu"
        v-bind="attrs"
        v-on="onMenu"
      >
        <template v-slot:activator="{ on: onTooltip, attrs }">
          <v-btn
            color="white"
            v-bind="attrs"
            v-on="{ ...onMenu, ...onTooltip }"
          >
            <v-icon>mdi-layers-triple</v-icon>
          </v-btn>
        </template>
        <v-container flex class="pb-2 pl-0 pr-0 white layers-container">
          <v-row class="title-row pl-4 py-1">
            /*% if (feature.MV_LM_HideLayer) { %*/
            <v-col cols="1" class="text-center col-visibility">
              <v-btn v-if="layersShown" x-small icon @click="hideAllLayers">
                <v-icon color="white">mdi-eye</v-icon>
              </v-btn>
              <v-btn v-else x-small icon @click="showAllLayers">
                <v-icon color="white">mdi-eye-off</v-icon>
              </v-btn>
            </v-col>
            /*% } %*/
            <v-col class="pl-2" cols="2">{{ $t("layerManager.name") }}</v-col>
            /*% if (feature.MV_LM_Style) { %*/
            <v-col class="text-center" cols="2">
              {{ $t("layerManager.style") }}
            </v-col>
            /*% } %*/
            /*% if (feature.MV_LM_Order || feature.MV_LM_CenterViewOnLayer || feature.MV_LM_StylePreview) { %*/
            <v-col class="pl-9 pr-2" cols="4">
              {{ $t("layerManager.center") }}/{{ $t("layerManager.order") }}
            </v-col>
            /*% } %*/
            /*% if (feature.MV_LM_Opacity) { %*/
            <v-col class="text-center" cols="3">
              {{ $t("layerManager.opacity") }}
            </v-col>
            /*% } %*/
          </v-row>

          <div class="rows-container">
            <v-row
              align="center"
              class="overlay-row"
              v-for="layer in calcOverlays"
              :key="layer.name"
            >
              <v-col class="overlay-col" cols="3">
                /*% if (feature.MV_LM_HideLayer) { %*/
                <v-checkbox
                  class="mt-1 pt-0"
                  :label="layer.name"
                  v-model="layer.selected"
                /*% if (!feature.MV_LM_ExternalLayer) { %*/
                  @change="(val) => changeSelectedLayer(layer, val)"
                >
                /*% } else { %*/
                  :readonly="layerBeingEdited === layer.id"
                   @change="
                      (val) =>
                        layerBeingEdited === layer.id
                          ? null
                          : changeSelectedLayer(layer, val)
                  "
                >
                  <template v-slot:label>
                    <div v-if="layerBeingEdited === layer.id">
                      <v-text-field
                        :label="$t('layerManager.editLayer')"
                        outlined
                        dense
                        type="input"
                        v-model="newLayerName"
                        :rules="[(v) => !!v || $t('layerManager.nameRequired')]"
                        @keydown.enter="editExternal(layer)"
                      ></v-text-field>
                    </div>
                    <div v-else>{{ layer.name }}</div>
                  </template>
                /*% } %*/
                </v-checkbox>
                /*% } else { %*/
                <div class="mt-1 pt-0">
                  /*% if (!feature.MV_LM_ExternalLayer) { %*/
                  {{ layer.name }}
                  /*% } else { %*/
                  <div v-if="layerBeingEdited === layer.id">
                    <v-text-field
                      :label="$t('layerManager.editLayer')"
                      outlined
                      dense
                      type="input"
                      v-model="newLayerName"
                      :rules="[(v) => !!v || $t('layerManager.nameRequired')]"
                      @keydown.enter="editExternal(layer)"
                    ></v-text-field>
                  </div>
                  <div v-else>{{ layer.name }}</div>
                  /*% } %*/
                </div>
                /*% } %*/
              </v-col>

              /*% if (feature.MV_LM_Style) { %*/
              <v-col class="ma-0 pa-0" cols="2">
                <div v-if="getLayerType(layer) === 'WMSLayer'">
                  <v-select
                    class="layer-selector"
                    :label="$t('layerManager.style')"
                    :value="getLayer(layer).getStyle()"
                    :items="getLayerStyles(layer)"
                    item-text="id"
                    @change="(val) => changeLayerStyle(val, layer)"
                  ></v-select>
                </div>
                <div v-else-if="getLayerType(layer) === 'GeoJSONLayer'">
                  <input
                    class="color-selector"
                    type="color"
                    :value="getLayer(layer).getColor()"
                    @change="(val) => changeLayerColor(val, layer)"
                  />
                </div>
              </v-col>
              /*% } %*/

              /*% if (feature.MV_LM_Order || feature.MV_LM_CenterViewOnLayer || feature.MV_LM_StylePreview || feature.MV_LM_ExternalLayer) { %*/
              <v-col class="ma-0 pa-0 pl-6" cols="5">
                /*% if (feature.MV_LM_CenterViewOnLayer) { %*/
                <v-btn small icon @click="setCenterOnLayer(layer)">
                  <v-icon :color="layer.centered ? 'primary' : ''">
                    mdi-crosshairs-gps
                  </v-icon>
                </v-btn>
                /*% } %*/
                /*% if (feature.MV_LM_StylePreview) { %*/
                <v-btn icon @click="wmsLegendSelected(layer)">
                  <v-icon>mdi-map-legend</v-icon>
                </v-btn>
                /*% } %*/
                /*% if (feature.MV_LM_Order) { %*/
                <v-btn
                  small
                  icon
                  :disabled="layer.order === 0"
                  @click="layerOrderUp(layer)"
                >
                  <v-icon> mdi-arrow-up </v-icon>
                </v-btn>
                <v-btn
                  small
                  icon
                  :disabled="layer.order === calcOverlays.length - 1"
                  @click="layerOrderDown(layer)"
                >
                  <v-icon> mdi-arrow-down</v-icon>
                </v-btn>
                /*% } %*/
                /*% if (feature.MV_LM_ExternalLayer) { %*/
                <v-tooltip
                  v-if="isExternalLayer(layer)"
                  left
                  open-delay="200"
                  color="var(--appColor)"
                >
                  <template v-slot:activator="{ on }">
                    <v-btn
                      v-if="layerBeingEdited === layer.id"
                      v-on="on"
                      small
                      icon
                      @click="editExternal(layer)"
                    >
                      <v-icon> save </v-icon>
                    </v-btn>
                    <v-btn
                      v-else
                      v-on="on"
                      small
                      icon
                      @click="
                        (layerBeingEdited = layer.id),
                          (newLayerName = layer.name)
                      "
                    >
                      <v-icon> edit </v-icon>
                    </v-btn>
                  </template>
                  <span>{{ $t("layerManager.edit") }}</span>
                </v-tooltip>
                <v-tooltip
                  v-if="isExternalLayer(layer)"
                  left
                  open-delay="200"
                  color="var(--appColor)"
                >
                  <template v-slot:activator="{ on }">
                    <v-btn v-on="on" small icon @click="deleteExternal(layer)">
                      <v-icon>mdi-delete</v-icon>
                    </v-btn>
                  </template>
                  <span>{{ $t("layerManager.delete") }}</span>
                </v-tooltip>
                /*% } %*/
              </v-col>
              /*% } %*/

              /*% if (feature.MV_LM_Opacity) { %*/
              <v-col class="ma-0 pa-0 pr-2" cols="2">
                <v-slider
                  v-bind:value="layer.slider"
                  min="0"
                  max="100"
                  thumb-label
                  @change="(val) => changeOpacity(val, layer)"
                ></v-slider>
              </v-col>
              /*% } %*/
            </v-row>
          </div>
        </v-container>
      </v-menu>
    </template>
    <span>{{ $t("layerManager.title") }}</span>
  </v-tooltip>
</template>

<script>
export default {
  props: {
    baseLayers: {
      type: Array,
      required: false,
    },
    value: {
      type: String,
      required: false,
    },
    map: {
      type: Object,
      required: true,
    },
    overlays: {
      type: Array,
      required: false,
    },
    multiple: {
      type: Boolean,
      required: false,
      default: false,
    },
    loadingMap: {
      type: Boolean,
      required: false,
    },
    /*% if (feature.MV_LM_StylePreview) { %*/
    buildWMSLegendControl: {
      type: Function,
      required: false,
      default: () => {},
    },
    /*% } %*/
  },
  data() {
    return {
      isMenuOpen: false,
      calcOverlays: [],
      /*% if (feature.MV_LM_ExternalLayer) { %*/
      layerBeingEdited: null,
      newLayerName: null,
      /*% } %*/
    };
  },
  watch: {
    isMenuOpen(newVal) {
      this.$emit("menu-open", newVal);

      /*% if (feature.MV_LM_ExternalLayer) { %*/
      if (newVal) {
        this.layerBeingEdited = null;
      }
      /*% } %*/
    },
    loadingMap(newVal) {
      if (!newVal) this.calculateOverlays();
    },
  },
  computed: {
    /*% if (feature.MV_LM_HideLayer) { %*/
    layersShown() {
      return this.calcOverlays.filter((layer) => layer.selected).length > 0;
    },
    /*% } %*/
  },
  mounted() {
    this.calculateOverlays();
  },
  methods: {
    calculateOverlays() {
      this.calcOverlays = this.map
        .getLayers()
        .filter((el) => !el.options.baseLayer)
        .map((layer, index) => {
          return {
            id: layer.options.id,
            name: layer.options.label,
            selected: layer.options.selected,
            slider: layer.options.opacity * 100,
            order: index,
            centered: false,
          };
        });
    },
    /*% if (feature.MV_LM_Style) { %*/
    changeLayerStyle(newStyle, layerSelected) {
      const realLayer = this.getLayer(layerSelected);
      realLayer.setStyle(newStyle);
    },
    changeLayerColor(newColor, layerSelected) {
      const realLayer = this.getLayer(layerSelected);
      realLayer.setColor(newColor.target.value);
    },
    /*% } %*/
    /*% if (feature.MV_LM_Opacity) { %*/
    changeOpacity(newOpacity, layerSelected) {
      const realLayer = this.getLayer(layerSelected);
      realLayer.setOpacity(newOpacity);
    },
    /*% } %*/
    /*% if (feature.MV_LM_HideLayer) { %*/
    changeSelectedLayer(layerSelected, newVal) {
      const realLayer = this.getLayer(layerSelected);
      if (!newVal) {
        layerSelected.selected = false;
        this.map.hideLayer(realLayer);
      } else {
        layerSelected.selected = true;
        this.map.showLayer(realLayer);
      }
    },
    /*% } %*/
    /*% if (feature.MV_LM_CenterViewOnLayer) { %*/
    setCenterOnLayer(layerSelected) {
      // If layer was already selected, we center on all layers
      if (layerSelected.centered) {
        this.calcOverlays = this.calcOverlays.map((el) => {
          el.centered = false;
          return el;
        });
        this.map.centerView("visible");
        return;
      }

      // Else, set center with the layer bounds
      const realLayer = this.getLayer(layerSelected);
      realLayer.getBounds().then((bounds) => {
        this.map.centerView(bounds);
        // Change state and style
        this.calcOverlays = this.calcOverlays.map((el) => {
          if (el.centered) el.centered = false;
          if (el.name === layerSelected.name) el.centered = true;
          return el;
        });
      });
    },
    /*% } %*/
    /*% if (feature.MV_LM_Order) { %*/
    // Sets the order of a layer to a higher value
    layerOrderUp(layer) {
      if (layer.order === 0) return;
      const index = layer.order;

      // Set new order on layer list
      this.calcOverlays[index].order = this.calcOverlays[index].order - 1;
      this.calcOverlays[index - 1].order =
        this.calcOverlays[index - 1].order + 1;

      // Set new order on map
      this.getLayer(layer).setOrder(layer.order);
      this.getLayer(this.calcOverlays[index - 1]).setOrder(layer.order + 1);

      // Re-render list sorted
      this.calcOverlays = this.calcOverlays.sort((a, b) => a.order - b.order);
    },
    // Sets the order of a layer to a lower value
    layerOrderDown(layer) {
      if (layer.order === this.calcOverlays.length - 1) return;

      const index = layer.order;
      // Set new order on layer list
      this.calcOverlays[index].order = this.calcOverlays[index].order + 1;
      this.calcOverlays[index + 1].order =
        this.calcOverlays[index + 1].order - 1;

      // Set new order on map
      this.getLayer(layer).setOrder(layer.order);
      this.getLayer(this.calcOverlays[index + 1]).setOrder(layer.order - 1);

      // Re-render list sorted
      this.calcOverlays = this.calcOverlays.sort((a, b) => a.order - b.order);
    },
    /*% } %*/
    /*% if (feature.MV_LM_ExternalLayer) { %*/
    editExternal(layer) {
      if (!this.newLayerName) return;

      this.getLayer(layer).setLabel(this.newLayerName);

      this.calculateOverlays();
      this.newLayerName = null;
      this.layerBeingEdited = null;
    },
    deleteExternal(layer) {
      this.map.removeLayer(layer.id);

      this.calculateOverlays();
    },
    /*% } %*/
    /*% if (feature.MV_LM_HideLayer) { %*/
    showAllLayers() {
      this.calcOverlays.forEach((layer) => {
        this.changeSelectedLayer(layer, true);
      });
    },
    hideAllLayers() {
      this.calcOverlays.forEach((layer) => {
        this.changeSelectedLayer(layer, false);
      });
    },
    /*% } %*/
    getLayer(layerSelected) {
      return this.map
        .getLayers()
        .find((layer) => layer.options.id === layerSelected.id);
    },
    /*% if (feature.MV_LM_Style) { %*/
    getLayerType(layerSelected) {
      const findLayer = this.map.getLayers().find((layer) => {
        return layer.options.id === layerSelected.id;
      });

      return findLayer.constructor.name;
    },
    getLayerStyles(layerSelected) {
      const findLayer = this.getLayer(layerSelected);

      return findLayer.getAvailableStyles();
    },
    /*% } %*/
    /*% if (feature.MV_LM_ExternalLayer) { %*/
    isExternalLayer(layerSelected) {
      const findLayer = this.getLayer(layerSelected);

      return findLayer.options.added ? true : false;
    },
    /*% } %*/
    /*% if (feature.MV_LM_StylePreview) { %*/
    wmsLegendSelected(layer) {
      this.$emit("wms-legend-selected", layer);
    },
    /*% } %*/
  },
};
</script>
<style scoped>
body {
  padding: 0;
  margin: 0;
}

.layers-container {
  padding-top: 0;
  width: 600px;
  margin-top: 0;
}

.title-row {
  margin-top: 0;
  padding-top: 0;
  margin-bottom: 1px;
  position: sticky;
  top: 0;
  background-color: #1976d2;
  opacity: 1;
  z-index: 100;
  font-size: small;
  color: white;
  box-shadow: 0 2px 2px -1px rgba(0, 0, 0, 0.4);
}

.col-visibility {
  padding-left: 11px;
}

.rows-container {
  overflow-y: auto;
  overflow-x: hidden;
  min-width: 510px;
  max-height: 50vh;
}

.overlay-row {
  margin-top: 0;
  padding-top: 0;
  margin-left: 0.8em;
  margin-right: 0.1em;
  padding-bottom: 1em;
}

.overlay-col {
  margin: 0;
  padding: 0;
  padding-top: 5px;
  font-size: small;
}

/*% if (feature.MV_LM_Style) { %*/
.layer-selector {
  float: right;
  margin-right: 2px;
  padding: 0;
  font-size: small;
  max-width: 70px;
}

.color-selector {
  float: right;
  margin-right: 2px;
  padding-top: 6px;
  max-width: 40%;
}
/*% } %*/

/*% if (feature.MV_LM_ExternalLayer) { %*/
.v-text-field {
  font-size: small;
}
/*% } %*/

.overlay-style-col {
  font-size: small;
}

::v-deep .v-messages {
  display: none;
}

::v-deep .v-label {
  font-size: small;
}

::v-deep .v-slider__thumb-label {
  position: absolute;
}
</style>
/*% } %*/
