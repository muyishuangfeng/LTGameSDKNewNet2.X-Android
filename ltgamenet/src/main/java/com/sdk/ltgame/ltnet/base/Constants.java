package com.sdk.ltgame.ltnet.base;

public class Constants {
    /***用户协议标志*/
    public static final String USER_AGREEMENT_FLAT = "USER_AGREEMENT_FLAT";
    /***乐推UID标志*/
    public static final String USER_LT_UID = "USER_FACEBOOK_LT_UID";
    /***乐推UIDtoken标志*/
    public static final String USER_LT_UID_TOKEN = "USER_FACEBOOK_LT_UID_TOKEN";
    /***apiToken标志*/
    public static final String USER_API_TOKEN = "USER_API_TOKEN";
    /***用户的UUID*/
    public static final String USER_UUID = "USER_UUID";
    /***QQ的AccessToken*/
    public static final String QQ_ACCESS_TOKEN = "QQ_ACCESS_TOKEN";
    /***QQ的openID*/
    public static final String QQ_OPEN_ID = "QQ_OPEN_ID";
    /***QQ token超时时间*/
    public static final String QQ_TOKEN_TIMEOUT = "QQ_TOKEN_TIMEOUT";
    /***微信的AccessToken*/
    public static final String WE_CHAT_ACCESS_TOKEN = "WE_CHAT_ACCESS_TOKEN";
    /***异常*/
    public static final String MSG_EXCEPTION_NAME = "MSG_EXCEPTION_NAME";
    /***异常*/
    public static final String MSG_SEND_EXCEPTION = "MSG_SEND_EXCEPTION";
    /***游客标记*/
    public static final String USER_GUEST_FLAG = "USER_GUEST_FLAG";
    /***绑定标记*/
    public static final String USER_BIND_FLAG = "USER_BIND_FLAG";


    private static final int BASE_CODE = 0X001;
    /***Facebook注册*/
    public static final int USER_REGISTER_FACEBOOK_CODE = BASE_CODE + 23;
    /***Google注册*/
    public static final int USER_REGISTER_GOOGLE_CODE = BASE_CODE + 24;
    /***游客注册*/
    public static final int USER_REGISTER_GUEST_CODE = BASE_CODE + 25;
    /***已经绑定过*/
    public static final int USER_ALREADY_BIND_CODE= BASE_CODE + 26;



}
