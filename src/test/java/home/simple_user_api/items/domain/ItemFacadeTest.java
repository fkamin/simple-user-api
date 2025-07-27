package home.simple_user_api.items.domain;

import home.simple_user_api.MySQLTestContainer;
import home.simple_user_api.items.dtos.requests.CreateItemRequest;
import home.simple_user_api.items.dtos.responses.ItemResponse;
import home.simple_user_api.users.domain.User;
import home.simple_user_api.users.domain.UserRepository;
import home.simple_user_api.users.dtos.exceptions.UserDoesNotExistsException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@Testcontainers
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ItemFacadeTest {
    @Autowired
    private ItemFacade itemFacade;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

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
    public void shouldCreateItem() {
        // given
        User user = thereIsUserWithName("test-user");
        CreateItemRequest createItemRequest = thereIsValidCreateItemRequest();

        int startNumberOfUserItems = 0;
        int expectedNumberOfUserItems = startNumberOfUserItems + 1;
        String expectedItemName = createItemRequest.name();

        // when
        itemFacade.create(createItemRequest, user.getLogin());

        // then
        List<Item> userItems = itemRepository.findAllByOwner(user);

        Assertions.assertThat(userItems).hasSize(expectedNumberOfUserItems);
        Assertions.assertThat(userItems.getFirst().getOwner().getId()).isEqualTo(user.getId());
        Assertions.assertThat(userItems.getFirst().getName()).isEqualTo(expectedItemName);
    }

    @Test
    public void shouldNotCreateItemWhenUserDoesNotExist() {
        // given
        CreateItemRequest createItemRequest = thereIsValidCreateItemRequest();

        // when then
        org.junit.jupiter.api.Assertions.assertThrows(UserDoesNotExistsException.class, () -> itemFacade.create(createItemRequest, "non-existent-user-login"));
    }

    @Test
    public void shouldReturnUserItems() {
        // given
        User user = thereIsUserWithName("test-user");
        User anotherUser = thereIsUserWithName("another-test-user");

        List<String> userItems = thereAreUserItems(user);
        thereAreUserItems(anotherUser);

        // when
        List<ItemResponse> actualUserItems = itemFacade.getItems(user.getLogin());

        // then
        List<String> actualUserItemsNames = actualUserItems.stream().map(ItemResponse::title).toList();

        Assertions.assertThat(userItems).containsOnly(actualUserItemsNames.toArray(String[]::new));
    }

    private User thereIsUserWithName(String name) {
        return userRepository.save(new User(name, "test-password"));
    }

    private CreateItemRequest thereIsValidCreateItemRequest() {
        return new CreateItemRequest("test-item-1");
    }

    private List<String> thereAreUserItems(User user) {
        var items = List.of(
                new Item("user-item-1", user),
                new Item("user-item-2", user),
                new Item("user-item-3", user));

        itemRepository.saveAll(items);

        return items.stream().map(Item::getName).toList();
    }
}
