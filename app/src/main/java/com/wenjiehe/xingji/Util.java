package com.wenjiehe.xingji;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.avos.avoscloud.AVUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public static Bitmap bytes2Bimap(byte[] b) {
              if (b.length != 0) {
                         return BitmapFactory.decodeByteArray(b, 0, b.length);
                     } else {
                         return null;
                     }
             }

    public static void  saveBitmap(Bitmap bm,String name) {
        //Log.e(TAG, "保存图片");

        File f = new File(Environment.getExternalStorageDirectory() +"/xingji/"+AVUser.getCurrentUser().getUsername()+"/"+name);
        //File f = new File(this.getFilesDir().getAbsolutePath() + File.separator +"xingji/headpicture.jpg");
        Date date = new Date(f.lastModified());
        Log.d("xing--",date.toString());
        //Log.d("xing--",getActivity().getFilesDir().getAbsolutePath() + File.separator +"xingji/headpicture3");
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            if(name.equals("headpicture.jpg"))
            bm.compress(Bitmap.CompressFormat.JPEG, 65, out);
            else
                bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            //Log.i(TAG, "已经保存");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static Bitmap file2bitmap(String filePath) {
        return  BitmapFactory.decodeFile(filePath);
    }

    public static boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    public static boolean hasFile(String path) {
        File f = new File(path);
        return f.exists();
    }

    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }




}
