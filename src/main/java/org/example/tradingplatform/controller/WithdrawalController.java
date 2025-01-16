package org.example.tradingplatform.controller;

import org.example.tradingplatform.domen.WalletTransactionType;
import org.example.tradingplatform.modal.UserEntity;
import org.example.tradingplatform.modal.Wallet;
import org.example.tradingplatform.modal.WalletTransaction;
import org.example.tradingplatform.modal.Withdrawal;
import org.example.tradingplatform.service.UserService;
import org.example.tradingplatform.service.WalletService;
import org.example.tradingplatform.service.WalletTransactionService;
import org.example.tradingplatform.service.WithdrawalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class WithdrawalController {
    @Autowired
    private WithdrawalService withdrawalService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserService userService;
    @Autowired
    private WalletTransactionService walletTransactionService;


    @PostMapping("/api/withdrawal/{amount}")
    public ResponseEntity<?> withdrawalRequest(@PathVariable Long amount,
                                               @RequestHeader("Authorization") String jwt) throws Exception {

        UserEntity user = userService.findUserByJwt(jwt);
        Wallet userWallet = walletService.getUserWallet(user);

        Withdrawal withdrawal = withdrawalService.requestWithdrawal(amount, user);
        walletService.addBalance(userWallet, -withdrawal.getAmount());

        WalletTransaction receiverTransaction = new WalletTransaction();
        receiverTransaction.setWallet(userWallet);
        receiverTransaction.setType(WalletTransactionType.WITHDRAWAL);
        receiverTransaction.setAmount(amount);
        walletTransactionService.saveWalletTransaction(receiverTransaction);

        return new ResponseEntity<>(withdrawal, HttpStatus.OK);
    }

    @PatchMapping("/api/admin/withdrawal/{id}/proceed/{accept}")
    public ResponseEntity<?> proceedWithdrawal(
            @PathVariable Long id,
            @PathVariable boolean accept,
            @RequestHeader("Authorization") String jwt) throws Exception {

        UserEntity user = userService.findUserByJwt(jwt);

        Withdrawal withdrawal = withdrawalService.proceedWithwithdrawal(id, accept);
        Wallet userWallet = walletService.getUserWallet(user);

        if (!accept) {
            walletService.addBalance(userWallet, withdrawal.getAmount());

        }
        return new ResponseEntity<>(withdrawal, HttpStatus.OK);
    }

    @GetMapping("/api/withdrawal")
    public ResponseEntity<?> getWithdrawalHistory(@RequestHeader("Authorization") String jwt) throws Exception {
        UserEntity user = userService.findUserByJwt(jwt);
        List<Withdrawal> list = withdrawalService.getUsersWithdrawalHistory(user);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
    @GetMapping("/api/admin/withdrawal")
    public ResponseEntity<?> getAllWithdrawalRequest(@RequestHeader("Authorization") String jwt) throws Exception {
        UserEntity user = userService.findUserByJwt(jwt);
        List<Withdrawal> list = withdrawalService.getAllWithdrawalRequest();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
