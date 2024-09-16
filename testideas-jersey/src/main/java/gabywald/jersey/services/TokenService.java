package gabywald.jersey.services;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;
import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.context.ApplicationScoped;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@ApplicationScoped
public class TokenService {
	// ***** Basic Solution : KEY has to be secure TODO !!
	private static final String SECRET_KEY = "qwertyuiopasdfghjklzxcvbnm123456";
	
	public String createToken(String subject, long ttlSeconds) {
		// ***** Algorithm used to sign the Token
		SignatureAlgorithm signatureAlgorithm	= SignatureAlgorithm.HS256;
		byte[] apiKeySecretBytes				= TokenService.SECRET_KEY.getBytes();
		Key signingKey							= new SecretKeySpec(
															apiKeySecretBytes,
															signatureAlgorithm.getJcaName());
		// Construire le token
		JwtBuilder builder = Jwts.builder()
				.setId(UUID.randomUUID().toString())
				.setIssuedAt(new Date())
				.setSubject(subject)
				.setIssuer("GWJersey")
				.signWith(signatureAlgorithm, signingKey);
		
		// calculer et affectation de la date/heure d'expiration
		if (ttlSeconds >= 0) {
			Date expiration = Date.from(LocalDateTime.now()
					.plusSeconds(ttlSeconds)
					.atZone(ZoneId.systemDefault())
					.toInstant());
			builder.setExpiration(expiration);
		}
		return builder.compact();
	}
	
	/**
	 * Valider le token.
	 * @param token (String)
	 * @return (Jws&lt;Claims&gt;)
	 * @throws ExpiredJwtException --
	 * @throws UnsupportedJwtException --
	 * @throws MalformedJwtException --
	 * @throws SignatureException --
	 * @throws IllegalArgumentException --
	 */
	public Jws<Claims> validToken(String token) 
			throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
		return Jwts.parser()
				.setSigningKey(SECRET_KEY.getBytes())
				.parseClaimsJws(token);
	}
}