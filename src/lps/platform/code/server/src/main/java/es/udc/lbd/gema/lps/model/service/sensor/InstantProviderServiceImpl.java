/*% if (feature.SensorViewer) { %*/
package es.udc.lbd.gema.lps.model.service.sensor;

import es.udc.lbd.gema.lps.model.repository.sensor.InstantProviderRepository;
import es.udc.lbd.gema.lps.model.service.dto.sensor.InstantDTO;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.inject.Inject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class InstantProviderServiceImpl implements InstantProviderService {
  @Inject
  private InstantProviderRepository instantProviderRepository;

  @Override
  public Page<InstantDTO> findInstantsByDate(String sensorId, LocalDate date, Pageable pageable) {
    return instantProviderRepository
        .getInstantsByDate(
            sensorId,
            date.atStartOfDay(),
            date.atTime(LocalTime.MAX),
            pageable)
        .map(d -> new InstantDTO<>(d, "H:mm"));
  }

  @Override
  public Page<InstantDTO> findDayInstantsByDate(String sensorId, LocalDate date, Pageable pageable) {
    return instantProviderRepository
        .getDayInstantsByDate(
            sensorId,
            date.atStartOfDay(),
            date.plusMonths(1).atStartOfDay(),
            pageable)
        .map(d -> new InstantDTO<>(d, "d"));
  }

  @Override
  public Page<InstantDTO> findMonthInstantsByDate(String sensorId, LocalDate date, Pageable pageable) {
    return instantProviderRepository
        .getMonthInstantsByDate(
            sensorId,
            date.atStartOfDay(),
            date.plusYears(1).atStartOfDay(),
            pageable)
        .map(d -> new InstantDTO<>(d, "M"));
  }

  @Override
  public Page<InstantDTO> findYearInstants(String sensorId, Pageable pageable) {
    return instantProviderRepository
        .getYearInstantsByDate(sensorId, pageable)
        .map(d -> new InstantDTO<>(d, "yyyy"));
  }

  @Override
  public List<InstantDTO<Object>> findAllYearInstants(String sensorId) {
    return instantProviderRepository
        .getYearInstantsByDate(sensorId, Pageable.unpaged())
        .map(d -> new InstantDTO<>(d.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy"))))
        .getContent();
  }

  @Override
  public String findLastInstant(String sensorId) {
    return instantProviderRepository
        .getLastInstant(sensorId)
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
  }
}
/*% } %*/
