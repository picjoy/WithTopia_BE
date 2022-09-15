package com.four.withtopia.db.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voteId;

    // 투표를 한 사람
    @Column
    private String voteBy;

    // 투표를 받을 사람
    @Column
    private String voteTo;
}
