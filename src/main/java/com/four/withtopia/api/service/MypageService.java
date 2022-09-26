package com.four.withtopia.api.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
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
import com.four.withtopia.util.MemberCheckUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MypageService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final MemberCheckUtils memberCheckUtils;
    private final ProfileImageRepository profileImageRepository;
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Transactional(readOnly = true)
    public MypageResponseDto getMypage(HttpServletRequest request){
        // 토큰 검사
        Member member = memberCheckUtils.checkMember(request);
        MypageResponseDto responseDto = MypageResponseDto.createMypageResponseDto(member);
        return responseDto;
    }

    @Transactional
    public MypageResponseDto updateMemberInfo(ProfileUpdateRequestDto requestDto, HttpServletRequest request){
        // 토큰 검사
        Member member = memberCheckUtils.checkMember(request);

        member.updateMember(requestDto, member);
        memberRepository.save(member);
        MypageResponseDto responseDto = MypageResponseDto.createMypageResponseDto(member);

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

    public String insertImage(MultipartFile multipartFile) throws IOException {
        if (multipartFile == null | multipartFile.isEmpty()){
            throw new PrivateException(new ErrorCode(HttpStatus.NOT_FOUND,"400","이미지 파일이 없습니다"));
        }
        String fileName = multipartFile.getOriginalFilename();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());

        byte[] bytes = IOUtils.toByteArray(multipartFile.getInputStream());
        objectMetadata.setContentLength(bytes.length);
        ByteArrayInputStream byteArrayIs = new ByteArrayInputStream(bytes);

        amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, byteArrayIs, objectMetadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        String imgurl = amazonS3Client.getUrl(bucketName, fileName).toString();

        ProfileImage profileImage = new ProfileImage(imgurl);

        profileImageRepository.save(profileImage);
        return "이미지 업로드 성공";
    }
}
