<script setup>
import parseSensorDSL from "sensor-dsl";
import MonacoEditor from "@/components/editor/MonacoEditor.vue";
import { onMounted, ref } from "vue";
// import DerivationEngine from "spl-js-engine/src/derivation-engine";

onMounted(() => {
  console.log("mounted");
  // const algo = parseSensorDSL("CREATE PRODUCT algo USING 4327;");
  // console.log(algo);
});

const codeEditor = ref(`CREATE PRODUCT algo USING 4327;`);

const updateCode = (code) => {
  codeEditor.value = code;
};

const generateProduct = () => {
  console.log("generateProduct");
  const algo = parseSensorDSL(codeEditor.value);
  console.log(algo);
};

const expandAside = ref(true);
</script>

<template>
  <v-container
    fluid
    class="flex flex-row items-center justify-center w-full h-full main-container"
  >
    <!-- <QueryList /> -->
    <section
      class="flex items-center justify-center h-full transition-all duration-500 bg-gradient-to-r from-indigo-500 to-blue-500 grow"
    >
      <div class="flex items-center justify-center w-4/5 h-full text-white">
        <div class="flex flex-col w-full h-full pt-5 pa-0">
          <!-- Deploy button -->
          <div class="flex justify-end h-10 mt-1 mb-2 mr-10">
            <v-btn
              class="text-white"
              append-icon="mdi-rocket"
              color="#E255D0"
              @click="generateProduct"
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
          :icon="expandAside ? 'mdi-chevron-right' : 'mdi-chevron-left'"
        >
        </v-btn>
      </div>
    </section>
    <!-- GET THE rest of the screen -->
    <aside
      class="flex items-center justify-center flex-none h-full transition-all duration-500 bg-gradient-to-r from-blue-500 to-indigo-500"
      :class="!expandAside ? 'w-2/6' : 'w-0 '"
    >
      <span v-if="!expandAside">Aside</span>
    </aside>
  </v-container>
</template>

<style lang="css" scoped></style>
