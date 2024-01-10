/*% if (feature.UserManagement) { %*/
package es.udc.lbd.gema.lps.model.service.exceptions.account;

import es.udc.lbd.gema.lps.model.service.exceptions.account.AccountException;

import org.springframework.http.HttpStatus;

public class AccountPasswordResetKeyExpiredException extends AccountException {

    public AccountPasswordResetKeyExpiredException(String param) {
        super("account.password.reset_key_expired", param, HttpStatus.BAD_REQUEST);
        logger.warn("Account: Password reset key expired: '{}'.", param);
    }
}
/*% } %*/
