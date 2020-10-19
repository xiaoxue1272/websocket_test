package com.test.controller;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.awt.image.VolatileImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author gyj
 * @date 2020年10月19日14点
 * @description
 */
@Component
@ServerEndpoint("/websocketTest")
public class WebsocketTest {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    private static volatile List<Session> sessionsArrs = new ArrayList<Session>();


    // 建立连接
    @OnOpen
    public void onOpen(Session session){
        System.out.println("建立连接");
        String id = session.getId();
        System.out.println("sessionId:"+id);
        sessionsArrs.add(session);
        sendMessage(session,"已连接");
    }


    // 关闭连接
    @OnClose
    public void onClose(Session session){
        String id = session.getId();
        System.out.println("Connection is close,sessionId:"+id);
        if (sessionsArrs.contains(session)){
            sessionsArrs.remove(session);
        }
        for (int i = 0; i < sessionsArrs.size(); i++) {
            Session nowSession = sessionsArrs.get(i);
            if (nowSession.equals(session))
                 continue;
            RemoteEndpoint.Basic basicRemote = session.getBasicRemote();
            try {
                basicRemote.sendText(DATE_FORMAT.format(new Date())+",sessionId:"+nowSession.getId()+",消息:"+"断开连接");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 收到消息
    @OnMessage
    public void onMessage(String message,Session nowSession){
        System.out.println("收到消息:"+message);
        sendMessage(nowSession,message);
    }

    public void sendMessage(Session nowSession,String text){
        for (int i = 0; i < sessionsArrs.size(); i++) {
            Session session = sessionsArrs.get(i);
            RemoteEndpoint.Basic basicRemote = session.getBasicRemote();
            try {
                basicRemote.sendText(DATE_FORMAT.format(new Date())+",sessionId:"+nowSession.getId()+",消息:"+text);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
