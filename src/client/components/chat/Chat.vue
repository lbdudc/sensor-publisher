<script setup>
  import { ref, reactive } from "vue";

  const SERVER_URL = `${
    import.meta.env.VITE_SERVER_URL || "http://localhost:5000"
  }/api`;
  let message = ref("");

  const loadingResponse = ref(false);
  let conversation = reactive([]);
  let nTokensConversation = 0;

  // Counts the approximate number of tokens in the input while adding the total number for the whole conversation
  async function tokenCounter(text) {
    const words = text.split(/\s+/);
    const nWords = words.length;
    const tokenCount = nWords*2 + nTokensConversation; // The number of words is doubled to take into account the possible response as well

    return tokenCount;
  }

  const getModelOutput = async () => {

    if (loadingResponse.value) return

    try {
      const userMessage = message.value;

      // Add the new message to the chat messages array
      if (userMessage != "") {
        conversation.push({
          text: userMessage,
          timestamp: new Date().toLocaleTimeString(),
          isCurrentUser: true // You may modify this condition based on your logic
        });
        // Clear the input after sending the message
        message.value = "";
      }

      // Check that the amount of tokens does not exceed the maximum number of 4096 (as the counting is approximate we use 4000 instead)
      if (tokenCounter(userMessage) > 4000) {
        console.log("The amount of tokens in the conversation will exceed the maximum limit for the specified model. You need to start a new conversation. To do so, just write \"Reset\".");
        process.exit(1);
      } 

      loadingResponse.value= true;
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
        loadingResponse.value= false;
        throw new Error(JSON.stringify(JSON.parse(error), null, 4));
      }
      

      // Get response and update conversation and number of tokens
      const openAIData = await response.json();
      nTokensConversation = openAIData.nTokens;
      // Extract the last message from the server's response
      const lastMessage = openAIData.messagesHistory.slice(-1)[0];
      console.log(lastMessage);

      // Add the last message to the conversation
      if (lastMessage) {
        conversation.push({
          text: lastMessage.content,
          timestamp: new Date().toLocaleTimeString(),
          isCurrentUser: false // Set this to false to distinguish from user's messages
        });
      }

      loadingResponse.value= false;

      console.log(openAIData);
    } catch (error) {
      throw new Error(error);
    }
  };


  const copytoClipboard = (text) => {
  // navigator.clipboard.
  }
</script>

<template>
  <div class="flex flex-col items-center justify-center w-full h-full">
    <!-- Component Start -->
    <div
      class="flex flex-col flex-grow w-full max-w-xl bg-white shadow-xl rounded-lg overflow-hidden"
    >
      <div class="flex flex-col flex-grow h-0 p-4 overflow-auto">
        <!-- Render chat messages dynamically -->
        <div v-for="(message, index) in conversation" :key="index" :class="{ 'ml-auto justify-end': message.isCurrentUser }" class="flex w-full mt-2 space-x-3 max-w-xs">
          <div v-if="!message.isCurrentUser" class="flex-shrink-0 h-10 w-10 rounded-full bg-gray-200 items-center justify-center flex">
            <!-- Image centered -->
            <img src="/codellamaicon.png" width="30" height="30" alt="" />
          </div>
          <div :class="{ 'ml-0': !message.isCurrentUser, 'mr-0': message.isCurrentUser }">
            <div :class="{ 'bg-blue-600 text-white align-end': message.isCurrentUser, 'bg-gray-300': !message.isCurrentUser }" class="p-3 rounded-lg">
              <p class="text-sm">{{ message.text }}</p>
            </div>
            <span @click="copytoClipboard(message.text)" class="text-xs text-gray-500 leading-none">{{ message.timestamp }}</span>
          </div>
          <div v-if="message.isCurrentUser" class="flex-shrink-0 h-10 w-10 rounded-full bg-gray-300"></div>
        </div>
      </div>

      <div v-if="loadingResponse" class="flex flex-col items-center mb-3">
        <v-progress-circular indeterminate></v-progress-circular>
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
