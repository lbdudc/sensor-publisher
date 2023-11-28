<script setup>
import EditorVue from "@/components/editor/Editor.vue";
import { onMounted, ref } from "vue";
// import { SensorBuilder } from "../../main.js";
import { DerivationEngine } from "spl-js-engine";
// import Engine from "@/services/engineStore.js";

const expandAside = ref(true);
</script>

<template>
  <v-container
    fluid
    class="h-full main-container flex flex-row w-full items-center justify-center"
  >
    <!-- <QueryList /> -->
    <section
      class="bg-gradient-to-r from-indigo-500 to-blue-500 h-full flex items-center justify-center grow transition-all duration-500"
    >
      <div class="text-white w-4/5 h-full flex items-center justify-center">
        <div class="flex flex-col pa-0 w-full h-full pt-5">
          <!-- Deploy button -->
          <div class="h-10 flex justify-end mr-10 mb-2 mt-1">
            <v-btn class="text-white" append-icon="mdi-rocket" color="#E255D0">
              <!-- color="green" -->
              Generate
            </v-btn>
          </div>

          <div no-gutters class="flex justify-center items-center">
            <!-- Editor -->
            <EditorVue />
          </div>
        </div>
      </div>

      <!-- relative in the middle of the screen and the right of the parent container -->
      <div class="relative right-5 top-4 transform -translate-y-1/2">
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
      class="bg-gradient-to-r from-blue-500 to-indigo-500 h-full flex items-center justify-center flex-none transition-all duration-500"
      :class="!expandAside ? 'w-2/6' : 'w-0 '"
    >
      <span v-if="!expandAside">Aside</span>
    </aside>
  </v-container>
</template>

<style lang="css" scoped>
</style>
