/*% if (feature.UserManagement) { %*/
package es.udc.lbd.gema.lps.model.service.exceptions.account;

import es.udc.lbd.gema.lps.model.service.exceptions.account.AccountException;

import org.springframework.http.HttpStatus;

public class AccountLoginInUseException extends AccountException {

    public AccountLoginInUseException(String param) {
        super("account.register.login_in_use", param, HttpStatus.BAD_REQUEST);
        logger.warn("Account: Login '{}' already in use.", param);
    }
}
/*% } %*/
