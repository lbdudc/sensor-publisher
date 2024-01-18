import { Server as SocketIOServer } from "socket.io";
import { generateProduct } from "./utils/generator-utils.js";
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
      console.log("message: " + msg);
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
          "Error parsing configuration or spec"
        );
      }

      console.log("entra");
      console.log(config);
      try {
        await generateProduct(engine, PRODUCT_FOLDER, spec);
        socket.emit("derivating-success", "Product generated");
      } catch (error) {
        socket.emit("derivating-error", "Error generating product");
      }
      console.log("sale");

      // CALL THE DEPLOYER TO DEPLOY THE PRODUCT
      // CAN THROW AN ERROR WHEN DEPLOYING THE PRODUCT
      // IF ERROR, socket.emit("deploying-error", "Error deploying product");

      // send message to client
      socket.emit("deploying-message", "Deploying started");
    });

    // Client emits wants to cancel deployment
    socket.on("cancel-deploy", (msg) => {
      console.log("message: " + msg);
      socket.emit("deploying-cancelled", "Deploying cancelled");
    });
  });
}
