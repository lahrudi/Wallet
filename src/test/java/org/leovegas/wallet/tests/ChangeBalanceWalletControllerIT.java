package org.leovegas.wallet.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.leovegas.wallet.models.TransactionType;
import org.leovegas.wallet.models.Wallet;
import org.leovegas.wallet.models.views.TransactionView;
import org.leovegas.wallet.models.views.WalletRequestBody;
import org.leovegas.wallet.models.views.WalletView;
import org.leovegas.wallet.repositories.TransactionRepository;
import org.leovegas.wallet.repositories.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.leovegas.wallet.TestUtils.generatePlayerId;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class ChangeBalanceWalletControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Test
    @DisplayName("should decrease balance for given player id")
    void decreaseBalanceForGivenPlayerId() {

        //given
        final Long playerId = generatePlayerId();
        final Long transactionId = generatePlayerId();
        final BigInteger debitBalance = new BigInteger("5");
        final BigInteger creditBalance = new BigInteger("50");
        final BigInteger balance = creditBalance.subtract(debitBalance);
        Wallet wallet = Wallet.builder().walletId(UUID.randomUUID())
                .playerId(playerId)
                .debitBalance(BigInteger.ZERO)
                .creditBalance(creditBalance)
                .version(0)
                .build();

        TransactionView expectedTransactionView = new TransactionView(
                playerId,
                debitBalance,
                TransactionType.DEBIT,
                LocalDateTime.now()
        );

        //then

        walletRepository.save(wallet);

        //when
        TransactionView actualTransactionView = webTestClient.post()
                .uri("/players/{playerId}/wallet/debit", playerId)
                .bodyValue(new WalletRequestBody(transactionId, debitBalance))
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionView.class)
                .returnResult()
                .getResponseBody();

        assertEquals(expectedTransactionView.playerId(), actualTransactionView.playerId());
        assertEquals(expectedTransactionView.amount(), actualTransactionView.amount());
        assertEquals(expectedTransactionView.operation(), actualTransactionView.operation());

        webTestClient.get()
                .uri("/players/{playerId}/wallet", playerId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(WalletView.class)
                .isEqualTo(new WalletView(
                        playerId,
                        balance
                ));

    }

    @Test
    @DisplayName("should throw if balance can be below Zero for given player id")
    void throwIfBalanceIsNegativeForGivenPlayerId() {
        //given

        final Long playerId = generatePlayerId();
        final Long transactionId = generatePlayerId();
        final BigInteger amount = new BigInteger("5");
        final Wallet wallet = Wallet.builder()
                .walletId(UUID.randomUUID())
                .debitBalance(BigInteger.ZERO)
                .creditBalance(BigInteger.ZERO)
                .playerId(playerId)
                .build();

        //then
        walletRepository.save(wallet);

        //when
        webTestClient.post()
                .uri("/players/{playerId}/wallet/debit", playerId)
                .contentType(APPLICATION_JSON)
                .bodyValue(new WalletRequestBody(transactionId, amount))
                .exchange()
                .expectStatus().isBadRequest();

        webTestClient.get()
                .uri("/players/{playerId}/wallet", playerId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(WalletView.class)
                .isEqualTo(new WalletView(playerId, BigInteger.ZERO));
    }
}
