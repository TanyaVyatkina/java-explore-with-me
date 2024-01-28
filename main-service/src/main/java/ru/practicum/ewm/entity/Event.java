package ru.practicum.ewm.entity;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.dto.EventState;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "event")
@Getter
@Setter
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String annotation;
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    private String description;
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    @ManyToOne(fetch = FetchType.LAZY)
    private User initiator;
    private float lat;
    private float lon;
    private boolean paid;
    @Column(name = "participant_limit")
    private int participantLimit;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
    @Column(name = "request_moderation")
    private boolean requestModeration;
    @Enumerated(EnumType.STRING)
    private EventState state;
    private String title;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="compilation_event",
            joinColumns=  @JoinColumn(name="event_id", referencedColumnName="id"),
            inverseJoinColumns= @JoinColumn(name="compilation_id", referencedColumnName="id"))
    private List<Compilation> compilations;
}
