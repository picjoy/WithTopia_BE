package com.four.withtopia.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.four.withtopia.db.domain.Member;
import com.four.withtopia.db.domain.ProfileImage;
import com.four.withtopia.db.repository.MemberRepository;
import com.four.withtopia.db.repository.ProfileImageRepository;
import com.four.withtopia.dto.KakaoUserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KakaoService {

    private final MemberRepository memberRepository;
    private final ProfileImageRepository profileImageRepository;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}") private String CLIENT_ID; // rest APi 키
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}") private String CLIENT_SECRET ; // 시크릿 키
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}") private String REDIRECT_URI; // 리다이렉트 uri

    // 카카오에서 엑세스 토큰 받아오기
    String getKakaoAccessToken(String code) throws JsonProcessingException {
        // Http 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type","application/x-www-form-urlencoded;charset=utf-8");
        // Http 바디
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_cod");
        body.add("client_id", CLIENT_ID);
        body.add("redirect_uri", REDIRECT_URI);
        body.add("code", code);
        body.add("client_secret", CLIENT_SECRET);
        // Http 요청
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body,headers);
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> response = template.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );
        // kakao Access Token
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        System.out.println("kakao access token = " + jsonNode.asText());
        return jsonNode.asText();
    }

    // 카카오에서 유저 인포 받아오기
    KakaoUserInfoDto getKakaoUserInfo(String token) throws JsonProcessingException {
        // Http 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("Content-type","application/x-www-form-urlencoded;charset=utf-8");
        // Http 요청
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> response = template.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );
        // kakao user info
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        System.out.println("kakao user info = " + jsonNode.asText());
        return KakaoUserInfoDto.createKakaoUserInfo(jsonNode);
    }

    // 필요시 회원가입 하기
    @Transactional
    Member createKakaoMember(KakaoUserInfoDto kakaoUserInfo) {
        // DB 에 중복된 Kakao Id 가 있는지 확인
        String kakaoUserId = kakaoUserInfo.getKakaoId();
        Member kakaoUser = memberRepository.findByKakaoId(kakaoUserId);

        // 없으면 회원가입 진행
        if (kakaoUser == null) {
            Member newMember = ConvertingKakaoUserToMember(kakaoUserInfo);
            return memberRepository.save(newMember);
        }

        return kakaoUser;
    }

    // 카카오 유저를 우리 회원 양식으로 맞춰 넣기
    public Member ConvertingKakaoUserToMember(KakaoUserInfoDto kakaoUserInfoDto){
        // 유저네임에 랜덤한 id 붙여주기
        String usernameId = UUID.randomUUID().toString();
        // 유저 이미지를 랜덤하게 부여하기
        Long randomImgId = (long)(Math.random() * 8);
        System.out.println("randomImgId = " + randomImgId);
        Optional<ProfileImage> randomImg = profileImageRepository.findById(randomImgId);

        return Member.builder()
                .kakaoId(kakaoUserInfoDto.getKakaoId())
                .nickName(kakaoUserInfoDto.getNickName() + "_kakao_" + usernameId)
                .email(kakaoUserInfoDto.getEmail())
                .profileImage(randomImg.get().getProfileIamge())
                .build();
    }
}
