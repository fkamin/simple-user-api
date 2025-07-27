package home.simple_user_api.items.domain;

import home.simple_user_api.items.dtos.requests.CreateItemRequest;
import home.simple_user_api.items.dtos.responses.ItemResponse;
import home.simple_user_api.users.domain.User;
import home.simple_user_api.users.domain.UserRepository;
import home.simple_user_api.users.dtos.exceptions.UserDoesNotExistsException;

import java.util.List;

public class ItemFacade {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemFacade(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public void create(CreateItemRequest createItemRequest, String authUsername) {
        User user = getLoggedInUser(authUsername);

        Item itemToSave = new Item(createItemRequest.name(), user);

        itemRepository.save(itemToSave);
    }

    public List<ItemResponse> getItems(String authUsername) {
        User user = getLoggedInUser(authUsername);

        return itemRepository.findAllByOwner(user).stream().map(Item::toItemResponse).toList();
    }

    private User getLoggedInUser(String login) {
        return userRepository.findByLogin(login).orElseThrow(UserDoesNotExistsException::new);
    }
}
