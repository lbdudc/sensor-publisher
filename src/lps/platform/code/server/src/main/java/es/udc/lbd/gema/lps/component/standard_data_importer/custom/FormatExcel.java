/*% if (feature.DM_DI_DF_Spreadsheet) { %*/
package es.udc.lbd.gema.lps.component.standard_data_importer.custom;

import java.net.URL;

import org.apache.commons.lang3.StringUtils;

public class FormatExcel extends FormatCommon {
    private ImportColumnJSON[] format;
    private String temporaryFileName;
    private URL url;
    private String urlString;
    private boolean skipFirstLine;
    private String entityClazz;

    public String getEntityClazz() {
        return entityClazz;
    }

    public void setEntityClazz(String entityClazz) {
        this.entityClazz = entityClazz;
    }

    public ImportColumnJSON[] getFormat() {
        return format;
    }

    public void setFormat(ImportColumnJSON[] format) {
        this.format = format;
    }

    public boolean isSkipFirstLine() {
        return skipFirstLine;
    }

    public void setSkipFirstLine(boolean skipFirstLine) {
        this.skipFirstLine = skipFirstLine;
    }

    public String getTemporaryFileName() {
        return temporaryFileName;
    }

    public void setTemporaryFileName(String temporaryFileName) {
        this.temporaryFileName = temporaryFileName;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public String getUrlString() {
        return urlString;
    }

    public void setUrlString(String urlString) {
        this.urlString = urlString;
    }

    /**
     * @return File extension obtained from file or from url
     */
    @Override
    public String getExtension() {
        if (temporaryFileName != null) {
            return StringUtils.substringAfterLast(temporaryFileName, ".");
        }
        if (urlString != null) {
            return StringUtils.substringAfterLast(urlString, ".");
        }
        return null;
    }
}
/*% } %*/
