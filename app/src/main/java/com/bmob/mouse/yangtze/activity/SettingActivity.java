package com.bmob.mouse.yangtze.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.bmob.mouse.yangtze.R;
import com.bmob.mouse.yangtze.entity.User;
import com.bmob.mouse.yangtze.util.ActivityTool;
import com.bmob.mouse.yangtze.util.CacheUtils;
import com.bmob.mouse.yangtze.util.Constant;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

;

/**
 * 设置
 * Created by Mouse on 2016/1/10.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static String TAG = "SettingActivity";
    private SharedPreferences sharedPreferences = null;
    private SharedPreferences.Editor editor;
    private Toolbar toolbar;
    private RippleView ripple;//人头
    private ImageView user_icon;
    private RippleView ripple2;//昵称
    private TextView nickName;

    private CheckBox sexSwitch;
    private RippleView ripple4;//个性签名
    private TextView signature;

    private RippleView ripple5;//清除缓存
    private RippleView ripple6;//退出登录
    private TextView logout;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_setting);
        sharedPreferences = getSharedPreferences(Constant.PREFS_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        initBar();
        initView();
        setListener();
        setupViews();

    }

    private void initPersonalInfo() {

        User user = BmobUser.getCurrentUser(mContext, User.class);
        if (user != null) {
            nickName.setText(user.getUsername());
            signature.setText(user.getSignature());
            if (user.getSex().equals(Constant.SEX_FEMALE)) {
                sexSwitch.setChecked(true);
                editor.putInt("set_settings", 0);
                editor.commit();
            } else {
                sexSwitch.setChecked(false);
                editor.putInt("set_settings", 1);
                editor.commit();

            }
            String avatarFile = user.getAvatar();
            if (avatarFile != null) {
                ImageLoader.getInstance().displayImage(
                        avatarFile,
                        user_icon,
                        MyApplication.getInstance().getOptions(
                                R.mipmap.girl),
                        new SimpleImageLoadingListener() {

                            @Override
                            public void onLoadingComplete(String imageUri,
                                                          View view, Bitmap loadedImage) {
                                // TODO Auto-generated method stub
                                super.onLoadingComplete(imageUri, view,
                                        loadedImage);
                            }

                        });
            } else {
                logout.setText("退出登录");
            }
        } else {
            logout.setText("登录");
        }

    }

    private void setupViews() {
        initPersonalInfo();
    }

    private void setListener() {
        ripple.setOnClickListener(this);//人头
        ripple2.setOnClickListener(this);//昵称
        sexSwitch.setOnCheckedChangeListener(this);
        ripple4.setOnClickListener(this);//个性签名
        ripple5.setOnClickListener(this);//清除缓存
        ripple6.setOnClickListener(this);//退出登录
        logout.setOnClickListener(this);

    }

    private void initView() {
        ripple = (RippleView) findViewById(R.id.ripple);
        user_icon = (ImageView) findViewById(R.id.user_icon_image);
        ripple2 = (RippleView) findViewById(R.id.ripple2);
        nickName = (TextView) findViewById(R.id.user_nick_text);
        sexSwitch = (CheckBox) findViewById(R.id.sex_choice_switch);
        ripple4 = (RippleView) findViewById(R.id.ripple4);
        signature = (TextView) findViewById(R.id.user_sign_text);
        ripple5 = (RippleView) findViewById(R.id.ripple5);
        ripple6 = (RippleView) findViewById(R.id.ripple6);
        logout = (TextView) findViewById(R.id.user_logout);

    }

    private void initBar() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("设置");
        setSupportActionBar(toolbar);//把 toolbar 设置到Activity 中

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //转给MainActivity更新avator
                Intent intent = new Intent();
                intent.setClass(MyApplication.getInstance().getTopActivity(),
                        MainActivity.class);
                mContext.startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ripple6://退出登录
                if (isLogined()) {
                    BmobUser.logOut(mContext);
                } else {
                    redictToLogin(Constant.GO_LOGIN);
                }
                break;
            case R.id.ripple5://清除缓存
                ImageLoader.getInstance().clearDiscCache();
                ActivityTool.show((Activity) mContext, "清除缓存完毕");
                break;
            case R.id.ripple://修改人头
                if (isLogined()) {
                    showAlbumDialog();
                } else {
                    redictToLogin(Constant.UPDATE_ICON);
                }
                break;
            case R.id.ripple4://个性签名
                if (isLogined()) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, EditSignActivity.class);
                    startActivityForResult(intent, Constant.EDIT_SIGN);
                } else {
                    redictToLogin(Constant.UPDATE_SIGN);
                }
                break;
            case R.id.sex_choice_switch://性别
                ShowToast("性别被点击啦");
                break;

        }

    }

    String dateTime;
    AlertDialog albumDialog;

    private void showAlbumDialog() {
        albumDialog = new AlertDialog.Builder(mContext).create();
        albumDialog.setCanceledOnTouchOutside(true);
        View v = LayoutInflater.from(mContext).inflate(
                R.layout.dialog_usericon, null);
        albumDialog.show();
        albumDialog.setContentView(v);
        albumDialog.getWindow().setGravity(Gravity.CENTER);
        TextView albumPic = (TextView) v.findViewById(R.id.album_pic);
        TextView cameraPic = (TextView) v.findViewById(R.id.camera_pic);
        albumPic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                albumDialog.dismiss();
                Date date1 = new Date(System.currentTimeMillis());
                dateTime = date1.getTime() + "";
                getAvataFromAlbum();
            }
        });
        cameraPic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                albumDialog.dismiss();
                Date date = new Date(System.currentTimeMillis());
                dateTime = date.getTime() + "";
                getAvataFromCamera();
            }
        });

    }

    private void getAvataFromCamera() {
        File f = new File(CacheUtils.getCacheDirectory(mContext, true, "icon")
                + dateTime);
        if (f.exists()) {
            f.delete();
        }
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Uri uri = Uri.fromFile(f);
        Log.e("uri", uri + "");

        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(camera, 1);
    }

    private void getAvataFromAlbum() {
        Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT);
        intent2.setType("image/*");
        startActivityForResult(intent2, 2);
    }

    private void redictToLogin(int requestCode) {
        Intent intent = new Intent();
        intent.setClass(mContext, RegisterALoginActivity.class);
        startActivityForResult(intent, requestCode);
        ActivityTool.show((Activity) mContext, "请先登录。");
    }

    /**
     * 判断用户是否登录
     *
     * @return
     */
    private boolean isLogined() {
        BmobUser user = BmobUser.getCurrentUser(mContext, User.class);
        if (user != null) {
            return true;
        }
        return false;
    }

    String iconUrl;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constant.UPDATE_SEX:
                    initPersonalInfo();
                    break;
                case Constant.EDIT_SIGN:
                    initPersonalInfo();
                    break;
                case Constant.UPDATE_ICON:
                    initPersonalInfo();
                    ripple.performClick();
                    break;
                case 1:
                    String files = CacheUtils.getCacheDirectory(mContext, true,
                            "icon") + dateTime;
                    File file = new File(files);
                    if (file.exists() && file.length() > 0) {
                        Uri uri = Uri.fromFile(file);
                        startPhotoZoom(uri);
                    }
                    break;
                case 2:
                    if (data == null) {
                        return;
                    }
                    startPhotoZoom(data.getData());
                    break;
                case 3:
                    if (data != null) {
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            Bitmap bitmap = extras.getParcelable("data");
                            iconUrl = saveToSdCard(bitmap);
                            user_icon.setImageBitmap(bitmap);
                            updateIcon(iconUrl);

                        }
                    }
                    break;
                case Constant.GO_LOGIN:
                    initPersonalInfo();
                    logout.setText("退出登录");
                    break;
                default:
            }
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.sex_choice_switch:
                if (isChecked) {
                    editor.putInt("sex_settings", 0);
                    editor.commit();
                    updateSex(0);
                } else {
                    editor.putInt("sex_settings", 1);
                    editor.commit();
                    updateSex(1);
                }
                break;
        }
    }

    private void updateIcon(String avataPath) {
        if (avataPath != null) {
            final BmobFile file = new BmobFile(new File(avataPath));
            file.upload(mContext, new UploadFileListener() {
                @Override
                public void onSuccess() {
                    Log.i(TAG, "上传文件成功。" + file.getFileUrl(mContext));
                    User  u =new User();
                    u.setAvatar(file.getFileUrl(mContext));
                    User currentUser = (User) userManager.getCurrentUser(User.class);
                    u.setObjectId(currentUser.getObjectId());
                    u.update(mContext, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            ActivityTool.show((Activity) mContext, "更改头像成功。");
                        }

                        @Override
                        public void onFailure(int arg0, String arg1) {
                            ActivityTool.show((Activity) mContext, "更新头像失败。请检查网络~");
                            Log.i(TAG, "更新失败2-->" + arg1);
                        }
                    });
                }

                @Override
                public void onProgress(Integer arg0) {

                }

                @Override
                public void onFailure(int arg0, String arg1) {

                    ActivityTool.show((Activity) mContext, "上传头像失败。请检查网络~");
                    Log.i(TAG, "上传文件失败。" + arg1);
                }
            });
        }
    }

    private void updateSex(int sex) {
        User user = BmobUser.getCurrentUser(mContext, User.class);
        if (user != null) {
            if (sex == 0) {
                user.setSex(Constant.SEX_FEMALE);
            } else {
                user.setSex(Constant.SEX_MALE);
            }
            user.update(mContext, new UpdateListener() {

                @Override
                public void onSuccess() {
                    ActivityTool.show((Activity) mContext, "更新信息成功。");
                    Log.i(TAG, "更新信息成功。");
                }

                @Override
                public void onFailure(int arg0, String arg1) {
                    ActivityTool.show((Activity) mContext, "更新信息失败。请检查网络~");
                    Log.i(TAG, "更新失败1-->" + arg1);
                }
            });
        } else {
            redictToLogin(Constant.UPDATE_SEX);
        }

    }

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 120);
        intent.putExtra("outputY", 120);
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);//
        // intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);

    }

    public String saveToSdCard(Bitmap bitmap) {
        String files = CacheUtils.getCacheDirectory(mContext, true, "icon")
                + dateTime + "_12.jpg";
        File file = new File(files);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i(TAG, file.getAbsolutePath());
        return file.getAbsolutePath();
    }
}
