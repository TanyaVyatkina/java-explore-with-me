package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ParticipationStat {
    private int eventId;
    private long requestCount;
}
