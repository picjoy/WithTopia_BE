package com.four.withtopia.api.service;

import com.four.withtopia.config.error.ErrorCode;
import com.four.withtopia.config.expection.PrivateException;
import com.four.withtopia.db.domain.Friend;
import com.four.withtopia.db.domain.Member;
import com.four.withtopia.db.repository.FriendRepository;
import com.four.withtopia.db.repository.MemberRepository;
import com.four.withtopia.dto.response.FriendResponseDto;
import com.four.withtopia.util.MemberCheckUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final MemberCheckUtils memberCheckUtils;

    private final MemberRepository memberRepository;

    private final FriendRepository friendRepository;

    // 친구 추가 메소드
    public FriendResponseDto makeFriend(String friendName, HttpServletRequest request) {

        // 토큰 검증 및 멤버 객체 가져오기
        Member member = memberCheckUtils.checkMember(request);

        // 해당 친구 찾기
        Member friend = memberRepository.findByNickName(friendName).orElseThrow(
                () -> new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","유저가 아닙니다")));

        // 자기 자신과는 친구를 할 수 없습니다.
        if (Objects.equals(member.getNickName(), friend.getNickName())){
            throw new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","자신과는 친구가 될 수 없습니다."));
        }

        // 이미 친구인지 확인
        Optional<Friend> alreadyFriend = friendRepository.findByMyNicknameAndFriendNickname(member.getNickName(), friend.getNickName());
        if (alreadyFriend.isPresent()){
            throw new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","이미 친구입니다."));
        }

        // 친구 빌드
        Friend myFriend = Friend.builder()
                .myNickname(member.getNickName())
                .friendNickname(friend.getNickName())
                .friendEmail(friend.getEmail())
                .friendProfileImage(friend.getProfileImage())
                .friendLikeCount(friend.getLikeCount())
                .build();


        // 친구 리스트 담기
        List<Friend> friends = friendRepository.findAllByMyNickname(myFriend.getMyNickname());

        List<FriendResponseDto> friendResponseDtoList = new ArrayList<>();

        for (Friend addFriend : friends){
            friendResponseDtoList.add(new FriendResponseDto(addFriend));
        }

        // 친구 저장
        friendRepository.save(myFriend);

        return FriendResponseDto.builder()
                .id(myFriend.getId())
                .myNickname(myFriend.getMyNickname())
                .friendNickname(myFriend.getFriendNickname())
                .friendEmail(myFriend.getFriendEmail())
                .friendProfileImage(myFriend.getFriendProfileImage())
                .friendLikeCount(myFriend.getFriendLikeCount())
                .build();
    }


    //친구 삭제 메서드
    public String deleteFriend(String friendName, HttpServletRequest request) {
        //토큰 검증 및 멤버 객체 가져오기
        Member member = memberCheckUtils.checkMember(request);

        // 해당 친구 찾기
        Friend friend = friendRepository.findByMyNicknameAndFriendNickname(member.getNickName(), friendName).orElseThrow(
                () -> new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","친구 리스트에서 해당 친구를 찾을 수 없습니다."))
        );

        // 친구 삭제
        friendRepository.delete(friend);

        return "Success";


    }


    //전체 친구 조회 메서드
    public Page<Friend> getAllFriends(int page, HttpServletRequest request) {

        //토큰 검증 및 멤버 객체 가져오기
        Member member = memberCheckUtils.checkMember(request);

        PageRequest pageable = PageRequest.of(page-1,6);

        Page<Friend> myFriends = friendRepository.findByMyNicknameOrderByCreatedAt(member.getNickName(), pageable);

        return myFriends;


    }

}
