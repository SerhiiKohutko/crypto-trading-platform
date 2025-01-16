package org.example.tradingplatform.controller;

import org.example.tradingplatform.domen.VerificationType;
import org.example.tradingplatform.modal.ForgotPasswordToken;
import org.example.tradingplatform.modal.UserEntity;
import org.example.tradingplatform.modal.VerificationCode;
import org.example.tradingplatform.request.ResetPasswordRequest;
import org.example.tradingplatform.response.ApiResponse;
import org.example.tradingplatform.response.AuthResponse;
import org.example.tradingplatform.service.EmailService;
import org.example.tradingplatform.service.ForgotPasswordService;
import org.example.tradingplatform.service.UserService;
import org.example.tradingplatform.service.VerificationCodeService;
import org.example.tradingplatform.utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private VerificationCodeService verificationCodeService;
    @Autowired
    private ForgotPasswordService forgotPasswordService;


    @GetMapping("/users/profile")
    public ResponseEntity<UserEntity> getUserProfile(@RequestHeader("Authorization") String jwt) throws Exception {
        UserEntity user = userService.findUserByJwt(jwt);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PatchMapping("/users/enable-two-factor/verify-otp/{otp}")
    public ResponseEntity<UserEntity> enableTwoFactorAuthentication
            (@RequestHeader("Authorization") String jwt, @PathVariable String otp) throws Exception
    {

        UserEntity user = userService.findUserByJwt(jwt);

        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());

        String sendTo = verificationCode.getVerificationType().equals(VerificationType.EMAIL)?
                verificationCode.getEmail():verificationCode.getMobile();

        boolean isVerified = verificationCode.getOtp().equals(otp);

        if (isVerified) {
            UserEntity updatedUser = userService.enableTwoFactorAuthentication(verificationCode.getVerificationType(), sendTo, user);

            verificationCodeService.deleteVerificationCodeById(verificationCode);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        }


        throw new Exception("Wrong otp");
    }
    @PostMapping("/users/verification/{verificationType}/send-otp")
    public ResponseEntity<String> sendVerificationOtp
            (@RequestHeader("Authorization") String jwt, @PathVariable VerificationType verificationType) throws Exception
    {

        UserEntity user = userService.findUserByJwt(jwt);
        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());

        if (verificationCode == null) {
            verificationCode = verificationCodeService.sendVerificationCode(user, verificationType);
        }

        if(verificationType.equals(VerificationType.EMAIL)){
            emailService.sendVerificationOtpEmail(user.getEmail(), verificationCode.getOtp());
        }


        return new ResponseEntity<>("verification otp sent", HttpStatus.OK);
    }

    @PostMapping("/users/verification/reset-password/send-otp")
    public ResponseEntity<AuthResponse> sendForgotPasswordOtp
            (@RequestBody ForgotPasswordToken req) throws Exception
    {

        UserEntity user  = userService.findUserByEmail(req.getSendTo());
        String otp = OtpUtils.generateOtp();

        String id = UUID.randomUUID().toString();

        ForgotPasswordToken token = forgotPasswordService.findByUser(user.getId());

        if (token == null) {
            token = forgotPasswordService.createToken(user, id , otp, req.getVerificationType(), req.getSendTo());

        }

        if (req.getVerificationType().equals(VerificationType.EMAIL)) {
            emailService.sendVerificationOtpEmail(user.getEmail(), otp);
        }

        AuthResponse response = new AuthResponse(token.getId(), "Password reset otp send");



        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PatchMapping("/auth/users/reset-password/verify-otp")
    public ResponseEntity<ApiResponse> resetPassword
            (@RequestHeader("Authorization") String jwt,
             @RequestBody ResetPasswordRequest req,
             @RequestParam String id) throws Exception
    {

        UserEntity user = userService.findUserByJwt(jwt);

        ForgotPasswordToken token = forgotPasswordService.findById(id);


        boolean isVerified = token.getOtp().equals(req.getOtp());

        if (isVerified) {
            userService.updatePassword(token.getUser(), req.getPassword());
            ApiResponse response = new ApiResponse();
            response.setMessage("Password reset otp verified");

            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        }

        throw new Exception("Wrong otp");
    }

}
