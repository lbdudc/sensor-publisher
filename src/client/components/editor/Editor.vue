<script setup>
import parseSensorDSL from "sensor-dsl";
import MonacoEditor from "@/components/editor/MonacoEditor.vue";
import { ref, onMounted } from "vue";
import SensorBuilder from "./sensor-builder.js";
import { useRoute } from "vue-router";
import FeatureTree from "./feature-selector/FeatureTree.vue";

const SERVER_URL = "http://localhost:3000/api";


const codeEditor = ref(`CREATE PRODUCT algo USING 4326;`);
const route = useRoute();
const loadingGenerateProduct = ref(false);
const updatedCode = ref();

onMounted(() => {
  if (route.query.text) {
    codeEditor.value = localStorage.getItem("fileContent");
    updatedCode.value = codeEditor.value;
  }

  getFeatures();
});

const updateCode = (code) => {
  updatedCode.value = code;
};

const generateProduct = async () => {
  loadingGenerateProduct.value = true;
  const sensorJSON = await parseSensorDSL(updatedCode.value);
  // await until the sensorJSON is not null, await 1 second
  await new Promise((resolve) => {
    const interval = setInterval(() => {
      if (sensorJSON) {
        clearInterval(interval);
        resolve();
      }
    }, 1000);
  });

  const sensorBuilder = new SensorBuilder(sensorJSON);
  sensorJSON.features = selectedFeatures.value;

  await downloadZip(sensorBuilder.getDSLSpec());

  loadingGenerateProduct.value = false;
};

const downloadZip = async (sensorData) => {
  try {
    const response = await fetch(`${SERVER_URL}/data`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(sensorData),
    });
    if (!response.ok) {
      // TODO: handle error on SplJSEngine: should pass this error to the aside
      throw new Error("Network response was not ok");
    }

    const blob = await response.blob();
    const blobUrl = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = blobUrl;
    link.download = "output.zip";
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(blobUrl);
  } catch (error) {
    console.error("Error downloading zip:", error);
  }
};

const expandAside = ref(true);

// Feature Selection
const features = ref(null);
const selectedFeatures = ref([]);

const getFeatures = async () => {
  try {
    const response = await fetch(`${SERVER_URL}/features`);
    if (!response.ok) {
      throw new Error("Network response was not ok");
    }
    const data = await response.json();
    features.value = data;
  } catch (error) {
    console.error("Error getting features:", error);
  }
};

const updateFeaturesSelection = (features) => {
  selectedFeatures.value = features.map((f) => f.name);
};
</script>

<template>
  <v-container
    fluid
    class="flex flex-row items-center justify-center w-full h-full main-container"
  >
    <!-- <QueryList /> -->
    <section
      class="flex items-center justify-center w-full h-full transition-all duration-500 grow bg-gradient-to-r from-indigo-500 to-blue-500 dark:from-purple-800 dark:to-indigo-900"
    >
      <div class="flex items-center justify-center w-11/12 h-full text-white">
        <div class="flex flex-col w-full h-full pt-10 pa-0">
          <!-- Deploy button -->
          <div class="absolute top-4 right-10 flex justify-end h-10 mb-2 mr-10">
            <v-btn
              class="text-white"
              append-icon="mdi-rocket"
              color="#E255D0"
              @click="generateProduct"
              :loading="loadingGenerateProduct"
            >
              <!-- color="green" -->
              Generate
            </v-btn>
          </div>

          <div no-gutters class="flex items-center justify-center">
            <!-- Editor -->
            <MonacoEditor :code="codeEditor" @update:value="updateCode" />
          </div>
        </div>
      </div>

      <!-- relative in the middle of the screen and the right of the parent container -->
      <div class="relative transform -translate-y-1/2 right-5 top-4">
        <v-btn
          class="text-white"
          variant="plain"
          @click="expandAside = !expandAside"
          density="compact"
          :icon="expandAside ? 'mdi-chevron-left' : 'mdi-chevron-right'"
        >
        </v-btn>
      </div>
    </section>
    <!-- GET THE rest of the screen -->
    <aside
      class="flex items-center justify-center flex-none h-full transition-all duration-500 bg-gradient-to-r to-indigo-500 from-blue-500 dark:from-indigo-900 dark:to-indigo-800"
      :class="!expandAside ? 'w-3/6' : 'w-0 '"
    >
      <div
        v-if="!expandAside && features != null"
        class="h-full w-full bg-blue-900 dark:bg-[#1e1e1e] pt-13 border-s-2 border-gray-200 overflow-y-auto max-h-fit"
      >
        <h1 class="text-left text-xl font-bold pl-2 text-white pb-5">
          <v-icon icon="mdi-graph" class="ml-2"></v-icon>
          Feature Model
        </h1>
        <FeatureTree
          :rootFeature="features"
          @change="updateFeaturesSelection"
        />
      </div>
    </aside>
  </v-container>
</template>

<style lang="css" scoped>
aside {
  will-change: width;
}
</style>
