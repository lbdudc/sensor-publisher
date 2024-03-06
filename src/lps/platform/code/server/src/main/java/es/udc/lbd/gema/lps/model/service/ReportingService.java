/*% if (feature.GUI_L_Export) { %*/
package es.udc.lbd.gema.lps.model.service;

import es.udc.lbd.gema.lps.component.entities_information.EntityService;
import es.udc.lbd.gema.lps.component.entities_information.custom.EntityJSON;
import es.udc.lbd.gema.lps.component.entities_information.custom.EntityPropertyJSON;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class ReportingService {

  @Inject private EntityService entityService;

  public <T> Workbook generateReport(
      String string, Class<T> clazz, JpaRepository<T, Long> repository) {
    List<T> items;
    Integer nFila = 0;
    Integer nCol = 0;
    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet(string);

    // Domain entities
    Collection<EntityJSON> entities = entityService.getEntities();

    // Entity Properties
    Collection<EntityPropertyJSON> properties = entityService.getEntity(string).getProperties();
    Collection<EntityPropertyJSON> validProperties = new ArrayList<>();

    // Header row setup
    Row header = sheet.createRow(nFila);
    nFila++;
    CellStyle headerCellStyle = workbook.createCellStyle();
    XSSFFont headerFont = (XSSFFont) workbook.createFont();
    headerFont.setBold(true);
    headerCellStyle.setBorderBottom(BorderStyle.THIN);
    headerCellStyle.setBorderRight(BorderStyle.THIN);
    headerCellStyle.setFont(headerFont);
    for (EntityPropertyJSON prop : properties) {
      if (entities.stream()
          .map(en -> en.getSimpleName())
          .collect(Collectors.toList())
          .contains(prop.getSimpleType())) {
        // Asociacion: no hacer nada todavia
      } else {
        validProperties.add(prop);
        Cell cell = header.createCell(nCol);
        cell.setCellValue(prop.getName().toUpperCase());
        cell.setCellStyle(headerCellStyle);
        nCol++;
      }
    }

    items = repository.findAll();

    // Writing each data entry in a row
    for (T item : items) {
      Row row = sheet.createRow(nFila);
      nCol = 0;
      nFila++;
      // For each header property:
      for (EntityPropertyJSON prop : validProperties) {
        try {
          Object value = PropertyUtils.getProperty(item, prop.getName());
          if (value != null) {
            row.createCell(nCol).setCellValue(value.toString());
          }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
          e.printStackTrace();
        }
        nCol++;
      }
    }

    return workbook;
  }
}
/*% } %*/