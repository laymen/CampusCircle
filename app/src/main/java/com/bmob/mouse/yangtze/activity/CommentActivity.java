package com.bmob.mouse.yangtze.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bmob.mouse.yangtze.R;
import com.bmob.mouse.yangtze.adapter.CommentAdapter;
import com.bmob.mouse.yangtze.db.DatabaseUtil;
import com.bmob.mouse.yangtze.entity.Comment;
import com.bmob.mouse.yangtze.entity.Mouse;
import com.bmob.mouse.yangtze.entity.User;
import com.bmob.mouse.yangtze.util.ActivityTool;
import com.bmob.mouse.yangtze.util.Constant;
import com.bmob.mouse.yangtze.util.ImageLoadOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 评论
 * Created by Mouse on 2016/1/6.
 */
public class CommentActivity extends BaseActivity implements View.OnClickListener {
    private static String TAG = "CommentActivity";
    private Context mContext;
    public Toolbar toolbar;
    private ListView commentList;
    private TextView footer;
    private CommentAdapter mAdapter;
    private List<Comment> comments = new ArrayList<Comment>();

    private Mouse mouse;
    private String commentEdit="";
    private int pageNum;

    private EditText commentContent;
    private Button commentCommit;
    private TextView userName;
    private TextView commentItemContent;
    private ImageView commentItemImage;
    private ImageView userLogo;
    private TextView publicTime;
    private ImageView myFav;
    private TextView comment;
    private TextView share;
    private TextView love;


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_comment);
        mContext = CommentActivity.this;
        initBar();
        initView();
        setupView();
        setListener();
        fetchData();
    }
    private void fetchData(){
        BmobQuery<Comment> query = new BmobQuery<>();//查询Comment表
        query.addWhereRelatedTo("relation", new BmobPointer(mouse));//查询Comment:mouse表中有relation-(comment)--->mouse
        query.include("user");                                      //查询Mouse:user表中有favorite--(mouse)------>User
                                                                   //  查询user:mouse表中有favorite----(user)------->mouse
        query.order("createdAt");
        query.setLimit(Constant.NUMBERS_PER_PAGE);
        query.setSkip(Constant.NUMBERS_PER_PAGE * (pageNum++));
        query.findObjects(this, new FindListener<Comment>() {

            @Override
            public void onSuccess(List<Comment> data) {
                // TODO Auto-generated method stub
                Log.i(TAG, "get comment success!" + data.size());
                if (data.size() != 0 && data.get(data.size() - 1) != null) {

                    if (data.size() < Constant.NUMBERS_PER_PAGE) {
                        ActivityTool.show((Activity) mContext, "已加载完所有评论~");
                        footer.setText("暂无更多评论~");
                    }

                    comments.addAll(data);
                    mAdapter.notifyDataSetChanged();
                    setListViewHeightBasedOnChildren(commentList);
                    Log.i(TAG, "refresh");
                } else {
                    ActivityTool.show((Activity) mContext, "暂无更多评论~");
                    footer.setText("暂无更多评论~");
                    pageNum--;
                }
            }

            @Override
            public void onError(int arg0, String arg1) {
                // TODO Auto-generated method stub
                ActivityTool.show(CommentActivity.this, "获取评论失败。请检查网络~");
                pageNum--;
            }
        });

    }

    private void setListener() {
        footer.setOnClickListener(this);
        commentCommit.setOnClickListener(this);
        userLogo.setOnClickListener(this);
        myFav.setOnClickListener(this);
        love.setOnClickListener(this);
        share.setOnClickListener(this);
        comment.setOnClickListener(this);

    }

    private void initMoodView(Mouse mouse) {
        if (mouse == null) {
            return;
        }
        if (mouse != null) {
            // mouse.getAuthor().getAvatar().getFileUrl(mContext),
            if(mouse.getAuthor().getAvatar()!=null) {
                ImageLoader.getInstance().displayImage(
                        mouse.getAuthor().getAvatar(),
                        userLogo,
                        MyApplication.getInstance().getOptions(
                                R.mipmap.girl),
                        new SimpleImageLoadingListener() {

                            @Override
                            public void onLoadingComplete(String imageUri,
                                                          View view, Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, view, loadedImage);
                                Log.i(TAG, "load personal icon completed.");
                            }

                        });
            }
            userName.setText(mouse.getAuthor().getUsername());
            publicTime.setText(mouse.getCreatedAt());
            commentItemContent.setText(mouse.getContent());
            if (mouse.getContentfigureurl() == null) {
                commentItemImage.setVisibility(View.GONE);

            } else {
                commentItemImage.setVisibility(View.VISIBLE);
                BmobFile contentUrl = mouse.getContentfigureurl();
                ImageLoader.getInstance().displayImage(contentUrl.getFileUrl(mContext), commentItemImage,
                        ImageLoadOptions.getOptions());
            }
            love.setText(mouse.getLove() + "");
            if (mouse.isMyLove()) {
                love.setTextColor(Color.parseColor("#D95555"));
            } else {
                love.setTextColor(Color.parseColor("#000000"));
            }
            if (mouse.isMyFav()) {
                myFav.setImageResource(R.mipmap.ic_action_fav_choose);
            } else {
                myFav.setImageResource(R.mipmap.ic_action_fav_normal);
            }
        }
    }

    private void setupView() {
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                        | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mouse = (Mouse) getIntent().getSerializableExtra("data");//来自MouseAdapter传来的
        pageNum = 0;
        mAdapter=new CommentAdapter(mContext,comments);
        commentList.setAdapter(mAdapter);
        setListViewHeightBasedOnChildren(commentList);
        commentList.setCacheColorHint(0);
        commentList.setScrollingCacheEnabled(false);
        commentList.setScrollContainer(false);
        commentList.setFastScrollEnabled(true);
        commentList.setSmoothScrollbarEnabled(true);

        initMoodView(mouse);
    }


    private void initView() {
        commentList = (ListView) findViewById(R.id.comment_list);
        footer = (TextView) findViewById(R.id.loadmore);

        commentContent = (EditText) findViewById(R.id.comment_content);
        commentCommit = (Button) findViewById(R.id.comment_commit);
//ai
        userName = (TextView) findViewById(R.id.user_name);
        commentItemContent = (TextView) findViewById(R.id.content_text);
        commentItemImage = (ImageView) findViewById(R.id.content_image);
        userLogo = (ImageView) findViewById(R.id.user_logo);
        publicTime = (TextView) findViewById(R.id.item_public_time);
        myFav = (ImageView) findViewById(R.id.item_action_fav);
        comment = (TextView) findViewById(R.id.item_action_comment);
        share = (TextView) findViewById(R.id.item_action_share);
        love = (TextView) findViewById(R.id.item_action_love);


    }

    private void initBar() {
        toolbar = (Toolbar) super.findViewById(R.id.tool_bar);
        toolbar.setTitle("发表评论");
        setSupportActionBar(toolbar);//把 toolbar 设置到Activity 中

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_logo:
                onClickUserLogo();
                break;
            case R.id.loadmore:
                fetchData();
                break;
            case R.id.comment_commit:
                onClickCommit();
                break;
            case R.id.item_action_fav:
                onClickFav(v);
                break;
            case R.id.item_action_love:
                onClickLove();
                break;
            case R.id.item_action_share:
               // onClickShare();
                break;
            case R.id.item_action_comment:
                onClickComment();
                break;
            default:
                break;
        }

    }

    private void onClickComment() {
        commentContent.requestFocus();
        InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(commentContent, 0);
    }

    boolean isFav = false;
    private  void onClickLove(){
        User user=BmobUser.getCurrentUser(this,User.class);
        if (user == null) {
            // 前往登录注册界面
            ActivityTool.show(this, "请先登录。");
            Intent intent = new Intent();
            intent.setClass(this, RegisterALoginActivity.class);
            startActivity(intent);
            return;
        }
        if (mouse.isMyLove()){
            ActivityTool.show((Activity) mContext, "您已经赞过啦~");
            return;
        }

        isFav=mouse.isMyFav();
        if (isFav) {
            mouse.setMyFav(false);
        }
        Log.i(TAG,"9999999999999999---->"+mouse.getAuthor().getUsername());
        Log.i(TAG,"-----------=========>"+mouse.getLove());
        love.setText(mouse.getLove() + 1+"");
        love.setTextColor(Color.parseColor("#D95555"));
        mouse.increment("love", 1);
        mouse.update(mContext, new UpdateListener() {

            @Override
            public void onSuccess() {
                mouse.setMyLove(true);
                mouse.setMyFav(isFav);
                DatabaseUtil.getInstance(mContext).insertFav(mouse);
                ActivityTool.show((Activity) mContext, "点赞成功~");
            }

            @Override
            public void onFailure(int arg0, String arg1) {
            }
        });

    }

    private void onClickFav(View v) {
        User userf = BmobUser.getCurrentUser(mContext, User.class);
        Log.i(TAG, "----------commentActivity--onClickFav-----------" + mouse.getAuthor().getUsername());
        if (mouse != null) {
            BmobRelation favRelaton = new BmobRelation();
            mouse.setMyFav(!mouse.isMyFav());
            if (mouse.isMyFav()) {
                ((ImageView) v)
                        .setImageResource(R.mipmap.ic_action_fav_choose);
                favRelaton.add(mouse);
                ActivityTool.show((Activity) mContext, "收藏成功。");
            } else {
                ((ImageView) v)
                        .setImageResource(R.mipmap.ic_action_fav_normal);
                favRelaton.remove(mouse);
                ActivityTool.show((Activity) mContext, "取消收藏。");
            }
            userf.setFavorite(favRelaton);
            userf.update(this, new UpdateListener() {
                @Override
                public void onSuccess() {
                    Log.i(TAG, "收藏成功。");
                    DatabaseUtil.getInstance(mContext).insertFav(mouse);
                    ActivityTool.show(CommentActivity.this, "收藏成功。");
                }

                @Override
                public void onFailure(int i, String s) {
                    Log.i(TAG, "收藏失败。请检查网络~");
                    ActivityTool.show(CommentActivity.this, "收藏失败。请检查网络~"
                            + s);
                }
            });
            mouse.update(mContext, new UpdateListener() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailure(int i, String s) {

                }
            });
        }
    }

    private void onClickCommit(){
        // TODO Auto-generated method stub
        User currentUser = BmobUser.getCurrentUser(this, User.class);
        if (currentUser != null) {// 已登录
            commentEdit = commentContent.getText().toString().trim();
            if (TextUtils.isEmpty(commentEdit)) {
                ActivityTool.show(this, "评论内容不能为空。");
                return;
            }

            publishComment(currentUser, commentEdit);
        } else {// 未登录
            ActivityTool.show(this, "发表评论前请先登录。");
            Intent intent = new Intent();
            intent.setClass(this, RegisterALoginActivity.class);
            startActivityForResult(intent, Constant.PUBLISH_COMMENT);
        }

    }

    private void publishComment(User user, String content){
        final Comment comment = new Comment();
        comment.setUser(user);
        comment.setCommentContent(content);
        comment.save(this, new SaveListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                ActivityTool.show(CommentActivity.this, "评论成功。");
                if (comments.size() < Constant.NUMBERS_PER_PAGE) {
                    comments.add(comment);
                    mAdapter.notifyDataSetChanged();
                    setListViewHeightBasedOnChildren(commentList);
                }
                commentContent.setText("");

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(commentContent.getWindowToken(), 0);

                // 将该评论与强语绑定到一起
                BmobRelation relation = new BmobRelation();
                relation.add(comment);
                mouse.setRelation(relation);
                mouse.update(mContext, new UpdateListener() {

                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        Log.i(TAG, "更新评论成功。");

                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        // TODO Auto-generated method stub
                        Log.i(TAG, "更新评论失败。" + arg1);
                    }
                });

            }

            @Override
            public void onFailure(int arg0, String arg1) {
                ActivityTool.show(CommentActivity.this, "评论失败。请检查网络~");
            }
        });
    }

    private void onClickUserLogo() {//commentActivity放在上面
        // 跳转到个人信息界面
        Log.i(TAG,mouse.getAuthor().getUsername()+"--------------------------");
        Intent intent = new Intent();
        intent.putExtra("data", mouse.getAuthor());//把User传过去啊
        intent.setClass(MyApplication.getInstance().getTopActivity(),
                PersonalActivity.class);
        mContext.startActivity(intent);

    }

    /***
     * 动态设置listview的高度 item 总布局必须是linearLayout
     *
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1))
                + 15;
        listView.setLayoutParams(params);
    }

}
