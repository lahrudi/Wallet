package org.leovegas.wallet.repositories;

import org.leovegas.wallet.exception.WalletException;
import org.leovegas.wallet.models.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

/**
 * @author Alireza Gholamzadeh Lahroodi
 */

@Transactional(rollbackOn = WalletException.class)
public interface CurrencyRepository  extends JpaRepository<Currency, Long> {
    Currency findByName(String name);
}