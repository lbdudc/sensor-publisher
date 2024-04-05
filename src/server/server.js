import { DerivationEngine, readJsonFromFile, readFile } from "spl-js-engine";
import express from "express";
import http from "http";
import { generateProduct } from "./utils/generator-utils.js";
import zipFolder from "../server/utils/zip-utils.js";
import { rmFolder, awaitCreation } from "./utils/folder-utils.js";
import { getConfig } from "./utils/config-utils.js";
import cors from "cors";
import fs from "fs";
import dotenv from "dotenv";
import { initializeSocket } from "./socket.js";
import OpenAI from "openai";

dotenv.config();
const CLIENT_URL = process.env.CLIENT_URL || "http://localhost:5173";
const PORT = process.env.SERVER_PORT || 3000;
const OPENAI_KEY = process.env.OPENAI_API_KEY;

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

// Create the http server
const server = http.createServer(app);
initializeSocket(
  server,
  {
    origin: CLIENT_URL,
    METHODS: ["GET", "POST"],
  },
  engine
);

app.post("/api/data", async (req, res) => {
  console.log(`POST http://localhost:${PORT}/api/data`);

  // remove old output
  if (fs.existsSync(PRODUCT_FOLDER)) {
    fs.rmdirSync(PRODUCT_FOLDER, { recursive: true });
  }

  try {
    const requestData = req.body;

    if (!requestData || typeof requestData !== "object") {
      console.error({ error: "Invalid request data", requestData });
      return res.status(400).send("Invalid request data");
    }

    try {
      await generateProduct(engine, PRODUCT_FOLDER, requestData);
    } catch (error) {
      console.error(error);
      return res.status(500).send(error);
    }

    // Await for the folders to be trully created
    await awaitCreation(
      [`${PRODUCT_FOLDER}/client`, `${PRODUCT_FOLDER}/server`],
      500
    );

    // Zip the folders
    zipFolder(PRODUCT_FOLDER, OUTPUT_PATH_FILE)
      .then(async () => {
        await new Promise((resolve) => setTimeout(resolve, 800));

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

app.post("/api/chat", (req, res) => {
  console.log(`POST http://localhost:${PORT}/api/chat`);

  try {
    // Initialize the variables and the context for correct model inference
    const openai = new OpenAI({
      apiKey: OPENAI_KEY,
    });
    let nTokens = 0;
    const initialContext = [
      {
        role: "system",
        content:
          "You are a helpful assistant that will provide answers converting our instructions using the grammar vocabulary and the examples that we will give you in the next two inputs.",
      },
      {
        role: "user",
        content:
          'I need you to use this grammar for a set of instructions that I will be giving you:\n\nroot ::= parse\nparse ::= sentence+\nsentence ::= createStatement\ncreateStatement ::= "CREATE " (createProduct | createRange | createDimension | createSensor)\ncreateProduct ::= "PRODUCT " identifier " USING " srid ";\\n\\n"\ncreateRange ::= "RANGE " identifier " (\\n" rangeProperty (",\\n" rangeProperty)* "\\n)" ";\\n\\n"\nrangeProperty ::= rangeNumber (" TO " rangeNumber)? " AS " text (" COLOR " hexColor)? | " DEFAULT " "AS " text (" COLOR " hexColor)?\nrangeNumber ::= identifier | floatNumber | intNumber | "INFINITY" | "-INFINITY"\ncreateDimension ::= createSpatialDimension | createCategoricalDimension\ncreateSpatialDimension ::= "SPATIAL DIMENSION " identifier " (\\n" "\\tgeometry" ":" (type | " Geometry") "\\n)" createDimensionProperties createParentDimension? ";\\n\\n"\ncreateCategoricalDimension ::= "CATEGORICAL DIMENSION " identifier " (\\n" "FIELD" ":" identifier ")" ";\\n\\n"\ncreateParentDimension ::= " WITH PARENT " "(\\n" identifier (",\\n" identifier)* "\\n)"\ncreateDimensionProperties ::= " WITH PROPERTIES " "(\\n" dimPropertyDefinition (",\\n" dimPropertyDefinition)* "\\n)"\ndimPropertyDefinition ::= "\\t" identifier type\ncreateSensor ::= "SENSOR " identifier " (\\n" "\\tinterval" ": " intNumber ",\\n" "\\tdatasource" ": " dataSource ",\\n" "\\tgeometry" ":" type "\\n)" createSensorProperties createSensorMeasurementData (addSpatialDimensionToSensor)* (addCategoricalDimensionToSensor)? (addBBXToSensor)? ";\\n\\n"\ncreateSensorProperties ::= " WITH PROPERTIES " "(\\n" sensorPropertyDefinition (",\\n" sensorPropertyDefinition)* "\\n)"\nsensorPropertyDefinition ::= "\\t" identifier type\ncreateSensorMeasurementData ::= " WITH MEASUREMENT DATA " "(\\n" createMeasurementProperty (",\\n" createMeasurementProperty)* "\\n)"\ncreateMeasurementProperty ::= "\\t" identifier type (" UNITS " text)? (" ICON " text)? (" RANGE " identifier)?\naddSpatialDimensionToSensor ::= " WITH SPATIAL DIMENSIONS " identifier "(\\n" "\\t" identifier (",\\n" "\\t" identifier)* "\\n)"\naddCategoricalDimensionToSensor ::= " WITH CATEGORICAL DIMENSIONS " "(\\n" "\\t" identifier (" RANGE " identifier)? (",\\n" "\\t" identifier (" RANGE " identifier)?)* "\\n)"\naddBBXToSensor ::= " WITH BBOX " "(" ("[")? coordinate ("]")? ", " intNumber")"\nsrid ::= intNumber\nidentifier ::= text\ntext ::= [a-zA-Z] [a-zA-Z]? [a-zA-Z]? [a-zA-Z]? [a-zA-Z]? [a-zA-Z]? [a-zA-Z]? [a-zA-Z]? [a-zA-Z]? [a-zA-Z]?\nquotedText ::= "\\"" text "\\""\nws ::= ([ \\t\\n] ws)?\ntype ::= " Long" | " Boolean" | " Float" | " Integer" | " Double" | " Localdate" | " String" | " Datetime" | " Linestring" | " Multilinestring" | " Polygon" | " Multipolygon" | " Point" | " Multipoint"\ndataSource ::= "postgres" | "elasticsearch"\nhexColor ::= "#" [0-9a-fA-F] [0-9a-fA-F] [0-9a-fA-F] [0-9a-fA-F] [0-9a-fA-F] [0-9a-fA-F]\ndigit ::= [0-9]\ndigits ::= digit+\nintNumber ::= digits\nfloatNumber ::= (digits? ".")? digits\nnegativeFloat ::= ("-")? floatNumber\ncoordinate ::= negativeFloat ", " negativeFloat\n\nCould you acknowledge that you understand what I want you to do in my following prompts?\nJust say "yes" or "no". I do not want you to do any additional work by now.',
      },
      { role: "assistant", content: "Yes." },
      {
        role: "user",
        content:
          'In addition to prior grammar, these are some examples of what I expect you to do, separated by <s> and [INST] to highlight the instruction:\n<s>[INST] Create a product called intecmar with an srid of 4326. [/INST]\nCREATE PRODUCT intecmar USING 4326; </s>\n\n<s>[INST] Create a spatial dimension called Municipality with a Geometry geometry and a property called cMun, which is an Integer. [/INST]\nCREATE SPATIAL DIMENSION Municipality (\n\tgeometry: Geometry\n) WITH PROPERTIES (\n\tcMun Integer\n); </s>\n\n<s>[INST] Create a categorical dimension called Depth with a field called depth. [/INST]\nCREATE CATEGORICAL DIMENSION Depth (\n\tfield: depth\n); </s>\n\n<s>[INST] Create a range called MagnitudRange with the following definitions: 0-3 for surface, 4.75-5.25 for 5m, 9.75-10.25 for 10m, 14.75-15.25 for 15m and 19.75-20.25 for 20m. [/INST]\nCREATE RANGE MagnitudRange (\n\t0 TO 3 AS "surface",\n\t4.75 TO 5.25 AS "5m",\n\t9.75 TO 10.25 AS "10m",\n\t14.75 TO 15.25 AS "15m",\n\t19.75 TO 20.25 AS "20m"\n); </s>\n\n<s>[INST] Create a sensor called StationObservation with a 300 interval, an elasticsearch datasource and a Point geometry. It also has several different properties called as follows, with their datatype between parenthesis: maxDepth (Float), code (String), name (String), descripcion (String), startTime (DateTime) and endTime (DateTime). It also has the following measurement data names, along with their datatypes, units, icon and range between parenthesis (note that many of them may lack the range and the icon information): temperature_ITS90 (Double, "�C�", "thermometer-low", TempRange), salinity (Double, "PSU"), pressure (Double, "dbar", "speedometer"), ph (Double, "pH", "ph"), oxygen (Double, "mg/l", "gas-cylinder"), transmittance (Double, "m"), irradiance (Double, "W/m2"), uv_flourescence (Double, "mg/m3"), density (Double, "kg/m3") and conductivity (Double, "S/m"). It also has one spatial dimension called Estuary, which includes the Estuary dimension. It also has one categorical dimension called Depth with a range called DepthRange. [/INST]\nCREATE SENSOR StationObservation (\n\tinterval: 300,\n\tdatasource: elasticsearch,\n\tgeometry: Point\n) WITH PROPERTIES (\n\tmaxDepth Float,\n\tcode String,\n\tname String,\n\tdescripcion String,\n\tstartTime DateTime,\n\tendTime DateTime\n) WITH MEASUREMENT DATA (\n\ttemperature_ITS90 Double UNITS "�C" ICON "thermometer-low" RANGE TempRange,\n\tsalinity Double UNITS "PSU",\n\tpressure Double UNITS "dbar" ICON "speedometer",\n\tph Double UNITS "pH" ICON "ph",\n\toxygen Double UNITS "mg/l" ICON "gas-cylinder",\n\ttransmittance Double UNITS "m",\n\tirradiance Double UNITS "W/m2",\n\tuv_flourescence Double UNITS "mg/m3",\n\tdensity Double UNITS "kg/m3",\n\tconductivity Double UNITS "S/m"\n) WITH SPATIAL DIMENSIONS Estuary (\n\tEstuary\n) WITH CATEGORICAL DIMENSIONS (\n\tDepth RANGE DepthRange\n); </s>\nCould you acknowledge that you understand what I want you to do in my following prompts?\nJust say "yes" or "no". I do not want you to do any additional work by now.',
      },
      { role: "assistant", content: "Yes." },
    ];
    let messagesHistory = initialContext;

    // Format user input and add to the conversation
    const userInput = { role: "user", content: req.body.message };
    messagesHistory.push(userInput);

    // Connect with OpenAI API
    let modelResponse = "";
    async () => {
      const stream = await openai.chat.completions.create({
        messages: messagesHistory,
        model: "gpt-3.5-turbo",
        temperature: 0.2,
        seed: 6,
      });

      // Get model output and consumed tokens
      modelResponse = stream.choices[0].message.content;
      nTokens = stream.usage.total_tokens;
    };

    // Format model output and add to the conversation
    const modelOutput = { role: "assistant", content: modelResponse };
    messagesHistory.push(modelOutput);

    res.send(messagesHistory, nTokens);
  } catch (error) {
    console.error(error);
    res.status(500).send("Error sending data to OpenAI API");
  }
});

server.listen(PORT, () => {
  console.log(`Server listening on port ${PORT}`);
});
