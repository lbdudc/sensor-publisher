/*% if (feature.SensorViewer) { %*/
package es.udc.lbd.gema.lps.model.repository.sensor;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.sql.Timestamp;

public interface InstantProviderRepository {

  Page<LocalDateTime> getInstantsByDate(
      String sensorId, LocalDateTime dateInit, LocalDateTime dateEnd, Pageable pageable);

  Page<Timestamp> getDayInstantsByDate(
      String sensorId, LocalDateTime dateInit, LocalDateTime dateEnd, Pageable pageable);

  Page<Timestamp> getMonthInstantsByDate(
      String sensorId, LocalDateTime dateInit, LocalDateTime dateEnd, Pageable pageable);

  Page<Timestamp> getYearInstantsByDate(String sensorId, Pageable pageable);

  LocalDateTime getLastInstant(String sensorId);
}
/*% } %*/
