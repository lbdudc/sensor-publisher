/*% if (!feature.GUI_StaticPages || !data.statics || data.statics.find(s => s.id == 'Home') == undefined) { %*/
<template>
  <v-container>
    <h1 class="text-center">Welcome to /*%= data.basicData.name %*/</h1>
  </v-container>
</template>
/*% } %*/
