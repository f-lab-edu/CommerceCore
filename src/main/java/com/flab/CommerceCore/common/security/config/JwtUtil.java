package com.flab.CommerceCore.common.security.config;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  private static SecretKey JWT_SECRET_KET;

  public JwtUtil(@Value("${jwt.secret-key}") String secretKey) {
    JwtUtil.JWT_SECRET_KET = Keys.hmacShaKeyFor(secretKey.getBytes());
  }

  public String generateToken(String username) {
    return Jwts.builder()
        .subject(username) // 주체 설정
        .issuedAt(new Date()) // 발행 시간
        .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10시간 후 만료
        .signWith(JWT_SECRET_KET) // 키를 사용해 서명
        .compact(); // 토큰 문자열 생성
  }

  public String getUsername(String token) {
    try {
      Jwt<?, ?> jwt = Jwts.parser()
          .verifyWith(JWT_SECRET_KET) // 서명 키 설정
          .build() // 파서 빌드
          .parse(token); // 토큰 파싱

      Claims claims = (Claims) jwt.getBody(); // 클레임에서 정보 추출
      return claims.getSubject(); // 토큰의 subject (username) 반환
    } catch (JwtException ex) {
      // 토큰 파싱 실패 시 처리
      throw new IllegalArgumentException("Invalid token");
    }
  }

  // 토큰 유효성 검증 메서드
  public boolean isTokenValid(String token, String username) {
    try {
      String extractedUsername = getUsername(token);
      return (extractedUsername.equals(username) && !isTokenExpired(token));
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  // 토큰 만료 여부 확인
  private boolean isTokenExpired(String token) {
    try {
      Jwt<?, ?> jwt = Jwts.parser()
          .verifyWith(JWT_SECRET_KET)
          .build()
          .parse(token);
      Claims claims = (Claims) jwt.getBody();
      return claims.getExpiration().before(new Date());
    } catch (JwtException ex) {
      // 만료 여부 확인 실패 시 false 반환
      return false;
    }
  }



}
