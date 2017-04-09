package com.wenjiehe.xingji.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.feedback.FeedbackAgent;
import com.wenjiehe.xingji.R;
import com.wenjiehe.xingji.Util;


public class EditSettingActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{


    TextView tv_activity_edit_setting_about;
    TextView tv_activity_edit_setting_access;
    TextView tv_activity_edit_setting_feedback;


    Switch sw_edit_showsign,sw_edit_privacy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_setting);

        //透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.edit_userinfo_toolbar);
        setSupportActionBar(toolbar);

        tv_activity_edit_setting_about = (TextView) findViewById(R.id.tv_activity_edit_setting_about);
        tv_activity_edit_setting_access = (TextView) findViewById(R.id.tv_activity_edit_setting_access);
        tv_activity_edit_setting_feedback = (TextView) findViewById(R.id.tv_activity_edit_setting_feedback);
        sw_edit_privacy = (Switch) findViewById(R.id.sw_edit_privacy);
        sw_edit_showsign = (Switch) findViewById(R.id.sw_edit_showsign);


        tv_activity_edit_setting_access.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        tv_activity_edit_setting_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(EditSettingActivity.this)
                        .setTitle("行迹"+ Util.getVersion(EditSettingActivity.this))
                .setMessage("    这是一款位置签到软件，目前还在开发中，即日起进行公测。"+
                        "主要功能有位置签到，签到分享，签到数据管理等。\n"+
                "    该产品是由独立开发者制作，若软件存在问题还请及时反馈给开发者进行修复。感谢您的使用！")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
            }
        });

        tv_activity_edit_setting_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedbackAgent agent = new FeedbackAgent(EditSettingActivity.this);
                agent.startDefaultThreadActivity();
            }
        });



        sw_edit_privacy.setChecked(AVUser.getCurrentUser().getBoolean("isShareSignInfo"));
        sw_edit_showsign.setChecked(AVUser.getCurrentUser().getBoolean("isShowOthersOnMap"));

        sw_edit_privacy.setOnCheckedChangeListener(this);
        sw_edit_showsign.setOnCheckedChangeListener(this);


    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()){
            case R.id.sw_edit_privacy:
                if(compoundButton.isChecked()) {
                    //Toast.makeText(this, "打开", Toast.LENGTH_SHORT).show();
                    AVUser.getCurrentUser().put("isShareSignInfo",true);
                }
                else {
                    if(sw_edit_showsign.isChecked()) {
                        sw_edit_showsign.setChecked(false);
                        return;//sw_edit_showsign.setChecked(false)中完成………………1
                    }
                    else {
                        AVUser.getCurrentUser().put("isShareSignInfo",false);
                    }
                    //AVUser.getCurrentUser().put("isShowOthersOnMap",false);
                }
                break;
            case R.id.sw_edit_showsign:
                if(compoundButton.isChecked()){
                    if(sw_edit_privacy.isChecked()) {
                        AVUser.getCurrentUser().put("isShowOthersOnMap",true);
                    }
                    else {
                        Toast.makeText(EditSettingActivity.this, "需要先允许共享签到信息", Toast.LENGTH_SHORT).show();
                        compoundButton.setChecked(false);
                        return;
                    }
                }
                else{
                    //完成…………1中的同步
                    if(!sw_edit_privacy.isChecked())
                    AVUser.getCurrentUser().put("isShareSignInfo",false);
                    AVUser.getCurrentUser().put("isShowOthersOnMap",false);
                }
                break;
        }

        AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if(e==null)
                    ;
                    //Toast.makeText(EditSettingActivity.this, "设置更新成功", Toast.LENGTH_SHORT).show();
                else {
                    switch (e.getCode()) {
                        default:
                            Toast.makeText(EditSettingActivity.this,EditSettingActivity.this
                                    .getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        });
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
