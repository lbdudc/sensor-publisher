import fs from "fs";
import JSZip from "jszip";
import path from "path";

function zipFolder(folderPath, outputFilePath) {
  return new Promise((resolve, reject) => {
    const zip = new JSZip();

    const addToZip = async (currentPath, relativePath) => {
      try {
        const files = await fs.promises.readdir(currentPath, {
          withFileTypes: true,
        });

        for (const file of files) {
          const filePath = path.join(currentPath, file.name);
          const stats = await fs.promises.stat(filePath);

          if (stats.isFile()) {
            const content = await fs.promises.readFile(filePath);
            zip.file(path.join(relativePath, file.name), content);
          } else if (stats.isDirectory()) {
            await addToZip(filePath, path.join(relativePath, file.name));
          }
        }

        resolve(); // Resolve the promise when all files are processed
      } catch (err) {
        reject(err); // Reject if there's an error
      }
    };

    addToZip(folderPath, "")
      .then(() => {
        zip
          .generateNodeStream({ type: "nodebuffer", streamFiles: true })
          .pipe(fs.createWriteStream(outputFilePath))
          .on("finish", () => {
            console.log(`Folder "${folderPath}" zipped to "${outputFilePath}"`);
            resolve(); // Resolve the promise when zipping is finished
          })
          .on("error", (err) => {
            reject(err); // Reject if there's an error during zipping
          });
      })
      .catch((err) => {
        reject(err); // Reject if there's an error in the initial processing
      });
  });
}

export default zipFolder;
