package org.leovegas.wallet.tests;

import org.junit.jupiter.api.Test;
import org.leovegas.wallet.models.Wallet;
import org.leovegas.wallet.models.views.WalletRequestBody;
import org.leovegas.wallet.models.views.WalletView;
import org.leovegas.wallet.repositories.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigInteger;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.leovegas.wallet.TestUtils.generatePlayerId;
import static org.leovegas.wallet.TestUtils.generateTransactionId;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class ConcurrencyChangeBalanceIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private WalletRepository walletRepository;

    @Test
    void callConcurrentDebitAndCreditRestService() throws InterruptedException {
        //given
        Long playerId = generatePlayerId();
        Wallet wallet = walletRepository.save(Wallet.builder().walletId(UUID.randomUUID()).playerId(playerId)
                .debitBalance(BigInteger.ZERO)
                .creditBalance(new BigInteger("100"))
                .version(0).build()
        );

        WalletView walletView = new WalletView(
                playerId,
                new BigInteger("100")
        );

        //when
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 100; i++) {
            executorService.submit(() -> {
                webTestClient.post()
                        .uri("/players/{playerId}/wallet/debit", playerId)
                        .bodyValue(new WalletRequestBody(generateTransactionId(), BigInteger.ONE))
                        .exchange();
                webTestClient.post()
                        .uri("/players/{playerId}/wallet/credit", playerId)
                        .bodyValue(new WalletRequestBody(generateTransactionId(), BigInteger.ONE))
                        .exchange();

            });
        }

        executorService.shutdown();
        if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
        }

        //then
        webTestClient.get()
                .uri("/players/{playerId}/wallet", playerId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(WalletView.class)
                .isEqualTo(walletView);

    }
}
