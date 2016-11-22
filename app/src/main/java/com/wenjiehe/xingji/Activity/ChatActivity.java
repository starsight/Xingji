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

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.avos.avoscloud.im.v2.callback.AVIMSingleMessageQueryCallback;
import com.wenjiehe.xingji.Adapter.ChatRecyclerViewAdapter;
import com.wenjiehe.xingji.ChatInfo;
import com.wenjiehe.xingji.Im.AVImClientManager;
import com.wenjiehe.xingji.R;
import com.wenjiehe.xingji.Util;
import com.yuyh.library.imgsel.utils.StatusBarCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.Observer;

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

    @Override
    protected void onRestart() {
        super.onRestart();
        AVImClientManager.getInstance().open(AVUser.getCurrentUser().getUsername(), new AVIMClientCallback() {
            @Override
            public void done(final AVIMClient avimClient, AVIMException e) {
                if (e == null) {
                    listChatList.clear();
                    //登录成功
                    AVIMConversationQuery query = avimClient.getQuery();
                    query.findInBackground(new AVIMConversationQueryCallback() {
                        @Override
                        public void done(List<AVIMConversation> convs, AVIMException e) {
                            if (e == null) {
                                if (convs != null) {
                                    AVIMClient.setMessageQueryCacheEnable(false);

                                    Observable.from(convs)
                                            .subscribe(new Observer<AVIMConversation>() {

                                                @Override
                                                public void onCompleted() {
                                                    Log.d("rxjava", "onCompleted");
                                                }

                                                @Override
                                                public void onError(Throwable e) {
                                                    Log.d("rxjava", "onError");
                                                }

                                                @Override
                                                public void onNext(AVIMConversation ac) {
                                                    final List<String> l = ac.getMembers();
                                                    final Date date = ac.getLastMessageAt();
                                                    String user = AVUser.getCurrentUser().getUsername();
                                                    for (String str : l) {
                                                        if (!str.equals(AVUser.getCurrentUser().getUsername()))
                                                            user = str;
                                                    }
                                                    ac.getLastMessage(new AVIMSingleMessageQueryCallback() {
                                                        @Override
                                                        public void done(AVIMMessage avimMessage, AVIMException e) {
                                                            if (avimMessage != null) {
                                                                String lastMessage = avimMessage.getContent();
                                                                String lastMessageFrom = avimMessage.getFrom();
                                                                ChatInfo chatinfo = new ChatInfo(lastMessageFrom, lastMessage, l, date);
                                                                listChatList.add(chatinfo);
                                                                Collections.sort(listChatList,ChatListComp);
                                                                //Log.d("rxjava",listChatList.toArray().toString());
                                                                adapter.notifyDataSetChanged();
                                                            }

                                                        }
                                                    });
                                                    Util.downloadPicture(user, "Chats");
                                                }
                                            });
//                                    for (AVIMConversation ac : convs) {
//                                        final List<String> l = ac.getMembers();
//                                        final Date date = ac.getLastMessageAt();
//                                        String user = AVUser.getCurrentUser().getUsername();
//                                        for (String str : l) {
//                                            if (!str.equals(AVUser.getCurrentUser().getUsername()))
//                                                user = str;
//                                        }
//                                        ac.getLastMessage(new AVIMSingleMessageQueryCallback() {
//                                            @Override
//                                            public void done(AVIMMessage avimMessage, AVIMException e) {
//                                                if (avimMessage != null) {
//                                                    String lastMessage = avimMessage.getContent();
//                                                    String lastMessageFrom = avimMessage.getFrom();
//                                                    ChatInfo chatinfo = new ChatInfo(lastMessageFrom, lastMessage, l, date);
//                                                    listChatList.add(chatinfo);
//                                                    adapter.notifyDataSetChanged();
//                                                }
//
//                                            }
//                                        });
//                                        Util.downloadPicture(user, "Chats");
//                                    }
                                }
                            }
                        }
                    });
                }

            }
        });
    }


    public static Comparator<ChatInfo> ChatListComp = new Comparator<ChatInfo>() {
        @Override
        public int compare(ChatInfo lhs, ChatInfo rhs) {
            return rhs.getLastTime().compareTo(lhs.getLastTime());
        }
    };
}
