package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.dto.ParticipationRequestStatus;
import ru.practicum.ewm.dto.ParticipationStat;
import ru.practicum.ewm.entity.ParticipationRequest;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Integer> {
    List<ParticipationRequest> findByEvent_Id(int eventId);

    List<ParticipationRequest> findByRequester_Id(int userId);

    int countAllByRequester_IdAndEventId(int userId, int eventId);

    int countAllByRequester_IdAndEventIdAndStatus(int userId, int eventId, ParticipationRequestStatus status);

    List<ParticipationRequest> findByIdIn(List<Integer> ids);

    @Query("select new ru.practicum.ewm.dto.ParticipationStat(p.event.id, count(p)) " +
            "from ParticipationRequest as p " +
            "where p.event.id in :eventIds and p.status = :status group by p.event.id")
    List<ParticipationStat> findParticipationRequestStatistic(@Param("eventIds") List<Integer> eventIds,
                                                              @Param("status") ParticipationRequestStatus status);

}
