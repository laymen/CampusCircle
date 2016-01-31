package com.bmob.mouse.yangtze.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bmob.mouse.yangtze.activity.CommentActivity;
import com.bmob.mouse.yangtze.activity.EditActivity;
import com.bmob.mouse.yangtze.activity.MyApplication;
import com.bmob.mouse.yangtze.R;
import com.bmob.mouse.yangtze.db.DatabaseUtil;
import com.bmob.mouse.yangtze.entity.Mouse;
import com.bmob.mouse.yangtze.entity.User;
import com.bmob.mouse.yangtze.util.ActivityTool;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Mouse on 2016/1/8.
 */
public class PersonsalAdapter  extends RecyclerView.Adapter<PersonsalAdapter.MyViewHolder> {
    private static String TAG = "MouseAdapter";
    private Context mContext;
    private List<Mouse> dataList;
    private LayoutInflater mInflater;
    public static final int SAVE_FAVOURITE = 2;

    //构造方法的实现
    public PersonsalAdapter(Context mContext, List<Mouse> dataList) {
        this.mContext = mContext;
        this.dataList = dataList;
        mInflater = LayoutInflater.from(mContext);
    }

    /**
     * 创建MyViewHolder
     *
     * @param parent
     * @param i
     * @return
     */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = mInflater.inflate(R.layout.ai_item, parent, false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    /**
     * 绑定MyViewHolder类，就是给控件赋值
     *
     * @param viewHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
        final Mouse entity = dataList.get(position);
        Log.i(TAG,"--------------------"+entity.getAuthor().getUsername());

        User user = entity.getAuthor();
        String avatarUrl = null;
        if (user.getAvatar() != null) {
            avatarUrl = user.getAvatar();
        }
        ImageLoader.getInstance().displayImage(
                avatarUrl,
                viewHolder.userLogo,
                MyApplication.getInstance().getOptions(
                        R.mipmap.girl),
                new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingComplete(String imageUri, View view,
                                                  Bitmap loadedImage) {
                        // TODO Auto-generated method stub
                        super.onLoadingComplete(imageUri, view, loadedImage);
                    }

                });

//        viewHolder.userLogo.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Log.i(TAG,"--------------------------------------"+entity.getAuthor().getUsername());
//                MyApplication.getInstance().setCurrentMouse(entity);
//                Intent intent = new Intent();
//                intent.setClass(MyApplication.getInstance().getTopActivity(),
//                        PersonalActivity.class);
//                mContext.startActivity(intent);
//
//            }
//        });

        viewHolder.userName.setText(user.getUsername());
        viewHolder.contentText.setText(entity.getContent());

        if (entity.getContentfigureurl() == null) {
            viewHolder.contentImage.setVisibility(View.GONE);
        } else {
            viewHolder.contentImage.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(entity.getContentfigureurl().getFileUrl(mContext) == null ? "" : entity.getContentfigureurl().getFileUrl(
                            mContext), viewHolder.contentImage,
                    MyApplication.getInstance().getOptions(
                            R.mipmap.bg_pic_loading),
                    new SimpleImageLoadingListener() {

                        @Override
                        public void onLoadingComplete(String imageUri,
                                                      View view, Bitmap loadedImage) {
                            // TODO Auto-generated method stub
                            super.onLoadingComplete(imageUri, view,
                                    loadedImage);
                            float[] cons = ActivityTool
                                    .getBitmapConfiguration(
                                            loadedImage,
                                            viewHolder.contentImage,
                                            1.0f);
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                                    (int) cons[0], (int) cons[1]);
                            layoutParams.addRule(RelativeLayout.BELOW,
                                    R.id.content_text);
                            viewHolder.contentImage
                                    .setLayoutParams(layoutParams);
                        }

                    });
        }

        viewHolder.date.setText(entity.getCreatedAt());

        //点赞
        viewHolder.love.setText(entity.getLove() + "");
        if (entity.isMyLove()) {
            viewHolder.love.setTextColor(Color.parseColor("#D95555"));
        }

        Log.i(TAG, "-------------------------我是谁--------------------->" + entity.getAuthor().getUsername());
        User itemUser=entity.getAuthor();
        User currentUser= BmobUser.getCurrentUser(mContext, User.class);//当前登陆的用户
        final  Mouse currentMouse=new Mouse();
        currentMouse.setAuthor(currentUser);
        Log.i(TAG,"--------------------------他是谁？------------------>"+currentUser.getUsername());

        viewHolder.love.setOnClickListener(new View.OnClickListener() {
            boolean oldFav=entity.isMyFav();

            @Override
            public void onClick(View v) {
                if (entity.isMyLove()){//当前用户自己赞过自己
                    ActivityTool.show((Activity)mContext, "您已赞过啦~");
                    return;
                }
                if (DatabaseUtil.getInstance(mContext).isLoved(entity)){//当前用户赞过mouse
                    ActivityTool.show((Activity)mContext, "您已赞过啦~");
                    return ;
                }
                entity.setLove(entity.getLove() + 1);
                viewHolder.love.setTextColor(Color.parseColor("#D95555"));
                viewHolder.love.setText(entity.getLove() + "");
                entity.increment("love", 1);
                if (entity.isMyFav()){//已经收藏了
                    entity.setMyFav(false);//数据库中会被调成已经收藏
                }//经过数据变化后，原来是fav=false,被改成true，所以要调整回来
                entity.update(mContext, new UpdateListener() {

                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        entity.setMyLove(true);
                        entity.setMyFav(oldFav);
                        DatabaseUtil.getInstance(mContext).insertFav(entity);
                        Log.i(TAG, "点赞成功~");
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        // TODO Auto-generated method stub
                        entity.setMyLove(true);
                        entity.setMyFav(oldFav);
                    }
                });
            }

        });
        //分享
        viewHolder.share.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // share to sociaty
                ActivityTool.show((Activity)mContext, "分享给好友看哦~");

            }
        });

//评论
        viewHolder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (MyApplication.getInstance().getCurrentUser()==null) {
                    MyApplication.getInstance().setCurrentMouse(entity);
                    Intent intent = new Intent();
                    intent.setClass(mContext, EditActivity.class);
                    mContext.startActivity(intent);
                    return;
                }
                Intent intent = new Intent();
                intent.setClass(MyApplication.getInstance().getTopActivity(),
                        CommentActivity.class);
                intent.putExtra("data", entity);
                mContext.startActivity(intent);
            }
        });
//收藏
        Log.i(TAG,"-------------------1--------------------"+entity.isMyFav()+"");
        if (entity.isMyFav()) {
            viewHolder.favMark
                    .setImageResource(R.mipmap.ic_action_fav_choose);
        } else {
            viewHolder.favMark
                    .setImageResource(R.mipmap.ic_action_fav_normal);
        }
        viewHolder.favMark.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                onClickFav(v, position);

            }
        });

    }

    public void onClickFav(View v,final  int position) {
        User userf=BmobUser.getCurrentUser(mContext,User.class);
        final  Mouse  tempMouse=dataList.get(position);
        if ( tempMouse != null ) {
            BmobRelation favRelaton = new BmobRelation();
            tempMouse.setMyFav(! tempMouse.isMyFav());
            if ( tempMouse.isMyFav()) {
                ((ImageView) v)
                        .setImageResource(R.mipmap.ic_action_fav_choose);
                favRelaton.add(tempMouse);
                userf.setFavorite(favRelaton);
                userf.update(mContext, new UpdateListener() {

                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        DatabaseUtil.getInstance(mContext).insertFav(tempMouse);
                        ActivityTool.show((Activity) mContext, "收藏成功。");
                        Log.i(TAG, "收藏成功。");

                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        // TODO Auto-generated method stub
                        Log.i(TAG, "收藏失败。请检查网络~");
                        ActivityTool.show((Activity) mContext, "收藏失败。请检查网络~" + arg0);
                    }
                });
                tempMouse.update(mContext, new UpdateListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });

            } else {
                ((ImageView) v)
                        .setImageResource(R.mipmap.ic_action_fav_normal);
                favRelaton.remove(tempMouse);
                userf.setFavorite(favRelaton);
                ActivityTool.show((Activity) mContext, "取消收藏。");
                userf.update(mContext, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        DatabaseUtil.getInstance(mContext).deleteFav(tempMouse);
                        Log.i(TAG, "取消收藏。");

                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        // TODO Auto-generated method stub
                        Log.i(TAG, "取消收藏失败。请检查网络~");
                        ActivityTool.show((Activity) mContext, "取消收藏失败。请检查网络~" + arg0);
                    }
                });

                tempMouse.update(mContext, new UpdateListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });
            }

        }
    }


    @Override
    public int getItemCount() {
        if (dataList != null)
            return dataList.size();
        return 0;
    }

    /**
     * MyViewHolder类，这个类的作用主要用于实例化控件
     */
    class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView userLogo;
        public TextView userName;
        public TextView contentText;
        public ImageView contentImage;

        public ImageView favMark;
        public TextView love;
        public TextView date;
        public TextView share;
        public TextView comment;

        public MyViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.user_name);
            userLogo = (ImageView) itemView.findViewById(R.id.user_logo);
            favMark = (ImageView) itemView.findViewById(R.id.item_action_fav);
            contentText = (TextView) itemView.findViewById(R.id.content_text);
            contentImage = (ImageView) itemView.findViewById(R.id.content_image);
            love = (TextView) itemView.findViewById(R.id.item_action_love);
            date = (TextView) itemView.findViewById(R.id.item_public_time);
            share = (TextView) itemView.findViewById(R.id.item_action_share);
            comment = (TextView) itemView.findViewById(R.id.item_action_comment);
        }
    }

}
