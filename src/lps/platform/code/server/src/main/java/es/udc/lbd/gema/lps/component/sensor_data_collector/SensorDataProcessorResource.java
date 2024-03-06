/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.SensorDataProcessorService;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.util.exception.ConversionException;

import java.io.IOException;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(SensorDataProcessorResource.SEGMENT_DATA_PROCESSOR_RESOURCE_URL)
public class SensorDataProcessorResource {

  public static final String SEGMENT_DATA_PROCESSOR_RESOURCE_URL =
      "/api/sensor-data-collector/annotate";

  private static final Logger log = LoggerFactory.getLogger(SensorDataProcessorResource.class);

  @Inject private SensorDataProcessorService sensorDataProcessorService;

  @PostMapping
  public ResponseEntity<String> runAnnotationEngine(
      @RequestParam(name = "debug", defaultValue = "false") Boolean debug)
      throws JsonParseException, JsonMappingException, IOException, ConversionException {
    String executionLog = sensorDataProcessorService.processSensorData(debug);
    return new ResponseEntity<String>(executionLog, HttpStatus.OK);
  }
}

/*% } %*/
