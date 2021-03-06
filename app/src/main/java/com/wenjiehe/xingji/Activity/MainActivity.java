package com.wenjiehe.xingji.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.avos.avoscloud.AVMixpushManager;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.LogUtil;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.RefreshCallback;
import com.avos.avoscloud.feedback.FeedbackAgent;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.avos.avoscloud.im.v2.callback.AVIMSingleMessageQueryCallback;
import com.huawei.android.pushagent.api.PushManager;
import com.wenjiehe.xingji.ChatInfo;
import com.wenjiehe.xingji.Fragment.SignFragment;

import com.wenjiehe.xingji.R;
import com.wenjiehe.xingji.SignInfo;
import com.wenjiehe.xingji.Util;


import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import rx.Observable;
import rx.Observer;

import static android.R.attr.delay;
import static com.wenjiehe.xingji.Activity.ChatActivity.ChatListComp;
import static com.wenjiehe.xingji.Util.RecursionDeleteFile;
import static com.wenjiehe.xingji.Util.authorityManagement;

@RuntimePermissions
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
    public TextView tv_headerUserName;
    public static TextView tv_headerSignNum;

    public static ArrayList<SignInfo> arraylistHistorySign = new ArrayList<SignInfo>();
    public static ArrayList<ChatInfo> listChatList = new ArrayList<>();//私信联系人列表
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
        iv_headeruserPhoto = (ImageView) headerView.findViewById(R.id.iv_header_userphoto);
        tv_headerUserName = (TextView) headerView.findViewById(R.id.tv_header_username);
        tv_headerSignNum = (TextView) headerView.findViewById(R.id.tv_header_signnum);

        Intent intent = getIntent();
        //获取数据
        userName = intent.getStringExtra("username");
        signNum = intent.getIntExtra("signnum", 0);
        tv_headerUserName.setText(userName);
        tv_headerSignNum.setText(String.valueOf(signNum));

        MainActivityPermissionsDispatcher.syncUserInfoWithCheck(this);//访问位置的权限
        MainActivityPermissionsDispatcher.createFileWithCheck(this);//访问外存储器的权限

        syncUserInfo();
        createFile();
        PushManager.requestToken(MainActivity.this);//huawei push

        iv_headeruserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                Intent mainIntent = new Intent(v.getContext(),
                        MyMomentsActivity.class);
                mainIntent.putExtra("type", MyMomentsActivity.STARTFROMMINE);
                startActivity(mainIntent);
                //activity.finish();
            }
        });

        //透明状态栏
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        //Log.d("2333", "member1");
        String memberId = intent.getStringExtra(com.wenjiehe.xingji.Im.Constants.MEMBER_ID);
        if (memberId != null) {
            //Log.d("2333", intent.getStringExtra(com.wenjiehe.xingji.Im.Constants.MEMBER_ID));
            Intent startActivityIntent = new Intent(this, com.wenjiehe.xingji.Im.AVSingleChatActivity.class);
            startActivityIntent.putExtra(com.wenjiehe.xingji.Im.Constants.MEMBER_ID, intent.getStringExtra(com.wenjiehe.xingji.Im.Constants.MEMBER_ID));
            startActivity(startActivityIntent);
        }
        Log.d("Main:","onCreate");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("2333", "member2");
        String memberId = intent.getStringExtra(com.wenjiehe.xingji.Im.Constants.MEMBER_ID);
        if (memberId != null) {
            Log.d("2333", intent.getStringExtra(com.wenjiehe.xingji.Im.Constants.MEMBER_ID));
            Intent startActivityIntent = new Intent(this, com.wenjiehe.xingji.Im.AVSingleChatActivity.class);
            startActivityIntent.putExtra(com.wenjiehe.xingji.Im.Constants.MEMBER_ID, intent.getStringExtra(com.wenjiehe.xingji.Im.Constants.MEMBER_ID));
            startActivity(startActivityIntent);
        }
    }

    protected boolean filterException(Exception e) {
        if (e != null) {
            e.printStackTrace();
            return false;
        } else {
            return true;
        }
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    protected void syncUserInfo() {
        final AVUser currentUser = AVUser.getCurrentUser();
        currentUser.refreshInBackground(new RefreshCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (currentUser.get("signnum") != null)
                    signNum = (Integer) currentUser.get("signnum");
                tv_headerSignNum.setText(String.valueOf(signNum));


                if (avObject == null) {//未联网获取
                    MainActivity.upadteUserPhotoBitmap = Util.file2bitmap
                            (Environment.getExternalStorageDirectory() + "/xingji/" + AVUser.getCurrentUser()  .getUsername() + "/" + "headpicture.jpg");
                    if (MainActivity.upadteUserPhotoBitmap != null) {
                        iv_headeruserPhoto.setImageBitmap(MainActivity.upadteUserPhotoBitmap);
                    }

                } else {
                    Date date = avObject.getDate("headphotodate");
                    long userphototime = Util.getFileDateInfo
                            (Environment.getExternalStorageDirectory() + "/xingji/" + AVUser.getCurrentUser().getUsername(), "headpicture.jpg");
                    long onlinephototime = date.getTime();

                    if ((onlinephototime - userphototime) < 10000) {
                        MainActivity.upadteUserPhotoBitmap = Util.file2bitmap
                                (Environment.getExternalStorageDirectory() + "/xingji/" + AVUser.getCurrentUser().getUsername() + "/" + "headpicture.jpg");
                        iv_headeruserPhoto.setImageBitmap(MainActivity.upadteUserPhotoBitmap);
                    }
                    //Log.d("MainActivity","不需要更新");
                    else {//获取头像
                        if (!currentUser.getString("headphotoid").equals("0")) {

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
                                            Util.saveBitmap(MainActivity.upadteUserPhotoBitmap, "headpicture.jpg");
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

                FeedbackAgent agent = new FeedbackAgent(MainActivity.this);
                agent.sync();

                AVMixpushManager.registerXiaomiPush(MainActivity.this, "2882303761517484277", "5991748410277", null);
                // 设置默认打开的 Activity
                PushService.setDefaultPushCallback(MainActivity.this, MainActivity.class);

                //注册默认的消息处理逻辑
                AVIMMessageManager.registerMessageHandler(AVIMTypedMessage.class, new com.wenjiehe.xingji.Im.MessageHandler(MainActivity.this));

                /**私信用户名验证*/
                com.wenjiehe.xingji.Im.AVImClientManager.getInstance().open(AVUser.getCurrentUser().getUsername(), new AVIMClientCallback() {
                    @Override
                    public void done(final AVIMClient avimClient, AVIMException e) {
                        if (e == null) {
                            listChatList.clear();
                            //登录成功
                            AVIMConversationQuery query = avimClient.getQuery();
                            query.findInBackground(new AVIMConversationQueryCallback() {
                                @Override
                                public void done(List<AVIMConversation> convs, AVIMException e) {
                                    if (filterException(e)) {
                                        if (convs != null) {
                                            AVIMClient.setMessageQueryCacheEnable(false);
                                            //Log.d("chatbefore", String.valueOf(convs.size()));
                                            Observable.from(convs)
                                                    .subscribe(new Observer<AVIMConversation>() {

                                                        @Override
                                                        public void onCompleted() {
                                                            Log.d("rxjava", "onCompleted");
                                                        }

                                                        @Override
                                                        public void onError(Throwable e) {
                                                            Log.d("rxjava", "onError");
                                                        }

                                                        @Override
                                                        public void onNext(AVIMConversation ac) {
                                                            final List<String> l = ac.getMembers();
                                                            final Date date = ac.getLastMessageAt();
                                                            String user = AVUser.getCurrentUser().getUsername();
                                                            for (String str : l) {
                                                                if (!str.equals(AVUser.getCurrentUser().getUsername()))
                                                                    user = str;
                                                            }
                                                            //Log.d("chatbefore",user);
                                                            ac.getLastMessage(new AVIMSingleMessageQueryCallback() {
                                                                @Override
                                                                public void done(AVIMMessage avimMessage, AVIMException e) {
                                                                    if (avimMessage != null) {
                                                                        String lastMessage = avimMessage.getContent();
                                                                        String lastMessageFrom = avimMessage.getFrom();
                                                                        ChatInfo chatinfo = new ChatInfo(lastMessageFrom, lastMessage, l, date);
                                                                        int count =0;
                                                                        for (ChatInfo c:listChatList) {
                                                                            if(!c.getPersons().equals(l)){
                                                                                count++;
                                                                            }
                                                                        }
                                                                        if(count==listChatList.size()){
                                                                            listChatList.add(chatinfo);
                                                                            Collections.sort(listChatList,ChatListComp);
                                                                        }
                                                                     }

                                                                }
                                                            });
                                                            Util.downloadPicture(user, "Chats");
                                                        }
                                                    });
                                        }
                                        //convs就是获取到的conversation列表
                                        //注意：按每个对话的最后更新日期（收到最后一条消息的时间）倒序排列
                                    }
                                }
                            });
                        }

                        /*
                        if (filterException(e)) {
                                Intent intent = new Intent(AVLoginActivity.this, AVSquareActivity.class);
                                intent.putExtra(Constants.CONVERSATION_ID, Constants.SQUARE_CONVERSATION_ID);
                                intent.putExtra(Constants.ACTIVITY_TITLE, getString(R.string.square_name));
                                startActivity(intent);
                                finish();
                        }*/
                    }
                });

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (id == R.id.slide_item_sign) {
            //Toast.makeText(MainActivity.this,"重新定位中…",Toast.LENGTH_SHORT).show();
            /*ft = this.getFragmentManager().beginTransaction();
            sf = new SignFragment();
            ft.replace(R.id.content_main, sf);
            ft.commit();*/
            sf.goToMyLocation();
        } else if (id == R.id.slide_item_history) {
            /*ft = this.getFragmentManager().beginTransaction();
            hsf = new MyHistorySignFragment();
            ft.replace(R.id.content_main, hsf);
            ft.commit();*/
            new Handler().postDelayed(new Runnable(){
                public void run() {
                    Intent Intent = new Intent(MainActivity.this, MyHistorySignActivity.class);
                    startActivity(Intent);
                }
            }, 500);
        } else if (id == R.id.slide_item_nearby_moments) {
//            ft = this.getFragmentManager().beginTransaction();
//            hsf = new MyHistorySignFragment();
//            ft.replace(R.id.content_main, hsf);
//            ft.commit();
            new Handler().postDelayed(new Runnable(){
                public void run() {
                    Intent Intent = new Intent(MainActivity.this, NearbyMomentsActivity.class);
                    startActivity(Intent);
                }
            }, 500);
        } else if (id == R.id.slide_item_settings) {
            new Handler().postDelayed(new Runnable(){
                public void run() {
                    Intent Intent = new Intent(MainActivity.this, EditSettingActivity.class);
                    startActivity(Intent);
                }
            }, 500);

        } else if (id == R.id.slide_item_chat) {

            new Handler().postDelayed(new Runnable(){
                public void run() {
                    Intent Intent = new Intent(MainActivity.this, ChatActivity.class);
                    startActivity(Intent);
                }
            }, 500);
        } else if (id == R.id.slide_item_exit) {
            //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            //drawer.closeDrawer(GravityCompat.START);
            new Handler().postDelayed(new Runnable(){
                public void run() {
                    //execute the task
                    finish();
                }
            }, 600);

        }

        return true;
    }

    public static void setTv_headerSignNum() {
        tv_headerSignNum.setText(String.valueOf(signNum));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MainActivity.isUpadteUserPhoto) {
            iv_headeruserPhoto.setImageBitmap(MainActivity.upadteUserPhotoBitmap);
            MainActivity.isUpadteUserPhoto = false;
        }
        Log.d("Main:","onResume");
    }


    private long exitTime = 0;

    /**
     * 捕捉返回事件按钮
     * <p>
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

    @OnShowRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    void showRationaleForLocation(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage("请求访问位置权限，进行位置签到。")
                //.setPositiveButton("允许", (dialog, button) -> request.proceed())
                .setPositiveButton("允许", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .setCancelable(false)
                .show();
    }

    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
    void showDeniedForLocation() {
        //Toast.makeText(this, "拒绝权限", Toast.LENGTH_SHORT).show();
        Util.authorityManagement(MainActivity.this,"行迹需要定位权限，点击确定跳转至应用详情授予定位权限");

    }

    @OnNeverAskAgain(Manifest.permission.ACCESS_FINE_LOCATION)
    void showNeverAskForLocation() {
        Util.authorityManagement(MainActivity.this,"行迹需要获取定位权限，点击确定跳转至应用详情授予定位权限");
    }


   @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showRationaleForStorage(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage("请求访问存储权限，进行必要的文件存储。")
                //.setPositiveButton("允许", (dialog, button) -> request.proceed())
                .setPositiveButton("允许", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .setCancelable(false)
                .show();
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showDeniedForStorage() {
        //Toast.makeText(this, "拒绝权限", Toast.LENGTH_SHORT).show();
        Util.authorityManagement(MainActivity.this,"行迹需要获取存储权限，点击确定跳转至应用详情授予文件存储权限");

    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showNeverAskForStorage() {
        Util.authorityManagement(MainActivity.this,"行迹需要获取存储权限，点击确定跳转至应用详情授予文件存储权限");
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void createFile() {
        //创建xingji目录
        File destDir = new File(Environment.getExternalStorageDirectory() + "/xingji/");
        File destDir2 = new File(Environment.getExternalStorageDirectory() + "/xingji/" + AVUser.getCurrentUser().getUsername());
        File destDir3 = new File(Environment.getExternalStorageDirectory() + "/xingji/" + AVUser.getCurrentUser().getUsername() + "/Moments/");
        File destDir4 = new File(Environment.getExternalStorageDirectory() + "/xingji/" + AVUser.getCurrentUser().getUsername() + "/Signs/");
        File destDir5 = new File(Environment.getExternalStorageDirectory() + "/xingji/" + AVUser.getCurrentUser().getUsername() + "/Chats/");
        File destDir6 = new File(Environment.getExternalStorageDirectory() + "/xingji/" + AVUser.getCurrentUser().getUsername() + "/Temp/");
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        if (!destDir2.exists()) {
            destDir2.mkdirs();
        }
        if (!destDir3.exists()) {
            destDir3.mkdirs();
        } else {
            Util.RecursionDeleteFile(destDir3);
            destDir3.mkdirs();
        }
        if (!destDir4.exists()) {
            destDir4.mkdirs();
        }
        if (!destDir5.exists()) {
            destDir5.mkdirs();
        } else {
            Util.RecursionDeleteFile(destDir5);
            destDir5.mkdirs();
        }
        if (!destDir6.exists()) {
            destDir6.mkdirs();
        } else {
            Util.RecursionDeleteFile(destDir6);
            destDir6.mkdirs();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Main:","onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Main:","onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Main:","onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Main:","onPause");
    }
}
