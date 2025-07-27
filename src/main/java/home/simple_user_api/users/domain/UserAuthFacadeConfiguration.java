package home.simple_user_api.users.domain;

import home.simple_user_api.commons.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserAuthFacadeConfiguration {
    @Bean
    public UserAuthFacade userFacade(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserDetailsService userDetailsService,
            JwtService jwtService) {
        return new UserAuthFacade(userRepository, passwordEncoder, userDetailsService, jwtService);
    }
}
