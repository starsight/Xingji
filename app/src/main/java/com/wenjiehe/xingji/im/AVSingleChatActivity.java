package com.wenjiehe.xingji.Im;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.avos.avoscloud.LogUtil;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.avos.avoscloud.im.v2.callback.AVIMSingleMessageQueryCallback;
import com.wenjiehe.xingji.Activity.MainActivity;
import com.wenjiehe.xingji.Activity.MyHistorySignActivity;
import com.wenjiehe.xingji.ChatInfo;
import com.wenjiehe.xingji.R;
import com.wenjiehe.xingji.SignInfo;
import com.yuyh.library.imgsel.utils.StatusBarCompat;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;

import static com.wenjiehe.xingji.Activity.MainActivity.listChatList;
import static com.wenjiehe.xingji.R.id.refresh;

/**
 * Created by wli on 15/8/14.
 * 一对一单聊的页面，需要传入 Constants.MEMBER_ID
 */
public class AVSingleChatActivity extends com.wenjiehe.xingji.Im.AVBaseActivity {

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;

    protected com.wenjiehe.xingji.Im.ChatFragment chatFragment;

    String memberId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_square);
        //透明状态栏
        if (Build.VERSION.SDK_INT >= 21) {
            StatusBarCompat.compat(this, getResources().getColor(R.color.colorPrimary));
        }

        chatFragment = (com.wenjiehe.xingji.Im.ChatFragment) getFragmentManager().findFragmentById(R.id.fragment_chat);


        setSupportActionBar(toolbar);
        //toolbar.setNavigationIcon(R.drawable.btn_navigation_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        memberId = getIntent().getStringExtra(com.wenjiehe.xingji.Im.Constants.MEMBER_ID);
        setTitle(memberId);
        getConversation(memberId);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (null != extras && extras.containsKey(com.wenjiehe.xingji.Im.Constants.MEMBER_ID)) {
            String memberId = extras.getString(com.wenjiehe.xingji.Im.Constants.MEMBER_ID);
            setTitle(memberId);
            getConversation(memberId);
        }
    }

    /**
     * 获取 conversation，为了避免重复的创建，此处先 query 是否已经存在只包含该 member 的 conversation
     * 如果存在，则直接赋值给 ChatFragment，否者创建后再赋值
     */
    private void getConversation(final String memberId) {
        //Log.d("999999",memberId);
        final AVIMClient client = com.wenjiehe.xingji.Im.AVImClientManager.getInstance().getClient();
        AVIMConversationQuery conversationQuery = client.getQuery();
        conversationQuery.withMembers(Arrays.asList(memberId), true);
        conversationQuery.whereEqualTo("customConversationType", 1);
        conversationQuery.findInBackground(new AVIMConversationQueryCallback() {
            @Override
            public void done(List<AVIMConversation> list, AVIMException e) {
                if (filterException(e)) {
                    //注意：此处仍有漏洞，如果获取了多个 conversation，默认取第一个
                    if (null != list && list.size() > 0) {
                        chatFragment.setConversation(list.get(0));
                    } else {
                        HashMap<String, Object> attributes = new HashMap<String, Object>();
                        attributes.put("customConversationType", 1);
                        client.createConversation(Arrays.asList(memberId), null, attributes, false, new AVIMConversationCreatedCallback() {
                            @Override
                            public void done(AVIMConversation avimConversation, AVIMException e) {
                                chatFragment.setConversation(avimConversation);
                            }
                        });
                    }
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
