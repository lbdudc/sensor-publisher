/*% if (feature.UserManagement) { %*/
package es.udc.lbd.gema.lps.model.repository.user_management;

import es.udc.lbd.gema.lps.model.domain.user_management.PersistentToken;
import es.udc.lbd.gema.lps.model.domain.user_management.UMUser;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the PersistentToken entity.
 */
public interface PersistentTokenRepository extends JpaRepository<PersistentToken, String> {

    List<PersistentToken> findByUser(UMUser user);

    List<PersistentToken> findByTokenDateBefore(LocalDate localDate);

}
/*% } %*/
