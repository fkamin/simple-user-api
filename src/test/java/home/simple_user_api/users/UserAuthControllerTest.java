package home.simple_user_api.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.simple_user_api.IntegrationAndUnitTest;
import home.simple_user_api.MySQLTestContainer;
import home.simple_user_api.items.domain.ItemRepository;
import home.simple_user_api.users.domain.User;
import home.simple_user_api.users.domain.UserRepository;
import home.simple_user_api.users.dtos.requests.AuthenticationRequest;
import home.simple_user_api.users.dtos.requests.RegistrationRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationAndUnitTest
public class UserAuthControllerTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    public void shouldSuccessfullyRegisterUserWithValidRegisterRequest() throws Exception {
        // given
        RegistrationRequest registrationRequest = thereIsValidRegistrationRequest();

        // when
        mockMvc.perform(
                        post("/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registrationRequest))
                )
                .andExpect(status().isNoContent()).andReturn();

        // then
        Boolean savedUser = userRepository.existsByLogin(registrationRequest.login());
        Assertions.assertThat(savedUser).isTrue();
    }

    @Test
    public void shouldNotRegisterUserWhenUserWithThatLoginAlreadyExists() throws Exception {
        // given
        RegistrationRequest registrationRequest = thereIsValidRegistrationRequest();
        thereIsUser();

        // when then
        mockMvc.perform(
                        post("/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registrationRequest))
                )
                .andExpect(status().isConflict());
    }

    @Test
    public void shouldNotRegisterUserWithInvalidRegistrationRequest() throws Exception {
        // given
        RegistrationRequest registrationRequest = thereIsInvalidRegistrationRequest();

        // when
        mockMvc.perform(
                        post("/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registrationRequest))
                )
                .andExpect(status().isBadRequest());

        // then
        Boolean savedUser = userRepository.existsByLogin(registrationRequest.login());
        Assertions.assertThat(savedUser).isFalse();
    }

    @Test
    public void shouldSuccessfullyAuthenticateUserAndReturnToken() throws Exception {
        // given
        AuthenticationRequest authenticationRequest = thereIsValidAuthenticationRequest();
        thereIsUser();

        // when
        MvcResult mvcResult = mockMvc.perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(authenticationRequest))
                )
                .andExpect(status().isOk()).andReturn();

        // then
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        Assertions.assertThat(jsonResponse).isNotEmpty();
    }

    @Test
    public void shouldNotAuthenticateUserDoesNotExists() throws Exception {
        // given
        AuthenticationRequest authenticationRequest = thereIsValidAuthenticationRequest();

        // when then
        mockMvc.perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(authenticationRequest))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldNotAuthenticateUserWithWrongPassword() throws Exception {
        // given
        AuthenticationRequest authenticationRequest = thereIsValidAuthenticationRequestWithWrongPassword();
        thereIsUser();

        // when then
        mockMvc.perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(authenticationRequest))
                )
                .andExpect(status().isUnauthorized());
    }

    private RegistrationRequest thereIsValidRegistrationRequest() {
        return new RegistrationRequest("test-user", "test-password");
    }

    private RegistrationRequest thereIsInvalidRegistrationRequest() {
        return new RegistrationRequest("test-user", "");
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
