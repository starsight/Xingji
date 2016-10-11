package com.wenjiehe.xingji.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.RefreshCallback;
import com.wenjiehe.xingji.R;
import com.wenjiehe.xingji.Fragment.SignFragment;
import com.wenjiehe.xingji.SignInfo;
import com.wenjiehe.xingji.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //private MainActivity activity;
    private FragmentTransaction ft;
    public SignFragment sf;
    //public MyHistorySignFragment hsf;

    public static String userName;
    public static int signNum;
   // public static ImageView iv_barSign;

    public ImageView iv_headeruserPhoto;
    public  TextView  tv_headerUserName;
    public static TextView tv_headerSignNum;

    public static ArrayList<SignInfo> arraylistHistorySign =new ArrayList<SignInfo>();
    public static boolean isUpadteUserPhoto = false;//头像更新
    public static Bitmap upadteUserPhotoBitmap = null;//更新的头像
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        iv_headeruserPhoto = (ImageView)headerView.findViewById(R.id.iv_header_userphoto);
        tv_headerUserName = (TextView) headerView.findViewById(R.id.tv_header_username);
        tv_headerSignNum = (TextView)headerView.findViewById(R.id.tv_header_signnum);

        Intent intent = getIntent();
        //获取数据
        userName = intent.getStringExtra("username");
        signNum = intent.getIntExtra("signnum",0);
        tv_headerUserName.setText(userName);
        tv_headerSignNum.setText(String.valueOf(signNum));

        syncUserInfo();

        iv_headeruserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                Intent mainIntent = new Intent(v.getContext(),
                        MyMomentsActivity.class);
                startActivity(mainIntent);
                //activity.finish();
            }
        });

        //透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        //创建xingji目录
        File destDir = new File(Environment.getExternalStorageDirectory()+"/xingji");
        File destDir2 = new File(Environment.getExternalStorageDirectory()+"/xingji/"+AVUser.getCurrentUser().getUsername());
        File destDir3 = new File(Environment.getExternalStorageDirectory()+"/xingji/"+AVUser.getCurrentUser().getUsername()+"/Moments");
        File destDir4 = new File(Environment.getExternalStorageDirectory()+"/xingji/"+AVUser.getCurrentUser().getUsername()+"/Signs");
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        if (!destDir2.exists()) {
            destDir2.mkdirs();
        }
        if (!destDir3.exists()) {
            destDir3.mkdirs();
        }
        if (!destDir4.exists()) {
            destDir4.mkdirs();
        }
    }

    private void syncUserInfo() {
        final AVUser currentUser = AVUser.getCurrentUser();
        currentUser.refreshInBackground(new RefreshCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (currentUser.get("signnum") != null)
                    signNum = (Integer) currentUser.get("signnum");
                tv_headerSignNum.setText(String.valueOf(signNum));

                /*AVObject todo = AVObject.createWithoutData("headpicture",currentUser.getString("headphotoid"));
                //todo.put("headpicture",file);
                todo.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {

                    }
                });*/
                if(avObject ==null){//未联网获取
                    MainActivity.upadteUserPhotoBitmap = Util.file2bitmap
                            (Environment.getExternalStorageDirectory() + "/xingji/"+AVUser.getCurrentUser().getUsername()+"/"+"headpicture.jpg");
                    if (MainActivity.upadteUserPhotoBitmap!=null) {
                        iv_headeruserPhoto.setImageBitmap(MainActivity.upadteUserPhotoBitmap);
                    }

                }
                else {
                    Date date = avObject.getDate("headphotodate");
                    long userphototime = Util.getFileDateInfo
                            (Environment.getExternalStorageDirectory() + "/xingji/"+AVUser.getCurrentUser().getUsername(), "headpicture.jpg");
                    long onlinephototime = date.getTime();
                    //Log.d("MainActivity-2-",String.valueOf(userphototime));
                    //Log.d("MainActivity-2-",String.valueOf(date));
                    //Log.d("MainActivity-2-",String.valueOf(userphototime));
                    //Log.d("MainActivity-2-",String.valueOf(onlinephototime));
                    if ((onlinephototime - userphototime) < 10000) {
                        MainActivity.upadteUserPhotoBitmap = Util.file2bitmap
                                (Environment.getExternalStorageDirectory() + "/xingji/"+AVUser.getCurrentUser().getUsername()+"/"+"headpicture.jpg");
                        iv_headeruserPhoto.setImageBitmap(MainActivity.upadteUserPhotoBitmap);
                    }
                    //Log.d("MainActivity","不需要更新");
                    else {//获取头像
                        if(!currentUser.getString("headphotoid").equals("0")) {

                            AVObject todo = AVObject.createWithoutData("headpicture", currentUser.getString("headphotoid"));
                            todo.fetchInBackground(new GetCallback<AVObject>() {
                                @Override
                                public void done(AVObject avObject, AVException e) {
                                    AVFile file = avObject.getAVFile("headpicture");
                                    file.getDataInBackground(new GetDataCallback() {
                                        @Override
                                        public void done(byte[] bytes, AVException e) {
                                            // bytes 就是文件的数据流
                                            MainActivity.upadteUserPhotoBitmap = Util.bytes2Bimap(bytes);
                                            iv_headeruserPhoto.setImageBitmap(MainActivity.upadteUserPhotoBitmap);
                                            Util.saveBitmap(MainActivity.upadteUserPhotoBitmap,"headpicture.jpg");
                                        }
                                    }, new ProgressCallback() {
                                        @Override
                                        public void done(Integer integer) {
                                            // 下载进度数据，integer 介于 0 和 100。
                                        }
                                    });
                                    //String title = avObject.getString("title");// 读取 title
                                    //String content = avObject.getString("content");// 读取 content
                                }
                            });
                        }
                    }
                /*if (!currentUser.getString("headphotoid").equals("0")) {
                    AVQuery<AVObject> avQuery = new AVQuery<>("headpicture");
                    avQuery.getInBackground(currentUser.getString("headphotoid"), new GetCallback<AVObject>() {
                        @Override
                        public void done(AVObject avObject, AVException e) {

                        }
                    });
                }*/
                }
                //Log.d("xingji-choose",String.valueOf(signNum));
                /*ft = getFragmentManager().beginTransaction();
                hsf = new MyHistorySignFragment();
                ft.replace(R.id.content_main, hsf);
                ft.commit();*/
                ft = getFragmentManager().beginTransaction();
                sf = new SignFragment();
                ft.replace(R.id.content_main, sf);
                ft.commit();
            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.slide_item_sign) {
            ft = this.getFragmentManager().beginTransaction();
            sf = new SignFragment();
            ft.replace(R.id.content_main, sf);
            ft.commit();
        } else if (id == R.id.slide_item_history) {
            /*ft = this.getFragmentManager().beginTransaction();
            hsf = new MyHistorySignFragment();
            ft.replace(R.id.content_main, hsf);
            ft.commit();*/
            Intent Intent = new Intent(this,
                    MyHistorySignActivity.class);
            startActivity(Intent);
        } else if (id == R.id.slide_item_nearby_moments) {
//            ft = this.getFragmentManager().beginTransaction();
//            hsf = new MyHistorySignFragment();
//            ft.replace(R.id.content_main, hsf);
//            ft.commit();
            Intent Intent = new Intent(this,
                    NearbyMomentsActivity.class);
            startActivity(Intent);
        } else if (id == R.id.slide_item_settings) {
            Intent Intent = new Intent(this,
                    EditSettingActivity.class);
            startActivity(Intent);
        } else if (id == R.id.slide_item_exit) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static void setTv_headerSignNum() {
        tv_headerSignNum.setText(String.valueOf(signNum));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(MainActivity.isUpadteUserPhoto==true){
            iv_headeruserPhoto.setImageBitmap(MainActivity.upadteUserPhotoBitmap);
            MainActivity.isUpadteUserPhoto = false;
        }
    }


    private long exitTime = 0;

    /**
     * 捕捉返回事件按钮
     *
     * 因为此 Activity 继承 TabActivity 用 onKeyDown 无响应，所以改用 dispatchKeyEvent
     * 一般的 Activity 用 onKeyDown 就可以了
     */

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                this.exitApp();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 退出程序
     */
    private void exitApp() {
        // 判断2次点击事件时间
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }
}
