/*% if (feature.DM_DI_DF_Raster) { %*/
package es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.client;

import es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.ImportException;

import org.springframework.http.HttpStatus;

public class ImportLongitudeNotValidException extends ImportException {

    public ImportLongitudeNotValidException(String param) {
        super("import.longitude_not_valid", param, HttpStatus.BAD_REQUEST);
        logger.warn("Import: Longitude not valid '{}'.", param);
    }
}
/*% } %*/
