/*% if (feature.MapViewer) { %*/
<template>
  <v-container fluid id="map-container">
    <v-row class="mt-1" no-gutters>
      <v-col cols="6" class="d-none d-md-block">
        <span class="headline no-split-words">
          {{ $t($route.meta.label) }}
        </span>
      </v-col>
      /*% if (feature.MV_MM_MMV_MapSelectorInMapViewer) { %*/
      <v-col cols="12" md="6">
        <v-row class="justify-end" no-gutters>
          <v-col cols="12" sm="5" class="mt-4 mr-4 text-center text-sm-right">
            <label>
              {{ $t("mapViewer.mapSelector") }}
            </label>
          </v-col>
          <v-col cols="12" sm="6">
            <v-select
              :items="maps"
              item-value="id"
              item-text="label"
              :label="$t('mapViewer.map')"
              v-if="maps.length > 0"
              v-model="mapSelected"
              @change="changeRoute"
              solo
            >
            </v-select>
          </v-col>
        </v-row>
      </v-col>
      /*% } %*/
    </v-row>
    /*% if (feature.MV_T_F_BasicSearch || feature.MV_T_Export) { %*/
    <v-row class="mt-1 mb-4" no-gutters>
      <v-col class="text-right">
        /*% if (feature.MV_T_F_BasicSearch) { %*/
        <v-text-field
          dense
          v-model="form.query"
          append-icon="search"
          @click:append="searchInMap"
          :label="$t('mapViewer.searchInMap')"
          @keydown.enter="searchInMap"
          single-line
          hide-details
          class="d-inline-block"
        ></v-text-field>
        /*% } %*/
        /*% if (feature.MV_T_Export) { %*/
        <v-btn class="primary ml-2" @click="openDialog('export-management')">
          {{ $t('mapViewer.export') }}
        </v-btn>
        /*% } %*/
      </v-col>
    </v-row>
    /*% } %*/

    <div ref="map" id="map">
    /*% if (feature.MV_LayerManagement || feature.MV_LM_ExternalLayer || feature.MV_T_ViewMapAsList || feature.MV_T_InformationMode) { %*/
      <right-map-controls
      /*% if (feature.MV_LM_ExternalLayer) { %*/:buildNewLayerControl="buildNewLayerControl"/*% } %*/
      /*% if (feature.MV_LM_BaseLayerSelector) { %*/:buildChangeBaseLayerControl="buildChangeBaseLayerControl"/*% } %*/
      /*% if (feature.MV_T_ViewMapAsList) { %*/:buildShowListControl="buildShowListControl"/*% } %*/
      /*% if (feature.MV_T_InformationMode) { %*/:buildWMSInfoControl="buildWMSInfoControl"/*% } %*/
      /*% if (feature.MV_T_InformationMode) { %*/:WMSInfoControlBtn="WMSInfoControlBtn"/*% } %*/
        :baseLayers="baseLayers"
        :overlays="viewOverlays"
        :map="map"
        :loadingMap="loadingMap"
      /*% if (feature.MV_LM_StylePreview) { %*/@wms-legend-selected="buildWMSLegendControl"/*% } %*/
      ></right-map-controls>
    /*% } %*/
    </div>

    /*% if (feature.MV_T_Export || feature.MV_T_ViewMapAsList || feature.MV_T_InformationMode || feature.MV_LM_BaseLayerSelector) { %*/
    <v-dialog
      v-model="showDialog"
      hide-overlay
      :fullscreen="$vuetify.breakpoint.mdAndDown"
      :width="/*% if (feature.MV_T_InformationMode) { %*/wmsFeatures != null ? 1200 :/*% } %*/500"
      @click:outside="closeDialog"
    >
      <component
        v-if="dialogComponent"
        v-bind:is="dialogComponent"
        :map="map"
      /*% if (feature.MV_T_InformationMode) { %*/:features="wmsFeatures"/*% } %*/
      /*% if (feature.MV_LM_StylePreview) { %*/:wmsLegend="wmsLegendSelected"/*% } %*/
        @layer-added="refreshLayerManager"
        @close="closeDialog"
      ></component>
    </v-dialog>
    /*% } %*/
    /*% if (feature.MV_DetailOnClick) { %*/
    <information-popup
      ref="informationPopup"
      :form="popupForm"
      :layer="popupLayer"
    ></information-popup>
    /*% } %*/
  </v-container>
</template>

<script>
import maps from "./config-files/maps.json";
import layers from "./config-files/layers.json";
import { getStyle } from "@/common/map-styles-common";
import { Map, TileLayer, WMSLayer, GeoJSONLayer } from "@lbdudc/map-viewer";
/*% if (feature.MV_DetailOnClick) { %*/
import InformationPopup from "@/components/map-viewer/InformationPopup";
/*% } %*/
import properties from "@/properties";
/*% if (feature.MV_CI_Map || feature.MV_T_ZoomWindow) { %*/
import devCheck from "@/common/device-check";
/*% } %*/
/*% if (feature.MV_T_Export) { %*/
import ExportManagement from "./export-management/ExportManagement";
/*% } %*/
/*% if (feature.MV_LayerManagement || feature.MV_LM_ExternalLayer || feature.MV_T_ViewMapAsList || feature.MV_T_InformationMode) { %*/
import RightMapControls from "./controls/RightMapControls.vue";
/*% } %*/
/*% if (feature.MV_LM_ExternalLayer) { %*/
import AddNewLayer from "./add-new-layer/AddNewLayer";
/*% } %*/
/*% if (feature.MV_LM_BaseLayerSelector ) { %*/
import ChangeBaseLayer from "./change-base-layer/ChangeBaseLayer";
/*% } %*/
/*% if (feature.MV_T_ViewMapAsList) { %*/
import ListLayerManagement from "./list-layer-management/ListLayerManagement";
/*% } %*/
/*% if (feature.MV_T_InformationMode) { %*/
import WMSInformation from "./wms-information/WMSInformation";
/*% } %*/
/*% if (feature.MV_LM_StylePreview) { %*/
import WMSLegend from "./wms-legend/WMSLegend.vue";
/*% } %*/
/*% if (feature.DM_A_C_RouteCalculation) { %*/
const MVRouteCalculationRepository = RepositoryFactory.get("MVRouteCalculationRepository");
/*% } %*/
/*% if (feature.DM_A_C_RC_GraphHopper) { %*/
import querystring from "querystring";
/*% } %*/
import RepositoryFactory from "@/repositories/RepositoryFactory";
/*% if (feature.MV_T_E_F_URL) { %*/
const MVExportManagementRepository = RepositoryFactory.get("MVExportManagementRepository");
/*% } %*/
/*% if (feature.MV_MS_GJ_Paginated) { %*/
import { incrementBBox } from "./utils/utils";
/*% } %*/

export default {
  name: "Map",
  data() {
    return {
      /*% if (feature.MV_T_F_BasicSearch) { %*/
      form: { query: null },
      /*% } %*/
      /*% if (feature.MV_T_Export || feature.MV_T_ViewMapAsList || feature.MV_T_InformationMode || feature.MV_LM_BaseLayerSelector) { %*/
      dialogComponent: null,
      showDialog: false,
      /*% if (feature.MV_T_InformationMode) { %*/
      WMSInfoControlBtn: true,
      wmsFeatures: null,
      /*% } %*/
      /*% } %*/
      /*% if (feature.DM_A_C_RouteCalculation) { %*/
      overlays: [],
      /*% } %*/
      mapSelected: null,
      map: null,
      maps: [],
      baseLayers: null,
      viewOverlays: [],
      loadingMap: false,
      /*% if (feature.MV_DetailOnClick) { %*/
      popupForm: null,
      popupLayer: null,
      /*% } %*/
      /*% if (feature.MV_LM_StylePreview) { %*/
      wmsLegendSelected: null,
      /*% } %*/
    };
  },
  watch: {
    $route() {
      const routeSelectedMap = this.maps.find(
        (map) => map.name === this.$route.params.id
      );
      if (routeSelectedMap) {
        this.mapSelected = routeSelectedMap.id;
        this.loadMap();
      }
      this.viewOverlays = this.loadOverlays();
      // Needed for child to refresh his overlays
      this.refreshLayerManager();

      // Deactivate info control if it is active
      if (!this.WMSInfoControlBtn) {
        this.buildWMSInfoControl();
      }
    },
  },
  /*% if (feature.MV_T_Export || feature.MV_T_ViewMapAsList || feature.MV_T_InformationMode || feature.MV_DetailOnClick) { %*/
  components: {
    /*% if (feature.MV_LayerManagement || feature.MV_LM_ExternalLayer || feature.MV_T_ViewMapAsList || feature.MV_T_InformationMode) { %*/
    RightMapControls,
    /*% } %*/
    /*% if (feature.MV_DetailOnClick) { %*/"information-popup": InformationPopup,/*% } %*/
    /*% if (feature.MV_T_Export) { %*/"export-management": ExportManagement,/*% } %*/
    /*% if (feature.MV_LM_BaseLayerSelector) { %*/"change-base-layer": ChangeBaseLayer,/*% } %*/
    /*% if (feature.MV_T_ViewMapAsList) { %*/"list-layer-management": ListLayerManagement,/*% } %*/
    /*% if (feature.MV_T_InformationMode) { %*/"wms-information": WMSInformation,/*% } %*/
    /*% if (feature.MV_LM_ExternalLayer) { %*/"add-new-layer": AddNewLayer,/*% } %*/
    /*% if (feature.MV_LM_StylePreview) { %*/"wms-legend": WMSLegend,/*% } %*/
  },
  /*% } %*/
  mounted() {
    this.maps = maps.maps.map((map, index) => {
      return {
        id: index,
        name: map.name,
        label: this.$t("mapViewer.map-label." + map.name.replace('.', '-'))
      };
    });
    let mapFromRoute = null;

    /*% if (feature.MV_T_F_BasicSearch) { %*/
    this.form.query = this.$route.query.query;
    /*% } %*/

    if (this.$route.params.id) {
      mapFromRoute = this.maps.find(map => map.name === this.$route.params.id);
      if (mapFromRoute) {
        this.mapSelected = mapFromRoute.id;
        this.loadMap();
      }
    }
    if (!mapFromRoute && this.maps.length === 1) {
      this.mapSelected = this.maps[0].id;
      this.changeRoute();
    } else if (this.$route.params.id) {
      this.changeRoute();
    }

    /*% if (feature.MV_MS_GJ_Paginated) { %*/
    // update features on map move
    this.map.getLeafletMap().on("moveend", () => {
      const bbox = this.map.getLeafletMap().getBounds();
      this.updateLayer(bbox);
    });
    /*% } %*/
  },
  beforeDestroy() {
    localStorage.setItem("state", JSON.stringify(this.map.exportState()));
  },
  methods: {
    loadOverlays() {
      return layers.layers.filter((l) =>
        maps.maps[this.mapSelected].layers.find(
          (mapLayer) => !mapLayer.baseLayer && mapLayer.name == l.name
        )
      );
    },
    /*% if (feature.MV_T_Export || feature.MV_T_ViewMapAsList || feature.MV_T_InformationMode || feature.MV_LM_StylePreview || feature.MV_LM_BaseLayerSelector) { %*/
    closeDialog() {
      this.dialogComponent = "";
      this.showDialog = false;
      this.wmsFeatures = null;
      this.wmsLegendSelected = null;
    },
    openDialog(componentName) {
      this.dialogComponent = componentName;
      this.showDialog = true;
    },
    /*% } %*/
    changeRoute() {
      if (
        this.$route.params.id !== this.maps[this.mapSelected].name /*% if (feature.MV_T_F_BasicSearch) { %*/||
        this.$route.query.query !== this.form.query /*% } %*/
      ) {
        this.$router.replace({
          name: "MapViewer",
          params: {
            id: this.maps[this.mapSelected].name
          }/*% if (feature.MV_T_F_BasicSearch) { %*/,
          query: { query: this.form.query || undefined }/*% } %*/
        });
      }
    },
    /* Map Controls Functions */
    /*% if (feature.MV_LM_ExternalLayer) { %*/
    buildNewLayerControl() {
      this.dialogComponent = "add-new-layer";
      this.showDialog = true;
    },
    /*% } %*/
    /*% if (feature.MV_LM_BaseLayerSelector) { %*/
    buildChangeBaseLayerControl() {
      this.dialogComponent = "change-base-layer";
      this.showDialog = true;
    },
    /*% } %*/
    /*% if (feature.MV_T_ViewMapAsList) { %*/
    buildShowListControl() {
      this.openDialog("list-layer-management");
    },
    /*% } %*/
    /*% if (feature.MV_LM_StylePreview) { %*/
    buildWMSLegendControl(layer) {
      this.wmsLegendSelected = layer;
      this.openDialog("wms-legend");
    },
    /*% } %*/
    /*% if (feature.MV_T_InformationMode) { %*/
    buildWMSInfoControl() {
      if (this.WMSInfoControlBtn) {
        document.getElementById("map").style.setProperty("cursor", "pointer");
        this.map.activateLayerInfo(this.wmsCallback);
      } else {
        this.map.deactivateLayerInfo();
        document.getElementById("map").style.setProperty("cursor", "grab");
      }
      this.WMSInfoControlBtn = !this.WMSInfoControlBtn;
    },
    /*% } %*/
    loadMap() {
      // If map is already created, we remove it
      if (this.map != null) {
        this.map.clearMap();
        this.map.getLeafletMap().off();
        this.map.getLeafletMap().remove();
      }

      // We don't continue creating a map if there is no one selected
      if (this.mapSelected == null) {
        return;
      }

      // Setting title's tab
      document.title =
        this.$t(`mapViewer.map-label.${maps.maps[this.mapSelected].name}`) +
        " - " +
        properties.APP_NAME;

      // By default, if the user don't define his own CRS, we use the Leaflet's default
      let crs = L.CRS.EPSG3857;
      if (
        maps.maps[this.mapSelected].mapOptions &&
        maps.maps[this.mapSelected].mapOptions.crs
      ) {
        const crsOptions = maps.maps[this.mapSelected].mapOptions.crs;
        crs = new L.Proj.CRS(
          crsOptions.srid,
          crsOptions.proj4Config.params,
          crsOptions.proj4Config.options
        );
      }

      let mapOptions = { ...maps.maps[this.mapSelected].mapOptions };
      mapOptions.crs = crs;

      this.map = new Map(
        "map",
        maps.maps[this.mapSelected].name,
        maps.maps[this.mapSelected].center,
        mapOptions
      );

      // We already obtain the base layers to obtain the default base layer of the map to build the Mini-Map
      let baseLayers = layers.layers.filter((layer) =>
        maps.maps[this.mapSelected].layers.find(
          (mapLayer) => mapLayer.name == layer.name && mapLayer.baseLayer
        )
      );

      /*% if (feature.MV_CI_Scale) { %*/
      this.map.addControl(buildMapScaleControl());
      /*% } %*/
      /*% if (feature.MV_CI_CenterCoordinates) { %*/
      this.map.addControl(buildMapCoordinatesControl());
      /*% } %*/
      /*% if (feature.MV_CI_Map) { %*/
      // Minimap control - we get the mini-map base layer from current map default base layer
      const defaultBaseLayer = baseLayers.find((bl) =>
        maps.maps[this.mapSelected].layers.find(
          (l) => l.selected && bl.name === l.name
        )
      );
      this.map.addControl(
        buildMiniMapControl(devCheck.isMobile(), defaultBaseLayer)
      );
      /*% } %*/
      /*% if (feature.MV_T_E_F_URL) { %*/
      if (this.$route.query.token != null) {
        MVExportManagementRepository
          .import(this.$route.query.token)
          .then(data => {
            this.map.importState(data);
            this.refreshLayerManager();
          });
      }
      /*% } %*/
      /*% if (feature.MV_T_MeasureControl) { %*/
      this.map.addControl(buildMeasureControl());
      /*% } %*/
      /*% if (feature.MV_T_ZoomWindow) { %*/
      if (!devCheck.isMobile()) {
        this.map.addControl(buildZoomBoxControl());
      }
      /*% } %*/
      /*% if (feature.MV_T_UserGeolocation) { %*/
      this.map.addControl(buildLocateControl());
      /*% } %*/

      /*% if (feature.DM_A_C_RC_pgRouting) { %*/
      this.map.addControl(buildPgroutingControl());
      /*% } %*/

      /*% if (feature.DM_A_C_RC_GraphHopper) { %*/
      this.map.addControl(buildGraphhopperControl());
      /*% } %*/
      /* Private functions */
      /*% if (feature.MV_CI_Scale) { %*/
      /**
       * Builds Scale control
       **/
      function buildMapScaleControl() {
        return new L.control.scale();
      }
      /*% } %*/
      /*% if (feature.MV_CI_CenterCoordinates) { %*/
      /**
       * Builds coordinates control
       * https://github.com/xguaita/Leaflet.MapCenterCoord
       */
      function buildMapCoordinatesControl() {
        var mapCenterOptions = {
          icon: false,
          onMove: true,
          latlngFormat: "DMS",
          latlngDesignators: true
        };
        // Show map coordinates
        return new L.control.mapCenterCoord(mapCenterOptions);
      }
      /*% } %*/
      /*% if (feature.MV_CI_Map) { %*/
      /**
       * Builds MiniMap control
       * https://github.com/Norkart/Leaflet-MiniMap
       */
      function buildMiniMapControl(isMobile, defaultBaseLayer) {
        var miniMapOptions = {
          position: "bottomright",
          width: isMobile ? 75 : 125,
          height: isMobile ? 75 : 125,
          toggleDisplay: true,
          zoomLevelOffset: -5,
          zoomLevelFixed: false,
          zoomAnimation: false,
          autoToggleDisplay: true,
          aimingRectOptions: {
            color: "#ff7800",
            weight: 1,
            clickable: false,
          },
          shadowRectOptions: {
            color: "#000000",
            weight: 1,
            clickable: false,
            opacity: 0,
            fillOpacity: 0,
          },
        };
        let miniMapLayer;
        if (defaultBaseLayer) {
          miniMapLayer = new L.TileLayer(defaultBaseLayer.url, {
            minZoom: 0,
            maxZoom: 13,
          });
        } else {
          miniMapLayer = new L.TileLayer(
            "http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
            {
              minZoom: 0,
              maxZoom: 13,
            }
          );
        }

        return new L.Control.MiniMap(miniMapLayer, miniMapOptions);
      }
      /*% } %*/
      /*% if (feature.MV_T_MeasureControl) { %*/
      /**
       * Builds the measure tool control
       */
      function buildMeasureControl() {
        const measureOptions = {
          position: "topleft",
          primaryLengthUnit: "kilometers",
          secondaryLengthUnit: undefined,
          primaryAreaUnit: "sqkilometers",
          activeColor: "#CC3E33",
          completedColor: "#33CC3E",
          units: {
            sqkilometers: {
              factor: 0.000001,
              display: "km2",
              decimals: 2
            }
          },
          decPoint: ",",
          thousandsSep: "."
        };
        return L.control.measure(measureOptions);
      }
      /*% } %*/
      /*% if (feature.MV_T_ZoomWindow) { %*/
      /**
       * Builds the zoombox control
       * http://angular-ui.github.io/ui-leaflet/examples/0000-viewer.html#/controls/minimap-example
       * https://github.com/consbio/Leaflet.ZoomBox
       */
      function buildZoomBoxControl() {
        return L.control.zoomBox({
          modal: false,
          position: "topleft",
          title: "Zoom Box Control"
        });
      }
      /*% } %*/
      /*% if (feature.MV_T_UserGeolocation) { %*/
      /** Builds the locate control
       * https://github.com/domoritz/leaflet-locatecontrol
       */
      function buildLocateControl() {
        const locateControlOptions = {
          strings: {
            title: "User location"
          },
          iconElementTag: "a",
          icon: "user-location-icon"
        };
        return L.control.locate(locateControlOptions);
      }
      /*% } %*/

      // Loading base layers
      let titleLayers = [];
      baseLayers
        .sort((a, b) => {
          return a.name - b.name;
        })
        .map(json => {
          const baseLayerInMap = maps.maps[this.mapSelected].layers.find(
            mapLayer => mapLayer.name === json.name
          );
          return {
            id: json.name,
            label: this.$t(
              "mapViewer.layer-label." + json.name.replace(".", "-")
            ),
            type: json.layerType,
            baseLayer: true,
            opacity: baseLayerInMap.opacity,
            selected: baseLayerInMap.selected === true,
            url: json.url || properties.GEOSERVER_URL + "/wms",
            params: json.options
          };
        })
        .forEach(layerParams => {
          let layer = null;
          if (layerParams.type === "tilelayer") {
            layer = new TileLayer(layerParams);
            this.map.addLayer(layer);
          } else if (layerParams.type === "wms") {
            layer = new WMSLayer(layerParams);
            this.map.addLayer(layer);
          }
          titleLayers.push(layer);
        });
      this.baseLayers = titleLayers;

      // Loading overlays
      const layersToShow = layers.layers.filter(l =>
        maps.maps[this.mapSelected].layers.find(
          mapLayer => !mapLayer.baseLayer && mapLayer.name === l.name
        )
      );

      layersToShow
        .sort((a, b) => {
          const layerA = maps.maps[this.mapSelected].layers.find(
              (mapLayer) => mapLayer.name === a.name
            ),
            layerB = maps.maps[this.mapSelected].layers.find(
              (mapLayer) => mapLayer.name === b.name
            );
          return (
            (layerA.order != null ? layerA.order : 100) -
            (layerB.order != null ? layerB.order : 100)
          );
        })
        .forEach(json => {
          const layerInMap = maps.maps[this.mapSelected].layers.find(
            mapLayer => mapLayer.name === json.name
          );
          const label = this.$t(
            "mapViewer.layer-label." + json.name.replace(".", "-")
          );
          let availableStyles, defaultStyle;
          // Tilelayers hasn't got styles
          if (json.layerType !== "tilelayer") {
            // Get all available styles
            availableStyles = json.availableStyles?.map(availableStyleName =>
              getStyle(availableStyleName)
            );
            // Get default style
            defaultStyle =
              layerInMap.style != null
                ? availableStyles.find(style => style.id === layerInMap.style)
                : json.defaultStyle != null
                ? availableStyles.find(style => style.id === json.defaultStyle)
                : null;
          }
          if (json.layerType === "wms") {
            const options =
              /*% if (feature.MV_T_F_BasicSearch) { %*/
              (this.form.query == null || this.form.query === "")
                ? json.options
                : Object.assign(
                {
                  cql_filter:
                    "in (" +
                    this.form.query +
                    ")"
                },
                json.options
                );
              /*% } else { %*/
            json.options;
              /*% } %*/

            this.map.addLayer(
              new WMSLayer(
                {
                  id: json.name,
                  label: label,
                  baseLayer: false,
                  selected: layerInMap.selected || layerInMap.selected == null,  // if no value is given, it is shown in map
                  /*% if (feature.MV_T_ViewMapAsList) { %*/
                  list: layerInMap.list || json.list,
                  /*% } %*/
                  url: json.url/*% if (feature.MV_MS_GeoServer) { %*/|| properties.GEOSERVER_URL + "/wms"/*% } %*/,
                  params: options
                },
                availableStyles,
                defaultStyle
              )
            );
          } else if (json.layerType === "geojson") {
            /*% if (!feature.MV_MS_GJ_Paginated) { %*/
            const repository = RepositoryFactory.get(
              this._getRepositoryNameFromJSON(json)
            );
            /*% } %*/
            /*% if (feature.MV_DetailOnClick) { %*/
            const form = layerInMap.form || json.form;
            /*% } %*/

            /*% if (feature.MV_MS_GJ_Cached) { %*/
            const repositoryName = this._getRepositoryNameFromJSON(json);
            const fileName = repositoryName.includes("Entity")
                ? this._getEntityNameFromJSON(json)
                : this._getPropertyNameFromJSON(json);
            /*% } %*/

            this.map.addLayer(
              new GeoJSONLayer(
                /*% if (feature.MV_MS_GJ_Paginated) { %*/
                {
                  type: "FeatureCollection",
                  features: [],
                },
                /*% } else { %*/
                repository.getGeom(
                  /*% if (feature.MV_MS_GJ_Cached) { %*/fileName, /*% } else { %*/this._getPropertyNameFromJSON(json), /*% } %*/ {
                  params: {
                    properties: true,
                    /*% if (feature.MV_T_F_BasicSearch) { %*/
                    search: this.form.query
                    /*% } %*/
                  }
                }),
                /*% } %*/
                {
                  id: json.name,
                  label: label,
                  baseLayer: false,
                  selected: layerInMap.selected || layerInMap.selected == null,  // if no value is given, it is shown in map
                  /*% if (feature.MV_Clustering) { %*/
                  clustering: true,
                  /*% } %*/
                  /*% if (feature.MV_T_ViewMapAsList) { %*/
                  list: layerInMap.list || json.list,
                  /*% } %*/
                  /*% if (feature.MV_DetailOnClick) { %*/
                  popup: this.geoJSONPopupFunction(form)
                  /*% } %*/
                },
                availableStyles,
                defaultStyle
              )
            );
          } else if (json.layerType === "tilelayer") {
            this.map.addLayer(
              new TileLayer({
                id: json.name,
                label: label,
                baseLayer: false,
                opacity: layerInMap.opacity,
                selected: layerInMap.selected || layerInMap.selected == null,  // if no value is given, it is shown in map
                url: json.url,
                params: json.options,
              })
            );
          }
        });

      /*% if (feature.DM_A_C_RouteCalculation) { %*/
      /**
       * Builds route calculation control
       */

      /*% if (feature.DM_A_C_RC_pgRouting) { %*/
      /**
       * Retrieves data using pgRouting queries
       */
      function buildPgroutingControl() {
        return new L.easyButton({
          tittle: "pgrbtn",
          id: "calcRouteButton",
          position: "bottomleft",
          states: [
            {
              stateName: "normal",
              icon:
                '<span>pgRouting</span>',
              onClick: function(btn, map) {
                settingOrigin(btn, map);
              }
            },
            {
              stateName: "origin",
              icon: "<span>Set origin</span>",
              onClick: function(btn, map) {
                cancelRouteCalculation(btn, map);
              }
            },
            {
              stateName: "destiny",
              icon: "<span>Set destiny</span>",
              onClick: function(btn, map) {
                cancelRouteCalculation(btn, map);
              }
            }
          ]
        });
      }

      const calculatePgrRoute = (btn, map) => {
        btn.state("normal");
        var from = this.routeToCalculate.origin.getLatLng()
        var to = this.routeToCalculate.destiny.getLatLng()

        MVRouteCalculationRepository.getPgrRoute({
          params: {
            fromLat: from.lat,
            fromLng: from.lng,
            toLat: to.lat,
            toLng: to.lng
          }
        })
          .then(data => {
            this.calculating = false;
            var routeLayer = new L.featureGroup();
            var opacity = 80;
            var color = {
              name: "blue",
              value: "#106CB6"
            };

            data.forEach(json => {
              new L.geoJson(json.geom_way, {
                style: function() {
                  return getStyleForRoute(color, opacity);
                },
                pointToLayer: (geoJsonPoint, latlng) => {
                  return getPointToLayer(latlng, color, opacity);
                }
              }).addTo(routeLayer);
          })
          addOverlay(color, opacity, routeLayer);
          routeLayer.addTo(map);
        })
        .catch(() => cancelRouteCalculation(btn, map));
      };
      /*% } %*/

      /*% if (feature.DM_A_C_RC_GraphHopper) { %*/
      /**
       * Retrieves data from GraphHopper server
       */
      function buildGraphhopperControl() {
        return new L.easyButton({
          tittle: "ghbtn",
          id: "calcRouteButton",
          position: "bottomleft",
          states: [
            {
              stateName: "normal",
              icon:
                '<span>GraphHopper</span>',
              onClick: function(btn, map) {
                settingOrigin(btn, map);
              }
            },
            {
              stateName: "origin",
              icon: "<span>Set origin</span>",
              onClick: function(btn, map) {
                cancelRouteCalculation(btn, map);
              }
            },
            {
              stateName: "destiny",
              icon: "<span>Set destiny</span>",
              onClick: function(btn, map) {
                cancelRouteCalculation(btn, map);
              }
            }
          ]
        });
      }

      const calculateGhRoute = (btn, map) => {
        btn.state("normal");
        var from = this.routeToCalculate.origin.getLatLng()
        var to = this.routeToCalculate.destiny.getLatLng()

        MVRouteCalculationRepository.getGhRoute(
          querystring.stringify({
            point: [from.lat + "," + from.lng, to.lat + "," + to.lng],
            vehicle: "car.json",
            points_encoded: false,
          })

        )
          .then(data => {
            this.calculating = false;
            var routeLayer = new L.featureGroup();
            var opacity = 80;
            var color = {
              name: "green",
              value: "#008F3F"
            };

            new L.geoJson(data.paths[0].points, {
              style: function() {
                return getStyleForRoute(color, opacity);
              },
              pointToLayer: (geoJsonPoint, latlng) => {
                return getPointToLayer(latlng, color, opacity);
              }
            }).addTo(routeLayer);

            addOverlay(color, opacity, routeLayer);

            routeLayer.addTo(map);
          })
          .catch(() => cancelRouteCalculation(btn, map));
      };
      /*% } %*/

      const settingOrigin = (btn, map) => {
        if (!this.calculating) {
          btn.state("origin");
          this.calculating = true;
          removeCurrentRoute(btn, map);
          this.routeToCalculate = {
            origin: null,
            destiny: null
          };

          map.on("click", e => {
            this.routeToCalculate.origin = L.marker(e.latlng);
            this.routeToCalculate.origin.addTo(map);
            map.off("click");
            settingDestiny(btn, map);
          });
        } else
          this.$notify({
            text: this.$t("mapViewer.routeCompletationError"),
            type: "error"
          });
      };

      const settingDestiny = (btn, map) => {
        btn.state("destiny");
        map.on("click", e => {
          this.routeToCalculate.destiny = L.marker(e.latlng);
          this.routeToCalculate.destiny.addTo(map);
          map.off("click");

          /*% if (feature.DM_A_C_RC_GraphHopper && feature.DM_A_C_RC_pgRouting) { %*/
          if (btn.options.tittle === "ghbtn") {
            calculateGhRoute(btn, map);
          } else calculatePgrRoute(btn, map);
          /*% } else if (feature.DM_A_C_RC_pgRouting) { %*/
          calculatePgrRoute(btn, map);
          /*% } else if (feature.DM_A_C_RC_GraphHopper) { %*/
          calculateGhRoute(btn, map);
          /*% } %*/
        });
      };

      function getPointToLayer(latlng, color, opacity) {
        return new L.circleMarker(latlng, {
          radius: 8,
          fillColor: color.value,
          color: color.value,
          weight: 1,
          opacity: 1,
          fillOpacity: opacity / 100
        });
      }

      function getStyleForRoute(color, opacity) {
        return {
          opacity: opacity / 100,
          fillOpacity: opacity / 100,
          color: color.value,
          fillColor: color.value
        };
      }

      const addOverlay = (color, opacity, routeLayer) => {
        this.overlays.push({
          id: "calculated-route",
          name: "mapviewer.route",
          color: color,
          opacity: opacity,
          layer: routeLayer,
          order: this.overlays.length
        });

      }

      const removeCurrentRoute = (btn, map) => {
        if (this.routeToCalculate) {
          if (this.routeToCalculate.origin) {
            map.removeLayer(this.routeToCalculate.origin);
          }
          if (this.routeToCalculate.destiny) {
            map.removeLayer(this.routeToCalculate.destiny);
          }
        }

        var old = this.overlays.find(l => {
          return l.id === "calculated-route";
        });
        if (old) {
          this.overlays.splice(this.overlays.indexOf(old), 1);
          map.removeLayer(old.layer);
        }
      };

      const cancelRouteCalculation = (btn, map) => {
        btn.state("normal");
        map.off("click");
        this.calculating = false;
        removeCurrentRoute(btn, map);
        this.routeToCalculate = null;
      };
      /*% } %*/
      /*% if (feature.GUI_L_ViewListAsMap || feature.GUI_L_LocateInMap) { %*/
      // hiding layers not selected (by url params)
      if (this.$route.query.layers) {
        // ?layers=layerA&layers=layerB <---- array
        // ?layers=layerA,layerB <---- string
        let visibleLayers = Array.isArray(this.$route.query.layers)
          ? this.$route.query.layers
          : this.$route.query.layers.split(",");

        this.map.getOverlays().forEach(layer => {
          if (!visibleLayers.includes(layer.getId())) {
            layer.hide();
          }
        });
      }
      /*% } %*/

      // focusing visible layers if focus (by url params)
      if (this.$route.query.focus) {
        this.map.centerView("visible");
      }

      /*% if (feature.GUI_L_LocateInMap) { %*/
      // highlight item
      let layerPreffix, targetId;
      if (this.$route.query.item) {
        let item = this.$route.query.item;
        layerPreffix = item.substring(0, item.lastIndexOf("-") + 1);
        targetId = item.substring(item.lastIndexOf("-") + 1, item.length);
      }
      this.map
        .getOverlays()
        .filter(layer => {
          return layer.getId().startsWith(layerPreffix);
        })
        .forEach(layer => {
          targetId = parseInt(targetId);
          layer.highlight(targetId);
        });
      /*% } %*/

      if (localStorage.getItem("state")) {
        this.map.importState(JSON.parse(localStorage.getItem("state")));
        localStorage.removeItem("state");
      }
    },
    /*% if (feature.MV_DetailOnClick) { %*/
    geoJSONPopupFunction(form) {
      return (layer) => {
        this.popupForm = form;
        this.popupLayer = layer;
        return this.$refs.informationPopup.$el;
      };
    },
    /*% } %*/
    /*% if (feature.MV_T_F_BasicSearch) { %*/
    searchInMap() {
      if (this.form.query !== this.$route.query.query) {
        localStorage.setItem("state", JSON.stringify(this.map.exportState()));
        this.changeRoute()
      }
    },
    /*% } %*/
    /*% if (feature.MV_T_InformationMode) { %*/
    wmsCallback(features) {
      let filteredFeatures =
        /*% if (feature.MV_T_F_BasicSearch) { %*/
        (this.form.query != null && this.form.query !== "")
          ? features.filter(f => f.id.endsWith(this.form.query))
          : /*% } %*/ features;
      if (filteredFeatures.length > 0) {
        this.wmsFeatures = filteredFeatures;
        this.openDialog("wms-information");
      }
    },
    /*% } %*/
    _getRepositoryNameFromJSON(json) {
      let entityName = this._getEntityNameFromJSON(json);
      let repositorySuffix = "EntityRepository"
      // Check if component entity
      if (entityName.indexOf("-") != -1) {
        repositorySuffix = "Repository";
        entityName = entityName.replace("-", "");
      }
      return (
        entityName.charAt(0).toUpperCase() +
        entityName.slice(1) +
        repositorySuffix
      );
    },
    _getEntityNameFromJSON(json) {
      const prop = json.entityName != null ? "entityName" : "name";
      const nameParts = json[prop].split("-");
      if (nameParts.length > 2) {
        return nameParts[0] + "-" + nameParts[1].charAt(0).toUpperCase() + nameParts[1].slice(1);
      }
      return nameParts[0];
    },
    _getPropertyNameFromJSON(json) {
      // returns last substring after a '-'
      const prop = json.entityName != null ? "entityName" : "name";
      const nameParts = json[prop].split("-");
      return nameParts[nameParts.length - 1];
    },
    refreshLayerManager() {
      this.loadingMap = true;
      setTimeout(() => {
        this.loadingMap = false;
      }, 0);
    },
    /*% if (feature.MV_MS_GJ_Paginated) { %*/
    updateLayer(bbox) {
      const layersToShow = layers.layers.filter(
        (layer) => layer.layerType === "geojson" && this.map.getLayer(layer.name)
      );

      const augmentedBBox = incrementBBox(
        bbox.getWest(),
        bbox.getEast(),
        bbox.getSouth(),
        bbox.getNorth()
      );

      const options = { params: {} };
      options.params.xmin = augmentedBBox.xmin;
      options.params.xmax = augmentedBBox.xmax;
      options.params.ymin = augmentedBBox.ymin;
      options.params.ymax = augmentedBBox.ymax;

      layersToShow.forEach((json) => {
        const repository = RepositoryFactory.get(
          this._getRepositoryNameFromJSON(json)
        );

        repository
          .getGeom(this._getPropertyNameFromJSON(json), options)
          .then((data) => {
            if (data.features.length !== 0) {
              let layer = this.map.getLayer(json.name);
              layer
                /*% if (feature.MV_Clustering) { %*/
                ._getRealLayer()
                /*% } else { %*/
                .getLayer()
                /*% } %*/
                .then((layr) => {
                  /*% if (feature.MV_Clustering) { %*/
                  layer.getLayer().then((cluster) => {
                    cluster.clearLayers();
                    cluster.addLayer(this.updateLayerData(layr, data));
                  });
                  /*% } else { %*/
                  this.updateLayerData(layr, data);
                  /*% } %*/
                /*% if (feature.MV_DetailOnClick) { %*/
                })
                .finally(() => {
                  layer.bindPopup(this.geoJSONPopupFunction(json.form));
                /*% } %*/
                });
            }
          });
      });
    },
    updateLayerData(layer, data) {
      if (Object.keys(layer._layers).length === 0) {
        layer.addData(data);
      } else {
        const dataObj = {};
        data.features.forEach((item) => (dataObj[item.id] = item));
        layer.eachLayer((subLayer) => {
          const found = dataObj[subLayer.feature.id];
          if (found) {
            delete dataObj[subLayer.feature.id];
          } else {
            layer.removeLayer(subLayer);
          }
        });

        const newFeatures = Object.values(dataObj);
        layer.addData(newFeatures);
      }
      /*% if (feature.MV_Clustering) { %*/
      return layer;
      /*% } %*/
    },
    /*% } %*/
  }
};
</script>

<style lang='css' scoped>
#map-container {
  height: 100%;
}

#map {
  width: 100%;
  /*% if (feature.MV_T_F_BasicSearch || feature.MV_T_Export) { %*/
  height: calc(100% - 60px);
  /*% } else { %*/
  height: 100%;
  /*% } %*/
  z-index: 1;
}

::v-deep .leaflet-top.leaflet-right {
  margin-top: 10px !important;
}

::v-deep .leaflet-top.leaflet-right .leaflet-control {
  margin-top: 0 !important;
}

::v-deep .leaflet-layer-manager-toggle {
  width: 30px !important;
  height: 30px !important;
}

::v-deep .leaflet-layer-manager-overlays {
  display: inline-grid;
}

::v-deep .leaflet-add-layer-control-icon {
  width: 30px !important;
  height: 30px !important;
}

::v-deep .leaflet-layer-manager-selector {
  vertical-align: text-top;
}

::v-deep .leaflet-layer-manager-style-selector {
  float: right;
  margin-right: 5px;
  margin-top: 3px;
}

::v-deep .leaflet-layer-manager-icon {
  max-height: 16px;
  position: relative;
  top: 4px;
}

::v-deep .leaflet-layer-manager-icon-square {
  display: inline-block;
  height: 13px;
  width: 16px;
  border: 2px solid white;
  border-right-width: 3px;
  border-bottom-width: 0px;
  margin-bottom: -1px;
  margin-top: 4px;
}

::v-deep .leaflet-layer-manager-button {
  float: right;
  margin-right: 5px;
  margin-top: 3px;
  background-repeat: no-repeat;
  background-size: 15px 15px;
  background-position: center;
  height: 16px;
  width: 10px;
}

/*% if (feature.MV_T_InformationMode) { %*/
::v-deep .disabled {
  color: red !important;
}
::v-deep .enabled {
  color: green !important;
}
/*% } %*/
/*% if (feature.DM_A_C_RouteCalculation) { %*/
::v-deep #calcRouteButton{
  width: 100px;
}
/*% } %*/
/*% if (feature.MV_T_UserGeolocation) { %*/
::v-deep .user-location-icon {
  background: url("../../assets/user-location-icon.png");
  background-repeat: inherit;
  background-size: 100%;
}
/*% } %*/
</style>
/*% } %*/
