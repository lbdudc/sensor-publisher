import { spawn } from "child_process";

const DEPLOYMENT_COMMAND = "npm run deploy";

class DeploymentListener {
  constructor(socket) {
    this.socket = socket;
    this.deploymentProcess = null;

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
    this.deploymentProcess = spawn(deploymentCommand, { shell: true });

    // Attach the stdout listener to capture real-time output
    this.deploymentProcess.stdout.on("data", this.stdoutListener);

    // Handle process exit
    this.deploymentProcess.on("exit", (code) => {
      console.log(`Deployment process exited with code ${code}`);
      if (code === 0) {
        this.socket.emit("deploying-success", `Deploying finished!`);
      } else {
        this.socket.emit(
          "deploying-error",
          `Deploying finished with code ${code}`
        );
      }
    });
  }

  cancelDeployment() {
    this.deploymentProcess.kill();
  }
}

export default DeploymentListener;
