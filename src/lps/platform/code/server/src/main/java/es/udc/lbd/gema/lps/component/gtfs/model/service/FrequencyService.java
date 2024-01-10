/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.service;

import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.FrequencyDTO;
import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.FrequencyFullDTO;
import es.udc.lbd.gema.lps.model.service.exceptions.NotFoundException;
import es.udc.lbd.gema.lps.model.service.exceptions.OperationNotAllowedException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FrequencyService {

  Page<FrequencyDTO> getAll(Pageable pageable, List<String> filters, String search);

  FrequencyFullDTO get(Long id) throws NotFoundException;

  FrequencyFullDTO create(FrequencyFullDTO frequencies) throws OperationNotAllowedException;

  FrequencyFullDTO update(Long id, FrequencyFullDTO frequencies)
      throws OperationNotAllowedException;

  void delete(Long id);
}
/*% } %*/
