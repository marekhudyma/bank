package com.marekhudyma.bank.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.marekhudyma.bank.domain.id.AccountId;
import com.marekhudyma.bank.dto.json.AccountIdDeserializer;
import com.marekhudyma.bank.dto.json.AccountIdSerializer;
import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransferDto {

    @NotNull
    @JsonDeserialize(using = AccountIdDeserializer.class)
    @JsonSerialize(using = AccountIdSerializer.class)
    private AccountId creditorId;

    @NotNull
    private BigDecimal amount;

}
