/*% if (feature.GUI_StaticPages) { %*/
<template>
  <v-container>
    <span v-html="body"></span>
  </v-container>
</template>

<script>
export default {
  name: "preview-code",
  props: ["body"]
};
</script>
/*% } %*/
