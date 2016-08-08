package com.wenjiehe.xingji;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wenjiehe.xingji.Activity.MainActivity;
import com.wenjiehe.xingji.Activity.SignInfoDetailActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenjie on 16/08/07.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.NewsViewHolder>{

    final String TAG = "RecyclerViewAdapter";
    private List<SignInfo> signInfo;
    private Context context;

    public RecyclerViewAdapter(List<SignInfo> signInfo,Context context) {
        this.signInfo = signInfo;
        this.context=context;
    }


    //自定义ViewHolder类
    static class NewsViewHolder extends RecyclerView.ViewHolder{

        CardView cardView;
        ImageView news_photo;
        TextView news_title;
        TextView news_desc;
        Button share;
        Button readMore;

        public NewsViewHolder(final View itemView) {
            super(itemView);
            cardView= (CardView) itemView.findViewById(R.id.card_view);
            news_photo= (ImageView) itemView.findViewById(R.id.news_photo);
            news_title= (TextView) itemView.findViewById(R.id.news_title);
            news_desc= (TextView) itemView.findViewById(R.id.news_desc);
            share= (Button) itemView.findViewById(R.id.btn_share);
            readMore= (Button) itemView.findViewById(R.id.btn_more);
            //设置TextView背景为半透明
            news_title.setBackgroundColor(Color.argb(20, 0, 0, 0));

        }


    }
    @Override
    public RecyclerViewAdapter.NewsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v= LayoutInflater.from(context).inflate(R.layout.recyclerview_sign_item_info,viewGroup,false);
        NewsViewHolder nvh=new NewsViewHolder(v);
        return nvh;
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.NewsViewHolder personViewHolder, int i) {
        final int j=i;

        //personViewHolder.news_photo.setImageResource(signInfo.get(i).getPhotoId());
        personViewHolder.news_title.setText(signInfo.get(i).getLocation());
        personViewHolder.news_desc.setText(signInfo.get(i).getEvent());

        //为btn_share btn_readMore cardView设置点击事件
        personViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,SignInfoDetailActivity.class);
                intent.putExtra("SignInfo",signInfo.get(j));
                context.startActivity(intent);
            }
        });

        personViewHolder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                intent.putExtra(Intent.EXTRA_TEXT, signInfo.get(j).getEvent());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(Intent.createChooser(intent, signInfo.get(j).getLocation()));
            }
        });

        personViewHolder.readMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,SignInfoDetailActivity.class);
                intent.putExtra("SignInfo",signInfo.get(j));
                context.startActivity(intent);
            }
        });


    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return signInfo.size();
    }

    public void addItem(SignInfo si , int position) {
        Log.d(TAG,"add"+String.valueOf(position));
        signInfo.add(position, si);
        notifyItemInserted(position); //Attention!
    }
    public void removeItem(int position) {
        Log.d(TAG,"delete"+String.valueOf(position));
        signInfo.remove(position);
        notifyItemRemoved(position);
    }
/*
    public void updateRecyclerView(){
        Log.d(TAG,TAG);
        ArrayList<Integer> isInfoExist = new ArrayList();
        for (int i = 0; i < signInfo.size(); i++)
            isInfoExist.add(i, 0);
        String str;
        List<SignInfo> signInfoTmp = MainActivity.arraylistHistorySign;
        for (SignInfo si : signInfoTmp
             ) {
             str = si.getObjectId();
            for (int i = 0; i < signInfo.size(); i++){
                if(signInfo.get(i).getObjectId().equals(str)){
                    isInfoExist.set(i,1);
                    break;
                }else if (i == (signInfo.size() - 1)) {
                    if (!signInfo.get(i).getObjectId().equals(str)) {
                        isInfoExist.add(signInfo.size(), 1);
                        addItem(si,getItemCount());
                    } else
                        isInfoExist.set(i, 1);
                }

            }

        }
        for (int i = 0; i < isInfoExist.size(); i++) {
            if (isInfoExist.get(i) == 0) {
                Log.d(TAG, "delete" + String.valueOf(i));
                removeItem(i);
            }
        }
        //addItem(signinfo,getItemCount());
    }*/
}
