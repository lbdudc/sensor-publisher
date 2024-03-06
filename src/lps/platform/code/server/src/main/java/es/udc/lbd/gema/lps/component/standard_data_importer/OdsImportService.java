/*% if (feature.DM_DI_DF_Spreadsheet) { %*/
package es.udc.lbd.gema.lps.component.standard_data_importer;

import es.udc.lbd.gema.lps.component.standard_data_importer.custom.FormatExcel;
import es.udc.lbd.gema.lps.component.standard_data_importer.custom.ImportFileJSON;
import es.udc.lbd.gema.lps.component.standard_data_importer.custom.ImportParamsExcel;
import es.udc.lbd.gema.lps.component.standard_data_importer.custom.ParseFormatJSON;
import es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.FileNotSupportedException;
import es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.ImportException;
import es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.TypeNotSupportedException;
import es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.client.ImportExcelCellTypeNotSupportedException;
import es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.client.ImportExcelRowEmptyException;
import es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.client.ImportFileAndUrlNotProvidedException;
import es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.client.ImportOdsUndefinedRowsColsException;
import es.udc.lbd.gema.lps.model.service.exceptions.AppRuntimeException;
import es.udc.lbd.gema.lps.model.repository._repositoriesBasePackage;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Service
public class OdsImportService extends StandardDataImportService {

    private static final Logger logger = LoggerFactory.getLogger(OdsImportService.class);

    private final String IMPORT_TYPE = "spreadsheet";
    private final String ODS = "ods";

    @Override
    ImportFileJSON uploadAndProcessHeader(MultipartHttpServletRequest request) throws ImportException, FileNotSupportedException, TypeNotSupportedException {
        // Obtain parameters from request
        ImportParamsExcel p = getParamsHeader(request);

        ImportFileJSON result = new ImportFileJSON();

        if (p.getFile() != null) {
            // Store the uploaded file in a temporary folder
            // This file will be deleted after a successful import or next day
            // via a cron job
            File file = fileUploadImport.saveTemporaryFile(p.getFile());
            result.setValues(readHeaderFromOds(file, null));
            result.setTemporaryFile(file.getName());
        } else if (p.getUrl() != null) {
            URL url;
            try {
                url = new URL(p.getUrl());
            } catch (MalformedURLException e) {
                throw new AppRuntimeException("URL: " + p.getUrl() + " is not valid");
            }
            result.setValues(readHeaderFromOds(null, url));
            result.setUrl(p.getUrl());
        } else {
            throw new ImportFileAndUrlNotProvidedException();
        }

        checkNoValuesRepeated(result.getValues());
        return result;
    }

    /**
     * Reads the first line of the ODS file
     *
     * @throws ImportException If any controller exception was detected
     */
    private String[] readHeaderFromOds(File file, URL url) throws ImportException {

        SpreadsheetDocument ss = getBufferedReader(file, url);

        validateSpreadsheetDocument(ss);

        // We must create the workbook from the specified source
        Row row = ss.getSheetByIndex(0).getRowByIndex(0);

        return row != null ? readOdsLine(row) : null;
    }

    private String[] readOdsLine(Row row) throws ImportException {
        if (row.getCellCount() == 0) {
            throw new ImportExcelRowEmptyException(String.valueOf(row.getRowIndex()));
        }

        ArrayList<String> values = new ArrayList<String>();

        for (int c = 0; c < row.getCellCount(); c++) {
            Cell cell = row.getCellByIndex(c);
            values.add(getCellValue(cell));
        }
        return values.toArray(new String[0]);
    }

    private static String getCellValue(Cell cell) throws ImportException {

        // Cell type can be "boolean", "currency", "date", "float", "percentage", "string" or "time".
        // If no value type is set, null will be returned.
        String cellType = cell.getValueType();
        String value;

        if (cellType == null) {
            value = cell.getStringValue();
        } else if ("string".equals(cellType)) {
            value = cell.getStringValue();
        } else if ("float".equals(cellType)) {
            value = Double.toString(cell.getDoubleValue());
        } else if ("percentage".equals(cellType)) {
            value = Double.toString(cell.getPercentageValue());
        } else if ("currency".equals(cellType)) {
            value = Double.toString(cell.getCurrencyValue());
        } else {
            throw new ImportExcelCellTypeNotSupportedException();
        }
        return value.equals("") ? null : value;
    }

    /**
     * Obtains parameters from request.
     *
     * @param request the request
     * @return Parameters obtained from request
     * @throws ImportException           No file or url were provided
     */
    private ImportParamsExcel getParamsHeader(MultipartHttpServletRequest request) throws ImportException, FileNotSupportedException, TypeNotSupportedException {

        // Get params form request
        ImportParamsExcel p = new ImportParamsExcel();

        // Check type
        String type = request.getParameter("type");
        if (!type.equals(IMPORT_TYPE)) {
            throw new TypeNotSupportedException();
        }

        Set<String> validExtensions = new HashSet<String>(Collections.singletonList(ODS));

        // Check File or URL provided
        if (request.getParameter("url") != null) {
            p.setUrl(checkAndGetUrl(request.getParameter("url"), validExtensions));
        } else {
            p.setFile(checkAndGetFile(request.getFile("file"), validExtensions));
        }

        return p;
    }

    /**
     * Imports the file content into DB
     *
     * @return Temporary file used
     */
    @Override
    public File processFile(ParseFormatJSON format) throws ImportException, FileNotSupportedException, TypeNotSupportedException {

        FormatExcel params = getParamsBody(format);

        // Validate format
        validateFormat(params.getFormat());

        // Obtain target object class and instance
        Class<?> clazzObject;
        try {
            clazzObject = Class.forName(params.getEntityClazz());
        } catch (ClassNotFoundException e) {
            throw new AppRuntimeException("Unable to obtain entity class", e);
        }

        // Obtain repository class and save method
        String clazzRepositoryPackage = StringUtils.removeEnd(_repositoriesBasePackage.class.getName(),
            _repositoriesBasePackage.class.getSimpleName());
        String clazzRepositoryName = clazzObject.getSimpleName() + "Repository";
        Class<?> clazzRepository;
        try {
            clazzRepository = Class.forName(clazzRepositoryPackage + clazzRepositoryName);
        } catch (ClassNotFoundException e) {
            throw new AppRuntimeException("Unable to obtain repository class", e);
        }

        // WARNING: We need to declare a .save method in every Repository class!
        // => Try to avoid this
        Method saveMethod;
        try {
            saveMethod = clazzRepository.getMethod("save", clazzObject);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new AppRuntimeException("Unable to obtain 'save' method for class " + clazzRepositoryName, e);
        }

        this.importFromOdsFile(params, clazzObject, clazzRepositoryName, saveMethod);

        return params.getFile();
    }

    private void importFromOdsFile(FormatExcel p, Class<?> clazzObject, String clazzRepositoryName, Method saveMethod)
        throws ImportException {

        try (SpreadsheetDocument ss = getBufferedReader(p.getFile(), p.getUrl())) {
            validateSpreadsheetDocument(ss);

            Table table = ss.getSheetByIndex(0);
            int rows = table.getRowCount();

            for (int r = p.isSkipFirstLine() ? 1 : 0; r < rows; r++) {
                Row row = table.getRowByIndex(r);
                if (row == null || isRowEmpty(row)) {
                    continue;
                }

                String[] line = readOdsLine(row);
                logger.debug("Line read from ODS: " + Arrays.toString(line));

                // Populate entity fields
                Object instanceObject = fillObjectWithData(p.getFormat(), line, clazzObject);
                // Save entity
                saveEntity(clazzRepositoryName, saveMethod, instanceObject);
            }
        }
    }

    /**
     * Check format values
     *
     * @param format the format
     * @return Parameters
     * @throws ImportException
     *             No file or url were provided
     *             File format not supported
     */
    private FormatExcel getParamsBody(ParseFormatJSON format) throws ImportException, FileNotSupportedException, TypeNotSupportedException {
        // Check type
        if (!format.getType().equals(IMPORT_TYPE)) {
            throw new TypeNotSupportedException();
        }

        FormatExcel p = new FormatExcel();

        Set<String> validExtensions = new HashSet<String>(Collections.singletonList(ODS));

        // Check provided file is excel format
        if (format.getUrl() != null) {
            p.setUrlString(checkAndGetUrl(format.getUrl(), validExtensions));
        } else {
            p.setTemporaryFileName(checkAndGetFile(format.getFile(), validExtensions));
        }

        // Get rest of parameters
        p.setEntityClazz(format.getEntityName());
        p.setFormat(format.getColumns());
        p.setSkipFirstLine(format.isSkipFirstLine());

        if (p.getTemporaryFileName() != null) {
            p.setFile(fileUploadImport.getTemporaryFile(p.getTemporaryFileName()));
        } else if (StringUtils.isNotBlank(p.getUrlString())) {
            try {
                p.setUrl(new URL(p.getUrlString()));
            } catch (MalformedURLException e) {
                throw new AppRuntimeException("URL: " + p.getUrlString() + " is not valid");
            }
        }

        return p;
    }

    /**
     * Checks is a {@link Row} contains any value
     */
    private static boolean isRowEmpty(Row row) throws ImportException {
        for (int c = 0; c < row.getCellCount(); c++) {
            Cell cell = row.getCellByIndex(c);
            if (StringUtils.isNotBlank(getCellValue(cell))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the document is a valid .ods file with a valid number of rows / columns. <br>
     * An excel document saved as .ods will have those values empty!.
     */
    private void validateSpreadsheetDocument(SpreadsheetDocument ss) throws ImportException {

        // Check if rows / columns number are defined
        Table table = ss.getSheetByIndex(0);
        int rows = table.getRowCount();
        int cols = table.getColumnCount();

        if (rows == 1048576 && cols == 1024) {
            throw new ImportOdsUndefinedRowsColsException();
        }
    }

    private SpreadsheetDocument getBufferedReader(File file, URL url) {
        if (file != null) {
            try {
                return SpreadsheetDocument.loadDocument(file);
            } catch (Exception e) {
                throw new AppRuntimeException("Unable to parse file " + file.getAbsolutePath() + " as workbook");
            }
        } else if (url != null) {
            try {
                URLConnection uc = url.openConnection();
                return SpreadsheetDocument.loadDocument(uc.getInputStream());
            } catch (Exception e) {
                throw new AppRuntimeException("Unable to parse URL " + url.getPath() + " as workbook");
            }
        } else {
            throw new AppRuntimeException("No file or url were provided");
        }
    }

}
/*% } %*/
