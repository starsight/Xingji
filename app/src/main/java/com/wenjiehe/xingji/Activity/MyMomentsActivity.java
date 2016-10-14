package com.wenjiehe.xingji.Activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.wenjiehe.xingji.R;
import com.wenjiehe.xingji.SignInfo;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



public class MyMomentsActivity extends AppCompatActivity {

    CanRefreshLayout refresh;
    ClassicRefreshView canRefreshHeader;
    private RecyclerView recyclerView;
    private MomentsRecyclerViewAdapter adapter = null;
    private ArrayList<JSONObject> mData = null;
    private Context mContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_moments);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_nearby_moments_toolbar);
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

        /**获取JSON*/
        AVQuery<AVObject> query = new AVQuery<>("u"+ AVUser.getCurrentUser().getObjectId());
        query.limit(10);// 最多返回 10 条结果
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                for (AVObject avObject : list) {
                    //Log.d("33",String.valueOf(list.size()));
                    JSONObject moments = avObject.getJSONObject("moments");
                    mData.add(moments);
                }

                adapter.notifyDataSetChanged();
            }
        });
    }

}
