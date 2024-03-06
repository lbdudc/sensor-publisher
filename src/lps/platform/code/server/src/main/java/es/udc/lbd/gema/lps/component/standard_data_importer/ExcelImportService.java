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
import es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.client.ImportExcelDateFieldsNotSupportedException;
import es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.client.ImportExcelRowEmptyException;
import es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.client.ImportFileAndUrlNotProvidedException;
import es.udc.lbd.gema.lps.model.service.exceptions.AppRuntimeException;
import es.udc.lbd.gema.lps.model.repository._repositoriesBasePackage;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
public class ExcelImportService extends StandardDataImportService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelImportService.class);

    private final String IMPORT_TYPE = "spreadsheet";
    private final String XLS = "xls";
    private final String XLSX = "xlsx";

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
            result.setValues(readHeaderFromXls(file, null));
            result.setTemporaryFile(file.getName());
        } else if (p.getUrl() != null) {
            URL url;
            try {
                url = new URL(p.getUrl());
            } catch (MalformedURLException e) {
                throw new AppRuntimeException("URL: " + p.getUrl() + " is not valid");
            }
            result.setValues(readHeaderFromXls(null, url));
            result.setUrl(p.getUrl());
        } else {
            throw new ImportFileAndUrlNotProvidedException();
        }

        checkNoValuesRepeated(result.getValues());
        return result;
    }

    /**
     * Reads the first line of the Excel file (Old and new files)
     *
     * @throws ImportException If any controller exception was detected
     */
    private String[] readHeaderFromXls(File file, URL url) throws ImportException {
        String[] result = null;

        try (Workbook wb = getBufferedReader(file, url)){
            // We must create the workbook from the specified source

            Row row = wb.getSheetAt(0).getRow(0);
            if (row != null) {
                result = readXlsLine(row);
            }
        } catch (IOException e) {
            throw new AppRuntimeException("Error reading/closing the workbook",e);
        }
        return result;
    }

    private String[] readXlsLine(Row row) throws ImportException {
        if (isRowEmpty(row)) {
            throw new ImportExcelRowEmptyException(String.valueOf(row.getRowNum()));
        }

        ArrayList<String> values = new ArrayList<String>();

        for (int c = 0; c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c, MissingCellPolicy.CREATE_NULL_AS_BLANK);

            String value = null;
            if (!isCellEmpty(cell)) {
                CellType cellType = cell.getCellType();
                if (CellType.NUMERIC.equals(cellType)) {
                    if (DateUtil.isCellDateFormatted(cell)) {
                        throw new ImportExcelDateFieldsNotSupportedException();
                    }
                    value = Double.toString(cell.getNumericCellValue());
                } else if (CellType.STRING.equals(cellType)) {
                    value = cell.getStringCellValue();
                } else if (CellType.BLANK.equals(cellType)) {
                    value = "";
                } else {
                    throw new ImportExcelCellTypeNotSupportedException();
                }
            }
            values.add(value);
        }
        return values.toArray(new String[0]);
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

        Set<String> validExtensions = new HashSet<String>(Arrays.asList(XLS, XLSX));

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

        this.importFromXlsFile(params, clazzObject, clazzRepositoryName, saveMethod);

        return params.getFile();
    }

    private void importFromXlsFile(FormatExcel p, Class<?> clazzObject, String clazzRepositoryName, Method saveMethod)
        throws ImportException {

        // We must to create the workbook from the specified source
        try (Workbook wb = getBufferedReader(p.getFile(), p.getUrl())){

            wb.setMissingCellPolicy(MissingCellPolicy.CREATE_NULL_AS_BLANK);
            Sheet sheet = wb.getSheetAt(0);
            int rows = sheet.getPhysicalNumberOfRows();
            for (int r = p.isSkipFirstLine() ? 1 : 0; r < rows; r++) {
                Row row = sheet.getRow(r);
                if (row == null || isRowEmpty(row)) {
                    continue;
                }

                String[] line = readXlsLine(row);
                logger.debug("Line read from XLS: " + Arrays.toString(line));

                // Populate entity fields
                Object instanceObject = fillObjectWithData(p.getFormat(), line, clazzObject);
                // Save entity
                saveEntity(clazzRepositoryName, saveMethod, instanceObject);
            }
        } catch(IOException e) {
            throw new AppRuntimeException("Error reading/closing workbook", e);
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

        Set<String> validExtensions = new HashSet<String>(Arrays.asList(XLS, XLSX));

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
    private static boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && !CellType.BLANK.equals(cell.getCellType())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the value of a given {@link Cell} is empty.
     */
    private static boolean isCellEmpty(final Cell cell) {
        //noinspection SimplifiableIfStatement
        if (cell == null || CellType.BLANK.equals(cell.getCellType())) {
            return true;
        }

        return CellType.STRING.equals(cell.getCellType()) && cell.getStringCellValue().isEmpty();
    }

    private Workbook getBufferedReader(File file, URL url) {
        if (file != null) {
            try {
                return WorkbookFactory.create(file);
            } catch (EncryptedDocumentException | IOException e) {
                throw new AppRuntimeException("Unable to parse file " + file.getAbsolutePath() + " as workbook");
            }
        } else if (url != null) {
            try {
                URLConnection uc = url.openConnection();
                return WorkbookFactory.create(uc.getInputStream());
            } catch (IOException | EncryptedDocumentException e) {
                throw new AppRuntimeException("Unable to parse URL " + url.getPath() + " as workbook");
            }
        } else {
            throw new AppRuntimeException("No file or url were provided");
        }
    }

}
/*% } %*/
