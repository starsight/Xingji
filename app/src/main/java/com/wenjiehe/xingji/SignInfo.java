package com.wenjiehe.xingji;

import android.content.Context;
import android.location.Location;
import android.os.Environment;
import android.util.Log;

import com.baidu.mapapi.model.LatLng;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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
    public String objectId = "0";

    public SignInfo(LatLng latlng, String date,SignLocation location,String objectId){
        this.latlng = latlng;
        this.date = date;
        this.location = location;
        this.objectId = objectId;
    }

    public SignInfo(LatLng latlng, String date,SignLocation location,String event,String objectId){
        this.latlng = latlng;
        this.date = date;
        this.location = location;
        this.objectId = objectId;
        this.event = event;
    }

    public String getLocation() {
        StringBuilder sb = new StringBuilder();
        sb.append(location.province);
        sb.append(location.city);
        sb.append(location.street);
        //sb.append(location.province);
        return sb.toString();
    }

    public static void  readSignInfoFromFile(Context context, ArrayList<SignInfo> arraylistHistorySign){
        // System.out.println(this.getFilesDir().getAbsolutePath() + File.separator+"xingji");
        //arraylistHistorySign = new ArrayList<SignInfo>();
        int count = arraylistHistorySign.size();
        for(int i=0;i<count;i++) {
            arraylistHistorySign.remove(0);
            Log.d("signinfo",String.valueOf(i));
        }

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
                String s,datetmp,provincetmp,citytmp,streettmp,eventtmp,locDescribetmp,objectIdtmp;
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
                    strtmp = str[7].split("=");//locDescribe
                    locDescribetmp = strtmp[1];
                    strtmp = str[8].split("=");//objectId
                    objectIdtmp = strtmp[1];
                    arraylistHistorySign.add(new SignInfo
                            (new LatLng(lattmp,lngtmp),datetmp,
                                    new SignLocation(provincetmp,citytmp,streettmp,locDescribetmp),
                                    eventtmp,objectIdtmp));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //arraylistHistorySign.add(new SignInfo(new LatLng(34.0,109),"2016-6-9 15:52:55",new SignLocation("江苏省","南京市","北京东路")));
        }
    }


    /*
    * 写入签到数据到文件
    * */
    public static void writeSignInfoToFile(String path ,ArrayList<SignInfo> signinfo){
        File file = new File(path);

        if(file.exists()) {
            file.delete();
        }
        FileWriter fw = null;
        BufferedWriter bw;
        try {
            fw = new FileWriter(file,false);
            bw = new BufferedWriter(fw);
            for(SignInfo signinfobuffer : signinfo){
                StringBuilder sb = new StringBuilder();
                sb.append("latitude=");
                sb.append(String.valueOf(signinfobuffer.latlng.latitude));
                sb.append(";longitude=");
                sb.append(String.valueOf(signinfobuffer.latlng.longitude));
                sb.append(";date=");
                sb.append(signinfobuffer.date);
                sb.append(";province=");
                sb.append(signinfobuffer.location.province);
                sb.append(";city=");
                sb.append(signinfobuffer.location.city);
                sb.append(";street=");
                sb.append(signinfobuffer.location.street);
                sb.append(";event=");
                sb.append(signinfobuffer.event);
                sb.append(";locDescribe=");
                sb.append(signinfobuffer.location.locDescribe);
                sb.append(";objectId=");
                sb.append(signinfobuffer.objectId);
                sb.append("\n");
                //Log.d(LOG_D,sb.toString());
                bw.append(sb);
            }
            bw.close();
            fw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
