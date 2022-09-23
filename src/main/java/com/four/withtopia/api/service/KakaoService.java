package com.four.withtopia.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.four.withtopia.db.domain.Member;
import com.four.withtopia.db.domain.ProfileImage;
import com.four.withtopia.db.repository.MemberRepository;
import com.four.withtopia.db.repository.ProfileImageRepository;
import com.four.withtopia.dto.request.KakaoUserInfoDto;
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

import java.util.List;
import java.util.Random;
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
        body.add("grant_type", "authorization_code");
        body.add("client_id", CLIENT_ID);
        body.add("redirect_uri", REDIRECT_URI);
        body.add("client_secret", CLIENT_SECRET);
        body.add("code", code);
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
        System.out.println("responseBody = " + responseBody);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    // 카카오에서 유저 인포 받아오기
    KakaoUserInfoDto getKakaoUserInfo(String kakaoAccessToken) throws JsonProcessingException {
        // Http 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + kakaoAccessToken);
        headers.add("Content-type","application/x-www-form-urlencoded;charset=utf-8");
        System.out.println("kakaoAccessToken = " + kakaoAccessToken);
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
        System.out.println("responseBody = " + responseBody);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
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
        List<ProfileImage> images = profileImageRepository.findAll();
        int randomInt = new Random().nextInt(images.size());

        return Member.builder()
                .kakaoId(kakaoUserInfoDto.getKakaoId())
                .nickName(kakaoUserInfoDto.getNickName() + "_kakao_" + usernameId)
                .email(kakaoUserInfoDto.getEmail())
                .profileImage(images.get(randomInt).getProfileIamge())
                .build();
    }
}
