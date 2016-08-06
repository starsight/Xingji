package com.wenjiehe.xingji;

import android.util.Log;

import java.io.File;
import java.util.Date;

/**
 * Created by wenjie on 16/08/06.
 */
public class Util {

    //获取文件时间
    public static long getFileDateInfo(String path,String filename){
        //  File file = new File(getActivity().getFilesDir().getAbsolutePath() + File.separator +"xingji/headpicture.jpg");
        File file = new File(path,filename);
        Date date = new Date(file.lastModified());
        Log.d("Util",String.valueOf(date.getTime()));
        if(date.getTime()==0)
            return 5000;
        return date.getTime();
    }

}
