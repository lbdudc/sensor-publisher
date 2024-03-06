/*% if (feature.UserManagement) { %*/
package es.udc.lbd.gema.lps.web.rest.custom.user_management;

import es.udc.lbd.gema.lps.config.Properties;
import es.udc.lbd.gema.lps.model.domain.user_management.PersistentToken;
import es.udc.lbd.gema.lps.model.domain.user_management.UMUser;
import es.udc.lbd.gema.lps.model.service.exceptions.AppRuntimeException;
import es.udc.lbd.gema.lps.model.service.exceptions.NotFoundException;
import es.udc.lbd.gema.lps.model.repository.user_management.PersistentTokenRepository;
import es.udc.lbd.gema.lps.model.repository.user_management.UMUserRepository;
/*% if (feature.T_MailSender) { %*/
import es.udc.lbd.gema.lps.model.service.MailService;
/*% } %*/
import es.udc.lbd.gema.lps.model.service.UMUserService;
import es.udc.lbd.gema.lps.model.service.dto.UMUserJSON;
/*% if (feature.UM_UP_ByUser) { %*/
import es.udc.lbd.gema.lps.model.service.dto.UMPasswordJSON;
/*% } %*/
/*% if (feature.UM_A_RecoverPass || feature.UM_UP_ByAdmin || feature.UM_R_ByAdmin) { %*/
import es.udc.lbd.gema.lps.model.service.dto.UMMailJSON;
/*% } %*/
import es.udc.lbd.gema.lps.model.service.exceptions.account.AccountException;
import es.udc.lbd.gema.lps.model.service.exceptions.account.AccountEmailInUseException;
import es.udc.lbd.gema.lps.model.service.exceptions.account.AccountEmailNotRegisteredException;
import es.udc.lbd.gema.lps.model.service.exceptions.account.AccountIncorrectPasswordException;
import es.udc.lbd.gema.lps.model.service.exceptions.account.CredentialsAreNotValidException;
import es.udc.lbd.gema.lps.model.service.exceptions.account.AccountPasswordResetKeyExpiredException;
import es.udc.lbd.gema.lps.security.AuthoritiesConstants;
import es.udc.lbd.gema.lps.security.SecurityUtils;
import es.udc.lbd.gema.lps.web.rest.custom.user_management.vm.KeyAndPasswordJSON;
import es.udc.lbd.gema.lps.web.rest.custom.user_management.vm.UserJSON;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

   /*% if (feature.UM_ST_JWT) { %*/
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import es.udc.lbd.gema.lps.security.jwt.TokenProvider;
import es.udc.lbd.gema.lps.security.jwt.JWTConfigurer;
import es.udc.lbd.gema.lps.security.jwt.JWTToken;

import org.springframework.http.HttpHeaders;
     /*% } %*/

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

    private static final Logger log = LoggerFactory.getLogger(AccountResource.class);

    @Inject
    private Properties properties;

    @Inject
    private UMUserRepository userRepository;

    @Inject
    private UMUserService userService;

    @Inject
    private PersistentTokenRepository persistentTokenRepository;

    /*% if (feature.UM_ST_JWT) { %*/
    @Inject
    private TokenProvider tokenProvider;

    @Inject
    private AuthenticationManager authenticationManager;
    /*% } %*/

    /*% if (feature.UM_R_Anonymous || feature.UM_A_RecoverPass || feature.UM_UP_ByAdmin || feature.UM_R_ByAdmin) { %*/
    @Inject
    private MailService mailService;
    /*% } %*/

    @Inject
    private HttpServletRequest request;

    /*% if (feature.UM_R_Anonymous) { %*/
    /**
     * POST  /register : The user registers by itself.
     *
     * @param userJSON the managed user View Model
     * @return the ResponseEntity with status 201 (Created) if the user is registered or 400 (Bad Request) if the login or e-mail is already in use
     */
    @PostMapping(path = "/register")
    public ResponseEntity<?> registerAccount(@Valid @RequestBody UserJSON userJSON) throws AccountException {
        log.debug("POST request to register an account");

        UMUser user = userService.createUser(userJSON.getLogin(), userJSON.getPassword(),
                userJSON.getFirstName(), userJSON.getLastName(),
                userJSON.getEmail().toLowerCase(), userJSON.getLangKey());

        /*% if (!feature.UM_AccountActivation) { %*/
        // Send information mail to the user
        mailService.sendAccountInformationEmail(user, properties.getClientHost());
        /*% } else if (feature.UM_AA_ByEmail) { %*/
        // Send activation mail to user
        mailService.sendActivationEmail(user, properties.getClientHost());
        /*% } else if (feature.UM_AA_ByAdmin) { %*/
        // An admin must accept user registration
        // Send notification to admins
        List<UMUser> admins = userRepository.findAllWithAuthorityName(AuthoritiesConstants.ADMIN);
        for (UMUser admin : admins) {
            mailService.sendNewAccountMail(admin.getEmail(), user, properties.getClientHost());
        }
        /*% } %*/
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    /*% } %*/

    /*% if (feature.UM_AA_ByEmail) { %*/
    /**
     * GET  /activate : activate the registered user.
     *
     * @param key the activation key
     * @return the ResponseEntity with status 200 (OK) and the activated user in body,
     * or status 500 (Internal Server Error) if the user couldn't be activated
     */
    @GetMapping("/activate")
    public ResponseEntity<String> activateAccount(@RequestParam(value = "key") String key) {
        log.debug("GET request to activate an account");
        return userService.activateRegistration(key)
            .map(user -> new ResponseEntity<String>(HttpStatus.OK))
            .orElseThrow(() -> new AppRuntimeException("Unable to activate user. Key: " + key));
    }
    /*% } %*/

    /**
     * GET  /authenticate : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request
     * @return the login if the user is authenticated
     */
    @GetMapping("/authenticate")
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("GET request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /*% if (feature.UM_ST_JWT) { %*/
    /**
     * POST  /authenticate : check if the user is in the system, and return a token.
     *
     * @param request the HTTP request
     * @return a new token if the user can be authenticated
     */
    @PostMapping("/authenticate")
    public JWTToken authenticate(@Valid @RequestBody UserJSON userJSON/*% if (feature.UM_A_RememberPass) { %*/,
           @RequestParam(name = "remember_me", defaultValue = "false") Boolean rememberMe/*% } %*/) throws AccountException {

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userJSON.getLogin(), userJSON.getPassword());
        try {
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.createToken(authentication/*% if (feature.UM_A_RememberPass) { %*/, rememberMe/*% } %*/);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JWTConfigurer.AUTHORIZATION_HEADER, "Bearer " + jwt);
            return new JWTToken(jwt);
        } catch (AuthenticationException e) {
            log.warn(e.getMessage(), e);
            throw new CredentialsAreNotValidException(e.getMessage());
            // HAY QUE CAMBIAR ESTA EXCEPCION
        }
    }
    /*% } %*/
    /**
     * GET  /account : get the current user.
     *
     * @return the ResponseEntity with status 200 (OK) and the current user in body,
     * or status 500 (Internal Server Error) if the user couldn't be returned
     */
    @GetMapping("/account")
    public ResponseEntity<UMUserJSON> getAccount() {
        log.debug("GET request to get an account");
        return Optional.ofNullable(userService.getUserWithAuthorities())
            .map(user -> new ResponseEntity<>(new UMUserJSON(user), HttpStatus.OK))
            .orElseThrow(() -> new AppRuntimeException("Unable to retrieve the current user "));
    }

    /**
     * POST  /account : update the current user information.
     *
     * @param userDTO the current user information
     * @return the ResponseEntity with status 200 (OK),
     * or status 400 (Bad Request)
     * or 500 (Internal Server Error) if the user couldn't be updated
     */
    @PostMapping("/account")
    public ResponseEntity<String> saveAccount(@Valid @RequestBody UMUserJSON userDTO) throws AccountException {
        log.debug("POST request to save an account");
        Optional<UMUser> existingUser = userRepository.findOneByEmail(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getLogin().equalsIgnoreCase(userDTO.getLogin()))) {
            throw new AccountEmailInUseException(userDTO.getEmail());
        }
        /*% if (!feature.UM_UpdateEmail) { %*/
        if (!existingUser.isPresent()) {
            throw new AppRuntimeException("Email change not allowed. User login: " + userDTO.getLogin());
        }
        /*% } %*/
        return userRepository
            .findOneByLogin(SecurityUtils.getCurrentUserLogin())
            .map(u -> {
                userService.updateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(),
                    userDTO.getLangKey());
                return new ResponseEntity<String>(HttpStatus.OK);
            })
            .orElseThrow(() -> new AppRuntimeException("Unable to save user account. User Login: " + userDTO.getLogin()));
    }

    /*% if (feature.UM_UP_ByUser) { %*/
    /**
     * POST  /account/change_password : changes the current user's password
     *
     * @param password the new password
     * @return the ResponseEntity with status 200 (OK), or status 400 (Bad Request) if the new password is not strong enough
     */
    @PostMapping(path = "/account/change_password", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> changePassword(@RequestBody UMPasswordJSON passwordDTO) throws AccountException {
        log.debug("POST request to change the password of an account");
        if (!checkPasswordLength(passwordDTO.getPassword())) { throw new AccountIncorrectPasswordException(); }
        userService.changePassword(passwordDTO.getPassword());
        return new ResponseEntity<>(HttpStatus.OK);
    }
    /*% } %*/
    /*% if (feature.UM_UserSessions) { %*/
    /**
     * GET  /account/sessions : get the current open sessions.
     *
     * @return the ResponseEntity with status 200 (OK) and the current open sessions in body,
     *  or status 500 (Internal Server Error) if the current open sessions couldn't be retrieved
     */
    @GetMapping("/account/sessions")
    public ResponseEntity<List<PersistentToken>> getCurrentSessions() {
        log.debug("GET request to get account sessions");
        return userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin())
            .map(user -> new ResponseEntity<>(
                persistentTokenRepository.findByUser(user),
                HttpStatus.OK))
            .orElseThrow(() -> new AppRuntimeException("Unable to get current open sessions"));
    }

    /**
     * DELETE  /account/sessions?series={series} : invalidate an existing session.
     *
     * - You can only delete your own sessions, not any other user's session
     * - If you delete one of your existing sessions, and that you are currently logged in on that session, you will
     *   still be able to use that session, until you quit your browser: it does not work in real time (there is
     *   no API for that), it only removes the "remember me" cookie
     * - This is also true if you invalidate your current session: you will still be able to use it until you close
     *   your browser or that the session times out. But automatic login (the "remember me" cookie) will not work
     *   anymore.
     *   There is an API to invalidate the current session, but there is no API to check which session uses which
     *   cookie.
     *
     * @param series the series of an existing session
     * @throws UnsupportedEncodingException if the series couldn't be URL decoded
     */
    @DeleteMapping("/account/sessions/{series}")
    public void invalidateSession(@PathVariable String series) throws UnsupportedEncodingException {
        log.debug("DELETE request to invalidate a session");
        String decodedSeries = URLDecoder.decode(series, "UTF-8");
        userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).ifPresent(u -> {
            persistentTokenRepository.findByUser(u).stream()
                .filter(persistentToken -> StringUtils.equals(persistentToken.getSeries(), decodedSeries))
                .findAny().ifPresent(t -> persistentTokenRepository.deleteById(decodedSeries));
        });
    }
    /*% } %*/
    /*% if (feature.UM_A_RecoverPass || feature.UM_UP_ByAdmin) { %*/
    /**
     * POST   /account/reset_password/init : Send an e-mail to reset the password of the user
     *
     * @param mail the mail of the user
     * @return the ResponseEntity with status 200 (OK) if the e-mail was sent, or status 400 (Bad Request) if the e-mail address is not registered
     */
    @PostMapping(path = "/account/reset_password/init", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> requestPasswordReset(@RequestBody UMMailJSON mailDTO) throws AccountException {
        log.debug("POST request to request a password reset");
        return userService.requestPasswordReset(mailDTO.getEmail())
            .map(user -> {
                mailService.sendPasswordResetMail(user, properties.getClientHost());
                return new ResponseEntity<>("e-mail was sent", HttpStatus.OK);
            }).orElseThrow(() -> new AccountEmailNotRegisteredException(mailDTO.getEmail()));
    }
    /*% } %*/
    /*% if (feature.UM_A_RecoverPass || feature.UM_UP_ByAdmin || feature.UM_R_ByAdmin) { %*/
    /**
     * POST   /account/reset_password/finish : Finish to reset the password of the user
     *
     * @param keyAndPassword the generated key and the new password
     * @return the ResponseEntity with status 200 (OK) if the password has been reset,
     * or status 400 (Bad Request)
     * or 500 (Internal Server Error) if the password could not be reset
     */
    @PostMapping(path = "/account/reset_password/finish", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> finishPasswordReset(@RequestBody KeyAndPasswordJSON keyAndPassword) throws AccountException {
        log.debug("POST request to finish a password reset");
        if (!checkPasswordLength(keyAndPassword.getNewPassword())) {
            throw new AccountIncorrectPasswordException();
        }
        return userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey())
            .map(user -> new ResponseEntity<String>(HttpStatus.OK))
            .orElseThrow(() -> new AccountPasswordResetKeyExpiredException(keyAndPassword.getKey()));
    }
    /*% } %*/

    @PutMapping("/account/{login}/language")
    public ResponseEntity<UserJSON> changeUserLanguage(
      @PathVariable String login, @RequestBody String newLang) throws NotFoundException {
      log.debug("PUT request to change user's language");
      return new ResponseEntity<>(userService.changeLanguage(login, newLang), HttpStatus.OK);
    }

    /*% if (feature.UM_UP_ByUser ||feature.UM_A_RecoverPass || feature.UM_UP_ByAdmin) { %*/
    private boolean checkPasswordLength(String password) {
        return (!StringUtils.isEmpty(password) &&
            password.length() >= UserJSON.PASSWORD_MIN_LENGTH &&
            password.length() <= UserJSON.PASSWORD_MAX_LENGTH);
    }
    /*% } %*/
}
/*% } %*/
