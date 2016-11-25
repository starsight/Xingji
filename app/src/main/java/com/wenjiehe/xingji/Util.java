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

    public  static Bitmap bitmapCompress(Bitmap bitmap) {
        // http://blog.csdn.net/chzphoenix/article/details/30242315
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while ( baos.toByteArray().length / 1024>800) {
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;
            if(options<=10)
                break;
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        return BitmapFactory.decodeStream(isBm, null, null);
    }

    public static Bitmap ratio(String imgPath) {
        int[] imgInfo =getImageWidthHeight(imgPath);
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true，即只读边不读内容
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        // Get bitmap info, but notice that bitmap is null now
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 想要缩放的目标尺寸
        float hh = imgInfo[1]>>2;// 设置高度为240f时，可以明显看到图片缩小了
        float ww = imgInfo[0]>>2;// 设置宽度为120f，可以明显看到图片缩小了
        Log.d("Util",String.valueOf(bitmap.getWidth()));
        Log.d("Util",String.valueOf(hh));
        Log.d("Util",String.valueOf(ww));
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        // 开始压缩图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
        // 压缩好比例大小后再进行质量压缩
//        return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
        return bitmap;
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

    public static File scal(String path){
       // String path = fileUri.getPath();
        File outputFile = new File(path);
        long fileSize = outputFile.length();
        final long fileMaxSize = 200 * 1024;
        if (fileSize >= fileMaxSize) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            int height = options.outHeight;
            int width = options.outWidth;

            double scale = Math.sqrt((float) fileSize / fileMaxSize);
            options.outHeight = (int) (height / scale);
            options.outWidth = (int) (width / scale);
            options.inSampleSize = (int) (scale + 0.5);
            options.inJustDecodeBounds = false;

            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            outputFile = new File(createImageFile().getPath());
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(outputFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
                fos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //Log.d(, sss ok  + outputFile.length());
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }else{
                File tempFile = outputFile;
                outputFile = new File(createImageFile().getPath());
                copyFileUsingFileChannels(tempFile, outputFile);
            }

        }
        return outputFile;

    }

    public static Uri createImageFile(){
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "XingJi_ "+ timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.getExternalStorageDirectory() + "/xingji/" + AVUser.getCurrentUser().getUsername() + "/Temp/");
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Save a file: path for use with ACTION_VIEW intents
        return Uri.fromFile(image);
    }
    public static void copyFileUsingFileChannels(File source, File dest){
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            try {
                inputChannel = new FileInputStream(source).getChannel();
                outputChannel = new FileOutputStream(dest).getChannel();
                outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } finally {
            try {
                inputChannel.close();
                outputChannel.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static int[] getImageWidthHeight(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();

        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null
        /**
         *options.outHeight为原始图片的高
         */
        return new int[]{options.outWidth,options.outHeight};
    }
}
