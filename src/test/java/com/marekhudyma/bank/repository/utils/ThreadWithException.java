package com.marekhudyma.bank.repository.utils;

public class ThreadWithException extends Thread {

    private final Runnable runnable;

    private Exception exception;

    public ThreadWithException(Runnable runnable) {
        this.runnable = runnable;
        this.exception = null;
    }

    @Override
    public void run() {
        try {
            this.runnable.run();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ThreadWithException");
            this.exception = e;
        }
    }

    public Exception getException() {
        return exception;
    }
}
