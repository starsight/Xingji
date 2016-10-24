package com.wenjiehe.xingji.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.wenjiehe.xingji.Adapter.ChatRecyclerViewAdapter;
import com.wenjiehe.xingji.Adapter.MomentsRecyclerViewAdapter;
import com.wenjiehe.xingji.Adapter.RecyclerViewAdapter;
import com.wenjiehe.xingji.ChatInfo;
import com.wenjiehe.xingji.R;

import java.util.Arrays;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView activity_chat_recycler;
    private ChatRecyclerViewAdapter adapter;

    private List<ChatInfo> listChatList = MainActivity.listChatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        activity_chat_recycler = (RecyclerView) findViewById(R.id.activity_chat_recycler);
        adapter = new ChatRecyclerViewAdapter(listChatList, ChatActivity.this);

    }


}
