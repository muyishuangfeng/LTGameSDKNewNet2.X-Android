package com.sdk.ltgame.ltnet.impl;


public interface OnWeChatAccessTokenListener<T> {

    void onWeChatSuccess(T t);

    void onWeChatFailed(String failed);
}
