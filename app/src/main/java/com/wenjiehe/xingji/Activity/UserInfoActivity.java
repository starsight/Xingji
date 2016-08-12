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
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.RefreshCallback;
import com.avos.avoscloud.SaveCallback;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.MapBound;
import com.canyinghao.canrefresh.CanRefreshLayout;
import com.canyinghao.canrefresh.classic.RotateRefreshView;
import com.canyinghao.canrefresh.shapeloading.ShapeLoadingRefreshView;
import com.wenjiehe.xingji.RecyclerViewAdapter;
import com.wenjiehe.xingji.view.MyInfoCardListView;
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
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.base.BaseCard;

public class UserInfoActivity extends AppCompatActivity implements CanRefreshLayout.OnRefreshListener, CanRefreshLayout.OnLoadMoreListener {

    private final int SETPERGET = 10;
    //private ArrayList<Card> cards = new ArrayList<Card>();
    //public CardArrayAdapter mCardArrayAdapter;
    //public MyInfoCardListView listView;//test
    private String removeobjectId;
    private int historySignNum = 0;
    private int beforeWeekNum = 1;
    private int setPerGet = SETPERGET;
    private int canLoadNum = 0;
    private boolean isAdapter = false;
    private CircleImageView iv_userinfo_headerphoto;

    String TAG = "UserInfoActivity";

    //private FragmentTransaction ft;
    //public MyHistorySignInfoFragment hsif;

    CanRefreshLayout refresh;
    RotateRefreshView canRefreshFooter;
    ShapeLoadingRefreshView canRefreshHeader;

    private RecyclerView recyclerView;
    private List<SignInfo> signInfo = MainActivity.arraylistHistorySign;
    private RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.meinfo_toolbar);
        setSupportActionBar(toolbar);

        refresh = (CanRefreshLayout) findViewById(R.id.refresh);
        canRefreshFooter = (RotateRefreshView) findViewById(R.id.can_refresh_footer);
        canRefreshHeader = (ShapeLoadingRefreshView) findViewById(R.id.can_refresh_header);

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

        iv_userinfo_headerphoto = (CircleImageView)findViewById(R.id.iv_userinfo_headerphoto);
        iv_userinfo_headerphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(v.getContext(),
                        EditUserInfoActivity.class);
                startActivity(mainIntent);
            }
        });


        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView= (RecyclerView) findViewById(R.id.can_scroll_view);
        adapter = new RecyclerViewAdapter(signInfo,UserInfoActivity.this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if(MainActivity.upadteUserPhotoBitmap!=null)
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

                Log.d("MainActivity--", date.toString());
                Log.d("MainActivity--", String.valueOf(date.getTime()));

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
                    canLoadNum = MainActivity.signNum-MainActivity.arraylistHistorySign.size();
                    getHistorySignRecord();
                } else {
                    SignInfo.readSignInfoFromFile(UserInfoActivity.this, MainActivity.arraylistHistorySign);
                    //showSignRecord();
                    //adapter.updateRecyclerView();
                    canLoadNum = MainActivity.signNum-MainActivity.arraylistHistorySign.size();
                    if(canLoadNum>0){
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
/*
    public void showSignRecord() {
        int signCount = cards.size();
        int historyCount = MainActivity.arraylistHistorySign.size();
        ArrayList<Integer> isCardExist = new ArrayList();
        ArrayList<Integer> isHistorySignExist = new ArrayList();
        for (int i = 0; i < signCount; i++)
            isCardExist.add(i, 0);
        for (int i = 0; i < historyCount; i++)
            isHistorySignExist.add(i, 0);

        for (SignInfo signinfo : MainActivity.arraylistHistorySign) {
            if (cards.isEmpty()) {
                for (SignInfo signinfo1 : MainActivity.arraylistHistorySign) {
                    isCardExist.add(cards.size(), 1);
                    addToCards(signinfo1);//数据添加
                    Log.d(TAG, "cards.isEmpty");
                }
                break;
            } else {
                for (int i = 0; i < cards.size(); i++) {
                    if (cards.get(i).getId().equals(signinfo.objectId)) {
                        isCardExist.set(i, 1);
                        Log.d(TAG + "showSignRecord", String.valueOf(i));
                        break;
                        //isHistorySignExist[i] = 1;
                    } else if (i == (cards.size() - 1)) {
                        if (!cards.get(i).getId().equals(signinfo.objectId)) {
                            Log.d(TAG + "showSignRecord--add", String.valueOf(i));
                            isCardExist.add(cards.size(), 1);
                            addToCards(signinfo);
                        } else
                            isCardExist.set(i, 1);
                    }
                }
            }
        }

        for (int i = 0; i < cards.size(); i++) {
            if (isCardExist.get(i) == 0)
                cards.remove(i);
        }
        //mCardArrayAdapter.notifyDataSetChanged();
    }


    private void addToCards(SignInfo signinfo) {
        Log.d(TAG, "enter-for-arraylistHistorySign");
        Card card = new Card(this);
        CardHeader header = new CardHeader(this);
        header.setTitle(signinfo.location.province + signinfo.location.city + signinfo.location.street);
        card.setId(signinfo.objectId);
        header.setPopupMenu(R.menu.record_menu, new CardHeader.OnClickCardHeaderPopupMenuListener() {
            @Override
            public void onMenuItemClick(BaseCard card, MenuItem item) {
                removeobjectId = card.getId();
                int count = MainActivity.arraylistHistorySign.size();
                for (int i = 0; i < count; i++) {
                    // Log.d(LOG_D, String.valueOf(MainActivity.arraylistHistorySign.size()));
                    if (MainActivity.arraylistHistorySign.get(i).objectId.equals(removeobjectId)) {
                        String objectIdTmp = MainActivity.arraylistHistorySign.get(i).objectId;
                        MainActivity.arraylistHistorySign.remove(i);
                        cards.remove(i);
                        AVQuery<AVObject> avQuery = new AVQuery<>("signInfo");
                        avQuery.getInBackground(objectIdTmp, new GetCallback<AVObject>() {
                            @Override
                            public void done(AVObject avObject, AVException e) {
                                avObject.deleteInBackground(new DeleteCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        AVUser currentUser = AVUser.getCurrentUser();
                                        if (currentUser != null) {//签到次数统计同步
                                            MainActivity.signNum -= 1;
                                            MainActivity.setTv_headerSignNum();
                                            currentUser.put("signnum", MainActivity.signNum);
                                            currentUser.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(AVException e) {
                                                    SignInfo.writeSignInfoToFile(
                                                            getFilesDir().getAbsolutePath() + File.separator + "xingji/.historySign",
                                                            MainActivity.arraylistHistorySign);
                                                    mCardArrayAdapter.notifyDataSetChanged();
                                                    Toast.makeText(UserInfoActivity.this, "签到删除成功", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }

                                    }
                                });
                            }
                        });

                        break;
                    }
                }

            }
        });
        card.setTitle(signinfo.event);
        //Add Header to card
        card.addCardHeader(header);
        cards.add(card);
    }
*/
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

                String datetmp, provincetmp, citytmp, streettmp, eventtmp, locDescribetmp, objectIdtmp,photoIdTmp="0";
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
                        if(avObject.getAVFile("signphoto")!=null)
                        photoIdTmp = avObject.getAVFile("signphoto").getObjectId();
                        else
                            photoIdTmp="0";
                        historySignNum++;
                        //Log.d(TAG, "arraylistHistorySign.isEmpty");
                        //Log.d(TAG, avObject.getAVFile("signphoto").getObjectId());
                        SignInfo signinfotmp = new SignInfo(new LatLng(lattmp, lngtmp), datetmp,
                                new SignLocation(provincetmp, citytmp, streettmp, locDescribetmp), eventtmp, objectIdtmp,photoIdTmp);
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
                                    if(avObject.getAVFile("signphoto")!=null)
                                        photoIdTmp = avObject.getAVFile("signphoto").getObjectId();
                                    else
                                        photoIdTmp="0";
                                    historySignNum++;
                                    //Log.d(TAG, String.valueOf(historySignNum));
                                    //Log.d(TAG, avObject.getAVFile("signphoto").getObjectId());
                                    SignInfo signinfotmp = new SignInfo(new LatLng(lattmp, lngtmp), datetmp,
                                            new SignLocation(provincetmp, citytmp, streettmp, locDescribetmp), eventtmp, objectIdtmp,photoIdTmp);
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
                        Log.d(TAG + "setPerNum", String.valueOf(setPerGet));
                        Log.d(TAG + "historySignNum", String.valueOf(historySignNum));
                        canLoadNum-=setPerGet;
                        getHistorySignRecord();
                    } else {
                        SignInfo.readSignInfoFromFile(UserInfoActivity.this, MainActivity.arraylistHistorySign);
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
                setPerGet=SETPERGET;
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
                refresh.refreshComplete();
            }
        }, 2800);
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
        if(MainActivity.isUpadteUserPhoto==true)
            iv_userinfo_headerphoto.setImageBitmap(MainActivity.upadteUserPhotoBitmap);
        //MainActivity.isUpadteUserPhoto = true;
        //MainActivity.upadteUserPhotoBitmap = bitmap;
    }
}
