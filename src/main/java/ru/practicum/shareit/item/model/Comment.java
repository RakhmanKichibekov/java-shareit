package ru.practicum.shareit.item.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    public Comment (String text) {
        this.text = text;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "text_comment")
    private String text;
    @ManyToOne(fetch=FetchType.EAGER,
            cascade=CascadeType.ALL)
    @JoinColumn(name="item_id")
    private Item item;
    @OneToOne(fetch=FetchType.EAGER,
            cascade=CascadeType.ALL)
    @JoinColumn(name="author_id")
    private User author;
    private LocalDateTime created;
}
