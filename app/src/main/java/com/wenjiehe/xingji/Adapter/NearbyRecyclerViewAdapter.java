package com.wenjiehe.xingji.Adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;

import com.avos.avoscloud.AVUser;
import com.wenjiehe.xingji.Activity.SignInfoDetailActivity;
import com.wenjiehe.xingji.SignInfo;
import com.wenjiehe.xingji.Util;

import java.util.List;

import static com.wenjiehe.xingji.Util.hasFile;

/**
 * Created by yiyuan on 2016/10/10.
 */

public class NearbyRecyclerViewAdapter extends RecyclerViewAdapter {

    public NearbyRecyclerViewAdapter(List<SignInfo> signInfo, Context context) {
        this.signInfo = signInfo;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (!signInfo.get(position).getPhotoId().equals("0")) {
            return 2;//有图 有头像
        } else {
            return 3;//无图 有头像
        }
    }



    @Override
    public void onBindViewHolder(RecyclerViewAdapter.NewsViewHolder personViewHolder, int i) {
        final int j = i;

        //personViewHolder.news_photo.setImageResource(signInfo.get(i).getPhotoId());
        personViewHolder.news_title.setText(signInfo.get(i).getLocation());
        personViewHolder.news_desc.setText(signInfo.get(i).getEvent());

        personViewHolder.tv_news_owns.setText(signInfo.get(i).username);
        String str = Environment.getExternalStorageDirectory() + "/xingji/" +
                AVUser.getCurrentUser().getUsername() + "/Moments/" + signInfo.get(i).username;
        if(hasFile(str)) {
            Bitmap b = Util.file2bitmap(str);
            personViewHolder.iv_news_userphoto.setImageBitmap(b);
        }
        //为btn_share btn_readMore cardView设置点击事件
        personViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SignInfoDetailActivity.class);
                intent.putExtra("SignInfo", signInfo.get(j));
                context.startActivity(intent);
            }
        });


        if (personViewHolder.news_photo.getVisibility() == View.VISIBLE)
            personViewHolder.news_photo.setImageBitmap(Util.file2bitmap(Environment.getExternalStorageDirectory() +
                    "/xingji/" + AVUser.getCurrentUser().getUsername() + "/Moments/" + signInfo.get(j).getPhotoId()));

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
                intent.putExtra("SignInfo", signInfo.get(j));
                context.startActivity(intent);
            }
        });
    }
}
