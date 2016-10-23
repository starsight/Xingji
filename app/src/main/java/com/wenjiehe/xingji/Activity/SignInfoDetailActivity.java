package com.wenjiehe.xingji.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.wenjiehe.xingji.R;
import com.wenjiehe.xingji.SignInfo;
import com.wenjiehe.xingji.Util;

import java.util.List;

import butterknife.Bind;

import static com.wenjiehe.xingji.Activity.MyMomentsActivity.STARTFROMOTHERS;
import static com.wenjiehe.xingji.Activity.NearbyMomentsActivity.arraylistNearbyMoments;
import static com.wenjiehe.xingji.R.id.tv_news_owns;
import static com.wenjiehe.xingji.Util.hasFile;

public class SignInfoDetailActivity extends AppCompatActivity {

    //private ImageView iv_detail_signinfophoto;
    private TextView tv_detail_signinfolocation;
    private TextView tv_detail_signinfoevent;
    private ImageView iv_detail_signinfophoto;
    private ImageView iv_detail_userphoto;
    private TextView tv_detail_owns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_info_detail);

        //透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_signdetail_toolbar);
        setSupportActionBar(toolbar);

        //iv_detail_signinfophoto= (ImageView) findViewById(R.id.iv_detail_signinfophoto);
        tv_detail_signinfolocation = (TextView) findViewById(R.id.tv_detail_signinfolocation);
        tv_detail_signinfoevent = (TextView) findViewById(R.id.tv_detail_signinfoevent);
        iv_detail_signinfophoto = (ImageView) findViewById(R.id.iv_detail_signinfophoto);
        iv_detail_userphoto = (ImageView) findViewById(R.id.iv_detail_userphoto);
        tv_detail_owns = (TextView) findViewById(R.id.tv_detail_owns);

        Intent intent = getIntent();

        final SignInfo item = (SignInfo) intent.getParcelableExtra("SignInfo");
        //iv_detail_signinfophoto.setImageResource(item.getPhotoId());
        tv_detail_signinfolocation.setText(item.getLocation());
        tv_detail_signinfoevent.setText(item.getEvent());

        if (!item.getPhotoId().equals("0")) {
            Bitmap bm = Util.file2bitmap(Environment.getExternalStorageDirectory() + "/xingji/" + AVUser.getCurrentUser().getUsername() + "/Moments/" + item.getPhotoId());
            iv_detail_signinfophoto.setImageBitmap(bm);
            iv_detail_signinfophoto.setVisibility(View.VISIBLE);
        }

        String str = Environment.getExternalStorageDirectory() + "/xingji/" +
                AVUser.getCurrentUser().getUsername() + "/Moments/" + item.username;
        if (hasFile(str)) {
            Bitmap b = Util.file2bitmap(str);
            iv_detail_userphoto.setImageBitmap(b);
        } else
            iv_detail_userphoto.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon));
        tv_detail_owns.setText(item.username);


        iv_detail_userphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AVQuery<AVObject> query = new AVQuery<>("_User");
                query.whereEqualTo("username", item.username);
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (list == null) {
                            Toast.makeText(SignInfoDetailActivity.this, "咦，查无此人诶，刷新试试~", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (AVObject avObject : list) {
                            Intent intent = new Intent(SignInfoDetailActivity.this, MyMomentsActivity.class);
                            intent.putExtra("username", item.username);
                            intent.putExtra("userobjectid", avObject.getObjectId());
                            intent.putExtra("type", MyMomentsActivity.STARTFROMOTHERS);
                            //intent.putExtra("photostr", str);

                            SignInfoDetailActivity.this.startActivity(intent);
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
