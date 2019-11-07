package com.marekhudyma.bank.service.result;

import com.marekhudyma.bank.domain.Account;

import static com.marekhudyma.bank.service.result.AccountResult.AccountResultStatus.SUCCESSFUL;

public class AccountResult extends ServiceOperationResult<Account, AccountResult.AccountResultStatus> {

    private AccountResult(Account result, AccountResultStatus status) {
        super(result, status);
    }

    public static AccountResult create(Account result) {
        return new AccountResult(result, SUCCESSFUL);
    }

    public static AccountResult create(AccountResultStatus status) {
        return new AccountResult(null, status);
    }

    public static AccountResult create(Account result, AccountResultStatus status) {
        return new AccountResult(result, status);
    }

    public enum AccountResultStatus implements Successfulable {
        SUCCESSFUL,
        NOT_FOUND;

        public boolean isSuccessful() {
            return SUCCESSFUL.equals(this);
        }
    }
}

