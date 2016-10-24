package com.wenjiehe.xingji.Activity;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

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
import com.yuyh.library.imgsel.utils.StatusBarCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.wenjiehe.xingji.R.id.toolbar;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView activity_chat_recycler;
    private ChatRecyclerViewAdapter adapter;

    private ArrayList<ChatInfo> listChatList;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //透明状态栏
        if (Build.VERSION.SDK_INT >= 21) {
            StatusBarCompat.compat(this, getResources().getColor(R.color.colorPrimary));
        }
        toolbar = (Toolbar) findViewById(R.id.activity_chat_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        activity_chat_recycler = (RecyclerView) findViewById(R.id.activity_chat_recycler);
        listChatList = MainActivity.listChatList;

        adapter = new ChatRecyclerViewAdapter(listChatList, ChatActivity.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        activity_chat_recycler.setAdapter(adapter);
        activity_chat_recycler.setHasFixedSize(true);
        activity_chat_recycler.setLayoutManager(layoutManager);
        activity_chat_recycler.setItemAnimator(new DefaultItemAnimator());

        //Log.d("2333", String.valueOf(listChatList.size()));
    }


}
