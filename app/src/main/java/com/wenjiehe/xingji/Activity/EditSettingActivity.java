package com.wenjiehe.xingji.Activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.wenjiehe.xingji.R;
import com.wenjiehe.xingji.ScoreUtils;

import java.util.ArrayList;
import java.util.StringTokenizer;

import static com.baidu.platform.comapi.util.f.i;
import static com.wenjiehe.xingji.ScoreUtils.queryInstalledMarketPkgs;

public class EditSettingActivity extends AppCompatActivity {


    TextView tv_activity_edit_setting_about;
    TextView tv_activity_edit_setting_access;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_setting);


        tv_activity_edit_setting_about = (TextView)findViewById(R.id.tv_activity_edit_setting_about);
        tv_activity_edit_setting_access = (TextView)findViewById(R.id.tv_activity_edit_setting_access);


        tv_activity_edit_setting_access.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*ArrayList<String> arr  = queryInstalledMarketPkgs(EditSettingActivity.this);
                if(arr.isEmpty())
                    Log.d("ww","kongde");
                else
                    Log.d("ww","bukongde");*/
                Intent intent = new Intent();
                Uri uri = Uri.parse("market://details?id="+getPackageName());
                intent = new Intent(Intent.ACTION_VIEW,uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }
}
