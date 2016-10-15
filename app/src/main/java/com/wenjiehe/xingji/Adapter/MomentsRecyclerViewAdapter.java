package com.wenjiehe.xingji.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.avos.avoscloud.AVUser;
import com.wenjiehe.xingji.Activity.SignInfoDetailActivity;
import com.wenjiehe.xingji.R;
import com.wenjiehe.xingji.SignInfo;
import com.wenjiehe.xingji.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.attr.type;
import static com.baidu.location.h.a.i;
import static com.wenjiehe.xingji.R.id.iv_icon;
import static com.wenjiehe.xingji.R.id.iv_news_userphoto;
import static com.wenjiehe.xingji.R.id.news_desc;
import static com.wenjiehe.xingji.R.id.news_photo;
import static com.wenjiehe.xingji.R.id.news_title;
import static com.wenjiehe.xingji.R.id.tv_content_my_moments;
import static com.wenjiehe.xingji.R.id.tv_news_owns;
import static com.wenjiehe.xingji.Util.hasFile;

/**
 * Created by yiyuan on 2016/10/14.
 */

public class MomentsRecyclerViewAdapter extends RecyclerView.Adapter<MomentsRecyclerViewAdapter.MomentsViewHolder> {
    private Context context;
    private ArrayList<JSONObject> data;

    public MomentsRecyclerViewAdapter(ArrayList<JSONObject> mData, Context mContext) {
        this.data = mData;
        this.context = mContext;
    }

    @Override
    public MomentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recyclerview_my_moments, parent, false);

        MomentsViewHolder nvh = new MomentsViewHolder(v, i);
        return nvh;
    }

    @Override
    public void onBindViewHolder(MomentsViewHolder holder, int position) {
        int type = 0;
        String moments = null, objectid = null;
        try {
            type = data.get(position).getInt("type");
            moments = data.get(position).getString("info");
            //objectid = data.get(position).getString("objectid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (type == 0) {//签到于
            holder.iv_icon.setImageResource(R.drawable.sign);
            holder.tv_content_my_moments.setText("签到于 " + moments);
        } else if (type == 1) {//喜欢了
            holder.iv_icon.setImageResource(R.drawable.love);
            holder.tv_content_my_moments.setText(moments);
        } else if(type==2) {//被喜欢
            holder.iv_icon.setImageResource(R.drawable.love);
            holder.tv_content_my_moments.setText(moments);
        }

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    //自定义ViewHolder类
    static class MomentsViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_icon;
        TextView tv_content_my_moments;

        public MomentsViewHolder(final View itemView, int type) {
            super(itemView);
            iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
            tv_content_my_moments = (TextView) itemView.findViewById(R.id.tv_content_my_moments);
        }
    }

    public void addItem(JSONObject jo, int position) {

        data.add(position, jo);
        notifyItemInserted(position); //Attention!
    }

    public void removeItem(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }
}


