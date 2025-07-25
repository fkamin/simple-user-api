package home.simple_user_api.items.domain;

import home.simple_user_api.users.domain.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ItemConfiguration {
    @Bean
    public ItemFacade itemFacade(ItemRepository itemRepository, UserRepository userRepository) {
        return new ItemFacade(itemRepository, userRepository);
    }
}
