package com.four.withtopia.db.domain;

import com.four.withtopia.dto.request.MemberRequestDto;
import com.four.withtopia.util.Timestamped;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

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

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String profileImage;

    @Column(nullable = false)
    private boolean isDelete;



    public Member(MemberRequestDto memberDTO) {
        this.nickName = memberDTO.getNickname();
        this.email = memberDTO.getEmail();
        this.password = memberDTO.getPassword();
        this.profileImage = "asdfasdfasdfasdf";
        this.isDelete = false;
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
}
