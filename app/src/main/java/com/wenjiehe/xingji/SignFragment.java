package com.wenjiehe.xingji;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.wenjiehe.xingji.Activity.MainActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class SignFragment extends Fragment {

    private MapView mv_BaiduView;
    private BaiduMap baiduMap;

    private ImageView iv_barSign, iv_myLocation;
    private BitmapDescriptor bd_Sign;

    private final String LOG_D = "xingji-SignFragment";

    private LocationClient mLocationClient;
    private myLocationListener mlocationlistener;
    private double mylatitude = 0.0;
    private double mylongitude = 0.0;
    private double precision = 0.001;
    private String street,city, province;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SDKInitializer.initialize(getActivity().getApplicationContext());
        View view = inflater.inflate(R.layout.fragment_sign, null);
        //Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);


        SignInfo.readSignInfoFromFile(getActivity(), MainActivity.arraylistHistorySign);

        initView(view);
        initLocation();
        showOnMap(MainActivity.arraylistHistorySign);
        return view;
    }


    private void initView(View view){
        //获取地图控件引用
        /*baiduMap init*/
        mv_BaiduView =(MapView)view.findViewById(R.id.bmapView);
        iv_myLocation = (ImageView)view.findViewById(R.id.mylocation);
        iv_barSign = MainActivity.iv_barSign;
        iv_barSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 定义Maker坐标点
                LatLng point = new LatLng(mylatitude, mylongitude);
                if(!MainActivity.arraylistHistorySign.isEmpty()){
                    for(SignInfo signifo :MainActivity.arraylistHistorySign)
                        if(Math.abs(signifo.latlng.latitude-mylatitude)<precision&&Math.abs(signifo.latlng.longitude-mylongitude)<precision){
                            Toast.makeText(getActivity(), city+street+" 附近已签到过啦", Toast.LENGTH_SHORT).show();
                            return ;
                        }
                }
                iv_barSign.setImageDrawable(getResources().getDrawable(R.mipmap.sign_bar));

                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String date = sDateFormat.format(new java.util.Date());
                //System.out.println(date);
                MainActivity.arraylistHistorySign.add(new SignInfo(point,date,
                        new SignLocation(province,city,street)));//加入到所有签到的序列中
                SignInfo.writeSignInfoToFile(getActivity().getFilesDir().getAbsolutePath() +
                        File.separator +"xingji/.historySign",MainActivity.arraylistHistorySign);

                baiduMap.clear();
                MarkerOptions options;
                for(SignInfo signInfotmp: MainActivity.arraylistHistorySign) {
                    // 构建MarkerOption，用于在地图上添加Marker
                    options = new MarkerOptions().position(signInfotmp.latlng)
                            .icon(bd_Sign);
                    // 在地图上添加Marker，并显示
                    baiduMap.addOverlay(options);
                }

                /*if(MeFragment.username!=null) {
                    Thread thread = new Thread(SignFragment.this);
                    thread.start();
                }*/
            }
        });

        iv_myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMyLocation();
            }
        });

        baiduMap = mv_BaiduView.getMap();
        mv_BaiduView.removeViewAt(1);

    }

    private void initLocation() {
        mLocationClient = new LocationClient(this.getActivity());
        mlocationlistener = new myLocationListener();
        mLocationClient.registerLocationListener(mlocationlistener);

        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setScanSpan(10000);
        option.setLocationNotify(true);
        option.setIsNeedAddress(true);
        option.setOpenGps(true);

        mLocationClient.setLocOption(option);
    }

    private void showOnMap(ArrayList<SignInfo> arraylistHistorySign) {
        // 设置marker图标
        bd_Sign = BitmapDescriptorFactory.fromResource(R.mipmap.sign);

        //先清除图层
        baiduMap.clear();
        MarkerOptions options;
        for(SignInfo signInfotmp: arraylistHistorySign) {
            // 构建MarkerOption，用于在地图上添加Marker
            options = new MarkerOptions().position(signInfotmp.latlng)
                    .icon(bd_Sign);
            // 在地图上添加Marker，并显示
            baiduMap.addOverlay(options);
        }

        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(16.0f);
        baiduMap.setMapStatus(msu);
    }

    private void goToMyLocation() {
        baiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
        initLocation();
        baiduMap.setMyLocationEnabled(true);
        mLocationClient.start();
        LatLng lat = new LatLng(mylatitude, mylongitude);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(lat);
        baiduMap.animateMapStatus(msu);
        for(SignInfo signInfo:MainActivity.arraylistHistorySign){
            if(isSigned(signInfo.latlng))
                iv_barSign.setImageDrawable(getResources().getDrawable(R.mipmap.sign_bar));
        }
    }

    public boolean isSigned(LatLng latlng){
        if((mylatitude!=0.0)&&(mylongitude!=0.0)){
            if((Math.abs(latlng.latitude-mylatitude)<=precision)&&Math.abs(latlng.longitude-mylongitude)<=precision)
                return true;
            else
                return false;
        }
        else
            return false;
    }

    private class myLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {

            if (location == null || mv_BaiduView == null) {
                return;
            }

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .direction(100)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();

            street=location.getStreet();
            city=location.getCity();
            province =location.getProvince();

            mylatitude=location.getLatitude();
            mylongitude=location.getLongitude();
            System.out.println("mylatitude=" + mylatitude + ",mylongitude=" + mylongitude);
            if(mylatitude==0.0)
                Log.d(LOG_D,"get location banned");
            baiduMap.setMyLocationData(locData);

            //location.getLocType();
            LatLng lat = new LatLng(mylatitude, mylongitude);
            MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(lat);
            baiduMap.animateMapStatus(msu);

            for(SignInfo signInfo:MainActivity.arraylistHistorySign){
                if(isSigned(signInfo.latlng))
                    iv_barSign.setImageDrawable(getResources().getDrawable(R.mipmap.sign_bar));
            }
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        baiduMap.setMyLocationEnabled(true);
        if(!mLocationClient.isStarted())
            mLocationClient.start();
        Log.d(LOG_D,"start~~");

    }
    @Override
    public void onStop() {
        super.onStop();
        baiduMap.setMyLocationEnabled(false);
        Log.d(LOG_D,"stop~~");
        mLocationClient.stop();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_D,"destory~~");
        //int count = arraylistHistorySign.size();
        //for(int i=0;i<arraylistHistorySign.size();)
        //    arraylistHistorySign.remove(0);
        //while(!arraylistHistorySign.isEmpty())
           // arraylistHistorySign.remove(0);
        //arraylistHistorySign.removeAll();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mv_BaiduView.onDestroy();
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_D,"resume~~");
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mv_BaiduView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_D,"pause~~");
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mv_BaiduView.onPause();
    }
}
