package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.*;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    public Item (String name, String description, Boolean available){
        this.name = name;
        this.description = description;
        this.available = available;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @Column(name = "is_available", nullable = false)
    private Boolean available;
    @ManyToOne(fetch=FetchType.EAGER,
            cascade=CascadeType.ALL)
    @JoinColumn(name="owner_id")
    private User owner;
    @ManyToOne(fetch = FetchType.EAGER,
            cascade=CascadeType.ALL)
    @JoinColumn(name = "request_id")
    private ItemRequest request;

}