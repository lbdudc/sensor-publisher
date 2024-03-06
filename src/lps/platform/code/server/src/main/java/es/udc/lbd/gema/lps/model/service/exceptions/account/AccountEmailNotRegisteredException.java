/*% if (feature.UserManagement) { %*/
package es.udc.lbd.gema.lps.model.service.exceptions.account;

import es.udc.lbd.gema.lps.model.service.exceptions.account.AccountException;

import org.springframework.http.HttpStatus;

public class AccountEmailNotRegisteredException extends AccountException {

    public AccountEmailNotRegisteredException(String param) {
        super("account.email_not_registered", param, HttpStatus.BAD_REQUEST);
        logger.warn("Account: Email '{}' not registered.", param);
    }
}
/*% } %*/
