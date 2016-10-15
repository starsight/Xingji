package com.wenjiehe.xingji.Adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.wenjiehe.xingji.Activity.SignActivity;
import com.wenjiehe.xingji.Activity.SignInfoDetailActivity;
import com.wenjiehe.xingji.R;
import com.wenjiehe.xingji.SignInfo;
import com.wenjiehe.xingji.ThumbUpView;
import com.wenjiehe.xingji.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.media.CamcorderProfile.get;
import static com.baidu.location.h.j.G;
import static com.baidu.location.h.j.V;
import static com.wenjiehe.xingji.Activity.NearbyMomentsActivity.arraylistNearbyMoments;
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
        if (hasFile(str)) {
            Bitmap b = Util.file2bitmap(str);
            personViewHolder.iv_news_userphoto.setImageBitmap(b);
        } else
            personViewHolder.iv_news_userphoto.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon));

        //为btn_share btn_readMore cardView设置点击事件
        personViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SignInfoDetailActivity.class);
                intent.putExtra("SignInfo", signInfo.get(j));
                context.startActivity(intent);
            }
        });

        if (signInfo.get(j).username.equals(AVUser.getCurrentUser().getUsername()))//不允许喜欢自己的签到信息
            personViewHolder.tpv.setVisibility(View.GONE);
        else
            personViewHolder.tpv.setVisibility(View.VISIBLE);

        if (personViewHolder.news_photo.getVisibility() == View.VISIBLE) {
            if (hasFile(Environment.getExternalStorageDirectory() +
                    "/xingji/" + AVUser.getCurrentUser().getUsername() + "/Moments/" + signInfo.get(j).getPhotoId()))
                personViewHolder.news_photo.setImageBitmap(Util.file2bitmap(Environment.getExternalStorageDirectory() +
                        "/xingji/" + AVUser.getCurrentUser().getUsername() + "/Moments/" + signInfo.get(j).getPhotoId()));
        }

        final JSONObject liker = signInfo.get(j).liker;
        String likerString = liker.toString();
        Log.d("liker", likerString);
        if (likerString.contains("\"" + AVUser.getCurrentUser().getUsername() + "\""))
            personViewHolder.tpv.setLike();
        else
            personViewHolder.tpv.setUnlike();
        // /personViewHolder.tpv.UnLike();

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


        personViewHolder.tpv.setOnThumbUp(new ThumbUpView.OnThumbUp() {
            @Override
            public void like(boolean like) {
                if (like) {
                    //在用户的moments状态表更新一条记录
                    AVObject moments = new AVObject("u" + AVUser.getCurrentUser().getObjectId());
                    JSONObject jo = new JSONObject();
                    try {
                        jo.put("type", "1");
                        jo.put("info", "喜欢了" + signInfo.get(j).username + "在" + signInfo.get(j).getLocation() + "的签到");
                        jo.put("signobjectid", signInfo.get(j).getObjectId());
                        //jo.put("ownerobjectid", signInfo.get(j).getObjectId());
                        jo.put("ownerusername", signInfo.get(j).username);

                        jo.put("likerusername", AVUser.getCurrentUser().getUsername());
                        jo.put("likerobjectid", AVUser.getCurrentUser().getObjectId());
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    moments.put("moments", jo);
                    moments.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {

                        }
                    });

                    //在对方的moments状态表更新一条记录
                    AVQuery<AVObject> queryOwnerId = new AVQuery<>("_User");
                    queryOwnerId.whereEqualTo("username", signInfo.get(j).username);
                    queryOwnerId.findInBackground(new FindCallback<AVObject>() {
                        @Override
                        public void done(List<AVObject> list, AVException e) {
                            for (AVObject avObject : list) {
                                String ownerObjectId = avObject.getObjectId();
                                AVObject momentsOwner = new AVObject("u" + ownerObjectId);
                                JSONObject joOwner = new JSONObject();
                                try {
                                    joOwner.put("type", "2");
                                    joOwner.put("info", AVUser.getCurrentUser().getUsername() + "喜欢了" + signInfo.get(j).username + "在" + signInfo.get(j).getLocation() + "的签到");
                                    joOwner.put("signobjectid", signInfo.get(j).getObjectId());
                                    //jo.put("ownerobjectid", signInfo.get(j).getObjectId());
                                    joOwner.put("ownerusername", signInfo.get(j).username);

                                    joOwner.put("likerusername", AVUser.getCurrentUser().getUsername());
                                    joOwner.put("likerobjectid", AVUser.getCurrentUser().getObjectId());
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                                momentsOwner.put("moments", joOwner);
                                momentsOwner.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(AVException e) {
                                    }
                                });
                            }
                        }
                    });

                    //查找Signinfo，在liker中增加一条记录
                    if (!signInfo.get(j).getObjectId().equals("0")) {
                        AVObject todo = AVObject.createWithoutData("signInfo", signInfo.get(j).getObjectId());
                        try {
                            liker.put(AVUser.getCurrentUser().getUsername(), AVUser.getCurrentUser().getObjectId());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        todo.put("liker", liker);
                        todo.saveInBackground();
                    }

                } else {//不喜欢
                    AVQuery<AVObject> query1 = new AVQuery<>("u" + AVUser.getCurrentUser().getObjectId());
                    JSONObject jo = new JSONObject();
                    try {
                        jo.put("type", "1");
                        jo.put("info", "喜欢了" + signInfo.get(j).username + "在" + signInfo.get(j).getLocation() + "的签到");
                        jo.put("signobjectid", signInfo.get(j).getObjectId());
                        jo.put("ownerusername", signInfo.get(j).username);

                        jo.put("likerusername", AVUser.getCurrentUser().getUsername());
                        jo.put("likerobjectid", AVUser.getCurrentUser().getObjectId());
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    query1.whereEqualTo("moments", jo);
                    query1.findInBackground(new FindCallback<AVObject>() {
                        @Override
                        public void done(List<AVObject> list, AVException e) {
                            for (AVObject l : list) {
                                l.deleteInBackground();
                            }
                        }
                    });

                    //在对方的moments状态表更新一条记录
                    AVQuery<AVObject> queryOwnerId = new AVQuery<>("_User");
                    queryOwnerId.whereEqualTo("username", signInfo.get(j).username);
                    queryOwnerId.findInBackground(new FindCallback<AVObject>() {
                        @Override
                        public void done(List<AVObject> list, AVException e) {
                            for (AVObject avObject : list) {
                                String ownerObjectId = avObject.getObjectId();
                                AVQuery<AVObject> query2 = new AVQuery<>("u" + ownerObjectId);
                                JSONObject joOwner = new JSONObject();
                                try {
                                    joOwner.put("type", "2");
                                    joOwner.put("info", AVUser.getCurrentUser().getUsername() + "喜欢了" + signInfo.get(j).username + "在" + signInfo.get(j).getLocation() + "的签到");
                                    joOwner.put("signobjectid", signInfo.get(j).getObjectId());
                                    //jo.put("ownerobjectid", signInfo.get(j).getObjectId());
                                    joOwner.put("ownerusername", signInfo.get(j).username);

                                    joOwner.put("likerusername", AVUser.getCurrentUser().getUsername());
                                    joOwner.put("likerobjectid", AVUser.getCurrentUser().getObjectId());
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                                query2.whereEqualTo("moments", joOwner);
                                query2.findInBackground(new FindCallback<AVObject>() {
                                    @Override
                                    public void done(List<AVObject> list, AVException e) {
                                        for (AVObject l : list) {
                                            l.deleteInBackground();
                                        }
                                    }
                                });
                            }
                        }
                    });

                    //查找Signinfo，在liker中删除一条记录
                    if (!signInfo.get(j).getObjectId().equals("0")){
                        AVObject todo = AVObject.createWithoutData("signInfo", signInfo.get(j).getObjectId());
                        liker.remove(AVUser.getCurrentUser().getUsername());
                        todo.put("liker",liker);
                        todo.saveInBackground();
                    }



                }
            }
        });

    }
}
