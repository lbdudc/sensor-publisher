/*% if (feature.UserManagement) { %*/
package es.udc.lbd.gema.lps.model.service.exceptions.account;

import es.udc.lbd.gema.lps.model.service.exceptions.account.AccountException;

import org.springframework.http.HttpStatus;

public class CredentialsAreNotValidException extends AccountException {

    public CredentialsAreNotValidException(String errorMsg) {
        super("account.credentials_are_not_valid", errorMsg, HttpStatus.BAD_REQUEST);
        logger.warn("Account: Credentials are not valid: '{}'.", errorMsg);
    }
}
/*% } %*/
