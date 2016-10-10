package com.wenjiehe.xingji.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.RefreshCallback;
import com.baidu.mapapi.model.LatLng;
import com.canyinghao.canrefresh.CanRefreshLayout;
import com.canyinghao.canrefresh.classic.ClassicRefreshView;
import com.canyinghao.canrefresh.classic.RotateRefreshView;
import com.canyinghao.canrefresh.shapeloading.ShapeLoadingRefreshView;
import com.canyinghao.canrefresh.shapeloading.ShapeLoadingView;
import com.wenjiehe.xingji.RecyclerViewAdapter;
import com.wenjiehe.xingji.Util;
import com.wenjiehe.xingji.R;
import com.wenjiehe.xingji.SignInfo;
import com.wenjiehe.xingji.SignLocation;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyHistorySignActivity extends AppCompatActivity
        implements CanRefreshLayout.OnRefreshListener, CanRefreshLayout.OnLoadMoreListener {

    private final int SETPERGET = 10;
    //private ArrayList<Card> cards = new ArrayList<Card>();
    //public CardArrayAdapter mCardArrayAdapter;
    //public MyInfoCardListView listView;//test
    private String removeobjectId;
    private int historySignNum = 0;
    private int beforeWeekNum = 1;
    private int setPerGet = SETPERGET;
    private int canLoadNum = 0;
    //private int loadSignNumThisTime = 0;
    private boolean isAdapter = false;
    private CircleImageView iv_userinfo_headerphoto;

    String TAG = "MyHistorySignActivity";

    //private FragmentTransaction ft;
    //public MyHistorySignInfoFragment hsif;

    CanRefreshLayout refresh;
    ShapeLoadingRefreshView canRefreshHeader;
    ClassicRefreshView canRefreshFooter;


    private RecyclerView recyclerView;
    private List<SignInfo> signInfo = MainActivity.arraylistHistorySign;
    private RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_history_sign);
        Toolbar toolbar = (Toolbar) findViewById(R.id.meinfo_toolbar);
        toolbar.setTitle(AVUser.getCurrentUser().getUsername()+"的历史签到");
        setSupportActionBar(toolbar);

        refresh = (CanRefreshLayout) findViewById(R.id.refresh);
        canRefreshHeader = (ShapeLoadingRefreshView) findViewById(R.id.can_refresh_header);
        canRefreshFooter = (ClassicRefreshView) findViewById(R.id.can_refresh_footer);
        canRefreshFooter.setPullStr("下拉刷新");
        canRefreshFooter.setReleaseStr("释放立即刷新");
        canRefreshFooter.setCompleteStr("刷新完成");
        canRefreshFooter.setRefreshingStr("刷新中");

        refresh.setOnLoadMoreListener(this);
        refresh.setOnRefreshListener(this);
        refresh.setRefreshBackgroundResource(R.color.colorPrimary);
        refresh.setLoadMoreBackgroundResource(R.color.window_background);
        canRefreshHeader.setColors(getResources().getColor(R.color.color_button), getResources().getColor(R.color.color_text), getResources().getColor(R.color.color_red));
//test
        //listView = (MyInfoCardListView) findViewById(R.id.carddemo_list_gplaycard2);
        //listView.setVerticalScrollBarEnabled(false);
        //mCardArrayAdapter = new CardArrayAdapter(this, cards);
        //listView.setAdapter(mCardArrayAdapter);

        iv_userinfo_headerphoto = (CircleImageView) findViewById(R.id.iv_userinfo_headerphoto);
        iv_userinfo_headerphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(v.getContext(),
                        EditUserInfoActivity.class);
                startActivity(mainIntent);
            }
        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.can_scroll_view);
        adapter = new RecyclerViewAdapter(signInfo, MyHistorySignActivity.this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if (MainActivity.upadteUserPhotoBitmap != null)
            iv_userinfo_headerphoto.setImageBitmap(MainActivity.upadteUserPhotoBitmap);
    }

    private void syncHistorySignInfo() {
        final AVUser currentUser = AVUser.getCurrentUser();
        currentUser.refreshInBackground(new RefreshCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (currentUser.get("signnum") != null)
                    MainActivity.signNum = (Integer) currentUser.get("signnum");
                MainActivity.tv_headerSignNum.setText(String.valueOf(MainActivity.signNum));

                File xingjiDir = new File(getFilesDir().getAbsolutePath() + File.separator + "xingji");
                if (!xingjiDir.exists()) {
                    xingjiDir.mkdir();
                    getHistorySignRecord();
                    return;
                }
                //File file = new File(getFilesDir().getAbsolutePath() + File.separator +"xingji/.historySign");
                File f = new File(getFilesDir().getAbsolutePath() + File.separator + "xingji/.historySign");
                Date date = new Date(f.lastModified());

                //Log.d("MainActivity--", date.toString());
                //Log.d("MainActivity--", String.valueOf(date.getTime()));

                Log.d("MainActivity", currentUser.getUpdatedAt().toString());
                Log.d("MainActivity", String.valueOf(currentUser.getUpdatedAt().getTime()));

                if (date.getTime() - currentUser.getUpdatedAt().getTime() < -4000) {//数据太旧，需要更新
                    Log.d("MainActivity", "enter-update-historysign");
                    f.delete();
                    if (!MainActivity.arraylistHistorySign.isEmpty()) {
                        int count = MainActivity.arraylistHistorySign.size();
                        for (int i = 0; i < count; i++) {
                            MainActivity.arraylistHistorySign.remove(0);
                        }
                        historySignNum = 0;
                        beforeWeekNum = 1;
                    }
                    canLoadNum = MainActivity.signNum - MainActivity.arraylistHistorySign.size();
                    Log.d(TAG,"数据太旧的--canLoadNum");
                    Log.d(TAG,String.valueOf(canLoadNum));
                    getHistorySignRecord();
                } else {
                    SignInfo.readSignInfoFromFile(MyHistorySignActivity.this, MainActivity.arraylistHistorySign);
                    //showSignRecord();
                    //adapter.updateRecyclerView();
                    canLoadNum = MainActivity.signNum - MainActivity.arraylistHistorySign.size();
                    Log.d(TAG,"canLoadNum");
                    Log.d(TAG,String.valueOf(canLoadNum));
                    if (canLoadNum > 0) {
                        getHistorySignRecord();
                    }
                    refresh.loadMoreComplete();
                    refresh.refreshComplete();
                    adapter.notifyDataSetChanged();
                    //mCardArrayAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    public ArrayList<Date> getBeforeSevenDate(int beforewWeek) {// 获取n周内的签到信息
        //if(beforewWeek>5)
        //return null;
        ArrayList<Date> beforeDate = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        Calendar cc = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -(beforewWeek * 7) + 1);
        cc.add(Calendar.DAY_OF_MONTH, 7 - (beforewWeek * 7) + 1);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String cmDateTime = formatter.format(c.getTime());
        String ccmDateTime = formatter.format(cc.getTime());
        //Log.d(TAG,cmDateTime);
        //Log.d(TAG,ccmDateTime);
        try {
            beforeDate.add(formatter.parse(ccmDateTime));//near
            beforeDate.add(formatter.parse(cmDateTime));//away

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return beforeDate;
    }

    public void getHistorySignRecord() {
        Date nearDate, awayDate;
        AVQuery<AVObject> query = new AVQuery<>("signInfo");
        if (canLoadNum < setPerGet)
            setPerGet = canLoadNum;

        ArrayList<Date> dateTmp = getBeforeSevenDate(beforeWeekNum);
        if (dateTmp == null)
            return;
        nearDate = dateTmp.get(0);
        awayDate = dateTmp.get(1);
        beforeWeekNum++;
        Log.d(TAG, String.valueOf(nearDate));
        Log.d(TAG, String.valueOf(awayDate));
        query.whereEqualTo("username", MainActivity.userName);
        query.whereGreaterThan("createdAt", awayDate);//小日期
        query.whereLessThanOrEqualTo("createdAt", nearDate);//大日期

        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                //historySignNum = list.size();

                String datetmp, provincetmp, citytmp, streettmp, eventtmp, locDescribetmp, objectIdtmp, photoIdTmp = "0";
                double lattmp, lngtmp;
                if (list == null)
                    return;
                Log.d(TAG, "list.size");
                Log.d(TAG, String.valueOf(list.size()));
                for (AVObject avObject : list) {
                    objectIdtmp = avObject.getObjectId();
                    Log.d(TAG, "avObject.getObjectId");
                    Log.d(TAG, objectIdtmp);
                    int count = MainActivity.arraylistHistorySign.size();
                    if (MainActivity.arraylistHistorySign.isEmpty()) {
                        lattmp = avObject.getDouble("latitude");
                        lngtmp = avObject.getDouble("longitude");
                        datetmp = avObject.getString("date");
                        provincetmp = avObject.getString("province");
                        citytmp = avObject.getString("city");
                        streettmp = avObject.getString("street");
                        eventtmp = avObject.getString("event");
                        locDescribetmp = avObject.getString("locdescribe");
                        if (avObject.getAVFile("signphoto") != null) {
                            photoIdTmp = avObject.getAVFile("signphoto").getObjectId();
                            final AVFile file = avObject.getAVFile("signphoto");
                            file.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] bytes, AVException e) {
                                    // bytes 就是文件的数据流
                                    Util.saveBitmap(Util.bytes2Bimap(bytes),"Signs/"+file.getObjectId());
                                }
                            }, new ProgressCallback() {
                                @Override
                                public void done(Integer integer) {
                                    // 下载进度数据，integer 介于 0 和 100。
                                }
                            });
                        }
                        else
                            photoIdTmp = "0";
                        historySignNum++;
                        //Log.d(TAG, "arraylistHistorySign.isEmpty");
                        //Log.d(TAG, avObject.getAVFile("signphoto").getObjectId());
                        SignInfo signinfotmp = new SignInfo(new LatLng(lattmp, lngtmp), datetmp,
                                new SignLocation(provincetmp, citytmp, streettmp, locDescribetmp), eventtmp, objectIdtmp, photoIdTmp);
                        MainActivity.arraylistHistorySign.add(signinfotmp);
                        SignInfo.writeSignInfoToFile(getFilesDir().getAbsolutePath() +
                                File.separator + "xingji/.historySign", MainActivity.arraylistHistorySign);
                    } else {
                        for (int i = 0; i < count; ) {
                            //Log.d(TAG, "objectid");
                            //Log.d(TAG, MainActivity.arraylistHistorySign.get(i).objectId);
                            //Log.d(TAG, objectIdtmp);
                            if (i == (count - 1)) {
                                if (!MainActivity.arraylistHistorySign.get(i).objectId.equals(objectIdtmp)) {
                                    lattmp = avObject.getDouble("latitude");
                                    lngtmp = avObject.getDouble("longitude");
                                    datetmp = avObject.getString("date");
                                    provincetmp = avObject.getString("province");
                                    citytmp = avObject.getString("city");
                                    streettmp = avObject.getString("street");
                                    eventtmp = avObject.getString("event");
                                    locDescribetmp = avObject.getString("locdescribe");
                                    if (avObject.getAVFile("signphoto") != null) {
                                        photoIdTmp = avObject.getAVFile("signphoto").getObjectId();
                                        final AVFile file = avObject.getAVFile("signphoto");
                                        file.getDataInBackground(new GetDataCallback() {
                                            @Override
                                            public void done(byte[] bytes, AVException e) {
                                                // bytes 就是文件的数据流
                                                Util.saveBitmap(Util.bytes2Bimap(bytes),"Signs/"+file.getObjectId());
                                            }
                                        }, new ProgressCallback() {
                                            @Override
                                            public void done(Integer integer) {
                                                // 下载进度数据，integer 介于 0 和 100。
                                            }
                                        });
                                    }
                                    else
                                        photoIdTmp = "0";
                                    historySignNum++;
                                    canLoadNum--;
                                    //loadSignNumThisTime++;//本次有效的签到数据
                                    //Log.d(TAG, String.valueOf(historySignNum));
                                    //Log.d(TAG, avObject.getAVFile("signphoto").getObjectId());
                                    SignInfo signinfotmp = new SignInfo(new LatLng(lattmp, lngtmp), datetmp,
                                            new SignLocation(provincetmp, citytmp, streettmp, locDescribetmp), eventtmp, objectIdtmp, photoIdTmp);
                                    MainActivity.arraylistHistorySign.add(signinfotmp);
                                    SignInfo.writeSignInfoToFile(getFilesDir().getAbsolutePath() +
                                            File.separator + "xingji/.historySign", MainActivity.arraylistHistorySign);
                                    break;
                                }
                            }
                            if (MainActivity.arraylistHistorySign.get(i).objectId.equals(objectIdtmp)) {
                                break;
                            } else
                                i++;
                        }
                    }
                    //String title = avObject.getString("title");
                }

                Message message = new Message();
                message.what = 1;
                mHandler.sendMessage(message);
            }
        });


    }

    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (historySignNum < setPerGet) {
                        //Log.d(TAG + "setPerNum", String.valueOf(setPerGet));
                        //Log.d(TAG + "canLoadNum", String.valueOf(canLoadNum));
                        //Log.d(TAG + "historySignNum", String.valueOf(historySignNum));
                        //canLoadNum -= setPerGet;
                        getHistorySignRecord();
                    } else {
                        SignInfo.readSignInfoFromFile(MyHistorySignActivity.this, MainActivity.arraylistHistorySign);
                        //showSignRecord();
                        //adapter.updateRecyclerView();

                        /*if (listView != null && isAdapter == false) {
                            listView.setAdapter(mCardArrayAdapter);
                            isAdapter = true;
                        }*///test
                        refresh.loadMoreComplete();
                        refresh.refreshComplete();
                        Log.d(TAG, String.valueOf(MainActivity.arraylistHistorySign.size()));
                        adapter.notifyDataSetChanged();
                        //swipeRefreshLayout.setRefreshing(false);
                        //swipeRefreshLayout.setLoading(false);
                        //listView.setSelection(listView.getCount() - 1);
                        //mCardArrayAdapter.notifyDataSetChanged();
                        //listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                        //listView.setStackFromBottom(true);
                        Log.d(TAG, "handlering refresh");
                    }

                    break;
                case 2:

                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onLoadMore() {
        refresh.postDelayed(new Runnable() {
            @Override
            public void run() {
                historySignNum = 0;
                beforeWeekNum = 1;
                setPerGet = SETPERGET;
                syncHistorySignInfo();

            }
        }, 1500);
    }

    @Override
    public void onRefresh() {
        refresh.postDelayed(new Runnable() {
            @Override
            public void run() {
                historySignNum = 0;
                syncHistorySignInfo();
                //refresh.refreshComplete();
            }
        }, 2600);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MainActivity.isUpadteUserPhoto == true)
            iv_userinfo_headerphoto.setImageBitmap(MainActivity.upadteUserPhotoBitmap);
        //MainActivity.isUpadteUserPhoto = true;
        //MainActivity.upadteUserPhotoBitmap = bitmap;
        refresh.autoRefresh();
    }
}
