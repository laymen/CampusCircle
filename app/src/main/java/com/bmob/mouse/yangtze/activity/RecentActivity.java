package com.bmob.mouse.yangtze.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bmob.mouse.yangtze.R;
import com.bmob.mouse.yangtze.adapter.MessageRecentAdapter;
import com.bmob.mouse.yangtze.view.DeletableEditText;
import com.bmob.mouse.yangtze.view.DialogTips;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.db.BmobDB;

/**
 * 最近会话列表
 * Created by Mouse on 2016/1/29.
 */
public class RecentActivity extends BaseActivity implements AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener {
    ListView listview;
    public Toolbar toolbar;
    MessageRecentAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);
        initBar();
        initView();
    }
    private void initView(){
        listview = (ListView)findViewById(R.id.list);
        listview.setOnItemClickListener(this);
        listview.setOnItemLongClickListener(this);
        adapter = new MessageRecentAdapter(mContext, R.layout.item_conversation, BmobDB.create(mContext).queryRecents());
        listview.setAdapter(adapter);


    }
    private void initBar() {
        toolbar = (Toolbar)findViewById(R.id.tool_bar);
        toolbar.setTitle("会话");
        setSupportActionBar(toolbar);//把 toolbar 设置到Activity 中

    } @Override
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BmobRecent recent = adapter.getItem(position);
        //重置未读消息
        BmobDB.create(mContext).resetUnread(recent.getTargetid());
        //组装聊天对象
        BmobChatUser user = new BmobChatUser();

        user.setAvatar(recent.getAvatar());
        user.setNick(recent.getNick());
        user.setUsername(recent.getUserName());
        user.setObjectId(recent.getTargetid());
        Intent intent = new Intent(mContext, ChatActivity.class);
        intent.putExtra("data", user);
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        BmobRecent recent = adapter.getItem(position);
        showDeleteDialog(recent);
        return true;

    }
    public void showDeleteDialog(final BmobRecent recent) {
        DialogTips dialog = new DialogTips(mContext,recent.getUserName(),"删除会话", "确定",true,true);
        // 设置成功事件
        dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int userId) {
                deleteRecent(recent);
            }
        });
        // 显示确认对话框
        dialog.show();
        dialog = null;
    }

    /**
     *删除会话
     * @param recent
     */
    private void deleteRecent(BmobRecent recent){
        adapter.remove(recent);
        BmobDB.create(mContext).deleteRecent(recent.getTargetid());
        BmobDB.create(mContext).deleteMessages(recent.getTargetid());
    }

}
