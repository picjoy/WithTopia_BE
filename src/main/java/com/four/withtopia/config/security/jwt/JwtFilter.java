package com.four.withtopia.config.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.four.withtopia.config.security.UserDetailsServiceImpl;
import com.four.withtopia.dto.response.ResponseDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    // 실제 필터링 로직은 doFilterInternal 에 들어감

    public static String AUTHORIZATION_HEADER = "authorization";

    public static String REFRESH_HEADER = "RefreshToken";

    public static String BEARER_PREFIX = "Bearer ";

    public static String AUTHORITIES_KEY = "auth";

    private final String SECRET_KEY;

    private final TokenProvider tokenProvider;

    private final UserDetailsServiceImpl userDetailsService;


    // 실제 필터링 로직은 doFilterInternal 에 들어감
    // JWT 토큰의 인증 정보를 현재 쓰레드의 SecurityContext 에 저장하는 역할 수행


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

//        Session 의 토큰 값 가져오기
        String jwt = resolveToken(request);
        System.out.println("---------------------------------------");
        System.out.println("UsingMethod : "+request.getMethod());
        System.out.println("UsingURL : "+request.getRequestURL());
        System.out.println("AccessToken : "+jwt);
        System.out.println("UsingIP : "+request.getLocalAddr());
        System.out.println(request.getProtocol());
        System.out.println("---------------------------------------");

        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        //바이트배열을 생성한 다음 키를 생성
        Key key = Keys.hmacShaKeyFor(keyBytes);
//     JWT 토큰이 정상적이라면 유저정보를 가져오는 부분
        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            //Payload 부분에는 토큰에 담을 정보가 들어있습니다. 여기에 담는 정보의 한 ‘조각’ 을 클레임(claim) 이라고 부름
            // name / value 의 한 쌍으로 이뤄져있습니다.
            Claims claims;
            try {
                claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();
            } catch (ExpiredJwtException e) {
                claims = e.getClaims();
            }
            System.out.println("----------------Claims-------------------");
            System.out.println(claims);
            System.out.println("-----------------------------------------");
            if (claims.getExpiration().toInstant().toEpochMilli() < Instant.now().toEpochMilli()) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().println(
                        new ObjectMapper().writeValueAsString(
                                ResponseDto.fail("BAD_REQUEST", "Token이 유효햐지 않습니다.")
                        )
                );
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                //SC_BAD_REQUEST = 400
            }

            String subject = claims.getSubject();
            Collection<? extends GrantedAuthority> authorities =
                    Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

            UserDetails principal = userDetailsService.loadUserByUsername(subject);

            Authentication authentication = new UsernamePasswordAuthenticationToken(principal, jwt, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
// --------------------------------------------
        filterChain.doFilter(request, response);


    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }

        return null;
    }

    private String resolveRefresh(HttpServletRequest request) {
        String RefreshToken = request.getHeader(REFRESH_HEADER);
        if (StringUtils.hasText(RefreshToken)) {
            return RefreshToken;
        }
        return null;
    }

}
