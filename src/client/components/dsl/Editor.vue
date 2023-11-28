<template>
  <v-container
    fluid
    class="h-full main-container flex flex-row w-full items-center justify-center pa-0"
  >
    <!-- <QueryList /> -->
    <section
      class="bg-gradient-to-r from-indigo-500 to-blue-500 h-full flex items-center justify-center grow transition-all duration-500"
      :class="!expandAside ? 'w-3/6' : 'w-full'"
    >
      <div class="text-white h-full flex items-center justify-center">
        <div class="flex flex-col pa-0 w-full h-full pt-5">
          <!-- Deploy button -->
          <div class="h-10 flex justify-end mr-10 mb-2 mt-1">
            <v-btn class="white--text" color="#E255D0">
              <!-- color="green" -->
              Generate
              <v-icon>mdi-rocket</v-icon>
            </v-btn>
          </div>

          <div no-gutters class="flex justify-center items-center">
            <!-- Editor -->
            <!-- <EditorVue /> -->
          </div>
        </div>
      </div>

      <!-- relative in the middle of the screen and the right of the parent container -->
      <div class="relative right-5 top-4 transform -translate-y-1/2">
        <v-btn icon color="white" @click="expandAside = !expandAside">
          <v-icon v-if="!expandAside">mdi-chevron-right</v-icon>
          <v-icon v-else>mdi-chevron-left</v-icon>
        </v-btn>
      </div>
    </section>
    <!-- GET THE rest of the screen -->
    <aside
      class="bg-gradient-to-r from-blue-500 to-indigo-500 h-full flex items-center justify-center flex-none transition-all duration-500"
    >
      <span v-if="!expandAside" class="w-full h-full">Aside</span>
    </aside>
  </v-container>
</template>

<script>
export default {
  name: "Editor",
  data() {
    return {
      expandAside: true,
    };
  },
};
</script>

<style lang="css" scoped></style>
