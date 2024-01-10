/*% if (feature.DM_DI_DF_Spreadsheet) { %*/
package es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.client;

import es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.ImportException;
import org.springframework.http.HttpStatus;

/**
 * Created by sfernandez on 30/11/2016.
 */
public class ImportExcelDateFieldsNotSupportedException extends ImportException {

    public ImportExcelDateFieldsNotSupportedException() {
        super("import.excel_date_fields_not_supported", HttpStatus.BAD_REQUEST);
        logger.warn("Import: Excel date fields not supported.");
    }
}
/*% } %*/
