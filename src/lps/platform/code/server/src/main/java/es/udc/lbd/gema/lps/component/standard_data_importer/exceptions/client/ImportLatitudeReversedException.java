/*% if (feature.DM_DI_DF_Raster) { %*/
package es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.client;

import es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.ImportException;

import org.springframework.http.HttpStatus;

public class ImportLatitudeReversedException extends ImportException {

    public ImportLatitudeReversedException(String param) {
        super("import.latitude_reversed", param, HttpStatus.BAD_REQUEST);
        logger.warn("Import: Latitude reversed '{}'.", param);
    }
}
/*% } %*/
