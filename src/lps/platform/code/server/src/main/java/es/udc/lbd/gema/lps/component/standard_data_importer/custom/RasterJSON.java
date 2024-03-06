/*% if (feature.DM_DI_DF_Raster) { %*/
package es.udc.lbd.gema.lps.component.standard_data_importer.custom;

public class RasterJSON {

    private String layer;
    private String format;
    private Double lngMin;
    private Double lngMax;
    private Double latMin;
    private Double latMax;
    private String temporaryFile;
    private String fileName;

    public String getLayer() {
        return layer;
    }

    public void setLayer(String layer) {
        this.layer = layer;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Double getLngMin() {
        return lngMin;
    }

    public void setLngMin(Double lngMin) {
        this.lngMin = lngMin;
    }

    public Double getLngMax() {
        return lngMax;
    }

    public void setLngMax(Double lngMax) {
        this.lngMax = lngMax;
    }

    public Double getLatMin() {
        return latMin;
    }

    public void setLatMin(Double latMin) {
        this.latMin = latMin;
    }

    public Double getLatMax() {
        return latMax;
    }

    public void setLatMax(Double latMax) {
        this.latMax = latMax;
    }

    public String getTemporaryFile() {
        return temporaryFile;
    }

    public void setTemporaryFile(String temporaryFile) {
        this.temporaryFile = temporaryFile;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
/*% } %*/
