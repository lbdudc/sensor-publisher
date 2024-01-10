/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.service;

import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.ShapeDTO;
import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.ShapeFullDTO;
import es.udc.lbd.gema.lps.model.service.exceptions.NotFoundException;
import es.udc.lbd.gema.lps.model.service.exceptions.OperationNotAllowedException;
import es.udc.lbd.gema.lps.web.rest.custom.FeatureCollectionJSON;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShapeService {

  Page<ShapeDTO> getAll(Pageable pageable, List<String> filters, String search);

  FeatureCollectionJSON getShapePtLoc(Boolean properties, List<String> filters);

  ShapeFullDTO get(Long id) throws NotFoundException;

  ShapeFullDTO create(ShapeFullDTO shape) throws OperationNotAllowedException;

  ShapeFullDTO update(Long id, ShapeFullDTO shape) throws OperationNotAllowedException;

  void delete(Long id);
}
/*% } %*/
