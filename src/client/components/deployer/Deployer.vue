<script setup>
import io from "socket.io-client";
import { onMounted, onUnmounted, reactive, ref, toRaw, watch } from "vue";
import ExpansionPanels from "./InformationPanels.vue";

const SERVER_URL = `http://localhost:${
  import.meta.env.VITE_SERVER_PORT || 3000
}`;

console.log(SERVER_URL);

const socket = io(SERVER_URL);

defineEmits(["close"]);

const props = defineProps({
  spec: {
    type: Object,
    required: true,
  },
});

// Panel variables
const panelSelected = ref(0);
const loadingDeployment = ref(false);
const toggle = ref(0);
const showConfigurationPanel = ref(true);
// const deploymentPhasePanel = ref([0]);

// Deploymennt logger
const configurationLogger = ref([]);
const derivationLogger = ref([]);
const deploymentLogger = ref([]);

const passedPhases = ref([]);
const deploymentStatusPhases = reactive({
  config: {
    status: "iddle",
    msg: "",
  },
  derivation: {
    status: "iddle",
    msg: "",
  },
  deployment: {
    status: "iddle",
    msg: "",
  },
});

// Deployment variables
const deploymentType = ref("local");
const deploymentItems = ["local", "ssh", "aws"];
const deployDataSel = ref({});
const showPass = ref(false);

const initialDataObj = {
  local: {
    host: "http://localhost:80",
    port: "80",
  },
  ssh: {
    host: "http://<ip>:<port>",
    port: "80",
    username: "root",
    password: "password",
    certRoute: "/home/user/.ssh/key_rsa",
    remoteRepoPath: "/home/user/code",
  },
  aws: {
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
  },
};

const deployData = reactive({
  ...initialDataObj,
});

const resetDeployment = () => {
  loadingDeployment.value = false;
  toggle.value = 0;
};

const retryDeployment = () => {
  loadingDeployment.value = false;
  toggle.value = 0;
  passedPhases.value = [];
  deploymentStatusPhases["config"].status = "iddle";
  deploymentStatusPhases["derivation"].status = "iddle";
  deploymentStatusPhases["deployment"].status = "iddle";
  configurationLogger.value = [];
  derivationLogger.value = [];
  deploymentLogger.value = [];
};

const deployProduct = () => {
  retryDeployment();
  showConfigurationPanel.value = false;
  loadingDeployment.value = true;

  // open accordion panel in console
  panelSelected.value = 1;
  toggle.value = 1;

  const deployDataComp = {
    config: {
      type: deploymentType.value,
      ...deployData[deploymentType.value],
    },
    spec: props.spec,
  };

  deployDataSel.value = deployDataComp;

  socket.emit("start-deploy", deployDataComp);
  passedPhases.value.push("config");
};

const cancelDeploy = () => {
  socket.emit("cancel-deploy");
  resetDeployment();
};

// Websocket logic
onMounted(() => {
  setDataObject();
  setDeploymentType();

  socket.on("connect", () => {
    console.log("connected");
  });

  // PARSING CONFIG PHASE
  socket.on("parsing-config-error", (data) => {
    deploymentStatusPhases["config"].status = "error";
    deploymentStatusPhases["config"].msg = data;
    configurationLogger.value.push({
      id: Date.now(),
      timestamp: new Date().toLocaleTimeString(),
      text: data,
    });
    resetDeployment();
  });

  socket.on("parsing-config-success", (data) => {
    deploymentStatusPhases["config"].status = "success";
    deploymentStatusPhases["config"].msg = data;
    configurationLogger.value.push({
      id: Date.now(),
      timestamp: new Date().toLocaleTimeString(),
      text: data,
    });
  });

  socket.on("parsing-config-msg", (data) => {
    panelSelected.value = 0;
    configurationLogger.value.push({
      id: Date.now(),
      timestamp: new Date().toLocaleTimeString(),
      text: data,
      isPre: true,
    });
  });

  // DERIVATION ENGINE PHASE
  socket.on("derivating-message", (data) => {
    panelSelected.value = 1;
    passedPhases.value.push("derivation");
    derivationLogger.value.push({
      id: Date.now(),
      timestamp: new Date().toLocaleTimeString(),
      text: data,
    });
  });

  socket.on("derivating-error", (data) => {
    deploymentStatusPhases["derivation"].status = "error";
    deploymentStatusPhases["derivation"].msg = data;
    derivationLogger.value.push({
      id: Date.now(),
      timestamp: new Date().toLocaleTimeString(),
      text: "Derivation process finished with errors: " + data,
    });
    resetDeployment();
  });

  socket.on("derivating-success", (data) => {
    deploymentStatusPhases["derivation"].status = "success";
    deploymentStatusPhases["derivation"].msg = data;
    derivationLogger.value.push({
      id: Date.now(),
      timestamp: new Date().toLocaleTimeString(),
      text: "Derivation process finished " + data,
    });
  });

  // DEPLOYMENT ENGINE PHASE
  socket.on("deploying-message", (data) => {
    panelSelected.value = 2;
    passedPhases.value.push("deployment");
    deploymentLogger.value.push({
      id: Date.now(),
      timestamp: new Date().toLocaleTimeString(),
      text: data,
    });
  });

  socket.on("deploying-error", (data) => {
    deploymentStatusPhases["deployment"].status = "error";
    deploymentStatusPhases["deployment"].msg = data;
    deploymentLogger.value.push({
      id: Date.now(),
      timestamp: new Date().toLocaleTimeString(),
      text: "Deployment process finished with errors: " + data,
    });

    resetDeployment();
  });

  socket.on("deploying-success", (data) => {
    panelSelected.value = null;
    deploymentStatusPhases["deployment"].status = "success";
    deploymentStatusPhases["deployment"].msg = data;
    deploymentLogger.value.push({
      id: Date.now(),
      timestamp: new Date().toLocaleTimeString(),
      text: "Deployment process finished " + data,
    });

    resetDeployment();
  });

  socket.on("deploying-cancelled", (data) => {
    panelSelected.value = null;
    deploymentStatusPhases["deployment"].status = "error";
    deploymentStatusPhases["deployment"].msg = data;
    deploymentLogger.value.push({
      id: Date.now(),
      timestamp: new Date().toLocaleTimeString(),
      text: "Deployment process cancelled " + data,
    });
    resetDeployment();
  });
});

onUnmounted(() => {
  socket.close();
});

// Persist data in LocalStorage
const setDataObject = () => {
  const deployDataStore = localStorage.getItem("deployData");
  if (deployDataStore) {
    const deployDataParsed = JSON.parse(deployDataStore);
    Object.keys(deployDataParsed).forEach((key) => {
      deployData[key] = deployDataParsed[key];
    });
  } else {
    localStorage.setItem("deployData", JSON.stringify(toRaw(deployData)));
  }
};

watch(deployData, (newVal) => {
  localStorage.setItem("deployData", JSON.stringify(newVal));
});

// Persist preferred deployment type
const setDeploymentType = (newVal) => {
  if (newVal) {
    localStorage.setItem("deployType", newVal);
    deploymentType.value = newVal;
    return;
  }

  const deployTypeStore = localStorage.getItem("deployType");
  if (deployTypeStore) {
    deploymentType.value = deployTypeStore;
  } else {
    localStorage.setItem("deployType", deploymentType.value);
  }
};
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
        <v-toolbar-title @click="$emit('close')" class="cursor-pointer">
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

    <v-card-text class="max-h-screen overflow-hidden">
      <v-row no-gutters justify="center" align="center">
        <v-col cols="12" md="6" class="pl-1">
          <v-btn
            prepend-icon="mdi-cog"
            :append-icon="
              showConfigurationPanel ? 'mdi-chevron-up' : 'mdi-chevron-down'
            "
            color="#6366f1"
            @click="showConfigurationPanel = !showConfigurationPanel"
            >Configuration</v-btn
          >
        </v-col>
        <v-col cols="12" md="6" class="text-end pr-1">
          <v-btn-toggle v-model="toggle" divided>
            <v-btn
              append-icon="mdi-rocket"
              color="#349157"
              @click="deployProduct"
              :disabled="loadingDeployment"
              :loading="loadingDeployment"
            >
              Deploy
            </v-btn>
            <v-btn
              append-icon="mdi-cancel"
              color="red-darken-2"
              @click="cancelDeploy"
              :disabled="!loadingDeployment"
            >
              Cancel
            </v-btn>
          </v-btn-toggle>
        </v-col>
      </v-row>

      <!-- CONFIGURATION PANEL -->
      <Transition>
        <v-row v-if="showConfigurationPanel">
          <v-card
            color="#6366f1"
            class="w-full mx-4"
            title="Deployment Configuration"
            variant="outlined"
          >
            <v-card-text>
              <v-container class="w-full bg-gray-00/70" fluid>
                <v-row no-gutters justify="center" align="center" class="mb-6">
                  <v-col cols="12" md="6">
                    <v-select
                      class="mt-4 mb-0"
                      variant="outlined"
                      :items="deploymentItems"
                      label="Deployment Type"
                      v-model="deploymentType"
                      @update:modelValue="setDeploymentType"
                    ></v-select>
                  </v-col>
                  <v-col
                    cols="12"
                    md="5"
                    offset-md="1"
                    class="d-flex align-center flex-column pb-2"
                  >
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
                      v-model="deployData['local'].host"
                      variant="solo"
                      label="Host"
                      placeholder="http://localhost:9000"
                    >
                    </v-text-field>
                  </v-col>
                  <v-col cols="12" md="6">
                    <v-text-field
                      v-model="deployData['local'].port"
                      variant="solo"
                      label="Port"
                      placeholder="9000"
                    >
                    </v-text-field>
                  </v-col>
                </v-row>

                <!-- IF deploymentType == "SSH" -->
                <v-row no-gutters v-if="deploymentType == 'ssh'">
                  <v-col cols="12" md="6" class="pr-4">
                    <v-text-field
                      v-model="deployData['ssh'].host"
                      label="Host"
                      variant="solo"
                      placeholder="http://localhost:9000"
                    >
                    </v-text-field>
                  </v-col>
                  <v-col cols="12" md="6">
                    <v-text-field
                      v-model="deployData['ssh'].port"
                      label="Port"
                      variant="solo"
                      placeholder="9000"
                    >
                    </v-text-field>
                  </v-col>
                  <v-col cols="12" md="6" class="pr-4">
                    <v-text-field
                      v-model="deployData['ssh'].username"
                      label="Username"
                      variant="solo"
                      placeholder="root"
                    >
                    </v-text-field>
                  </v-col>
                  <v-col cols="12" md="6">
                    <v-text-field
                      v-model="deployData['ssh'].password"
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
                      v-model="deployData['ssh'].certRoute"
                      label="Certificate Route"
                      variant="solo"
                      placeholder="/home/user/.ssh/key_rsa"
                    >
                    </v-text-field>
                  </v-col>
                  <v-col cols="12" md="6">
                    <v-text-field
                      v-model="deployData['ssh'].remoteRepoPath"
                      label="Remote Repository Path"
                      variant="solo"
                      placeholder="/home/user/code"
                    >
                    </v-text-field>
                  </v-col>
                </v-row>

                <!-- IF deploymentType == "AWS" -->
                <v-row
                  no-gutters
                  v-if="deploymentType == 'aws'"
                  justify="center"
                >
                  <v-col cols="12" md="6" class="pr-4">
                    <v-text-field
                      v-model="deployData['aws'].AWS_ACCESS_KEY_ID"
                      label="AWS_ACCESS_KEY_ID"
                      variant="solo"
                      placeholder="AKIAJY2Q..."
                    >
                    </v-text-field>
                  </v-col>
                  <v-col cols="12" md="6">
                    <v-text-field
                      v-model="deployData['aws'].AWS_SECRET_ACCESS_KEY"
                      label="AWS_SECRET_ACCESS_KEY"
                      variant="solo"
                      placeholder="X8Y4X0..."
                    >
                    </v-text-field>
                  </v-col>
                  <v-col cols="12" md="6" class="pr-4">
                    <v-text-field
                      v-model="deployData['aws'].AWS_REGION"
                      label="AWS_REGION"
                      variant="solo"
                      placeholder="eu-west-2"
                    >
                    </v-text-field>
                  </v-col>

                  <v-col cols="12" md="6">
                    <v-text-field
                      v-model="deployData['aws'].AWS_AMI_ID"
                      label="AWS_AMI_ID"
                      variant="solo"
                      placeholder="ami-08b064b1296caf3b2"
                    >
                    </v-text-field>
                  </v-col>

                  <v-col cols="12" md="6" class="pr-4">
                    <v-text-field
                      v-model="deployData['aws'].AWS_INSTANCE_TYPE"
                      label="AWS_INSTANCE_TYPE"
                      variant="solo"
                      placeholder="t2.micro"
                    >
                    </v-text-field>
                  </v-col>
                  <v-col cols="12" md="6">
                    <v-text-field
                      v-model="deployData['aws'].AWS_INSTANCE_NAME"
                      label="AWS_INSTANCE_NAME"
                      variant="solo"
                      placeholder="my-aws-instance"
                    >
                    </v-text-field>
                  </v-col>

                  <v-col cols="12" md="6" class="pr-4">
                    <v-text-field
                      v-model="deployData['aws'].AWS_SECURITY_GROUP_ID"
                      label="AWS_SECURITY_GROUP_ID"
                      variant="solo"
                      placeholder="sg-0a1b2c3d4e5f6a7b8"
                    >
                    </v-text-field>
                  </v-col>

                  <v-col cols="12" md="6">
                    <v-text-field
                      v-model="deployData['aws'].AWS_KEY_NAME"
                      label="AWS_KEY_NAME"
                      variant="solo"
                      placeholder="mykey"
                    >
                    </v-text-field>
                  </v-col>

                  <v-col cols="12" md="6" class="pr-4">
                    <v-text-field
                      v-model="deployData['aws'].AWS_USERNAME"
                      label="AWS_USERNAME"
                      variant="solo"
                      placeholder="ec2-user"
                    >
                    </v-text-field>
                  </v-col>

                  <v-col cols="12" md="6">
                    <v-text-field
                      v-model="deployData['aws'].AWS_SSH_PRIVATE_KEY_PATH"
                      label="AWS_SSH_PRIVATE_KEY_PATH"
                      variant="solo"
                      placeholder="user/.ssh/mykey.pem"
                    >
                    </v-text-field>
                  </v-col>

                  <v-col cols="12" md="6">
                    <v-text-field
                      v-model="deployData['aws'].REMOTE_REPO_PATH"
                      label="REMOTE_REPO_PATH"
                      variant="solo"
                      placeholder="/home/ec2-user/code"
                    >
                    </v-text-field>
                  </v-col>
                </v-row>
              </v-container>
            </v-card-text>
          </v-card>
        </v-row>
      </Transition>

      <!-- DEPLOYMENT PANELS -->
      <ExpansionPanels
        :deploymentStatusPhases="deploymentStatusPhases"
        :configurationLogger="configurationLogger"
        :derivationLogger="derivationLogger"
        :deploymentLogger="deploymentLogger"
        :passedPhases="passedPhases"
        :panelSelected="panelSelected"
        :deployDataSel="deployDataSel"
      />
    </v-card-text>
  </v-card>
</template>

<style>
.v-expansion-panel-text__wrapper {
  padding: 0 !important;
  margin: 0 !important;
}

.v-enter-active,
.v-leave-active {
  transition: opacity 0.2s ease-in-out;
}

.v-enter-from,
.v-leave-to {
  opacity: 0;
}
</style>
