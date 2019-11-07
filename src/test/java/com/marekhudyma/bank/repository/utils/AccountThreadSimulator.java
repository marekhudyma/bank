package com.marekhudyma.bank.repository.utils;

import com.marekhudyma.bank.domain.AccountTestBuilder;
import com.marekhudyma.bank.domain.id.AccountId;
import com.marekhudyma.bank.repository.AccountRepository;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;

@Singleton
public class AccountThreadSimulator {

    @Inject
    private AccountRepository accountRepository;

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public void createAccount(int seed) {
        accountRepository.save(new AccountTestBuilder(seed).withTestDefaults().build());
    }

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public void findByIdAndLockNoWait(AccountId accountId,
                                      RuntimeCountDownLatch mainThreadCountDownLatch,
                                      RuntimeCountDownLatch otherThreadCountDownLatch) {
        accountRepository.findByIdAndLockNoWait(accountId);
        mainThreadCountDownLatch.countDown();
        otherThreadCountDownLatch.await();
    }

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public void findByIdAndLockNoWait(AccountId accountId,
                                      RuntimeCountDownLatch otherThreadCountDownLatch) {
        accountRepository.findByIdAndLockNoWait(accountId);
        otherThreadCountDownLatch.await();
    }
}
