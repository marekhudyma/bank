package com.marekhudyma.bank.service.result;

import com.marekhudyma.bank.domain.Account;

import static com.marekhudyma.bank.service.result.TransferResult.TransferResultStatus.SUCCESSFUL;


public class TransferResult extends ServiceOperationResult<Account, TransferResult.TransferResultStatus> {

    private TransferResult(Account result, TransferResultStatus status) {
        super(result, status);
    }

    public static TransferResult create(Account result) {
        return new TransferResult(result, SUCCESSFUL);
    }

    public static TransferResult create(TransferResultStatus status) {
        return new TransferResult(null, status);
    }

    public static TransferResult create(Account result, TransferResultStatus status) {
        return new TransferResult(result, status);
    }

    public enum TransferResultStatus implements Successfulable {
        SUCCESSFUL,
        INVALID_TRANSFER_AMOUNT,
        CREDITOR_NOT_FOUND,
        DEBTOR_NOT_FOUND,
        INSUFFICIENT_FOUNDS,
        LOCKED;

        public boolean isSuccessful() {
            return SUCCESSFUL.equals(this);
        }
    }
}

