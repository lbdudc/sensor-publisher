/*% if (feature.DM_DI_DF_Spreadsheet) { %*/
package es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.client;

import es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.ImportException;
import org.springframework.http.HttpStatus;

/**
 * Created by sfernandez on 30/11/2016.
 */
public class ImportExcelCellTypeNotSupportedException extends ImportException {

    public ImportExcelCellTypeNotSupportedException() {
        super("import.excel_cell_type_not_supported", HttpStatus.BAD_REQUEST);
        logger.warn("Import: Excel cell type not supported.");
    }
}
/*% } %*/
