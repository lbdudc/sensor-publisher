/*% if (feature.DM_DS_Address) { %*/
<template>
  <div>
    <v-card v-if="address">
      <v-card-text>
        <v-row dense>
          <!-- Address Lines -->
          <v-col cols="12" md="6">
            <label class="font-weight-bold">{{ $t("gc_address.line1") }}</label>
            <div>{{ address.line1 }}</div>
            <label class="font-weight-bold">{{ $t("gc_address.line2") }}</label>
            <div>{{ address.line2 || "-" }}</div>
            <!-- Zipcode -->
            <v-row>
              <v-col cols="6">
                <label class="font-weight-bold">{{
                  $t("gc_address.zipcode")
                }}</label>
                <div>{{ address.zipCode }}</div>
              </v-col>
              <!-- Country -->
              <v-col cols="6">
                <label class="font-weight-bold">{{
                  $t("gc_address.country")
                }}</label>
                <div>{{ countryName }}</div>
              </v-col>
              <!-- Subdivision 1 -->
              <v-col cols="6">
                <label class="font-weight-bold">{{
                  $t("gc_address.subdivision1")
                }}</label>
                <div>{{ subdivision1Name }}</div>
              </v-col>
              <!-- Subdivision 2 -->
              <v-col cols="6">
                <label class="font-weight-bold">{{
                  $t("gc_address.subdivision2")
                }}</label>
                <div>{{ subdivision2Name }}</div>
              </v-col>
              <!-- Subdivision 3 -->
              <v-col cols="6">
                <label class="font-weight-bold">{{
                  $t("gc_address.subdivision3")
                }}</label>
                <div>{{ subdivision3Name }}</div>
              </v-col>
              <!-- Subdivision 4 -->
              <v-col cols="6">
                <label class="font-weight-bold">{{
                  $t("gc_address.subdivision4")
                }}</label>
                <div>{{ subdivision4Name }}</div>
              </v-col>
              <!-- Town -->
              <v-col cols="6">
                <label class="font-weight-bold">{{
                  $t("gc_address.town")
                }}</label>
                <div>{{ townName }}</div>
              </v-col>
            </v-row>
            <v-row>
              <v-col cols="6">
                <label class="font-weight-bold">{{
                  $t("gc_address.latitude")
                }}</label>
                <div v-if="address.point">
                  {{ address.point.coordinates[1] }}
                </div>
                <div v-else>-</div>
              </v-col>
              <v-col cols="6">
                <label class="font-weight-bold">{{
                  $t("gc_address.longitude")
                }}</label>
                <div v-if="address.point">
                  {{ address.point.coordinates[0] }}
                </div>
                <div v-else>-</div>
              </v-col>
            </v-row>
          </v-col>
          <!-- Map -->
          <v-col cols="12" md="6">
            <gc-map
              :id="id"
              :feature="address.point"></gc-map>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>
  </div>
</template>

<script>
import GCMap from "./GCMap";
import RepositoryFactory from "@/repositories/RepositoryFactory";

const GCAddressRepository = RepositoryFactory.get("GCAddressRepository");

export default {
  name: "GCAddressDetail",
  components: {
    "gc-map": GCMap
  },
  props: {
    address: {
      required: true,
    },
    id: {
      type: String,
      required: false,
      default: "address"
    }
  },
  data() {
    return {
      countryName: "-",
      subdivision1Name: "-",
      subdivision2Name: "-",
      subdivision3Name: "-",
      subdivision4Name: "-",
      townName: "-"
    }
  },
  created() {
    if (this.address) {
      GCAddressRepository.getName(this.address.country.id)
      .then(data => (this.countryName = data.name));
      for (let index = 1; index <= 5; index++) {
        if (index === 5) {
          if (this.address.town)
            GCAddressRepository.getName(this.address.town.id)
            .then(data => (this.townName = data.name));
        } else {
          let propName = "subdivision" + index;
          if (this.address[propName])
            GCAddressRepository.getName(this.address[propName].id)
              .then(data => (this[propName + "Name"] = data.name));
          else break;
        }
      }
    }
  }
};
</script>
/*% } %*/
