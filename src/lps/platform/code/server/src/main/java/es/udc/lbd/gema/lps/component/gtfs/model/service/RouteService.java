/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.service;

import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.RouteDTO;
import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.RouteFullDTO;
import es.udc.lbd.gema.lps.model.service.exceptions.NotFoundException;
import es.udc.lbd.gema.lps.model.service.exceptions.OperationNotAllowedException;
import es.udc.lbd.gema.lps.web.rest.custom.FeatureCollectionJSON;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RouteService {

  Page<RouteDTO> getAll(Pageable pageable, List<String> filters, String search);

  FeatureCollectionJSON getShape(Boolean properties, List<String> filters);

  RouteFullDTO get(String id) throws NotFoundException;

  RouteFullDTO create(RouteFullDTO route) throws OperationNotAllowedException;

  RouteFullDTO update(String id, RouteFullDTO route) throws OperationNotAllowedException;

  void delete(String id);
}
/*% } %*/
