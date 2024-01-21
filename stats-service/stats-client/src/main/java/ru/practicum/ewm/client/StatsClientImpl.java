package ru.practicum.ewm.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.dto.EndpointHit;
import ru.practicum.ewm.dto.ViewStats;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StatsClientImpl implements StatsClient {
    private final RestTemplate rest;

    @Autowired
    public StatsClientImpl(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        this.rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public void saveEndpoint(EndpointHit endpointHit) {
        makeAndSendRequest(HttpMethod.POST, "/hit", null, endpointHit);
    }

    public List<ViewStats> findStatistic(ViewStatsRequest request) {
        LocalDateTime start = request.getStart();
        LocalDateTime end = request.getEnd();
        if (start == null || end == null) {
            throw new IllegalArgumentException("Не заданы даты поиска.");
        }

        Map<String, Object> parameters = new LinkedHashMap<>();

        parameters.put("start", encodeTime(start));
        parameters.put("end", encodeTime(end));
        parameters.put("uris", request.getUris());
        if (request.getUnique() != null) {
            parameters.put("unique", request.getUnique());
        }
        List<ViewStats> viewStats = (List<ViewStats>) makeAndSendRequest(HttpMethod.GET, "/stats",
                parameters, null).getBody();
        return viewStats;
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path,
                                                          @Nullable Map<String, Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<Object> statsServerResponse;
        try {
            if (parameters != null) {
                statsServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                statsServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (Exception e) {
            return ResponseEntity.of(Optional.of(e.getCause()));
        }
        return prepareGatewayResponse(statsServerResponse);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }

    private String encodeTime(LocalDateTime time) {
        return URLEncoder.encode(time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                StandardCharsets.UTF_8);
    }
}