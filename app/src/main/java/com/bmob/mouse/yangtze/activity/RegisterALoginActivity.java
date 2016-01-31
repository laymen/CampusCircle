package com.bmob.mouse.yangtze.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bmob.mouse.yangtze.R;
import com.bmob.mouse.yangtze.proxy.UserProxy;
import com.bmob.mouse.yangtze.util.ActivityTool;
import com.bmob.mouse.yangtze.util.StringUtils;
import com.bmob.mouse.yangtze.view.DeletableEditText;

/**
 * Created by Mouse on 2016/1/5.
 */
public class RegisterALoginActivity extends BaseActivity implements View.OnClickListener, UserProxy.ILoginListener, UserProxy.ISignUpListener, UserProxy.IResetPasswordListener {
    private static String TAG = "RegisterAndLoginActivity";
    private Toolbar toolbar;
    TextView loginTitle;
    TextView registerTitle;
    TextView resetPassword;

    DeletableEditText userNameInput;
    DeletableEditText userPasswordInput;
    DeletableEditText userEmailInput;
    DeletableEditText userSchoolInput;
    Button registerButton;
    private Context mContext;

    @Override
    public void onLoginSuccess() {
        ActivityTool.show(this, "登录成功~");
        ActivityTool.goNextActivity(mContext,MainActivity.class,true);//跳转
        Log.i(TAG, "login sucessed!");
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onLoginFailure(String msg) {
        // TODO Auto-generated method stub
        ActivityTool.show(this, "登录失败。请确认网络连接后再重试。");
        Log.i(TAG, "login failed!" + msg);
    }

    @Override
    public void onSignUpSuccess() {
        // TODO Auto-generated method stub
        ActivityTool.show(this, "注册成功");
        Log.i(TAG, "register successed！");
        operation = UserOperation.LOGIN;
        updateLayout(operation);
    }

    @Override
    public void onSignUpFailure(String msg) {
        // TODO Auto-generated method stub
        ActivityTool.show(this, "注册失败。请确认网络连接后再重试。");
        Log.i(TAG, "register failed！");
    }

    @Override
    public void onResetSuccess() {
        // TODO Auto-generated method stub
        ActivityTool.show(this, "请到邮箱修改密码后再登录。");
        Log.i(TAG, "reset successed！");
        operation = UserOperation.LOGIN;
        updateLayout(operation);
    }

    @Override
    public void onResetFailure(String msg) {
        // TODO Auto-generated method stub
        ActivityTool.show(this, "重置密码失败。请确认网络连接后再重试。");
        Log.i(TAG, "register failed！");
    }

    private enum UserOperation {
        LOGIN, REGISTER, RESET_PASSWORD
    }

    UserOperation operation = UserOperation.LOGIN;
    UserProxy userProxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mContext = RegisterALoginActivity.this;
        initBar();
        findViews();
        setupViews();
        setListener();
    }

    private void initBar() {
        toolbar = (Toolbar) super.findViewById(R.id.tool_bar);
        toolbar.setTitle("用户管理");
        setSupportActionBar(toolbar);//把 toolbar 设置到Activity 中
    }

    private void findViews() {
        loginTitle = (TextView) findViewById(R.id.login_menu);
        registerTitle = (TextView) findViewById(R.id.register_menu);
        resetPassword = (TextView) findViewById(R.id.reset_password_menu);

        userNameInput = (DeletableEditText) findViewById(R.id.user_name_input);
        userPasswordInput = (DeletableEditText) findViewById(R.id.user_password_input);
        userEmailInput = (DeletableEditText) findViewById(R.id.user_email_input);
        userSchoolInput=(DeletableEditText)findViewById(R.id.user_school_input);

        registerButton = (Button) findViewById(R.id.register);

    }

    private void setupViews() {
        updateLayout(operation);
        userProxy = new UserProxy(mContext);
    }

    private void setListener() {
        loginTitle.setOnClickListener(this);
        registerTitle.setOnClickListener(this);
        resetPassword.setOnClickListener(this);
        registerButton.setOnClickListener(this);
    }

    private void updateLayout(UserOperation op) {
        if (op == UserOperation.LOGIN) {
            loginTitle.setTextColor(Color.parseColor("#D95555"));
            loginTitle.setBackgroundResource(R.mipmap.bg_login_tab);
            loginTitle.setPadding(16, 16, 16, 16);
            loginTitle.setGravity(Gravity.CENTER);


            registerTitle.setTextColor(Color.parseColor("#888888"));
            registerTitle.setBackgroundDrawable(null);
            registerTitle.setPadding(16, 16, 16, 16);
            registerTitle.setGravity(Gravity.CENTER);

            resetPassword.setTextColor(Color.parseColor("#888888"));
            resetPassword.setBackgroundDrawable(null);
            resetPassword.setPadding(16, 16, 16, 16);
            resetPassword.setGravity(Gravity.CENTER);

            userNameInput.setVisibility(View.VISIBLE);
            userPasswordInput.setVisibility(View.VISIBLE);
            userSchoolInput.setVisibility(View.GONE);
            userEmailInput.setVisibility(View.GONE);
            registerButton.setText("登录");
        } else if (op == UserOperation.REGISTER) {
            loginTitle.setTextColor(Color.parseColor("#888888"));
            loginTitle.setBackgroundDrawable(null);
            loginTitle.setPadding(16, 16, 16, 16);
            loginTitle.setGravity(Gravity.CENTER);

            registerTitle.setTextColor(Color.parseColor("#D95555"));
            registerTitle.setBackgroundResource(R.mipmap.bg_login_tab);
            registerTitle.setPadding(16, 16, 16, 16);
            registerTitle.setGravity(Gravity.CENTER);

            resetPassword.setTextColor(Color.parseColor("#888888"));
            resetPassword.setBackgroundDrawable(null);
            resetPassword.setPadding(16, 16, 16, 16);
            resetPassword.setGravity(Gravity.CENTER);

            userNameInput.setVisibility(View.VISIBLE);
            userPasswordInput.setVisibility(View.VISIBLE);
            userEmailInput.setVisibility(View.VISIBLE);
            userSchoolInput.setVisibility(View.VISIBLE);
            registerButton.setText("注册");
        } else {
            loginTitle.setTextColor(Color.parseColor("#888888"));
            loginTitle.setBackgroundDrawable(null);
            loginTitle.setPadding(16, 16, 16, 16);
            loginTitle.setGravity(Gravity.CENTER);

            registerTitle.setTextColor(Color.parseColor("#888888"));
            registerTitle.setBackgroundDrawable(null);
            registerTitle.setPadding(16, 16, 16, 16);
            registerTitle.setGravity(Gravity.CENTER);

            resetPassword.setTextColor(Color.parseColor("#D95555"));
            resetPassword.setBackgroundResource(R.mipmap.bg_login_tab);
            resetPassword.setPadding(16, 16, 16, 16);
            resetPassword.setGravity(Gravity.CENTER);


            userNameInput.setVisibility(View.GONE);
            userPasswordInput.setVisibility(View.GONE);
            userEmailInput.setVisibility(View.VISIBLE);
            registerButton.setText("找回密码");
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                RegisterALoginActivity.this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.register:
                if (operation == UserOperation.LOGIN) {
                    if (TextUtils.isEmpty(userNameInput.getText())) {
                        userNameInput.setShakeAnimation();
                        Toast.makeText(mContext, "请输入用户名", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(userPasswordInput.getText())) {
                        userPasswordInput.setShakeAnimation();
                        Toast.makeText(mContext, "请输入密码", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    userProxy.setOnLoginListener(this);
                    Log.i(TAG, "login begin....");
                    // progressbar.setVisibility(View.VISIBLE);
                    userProxy.login(userNameInput.getText().toString().trim(), userPasswordInput.getText().toString().trim());

                } else if (operation == UserOperation.REGISTER) {//注册
                    if (TextUtils.isEmpty(userNameInput.getText())) {
                        userNameInput.setShakeAnimation();
                        Toast.makeText(mContext, "请输入用户名", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(userPasswordInput.getText())) {
                        userPasswordInput.setShakeAnimation();
                        Toast.makeText(mContext, "请输入密码", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(userEmailInput.getText())) {
                        userEmailInput.setShakeAnimation();
                        Toast.makeText(mContext, "请输入邮箱地址", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!StringUtils.isValidEmail(userEmailInput.getText())) {
                        userEmailInput.setShakeAnimation();
                        Toast.makeText(mContext, "邮箱格式不正确", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(userSchoolInput.getText())){
                        userSchoolInput.setShakeAnimation();
                        Toast.makeText(mContext, "请输入学校全称", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    userProxy.setOnSignUpListener(this);
                    Log.i(TAG, "register begin....");
                    //  progressbar.setVisibility(View.VISIBLE);
                    userProxy.signUp(userNameInput.getText().toString().trim(),
                            userPasswordInput.getText().toString().trim(),
                            userEmailInput.getText().toString().trim(),userSchoolInput.getText().toString().trim());

                } else {
                    if (TextUtils.isEmpty(userEmailInput.getText())) {
                        userEmailInput.setShakeAnimation();
                        Toast.makeText(mContext, "请输入邮箱地址", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!StringUtils.isValidEmail(userEmailInput.getText())) {
                        userEmailInput.setShakeAnimation();
                        Toast.makeText(mContext, "邮箱格式不正确", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    userProxy.setOnResetPasswordListener(this);
                    Log.i(TAG, "reset password begin....");
                    //  progressbar.setVisibility(View.VISIBLE);
                    userProxy.resetPassword(userEmailInput.getText().toString().trim());
                }
                break;
            case R.id.login_menu:
                operation = UserOperation.LOGIN;
                updateLayout(operation);
                break;
            case R.id.register_menu:
                operation = UserOperation.REGISTER;
                updateLayout(operation);
                break;
            case R.id.reset_password_menu:
                operation = UserOperation.RESET_PASSWORD;
                updateLayout(operation);
                break;
            default:
                break;
        }

    }
}