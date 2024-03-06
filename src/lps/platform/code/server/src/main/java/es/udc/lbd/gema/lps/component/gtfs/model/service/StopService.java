/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.service;

import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.StopDTO;
import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.StopFullDTO;
import es.udc.lbd.gema.lps.model.service.exceptions.NotFoundException;
import es.udc.lbd.gema.lps.model.service.exceptions.OperationNotAllowedException;
import es.udc.lbd.gema.lps.web.rest.custom.FeatureCollectionJSON;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StopService {

  Page<StopDTO> getAll(Pageable pageable, List<String> filters, String search);

  FeatureCollectionJSON getStopLoc(Boolean properties, List<String> filters);

  StopFullDTO get(String id) throws NotFoundException;

  StopFullDTO create(StopFullDTO stop) throws OperationNotAllowedException;

  StopFullDTO update(String id, StopFullDTO stop) throws OperationNotAllowedException;

  void delete(String id);
}
/*% } %*/
