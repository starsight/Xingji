package com.wenjiehe.xingji.Activity;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wenjiehe.xingji.R;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

public class ChangeUserInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_info);


        Toolbar toolbar = (Toolbar) findViewById(R.id.change_userinfo_toolbar);
        setSupportActionBar(toolbar);
    }
}
