/*% if (feature.T_MailSender) { %*/
package es.udc.lbd.gema.lps.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class LocaleConfiguration {
  @Bean
  public ResourceBundleMessageSource messageSource() {
    ResourceBundleMessageSource source = new ResourceBundleMessageSource();
    source.setBasename("i18n/messages");
    source.setUseCodeAsDefaultMessage(true);
    return source;
  }

}
/*% } %*/
