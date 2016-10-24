package com.wenjiehe.xingji.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.avos.avoscloud.AVUser;
import com.wenjiehe.xingji.Activity.SignInfoDetailActivity;
import com.wenjiehe.xingji.R;
import com.wenjiehe.xingji.SignInfo;
import com.wenjiehe.xingji.ThumbUpView;
import com.wenjiehe.xingji.Util;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by wenjie on 16/08/07.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.NewsViewHolder> {

    final String TAG = "RecyclerViewAdapter";
    protected List<SignInfo> signInfo;
    protected Context context;

    public RecyclerViewAdapter() {

    }

    public RecyclerViewAdapter(List<SignInfo> signInfo, Context context) {
        this.signInfo = signInfo;
        this.context = context;
    }


    //自定义ViewHolder类
    static class NewsViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        ImageView news_photo;
        TextView news_title;
        TextView news_desc;
        Button share;
        Button readMore;
        ThumbUpView tpv;//喜欢按钮

        //add for moments
        TextView tv_news_owns;
        CircleImageView iv_news_userphoto;

        public NewsViewHolder(final View itemView, int type) {

            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            news_photo = (ImageView) itemView.findViewById(R.id.news_photo);
            news_title = (TextView) itemView.findViewById(R.id.news_title);
            news_desc = (TextView) itemView.findViewById(R.id.news_desc);
            share = (Button) itemView.findViewById(R.id.btn_share);
            readMore = (Button) itemView.findViewById(R.id.btn_more);
            //设置TextView背景为半透明
            news_title.setBackgroundColor(Color.argb(20, 0, 0, 0));

//add for moments
            tv_news_owns = (TextView) itemView.findViewById(R.id.tv_news_owns);
            iv_news_userphoto = (CircleImageView) itemView.findViewById(R.id.iv_news_userphoto);
            tpv = (ThumbUpView)itemView.findViewById(R.id.tpv);
            if (type == 1)
                news_photo.setVisibility(View.VISIBLE);
            else if (type == 0)
                news_title.setTextColor(Color.parseColor("#434343"));
            else if (type == 2) {//add for moments
                news_photo.setVisibility(View.VISIBLE);

                tv_news_owns.setVisibility(View.VISIBLE);
                iv_news_userphoto.setVisibility(View.VISIBLE);
                tpv.setVisibility(View.VISIBLE);
            } else if (type == 3) {//add for moments
                news_title.setTextColor(Color.parseColor("#434343"));

                tv_news_owns.setVisibility(View.VISIBLE);
                iv_news_userphoto.setVisibility(View.VISIBLE);
                tpv.setVisibility(View.VISIBLE);
            }

        }


    }

    @Override
    public RecyclerViewAdapter.NewsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.recyclerview_sign_item_info, viewGroup, false);

        NewsViewHolder nvh = new NewsViewHolder(v, i);
        return nvh;
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.NewsViewHolder personViewHolder, int i) {
        final int j = i;

        //personViewHolder.news_photo.setImageResource(signInfo.get(i).getPhotoId());
        personViewHolder.news_title.setText(signInfo.get(i).getLocation());
        personViewHolder.news_desc.setText(signInfo.get(i).getEvent());


        //为btn_share btn_readMore cardView设置点击事件
        personViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SignInfoDetailActivity.class);
                SignInfo s = signInfo.get(j);
                s.username = AVUser.getCurrentUser().getUsername();
                intent.putExtra("SignInfo", s);
                context.startActivity(intent);
            }
        });

       /*Log.d(TAG,signInfo.get(i).getPhotoId());
        if (!signInfo.get(i).getPhotoId().equals("0")) {
            personViewHolder.news_photo.setVisibility(View.VISIBLE);
            personViewHolder.news_photo.setImageBitmap(Util.file2bitmap(Environment.getExternalStorageDirectory()+
                    "/xingji/"+ AVUser.getCurrentUser().getUsername()+"/"+signInfo.get(j).getPhotoId()));
        }
        else//纯文本
            personViewHolder.news_title.setTextColor(Color.parseColor("#434343"));*/


        if (personViewHolder.news_photo.getVisibility() == View.VISIBLE)
            personViewHolder.news_photo.setImageBitmap(Util.file2bitmap(Environment.getExternalStorageDirectory() +
                    "/xingji/" + AVUser.getCurrentUser().getUsername() + "/Signs/" + signInfo.get(j).getPhotoId()));


        personViewHolder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                intent.putExtra(Intent.EXTRA_TEXT, "在" + signInfo.get(j).getLocation() + "签到\n" + signInfo.get(j).getEvent());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(Intent.createChooser(intent, signInfo.get(j).getLocation()));
            }
        });

        personViewHolder.readMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SignInfoDetailActivity.class);
                SignInfo s = signInfo.get(j);
                s.username = AVUser.getCurrentUser().getUsername();
                intent.putExtra("SignInfo", s);
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


    @Override
    public int getItemViewType(int position) {
        if (!signInfo.get(position).getPhotoId().equals("0")) {
            return 1;//有图
        } else {
            return 0;//无图
        }
    }

    public void addItem(SignInfo si, int position) {
        Log.d(TAG, "add" + String.valueOf(position));
        signInfo.add(position, si);
        notifyItemInserted(position); //Attention!
    }

    public void removeItem(int position) {
        Log.d(TAG, "delete" + String.valueOf(position));
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
