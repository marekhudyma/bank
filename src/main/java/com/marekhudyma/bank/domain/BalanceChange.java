package com.marekhudyma.bank.domain;

import com.marekhudyma.bank.domain.id.AccountId;
import com.marekhudyma.bank.domain.id.BalanceChangeId;
import java.math.BigDecimal;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "balance_changes")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Data
@Builder
public class BalanceChange {

    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "id"))
    private BalanceChangeId id;

    @Convert(converter = AccountId.DbConverter.class)
    private AccountId accountId;

    private BigDecimal amount;

}
