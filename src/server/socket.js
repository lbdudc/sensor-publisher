import { Server as SocketIOServer } from "socket.io";

export function initializeSocket(server, cors) {
  const io = new SocketIOServer(server, {
    cors: cors,
  });

  io.on("connection", (socket) => {
    console.log("a user connected");

    socket.on("disconnect", () => {
      console.log("user disconnected");
    });

    socket.on("message", (msg) => {
      console.log("message: " + msg);
    });

    setInterval(() => {
      socket.emit("message", "Hello from server");
    }, 1000);
  });
}
