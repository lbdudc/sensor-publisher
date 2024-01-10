/*% if (feature.DM_DI_DF_Raster) { %*/
package es.udc.lbd.gema.lps.component.standard_data_importer.model.domain;

import es.udc.lbd.gema.lps.component.entities_information.EntityInfo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.SequenceGenerator;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "sdi_raster")
@EntityInfo(hidden = true)
public class SDIRaster {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rasterGen")
    @SequenceGenerator(name = "rasterGen", sequenceName = "sdi_raster_id_seq", allocationSize = 1)
    private Long id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String format;

    @NotNull
    private Double lngMin;

    @NotNull
    private Double lngMax;

    @NotNull
    private Double latMin;

    @NotNull
    private Double latMax;

    private String fileName;

    @NotEmpty
    private String code;

    @NotEmpty
    private String space;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSpace() {
        return space;
    }

    public void setSpace(String space) {
        this.space = space;
    }
}
/*% } %*/
