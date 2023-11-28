<template>
  <v-container>
    <span class="title">{{ $t("database.title") }}</span>
    <v-row>
      <v-col cols="12" sm="3">
        <v-text-field
          v-model="innerConfig.host"
          :label="$t('database.hostname')"
        ></v-text-field>
      </v-col>
      <v-col cols="12" sm="2">
        <v-text-field
          v-model="innerConfig.port"
          type="number"
          :label="$t('database.port')"
        >
        </v-text-field>
      </v-col>
      <v-col cols="12" sm="3">
        <v-text-field
          v-model="innerConfig.database"
          :label="$t('database.database')"
        ></v-text-field>
      </v-col>
      <v-col cols="12" sm="2">
        <v-text-field
          v-model="innerConfig.username"
          :label="$t('database.username')"
        ></v-text-field>
      </v-col>
      <v-col cols="12" sm="2">
        <v-text-field
          v-model="innerConfig.password"
          :label="$t('database.password')"
        ></v-text-field>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  props: {
    config: {
      type: Object,
      required: true,
    },
  },
  computed: {
    innerConfig: {
      get() {
        return this.config;
      },
      set() {
        this.$emit("input", this.innerConfig);
      },
    },
  },
};
</script>
