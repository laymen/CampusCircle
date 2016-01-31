
package com.bmob.mouse.yangtze.util;

/**
 * Created by Mouse on 2016/1/5.
 */
public class Constant {
    /**
     * 是不是第一次开启
     */
    public static final String PREFS_NAME = "IsFirst";
    public static final String Bmob_ID = "b3f8a19ae2cdebd00b2ff3da54ce24bb";
    public static  final String Tencent_ID="1105034225";
    public static final String SEX_MALE = "male";
    public static final String SEX_FEMALE = "female";
    public static final int NUMBERS_PER_PAGE = 15;// 每次请求返回评论条数
    public static final int PUBLISH_COMMENT = 1;
    public static final int SAVE_FAVOURITE = 2;

    /**
     * 我的头像保存目录
     */
    public static String MyAvatarDir = "/sdcard/bmobmouse/avatar/";
    /**
     * 拍照回调
     */
    public static final int REQUEST_CODE_ALBUM = 1;
    ;//本地相册修改头像
    public static final int REQUEST_CODE_CAMERA = 2;//本地相册修改头像
    public static final int REQUESTCODE_UPLOADAVATAR_CROP = 3;//系统裁剪头像
    //
    public static final int GO_LOGIN = 10;
    public static final int UPDATE_ICON = 12;
    public static final int EDIT_SIGN = 15;
    public static final int UPDATE_SIGN = 13;
    public static final int UPDATE_SEX = 11;

    public static String NETWORK_TYPE_WIFI = "wifi";
    public static String NETWORK_TYPE_MOBILE = "mobile";
    public static String NETWORK_TYPE_ERROR = "error";
    public  static  String PRE_NAME = "my_pre";
}
