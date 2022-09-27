package com.four.withtopia.db.domain;

import com.four.withtopia.util.Timestamped;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Report extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    // 신고 당한 사람
    @Column
    private String reportTo;
    @Column
    private Long reportToId;

    // 신고 한 사람
    @Column
    private String reportBy;
    @Column
    private Long reportById;

    // 신고 내용
    @Column
    private String content;

    @Column
    private String reportImg;

}
