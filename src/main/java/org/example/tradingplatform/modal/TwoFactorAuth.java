package org.example.tradingplatform.modal;

import lombok.Data;
import org.example.tradingplatform.domen.VerificationType;

@Data
public class TwoFactorAuth {
    private boolean isEnabled = false;
    private VerificationType sendTo;
}
