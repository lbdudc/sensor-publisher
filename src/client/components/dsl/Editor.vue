<template>
  <v-container
    fluid
    class="flex flex-row items-center justify-center w-full h-full main-container pa-0"
  >
    <!-- <QueryList /> -->
    <section
      class="flex items-center justify-center h-full transition-all duration-500 bg-gradient-to-r from-indigo-500 to-blue-500 grow"
      :class="!expandAside ? 'w-3/6' : 'w-full'"
    >
      <div
        class="relative flex flex-col items-center justify-center h-screen text-white"
      >
        <!-- Deploy button -->
        <div class="absolute top-0 right-0 mt-5 mr-5">
          <v-btn class="white--text" color="#E255D0" @click="generateProduct">
            Generate
            <v-icon>mdi-rocket</v-icon>
          </v-btn>
        </div>

        <!-- Second flex container centered -->
        <div class="flex items-center justify-center w-4/6 my-20 h-4/5">
          <!-- Editor -->
          <monaco-editor
            :code="code"
            language="plaintext"
            @update:code="updateCode"
          ></monaco-editor>
        </div>
      </div>

      <!-- Toggle button -->
      <div class="relative transform -translate-y-1/2 right-5 top-4">
        <v-btn icon color="white" @click="expandAside = !expandAside">
          <v-icon v-if="!expandAside">mdi-chevron-right</v-icon>
          <v-icon v-else>mdi-chevron-left</v-icon>
        </v-btn>
      </div>
    </section>
    <!-- GET THE rest of the screen -->
    <aside
      class="flex items-center justify-center flex-none h-full transition-all duration-500 bg-gradient-to-r from-blue-500 to-indigo-500"
    >
      <span v-if="!expandAside" class="w-full h-full">Aside</span>
    </aside>
  </v-container>
</template>

<script>
import MonacoEditor from "./MonacoEditor.vue";
import parse from "@/services/sensor.js";

export default {
  name: "Editor",
  components: {
    MonacoEditor,
  },
  data() {
    return {
      expandAside: true,
      code: `
        CREATE PRODUCT intecmar USING 4326;

        CREATE SPATIAL DIMENSION Estuary (
            geometry: Polygon
        ) WITH PROPERTIES (
            shortName String DISPLAY_STRING,
            name String,
            zone String
        );

        SET DEPLOYMENT (
          "client_deploy_url" "https://pcmg.citic.udc.es",
          "geoserver_url" "http://cronos.lbd.org.es/geoserver",
          "geoserver_url_wms" "http://cronos.lbd.org.es/geoserver/wms",
          "geoserver_user" "admin",
          "geoserver_password" "$$geoserver$$",
          "server_deploy_url" "https://pcmg.citic.udc.es/backend",
          "server_deploy_port" "9001"
        );`,
    };
  },
  methods: {
    updateCode(newCode) {
      this.code = newCode;
    },
    generateProduct() {
      console.log("Generating product...");
      const parsed = parse(this.code);
      console.log(parsed);
    },
  },
};
</script>

<style lang="css" scoped></style>
