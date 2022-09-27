package com.four.withtopia.dto.response;

import com.four.withtopia.db.domain.Friend;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FriendResponseDto {

    private Long id;
    private String myNickname;
    private String friendNickname;
    private String friendEmail;
    private String friendProfileImage;
    private long friendLikeCount;


    public FriendResponseDto(Friend friend){
        this.id = friend.getId();
        this.myNickname = friend.getMyNickname();
        this.friendNickname = friend.getFriendNickname();
        this.friendEmail = friend.getFriendEmail();
        this.friendProfileImage = friend.getFriendProfileImage();
        this.friendLikeCount = friend.getFriendLikeCount();
    }




}
