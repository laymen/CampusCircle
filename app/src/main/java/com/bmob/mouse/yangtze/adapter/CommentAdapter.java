package com.bmob.mouse.yangtze.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bmob.mouse.yangtze.activity.MyApplication;
import com.bmob.mouse.yangtze.activity.PersonalActivity;
import com.bmob.mouse.yangtze.R;
import com.bmob.mouse.yangtze.entity.Comment;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

/**
 * Created by Mouse on 2016/1/8.
 */
public class CommentAdapter extends BaseAdapter {
    private static  String TAG="CommentAdapter2";
    private Context mContext;
    private List<Comment> dataList;
    private LayoutInflater mInflater;

    //构造方法的实现
    public CommentAdapter(Context mContext, List<Comment> dataList) {
        this.mContext = mContext;
        this.dataList = dataList;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommentViewHolder commentViewHolder;
        if (convertView==null){
            commentViewHolder=new CommentViewHolder();
            convertView=mInflater.inflate(R.layout.comment_item, null);
            commentViewHolder.userLogo=(ImageView)convertView.findViewById(R.id.user_logo2);
            commentViewHolder.userName=(TextView)convertView.findViewById(R.id.item_user_name);
            commentViewHolder.commentContent=(TextView)convertView.findViewById(R.id.item_content_comment);
            commentViewHolder.date=(TextView)convertView.findViewById(R.id.item_public_time2);
            commentViewHolder.index=(TextView)convertView.findViewById(R.id.item_index_comment);
            convertView.setTag(commentViewHolder);
        }else {
            commentViewHolder=(CommentViewHolder)convertView.getTag();
        }
        final Comment comment=dataList.get(position);
        Log.i(TAG,comment.getUser()+"<-----------c---------->");
        if (comment.getUser()!=null){
            commentViewHolder.userName.setText(comment.getUser().getUsername());
        }else {
            commentViewHolder.userName.setText("墙友");
        }
        String avatarUrl = null;
        Log.i(TAG,"---------ceshi------->"+comment.getUser().getUsername());
        if (comment.getUser().getAvatar() != null) {
            avatarUrl = comment.getUser().getAvatar();
        }

            ImageLoader.getInstance().displayImage(
                    avatarUrl,
                    commentViewHolder.userLogo,
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

        commentViewHolder.userLogo.setOnClickListener(new View.OnClickListener() {//也将传给PersonalActivity.java

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Log.i(TAG,"-------------data-------------->"+comment.getUser().getUsername());
                intent.putExtra("data", comment.getUser());
                intent.setClass(MyApplication.getInstance().getTopActivity(),
                        PersonalActivity.class);
                mContext.startActivity(intent);

            }
        });
        commentViewHolder.index.setText((position+1)+"楼");
        commentViewHolder.date.setText(comment.getCreatedAt());
        commentViewHolder.commentContent.setText(comment.getCommentContent());
        return convertView;
    }
    public static class CommentViewHolder{
        public ImageView userLogo;
        public TextView userName;
        public TextView commentContent;
        public TextView date;
        public  TextView index;
    }
}

