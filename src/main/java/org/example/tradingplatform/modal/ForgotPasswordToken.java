package org.example.tradingplatform.modal;

import jakarta.persistence.*;
import lombok.Data;
import org.example.tradingplatform.domen.VerificationType;

@Entity
@Data
public class ForgotPasswordToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;


    @OneToOne
    private UserEntity user;
    private String otp;

    private VerificationType verificationType;

    private String sendTo;


}
