package com.wenjiehe.xingji.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.avos.avoscloud.AVUser;
import com.wenjiehe.xingji.ChatInfo;
import com.wenjiehe.xingji.Im.AVSingleChatActivity;
import com.wenjiehe.xingji.Im.Constants;
import com.wenjiehe.xingji.R;
import com.wenjiehe.xingji.Util;
import java.util.ArrayList;
import java.util.List;
import static com.baidu.location.h.a.i;
import static com.wenjiehe.xingji.Util.hasFile;

/**
 * Created by yiyuan on 2016/10/23.
 */

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.ChatViewHolder> {
    private Context context;
    private ArrayList<ChatInfo> data;
    private AdapterView.OnItemClickListener clickListener;

    //自定义ViewHolder类
    static class ChatViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_chat_icon;
        TextView tv_user_chat;
        TextView tv_content_chat;
        CardView chat_card_view;

        public ChatViewHolder(final View itemView) {
            super(itemView);
            iv_chat_icon = (ImageView) itemView.findViewById(R.id.iv_chat_icon);
            tv_user_chat = (TextView) itemView.findViewById(R.id.tv_user_chat);
            tv_content_chat = (TextView) itemView.findViewById(R.id.tv_content_chat);
            chat_card_view = (CardView) itemView.findViewById(R.id.chat_card_view);
        }
    }

    public ChatRecyclerViewAdapter(ArrayList<ChatInfo> mData, Context mContext) {
        this.data = mData;
        this.context = mContext;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recyclerview_chat_list, parent, false);

        ChatViewHolder nvh = new ChatViewHolder(v);
        return nvh;
    }

    @Override
    public void onBindViewHolder(ChatRecyclerViewAdapter.ChatViewHolder holder, int position) {
        final ChatViewHolder holdertmp = holder;
        List<String> l = data.get(position).getPersons();
        String user=AVUser.getCurrentUser().getUsername();
        for(String str:l){
            if(!str.equals(AVUser.getCurrentUser().getUsername()))
                user = str;
        }
        final String username = user;
        holder.tv_user_chat.setText(user);
        com.alibaba.fastjson.JSONObject json =JSON.parseObject(data.get(position).getLastMessage());
        holder.tv_content_chat.setText(json.getString("_lctext"));

        String str2 = Environment.getExternalStorageDirectory() + "/xingji/" +
                AVUser.getCurrentUser().getUsername() + "/Chats/" + user;
        if (hasFile(str2)) {
            Bitmap b = Util.file2bitmap(str2);
            holder.iv_chat_icon.setImageBitmap(b);
        } else
            holder.iv_chat_icon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon));

        /*holder.chat_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Intent = new Intent(context, AVSingleChatActivity.class);
                Intent.putExtra(Constants.MEMBER_ID, username);
                context.startActivity(Intent);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public void addItem(ChatInfo ci, int position) {

        data.add(position, ci);
        notifyItemInserted(position); //Attention!
    }

    public void removeItem(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }
}