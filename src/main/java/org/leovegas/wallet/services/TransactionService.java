package org.leovegas.wallet.services;

import org.leovegas.wallet.exception.RestExceptionHandler;
import org.leovegas.wallet.models.TransactionType;
import org.leovegas.wallet.models.Transaction;
import org.leovegas.wallet.models.Wallet;
import org.leovegas.wallet.models.views.TransactionView;
import org.leovegas.wallet.repositories.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

/**
 * @author Alireza Gholamzadeh Lahroodi
 */

@Service
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    private final WalletService walletOperationService;
    private final TransactionRepository transactionRepository;

    public TransactionService(final WalletService walletOperationService,
                              final TransactionRepository transactionRepository) {
        this.walletOperationService = walletOperationService;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransactionView changePlayerBalance(
            final long playerId,
            final long transactionId,
            final BigInteger amount,
            final TransactionType operation
    ) {
        Wallet wallet = walletOperationService.getOrCreateWallet(playerId);

        try {

            Transaction transaction = transactionRepository.save(
                    Transaction.builder().id(transactionId).
                            wallet(wallet).amount(amount).operation(operation).build());

            switch (operation) {
                case CREDIT -> walletOperationService.creditBalance(playerId, amount);
                case DEBIT -> walletOperationService.debitBalance(playerId, amount);
            }
            return new TransactionView(
                    playerId,
                    transaction.getAmount(),
                    transaction.getOperation(),
                    transaction.getCreatedAt()
            );

        } catch (DuplicateKeyException e) {
            log.warn(e.getMessage(), e);
            throw new IllegalArgumentException("Transaction '" + transactionId + "' have already Settled");
        }

    }


}
