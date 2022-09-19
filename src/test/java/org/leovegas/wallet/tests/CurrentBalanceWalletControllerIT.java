package org.leovegas.wallet.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.leovegas.wallet.models.Wallet;
import org.leovegas.wallet.models.views.WalletView;
import org.leovegas.wallet.repositories.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigInteger;
import java.util.UUID;

import static org.leovegas.wallet.TestUtils.generatePlayerId;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class CurrentBalanceWalletControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private WalletRepository walletRepository;

    @Test
    @DisplayName("should return balance for given player id")
    void returnBalanceForGivenPlayerId() {
        //given
        final Long playerId = generatePlayerId();
        final WalletView expectedWalletView = new WalletView(playerId, new BigInteger("50"));

        walletRepository.save(Wallet.builder().playerId(playerId).walletId(UUID.randomUUID()).debitBalance(BigInteger.ZERO).creditBalance(new BigInteger("50")).build());
        //then
        //when
        webTestClient.get()
                .uri("/players/{playerId}/wallet", playerId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(WalletView.class)
                .isEqualTo(expectedWalletView);
    }

    @Test
    @DisplayName("should return balance for existed given player id")
    void returnBalanceForExistedGivenPlayerId() {
        //given
        final Long playerId = generatePlayerId();
        Wallet wallet = walletRepository.save(Wallet.builder().walletId(UUID.randomUUID())
                .playerId(playerId)
                .creditBalance(new BigInteger("19"))
                .debitBalance(new BigInteger("45"))
                .version(0)
                .build()
        );

        WalletView expectedWalletView = new WalletView(
                playerId,
                wallet.balance()
        );

        //when
        //then
        webTestClient.get()
                .uri("/players/{playerId}/wallet", playerId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(WalletView.class)
                .isEqualTo(expectedWalletView);
    }

}
