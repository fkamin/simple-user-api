package home.simple_user_api.users.domain;

import home.simple_user_api.MySQLTestContainer;
import home.simple_user_api.commons.ApiException;
import home.simple_user_api.items.domain.ItemRepository;
import home.simple_user_api.users.dtos.exceptions.InvalidLoginOrPasswordException;
import home.simple_user_api.users.dtos.exceptions.UserAlreadyExistsException;
import home.simple_user_api.users.dtos.requests.AuthenticationRequest;
import home.simple_user_api.users.dtos.requests.RegistrationRequest;
import home.simple_user_api.users.dtos.responses.JwtResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserAuthFacadeTest {
    @Autowired
    private UserAuthFacade userAuthFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        MySQLContainer<?> mySQLContainer = MySQLTestContainer.getInstance();
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", mySQLContainer::getDriverClassName);
    }

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void shouldRegisterUserWithValidRegistrationRequest() {
        // given
        RegistrationRequest registrationRequest = thereIsValidRegistrationRequest();

        // when
        userAuthFacade.register(registrationRequest);

        // then
        Assertions.assertThat(userRepository.existsByLogin(registrationRequest.login())).isTrue();
    }

    @Test
    public void shouldNotRegisterUserWithAlreadyExistingLogin() {
        // given
        thereIsUser();
        String expectedExceptionMessage = "User already exists exception";
        RegistrationRequest registrationRequest = thereIsValidRegistrationRequest();

        // when
        ApiException exception = org.junit.jupiter.api.Assertions.assertThrows(UserAlreadyExistsException.class, () -> userAuthFacade.register(registrationRequest));

        // then
        Assertions.assertThat(exception.getErrorResponseCode().getMessage()).isEqualTo(expectedExceptionMessage);
    }

    @Test
    public void shouldAuthenticateUserWithValidAuthenticationRequest() {
        // given
        thereIsUser();
        AuthenticationRequest authenticationRequest = thereIsValidAuthenticationRequest();

        // when
        JwtResponse jwtResponse = userAuthFacade.authenticate(authenticationRequest);

        // then
        Assertions.assertThat(jwtResponse).isNotNull();
        Assertions.assertThat(jwtResponse.token()).isNotBlank();
    }

    @Test
    public void shouldNotAuthenticateUserWithWrongPassword() {
        // given
        thereIsUser();
        String expectedExceptionMessage = "Invalid username or password exception";
        AuthenticationRequest authenticationRequest = thereIsValidAuthenticationRequestWithWrongPassword();

        // when
        Assertions.assertThat(userRepository.existsByLogin(authenticationRequest.login())).isTrue();
        ApiException exception = org.junit.jupiter.api.Assertions.assertThrows(InvalidLoginOrPasswordException.class, () -> userAuthFacade.authenticate(authenticationRequest));

        // then
        Assertions.assertThat(exception.getErrorResponseCode().getMessage()).isEqualTo(expectedExceptionMessage);
    }

    private RegistrationRequest thereIsValidRegistrationRequest() {
        return new RegistrationRequest("test-user", "test-password");
    }

    private AuthenticationRequest thereIsValidAuthenticationRequest() {
        return new AuthenticationRequest("test-user", "test-password");
    }

    private AuthenticationRequest thereIsValidAuthenticationRequestWithWrongPassword() {
        return new AuthenticationRequest("test-user", "test-password123");
    }

    private void thereIsUser() {
        String encodedPassword = passwordEncoder.encode("test-password");
        userRepository.save(new User("test-user", encodedPassword));
    }
}
