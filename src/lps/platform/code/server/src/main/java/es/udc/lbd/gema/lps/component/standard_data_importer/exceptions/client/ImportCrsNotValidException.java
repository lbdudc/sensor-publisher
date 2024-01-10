/*% if (feature.DM_DI_DF_Raster) { %*/
package es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.client;

import es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.ImportException;

import org.springframework.http.HttpStatus;

public class ImportCrsNotValidException extends ImportException {

    public ImportCrsNotValidException(String param) {
        super("import.crs_not_valid", param, HttpStatus.BAD_REQUEST);
        logger.warn("Import: CRS not valid '{}'.", param);
    }
}
/*% } %*/
