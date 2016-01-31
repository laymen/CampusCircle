package com.bmob.mouse.yangtze.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.bmob.mouse.yangtze.R;

/**
 * 关于
 * Created by Mouse on 2016/1/11.
 */
public class AboutActivity extends BaseActivity {
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_about);
        initBar();

    }
    private void initBar(){
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("关于");
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
