/*% if (feature.GUI_SP_Management) { %*/
<template>
  <v-container>
    <v-card v-if="!isLoading">
      <v-card-title>
        <v-row align="center" no-gutters>
          <v-col cols="6" class="d-none d-md-block">
            <span class="headline no-split-words">
              {{ $t($route.meta.label) }}
            </span>
          </v-col>
          <v-col cols="12" md="6" class="text-right">
            <v-btn class="ml-2" @click="back()">
              <v-icon>mdi-arrow-left</v-icon>
              <span class="d-none d-sm-block">
                {{ $t("cancel") }}
              </span>
            </v-btn>
            <v-tooltip bottom :disabled="!lengthExceeded">
              <template v-slot:activator="{ on }">
                <span v-on="on">
                  <v-btn
                    class="success ml-2"
                    @click="save(static_page)"
                    :disabled="!validForm || emptyBody || lengthExceeded"
                  >
                    <v-icon>save</v-icon>
                    <span class="d-none d-sm-block">
                      {{ $t("save") }}
                    </span>
                  </v-btn>
                </span>
              </template>
              <span>{{ $t("static_editor.lenght_exceeded") }}</span>
            </v-tooltip>
          </v-col>
        </v-row>
      </v-card-title>
      <v-card-text>
        <v-container>
          <v-row>
            <v-col>
              <v-form v-model="validForm">
                <v-text-field
                  v-model="static_page.definedId"
                  type="String"
                  :label="$t('static_editor.id')"
                  :disabled="disabledId"
                  :rules="[v => !!v || $t('static_editor.errors.id_mandatory')]"
                ></v-text-field>
                <v-card flat>
                  <v-tabs
                    v-if="languages.length > 1"
                    v-model="selectedLanguageIdx"
                    @change="onLanguageSelected"
                    grow
                  >
                    <v-tab v-for="language in languages" :key="language.value">
                      {{ $t(language.text) }}
                    </v-tab>
                  </v-tabs>
                  <v-col>
                    <v-text-field
                      v-model="selectedDescription"
                      @input="updateDescription"
                      type="String"
                      :label="$t('static_editor.description')"
                    ></v-text-field>
                  </v-col>
                  <v-col>
                    <!--
                      Initialize model value on ready event due to
                      https://github.com/ckeditor/ckeditor5-vue/issues/167
                    -->
                    <ckeditor
                      :editor="editor"
                      @input="updateBody"
                      @ready="setSelected"
                      v-model="selectedBody"
                      :config="editorConfig"
                    >
                    </ckeditor>
                  </v-col>
                </v-card>
              </v-form>
            </v-col>
          </v-row>
        </v-container>
      </v-card-text>
    </v-card>

    <loading-page v-if="isLoading"></loading-page>
  </v-container>
</template>

<script>
var updatableBodies = [];
var updatableDescriptions = [];

import Vue from "vue";
import checkInvalidID from "@/common/checkInvalidID";
import ClassicEditor from "@/plugins/ckeditor.js";
import LoadingPage from "@/components/loading-page/LoadingPage.vue";
import i18n from "@/plugins/i18n";
import Static_PageRepository from "@/repositories/components/Static_PageRepository";

export default {
  name: "Static_PageForm",
  components: {
    "loading-page": LoadingPage
  },
  data() {
    return {
      static_page: {},
      languages: [
/*%
  data.basicData.languages.forEach(function(lang) {
%*/
        {
          text: "languages./*%= normalize(lang) %*/",
          value: "/*%= lang.toLocaleUpperCase() %*/"
        },
/*%
  });

%*/
      ],
      selectedLanguage: "/*%= data.basicData.languages[0].toLocaleUpperCase() %*/",
      selectedLanguageIdx: 0,
      selectedBody: null,
      lengthExceeded: false,
      selectedDescription: null,
      editor: ClassicEditor,
      editorConfig: {
        language: i18n.locale,
        toolbar: {
          items: [
            'heading',
            '|',
            'bold',
            'italic',
            'underline',
            'strikethrough',
            'blockquote',
            'code',
            'subscript',
            'superscript',
            '|',
            'alignment:left',
            'alignment:right',
            'alignment:center',
            'alignment:justify',
            'numberedList',
            'bulletedList',
            '|',
            'undo',
            'redo',
            '|',
            'link',
            'imageUpload'
          ]
        },
        image: {
          toolbar: [
            "imageTextAlternative",
            "|",
            "imageStyle:full",
            "imageStyle:alignCenter",
            "imageStyle:alignLeft",
            "imageStyle:alignRight"
          ],
          styles: ["full", "alignCenter", "alignLeft", "alignRight"]
        }
      },
      loading: false,
      validForm: true
    }
  },
  beforeRouteUpdate(to, from, next) {
    // si se accede a la misma ruta con diferentes parámetros, se cargará el nuevo objeto
    if (to.params.id)
      this._fetchData(to.params.id)
        .then(() => {
          this.initTranslations();
          this.setUpdatableItems();
          this.setSelected();
        });
    else {
      this.initTranslations();
      this.setUpdatableItems();
      this.setSelected();
    }
    next();
  },
  created() {
    if (this.$route.params.id) {
      this._fetchData(this.$route.params.id).then(() => {
        this.initTranslations();
        this.setUpdatableItems();
      });
    } else {
      this.initTranslations();
      this.setUpdatableItems();
    }
  },
  watch: {
    $route: "mounted"
  },
  computed: {
    disabledId() {
      return !!this.$route.params.id
    },
    emptyBody() {
      return !this.selectedBody;
    },
    isLoading() {
      return this.loading;
    },
  },
  methods: {
    _fetchData(id) {
      this.loading = true;
      return Static_PageRepository.get(id)
        .then(res => (this.static_page = res))
        .catch(err => checkInvalidID(err))
        .finally(() => (this.loading = false));
    },
    updateBody(val) {
      let hasBodyTooLong = false;
      updatableBodies.forEach(idx => {
        Vue.set(this.static_page.translations[idx], "body", val);
        if (val.length > 10485760) hasBodyTooLong = true;
      });
      this.lengthExceeded = hasBodyTooLong;
    },
    updateDescription(val) {
      updatableDescriptions.forEach(idx =>
        Vue.set(this.static_page.translations[idx], "description", val)
      );
    },
    setUpdatableItems() {
      // Texts written in a language are propageted to languages without texts
      updatableBodies = [];
      updatableDescriptions = [];

      let translations = JSON.parse(JSON.stringify(this.static_page.translations));

      translations.map((el, index) => {
          el.index = index;
          return el;
        })
        .filter(el => el.language === this.selectedLanguage || !el.body || el.body === "")
        .forEach(el => updatableBodies.push(el.index));

      translations.map((el, index) => {
          el.index = index;
          return el;
        })
        .filter(el => el.language === this.selectedLanguage || !el.description)
        .forEach(el => updatableDescriptions.push(el.index));
    },
    setSelected() {
      // update inputs data to language selected
      let translation = this.static_page.translations.find(
        el => el.language === this.selectedLanguage
      );

      this.selectedBody = translation.body;
      this.selectedDescription = translation.description;
    },
    onLanguageSelected(val) {
      this.selectedLanguage = this.languages[val].value;
      this.setSelected();
      this.setUpdatableItems();
    },
    initTranslations() {
      // initialize translations if static_page doesn't have them
      if (!this.static_page.translations) this.static_page.translations = [];
      this.languages.forEach(lang => {
        const idx = this.static_page.translations.findIndex(
          el => el.language === lang.value
        );
        if (idx === -1) {
          let translation = {
            language: lang.value,
            body: "",
            description: null
          };
          this.static_page.translations.push(translation);
        }
      });
    },
    back() {
      this.$router.push({
        name: "Static_PageList",
        params: { backAction: true }
      });
    },
    save() {
      if (!this.validForm) {
        this.$notify({
          type: "error",
          text: this.$t("static_editor.formFailures")
        });
      } else {
        this.loading = true;
        Static_PageRepository.save(this.static_page)
          .then(() => {
            if (this.$route.params.id) {
              this.$router.push({
                name: "Static_PageDetail",
                params: { id: this.static_page.definedId, backAction: true }
              });
            } else {
              this.$router.push({
                name: "Static_PageList",
                params: { backAction: true }
              });
            }
          })
          .finally(() => (this.loading = false));
      }
    }
  }
};
</script>
/*% } %*/
