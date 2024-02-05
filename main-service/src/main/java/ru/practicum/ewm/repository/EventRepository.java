package ru.practicum.ewm.repository;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.entity.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Integer>, QuerydslPredicateExecutor<Event> {
    int countAllByCategory_Id(int catId);

    List<Event> findByInitiator_Id(Integer userId, Pageable page);

    Optional<Event> findByIdAndInitiator_Id(int id, int userId);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "event-graph")
    List<Event> findByIdIn(List<Integer> ids);

    @Override
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "event-graph")
    Page<Event> findAll(Pageable pageable);

    @Override
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "event-graph")
    Page<Event> findAll(Predicate predicate, Pageable pageable);
}
