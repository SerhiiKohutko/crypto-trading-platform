package org.example.tradingplatform.modal;

import jakarta.persistence.*;
import lombok.Data;
import org.example.tradingplatform.domen.VerificationType;

@Entity
@Data
public class VerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String otp;

    @OneToOne
    private UserEntity user;

    private String email;

    private String mobile;

    private VerificationType verificationType;
}
