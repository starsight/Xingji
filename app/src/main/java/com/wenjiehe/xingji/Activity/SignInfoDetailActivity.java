package com.wenjiehe.xingji.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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

import com.wenjiehe.xingji.R;
import com.wenjiehe.xingji.SignInfo;
import com.wenjiehe.xingji.Util;

public class SignInfoDetailActivity extends AppCompatActivity {

    //private ImageView iv_detail_signinfophoto;
    private TextView tv_detail_signinfolocation;
    private TextView tv_detail_signinfoevent;
    private ImageView iv_detail_signinfophoto;

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

        Intent intent = getIntent();

        SignInfo item = (SignInfo) intent.getParcelableExtra("SignInfo");
        //iv_detail_signinfophoto.setImageResource(item.getPhotoId());
        tv_detail_signinfolocation.setText(item.getLocation());
        tv_detail_signinfoevent.setText(item.getEvent());

        if (!item.getPhotoId().equals("0")) {
            Bitmap bm = Util.file2bitmap(Environment.getExternalStorageDirectory() + "/xingji/demo/" + item.getPhotoId());
            iv_detail_signinfophoto.setImageBitmap(bm);
            iv_detail_signinfophoto.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO Auto-generated method stub
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
