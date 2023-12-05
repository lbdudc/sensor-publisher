import fs from "fs";

export const getConfig = (configPath) => {
  const configFile = fs.readFileSync(configPath, "utf8");

  if (!configFile) {
    console.error("Config file not found");
    process.exit(1);
  }

  try {
    return JSON.parse(configFile).platform;
  } catch (error) {
    console.error("Invalid config file");
    process.exit(1);
  }
};
