package home.simple_user_api.commons;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtService {
    private final JwtEncoder jwtEncoder;

    public JwtService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    @Value("${security.jwt-exp}")
    private Long expirationTime;

    public Jwt generateJwtToken(UserDetails userDetails) {
        Instant now = Instant.now();
        Instant expiryDate = now.plusSeconds(expirationTime);

        var jwtHeader = JwsHeader.with(() -> "HS256").build();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(expiryDate)
                .subject(userDetails.getUsername())
                .claim("user", userDetails.getUsername())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(jwtHeader, claims));
    }
}
