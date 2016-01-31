package com.bmob.mouse.yangtze.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Mouse on 2016/1/5.
 */
public class ToastTool {
    public static void Show(Context mContext, String msg){
        Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
    }
}
