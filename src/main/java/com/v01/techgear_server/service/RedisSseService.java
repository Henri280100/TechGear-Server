package com.v01.techgear_server.service;

import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

@Service
public class RedisSseService implements MessageListener {

    private final CopyOnWriteArrayList<FluxSink<String>> clients = new CopyOnWriteArrayList<>();

    public Flux<String> subscribeToRedisEvents() {
        return Flux.create(sink -> {
            clients.add(sink);
            sink.onCancel(() -> clients.remove(sink));
        });
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String event = new String(message.getBody());
        clients.forEach(client -> client.next(event));
    }

}
