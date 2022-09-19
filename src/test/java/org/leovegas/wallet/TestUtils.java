package org.leovegas.wallet;

import java.util.Random;

public class TestUtils {
    public static Long generatePlayerId() {
        return new Random().nextLong(0, Long.MAX_VALUE);
    }

    public static Long generateTransactionId() {
        return new Random().nextLong(0, Long.MAX_VALUE);
    }
}
