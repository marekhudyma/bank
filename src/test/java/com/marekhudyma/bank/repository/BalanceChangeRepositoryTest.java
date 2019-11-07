package com.marekhudyma.bank.repository;

import com.marekhudyma.bank.domain.Account;
import com.marekhudyma.bank.domain.AccountTestBuilder;
import com.marekhudyma.bank.domain.BalanceChange;
import com.marekhudyma.bank.domain.BalanceChangeTestBuilder;
import com.marekhudyma.bank.repository.impl.BalanceChangeTestRepository;
import com.marekhudyma.bank.util.AbstractTest;
import io.micronaut.test.annotation.MicronautTest;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest
@Transactional
class BalanceChangeRepositoryTest extends AbstractTest {

    @Inject
    private BalanceChangeRepository underTest;

    @Inject
    private BalanceChangeTestRepository balanceChangeTestRepository;

    @Inject
    private AccountRepository accountRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private int seed;

    @BeforeEach
    void setUp() {
        seed = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
    }

    @Test
    void shouldCreateBalanceChange() {
        // given
        Account account = createAccount();
        BalanceChange balanceChange = new BalanceChangeTestBuilder(seed).withTestDefaults()
                .accountId(account.getId())
                .build();

        // when
        underTest.save(balanceChange);

        // then
        Optional<BalanceChange> actual = balanceChangeTestRepository.findById(balanceChange.getId());
        assertThat(actual).isNotEmpty();
        BalanceChange expected = new BalanceChangeTestBuilder(seed).withTestDefaults().build();
        assertThat(actual.get()).isEqualTo(expected);
    }

    @Test
    void shouldNotCreateBalanceChangeBecauseNoAccount() {
        // given
        BalanceChange balanceChange = new BalanceChangeTestBuilder(seed).withTestDefaults().build();

        // when, then
        assertThrows(PersistenceException.class, () -> {
            underTest.save(balanceChange);
            entityManager.flush();
        });
    }

    private Account createAccount() {
        Account account = new AccountTestBuilder(seed).withTestDefaults().build();
        accountRepository.save(account);
        entityManager.flush();
        return account;
    }

}