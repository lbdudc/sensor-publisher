/*% if (feature.UserManagement) { %*/
package es.udc.lbd.gema.lps.web.rest.custom.user_management;

import es.udc.lbd.gema.lps.config.Constants;
import es.udc.lbd.gema.lps.config.Properties;
import es.udc.lbd.gema.lps.model.domain.user_management.UMUser;
import es.udc.lbd.gema.lps.model.repository.user_management.UMUserRepository;
/*% if (feature.T_MailSender) { %*/
import es.udc.lbd.gema.lps.model.service.MailService;
/*% } %*/
import es.udc.lbd.gema.lps.model.service.UMUserService;
import es.udc.lbd.gema.lps.model.service.exceptions.account.AccountException;
import es.udc.lbd.gema.lps.model.service.exceptions.account.AccountEmailInUseException;
import es.udc.lbd.gema.lps.model.service.exceptions.account.AccountLoginInUseException;
import es.udc.lbd.gema.lps.security.SecurityUtils;
import es.udc.lbd.gema.lps.security.AuthoritiesConstants;
import es.udc.lbd.gema.lps.web.rest.custom.user_management.vm.UserJSON;
import es.udc.lbd.gema.lps.web.rest.util.PaginationUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing users.
 *
 * <p>This class accesses the User entity, and needs to fetch its collection of authorities.</p>
 * <p>
 * For a normal use-case, it would be better to have an eager relationship between User and Authority,
 * and send everything to the client side: there would be no View Model and DTO, a lot less code, and an outer-join
 * which would be good for performance.
 * </p>
 * <p>
 * We use a View Model and a DTO for 3 reasons:
 * <ul>
 * <li>We want to keep a lazy association between the user and the authorities, because people will
 * quite often do relationships with the user, and we don't want them to get the authorities all
 * the time for nothing (for performance reasons). This is the #1 goal: we should not impact our users'
 * application because of this use-case.</li>
 * <li> Not having an outer join causes n+1 requests to the database. This is not a real issue as
 * we have by default a second-level cache. This means on the first HTTP call we do the n+1 requests,
 * but then all authorities come from the cache, so in fact it's much better than doing an outer join
 * (which will get lots of data from the database, for each HTTP call).</li>
 * <li> As this manages users, for security reasons, we'd rather have a DTO layer.</li>
 * </ul>
 * <p>Another option would be to have a specific JPA entity graph to handle this case.</p>
 */
@RestController
@RequestMapping("/api/user-management")
public class UMUserResource {
/*% if (feature.UM_UserCRUD) { %*/

    private static final Logger log = LoggerFactory.getLogger(UMUserResource.class);

    @Inject
    private Properties properties;

    @Inject
    private UMUserRepository userRepository;
    /*% if (feature.UM_R_ByAdmin || feature.UM_AA_ByAdmin) { %*/
    @Inject
    private MailService mailService;
    /*% } %*/
    @Inject
    private UMUserService userService;

    @Inject
    private HttpServletRequest request;

    /*% if (feature.UM_R_ByAdmin) { %*/
    /**
     * POST  /users  : Creates a new user.
     * <p>
     * Creates a new user if the login and email are not already used
     * </p>
     *
     * @param userJSON the user to create
     * @return the ResponseEntity with status 201 (Created) and with body the new user, or with status 400 (Bad Request) if the login or email is already in use
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/users")
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<?> createUser(@RequestBody UserJSON userJSON) throws URISyntaxException, AccountException {
        log.debug("POST request to save User : {}", userJSON);

        UMUser user = userService.createUser(userJSON);
        // Send set account password mail to the user
        mailService.sendAccountPasswordSetEmail(user, properties.getClientHost());

        return ResponseEntity.created(new URI("/api/user-management/users/" + user.getLogin()))
            .body(user);
    }
    /*% } %*/
    /**
     * PUT  /users : Updates an existing User.
     *
     * @param userJSON the user to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated user,
     * or with status 400 (Bad Request) if the login or email is already in use,
     * or with status 500 (Internal Server Error) if the user couldn't be updated
     */
    @PutMapping("/users")
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<UserJSON> updateUser(@RequestBody UserJSON userJSON)
      throws AccountException {
      log.debug("PUT request to update User : {}", userJSON);

      /*% if (feature.MWM_EmployeeAuthentication) { %*/
      Long employeeId = userJSON.getEmployee() != null ? userJSON.getEmployee().getId() : null;
      /*% } %*/

      UMUser updatedUser =
        userService.updateUser(
          userJSON.getId(),
          userJSON.getLogin(),
          userJSON.getFirstName(),
          userJSON.getLastName(),
          userJSON.getEmail(),
          /*% if (feature.UM_AccountActivation) { %*/
          userJSON.isActivated(),
          /*% } %*/
          userJSON.getLangKey(),
          userJSON.getAuthorities()
          /*% if (feature.MWM_EmployeeAuthentication) { %*/ , employeeId /*% } %*/
        );

      /*% if (feature.UM_AA_ByAdmin) { %*/
      // If the user has been activated, we send an email
      if (!userJSON.isActivated() && updatedUser.getActivated()) {
        mailService.sendAccountActivatedByAdminMail(updatedUser, properties.getClientHost());
      }
      /*% } %*/

      return ResponseEntity.ok().body(new UserJSON(updatedUser));
    }

    /**
     * GET  /users : get all users.
     *
     * @param pageable the pagination information
     * @param search string to search by user attributes
     * @return the ResponseEntity with status 200 (OK) and with body all users paginated
     * @throws URISyntaxException if the pagination headers couldn't be generated
     */
    @GetMapping("/users")
    public ResponseEntity<Page<UserJSON>> getAllUsers(
        @PageableDefault(page = 0, size = 100000, sort = "id") Pageable pageable,
        @RequestParam(value = "search", required = false) String search)
        throws URISyntaxException {
      log.debug("GET request to get all users");
      Page<UserJSON> page = userService.getAllWithAuthorities(search, pageable);
      HttpHeaders headers =
        PaginationUtil.generatePaginationHttpHeaders(page, "/api/user-management/users");
      return new ResponseEntity<>(page, headers, HttpStatus.OK);
    }

    /**
     * GET  /users/:login : get the "login" user.
     *
     * @param login the login of the user to find
     * @return the ResponseEntity with status 200 (OK) and with body the "login" user, or with status 404 (Not Found)
     */
    @GetMapping("/users/{login:" + Constants.LOGIN_REGEX + "}")
    public ResponseEntity<UserJSON> getUser(@PathVariable String login) {
        log.debug("GET request to get User : {}", login);
        return userService.getUserWithAuthoritiesByLogin(login)
                .map(UserJSON::new)
                .map(userJSON -> new ResponseEntity<>(userJSON, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE /users/:login : delete the "login" User.
     *
     * @param login the login of the user to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/users/{login:" + Constants.LOGIN_REGEX + "}")
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> deleteUser(@PathVariable String login) {
        log.debug("DELETE request to delete User: {}", login);
        userService.deleteUser(login);
        return ResponseEntity.ok().build();
    }
/*% } %*/
}
/*% } %*/
