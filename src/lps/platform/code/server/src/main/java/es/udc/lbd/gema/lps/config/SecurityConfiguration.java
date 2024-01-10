/*% if (feature.UserManagement) { %*/
package es.udc.lbd.gema.lps.config;

import javax.inject.Inject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import es.udc.lbd.gema.lps.security.Http401UnauthorizedEntryPoint;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.AuthenticationManager;

    /*% if (feature.UM_ST_JWT) { %*/
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.beans.factory.BeanInitializationException;
import es.udc.lbd.gema.lps.security.jwt.JWTConfigurer;
import es.udc.lbd.gema.lps.security.jwt.TokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.security.config.http.SessionCreationPolicy;
    /*% } %*/

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Inject
    private UserDetailsService userDetailsService;

    @Inject
    private Http401UnauthorizedEntryPoint authenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*% if (feature.UM_ST_JWT) { %*/

    @Inject
    private TokenProvider tokenProvider;

    @Inject
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        try {
            auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        } catch (Exception e) {
            throw new BeanInitializationException("Security configuration failed", e);
        }
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
            .antMatchers(HttpMethod.OPTIONS, "/**")
        ;
    }
  /*% if (feature.T_Swagger) { %*/
    private static final String[] AUTH_SWAGGER = {
      "/swagger-resources/**", "/swagger-ui.html", "/v2/api-docs", "/webjars/**"
    };
  /*% } %*/

  @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        /*% if (feature.UM_ST_JWT) { %*/
           .csrf()
        .disable()
        .exceptionHandling()
        .authenticationEntryPoint(authenticationEntryPoint)
        .and()
        .headers()
        .frameOptions()
        .disable()
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests()
        .antMatchers("/api/authenticate")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/api/users/**")
        .permitAll()
        /*% if (feature.UM_A_RememberPass || feature.UM_UP_ByAdmin) { %*/
        .antMatchers(HttpMethod.POST, "/api/account/reset_password/init")
        .permitAll()
        /*% } %*/
        /*% if (feature.UM_UpdatePassword  || feature.UM_R_ByAdmin) { %*/
        .antMatchers(HttpMethod.POST, "/api/account/reset_password/finish")
        .permitAll()
        /*% } %*/
        /*% if (feature.UM_AccountActivation) { %*/
        .antMatchers(HttpMethod.GET, "/api/activate")
        .permitAll()
        /*% } %*/
        /*% if (feature.UM_R_Anonymous) { %*/
        .antMatchers(HttpMethod.POST, "/api/register")
        .permitAll()
        /*% } %*/
        /*% if (feature.UM_C_AnonymousUserCanViewEntities) { %*/
        .antMatchers(HttpMethod.GET, "/api/entities/**")
        .permitAll()
        .antMatchers(HttpMethod.GET, "/api/download/**")
        .permitAll()
        .antMatchers(HttpMethod.GET, "/api/images/**")
        .permitAll()
        /*% } %*/
        /*% if (feature.GUI_StaticPages) { %*/
        .antMatchers(HttpMethod.GET, "/api/entities/statics/*/**")
        .permitAll()
        /*% } %*/
        /*% if (feature.SensorViewer) { %*/
        .antMatchers(HttpMethod.GET, "/api/sensors/*/**")
        .permitAll()
        /*% } %*/
          /*% if (feature.T_Swagger) { %*/
          .antMatchers(AUTH_SWAGGER)
          .permitAll()
          /*% } %*/
          .antMatchers("/**")
        .authenticated()
        .and()
        .apply(securityConfigurerAdapter())
        /*% } %*/
        ;
    }

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }

    @Inject
    public void configureAuth(AuthenticationManagerBuilder auth) {
        try {
            auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        } catch (Exception e) {
            throw new BeanInitializationException("SecurityConfiguration.configureAuth failed", e);
        }
    }

    private JWTConfigurer securityConfigurerAdapter() {
        return new JWTConfigurer(tokenProvider);
    }

    /*% } %*/
}
/*% } %*/
