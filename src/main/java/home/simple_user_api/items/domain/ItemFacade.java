package home.simple_user_api.items.domain;

import home.simple_user_api.items.dtos.requests.CreateItemRequest;
import home.simple_user_api.items.dtos.responses.ItemResponse;
import home.simple_user_api.users.domain.UserRepository;

import java.util.List;

public class ItemFacade {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemFacade(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public ItemResponse create(CreateItemRequest createItemRequest, String authUsername) {
        return null;
    }

    public List<ItemResponse> getItems(String authUsername) {
        return null;
    }
}
