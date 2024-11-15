package com.sky.task;

import com.sky.webSocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class WebSocketTask {
    private final WebSocketServer webSocketServer;

    @Autowired
    public WebSocketTask(WebSocketServer webSocketServer) {
        this.webSocketServer = webSocketServer;
    }

    /**
     * 此方法用于：通过 WebSocket 每隔 5 秒向客户端发送消息
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void sendMessageToClient() {
        webSocketServer.sendToAllClient("这是来自服务端的消息：" + DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now()));
    }
}
