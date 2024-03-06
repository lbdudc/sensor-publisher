/*% if (feature.DM_DI_DF_Spreadsheet) { %*/
package es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.client;

import es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.ImportException;
import org.springframework.http.HttpStatus;

/**
 * Created by sfernandez on 30/11/2016.
 */
public class ImportExcelRowEmptyException extends ImportException {

    public ImportExcelRowEmptyException(String param) {
        super("import.excel_row_empty", param, HttpStatus.BAD_REQUEST);
        logger.warn("Import: Excel row {} is empty.", param);
    }
}
/*% } %*/
