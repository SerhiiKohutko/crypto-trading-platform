package org.example.tradingplatform.modal;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import org.example.tradingplatform.domen.UserRole;

@Data
@Entity
@Table
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private String fullName;
    @Column
    private String email;

    @OneToOne
    private Watchlist watchlist;


    // при сериализации в json это поле не будет включено,
    // т.е его можно только "записать" в бд, но не прочитать
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    //эта аннотация грубо говоря разбивает компонент
    // на поля и делает из них атрибуты обьекта в базе
    @Embedded
    private TwoFactorAuth twoFactorAuth = new TwoFactorAuth();

    private UserRole role = UserRole.CUSTOMER;
}
