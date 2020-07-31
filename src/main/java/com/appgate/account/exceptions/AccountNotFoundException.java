package com.appgate.account.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Jgutierrez on 21/02/2019.
 */
@ResponseStatus(org.springframework.http.HttpStatus.NOT_FOUND)
public class AccountNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public AccountNotFoundException(String accountNumber) {
        super("No such account: " + accountNumber);
    }

}
