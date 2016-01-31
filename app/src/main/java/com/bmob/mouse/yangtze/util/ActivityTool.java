package com.bmob.mouse.yangtze.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;

import com.bmob.mouse.yangtze.activity.MyApplication;
import com.bmob.mouse.yangtze.R;

/**
 * Activity跳转
 * Created by Mouse on 2016/1/5.
 */
public class ActivityTool {
    public static void goNextActivity(Context context, Class clazz, Boolean finish){
        Intent intent = new Intent(context, clazz);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.slide_left_in,
                R.anim.fade_out_long_animation);
        if (finish)
            //结束当前Activity
            ((Activity) context).finish();
    }

    public static void goNextActivity(Context context, Class clazz, Bundle bundle,Boolean finish){
        Intent intent = new Intent(context, clazz);
        intent.putExtras(bundle);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.slide_left_in,
                R.anim.fade_out_long_animation);
        if (finish)
            //结束当前Activity
            ((Activity) context).finish();
    }

    public static void goNextActivityForResult(Context context, Class clazz, Bundle bundle, int requestCode,Boolean finish){
        Intent intent = new Intent(context, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        ((Activity)context).startActivityForResult(intent, requestCode);
        if (finish)
            //结束当前Activity
            ((Activity) context).finish();
    }
    //发现以上三种办法不够



    public interface MessageFilter {

        String filter(String msg);
    }

    public static MessageFilter msgFilter;

    public static void show(final Activity activity, final String message) {
        final String msg = msgFilter != null ? msgFilter.filter(message) : message;
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                Toast toast = ToastFactory.getToast(activity, message);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }
    /**
     * 获取屏幕宽高
     * @return
     */
    public static int[] getScreenSize() {
        int[] screens;
        DisplayMetrics dm=new DisplayMetrics();
        dm= MyApplication.getInstance().getResources().getDisplayMetrics();
        screens=new int[]{dm.widthPixels, dm.heightPixels};
        return screens;
    }
    public static float[] getBitmapConfiguration(Bitmap bitmap, ImageView imageView, float screenRadio) {
        int screenWidth=ActivityTool.getScreenSize()[0];
        float rawWidth=0;
        float rawHeight=0;
        float width=0;
        float height=0;
        if(bitmap == null) {
            // rawWidth = sourceWidth;
            // rawHeight = sourceHeigth;
            width=(float)(screenWidth / screenRadio);
            height=(float)width;
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            rawWidth=bitmap.getWidth();
            rawHeight=bitmap.getHeight();
            if(rawHeight > 10 * rawWidth) {
                imageView.setScaleType(ImageView.ScaleType.CENTER);
            } else {
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            }
            float radio=rawHeight / rawWidth;

            width=(float)(screenWidth / screenRadio);
            height=(float)(radio * width);
        }
        return new float[]{width, height};
    }


}
