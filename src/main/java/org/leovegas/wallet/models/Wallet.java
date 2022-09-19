package org.leovegas.wallet.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 *  @author Alireza Gholamzadeh Lahroodi
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Wallet Id can't be NULL")
    private UUID walletId;

    @NotNull(message = "Player Id must be provided")
    private Long playerId;

    @Min(0)
    @NotNull(message = "DebitBalance should be more then Zero")
    private BigInteger debitBalance;

    @Min(0)
    @NotNull(message = "CreditBalance should be more then Zero")
    private BigInteger creditBalance;

    @ManyToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    @JoinColumn(name="currency_id", referencedColumnName="id")
    private Currency currency;

    private LocalDateTime lastUpdated;

    private String lastUpdatedBy;

    @OneToMany(mappedBy = "wallet", fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    @Transient
    public BigInteger balance() {
        return creditBalance.subtract(debitBalance);
    }

    @Version
    private Integer version;
}
