package com.tianyu.websocket.config;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@ServerEndpoint("/websocket/{uid}/{sid}")
public class WebSocketServer {

    /**
     * 在线人数
     */
    public static final AtomicInteger OnlineCount = new AtomicInteger();

    /**
     * 客户端池
     */
    public static final ConcurrentHashMap<Long, WebSocketClient> clients = new ConcurrentHashMap<>();

    private static final Gson GSON = new Gson();

    /**
     * 回调函数，当有新的客户端连接时调用
     *
     * @param session 会话
     * @param uid     用户ID
     * @param sid     客户端ID
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("uid") Long uid, @PathParam("sid") Long sid) {
        if (clients.containsKey(uid)) {
            log.error(String.format("用户ID: %d, 已登录, 无法重复登录", uid));
        } else {
            var client = new WebSocketClient(uid, sid, session);
            var count = OnlineCount.incrementAndGet();
            clients.put(uid, client);

            // 发送上线信息
            String msg = String.format("用户ID: %d, 设备ID: %d, 连接成功, 当前在线人数: %d", uid, sid, count);
            var message = new WebSocketMessage("online", msg);
            broadcast(message);

            log.info(msg);
        }
    }

    /**
     * @param uid 用户ID
     * @param sid 客户端ID
     */
    @OnClose
    public void onClose(@PathParam("uid") Long uid, @PathParam("sid") Long sid) {
        if (clients.containsKey(uid)) {
            clients.remove(uid);
            var count = OnlineCount.decrementAndGet();

            String msg = String.format("用户ID: %d, 设备ID: %d, 已断开连接, 当前在线人数: %d", uid, sid, count);
            var message = new WebSocketMessage("offline", msg);
            broadcast(message);

            log.info(msg);
        }
    }

    @OnError
    public void onError(Throwable error, @PathParam("uid") Long uid, @PathParam("sid") Long sid) {
        log.error(String.format("用户ID: %d, 设备ID: %d, 发生异常, 异常信息: %s", uid, sid, error.getMessage()));
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("uid") Long uid, @PathParam("sid") Long sid) {
        String msg = String.format("用户ID: %d, 设备ID: %d, 已接受消息: %s", uid, sid, message);

        var m = new WebSocketMessage("message", "已接受消息", msg);
        send(uid, m);

        // TODO: notify events

        log.info(msg);
    }

    /**
     * 广播群发消息
     *
     * @param message 消息内容
     */
    public void broadcast(WebSocketMessage message) {
        clients.forEach((uid, client) -> {
            try {
                client.getSession().getBasicRemote().sendText(GSON.toJson(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 点对点发送消息
     *
     * @param uid     用户ID
     * @param message 消息内容
     */
    public void send(Long uid, WebSocketMessage message) {
        if (clients.containsKey(uid)) {
            var client = clients.get(uid);
            try {
                client.getSession().getBasicRemote().sendText(GSON.toJson(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
