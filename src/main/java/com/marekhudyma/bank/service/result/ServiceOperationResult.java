package com.marekhudyma.bank.service.result;

public abstract class ServiceOperationResult<R, S extends Successfulable> {

    protected final R result;

    protected final S status;

    public ServiceOperationResult(R result, S status) {
        this.result = result;
        this.status = status;
    }

    public R getResult() {
        return result;
    }

    public S getStatus() {
        return status;
    }

    public boolean isSuccessful() {
        return status.isSuccessful();
    }

}
