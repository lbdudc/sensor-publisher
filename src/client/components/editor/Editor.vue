<script setup>
import { computed, nextTick, onMounted, onUnmounted, ref, watch } from "vue";
import MonacoEditor from "monaco-editor-vue3";

const emit = defineEmits(["update:value"]);

const props = defineProps({
  dslSpec: {
    type: String,
    required: false,
  },
});

watch(
  () => props.dslSpec,
  (value) => {
    localQuery.value = JSON.stringify(value, null, 2);
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
      // Place your code here
      saveQuery();
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
      // Place your code here
      saveQuery();
    }
  });
});

// Editor configuration
const localQuery = ref(JSON.stringify(props.dslSpec, null, 2));

const options = ref({
  colorDecorators: true,
  lineHeight: 24,
  tabSize: 2,
  readOnly: false,
  automaticLayout: true,
});

const saveQuery = () => {
  try {
    const res = JSON.parse(localQuery.value);
    emit("update:value", res);
    queryError.value = false;
    saveButtonAnimation.value = false;
    saveAlert();
  } catch (e) {
    queryError.value = true;
    saveAlert();
    return;
  }
};

// Save Alert notification
const saveAlertNotification = ref(false);
const queryError = ref(false);

const saveAlert = () => {
  saveAlertNotification.value = true;
  setTimeout(() => {
    saveAlertNotification.value = false;
  }, 2000);
};

const alertText = computed(() => {
  return queryError.value
    ? "Error al guardar la consulta"
    : "Consulta guardada correctamente";
});

// Animation button
const saveButtonAnimation = ref(false);
const saveAnimation = () => {
  saveButtonAnimation.value = true;
};
</script>

<template>
  <!-- <v-alert
    v-if="saveAlertNotification"
    :type="!queryError ? 'success' : 'error'"
  >
    {{ alertText }}
  </v-alert> -->
  <div class="flex w-full h-full pa-10 items-center justify-center">
    <MonacoEditor
      v-if="rendered"
      v-model:value="localQuery"
      :options="options"
      language="json"
      theme="vs"
      :height="700"
      @change="saveAnimation"
    />
  </div>
  <!-- <v-btn
    id="bottom-button"
    :class="
      saveButtonAnimation ? 'action-button save-animation' : 'action-button'
    "
    position="absolute"
    elevation="4"
    location="bottom end"
    right
    icon="mdi-content-save"
    @click="saveQuery"
  ></v-btn> -->
</template>


<style scoped>
#bottom-button {
  margin-bottom: 2rem;
  margin-right: 3rem;
  z-index: 10;
  will-change: auto;
}

.action-button {
  background-color: #3e8f40;
  color: white;
}

/* change background color animation */
.save-animation {
  animation: save 2s ease-in-out infinite;
}

@keyframes save {
  0% {
    color: white;
    background-color: #3e8f40;
  }
  50% {
    color: #3e8f40;
    background-color: #ffffff;
  }
  100% {
    color: white;
    background-color: #3e8f40;
  }
}
</style>
