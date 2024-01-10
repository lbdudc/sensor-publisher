/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.service;

import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.AgencyDTO;
import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.AgencyFullDTO;
import es.udc.lbd.gema.lps.model.service.exceptions.NotFoundException;
import es.udc.lbd.gema.lps.model.service.exceptions.OperationNotAllowedException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AgencyService {

  Page<AgencyDTO> getAll(Pageable pageable, List<String> filters, String search);

  AgencyFullDTO get(String id) throws NotFoundException;

  AgencyFullDTO create(AgencyFullDTO agency) throws OperationNotAllowedException;

  AgencyFullDTO update(String id, AgencyFullDTO agency) throws OperationNotAllowedException;

  void delete(String id);
}
/*% } %*/
