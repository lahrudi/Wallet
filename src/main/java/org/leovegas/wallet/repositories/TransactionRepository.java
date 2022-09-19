package org.leovegas.wallet.repositories;

import org.leovegas.wallet.models.Transaction;
import org.leovegas.wallet.models.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

/**
 * @author Alireza Gholamzadeh Lahroodi
 */

@Transactional(rollbackOn = Exception.class)
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByWallet(Wallet wallet);
    List<Transaction> findAllByAmountIsNotNull();

    @Query("SELECT t FROM Transaction t WHERE t.wallet.playerId = :playerId and " +
            "( :from is Null or t.createdAt >= :from ) and " +
            "( :to is Null or t.createdAt <= :to )")
    List<Transaction> findAll(@Param("playerId") long playerId,
                              @Param("from") LocalDate from,
                              @Param("to") LocalDate to);
}
