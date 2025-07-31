package home.simple_user_api.users.domain;

import home.simple_user_api.IntegrationAndUnitTest;
import home.simple_user_api.MySQLTestContainer;
import home.simple_user_api.commons.ApiException;
import home.simple_user_api.items.domain.ItemRepository;
import home.simple_user_api.users.dtos.exceptions.UserDoesNotExistsException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@IntegrationAndUnitTest
public class CustomUserDetailsServiceTest {
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        MySQLTestContainer.configureProperties(registry);
    }

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void shouldLoadExistingUserByUsernameAndReturnUserDetails() {
        // given
        thereIsUser();
        String expectedUserLogin = "test-user";

        // when
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(expectedUserLogin);

        // then
        Assertions.assertThat(userDetails.getUsername()).isEqualTo(expectedUserLogin);
    }

    @Test
    public void shouldNotLoadExistingUserByUsernameAndThrowException() {
        // given
        String nonexistentUserLogin = "non-existent-user";
        String expectedExceptionMessage = "User does not exist exception";
        Assertions.assertThat(userRepository.existsByLogin(nonexistentUserLogin)).isFalse();

        // when
        ApiException exception = org.junit.jupiter.api.Assertions.assertThrows(UserDoesNotExistsException.class, () -> customUserDetailsService.loadUserByUsername(nonexistentUserLogin));

        // then
        Assertions.assertThat(exception.getErrorResponseCode().getMessage()).isEqualTo(expectedExceptionMessage);
    }

    private void thereIsUser() {
        String encodedPassword = passwordEncoder.encode("test-password");
        userRepository.save(new User("test-user", encodedPassword));
    }
}
