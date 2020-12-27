package com.tianyu.websocket.config;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage {

    private String evt;
    private String msg;
    private String body;

    public WebSocketMessage(String evt, String msg) {
        this.evt = evt;
        this.msg = msg;
    }

}
