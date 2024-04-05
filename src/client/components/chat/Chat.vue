<script setup>
  import { ref } from "vue";

  const SERVER_URL = `${
    import.meta.env.VITE_SERVER_URL || "http://localhost:5000"
  }/api`;
  const message = ref("");
  let conversation = [];
  let nTokensConversation = 0;

  // Counts the approximate number of tokens in the input while adding the total number for the whole conversation
  async function tokenCounter(text) {
    const words = text.split(/\s+/);
    const nWords = words.length;
    const tokenCount = nWords*2 + nTokensConversation; // The number of words is doubled to take into account the possible response as well

    return tokenCount;
  }

  const getModelOutput = async () => {
    try {
      const userMessage = message.value;

      // Check that the amount of tokens does not exceed the maximum number of 4096 (as the counting is approximate we use 4000 instead)
      if (tokenCounter(userMessage) > 4000) {
        console.log("The amount of tokens in the conversation will exceed the maximum limit for the specified model. You need to start a new conversation. To do so, just write \"Reset\".");
        process.exit(1);
      } 

      // Send petition with the user input
      const response = await fetch(`${SERVER_URL}/chat`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          message: userMessage,
        })

      });
      if (!response.ok) {
        const error = await response.text();
        throw new Error(JSON.stringify(JSON.parse(error), null, 4));
      }

      // Get response and update conversation and number of tokens
      const openAIData = await response.json();
      conversation = openAIData.messagesHistory;
      nTokensConversation = openAIData.nTokens;
      // TO DO: Change conversation

      console.log(openAIData);
    } catch (error) {
      throw new Error(error);
    }
  };
</script>

<template>
  <div class="flex flex-col items-center justify-center w-full h-full">
    <!-- Component Start -->
    <div
      class="flex flex-col flex-grow w-full max-w-xl bg-white shadow-xl rounded-lg overflow-hidden"
    >
      <div class="flex flex-col flex-grow h-0 p-4 overflow-auto">
        <div class="flex w-full mt-2 space-x-3 max-w-xs">
          <div
            class="flex-shrink-0 h-10 w-10 rounded-full bg-gray-200 items-center justify-center flex"
          >
            <!-- Image centered -->
            <img src="/codellamaicon.png" width="30" height="30" alt="" />
          </div>
          <div>
            <div class="bg-gray-300 p-3 rounded-e-lg rounded-bs-lg">
              <p class="text-sm">
                Lorem ipsum dolor sit amet, consectetur adipiscing elit.
              </p>
            </div>
            <span class="text-xs text-gray-500 leading-none">2 min ago</span>
          </div>
        </div>
        <div class="flex w-full mt-2 space-x-3 max-w-xs ml-auto justify-end">
          <div>
            <div class="bg-blue-600 text-white p-3 rounded-s-lg rounded-be-lg">
              <p class="text-sm">
                Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do
                eiusmod.
              </p>
            </div>
            <span class="text-xs text-gray-500 leading-none">2 min ago</span>
          </div>
          <div class="flex-shrink-0 h-10 w-10 rounded-full bg-gray-300"></div>
        </div>
        <div class="flex w-full mt-2 space-x-3 max-w-xs ml-auto justify-end">
          <div>
            <div class="bg-blue-600 text-white p-3 rounded-s-lg rounded-be-lg">
              <p class="text-sm">Lorem ipsum dolor sit amet.</p>
            </div>
            <span class="text-xs text-gray-500 leading-none">2 min ago</span>
          </div>
          <div class="flex-shrink-0 h-10 w-10 rounded-full bg-gray-300"></div>
        </div>
        <div class="flex w-full mt-2 space-x-3 max-w-xs">
          <div class="flex-shrink-0 h-10 w-10 rounded-full bg-gray-300"></div>
          <div>
            <div class="bg-gray-300 p-3 rounded-e-lg rounded-bs-lg">
              <p class="text-sm">
                Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do
                eiusmod tempor incididunt ut labore et dolore magna aliqua.
              </p>
            </div>
            <span class="text-xs text-gray-500 leading-none">2 min ago</span>
          </div>
        </div>
        <div class="flex w-full mt-2 space-x-3 max-w-xs ml-auto justify-end">
          <div>
            <div class="bg-blue-600 text-white p-3 rounded-s-lg rounded-be-lg">
              <p class="text-sm">
                Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do
                eiusmod tempor incididunt ut labore et dolore magna aliqua.
              </p>
            </div>
            <span class="text-xs text-gray-500 leading-none">2 min ago</span>
          </div>
          <div class="flex-shrink-0 h-10 w-10 rounded-full bg-gray-300"></div>
        </div>
        <div class="flex w-full mt-2 space-x-3 max-w-xs ml-auto justify-end">
          <div>
            <div class="bg-blue-600 text-white p-3 rounded-s-lg rounded-be-lg">
              <p class="text-sm">
                Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do
                eiusmod tempor incididunt.
              </p>
            </div>
            <span class="text-xs text-gray-500 leading-none">2 min ago</span>
          </div>
          <div class="flex-shrink-0 h-10 w-10 rounded-full bg-gray-300"></div>
        </div>
        <div class="flex w-full mt-2 space-x-3 max-w-xs ml-auto justify-end">
          <div>
            <div class="bg-blue-600 text-white p-3 rounded-s-lg rounded-be-lg">
              <p class="text-sm">Lorem ipsum dolor sit amet.</p>
            </div>
            <span class="text-xs text-gray-500 leading-none">2 min ago</span>
          </div>
          <div class="flex-shrink-0 h-10 w-10 rounded-full bg-gray-300"></div>
        </div>
        <div class="flex w-full mt-2 space-x-3 max-w-xs">
          <div class="flex-shrink-0 h-10 w-10 rounded-full bg-gray-300"></div>
          <div>
            <div class="bg-gray-300 p-3 rounded-e-lg rounded-bs-lg">
              <p class="text-sm">
                Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do
                eiusmod tempor incididunt ut labore et dolore magna aliqua.
              </p>
            </div>
            <span class="text-xs text-gray-500 leading-none">2 min ago</span>
          </div>
        </div>
        <div class="flex w-full mt-2 space-x-3 max-w-xs ml-auto justify-end">
          <div>
            <div class="bg-blue-600 text-white p-3 rounded-s-lg rounded-be-lg">
              <p class="text-sm">Lorem ipsum dolor sit.</p>
            </div>
            <span class="text-xs text-gray-500 leading-none">2 min ago</span>
          </div>
          <div class="flex-shrink-0 h-10 w-10 rounded-full bg-gray-300"></div>
        </div>
      </div>

      <div class="bg-gray-300 p-4">
        <input
          class="flex items-center h-10 w-full rounded px-3 text-sm"
          type="text"
          placeholder="Type your message…"
          v-model="message"
          @keyup.enter="getModelOutput"
        />
      </div>
    </div>
    <!-- Component End  -->
  </div>
</template>


<style lang="scss" scoped>
</style>
