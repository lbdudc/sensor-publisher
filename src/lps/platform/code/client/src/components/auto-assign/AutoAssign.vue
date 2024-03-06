/*% if (feature.DM_A_G_Batch) { %*/
<template>
  <v-container>
    <v-card>
      <v-card-title>
        <v-row align="center" justify="space-between" no-gutters>
          <v-col cols="6" class="d-none d-md-block">
            <span class="headline no-split-words">
              {{ $t($route.meta.label) }}
            </span>
          </v-col>
        </v-row>
      </v-card-title>
      <v-card-text>
        <v-form v-model="validForm" @submit.prevent="geolocate">
          <v-row dense>
            <v-col cols="12" md="3">
              <v-select
                :items="entities"
                :loading="loadingEntities"
                item-text="displayName"
                return-object
                v-model="selectedEntity"
                :label="$t('autoassign.entity')"
                :rules="[v => !!v || $t('autoassign.field_required')]"
                @change="onEntityChange"
              ></v-select>
            </v-col>
          </v-row>
          <v-row dense v-if="form.entity">
            <v-row dense>
              <v-col cols="12" md="6">
                <v-row dense>
                  <v-col cols="12" class="headline">
                    {{ $t("autoassign.address") }}
                    <v-divider></v-divider>
                  </v-col>
                  <v-col cols="12" md="6">
                    <v-select
                      v-model="form.country"
                      :items="stringProperties"
                      item-text="displayName"
                      item-value="name"
                      :label="$t('gc_address.country')"
                      :rules="[v => !!v || $t('autoassign.field_required')]"
                    ></v-select>
                  </v-col>
                  <v-col cols="12" md="6">
                    <v-select
                      v-model="form.subdivision1"
                      :items="stringProperties"
                      item-text="displayName"
                      item-value="name"
                      :label="$t('gc_address.subdivision1')"
                    ></v-select>
                  </v-col>
                  <v-col cols="12" md="6">
                    <v-select
                    v-model="form.subdivision2"
                      :items="stringProperties"
                      item-text="displayName"
                      item-value="name"
                      :label="$t('gc_address.subdivision2')"
                    ></v-select>
                  </v-col>
                  <v-col cols="12" md="6">
                    <v-select
                      v-model="form.subdivision3"
                      :items="stringProperties"
                      item-text="displayName"
                      item-value="name"
                      :label="$t('gc_address.subdivision3')"
                    ></v-select>
                  </v-col>
                  <v-col cols="12" md="6">
                    <v-select
                      v-model="form.subdivision4"
                      :items="stringProperties"
                      item-text="displayName"
                      item-value="name"
                      :label="$t('gc_address.subdivision4')"
                    ></v-select>
                  </v-col>
                  <v-col cols="12" md="6">
                    <v-select
                      v-model="form.town"
                      :items="stringProperties"
                      item-text="displayName"
                      item-value="name"
                      :label="$t('gc_address.town')"
                    ></v-select>
                  </v-col>
                  <v-col cols="12" md="6">
                    <v-select
                      v-model="form.address"
                      :items="stringProperties"
                      item-text="displayName"
                      item-value="name"
                      :label="$t('autoassign.address')"
                    ></v-select>
                  </v-col>
                </v-row>
              </v-col>
              <v-col cols="12" md="3">
                <v-row dense>
                  <v-col cols="12" class="headline">
                    {{ $t("autoassign.position") }}
                    <v-divider></v-divider>
                  </v-col>
                  <v-col cols="12">
                    <v-select
                      v-model="form.position"
                      :items="pointProperties"
                      item-text="displayName"
                      item-value="name"
                      :label="$t('autoassign.position')"
                      :rules="[v => !!v || $t('autoassign.field_required')]"
                    ></v-select>
                  </v-col>
                </v-row>
              </v-col>
              <v-col cols="12" md="3">
                <v-row dense>
                  <v-col cols="12" class="headline">
                    {{ $t("autoassign.strategy.label") }}
                    <v-divider></v-divider>
                  </v-col>
                  <v-col cols="12" class="font-weight-bold">
                    {{ $t("autoassign.strategy.label") }}
                  </v-col>
                  <v-col cols="12">
                    {{ $t("autoassign.strategy." + form.strategy) }}
                  </v-col>
                  <v-col cols="12">
                    <v-checkbox
                      :label="$t('autoassign.only_not_geolocated')"
                      v-model="form.geolocateOnlyNotLocated"
                    ></v-checkbox>
                  </v-col>
                </v-row>
              </v-col>
            </v-row>
            <v-row dense>
              <v-col cols="12">
                <v-btn
                  type="submit"
                  color="primary"
                  :disabled="!validForm || loading"
                  :loading="loading">
                  {{ $t("autoassign.geolocate")}}
                </v-btn>
              </v-col>
            </v-row>
          </v-row>
        </v-form>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script>
import RespositoryFactory from "@/repositories/RepositoryFactory.js";
import { firstToLowerCase } from "@/common/conversion-utils.js";

const EntitiesInformationRepository = RespositoryFactory.get(
  "EntitiesInformationRepository"
);
const AutoAssignRepository = RespositoryFactory.get("AutoAssignRepository");
const DomainEntityPackage = "es.udc.lbd.gema.lps.model.domain";
const StringType = "java.lang.String";
const PointType = "org.locationtech.jts.geom.Point";
/*% if (feature.DM_DS_Address) { %*/
const GCAddressType =
  "es.udc.lbd.gema.lps.component.geolocation.model.domain.GCAddress";
/*% } %*/
const ValidTypes = [PointType/*% if (feature.DM_DS_Address) { %*/, GCAddressType/*% } %*/];
export default {
  name: "AutoAssign",

  data() {
    return {
      entities: [],
      loading: false,
      loadingEntities: false,
      form: {
        entity: null,
        country: null,
        subdivision1: null,
        subdivision2: null,
        subdivision3: null,
        subdivision4: null,
        town: null,
        address: null,
        position: null,
        /*% if (feature.DM_A_G_Nominatim) { %*/
        strategy: "nominatim",
        /*% } else if (feature.DM_A_G_GoogleMaps) { %*/
        strategy: "googlemaps",
        /*% } %*/
        geolocateOnlyNotLocated: true
      },
      pointProperties: [],
      selectedEntity: null,
      stringProperties: [],
      validForm: false
    };
  },
  created() {
    this.loadingEntities = true;
    EntitiesInformationRepository.getAll()
      .then(data => (this.entities = this._processEntities(data)))
      .finally(() => (this.loadingEntities = false));
  },
  methods: {
    _processEntities(entities) {
      // Remove entities without a Point or a GCAddress property
      // and those which aren't domain related
      let validEntities = entities.filter(
        e =>
          e.name.includes(DomainEntityPackage) &&
          e.properties.filter(p => ValidTypes.includes(p.type)).length > 0
      );

      validEntities.forEach(e => {
        // Remove useless properties
        e.properties = e.properties.filter(
          p => !p.autogenerated && !p.collection
        );

        // Add I18n to entity name
        let nameStartIdx = e.name.lastIndexOf(".") + 1;
        let className = firstToLowerCase(e.name.substring(nameStartIdx));
        e.displayName = this.$t(className + ".name");

        e.properties.forEach(p => {
          p.displayName = this.$t(className + ".prop." + p.name);
        });
        /*% if (feature.DM_DS_Address) { %*/
        // Unnest GCAddress properties
        this._expandAddress(e);
        /*% } %*/
      });

      return validEntities;
    },
    /*% if (feature.DM_DS_Address) { %*/
    _expandAddress(entity) {
      let gcAddressProperties = entity.properties.filter(
        p => p.type == GCAddressType
      );
      if (gcAddressProperties.length > 0) {
        entity.properties = entity.properties.filter(
          p => p.type != GCAddressType
        );
        gcAddressProperties.forEach(p => {
          let fields = [
            "country",
            "subdivision1",
            "subdivision2",
            "subdivision3",
            "subdivision4",
            "town",
            "line1"
          ];
          fields.forEach(field => {
            let newProp = {
              displayName: this.$t("gc_address." + field),
              name: p.name + "." + field,
              type: StringType,
              simpleType: "String"
            };
            entity.properties.push(newProp);
          });

          let point = {
            displayName: p.displayName,
            name: p.name + ".point",
            type: PointType,
            simpleType: "Point"
          };
          entity.properties.push(point);
        });
      }
    },
    /*% } %*/
    onEntityChange() {
      // set entity name
      this.form.entity = this.selectedEntity.name;
      // set choosable properties
      this.stringProperties = this.selectedEntity.properties.filter(
        p => p.type == StringType
      );

      this.pointProperties = this.selectedEntity.properties.filter(
        p => p.type == PointType
      );
    },
    geolocate() {
      this.loading = true;
      AutoAssignRepository.autoassignLocations(this.form)
        .then(data => {
          this.$notify({
            title: this.$t("autoassign.notification.title"),
            text: this.$t("autoassign.notification.text", {
              geolocated: data.geolocated,
              notGeolocated: data.notGeolocated
            }),
            type: "success"
          })
        })
        .finally(() => (this.loading = false));
    }
  }
};
</script>
/*% } %*/
