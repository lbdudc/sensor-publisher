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
      console.log(config);

      setTimeout(() => {
        socket.emit("deploying-success", "Deploying finished");
      }, 3000);
    });

    // Client emits wants to cancel deployment
    socket.on("cancel-deploy", (msg) => {
      console.log("message: " + msg);
      socket.emit("deploying-cancelled", "Deploying cancelled");
    });
  });
}
