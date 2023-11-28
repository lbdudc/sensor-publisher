<template>
  <section
    class="h-screen flex flex-col gap-10 justify-center content-start items-center bg-gradient-to-r from-indigo-500 to-blue-500 dark:from-purple-800 dark:to-indigo-900"
  >
    <!-- SPINNING CIRCLE -->
    <div class="flex justify-center items-center">
      <div
        class="relative w-64 h-64 cursor-pointer hover:bg-white/10 dark:border-gray-300 dark:text-gray-300 dark:hover:bg-gray-300/10 rounded-full shadow-lg"
        @click="goToDslEditor"
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
    <div class="flex flex-row justify-center space-x-6 mb-10">
      <div
        class="border-2 border-gray-300 hover:border-blue-500 bg-white dark:bg-gray-800 p-2 rounded-lg cursor-pointer shadow-md"
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
        class="border-2 border-gray-300 hover:border-blue-500 bg-white dark:bg-gray-800 p-2 rounded-lg cursor-pointer shadow-md"
        role="button"
        aria-label="Traffic Control"
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
        class="border-2 border-gray-300 hover:border-blue-500 bg-white dark:bg-gray-800 p-2 rounded-lg cursor-pointer shadow-md"
        role="button"
        aria-label="Quality Air"
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
    </div>
  </section>
</template>

<script>
export default {
  methods: {
    goToDslEditor() {
      this.$router.push({ name: "Editor" });
    },
  },
};
</script>

<style scoped>
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
