package com.four.withtopia.db.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
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

    public void updateLikeCnt(Long likeCnt){
        this.likeCnt = likeCnt;
    }
}
