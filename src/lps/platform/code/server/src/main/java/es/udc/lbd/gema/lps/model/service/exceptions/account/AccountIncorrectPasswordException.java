/*% if (feature.UserManagement) { %*/
package es.udc.lbd.gema.lps.model.service.exceptions.account;

import es.udc.lbd.gema.lps.model.service.exceptions.account.AccountException;

import org.springframework.http.HttpStatus;

public class AccountIncorrectPasswordException extends AccountException {

    public AccountIncorrectPasswordException() {
        super("account.password.incorrect_password", HttpStatus.BAD_REQUEST);
        logger.warn("Account: Incorrect password");
    }
}
/*% } %*/
