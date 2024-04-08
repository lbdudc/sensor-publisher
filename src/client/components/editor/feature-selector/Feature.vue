<script setup>
import { ref, computed } from "vue";
// Replace 'Feature' with your actual Feature component name if needed

const props = defineProps({
  feature: {
    type: Object,
    required: true,
  },
});

// define emits for selected and deselected events
const emit = defineEmits(["select", "deselect", "change"]);

const innerFeature = ref(props.feature);

const icon = computed(() => {
  if (!expandable.value) return "";
  return innerFeature.value.expanded ? "mdi-chevron-up" : "mdi-chevron-down";
});

const descriptionIcon = computed(() => {
  if (!description.value) return "";
  return "help";
});

const description = computed(() => {
  return innerFeature.value.name?.desc || null;
});

const label = computed(() => {
  return innerFeature.value.name;
});

const expandable = computed(() => {
  return innerFeature.value.features && innerFeature.value.features.length > 0;
});

const updateSelection = () => {
  if (innerFeature.value.selected) {
    emit("select", innerFeature.value);
  } else {
    emit("deselect", innerFeature.value);
  }
};

const switchExpand = () => {
  if (!expandable.value) return;
  innerFeature.value.expanded = !innerFeature.value.expanded;
  emit("change", innerFeature.value);
};
</script>

<template>
  <div v-if="!innerFeature.hidden" class="text-white">
    <v-container fluid class="pa-0 ma-0">
      <v-row no-gutters align="start" justify="start" class="max-h-10">
        <v-col cols="1">
          <v-btn
            v-if="expandable"
            size="x-small"
            :icon="icon"
            @click="switchExpand"
          ></v-btn>
        </v-col>
        <v-col offset="1" cols="10">
          <v-checkbox
            density="compact"
            v-model="innerFeature.selected"
            :disabled="innerFeature.mandatory"
            @update:modelValue="updateSelection"
            :label="label"
          >
            <template v-slot:append v-if="description">
              <v-icon v-on="on">{{ descriptionIcon }}</v-icon>
            </template>
          </v-checkbox>
        </v-col>
      </v-row>
    </v-container>

    <div
      v-if="innerFeature.features && innerFeature.expanded"
      class="feature-children"
    >
      <Feature
        v-for="c in innerFeature.features"
        :key="c.name"
        :feature="c"
        @change="(newVal) => (c = newVal)"
        @select="$emit('select', $event)"
        @deselect="$emit('deselect', $event)"
      ></Feature>
    </div>
  </div>
</template>

<style scoped>
.clickable {
  cursor: pointer;
}

.description {
  font-size: smaller;
  font-style: italic;
}

.feature-children {
  padding-left: 40px;
}

.mandatory {
  text-decoration: underline;
}

.XOR::after {
  font-style: italic;
  content: "(XOR)";
}

.OR::after {
  font-style: italic;
  content: "(OR)";
}

/* compact style */
.v-input {
  margin: 0;
  padding: 0;
}

.error-feature {
  background-color: #ff5252;
  color: white;
  padding-left: 20px;
  padding-right: 20px;
}
</style>
