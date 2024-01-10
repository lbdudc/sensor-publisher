/*% if (feature.MWM_TA_SensorDataCollector) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector;

import es.udc.lbd.gema.lps.component.gema.model.service.EmployeeService;
import es.udc.lbd.gema.lps.component.gema.model.service.dto.EmployeeDTO;
import java.util.List;
import javax.inject.Inject;
import net.kaczmarzyk.spring.data.jpa.domain.In;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(EmployeeSensorDataResource.EMPLOYEE_RESOURCE_URL)
public class EmployeeSensorDataResource {

  public static final String EMPLOYEE_RESOURCE_URL = "/api/sensor-data-collector/employees";;

  private static final Logger log = LoggerFactory.getLogger(EmployeeSensorDataResource.class);

  @Inject private EmployeeService employeeService;

  @GetMapping
  public ResponseEntity<List<EmployeeDTO>> findAllWithCurrentPosition(
    @Spec(path = "fullName", paramSeparator = ',', spec = In.class) Specification spec,
    @RequestParam(name = "bbox", required = false) String wkt) {

    List<EmployeeDTO> employeesDTO;
    Geometry bbox = null;

    if (wkt != null) {
      WKTReader reader = new WKTReader();
      try {
        bbox = reader.read(wkt);
      } catch (ParseException e) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }
    }

    employeesDTO = employeeService.findAll(spec, bbox);

    return new ResponseEntity<>(employeesDTO, HttpStatus.OK);
  }
}
/*% } %*/
