<script setup>
import sensorDSL from "sensor-dsl";
import MonacoEditor from "@/components/editor/MonacoEditor.vue";
import { ref, onMounted } from "vue";
import SensorBuilder from "./sensor-builder.js";
import { useRoute } from "vue-router";
import FeatureTree from "./feature-selector/FeatureTree.vue";

const SERVER_URL = `http://localhost:${import.meta.env.VITE_SERVER_PORT || 3000}/api`;

const codeEditor = ref(`CREATE PRODUCT algo USING 4326;`);
const route = useRoute();
const loadingGenerateProduct = ref(false);
const updatedCode = ref();
const expandAside = ref(true);

// Error handling
const showError = ref(false);
const showErrorDialog = ref(false);
const errorTitle = ref("");
const errorText = ref("");

const resetErrors = () => {
  showError.value = false;
  errorTitle.value = "";
  errorText.value = "";
};

const resetParser = () => {
  sensorDSL.reset();
};

onMounted(() => {
  if (route.query.text) {
    codeEditor.value = localStorage.getItem("fileContent");
    updatedCode.value = codeEditor.value;
  } else if (route.query.example) {
    const url = route.query.example + ".txt";
    fetch(url).then((res) => {
      res.text().then((res) => (codeEditor.value = res));
    });
  }
  // Get feature model from server
  getFeatures();
});

const updateCode = (code) => {
  updatedCode.value = code;
};

const generateProduct = async () => {
  loadingGenerateProduct.value = true;

  resetErrors();
  resetParser();

  let sensorJSON;
  try {
    sensorJSON = await sensorDSL.parse(updatedCode.value);
  } catch (error) {
    showError.value = true;
    errorTitle.value = "Error parsing DSL";
    errorText.value = error;
    loadingGenerateProduct.value = false;
  }

  if (!sensorJSON) {
    return;
  }

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

  if (selectedFeatures.value.length > 0)
    sensorJSON.features = selectedFeatures.value;

  // Generate product
  try {
    await downloadZip(sensorBuilder.getDSLSpec());
  } catch (error) {
    showError.value = true;
    errorTitle.value = "Error generating product";
    errorText.value = error;
  }

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
      const error = await response.text();
      throw new Error(JSON.stringify(JSON.parse(error), null, 4));
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
    throw new Error(error);
  }
};

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
    <!-- <Main editor Section /> -->
    <section
      class="flex items-center justify-center w-full h-full transition-all duration-500 grow bg-gradient-to-r from-indigo-500 to-blue-500 dark:from-purple-800 dark:to-indigo-900"
    >
      <div
        class="flex flex-col items-center justify-center w-11/12 h-full text-white pr-4"
      >
        <div class="flex flex-col w-full h-fit pa-0">
          <!-- Error Message -->
          <v-alert
            v-if="showError"
            class="cursor-pointer w-fit"
            @click="showErrorDialog = true"
            @click:close.stop="showError = false"
            v-model="showError"
            closable
            color="#d90303"
            icon="mdi-alert-circle-outline"
            :title="errorTitle"
            density="compact"
          >
            <!-- Cut the text with ... to avoid the alert to be too big -->
            <span class="truncate w-full" slot="text" v-html="errorText"></span>
          </v-alert>

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

      <!-- Button to open the feature model tree -->
      <div class="relative transform -translate-y-1/2 right-0 top-4">
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

    <!-- Error Dialog -->
    <v-dialog v-model="showErrorDialog" max-width="700">
      <v-card>
        <v-toolbar color="error">
          <v-icon class="ml-4">mdi-alert-circle-outline</v-icon>
          <v-toolbar-title>{{ errorTitle }}</v-toolbar-title>
          <v-spacer></v-spacer>
          <v-btn size="small" icon="mdi-close" @click="showErrorDialog = false">
          </v-btn>
        </v-toolbar>
        <v-card-text class="max-h-screen overflow-y-auto">
          <pre>{{ errorText }}</pre>
        </v-card-text>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<style lang="css" scoped>
aside {
  will-change: width;
}
</style>
