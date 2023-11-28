<template>
  <div v-if="!feature.hidden">
    <v-layout>
      <v-btn :disabled="!expandable" icon @click="switchExpand()">
        <v-icon>{{ icon }}</v-icon>
      </v-btn>
      <v-tooltip right :disabled="!description">
        <template v-slot:activator="{ on }">
          <v-checkbox
            v-model="innerFeature.selected"
            :disabled="feature.mandatory"
            @change="updateSelection()"
          >
            <div slot="label" :class="featureClass">
              {{ label }}
            </div>
            <template v-slot:append v-if="description">
              <v-icon v-on="on">{{ descriptionIcon }}</v-icon>
            </template>
          </v-checkbox>
        </template>
        {{ description }}
      </v-tooltip>
    </v-layout>

    <div v-if="feature.features && feature.expanded" class="feature-children">
      <feature
        v-for="c in feature.features"
        :key="c.name"
        :feature="c"
        @change="(newVal) => (c = newVal)"
      ></feature>
    </div>
  </div>
</template>

<script>
import engineStore from "@/services/engineStore";
import featureLabel from "@/services/feature-label";
import i18n from "@/plugins/i18n";

export default {
  name: "Feature",
  props: {
    feature: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      innerFeature: this.feature,
    };
  },
  computed: {
    icon() {
      if (!this.expandable) return "";
      if (this.feature.expanded) return "expand_less";
      return "expand_more";
    },
    descriptionIcon() {
      if (!this.description) return "";
      return "help";
    },
    description() {
      return featureLabel[i18n.locale][this.feature.name]
        ? featureLabel[i18n.locale][this.feature.name].desc
        : null;
    },
    label() {
      return featureLabel[i18n.locale][this.feature.name]
        ? featureLabel[i18n.locale][this.feature.name].label
        : this.feature.name;
    },
    expandable() {
      return this.feature.features && this.feature.features.length > 0;
    },
    featureClass() {
      const classes = [this.feature.type];
      if (this.feature.mandatory) classes.push("mandatory");
      if (engineStore.errorFeature === this.feature.name)
        classes.push("error-feature");
      return classes;
    },
  },
  methods: {
    updateSelection() {
      if (this.feature.selected) {
        engineStore.selects(this.feature);
      } else {
        engineStore.deselects(this.feature);
      }
    },
    switchExpand() {
      if (!this.expandable) return;
      this.innerFeature.expanded = !this.feature.expanded;
      this.$emit("change", this.innerFeature);
    },
  },
};
</script>

<style lang="css" scoped>
.clickable {
  cursor: pointer;
}

.description {
  font-size: smaller;
  font-style: italic;
}

.feature-children {
  padding-left: 40px;
}

.mandatory {
  text-decoration: underline;
}

.XOR::after {
  font-style: italic;
  content: "(XOR)";
}

.OR::after {
  font-style: italic;
  content: "(OR)";
}

/* compact style */

.v-btn {
  height: 24px;
  margin: 0;
}

.v-input {
  margin: 0;
  padding: 0;
}

/* vuetify v-input loses scoped style,
  using >>> solves it (deep selector),
  but scss parcel fail so this style goes
  with css */
.v-input >>> .v-messages {
  min-height: 0px;
}

.v-input >>> .v-input__slot {
  margin: 0;
}

.v-input--selection-controls:not(.v-input--hide-details) >>> .v-input__slot {
  margin-bottom: 2px;
}

.error-feature {
  background-color: #ff5252;
  color: white;
  padding-left: 20px;
  padding-right: 20px;
}
</style>
