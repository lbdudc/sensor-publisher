/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.service;

import es.udc.lbd.gema.lps.component.gtfs.model.domain.Route;
import es.udc.lbd.gema.lps.component.gtfs.model.repository.RouteRepository;
import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.RouteDTO;
import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.RouteFullDTO;
import es.udc.lbd.gema.lps.component.gtfs.specifications.RouteSpecification;
import es.udc.lbd.gema.lps.model.service.exceptions.NotFoundException;
import es.udc.lbd.gema.lps.model.service.exceptions.OperationNotAllowedException;
import es.udc.lbd.gema.lps.web.rest.custom.FeatureCollectionJSON;
import es.udc.lbd.gema.lps.web.rest.custom.FeatureJSON;
import es.udc.lbd.gema.lps.web.rest.util.specification_utils.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.locationtech.jts.geom.Geometry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class RouteServiceImpl implements RouteService {

  @Inject private RouteRepository routeRepository;

  public Page<RouteDTO> getAll(Pageable pageable, List<String> filters, String search) {
    Page<Route> page;
    if (search != null && !search.isEmpty()) {
      page = routeRepository.findAll(RouteSpecification.searchAll(search), pageable);
    } else {
      page =
          routeRepository.findAll(
              SpecificationUtil.getSpecificationFromFilters(filters, false), pageable);
    }
    return page.map(RouteDTO::new);
  }

  public FeatureCollectionJSON getShape(Boolean properties, List<String> filters) {
    List<Route> list =
        routeRepository.findAll(SpecificationUtil.getSpecificationFromFilters(filters, false));

    Map<Geometry, Route> geometryRoutes =
        routeRepository.findRoutesShapes(
            list.stream().map(r -> r.getRouteId()).collect(Collectors.toList()));
    List<FeatureJSON> ret = new ArrayList<FeatureJSON>();
    for (Map.Entry<Geometry, Route> entry : geometryRoutes.entrySet()) {
      FeatureJSON geoJSON = new FeatureJSON();
      Route route = entry.getValue();
      if (properties) {
        geoJSON = new FeatureJSON(Route.class, route);
      } else {
        geoJSON.setProperties(new HashMap());
      }
      geoJSON.getProperties().put("displayString", "" + route.getRouteId() + "");
      geoJSON.setGeometry(entry.getKey());
      ret.add(geoJSON);
    }
    return new FeatureCollectionJSON(ret);
  }

  public RouteFullDTO get(String id) throws NotFoundException {
    Route route = findById(id);
    return new RouteFullDTO(route);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public RouteFullDTO create(RouteFullDTO routeDto) throws OperationNotAllowedException {
    if (routeDto.getRouteId() == null) {
      throw new OperationNotAllowedException("route.error.id-not-exists");
    }
    Route routeEntity = routeDto.toRoute();
    Route routeSaved = routeRepository.save(routeEntity);
    return new RouteFullDTO(routeSaved);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public RouteFullDTO update(String id, RouteFullDTO routeDto) throws OperationNotAllowedException {
    if (routeDto.getRouteId() == null) {
      throw new OperationNotAllowedException("route.error.id-not-exists");
    }
    if (!id.equals(routeDto.getRouteId())) {
      throw new OperationNotAllowedException("route.error.id-dont-match");
    }
    Route route =
        routeRepository
            .findById(id)
            .orElseThrow(() -> new OperationNotAllowedException("route.error.id-not-exists"));
    Route routeToUpdate = routeDto.toRoute();
    Route routeUpdated = routeRepository.save(routeToUpdate);
    return new RouteFullDTO(routeUpdated);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public void delete(String id) {
    routeRepository.deleteById(id);
  }

  /** PRIVATE METHODS * */
  private Route findById(String id) throws NotFoundException {
    return routeRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Cannot find Route with id " + id));
  }
}
/*% } %*/
