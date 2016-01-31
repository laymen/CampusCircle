package com.bmob.mouse.yangtze.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bmob.mouse.yangtze.R;
import com.bmob.mouse.yangtze.adapter.PersonsalAdapter;
import com.bmob.mouse.yangtze.entity.Mouse;
import com.bmob.mouse.yangtze.entity.User;
import com.bmob.mouse.yangtze.util.ActivityTool;
import com.bmob.mouse.yangtze.util.Constant;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Mouse on 2016/1/6.
 */
public class PersonalActivity extends BaseActivity {
    private static String TAG="PersonalActivity";
    public Toolbar toolbar;

    private ImageView personalIcon;
    private TextView personalName;
    private TextView personalTitle;
    private TextView personalSign;

    private List<Mouse> dataList=new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private PersonsalAdapter  mAdapter;
    private int pageNum;

    private User user2;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_personal);
        user2 = (User) getIntent().getSerializableExtra("data");//来自CommentActivity传来的 即是item上的对象
                                                               //或来自CommentAdapter传来的数据
        initBar();
        initView();
        setupView();
    }
    private  void initSwipe(List<Mouse> dataList){
        mAdapter=new PersonsalAdapter(mContext,dataList);
        mRecyclerView.setAdapter(mAdapter);
        //设置RecyclerView的布局管理
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        //设置RecyclerView的动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //设置刷新时动画的颜色

    }
    private void setupView(){
        updatePersonalInfo(user2);
        initMyPublish();


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

    private void initMyPublish(){
        if (isCurrentUser(user2)) {//自己
            personalTitle.setText("我发表过的");
            User user = BmobUser.getCurrentUser(mContext, User.class);
            updatePersonalInfo(user2);
        } else {
            if (user2 != null && user2.getSex().equals(Constant.SEX_FEMALE)) {
                personalTitle.setText("她发表过的");
            } else if (user2 != null
                    && user2.getSex().equals(Constant.SEX_MALE)) {
                personalTitle.setText("他发表过的");
            }
        }
        pageNum = 0;
        fetchData();
    }

    /**
     * 判断点击条目的用户是否是当前登录用户
     *
     * @return
     */
    private boolean isCurrentUser(User mouse) {
        if (mouse!=null) {
            User cUser = BmobUser.getCurrentUser(mContext, User.class);
            if (cUser != null &&cUser.getUsername().equals(mouse.getUsername())) {
                return true;
            }
        }
        return false;
    }
    private void updatePersonalInfo(User mouse) {//item上面的对象
        if(mouse==null){
            return;
        }
        personalName.setText(mouse.getUsername());
        personalSign.setText(mouse.getSignature());
        if (mouse != null&&mouse.getAvatar()!=null) {
            ImageLoader.getInstance().displayImage(
                    mouse.getAvatar(),
                    personalIcon,
                    MyApplication.getInstance().getOptions(
                            R.mipmap.girl),
                    new SimpleImageLoadingListener() {

                        @Override
                        public void onLoadingComplete(String imageUri,
                                                      View view, Bitmap loadedImage) {
                            // TODO Auto-generated method stub
                            super.onLoadingComplete(imageUri, view, loadedImage);
                            Log.i(TAG, "load personal icon completed.");
                        }

                    });

        }
    }

    private void fetchData(){
        BmobQuery<Mouse> query = new BmobQuery<>();
        query.setLimit(Constant.NUMBERS_PER_PAGE);
//        query.setSkip(Constant.NUMBERS_PER_PAGE * (pageNum++));
        query.order("-createdAt");
        query.include("author");
        query.addWhereEqualTo("author", user2);
        Log.i(TAG, "------List<Mouse> data---------------------" );
        query.findObjects(mContext, new FindListener<Mouse>() {

            @Override
            public void onSuccess(List<Mouse> data) {
                Log.i(TAG,"------List<Mouse> data---------------------"+data.size());
                if (data != null) {
                    initSwipe(data);
                    pageNum++;
                    if (data.size() < Constant.NUMBERS_PER_PAGE) {
                        ActivityTool.show(PersonalActivity.this, "已加载完所有数据~");
                    }
                } else {
                    pageNum--;
                    ActivityTool.show(PersonalActivity.this,"暂无更多数据~");
                }
            }

            @Override
            public void onError(int arg0, String msg) {
                pageNum--;
            }
        });


    }

    private void initView(){
        personalIcon=(ImageView)findViewById(R.id.personal_icon);
        personalName=(TextView)findViewById(R.id.personl_name);
        personalTitle=(TextView)findViewById(R.id.personl_title);
        personalSign=(TextView)findViewById(R.id.personl_signature);
        swipeRefreshLayout=(SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mRecyclerView=(RecyclerView)findViewById(R.id.rv_news);


    }
    private  void initBar(){
        toolbar = (Toolbar) super.findViewById(R.id.tool_bar);
        toolbar.setTitle("个人中心");
        setSupportActionBar(toolbar);//把 toolbar 设置到Activity 中
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
