package com.wenjiehe.xingji.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.wenjiehe.xingji.R;


public class EditSettingActivity extends AppCompatActivity {


    TextView tv_activity_edit_setting_about;
    TextView tv_activity_edit_setting_access;

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


        tv_activity_edit_setting_about = (TextView) findViewById(R.id.tv_activity_edit_setting_about);
        tv_activity_edit_setting_access = (TextView) findViewById(R.id.tv_activity_edit_setting_access);


        tv_activity_edit_setting_access.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*ArrayList<String> arr  = queryInstalledMarketPkgs(EditSettingActivity.this);
                if(arr.isEmpty())
                    Log.d("ww","kongde");
                else
                    Log.d("ww","bukongde");*/
                Intent intent = new Intent();
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
                        .setTitle("关于行迹")
                .setMessage("  这是一款位置签到软件，目前还在开发中，即日起进行公测。 \n" +
                        "       主要功能有位置签到，签到分享，签到数据管理等。\n"+
                "  该产品是由独立开发者制作，若软件存在问题还请及时反馈给开发者进行修复。感谢您的使用！")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
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
