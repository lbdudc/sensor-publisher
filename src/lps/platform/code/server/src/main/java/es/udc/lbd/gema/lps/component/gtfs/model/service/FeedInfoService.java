/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.service;

import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.FeedInfoDTO;
import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.FeedInfoFullDTO;
import es.udc.lbd.gema.lps.model.service.exceptions.NotFoundException;
import es.udc.lbd.gema.lps.model.service.exceptions.OperationNotAllowedException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FeedInfoService {

  Page<FeedInfoDTO> getAll(Pageable pageable, List<String> filters, String search);

  FeedInfoFullDTO get(Long id) throws NotFoundException;

  FeedInfoFullDTO create(FeedInfoFullDTO feedInfo) throws OperationNotAllowedException;

  FeedInfoFullDTO update(Long id, FeedInfoFullDTO feedInfo) throws OperationNotAllowedException;

  void delete(Long id);
}
/*% } %*/
