package com.wenjiehe.xingji.Fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.wenjiehe.xingji.Activity.MainActivity;
import com.wenjiehe.xingji.MyInfoCardListView;
import com.wenjiehe.xingji.R;
import com.wenjiehe.xingji.RefreshLayout;
import com.wenjiehe.xingji.SignInfo;
import com.wenjiehe.xingji.SignLocation;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import it.gmariotti.cardslib.library.view.CardListView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyHistorySignInfoFragment extends Fragment {


    //private RefreshLayout swipeRefreshLayout;
    private ArrayList<Card> cards = new ArrayList<Card>();
    public CardArrayAdapter mCardArrayAdapter;
    public MyInfoCardListView listView;
    private String removeobjectId;
    private int historySignNum = 0;
    private int beforeWeekNum = 1;
    private int setPerGet = 21;
    private boolean isAdapter = false;

    String TAG = "historysigninfofragment";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_history_sign_info, null);
        listView = (MyInfoCardListView) view.findViewById(R.id.myhistory_signinfo_listcard);
        //historySignNum = MainActivity.arraylistHistorySign.size();
        //Log.d(TAG+"historySignNum",String.valueOf(MainActivity.arraylistHistorySign.size()));
        //listView.setVerticalScrollBarEnabled(false);
        mCardArrayAdapter = new CardArrayAdapter(getActivity(), cards);
        listView.setAdapter(mCardArrayAdapter);

        syncHistorySignInfo();
        return view;
    }

    private void syncHistorySignInfo() {

        final AVUser currentUser = AVUser.getCurrentUser();
        currentUser.refreshInBackground(new RefreshCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (currentUser.get("signnum") != null)
                    MainActivity.signNum = (Integer) currentUser.get("signnum");
                MainActivity.tv_headerSignNum.setText(String.valueOf(MainActivity.signNum));

                File xingjiDir = new File(getActivity().getFilesDir().getAbsolutePath() + File.separator + "xingji");
                if (!xingjiDir.exists()) {
                    xingjiDir.mkdir();
                    getHistorySignRecord();
                    return;
                }
                //File file = new File(getFilesDir().getAbsolutePath() + File.separator +"xingji/.historySign");
                File f = new File(getActivity().getFilesDir().getAbsolutePath() + File.separator + "xingji/.historySign");
                Date date = new Date(f.lastModified());

                Log.d("MainActivity--", date.toString());
                Log.d("MainActivity--", String.valueOf(date.getTime()));

                Log.d("MainActivity", currentUser.getUpdatedAt().toString());
                Log.d("MainActivity", String.valueOf(currentUser.getUpdatedAt().getTime()));

                if (date.getTime() - currentUser.getUpdatedAt().getTime() < -4000) {
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
                    getHistorySignRecord();
                } else {
                    SignInfo.readSignInfoFromFile(getActivity(), MainActivity.arraylistHistorySign);
                    //historySignNum = MainActivity.arraylistHistorySign.size();
                    showSignRecord();
                    //mCardArrayAdapter.notifyDataSetChanged();
                    //listView.setSelection(listView.getCount() - 1);

                    if (listView != null && isAdapter == false) {

                        isAdapter = true;
                    }
                    //swipeRefreshLayout.setRefreshing(false);
                    //swipeRefreshLayout.setLoading(false);
                    mCardArrayAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    public void showSignRecord() {
        /*if(!cards.isEmpty()){
            int count=cards.size();
            //Log.d("--wenjie",String.valueOf(count));
            for(int i=0;i<count;i++) {
                cards.remove(0);
                Log.d("--wenjie",String.valueOf(i));
            }
        }*/
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
                    addToCards(signinfo1);
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
        Card card = new Card(getActivity());
        CardHeader header = new CardHeader(getActivity());
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
                                                            getActivity().getFilesDir().getAbsolutePath() + File.separator + "xingji/.historySign",
                                                            MainActivity.arraylistHistorySign);
                                                    mCardArrayAdapter.notifyDataSetChanged();
                                                    Toast.makeText(getActivity(), "签到删除成功", Toast.LENGTH_SHORT).show();
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


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        if (MainActivity.signNum < setPerGet)
            setPerGet = MainActivity.signNum;

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
                Log.d(TAG, "list.size");
                Log.d(TAG, String.valueOf(list.size()));
                String datetmp, provincetmp, citytmp, streettmp, eventtmp, locDescribetmp, objectIdtmp;
                double lattmp, lngtmp;
                if (list == null)
                    return;
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
                        historySignNum++;
                        Log.d(TAG, "arraylistHistorySign.isEmpty");
                        SignInfo signinfotmp = new SignInfo(new LatLng(lattmp, lngtmp), datetmp,
                                new SignLocation(provincetmp, citytmp, streettmp, locDescribetmp), eventtmp, objectIdtmp);
                        MainActivity.arraylistHistorySign.add(signinfotmp);
                        SignInfo.writeSignInfoToFile(getActivity().getFilesDir().getAbsolutePath() +
                                File.separator + "xingji/.historySign", MainActivity.arraylistHistorySign);
                    } else {
                        for (int i = 0; i < count; ) {
                            Log.d(TAG, "objectid");
                            Log.d(TAG, MainActivity.arraylistHistorySign.get(i).objectId);
                            Log.d(TAG, objectIdtmp);
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
                                    historySignNum++;
                                    Log.d(TAG, String.valueOf(historySignNum));
                                    SignInfo signinfotmp = new SignInfo(new LatLng(lattmp, lngtmp), datetmp,
                                            new SignLocation(provincetmp, citytmp, streettmp, locDescribetmp), eventtmp, objectIdtmp);
                                    MainActivity.arraylistHistorySign.add(signinfotmp);
                                    SignInfo.writeSignInfoToFile(getActivity().getFilesDir().getAbsolutePath() +
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
                        getHistorySignRecord();
                    } else {
                        SignInfo.readSignInfoFromFile(getActivity(), MainActivity.arraylistHistorySign);
                        showSignRecord();
                        if (listView != null && isAdapter == false) {
                            listView.setAdapter(mCardArrayAdapter);
                            isAdapter = true;
                        }

                        //swipeRefreshLayout.setRefreshing(false);
                        //swipeRefreshLayout.setLoading(false);
                        //listView.setSelection(listView.getCount() - 1);
                        mCardArrayAdapter.notifyDataSetChanged();
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
}