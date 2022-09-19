package org.leovegas.wallet.services;

import org.leovegas.wallet.models.Wallet;
import org.leovegas.wallet.repositories.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.UUID;

/**
 * @author Alireza Gholamzadeh Lahroodi
 */

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    @Autowired
    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet getOrCreateWallet(final long playerId) {
        Wallet wallet = walletRepository.findByPlayerId(playerId).get();

        if (wallet == null) {
            wallet = walletRepository.save(
                    Wallet.builder()
                            .walletId(UUID.randomUUID())
                            .playerId(playerId).build());
        }

        return wallet;
    }

    public void creditBalance(final Long playerId, final BigInteger amount) {
        Wallet wallet = getOrCreateWallet(playerId);
        wallet.setCreditBalance(wallet.getCreditBalance().add(amount));
        save(wallet);
    }

    public void debitBalance(final Long playerId, final BigInteger amount) {
        Wallet wallet = getOrCreateWallet(playerId);

        if (wallet.getCreditBalance().subtract(amount).signum() == -1)
            throw new IllegalArgumentException("balance can't be less then Zero");
        else {
            wallet.setDebitBalance(wallet.getDebitBalance().add(amount));
            save(wallet);
        }

    }

    public void save(Wallet wallet) {
        walletRepository.save(wallet);
    }
}
