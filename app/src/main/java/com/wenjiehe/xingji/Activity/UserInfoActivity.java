package com.wenjiehe.xingji.Activity;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.canyinghao.canrefresh.CanRefreshLayout;
import com.canyinghao.canrefresh.classic.RotateRefreshView;
import com.canyinghao.canrefresh.shapeloading.ShapeLoadingRefreshView;
import com.wenjiehe.xingji.Fragment.MyHistorySignFragment;
import com.wenjiehe.xingji.Fragment.MyHistorySignInfoFragment;
import com.wenjiehe.xingji.Fragment.SignFragment;
import com.wenjiehe.xingji.R;
import com.wenjiehe.xingji.SignInfo;

import java.io.File;
import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import it.gmariotti.cardslib.library.view.CardListView;

public class UserInfoActivity extends AppCompatActivity implements  CanRefreshLayout.OnRefreshListener,CanRefreshLayout.OnLoadMoreListener  {

    private ArrayList<Card> cards = new ArrayList<Card>();
    public CardArrayAdapter mCardArrayAdapter;
    public CardListView listView;

    //private FragmentTransaction ft;
    //public MyHistorySignInfoFragment hsif;

    CanRefreshLayout refresh;
    RotateRefreshView canRefreshFooter;
    ShapeLoadingRefreshView canRefreshHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.meinfo_toolbar);
        setSupportActionBar(toolbar);

        refresh = (CanRefreshLayout)findViewById(R.id.refresh);
        canRefreshFooter = (RotateRefreshView)findViewById(R.id.can_refresh_footer);
        canRefreshHeader = (ShapeLoadingRefreshView)findViewById(R.id.can_refresh_header);

        refresh.setOnLoadMoreListener(this);
        refresh.setOnRefreshListener(this);
        refresh.setRefreshBackgroundResource(R.color.colorPrimary);
        refresh.setLoadMoreBackgroundResource(R.color.window_background);
        canRefreshHeader.setColors(getResources().getColor(R.color.color_button), getResources().getColor(R.color.color_text), getResources().getColor(R.color.color_red));

        listView = (CardListView) findViewById(R.id.carddemo_list_gplaycard2);
        //historySignNum = MainActivity.arraylistHistorySign.size();
        //Log.d(TAG+"historySignNum",String.valueOf(MainActivity.arraylistHistorySign.size()));
        listView.setVerticalScrollBarEnabled(false);
        mCardArrayAdapter = new CardArrayAdapter(this, cards);
        listView.setAdapter(mCardArrayAdapter);



        for (SignInfo signinfo : MainActivity.arraylistHistorySign) {

        Card card = new Card(this);
        CardHeader header = new CardHeader(this);
        header.setTitle(signinfo.location.province + signinfo.location.city + signinfo.location.street);
        card.setId(signinfo.objectId);
        header.setPopupMenu(R.menu.record_menu, new CardHeader.OnClickCardHeaderPopupMenuListener() {
            @Override
            public void onMenuItemClick(BaseCard card, MenuItem item) {

                }


        });
        card.setTitle(signinfo.event);
        //Add Header to card
        card.addCardHeader(header);
        cards.add(card);
}
    }


    @Override
    public void onLoadMore() {
        refresh.postDelayed(new Runnable() {
            @Override
            public void run() {
                refresh.loadMoreComplete();
            }
        }, 1000);
    }

    @Override
    public void onRefresh() {
        refresh.postDelayed(new Runnable() {
            @Override
            public void run() {

                refresh.refreshComplete();
            }
        }, 1500);
    }
}
