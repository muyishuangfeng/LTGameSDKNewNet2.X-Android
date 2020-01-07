package com.sdk.ltgame.ltnet.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sdk.ltgame.ltnet.base.Constants;


public abstract class NetReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case Constants.MSG_SEND_EXCEPTION: {
                    if (intent.getStringExtra(Constants.MSG_EXCEPTION_NAME) != null) {
                        getResult(intent.getStringExtra(Constants.MSG_EXCEPTION_NAME));
                    }
                    break;
                }
            }
        }
    }

    /**
     * 结果回调
     *
     * @param result 错误结果
     */
    public abstract void getResult(String result);
}
