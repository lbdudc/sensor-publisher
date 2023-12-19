<script setup>
import { computed, onMounted, ref } from "vue";
import Feature from "./Feature.vue"; // Replace 'Feature' with your actual Feature component name
import initialFMSelection from "./feature-selection.js";

const emit = defineEmits(["change"]);

const props = defineProps({
  rootFeature: {
    type: Object,
    required: true,
  },
});

onMounted(() => {
  // initialize the root feature
  const initialSelection = initialFMSelection;

  initialSelection.forEach((fName) => {
    const feature = _findFeature(fName);
    if (feature) {
      selectFeature(feature);
    }
  });
});

// get the rootfeature from props.rootFeature
const rootFeature = computed(() => props.rootFeature);
const selection = ref([]);

const updateRootFeature = (newVal) => {
  rootFeature.value = newVal;
};

const selectFeature = (feature) => {
  feature.selected = true;
  selection.value.push(feature);
  _selectsXOR(feature);
  if (feature.features) {
    feature.expanded = true;
  }
  emit("change", selection.value);
};

const deselectFeature = (feature) => {
  feature.selected = false;
  selection.value = selection.value.filter((f) => f !== feature);
  emit("change", selection.value);
};

function _selectsXOR(feature) {
  const parent = _getParent(feature);
  if (parent && parent.type == "XOR") {
    parent.features.filter((f) => f != feature).forEach((f) => _deselects(f));
  }
}

function _deselects(feature, selection = selection) {
  feature.selected = false;
  if (feature.features) {
    feature.features.forEach((f) => _deselects(f));
  }
  _deleteSelection(feature.name, selection);
}

function _deleteSelection(fName, selection = selection) {
  if (selection.indexOf(fName) != -1)
    selection.splice(selection.indexOf(fName), 1);
}

function _getParent(feature) {
  return feature.parent ? _findFeature(feature.parent) : null;
}

function _findFeature(fName, feature = props.rootFeature) {
  if (feature.name == fName) return feature;
  let aux, f;
  if (feature.features) {
    for (f of feature.features) {
      if ((aux = _findFeature(fName, f))) {
        return aux;
      }
    }
  }
  return null;
}
</script>


<template>
  <v-container>
    <Feature
      :feature="rootFeature"
      @change="updateRootFeature"
      @select="selectFeature"
      @deselect="deselectFeature"
    ></Feature>
  </v-container>
</template>


<style scoped>
.error {
  top: 0px;
  left: 25%;
  width: 50%;
  word-wrap: break-word;
  position: fixed;
}
</style>
