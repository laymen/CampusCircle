package com.bmob.mouse.yangtze.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by Mouse on 2016/1/5.
 */
public class NetWorkHelp {
    private MaterialDialog mMaterialDialog;
    /**
     * 判断wifi 是否可用
     * @param context
     * @return
     */
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null)
            return false;
        NetworkInfo netinfo = cm.getActiveNetworkInfo();
        if (netinfo == null) {
            return false;
        }
        if (netinfo.isConnected()) {
            return true;
        }
        return false;
    }

    public void showNetDialog(final Context context){
        mMaterialDialog = new MaterialDialog(context)
        .setMessage("世界上最遥远的距离就是没网")
                .setPositiveButton("检查设置", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = null;
                        try {
                            @SuppressWarnings("deprecation")
                            String sdkVersion = android.os.Build.VERSION.SDK;
                            if(Integer.valueOf(sdkVersion) > 10) {
                                intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                            }else {
                                intent = new Intent();
                                ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
                                intent.setComponent(comp);
                                intent.setAction("android.intent.action.VIEW");
                            }
                            context.startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mMaterialDialog.dismiss();

                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });
        mMaterialDialog.show();
    }
}