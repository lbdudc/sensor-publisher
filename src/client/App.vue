<template>
  <v-app>
    <link
      href="https://cdn.jsdelivr.net/npm/@mdi/font@latest/css/materialdesignicons.min.css"
      rel="stylesheet"
    />
    <link
      href="https://fonts.googleapis.com/css?family=Roboto:300,400,500,700|Material+Icons"
      rel="stylesheet"
    />
    <v-main>
      <nav-bar />
      <router-view></router-view>
    </v-main>
  </v-app>
</template>

<script>
import Navbar from "@/components/common/NavBar.vue";
import engineStore from "@/services/engineStore";
import store from "@/services/store";
import NavBar from "./components/common/NavBar.vue";

export default {
  components: { NavBar },
  name: "App",
  data() {
    return {
      engineStore,
      store,
    };
  },
};
</script>

<style lang="scss"></style>
