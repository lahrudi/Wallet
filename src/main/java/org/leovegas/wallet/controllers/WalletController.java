package org.leovegas.wallet.controllers;

import com.google.common.base.Preconditions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.leovegas.wallet.models.TransactionType;
import org.leovegas.wallet.models.Wallet;
import org.leovegas.wallet.models.views.TransactionView;
import org.leovegas.wallet.models.views.WalletRequestBody;
import org.leovegas.wallet.models.views.WalletView;
import org.leovegas.wallet.services.TransactionHistoryService;
import org.leovegas.wallet.services.TransactionService;
import org.leovegas.wallet.services.WalletService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.print.Book;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Alireza Gholamzadeh Lahroodi
 */

@RestController
@RequestMapping("/players")
public class WalletController {

    private final WalletService walletOperationService;
    private final TransactionService transactionService;
    private final TransactionHistoryService historyService;

    public WalletController(WalletService walletOperationService,
                            TransactionService transactionService,
                            TransactionHistoryService historyService) {
        this.walletOperationService = walletOperationService;
        this.transactionService = transactionService;
        this.historyService = historyService;
    }

    @Operation(summary = "Get a wallet by player id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the wallet",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WalletView.class)) }),
            @ApiResponse(responseCode = "400", description = "Player id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Player not found",
                    content = @Content) })
    @GetMapping(value = "{playerId}/wallet", produces = APPLICATION_JSON_VALUE)
    public WalletView getWallet(@PathVariable final Long playerId) {
        Wallet wallet = walletOperationService.getOrCreateWallet(playerId);
        return new WalletView( wallet.getPlayerId(), wallet.getCreditBalance().subtract(wallet.getDebitBalance()) );
    }

    @Operation(summary = "Get a wallet histories by player id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the wallet",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WalletView.class)) }),
            @ApiResponse(responseCode = "400", description = "Player id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Player not found",
                    content = @Content) })
    @GetMapping(value = "{playerId}/wallet/history", produces = APPLICATION_JSON_VALUE)
    public List<TransactionView> getHistory(
            @PathVariable final Long playerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate to
    ) {
        if (to != null) {
            Preconditions.checkArgument(from != null, "'to' date can't be used without 'from' date");
            Preconditions.checkArgument(!from.isAfter(to), "'from' date should be before 'to' date");
        }

        return historyService.getTransactionHistory(playerId, from, to);

    }

    @Operation(summary = "Post request for deposit from the wallet by player id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "deposit is done successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WalletView.class)) }),
            @ApiResponse(responseCode = "400", description = "Player id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Player not found",
                    content = @Content) })
    @PostMapping(
            value = "{playerId}/wallet/credit",
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE
    )
    public TransactionView credit(
            @PathVariable final Long playerId,
            @RequestBody final WalletRequestBody operationRequest
    ) {
        return transactionService.changePlayerBalance(
                playerId,
                operationRequest.transactionId(),
                operationRequest.amount(),
                TransactionType.CREDIT
        );
    }

    @Operation(summary = "Post request for withdrawal from the wallet by player id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Withdrawal is done successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WalletView.class)) }),
            @ApiResponse(responseCode = "400", description = "Player id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Player not found",
                    content = @Content) })
    @PostMapping(
            value = "{playerId}/wallet/debit",
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE
    )
    public TransactionView debit(
            @PathVariable final Long playerId,
            @RequestBody final WalletRequestBody operationRequest
    ) {
        return transactionService.changePlayerBalance(
                playerId,
                operationRequest.transactionId(),
                operationRequest.amount(),
                TransactionType.DEBIT
        );

    }
}
