<template>
  <div>
    <v-alert :value="isError" type="error" class="mt-3">
      {{ error }}
    </v-alert>
    <v-container>
      <feature
        :feature="rootFeature"
        @change="(newVal) => (rootFeature = newVal)"
      ></feature>
    </v-container>
  </div>
</template>

<script>
import engineStore from "@/services/engineStore";
import Feature from "./Feature";

export default {
  name: "FeatureTree",
  components: { Feature },
  created() {
    engineStore.syncSelection();
  },
  data() {
    return {
      rootFeature: engineStore.rootFeature,
    };
  },
  computed: {
    isError() {
      return engineStore.error != "";
    },
    error() {
      return engineStore.error;
    },
  },
};
</script>

<style lang="scss" scoped>
.error {
  top: 0px;
  left: 25%;
  width: 50%;
  word-wrap: break-word;
  position: fixed;
}
</style>
