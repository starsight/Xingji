package com.wenjiehe.xingji.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.canyinghao.canrefresh.CanRefreshLayout;
import com.canyinghao.canrefresh.classic.ClassicRefreshView;
import com.wenjiehe.xingji.Adapter.MomentsRecyclerViewAdapter;
import com.wenjiehe.xingji.Adapter.NearbyRecyclerViewAdapter;
import com.wenjiehe.xingji.Im.AVSingleChatActivity;
import com.wenjiehe.xingji.Im.Constants;
import com.wenjiehe.xingji.R;
import com.wenjiehe.xingji.SignInfo;
import com.wenjiehe.xingji.Util;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.avos.avoscloud.Messages.OpType.query;
import static com.wenjiehe.xingji.R.id.iv_detail_userphoto;
import static com.wenjiehe.xingji.R.id.iv_userinfo_headerphoto;
import static com.wenjiehe.xingji.R.id.toolbar;
import static com.wenjiehe.xingji.Util.hasFile;


public class MyMomentsActivity extends AppCompatActivity
        implements CanRefreshLayout.OnRefreshListener, CanRefreshLayout.OnLoadMoreListener {

    CanRefreshLayout refresh;
    ClassicRefreshView canRefreshHeader;
    ClassicRefreshView canRefreshFooter;
    private RecyclerView recyclerView;
    private MomentsRecyclerViewAdapter adapter = null;
    private ArrayList<JSONObject> mData = null;
    private Context mContext = null;
    private CircleImageView iv_userinfo_headerphoto;//修改头像

    public static final int STARTFROMOTHERS = 0;
    public static final int STARTFROMMINE = 1;

    private String username, userobjectid;
    private int startType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_moments);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        userobjectid = intent.getStringExtra("userobjectid");

        startType = intent.getIntExtra("type", STARTFROMOTHERS);
        if (username == null)
            username = AVUser.getCurrentUser().getUsername();
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_nearby_moments_toolbar);
        toolbar.setTitle(username + "的动态");
        setSupportActionBar(toolbar);

        mContext = MyMomentsActivity.this;

        mData = new ArrayList<JSONObject>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.can_scroll_view);
        adapter = new MomentsRecyclerViewAdapter(mData, MyMomentsActivity.this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        count = 0;

        if (userobjectid == null)
            userobjectid = "u" + AVUser.getCurrentUser().getObjectId();
        else
            userobjectid = "u" + userobjectid;
        /**获取JSON*/
        /*AVQuery<AVObject> query = new AVQuery<>("u"+userobjectid);
        query.limit(10);// 最多返回 10 条结果
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(list==null)
                    return;
                for (AVObject avObject : list) {
                    //Log.d("33",String.valueOf(list.size()));
                    JSONObject moments = avObject.getJSONObject("moments");
                    mData.add(moments);
                }

                adapter.notifyDataSetChanged();
            }
        });*/
        System.out.println(userobjectid);
        ;
        refresh = (CanRefreshLayout) findViewById(R.id.refresh);
        //canRefreshFooter = (StoreHouseRefreshView) findViewById(R.id.can_refresh_footer);
        canRefreshHeader = (ClassicRefreshView) findViewById(R.id.can_refresh_header);
        canRefreshHeader.setPullStr("下拉刷新");
        canRefreshHeader.setReleaseStr("释放立即刷新");
        canRefreshHeader.setCompleteStr("刷新完成");
        canRefreshHeader.setRefreshingStr("刷新中");
        canRefreshFooter = (ClassicRefreshView) findViewById(R.id.can_refresh_footer);
        canRefreshFooter.setPullStr("下拉刷新");
        canRefreshFooter.setReleaseStr("释放立即刷新");
        canRefreshFooter.setCompleteStr("刷新完成");
        canRefreshFooter.setRefreshingStr("刷新中");

        refresh.setOnLoadMoreListener(this);
        refresh.setOnRefreshListener(this);
        refresh.setRefreshBackgroundResource(R.color.colorPrimary);
        refresh.setLoadMoreBackgroundResource(R.color.window_background);

        iv_userinfo_headerphoto = (CircleImageView) findViewById(R.id.iv_userinfo_headerphoto);
        if (startType == STARTFROMMINE) {
            if (MainActivity.upadteUserPhotoBitmap != null)
                iv_userinfo_headerphoto.setImageBitmap(MainActivity.upadteUserPhotoBitmap);
            iv_userinfo_headerphoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mainIntent = new Intent(v.getContext(),
                            EditUserInfoActivity.class);
                    startActivity(mainIntent);
                }
            });
        } else {
            String str = Environment.getExternalStorageDirectory() + "/xingji/" +
                    AVUser.getCurrentUser().getUsername() + "/Moments/" + username;
            if (hasFile(str)) {
                Bitmap b = Util.file2bitmap(str);
                iv_userinfo_headerphoto.setImageBitmap(b);
            } else
                iv_userinfo_headerphoto.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon));

            iv_userinfo_headerphoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent Intent = new Intent(MyMomentsActivity.this,
                            AVSingleChatActivity.class);
                    Intent.putExtra(Constants.MEMBER_ID, username);
                    startActivity(Intent);
                }
            });

        }


        refresh.autoRefresh();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    static int count = 0;

    @Override
    public void onLoadMore() {
        refresh.postDelayed(new Runnable() {
            @Override
            public void run() {
                MyMomentsActivity.count++;
                final int count = MyMomentsActivity.count;
                System.out.println(String.valueOf(MyMomentsActivity.count));
                AVQuery<AVObject> query = new AVQuery<>(userobjectid);
                query.skip(10 * count);// 跳过 10 条结果
                query.limit(10);// 最多返回 10 条结果
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (list == null) {
                            refresh.loadMoreComplete();
                            return;
                        }
                        for (AVObject avObject : list) {
                            //Log.d("33",String.valueOf(list.size()));
                            JSONObject moments = avObject.getJSONObject("moments");
                            mData.add(moments);
                        }

                        adapter.notifyDataSetChanged();
                        refresh.loadMoreComplete();
                    }
                });
            }
        }, 1500);
    }

    @Override
    public void onRefresh() {
        refresh.postDelayed(new Runnable() {
            @Override
            public void run() {
                AVQuery<AVObject> query = new AVQuery<>(userobjectid);
                query.limit(10);// 最多返回 10 条结果
                mData.clear();
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (list == null) {
                            refresh.refreshComplete();
                            return;
                        }
                        for (AVObject avObject : list) {
                            //Log.d("33",String.valueOf(list.size()));
                            Log.d("233", String.valueOf(avObject));
                            JSONObject moments = avObject.getJSONObject("moments");
                            mData.add(moments);
                        }
                        count = 0;
                        adapter.notifyDataSetChanged();
                        refresh.refreshComplete();
                    }
                });
            }
        }, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (startType == STARTFROMMINE) {
            if (MainActivity.isUpadteUserPhoto == true)
                iv_userinfo_headerphoto.setImageBitmap(MainActivity.upadteUserPhotoBitmap);
        } else {
            String str = Environment.getExternalStorageDirectory() + "/xingji/" +
                    AVUser.getCurrentUser().getUsername() + "/Moments/" + username;
            if (hasFile(str)) {
                Bitmap b = Util.file2bitmap(str);
                iv_userinfo_headerphoto.setImageBitmap(b);
            } else
                iv_userinfo_headerphoto.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon));
        }

    }
}
