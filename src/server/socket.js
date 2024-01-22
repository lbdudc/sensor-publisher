import { Server as SocketIOServer } from "socket.io";
import { generateProduct } from "./utils/generator-utils.js";
import DeploymentListener from "./utils/deployment-listener.js";
import fs from "fs";

const PRODUCT_FOLDER = "output";

export function initializeSocket(server, cors, engine) {
  const io = new SocketIOServer(server, {
    cors: cors,
  });

  io.on("connection", (socket) => {
    console.log("Connection established with client");

    socket.on("disconnect", () => {
      console.log("Connection closed with client");
    });

    // Client emits wants to start deployment
    socket.on("start-deploy", async (msg) => {
      // Parse message
      let spec,
        config = null;
      try {
        const msgParsed = JSON.parse(JSON.stringify(msg));
        spec = msgParsed.spec;
        config = msgParsed.config;
        socket.emit("parsing-config-msg", JSON.stringify(spec, null, 2));
        socket.emit(
          "parsing-config-success",
          "Configuration parsed correctly!"
        );
      } catch (error) {
        socket.emit(
          "parsing-config-error",
          "Error parsing configuration: " + error.message
        );
      }

      // GENERATE THE PRODUCT
      socket.emit("derivating-message", "Generating product");
      try {
        await generateProduct(engine, PRODUCT_FOLDER, spec);
        socket.emit("derivating-success", "Product generated");
      } catch (error) {
        socket.emit("derivating-error", error.message);
      }

      // DEPLOY THE PRODUCT
      socket.emit("deploying-message", "Deploying started");
      try {
        await deploy(config, PRODUCT_FOLDER, socket);
      } catch (error) {
        socket.emit("deploying-error", error.message);
      }
    });

    // Client emits wants to cancel deployment
    socket.on("cancel-deploy", (msg) => {
      console.log("Cancel deploy", msg);
      socket.emit("deploying-cancelled", "Deploying cancelled");
    });
  });
}

const deploy = async (config, route, socket) => {
  const TEMP_FILE = "temp_config.json";

  // Adding the config and the route to a file to be read by the deployer
  const deployConf = {
    ...config,
    repoPath: route,
  };

  fs.writeFileSync(TEMP_FILE, JSON.stringify(deployConf, null, 2));

  socket.emit(
    "deploying-message",
    "Deploying configuration: " + JSON.stringify(deployConf, null, 2)
  );

  // Start the deployment process
  try {
    const deploymentListener = new DeploymentListener(socket);
    deploymentListener.startDeployment();
  } catch (error) {
    throw new Error("Error starting deployment: " + error.message);
  }
};
