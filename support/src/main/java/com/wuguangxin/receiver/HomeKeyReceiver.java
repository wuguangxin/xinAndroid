package com.wuguangxin.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Home键状态观察者
 * <p>Created by wuguangxin on 15/3/31 </p>
 */
public class HomeKeyReceiver {
    private Receiver mReceiver;
    private Context mContext;
    private boolean registered;

    public HomeKeyReceiver(Context context, Callback callback) {
        this.mContext = context;
        mReceiver = new Receiver(callback);
    }

    /**
     * 开始Home键监听
     */
    public void register() {
        if (!registered) {
            registered = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            mContext.getApplicationContext().registerReceiver(mReceiver, filter);
        }
    }

    /**
     * 停止Home键监听
     */
    public void unregister() {
        if (registered && mReceiver != null) {
            registered = false;
            mContext.getApplicationContext().unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    /**
     * 广播接收者
     */
    public static class Receiver extends BroadcastReceiver {
        final String REASON_KEY = "reason";
        final String REASON_GLOBAL_ACTIONS = "globalactions";
        final String REASON_RECENT_APPS = "recentapps";
        final String REASON_HOME_KEY = "homekey";

        Callback callback;

        public Receiver(Callback callback) {
            this.callback = callback;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (callback != null && intent != null) {
                if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
                    String reason = intent.getStringExtra(REASON_KEY);
                    if (reason != null) {
                        if (reason.equals(REASON_HOME_KEY)) {
                            callback.onHomePressed(); // 短按home键
                        } else if (reason.equals(REASON_RECENT_APPS)) {
                            callback.onHomeLongPressed(); // 长按home键
                        }
                    }
                }
            }
        }
    }

    /**
     * 回调接口
     */
    public interface Callback {
        /**
         * Home键被按下
         */
        void onHomePressed();

        /**
         * Home键被长按
         */
        void onHomeLongPressed();
    }
}