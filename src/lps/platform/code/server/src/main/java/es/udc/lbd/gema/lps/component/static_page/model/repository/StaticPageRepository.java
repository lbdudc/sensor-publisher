/*% if (feature.GUI_StaticPages) { %*/
package es.udc.lbd.gema.lps.component.static_page.model.repository;

import es.udc.lbd.gema.lps.component.static_page.model.domain.StaticPage;
import es.udc.lbd.gema.lps.component.static_page.model.domain.StaticPageI18n;
import es.udc.lbd.gema.lps.model.domain.language.Language;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface StaticPageRepository
  extends JpaRepository<StaticPage, Long>,
  JpaSpecificationExecutor<StaticPage>,
  PagingAndSortingRepository<StaticPage, Long> {

  Optional<StaticPage> findByDefinedId(String definedId);
}
/*% } %*/
