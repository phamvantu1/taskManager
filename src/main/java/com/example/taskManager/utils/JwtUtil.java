package com.example.taskManager.utils;

import com.example.taskManager.model.entity.Permission;
import com.example.taskManager.model.entity.Role;
import com.example.taskManager.model.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Value("${application.sercurity.jwt.secret-key}")
    private String secretKey;

    @Value("${application.sercurity.jwt.expiration}")
    private long expiration; // milliseconds

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();

        // Đưa danh sách tên role vào claims
        claims.put("role", user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList()));

        // Đưa danh sách permission vào claims
        Set<Permission> permissionsSet = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .collect(Collectors.toSet());

        List<String> permissions = permissionsSet.stream()
                .map(Permission::getName)
                .collect(Collectors.toList());

        claims.put("permissions", permissions);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS512)
                .compact();
    }



    // Trích xuất username
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Kiểm tra token có hợp lệ không
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    //  Kiểm tra token hết hạn chưa
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    //  Trích xuất bất kỳ claim nào
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    //  Parse token để lấy claims
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //  Lấy Key phù hợp HS512 từ secret string
    private Key getSignInKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
