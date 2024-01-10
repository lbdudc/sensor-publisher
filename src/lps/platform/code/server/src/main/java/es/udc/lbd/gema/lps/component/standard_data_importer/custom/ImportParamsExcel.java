/*% if (feature.DM_DI_DF_Spreadsheet) { %*/
package es.udc.lbd.gema.lps.component.standard_data_importer.custom;

import org.apache.commons.io.FilenameUtils;

public class ImportParamsExcel extends ImportParamsCommon {

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return File extension obtained from file or from url
     */
    @Override
    public String getExtension() {
        if (file != null) {
            return FilenameUtils.getExtension(file.getOriginalFilename());
        }
        if (url != null) {
            return FilenameUtils.getExtension(url);
        }
        return null;
    }
}
/*% } %*/
