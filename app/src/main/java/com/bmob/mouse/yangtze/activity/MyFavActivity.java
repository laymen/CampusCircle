package com.bmob.mouse.yangtze.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.bmob.mouse.yangtze.R;
import com.bmob.mouse.yangtze.adapter.MouseAdapter;
import com.bmob.mouse.yangtze.db.DatabaseUtil;
import com.bmob.mouse.yangtze.entity.Mouse;
import com.bmob.mouse.yangtze.entity.User;
import com.bmob.mouse.yangtze.util.ActivityTool;
import com.bmob.mouse.yangtze.util.Constant;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;

/**
 * 我的收藏
 * Created by Mouse on 2016/1/9.
 */
public class MyFavActivity extends BaseActivity {
    private static String TAG = "MyFavActivity";
    private Toolbar mToolBar;
    private MouseAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Mouse> dataList = new ArrayList<>();
    private int pageNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);
        pageNum = 0;
        initView();
        initToolBar();  //初始化ToolBar
        setupView();
        fetchData();

    }

    private void fetchData() {//查找帖子是当前用户收藏的
        // ArrayList<Mouse> mouses = DatabaseUtil.getInstance(mContext).queryFav();//当前用户所有的收藏对象
        User user = BmobUser.getCurrentUser(mContext, User.class);//当前用户
        if (user != null) {
            BmobQuery<Mouse>query=new BmobQuery<>();
            query.addWhereRelatedTo("favorite",new BmobPointer(user));
            query.include("user");
            query.order("-createdAt");
            query.setLimit(Constant.NUMBERS_PER_PAGE);
            query.findObjects(mContext, new FindListener<Mouse>() {

                @Override
                public void onSuccess(List<Mouse> data) {
                    Log.i(TAG, "------------------------------------" + data.size());
                    if (data != null) {
                        initSwipe(data);
                        pageNum++;
                        if (data.size() < Constant.NUMBERS_PER_PAGE) {
                            ActivityTool.show((Activity) mContext, "已加载完所有数据~");
                        }
                    } else {
                        pageNum--;
                        ActivityTool.show((Activity) mContext, "暂无更多数据~");
                    }
                }

                @Override
                public void onError(int i, String s) {
                    pageNum--;
                }
            });
        }
    }
    private void initSwipe(List<Mouse> dataList) {
        mAdapter = new MouseAdapter(mContext, dataList);
        mRecyclerView.setAdapter(mAdapter);
        //设置RecyclerView的布局管理
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        //设置RecyclerView的动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //设置刷新时动画的颜色

    }

    private void setupView() {
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //TODO 刷新的时候获取数据
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fetchData(); //刷新
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });

    }

    private void initView() {
        mToolBar = (Toolbar) findViewById(R.id.tool_bar);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_news);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
    }

    private void initToolBar() {
        mToolBar.setTitle("我的收藏");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


}

