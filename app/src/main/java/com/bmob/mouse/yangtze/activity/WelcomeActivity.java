package com.bmob.mouse.yangtze.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bmob.mouse.yangtze.util.ActivityTool;
import com.bmob.mouse.yangtze.util.Constant;

/**
 * Created by Mouse on 2016/1/5.
 */
public class WelcomeActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences =getSharedPreferences(Constant.PREFS_NAME,MODE_PRIVATE);
        //线程操作
        new Thread(){
            public void run() {
                try {
                    sleep(1500);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (sharedPreferences.getBoolean(Constant.PREFS_NAME,true)) {
                        editor.putBoolean(Constant.PREFS_NAME, false);
                        editor.commit();
                        //引导页面
                        ActivityTool.goNextActivity(WelcomeActivity.this, ViewPageActivity.class,true);
                    }else{
                        //主程序
                        if(MyApplication.getInstance().getCurrentUser()==null){
                            ActivityTool.goNextActivity(WelcomeActivity.this, RegisterALoginActivity.class,false);
                        }else{
                            ActivityTool.goNextActivity(WelcomeActivity.this, MainActivity.class,false);
                        }
                    }
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };
        }.start();

    }
}
