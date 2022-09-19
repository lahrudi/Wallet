package org.leovegas.wallet.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 *  @author Alireza Gholamzadeh Lahroodi
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Transaction {
    @Id
    @Column(name = "id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Operation must be provided")
    private TransactionType operation;

    @NotNull(message = "Transaction amount must be provided")
    private BigInteger amount;

    @NotNull(message = "Transaction wallet must be provided")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    @NotNull(message = "Transaction currency must be provided")
    @ManyToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    @JoinColumn(name="currency_id", referencedColumnName="id")
    private Currency currency;

    String description;

    private LocalDateTime lastUpdated;

    private String lastUpdatedBy;

    private LocalDateTime createdAt;

}
