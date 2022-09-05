package com.four.withtopia.db.domain;

import com.four.withtopia.dto.request.MemberRequestDto;
import com.four.withtopia.dto.request.ProfileUpdateRequestDto;
import com.four.withtopia.util.Timestamped;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
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
    private boolean isDelete = false;

    @Column
    private String kakaoId;
    @Column
    private String googleId;

    public Member(MemberRequestDto requestDto) {
        this.nickName = requestDto.getNickname();
        this.email = requestDto.getEmail();
        this.password = requestDto.getPassword();
        this.profileImage = "https://hanghae99-wonyoung.s3.ap-northeast-2.amazonaws.com/e3f569cf-b23a-4462-a0e1-9caa51e36aca";
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
    public void updateMember(ProfileUpdateRequestDto requestDto){
        this.nickName = requestDto.getNickName();
        this.profileImage = requestDto.getProfileImage();
    }

    public void deleteMember(){
        this.isDelete = true;
    }

}
