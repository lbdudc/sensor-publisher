/*% if (feature.UserManagement) { %*/
package es.udc.lbd.gema.lps.model.repository.user_management;

import es.udc.lbd.gema.lps.model.domain.user_management.UMUser;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface UMUserRepository
  extends JpaRepository<UMUser, Long>, JpaSpecificationExecutor<UMUser> {
    /*% if (feature.UM_AccountActivation) { %*/
    Optional<UMUser> findOneByActivationKey(String activationKey);

    List<UMUser> findAllByActivatedIsFalseAndCreatedDateBefore(ZonedDateTime dateTime);
    /*% } %*/

    Optional<UMUser> findOneByResetKey(String resetKey);

    Optional<UMUser> findOneByEmail(String email);

    Optional<UMUser> findOneByLogin(String login);

    @Query(value = "select distinct user from UMUser user left join user.authorities",
        countQuery = "select count(user) from UMUser user")
    Page<UMUser> findAllWithAuthorities(Pageable pageable);

    /*% if(!feature.UM_AA_ByEmail && feature.UM_AA_ByAdmin) { %*/
    /**
     * @param authority Authority name
     * @return Users with specified authority
     */
    @Query(value = "SELECT user FROM UMUser user JOIN user.authorities a WHERE a.name LIKE %?1 ")
    List<UMUser> findAllWithAuthorityName(String authority);
    /*% } %*/
}
/*% } %*/
