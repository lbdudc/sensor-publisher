<template>
  <div>
    <nav-bar>
      <template v-slot:title>
        <v-container class="pl-0" fluid>
          <span class="font-italic">
            {{ engineStore.basicData.name }} (v.
            {{ engineStore.basicData.version }})
          </span>
        </v-container>
      </template>

      <template v-slot:items>
        <v-btn :to="{ name: 'ProjectMain' }" text exact>
          {{ $tc("project.name", 1) }}
        </v-btn>
        <v-btn :to="{ name: 'FeatureTree' }" text exact>
          {{ $t("menu.feature_selection") }}
        </v-btn>
        <v-btn :to="{ name: 'DataModel' }" text exact>
          {{ $t("menu.class_diagram") }}
        </v-btn>
        <v-btn
          v-if="staticsSelected"
          :to="{ name: 'Static_PageList' }"
          text
          exact
        >
          {{ $t("menu.statics") }}
        </v-btn>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-btn icon @click="save" v-on="on" :class="{ quadrat: changes }">
              <v-icon>save</v-icon>
            </v-btn>
          </template>
          {{ $t("menu.save_tooltip") }}
        </v-tooltip>
      </template>
    </nav-bar>

    <router-view></router-view>
  </div>
</template>

<script>
import engineStore from "@/services/engineStore";
import Project from "@/services/project.service";
import customization from "@/services/customization.service";
import NavBar from "@/components/common/NavBar";

export default {
  components: { NavBar },
  data() {
    return { engineStore, changes: false, currentSpec: null };
  },
  computed: {
    staticsSelected() {
      return this.engineStore.selection.indexOf("GUI_StaticPages") !== -1;
    },
  },
  created() {
    this._updateCurrentSpec();
  },
  mounted() {
    setInterval(() => {
      this.changes =
        this.changes ||
        this.currentSpec !== JSON.stringify(engineStore.getSpec());
    }, 1000);
  },
  methods: {
    _updateCurrentSpec() {
      this.currentSpec = JSON.stringify(engineStore.getSpec());
      this.changes = false;
    },
    async save() {
      try {
        this._updateCurrentSpec();
        await Project.update(this.engineStore.getSpec());
      } catch (err) {
        this.$notify({
          text: err.response.data.message,
          type: "error",
        });
      }
    },
  },
  async beforeRouteEnter(to, from, next) {
    if (customization.customizing) {
      customization.endCustomization();
    }
    if (
      engineStore.basicData.name === to.params.pName &&
      engineStore.basicData.version === to.params.pVersion
    ) {
      next();
      return;
    }
    try {
      engineStore.load(await Project.get(to.params.pName, to.params.pVersion));
      next();
    } catch (err) {
      console.error(err);
      next("/");
    }
  },
  async beforeRouteUpdate(to, from, next) {
    if (customization.customizing) {
      customization.endCustomization();
    }
    if (
      engineStore.basicData.name === to.params.pName &&
      engineStore.basicData.version === to.params.pVersion
    ) {
      next();
      return;
    }
    try {
      engineStore.load(await Project.get(to.params.pName, to.params.pVersion));
      next();
    } catch (err) {
      console.error(err);
      next("/");
    }
  },
};
</script>

<style scoped>
.quadrat {
  -webkit-animation: save-badge-animation 1s infinite; /* Safari 4+ */
  -moz-animation: save-badge-animation 1s infinite; /* Fx 5+ */
  -o-animation: save-badge-animation 1s infinite; /* Opera 12+ */
  animation: save-badge-animation 1s infinite; /* IE 10+, Fx 29+ */
}

@keyframes save-badge-animation {
  0%,
  49% {
    color: #ffffff;
  }
  50%,
  100% {
    color: #fb8c00;
  }
}
</style>
