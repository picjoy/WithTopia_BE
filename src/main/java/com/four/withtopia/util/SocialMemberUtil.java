package com.four.withtopia.util;

import com.four.withtopia.config.error.ErrorCode;
import com.four.withtopia.config.expection.PrivateException;
import com.four.withtopia.db.domain.Member;
import com.four.withtopia.db.domain.ProfileImage;
import com.four.withtopia.db.repository.MemberRepository;
import com.four.withtopia.db.repository.ProfileImageRepository;
import com.four.withtopia.dto.request.SocialUserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SocialMemberUtil {

    public final MemberRepository memberRepository;
    private final ProfileImageRepository profileImageRepository;

    // 필요시 회원가입 하기
    @Transactional
    public Member createSocialMember(SocialUserInfoDto socialUserInfoDto) {

        // DB 에 중복된 social Id 가 있는지 확인
        String socialUserId;
        Member socialUser = null;

        // google
        if(socialUserInfoDto.getKakaoId() == null && socialUserInfoDto.getGoogleId() != null ){
            socialUserId = socialUserInfoDto.getGoogleId();
            socialUser = memberRepository.findByGoogleId(socialUserId);
        }

        //kakao
        if(socialUserInfoDto.getKakaoId() != null){
            socialUserId = socialUserInfoDto.getKakaoId();
            socialUser = memberRepository.findByKakaoId(socialUserId);
        }

        socialMemberCheckException(socialUser, socialUserInfoDto);

        // 없으면 회원가입 진행
        if (socialUser == null) {
            Member newMember = ConvertingSocialUserToMember(socialUserInfoDto);
            return memberRepository.save(newMember);
        }

        return socialUser;
    }

    @Transactional(readOnly = true)
    void socialMemberCheckException (Member member, SocialUserInfoDto socialUserInfoDto){
        // 이미 가입한 메일이면 이미 가입한 멤버라고 알려주기
        if(member == null && socialUserInfoDto.getEmail() != null && memberRepository.existsByEmail(socialUserInfoDto.getEmail())){
            System.out.println("여기");
            System.out.println(memberRepository.existsByEmail(socialUserInfoDto.getEmail()));
            throw new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400", "동일한 이메일이 이미 존재합니다."));
        }

        // 탈퇴한지 3일이 되지 않은 유저 예외처리
        if(member != null && member.isDelete()){
            throw new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400", "이미 탈퇴한 멤버입니다."));
        }
    }


    // social 유저를 우리 회원 양식으로 맞춰 넣기
    @Transactional(readOnly = true)
    Member ConvertingSocialUserToMember(SocialUserInfoDto socialUserInfo){
        // 유저네임에 랜덤한 id 붙여주기
        String usernameId = UUID.randomUUID().toString();
        // 유저 이미지를 랜덤하게 부여하기
        List<ProfileImage> images = profileImageRepository.findAll();
        int randomInt = new Random().nextInt(images.size());


        //google
        if(socialUserInfo.getGoogleId() != null){
            return Member.builder()
                    .googleId(socialUserInfo.getGoogleId())
                    .nickName(socialUserInfo.getNickName() + "_google_" + usernameId)
                    .email(socialUserInfo.getEmail())
                    .profileImage(images.get(randomInt).getProfileIamge())
                    .build();
        }

        //kakao
        return Member.builder()
                .kakaoId(socialUserInfo.getKakaoId())
                .nickName(socialUserInfo.getNickName() + "_kakao_" + usernameId)
                .email(socialUserInfo.getEmail())
                .profileImage(images.get(randomInt).getProfileIamge())
                .build();
    }
}
