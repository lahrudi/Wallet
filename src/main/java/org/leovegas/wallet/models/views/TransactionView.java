package org.leovegas.wallet.models.views;

import org.leovegas.wallet.models.TransactionType;

import java.math.BigInteger;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * @author Alireza Gholamzadeh Lahroodi
 */

public record TransactionView(
        Long playerId,
        BigInteger amount,
        TransactionType operation,
        LocalDateTime createdAt
) { }
