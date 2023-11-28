<template>
  <v-container fluid>
    <transition name="slide-fade">
      <database-configuration
        v-if="showDBConfig"
        :config="store.basicData.database"
        @input="(newVal) => (store.basicData.database = newVal)"
      ></database-configuration
    ></transition>

    <!-- Actions  -->
    <v-row justify="center">
      <v-col cols="12" md="7" lg="6" offset-xl="0" xl="4">
        <p class="text-center blue--text">{{ $t("project.button.actions") }}</p>
        <v-divider></v-divider>
        <v-btn mt-2 text block @click="newProjectFromCurrent()">
          {{ $t("project.button.duplicate") }}
        </v-btn>
        <v-btn block text @click="renameProject()">
          {{ $t("project.button.rename") }}
        </v-btn>
        <v-btn block text color="error" @click="deleteDialog = true">
          {{ $t("project.button.deleteProject") }}
        </v-btn>
      </v-col>
      <v-col cols="12" md="7" lg="6" offset-xl="0" xl="4">
        <p class="text-center blue--text">
          {{ $t("project.button.configuration") }}
        </p>
        <v-divider></v-divider>
        <v-btn
          block
          text
          @click="showDBConfig = !showDBConfig"
          v-show="!showDBConfig"
        >
          {{ $t("project.button.show_database") }}
        </v-btn>
        <v-btn
          block
          color="warning"
          text
          @click="showDBConfig = !showDBConfig"
          v-show="showDBConfig"
        >
          {{ $t("project.button.hide_database") }}
        </v-btn>
        <v-btn
          v-if="listsOrFormsSelected"
          block
          color="warning"
          text
          @click="enterParametrizationDialog = true"
        >
          {{ $t("project.button.parametrize") }}
        </v-btn>
      </v-col>
      <v-col cols="12" md="7" lg="6" offset-xl="0" xl="4">
        <p class="text-center blue--text">
          {{ $t("project.button.import_export") }}
        </p>
        <v-divider></v-divider>
        <v-btn pt-4 block text @click="startImportJSON()">
          {{ $t("project.button.import") }}
        </v-btn>
        <v-divider></v-divider>
        <v-btn block text @click="exportJSON()">
          {{ $t("project.button.export") }}
        </v-btn>
        <v-btn block text color="success" @click="generate()">
          {{ $t("project.button.generate") }}
        </v-btn>
      </v-col>
    </v-row>
    <input ref="hiddenUploader" type="file" class="d-none" />

    <!-- Dialog of confirmation to delete project -->
    <modal-dialog
      @cancel="deleteDialog = false"
      @submit="removeVersion()"
      :dialog="deleteDialog"
      :title="$t('project.delete_title')"
      titleClass="error white--text"
      titleIcon="warning"
      submitClass="error"
      :submitText="$t('delete')"
      :content="$t('project.delete_dialog')"
    ></modal-dialog>

    <!-- Dialog of confirmation to change phase -->
    <modal-dialog
      @cancel="enterParametrizationDialog = false"
      @submit="loadParametrization()"
      :dialog="enterParametrizationDialog"
      :title="$t('warning')"
      titleClass="warning white--text"
      titleIcon="warning"
      submitClass="warning"
      :submitText="$t('project.button.parametrize')"
      :content="$t('project.parametrization_dialog')"
    ></modal-dialog>
  </v-container>
</template>

<script>
import engineStore from "@/services/engineStore";
import DatabaseConfiguration from "./DatabaseConfiguration";
import normalize from "@/services/normalize";
import Project from "@/services/project.service";
import FileSaver from "file-saver";
import ModalDialog from "@/components/common/ModalDialog";
const reader = new FileReader();

export default {
  components: { DatabaseConfiguration, ModalDialog },
  data() {
    return {
      store: engineStore,
      showDBConfig: false,
      enterParametrizationDialog: false,
      deleteDialog: false,
    };
  },
  computed: {
    listsOrFormsSelected() {
      return (
        this.store.selection.indexOf("GUI_Lists") != -1 ||
        this.store.selection.indexOf("GUI_Forms") != -1
      );
    },
  },
  mounted() {
    this._setupUploaderCallback();
  },
  methods: {
    newProjectFromCurrent() {
      this.$router.push({
        name: "NewProject",
        params: {
          specification: JSON.parse(JSON.stringify(engineStore.getSpec())),
        },
      });
    },
    renameProject() {
      this.$router.push({
        name: "NewProject",
        params: {
          specification: JSON.parse(JSON.stringify(engineStore.getSpec())),
          renaming: true,
        },
      });
    },
    startImportJSON() {
      this.$refs.hiddenUploader.click();
    },
    _setupUploaderCallback() {
      this.$refs.hiddenUploader.addEventListener("change", (evt) => {
        reader.onload = (evt) => {
          const result = JSON.parse(evt.target.result);
          if (result.data) {
            this.$notify({
              text: this.$t("project.notification.error-importing-cli-spec"),
              type: "error",
            });
            this.$refs.hiddenUploader.value = null;
            return;
          }
          engineStore.load(result, false);
          this.$notify({
            text: this.$t("project.notification.specification_loaded"),
            type: "success",
          });
        };
        reader.readAsText(evt.target.files[0]);
      });
    },
    exportJSON() {
      const spec = engineStore.getSpec();
      const blob = new Blob([JSON.stringify(spec, null, 2)], {
        type: "text/plain;charset=utf-8",
      });
      FileSaver.saveAs(
        blob,
        normalize(engineStore.basicData.name, true) +
          "." +
          engineStore.basicData.version +
          ".json"
      );
    },
    generate() {
      engineStore.engine.generateZip(engineStore.getSpec()).then((zip) => {
        zip.generateAsync({ type: "blob" }).then((file) => {
          FileSaver.saveAs(
            file,
            normalize(engineStore.basicData.name, true) +
              "." +
              engineStore.basicData.version +
              ".zip"
          );
        });
      });
    },
    loadParametrization() {
      this.$router.push({
        name: "CustomizationExporter",
        params: {
          pName: engineStore.basicData.name,
          pVersion: engineStore.basicData.version,
        },
      });
    },
    async removeVersion() {
      try {
        await Project.remove(
          engineStore.basicData.name,
          engineStore.basicData.version
        );
        engineStore.resetSpec();
        this.deleteDialog = false;
        this.$router.push({ name: "ProjectSelector" });
      } catch (err) {
        console.error(err);
      }
    },
  },
};
</script>

<style lang="scss" scoped>
.menu {
  transition: all 0.3s ease-in;
  min-height: 220px;
  animation: configurationAnimation 0.8s forwards 0s ease-in-out;
}

@keyframes configurationAnimation {
  0% {
    transform: rotate(0) translateX(-4%);
    opacity: 0;
  }
  100% {
    transform: rotate(0) translateX(0px);
    opacity: 1;
  }
}

.slide-fade-enter-active {
  transition: all 0.6s ease;
  height: 150px;
  overflow: hidden;
}

.slide-fade-leave-active {
  transition: all 0.6s ease;
  height: 150px;
}

.slide-fade-enter,
.slide-fade-leave-to {
  opacity: 0;
  height: 0;
  padding: 0 10px;
}
</style>
