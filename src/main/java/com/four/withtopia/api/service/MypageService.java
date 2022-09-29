package com.four.withtopia.api.service;

import com.four.withtopia.config.error.ErrorCode;
import com.four.withtopia.config.expection.PrivateException;
import com.four.withtopia.config.security.jwt.TokenProvider;
import com.four.withtopia.db.domain.Member;
import com.four.withtopia.db.domain.ProfileImage;
import com.four.withtopia.db.repository.MemberRepository;
import com.four.withtopia.db.repository.ProfileImageRepository;
import com.four.withtopia.dto.request.ChangePasswordRequestDto;
import com.four.withtopia.dto.request.ProfileUpdateRequestDto;
import com.four.withtopia.dto.response.MypageResponseDto;
import com.four.withtopia.dto.response.ProfileImageListResponseDto;
import com.four.withtopia.util.InsertImageUtil;
import com.four.withtopia.util.MemberCheckUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MypageService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final MemberCheckUtils memberCheckUtils;
    private final InsertImageUtil insertImageUtil;
    private final ProfileImageRepository profileImageRepository;

    @Transactional(readOnly = true)
    public MypageResponseDto getMypage(HttpServletRequest request){
        // 토큰 검사
        Member member = memberCheckUtils.checkMember(request);
        MypageResponseDto responseDto = MypageResponseDto.createMypageResponseDto(member);
        return responseDto;
    }

    @Transactional
    public MypageResponseDto updateMemberInfo(ProfileUpdateRequestDto requestDto, HttpServletRequest request, HttpServletResponse response){
        // 토큰 검사
        Member member = memberCheckUtils.checkMember(request);

        // 만약 닉네임이 null이면
        if(requestDto.getNickName()==null || requestDto.getNickName().isEmpty() || requestDto.getNickName().isBlank()){
            requestDto.nicknameUpdateRequestDto(member.getNickName());
            System.out.println("==========================================");
            System.out.println(requestDto.getNickName());
        }

        // 닉네임 양식 확인
        if (requestDto.getNickName().length() < 2 || requestDto.getNickName().length()  > 12){
            response.addHeader("Authorization", request.getHeader("Authorization"));
            response.addHeader("RefreshToken", request.getHeader("RefreshToken"));
            throw new PrivateException(new ErrorCode(HttpStatus.OK, "200","닉네임 양식에 맞지 않습니다."));
        }

        // 멤버 db에 동일한 닉네임이 있으면 예외
        if(memberRepository.existsByNickName(requestDto.getNickName())){
            throw new PrivateException(new ErrorCode(HttpStatus.OK, "200","동일한 닉네임이 이미 존재합니다."));
        }

        member.updateMember(requestDto, member);
        memberRepository.save(member);
        MypageResponseDto responseDto = MypageResponseDto.createMypageResponseDto(member);

        // 토큰 재발급
        String accessToken = tokenProvider.GenerateAccessToken(member);
        String refreshToken = tokenProvider.GenerateRefreshToken(member);

        response.addHeader("Authorization", "Bearer " + accessToken);
        response.addHeader("RefreshToken", refreshToken);

        return responseDto;
    }

    @Transactional
    public String deleteMember(HttpServletRequest request){
        // 토큰 검사
        Member member = memberCheckUtils.checkMember(request);

        member.deleteMember();
        memberRepository.save(member);

        // 3일 뒤 회원 지우기
        memberDelete();

        return "success";
    }

    @Transactional
    public String changePassword(ChangePasswordRequestDto requestDto){
        Member member = tokenProvider.getMemberFromAuthentication();
        if (member.validatePassword(passwordEncoder,requestDto.getPassword())){
            throw new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","현재 비밀번호가 일치하지않습니다."));
        }
        if (!Objects.equals(requestDto.getPassword(),requestDto.getPasswordConfirm())){
            throw new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","패스워드가 일치하지않습니다."));
        }
        String password = passwordEncoder.encode(requestDto.getPassword());
        member.updatePw(password);
        memberRepository.save(member);
        return "success";
    }

    // 프로필 이미지 전체 보내기
    @Transactional
    public List<ProfileImageListResponseDto> getProfileImage(){
        List<ProfileImage> imageList = profileImageRepository.findAll();
        List<ProfileImageListResponseDto> responseDtoList = new ArrayList<>();

        for (ProfileImage image: imageList) {
            ProfileImageListResponseDto responseDto = ProfileImageListResponseDto.builder()
                    .imageId(image.getImageId())
                    .imageUrl(image.getProfileIamge())
                    .build();
            responseDtoList.add(responseDto);
        }

        return responseDtoList;
    }

    // 회원이 탈퇴한 후 3일이 지나면 회원 내역 삭제
    public void memberDelete(){
        // 3일 뒤
        long lateTime = 1000 * 60 * 60 * 24 * 3;

        Timer finalDelete = new Timer();
        TimerTask deleteTask = new TimerTask() {
            @Override
            public void run() {
                // 멤버 지우기
                Member deleteMember = memberRepository.findByIsDelete(true);
                memberRepository.delete(deleteMember);
                finalDelete.cancel();
            }
        };

        finalDelete.schedule(deleteTask, lateTime);
    }

    public String insertProfileImage(MultipartFile multipartFile) throws IOException {
        String imgUrl = insertImageUtil.insertImage(multipartFile);
        ProfileImage profileImage = new ProfileImage(imgUrl);

        profileImageRepository.save(profileImage);
        return "이미지 업로드 성공";
    }
}
