import fs from "fs";

export const awaitCreation = (folderPaths, time) => {
  return new Promise((resolve) => {
    const interval = setInterval(() => {
      // if all folders exist
      let allFoldersExist = true;

      for (const folderPath of folderPaths) {
        if (!fs.existsSync(folderPath)) {
          allFoldersExist = false;
          break;
        }
      }

      if (allFoldersExist) {
        clearInterval(interval);
        resolve();
      }
    }, time);
  });
};

export const rmFolder = (folderPath) => {
  fs.rm(folderPath, { recursive: true }, (err) => {
    if (err) {
      console.error("Error deleting folder:", err);
    } else {
      console.log("Folder deleted successfully");
    }
  });
};
