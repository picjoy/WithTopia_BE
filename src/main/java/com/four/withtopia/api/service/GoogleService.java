package com.four.withtopia.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.four.withtopia.config.error.ErrorCode;
import com.four.withtopia.config.expection.PrivateException;
import com.four.withtopia.db.domain.Member;
import com.four.withtopia.db.domain.ProfileImage;
import com.four.withtopia.db.repository.MemberRepository;
import com.four.withtopia.db.repository.ProfileImageRepository;
import com.four.withtopia.dto.request.GoogleUserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoogleService {
    private final MemberRepository memberRepository;
    private final ProfileImageRepository profileImageRepository;

    @Value("${spring.security.oauth2.client.registration.google.client-id}") private String CLIENT_ID; // rest APi 키
    @Value("${spring.security.oauth2.client.registration.google.client-secret}") private String CLIENT_SECRET ; // 시크릿 키
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}") private String REDIRECT_URI; // 리다이렉트 uri

    // 카카오에서 엑세스 토큰 받아오기
    String getGoogleAccessToken(String code) throws JsonProcessingException {
        // Http 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type","application/x-www-form-urlencoded;charset=utf-8");
        // Http 바디
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", CLIENT_ID);
        body.add("redirect_uri", REDIRECT_URI);
        body.add("client_secret", CLIENT_SECRET);
        body.add("code", code);
        // Http 요청
        HttpEntity<MultiValueMap<String, String>> googleTokenRequest = new HttpEntity<>(body,headers);
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> response = template.exchange(
                "https://oauth2.googleapis.com/token",
                HttpMethod.POST,
                googleTokenRequest,
                String.class
        );
        // kakao Access Token
        String responseBody = response.getBody();
        System.out.println("responseBody = " + responseBody);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    // 카카오에서 유저 인포 받아오기
    GoogleUserInfoDto getGoogleUserInfo(String googleAccessToken) throws JsonProcessingException {
        // Http 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + googleAccessToken);
        headers.add("Content-type","application/x-www-form-urlencoded;charset=utf-8");
        System.out.println("googleAccessToken = " + googleAccessToken);
        // Http 요청
        HttpEntity<MultiValueMap<String, String>> googleUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> response = template.exchange(
                "https://www.googleapis.com/oauth2/v1/userinfo",
                HttpMethod.GET,
                googleUserInfoRequest,
                String.class
        );
        // kakao user info
        String responseBody = response.getBody();
        System.out.println("responseBody = " + responseBody);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return GoogleUserInfoDto.createGoogleUserInfoDto(jsonNode);
    }

    // 필요시 회원가입 하기
    @Transactional
    Member createGoogleMember(GoogleUserInfoDto googleUserInfo) {
        // DB 에 중복된 Kakao Id 가 있는지 확인
        String googleUserId = googleUserInfo.getGoogleId();
        Member googleUser = memberRepository.findByGoogleId(googleUserId);

        // 이미 가입한 메일이면 이미 가입한 멤버라고 알려주기
        if(googleUser == null && memberRepository.existsByEmail(googleUserInfo.getEmail())){
            throw new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400", "동일한 이메일이 이미 존재합니다."));
        }

        // 탈퇴한지 3일이 되지 않은 유저 예외처리
        if(googleUser != null && googleUser.isDelete()){
            throw new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400", "이미 탈퇴한 멤버입니다."));
        }

        // 없으면 회원가입 진행
        if (googleUser == null) {
            Member newMember = ConvertingGoogleUserToMember(googleUserInfo);
            return memberRepository.save(newMember);
        }

        return googleUser;
    }

    // 카카오 유저를 우리 회원 양식으로 맞춰 넣기
    public Member ConvertingGoogleUserToMember(GoogleUserInfoDto googleUserInfo){
        // 유저네임에 랜덤한 id 붙여주기
        String usernameId = UUID.randomUUID().toString();
        // 유저 이미지를 랜덤하게 부여하기
        List<ProfileImage> images = profileImageRepository.findAll();
        int randomInt = new Random().nextInt(images.size());

        return Member.builder()
                .googleId(googleUserInfo.getGoogleId())
                .nickName(googleUserInfo.getNickName() + "_google_" + usernameId)
                .email(googleUserInfo.getEmail())
                .profileImage(images.get(randomInt).getProfileIamge())
                .build();
    }
}
