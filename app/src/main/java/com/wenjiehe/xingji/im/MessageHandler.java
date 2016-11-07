package com.wenjiehe.xingji.Im;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessageHandler;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.wenjiehe.xingji.Activity.MainActivity;
import com.wenjiehe.xingji.R;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

import static android.media.CamcorderProfile.get;
import static com.xiaomi.push.service.z.s;

/**
 * Created by zhangxiaobo on 15/4/20.
 */
public class MessageHandler extends AVIMTypedMessageHandler<AVIMTypedMessage> {

    private Context context;

    public MessageHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onMessage(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
        //Log.d("2333",message.getFrom());
        final AVIMClient mclient =client;
        final AVIMTypedMessage mmessage =message;
        //final AVIMClient client2 = AVImClientManager.getInstance().getClient();
        AVIMConversationQuery conversationQuery = client.getQuery();
        conversationQuery.withMembers(Arrays.asList(message.getFrom()), true);
        conversationQuery.whereEqualTo("customConversationType", 1);
        conversationQuery.findInBackground(new AVIMConversationQueryCallback() {
            @Override
            public void done(List<AVIMConversation> list, AVIMException e) {
                if (e==null) {
                    //注意：此处仍有漏洞，如果获取了多个 conversation，默认取第一个
                    if (null != list && list.size() > 0) {
                        AVIMConversation  mconversation =list.get(0);

                        String clientID = "";
                        try {
                            clientID = AVImClientManager.getInstance().getClientId();
                            if (mclient.getClientId().equals(clientID)) {
                                // 过滤掉自己发的消息
                                if (!mmessage.getFrom().equals(clientID)) {
                                    sendEvent(mmessage, mconversation);
                                    if (NotificationUtils.isShowNotification(mconversation.getConversationId())) {
                                        sendNotification(mmessage, mconversation);
                                    }

                                }
                            } else {
                                mclient.close(null);
                            }
                        } catch (IllegalStateException ee) {
                            mclient.close(null);
                        }
                    }
                }
            }
        });


    }

    /**
     * 因为没有 db，所以暂时先把消息广播出去，由接收方自己处理
     * 稍后应该加入 db
     *
     * @param message
     * @param conversation
     */
    private void sendEvent(AVIMTypedMessage message, AVIMConversation conversation) {
        ImTypeMessageEvent event = new ImTypeMessageEvent();
        event.message = message;
        event.conversation = conversation;
        EventBus.getDefault().post(event);
    }

    private void sendNotification(AVIMTypedMessage message, AVIMConversation conversation) {
        String notificationContent = message instanceof AVIMTextMessage ?
                ((AVIMTextMessage) message).getText() : context.getString(R.string.unspport_message_type);
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constants.CONVERSATION_ID, conversation.getConversationId());
        intent.putExtra(Constants.MEMBER_ID, message.getFrom());
        List<String> l = conversation.getMembers();
        String tittle = "消息";
        for (String s : l) {
           // Log.d("messagess",s);
            if (s.equals(message.getFrom()))
                tittle = s;
        }
        NotificationUtils.showNotification(context, tittle, notificationContent, null, intent);
    }
}
