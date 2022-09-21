package com.four.withtopia.db.domain;

import com.four.withtopia.util.Timestamped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Friend extends Timestamped {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String myNickname;

    // 친구 닉네임
    @Column
    private String friendNickname;

    private String friendEmail;

    private String friendProfileImage;

    private long friendLikeCount;

}
