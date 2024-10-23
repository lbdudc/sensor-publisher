<script setup>
import { useRouter } from "vue-router";
const router = useRouter();

const goToDslEditor = (params) => {
  router.push({ name: "dsl-editor", query: params });
};

function dropHandler(ev) {
  ev.preventDefault();
  if (ev.dataTransfer.items) {
    [...ev.dataTransfer.items].forEach((item) => {
      if (item.kind === "file") {
        const file = item.getAsFile();
        if (file.name.endsWith(".txt")) {
          const reader = new FileReader();
          reader.onload = function (event) {
            const fileContent = event.target.result;
            localStorage.setItem("fileContent", fileContent);
            goToDslEditor({ text: file.name });
          };
          reader.readAsText(file);
        }
      }
    });
  }
}

function dragOverHandler(ev) {
  ev.preventDefault();
  const dropZone = document.getElementById("drop-zone");
  dropZone.classList.add("bg-white/10");
}
function dragLeaveHandler() {
  const dropZone = document.getElementById("drop-zone");
  dropZone.classList.remove("bg-white/10");
}
</script>

<template>
  <section
    class="h-screen flex flex-col gap-20 justify-center items-center bg-gradient-to-r from-indigo-500 to-blue-500 dark:from-purple-800 dark:to-indigo-900"
  >
    <!-- SPINNING CIRCLE -->
    <div
      class="flex justify-center items-center"
      d="drop_zone"
      :ondrop="dropHandler"
      :ondragover="dragOverHandler"
      :ondragleave="dragLeaveHandler"
    >
      <div
        id="drop-zone"
        class="relative w-64 h-64 cursor-pointer hover:bg-white/10 dark:border-gray-300 dark:text-gray-300 dark:hover:bg-gray-300/10 rounded-full shadow-lg"
        @click.stop="goToDslEditor({ query: 'new' })"
      >
        <div
          class="relative w-64 h-64 border-4 border-dashed rounded-full border-blue-200"
          id="dropzone"
          role="button"
          aria-label="Drag and Drop Sensor"
        ></div>

        <!-- INSIDE THE CIRCLE -->
        <div
          class="absolute inset-0 text-white flex flex-col items-center justify-center"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="30"
            height="30"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
            class="text-indigo-600 dark:text-blue-300 mb-2 text-9xl"
          >
            <path
              d="M4 20h16a2 2 0 0 0 2-2V8a2 2 0 0 0-2-2h-7.93a2 2 0 0 1-1.66-.9l-.82-1.2A2 2 0 0 0 7.93 3H4a2 2 0 0 0-2 2v13c0 1.1.9 2 2 2Z"
            ></path>
          </svg>
          <p class="text-center text-2xl">Drag &amp; Drop Configuration</p>
          <p class="text-center text-sm mt-2">
            Or, <span class="underline"> create your own here</span>
          </p>
        </div>
      </div>
    </div>

    <!-- PRODUCT PRESET CONFIGURATIONS -->
    <div
      class="flex justify-center space-x-6"
      @click.stop="goToDslEditor({ example: 'intecmar' })"
    >
      <div
        class="bg-blue-100 dark:hover:bg-gray-700 hover:bg-gray-400 py-4 px-8 dark:bg-gray-800 p-2 rounded-lg cursor-pointer shadow-md"
        role="button"
        aria-label="Maritime Sensors"
      >
        <div class="flex flex-col items-center">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="24"
            height="24"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
            class="text-blue-500 dark:text-blue-300 mb-1"
          >
            <path d="M9.3 6.2a4.55 4.55 0 0 0 5.4 0"></path>
            <path d="M7.9 10.7c.9.8 2.4 1.3 4.1 1.3s3.2-.5 4.1-1.3"></path>
            <path
              d="M13.9 3.5a1.93 1.93 0 0 0-3.8-.1l-3 10c-.1.2-.1.4-.1.6 0 1.7 2.2 3 5 3s5-1.3 5-3c0-.2 0-.4-.1-.5Z"
            ></path>
            <path
              d="m7.5 12.2-4.7 2.7c-.5.3-.8.7-.8 1.1s.3.8.8 1.1l7.6 4.5c.9.5 2.1.5 3 0l7.6-4.5c.7-.3 1-.7 1-1.1s-.3-.8-.8-1.1l-4.7-2.8"
            ></path>
          </svg>
          <h2 class="text-center text-gray-900 dark:text-gray-200 mb-1 text-lg">
            Maritime Sensors
          </h2>
          <p class="text-center text-gray-700 dark:text-gray-400 text-sm">
            Click to load this configuration
          </p>
        </div>
      </div>

      <div
        class="bg-blue-100 dark:hover:bg-gray-700 hover:bg-gray-400 py-4 px-8 dark:bg-gray-800 p-2 rounded-lg cursor-pointer shadow-md"
        role="button"
        aria-label="Traffic Control"
        @click.stop="goToDslEditor({ example: 'magist_traffic' })"
      >
        <div class="flex flex-col items-center">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="24"
            height="24"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
            class="text-blue-500 dark:text-blue-300 mb-1"
          >
            <path d="M9.3 6.2a4.55 4.55 0 0 0 5.4 0"></path>
            <path d="M7.9 10.7c.9.8 2.4 1.3 4.1 1.3s3.2-.5 4.1-1.3"></path>
            <path
              d="M13.9 3.5a1.93 1.93 0 0 0-3.8-.1l-3 10c-.1.2-.1.4-.1.6 0 1.7 2.2 3 5 3s5-1.3 5-3c0-.2 0-.4-.1-.5Z"
            ></path>
            <path
              d="m7.5 12.2-4.7 2.7c-.5.3-.8.7-.8 1.1s.3.8.8 1.1l7.6 4.5c.9.5 2.1.5 3 0l7.6-4.5c.7-.3 1-.7 1-1.1s-.3-.8-.8-1.1l-4.7-2.8"
            ></path>
          </svg>
          <h2 class="text-center text-gray-900 dark:text-gray-200 mb-1 text-lg">
            Traffic Control
          </h2>
          <p class="text-center text-gray-700 dark:text-gray-400 text-sm">
            Click to load this configuration
          </p>
        </div>
      </div>

      <div
        class="bg-blue-100 dark:hover:bg-gray-700 hover:bg-gray-400 py-4 px-8 dark:bg-gray-800 p-2 rounded-lg cursor-pointer shadow-md"
        role="button"
        aria-label="Quality Air"
        @click.stop="goToDslEditor({ example: 'magist_qa' })"
      >
        <div class="flex flex-col items-center">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="24"
            height="24"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
            class="text-blue-500 dark:text-blue-300 mb-1"
          >
            <path d="M17.7 7.7a2.5 2.5 0 1 1 1.8 4.3H2"></path>
            <path d="M9.6 4.6A2 2 0 1 1 11 8H2"></path>
            <path d="M12.6 19.4A2 2 0 1 0 14 16H2"></path>
          </svg>
          <h2 class="text-center text-gray-900 dark:text-gray-200 mb-1 text-lg">
            Quality Air
          </h2>
          <p class="text-center text-gray-700 dark:text-gray-400 text-sm">
            Click to load this configuration
          </p>
        </div>
      </div>

      <div
        class="bg-blue-100 dark:hover:bg-gray-700 hover:bg-gray-400 py-4 px-8 dark:bg-gray-800 p-2 rounded-lg cursor-pointer shadow-md"
        role="button"
        aria-label="Moving Ships"
        @click.stop="goToDslEditor({ example: 'moving_ships' })"
      >
        <div class="flex flex-col items-center">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            height="24px"
            viewBox="0 -960 960 960"
            width="24px"
            fill="#93c5fd"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
            class="text-blue-500 dark:text-blue-300 mb-1"
          >
            <path
              d="M479-418ZM158-200 82-468q-3-12 2.5-28t23.5-22l52-18v-184q0-33 23.5-56.5T240-800h120v-120h240v120h120q33 0 56.5 23.5T800-720v184l52 18q21 8 25 23.5t1 26.5l-76 268q-50 0-91-23.5T640-280q-30 33-71 56.5T480-200q-48 0-89-23.5T320-280q-30 33-71 56.5T158-200ZM80-40v-80h80q42 0 83-13t77-39q36 26 77 38t83 12q42 0 83-12t77-38q36 26 77 39t83 13h80v80h-80q-42 0-82-10t-78-30q-38 20-78.5 30T480-40q-41 0-81.5-10T320-80q-38 20-78 30t-82 10H80Zm160-522 240-78 240 78v-158H240v158Zm240 282q47 0 79.5-33t80.5-89q48 54 65 74t41 34l44-160-310-102-312 102 46 158q24-14 41-32t65-74q50 57 81.5 89.5T480-280Z"
            />
          </svg>

          <h2 class="text-center text-gray-900 dark:text-gray-200 mb-1 text-lg">
            Moving Ships
          </h2>
          <p class="text-center text-gray-700 dark:text-gray-400 text-sm">
            Click to load this configuration
          </p>
        </div>
      </div>
    </div>
  </section>
</template>

<style lang="css" scoped>
#dropzone {
  animation: spin 40s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>
