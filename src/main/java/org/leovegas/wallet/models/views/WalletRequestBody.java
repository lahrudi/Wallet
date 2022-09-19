package org.leovegas.wallet.models.views;

import com.google.common.base.Preconditions;

import java.math.BigInteger;

/**
 * @author Alireza Gholamzadeh Lahroodi
 */

public record WalletRequestBody(
        Long transactionId,
        BigInteger amount
) {
    public WalletRequestBody {
        Preconditions.checkNotNull(transactionId, "Transaction Id can't be Null");
        Preconditions.checkNotNull(amount, "Amount can't be NULL");
        Preconditions.checkArgument(amount.signum() != -1, "Amount should be more then Zero");
    }
}
