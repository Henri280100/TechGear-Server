package com.v01.techgear_server.controller.SSE;

import java.time.Duration;
import java.time.LocalTime;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
@RestController
public class ReactiveSseController {

    //Allows non-blocking I/O to serve many clients efficiently.
    @GetMapping(value = "/api/reactive-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamEvents() {
        return Flux.interval(Duration.ofSeconds(1)) // Produces an infinite stream of events, sending data every second
                .map(sequence -> "Real-time event: " + LocalTime.now())
                .take(20); // Send only 20 events
    }
}
