package com.four.withtopia.db.domain;


import com.four.withtopia.dto.request.SubCommentRequestDto;
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
public class SubComment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "commentId",nullable = false)
    private Comment comment;

    @Column
    private String nickname;

    @Column
    private String profileImage;

    @Column
    private String content;

    public void update(SubCommentRequestDto subCommentRequestDto){
        this.content = subCommentRequestDto.getContent();
    }



}
