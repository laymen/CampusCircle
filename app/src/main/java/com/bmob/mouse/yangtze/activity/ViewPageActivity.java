package com.bmob.mouse.yangtze.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bmob.mouse.yangtze.R;
import com.bmob.mouse.yangtze.adapter.ViewPagerAdapter;
import com.bmob.mouse.yangtze.util.ActivityTool;

import java.util.ArrayList;
import java.util.List;

/**
 * 引导页
 * Created by Mouse on 2016/1/5.
 */
public class ViewPageActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private ViewPager viewPager;
    private List<View> viewList;
    private View view1, view2, view3, view4;

    private TextView go;
    private ViewPagerAdapter adapter;

    private ImageView[] dots;
    private int[] ids = {R.id.iv0, R.id.iv1, R.id.iv2, R.id.iv3};//4个导航点

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nag_view_page);

        initialize();
        initDots();
        adapter = new ViewPagerAdapter(viewList);
        viewPager.setAdapter(adapter);
    }

    /**
     * 初始化控件
     */
    private void initialize() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        view1 = View.inflate(this, R.layout.viewpager01, null);
        view2 = View.inflate(this, R.layout.viewpager02, null);
        view3 = View.inflate(this, R.layout.viewpager03, null);
        view4 = View.inflate(this, R.layout.viewpager04, null);

        go = (TextView) view4.findViewById(R.id.go);
        go.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ActivityTool.goNextActivity(ViewPageActivity.this,RegisterALoginActivity.class,false);
            }
        });

        viewList = new ArrayList<View>();
        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);
        viewList.add(view4);

        //TODO
        viewPager.setOnPageChangeListener(this);
    }


    //TODO

    /**
     * 初始化导航点
     */
    private void initDots() {
        dots = new ImageView[viewList.size()];
        for (int i = 0; i < viewList.size(); i++) {
            dots[i] = (ImageView) findViewById(ids[i]);
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    /**
     * 当Viewpager被选择,即正在显示
     *
     * @param position 当前显示的是第几个
     */
    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < ids.length; i++) {
            if (position == i) {
                dots[i].setImageResource(R.drawable.bg_point_selected);
            } else {
                dots[i].setImageResource(R.drawable.bg_point);
            }
        }
    }
}