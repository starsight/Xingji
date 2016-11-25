package com.wenjiehe.xingji;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;

import static android.R.attr.path;
import static android.R.string.ok;

/**
 * Created by wenjie on 16/08/06.
 */
public class Util {

    //获取文件时间
    public static long getFileDateInfo(String path, String filename) {
        //  File file = new File(getActivity().getFilesDir().getAbsolutePath() + File.separator +"xingji/headpicture.jpg");
        File file = new File(path, filename);
        Date date = new Date(file.lastModified());
        Log.d("Util", String.valueOf(date.getTime()));
        if (date.getTime() == 0)
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

    public static void saveBitmap(Bitmap bm, String name) {
        //Log.e(TAG, "保存图片");

        File f = new File(Environment.getExternalStorageDirectory() + "/xingji/" + AVUser.getCurrentUser().getUsername() + "/" + name);
        //File f = new File(this.getFilesDir().getAbsolutePath() + File.separator +"xingji/headpicture.jpg");
        Date date = new Date(f.lastModified());
        Log.d("xing--", date.toString());
        //Log.d("xing--",getActivity().getFilesDir().getAbsolutePath() + File.separator +"xingji/headpicture3");
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            if (name.equals("headpicture.jpg"))
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
        return BitmapFactory.decodeFile(filePath);
    }

    public static boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    public static boolean hasFile(String path) {
        File f = new File(path);
        return f.exists();
    }

    public static void copyFile(String oldPath, String newPath) {//把本机上的签到图片拷贝到xingji目录下，需优化！
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void downloadPicture(String usernameTmp, String pathTmp) {
        final String str = usernameTmp;
        final String path = pathTmp;
        if (!hasFile(Environment.getExternalStorageDirectory() + "/xingji/" +
                AVUser.getCurrentUser().getUsername() + "/" + pathTmp + "/" + usernameTmp)) {//没有这个头像
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
                                Bitmap bitmap = Util.bytes2Bimap(bytes);
                                Util.saveBitmap(bitmap, path + "/" + str);
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
        }
    }

    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return context.getString(R.string.version_name);//+ version
        } catch (Exception e) {
            e.printStackTrace();
            return context.getString(R.string.can_not_find_version_name);
        }
    }
}
