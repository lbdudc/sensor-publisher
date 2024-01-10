/*% if (feature.DM_DI_DF_Spreadsheet) { %*/
package es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.client;

import es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.ImportException;

import org.springframework.http.HttpStatus;

public class ImportOdsUndefinedRowsColsException extends ImportException {

    public ImportOdsUndefinedRowsColsException() {
        super("import.ods_undefined_rows_cols", HttpStatus.BAD_REQUEST);
        logger.warn("Import: No rows / columns have been defined for ods file");
    }
}
/*% } %*/
