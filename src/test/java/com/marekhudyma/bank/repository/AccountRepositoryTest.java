package com.marekhudyma.bank.repository;

import com.marekhudyma.bank.domain.Account;
import com.marekhudyma.bank.domain.AccountTestBuilder;
import com.marekhudyma.bank.domain.id.AccountId;
import com.marekhudyma.bank.repository.impl.AccountTestRepository;
import com.marekhudyma.bank.repository.utils.AccountThreadSimulator;
import com.marekhudyma.bank.repository.utils.RuntimeCountDownLatch;
import com.marekhudyma.bank.util.AbstractTest;
import io.micronaut.test.annotation.MicronautTest;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PessimisticLockException;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest
@Transactional
class AccountRepositoryTest extends AbstractTest {

    @Inject
    private AccountRepository underTest;

    @Inject
    private AccountTestRepository accountTestRepository;

    @Inject
    private EntityManager entityManager;

    @Inject
    private AccountThreadSimulator otherThread;

    private int seed;

    @BeforeEach
    void setUp() {
        seed = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
    }

    @Test
    void shouldCreateAccount() {
        // given
        Account account = new AccountTestBuilder(seed).withTestDefaults().build();

        // when
        underTest.save(account);
        entityManager.flush();

        // then
        Optional<Account> actual = accountTestRepository.findById(account.getId());

        assertThat(actual).isNotEmpty();
        Account expected = new AccountTestBuilder(seed).withTestDefaults().build();
        assertThat(actual.get()).isEqualTo(expected);
    }

    @Test
    void shouldModifyAccount() {
        // given
        Account account = underTest.save(new AccountTestBuilder(seed).withTestDefaults().build());

        // when
        account.setBankBalance(new BigDecimal("1000000000.00"));
        underTest.save(account);

        // then
        Optional<Account> actual = accountTestRepository.findById(account.getId());
        assertThat(actual).isNotEmpty();
        Account expected = new AccountTestBuilder(seed).withTestDefaults()
                .bankBalance(new BigDecimal("1000000000.00"))
                .build();
        assertThat(actual.get()).isEqualTo(expected);
    }

    @Test
    void shouldFindAndLockAccount() {
        // given
        Account account = underTest.save(new AccountTestBuilder(seed).withTestDefaults().build());
        entityManager.flush();

        // when
        Optional<Account> actual = underTest.findByIdAndLockNoWait(account.getId());

        // then
        assertThat(actual).isNotEmpty();
        Account expected = new AccountTestBuilder(seed).withTestDefaults().build();
        assertThat(actual.get()).isEqualTo(expected);
    }

    @Test
    void shouldNotFindAndLockAccount() {
        // given
        AccountId randomId = AccountId.random();

        // when
        Optional<Account> actual = underTest.findByIdAndLockNoWait(randomId);

        // then
        assertThat(actual).isEmpty();
    }

    // this test will run other thread, that will lock 1 row,
    // after this fact main thread will try to lock the same row and will fail.
    @Test
    void shouldThrowPessimisticLockExceptionWhenRowIsLockedByOtherTransaction() {
        // given
        RuntimeCountDownLatch mainThreadCountDownLatch = new RuntimeCountDownLatch(1);
        RuntimeCountDownLatch otherThreadCountDownLatch = new RuntimeCountDownLatch(1);
        AccountId accountId = new AccountId(new UUID(0, seed));
        // create account
        executeInBlockingThread(() -> {
            otherThread.createAccount(seed);
        });
        // run other thread that will lock row, then main thread will try to lock it and will fail
        startThread(() -> {
            otherThread.findByIdAndLockNoWait(accountId, mainThreadCountDownLatch, otherThreadCountDownLatch);
        });
        // wait till other thread lock row
        mainThreadCountDownLatch.await();

        // when, then
        assertThrows(PessimisticLockException.class, () -> underTest.findByIdAndLockNoWait(accountId));

        // clean
        otherThreadCountDownLatch.countDown();
    }

}