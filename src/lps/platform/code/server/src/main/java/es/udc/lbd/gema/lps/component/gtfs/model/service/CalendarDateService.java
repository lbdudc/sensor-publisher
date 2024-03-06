/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.service;

import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.CalendarDateDTO;
import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.CalendarDateFullDTO;
import es.udc.lbd.gema.lps.model.service.exceptions.NotFoundException;
import es.udc.lbd.gema.lps.model.service.exceptions.OperationNotAllowedException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CalendarDateService {

  Page<CalendarDateDTO> getAll(Pageable pageable, List<String> filters, String search);

  CalendarDateFullDTO get(String id) throws NotFoundException;

  CalendarDateFullDTO create(CalendarDateFullDTO calendarDate) throws OperationNotAllowedException;

  CalendarDateFullDTO update(String id, CalendarDateFullDTO calendarDate)
      throws OperationNotAllowedException;

  void delete(String id);
}
/*% } %*/
