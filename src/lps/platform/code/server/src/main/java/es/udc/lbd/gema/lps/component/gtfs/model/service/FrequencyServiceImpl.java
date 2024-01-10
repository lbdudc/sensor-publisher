/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.service;

import es.udc.lbd.gema.lps.component.gtfs.model.domain.Frequency;
import es.udc.lbd.gema.lps.component.gtfs.model.repository.FrequencyRepository;
import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.FrequencyDTO;
import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.FrequencyFullDTO;
import es.udc.lbd.gema.lps.component.gtfs.specifications.FrequencySpecification;
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
public class FrequencyServiceImpl implements FrequencyService {

  @Inject private FrequencyRepository frequenciesRepository;

  public Page<FrequencyDTO> getAll(Pageable pageable, List<String> filters, String search) {
    Page<Frequency> page;
    if (search != null && !search.isEmpty()) {
      page = frequenciesRepository.findAll(FrequencySpecification.searchAll(search), pageable);
    } else {
      page =
          frequenciesRepository.findAll(
              SpecificationUtil.getSpecificationFromFilters(filters, false), pageable);
    }
    return page.map(FrequencyDTO::new);
  }

  public FrequencyFullDTO get(Long id) throws NotFoundException {
    Frequency frequencies = findById(id);
    return new FrequencyFullDTO(frequencies);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public FrequencyFullDTO create(FrequencyFullDTO frequenciesDto)
      throws OperationNotAllowedException {
    if (frequenciesDto.getId() != null) {
      throw new OperationNotAllowedException("frequencies.error.id-exists");
    }
    Frequency frequenciesEntity = frequenciesDto.toFrequency();
    Frequency frequenciesSaved = frequenciesRepository.save(frequenciesEntity);
    return new FrequencyFullDTO(frequenciesSaved);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public FrequencyFullDTO update(Long id, FrequencyFullDTO frequenciesDto)
      throws OperationNotAllowedException {
    if (frequenciesDto.getId() == null) {
      throw new OperationNotAllowedException("frequencies.error.id-not-exists");
    }
    if (!id.equals(frequenciesDto.getId())) {
      throw new OperationNotAllowedException("frequencies.error.id-dont-match");
    }
    Frequency frequencies =
        frequenciesRepository
            .findById(id)
            .orElseThrow(() -> new OperationNotAllowedException("frequencies.error.id-not-exists"));
    Frequency frequenciesToUpdate = frequenciesDto.toFrequency();
    Frequency frequenciesUpdated = frequenciesRepository.save(frequenciesToUpdate);
    return new FrequencyFullDTO(frequenciesUpdated);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public void delete(Long id) {
    frequenciesRepository.deleteById(id);
  }

  /** PRIVATE METHODS * */
  private Frequency findById(Long id) throws NotFoundException {
    return frequenciesRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Cannot find Frequency with id " + id));
  }
}
/*% } %*/
