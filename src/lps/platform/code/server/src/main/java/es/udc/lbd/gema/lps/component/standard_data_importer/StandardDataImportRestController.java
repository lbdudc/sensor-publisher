/*% if (feature.DM_DI_DataFeeding) { %*/
package es.udc.lbd.gema.lps.component.standard_data_importer;

import es.udc.lbd.gema.lps.component.file_uploader.file_uploaders.FileUploadImport;
import es.udc.lbd.gema.lps.component.standard_data_importer.custom.ImportFileJSON;
import es.udc.lbd.gema.lps.component.standard_data_importer.custom.ParseFormatJSON;
/*% if (feature.DM_DI_DF_Raster) { %*/
import es.udc.lbd.gema.lps.component.standard_data_importer.custom.RasterJSON;
/*% } %*/
import es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.FileNotSupportedException;
import es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.ImportException;
import es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.TypeNotSupportedException;
import es.udc.lbd.gema.lps.component.standard_data_importer.exceptions.client.ImportFileNotSupportedException;
/*% if (feature.DM_DI_DF_Raster) { %*/
import es.udc.lbd.gema.lps.component.standard_data_importer.model.domain.SDIRaster;
import es.udc.lbd.gema.lps.component.standard_data_importer.model.repository.SDIRasterRepository;
import es.udc.lbd.gema.lps.web.rest.util.specification_utils.SpecificationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import es.udc.lbd.gema.lps.web.rest.util.PaginationUtil;

/*% } %*/

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;

/*% if (feature.DM_DI_DF_Shapefile) { %*/
import org.apache.commons.io.FilenameUtils;
/*% } %*/
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RestController
@RequestMapping(StandardDataImportRestController.SDI_REST_URL)
public class StandardDataImportRestController {
  static final String SDI_REST_URL = "/api/import";
  /*% if (feature.DM_DI_DF_CSV) { %*/

  @Inject
  private CsvImportService csvImportService;
  /*% } %*/
  /*% if (feature.DM_DI_DF_Spreadsheet) { %*/

  @Inject
  private ExcelImportService excelImportService;

  @Inject
  private OdsImportService odsImportService;
  /*% } %*/
  /*% if (feature.DM_DI_DF_Shapefile) { %*/

  @Inject
  private GisImportService gisImportService;
  /*% } %*/
  /*% if (feature.DM_DI_DF_Raster) { %*/

  @Inject
  private RasterImportService rasterImportService;

  @Inject
  private SDIRasterRepository sdiRasterRepository;
  /*% } %*/

  @Inject
  private FileUploadImport fileUploadImport;

  /**
   * Read the first line of the file, it can be provided in the body of the
   * request or via an URL
   */
  @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ImportFileJSON obtainHeader(MultipartHttpServletRequest request)
    throws ImportException {
    Set<StandardDataImportService> importers = new HashSet<>();
    /*% if (feature.DM_DI_DF_CSV) { %*/
    importers.add(csvImportService);
    /*% } %*/
    /*% if (feature.DM_DI_DF_Spreadsheet) { %*/
    importers.add(excelImportService);
    importers.add(odsImportService);
    /*% } %*/
    /*% if (feature.DM_DI_DF_Shapefile) { %*/
    importers.add(gisImportService);
    /*% } %*/
    for (StandardDataImportService importer : importers) {
      try {
        return importer.uploadAndProcessHeader(request);
      } catch (FileNotSupportedException | TypeNotSupportedException e) {
        // Try with next importer
      }
    }

    throw new ImportFileNotSupportedException();

  }

  /**
   * Imports data of the file, it can be provided in the body of the request
   * or via an URL
   */
  @RequestMapping(method = RequestMethod.PUT)
  public void processFile(@Validated @RequestBody ParseFormatJSON parseFormat)
    throws ImportException {
    Set<StandardDataImportService> importers = new HashSet<>();
    /*% if (feature.DM_DI_DF_CSV) { %*/
    importers.add(csvImportService);
    /*% } %*/
    /*% if (feature.DM_DI_DF_Spreadsheet) { %*/
    importers.add(excelImportService);
    importers.add(odsImportService);
    /*% } %*/
    /*% if (feature.DM_DI_DF_Shapefile) { %*/
    importers.add(gisImportService);
    /*% } %*/
    for (StandardDataImportService importer : importers) {
      try {
        File file = importer.processFile(parseFormat);
        if (file != null) {
          // Remove tmp file
          fileUploadImport.deleteTemporaryFile(file.getName());
          /*% if (feature.DM_DI_DF_Shapefile) { %*/
          // Remove tmp folder
          if (parseFormat.getType().equals("gis")) {
            fileUploadImport.deleteTemporaryFile(FilenameUtils.getBaseName(file.getAbsolutePath()));
          }
          /*% } %*/
        }
        return;
      } catch (FileNotSupportedException | TypeNotSupportedException e) {
        // Try with next importer
      }
    }
    throw new ImportFileNotSupportedException();
  }
  /*% if (feature.DM_DI_DF_Raster) { %*/

  @RequestMapping(value = "/raster", method = RequestMethod.PUT)
  public void importRaster(@Validated @RequestBody RasterJSON parseFormat) throws ImportException {
    rasterImportService.importRaster(parseFormat);
  }

  /**
   * Get entities in pages<br>
   * <p>
   * If no <code>pageNum</code> is provided, then all results will be returned in one page
   *
   * @param pageable  Contains all information about number page, items per page, order, ...
   * @param filters  Static filters to apply
   * @return Paginated entities
   */
  @GetMapping(value = "/raster/list")
  public ResponseEntity<Page<SDIRaster>> getAll(
    @PageableDefault(page = 0, size = 100000, sort = "id") Pageable pageable,
    @RequestParam(value = "filters", required = false) List<String> filters) {

    Page<SDIRaster> page =
      sdiRasterRepository.findAll(
        SpecificationUtil.getSpecificationFromFilters(filters, false), pageable);

    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, SDI_REST_URL);
    return new ResponseEntity<>(page, headers, HttpStatus.OK);
  }

  @DeleteMapping(value = "/raster/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    rasterImportService.delete(id);
    return ResponseEntity.ok().build();
  }

  /*% } %*/
}
/*% } %*/
