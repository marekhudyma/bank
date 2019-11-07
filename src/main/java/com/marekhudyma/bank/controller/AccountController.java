package com.marekhudyma.bank.controller;

import com.marekhudyma.bank.domain.id.AccountId;
import com.marekhudyma.bank.dto.CreateAccountRequestDto;
import com.marekhudyma.bank.dto.CreateTransferDto;
import com.marekhudyma.bank.dto.ErrorDto;
import com.marekhudyma.bank.service.AccountService;
import com.marekhudyma.bank.service.result.AccountResult;
import com.marekhudyma.bank.service.result.TransferResult;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.net.URI;
import java.util.UUID;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;

import static com.marekhudyma.bank.service.result.AccountResult.AccountResultStatus.NOT_FOUND;
import static com.marekhudyma.bank.service.result.TransferResult.TransferResultStatus.CREDITOR_NOT_FOUND;
import static com.marekhudyma.bank.service.result.TransferResult.TransferResultStatus.DEBTOR_NOT_FOUND;
import static com.marekhudyma.bank.service.result.TransferResult.TransferResultStatus.INVALID_TRANSFER_AMOUNT;
import static com.marekhudyma.bank.service.result.TransferResult.TransferResultStatus.LOCKED;
import static io.micronaut.http.MediaType.APPLICATION_JSON;


@Validated
@Controller
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    private final AccountToAccountResponseConverter accountToAccountResponseConverter;

    @Operation(summary = "Creates account", description = "Creates account")
    @ApiResponse(content = @Content(mediaType = APPLICATION_JSON))
    @ApiResponse(responseCode = "201", description = "Account created")
    @Post("/accounts")
    public HttpResponse createAccount(@Body @Valid CreateAccountRequestDto createAccountRequestDto) {
        AccountResult result = accountService.create(createAccountRequestDto.getOwner().getFirstName(),
                createAccountRequestDto.getOwner().getLastName());
        if (result.getStatus() == AccountResult.AccountResultStatus.SUCCESSFUL) {
            return HttpResponse
                    .created(accountToAccountResponseConverter.convert(result.getResult()))
                    .headers(headers -> headers.location(location(result.getResult().getId().getValue())));
        } else {
            return HttpResponse.serverError(ErrorDto.builder().error("UNKNOWN_ERROR").build());
        }
    }

    @Operation(summary = "Get account by id", description = "Get account by id")
    @ApiResponse(content = @Content(mediaType = APPLICATION_JSON))
    @ApiResponse(responseCode = "200", description = "Returned account")
    @ApiResponse(responseCode = "404", description = "When account was not found")
    @Get(value = "/accounts/{id}", produces = APPLICATION_JSON)
    public HttpResponse getAccount(UUID id) {
        AccountResult result = accountService.get(new AccountId(id));

        switch (result.getStatus()) {
            case SUCCESSFUL:
                return HttpResponse
                        .ok()
                        .body(accountToAccountResponseConverter.convert(result.getResult()));
            case NOT_FOUND:
                return HttpResponse.notFound(ErrorDto.builder().error(NOT_FOUND.toString()).build());
            default:
                return HttpResponse.serverError(ErrorDto.builder().error("UNKNOWN_ERROR").build());
        }
    }

    @Operation(summary = "Creates transfer", description = "Creates transfer between two accounts")
    @ApiResponse(content = @Content(mediaType = APPLICATION_JSON))
    @ApiResponse(responseCode = "204", description = "Transfer created")
    @ApiResponse(responseCode = "400", description = "Bad requests (for example negative amount)")
    @ApiResponse(responseCode = "404", description = "When debtor or debtor account was not found")
    @ApiResponse(responseCode = "412", description = "When debtor had insuffiecent founds")
    @ApiResponse(responseCode = "423", description = "Resource locked, try again later")
    @Post("/accounts/{debtorId}/transfers")
    public HttpResponse createTransfer(UUID debtorId,
                                       @Body @Valid CreateTransferDto createTransferDto) {
        TransferResult result = accountService.makeTransfer(new AccountId(debtorId),
                createTransferDto.getCreditorId(),
                createTransferDto.getAmount());

        switch (result.getStatus()) {
            case SUCCESSFUL:
                return HttpResponse.noContent();
            case INVALID_TRANSFER_AMOUNT:
                return HttpResponse.badRequest(ErrorDto.builder().error(INVALID_TRANSFER_AMOUNT.toString()).build());
            case DEBTOR_NOT_FOUND:
                return HttpResponse.notFound(ErrorDto.builder().error(DEBTOR_NOT_FOUND.toString()).build());
            case CREDITOR_NOT_FOUND:
                return HttpResponse.notFound(ErrorDto.builder().error(CREDITOR_NOT_FOUND.toString()).build());
            case INSUFFICIENT_FOUNDS:
                return HttpResponse.status(HttpStatus.PRECONDITION_FAILED)
                        .body(ErrorDto.builder().error(CREDITOR_NOT_FOUND.toString()).build());
            case LOCKED:
                return HttpResponse.status(HttpStatus.LOCKED)
                        .body(ErrorDto.builder().error(LOCKED.toString()).build());
            default:
                return HttpResponse.serverError();
        }
    }

    private URI location(UUID id) {
        return URI.create("/accounts/" + id.toString());
    }
}