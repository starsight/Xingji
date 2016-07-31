package com.wenjiehe.xingji.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
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
import com.avos.avoscloud.SaveCallback;
import com.baidu.mapapi.model.LatLng;
import com.wenjiehe.xingji.Activity.MainActivity;
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


public class HistorySignFragment extends Fragment {

    private RefreshLayout swipeRefreshLayout;
    private ArrayList<Card> cards = new ArrayList<Card>();
    public CardArrayAdapter mCardArrayAdapter;
    public CardListView listView;
    private String removeobjectId;
    private int historySignNum = 0;
    private int beforeWeekNum = 1;
    private int setPerGet = 21;

    String TAG="historysignfragment";
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_sign, null);
        listView = (CardListView) view.findViewById(R.id.carddemo_list_gplaycard);
        //historySignNum = MainActivity.arraylistHistorySign.size();
        Log.d(TAG+"historySignNum",String.valueOf(MainActivity.arraylistHistorySign.size()));
        SignInfo.readSignInfoFromFile(getActivity(), MainActivity.arraylistHistorySign);
        getHistorySignRecord();
        showSignRecord();
        return view;
    }

    public void showSignRecord(){
        if(!cards.isEmpty()){
            int count=cards.size();
            //Log.d("--wenjie",String.valueOf(count));
            for(int i=0;i<count;i++) {
                cards.remove(0);
                Log.d("--wenjie",String.valueOf(i));
            }
        }

        for(SignInfo signinfo :MainActivity.arraylistHistorySign) {
            Log.d(TAG,"enter-for-arraylistHistorySign");
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
                                                MainActivity.signNum -=1;
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

        mCardArrayAdapter = new CardArrayAdapter(getActivity(),cards);
        mCardArrayAdapter.notifyDataSetChanged();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (listView!=null){
            listView.setAdapter(mCardArrayAdapter);
        }
    }

    public ArrayList<Date> getBeforeSevenDate(int beforewWeek){// 获取n周内的签到信息
        if(beforewWeek>5)
            return null;
        ArrayList<Date> beforeDate = new ArrayList<>();
        Calendar c =  Calendar.getInstance();
        Calendar cc =  Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -(beforewWeek*7)+1);
        cc.add(Calendar.DAY_OF_MONTH, 7-(beforewWeek*7)+1);
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
        Date nearDate,awayDate;
        AVQuery<AVObject> query = new AVQuery<>("signInfo");
        if(MainActivity.signNum<setPerGet)
            setPerGet = MainActivity.signNum;

            ArrayList<Date> dateTmp= getBeforeSevenDate(beforeWeekNum);
        if(dateTmp==null)
            return ;
            nearDate = dateTmp.get(0);
            awayDate = dateTmp.get(1);
            beforeWeekNum++;
            Log.d(TAG,String.valueOf(nearDate));
            Log.d(TAG,String.valueOf(awayDate));
            query.whereEqualTo("username", MainActivity.userName);
            query.whereGreaterThan("createdAt", awayDate);//小日期
            query.whereLessThanOrEqualTo("createdAt", nearDate);//大日期

            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    //historySignNum = list.size();
                    Log.d(TAG,"list.size");
                    Log.d(TAG,String.valueOf(list.size()));
                    String datetmp,provincetmp,citytmp,streettmp,eventtmp,locDescribetmp,objectIdtmp;
                    double lattmp,lngtmp;
                    if(list==null)
                        return ;
                    for (AVObject avObject : list) {
                        objectIdtmp = avObject.getObjectId();
                        Log.d(TAG,"avObject.getObjectId");
                        Log.d(TAG,objectIdtmp);
                        int count = MainActivity.arraylistHistorySign.size();
                        if(MainActivity.arraylistHistorySign.isEmpty()){
                            lattmp = avObject.getDouble("latitude");
                            lngtmp = avObject.getDouble("longitude");
                            datetmp = avObject.getString("date");
                            provincetmp = avObject.getString("province");
                            citytmp = avObject.getString("city");
                            streettmp = avObject.getString("street");
                            eventtmp = avObject.getString("event");
                            locDescribetmp = avObject.getString("locdescribe");
                            historySignNum++;
                            Log.d(TAG,"arraylistHistorySign.isEmpty");
                            SignInfo signinfotmp = new SignInfo(new LatLng(lattmp,lngtmp),datetmp,
                                    new SignLocation(provincetmp,citytmp,streettmp,locDescribetmp),eventtmp,objectIdtmp);
                            MainActivity.arraylistHistorySign.add(signinfotmp);
                            SignInfo.writeSignInfoToFile(getActivity().getFilesDir().getAbsolutePath() +
                                    File.separator +"xingji/.historySign",MainActivity.arraylistHistorySign);
                        }
                        else {
                            for (int i = 0; i < count; ) {
                                Log.d(TAG,"objectid");
                                Log.d(TAG,MainActivity.arraylistHistorySign.get(i).objectId);
                                Log.d(TAG,objectIdtmp);
                                if(i==(count-1)) {
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
                                }
                                else
                                    i++;
                            }
                        }
                        //String title = avObject.getString("title");
                    }

                    Message message=new Message();
                    message.what=1;
                    mHandler.sendMessage(message);
                }
            });


    }

    public Handler mHandler=new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case 1:
                    if(historySignNum<setPerGet) {
                        getHistorySignRecord();
                    }
                    else{
                        showSignRecord();
                        Log.d(TAG,"handlering refresh");
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
