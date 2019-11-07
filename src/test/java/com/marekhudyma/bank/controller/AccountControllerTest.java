package com.marekhudyma.bank.controller;

import com.marekhudyma.bank.domain.Account;
import com.marekhudyma.bank.domain.AccountTestBuilder;
import com.marekhudyma.bank.domain.BalanceChange;
import com.marekhudyma.bank.domain.BalanceChangeTestBuilder;
import com.marekhudyma.bank.domain.Transfer;
import com.marekhudyma.bank.domain.id.AccountId;
import com.marekhudyma.bank.dto.AccountResponseDto;
import com.marekhudyma.bank.dto.AccountResponseDtoTestBuilder;
import com.marekhudyma.bank.dto.CreateAccountRequestDto;
import com.marekhudyma.bank.dto.CreateAccountRequestDtoTestBuilder;
import com.marekhudyma.bank.dto.CreateTransferDto;
import com.marekhudyma.bank.dto.ErrorDto;
import com.marekhudyma.bank.repository.AccountRepository;
import com.marekhudyma.bank.repository.impl.BalanceChangeTestRepository;
import com.marekhudyma.bank.repository.impl.TransferTestRepository;
import com.marekhudyma.bank.repository.utils.AccountThreadSimulator;
import com.marekhudyma.bank.repository.utils.RuntimeCountDownLatch;
import com.marekhudyma.bank.util.AbstractTest;
import io.micronaut.context.env.Environment;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MicronautTest;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import javax.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest
class AccountControllerTest extends AbstractTest {

    @Inject
    @Client("/")
    private RxHttpClient client;

    @Inject
    private AccountRepository accountRepository;

    @Inject
    private BalanceChangeTestRepository balanceChangeRepository;

    @Inject
    private TransferTestRepository transferRepository;

    @Inject
    private Environment environment;

    @Inject
    private AccountThreadSimulator accountThreadSimulator;

    private int seed;

    private BigDecimal initialBankBalance;

    @BeforeEach
    void setUp() {
        seed = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
        initialBankBalance = getInitialBankBalance();
    }

    @Test
    void shouldCreateAccount() {
        // given
        CreateAccountRequestDto createAccountRequestDto =
                new CreateAccountRequestDtoTestBuilder(seed).withTestDefaults().build();
        HttpRequest<CreateAccountRequestDto> request = HttpRequest.POST("/accounts", createAccountRequestDto);

        // when
        HttpResponse<AccountResponseDto> response = client.toBlocking().exchange(request, AccountResponseDto.class);

        // then
        assertThat(response).hasFieldOrPropertyWithValue("status", HttpStatus.CREATED);
        AccountId idActual = entityId(response);
        assertThat(idActual).isNotNull();

        Optional<AccountResponseDto> actualDtoOptional = response.getBody(AccountResponseDto.class);
        assertThat(actualDtoOptional).isPresent();
        AccountResponseDto actualDto = actualDtoOptional.get();
        AccountResponseDto expectedDto = new AccountResponseDtoTestBuilder(seed).withTestDefaults()
                .id(idActual)
                .bankBalance(initialBankBalance)
                .build();
        assertThat(actualDto).isEqualTo(expectedDto);

        // validate if account was created
        Optional<Account> accountActualOptional = accountRepository.findByIdAndLockNoWait(idActual);
        assertThat(accountActualOptional).isPresent();
        Account accountActual = accountActualOptional.get();
        Account accountExpected = new AccountTestBuilder(seed).withTestDefaults()
                .id(idActual)
                .bankBalance(initialBankBalance)
                .build();
        assertThat(accountActual).isEqualTo(accountExpected);

        // validate if balance change was created
        Collection<BalanceChange> balanceChangesActual = balanceChangeRepository.findByAccountIdOrderByCreatedAtDesc(idActual);
        assertThat(balanceChangesActual).hasSize(1);
        BalanceChange balanceChangeActual = balanceChangesActual.iterator().next();
        BalanceChange balanceChangeExpected = new BalanceChangeTestBuilder(seed).withTestDefaults()
                .accountId(idActual)
                .amount(initialBankBalance)
                .build();
        assertThat(balanceChangeActual).isEqualToIgnoringGivenFields(balanceChangeExpected, "id");
    }

    @Test
    void shouldGetAccount() {
        AccountId id = createAccount();
        HttpRequest<?> request = HttpRequest.GET(format("/accounts/%s", id));

        // when
        HttpResponse<AccountResponseDto> response = client.toBlocking().exchange(request, AccountResponseDto.class);

        // then
        assertThat(response).hasFieldOrPropertyWithValue("status", HttpStatus.OK);
        Optional<AccountResponseDto> actualDtoOptional = response.getBody(AccountResponseDto.class);
        assertThat(actualDtoOptional).isPresent();
        AccountResponseDto actualDto = actualDtoOptional.get();
        AccountResponseDto expectedDto = new AccountResponseDtoTestBuilder(seed).withTestDefaults()
                .id(id)
                .bankBalance(initialBankBalance)
                .build();
        assertThat(actualDto).isEqualTo(expectedDto);
    }

    @Test
    void shouldNotGetAccount() {
        // given
        UUID randomId = UUID.randomUUID();
        HttpRequest<ErrorDto> request = HttpRequest.GET(format("/accounts/%s", randomId));

        // when
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, ErrorDto.class));

        // then
        assertThat(exception.getStatus().getCode()).isEqualTo(HttpStatus.NOT_FOUND.getCode());
    }

    @Test
    void shouldCreateTransfer() {
        // given
        AccountId debtorId = createAccount();
        AccountId creditorId = createAccount();
        HttpRequest<CreateTransferDto> request = HttpRequest.POST(format("/accounts/%s/transfers", debtorId),
                CreateTransferDto.builder().creditorId(creditorId).amount(new BigDecimal("1.00")).build());

        // when
        HttpResponse<?> response = client.toBlocking().exchange(request);

        // then
        assertThat(response.getStatus().getCode()).isEqualTo(HttpStatus.NO_CONTENT.getCode());

        // validate debtor
        Account debtorActual = accountRepository.findByIdAndLockNoWait(debtorId).get();
        Account debtorExpected = Account.builder()
                .id(debtorId)
                .firstName(format("firstName.%d", seed))
                .lastName(format("lastName.%d", seed))
                .build();
        assertThat(debtorActual).isEqualToIgnoringGivenFields(debtorExpected, "bankBalance");
        assertThat(debtorActual.getBankBalance()).isEqualByComparingTo(new BigDecimal("99.00"));

        // validate creditor
        Account creditorActual = accountRepository.findByIdAndLockNoWait(creditorId).get();
        Account creditorExpected = Account.builder()
                .id(creditorId)
                .firstName(format("firstName.%d", seed))
                .lastName(format("lastName.%d", seed))
                .build();
        assertThat(creditorActual).isEqualToIgnoringGivenFields(creditorExpected, "bankBalance");
        assertThat(creditorActual.getBankBalance()).isEqualByComparingTo(new BigDecimal("101.00"));

        // validate debtor balance changes
        List<BalanceChange> debtorBalanceChanges = balanceChangeRepository.findByAccountIdOrderByCreatedAtDesc(debtorId);
        assertThat(debtorBalanceChanges).hasSize(2);
        BigDecimal debtorBalanceChangesSumActual = debtorBalanceChanges.stream()
                .map(BalanceChange::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertThat(debtorBalanceChangesSumActual).isEqualByComparingTo(new BigDecimal("99.00"));

        // validate creditor balance changes
        List<BalanceChange> creditorBalanceChanges = balanceChangeRepository.findByAccountIdOrderByCreatedAtDesc(creditorId);
        assertThat(creditorBalanceChanges).hasSize(2);
        BigDecimal creditorBalanceChangesSumActual = creditorBalanceChanges.stream()
                .map(BalanceChange::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertThat(creditorBalanceChangesSumActual).isEqualByComparingTo(new BigDecimal("101.00"));

        // validate tranfer
        Collection<Transfer> transfers = transferRepository.findByDebtorChangeIdAndCreditorChangeId(
                debtorBalanceChanges.get(0).getId(),
                creditorBalanceChanges.get(0).getId());
        assertThat(transfers).hasSize(1);
    }

    @Test
    void shouldNotCreateTransferBecauseLockedAccount() {
        // given
        AccountId debtorId = createAccount();
        AccountId creditorId = createAccount();

        RuntimeCountDownLatch otherThreadCountDownLatch = new RuntimeCountDownLatch(1);
        startThread(() -> {
            accountThreadSimulator.findByIdAndLockNoWait(debtorId, otherThreadCountDownLatch);
        });

        HttpRequest<CreateTransferDto> request = HttpRequest.POST(format("/accounts/%s/transfers", debtorId),
                CreateTransferDto.builder().creditorId(creditorId).amount(new BigDecimal("1.00")).build());

        // when
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, ErrorDto.class));

        // then
        otherThreadCountDownLatch.countDown();
        assertThat(exception.getStatus().getCode()).isEqualTo(HttpStatus.LOCKED.getCode());
    }

    @Test
    void shouldNotCreateTransferBecauseMissingDebtor() {
        // given
        AccountId randomId = AccountId.random();
        AccountId creditorId = createAccount();
        HttpRequest<CreateTransferDto> request = HttpRequest.POST(format("/accounts/%s/transfers", randomId),
                CreateTransferDto.builder().creditorId(creditorId).amount(new BigDecimal("1.00")).build());

        // when
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, ErrorDto.class));

        // then
        assertThat(exception.getStatus().getCode()).isEqualTo(HttpStatus.NOT_FOUND.getCode());
    }

    @Test
    void shouldNotCreateTransferBecauseMissingCreditor() {
        // given
        AccountId creditorId = AccountId.random();
        AccountId randomId = createAccount();
        HttpRequest<CreateTransferDto> request = HttpRequest.POST(format("/accounts/%s/transfers", randomId),
                CreateTransferDto.builder().creditorId(creditorId).amount(new BigDecimal("1.00")).build());

        // when
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, ErrorDto.class));

        // then
        assertThat(exception.getStatus().getCode()).isEqualTo(HttpStatus.NOT_FOUND.getCode());
    }

    @Test
    void shouldNotCreateTransferBecauseInvalidAmount() {
        // given
        AccountId debtorId = createAccount();
        AccountId creditorId = createAccount();
        HttpRequest<CreateTransferDto> request = HttpRequest.POST(format("/accounts/%s/transfers", debtorId),
                CreateTransferDto.builder().creditorId(creditorId).amount(new BigDecimal("-1.00")).build());

        // when
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, ErrorDto.class));

        // then
        assertThat(exception.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
    }

    @Test
    void shouldNotCreateTransferBecauseInsufficientFounds() {
        // given
        AccountId debtorId = createAccount();
        AccountId creditorId = createAccount();
        HttpRequest<CreateTransferDto> request = HttpRequest.POST(format("/accounts/%s/transfers", debtorId),
                CreateTransferDto.builder().creditorId(creditorId).amount(new BigDecimal("1000000000.00")).build());

        // when
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, ErrorDto.class));

        // then
        assertThat(exception.getStatus().getCode()).isEqualTo(HttpStatus.PRECONDITION_FAILED.getCode());
    }

    private AccountId createAccount() {
        CreateAccountRequestDto createAccountRequestDto =
                new CreateAccountRequestDtoTestBuilder(seed).withTestDefaults().build();
        HttpRequest<?> request = HttpRequest.POST("/accounts", createAccountRequestDto);
        HttpResponse<?> response = client.toBlocking().exchange(request);
        return entityId(response);
    }

    private AccountId entityId(HttpResponse<?> response) {
        String path = "/accounts/";
        String value = response.header(HttpHeaders.LOCATION);
        return new AccountId(Objects.requireNonNull(value).replaceFirst(path, ""));
    }

    private BigDecimal getInitialBankBalance() {
        return environment.getProperty("application.balance", BigDecimal.class, BigDecimal.ZERO);
    }
}
