package com.four.withtopia.db.domain;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@Setter
@Entity
public class EmailAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long authId;

    @Column
    private String email;

    @Column
    private String Auth;

    public EmailAuth(String email, String authKey) {
        this.email = email;
        this.Auth = authKey;
    }
}
