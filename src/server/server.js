import { DerivationEngine, readJsonFromFile, readFile } from "spl-js-engine";
import express from "express";
import zipFolder from "../server/utils/zip-utils.js";
import { rmFolder, awaitCreation } from "./utils/folder-utils.js";
import { getConfig } from "./utils/config-utils.js";
import cors from "cors";
import fs from "fs";

const CLIENT_URL = process.env.CLIENT_URL || "http://localhost:5173";
const PORT = process.env.PORT || 3000;

const CONFIG_FILE_PATH = "config.json";
const PRODUCT_FOLDER = "output";
const OUTPUT_PATH_FILE = "./src/server/output.zip";

/**
 * Create the express app
 */
const app = express();
app.use(
  cors({
    origin: CLIENT_URL,
    optionsSuccessStatus: 200,
  })
);
app.use(express.json());

/**
 * CREATE THE DERIVATION ENGINE
 */
const config = getConfig(CONFIG_FILE_PATH);

const engine = await new DerivationEngine({
  codePath: config.codePath,
  featureModel: readFile(config.featureModel),
  config: readJsonFromFile(config.config),
  extraJS: readFile(config.extraJS),
  modelTransformation: readFile(config.modelTransformation),
  verbose: false,
});

app.post("/api/data", async (req, res) => {
  console.log(`POST http://localhost:${PORT}/api/data`);

  try {
    const requestData = req.body;

    if (!requestData || typeof requestData !== "object") {
      console.error({ error: "Invalid request data", requestData });
      return res.status(400).send("Invalid request data");
    }

    await engine.generateProduct(PRODUCT_FOLDER, requestData);

    // Await for the folders to be trully created
    await awaitCreation(
      [`${PRODUCT_FOLDER}/client`, `${PRODUCT_FOLDER}/server`],
      500
    );

    // Zip the folders
    zipFolder(PRODUCT_FOLDER, OUTPUT_PATH_FILE)
      .then(async () => {
        await new Promise((resolve) => setTimeout(resolve, 500));

        // Send the zip filem then delete it
        res.download(OUTPUT_PATH_FILE, "output.zip", (err) => {
          if (err) console.error("Error sending file:", err);

          // Delete files and folders
          fs.unlinkSync(OUTPUT_PATH_FILE);
          rmFolder(PRODUCT_FOLDER);
        });
      })
      .catch((err) => {
        console.error("Error zipping files:", err);
        res.status(500).send("Error zipping files");
      });
  } catch (error) {
    console.error(error);
    res.status(500).send("Error");
  }
});

app.get("/api/features", (req, res) => {
  console.log(`GET http://localhost:${PORT}/api/features`);

  try {
    const features = engine.featureModel.getFeatures();
    res.send(features);
  } catch (error) {
    console.error(error);
    res.status(500).send("Error getting features from feature model");
  }
});

app.listen(PORT, () => {
  console.log(`Example app listening at http://localhost:${PORT}`);
});
