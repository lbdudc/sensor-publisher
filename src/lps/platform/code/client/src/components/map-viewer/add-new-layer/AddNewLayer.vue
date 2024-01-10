/*% if (feature.MV_LM_ExternalLayer) { %*/
<template>
  <v-card class="card">
    <v-card-title primary-title class="headline card-title white--text">
      <v-container class="pa-0 ma-0">
        <v-row no-gutters>
          <v-col class="ma-0">
            {{ $t("addNewLayer.addNewLayerTitle") }}
          </v-col>
          <v-col cols="1">
            <v-btn @click="close" color="white" icon>
              <v-icon>mdi-close</v-icon>
            </v-btn>
          </v-col>
        </v-row>
      </v-container>
    </v-card-title>
    <v-card-text>
      <v-container class="ma-0 pa-0">
        <v-row no-gutters>
          <v-radio-group v-model="radios" row mandatory>
            <v-radio
              class="radio"
              name="radio2"
              :label="$t('addNewLayer.addExistentLayer')"
              value="1"
              @click="
                () => ((this.newLayer = null), (this.selectedUrlLayer = null))
              "
            ></v-radio>

            <v-radio
              class="radio"
              name="radio1"
              :label="$t('addNewLayer.addUrlLayer')"
              value="0"
              @click="() => ((this.newLayer = null), (this.layerName = null))"
            ></v-radio>
          </v-radio-group>
        </v-row>

        <v-divider class="mb-4"></v-divider>
        <v-row no-gutters>
          <v-col cols="12">
            <v-form ref="form" v-model="isValid" class="formData">
              <v-row align="center" no-gutters v-if="radios == '0'">
                <v-col class="text-left mt-6" cols="12" md="8">
                  <v-text-field
                    @click:clear="() => ((isSearch = false), (newLayer = null))"
                    @input="() => (isSearch = false)"
                    clearable
                    outlined
                    dense
                    :rules="[(v) => !!v || $t('addNewLayer.urlRequired')]"
                    :label="$t('addNewLayer.url')"
                    v-model="url"
                  ></v-text-field>
                </v-col>
                <v-col
                  cols="12"
                  md="3"
                  offset-md="1"
                  class="text-left"
                  v-if="radios == '0' && !isSearch"
                >
                  <v-btn
                    color="#6caac6"
                    class="white--text"
                    @click="searchLayers"
                  >
                    <v-icon class="mr-1">mdi-magnify</v-icon>
                    {{ $t("addNewLayer.search") }}
                  </v-btn>
                </v-col>
              </v-row>
              <v-row no-gutters align="center" v-if="radios == '0'">
                <v-col cols="8">
                  <v-select
                    v-if="urlLayers != null && isSearch"
                    v-model="selectedUrlLayer"
                    outlined
                    dense
                    :items="urlLayers"
                    item-title="layerName"
                    item-value="layerName"
                    :label="$t('addNewLayer.selectLayer')"
                    :menu-props="{ offsetY: true }"
                    :rules="[(v) => !!v || $t('addNewLayer.layerRequired')]"
                    @change="createLayer"
                  >
                    <template v-slot:item="{ item }">
                      {{ item.layerName }}
                    </template>
                    <template v-slot:selection="{ item }">
                      {{ item.layerName }}
                    </template>
                  </v-select>
                </v-col>
                <v-col
                  cols="12"
                  md="3"
                  offset-md="1"
                  v-if="urlLayers != null && isSearch"
                >
                  <v-btn class="appButton" @click="addLayer"
                  ><v-icon class="mr-2"> mdi-plus </v-icon>
                    {{ $t("addNewLayer.add") }}
                  </v-btn>
                </v-col>
              </v-row>
              <v-row align="center" no-gutters v-if="radios == '1'">
                <v-col class="text-left mt-6">
                  <v-autocomplete
                    v-model="layerName"
                    outlined
                    dense
                    :items="items"
                    item-text="name"
                    item-value="id"
                    :label="$t('addNewLayer.selectLayer')"
                    :menu-props="{ offsetY: true }"
                    :rules="[(v) => !!v || $t('addNewLayer.layerRequired')]"
                    @change="createLayer"
                  >
                    <template v-slot:item="{ item }">
                      {{ $t("mapViewer.layer-label." + item.id) }}
                    </template>
                    <template v-slot:selection="{ item }">
                      {{ $t("mapViewer.layer-label." + item.id) }}
                    </template>
                  </v-autocomplete>
                </v-col>
                <v-col cols="12" md="3" offset-md="1">
                  <v-btn class="appButton" @click="addLayer"
                  ><v-icon class="mr-2"> mdi-plus </v-icon>
                    {{ $t("addNewLayer.add") }}
                  </v-btn>
                </v-col>
              </v-row>
            </v-form>
          </v-col>
        </v-row>

        <v-row no-gutters>
          <v-col cols="12">
            <map-preview
              v-show="newLayer != null"
              id="extension"
              height="250px"
              :entity="{ sampleGeom: null }"
              propName="extension"
              :newLayer="newLayer"
            ></map-preview>
          </v-col>
        </v-row>
      </v-container>
    </v-card-text>
  </v-card>
</template>

<script>
import layerList from "@/components/map-viewer/config-files/layers.json";
import { getStyle } from "@/common/map-styles-common";
import properties from "@/properties";
import RepositoryFactory from "@/repositories/RepositoryFactory";
import MapPreview from "@/components/map-viewer/add-new-layer/MapPreview.vue";

export default {
  name: "addNewLayerControl",
  props: ["map"],
  components: {
    MapPreview,
  },
  data() {
    return {
      items: [],
      radios: null,
      isValid: false,
      layerName: null,
      url: null,
      urlLayers: null,
      selectedUrlLayer: null,
      isSearch: false,
      newLayer: null,
    };
  },
  mounted() {
    layerList.layers.forEach((layer) => {
      if (layer.layerType !== "tilelayer" && !this.map.getLayer(layer.name)) {
        this.items.push({
          id: layer.name,
          name: this.$t("mapViewer.layer-label." + layer.name),
        });
      }
    });

    // Sorting layers based on i18n
    this.items.sort((a, b) => {
      const renamedA = this.$t("mapViewer.layer-label." + a.id);
      const renamedB = this.$t("mapViewer.layer-label." + b.id);
      return renamedA.localeCompare(renamedB);
    });
  },
  methods: {
    close() {
      this.$emit("close");
      this.$destroy();
    },
    createLayer() {
      // Return error if no layer is selected
      if (this.layerName == null && this.selectedUrlLayer == null) {
        this.$refs.form.validate();
        return;
      }

      if (this.layerName != null && this.radios === "1") {
        const layer = layerList.layers.find(
          (layer) => layer.name === this.layerName
        );

        const label = this.$t("mapViewer.layer-label." + layer.name);
        let availableStyles, defaultStyle;

        // Get all available styles
        availableStyles = layer.availableStyles?.map((availableStyleName) =>
          getStyle(availableStyleName)
        );

        // Get default style
        defaultStyle = availableStyles
          ? availableStyles.find((style) => style._id === layer.defaultStyle)
          : null;

        if (layer.layerType === "wms") {
          this.map.removeLayer(layer.name);
          this.newLayer = new MV.WMSLayer(
            {
              id: layer.name,
              label: label,
              baseLayer: false,
              selected: true,
              added: true,
              list: layer.list,
              url: layer.url || properties.GEOSERVER_URL,
              params: layer.options,
            },
            availableStyles,
            defaultStyle
          );
        } else if (layer.layerType === "geojson") {
          const repository = RepositoryFactory.get(
            this._getRepositoryNameFromJSON(layer)
          );
          const form = layer.form;
          /*% if (feature.MV_MS_GJ_Paginated) { %*/
          const options = { params: {} };
          var bbox = this.map.getLeafletMap().getBounds();
          options.params.xmin = bbox.getWest();
          options.params.xmax = bbox.getEast();
          options.params.ymin = bbox.getSouth();
          options.params.ymax = bbox.getNorth();
          /*% } %*/
          /*% if (feature.MV_MS_GJ_Cached) { %*/
          const repositoryName = this._getRepositoryNameFromJSON(layer);
          const fileName = repositoryName.includes("Entity")
              ? this._getEntityNameFromJSON(layer)
              : this._getPropertyNameFromJSON(layer);
          /*% } %*/

          this.newLayer = new MV.GeoJSONLayer(
            repository
              /*% if (feature.MV_MS_GJ_Cached) { %*/
              .getGeom(fileName, {
                params: {
                  properties: true,
                },
              })
              /*% } else { %*/
              .getGeom(this._getPropertyNameFromJSON(layer), /*% if (feature.MV_MS_GJ_Paginated) { %*/options)/*% } else { %*/{
            params: {
              properties: true,
                /*% if (feature.MV_T_F_BasicSearch) { %*/
                search: form.query,
              /*% } %*/
            },
          })/*% } %*/
          /*% } %*/
              .then((data) => {
                // We can access to forms for geojson elements
                return data.features.map((item) => {
                  return {
                    geometry: item.geometry,
                    type: "Feature",
                    id: item.id,
                    properties: {
                      displayString: item.displayString,
                      ...item.properties,
                    },
                  };
                });
              })
              .then((featureList) => {
                // we need a feature collection, so we build it from the list of features
                return {
                  type: "FeatureCollection",
                  features: featureList,
                };
              }),
            {
              id: layer.name,
              label: label,
              baseLayer: false,
              selected: true,
              added: true,
              clustering: true,
              list: layer.list,
              popup: this.geoJSONPopupFunction(form),
            },
            availableStyles,
            defaultStyle
          );
        }
      } else if (this.selectedUrlLayer != null) {
        this.map.removeLayer(this.selectedUrlLayer);
        this.newLayer = new MV.WMSLayer({
          id: this.selectedUrlLayer,
          label: this.selectedUrlLayer,
          selected: true,
          added: true,
          baseLayer: false,
          url: this.url,
          params: {
            layers: [this.selectedUrlLayer],
            format: "image/png",
            transparent: true,
          },
        });
      }
    },
    addLayer() {
      if (this.newLayer == null) return;

      this.map.addLayer(this.newLayer);
      this.$emit("layer-added", this.newLayer.options.id);
      this.close();
    },
    searchLayers() {
      this.isSearch = true;
      if (this.isValid) {
        try {
          new URL(
            this.url + "?service=WMS&version=1.1.1&request=GetCapabilities"
          );
        } catch (error) {
          this.$notify({
            text: this.$t("addNewLayer.invalidUrl"),
            type: "error",
          });
          return;
        }

        fetch(
          new URL(
            this.url + "?service=WMS&version=1.1.1&request=GetCapabilities"
          )
        )
          .then((response) => {
            return response.text();
          })
          .then((data) => {
            this.urlLayers = [];
            var xmldoc = new DOMParser().parseFromString(
              data,
              "application/xml"
            );
            var layerTitles = [];
            var layerNames = [];

            Array.from(
              xmldoc.querySelectorAll("Capability Layer Layer > Name")
            ).forEach((layerName) => {
              layerNames.push(layerName.textContent);
            });
            Array.from(
              xmldoc.querySelectorAll("Capability Layer Layer > Title")
            ).forEach((layerTitle) => {
              layerTitles.push(layerTitle.textContent);
            });

            for (var i = 0; i < layerNames.length; i++) {
              var layer = {
                layerName: layerNames[i],
                layerTitle: layerTitles[i],
              };
              this.urlLayers.push(layer);
            }

            this.urlLayers.sort(function (a, b) {
              if (a.layerName > b.layerName) {
                return 1;
              }
              if (a.layerName < b.layerName) {
                return -1;
              }
              return 0;
            });

            if (this.urlLayers.length === 0) {
              this.isSearch = true;
              this.urlLayers = null;
              this.$notify({
                text: this.$t("addNewLayer.noLayers"),
                type: "error",
              });
            }
          })
          .catch(() => {
            this.$notify({
              text: this.$t("addNewLayer.noLayers"),
              type: "error",
            });
          });
      } else {
        this.$refs.form.validate();
      }
    },
    geoJSONPopupFunction(form) {
      const route = {
        name: this.$route.name,
        query: this.$route.query,
        fullPath: this.$route.fullPath,
        params: this.$route.params,
      };
      return (layer) => {
        localStorage.setItem("backPrevious", JSON.stringify(route));
        return (
          layer.feature.properties.displayString +
          "&nbsp" +
          '<a href="/' +
          this.formIdToUrl(form) +
          "/" +
          layer.feature.id +
          '">' +
          '<i class="material-icons" style="font-size: 15px;">remove_red_eye</i>' +
          "</a>"
        );
      };
    },
    formIdToUrl(form) {
      return form.toLowerCase().replace(" ", "-");
    },
    _getRepositoryNameFromJSON(json) {
      let entityName = this._getEntityNameFromJSON(json);
      let repositorySuffix = "EntityRepository";
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
        return (
          nameParts[0] +
          "-" +
          nameParts[1].charAt(0).toUpperCase() +
          nameParts[1].slice(1)
        );
      }
      return nameParts[0];
    },
    _getPropertyNameFromJSON(json) {
      // returns last substring after a '-'
      const prop = json.entityName != null ? "entityName" : "name";
      const nameParts = json[prop].split("-");
      return nameParts[nameParts.length - 1];
    },
  },
};
</script>

<style scoped>
.card-title {
  background-color: #1976d2;
}
.radio {
  font-size: 0.9em;
}
.formData {
  margin-top: -20px;
  margin-left: 20px;
  margin-right: 20px;
}
</style>
/*% } %*/
