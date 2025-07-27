package home.simple_user_api.items;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import home.simple_user_api.MySQLTestContainer;
import home.simple_user_api.items.domain.Item;
import home.simple_user_api.items.domain.ItemRepository;
import home.simple_user_api.items.dtos.requests.CreateItemRequest;
import home.simple_user_api.items.dtos.responses.ItemResponse;
import home.simple_user_api.users.domain.User;
import home.simple_user_api.users.domain.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ItemControllerTest {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

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
    public void shouldCreateItemWhenUserIsLoggedIn() throws Exception {
        // given
        User user = thereIsUserWithoutItems();
        CreateItemRequest createItemRequest = thereIsValidCreateItemRequest();

        int startNumberOfUserItems = 0;
        int expectedNumberOfUserItems = startNumberOfUserItems + 1;
        String expectedItemName = "test-item-name";

        // when
        Assertions.assertThat(startNumberOfUserItems).isEqualTo(0);

        mockMvc.perform(
                post("/items")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject("test-user"))
                                .authorities(List.of(new SimpleGrantedAuthority("USER"))))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createItemRequest))
        ).andExpect(status().isNoContent());

        // then
        List<Item> userItems = itemRepository.findAllByOwner(user);
        int currentNumberOfUserItems = userItems.size();

        Assertions.assertThat(currentNumberOfUserItems).isEqualTo(expectedNumberOfUserItems);
        Assertions.assertThat(userItems.getFirst().getName()).isEqualTo(expectedItemName);
        Assertions.assertThat(userItems.getFirst().getOwner().getId()).isEqualTo(user.getId());
    }

    @Test
    public void shouldNotCreateLoggedInUserNewItemWithInvalidCreateItemRequest() throws Exception {
        // given
        thereIsUserWithoutItems();
        CreateItemRequest createItemRequest = thereIsInvalidCreateItemRequest();

        int startNumberOfUserItems = 0;
        int expectedNumberOfUserItems = 0;

        // when
        mockMvc.perform(
                post("/items")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject("test-user"))
                                .authorities(List.of(new SimpleGrantedAuthority("USER"))))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createItemRequest))
        ).andExpect(status().isBadRequest());

        // them
        Assertions.assertThat(startNumberOfUserItems).isEqualTo(expectedNumberOfUserItems);
    }

    @Test
    public void shouldReturnLoggedInUserItems() throws Exception {
        // given
        thereIsUserWithItems();
        int expectedNumberOfUserItems = 3;

        // when
        MvcResult mvcResult = mockMvc.perform(
                get("/items")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject("test-user"))
                                .authorities(List.of(new SimpleGrantedAuthority("USER"))))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        // them
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        List<ItemResponse> userItems = objectMapper.readValue(jsonResponse, new TypeReference<>() {
        });

        Assertions.assertThat(userItems.size()).isEqualTo(expectedNumberOfUserItems);
    }

    @Test
    public void shouldReturnLoggedInUserEmptyItemsList() throws Exception {
        // given
        thereIsUserWithoutItems();
        int expectedNumberOfUserItems = 0;

        // when
        MvcResult mvcResult = mockMvc.perform(
                get("/items")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject("test-user"))
                                .authorities(List.of(new SimpleGrantedAuthority("USER"))))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        // them
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        List<ItemResponse> userItems = objectMapper.readValue(jsonResponse, new TypeReference<>() {
        });

        Assertions.assertThat(userItems.size()).isEqualTo(expectedNumberOfUserItems);
    }

    @Test
    public void shouldNotCreateItemForAnonymousUser() throws Exception {
        // given
        thereIsUserWithoutItems();
        CreateItemRequest createItemRequest = thereIsInvalidCreateItemRequest();

        // when
        mockMvc.perform(
                post("/items")
                        .with(SecurityMockMvcRequestPostProcessors.anonymous())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createItemRequest))
        ).andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldNotReturnAnyItemForAnonymousUser() throws Exception {
        // given
        thereIsUserWithItems();

        // when
        mockMvc.perform(
                get("/items")
                        .with(SecurityMockMvcRequestPostProcessors.anonymous())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }

    private CreateItemRequest thereIsValidCreateItemRequest() {
        return new CreateItemRequest("test-item-name");
    }

    private CreateItemRequest thereIsInvalidCreateItemRequest() {
        return new CreateItemRequest("");
    }

    private User thereIsUserWithoutItems() {
        return userRepository.save(new User("test-user", "test-password"));
    }

    private void thereIsUserWithItems() {
        User user = thereIsUserWithoutItems();
        itemRepository.save(new Item("test-item-name-1", user));
        itemRepository.save(new Item("test-item-name-2", user));
        itemRepository.save(new Item("test-item-name-3", user));
    }
}
