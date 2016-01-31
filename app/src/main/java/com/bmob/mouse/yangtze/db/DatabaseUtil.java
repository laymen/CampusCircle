package com.bmob.mouse.yangtze.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.bmob.mouse.yangtze.activity.MyApplication;
import com.bmob.mouse.yangtze.entity.Mouse;
import com.bmob.mouse.yangtze.entity.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class DatabaseUtil {
    private static final String TAG = "DatabaseUtil";
    private static DatabaseUtil instance;

    /**
     * 数据库帮助类
     **/
    private DBHelper dbHelper;

    public synchronized static DatabaseUtil getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseUtil(context);
        }
        return instance;
    }

    /**
     * 初始化
     *
     * @param context
     */
    private DatabaseUtil(Context context) {
        dbHelper = new DBHelper(context);
    }

    /**
     * 销毁
     */
    public static void destory() {
        if (instance != null) {
            instance.onDestory();
        }
    }

    /**
     * 销毁
     */
    public void onDestory() {
        instance = null;
        if (dbHelper != null) {
            dbHelper.close();
            dbHelper = null;
        }
    }


    public void deleteFav(Mouse mouse) {
        Cursor cursor = null;
        String where = DBHelper.FavTable.USER_ID + " = '" + MyApplication.getInstance().getCurrentUser().getObjectId()
                + "' AND " + DBHelper.FavTable.OBJECT_ID + " = '" + mouse.getObjectId() + "'";
        cursor = dbHelper.query(DBHelper.TABLE_NAME, null, where, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int isLove = cursor.getInt(cursor.getColumnIndex(DBHelper.FavTable.IS_LOVE));
            if (isLove == 0) {
                dbHelper.delete(DBHelper.TABLE_NAME, where, null);
            } else {
                ContentValues cv = new ContentValues();
                cv.put(DBHelper.FavTable.IS_FAV, 0);
                dbHelper.update(DBHelper.TABLE_NAME, cv, where, null);
            }
        }
        if (cursor != null) {
            cursor.close();
            dbHelper.close();
        }
    }


    public boolean isLoved(Mouse mouse) {
        Cursor cursor = null;
        String where = DBHelper.FavTable.USER_ID + " = '" + MyApplication.getInstance().getCurrentUser().getObjectId()
                + "' AND " + DBHelper.FavTable.OBJECT_ID + " = '" + mouse.getObjectId() + "'";
        Log.i(TAG,"------------------------------------"+where);
        cursor = dbHelper.query(DBHelper.TABLE_NAME, null, where, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            if (cursor.getInt(cursor.getColumnIndex(DBHelper.FavTable.IS_LOVE)) == 1) {
                Log.i(TAG,"------------------------------------"+"到这里了");
                return true;
            }
        }
        return false;
    }

    public boolean insertFav(Mouse mouse) {//是成功的
        long uri = 0;
        Cursor cursor = null;
        String where = DBHelper.FavTable.USER_ID + " = '" + MyApplication.getInstance().getCurrentUser().getObjectId()
                + "' AND " + DBHelper.FavTable.OBJECT_ID + " = '" + mouse.getObjectId() + "'";
        cursor = dbHelper.query(DBHelper.TABLE_NAME, null, where, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            ContentValues conv = new ContentValues();
            conv.put(DBHelper.FavTable.IS_FAV, 1);
            conv.put(DBHelper.FavTable.IS_LOVE, 1);
            dbHelper.update(DBHelper.TABLE_NAME, conv, where, null);
        } else {
            ContentValues cv = new ContentValues();
            cv.put(DBHelper.FavTable.USER_ID, MyApplication.getInstance().getCurrentUser().getObjectId());
            cv.put(DBHelper.FavTable.OBJECT_ID, mouse.getObjectId());
            cv.put(DBHelper.FavTable.IS_LOVE, mouse.isMyFav() == true ? 1 : 0);
            cv.put(DBHelper.FavTable.IS_FAV, mouse.isMyFav() == true ? 1 : 0);
            uri = dbHelper.insert(DBHelper.TABLE_NAME, null, cv);
        }
        if (cursor != null) {
            cursor.close();
            dbHelper.close();
        }
        Log.i(TAG,"insertFav------------->"+uri);
        Log.i(TAG,"insertFav-->"+"isMyFav:"+mouse.isMyFav()+"-----"+"isMyLove:"+mouse.isMyLove()+"----"+"mouse.getLove:"+mouse.getLove());
        Log.i(TAG,"insertFav-->"+"被收藏的objectId："+mouse.getObjectId());
        if (uri!=0){
            Log.i(TAG,"insertFav-->"+uri);
            return true;
        }else {
            return false;
        }

    }

    /**
     * 设置内容的收藏状态
     *
     * @param
     * @param lists
     */
    public List<Mouse> setFav(List<Mouse> lists) {
        Cursor cursor = null;
        if (lists != null && lists.size() > 0) {
            for (Iterator iterator = lists.iterator(); iterator.hasNext(); ) {
                Mouse content = (Mouse) iterator.next();
                String where = DBHelper.FavTable.USER_ID + " = '" + MyApplication.getInstance().getCurrentUser().getObjectId()//content.getAuthor().getObjectId()
                        + "' AND " + DBHelper.FavTable.OBJECT_ID + " = '" + content.getObjectId() + "'";
                cursor = dbHelper.query(DBHelper.TABLE_NAME, null, where, null, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    if (cursor.getInt(cursor.getColumnIndex(DBHelper.FavTable.IS_FAV)) == 1) {
                        content.setMyFav(true);
                    } else {
                        content.setMyFav(false);
                    }
                    if (cursor.getInt(cursor.getColumnIndex(DBHelper.FavTable.IS_LOVE)) == 1) {
                        content.setMyLove(true);
                    } else {
                        content.setMyLove(false);
                    }
                }
                Log.i(TAG, content.isMyFav() + "......" + content.isMyLove());
            }
        }
        if (cursor != null) {
            cursor.close();
            dbHelper.close();
        }
        return lists;
    }

    /**
     * 设置内容的收藏状态
     *
     * @param
     * @param lists
     */
    public List<Mouse> setFavInFav(List<Mouse> lists) {
        Cursor cursor = null;
        if (lists != null && lists.size() > 0) {
            for (Iterator iterator = lists.iterator(); iterator.hasNext(); ) {
                Mouse content = (Mouse) iterator.next();
                content.setMyFav(true);
                String where = DBHelper.FavTable.USER_ID + " = '" + MyApplication.getInstance().getCurrentUser().getObjectId()
                        + "' AND " + DBHelper.FavTable.OBJECT_ID + " = '" + content.getObjectId() + "'";
                cursor = dbHelper.query(DBHelper.TABLE_NAME, null, where, null, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    if (cursor.getInt(cursor.getColumnIndex(DBHelper.FavTable.IS_LOVE)) == 1) {
                        content.setMyLove(true);
                    } else {
                        content.setMyLove(false);
                    }
                }
                Log.i(TAG, content.isMyFav() + ".." + content.isMyLove());
            }
        }
        if (cursor != null) {
            cursor.close();
            dbHelper.close();
        }
        return lists;
    }


    public ArrayList<Mouse> queryFav() {
        ArrayList<Mouse> contents = null;
        // ContentResolver resolver = context.getContentResolver();
        Cursor cursor = dbHelper.query(DBHelper.TABLE_NAME, null, null, null, null, null, null);
        Log.i("queryFav","queryFav"+"--1--"+cursor.getCount() + "");
        if (cursor == null) {
            return null;
        }
        contents = new ArrayList<Mouse>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Mouse content = new Mouse();
            User user=new User();
            content.setMyFav(cursor.getInt(3) == 1 ? true : false);
            content.setMyLove(cursor.getInt(4) == 1 ? true : false);
            Log.i("queryFav", cursor.getColumnIndex("isfav") + "/" + cursor.getColumnIndex("islove") + "/" + content.isMyFav() + "/" + content.isMyLove());
            String id=cursor.getString(cursor.getColumnIndex(DBHelper.FavTable.OBJECT_ID));
            String nameId=cursor.getString(cursor.getColumnIndex(DBHelper.FavTable.USER_ID));
            String fav=cursor.getString(cursor.getColumnIndex(DBHelper.FavTable.IS_FAV));


            if (fav.equals("1")){//被收藏了

                Log.i("queryFav","idddd----->"+id);
                Log.i("queryFav", "String---->" + nameId);
                Log.i("queryFav", "Fav------->" + fav);//1或0

                user.setObjectId(nameId);
                content.setAuthor(user);
                content.setObjectId(id);
                contents.add(content);
            }
            for (int i=0;i<contents.size();i++){
                Log.i("queryFav",i+1+"--->"+contents.get(i).getObjectId());
            }

        }
        if (cursor != null) {
            cursor.close();
        }
        return contents;
    }

}
