package com.wenjiehe.xingji.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wenjiehe.xingji.R;
import com.wenjiehe.xingji.SignInfo;

public class SignInfoDetailActivity extends AppCompatActivity {

    //private ImageView iv_detail_signinfophoto;
    private TextView tv_detail_signinfolocation;
    private TextView tv_detail_signinfoevent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_info_detail);

        //iv_detail_signinfophoto= (ImageView) findViewById(R.id.iv_detail_signinfophoto);
        tv_detail_signinfolocation= (TextView) findViewById(R.id.tv_detail_signinfolocation);
        tv_detail_signinfoevent= (TextView) findViewById(R.id.tv_detail_signinfoevent);

        Intent intent=getIntent();

        SignInfo item= (SignInfo) intent.getParcelableExtra("SignInfo");
        //iv_detail_signinfophoto.setImageResource(item.getPhotoId());
        tv_detail_signinfolocation.setText(item.getLocation());
        tv_detail_signinfoevent.setText(item.getEvent());
    }

}
