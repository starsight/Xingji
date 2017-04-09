package com.wenjiehe.xingji.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.RequestPasswordResetCallback;
import com.wenjiehe.xingji.R;
import com.wenjiehe.xingji.Util;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.wenjiehe.xingji.R.id.et_regEmail;

/**
 * Created by wenjie on 16/07/22.
 */
public class ForgetPasswordActivity  extends AppCompatActivity{

    @Bind(R.id.activity_forgetpasswd_toolbar)
    Toolbar toolbar;
    @Bind(R.id.edit_forgetpasswd)
    EditText edit_forgetpasswd;
    @Bind(R.id.bt_forgetpasswd)
    Button bt_forgetpasswd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword);

        //透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        bt_forgetpasswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = edit_forgetpasswd.getText().toString();

                if(Util.isEmail(email)){
                    AVUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
                        public void done(AVException e) {
                            if (e == null) {
                                // 已发送一份重置密码的指令到用户的邮箱
                                Toast.makeText(ForgetPasswordActivity.this,"重置邮件已发送到"+email,Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ForgetPasswordActivity.this,"重置密码出错！",Toast.LENGTH_SHORT).show();
                                // 重置密码出错。
                            }
                        }
                    });
                } else {
                    showError(getString(R.string.error_register_email_address_format));
                }
            }
        });
    }

    public void showError(String errorMessage) {
        new AlertDialog.Builder(this)
                .setTitle(
                        getResources().getString(
                                R.string.dialog_message_title))
                .setMessage(errorMessage)
                .setNegativeButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).show();
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
