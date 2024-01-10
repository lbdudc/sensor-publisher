/*% if (feature.DM_DI_DF_Raster) { %*/
package es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.client;

import es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.ImportException;

import org.springframework.http.HttpStatus;

public class ImportLongitudeMinMaxEqualsException extends ImportException {

    public ImportLongitudeMinMaxEqualsException(String param) {
        super("import.longitude_min_max_equals", param, HttpStatus.BAD_REQUEST);
        logger.warn("Import: Longitude min equals to longitude max '{}'.", param);
    }
}
/*% } %*/
