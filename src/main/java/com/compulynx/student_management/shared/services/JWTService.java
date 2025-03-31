package com.compulynx.student_management.shared.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JWTService {
    private static final Logger log = LogManager.getLogger(JWTService.class);
    @Value("${jwt.secret}")
    private String signInKey;
    @Value("${jwt.expiry:1800}")
    private String tokenExpiry;
    @Value("${jwt.issuer:voltax-solutions.com}")
    private String tokenIssuer;

    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(),userDetails);
    }
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setIssuer(this.tokenIssuer)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * Integer.parseInt(tokenExpiry)))
                .signWith(getSigninKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean isValidToken(String token,UserDetails userDetails){
        final String userName=extractUserName(token);
        final String issuer=extractIssuer(token);
        if (!issuer.equals(tokenIssuer))return false;
        return userName.equals(userDetails.getUsername()) && isTokenExpired(token);
    }

    private String extractIssuer(String token) {
        var issuer= extractClaim(token,Claims::getIssuer);
        return issuer==null?"No Issuer":issuer;
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).after(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token,Claims::getExpiration);
    }

    public String extractUserName(String token) {
        return extractClaim(token,Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        Optional<Claims> claimsOptional = extractAllClaims(token);
        if(claimsOptional.isEmpty())
            log.warn("Claims not present for the provided token.");
        return claimsOptional.map(claimsResolver).orElse(null);
    }

    private Optional<Claims> extractAllClaims(String token){
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigninKey())
                    .build().parseClaimsJws(token).getBody();
            return Optional.of(claims);
        }catch (Exception e){
            log.warn("Token validation error error {}",e.getMessage());
        }
        return Optional.empty();
    }

    private Key getSigninKey() {
        byte[] keyBytes = Base64.getDecoder().decode(signInKey);
        if (keyBytes.length < 64) {
            throw new IllegalArgumentException("The signing key must be at least 64 bytes (512 bits) for HS512.");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
