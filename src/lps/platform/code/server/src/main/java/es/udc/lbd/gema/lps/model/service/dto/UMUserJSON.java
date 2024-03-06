/*% if (feature.UserManagement) { %*/
package es.udc.lbd.gema.lps.model.service.dto;
/*% if (feature.MWM_EmployeeAuthentication) { %*/

import es.udc.lbd.gema.lps.component.gema.model.domain.Employee;
import es.udc.lbd.gema.lps.component.gema.model.service.dto.EmployeeDTO;
/*% } %*/
import es.udc.lbd.gema.lps.config.Constants;
import es.udc.lbd.gema.lps.model.domain.user_management.Authority;
import es.udc.lbd.gema.lps.model.domain.user_management.UMUser;

import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

/**
 * A DTO representing a user, with his authorities.
 */
public class UMUserJSON {

    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    private String login;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @Email
    @Size(min = 5, max = 100)
    private String email;
    /*% if (feature.UM_AccountActivation) { %*/
    private boolean activated = false;
    /*% } %*/

    @Size(min = 2, max = 5)
    private String langKey;

    private Set<String> authorities;

    /*% if (feature.MWM_EmployeeAuthentication) { %*/
    private EmployeeDTO employee;
    /*% } %*/

    public UMUserJSON() {
    }

    public UMUserJSON(UMUser user) {
        this(user.getLogin(), user.getFirstName(), user.getLastName(),
            user.getEmail(), /*% if (feature.UM_AccountActivation) { %*/ user.getActivated(), /*% } %*/ user.getLangKey(),
            user.getAuthorities().stream().map(Authority::getName)
                .collect(Collectors.toSet()) /*% if (feature.MWM_EmployeeAuthentication) { %*/ , user.getEmployee() /*% } %*/);
    }

    public UMUserJSON(String login, String firstName, String lastName,
                      String email, /*% if (feature.UM_AccountActivation) { %*/ boolean activated, /*% } %*/ String langKey, Set<String> authorities /*% if (feature.MWM_EmployeeAuthentication) { %*/ , Employee employee /*% } %*/) {
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
/*% if (feature.UM_AccountActivation) { %*/        this.activated = activated; /*% } %*/
        this.langKey = langKey;
        this.authorities = authorities;
/*% if (feature.MWM_EmployeeAuthentication) { %*/
        if (employee != null) {
          this.employee = new EmployeeDTO(employee);
        }
/*% } %*/
    }

    public String getLogin() {
        return login;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }
    /*% if (feature.UM_AccountActivation) { %*/
    public boolean isActivated() {
        return activated;
    }
    /*% } %*/

    public String getLangKey() {
        return langKey;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }
    /*% if (feature.MWM_EmployeeAuthentication) { %*/

    public EmployeeDTO getEmployee() {
        return employee;
    }

    /*% } %*/
    @Override
    public String toString() {
        return "UMUserJSON{" +
            "login='" + login + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", email='" + email + '\'' +
            /*% if (feature.UM_AccountActivation) { %*/ ", activated=" + activated + /*% } %*/
            ", langKey='" + langKey + '\'' +
            ", authorities=" + authorities +
            "}";
    }

}
/*% } %*/
