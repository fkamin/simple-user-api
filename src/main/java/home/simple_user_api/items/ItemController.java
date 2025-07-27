package home.simple_user_api.items;

import home.simple_user_api.items.domain.ItemFacade;
import home.simple_user_api.items.dtos.requests.CreateItemRequest;
import home.simple_user_api.items.dtos.responses.ItemResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@SecurityRequirement(name = "Bearer Authentication")
public class ItemController {
    private final ItemFacade itemFacade;

    public ItemController(ItemFacade itemFacade) {
        this.itemFacade = itemFacade;
    }

    @PostMapping
    public ResponseEntity<String> createItem(@RequestBody @Valid CreateItemRequest createItemRequest, Authentication authentication) {
        String authName = authentication.getName();
        itemFacade.create(createItemRequest, authName);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ItemResponse>> getItems(Authentication authentication) {
        String authName = authentication.getName();
        return ResponseEntity.ok(itemFacade.getItems(authName));
    }
}
