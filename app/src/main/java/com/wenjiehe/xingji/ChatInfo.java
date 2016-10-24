package com.wenjiehe.xingji;

import com.avos.avoscloud.im.v2.AVIMConversation;

import java.util.Date;
import java.util.List;

/**
 * Created by yiyuan on 2016/10/24.
 */

public class ChatInfo {
    String lastMessageFrom;
    String lastMessage;
    List<String> persons;
    Date lastTime;

     public ChatInfo(String lastMessageFrom, String lastMessage, List<String> persons, Date lastTime) {
        this.lastMessageFrom = lastMessageFrom;
        this.lastMessage = lastMessage;
        this.persons = persons;
        this.lastTime = lastTime;
    }
}
