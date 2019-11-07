package com.marekhudyma.bank.repository;

import com.marekhudyma.bank.domain.Account;
import com.marekhudyma.bank.domain.AccountTestBuilder;
import com.marekhudyma.bank.domain.BalanceChange;
import com.marekhudyma.bank.domain.BalanceChangeTestBuilder;
import com.marekhudyma.bank.domain.Transfer;
import com.marekhudyma.bank.domain.TransferTestBuilder;
import com.marekhudyma.bank.domain.id.AccountId;
import com.marekhudyma.bank.repository.impl.TransferTestRepository;
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
class TransferRepositoryTest extends AbstractTest {

    @Inject
    private TransferRepository underTest;

    @Inject
    private TransferTestRepository transferTestRepository;

    @Inject
    private BalanceChangeRepository balanceChangeRepository;

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
    void shouldCreateTransaction() {
        // given
        Account account = createAccount();
        BalanceChange balanceChangeCreditor = createBalanceChange(++seed, account.getId());
        BalanceChange balanceChangeDebtor = createBalanceChange(++seed, account.getId());
        Transfer transfer = new TransferTestBuilder(seed).withTestDefaults()
                .creditorChangeId(balanceChangeCreditor.getId())
                .debtorChangeId(balanceChangeDebtor.getId())
                .build();

        // when
        underTest.save(transfer);

        // then
        Optional<Transfer> actual = transferTestRepository.findById(transfer.getId());
        assertThat(actual).isNotEmpty();
        Transfer expected = new TransferTestBuilder(seed).withTestDefaults()
                .creditorChangeId(balanceChangeCreditor.getId())
                .debtorChangeId(balanceChangeDebtor.getId())
                .build();
        assertThat(actual.get()).isEqualTo(expected);
    }

    @Test
    void shouldNotCreateTransferBecauseNoCreditorBalanceChange() {
        // given
        Account account = createAccount();
        BalanceChange balanceChangeDebtor = createBalanceChange(++seed, account.getId());
        Transfer transfer = new TransferTestBuilder(seed).withTestDefaults()
                .creditorChangeId(null)
                .debtorChangeId(balanceChangeDebtor.getId())
                .build();

        // when, then
        assertThrows(PersistenceException.class, () -> {
            underTest.save(transfer);
            entityManager.flush();
        });
    }

    @Test
    void shouldNotCreateTransferBecauseNoDebtorBalanceChange() {
        // given
        Account account = createAccount();
        BalanceChange balanceChangeCreditor = createBalanceChange(++seed, account.getId());
        Transfer transfer = new TransferTestBuilder(seed).withTestDefaults()
                .creditorChangeId(balanceChangeCreditor.getId())
                .debtorChangeId(null)
                .build();

        // when, then
        assertThrows(PersistenceException.class, () -> {
            underTest.save(transfer);
            entityManager.flush();
        });
    }

    private Account createAccount() {
        Account account = new AccountTestBuilder(seed).withTestDefaults().build();
        accountRepository.save(account);
        entityManager.flush();
        return account;
    }

    private BalanceChange createBalanceChange(int seed, AccountId accountId) {
        BalanceChange balanceChange = new BalanceChangeTestBuilder(seed).withTestDefaults()
                .accountId(accountId)
                .build();
        balanceChangeRepository.save(balanceChange);
        entityManager.flush();
        return balanceChange;
    }

}