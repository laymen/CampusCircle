package com.bmob.mouse.yangtze.util;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

/**
 * ListView 为空时的文本
 * Created by Mouse on 2016/1/9.
 */
public class ListViewUtil {
    /**
     * 设置RecyclerView为空时的文本
     * @param context
     * @param lv
     * @param text
     */
    public static void setEmptyText(Context context,ListView lv,String text){
        TextView tvEmptyText=new TextView(context);//创建TextView
        tvEmptyText.setTextColor(Color.GRAY);
        tvEmptyText.setText(text);
        tvEmptyText.setTextSize(20);
        tvEmptyText.setGravity(Gravity.CENTER);//设置文字居中
        tvEmptyText.setVisibility(View.GONE);//设置默认不可见
        ((ViewGroup)lv.getParent()).addView(tvEmptyText);//将tvEmptyText添加到ListView所在的父布局中
        lv.setEmptyView(tvEmptyText);//设置当ListView没有数据时显示的内容
    }

}
