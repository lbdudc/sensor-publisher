/*% if (feature.DM_DS_Address) { %*/
<template>
  <v-container class="ml-md-1 m-0 p-00" :ref="id" :id="id"></v-container>
</template>

<script>
import properties from "@/properties";
export default {
  name: "GCMap",
  props: {
    feature: {
      required: false
    },
    geolocation: {
      type: Boolean,
      required: false,
      default: false
    },
    id: {
      required: false,
      type: String,
      default: "map"
    }
  },
  data() {
    return {
      geocoder: null,
      map: null,
      leafletMap: null,
      coordinates: [],
      params: [],
      searched: false,
      mapInteraction: false
    };
  },
  computed: {
    geoJSON() {
      return {
        geometry: {
          type: "Point",
          coordinates: this.coordinates
        },
        type: "Feature",
        id: "address"
      };
    }
  },
  mounted() {
    this.map = new MV.Map(this.id, this.id, [
      [41.508742458803326, -4.087890625],
      [44.508742458803326, -12.087890625]
    ]);
    const baseLayer = new MV.TileLayer({
      id: "Esri.WorldStreetMap",
      baseLayer: true,
      url:
        "https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/{z}/{y}/{x}",
      options: {
        attribution:
          "Tiles &copy; Esri &mdash; Source: Esri, DeLorme, NAVTEQ, USGS, Intermap, iPC, NRCAN, Esri Japan, METI, Esri China (Hong Kong), Esri (Thailand), TomTom, 2012"
      }
    });
    this.map.addLayer(baseLayer);

    const featureLayer = new MV.GeoJSONLayer(
      {
        type: "FeatureCollection",
        features: []
      },
      {
        id: "gc-address",
        label: "gc-address",
        baseLayer: false,
        draggable: this.geolocation,
        dragend: this.geolocation
        ? e => {
            this._updateCoordinates(e.target._latlng);
            this.geolocatePoint(e.target._latlng);
          }
        : undefined
      }
    );
    this.map.addLayer(featureLayer);

    this.leafletMap = this.map.getLeafletMap();

    if (this.geolocation) {
      let self = this;

      this.leafletMap.on("click", function(e) {
        self._updateCoordinates(e.latlng);
        featureLayer.getLayer().then(layer => {
          layer.clearLayers();
          layer.addData(self.geoJSON);
          self.geolocatePoint(e.latlng);
        });
      });

      this.geocoder = L.Control.geocoder({
        geocoder: new L.Control.Geocoder.Google(properties.GOOGLE_API_KEY),
        errorMessage: this.$t("gc_address.geocoder.errorMessage"),
        defaultMarkGeocode: false,
        defaultNoResults: false
      })
        // fired when address is selected (fires automatically if there's only one address)
        .on("markgeocode", function(e) {
          e = e.geocode || e;
          if (self.mapInteraction)
            // emit address searched by point clicked/dragged and that point
            self.$emit("searched", e.properties, {
              lat: self.coordinates[1],
              lng: self.coordinates[0]
            });
          else if (self.searched)
            // emit address searched by words and point of that address
            self.$emit("searched", e.properties, {
              lat: e.center.lat,
              lng: e.center.lng
            });
          else
            // emit point of given address to geolocate
            self.$emit("geolocated", {
              lat: e.center.lat,
              lng: e.center.lng
            });
          self.leafletMap.fitBounds(e.bbox);
          // update map marker when geocode isn't fired by user click/drag on map
          if (!self.mapInteraction) {
            self._updateCoordinates({ lat: e.center.lat, lng: e.center.lng });

            featureLayer.getLayer().then(layer => {
              layer.clearLayers(); // removing previous marker
              layer.addData(self.geoJSON);
            });
          }

          // reinitialize values
          self.searched = false;
          self.mapInteraction = false;

          return this;
        })
        // fired when search locate addresses
        .on("finishgeocode", function(eventData) {
          // search again, with less params, if no results
          if (eventData.results.length == 0) {
            self.params.shift();
            self._geolocate();
          }
        })
        .addTo(this.leafletMap);

      // mark searched flag as true on search enter
      this.geocoder._input.onkeydown = function(e) {
        let keycode = e.keyCode ? e.keyCode : e.which;
        if (keycode == "13") {
          self.searched = true;
        }
      };
    }

    // add feature if exists
    if (this.feature && this.feature.coordinates.length == 2) {
      this.coordinates = JSON.parse(JSON.stringify(this.feature.coordinates));
      featureLayer.getLayer().then(layer => {
        layer.clearLayers(); // removing previous marker
        layer.addData(this.geoJSON);
        this.map.centerView(layer.getBounds().pad(0.2));
      });
    }
  },
  methods: {
    geolocatePoint(latlng) {
      this.mapInteraction = true;
      this.params = [];

      this.params.push(latlng.lat);
      this.params.push(latlng.lng);

      this._geolocate();
    },
    geolocate(address) {
      this.params = [];

      if (address.line1) this.params.push(address.line1);
      if (address.town) this.params.push(address.town.name);
      if (address.subdivision4) this.params.push(address.subdivision4.name);
      if (address.subdivision3) this.params.push(address.subdivision3.name);
      if (address.subdivision2) this.params.push(address.subdivision2.name);
      if (address.subdivision1) this.params.push(address.subdivision1.name);
      if (address.country) this.params.push(address.country.name);

      this._geolocate();
    },
    _geolocate() {
      if (this.params.length > 0) {
        this.geocoder._input.value = this.params.join(", ");
        this.geocoder._geocode();
      }
    },
    _updateCoordinates(latlng) {
      this.coordinates.splice(0);
      this.coordinates.push(latlng.lng);
      this.coordinates.push(latlng.lat);
    }
  }
};
</script>

<style scoped>
.container {
  width: auto;
  height: 100%;
  min-height: 400px;
  z-index: 3;
}
</style>
/*% } %*/
