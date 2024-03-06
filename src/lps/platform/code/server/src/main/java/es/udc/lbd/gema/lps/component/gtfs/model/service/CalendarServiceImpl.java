/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.service;

import es.udc.lbd.gema.lps.component.gtfs.model.domain.Calendar;
import es.udc.lbd.gema.lps.component.gtfs.model.repository.CalendarRepository;
import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.CalendarDTO;
import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.CalendarFullDTO;
import es.udc.lbd.gema.lps.component.gtfs.specifications.CalendarSpecification;
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
public class CalendarServiceImpl implements CalendarService {

  @Inject private CalendarRepository calendarRepository;

  public Page<CalendarDTO> getAll(Pageable pageable, List<String> filters, String search) {
    Page<Calendar> page;
    if (search != null && !search.isEmpty()) {
      page = calendarRepository.findAll(CalendarSpecification.searchAll(search), pageable);
    } else {
      page =
          calendarRepository.findAll(
              SpecificationUtil.getSpecificationFromFilters(filters, false), pageable);
    }
    return page.map(CalendarDTO::new);
  }

  public CalendarFullDTO get(String id) throws NotFoundException {
    Calendar calendar = findById(id);
    return new CalendarFullDTO(calendar);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public CalendarFullDTO create(CalendarFullDTO calendarDto) throws OperationNotAllowedException {
    if (calendarDto.getId() != null) {
      throw new OperationNotAllowedException("calendar.error.id-exists");
    }
    Calendar calendarEntity = calendarDto.toCalendar();
    Calendar calendarSaved = calendarRepository.save(calendarEntity);
    return new CalendarFullDTO(calendarSaved);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public CalendarFullDTO update(String id, CalendarFullDTO calendarDto)
      throws OperationNotAllowedException {
    if (calendarDto.getId() == null) {
      throw new OperationNotAllowedException("calendar.error.id-not-exists");
    }
    if (!id.equals(calendarDto.getId())) {
      throw new OperationNotAllowedException("calendar.error.id-dont-match");
    }
    Calendar calendar =
        calendarRepository
            .findById(id)
            .orElseThrow(() -> new OperationNotAllowedException("calendar.error.id-not-exists"));
    Calendar calendarToUpdate = calendarDto.toCalendar();
    Calendar calendarUpdated = calendarRepository.save(calendarToUpdate);
    return new CalendarFullDTO(calendarUpdated);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public void delete(String id) {
    calendarRepository.deleteById(id);
  }

  /** PRIVATE METHODS * */
  private Calendar findById(String id) throws NotFoundException {
    return calendarRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Cannot find Calendar with id " + id));
  }
}
/*% } %*/
