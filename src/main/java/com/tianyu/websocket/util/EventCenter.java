package com.tianyu.websocket.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Observer;

public class EventCenter {

    private static EventCenter instance;

    private static final Map<String, Observer> center = new HashMap<>();

    public static EventCenter getInstance() {
        if (instance == null) {
            instance = new EventCenter();
        }
        return instance;
    }

    public void on() {

    }

    public void off() {

    }

    public void ntf() {

    }
}
