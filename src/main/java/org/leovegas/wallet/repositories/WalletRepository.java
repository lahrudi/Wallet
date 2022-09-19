package org.leovegas.wallet.repositories;

import org.leovegas.wallet.exception.WalletException;
import org.leovegas.wallet.models.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * @author Alireza Gholamzadeh Lahroodi
 */

@Transactional(rollbackOn = WalletException.class)
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    List<Wallet> findAllByOrderByIdAsc();

    @Override
    @Lock(value = LockModeType.PESSIMISTIC_READ)
    Optional<Wallet> findById(Long id);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    Optional<Wallet> findByPlayerId(Long playerId);

}