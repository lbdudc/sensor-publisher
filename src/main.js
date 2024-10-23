import { DerivationEngine, readJsonFromFile, readFile } from "spl-js-engine";
import { setDatabaseConfig } from "./config-util.js";
import {
  Uploader,
  DebianUploadStrategy,
  AWSUploadStrategy,
  LocalUploadStrategy,
} from "code-uploader";
// import path from "path";
// import {
//   createBaseDSLInstance
// } from "./dsl-util.js";
import sensordslParser from "@lbdudc/sensor-dsl";
import fs from "fs";

const DEBUG = process.env.DEBUG;

export default class SensorBuilder {
  constructor(config) {
    this.config = config;
    setDatabaseConfig(this.config);
  }

  async run(dslSpec, onlyGenerate, onlyDeploy, customFm) {
    if (onlyDeploy) {
      await this.deploy();
      return;
    }

    const json = sensordslParser.parse(dslSpec);

    // Add custom feature model selection
    if (customFm != null) {
      json.features = customFm;
    }

    // Add postgres search to the config
    json.data.basicData.database = setDatabaseConfig(this.config);

    if (DEBUG) {
      fs.writeFileSync("spec.dsl", dslSpec, "utf-8");
    }

    fs.writeFileSync("spec.json", JSON.stringify(json, null, 2), "utf-8");

    const engine = await new DerivationEngine({
      codePath: this.config.platform.codePath,
      featureModel: readFile(this.config.platform.featureModel),
      config: readJsonFromFile(this.config.platform.config),
      extraJS: readFile(this.config.platform.extraJS),
      modelTransformation: readFile(this.config.platform.modelTransformation),
      verbose: DEBUG,
    });

    // delete old output
    if (fs.existsSync("output")) {
      fs.rmdirSync("output", { recursive: true });
    }

    engine.generateProduct("output", readJsonFromFile("spec.json"));

    if (!onlyGenerate) {
      await this.deploy();
    }
  }

  async deploy() {
    const uploader = new Uploader();

    const strategies = {
      ssh: new DebianUploadStrategy(),
      aws: new AWSUploadStrategy(),
      local: new LocalUploadStrategy(),
    };

    uploader.setUploadStrategy(
      strategies[this.config.deploy.type] || strategies.local
    );

    let deployConf = this.config.deploy;

    deployConf.repoPath = "output";

    if (deployConf.type && deployConf.type.toLowerCase() == "aws") {
      const ip = await uploader.createInstance(deployConf);
      deployConf.host = ip;
    }

    console.log("Deploying to", deployConf);

    // Upload and deploy code
    await uploader.uploadCode(deployConf);
  }
}
