<script setup>
defineEmits(["update:panelSelected"]);

defineProps({
  deploymentStatusPhases: {
    type: Object,
    required: true,
  },
  configurationLogger: {
    type: Array,
    required: true,
  },
  derivationLogger: {
    type: Array,
    required: true,
  },
  deploymentLogger: {
    type: Array,
    required: true,
  },
  passedPhases: {
    type: Array,
    required: true,
  },
  panelSelected: {
    type: Number,
    required: true,
  },
});

const scrollBottom = (id) => {
  const element = document.getElementById(id);
  element.scrollTop = element.scrollHeight;
};
</script>

<template>
  <!-- DEPLOYMENT PANELS -->
  <v-expansion-panels class="mt-8" :model-value="panelSelected">
    <!-- CONFIG PANEL -->
    <v-expansion-panel
      value="0"
      v-if="passedPhases.includes('config')"
      @click="scrollBottom('config')"
    >
      <v-expansion-panel-title
        class="text-white flex flex-row items-center justify-between"
        :class="
          deploymentStatusPhases['config'].status == 'iddle'
            ? 'bg-gray-800'
            : deploymentStatusPhases['config'].status == 'success'
            ? 'bg-green-700/90'
            : 'bg-red-700/90'
        "
      >
        <v-icon class="mr-4">mdi-console-line</v-icon>
        <span class="font-bold text-l">Phase 1: </span>
        <span class="ml-2"> Parsing Configuration</span>
        <v-icon
          v-if="deploymentStatusPhases['config'].status == 'iddle'"
          class="ml-4 text-white"
        >
          mdi-loading mdi-spin
        </v-icon>
        <v-icon
          v-else-if="deploymentStatusPhases['config'].status == 'success'"
          class="ml-4 text-white"
          >mdi-check</v-icon
        >
        <v-icon
          v-else="deploymentStatusPhases['config'].status == 'error'"
          class="ml-4 text-white"
          >mdi-alert-circle-outline</v-icon
        >
      </v-expansion-panel-title>
      <v-expansion-panel-text
        id="config"
        class="bg-gray-800 text-white p-6 rounded-md mx-auto overflow-auto max-h-[60vh] min-h-[60vh] flex flex-col text-left"
      >
        <span v-for="log in configurationLogger" :key="log.id" class="text-sm">
          <span class="font-bold">{{ log.timestamp }}</span>
          <pre v-if="log.isPre">
              {{ log.text }}
              </pre
          >
          <span v-else class="ml-4">
            {{ log.text }}
          </span>
          <br />
        </span>
      </v-expansion-panel-text>
    </v-expansion-panel>

    <!-- DERIVATION PANEL -->
    <v-expansion-panel
      value="1"
      v-if="passedPhases.includes('derivation')"
      @click="scrollBottom('derivation')"
    >
      <v-expansion-panel-title
        class="text-white flex flex-row items-center justify-between"
        :class="
          deploymentStatusPhases['derivation'].status == 'iddle'
            ? 'bg-gray-800'
            : deploymentStatusPhases['derivation'].status == 'success'
            ? 'bg-green-700/90'
            : 'bg-red-700/90'
        "
      >
        <v-icon class="mr-4">mdi-console-line</v-icon>
        <span class="font-bold text-l">Phase 2: </span>
        <span class="ml-2"> Product Creation </span>
        <v-icon
          v-if="deploymentStatusPhases['derivation'].status == 'iddle'"
          class="ml-4 text-white"
        >
          mdi-loading mdi-spin
        </v-icon>
        <v-icon
          v-else-if="deploymentStatusPhases['derivation'].status == 'success'"
          class="ml-4 text-white"
          >mdi-check</v-icon
        >
        <v-icon
          v-else="deploymentStatusPhases['derivation'].status == 'error'"
          class="ml-4 text-white"
          >mdi-alert-circle-outline</v-icon
        >
      </v-expansion-panel-title>
      <v-expansion-panel-text
        id="derivation"
        class="bg-gray-800 text-white p-6 rounded-md mx-auto overflow-auto max-h-[60vh] min-h-[60vh] flex flex-col text-left"
      >
        <span v-for="log in derivationLogger" :key="log.id" class="text-sm">
          <span class="font-bold">{{ log.timestamp }}</span>
          <pre v-if="log.isPre">
              {{ log.text }}
              </pre
          >
          <span v-else class="ml-4">
            {{ log.text }}
          </span>
          <br />
        </span>
      </v-expansion-panel-text>
    </v-expansion-panel>

    <!-- DEPLOYMENT PANEL -->
    <v-expansion-panel
      value="2"
      v-if="passedPhases.includes('deployment')"
      @click="scrollBottom('deployment')"
    >
      <v-expansion-panel-title
        class="text-white flex flex-row items-center justify-between"
        :class="
          deploymentStatusPhases['deployment'].status == 'iddle'
            ? 'bg-gray-800'
            : deploymentStatusPhases['deployment'].status == 'success'
            ? 'bg-green-700/90'
            : 'bg-red-700/90'
        "
      >
        <v-icon class="mr-4">mdi-console-line</v-icon>
        <span class="font-bold text-l">Phase 3: </span>
        <span class="ml-2"> Product Deployment </span>
        <v-icon
          v-if="deploymentStatusPhases['deployment'].status == 'iddle'"
          class="ml-4 text-white"
        >
          mdi-loading mdi-spin
        </v-icon>
        <v-icon
          v-else-if="deploymentStatusPhases['deployment'].status == 'success'"
          class="ml-4 text-white"
          >mdi-check</v-icon
        >
        <v-icon
          v-else="deploymentStatusPhases['deployment'].status == 'error'"
          class="ml-4 text-white"
          >mdi-alert-circle-outline</v-icon
        >
      </v-expansion-panel-title>
      <v-expansion-panel-text
        id="deployment"
        class="bg-gray-800 text-white p-6 rounded-md mx-auto overflow-auto max-h-[60vh] min-h-[60vh] flex flex-col text-left"
      >
        <span v-for="log in deploymentLogger" :key="log.id" class="text-sm">
          <span class="font-bold">{{ log.timestamp }}</span>
          <pre v-if="log.isPre">
              {{ log.text }}
              </pre
          >
          <span v-else class="ml-4">
            {{ log.text }}
          </span>
          <br />
        </span>
      </v-expansion-panel-text>
    </v-expansion-panel>
  </v-expansion-panels>
</template>
