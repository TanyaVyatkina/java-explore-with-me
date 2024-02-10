package ru.practicum.ewm.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
@Getter
@Setter
@NamedEntityGraph(name = "comment_entity-graph", attributeNodes = @NamedAttributeNode("author"))
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String text;
    @ManyToOne(fetch = FetchType.LAZY)
    private Event event;
    @ManyToOne(fetch = FetchType.LAZY)
    private User author;
    private LocalDateTime created;
    private LocalDateTime updated;

}
