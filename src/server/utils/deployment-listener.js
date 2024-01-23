import { spawn } from "child_process";

const DEPLOYMENT_COMMAND = "npm run deploy";

class DeploymentListener {
  constructor(socket) {
    this.socket = socket;

    // Event listener for capturing stdout data
    this.stdoutListener = (data) => {
      const logMessage = data.toString();
      this.socket.emit("deploying-message", logMessage);
    };
  }

  startDeployment() {
    // Command to start the deployment process (replace this with your actual command)
    const deploymentCommand = DEPLOYMENT_COMMAND;

    // Spawn the child process
    const deploymentProcess = spawn(deploymentCommand, { shell: true });

    // Attach the stdout listener to capture real-time output
    deploymentProcess.stdout.on("data", this.stdoutListener);

    // Handle process exit
    deploymentProcess.on("exit", (code) => {
      console.log(`Deployment process exited with code ${code}`);
      if (code === 0) {
        this.socket.emit("deploying-success", `Deploying finished!`);
      } else {
        this.socket.emit(
          "deploying-error",
          `Deploying finished with code ${code}`
        );
      }
      this.stopDeployment();
    });
  }

  stopDeployment() {
    // Close the log stream
    this.socket.emit("deploying-success", `Deploying finished!`);
  }
}

export default DeploymentListener;
