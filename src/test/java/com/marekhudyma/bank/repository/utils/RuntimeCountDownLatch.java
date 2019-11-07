package com.marekhudyma.bank.repository.utils;

import java.util.concurrent.CountDownLatch;

public class RuntimeCountDownLatch extends CountDownLatch {

    public RuntimeCountDownLatch(int count) {
        super(count);
    }

    @Override
    public void await() {
        try {
            super.await();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}