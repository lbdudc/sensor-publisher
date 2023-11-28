import "./init.js";
import { DerivationEngine, readJsonFromFile, readFile } from "spl-js-engine";
import { setDatabaseConfig, setElasticsearchConfig } from "./config-util.js";
// import {
//   Uploader,
//   DebianUploadStrategy,
//   AWSUploadStrategy,
//   LocalUploadStrategy,
// } from "code-uploader";
// import path from "path";
// import {
//   createBaseDSLInstance
// } from "./dsl-util.js";
import sensordslParser from "sensor-dsl";
import fs from "fs";

const DEBUG = process.env.DEBUG;

export default class SensorBuilder {
  constructor(config) {
    this.config = config;
    setDatabaseConfig(this.config);
    setElasticsearchConfig(this.config);
  }

  async run(dslSpec, onlyGenerate, onlyDeploy) {

    if (onlyDeploy) {
      //   await this.deploy();
      return;
    }

    const json = sensordslParser(dslSpec);

    // Add postgres and elastic search to the config
    json.data.basicData.database = setDatabaseConfig(this.config);
    json.data.basicData.elastic = setElasticsearchConfig(this.config);

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

    engine.generateProduct("output", readJsonFromFile("spec.json"));

    if (!onlyGenerate) {
      //   await this.deploy();
    }
  }

  // async deploy() {
  //   const uploader = new Uploader();

  //   const strategies = {
  //     ssh: new DebianUploadStrategy(),
  //     aws: new AWSUploadStrategy(),
  //     local: new LocalUploadStrategy(),
  //   };

  //   uploader.setUploadStrategy(
  //     strategies[this.config.deploy.type] || strategies.local
  //   );

  //   let deployConf = this.config.deploy;

  //   deployConf.repoPath = "output";

  //   // Upload and deploy code
  //   await uploader.uploadCode(deployConf);
  // }
}
