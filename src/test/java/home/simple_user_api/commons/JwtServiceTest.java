package home.simple_user_api.commons;

import home.simple_user_api.IntegrationAndUnitTest;
import home.simple_user_api.MySQLTestContainer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@IntegrationAndUnitTest
public class JwtServiceTest {
    @Autowired
    private JwtService jwtService;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        MySQLTestContainer.configureProperties(registry);
    }

    @Test
    public void shouldGenerateJwtToken() {
        // given
        UserDetails user = thereIsUserDetails();

        // when
        Jwt jwt = jwtService.generateJwtToken(user);

        // then
        Assertions.assertThat(jwt.getSubject()).isEqualTo(user.getUsername());
    }

    private UserDetails thereIsUserDetails() {
        return User.withUsername("test-user")
                .password("test-password")
                .roles("USER")
                .build();
    }
}
