/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.service;

import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.StopTimeDTO;
import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.StopTimeFullDTO;
import es.udc.lbd.gema.lps.model.service.exceptions.NotFoundException;
import es.udc.lbd.gema.lps.model.service.exceptions.OperationNotAllowedException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StopTimeService {

  Page<StopTimeDTO> getAll(Pageable pageable, List<String> filters, String search);

  StopTimeFullDTO get(Long id) throws NotFoundException;

  StopTimeFullDTO create(StopTimeFullDTO stopTime) throws OperationNotAllowedException;

  StopTimeFullDTO update(Long id, StopTimeFullDTO stopTime) throws OperationNotAllowedException;

  void delete(Long id);
}
/*% } %*/
