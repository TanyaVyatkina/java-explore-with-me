package ru.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.entity.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Integer> {
    int countAllByCategory_Id(int catId);

    List<Event> findByInitiator_Id(Integer userId, Pageable page);

    Optional<Event> findByIdAndInitiator_Id(int id, int userId);

    List<Event> findByIdIn(List<Integer> ids);

    @Query("select e " +
            "from Event as e " +
            "where e.initiator.id in :users and e.state in :states and e.category.id in :categories " +
            "and e.eventDate between :rangeStart and :rangeEnd")
    List<Event> searchEventsByAdmin(@Param("users") int[] users, @Param("states") String[] states,
                                    @Param("categories") int[] categories, @Param("rangeStart") LocalDateTime rangeStart,
                                    @Param("rangeEnd") LocalDateTime rangeEnd, Pageable page);

    @Query("select e " +
            "from Event as e " +
            "where (lower(e.annotation) like lower(concat('%', :text,'%')) or lower(e.description) like lower(concat('%', :text,'%'))) " +
            "and e.category.id in :categories and e.paid = :paid " +
            "and e.eventDate between :rangeStart and :rangeEnd")
    List<Event> searchEventsWithDates(@Param("text") String text, @Param("categories") Integer[] categories,
                                      @Param("paid") Boolean paid, @Param("rangeStart") LocalDateTime rangeStart,
                                      @Param("rangeEnd") LocalDateTime rangeEnd, Pageable page);

    @Query("select e " +
            "from Event as e " +
            "where (lower(e.annotation) like lower(concat('%', :text,'%')) or lower(e.description) like lower(concat('%', :text,'%'))) " +
            "and e.category.id in :categories and e.paid = :paid and e.eventDate > :now")
    List<Event> searchFutureEvents(@Param("text") String text, @Param("categories") Integer[] categories,
                                   @Param("paid") Boolean paid, @Param("now") LocalDateTime now, Pageable page);

    @Query("select e " +
            "from Event as e " +
            "where (lower(e.annotation) like lower(concat('%', :text,'%')) or lower(e.description) like lower(concat('%', :text,'%'))) " +
            "and e.category.id in :categories and e.paid = :paid " +
            "and e.eventDate between :rangeStart and :rangeEnd " +
            "and e.participantLimit > (select count(p) from ParticipationRequest p where p.event.id = e.id and p.status = CONFIRMED)")
    List<Event> searchOnlyAvailableEventsWithDates(@Param("text") String text, @Param("categories") Integer[] categories,
                                                   @Param("paid") Boolean paid, @Param("rangeStart") LocalDateTime rangeStart,
                                                   @Param("rangeEnd") LocalDateTime rangeEnd, Pageable page);

    @Query("select e " +
            "from Event as e " +
            "where (lower(e.annotation) like lower(concat('%', :text,'%')) or lower(e.description) like lower(concat('%', :text,'%'))) " +
            "and e.category.id in :categories and e.paid = :paid and e.eventDate > :now " +
            "and e.participantLimit > (select count(p) from ParticipationRequest p where p.event.id = e.id and p.status = CONFIRMED)")
    List<Event> searchOnlyAvailableFutureEvents(@Param("text") String text, @Param("categories") Integer[] categories,
                                                @Param("paid") Boolean paid, @Param("now") LocalDateTime now, Pageable page);
}
