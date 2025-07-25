package home.simple_user_api.commons;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
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

        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiresAt(expiryDate)

                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet));
    }
}
