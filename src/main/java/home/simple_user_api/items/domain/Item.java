package home.simple_user_api.items.domain;

import home.simple_user_api.items.dtos.responses.ItemResponse;
import home.simple_user_api.users.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue
    @JdbcTypeCode(Types.CHAR)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private User owner;

    public Item(String name, User owner) {
        this.name = name;
        this.owner = owner;
    }

    public ItemResponse toItemResponse() {
        return new ItemResponse(id, name);
    }
}
