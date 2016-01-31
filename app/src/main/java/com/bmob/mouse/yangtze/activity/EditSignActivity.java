package com.bmob.mouse.yangtze.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bmob.mouse.yangtze.R;
import com.bmob.mouse.yangtze.entity.User;
import com.bmob.mouse.yangtze.util.ActivityTool;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 编辑签名Sigin
 * Created by Mouse on 2016/1/10.
 */
public class EditSignActivity extends BaseActivity {
    private Toolbar toolbar;
    private Button commit;
    private EditText input;
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_change_sign);
        initBar();
        input = (EditText)findViewById(R.id.sign_comment_content);
        commit = (Button)findViewById(R.id.sign_comment_commit);
        commit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(input.getText().toString().trim())){
                    ActivityTool.show(EditSignActivity.this, "请先输入。。。");
                }else{
                    updateSign(input.getText().toString().trim());
                }
            }
        });

    }

    private void initBar(){
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("修改备注");
        setSupportActionBar(toolbar);//把 toolbar 设置到Activity 中
    }

    private void updateSign(String sign){
        User user = BmobUser.getCurrentUser(mContext, User.class);
        if(user != null && sign != null){
            user.setSignature(sign);
            user.update(mContext, new UpdateListener() {

                @Override
                public void onSuccess() {
                    ActivityTool.show(EditSignActivity.this, "更改信息成功。");
                    setResult(Activity.RESULT_OK);
                    finish();
                }

                @Override
                public void onFailure(int arg0, String arg1) {
                    ActivityTool.show(EditSignActivity.this, "更改信息失败。请检查网络");
                }
            });
        }
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
