/*% if (feature.DM_DI_DF_Raster) { %*/
package es.udc.lbd.gema.lps.component.standard_data_importer;

import es.udc.lbd.gema.lps.component.file_uploader.file_uploaders.FileUploadRaster;
import es.udc.lbd.gema.lps.component.standard_data_importer.custom.RasterJSON;
import es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.ImportException;
import es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.client.ImportCrsNotValidException;
import es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.client.ImportEmptyValueException;
import es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.client.ImportLatitudeMinMaxEqualsException;
import es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.client.ImportLatitudeNotValidException;
import es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.client.ImportLatitudeReversedException;
import es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.client.ImportLongitudeMinMaxEqualsException;
import es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.client.ImportLongitudeNotValidException;
import es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.client.ImportLongitudeReversedException;
import es.udc.lbd.gema.lps.component.standard_data_importer.model.domain.SDIRaster;
import es.udc.lbd.gema.lps.component.standard_data_importer.model.repository.SDIRasterRepository;
import es.udc.lbd.gema.lps.config.Properties;
import es.udc.lbd.gema.lps.model.service.exceptions.AppRuntimeException;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.util.factory.Hints;
import org.geotools.gce.arcgrid.ArcGridReader;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.opengis.coverage.grid.GridCoverageReader;
import org.opengis.geometry.Envelope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RasterImportService {

    static final Logger logger = LoggerFactory.getLogger(RasterImportService.class);
    private static final String JPEG = "JPEG";
    private static final String PNG = "PNG";
    private static final String TIFF = "TIFF";
    private static final String ASCII = "ASCII";

    @Inject
    private FileUploadRaster fileUploadRaster;

    @Inject
    private SDIRasterRepository rasterRepository;

    @Inject
    private Properties properties;

    public void importRaster(RasterJSON data) throws ImportException {

        if (data.getFormat().equals(TIFF)) {
            calculateTIFFCoordinates(data);
        } else if (data.getFormat().equals(ASCII)) {
            calculateASCIICoordinates(data);
        }

        validateData(data);

        // Create raster
        SDIRaster raster = new SDIRaster();
        raster.setName(data.getLayer());
        raster.setFormat(data.getFormat());
        raster.setLngMin(data.getLngMin());
        raster.setLngMax(data.getLngMax());
        raster.setLatMin(data.getLatMin());
        raster.setLatMax(data.getLatMax());
        raster.setSpace("EPSG");
        raster.setCode(((Integer) properties.getGis().getDefaultSrid()).toString());
        rasterRepository.save(raster);

        fileUploadRaster.saveFromTemporaryFile(data.getTemporaryFile(), raster.getId().toString(), data.getFileName());
        raster.setFileName(data.getFileName());
    }

    public void validateData(RasterJSON data) throws ImportException {
        if (data.getFormat().equals(JPEG) || data.getFormat().equals(PNG)) {
            if (data.getLngMin() == null) {
                throw new ImportEmptyValueException("Lng. min");
            } else if (data.getLngMax() == null) {
                throw new ImportEmptyValueException("Lng. max");
            } else if (data.getLatMin() == null) {
                throw new ImportEmptyValueException("Lat. min");
            } else if (data.getLatMax() == null) {
                throw new ImportEmptyValueException("Lat. max");
            }
        }

        validateCoordinates(data);
    }

    public void validateCoordinates(RasterJSON data) throws ImportException {
        // X: longitude, Y: Latitude
        if (data.getLatMin() < -90 || data.getLatMin() > 90) {
            throw new ImportLatitudeNotValidException(String.valueOf(data.getLatMin()));
        } else if (data.getLatMax() < -90 || data.getLatMax() > 90) {
            throw new ImportLatitudeNotValidException(String.valueOf(data.getLatMax()));
        } else if (data.getLngMin() < -180 || data.getLngMin() > 180) {
            throw new ImportLongitudeNotValidException(String.valueOf(data.getLngMin()));
        } else if (data.getLngMax() < -180 || data.getLngMax() > 180) {
            throw new ImportLongitudeNotValidException(String.valueOf(data.getLngMax()));
        } else if (data.getLngMin().equals(data.getLngMax())) {
            throw new ImportLongitudeMinMaxEqualsException(String.valueOf(data.getLngMin()));
        } else if (data.getLatMin().equals(data.getLatMax())) {
            throw new ImportLatitudeMinMaxEqualsException(String.valueOf(data.getLatMin()));
        } else if (data.getLngMin() > data.getLngMax()) {
            throw new ImportLongitudeReversedException(data.getLngMin() + " ... " + data.getLngMax());
        } else if (data.getLatMin() > data.getLatMax()) {
            throw new ImportLatitudeReversedException(data.getLatMin() + " ... " + data.getLatMax());
        }
    }

    public void calculateTIFFCoordinates(RasterJSON data) throws ImportException {
        File file = fileUploadRaster.getTemporaryFile(data.getTemporaryFile());

        try {
            GeoTiffReader reader = new GeoTiffReader(file, new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE));
            GridCoverage2D coverage = reader.read(null);
            setGeographicExtensions(data, coverage);

            String space = coverage.getCoordinateReferenceSystem2D().getIdentifiers().iterator().next().getCodeSpace();
            String code = coverage.getCoordinateReferenceSystem2D().getIdentifiers().iterator().next().getCode();

            if (!space.equals("EPSG") || !code.equals(((Integer) properties.getGis().getDefaultSrid()).toString())) {
                throw new ImportCrsNotValidException(StringUtils.trimToEmpty(space) + ":" + StringUtils.trimToEmpty(code));
            }

        } catch (IOException e) {
            throw new AppRuntimeException(e);
        }
    }

    public void calculateASCIICoordinates(RasterJSON data) {
        File file = fileUploadRaster.getTemporaryFile(data.getTemporaryFile());

        try {
            GridCoverageReader reader = new ArcGridReader(file);
            GridCoverage2D coverage = (GridCoverage2D) reader.read(null);
            setGeographicExtensions(data, coverage);
        } catch (IOException e) {
            throw new AppRuntimeException(e);
        }

    }

    private void setGeographicExtensions(RasterJSON data, GridCoverage2D coverage) {
        Envelope env = coverage.getEnvelope();

        double lngMin = env.getLowerCorner().getOrdinate(0);
        double latMin = env.getLowerCorner().getOrdinate(1);

        double lngMax = env.getUpperCorner().getOrdinate(0);
        double latMax = env.getUpperCorner().getOrdinate(1);

        logger.debug(coverage.getCoordinateReferenceSystem2D().getName().toString() + " " + coverage.getName() + ": " + lngMin + "," + latMin + "  " + lngMax + "," + latMax);

        data.setLngMin(lngMin);
        data.setLngMax(lngMax);
        data.setLatMin(latMin);
        data.setLatMax(latMax);
    }

    /**
     * Removes a resource and related files
     * @param idRaster
     */
    public void delete(Long idRaster) {
        // Delete raster
        rasterRepository.deleteById(idRaster);
        // Delete resource folder
        fileUploadRaster.deleteResourceWithFiles(idRaster);
    }

}
/*% } %*/
