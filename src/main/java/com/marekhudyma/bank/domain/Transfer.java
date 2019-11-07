package com.marekhudyma.bank.domain;

import com.marekhudyma.bank.domain.id.BalanceChangeId;
import com.marekhudyma.bank.domain.id.TransferId;
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
@Table(name = "transfers")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Data
@Builder
public class Transfer {

    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "id"))
    private TransferId id;

    @Convert(converter = BalanceChangeId.DbConverter.class)
    private BalanceChangeId debtorChangeId;

    @Convert(converter = BalanceChangeId.DbConverter.class)
    private BalanceChangeId creditorChangeId;

}