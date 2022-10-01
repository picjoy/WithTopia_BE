package com.four.withtopia.db.domain;

import com.four.withtopia.util.Timestamped;
import lombok.*;
import org.springframework.stereotype.Component;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vote extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voteId;

    // 투표를 한 사람
    @Column
    private String voteBy;
    @Column
    private Long voteById;

    // 투표를 받을 사람
    @Column
    private String voteTo;
    @Column
    private Long voteToId;
}
