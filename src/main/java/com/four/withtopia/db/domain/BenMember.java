package com.four.withtopia.db.domain;

import com.four.withtopia.util.Timestamped;
import lombok.*;

import javax.persistence.*;
import java.util.concurrent.TimeoutException;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BenMember extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long benMemberId;

    @Column
    private String roomId;

    @Column
    private Long memberId;
}
