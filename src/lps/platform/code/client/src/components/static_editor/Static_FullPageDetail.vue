/*% if (feature.GUI_StaticPages) { %*/
<template>
  <v-container>
    <span
      v-if="!isLoading && static_page.translations"
      v-html="static_page.translations[0].body"
    ></span>
    <loading-page v-if="isLoading"></loading-page>
  </v-container>
</template>

<script>
import checkInvalidID from "@/common/checkInvalidID";
import LoadingPage from "@/components/loading-page/LoadingPage";
import Static_PageRepository from "@/repositories/components/Static_PageRepository";
import i18n from "@/plugins/i18n";

export default {
  name: "Static_FullPageDetail",
  props: {
    static_page_id: {
      type: String,
      required: true
    }
  },
  components: { LoadingPage },
  data() {
    return {
      loading: false,
      static_page: {}
    };
  },
  computed: {
    isLoading() {
      return this.loading;
    }
  },
  watch: {
    "$i18n.locale"() {
      this._fetchData(this.static_page_id);
    },
  },
  created() {
    this._fetchData(this.static_page_id);
  },
  methods: {
    _fetchData(id) {
      this.loading = true;
      return Static_PageRepository.getTranslation(id, i18n.locale)
        .then(page => (this.static_page = page))
        .catch(err => checkInvalidID(err))
        .finally(() => (this.loading = false));
    }
  }
};
</script>
/*% } %*/
