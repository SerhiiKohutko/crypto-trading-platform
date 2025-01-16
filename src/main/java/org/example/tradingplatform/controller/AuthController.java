package org.example.tradingplatform.controller;


import org.example.tradingplatform.config.JwtProvider;
import org.example.tradingplatform.modal.TwoFactorOTP;
import org.example.tradingplatform.modal.UserEntity;
import org.example.tradingplatform.repository.UserRepository;
import org.example.tradingplatform.response.AuthResponse;
import org.example.tradingplatform.service.CustomUserDetailsService;
import org.example.tradingplatform.service.EmailService;
import org.example.tradingplatform.service.TwoFactorOTPService;
import org.example.tradingplatform.service.WatchlistService;
import org.example.tradingplatform.utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private TwoFactorOTPService twoFactorOTPService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private WatchlistService watchlistService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> register(@RequestBody UserEntity user) throws Exception {

        UserEntity doesEmailExist = userRepository.findByEmail(user.getEmail());

        if (doesEmailExist != null) {
            throw new Exception("Email already exists");
        }

        UserEntity newUser = new UserEntity();
        newUser.setFullName(user.getFullName());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(user.getPassword());

        UserEntity savedUser = userRepository.save(newUser);

        watchlistService.createWatchList(savedUser);

        Authentication auth = new UsernamePasswordAuthenticationToken(savedUser.getEmail(), savedUser.getPassword());

        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = JwtProvider.generateToken(auth);
        AuthResponse authResponse = new AuthResponse(jwt, true, "register_success");

        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> login(@RequestBody UserEntity user) throws Exception {

        Authentication auth =
                 authenticate(user.getEmail(), user.getPassword());

        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = JwtProvider.generateToken(auth);

        UserEntity authUser = userRepository.findByEmail(user.getEmail());

        if (user.getTwoFactorAuth().isEnabled()){

            String otp = OtpUtils.generateOtp();

            TwoFactorOTP oldOTP = twoFactorOTPService.findByUser(authUser.getId());
            if (oldOTP != null) {
                twoFactorOTPService.deleteTwoFactorOTP(oldOTP);
            }
            TwoFactorOTP newOTP = twoFactorOTPService.createTwoFactorOTP(authUser, otp, jwt);

            AuthResponse authResponse =
                    new AuthResponse(jwt, true, "two_factor_enabled", true, newOTP.getId());

            emailService.sendVerificationOtpEmail(authUser.getEmail(), otp);

            return new ResponseEntity<>(authResponse, HttpStatus.ACCEPTED);
        }

        AuthResponse authResponse = new AuthResponse(jwt, true, "register_success");

        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    private Authentication authenticate(String email, String password) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        if (userDetails == null) {
            throw new UsernameNotFoundException(email);
        }

        if (!password.equals(userDetails.getPassword())){
            throw new BadCredentialsException("Wrong password");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }


    @PostMapping("/two-factor/otp/{otp}")
    public ResponseEntity<AuthResponse> verify(@PathVariable String otp,
                                               @RequestParam String id){

        TwoFactorOTP twoFactorOTP = twoFactorOTPService.findById(id);
        if (twoFactorOTPService.verifyTwoFactor(twoFactorOTP, otp)) {
            AuthResponse response = new AuthResponse(twoFactorOTP.getJwt(), true, "verify_success",true);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        throw new BadCredentialsException("Wrong otp");
    }

}

