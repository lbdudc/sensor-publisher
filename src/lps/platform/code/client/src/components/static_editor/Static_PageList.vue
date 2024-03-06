/*% if (feature.GUI_StaticPages) { %*/
<template>
  <v-container>
    <v-card>
      <v-card-title>
        <v-row align="center" no-gutters>
          <v-col cols="6" class="d-none d-md-block">
            <span class="headline no-split-words">
              {{ $t($route.meta.label) }}
            </span>
          </v-col>
          <v-col cols="12" md="6" class="text-center text-md-right">
            <v-text-field
              class="text-field"
              v-model="search"
              append-icon="search"
              :label="$t('static_editor.search')"
              single-line
              hide-details
              dense
            ></v-text-field>
            /*% if (feature.GUI_SP_Management) { %*/
            <v-btn
              v-if="isAdmin"
              color="success"
              class="ml-3"
              :to="{ name: 'Static_PageCreate' }"
            >
              <v-icon>add</v-icon>
              <span class="d-none d-sm-block">
                {{ $t("static_editor.new") }}
              </span>
            </v-btn>
            /*% } %*/
          </v-col>
        </v-row>
      </v-card-title>
      <v-card-text>
        <v-data-table
          :headers="headers"
          :items="statics"
          :search="search"
          @click:row="staticPageDetail"
        >
          <template v-slot:[`item.description`]="{ item }">
            <span v-if="item.translations[0]">
              {{ item.translations[0].description }}
            </span>
          </template>
          <template v-slot:[`item.created_date`]="{ item }">
            <span>{{ item.created_date | dateFormat }} </span>
          </template>
          <template v-slot:[`item.modified_date`]="{ item }">
            <span>{{ item.modified_date | dateFormat }} </span>
          </template>
          <template v-slot:[`item.action`]="{ item }">
            /*% if (feature.GUI_SP_Management) { %*/
            <v-icon class="mr-1" color="blue" @click.stop="showPreview(item)"
              >filter_none
            </v-icon>
            /*% } %*/
            <v-icon color="primary" @click.stop="staticPageDetail(item)">
              description
            </v-icon>
            /*% if (feature.GUI_SP_Management) { %*/
            <v-icon
              v-if="isAdmin"
              color="warning"
              @click.stop="editStaticPage(item)"
              >edit</v-icon
            >
            <v-icon
              /*% if (data.statics && data.statics.find(s => s.id == 'Home')) { %*/
              v-if="isAdmin && item.definedId !== 'Home'"
              /*% } else { %*/
              v-if="isAdmin"
              /*% } %*/
              color="error"
              @click.stop="showDeleteDialog(item)"
              >delete
            </v-icon>
            /*% } %*/
          </template>
        </v-data-table>
      </v-card-text>
    </v-card>

    /*% if (feature.GUI_SP_Management) { %*/
    <!-- Dialogo para la eliminacion de paginas estaticas -->
    <delete-dialog
      :dialog="dialog"
      :title="$t('static_editor.delete_title')"
      @cancel="dialog = false"
      @submit="deleteStaticPage(selected)"
    ></delete-dialog>
    /*% } %*/

    <!-- Dialogo que muestra el preview de las paginas estaticas -->
    <v-dialog v-model="preview_dialog">
      <v-card>
        <v-card-title>
          <span>{{ $t('static_editor.preview') }}</span>
          <v-spacer></v-spacer>
          <v-icon @click="preview_dialog = false">close</v-icon>
        </v-card-title>
        <v-card-text>
          <preview-code :body="preview_selected"></preview-code>
        </v-card-text>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import PreviewCodeVue from "./PreviewCode.vue";
/*% if (feature.GUI_SP_Management) { %*/
import DeleteDialog from "@/components/modal_dialog/DeleteDialog";
import {mapAuthGetter} from "@/common/mapAuthGetter";
/*% } %*/
import Static_PageRepository from "@/repositories/components/Static_PageRepository";

export default {
  name: "Static_PageList",
  components: { "preview-code": PreviewCodeVue/*% if (feature.GUI_SP_Management) { %*/, DeleteDialog/*% } %*/ },
  data() {
    return {
      search: "",
      statics: [],
      /*% if (feature.GUI_SP_Management) { %*/
      dialog: false,
      selected: null,
      /*% } %*/
      loading: false,
      preview_selected: null,
      preview_dialog: false
    };
  },
  computed: {
    /*% if (feature.GUI_SP_Management) { %*/
    ...mapAuthGetter(["isAdmin"]),
    /*% } %*/
    headers() {
      return [
        { text: this.$t("static_editor.id"), value: "definedId" },
        { text: this.$t("static_editor.description"), value: "description" },
        { text: this.$t("static_editor.creationDate"), value: "created_date" },
        { text: this.$t("static_editor.lastModification"), value: "modified_date" },
        /*% if (feature.UserManagement) { %*/
        { text: this.$t("static_editor.author"), value: "author.login" },
        /*% } %*/
        { text: "", sortable: false, value: "action" }
      ];
    }
  },
  watch: {
    "$i18n.locale": {
      handler() {
        this._fetchData();
      },
      immediate: true,
    },
  },
  methods: {
    _fetchData() {
      this.loading = true;
      const options = { params: { language: this.$i18n.locale } };
      Static_PageRepository.getAll(options)
        .then(res => (this.statics = res.content))
        .finally(() => (this.loading = false));
    },
    /*% if (feature.GUI_SP_Management) { %*/
    editStaticPage(item) {
      this.$router.push({
        name: "Static_PageForm",
        params: { id: item.definedId }
      });
    },
    showDeleteDialog(entity) {
      this.selected = entity;
      this.dialog = true;
    },
    deleteStaticPage(entity) {
      this.loading = true;
      Static_PageRepository.delete(entity.id)
        .then(() => {
          this.statics.splice(this.statics.indexOf(entity), 1);
          this.dialog = false;
        })
        .finally(() => (this.loading = false));
    },
    /*% } %*/
    showPreview(selection) {
      const translation = selection.translations.find(
        el => el.language.toLowerCase() === this.$i18n.locale.toLowerCase()
      );
      this.preview_dialog = true;
      this.preview_selected = translation.body;
    },
    staticPageDetail(item) {
      this.$router.push({
        name: "Static_PageDetail",
        params: { id: item.definedId }
      });
    }
  }
};
</script>

<style scoped>
.text-field {
  width: 71%;
  display: inline-block;
}
</style>

/*% } %*/
