package ru.practicum.ewm.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class EndpointHit {
    private Integer id;
    @NonNull
    private String app;
    @NonNull
    private String uri;
    @NonNull
    private String ip;
    @NonNull
    private String timestamp;
}
