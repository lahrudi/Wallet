package org.leovegas.wallet.models.views;

import java.math.BigInteger;

/**
 * @author Alireza Gholamzadeh Lahroodi
 */

public record WalletView(
        Long playerId,
        BigInteger balance
) {
}
