package com.bmob.mouse.yangtze.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bmob.mouse.yangtze.R;
import com.bmob.mouse.yangtze.adapter.MouseAdapter;
import com.bmob.mouse.yangtze.entity.Mouse;
import com.bmob.mouse.yangtze.entity.User;
import com.bmob.mouse.yangtze.util.ActivityTool;
import com.bmob.mouse.yangtze.util.Constant;
import com.bmob.mouse.yangtze.util.ToastTool;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;

public class MainActivity extends BaseActivity {
    private static String TAG = "MainActivity";
    private Toolbar mToolBar;
    private FloatingActionButton fab;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView; //菜单的所在布局
    private ActionBarDrawerToggle mActionBarDrawerToggle;    // 点击菜单出现与关闭
    private RelativeLayout user_layout;

    private ImageView user_logo_navi;
    private  TextView user_name_navi;
    private  TextView user_signup_navi;

    //数据显示所用控件
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private MouseAdapter adapter;

    private BmobQuery<Mouse> query;//用于查询数据
    private BmobUser currentUser;
    private   User user;
    private int pageNum=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start();
    }

    /**
     * 无网络情况下得到缓存数据
     */
    private void getCacheData() {

        query = new BmobQuery<>();
        query.include("author");
        query.setLimit(30);  //限制接收的数据数量
        query.order("-updatedAt");
        query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ONLY);  //只存缓存中获得数据

        query.findObjects(MainActivity.this, new FindListener<Mouse>() {
            @Override
            public void onSuccess(List<Mouse> list) {
                if (list != null) {
                    initNews(list);
                } else {
                    ToastTool.Show(mContext, "当前缓存中无数据，请检查你的网络");
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    private void start() {
        currentUser = MyApplication.getInstance().getCurrentUser();
        initView();//初始化视图
        initDatas(); //数据源操作
        initSwipe();
        initLeftInform();
        doEvent();
        initToolBar();  //初始化ToolBar
    }


    private void doEvent() {
        user_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser != null) {
                } else {
                    login();
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentUser!=null){
                    Bundle m=new Bundle();
                    ActivityTool.goNextActivityForResult(mContext, EditActivity.class,m ,RESULT_OK,false);
                }else {
                    ToastTool.Show(mContext, "亲，只有登录才能发布帖子呀~");
                }
            }
        });

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id=menuItem.getItemId();
                Intent intent = new Intent();
                switch (id){
                    case R.id.nav_chat:
                        intent.setClass(MyApplication.getInstance().getTopActivity(),
                                RecentActivity.class);
                        mContext.startActivity(intent);
                        break;
                    case R.id.nav_setting:
                        intent.setClass(MyApplication.getInstance().getTopActivity(),
                                SettingActivity.class);
                        mContext.startActivity(intent);
                        break;
                    case R.id.nav_about:
                        intent.setClass(MyApplication.getInstance().getTopActivity(),
                                AboutActivity.class);
                        mContext.startActivity(intent);
                        break;
                    case R.id.nav_feedback:
                        intent.setClass(MyApplication.getInstance().getTopActivity(),
                                FeedBackActivity.class);
                        mContext.startActivity(intent);
                        break;
                    default:
                        break;

                }
                return false;
            }
        });


    }

    private void initLeftInform() {
        if (currentUser != null) {
            String objectId = currentUser.getObjectId();
            BmobQuery<Mouse> query = new BmobQuery<>();
            query.getObject(MainActivity.this, objectId, new GetListener<Mouse>() {
                @Override
                public void onSuccess(Mouse hUser) {

                }

                @Override
                public void onFailure(int i, String s) {

                }
            });
        }

    }

    /**
     * 初始化下拉刷新
     */
    private void initSwipe() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        //设置刷新时动画的颜色
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //TODO 刷新的时候获取数据
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initDatas(); //刷新
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });

    }

    /**
     * 初始化数据源
     */
    private void initDatas() {
        user=BmobUser.getCurrentUser(mContext,User.class);//得到当前的登陆用户
        query = new BmobQuery<Mouse>();
        BmobQuery<User>innerQuery=new BmobQuery<>();
        innerQuery.addWhereEqualTo("schoolName", user.getSchoolName());
        query.include("author");
        query.setLimit(Constant.NUMBERS_PER_PAGE);
        query.order("-createdAt");
        query.addWhereMatchesQuery("author","_User",innerQuery);
        query.findObjects(this, new FindListener<Mouse>() {
            @Override
            public void onSuccess(List<Mouse> data) {
                Log.i("datasize", "----------muouse----------------------------" + data.size());
                if (data != null) {
                    initNews(data);
                    pageNum++;
                    if (data.size() < Constant.NUMBERS_PER_PAGE) {
                        ActivityTool.show(MainActivity.this, "已加载完所有数据~");
                    }
                } else {
                    pageNum--;
                    ActivityTool.show(MainActivity.this, "暂无更多数据~");
                }
            }

            @Override
            public void onError(int i, String s) {
                pageNum--;
                Log.i(TAG,"wrong----->"+s);
                ToastTool.Show(mContext, "查找失败,检查网络是否畅通~");
            }
        });

    }

    private void initNews(List<Mouse> list) {
        adapter = new MouseAdapter(mContext, list);
        mRecyclerView.setAdapter(adapter);

        //设置RecyclerView的布局管理
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        //设置RecyclerView的动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //设置监听事件

    }

    private void initToolBar() {
        if (user.getSchoolName()==null){
            return;
        }
        String Titlename=user.getSchoolName();
        mToolBar.setTitle(Titlename+"校园圈");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolBar, R.string.drawer_open, R.string.drawer_close);
        mActionBarDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);



    }

    private void initView() {
        mToolBar = (Toolbar) findViewById(R.id.tool_bar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mNavigationView = (NavigationView) findViewById(R.id.nv_main_menu);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_news);
        user_layout = (RelativeLayout) findViewById(R.id.header_content);

        user_logo_navi=(ImageView)findViewById(R.id.user_logo_navi);
        user_name_navi=(TextView)findViewById(R.id.user_name_navi);
        user_signup_navi=(TextView)findViewById(R.id.user_signup_navi);

        User currentUser = MyApplication.getInstance().getCurrentUser();
        Log.i(TAG,currentUser.getUsername()+"----------------------");
        if (currentUser!=null&&currentUser.getAvatar()!=null){
            ImageLoader.getInstance().displayImage(
                    currentUser.getAvatar(),
                    user_logo_navi,
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
        user_name_navi.setText(currentUser.getUsername());
        user_signup_navi.setText(currentUser.getSignature());

    }

    private void login() {
        // 当前用户登录
        User currentUser = MyApplication.getInstance().getCurrentUser();
        if (currentUser != null) {
            // 允许用户使用应用,即有了用户的唯一标识符，可以作为发布内容的字段
            String name = currentUser.getUsername();
            String email = currentUser.getEmail();
            Log.i(TAG, "username:" + name + ",email:" + email);
            // ActivityTool.goNextActivity(mConext, EditActivity.class, null, false);
        } else {
            // 缓存用户对象为空时， 可打开用户注册界面…
            ToastTool.Show(mContext, "请先登录~");
            ActivityTool.goNextActivity(mContext, RegisterALoginActivity.class, false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);// 按钮按下，将抽屉打开
                return true;
            case R.id.item_store:
                Intent intent = new Intent();
                intent.setClass(MyApplication.getInstance().getTopActivity(),
                        MyFavActivity.class);
                mContext.startActivity(intent);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 使用inflate方法来把布局文件中的定义的菜单 加载给 第二个参数所对应的menu对象
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


}