package com.jiawa.wiki.service.impl;

import com.jiawa.wiki.service.WsService;
import com.jiawa.wiki.websocket.WebSocketServer;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author SongShengLin
 * @date 2022/5/28 14:48
 * @description
 */
@Service
public class WsServiceImpl implements WsService {

    @Resource
    private WebSocketServer webSocketServer;

    /**
     * Async异步化：不能在同一个类内，方法A对方法B调用，这是因为代理类相同无法生效
     * 必须另起一个新的类加上Async
     *
     * @param message 发送的消息
     * @param logId   流水号
     */
    @Override
    public void sendInfo(String message, String logId) {
//        MDC.put("LOG_ID", logId);
//        webSocketServer.sendInfo(message);

        // 坑：implements的实现类中的继承方法，是不能直接@Async，会导致不生效
        // 想让异步生效，另起一个方法
        sendMessageInfo(message, logId);
    }

    @Async
    public void sendMessageInfo(String message, String logId) {
        MDC.put("LOG_ID", logId);

        webSocketServer.sendInfo(message);
    }

}
