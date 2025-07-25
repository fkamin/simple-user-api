package home.simple_user_api.users.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserAuthConfiguration {

    @Bean
    public UserAuthFacade userFacade(UserRepository userRepository) {
        return new UserAuthFacade(userRepository);
    }
}
