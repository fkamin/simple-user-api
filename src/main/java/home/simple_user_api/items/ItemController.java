package home.simple_user_api.items;

import home.simple_user_api.items.domain.ItemFacade;
import home.simple_user_api.items.dtos.requests.CreateItemRequest;
import home.simple_user_api.items.dtos.responses.ItemResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemFacade itemFacade;

    public ItemController(ItemFacade itemFacade) {
        this.itemFacade = itemFacade;
    }

    @PostMapping
    public ResponseEntity<ItemResponse> createItem(@RequestBody CreateItemRequest createItemRequest, Authentication authentication) {
        String authName = authentication.getName();
        ItemResponse itemResponse = itemFacade.create(createItemRequest, authName);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(itemResponse);
    }

    @GetMapping
    public ResponseEntity<List<ItemResponse>> getItems(Authentication authentication) {
        String authName = authentication.getName();
        return ResponseEntity.ok(itemFacade.getItems(authName));
    }
}
