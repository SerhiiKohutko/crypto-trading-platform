package org.example.tradingplatform.request;


import lombok.Data;
import org.example.tradingplatform.domen.VerificationType;

@Data
public class ForgotPasswordTokenRequest {
    private String sendTo;
    private VerificationType verificationType;
}
