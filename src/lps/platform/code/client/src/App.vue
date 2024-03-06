<template>
  <v-app>
    <notifications :max="3" :width="300" position="top center" />
    /*% if (feature.GUI_Menu) { %*/
    <MenuBar>
      <!-- -->
    </MenuBar>
    /*% } %*/
    <v-main>
      <router-view class="content">
        <!-- -->
      </router-view>
    </v-main>
  </v-app>
</template>

<script>
/*% if (feature.GUI_Menu) { %*/import MenuBar from "./components/MenuBar";/*% } %*/

export default {
  name: "App",
  /*% if (feature.GUI_Menu) { %*/components: { MenuBar },/*% } %*/
};
</script>
