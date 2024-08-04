package com.security.springsecurityremake.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    static final String CLAIM_KEY = "wK3s8dXNvRr2Gs9mZ6tyMw1Q6F4Bd8O1HzgXW/RW5Q0=";
    public String extractUsername(String token) {
      return extractClaims(token, Claims::getSubject);
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsFunction){
        final Claims claims = extractClaims(token);
        return claimsFunction.apply(claims);
    }

    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }

    Claims extractClaims(String token) {
        //return Jwts.parserBuilder().setSigningKey("").build().parseClaimsJws(token).getBody();
      return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }
    Key getSigningKey(){
        byte [] keyBytes = Decoders.BASE64.decode(CLAIM_KEY);
        return Keys.hmacShaKeyFor(keyBytes);

    }

    public String generateToken( Map<String, Object> extraclaims,
    final UserDetails userDetails){
        return Jwts.builder().setClaims(extraclaims).setSubject(userDetails.getUsername()).
                setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails ){
        final String username = extractUsername(token);

        System.out.println("Username is username "+ username+ " userDetails.getUsername() is "+userDetails.getUsername() );
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }
}
