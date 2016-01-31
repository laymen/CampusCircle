package com.bmob.mouse.yangtze.util;

import android.util.Log;

public class IsDoubleChick {

    // 双击事件记录最近一次点击的ID
    private static long lastClickId = -1;
    // 双击事件记录最近一次点击的时间
    private static long lastClickTime;
    private static int isdoubleclicks = 0;
    private static String TAG = "IsDoubleChick";

    public static boolean isDoublechick(long id) {
        boolean flag = false;
        Log.i(TAG, "----->" + lastClickTime);

        // 下面是我对双击时间的响应的处理方法
        isdoubleclicks++;// 点击的次数
        if (lastClickId != id) {// 点击是否还是当前位置的id
            isdoubleclicks = 1;// 要是不是就从1开始
            lastClickId = id;// 记录当前的点击的id
            lastClickTime = System.currentTimeMillis();// 获得系统时间
        }
        if (1 == isdoubleclicks) {// 要是在当前的位置点击了一次了
            lastClickId = id;// 同理记录id
            lastClickTime = System.currentTimeMillis();// 获得系统时间
            Log.i(TAG, "lastClickId==----第一次lastClickId->>" + lastClickId + "----" + "lastClickTime==----第一次lastClickTime->>" +
                    lastClickTime);
        }
        if (2 == isdoubleclicks) {// 要是点击第二次了
            isdoubleclicks = 0;// 先要清零点击次数
            Log.i(TAG, "lastClickId==----第2次lastClickId->>" + id + "----" + "lastClickTime==----第2次lastClickTime->>" + System.currentTimeMillis());
            if (id == lastClickId) {// 要是在同一位置点击了两次
                if ((Math.abs(lastClickTime - System.currentTimeMillis()) < 1000)) {// 看电接点额时间是还不是在1秒之内
                    // 是在1秒之内,就认为是doublechick
                    lastClickId = -1;
                    lastClickTime = 0;
                    flag = true;
                } else {// 要是不是在同一位置了 就重新开始计算 但是这时已经在当前的位置算点击了一次了
                    isdoubleclicks = 1;
                    lastClickTime = System.currentTimeMillis();
                }
            }
        }
        return flag;
    }

}
