/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.repository;

import es.udc.lbd.gema.lps.component.gtfs.model.domain.FeedInfo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FeedInfoRepository
    extends JpaRepository<FeedInfo, Long>, JpaSpecificationExecutor<FeedInfo> {

  FeedInfo save(FeedInfo feedInfo);

  Optional<FeedInfo> findById(Long pk);

  Page<FeedInfo> findByIdIn(List<Long> pk, Pageable pageable);
}
/*% } %*/
