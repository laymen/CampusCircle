package com.bmob.mouse.yangtze.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.bmob.mouse.yangtze.util.Constant;

import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.v3.Bmob;

/**
 * Created by Mouse on 2016/1/5.
 */
public class BaseActivity extends AppCompatActivity {
    protected MyApplication mMyApplication;
    protected Context mContext;
    BmobUserManager userManager;
    BmobChatManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 是否开启debug模式--默认开启状态
        BmobChat.DEBUG_MODE = true;
        Bmob.initialize(this, Constant.Bmob_ID);
        //BmobIM SDK初始化--只需要这一段代码即可完成初始化
        BmobChat.getInstance(this).init(Constant.Bmob_ID);
        userManager = BmobUserManager.getInstance(this);
        manager = BmobChatManager.getInstance(this);

        initConfigure();
    }
    private  void initConfigure(){
        mContext = this;
        if(null == mMyApplication){
            mMyApplication = MyApplication.getInstance();
        }
        mMyApplication.addActivity(this);
    }
    Toast mToast;

    public void ShowToast(final String text) {
        if (!TextUtils.isEmpty(text)) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    if (mToast == null) {
                        mToast = Toast.makeText(getApplicationContext(), text,
                                Toast.LENGTH_LONG);
                    } else {
                        mToast.setText(text);
                    }
                    mToast.show();
                }
            });

        }
    }
}
