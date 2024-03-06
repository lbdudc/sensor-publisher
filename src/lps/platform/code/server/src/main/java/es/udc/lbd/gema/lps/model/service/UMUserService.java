/*% if (feature.UserManagement) { %*/
package es.udc.lbd.gema.lps.model.service;

/*% if (feature.MWM_EmployeeAuthentication) { %*/
import es.udc.lbd.gema.lps.component.gema.model.repository.EmployeeRepository;
import es.udc.lbd.gema.lps.component.gema.model.domain.Employee;
/*% } %*/
import es.udc.lbd.gema.lps.model.domain.user_management.Authority;
import es.udc.lbd.gema.lps.model.domain.user_management.UMUser;
import es.udc.lbd.gema.lps.model.repository.user_management.AuthorityRepository;
import es.udc.lbd.gema.lps.model.repository.user_management.PersistentTokenRepository;
import es.udc.lbd.gema.lps.model.repository.user_management.UMUserRepository;
import es.udc.lbd.gema.lps.model.service.exceptions.NotFoundException;
import es.udc.lbd.gema.lps.model.service.exceptions.account.AccountException;
import es.udc.lbd.gema.lps.model.service.exceptions.account.AccountEmailInUseException;
import es.udc.lbd.gema.lps.model.service.exceptions.account.AccountLoginInUseException;
import es.udc.lbd.gema.lps.model.service.exceptions.account.AccountNoAuthoritiesProvidedException;
import es.udc.lbd.gema.lps.model.service.util.RandomUtil;
import es.udc.lbd.gema.lps.security.AuthoritiesConstants;
import es.udc.lbd.gema.lps.security.SecurityUtils;
import es.udc.lbd.gema.lps.web.rest.custom.user_management.vm.UserJSON;
import es.udc.lbd.gema.lps.web.rest.specifications.UMUserSpecification;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing users.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UMUserService {

    private final Logger log = LoggerFactory.getLogger(UMUserService.class);

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private UMUserRepository userRepository;

    @Inject
    private PersistentTokenRepository persistentTokenRepository;

    @Inject
    private AuthorityRepository authorityRepository;
    /*% if (feature.MWM_EmployeeAuthentication) { %*/

    @Inject
    private EmployeeRepository employeeRepository;

    /*% } %*/
    /*% if (feature.UM_AccountActivation) { %*/
    public Optional<UMUser> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository.findOneByActivationKey(key)
            .map(user -> {
                // activate given user for the registration key.
                user.setActivated(true);
                user.setActivationKey(null);
                log.debug("Activated user: {}", user);
                return user;
            });
    }
    /*% } %*/

    public Optional<UMUser> completePasswordReset(String newPassword, String key) {
        log.debug("Reset user password for reset key {}", key);

        return userRepository.findOneByResetKey(key)
            .filter(user -> {
                ZonedDateTime oneDayAgo = ZonedDateTime.now().minusHours(24);
                return user.getResetDate().isAfter(oneDayAgo);
            })
            .map(user -> {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetKey(null);
                user.setResetDate(null);
                return user;
            });
    }

    public Optional<UMUser> requestPasswordReset(String mail) {
        return userRepository.findOneByEmail(mail)
            .map(user -> {
                user.setResetKey(RandomUtil.generateResetKey());
                user.setResetDate(ZonedDateTime.now());
                return user;
            });
    }

    /**
     * Creates a new user.
     * Typical 'register' action
     */
    public UMUser createUser(String login, String password, String firstName, String lastName, String email, String langKey) throws AccountException {

        // Default role: User
        Set<Authority> authorities = new HashSet<Authority>(Arrays.asList(authorityRepository.getOne(AuthoritiesConstants.USER)));
        String encryptedPassword = passwordEncoder.encode(password);

        UMUser newUser = new UMUser();
        newUser.setLogin(login);
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setLangKey(langKey);
        newUser.setAuthorities(authorities);
        /*% if (feature.UM_AccountActivation) { %*/
        newUser.setActivated(false);
        // Not activated users need a registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        /*% } %*/

        validateUser(newUser);

        userRepository.save(newUser);
        log.debug("UMUser created: {}", newUser);
        return newUser;
    }

    /**
     * Creates a new user.
     * Typical 'user created by admin' action
     */
    public UMUser createUser(UserJSON userJSON) throws AccountException {
        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());

        UMUser user = new UMUser();
        user.setLogin(userJSON.getLogin());
        user.setPassword(encryptedPassword);
        user.setFirstName(userJSON.getFirstName());
        user.setLastName(userJSON.getLastName());
        user.setEmail(userJSON.getEmail());
        if (userJSON.getLangKey() == null) {
            user.setLangKey("en"); // default language TODO: Obtener el idioma que se muestra en la plataforma
        } else {
            user.setLangKey(userJSON.getLangKey());
        }
        /*% if (feature.UM_AccountActivation) { %*/
        user.setActivated(true);
        /*% } %*/

        if (userJSON.getAuthorities() != null) {
            Set<Authority> authorities = new HashSet<>();
            userJSON.getAuthorities().forEach(
                authority -> authorities.add(authorityRepository.getOne(authority))
            );
            user.setAuthorities(authorities);
        }

        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(ZonedDateTime.now());
        /*% if (feature.MWM_EmployeeAuthentication) { %*/

        Employee employee = userJSON.getEmployee() != null ? employeeRepository.findById(userJSON.getEmployee().getId()).orElse(null) : null;
        //Only admins can set employee to user
        user.setEmployee(employee);

        /*% } %*/
        validateUser(user);

        userRepository.save(user);
        log.debug("UMUser created: {}", user);
        return user;
    }

    /**
     * Validates the user. To be used before saving / updating the user
     * @param user
     * @throws AccountException
     */
    private void validateUser(UMUser user) throws AccountException {
        if ((userRepository.findOneByLogin(user.getLogin().toLowerCase()).isPresent())) {
            throw new AccountLoginInUseException(user.getLogin());
        }

        if ((userRepository.findOneByEmail(user.getEmail().toLowerCase()).isPresent())) {
            throw new AccountEmailInUseException(user.getEmail());
        }

        if (user.getAuthorities().isEmpty()) {
            throw new AccountNoAuthoritiesProvidedException();
        }
    }

    public void updateUser(String firstName, String lastName, String email, String langKey) {
        userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).ifPresent(user -> {
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setLangKey(langKey);
            log.debug("Changed Information for UMUser: {}", user);
        });
    }

    public UMUser updateUser(Long id, String login, String firstName, String lastName, String email,
      /*% if (feature.UM_AccountActivation) { %*/ boolean activated, /*% } %*/ String langKey, Set<String> authorities
      /*% if (feature.MWM_EmployeeAuthentication) { %*/ , Long employeeId /*% } %*/)
      throws AccountException {

      /*% if (feature.MWM_EmployeeAuthentication) { %*/
      Employee employee = employeeId != null ? employeeRepository.findById(employeeId).orElse(null) : null;
      /*% } %*/

      UMUser user = userRepository.getOne(id);
      Optional<UMUser> foundUser = null;

      // Check if other user has the same email
      foundUser = userRepository.findOneByEmail(email);
      if (foundUser.isPresent() && foundUser.get().getId() != id) {
        throw new AccountEmailInUseException(email);
      }

      // Check if other user has the same login
      foundUser = userRepository.findOneByLogin(login.toLowerCase());
      if (foundUser.isPresent() && foundUser.get().getId() != id) {
        throw new AccountLoginInUseException(login);
      }

      /*% if (feature.UM_AccountActivation) { %*/
      boolean alreadyActivated = user.getActivated();
      /*% } %*/
      user.setLogin(login);
      user.setFirstName(firstName);
      user.setLastName(lastName);
      user.setEmail(email);
      /*% if (feature.UM_AccountActivation) { %*/
      if (!login.equals(SecurityUtils.getCurrentUserLogin())) {
        user.setActivated(activated);
      }
      /*% } %*/
      /*% if (feature.MWM_EmployeeAuthentication) { %*/
      user.setEmployee(employee);
      /*% } %*/
      user.setLangKey(langKey);
      Set<Authority> managedAuthorities = user.getAuthorities();
      managedAuthorities.clear();
      authorities.forEach(authority -> managedAuthorities.add(authorityRepository.getOne(authority)));

      /*% if (feature.UM_AccountActivation) { %*/
      if (!alreadyActivated && user.getActivated()) {
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(ZonedDateTime.now());
      }
      /*% } %*/

      log.debug("Changed Information for UMUser: {}", user);
      return user;
    }

    public void deleteUser(String login) {
        userRepository.findOneByLogin(login).ifPresent(user -> {
            userRepository.delete(user);
            log.debug("Deleted UMUser: {}", user);
        });
    }

    public void changePassword(String password) {
        userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).ifPresent(user -> {
            String encryptedPassword = passwordEncoder.encode(password);
            user.setPassword(encryptedPassword);
            log.debug("Changed password for UMUser: {}", user);
        });
    }

    @Transactional(readOnly = true)
    public Optional<UMUser> getUserWithAuthoritiesByLogin(String login) {
        return userRepository.findOneByLogin(login).map(user -> {
            user.getAuthorities().size();
            return user;
        });
    }

    @Transactional(readOnly = true)
    public UMUser getUserWithAuthorities(Long id) {
        UMUser user = userRepository.getOne(id);
        user.getAuthorities().size(); // eagerly load the association
        return user;
    }

    @Transactional(readOnly = true)
    public UMUser getUserWithAuthorities() {
        Optional<UMUser> optionalUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
        UMUser user = null;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            user.getAuthorities().size(); // eagerly load the association
        }
        return user;
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Page<UserJSON> getAllWithAuthorities(String search, Pageable pageable) {
      return userRepository
        .findAll(UMUserSpecification.searchAll(search, null), pageable)
        .map(UserJSON::new);
    }

    /**
     * Persistent Token are used for providing automatic authentication, they should be automatically deleted after
     * 30 days.
     * <p>
     * This is scheduled to get fired everyday, at midnight.
     * </p>
     */
    public void removeOldPersistentTokens() {
        LocalDate now = LocalDate.now();
        persistentTokenRepository.findByTokenDateBefore(now.minusMonths(1)).forEach(token -> {
            log.debug("Deleting token {}", token.getSeries());
            UMUser user = token.getUser();
            user.getPersistentTokens().remove(token);
            persistentTokenRepository.delete(token);
        });
    }
    /*% if (feature.UM_AccountActivation) { %*/
    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     * </p>
     */
    public void removeNotActivatedUsers() {
        ZonedDateTime now = ZonedDateTime.now();
        List<UMUser> users = userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(now.minusDays(3));
        for (UMUser user : users) {
            log.debug("Deleting not activated user {}", user.getLogin());
            userRepository.delete(user);
        }
    }
    /*% } %*/

    public UserJSON changeLanguage(String login, String newLanguage) throws NotFoundException {
      Optional<UMUser> optionalUser = userRepository.findOneByLogin(login);
      if (!optionalUser.isPresent()) {
        throw new NotFoundException("User with login " + login + " not found!");
      }
      optionalUser.get().setLangKey(newLanguage);
      userRepository.save(optionalUser.get());
      return new UserJSON(optionalUser.get());
    }
}

/*% } %*/
