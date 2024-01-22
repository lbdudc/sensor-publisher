import fs from "fs";
import {
  Uploader,
  DebianUploadStrategy,
  AWSUploadStrategy,
  LocalUploadStrategy,
} from "code-uploader";

const PRODUCT_FOLDER = "output";

class Deployer {
  constructor() {
    const config = JSON.parse(fs.readFileSync("temp_config.json", "utf8"));
    this.config = config;
    this.route = PRODUCT_FOLDER;
  }

  setConfig(config) {
    this.config = config;
  }

  async deploy() {
    const uploader = new Uploader();

    const strategies = {
      ssh: new DebianUploadStrategy(),
      aws: new AWSUploadStrategy(),
      local: new LocalUploadStrategy(),
    };

    uploader.setUploadStrategy(
      strategies[this.config.type] || strategies.local
    );

    let deployConf = this.config;
    deployConf.repoPath = this.route;

    await uploader.uploadCode(deployConf);
  }
}

const deployer = new Deployer();
deployer.deploy();
