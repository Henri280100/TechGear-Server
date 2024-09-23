package com.v01.techgear_server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendProgressUpdate(String taskId, String message) {
        messagingTemplate.convertAndSend("/topic/progress" + taskId, message);
    }
}