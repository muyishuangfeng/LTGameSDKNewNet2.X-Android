package com.sdk.ltgame.ltnet.impl;

import com.gentop.ltgame.ltgamesdkcore.exception.LTGameError;

public interface OnAutoCheckLoginListener {

    void onCheckedSuccess(String result);

    void onCheckedFailed(String failed);

    void onCheckedException(LTGameError ex);
}
