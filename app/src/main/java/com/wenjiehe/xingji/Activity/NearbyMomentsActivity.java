package com.wenjiehe.xingji.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
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
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.canyinghao.canrefresh.CanRefreshLayout;
import com.canyinghao.canrefresh.classic.ClassicRefreshView;
import com.wenjiehe.xingji.Adapter.NearbyRecyclerViewAdapter;
import com.wenjiehe.xingji.R;
import com.wenjiehe.xingji.SignInfo;
import com.wenjiehe.xingji.SignLocation;
import com.wenjiehe.xingji.Util;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.wenjiehe.xingji.Activity.MainActivity.arraylistHistorySign;
import static com.wenjiehe.xingji.Util.hasFile;

public class NearbyMomentsActivity extends AppCompatActivity
        implements CanRefreshLayout.OnRefreshListener, CanRefreshLayout.OnLoadMoreListener {

    String TAG = "NearByMoments";

    CanRefreshLayout refresh;
    ClassicRefreshView canRefreshHeader;
    //StoreHouseRefreshView canRefreshFooter;


    private RecyclerView recyclerView;
    private NearbyRecyclerViewAdapter adapter;

    /*baiduMap 定位*/
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    private double mylatitude = 0.0;
    private double mylongitude = 0.0;
    private double nearbySize = 0.06;

    public static ArrayList<SignInfo> arraylistNearbyMoments =new ArrayList<SignInfo>();
    //public ArrayList<SignInfo> signTmp = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_moments);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_nearby_moments_toolbar);
        setSupportActionBar(toolbar);

        if(!AVUser.getCurrentUser().getBoolean("isShareSignInfo")) {
            new AlertDialog.Builder(NearbyMomentsActivity.this)
                    .setTitle("提示")
                    .setMessage("若需使用此功能请先在-设置中允许共享签到信息")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
            //finish();
        }

        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        initLocation();

        refresh = (CanRefreshLayout) findViewById(R.id.refresh);
        //canRefreshFooter = (StoreHouseRefreshView) findViewById(R.id.can_refresh_footer);
        canRefreshHeader = (ClassicRefreshView) findViewById(R.id.can_refresh_header);
        canRefreshHeader.setPullStr("下拉刷新");
        canRefreshHeader.setReleaseStr("释放立即刷新");
        canRefreshHeader.setCompleteStr("刷新完成");
        canRefreshHeader.setRefreshingStr("刷新中");
        refresh.setOnLoadMoreListener(this);
        refresh.setOnRefreshListener(this);
        refresh.setRefreshBackgroundResource(R.color.colorPrimary);
        refresh.setLoadMoreBackgroundResource(R.color.window_background);
        //canRefreshHeader.setColors(getResources().getColor(R.color.color_button), getResources().getColor(R.color.color_text), getResources().getColor(R.color.color_red));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.can_scroll_view);
        adapter = new NearbyRecyclerViewAdapter(arraylistNearbyMoments, NearbyMomentsActivity.this);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
       // recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //mRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    }


    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
        //mLocationClient.start();
    }

    public final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //adapter = new NearbyRecyclerViewAdapter(arraylistNearbyMoments, NearbyMomentsActivity.this);
                   // adapter.notifyAll();
                    adapter.notifyDataSetChanged();
                    break;
                case 2:
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void syncNearbyMoments() {
        arraylistNearbyMoments.clear();
        adapter.notifyDataSetChanged();
        AVQuery<AVObject> query = new AVQuery<>("signInfo");

        query.whereGreaterThan("latitude", mylatitude - nearbySize);
        query.whereLessThanOrEqualTo("latitude", mylatitude + nearbySize);
        query.whereGreaterThan("longitude", mylongitude - nearbySize);
        query.whereLessThanOrEqualTo("longitude", mylongitude + nearbySize);

        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                String datetmp, provincetmp, citytmp, streettmp, eventtmp, locDescribetmp, objectIdtmp, photoIdTmp = "0";
                double lattmp, lngtmp;
                JSONObject likertmp;
                String usernameTmp;
                if (list == null)
                    return;

                for (AVObject avObject : list) {
                    objectIdtmp = avObject.getObjectId();
                    lattmp = avObject.getDouble("latitude");
                    lngtmp = avObject.getDouble("longitude");
                    datetmp = avObject.getString("date");
                    provincetmp = avObject.getString("province");
                    citytmp = avObject.getString("city");
                    streettmp = avObject.getString("street");
                    eventtmp = avObject.getString("event");
                    usernameTmp = avObject.getString("username");
                    likertmp = avObject.getJSONObject("liker");
                    final String str = usernameTmp;
                    locDescribetmp = avObject.getString("locdescribe");
                    if (avObject.getAVFile("signphoto") != null) {
                        photoIdTmp = avObject.getAVFile("signphoto").getObjectId();
                        final AVFile file = avObject.getAVFile("signphoto");
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] bytes, AVException e) {
                                // bytes 就是文件的数据流
                                Util.saveBitmap(Util.bytes2Bimap(bytes), "Moments/" + file.getObjectId());
                            }
                        }, new ProgressCallback() {
                            @Override
                            public void done(Integer integer) {
                                // 下载进度数据，integer 介于 0 和 100。
                            }
                        });
                    } else
                        photoIdTmp = "0";

                    Util.downloadPicture(usernameTmp,"Moments");
                    /*if (!hasFile(Environment.getExternalStorageDirectory() + "/xingji/" +
                            AVUser.getCurrentUser().getUsername() + "/Moments/" + usernameTmp)) {//没有这个头像
                        AVQuery<AVObject> queryPhoto = new AVQuery<>("headpicture");
                        queryPhoto.whereEqualTo("username", usernameTmp);
                        queryPhoto.findInBackground(new FindCallback<AVObject>() {
                            @Override
                            public void done(List<AVObject> list, AVException e) {
                                if (list == null)
                                    return;
                                for (AVObject avObject : list) {
                                    AVFile file = avObject.getAVFile("headpicture");
                                    file.getDataInBackground(new GetDataCallback() {
                                        @Override
                                        public void done(byte[] bytes, AVException e) {
                                            // bytes 就是文件的数据流
                                            Bitmap bitmap  = Util.bytes2Bimap(bytes);
                                            Util.saveBitmap(bitmap,"Moments/"+str);
                                        }
                                    }, new ProgressCallback() {
                                        @Override
                                        public void done(Integer integer) {
                                            // 下载进度数据，integer 介于 0 和 100。
                                        }
                                    });
                                }
                            }
                        });
                    }*/
                    final SignInfo signinfotmp = new SignInfo(new LatLng(lattmp, lngtmp), datetmp,
                            new SignLocation(provincetmp, citytmp, streettmp, locDescribetmp), eventtmp, objectIdtmp, photoIdTmp, usernameTmp,likertmp);

                    AVQuery<AVObject> query = new AVQuery<>("_User");
                    query.whereEqualTo("username",usernameTmp);
                    query.findInBackground(new FindCallback<AVObject>() {
                                               @Override
                                               public void done(List<AVObject> list, AVException e) {
                                                   for (AVObject avObject : list) {
                                                       Boolean b = avObject.getBoolean("isShareSignInfo");
                                                       if(b==true) {
                                                           arraylistNearbyMoments.add(signinfotmp);
                                                           //signTmp.add(signinfotmp);
                                                           Message message = new Message();
                                                           message.what = 1;
                                                           mHandler.sendMessage(message);
                                                       }
                                                   }
                                               }
                                           });

                }
                refresh.loadMoreComplete();
                refresh.refreshComplete();
            }

        });

    }

    @Override
    public void onLoadMore() {
        refresh.postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, 1500);
    }

    @Override
    public void onRefresh() {
        refresh.postDelayed(new Runnable() {
            @Override
            public void run() {
                //arraylistNearbyMoments.clear();
                syncNearbyMoments();
            }
        }, 2600);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStop() {
        super.onStop();
        mLocationClient.stop();
        //arraylistNearbyMoments.clear();
        Log.d("Nearby","onStop~~~");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Nearby","onDestory~~~");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mLocationClient.isStarted())
            mLocationClient.start();
        if(AVUser.getCurrentUser().getBoolean("isShareSignInfo")) {
            //arraylistNearbyMoments.clear();
            //refresh.autoRefresh();
        }
    }

    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {

            if (location == null) {
                return;
            }

            mylatitude = location.getLatitude();
            mylongitude = location.getLongitude();

            Log.d(TAG, String.valueOf(mylatitude));
            Log.d(TAG, String.valueOf(mylongitude));
           /* //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            Log.i("BaiduLocationApiDem", sb.toString());*/
        }
    }

}
