package com.wenjiehe.xingji.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.bumptech.glide.Glide;
import com.wenjiehe.xingji.R;
import com.wenjiehe.xingji.SignInfo;
import com.wenjiehe.xingji.SignLocation;
import com.wenjiehe.xingji.Util;
import com.yuyh.library.imgsel.ImageLoader;
import com.yuyh.library.imgsel.ImgSelActivity;
import com.yuyh.library.imgsel.ImgSelConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static com.baidu.location.h.j.O;
import static com.baidu.location.h.j.u;

public class SignActivity extends AppCompatActivity {

    private EditText et_activity_sign;
    private TextView tv_activity_sign_location;
    private TextView tv_activity_sign_send;
    private ImageView iv_activity_sign_photo;

    private String username, street, city, province, date, event = "到此一游", locDescribe;
    private double latitude = 0.0;
    private double longitude = 0.0;
    //private LatLng point= null;
    private boolean isWithPhoto = false;
    private String photoDis;

    private boolean isSigning = false;
    private final static int RESQUESTCODE = 1;
    private final static int RESULTCODE_TO_INTENTRESULT = 2;

    //private final static int RESQUESTCAMERA = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        //透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_sign_toolbar);
        setSupportActionBar(toolbar);

        et_activity_sign = (EditText) findViewById(R.id.et_activity_sign);
        tv_activity_sign_location = (TextView) findViewById(R.id.tv_activity_sign_location);
        tv_activity_sign_send = (TextView) findViewById(R.id.tv_activity_sign_send);
        iv_activity_sign_photo = (ImageView) findViewById(R.id.iv_activity_sign_photo);


        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        latitude = intent.getDoubleExtra("latitude", (double) 0.0);
        longitude = intent.getDoubleExtra("longitude", (double) 0.0);
        date = intent.getStringExtra("date");
        province = intent.getStringExtra("province");
        city = intent.getStringExtra("city");
        street = intent.getStringExtra("street");
        event = intent.getStringExtra("event");
        locDescribe = intent.getStringExtra("locdescribe");
        //Log.d("event0",intent.getStringExtra("event"));
        //point = new LatLng(latitude, longitude);
        tv_activity_sign_location.setText(locDescribe);

        if (intent.getIntExtra("type", 1) == 2) {
            iv_activity_sign_photo.setVisibility(View.VISIBLE);
            isWithPhoto = true;
            //Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //startActivityForResult(intent2, RESQUESTCAMERA);
            withphoto();
        }

        iv_activity_sign_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                withphoto();
            }
        });

        tv_activity_sign_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_activity_sign_send.setVisibility(View.GONE);
                if (isSigning == true) {
                    Toast.makeText(SignActivity.this, "正在签到···", Toast.LENGTH_SHORT).show();
                    return;
                }
                isSigning = true;
                if ((longitude == 0.0) && (latitude == 0.0)) {
                    Toast.makeText(SignActivity.this, "网络不畅……", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (String.valueOf(et_activity_sign.getText()) != null && !String.valueOf(et_activity_sign.getText()).equals(""))
                    event = String.valueOf(et_activity_sign.getText());
                Log.d("event3", event);
                /*SignInfo signInfoTmp = new SignInfo(new LatLng(latitude, longitude),date,
                        new SignLocation(province,city,street,locDescribe),"0");*/
                final AVObject userSign = new AVObject("signInfo");
                userSign.put("username", username);
                userSign.put("latitude", latitude);
                userSign.put("longitude", longitude);
                userSign.put("date", date);
                userSign.put("province", province);
                userSign.put("city", city);
                userSign.put("street", street);
                userSign.put("event", event);
                userSign.put("locdescribe", locDescribe);
                if (isWithPhoto) {
                    AVFile file = null;
                    try {
                        file = AVFile.withAbsoluteLocalPath("signphoto.jpg",
                                photoDis);
                        userSign.put("signphoto", file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                userSign.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            MainActivity.signNum += 1;
                            MainActivity.setTv_headerSignNum();
                            AVUser.getCurrentUser().put("signnum", MainActivity.signNum);
                            AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    SignInfo signinfotmp;
                                    if (isWithPhoto) {
                                        //Log.d("photo",userSign.getAVFile("signphoto").getObjectId());
                                        signinfotmp = new SignInfo(new LatLng(latitude, longitude), date,
                                                new SignLocation(province, city, street, locDescribe), event,
                                                userSign.getObjectId(), userSign.getAVFile("signphoto").getObjectId());
                                        MainActivity.arraylistHistorySign.add(signinfotmp);
                                        Util.copyFile(photoDis, Environment.getExternalStorageDirectory() +
                                                "/xingji/" + AVUser.getCurrentUser().getUsername() + "/Signs/" + userSign.getAVFile("signphoto").getObjectId());
                                    } else {
                                        signinfotmp = new SignInfo(new LatLng(latitude, longitude), date,
                                                new SignLocation(province, city, street, locDescribe), event, userSign.getObjectId());
                                        MainActivity.arraylistHistorySign.add(signinfotmp);//加入到所有签到的序列中

                                    }
                                    SignInfo.writeSignInfoToFile(getFilesDir().getAbsolutePath() +
                                            File.separator + "xingji/.historySign", MainActivity.arraylistHistorySign);

                                    //在用户的moments状态表更新一条记录
                                    AVObject moments = new AVObject("u" + AVUser.getCurrentUser().getObjectId());
                                    JSONObject jo = new JSONObject();
                                    try {
                                        jo.put("type", "0");
                                        jo.put("info", signinfotmp.getLocation());
                                        jo.put("objectid", userSign.getObjectId());
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }
                                    //Log.d("33",jo.toString());
                                    moments.put("moments", jo);
                                    moments.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(AVException e) {
                                            Toast.makeText(SignActivity.this, city + street + " 签到成功~", Toast.LENGTH_SHORT).show();

                                            Intent intent = new Intent();
                                            //intent.putExtra("result", result);
                                            setResult(RESULTCODE_TO_INTENTRESULT, intent);
                                            finish();// 结束当前的Activity的声明周期
                                        }
                                    });
                                }
                            });

                        } else {
                            // 失败的话，请检查网络环境以及 SDK 配置是否正确
                        }
                    }
                });
            }
        });

    }

    private void withphoto() {
        // 自定义图片加载器
        ImageLoader loader = new ImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                // TODO 在这边可以自定义图片加载库来加载ImageView，例如Glide、Picasso、ImageLoader等
                Glide.with(context).load(path).into(imageView);
            }
        };
        // 配置选项
        ImgSelConfig config = new ImgSelConfig.Builder(loader)
                // 是否多选
                .multiSelect(false)
                // “确定”按钮背景色
                //.btnBgColor(Color.parseColor("#6495ED"))
                .btnBgColor(Color.parseColor("#006495ED"))
                // “确定”按钮文字颜色
                .btnTextColor(Color.parseColor("#006495ED"))
                // 标题
                .title("图片")
                // 标题文字颜色
                .titleColor(Color.WHITE)
                // TitleBar背景色
                .titleBgColor(Color.parseColor("#6495ED"))
                // 裁剪大小。needCrop为true的时候配置
                //.cropSize(1, 1, 200, 200)
                .needCrop(false)
                // 第一个是否显示相机
                .needCamera(true)
                // 最大选择图片数量
                .maxNum(1)
                // 使用沉浸式状态栏
                .statusBarColor(Color.parseColor("#6495ED"))
                .build();

        // 跳转到图片选择器
        ImgSelActivity.startActivity(this, config, RESQUESTCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 图片选择结果回调
        if (requestCode == RESQUESTCODE && resultCode == RESULT_OK && data != null) {
            List<String> pathList = data.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT);
            for (String path : pathList) {
                photoDis = path;
                iv_activity_sign_photo.setImageBitmap(Util.file2bitmap(path));
                //tvResult.append(path + "\n");
            }
        }
        /*if (resultCode == Activity.RESULT_OK) {
            if (Util.hasSdcard()) {
                Bundle bundle = data.getExtras();
                Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
                FileOutputStream b = null;
                //File file = new File("/sdcard/myImage/");
                //file.mkdirs();// 创建文件夹
                //String fileName = "/sdcard/myImage/111.jpg";
                try {
                    b = new FileOutputStream(fileName);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        b.flush();
                        b.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                iv_activity_sign_photo.setImageBitmap(bitmap);
                //((ImageView) findViewById(R.id.imageView)).setImageBitmap(bitmap);// 将图片显示在ImageView里
            }
        }*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
