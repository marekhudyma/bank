package com.marekhudyma.bank.domain;

import com.marekhudyma.bank.domain.id.AccountId;
import java.math.BigDecimal;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "accounts")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Data
@Builder
public class Account {

    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "id"))
    private AccountId id;

    private String firstName;

    private String lastName;

    private BigDecimal bankBalance;
}
