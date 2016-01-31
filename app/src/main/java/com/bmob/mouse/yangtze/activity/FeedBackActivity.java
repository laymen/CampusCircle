package com.bmob.mouse.yangtze.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bmob.mouse.yangtze.R;
import com.bmob.mouse.yangtze.entity.Advice;
import com.bmob.mouse.yangtze.util.ActivityTool;
import com.bmob.mouse.yangtze.util.IsDoubleChick;

import cn.bmob.v3.listener.SaveListener;

/**
 * 反馈
 * Created by Mouse on 2016/1/11.
 */
public class FeedBackActivity extends BaseActivity {
    private Toolbar toolbar;
    private Button commit;
    private EditText input;
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_feedback);
        initBar();
        input=(EditText)findViewById(R.id.question_comment_content);
        commit=(Button)findViewById(R.id.question_comment_commit);
        commit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (IsDoubleChick.isDoublechick(v.getId())) {
                    if (TextUtils.isEmpty(input.getText().toString().trim())) {
                        ActivityTool.show(FeedBackActivity.this, "请先输入。。。");
                    } else {
                        sendFeedBack(input.getText().toString().trim());
                    }
                }
            }
        });

    }
    private void sendFeedBack(String ques){
        Advice que=new Advice();
        que.setQues(ques);
        que.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                finish();
                ActivityTool.show(FeedBackActivity.this,"我们已收到你的反馈，谢谢");
            }

            @Override
            public void onFailure(int i, String s) {
                ActivityTool.show(FeedBackActivity.this,"发送反馈失败~");
            }
        });


    }

    private void initBar(){
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("反馈");
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
