package com.four.withtopia.db.domain;

import com.four.withtopia.dto.request.MemberRequestDto;
import com.four.withtopia.dto.request.ProfileUpdateRequestDto;
import com.four.withtopia.util.Timestamped;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Member extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false)
    private String nickName;

    @Column
    private String email;

    @Column
    private String password;

    @Column(nullable = false)
    private String profileImage;

    @Column(nullable = false)
    @Builder.Default
    private boolean isDelete = false;

    @Column
    private String kakaoId;
    @Column
    private String googleId;

    @Column
    @Builder.Default
    private long likeCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private boolean suspend = false;

    @Builder
    public Member(MemberRequestDto requestDto,String password,String image) {
        this.nickName = requestDto.getNickname();
        this.email = requestDto.getEmail();
        this.password = password;
        this.profileImage = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Member member = (Member) o;
        return memberId != null && Objects.equals(memberId, member.memberId);
    }


//    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
//            return false;
//        }
//        Member member = (Member) o;
//        return memberId != null && Objects.equals(memberId, member.memberId);
//    }
//
//    @Override
//    public int hashCode() {
//        return getClass().hashCode();
//    }
//
    public boolean validatePassword(PasswordEncoder passwordEncoder, String password) {
        return passwordEncoder.matches(password, this.password);
    }

    // 멤버 프로필 업데이트
    public void updateMember(ProfileUpdateRequestDto requestDto, Member member){
        if(requestDto.getNickName() == null || Objects.equals(requestDto.getNickName(), "")){
            this.nickName = member.getNickName();
            this.profileImage = requestDto.getProfileImage();
        }
        else if(requestDto.getProfileImage() == null || Objects.equals(requestDto.getProfileImage(), "")){
            this.nickName = requestDto.getNickName();
            this.profileImage = member.getProfileImage();
        }
        else{
            this.nickName = requestDto.getNickName();
            this.profileImage = requestDto.getProfileImage();
        }
    }

    public void updatePw(String password){
        this.password = password;
    }

    public void deleteMember(){
        this.isDelete = true;
    }

    public void updatePopularity(Long likCnt){
        this.likeCount = likCnt;
    }
    public void updateSuspend(boolean suspend){
        this.suspend = suspend;
    }
}
