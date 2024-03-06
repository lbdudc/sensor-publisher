/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.service;

import es.udc.lbd.gema.lps.component.gtfs.model.domain.Stop;
import es.udc.lbd.gema.lps.component.gtfs.model.repository.StopRepository;
import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.StopDTO;
import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.StopFullDTO;
import es.udc.lbd.gema.lps.component.gtfs.specifications.StopSpecification;
import es.udc.lbd.gema.lps.model.service.exceptions.NotFoundException;
import es.udc.lbd.gema.lps.model.service.exceptions.OperationNotAllowedException;
import es.udc.lbd.gema.lps.web.rest.custom.FeatureCollectionJSON;
import es.udc.lbd.gema.lps.web.rest.custom.FeatureJSON;
import es.udc.lbd.gema.lps.web.rest.util.specification_utils.*;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class StopServiceImpl implements StopService {

  @Inject private StopRepository stopRepository;

  public Page<StopDTO> getAll(Pageable pageable, List<String> filters, String search) {
    Page<Stop> page;
    if (search != null && !search.isEmpty()) {
      page = stopRepository.findAll(StopSpecification.searchAll(search), pageable);
    } else {
      page =
          stopRepository.findAll(
              SpecificationUtil.getSpecificationFromFilters(filters, false), pageable);
    }
    return page.map(StopDTO::new);
  }

  public FeatureCollectionJSON getStopLoc(Boolean properties, List<String> filters) {
    List<Stop> list =
        stopRepository.findAll(SpecificationUtil.getSpecificationFromFilters(filters, false));

    List<FeatureJSON> ret =
        list.stream()
            .map(
                e -> {
                  FeatureJSON geoJSON = new FeatureJSON();
                  if (properties) {
                    geoJSON = new FeatureJSON(Stop.class, e);
                  } else {
                    geoJSON.setProperties(new HashMap());
                  }
                  geoJSON.getProperties().put("displayString", "" + e.getStopId() + "");
                  geoJSON.setGeometry(e.getStopLoc());
                  return geoJSON;
                })
            .filter(e -> e.getGeometry() != null)
            .collect(Collectors.toList());
    return new FeatureCollectionJSON(ret);
  }

  public StopFullDTO get(String id) throws NotFoundException {
    Stop stop = findById(id);
    return new StopFullDTO(stop);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public StopFullDTO create(StopFullDTO stopDto) throws OperationNotAllowedException {
    if (stopDto.getStopId() != null) {
      throw new OperationNotAllowedException("stop.error.id-exists");
    }
    Stop stopEntity = stopDto.toStop();
    Stop stopSaved = stopRepository.save(stopEntity);
    return new StopFullDTO(stopSaved);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public StopFullDTO update(String id, StopFullDTO stopDto) throws OperationNotAllowedException {
    if (stopDto.getStopId() == null) {
      throw new OperationNotAllowedException("stop.error.id-not-exists");
    }
    if (!id.equals(stopDto.getStopId())) {
      throw new OperationNotAllowedException("stop.error.id-dont-match");
    }
    Stop stop =
        stopRepository
            .findById(id)
            .orElseThrow(() -> new OperationNotAllowedException("stop.error.id-not-exists"));
    Stop stopToUpdate = stopDto.toStop();
    Stop stopUpdated = stopRepository.save(stopToUpdate);
    return new StopFullDTO(stopUpdated);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public void delete(String id) {
    stopRepository.deleteById(id);
  }

  /** PRIVATE METHODS * */
  private Stop findById(String id) throws NotFoundException {
    return stopRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Cannot find Stop with id " + id));
  }
}
/*% } %*/
