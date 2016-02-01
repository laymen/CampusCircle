package com.bmob.mouse.yangtze.share;


import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.bmob.mouse.yangtze.R;
import com.bmob.mouse.yangtze.util.ActivityTool;
import com.bmob.mouse.yangtze.util.NetworkUtil;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class TencentShare implements TencentShareConstants {
    public static final String TAG="TencentShare";
    private Activity mContext;
    private Tencent tencent;
    private TencentShareEntity shareEntity;
    public TencentShare(Activity context, TencentShareEntity entity) {
        mContext=context;
        initTencent();
        shareEntity=entity;
        if(shareEntity == null) {
            shareEntity=
                new TencentShareEntity(TencentShareConstants.TITLE, TencentShareConstants.IMG_URL,
                    TencentShareConstants.TARGET_URL, TencentShareConstants.SUMMARY, TencentShareConstants.COMMENT);
        }
    }

    /**
     * 初始化tencent实例
     */
    private void initTencent() {
        if(tencent == null) {
            tencent=Tencent.createInstance(getAppId(), mContext);
        }

    }

    /**
     * 从Adminifest.xml里读取app_id
     */
    private String getAppId() {
        String appId="";
        try {
            ApplicationInfo appInfo=
                mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
            appId=appInfo.metaData.getString("TA_APPKEY");
            Log.i("apppid",appId+"");
        } catch(PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appId.substring(3);
    }

    /**
     * 检查网络并开始分享
     */
    public void shareToQQ() {
        shareToQQ(shareEntity);
    }

    /**
     * 检查网络并开始分享,支持动态改变分享参数
     */
    private void shareToQQ(TencentShareEntity entity) {
        if(NetworkUtil.isAvailable(mContext)) {
            doShareToQQ(entity);
        } else {
            ActivityTool.show(mContext, "网络无连接");
        }
    }

    /**
     * QQ分享实际操作title,imgUrl,targetUrl,summary
     */
    private void doShareToQQ(TencentShareEntity entity) {
        System.out.println(entity);
        Bundle params=new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, entity.getTitle());
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, entity.getImgUrl());
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, entity.getTargetUrl());
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, entity.getSummary());
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, mContext.getResources().getString(R.string.app_name));
        initTencent();
        tencent.shareToQQ(mContext, params, new BaseUiListener(0));
    }


    private class BaseUiListener implements IUiListener {

        private int flag=-1;

        public BaseUiListener(int flag) {
            this.flag=flag;
        }

        @Override
        public void onError(UiError e) {
            Log.i("QQ", "onError----" + "code:" + e.errorCode + ", msg:" + e.errorMessage + ", detail:" + e.errorDetail);
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onComplete(Object arg0) {
            switch(flag) {
                case 0:
                    Log.i(TAG, "share to qq complete!");
                    break;
                default:
                    break;
            }
        }
    }

}
