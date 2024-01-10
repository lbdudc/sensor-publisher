/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.service;

import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.CalendarDTO;
import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.CalendarFullDTO;
import es.udc.lbd.gema.lps.model.service.exceptions.NotFoundException;
import es.udc.lbd.gema.lps.model.service.exceptions.OperationNotAllowedException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CalendarService {

  Page<CalendarDTO> getAll(Pageable pageable, List<String> filters, String search);

  CalendarFullDTO get(String id) throws NotFoundException;

  CalendarFullDTO create(CalendarFullDTO calendar) throws OperationNotAllowedException;

  CalendarFullDTO update(String id, CalendarFullDTO calendar) throws OperationNotAllowedException;

  void delete(String id);
}
/*% } %*/
