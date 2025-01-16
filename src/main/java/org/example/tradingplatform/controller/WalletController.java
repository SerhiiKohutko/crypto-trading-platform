package org.example.tradingplatform.controller;

import org.example.tradingplatform.domen.WalletTransactionType;
import org.example.tradingplatform.modal.*;
import org.example.tradingplatform.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
public class WalletController {

    @Autowired
    private WalletService walletService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private WalletTransactionService walletTransactionService;

    @GetMapping("/api/wallet")
    public ResponseEntity<Wallet> getUserWallet(@RequestHeader("Authorization") String jwt) throws Exception {
        UserEntity user = userService.findUserByJwt(jwt);
        Wallet wallet = walletService.getUserWallet(user);

        return ResponseEntity.ok(wallet);
    }

    @PutMapping("/api/wallet/{walletId}/transfer")
    public ResponseEntity<Wallet> walletToWalletTransfer(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long walletId,
            @RequestBody WalletTransaction req) throws Exception{

        UserEntity senderUser = userService.findUserByJwt(jwt);
        Wallet receiverWallet = walletService.findWalletById(walletId);
        Wallet wallet = walletService.walletToWalletTransfer(senderUser,
                receiverWallet,
                req.getAmount());

        req.setWallet(wallet);
        req.setType(WalletTransactionType.WALLET_TRANSFER);
        walletTransactionService.saveWalletTransaction(req);

        WalletTransaction receiverTransaction = new WalletTransaction();
        receiverTransaction.setWallet(receiverWallet);
        receiverTransaction.setType(WalletTransactionType.WALLET_TRANSFER);
        receiverTransaction.setAmount(req.getAmount());
        walletTransactionService.saveWalletTransaction(receiverTransaction);

        //TODO
        // transfer id
        // transfer data base
        return ResponseEntity.ok(wallet);
    }

    @PutMapping("/api/wallet/{orderId}/pay")
    public ResponseEntity<Wallet> payOrderPayment(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long orderId) throws Exception{

        UserEntity senderUser = userService.findUserByJwt(jwt);

        Order order = orderService.getOrderById(orderId);

        Wallet wallet = walletService.payOrderPayment(order, senderUser);

        return ResponseEntity.ok(wallet);
    }

    @PutMapping("/api/wallet/deposit")
    public ResponseEntity<Wallet> addBalanceToWallet(
            @RequestHeader("Authorization") String jwt,
            @RequestParam(name="order_id") Long orderId,
            @RequestParam(name = "payment_id") String paymentId) throws Exception{

        UserEntity senderUser = userService.findUserByJwt(jwt);

        Wallet wallet = walletService.getUserWallet(senderUser);

        PaymentOrder paymentOrder = paymentService.getPaymentOrder(orderId);

        Boolean status = paymentService.proceedPaymentOrder(paymentOrder, paymentId);
        if(wallet.getBalance()==null){
            wallet.setBalance(BigDecimal.ZERO);
        }
        if (status) {
            wallet = walletService.addBalance(wallet, paymentOrder.getAmount());

            WalletTransaction transaction = new WalletTransaction();
            transaction.setAmount(paymentOrder.getAmount());
            transaction.setDate(LocalDate.now());
            transaction.setType(WalletTransactionType.ADD_MONEY);
            transaction.setWallet(wallet);

            walletTransactionService.saveWalletTransaction(transaction);
        }

        return ResponseEntity.ok(wallet);
    }


    @GetMapping("/api/wallet/transactions")
    public ResponseEntity<List<WalletTransaction>> getWalletTransactions(@RequestHeader("Authorization") String jwt) throws Exception {
        Wallet userWallet = walletService.getUserWallet(userService.findUserByJwt(jwt));

        List<WalletTransaction> transactions = walletTransactionService.getWalletTransactions(userWallet);

        return ResponseEntity.ok(transactions);
    }

}
