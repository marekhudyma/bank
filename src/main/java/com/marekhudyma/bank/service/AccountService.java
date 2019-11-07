package com.marekhudyma.bank.service;


import com.marekhudyma.bank.domain.Account;
import com.marekhudyma.bank.domain.BalanceChange;
import com.marekhudyma.bank.domain.Transfer;
import com.marekhudyma.bank.domain.id.AccountId;
import com.marekhudyma.bank.domain.id.BalanceChangeId;
import com.marekhudyma.bank.domain.id.TransferId;
import com.marekhudyma.bank.repository.AccountRepository;
import com.marekhudyma.bank.repository.BalanceChangeRepository;
import com.marekhudyma.bank.repository.TransferRepository;
import com.marekhudyma.bank.service.result.AccountResult;
import com.marekhudyma.bank.service.result.TransferResult;
import io.micronaut.context.env.Environment;
import io.micronaut.spring.tx.annotation.Transactional;
import java.math.BigDecimal;
import java.util.UUID;
import javax.inject.Singleton;
import javax.persistence.PessimisticLockException;
import lombok.RequiredArgsConstructor;

import static com.marekhudyma.bank.service.result.TransferResult.TransferResultStatus.CREDITOR_NOT_FOUND;
import static com.marekhudyma.bank.service.result.TransferResult.TransferResultStatus.DEBTOR_NOT_FOUND;
import static com.marekhudyma.bank.service.result.TransferResult.TransferResultStatus.INSUFFICIENT_FOUNDS;
import static com.marekhudyma.bank.service.result.TransferResult.TransferResultStatus.INVALID_TRANSFER_AMOUNT;
import static com.marekhudyma.bank.service.result.TransferResult.TransferResultStatus.LOCKED;
import static com.marekhudyma.bank.service.result.TransferResult.TransferResultStatus.SUCCESSFUL;

@Singleton
@RequiredArgsConstructor
public class AccountService {

    private static final BigDecimal INITIAL_BANK_BALANCE_DEFAULT = BigDecimal.ZERO;
    private final AccountRepository accountRepository;
    private final BalanceChangeRepository balanceChangeRepository;
    private final TransferRepository transferRepository;
    private final Environment environment;

    @Transactional
    public AccountResult create(String firstName, String lastName) {
        BigDecimal initialBankBalance =
                environment.getProperty("application.balance", BigDecimal.class, INITIAL_BANK_BALANCE_DEFAULT);
        Account account = accountRepository.save(Account.builder()
                .id(new AccountId(UUID.randomUUID()))
                .firstName(firstName)
                .lastName(lastName)
                .bankBalance(initialBankBalance)
                .build());
        balanceChangeRepository.save(BalanceChange.builder()
                .id(BalanceChangeId.random())
                .accountId(account.getId())
                .amount(initialBankBalance)
                .build());
        return AccountResult.create(account);
    }

    @Transactional
    public AccountResult get(AccountId id) {
        return accountRepository.findByIdAndLockNoWait(id)
                .map(AccountResult::create)
                .orElseGet(() -> AccountResult.create(AccountResult.AccountResultStatus.NOT_FOUND));
    }

    /**
     * Creates a transfer between debtor and creditor
     *
     * @param debtorAccountId   debtor is source of money (substract money to account)
     * @param creditorAccountId creditor is target of monet (add money to account)
     * @param amount            how much money are transferred from debtor to creditor
     * @return result of operation
     */
    @Transactional
    public TransferResult makeTransfer(AccountId debtorAccountId, AccountId creditorAccountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            return TransferResult.create(INVALID_TRANSFER_AMOUNT);
        }
        try {
            return accountRepository.findByIdAndLockNoWait(debtorAccountId)
                    .map(debtor -> accountRepository.findByIdAndLockNoWait(creditorAccountId)
                            .map(creditor -> {
                                if (debtor.getBankBalance().compareTo(amount) < 0) {
                                    return TransferResult.create(INSUFFICIENT_FOUNDS);
                                }
                                makeTransferChanges(debtor, creditor, amount);
                                return TransferResult.create(SUCCESSFUL);
                            }).orElse(TransferResult.create(CREDITOR_NOT_FOUND)))
                    .orElse(TransferResult.create(DEBTOR_NOT_FOUND));
        } catch (PessimisticLockException e) {
            return TransferResult.create(LOCKED);
        }
    }

    private void makeTransferChanges(Account debtor, Account creditor, BigDecimal amount) {
        BalanceChange debtorBalanceChange = generateBalanceChange(debtor.getId(), amount.negate());
        BalanceChange creditorBalanceChange = generateBalanceChange(creditor.getId(), amount);
        generatTransfer(debtorBalanceChange.getId(), creditorBalanceChange.getId());
        updateBankBalance(debtor, amount.negate());
        updateBankBalance(creditor, amount);
    }

    private BalanceChange generateBalanceChange(AccountId accountId, BigDecimal amount) {
        BalanceChange debtorBalanceChange = BalanceChange.builder()
                .id(BalanceChangeId.random())
                .accountId(accountId)
                .amount(amount)
                .build();
        return balanceChangeRepository.save(debtorBalanceChange);
    }

    private Transfer generatTransfer(BalanceChangeId debtorChangeId, BalanceChangeId creditorBalanceChangeId) {
        Transfer transfer = Transfer.builder()
                .id(TransferId.random())
                .debtorChangeId(debtorChangeId)
                .creditorChangeId(creditorBalanceChangeId)
                .build();
        return transferRepository.save(transfer);
    }

    private Account updateBankBalance(Account account, BigDecimal amount) {
        account.setBankBalance(account.getBankBalance().add(amount));
        return accountRepository.save(account);
    }
}