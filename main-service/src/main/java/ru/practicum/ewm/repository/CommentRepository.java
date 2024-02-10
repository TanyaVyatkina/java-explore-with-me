package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.entity.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "comment_entity-graph")
    List<Comment> findByEvent_Id(Integer eventId);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "comment_entity-graph")
    List<Comment> findByEvent_IdIn(List<Integer> eventIds);
}
