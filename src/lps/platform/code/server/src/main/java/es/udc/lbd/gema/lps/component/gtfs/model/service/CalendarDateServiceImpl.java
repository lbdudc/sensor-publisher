/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.service;

import es.udc.lbd.gema.lps.component.gtfs.model.domain.CalendarDate;
import es.udc.lbd.gema.lps.component.gtfs.model.repository.CalendarDateRepository;
import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.CalendarDateDTO;
import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.CalendarDateFullDTO;
import es.udc.lbd.gema.lps.component.gtfs.specifications.CalendarDateSpecification;
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
public class CalendarDateServiceImpl implements CalendarDateService {

  @Inject private CalendarDateRepository calendarDateRepository;

  public Page<CalendarDateDTO> getAll(Pageable pageable, List<String> filters, String search) {
    Page<CalendarDate> page;
    if (search != null && !search.isEmpty()) {
      page = calendarDateRepository.findAll(CalendarDateSpecification.searchAll(search), pageable);
    } else {
      page =
          calendarDateRepository.findAll(
              SpecificationUtil.getSpecificationFromFilters(filters, false), pageable);
    }
    return page.map(CalendarDateDTO::new);
  }

  public CalendarDateFullDTO get(String id) throws NotFoundException {
    CalendarDate calendarDates = findById(id);
    return new CalendarDateFullDTO(calendarDates);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public CalendarDateFullDTO create(CalendarDateFullDTO calendarDatesDto)
      throws OperationNotAllowedException {
    if (calendarDatesDto.getId() != null) {
      throw new OperationNotAllowedException("calendarDates.error.id-exists");
    }
    CalendarDate calendarDatesEntity = calendarDatesDto.toCalendarDate();
    CalendarDate calendarDatesSaved = calendarDateRepository.save(calendarDatesEntity);
    return new CalendarDateFullDTO(calendarDatesSaved);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public CalendarDateFullDTO update(String id, CalendarDateFullDTO calendarDatesDto)
      throws OperationNotAllowedException {
    if (calendarDatesDto.getId() == null) {
      throw new OperationNotAllowedException("calendarDates.error.id-not-exists");
    }
    if (!id.equals(calendarDatesDto.getId())) {
      throw new OperationNotAllowedException("calendarDates.error.id-dont-match");
    }
    CalendarDate calendarDates =
        calendarDateRepository
            .findById(id)
            .orElseThrow(
                () -> new OperationNotAllowedException("calendarDates.error.id-not-exists"));
    CalendarDate calendarDatesToUpdate = calendarDatesDto.toCalendarDate();
    CalendarDate calendarDatesUpdated = calendarDateRepository.save(calendarDatesToUpdate);
    return new CalendarDateFullDTO(calendarDatesUpdated);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public void delete(String id) {
    calendarDateRepository.deleteById(id);
  }

  /** PRIVATE METHODS * */
  private CalendarDate findById(String id) throws NotFoundException {
    return calendarDateRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Cannot find CalendarDate with id " + id));
  }
}
/*% } %*/
