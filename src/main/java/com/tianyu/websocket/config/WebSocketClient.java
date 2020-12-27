package com.tianyu.websocket.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.websocket.Session;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketClient {

    private Long uid;
    private Long sid;
    private Session session;

}
