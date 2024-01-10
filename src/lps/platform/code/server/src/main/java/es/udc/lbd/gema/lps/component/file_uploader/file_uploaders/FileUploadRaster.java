/*% if (feature.DM_DI_DF_Raster) { %*/
package es.udc.lbd.gema.lps.component.file_uploader.file_uploaders;

import es.udc.lbd.gema.lps.component.file_uploader.FileUploadUtils;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class FileUploadRaster extends FileUpload {

    private static final String FOLDER = File.separator + "raster";

    protected String getFolder() {
        return FOLDER;
    }

    /**
     * Copies the temporary file to target folder and removes temporary file
     *
     * @param temporaryFile temporary file name
     * @param subfolder     subfolder, (raster id)
     * @param filename      real filename
     */
    public void saveFromTemporaryFile(String temporaryFile, String subfolder, String filename) {

        String fileRelativePath = getUploadsPath() + File.separator + FOLDER + File.separator + subfolder;
        File filePath = fileUtil.getFile(fileRelativePath);

        if (StringUtils.isNotBlank(temporaryFile)) {

            File tempFile = new File(getTemporaryPath() + File.separator + temporaryFile);

            // Remove folder
            FileUploadUtils.removeFileOrFolder(filePath);
            // Copy temporary file to target folder
            fileUtil.copyFileToFolder(tempFile, filePath, filename);
            // Remove temporary file
            FileUploadUtils.removeFileOrFolder(tempFile);
        }
    }

    public void deleteResourceWithFiles(Long idRaster) {
        String fileRelativePath = getUploadsPath() + File.separator + FOLDER + File.separator + idRaster;
        File filePath = fileUtil.getFile(fileRelativePath);
        FileUploadUtils.removeFileOrFolder(filePath);
    }
}
/*% } %*/
