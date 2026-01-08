package com.jobportal.utility;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.jobportal.dto.AccountType;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	
	@Value("${jwt.secret:your-secret-key-change-this-in-production-minimum-256-bits}")
	private String secret;
	
	@Value("${jwt.expiration:3600000}") // 1 hour in milliseconds
	private Long expiration;
	
	// Extract username from token
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}
	
	// Extract expiration date from token
	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}
	
	// Extract claim from token
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}
	
	// Extract all claims from token
	private Claims extractAllClaims(String token) {
		return Jwts.parser()
				.verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
	
	// Check if token is expired
	public Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}
	
	// Generate token for user
	public String generateToken(UserDetails userDetails, String userId, AccountType role) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("userId", userId);
		claims.put("role", role.toString());
		return createToken(claims, userDetails.getUsername());
	}
	
	// Create token
	private String createToken(Map<String, Object> claims, String subject) {
		return Jwts.builder()
				.claims(claims)
				.subject(subject)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + expiration))
				.signWith(getSigningKey())
				.compact();
	}
	
	// Validate token
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
	
	// Get signing key
	private Key getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secret);
		return Keys.hmacShaKeyFor(keyBytes);
	}
	
	// Extract user ID from token
	public String extractUserId(String token) {
		return extractClaim(token, claims -> claims.get("userId", String.class));
	}
	
	// Extract role from token
	public String extractRole(String token) {
		return extractClaim(token, claims -> claims.get("role", String.class));
	}
}

