/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.service;

import es.udc.lbd.gema.lps.component.gtfs.model.domain.Agency;
import es.udc.lbd.gema.lps.component.gtfs.model.repository.AgencyRepository;
import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.AgencyDTO;
import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.AgencyFullDTO;
import es.udc.lbd.gema.lps.component.gtfs.specifications.AgencySpecification;
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
public class AgencyServiceImpl implements AgencyService {

  @Inject private AgencyRepository agencyRepository;

  public Page<AgencyDTO> getAll(Pageable pageable, List<String> filters, String search) {
    Page<Agency> page;
    if (search != null && !search.isEmpty()) {
      page = agencyRepository.findAll(AgencySpecification.searchAll(search), pageable);
    } else {
      page =
          agencyRepository.findAll(
              SpecificationUtil.getSpecificationFromFilters(filters, false), pageable);
    }
    return page.map(AgencyDTO::new);
  }

  public AgencyFullDTO get(String id) throws NotFoundException {
    Agency agency = findById(id);
    return new AgencyFullDTO(agency);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public AgencyFullDTO create(AgencyFullDTO agencyDto) throws OperationNotAllowedException {
    if (agencyDto.getAgencyId() == null) {
      throw new OperationNotAllowedException("agency.error.id-not-exists");
    }
    Agency agencyEntity = agencyDto.toAgency();
    Agency agencySaved = agencyRepository.save(agencyEntity);
    return new AgencyFullDTO(agencySaved);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public AgencyFullDTO update(String id, AgencyFullDTO agencyDto)
      throws OperationNotAllowedException {
    if (agencyDto.getAgencyId() == null) {
      throw new OperationNotAllowedException("agency.error.id-not-exists");
    }
    if (!id.equals(agencyDto.getAgencyId())) {
      throw new OperationNotAllowedException("agency.error.id-dont-match");
    }
    Agency agency =
        agencyRepository
            .findById(id)
            .orElseThrow(() -> new OperationNotAllowedException("agency.error.id-not-exists"));
    Agency agencyToUpdate = agencyDto.toAgency();
    Agency agencyUpdated = agencyRepository.save(agencyToUpdate);
    return new AgencyFullDTO(agencyUpdated);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public void delete(String id) {
    agencyRepository.deleteById(id);
  }

  /** PRIVATE METHODS * */
  private Agency findById(String id) throws NotFoundException {
    return agencyRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Cannot find Agency with id " + id));
  }
}
/*% } %*/
