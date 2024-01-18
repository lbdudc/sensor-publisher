<script setup>
import io from "socket.io-client";
import { onMounted, onUnmounted, reactive, ref } from "vue";

const SERVER_URL = `http://localhost:${
  import.meta.env.VITE_SERVER_PORT || 3000
}`;

const socket = io(SERVER_URL);

defineEmits(["close"]);

defineProps({
  spec: {
    type: Object,
    required: true,
  },
});

// Panel variables
const panelSelected = ref([0]);

// Deployment variables
const deploymentType = ref("local");
const deploymentItems = ["local", "SSH", "AWS"];
const showPass = ref(false);

const localDeployVar = reactive({
  host: "http://localhost:9000",
  port: "9000",
});

const sshDeployVar = reactive({
  host: "http://localhost:9000",
  port: "9000",
  username: "root",
  password: "password",
  certRoute: "/home/user/.ssh/key_rsa",
  remoteRepoPath: "/home/user/code",
});

const awsDeployVar = reactive({
  AWS_ACCESS_KEY_ID: "AKIAJY2Q...",
  AWS_SECRET_ACCESS_KEY: "X8Y4X0...",
  AWS_REGION: "eu-west-2",
  AWS_AMI_ID: "ami-08b064b1296caf3b2",
  AWS_INSTANCE_TYPE: "t2.micro",
  AWS_INSTANCE_NAME: "my-aws-instance",
  AWS_SECURITY_GROUP_ID: "sg-0a1b2c3d4e5f6a7b8",
  AWS_KEY_NAME: "mykey",
  AWS_USERNAME: "ec2-user",
  AWS_SSH_PRIVATE_KEY_PATH: "user/.ssh/mykey.pem",
  REMOTE_REPO_PATH: "/home/ec2-user/code",
});

// Websocket logic
onMounted(() => {
  socket.on("connect", () => {
    console.log("connected");
  });

  socket.on("message", (data) => {
    console.log(data);
  });
});

onUnmounted(() => {
  socket.close();
});
</script>


<template>
  <v-card v-if="spec != null">
    <v-toolbar>
      <div
        class="w-full h-full flex flex-row items-center text-white bg-gradient-to-r from-indigo-500 to-blue-500 dark:from-purple-800 dark:to-indigo-900"
      >
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
          class="mx-4"
        >
          <rect width="16" height="20" x="4" y="2" rx="2" ry="2"></rect>
          <path d="M9 22v-4h6v4"></path>
          <path d="M8 6h.01"></path>
          <path d="M16 6h.01"></path>
          <path d="M12 6h.01"></path>
          <path d="M12 10h.01"></path>
          <path d="M12 14h.01"></path>
          <path d="M16 10h.01"></path>
          <path d="M16 14h.01"></path>
          ç
          <path d="M8 10h.01"></path>
          <path d="M8 14h.01"></path>
        </svg>
        <v-toolbar-title>
          <h1 class="text-left text-xl font-bold">Product Deployment</h1>
        </v-toolbar-title>
        <v-spacer></v-spacer>
        <v-btn
          class="mr-4"
          size="small"
          icon="mdi-close"
          @click="$emit('close')"
        >
        </v-btn>
      </div>
    </v-toolbar>

    <v-card-text class="max-h-screen overflow-y-auto">
      <v-expansion-panels v-model="panelSelected">
        <v-expansion-panel class="pa-0 ma-0" key="0">
          <v-expansion-panel-title
            class="text-white font-bold text-xl bg-gray-400"
          >
            <v-icon class="pr-4">mdi-cog</v-icon>
            Options</v-expansion-panel-title
          >
          <v-expansion-panel-text class="bg-gray-100/20">
            <v-container class="ma-0 pa-0 w-full" fluid>
              <v-row no-gutters justify="center">
                <v-col cols="8">
                  <v-select
                    class="mt-4 mb-0"
                    variant="outlined"
                    :items="deploymentItems"
                    label="Deployment Type"
                    v-model="deploymentType"
                  ></v-select>
                </v-col>
              </v-row>

              <!-- IF deploymentType == "local" -->
              <v-row
                no-gutters
                justify="space-between"
                v-if="deploymentType == 'local'"
              >
                <v-col cols="12" md="6" class="pr-4">
                  <v-text-field
                    v-model="localDeployVar.host"
                    variant="solo"
                    label="Host"
                    placeholder="http://localhost:9000"
                  >
                  </v-text-field>
                </v-col>
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="localDeployVar.port"
                    variant="solo"
                    label="Port"
                    placeholder="9000"
                  >
                  </v-text-field>
                </v-col>
              </v-row>

              <!-- IF deploymentType == "SSH" -->
              <v-row no-gutters v-if="deploymentType == 'SSH'">
                <v-col cols="12" md="6" class="pr-4">
                  <v-text-field
                    v-model="sshDeployVar.host"
                    label="Host"
                    variant="solo"
                    placeholder="http://localhost:9000"
                  >
                  </v-text-field>
                </v-col>
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="sshDeployVar.port"
                    label="Port"
                    variant="solo"
                    placeholder="9000"
                  >
                  </v-text-field>
                </v-col>
                <v-col cols="12" md="6" class="pr-4">
                  <v-text-field
                    v-model="sshDeployVar.username"
                    label="Username"
                    variant="solo"
                    placeholder="root"
                  >
                  </v-text-field>
                </v-col>
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="sshDeployVar.password"
                    label="Password"
                    placeholder="password"
                    variant="solo"
                    :type="showPass ? 'text' : 'password'"
                    :append-icon="showPass ? 'mdi-eye' : 'mdi-eye-off'"
                    @click:append="showPass = !showPass"
                  >
                  </v-text-field>
                </v-col>
                <v-col cols="12" md="6" class="pr-4">
                  <v-text-field
                    v-model="sshDeployVar.certRoute"
                    label="Certificate Route"
                    variant="solo"
                    placeholder="/home/user/.ssh/key_rsa"
                  >
                  </v-text-field>
                </v-col>
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="sshDeployVar.remoteRepoPath"
                    label="Remote Repository Path"
                    variant="solo"
                    placeholder="/home/user/code"
                  >
                  </v-text-field>
                </v-col>
              </v-row>

              <!-- IF deploymentType == "AWS" -->
              <v-row no-gutters v-if="deploymentType == 'AWS'" justify="center">
                <v-col cols="12" md="6" class="pr-4">
                  <v-text-field
                    v-model="awsDeployVar.AWS_ACCESS_KEY_ID"
                    label="AWS_ACCESS_KEY_ID"
                    variant="solo"
                    placeholder="AKIAJY2Q..."
                  >
                  </v-text-field>
                </v-col>
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="awsDeployVar.AWS_SECRET_ACCESS_KEY"
                    label="AWS_SECRET_ACCESS_KEY"
                    variant="solo"
                    placeholder="X8Y4X0..."
                  >
                  </v-text-field>
                </v-col>
                <v-col cols="12" md="6" class="pr-4">
                  <v-text-field
                    v-model="awsDeployVar.AWS_REGION"
                    label="AWS_REGION"
                    variant="solo"
                    placeholder="eu-west-2"
                  >
                  </v-text-field>
                </v-col>

                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="awsDeployVar.AWS_AMI_ID"
                    label="AWS_AMI_ID"
                    variant="solo"
                    placeholder="ami-08b064b1296caf3b2"
                  >
                  </v-text-field>
                </v-col>

                <v-col cols="12" md="6" class="pr-4">
                  <v-text-field
                    v-model="awsDeployVar.AWS_INSTANCE_TYPE"
                    label="AWS_INSTANCE_TYPE"
                    variant="solo"
                    placeholder="t2.micro"
                  >
                  </v-text-field>
                </v-col>
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="awsDeployVar.AWS_INSTANCE_NAME"
                    label="AWS_INSTANCE_NAME"
                    variant="solo"
                    placeholder="my-aws-instance"
                  >
                  </v-text-field>
                </v-col>

                <v-col cols="12" md="6" class="pr-4">
                  <v-text-field
                    v-model="awsDeployVar.AWS_SECURITY_GROUP_ID"
                    label="AWS_SECURITY_GROUP_ID"
                    variant="solo"
                    placeholder="sg-0a1b2c3d4e5f6a7b8"
                  >
                  </v-text-field>
                </v-col>

                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="awsDeployVar.AWS_KEY_NAME"
                    label="AWS_KEY_NAME"
                    variant="solo"
                    placeholder="mykey"
                  >
                  </v-text-field>
                </v-col>

                <v-col cols="12" md="6" class="pr-4">
                  <v-text-field
                    v-model="awsDeployVar.AWS_USERNAME"
                    label="AWS_USERNAME"
                    variant="solo"
                    placeholder="ec2-user"
                  >
                  </v-text-field>
                </v-col>

                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="awsDeployVar.AWS_SSH_PRIVATE_KEY_PATH"
                    label="AWS_SSH_PRIVATE_KEY_PATH"
                    variant="solo"
                    placeholder="user/.ssh/mykey.pem"
                  >
                  </v-text-field>
                </v-col>

                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="awsDeployVar.REMOTE_REPO_PATH"
                    label="REMOTE_REPO_PATH"
                    variant="solo"
                    placeholder="/home/ec2-user/code"
                  >
                  </v-text-field>
                </v-col>
              </v-row>
            </v-container>
          </v-expansion-panel-text>
        </v-expansion-panel>

        <v-expansion-panel key="1">
          <v-expansion-panel-title
            class="bg-gray-800 text-white font-bold text-xl"
          >
            <v-icon class="pr-4">mdi-console-line</v-icon>
            Console</v-expansion-panel-title
          >
          <!-- // get half of the screen -->
          <v-expansion-panel-text class="min-h-[50vh] w-[95vw] mx-auto">
            <v-container
              class="h-full w-full flex flex-col bg-gray-800 text-white p-6 rounded-md max-w-2xl mx-auto font-mono text-sm overflow-auto max-h-[70vh]"
            >
              <span>holis</span>
            </v-container>
          </v-expansion-panel-text>
        </v-expansion-panel>
      </v-expansion-panels>
    </v-card-text>
  </v-card>
</template>


