<script setup>
import { watch } from "vue";

defineEmits(["update:panelSelected"]);

const props = defineProps({
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
    required: false,
  },
  deployDataSel: {
    type: Object,
    required: true,
  },
});

const scrollBottom = (id) => {
  const element = document.getElementById(id);
  if (!element) return;
  element.scrollTop = element.scrollHeight;
};

const logFormatted = (text, timestamp) => {
  // if (log.text.includes('Error') || log.text.includes('#number')) || log.text.includes('Step')) add a break line
  // if text includes a "#" followed by a number, add a break line, replace all # with \n#
  let finalText = "";

  // replace all the # with \n#, all the "Step" with \nStep, and all the "Error" with \nError
  finalText = text.replace(/#/g, "\n#");
  finalText = finalText.replace(/Step/g, "\nStep");
  finalText = finalText.replace(/Error/g, "\nError");

  // if text starts with blank spaces, remove them
  if (text.startsWith(" ")) {
    finalText = text.replace(/ /g, "");
  }

  let finalDiv = '<div class="flex flex-row items-center ma-0 pa-0">';

  if (!isNullOrWhiteSpace(finalText)) {
    finalDiv += `<span class="font-light text-gray-400/90">${timestamp}</span>`;
    // all the \n should be a new row of the second column
    const textSplitted = finalText.split("\n");
    finalDiv += `<div class="flex flex-col items-start justify-start">`;
    textSplitted.forEach((text) => {
      if (!isNullOrWhiteSpace(text)) {
        finalDiv += `<span class="ml-4">${text}</span>`;
      }
    });
  }

  finalDiv += "</div>";
  return finalDiv;
};

const isNullOrWhiteSpace = (text) => {
  // check if text is null, undefined, or only has a \n, or empty spaces
  return !text || !text.trim();
};

const downloadFile = (data) => {
  const blob = new Blob([JSON.stringify(data)], {
    type: "application/json",
  });
  const url = window.URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.setAttribute("download", "file.json");
  document.body.appendChild(link);
  link.click();
};

watch(
  () => [
    props.configurationLogger.length,
    props.derivationLogger.length,
    props.deploymentLogger.length,
  ],
  () => {
    scrollBottom("config");
    scrollBottom("derivation");
    scrollBottom("deployment");
  }
);
</script>

<template>
  <!-- DEPLOYMENT PANELS -->
  <div>
    <v-expansion-panels class="mt-8" :model-value="panelSelected">
      <!-- CONFIG PANEL -->
      <v-expansion-panel
        eager
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
          <span
            v-for="log in configurationLogger"
            :key="log.id"
            class="text-sm"
          >
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
        eager
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
        eager
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
            <div v-html="logFormatted(log.text, log.timestamp)"></div>
            <br />
          </span>
        </v-expansion-panel-text>
      </v-expansion-panel>
    </v-expansion-panels>

    <!-- DEPLOYMENT INFORMATION -->
    <div
      v-if="
        passedPhases.includes('deployment') &&
        (deploymentStatusPhases['deployment'].status == 'success' ||
          deploymentStatusPhases['deployment'].status == 'error')
      "
      class="container mx-auto px-4 py-8 grid grid-cols-5 gap-4"
    >
      <div
        class="rounded-lg border bg-card text-card-foreground shadow-sm col-span-4 flex flex-col items-center justify-center p-4"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="24"
          height="24"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
          class="w-8 h-8 mb-2"
          :class="
            deploymentStatusPhases['deployment'].status == 'success'
              ? 'text-[#349157]'
              : 'text-red-500'
          "
        >
          <polyline points="20 6 9 17 4 12"></polyline>
        </svg>
        <h3
          class="text-xl font-semibold whitespace-nowrap leading-none tracking-tight text-center"
        >
          <p class="mb-2">
            Your deployment
            <span>
              {{
                deploymentStatusPhases["deployment"].status == "success"
                  ? "started here:"
                  : "failed"
              }}
            </span>
          </p>
          <p
            v-if="deploymentStatusPhases['deployment'].status == 'success'"
            class="mb-2 text-base font-normal"
          >
            Maybe is not ready yet, check your docker logs
          </p>
          <a
            v-if="deploymentStatusPhases['deployment'].status == 'success'"
            :href="deployDataSel?.config?.host"
            target="_blank"
            class="text-[#349157] underline"
          >
            {{ deployDataSel?.config?.host }}
          </a>
        </h3>
      </div>
      <div
        class="rounded-lg border bg-card text-card-foreground shadow-sm flex flex-col divide-y divide-gray-200 dark:divide-gray-700"
      >
        <div
          class="flex flex-col items-center justify-center p-4 cursor-pointer hover:bg-gray-50 dark:hover:bg-gray-800"
          @click="downloadFile(deployDataSel.config)"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="24"
            height="24"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
            class="w-8 h-8 mb-2 text-gray-500 dark:text-gray-400"
          >
            <path
              d="M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z"
            ></path>
            <circle cx="12" cy="12" r="3"></circle>
          </svg>
          <h3
            class="font-semibold whitespace-nowrap leading-none tracking-tight"
          >
            Configuration
          </h3>
        </div>
        <div
          class="flex flex-col items-center justify-center p-4 hover:bg-gray-50 dark:hover:bg-gray-800 cursor-pointer"
          @click="downloadFile(deployDataSel.spec)"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="24"
            height="24"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
            class="w-8 h-8 mb-2 text-gray-500 dark:text-gray-400"
          >
            <path
              d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"
            ></path>
            <polyline points="14 2 14 8 20 8"></polyline>
          </svg>
          <h3
            class="font-semibold whitespace-nowrap leading-none tracking-tight"
          >
            Specification
          </h3>
        </div>
      </div>
    </div>
  </div>
</template>
