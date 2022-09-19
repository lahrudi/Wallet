package org.leovegas.wallet.services;

import org.jetbrains.annotations.Nullable;
import org.leovegas.wallet.models.views.TransactionView;
import org.leovegas.wallet.repositories.TransactionRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Alireza Gholamzadeh Lahroodi
 */

@Service
public class TransactionHistoryService {

    private final TransactionRepository transactionRepository;

    public TransactionHistoryService(final TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<TransactionView> getTransactionHistory(
            final Long playerId,
            @Nullable final LocalDate from,
            @Nullable final LocalDate to) {
        return transactionRepository.findAll(playerId, from, to).stream().map(transaction ->
                new TransactionView(
                        transaction.getWallet().getPlayerId(),
                        transaction.getAmount(),
                        transaction.getOperation(),
                        transaction.getCreatedAt()
                )
        ).collect(Collectors.toList());
    }

}
