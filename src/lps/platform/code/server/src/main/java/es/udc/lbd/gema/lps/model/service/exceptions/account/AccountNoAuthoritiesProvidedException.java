/*% if (feature.UserManagement) { %*/
package es.udc.lbd.gema.lps.model.service.exceptions.account;

import es.udc.lbd.gema.lps.model.service.exceptions.account.AccountException;

import org.springframework.http.HttpStatus;

public class AccountNoAuthoritiesProvidedException extends AccountException {

    public AccountNoAuthoritiesProvidedException() {
        super("account.not_authorities_provided", HttpStatus.BAD_REQUEST);
        logger.warn("Account: No authorities provided");
    }
}
/*% } %*/
