/*% if (feature.DM_DI_DF_Raster) { %*/
package es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.client;

import es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.ImportException;

import org.springframework.http.HttpStatus;

public class ImportLatitudeMinMaxEqualsException extends ImportException {

    public ImportLatitudeMinMaxEqualsException(String param) {
        super("import.latitude_min_max_equals", param, HttpStatus.BAD_REQUEST);
        logger.warn("Import: Latitude min equals to longitude max'{}'.", param);
    }
}
/*% } %*/
