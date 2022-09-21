package com.four.withtopia.db.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity(name = "Top")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rankId;

    @Column
    private String nickname;

    @Column
    private Long likeCnt;

}
