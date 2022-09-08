package com.four.withtopia.config.security.jwt;


import com.four.withtopia.config.security.Authority;
import com.four.withtopia.config.security.UserDetailsImpl;
import com.four.withtopia.db.domain.Member;
import com.four.withtopia.db.domain.RefreshToken;
import com.four.withtopia.db.repository.MemberRepository;
import com.four.withtopia.db.repository.RefreshTokenRepository;
import com.four.withtopia.dto.request.TokenDto;
import com.four.withtopia.dto.response.ResponseDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Date;
import java.util.Optional;


@Slf4j
@Component
public class TokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;            //30분
    private static final long REFRESH_TOKEN_EXPRIRE_TIME = 1000 * 60 * 60 * 24 * 7;     //7일


    private final Key key;

    private final RefreshTokenRepository refreshTokenRepository;

    public TokenProvider(@Value("${jwt.secret}") String secretKey,
                         RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // -------------------------------------------------------엑세스 토큰 발급
    public String GenerateAccessToken(Member member){
        long now = (new Date().getTime());
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setSubject(member.getEmail())
                .claim(AUTHORITIES_KEY, Authority.ROLE_MEMBER.toString())
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    // -------------------------------------------------------리프레쉬 토큰 발급
    public String GenerateRefreshToken(Member member){
        long now = (new Date().getTime());

        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPRIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        RefreshToken refreshTokenObject = RefreshToken.builder()
                .id(member.getMemberId())
                .member(member)
                .value(refreshToken)
                .build();

        refreshTokenRepository.save(refreshTokenObject);
        return refreshToken;
    }
    // -------------------------------------------------------인증 정보로 부터 유저 정보를 가져옴
    public Member getMemberFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.
                isAssignableFrom(authentication.getClass())) {
            return null;
        }
        return ((UserDetailsImpl) authentication.getPrincipal()).getMember();
    }
    // ------------------------------------------------------- 유저 email 정보로 토큰 발급

    public String ReissueAccessToken(String Refresh){
        long now = (new Date().getTime());
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(Refresh).getBody();
        Member member = refreshTokenRepository.findByValue(claims.getSubject()).getMember();
        return Jwts.builder()
                .setSubject(member.getEmail())
                .claim(AUTHORITIES_KEY, Authority.ROLE_MEMBER.toString())
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    @Transactional(readOnly = true)
    public RefreshToken isPresentRefreshToken(Member member) {
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByMember(member);
        return optionalRefreshToken.orElse(null);
    }

    @Transactional
    public ResponseEntity<?> deleteRefreshToken(Member member) {
        RefreshToken refreshToken = isPresentRefreshToken(member);
        if (null == refreshToken) {
            return ResponseEntity.ok("TOKEN_NOT_FOUND, 존재하지 않는 Token 입니다.");
        }

        refreshTokenRepository.delete(refreshToken);
        return ResponseEntity.ok("success");
    }
    // -------------------------------------------------------Reissue

}