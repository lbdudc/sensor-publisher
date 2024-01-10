/*% if (feature.DM_DI_DF_Raster) { %*/
package es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.client;

import es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.ImportException;

import org.springframework.http.HttpStatus;

public class ImportLatitudeNotValidException extends ImportException {

    public ImportLatitudeNotValidException(String param) {
        super("import.latitude_not_valid", param, HttpStatus.BAD_REQUEST);
        logger.warn("Import: Latitude not valid '{}'.", param);
    }
}
/*% } %*/
