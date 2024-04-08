<script setup>
import { nextTick, onMounted, onUnmounted, ref, watch, computed } from "vue";
import * as monaco from "monaco-editor";
import snippets from "@/components/editor/snippets.js";

const emit = defineEmits(["update:value"]);

let monacoEditor = null;

const props = defineProps({
  code: {
    type: String,
    required: false,
  },
});

watch(
  () => props.code,
  (value) => {
    monacoEditor.setValue(value);
  },
  {
    deep: true,
  }
);

// get event resize the window and update the height
window.addEventListener("resize", () => {
  if (window.innerWidth < 1025) {
    // get the height of the editor
    document.getElementById("editor").style.height =
      window.innerHeight / 2 + "px";
    return;
  }
  // get the height of the editor
  document.getElementById("editor").style.height =
    window.innerHeight - 200 + "px";
});

// computed height
const calcHeight = computed(() => {
  // get in medium screen only the half of the screen
  if (window.innerWidth < 1025) {
    const height = window.innerHeight / 2;
    return height > 0 ? "height:" + height + "px" : 0;
  }
  const height = window.innerHeight - 200;
  return height > 0 ? "height:" + height + "px" : 0;
});

const calcTheme = computed(() => {
  if (window.matchMedia("(prefers-color-scheme: dark)").matches) {
    return "vs-dark";
  }
  return "vs";
});

const options = ref({
  language: "sql",
  colorDecorators: true,
  lineHeight: 24,
  tabSize: 2,
  readOnly: false,
  automaticLayout: true,
  theme: calcTheme,
});

const editor = ref();

const initEditor = () => {
  monaco.languages.register({ id: "custom" });
  monacoEditor = monaco.editor.create(editor.value, options.value);
  monacoEditor.getModel().onDidChangeContent(() => {
    updateCode(monacoEditor.getValue());
  });
  monacoEditor.setValue(props.code);
  registerSnippets();
};

const registerSnippets = () => {
  snippets.suggestions.map((snippet) => {
    snippet.kind = monaco.languages.CompletionItemKind.Snippet;
    snippet.insertTextRules =
      monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet;
  });
  monaco.languages.registerCompletionItemProvider("custom", {
    provideCompletionItems: () => {
      return snippets;
    },
  });
};

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
  initEditor();
});

onUnmounted(() => {
  document.removeEventListener("keydown", (e) => {
    if (e.ctrlKey && e.key === "s") {
      // Prevent the Save dialog to open
      e.preventDefault();
    }
  });
});

const updateCode = (code) => {
  emit("update:value", code);
};
</script>
<template>
  <div
    class="mt-2 text-editor"
    :style="calcHeight"
    id="editor"
    ref="editor"
  ></div>
</template>
<style scoped></style>

<style scoped>
.text-editor {
  width: 100%;
}
</style>
