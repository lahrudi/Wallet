package org.leovegas.wallet.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.leovegas.wallet.models.TransactionType;
import org.leovegas.wallet.models.Transaction;
import org.leovegas.wallet.models.Wallet;
import org.leovegas.wallet.models.views.TransactionView;
import org.leovegas.wallet.repositories.TransactionRepository;
import org.leovegas.wallet.repositories.WalletRepository;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.leovegas.wallet.TestUtils.generatePlayerId;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class HistoryWalletControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    @DisplayName("should return balance for given player id")
    void returnBalanceExistedForGivenPlayerId() {

        //given
        final Long playerId = generatePlayerId();
        Wallet wallet = walletRepository.save(Wallet.builder().walletId(UUID.randomUUID()).playerId(playerId).build());

        List<Transaction> transactions = List.of(
                Transaction.builder().wallet(wallet)
                        .amount(BigInteger.ONE)
                        .operation(TransactionType.CREDIT)
                        .build()
                ,
                Transaction.builder().wallet(wallet)
                        .amount(BigInteger.ONE)
                        .operation(TransactionType.DEBIT)
                        .build()
                ,
                Transaction.builder().wallet(wallet)
                        .amount(BigInteger.ONE)
                        .operation(TransactionType.CREDIT)
                        .build()
                ,
                Transaction.builder().wallet(wallet)
                        .amount(BigInteger.ONE)
                        .createdAt(LocalDateTime.now())
                        .operation(TransactionType.DEBIT)
                        .build()
        );

        transactions.forEach(transaction -> transactionRepository.saveAndFlush(transaction));

        //when
        List<TransactionView> actualList = webTestClient.get()
                .uri("/players/{playerId}/wallet/history", playerId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<TransactionView>>() {
                })
                .returnResult()
                .getResponseBody();

        //then
        List<TransactionView> expectedList = transactions.stream().map(transaction ->
                new TransactionView(
                        transaction.getWallet().getPlayerId(),
                        transaction.getAmount(),
                        transaction.getOperation(),
                        transaction.getCreatedAt()
                )
        ).toList();

        Long skip = 0l;
        assertEquals(expectedList.size(), actualList.size());
        for (TransactionView expected : expectedList) {
            TransactionView actual = findTransactionById(actualList, expected.playerId(), skip).orElseThrow(() -> new AssertionFailedError(null, expected, null));
            skip++;
            assertEquals(expected.playerId(), actual.playerId());
            assertEquals(expected.amount(), actual.amount());
            assertEquals(expected.operation(), actual.operation());
        }
    }

    private Optional<TransactionView> findTransactionById(List<TransactionView> actualList, Long id, Long skip) {
        return actualList.stream().skip(skip).filter(transaction -> transaction.playerId().equals(id)).findFirst();
    }

}
