package ru.practicum.ewm.stats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.dto.ViewStats;
import ru.practicum.ewm.stats.model.Endpoint;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Endpoint, Integer> {
    @Query("select new ru.practicum.ewm.dto.ViewStats(e.app, e.uri, count(distinct e.ip)) " +
            "from Endpoint as e " +
            "where e.dateTime BETWEEN :startDate AND :endDate " +
            "and e.uri in :uris group by e.app, e.uri order by count(distinct e.ip) desc")
    List<ViewStats> findUniqueStatWithGivenUris(@Param("startDate") LocalDateTime start, @Param("endDate") LocalDateTime end, @Param("uris") String[] uris);

    @Query("select new ru.practicum.ewm.dto.ViewStats(e.app, e.uri, count(e)) " +
            "from Endpoint as e " +
            "where e.dateTime BETWEEN :startDate AND :endDate " +
            "and e.uri in :uris group by e.app, e.uri order by count(e) desc")
    List<ViewStats> findNotUniqueStatWithGivenUris(@Param("startDate") LocalDateTime start, @Param("endDate") LocalDateTime end, @Param("uris") String[] uris);

    @Query("select new ru.practicum.ewm.dto.ViewStats(e.app, e.uri, count(distinct e.ip)) " +
            "from Endpoint as e " +
            "where e.dateTime BETWEEN :startDate AND :endDate " +
            "group by e.app, e.uri order by count(distinct e.ip) desc")
    List<ViewStats> findUniqueStat(@Param("startDate") LocalDateTime start, @Param("endDate") LocalDateTime end);

    @Query("select new ru.practicum.ewm.dto.ViewStats(e.app, e.uri, count(e)) " +
            "from Endpoint as e " +
            "where e.dateTime BETWEEN :startDate AND :endDate " +
            "group by e.app, e.uri order by count(e) desc")
    List<ViewStats> findNotUniqueStat(@Param("startDate") LocalDateTime start, @Param("endDate") LocalDateTime end);
}
