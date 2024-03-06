/*% if (feature.T_MailSender) { %*/
package es.udc.lbd.gema.lps.model.service;

/*% if (feature.UserManagement) { %*/
import es.udc.lbd.gema.lps.model.domain.user_management.UMUser;
/*% } %*/

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.mail.internet.MimeMessage;
import java.util.Locale;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private Locale locale = LocaleContextHolder.getLocale();

    @Inject
    private Environment env;

    @Inject
    private JavaMailSenderImpl javaMailSender;

    @Inject
    private MessageSource messageSource;

    @Inject
    private SpringTemplateEngine templateEngine;

    /**
     * System default email address that sends the e-mails.
     */
    private String from;

    @PostConstruct
    public void init() {
        this.from = env.getProperty("spring.mail.from");
    }

    @Async
    private void sendMail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        log.debug("Send mail[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}", isMultipart, isHtml, to, subject, content);
        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, "UTF-8");
            message.setTo(to);
            message.setFrom(from);
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
            log.debug("Sent e-mail to UMUser '{}'", to);
        } catch (Exception e) {
            log.warn("E-mail could not be sent to user '{}', exception is: {}", to, e.getMessage());
        }
    }
    /*% if (!feature.UM_AccountActivation) { %*/

    /**
     * Email with account details and sign-in link
     * @param user User registered
     * @param baseUrl Application base URL
     */
    @Async
    @Retryable(
      maxAttempts = 5,
      backoff = @Backoff(delay = 3000, maxDelay = 10000, multiplier = 3),
      value = Exception.class)
    public void sendAccountInformationEmail(UMUser user, String baseUrl) {
        log.debug("Sending account information e-mail to '{}'", user.getEmail());
        Locale locale = Locale.forLanguageTag(user.getLangKey());
        Context context = new Context(locale);
        context.setVariable("user", user);
        context.setVariable("baseUrl", baseUrl);
        context.setVariable("appName", "/*%= data.basicData.name %*/");
        String content = templateEngine.process("accountInformationEmail", context);
        log.debug(content);
        String subject = messageSource.getMessage("mail.account.information.title", new Object[]{"/*%= data.basicData.name %*/"}, locale);
        sendMail(user.getEmail(), subject, content, false, true);
    }
    /*% } %*/
    /*% if (feature.UM_R_ByAdmin) { %*/

    /**
     * Email with account details and set password
     * @param user Registered user
     * @param baseUrl Application base URL
     */
    @Async
    @Retryable(
      maxAttempts = 5,
      backoff = @Backoff(delay = 3000, maxDelay = 10000, multiplier = 3),
      value = Exception.class)
    public void sendAccountPasswordSetEmail(UMUser user, String baseUrl) {
        log.debug("Sending set account password e-mail to '{}'", user.getEmail());
        Locale locale = Locale.forLanguageTag(user.getLangKey());
        Context context = new Context(locale);
        context.setVariable("user", user);
        context.setVariable("baseUrl", baseUrl);
        context.setVariable("appName", "/*%= data.basicData.name %*/");
        String content = templateEngine.process("accountPasswordSetEmail", context);
        String subject = "Account details for user Sample Product";
        sendMail(user.getEmail(), subject, content, false, true);
    }
    /*% } %*/
    /*% if (feature.UM_AA_ByEmail) { %*/

    @Async
    @Retryable(
      maxAttempts = 5,
      backoff = @Backoff(delay = 3000, maxDelay = 10000, multiplier = 3),
      value = Exception.class)
    public void sendActivationEmail(UMUser user, String baseUrl) {
        log.debug("Sending activation e-mail to '{}'", user.getEmail());
        Locale locale = Locale.forLanguageTag(user.getLangKey());
        Context context = new Context(locale);
        context.setVariable("user", user);
        context.setVariable("baseUrl", baseUrl);
        String content = templateEngine.process("accountActivationEmail", context);
        String subject = "Account activation";
        sendMail(user.getEmail(), subject, content, false, true);
    }

    /*% } %*/
    /*% if (feature.UM_A_RecoverPass || feature.UM_UP_ByAdmin) { %*/

    @Async
    @Retryable(
      maxAttempts = 5,
      backoff = @Backoff(delay = 3000, maxDelay = 10000, multiplier = 3),
      value = Exception.class)
    public void sendPasswordResetMail(UMUser user, String baseUrl) {
        log.debug("Sending password reset e-mail to '{}'", user.getEmail());
        Locale locale = Locale.forLanguageTag(user.getLangKey());
        Context context = new Context(locale);
        context.setVariable("user", user);
        context.setVariable("baseUrl", baseUrl);
        String content = templateEngine.process("accountPasswordResetEmail", context);
        String subject = "Account password reset";
        sendMail(user.getEmail(), subject, content, false, true);
    }
    /*% } %*/
    /*% if(!feature.UM_AA_ByEmail && feature.UM_AA_ByAdmin) { %*/

    /**
     * Sends an email to all admins of the platform to notify that a new account has been registered
     * in the platform and it's waiting for an activation.
     *
     * @param to      Address where the mail will be sent
     * @param newUser New user account
     * @param baseUrl Application base URL
     */
    @Async
    @Retryable(
      maxAttempts = 5,
      backoff = @Backoff(delay = 3000, maxDelay = 10000, multiplier = 3),
      value = Exception.class)
    public void sendNewAccountMail(String to, UMUser newUser, String baseUrl) {
        log.debug("Sending new account (activation needed) to '{}'", newUser);
        Locale locale = Locale.forLanguageTag(newUser.getLangKey());
        Context context = new Context(locale);
        context.setVariable("user", newUser);
        context.setVariable("baseUrl", baseUrl);
        String content = templateEngine.process("accountNewAccountAdminEmail", context);
        String subject = messageSource.getMessage("mail.account.new.admin.title", null, locale);
        sendMail(to, subject, content, false, true);
    }
    /*% } %*/
    /*% if (feature.UM_AccountActivation) { %*/

    /**
     * Sends an email notifying the user that its account is activated
     *
     * @param user    User account activated
     * @param baseUrl Application base URL
     */
    @Async
    @Retryable(
      maxAttempts = 5,
      backoff = @Backoff(delay = 3000, maxDelay = 10000, multiplier = 3),
      value = Exception.class)
    public void sendAccountActivatedByAdminMail(UMUser user, String baseUrl) {
        log.debug("Sending account activated by admin e-mail to '{}'", user.getEmail());
        Locale locale = Locale.forLanguageTag(user.getLangKey());
        Context context = new Context(locale);
        context.setVariable("user", user);
        context.setVariable("baseUrl", baseUrl);
        context.setVariable("appName", "Test");
        String content = templateEngine.process("accountActivatedByAdminEmail", context);
        String subject = "Your account in Test has been activated";
        sendMail(user.getEmail(), subject, content, false, true);
    }
    /*% } %*/
}
/*% } %*/
