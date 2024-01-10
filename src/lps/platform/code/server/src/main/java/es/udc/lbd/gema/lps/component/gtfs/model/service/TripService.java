/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.service;

import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.TripDTO;
import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.TripFullDTO;
import es.udc.lbd.gema.lps.model.service.exceptions.NotFoundException;
import es.udc.lbd.gema.lps.model.service.exceptions.OperationNotAllowedException;
import es.udc.lbd.gema.lps.web.rest.custom.FeatureCollectionJSON;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TripService {

  Page<TripDTO> getAll(Pageable pageable, List<String> filters, String search);

  FeatureCollectionJSON getShape(Boolean properties, List<String> filters);

  TripFullDTO get(String id) throws NotFoundException;

  TripFullDTO create(TripFullDTO trip) throws OperationNotAllowedException;

  TripFullDTO update(String id, TripFullDTO trip) throws OperationNotAllowedException;

  void delete(String id);

}
/*% } %*/
