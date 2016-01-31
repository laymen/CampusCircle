package com.bmob.mouse.yangtze.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bmob.mouse.yangtze.R;
import com.bmob.mouse.yangtze.entity.Mouse;
import com.bmob.mouse.yangtze.entity.User;
import com.bmob.mouse.yangtze.util.ActivityTool;
import com.bmob.mouse.yangtze.util.Constant;
import com.bmob.mouse.yangtze.util.IsDoubleChick;
import com.bmob.mouse.yangtze.util.PhotoUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by Mouse on 2016/1/2.
 */
public class EditActivity extends BaseActivity implements View.OnClickListener {
    private static String TAG="EditActivity";

    EditText content;

    LinearLayout openLayout;
    LinearLayout takeLayout;

    ImageView albumPic;
    ImageView takePic;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle bundle) {
        // TODO Auto-generated method stub
        super.onCreate(bundle);
        setContentView(R.layout.activity_edit);
        initBar();
        initView();
        setupView();
        setListener();
    }

    public void initBar() {
        toolbar = (Toolbar) super.findViewById(R.id.tool_bar);
        toolbar.setTitle("感想");
        setSupportActionBar(toolbar);//把 toolbar 设置到Activity 中

    }

    protected void initView() {
        content = (EditText) findViewById(R.id.edit_content);
        openLayout = (LinearLayout) findViewById(R.id.open_layout);
        takeLayout = (LinearLayout) findViewById(R.id.take_layout);

        albumPic = (ImageView) findViewById(R.id.open_pic);
        takePic = (ImageView) findViewById(R.id.take_pic);

    }

    public void setupView() {
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
                        | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

    }

    public void setListener() {
        openLayout.setOnClickListener(this);
        takeLayout.setOnClickListener(this);

        albumPic.setOnClickListener(this);
        takeLayout.setOnClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                EditActivity.this.finish();
                break;
            case R.id.item_sure:
                if ( IsDoubleChick.isDoublechick(R.id.item_sure)){
                    ShowToast("正在提交.......");
                    preformAction();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 使用inflate方法来把布局文件中的定义的菜单 加载给 第二个参数所对应的menu对象
        getMenuInflater().inflate(R.menu.menu_store, menu);
        return true;
    }


    //发表
    public void preformAction() {
        String commitContent = content.getText().toString();
        if (TextUtils.isEmpty(commitContent)) {
            Toast.makeText(mContext,"内容不能为空~",Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i(TAG,"0------------------------------------------------"+filePath);
        if (filePath == null) {
            publishWithoutFigure(commitContent, null);
        } else {
            Log.i(TAG,"------------------------------------------------"+"mouse");
            publish(commitContent);
            filePath=null;
        }

    }

    /**
     * 发表带图片
     * @param commitContent
     */

    public void publish(final String commitContent) {
        Log.i(TAG, "000000000--------------发表带图片");
        final BmobFile figureFile = new BmobFile(new File(filePath));

        figureFile.upload(getApplication(), new UploadFileListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                Log.i(TAG, "上传文件成功。" + figureFile.getFileUrl(EditActivity.this));
                publishWithoutFigure(commitContent, figureFile);

            }

            @Override
            public void onProgress(Integer arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
                Log.i(TAG, "上传文件失败。" + arg1);
            }
        });


    }

    public void publishWithoutFigure(final String commitContent,
                                     final BmobFile figureFile) {
//        User user = BmobUser.getCurrentUser(m, User.class);
        User user= MyApplication.getInstance().getCurrentUser();
        final Mouse mouse = new Mouse();
        mouse.setAuthor(user);
        mouse.setContent(commitContent);
        if (figureFile != null) {
            mouse.setContentfigureurl(figureFile);
        }
        mouse.setLove(0);
//        mouse.setHate(0);
        mouse.setShare(0);
        mouse.setComment(0);
        mouse.setPass(true);
        mouse.save(mContext, new SaveListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                ActivityTool.show(EditActivity.this, "发表成功！");
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
                ActivityTool.show(EditActivity.this, "发表失败!" + arg1);
            }
        });

    }

    public String filePath = "";
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open_layout://本地
                Log.i(TAG, "--------------------------" + "open_layout");
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent,
                        Constant.REQUEST_CODE_ALBUM);
                takeLayout.setVisibility(View.GONE);
                break;
            case R.id.take_layout://来自相机
                File dir = new File(Constant.MyAvatarDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                // 原图
                File file = new File(dir, new SimpleDateFormat("yyMMddHHmmss")
                        .format(new Date()));
                filePath = file.getAbsolutePath();// 获取相片的保存路径
                Uri imageUri = Uri.fromFile(file);

                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent2.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent2,
                        Constant.REQUEST_CODE_CAMERA);
                openLayout.setVisibility(View.GONE);

                break;
            default:
                break;
        }
    }

    int degree = 0;
    boolean isFromCamera = false;// 区分拍照旋转

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            switch (requestCode) {
                case Constant.REQUEST_CODE_ALBUM:// 本地修改头像
                    Uri uri=null;
                    if (data==null){
                        return;
                    }
                    if (resultCode==RESULT_OK){
                        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                            ShowToast("SD卡不可用");
                            return;
                        }
                        isFromCamera=false;//来自本地
                        uri=data.getData();
                        startImageAction(uri, 400, 400,
                                Constant.REQUESTCODE_UPLOADAVATAR_CROP, true);
                    }else {
                        ShowToast("照片获取失败~");
                    }
                    break;


                case Constant.REQUEST_CODE_CAMERA:// 拍照修改头像
                    if (resultCode==RESULT_OK){
                        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                            ShowToast("SD卡不可用");
                            return;
                        }
                        isFromCamera=true;//来自拍照
                        File file=new File(filePath);
                        degree = PhotoUtil.readPictureDegree(file.getAbsolutePath());
                        Log.i("life", "拍照后的角度：" + degree);
                        startImageAction(Uri.fromFile(file), 200, 200,
                                Constant.REQUESTCODE_UPLOADAVATAR_CROP, true);
                    }
                    break;
                case Constant.REQUESTCODE_UPLOADAVATAR_CROP:// 裁剪头像返回
                    // TODO sent to crop
                    if (data == null) {
                        ShowToast("取消选择");
                        return;
                    } else {
                        saveCropAvator(data);
                    }
                    break;
                default:
                    break;
            }
        }

    /**
     * 保存裁剪的头像
     *
     * @param data
     */
    private void saveCropAvator(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap bitmap = extras.getParcelable("data");
            Log.i("life", "-----------------------------avatar - bitmap = " + bitmap);
            if (bitmap != null) {
                bitmap = PhotoUtil.toRoundCorner(bitmap, 10);
                if (isFromCamera && degree != 0) {
                    bitmap = PhotoUtil.rotaingImageView(degree, bitmap);
                }
                if (isFromCamera){//为true则来自拍照，为false来自本地
                    takePic.setImageBitmap(bitmap);
                }else{
                    albumPic.setImageBitmap(bitmap);
                }
                // 保存图片
                String filename = new SimpleDateFormat("yyMMddHHmmss")
                        .format(new Date())+".png";

                filePath = Constant.MyAvatarDir + filename;
                PhotoUtil.saveBitmap(Constant.MyAvatarDir, filename,
                        bitmap, true);
                // 上传头像
                if (bitmap != null && bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
        }
    }

    private void startImageAction(Uri uri, int outputX, int outputY,
                                  int requestCode, boolean isCrop) {
        Intent intent = null;
        if (isCrop) {
            intent = new Intent("com.android.camera.action.CROP");
        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        }
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, requestCode);
    }

}
