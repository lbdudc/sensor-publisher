/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.service;

import es.udc.lbd.gema.lps.component.gtfs.model.domain.FeedInfo;
import es.udc.lbd.gema.lps.component.gtfs.model.repository.FeedInfoRepository;
import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.FeedInfoDTO;
import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.FeedInfoFullDTO;
import es.udc.lbd.gema.lps.component.gtfs.specifications.FeedInfoSpecification;
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
public class FeedInfoServiceImpl implements FeedInfoService {

  @Inject private FeedInfoRepository feedInfoRepository;

  public Page<FeedInfoDTO> getAll(Pageable pageable, List<String> filters, String search) {
    Page<FeedInfo> page;
    if (search != null && !search.isEmpty()) {
      page = feedInfoRepository.findAll(FeedInfoSpecification.searchAll(search), pageable);
    } else {
      page =
          feedInfoRepository.findAll(
              SpecificationUtil.getSpecificationFromFilters(filters, false), pageable);
    }
    return page.map(FeedInfoDTO::new);
  }

  public FeedInfoFullDTO get(Long id) throws NotFoundException {
    FeedInfo feedInfo = findById(id);
    return new FeedInfoFullDTO(feedInfo);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public FeedInfoFullDTO create(FeedInfoFullDTO feedInfoDto) throws OperationNotAllowedException {
    if (feedInfoDto.getId() != null) {
      throw new OperationNotAllowedException("feedInfo.error.id-exists");
    }
    FeedInfo feedInfoEntity = feedInfoDto.toFeedInfo();
    FeedInfo feedInfoSaved = feedInfoRepository.save(feedInfoEntity);
    return new FeedInfoFullDTO(feedInfoSaved);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public FeedInfoFullDTO update(Long id, FeedInfoFullDTO feedInfoDto)
      throws OperationNotAllowedException {
    if (feedInfoDto.getId() == null) {
      throw new OperationNotAllowedException("feedInfo.error.id-not-exists");
    }
    if (!id.equals(feedInfoDto.getId())) {
      throw new OperationNotAllowedException("feedInfo.error.id-dont-match");
    }
    FeedInfo feedInfo =
        feedInfoRepository
            .findById(id)
            .orElseThrow(() -> new OperationNotAllowedException("feedInfo.error.id-not-exists"));
    FeedInfo feedInfoToUpdate = feedInfoDto.toFeedInfo();
    FeedInfo feedInfoUpdated = feedInfoRepository.save(feedInfoToUpdate);
    return new FeedInfoFullDTO(feedInfoUpdated);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public void delete(Long id) {
    feedInfoRepository.deleteById(id);
  }

  /** PRIVATE METHODS * */
  private FeedInfo findById(Long id) throws NotFoundException {
    return feedInfoRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Cannot find FeedInfo with id " + id));
  }
}
/*% } %*/
