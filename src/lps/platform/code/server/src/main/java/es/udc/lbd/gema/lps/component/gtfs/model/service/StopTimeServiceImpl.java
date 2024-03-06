/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.service;

import es.udc.lbd.gema.lps.component.gtfs.model.domain.StopTime;
import es.udc.lbd.gema.lps.component.gtfs.model.repository.StopTimeRepository;
import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.StopTimeDTO;
import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.StopTimeFullDTO;
import es.udc.lbd.gema.lps.component.gtfs.specifications.StopTimeSpecification;
import es.udc.lbd.gema.lps.model.service.exceptions.NotFoundException;
import es.udc.lbd.gema.lps.model.service.exceptions.OperationNotAllowedException;
import es.udc.lbd.gema.lps.web.rest.util.specification_utils.*;
import java.util.List;
import javax.inject.Inject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class StopTimeServiceImpl implements StopTimeService {

  @Inject private StopTimeRepository stopTimeRepository;

  public Page<StopTimeDTO> getAll(Pageable pageable, List<String> filters, String search) {
    Page<StopTime> page;
    if (search != null && !search.isEmpty()) {
      page = stopTimeRepository.findAll(StopTimeSpecification.searchAll(search), pageable);
    } else {
      page =
          stopTimeRepository.findAll(
              SpecificationUtil.getSpecificationFromFilters(filters, false), pageable);
    }
    return page.map(StopTimeDTO::new);
  }

  public StopTimeFullDTO get(Long id) throws NotFoundException {
    StopTime stopTime = findById(id);
    return new StopTimeFullDTO(stopTime);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public StopTimeFullDTO create(StopTimeFullDTO stopTimeDto) throws OperationNotAllowedException {
    if (stopTimeDto.getId() != null) {
      throw new OperationNotAllowedException("stopTime.error.id-exists");
    }
    StopTime stopTimeEntity = stopTimeDto.toStopTime();
    StopTime stopTimeSaved = stopTimeRepository.save(stopTimeEntity);
    return new StopTimeFullDTO(stopTimeSaved);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public StopTimeFullDTO update(Long id, StopTimeFullDTO stopTimeDto)
      throws OperationNotAllowedException {
    if (stopTimeDto.getId() == null) {
      throw new OperationNotAllowedException("stopTime.error.id-not-exists");
    }
    if (!id.equals(stopTimeDto.getId())) {
      throw new OperationNotAllowedException("stopTime.error.id-dont-match");
    }
    StopTime stopTime =
        stopTimeRepository
            .findById(id)
            .orElseThrow(() -> new OperationNotAllowedException("stopTime.error.id-not-exists"));
    StopTime stopTimeToUpdate = stopTimeDto.toStopTime();
    StopTime stopTimeUpdated = stopTimeRepository.save(stopTimeToUpdate);
    return new StopTimeFullDTO(stopTimeUpdated);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public void delete(Long id) {
    stopTimeRepository.deleteById(id);
  }

  /** PRIVATE METHODS * */
  private StopTime findById(Long id) throws NotFoundException {
    return stopTimeRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Cannot find StopTime with id " + id));
  }
}
/*% } %*/
