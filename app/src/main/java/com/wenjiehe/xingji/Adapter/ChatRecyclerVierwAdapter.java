package com.wenjiehe.xingji.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wenjiehe.xingji.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.baidu.location.h.a.i;

/**
 * Created by yiyuan on 2016/10/23.
 */

public class ChatRecyclerVierwAdapter extends RecyclerView.Adapter<MomentsRecyclerViewAdapter.MomentsViewHolder> {
    private Context context;
    private ArrayList<JSONObject> data;

    public ChatRecyclerVierwAdapter(ArrayList<JSONObject> mData, Context mContext) {
        this.data = mData;
        this.context = mContext;
    }

    @Override
    public MomentsRecyclerViewAdapter.MomentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recyclerview_my_moments, parent, false);

        MomentsRecyclerViewAdapter.MomentsViewHolder nvh = new MomentsRecyclerViewAdapter.MomentsViewHolder(v, i);
        return nvh;
    }

    @Override
    public void onBindViewHolder(MomentsRecyclerViewAdapter.MomentsViewHolder holder, int position) {
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