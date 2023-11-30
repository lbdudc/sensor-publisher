<script setup>
import { nextTick, onMounted, onUnmounted, ref, watch } from "vue";
import MonacoEditor from "monaco-editor-vue3";

const emit = defineEmits(["update:value"]);

const props = defineProps({
  code: {
    type: String,
    required: false,
  },
});

const localCode = ref(props.code);

watch(
  () => props.code,
  (value) => {
    localCode.value = value;
  },
  {
    deep: true,
  }
);

// Trick to wait for the component to be mounted
const rendered = ref(false);

onMounted(() => {
  document.addEventListener("keydown", (e) => {
    if (e.ctrlKey && e.key === "s") {
      // Prevent the Save dialog to open
      e.preventDefault();
    }
  });

  nextTick(() => {
    rendered.value = true;
  });
});

onUnmounted(() => {
  document.removeEventListener("keydown", (e) => {
    if (e.ctrlKey && e.key === "s") {
      // Prevent the Save dialog to open
      e.preventDefault();
    }
  });
});

const options = ref({
  colorDecorators: true,
  lineHeight: 24,
  tabSize: 2,
  readOnly: false,
  automaticLayout: true,
});

const updateCode = (code) => {
  emit("update:value", code);
};
</script>

<template>
  <div class="flex w-full h-full pa-10 items-center justify-center">
    <MonacoEditor
      v-if="rendered"
      v-model:value="localCode"
      :options="options"
      language="plaintext"
      theme="vs"
      :height="700"
      @change="updateCode"
    />
  </div>
</template>

<style scoped></style>
