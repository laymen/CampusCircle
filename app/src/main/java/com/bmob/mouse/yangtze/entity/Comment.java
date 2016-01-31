package com.bmob.mouse.yangtze.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by Mouse on 2016/1/1.
 */
public class Comment extends BmobObject {
    public static final String TAG="Comment";
    private User user;//指向User表,一对一的关系该comment属于某个用户User,评论的用户
    private String commentContent;

    public static String getTag() {
        return TAG;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }
}
