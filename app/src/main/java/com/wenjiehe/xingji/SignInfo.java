package com.wenjiehe.xingji;

import android.content.Context;
import android.location.Location;
import android.os.Environment;

import com.baidu.mapapi.model.LatLng;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by wenjie on 16/06/13.
 */
public class SignInfo {

    public LatLng latlng;
    public SignLocation location;
    public String date;
    public String event = "到此一游~";
    public SignInfo(LatLng latlng, String date,SignLocation location){
        this.latlng = latlng;
        this.date = date;
        this.location = location;
    }

    public SignInfo(LatLng latlng, String date,SignLocation location,String event){
        this.latlng = latlng;
        this.date = date;
        this.location = location;
        this.event = event;
    }

    public static ArrayList<SignInfo>  readSignInfo(Context context,ArrayList<SignInfo> arraylistHistorySign){
        // System.out.println(this.getFilesDir().getAbsolutePath() + File.separator+"xingji");
        System.out.println(Environment.getExternalStorageDirectory());
        File xingjiDir = new File(context.getFilesDir().getAbsolutePath() + File.separator+"xingji");
        if(!xingjiDir.exists()){
            xingjiDir.mkdir();
        }
        File file = new File(context.getFilesDir().getAbsolutePath() + File.separator +"xingji/.historySign");

        if(file.exists()){
            FileReader fr = null ;
            BufferedReader br;
            try {
                fr = new FileReader(file);
                br = new BufferedReader (fr);
                String s,datetmp,provincetmp,citytmp,streettmp,eventtmp;
                Double lattmp,lngtmp;
                while((s=br.readLine())!=null){
                    String[] str = s.split(";");
                    // for(int i=0;i<str.length;i++){
                    String[] strtmp = str[0].split("=");//lat
                    //Log.d("--xingji--lat",strtmp[1]);
                    lattmp=Double.parseDouble(strtmp[1]);
                    strtmp = str[1].split("=");//lng
                    lngtmp=Double.parseDouble(strtmp[1]);
                    //Log.d("--xingji--lat-lng",strtmp[1]);
                    strtmp = str[2].split("=");//date
                    datetmp = strtmp[1];
                    strtmp = str[3].split("=");//province
                    provincetmp = strtmp[1];
                    strtmp = str[4].split("=");//city
                    citytmp = strtmp[1];
                    strtmp = str[5].split("=");//street
                    streettmp = strtmp[1];
                    strtmp = str[6].split("=");//event
                    eventtmp = strtmp[1];
                    arraylistHistorySign.add(new SignInfo
                            (new LatLng(lattmp,lngtmp),datetmp,new SignLocation(provincetmp,citytmp,streettmp),eventtmp));

                }

                return arraylistHistorySign;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //arraylistHistorySign.add(new SignInfo(new LatLng(34.0,109),"2016-6-9 15:52:55",new SignLocation("江苏省","南京市","北京东路")));
        }
        return  null;
    }

}
