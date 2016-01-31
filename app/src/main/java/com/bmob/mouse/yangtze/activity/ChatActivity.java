package com.bmob.mouse.yangtze.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bmob.mouse.yangtze.R;
import com.bmob.mouse.yangtze.adapter.EmoViewPagerAdapter;
import com.bmob.mouse.yangtze.adapter.EmoteAdapter;
import com.bmob.mouse.yangtze.adapter.MessageChatAdapter;
import com.bmob.mouse.yangtze.adapter.NewRecordPlayClickListener;
import com.bmob.mouse.yangtze.entity.FaceText;
import com.bmob.mouse.yangtze.util.ActivityTool;
import com.bmob.mouse.yangtze.util.CommonUtils;
import com.bmob.mouse.yangtze.util.FaceTextUtils;
import com.bmob.mouse.yangtze.view.DialogTips;
import com.bmob.mouse.yangtze.view.EmoticonsEditText;
import com.bmob.mouse.yangtze.view.XListView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.BmobRecordManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.im.inteface.OnRecordChangeListener;
import cn.bmob.im.inteface.UploadListener;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.PushListener;

/**
 * 会话
 * Created by Mouse on 2016/1/22.
 */
public class ChatActivity extends BaseActivity implements View.OnClickListener,XListView.IXListViewListener ,EventListener {
   private static  String TAG="ChatActivity";
    private Button btn_chat_emo, btn_chat_send, btn_chat_add,btn_chat_keyboard, btn_speak, btn_chat_voice;
    private LinearLayout layout_emo, layout_add,layout_more;
    private ViewPager pager_emo;
    XListView mListView;
    MessageChatAdapter mAdapter;
    EmoticonsEditText edit_user_comment;

    public Toolbar toolbar;
    public BmobChatUser targetUser;
    public  String targetId = "";
    private static int MsgPagerNum;

    // 语音有关
    RelativeLayout layout_record;
    TextView tv_voice_tips;
    ImageView iv_record;

    BmobRecordManager recordManager;
    private Drawable[] drawable_Anims;// 话筒动画

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_chat);
        // 组装聊天对象
        targetUser = (BmobChatUser) getIntent().getSerializableExtra("data");//来自MouseAdapter传来的
        targetId=targetUser.getObjectId();
        MsgPagerNum=0;
        initBar();
        //注册广播接收器
        //initNewMessageBroadCast();
        initView();
    }

    private void initView(){
        mListView = (XListView) findViewById(R.id.mListView);
        initBottomView();
        initXListView();
        initVoiceView();
    }

    private void initVoiceView(){
        layout_record = (RelativeLayout) findViewById(R.id.layout_record);
        tv_voice_tips = (TextView) findViewById(R.id.tv_voice_tips);
        iv_record = (ImageView) findViewById(R.id.iv_record);
        btn_speak.setOnTouchListener(new VoiceTouchListen());
        initVoiceAnimRes();
        initRecordManager();
    }

    private void initRecordManager(){
        // 语音相关管理器
        recordManager = BmobRecordManager.getInstance(this);
        // 设置音量大小监听--在这里开发者可以自己实现：当剩余10秒情况下的给用户的提示，类似微信的语音那样
        Log.i("recordManager",recordManager.getRecordFileName()+"");
        recordManager.setOnRecordChangeListener(new OnRecordChangeListener() {

            @Override
            public void onVolumnChanged(int value) {
                iv_record.setImageDrawable(drawable_Anims[value]);
            }

            @Override
            public void onTimeChanged(int recordTime, String localPath) {
                BmobLog.i("voice", "已录音长度:" + recordTime);
                if (recordTime >= BmobRecordManager.MAX_RECORD_TIME) {// 1分钟结束，发送消息
                    // 需要重置按钮
                    btn_speak.setPressed(false);
                    btn_speak.setClickable(false);
                    // 取消录音框
                    layout_record.setVisibility(View.INVISIBLE);
                    // 发送语音消息
                    sendVoiceMessage(localPath, recordTime);
                    //是为了防止过了录音时间后，会多发一条语音出去的情况。
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            btn_speak.setClickable(true);
                        }
                    }, 1000);
                } else {

                }
            }
        });
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == NEW_MESSAGE) {
                BmobMsg message = (BmobMsg) msg.obj;
                String uid = message.getBelongId();
                BmobMsg m = BmobChatManager.getInstance(ChatActivity.this).getMessage(message.getConversationId(), message.getMsgTime());
                if (!uid.equals(targetId))// 如果不是当前正在聊天对象的消息，不处理
                    return;
                mAdapter.add(m);
                // 定位
                mListView.setSelection(mAdapter.getCount() - 1);
                //取消当前聊天对象的未读标示
                BmobDB.create(ChatActivity.this).resetUnread(targetId);
            }
        }
    };
    public static final int NEW_MESSAGE = 0x001;// 收到消息

    private void initVoiceAnimRes() {
        drawable_Anims = new Drawable[] {
                getResources().getDrawable(R.mipmap.chat_icon_voice2),
                getResources().getDrawable(R.mipmap.chat_icon_voice3),
                getResources().getDrawable(R.mipmap.chat_icon_voice4),
                getResources().getDrawable(R.mipmap.chat_icon_voice5),
                getResources().getDrawable(R.mipmap.chat_icon_voice6) };
    }

    /**
     * 长按说话
     */
    class VoiceTouchListen implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.i("eventyuyuyu","1evntGetAction"+event.getAction());
                    if (!CommonUtils.checkSdCard()) {
                        ShowToast("发送语音需要sdcard支持！");
                        return false;
                    }
                    try {
                        v.setPressed(true);
                        layout_record.setVisibility(View.VISIBLE);
                        tv_voice_tips.setText(getString(R.string.voice_cancel_tips));
                        Log.i("eventyuyuyu", "2evntGetAction" +"----");
                        // 开始录音
                        recordManager.startRecording(targetId);
                    } catch (Exception e) {
                    }
                    return true;
                case MotionEvent.ACTION_MOVE: {
                    Log.i("eventyuyuyu","2evntGetAction"+event.getAction());
                    if (event.getY() < 0) {
                        tv_voice_tips
                                .setText(getString(R.string.voice_cancel_tips));
                        tv_voice_tips.setTextColor(Color.RED);
                    } else {
                        tv_voice_tips.setText(getString(R.string.voice_up_tips));
                        tv_voice_tips.setTextColor(Color.WHITE);
                    }
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
                    layout_record.setVisibility(View.INVISIBLE);
                    try {
                        if (event.getY() < 0) {// 放弃录音
                            recordManager.cancelRecording();
                            BmobLog.i("voice", "放弃发送语音");
                        } else {
                            int recordTime = recordManager.stopRecording();
                            if (recordTime > 1) {
                                // 发送语音文件
                                BmobLog.i("voice", "发送语音");
                                sendVoiceMessage(
                                        recordManager.getRecordFilePath(targetId),
                                        recordTime);
                            } else {// 录音时间过短，则提示录音过短的提示
                                layout_record.setVisibility(View.GONE);
                                showShortToast().show();
                            }
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    return true;
                default:
                    return false;
            }
        }
    }

    Toast toast;
    /**
     * 显示录音时间过短的Toast
     */
    private Toast showShortToast() {
        if (toast == null) {
            toast = new Toast(this);
        }
        View view = LayoutInflater.from(this).inflate(
                R.layout.include_chat_voice_short, null);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        return toast;
    }


    /**
     * 发送语音消息
     *  sendImageMessage
     */
    private void sendVoiceMessage(String local, int length) {
        Log.i("-----","0000000000000000");
        manager.sendVoiceMessage(targetUser, local, length,
                new UploadListener() {

                    @Override
                    public void onStart(BmobMsg msg) {
                        // TODO Auto-generated method stub
                        refreshMessage(msg);
                    }

                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(int error, String arg1) {
                        // TODO Auto-generated method stub
                        ActivityTool.show((Activity) mContext, "上传语音失败 -->arg1：" + arg1);
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

    /**
     * 刷新界面
     * @Title: refreshMessage
     * @Description: TODO
     * @param @param message
     * @return void
     * @throws
     */
    private void refreshMessage(BmobMsg msg) {
        // 更新界面
        mAdapter.add(msg);
        mListView.setSelection(mAdapter.getCount() - 1);
        edit_user_comment.setText("");
    }


    private void initXListView(){
        // 首先不允许加载更多
        mListView.setPullLoadEnable(false);
        // 允许下拉
        mListView.setPullRefreshEnable(true);
        // 设置监听器
        mListView.setXListViewListener(this);
        mListView.pullRefreshing();
        mListView.setDividerHeight(0);
        // 加载数据
        initOrRefresh();
        mListView.setSelection(mAdapter.getCount() - 1);
        mListView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                hideSoftInputView();
                layout_more.setVisibility(View.GONE);
                layout_add.setVisibility(View.GONE);
                btn_chat_voice.setVisibility(View.VISIBLE);
                btn_chat_keyboard.setVisibility(View.GONE);
                btn_chat_send.setVisibility(View.GONE);
                return false;
            }
        });

        // 重发按钮的点击事件
        mAdapter.setOnInViewClickListener(R.id.iv_fail_resend,
                new MessageChatAdapter.onInternalClickListener() {

                    @Override
                    public void OnClickListener(View parentV, View v,
                                                Integer position, Object values) {
                        // 重发消息
                        showResendDialog(parentV, v, values);
                    }
                });
    }

    /**
     * 显示重发按钮 showResendDialog
     * @Title: showResendDialog
     * @Description: TODO
     * @param @param recent
     * @return void
     * @throws
     */
    public void showResendDialog(final View parentV, View v, final Object values) {
        DialogTips dialog = new DialogTips(this, "确定重发该消息", "确定", "取消", "提示",
                true);
        // 设置成功事件
        dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int userId) {
                if (((BmobMsg) values).getMsgType() == BmobConfig.TYPE_IMAGE
                        || ((BmobMsg) values).getMsgType() == BmobConfig.TYPE_VOICE) {// 图片和语音类型的采用
                    resendFileMsg(parentV, values);
                } else {
                    resendTextMsg(parentV, values);
                }
                dialogInterface.dismiss();
            }
        });
        // 显示确认对话框
        dialog.show();
        dialog = null;
    }

    /**
     * 重发文本消息
     */
    private void resendTextMsg(final View parentV, final Object values) {
        BmobChatManager.getInstance(ChatActivity.this).resendTextMessage(
                targetUser, (BmobMsg) values, new PushListener() {

                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        ActivityTool.show((Activity) mContext, "发送成功~");
                        ((BmobMsg) values)
                                .setStatus(BmobConfig.STATUS_SEND_SUCCESS);
                        parentV.findViewById(R.id.progress_load).setVisibility(
                                View.INVISIBLE);
                        parentV.findViewById(R.id.iv_fail_resend)
                                .setVisibility(View.INVISIBLE);
                        parentV.findViewById(R.id.tv_send_status)
                                .setVisibility(View.VISIBLE);
                        ((TextView) parentV.findViewById(R.id.tv_send_status))
                                .setText("已发送");
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        // TODO Auto-generated method stub
                        ActivityTool.show((Activity) mContext, "发送失败~" + arg1);
                        ((BmobMsg) values)
                                .setStatus(BmobConfig.STATUS_SEND_FAIL);
                        parentV.findViewById(R.id.progress_load).setVisibility(
                                View.INVISIBLE);
                        parentV.findViewById(R.id.iv_fail_resend)
                                .setVisibility(View.VISIBLE);
                        parentV.findViewById(R.id.tv_send_status)
                                .setVisibility(View.INVISIBLE);
                    }
                });
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 重发图片消息
     * @Title: resendImageMsg
     * @Description: TODO
     * @param @param parentV
     * @param @param values
     * @return void
     * @throws
     */
    private void resendFileMsg(final View parentV, final Object values) {
        BmobChatManager.getInstance(ChatActivity.this).resendFileMessage(
                targetUser, (BmobMsg) values, new UploadListener() {

                    @Override
                    public void onStart(BmobMsg msg) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        ((BmobMsg) values)
                                .setStatus(BmobConfig.STATUS_SEND_SUCCESS);
                        parentV.findViewById(R.id.progress_load).setVisibility(
                                View.INVISIBLE);
                        parentV.findViewById(R.id.iv_fail_resend)
                                .setVisibility(View.INVISIBLE);
                        if (((BmobMsg) values).getMsgType() == BmobConfig.TYPE_VOICE) {
                            parentV.findViewById(R.id.tv_send_status)
                                    .setVisibility(View.GONE);
                            parentV.findViewById(R.id.tv_voice_length)
                                    .setVisibility(View.VISIBLE);
                        } else {
                            parentV.findViewById(R.id.tv_send_status)
                                    .setVisibility(View.VISIBLE);
                            ((TextView) parentV
                                    .findViewById(R.id.tv_send_status))
                                    .setText("已发送");
                        }
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        ((BmobMsg) values)
                                .setStatus(BmobConfig.STATUS_SEND_FAIL);
                        parentV.findViewById(R.id.progress_load).setVisibility(
                                View.INVISIBLE);
                        parentV.findViewById(R.id.iv_fail_resend)
                                .setVisibility(View.VISIBLE);
                        parentV.findViewById(R.id.tv_send_status)
                                .setVisibility(View.INVISIBLE);
                    }
                });
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 界面刷新
     * @Title: initOrRefresh
     * @Description: TODO
     * @param
     * @return void
     * @throws
     */
    private void initOrRefresh() {
        if (mAdapter != null) {
            if (MyMessageReceiver.mNewNum != 0) {// 用于更新当在聊天界面锁屏期间来了消息，这时再回到聊天页面的时候需要显示新来的消息
                int news=  MyMessageReceiver.mNewNum;//有可能锁屏期间，来了N条消息,因此需要倒叙显示在界面上
                int size = initMsgData().size();
                for(int i=(news-1);i>=0;i--){
                    mAdapter.add(initMsgData().get(size-(i+1)));// 添加最后一条消息到界面显示
                }
                mListView.setSelection(mAdapter.getCount() - 1);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        } else {
            mAdapter = new MessageChatAdapter(this, initMsgData());
            mListView.setAdapter(mAdapter);
        }
    }

    /**
     * 加载消息历史，从数据库中读出
     */
    private List<BmobMsg> initMsgData() {
        List<BmobMsg> list = BmobDB.create(this).queryMessages(targetId,MsgPagerNum);
        return list;
    }

    private void initBottomView(){
        // 最左边
        btn_chat_emo = (Button) findViewById(R.id.btn_chat_emo);
        btn_chat_emo.setOnClickListener(this);
        // 最右边
        btn_chat_keyboard = (Button) findViewById(R.id.btn_chat_keyboard);
        btn_chat_voice = (Button) findViewById(R.id.btn_chat_voice);
        btn_chat_voice.setOnClickListener(this);
        btn_chat_keyboard.setOnClickListener(this);
        btn_chat_send = (Button) findViewById(R.id.btn_chat_send);
        btn_chat_send.setOnClickListener(this);
        // 最下面
        layout_more = (LinearLayout) findViewById(R.id.layout_more);
        layout_emo = (LinearLayout) findViewById(R.id.layout_emo);
        layout_add = (LinearLayout) findViewById(R.id.layout_add);
        initAddView();
        initEmoView();
        // 最中间
        // 语音框
        btn_speak = (Button) findViewById(R.id.btn_speak);
        // 输入框
        edit_user_comment = (EmoticonsEditText) findViewById(R.id.edit_user_comment);
        edit_user_comment.setOnClickListener(this);
        edit_user_comment.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (!TextUtils.isEmpty(s)) {
                    btn_chat_send.setVisibility(View.VISIBLE);
                    btn_chat_keyboard.setVisibility(View.GONE);
                    btn_chat_voice.setVisibility(View.GONE);
                } else {
                    if (btn_chat_voice.getVisibility() != View.VISIBLE) {
                        btn_chat_voice.setVisibility(View.VISIBLE);
                        btn_chat_send.setVisibility(View.GONE);
                        btn_chat_keyboard.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });
    }

    List<FaceText> emos;
    /**
     * 初始化表情布局
     */
    private void initEmoView(){
        pager_emo = (ViewPager) findViewById(R.id.pager_emo);
        emos = FaceTextUtils.faceTexts;

        List<View> views = new ArrayList<>();
        for (int i = 0; i < 2; ++i) {
            views.add(getGridView(i));
        }
        pager_emo.setAdapter(new EmoViewPagerAdapter(views));

    }

    private View getGridView(final int i) {
        View view = View.inflate(this, R.layout.include_emo_gridview, null);
        GridView gridview = (GridView) view.findViewById(R.id.gridview);
        List<FaceText> list = new ArrayList<FaceText>();
        if (i == 0) {
            list.addAll(emos.subList(0, 21));
        } else if (i == 1) {
            list.addAll(emos.subList(21, emos.size()));
        }
        final EmoteAdapter gridAdapter = new EmoteAdapter(ChatActivity.this,
                list);
        gridview.setAdapter(gridAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                FaceText name = (FaceText) gridAdapter.getItem(position);
                String key = name.text.toString();
                Log.i(TAG,"key-->"+key);
                Log.i(TAG,"edit_user_comment-->"+edit_user_comment.getText()+"");
                try {
                    if (edit_user_comment != null && !TextUtils.isEmpty(key)) {
                        int start = edit_user_comment.getSelectionStart();
                        Log.i(TAG,"start--->"+start+"");
                        CharSequence content = edit_user_comment.getText()
                                .insert(start, key);
                        Log.i(TAG,"content-->"+content+"");
                        edit_user_comment.setText(content);
                        // 定位光标位置
                        CharSequence info = edit_user_comment.getText();
                        if (info instanceof Spannable) {
                            Spannable spanText = (Spannable) info;
                            Selection.setSelection(spanText,
                                    start + key.length());
                        }
                    }
                } catch (Exception e) {

                }

            }
        });
        return view;
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // 新消息到达，重新刷新界面
        initOrRefresh();
        MyMessageReceiver.ehList.add(this);// 监听推送的消息
        // 有可能锁屏期间，在聊天界面出现通知栏，这时候需要清除通知和清空未读消息数
        BmobNotifyManager.getInstance(this).cancelNotify();
        BmobDB.create(this).resetUnread(targetId);
        //清空消息未读数-这个要在刷新之后
        MyMessageReceiver.mNewNum=0;
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MyMessageReceiver.ehList.remove(this);// 监听推送的消息
        // 停止录音
        if (recordManager.isRecording()) {
            recordManager.cancelRecording();
            layout_record.setVisibility(View.GONE);
        }
        // 停止播放录音
        if (NewRecordPlayClickListener.isPlaying
                && NewRecordPlayClickListener.currentPlayListener != null) {
            NewRecordPlayClickListener.currentPlayListener.stopPlayRecord();
        }

    }

    private void initAddView(){

    }


    NewBroadcastReceiver  receiver;

    private void initNewMessageBroadCast(){
        // 注册接收消息广播
        receiver = new NewBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_NEW_MESSAGE);
        //设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
        intentFilter.setPriority(5);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_user_comment:// 点击文本输入框
                mListView.setSelection(mListView.getCount() - 1);
                if (layout_more.getVisibility() == View.VISIBLE) {
                    layout_add.setVisibility(View.GONE);
                    layout_emo.setVisibility(View.GONE);
                    layout_more.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_chat_emo:// 点击笑脸图标
                if (layout_more.getVisibility() == View.GONE) {
                    showEditState(true);
                } else {
                    if (layout_add.getVisibility() == View.VISIBLE) {
                        layout_add.setVisibility(View.GONE);
                        layout_emo.setVisibility(View.VISIBLE);
                    } else {
                        layout_more.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.btn_chat_voice:// 语音按钮
                edit_user_comment.setVisibility(View.GONE);
                layout_more.setVisibility(View.GONE);
                btn_chat_voice.setVisibility(View.GONE);
                btn_chat_keyboard.setVisibility(View.VISIBLE);
                btn_speak.setVisibility(View.VISIBLE);
                hideSoftInputView();
                break;
            case R.id.btn_chat_keyboard:// 键盘按钮，点击就弹出键盘并隐藏掉声音按钮
                showEditState(false);
                break;
            case R.id.btn_chat_send:// 发送文本
                final String msg = edit_user_comment.getText().toString();
                if (msg.equals("")) {
                    ShowToast("请输入发送消息!");
                    return;
                }
                boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
                if (!isNetConnected) {
                    ActivityTool.show(ChatActivity.this,"当前网络不可用,请检查您的网络!");
                     return;
                }
                // 组装BmobMessage对象
                BmobMsg message = BmobMsg.createTextSendMsg(this, targetId, msg);
                message.setExtra("Bmob");
                // 默认发送完成，将数据保存到本地消息表和最近会话表中
                manager.sendTextMessage(targetUser, message);
                // 刷新界面
                refreshMessage(message);
                break;
            default:
                break;
        }

    }

    /**
     *  根据是否点击笑脸来显示文本输入框的状态
     * @param isEmo
     */
    private void showEditState(boolean isEmo) {
        edit_user_comment.setVisibility(View.VISIBLE);
        btn_chat_keyboard.setVisibility(View.GONE);
        btn_chat_voice.setVisibility(View.VISIBLE);
        btn_speak.setVisibility(View.GONE);
        edit_user_comment.requestFocus();
        if (isEmo) {
            layout_more.setVisibility(View.VISIBLE);
            layout_more.setVisibility(View.VISIBLE);
            layout_emo.setVisibility(View.VISIBLE);
            layout_add.setVisibility(View.GONE);
            hideSoftInputView();
        } else {
            layout_more.setVisibility(View.GONE);
            showSoftInputView();
        }
    }

    // 显示软键盘
    public void showSoftInputView() {
        if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                        .showSoftInput(edit_user_comment, 0);
        }
    }


    /**
     * 新消息广播接收者
     *
     */
    private class NewBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String from = intent.getStringExtra("fromId");
            String msgId = intent.getStringExtra("msgId");
            String msgTime = intent.getStringExtra("msgTime");
            // 收到这个广播的时候，message已经在消息表中，可直接获取
            if(!TextUtils.isEmpty(from)&& !TextUtils.isEmpty(msgId)&& !TextUtils.isEmpty(msgTime)){
                BmobMsg msg = BmobChatManager.getInstance(ChatActivity.this).getMessage(msgId, msgTime);
                if (!from.equals(targetId))// 如果不是当前正在聊天对象的消息，不处理
                    return;
                //添加到当前页面
                mAdapter.add(msg);
                //取消当前聊天对象的未读标示
                BmobDB.create(ChatActivity.this).resetUnread(targetId);
            }
            // 记得把广播给终结掉
            abortBroadcast();
        }
    }

    private void initBar() {
        toolbar = (Toolbar)findViewById(R.id.tool_bar);
        toolbar.setTitle("和" + targetUser.getUsername()+"聊天");
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

    /** 隐藏软键盘
     * hideSoftInputView
     * @Title: hideSoftInputView
     * @Description: TODO
     * @param
     * @return void
     * @throws
     */
    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onMessage(BmobMsg message) {
        // TODO Auto-generated method stub
        Message handlerMsg = handler.obtainMessage(NEW_MESSAGE);
        handlerMsg.obj = message;
        handler.sendMessage(handlerMsg);
    }

    @Override
    public void onNetChange(boolean isNetConnected) {
        // TODO Auto-generated method stub
        if (!isNetConnected) {
            ActivityTool.show((Activity)mContext, "当前网络不可用,请检查您的网络!");
        }
    }

    @Override
    public void onAddUser(BmobInvitation invite) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onOffline() {
        // TODO Auto-generated method stub
        //showOfflineDialog(this);
    }

    @Override
    public void onReaded(String conversionId, String msgTime) {
        // 此处应该过滤掉不是和当前用户的聊天的回执消息界面的刷新
        if (conversionId.split("&")[1].equals(targetId)) {
            // 修改界面上指定消息的阅读状态
            for (BmobMsg msg : mAdapter.getList()) {
                if (msg.getConversationId().equals(conversionId)
                        && msg.getMsgTime().equals(msgTime)) {
                    msg.setStatus(BmobConfig.STATUS_SEND_RECEIVERED);
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public void onRefresh() {
        // TODO Auto-generated method stub
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                MsgPagerNum++;
                int total = BmobDB.create(ChatActivity.this).queryChatTotalCount(targetId);
                BmobLog.i("记录总数：" + total);
                int currents = mAdapter.getCount();
                if (total <= currents) {
                    ShowToast("聊天记录加载完了哦!");
                } else {
                    List<BmobMsg> msgList = initMsgData();
                    mAdapter.setList(msgList);
                    mListView.setSelection(mAdapter.getCount() - currents - 1);
                }
                mListView.stopRefresh();
            }
        }, 1000);
    }

    @Override
    public void onLoadMore() {
        // TODO Auto-generated method stub
    }

}
