package com.marekhudyma.bank.util;

import com.marekhudyma.bank.Database;
import com.marekhudyma.bank.repository.utils.ThreadWithException;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AbstractTest {

    static {
        Database.startDatabase();
    }

    protected ThreadWithException executeInBlockingThread(Runnable runnable) {
        ThreadWithException t = startThread(runnable);
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return t;
    }

    protected ThreadWithException startThread(Runnable runnable) {
        ThreadWithException t = new ThreadWithException(runnable);
        t.start();
        return t;
    }
}
