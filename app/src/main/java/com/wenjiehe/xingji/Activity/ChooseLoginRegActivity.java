package com.wenjiehe.xingji.Activity;

/**
 * Created by wenjie on 16/07/22.
 */

import com.avos.avoscloud.AVException;
//import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SignUpCallback;
import com.baidu.mapapi.SDKInitializer;
import com.wenjiehe.xingji.AVService;
import com.wenjiehe.xingji.R;
import com.wenjiehe.xingji.Util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


import permissions.dispatcher.NeedsPermission;

import static com.avos.avoscloud.AVUser.getCurrentUser;
import static com.wenjiehe.xingji.Util.isEmail;


public class ChooseLoginRegActivity extends BaseActivity {

    Button bt_login;
    Button bt_register;
    TextView tv_loginForgetPassword;//login
    EditText et_loginUserName,et_loginPassword;

    EditText et_regUserName,et_regEmail,et_regPassword;//register

    Button bt_chooseLogin,bt_chooseRegister;//启动选择登陆or注册
    ImageView iv_choose_icon;

    boolean isEnterLoginOrReg = false;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_reg_login);
        //AVAnalytics.trackAppOpened(getIntent());

        //AVService.initPushService(this);

        bt_chooseLogin = (Button) findViewById(R.id.bt_choose_login);
        bt_chooseRegister = (Button) findViewById(R.id.bt_choose_register);
        iv_choose_icon =(ImageView) findViewById(R.id.iv_choose_icon);

        if (getUserId() != null) {
            Intent mainIntent = new Intent(activity, MainActivity.class);
            mainIntent.putExtra("username",getUserName());
            mainIntent.putExtra("signnum",getSignNum());

            startActivity(mainIntent);
            activity.finish();
        }

        bt_chooseLogin.setOnClickListener(chooseLoginListener);
        bt_chooseRegister.setOnClickListener(chooseRegisterListener);

        bt_chooseRegister.setOnTouchListener(regTouchListener);
        bt_chooseLogin.setOnTouchListener(loginTouchListener);

    }

    View.OnTouchListener loginTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            //bt_chooseLogin.setBackgroundColor(getResources().getColor(R.color.choose_log_reg_background));
            //Button bt_chooseRegister2 = (Button) findViewById(R.id.bt_choose_register2);



                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        System.out.print(bt_chooseRegister.getY());
                        System.out.print("---"+event.getY());
                        //按钮按下逻辑
                        bt_chooseLogin.setTextColor(getResources().getColor(R.color.white));
                        //bt_chooseRegister2.setVisibility(View.VISIBLE);
                        //bt_chooseLogin.setBackgroundDrawable(getResources().getDrawable(R.drawable.corner_textview));
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //Log.d("xingji",String.valueOf(bt_chooseRegister.getHeight()));
                        // Log.d("xingji--",String.valueOf(event.getY()));
                        if(event.getY()>25+bt_chooseLogin.getHeight()||event.getY()<-25) {
                            bt_chooseLogin.setTextColor(getResources().getColor(R.color.black));
                        }
                        if(event.getX()>25+bt_chooseLogin.getWidth()||event.getX()<-25) {
                            bt_chooseLogin.setTextColor(getResources().getColor(R.color.black));
                        }
                        break;
                }

            return false;
        }
    };

    View.OnTouchListener regTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {


            //bt_chooseLogin.setBackgroundColor(getResources().getColor(R.color.choose_log_reg_background));
            //Button bt_chooseRegister2 = (Button) findViewById(R.id.bt_choose_register2);
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    //按钮按下逻辑
                    bt_chooseRegister.setTextColor(getResources().getColor(R.color.white));
                    break;

                case MotionEvent.ACTION_MOVE:
                    if(event.getY()>25+bt_chooseRegister.getHeight()||event.getY()<-25) {
                        bt_chooseRegister.setTextColor(getResources().getColor(R.color.black));
                    }
                    if(event.getX()>25+bt_chooseRegister.getWidth()||event.getX()<-25) {
                        bt_chooseRegister.setTextColor(getResources().getColor(R.color.black));
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    bt_chooseRegister.setTextColor(getResources().getColor(R.color.black));
                    //按钮弹起逻辑
                    break;
            }
            return false;
        }
    };

    OnClickListener loginListener = new OnClickListener() {

        @SuppressLint("NewApi")
        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        public void onClick(View arg0) {
            String username = et_loginUserName.getText().toString();
            if (username.equals("")) {
                showUserNameEmptyError();
                return;
            }
            String passwd = et_loginPassword.getText().toString();
            if (passwd.isEmpty()) {
                showUserPasswordEmptyError();
                return;
            }
            progressDialogShow();
            AVUser.logInInBackground(username, passwd,
                    new LogInCallback() {
                        public void done(AVUser user, AVException e) {
                            if (user != null) {
                                progressDialogDismiss();

                                Intent mainIntent = new Intent(activity,
                                        MainActivity.class);
                                AVUser currentUser = getCurrentUser();
                                if (currentUser != null) {
                                    mainIntent.putExtra("username",currentUser.getUsername());
                                    mainIntent.putExtra("signnum",(Integer)currentUser.get("signnum"));
                                }
                                startActivity(mainIntent);
                                activity.finish();
                            } else {
                                progressDialogDismiss();
                                showLoginError();
                            }
                        }
                    });
        }

    };

    OnClickListener forgetPasswordListener = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent forgetPasswordIntent = new Intent(activity, ForgetPasswordActivity.class);
            startActivity(forgetPasswordIntent);
            //activity.finish();
        }
    };


    //选择登陆
    OnClickListener chooseLoginListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            //iv_choose_icon.setVisibility(View.GONE);

            //bt_chooseLogin.setBackground(R.color.choose_log_reg_background);
            isEnterLoginOrReg = true;
            setContentView(R.layout.choose_login);
            final TextInputLayout textInputLayout1 = (TextInputLayout)findViewById(R.id.til_loginUserName);
            final TextInputLayout textInputLayout2 = (TextInputLayout)findViewById(R.id.til_loginPasswd);
            generateEditUserName(textInputLayout1, "用户名");
            generateEditPasswd(textInputLayout2,"密码");


            bt_login = (Button) findViewById(R.id.bt_login);
            tv_loginForgetPassword = (TextView) findViewById(R.id.tv_loginForgetPassword);
            et_loginUserName = (EditText) findViewById(R.id.et_loginUserName);
            et_loginPassword = (EditText) findViewById(R.id.et_loginPassword);

            bt_login.setOnClickListener(loginListener);
            tv_loginForgetPassword.setOnClickListener(forgetPasswordListener);
        }
    };

    //选择注册
    OnClickListener chooseRegisterListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            isEnterLoginOrReg = true;

            //bt_chooseRegister.setVisibility(View.GONE);
            //Button bt_chooseRegister2 = (Button) findViewById(R.id.bt_choose_register2);
            //bt_chooseRegister2.setVisibility(View.VISIBLE);

            setContentView(R.layout.choose_register);

            final TextInputLayout textInputLayout1 = (TextInputLayout)findViewById(R.id.til_regUserName);
            final TextInputLayout textInputLayout2 = (TextInputLayout)findViewById(R.id.til_regPasswd);
            final TextInputLayout textInputLayout3 = (TextInputLayout)findViewById(R.id.til_regEmail);
            generateEditUserName(textInputLayout1, "用户名");
            generateEditPasswd(textInputLayout2,"密码");
            generateEditEmail(textInputLayout3, "邮箱地址");

            bt_register = (Button) findViewById(R.id.bt_register);

            et_regUserName = (EditText) findViewById(R.id.et_regUserName);
            et_regEmail = (EditText) findViewById(R.id.et_regEmail);
            et_regPassword = (EditText) findViewById(R.id.et_regPasswd);

            bt_register.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                        if (!et_regUserName.getText().toString().isEmpty()) {
                            if (!et_regPassword.getText().toString().isEmpty()) {
                                if (!et_regEmail.getText().toString().isEmpty()) {
                                    if(Util.isEmail(et_regEmail.getText().toString())){
                                        if(et_regUserName.getText().length()>=3&&et_regUserName.getText().length()<=15){
                                            if(et_regPassword.getText().length()>=6&&et_regPassword.getText().length()<=20){
                                                progressDialogShow();
                                                register();
                                            }else{
                                                showError(activity
                                                        .getString(R.string.error_register_password_length));
                                            }
                                        }else{
                                            showError(activity
                                                    .getString(R.string.error_register_user_name_length));
                                        }

                                    } else {
                                        showError(activity
                                                .getString(R.string.error_register_email_address_format));
                                    }
                                } else {
                                    showError(activity
                                            .getString(R.string.error_register_email_address_null));
                                }
                            } else {
                                showError(activity
                                        .getString(R.string.error_register_password_null));
                            }
                        } else {
                            showError(activity
                                    .getString(R.string.error_register_user_name_null));
                        }

                }
            });


        }
    };

    private void progressDialogDismiss() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    private void progressDialogShow() {
        progressDialog = ProgressDialog
                .show(activity,
                        activity.getResources().getText(
                                R.string.dialog_message_title),
                        activity.getResources().getText(
                                R.string.dialog_text_wait), true, false);
    }

    private void showLoginError() {
        new AlertDialog.Builder(activity)
                .setTitle(
                        activity.getResources().getString(
                                R.string.dialog_message_title))
                .setMessage(
                        activity.getResources().getString(
                                R.string.error_login_error))
                .setNegativeButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).show();
    }

    private void showUserPasswordEmptyError() {
        new AlertDialog.Builder(activity)
                .setTitle(
                        activity.getResources().getString(
                                R.string.dialog_error_title))
                .setMessage(
                        activity.getResources().getString(
                                R.string.error_register_password_null))
                .setNegativeButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).show();
    }

    private void showUserNameEmptyError() {
        new AlertDialog.Builder(activity)
                .setTitle(
                        activity.getResources().getString(
                                R.string.dialog_error_title))
                .setMessage(
                        activity.getResources().getString(
                                R.string.error_register_user_name_null))
                .setNegativeButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).show();
    }


    public void register() {
        SignUpCallback signUpCallback = new SignUpCallback() {
            public void done(AVException e) {
                progressDialogDismiss();
                if (e == null) {
                    //showRegisterSuccess();
                    /*建uMoments表*/
                    /*String objectid = AVUser.getCurrentUser().getObjectId();
                    Log.d("choose",objectid);
                    AVObject todo2 = new AVObject("u"+objectid);
                    todo2.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {

                            } else {
                                progressDialogDismiss();
                                showLoginError();
                                // 失败的话，请检查网络环境以及 SDK 配置是否正确
                            }
                        }
                    });*/
                    // 存储成功
                    Intent mainIntent = new Intent(activity, MainActivity.class);
                    AVUser currentUser = AVUser.getCurrentUser();
                    if (currentUser != null) {
                        mainIntent.putExtra("username",currentUser.getUsername());
                        mainIntent.putExtra("signnum",(Integer)currentUser.get("signnum"));
                    }
                    startActivity(mainIntent);
                    activity.finish();

                } else {
                    switch (e.getCode()) {
                        case 202:
                            showError(activity
                                    .getString(R.string.error_register_user_name_repeat));
                            break;
                        case 203:
                            showError(activity
                                    .getString(R.string.error_register_email_repeat));
                            break;
                        default:
                            showError(activity
                                    .getString(R.string.network_error));
                            break;
                    }
                }
            }
        };
        String username = et_regUserName.getText().toString();
        String password = et_regPassword.getText().toString();
        String email = et_regEmail.getText().toString();

        AVService.signUp(username, password, email, signUpCallback);
    }


    private void showRegisterSuccess() {
        new AlertDialog.Builder(activity)
                .setTitle(
                        activity.getResources().getString(
                                R.string.dialog_message_title))
                .setMessage(
                        activity.getResources().getString(
                                R.string.success_register_success))
                .setNegativeButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).show();
    }

    @Override
    public void onBackPressed() {
       //
        if(isEnterLoginOrReg == true) {
            setContentView(R.layout.choose_reg_login);
            bt_chooseLogin = (Button) findViewById(R.id.bt_choose_login);
            bt_chooseRegister = (Button) findViewById(R.id.bt_choose_register);
            iv_choose_icon =(ImageView) findViewById(R.id.iv_choose_icon);

            //bt_chooseRegister.setVisibility(View.VISIBLE);
            //Button bt_chooseRegister2 = (Button) findViewById(R.id.bt_choose_register2);
            //bt_chooseRegister2.setVisibility(View.GONE);

            bt_chooseLogin.setOnClickListener(chooseLoginListener);
            bt_chooseRegister.setOnClickListener(chooseRegisterListener);
            bt_chooseRegister.setOnTouchListener(regTouchListener);
            bt_chooseLogin.setOnTouchListener(loginTouchListener);
        }
        else
            super.onBackPressed();
        isEnterLoginOrReg = false;
        //System.out.println("按下了back键   onBackPressed()");
    }

    private void generateEditUserName(final TextInputLayout text,String textinput) {
        EditText editText = text.getEditText();
        final String texterrorsmall ="用户名输入不应小于3位";
        final String texterrorbig = "用户名输入不要超过15位";
        final int lengthSmall=3;
        final int lengthBig=15;

        text.setHint(textinput);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < lengthSmall) {
                    text.setError(texterrorsmall);
                    text.setErrorEnabled(true);
                } else if (s.length() > lengthBig) {
                    text.setError(texterrorbig);
                    text.setErrorEnabled(true);
                }else {
                    text.setErrorEnabled(false);
                }
            }
        });
    }

    private void generateEditPasswd(final TextInputLayout text,String textinput) {
        EditText editText = text.getEditText();
        final String texterrorsmall ="密码长度应大于6位";
        final String texterrorbig = "密码长度应小于20位";
        final int lengthSmall=6;
        final int lengthBig=20;

        text.setHint(textinput);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < lengthSmall) {
                    text.setError(texterrorsmall);
                    text.setErrorEnabled(true);
                } else if (s.length() > lengthBig) {
                    text.setError(texterrorbig);
                    text.setErrorEnabled(true);
                }else {
                    text.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void generateEditEmail(final TextInputLayout text,String textinput) {
        EditText editText = text.getEditText();
        text.setHint(textinput);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!Util.isEmail(s.toString())){
                    text.setError(getString(R.string.error_register_email_address_format));
                    text.setErrorEnabled(true);
                }else{
                    text.setErrorEnabled(false);
                }
            }
        });
    }

}
