/*% if (feature.MV_LM_ExternalLayer) { %*/
<template>
  <div>
    <v-container class="ml-md-1 m-0 p-00" :ref="id" :id="id"></v-container>
  </div>
</template>

<script>
import initMap from "@/common/initMap";
import layerList from "@/components/map-viewer/config-files/layers.json";

export default {
  name: "MapPreview",
  props: {
    entity: {},
    propName: {},
    id: {
      required: false,
      type: String,
      default: "map",
    },
    height: {
      required: false,
      type: String,
      default: "250px",
    },
    newLayer: null,
  },
  data() {
    return {
      previewMap: null,
      layerGroup: null,
    };
  },
  mounted() {
    // setting map height
    this.$refs[this.id].style.height = this.height;

    // setting leaflet map
    this.map = initMap(this.id, this.id);
    this.previewMap = this.map.getLeafletMap();
    this.layerGroup = new L.LayerGroup().addTo(this.previewMap);

    // Full screen button
    L.control.fullscreen({ forceSeparateButton: true }).addTo(this.previewMap);
  },
  watch: {
    newLayer() {
      this.layerPreview();
    },
  },
  methods: {
    async layerPreview() {
      if (this.newLayer == null) {
        return;
      }
      // remove previous drawn layers
      this.layerGroup.clearLayers();

      // center map to layer bounds
      this.newLayer.getBounds().then((bounds) => {
        this.previewMap.fitBounds(bounds);
      });

      // added so that bounds are set properly
      setTimeout(() => {
        this.previewMap.invalidateSize();
      }, 0);

      const layr = layerList.layers.find(
        (layer) => layer.name === this.newLayer.options.id
      );

      if (layr === undefined) {
        var url = this.newLayer.options.url;
        var layer = this.newLayer.options.params;

        this.layerGroup.addLayer(
          L.tileLayer.wms(url, {
            layers: layer.layers[0],
            format: layer.format,
            transparent: layer.transparent,
            opacity: this.newLayer.options.opacity,
          })
        );
      } else if (layr.layerType === "wms") {
        var url = this.newLayer.options.url;
        var layer = this.newLayer.options.params;

        this.layerGroup.addLayer(
          L.tileLayer.wms(url, {
            layers: layer.layers[0],
            format: layer.format,
            transparent: layer.transparent,
            opacity: this.newLayer.options.opacity,
            styles:
              this.newLayer.getStyle() != undefined
                ? this.newLayer.getStyle()
                : "",
          })
        );
      } else if (layr.layerType === "geojson") {
        var style = this.newLayer.getStyle().getLeafletStyle();

        this.newLayer._data.then((data) => {
          var feature = data.features[0];

          if (feature != undefined && feature.geometry.type == "Point") {
            this.layerGroup.addLayer(
              L.geoJSON(data, {
                pointToLayer: function (feature, latlng) {
                  return L.circleMarker(latlng, style);
                },
              })
            );
          } else {
            this.layerGroup.addLayer(
              L.geoJSON(data, {
                style: style,
              })
            );
          }
        });
      }
    },
  },
};
</script>

<style scoped>
.container {
  width: auto;
  min-height: 250px;
  z-index: 3;
}
</style>
/*% } %*/
