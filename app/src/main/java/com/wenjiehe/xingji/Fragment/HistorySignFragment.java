package com.wenjiehe.xingji.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
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
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.baidu.mapapi.SDKInitializer;
import com.wenjiehe.xingji.Activity.MainActivity;
import com.wenjiehe.xingji.R;
import com.wenjiehe.xingji.RefreshLayout;
import com.wenjiehe.xingji.SignInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

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
    private String removedate;

    String TAG="historysignfragment";
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_sign, null);

        SignInfo.readSignInfoFromFile(getActivity(), MainActivity.arraylistHistorySign);
        showSignRecord(view);
        return view;
    }

    public void showSignRecord(View view){
        if(!cards.isEmpty()){
            int count=cards.size();
            //Log.d("--wenjie",String.valueOf(count));
            for(int i=0;i<count;i++) {
                cards.remove(0);
                System.out.println(String.valueOf(i));
            }
        }

        for(SignInfo signinfo :MainActivity.arraylistHistorySign) {
            Log.d(TAG,"enter-for-arraylistHistorySign");
            Card card = new Card(getActivity());
            CardHeader header = new CardHeader(getActivity());
            header.setTitle(signinfo.location.province + signinfo.location.city + signinfo.location.street);
            card.setId(signinfo.date);
            header.setPopupMenu(R.menu.record_menu, new CardHeader.OnClickCardHeaderPopupMenuListener() {
                @Override
                public void onMenuItemClick(BaseCard card, MenuItem item) {
                    removedate = card.getId();
                    int count = MainActivity.arraylistHistorySign.size();
                    for (int i = 0; i < count; i++) {
                       // Log.d(LOG_D, String.valueOf(MainActivity.arraylistHistorySign.size()));
                        if (MainActivity.arraylistHistorySign.get(i).date.equals(removedate)) {
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
        listView = (CardListView) view.findViewById(R.id.carddemo_list_gplaycard);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (listView!=null){
            listView.setAdapter(mCardArrayAdapter);
        }
    }

}
