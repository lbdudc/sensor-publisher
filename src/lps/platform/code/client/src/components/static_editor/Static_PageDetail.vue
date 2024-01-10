/*% if (feature.GUI_StaticPages) { %*/
<template>
  <v-container>
    <v-card v-if="!isLoading">
      <v-card-title>
        <v-row no-gutters v-if="!isLoading">
          <v-col cols="6" class="d-none d-md-block">
            <span class="headline no-split-words" v-if="static_page">
              {{ $t($route.meta.label) }}
            </span>
          </v-col>
          <v-col class="text-right">
            <v-btn @click="back()">
              <v-icon class="material-icons">arrow_back</v-icon>
              <span class="d-none d-sm-block"> {{ $t("back") }} </span>
            </v-btn>
            /*% if (feature.GUI_SP_Management) { %*/
            <v-btn v-if="isAdmin" class="warning ml-2" @click="edit(static_page)">
              <v-icon class="material-icons details-close-button">edit</v-icon>
              <span class="d-none d-sm-block"> {{ $t("edit") }} </span>
            </v-btn>

            <v-btn
              /*% if (data.statics && data.statics.find(s => s.id == 'Home')) { %*/
              v-if="isAdmin && static_page.definedId !== 'Home'"
              /*% } else { %*/
              v-if="isAdmin"
              /*% } %*/
              class="error ml-2"
              @click="showDialog"
            >
              <v-icon class="material-icons details-close-button"
                >delete</v-icon
              >
              <span class="d-none d-sm-block"> {{ $t("remove") }} </span>
            </v-btn>
            /*% } %*/
          </v-col>
        </v-row>
      </v-card-title>
      <v-card-text>
        <v-row dense>
          <v-col cols="3" md="2" class="text-right">
            <b> {{ $t("static_editor.id") }}: </b>
          </v-col>
          <v-col cols="9" md="10">
            {{ static_page.definedId }}
          </v-col>

          <v-col cols="3" md="2" class="text-right">
            <b> {{ $t("static_editor.description") }}: </b>
          </v-col>
          <v-col cols="9" md="10">
            {{ translation.description }}
          </v-col>

          <v-col cols="3" md="2" class="text-right">
            <b> {{ $t("static_editor.body") }}: </b>
          </v-col>
          <v-col cols="9" md="10" class="page-content pa-2" v-html="translation.body">
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>

    <loading-page v-if="isLoading"></loading-page>
    /*% if (feature.GUI_SP_Management) { %*/
    <delete-dialog
      :dialog="dialog"
      @cancel="closeDialog"
      @submit="deleteStaticPage"
    ></delete-dialog>
    /*% } %*/
  </v-container>
</template>

<script>
import checkInvalidID from "@/common/checkInvalidID";
import Static_PageRepository from "@/repositories/components/Static_PageRepository";
/*% if (feature.GUI_SP_Management) { %*/
import DeleteDialog from "@/components/modal_dialog/DeleteDialog";
import {mapAuthGetter} from "@/common/mapAuthGetter";
/*% } %*/
import LoadingPage from "@/components/loading-page/LoadingPage.vue"

export default {
  name: "Static_PageDetail",
  components: { /*% if (feature.GUI_SP_Management) { %*/DeleteDialog, /*% } %*/LoadingPage },
  data() {
    return {
      static_page: null,
      /*% if (feature.GUI_SP_Management) { %*/
      dialog: false,
      /*% } %*/
      loading: false
    }
  },
  computed: {
    /*% if (feature.GUI_SP_Management) { %*/
    ...mapAuthGetter(["isAdmin"]),
    /*% } %*/
    isLoading() {
      return this.loading;
    },
    translation() {
      if (!this.static_page || !this.static_page.translations)
        return { body: null, description: null };
      else return this.static_page.translations.find(
        el => el.language.toLowerCase() === this.$i18n.locale.toLowerCase()
      );
    }
  },
  watch: {
    "$i18n.locale"() {
      this._fetchData(this.$route.params.id);
    },
  },
  created() {
    this._fetchData(this.$route.params.id);
  },
  methods: {
  /*% if (feature.GUI_SP_Management) { %*/
    edit() {
      this.$router.push({
        name: "Static_PageForm",
        params: { id: this.static_page.definedId, backPrevious: true }
      });
    },
    showDialog() {
      this.dialog = true;
    },
    closeDialog() {
      this.dialog = false;
    },
    deleteStaticPage() {
      this.loading = true;
      Static_PageRepository.delete(this.static_page.id)
        .then(() => {
          this.dialog = false;
          this.$router.push({ name: "Static_PageList" });
        })
        .finally(() => (this.loading = false));
    },
  /*% } %*/
    _fetchData(id) {
      this.loading = true;
      Static_PageRepository.get(id)
        .then(res => (this.static_page = res))
        .catch(err => checkInvalidID(err))
        .finally(() => (this.loading = false));
    },
    back() {
      this.$router.push({
        name: "Static_PageList",
        params: { backAction: true }
      });
    }
  }
};
</script>
<style scoped>
.page-content {
  margin: 0 auto;
  border: 1px solid grey;
}
</style>
/*% } %*/
