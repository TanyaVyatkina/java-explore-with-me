package ru.practicum.ewm.stats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.stats.model.Endpoint;
import ru.practicum.ewm.stats.model.Stat;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Endpoint, Integer> {
    @Query("select new ru.practicum.ewm.stats.model.Stat(e.app, e.uri, count(distinct e.ip)) " +
            "from Endpoint as e " +
            "where e.dateTime BETWEEN :startDate AND :endDate " +
            "and e.uri in :uris group by e.app, e.uri order by count(distinct e.ip) desc")
    List<Stat> findUniqueStatWithGivenUris(@Param("startDate")LocalDateTime start, @Param("endDate") LocalDateTime end,
                                           @Param("uris") String[] uris);

    @Query("select new ru.practicum.ewm.stats.model.Stat(e.app, e.uri, count(e)) " +
            "from Endpoint as e " +
            "where e.dateTime BETWEEN :startDate AND :endDate " +
            "and e.uri in :uris group by e.app, e.uri order by count(e) desc")
    List<Stat> findNotUniqueStatWithGivenUris(@Param("startDate")LocalDateTime start, @Param("endDate") LocalDateTime end,
                                              @Param("uris") String[] uris);

    @Query("select new ru.practicum.ewm.stats.model.Stat(e.app, e.uri, count(distinct e.ip)) " +
            "from Endpoint as e " +
            "where e.dateTime BETWEEN :startDate AND :endDate " +
            "group by e.app, e.uri order by count(distinct e.ip) desc")
    List<Stat> findUniqueStat(@Param("startDate")LocalDateTime start, @Param("endDate") LocalDateTime end);

    @Query("select new ru.practicum.ewm.stats.model.Stat(e.app, e.uri, count(e)) " +
            "from Endpoint as e " +
            "where e.dateTime BETWEEN :startDate AND :endDate " +
            "group by e.app, e.uri order by count(e) desc")
    List<Stat> findNotUniqueStat(@Param("startDate")LocalDateTime start, @Param("endDate") LocalDateTime end);
}
