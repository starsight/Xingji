package com.wenjiehe.xingji.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.RefreshCallback;
import com.avos.avoscloud.SaveCallback;
import com.wenjiehe.xingji.Fragment.MyHistorySignFragment;
import com.wenjiehe.xingji.R;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditUserInfoActivity extends AppCompatActivity implements View.OnClickListener{

    final String TAG = "EditUserInfoActivity";
    CircleImageView iv_edit_userphoto;
    TextView tv_edit_sex,tv_edit_age,tv_edit_tel,tv_edit_introduce;
    RelativeLayout layout_edit_userphoto,layout_edit_sex,layout_edit_age,layout_edit_tel,layout_edit_introduce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_info);

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

        iv_edit_userphoto = (CircleImageView)findViewById(R.id.iv_edit_userphoto);
        tv_edit_sex = (TextView)findViewById(R.id.tv_edit_sex);
        tv_edit_age = (TextView)findViewById(R.id.tv_edit_age);
        tv_edit_tel = (TextView)findViewById(R.id.tv_edit_tel);
        tv_edit_introduce = (TextView)findViewById(R.id.tv_edit_introduce);

//        iv_edit_userphoto.setOnClickListener(this);
//        tv_edit_sex.setOnClickListener(this);
//        tv_edit_age.setOnClickListener(this);
//        tv_edit_tel.setOnClickListener(this);
//        tv_edit_introduce.setOnClickListener(this);

        layout_edit_userphoto = (RelativeLayout)findViewById(R.id.layout_edit_userphoto);
        layout_edit_userphoto.setOnClickListener(this);
        layout_edit_sex = (RelativeLayout)findViewById(R.id.layout_edit_sex);
        layout_edit_sex.setOnClickListener(this);
        layout_edit_age = (RelativeLayout)findViewById(R.id.layout_edit_age);
        layout_edit_age.setOnClickListener(this);
        layout_edit_tel = (RelativeLayout)findViewById(R.id.layout_edit_tel);
        layout_edit_tel.setOnClickListener(this);
        layout_edit_introduce = (RelativeLayout)findViewById(R.id.layout_edit_introduce);
        layout_edit_introduce.setOnClickListener(this);

        AVUser currentUser = AVUser.getCurrentUser();
        currentUser.refreshInBackground(new RefreshCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                //if (currentUser.get("signnum") != null)

            }
        });


        tv_edit_sex.setText(currentUser.getString("sex"));
        tv_edit_age.setText(String.valueOf(currentUser.getInt("age")));
        tv_edit_tel.setText(currentUser.getMobilePhoneNumber());
        tv_edit_introduce.setText(currentUser.getString("introduce"));
    }

    private void syncUpateUserInfo() {
        AVUser.getCurrentUser().put("sex",tv_edit_sex.getText());
        AVUser.getCurrentUser().put("age",Integer.parseInt(String.valueOf(tv_edit_age.getText())));
        AVUser.getCurrentUser().setMobilePhoneNumber(String.valueOf(tv_edit_tel.getText()));
        AVUser.getCurrentUser().put("introduce",String.valueOf(tv_edit_introduce.getText()));

        Log.d(TAG,String.valueOf(tv_edit_introduce.getText()));
        Log.d(TAG,String.valueOf(Integer.parseInt(String.valueOf(tv_edit_age.getText()))));
        //AVUser currentUser = AVUser.getCurrentUser();

        AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                AVUser.getCurrentUser().refreshInBackground(new RefreshCallback<AVObject>() {
                    @Override
                    public void done(AVObject avObject, AVException e) {
                        Toast.makeText(EditUserInfoActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });




    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.layout_edit_userphoto:
                new  AlertDialog.Builder(this)
                        .setTitle("更换头像" )
                        .setItems(new  String[] {"拍照", "从相册中选择" },
                                new  DialogInterface.OnClickListener() {
                                    public   void  onClick(DialogInterface dialog,  int  which) {
                                        if(which==0)
                                            camera();
                                        else
                                        gallery();
                                        dialog.dismiss();
                                    }
                                }
                        )
                        .show();
                break;
            case R.id.layout_edit_sex:
                editTextInfo("性别",tv_edit_sex);
                break;
            case R.id.layout_edit_age:
                editTextInfo("年龄",tv_edit_age);
                break;
            case R.id.layout_edit_tel:
                editTextInfo("手机号",tv_edit_tel);
                break;
            case R.id.layout_edit_introduce:
                editTextInfo("一句话介绍自己",tv_edit_introduce);
                break;

            default:
                break;
        }
    }



    public void editTextInfo(final String Message, final TextView textview) {
        if(Message.equals("性别")){
            final String[] str = new String[]{"男", "女"};
            new AlertDialog.Builder(this)
                    .setTitle("请选择")
                    .setSingleChoiceItems(str, 0,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    textview.setText(str[which]);
                                    syncUpateUserInfo();
                                    dialog.dismiss();
                                }
                            }
                    )
                    .show();
        }
        else {
            final EditText et = new EditText(this);
            et.setSingleLine();
            //et.setPadding(0,5,0,10);
            if (Message.equals("年龄"))
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
            else if (Message.equals("手机号"))
                et.setInputType(InputType.TYPE_CLASS_PHONE);

            //et.requestFocus(); //请求获取焦点
            //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            //imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

            AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(Message)
                    .setView(et)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String input = et.getText().toString();
                            if (!input.equals("")) {
                                //Toast.makeText(getApplicationContext(), "内容不能为空" + input, Toast.LENGTH_LONG).show();
                                textview.setText(input);
                                syncUpateUserInfo();
                                dialog.dismiss();
                            }
                        }
                    })
                    .setNegativeButton("取消", null);

            AlertDialog ad = builder.create();
            ad.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            ad.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            ad.show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private static final int PHOTO_REQUEST_CAMERA = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果

    private ImageView mFace;
    private Bitmap bitmap;
    String headPicture;
    /* 头像名称 */
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
    private File tempFile;

    /*
	 * 从相册获取
	 */
    public void gallery() {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);

    }

    /*
     * 从相机获取
     */
    public void camera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        // 判断存储卡是否可以用，可用进行存储
        if (hasSdcard()) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(Environment
                            .getExternalStorageDirectory(), PHOTO_FILE_NAME)));
        }
        startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Log.d("xingji",Integer.toString(requestCode));
        //window.dismiss();
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                crop(uri);
            }

        } else if (requestCode == PHOTO_REQUEST_CAMERA) {
            if (hasSdcard()) {
                tempFile = new File(Environment.getExternalStorageDirectory(),
                        PHOTO_FILE_NAME);
                crop(Uri.fromFile(tempFile));
            } else {
                Toast.makeText(EditUserInfoActivity.this, "未找到存储卡，无法存储照片！",Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == PHOTO_REQUEST_CUT) {
            try {
                bitmap = data.getParcelableExtra("data");
                iv_edit_userphoto.setImageBitmap(bitmap);
                //mFace.setImageBitmap(bitmap);
                saveBitmap(bitmap);
                upLoadHeadPhoto();
                boolean delete = tempFile.delete();
                System.out.println("delete = " + delete);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 剪切图片
     *
     * @function:
     * @author:Jerry
     * @date:2013-12-30
     * @param uri
     */
    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        // 图片格式
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);// true:不返回uri，false：返回uri
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    public void saveBitmap(Bitmap bm) {
        //Log.e(TAG, "保存图片");
        File f = new File(Environment.getExternalStorageDirectory() +"/headpicture.jpg");
        //File f = new File(this.getFilesDir().getAbsolutePath() + File.separator +"xingji/headpicture.jpg");
        Date date = new Date(f.lastModified());
        Log.d("xing--",date.toString());
        //Log.d("xing--",getActivity().getFilesDir().getAbsolutePath() + File.separator +"xingji/headpicture3");
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 65, out);
            out.flush();
            out.close();
            //Log.i(TAG, "已经保存");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /*
     * 上传图片
     */
    public void upLoadHeadPhoto() {
        try {
            AVFile file = AVFile.withAbsoluteLocalPath("headpicture.jpg", Environment.getExternalStorageDirectory() + "/headpicture.jpg");
            AVObject object = new AVObject("headpicture");
            object.put("headpicture",file);
            object.put("username",MainActivity.userName);
            object.saveInBackground();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }
}
