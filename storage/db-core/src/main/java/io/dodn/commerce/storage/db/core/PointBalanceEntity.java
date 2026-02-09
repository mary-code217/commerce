package io.dodn.commerce.storage.db.core;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "point_balance")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointBalanceEntity extends BaseEntity {

    private Long userId;
    private BigDecimal balance;

    @Version
    private Long version = 0L;

    public PointBalanceEntity(Long userId, BigDecimal balance) {
        this.userId = userId;
        this.balance = balance;
    }

    public void apply(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }
}
