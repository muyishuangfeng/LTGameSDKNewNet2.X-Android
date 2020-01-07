package com.sdk.ltgame.ltnet.manager;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.gentop.ltgame.ltgamesdkcore.util.FileUtil;
import com.sdk.ltgame.ltnet.base.Constants;
import com.sdk.ltgame.ltnet.impl.OnAutoCheckLoginListener;
import com.sdk.ltgame.ltnet.impl.OnUploadDeviceListener;
import com.sdk.ltgame.ltnet.impl.OnWeChatAccessTokenListener;
import com.sdk.ltgame.ltnet.model.AuthWXModel;
import com.sdk.ltgame.ltnet.model.WeChatAccessToken;
import com.sdk.ltgame.ltnet.net.Api;
import com.sdk.ltgame.ltnet.net.exception.ExceptionHelper;
import com.sdk.ltgame.ltnet.util.AppUtil;
import com.sdk.ltgame.ltnet.util.MD5Util;
import com.sdk.ltgame.ltnet.util.PreferencesUtils;
import com.gentop.ltgame.ltgamesdkcore.base.BaseEntry;
import com.gentop.ltgame.ltgamesdkcore.common.LTGameOptions;
import com.gentop.ltgame.ltgamesdkcore.common.LTGameSdk;
import com.gentop.ltgame.ltgamesdkcore.exception.LTGameError;
import com.gentop.ltgame.ltgamesdkcore.impl.OnLoginStateListener;
import com.gentop.ltgame.ltgamesdkcore.impl.OnRechargeListener;
import com.gentop.ltgame.ltgamesdkcore.model.LoginResult;
import com.gentop.ltgame.ltgamesdkcore.model.RechargeResult;
import com.gentop.ltgame.ltgamesdkcore.model.ResultModel;
import com.google.gson.Gson;

import java.util.Map;
import java.util.WeakHashMap;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

public class LoginRealizeManager {


    /**
     * Google登录
     *
     * @param idToken   google返回的Token
     * @param mListener 接口回调
     */
    public static void googleLogin(final Context context, String idToken,
                                   final OnLoginStateListener mListener) {
        LTGameOptions options = LTGameSdk.options();
        if (!TextUtils.isEmpty(options.getLtAppId()) &&
                !TextUtils.isEmpty(options.getLtAppKey()) &&
                !TextUtils.isEmpty(options.getAdID())) {
            long LTTime = System.currentTimeMillis() / 1000L;
            String LTToken = MD5Util.md5Decode("POST" + options.getLtAppId() + LTTime + options.getLtAppKey());
            Map<String, Object> map = new WeakHashMap<>();
            map.put("access_token", idToken);
            map.put("platform", 2);
            map.put("adid", options.getAdID());
            map.put("gps_adid", options.getAdID());
            map.put("platform_id",AppUtil.getPackageName(context));
            String baseUrl = "";
            if (options.getISServerTest()) {
                baseUrl = Api.TEST_SERVER_URL;
            } else {
                baseUrl = Api.FORMAL_SERVER_URL;
            }

            Api.getInstance((Activity) context, baseUrl)
                    .googleLogin(options.getLtAppId(), LTToken, (int) LTTime, map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry<ResultModel>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry<ResultModel> result) {
                            if (result != null) {
                                if (result.getCode() == 200) {
                                    if (mListener != null) {
                                        int code = 0;
                                        BaseEntry<ResultModel> baseEntry = new BaseEntry<>();
                                        baseEntry.setResult(result.getResult());
                                        if (result.getData().getLt_type().equals("register")) {
                                            code = Constants.USER_REGISTER_GOOGLE_CODE;
                                        }
                                        baseEntry.setCode(code);
                                        baseEntry.setData(result.getData());
                                        baseEntry.setMsg(result.getMsg());
                                        mListener.onState((Activity) context, LoginResult.successOf(baseEntry));
                                    }
                                    if (!TextUtils.isEmpty(result.getData().getApi_token())) {
                                        PreferencesUtils.putString(context, Constants.USER_API_TOKEN,
                                                result.getData().getApi_token());
                                    }
                                    if (!TextUtils.isEmpty(result.getData().getLt_uid())) {
                                        PreferencesUtils.putString(context, Constants.USER_LT_UID,
                                                result.getData().getLt_uid());
                                    }
                                    if (!TextUtils.isEmpty(result.getData().getLt_uid_token())) {
                                        PreferencesUtils.putString(context, Constants.USER_LT_UID_TOKEN,
                                                result.getData().getLt_uid_token());
                                    }
                                } else {
                                    if (mListener != null) {
                                        mListener.onState((Activity) context,
                                                LoginResult.failOf(LTGameError.make(LTGameError.CODE_COMMON_ERROR, result.getMsg())));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onState((Activity) context,
                                    LoginResult.failOf(ExceptionHelper.handleException(e)));
                        }

                        @Override
                        public void onComplete() {
                            if (mListener != null) {
                                mListener.onState((Activity) context,
                                        LoginResult.completeOf(LTGameError.CODE_COMPLETE));
                            }
                        }
                    });
        }
    }


    /**
     * facebook登录
     *
     * @param accessToken facebook返回的Token
     * @param mListener   接口回调
     */
    public static void facebookLogin(final Context context, String accessToken,
                                     final OnLoginStateListener mListener) {
        LTGameOptions options = LTGameSdk.options();
        if (!TextUtils.isEmpty(options.getLtAppId()) &&
                !TextUtils.isEmpty(options.getLtAppKey()) &&
                !TextUtils.isEmpty(options.getAdID())) {
            long LTTime = System.currentTimeMillis() / 1000L;
            String LTToken = MD5Util.md5Decode("POST" + options.getLtAppId() + LTTime + options.getLtAppKey());
            Map<String, Object> map = new WeakHashMap<>();
            map.put("access_token", accessToken);
            map.put("platform", 2);
            map.put("adid", options.getAdID());
            map.put("gps_adid", options.getAdID());
            map.put("platform_id", AppUtil.getPackageName(context));

            String baseUrl = "";
            if (options.getISServerTest()) {
                baseUrl = Api.TEST_SERVER_URL;
            } else {
                baseUrl = Api.FORMAL_SERVER_URL;
            }

            Api.getInstance((Activity) context, baseUrl)
                    .faceBookLogin(options.getLtAppId(), LTToken, (int) LTTime, map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry<ResultModel>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry<ResultModel> result) {
                            if (result != null) {
                                if (result.getCode() == 200) {
                                    if (mListener != null) {
                                        int code = 0;
                                        BaseEntry<ResultModel> baseEntry = new BaseEntry<>();
                                        baseEntry.setResult(result.getResult());
                                        if (result.getData().getLt_type().equals("register")) {
                                            code = Constants.USER_REGISTER_FACEBOOK_CODE;
                                        }
                                        baseEntry.setCode(code);
                                        baseEntry.setData(result.getData());
                                        baseEntry.setMsg(result.getMsg());
                                        mListener.onState((Activity) context, LoginResult.successOf(baseEntry));
                                    }
                                    if (!TextUtils.isEmpty(result.getData().getApi_token())) {
                                        PreferencesUtils.putString(context, Constants.USER_API_TOKEN,
                                                result.getData().getApi_token());
                                    }
                                    if (!TextUtils.isEmpty(result.getData().getLt_uid())) {
                                        PreferencesUtils.putString(context, Constants.USER_LT_UID,
                                                result.getData().getLt_uid());
                                    }
                                    if (!TextUtils.isEmpty(result.getData().getLt_uid_token())) {
                                        PreferencesUtils.putString(context, Constants.USER_LT_UID_TOKEN,
                                                result.getData().getLt_uid_token());
                                    }
                                } else {
                                    if (mListener != null) {
                                        mListener.onState((Activity) context,
                                                LoginResult.failOf(LTGameError.make(LTGameError.CODE_COMMON_ERROR,
                                                        result.getMsg())));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onState((Activity) context,
                                    LoginResult.failOf(ExceptionHelper.handleException(e)));
                        }

                        @Override
                        public void onComplete() {
                            if (mListener != null) {
                                mListener.onState((Activity) context,
                                        LoginResult.completeOf(LTGameError.CODE_COMPLETE));
                            }
                        }
                    });
        }
    }


    /**
     * 创建订单
     */
    public static void createOrder(final Activity context, String mProductID, Map<String, Object> map,
                                   final OnRechargeListener mListener) {
        LTGameOptions options = LTGameSdk.options();
        if (map != null &&
                !TextUtils.isEmpty(options.getLtAppId()) &&
                !TextUtils.isEmpty(options.getAdID()) &&
                !TextUtils.isEmpty(options.getLtAppKey())) {
            Map<String, Object> params = new WeakHashMap<>();
            params.put("package_id", options.getPackageID());
            map.put("platform", 2);
            map.put("adid", options.getAdID());
            map.put("gps_adid", options.getAdID());
            map.put("platform_id", AppUtil.getPackageName(context));
            params.put("gid", mProductID);
            params.put("custom", map);
            long LTTime = System.currentTimeMillis() / 1000L;
            String LTToken = MD5Util.md5Decode("POST" + options.getLtAppId() + LTTime
                    + options.getLtAppKey());
            String json = new Gson().toJson(params);//要传递的json
            final RequestBody requestBody = RequestBody
                    .create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json);
            String baseUrl = "";
            if (options.getISServerTest()) {
                baseUrl = Api.TEST_SERVER_URL;
            } else {
                baseUrl = Api.FORMAL_SERVER_URL;
            }
            if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.USER_API_TOKEN))) {
                String authorization = "Bearer " + PreferencesUtils.getString(context, Constants.USER_API_TOKEN);

                Api.getInstance(context, baseUrl)
                        .createOrder(options.getLtAppId(), LTToken, (int) LTTime, authorization,
                                requestBody)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<BaseEntry<ResultModel>>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(BaseEntry<ResultModel> result) {
                                if (result != null) {
                                    if (result.getCode() == 200) {
                                        if (result.getData().getLt_order_id() != null) {
                                            mListener.onState(context, RechargeResult.successOf(result));
                                        }
                                    } else {
                                        mListener.onState(context, RechargeResult.failOf(result));
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                mListener.onState(context, RechargeResult.failOf(e.getMessage()));
                            }

                            @Override
                            public void onComplete() {
                            }
                        });
            }


        }
    }


    /**
     * google
     */
    public static void googlePlay(final Context context, String purchaseToken, String orderID,
                                  int mPayTest, final OnRechargeListener mListener) {
        LTGameOptions options = LTGameSdk.options();
        if (!TextUtils.isEmpty(purchaseToken) &&
                !TextUtils.isEmpty(orderID) &&
                !TextUtils.isEmpty(options.getLtAppId()) &&
                !TextUtils.isEmpty(options.getAdID()) &&
                !TextUtils.isEmpty(options.getLtAppKey())) {
            long LTTime = System.currentTimeMillis() / 1000L;
            String LTToken = MD5Util.md5Decode("POST" + options.getLtAppId() + LTTime
                    + options.getLtAppKey());
            Map<String, Object> params = new WeakHashMap<>();
            params.put("platform", 2);
            params.put("adid", options.getAdID());
            params.put("gps_adid", options.getAdID());
            params.put("platform_id", AppUtil.getPackageName(context));
            params.put("purchase_token", purchaseToken);
            params.put("lt_order_id", orderID);
            params.put("pay_test", mPayTest);

            String json = new Gson().toJson(params);//要传递的json
            final RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json);

            String baseUrl = "";
            if (options.getISServerTest()) {
                baseUrl = Api.TEST_SERVER_URL;
            } else {
                baseUrl = Api.FORMAL_SERVER_URL;
            }

            Api.getInstance((Activity) context, baseUrl)
                    .googlePlay(options.getLtAppId(), LTToken, (int) LTTime, requestBody)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry<ResultModel>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry<ResultModel> result) {
                            if (result != null) {
                                mListener.onState((Activity) context, RechargeResult.successOf(result));
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onState((Activity) context,
                                    RechargeResult.failOf(ExceptionHelper.handleException(e)));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    /**
     * oneStore
     */
    public static void oneStorePlay(final Context context, String purchaseToken, String orderID,
                                    int mPayTest, final OnRechargeListener mListener) {
        LTGameOptions options = LTGameSdk.options();
        if (!TextUtils.isEmpty(options.getLtAppId()) &&
                !TextUtils.isEmpty(options.getLtAppKey()) &&
                !TextUtils.isEmpty(options.getAdID()) &&
                !TextUtils.isEmpty(orderID) &&
                !TextUtils.isEmpty(purchaseToken) &&
                mPayTest != -1) {
            long LTTime = System.currentTimeMillis() / 1000L;
            String LTToken = MD5Util.md5Decode("POST" + options.getLtAppId() + LTTime + options.getLtAppKey());
            Map<String, Object> params = new WeakHashMap<>();
            params.put("purchase_id", purchaseToken);
            params.put("lt_order_id", orderID);
            params.put("pay_test", mPayTest);
            params.put("platform", 2);
            params.put("adid", options.getAdID());
            params.put("gps_adid", options.getAdID());
            params.put("platform_id", AppUtil.getPackageName(context));

            String json = new Gson().toJson(params);//要传递的json
            final RequestBody requestBody = RequestBody.create(okhttp3.MediaType
                    .parse("application/json; charset=utf-8"), json);
            String baseUrl = "";
            if (options.getISServerTest()) {
                baseUrl = Api.TEST_SERVER_URL;
            } else {
                baseUrl = Api.FORMAL_SERVER_URL;
            }

            Api.getInstance((Activity) context, baseUrl)
                    .oneStorePlay(options.getLtAppId(), LTToken, (int) LTTime, requestBody)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry<ResultModel>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry<ResultModel> result) {
                            if (result != null) {
                                mListener.onState((Activity) context, RechargeResult.successOf(result));
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onState((Activity) context,
                                    RechargeResult.failOf(ExceptionHelper.handleException(e)));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    /**
     * 自动登录验证
     */
    public static void autoLoginCheck(Context context, String mLtAppID,
                                      String mLtAppKey, String mLtUid, String mLTUidToken,
                                       final OnAutoCheckLoginListener mListener) {
        LTGameOptions options = LTGameSdk.options();
        String baseUrl = "";
        if (!TextUtils.isEmpty(mLtUid) &&
                !TextUtils.isEmpty(mLTUidToken) &&
                !TextUtils.isEmpty(options.getAdID()) &&
                !TextUtils.isEmpty(mLtAppID) &&
                !TextUtils.isEmpty(mLtAppKey)) {
            long LTTime = System.currentTimeMillis() / 1000L;
            String LTToken = MD5Util.md5Decode("POST" + mLtAppID + LTTime + mLtAppKey);
            Map<String, Object> params = new WeakHashMap<>();
            params.put("lt_uid", mLtUid);
            params.put("lt_uid_token", mLTUidToken);
            params.put("platform_id", AppUtil.getPackageName(context));
            params.put("platform", 2);
            params.put("adid", options.getAdID());
            params.put("gps_adid", options.getAdID());
            String json = new Gson().toJson(params);//要传递的json
            final RequestBody requestBody = RequestBody.create(okhttp3.MediaType
                    .parse("application/json; charset=utf-8"), json);

            if (options.getISServerTest()) {
                baseUrl = Api.TEST_SERVER_URL;
            } else {
                baseUrl = Api.FORMAL_SERVER_URL;
            }

            Api.getInstance((Activity) context, baseUrl)
                    .autoLogin(mLtAppID, LTToken, (int) LTTime, requestBody)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry result) {
                            if (result != null) {
                                if (mListener != null) {
                                    if (result.getCode() == 200) {
                                        mListener.onCheckedSuccess(result.getMsg());
                                    } else {
                                        mListener.onCheckedFailed(result.getMsg());
                                    }

                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mListener != null) {
                                mListener.onCheckedException(ExceptionHelper.handleException(e));
                            }
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }


    /**
     * 获取验证码
     *
     * @param phone     手机号
     * @param mListener 接口回调
     */
    public static void getAuthenticationCode(final Activity context, String phone,
                                             final OnLoginStateListener mListener) {
        LTGameOptions options = LTGameSdk.options();
        if (!TextUtils.isEmpty(options.getLtAppId()) &&
                !TextUtils.isEmpty(options.getLtAppKey()) &&
                !TextUtils.isEmpty(options.getAdID()) &&
                !TextUtils.isEmpty(phone)) {
            long LTTime = System.currentTimeMillis() / 1000L;
            String LTToken = MD5Util.md5Decode("GET" + options.getLtAppId() + LTTime + options.getLtAppKey());

            String baseUrl = "";
            if (options.getISServerTest()) {
                baseUrl = Api.TEST_SERVER_URL;
            } else {
                baseUrl = Api.FORMAL_SERVER_URL;
            }
            Api.getInstance(context, baseUrl)
                    .getAuthenCode(options.getLtAppId(), LTToken, (int) LTTime, phone, options.getAdID(), 2)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry<ResultModel>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry<ResultModel> result) {
                            if (result != null) {
                                if (result.getCode() == 200) {
                                    if (mListener != null) {
                                        mListener.onState(context, LoginResult.successOf(result));
                                    }
                                } else {
                                    if (mListener != null) {
                                        mListener.onState(context, LoginResult.failOf(LTGameError.make(result.getMsg())));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onState((Activity) context,
                                    LoginResult.failOf(ExceptionHelper.handleException(e)));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    /**
     * 注册
     * <p>
     *
     * @param phone     手机号
     * @param authCode  验证码
     * @param password  密码
     * @param mListener 接口回调
     */
    public static void register(final Activity context,
                                String phone, int authCode, String password, String adID,
                                final OnLoginStateListener mListener) {
        LTGameOptions options = LTGameSdk.options();
        if (!TextUtils.isEmpty(options.getLtAppId()) &&
                !TextUtils.isEmpty(options.getLtAppKey()) &&
                !TextUtils.isEmpty(options.getAdID()) &&
                !TextUtils.isEmpty(phone) &&
                authCode != 0 &&
                !TextUtils.isEmpty(password) &&
                !TextUtils.isEmpty(adID)) {
            long LTTime = System.currentTimeMillis() / 1000L;
            String LTToken = MD5Util.md5Decode("POST" + options.getLtAppId() + LTTime + options.getLtAppKey());
            Map<String, Object> params = new WeakHashMap<>();
            params.put("phone", phone);
            params.put("auth_code", authCode);
            params.put("password", password);
            params.put("adid", adID);
            params.put("platform", 2);
            params.put("gps_adid", options.getAdID());
            params.put("platform_id", AppUtil.getPackageName(context));
            String baseUrl = "";
            if (options.getISServerTest()) {
                baseUrl = Api.TEST_SERVER_URL;
            } else {
                baseUrl = Api.FORMAL_SERVER_URL;
            }
            Api.getInstance(context, baseUrl)
                    .register(options.getLtAppId(), LTToken, (int) LTTime, params)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry<ResultModel>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry<ResultModel> result) {
                            if (result != null) {
                                if (result.getCode() == 200) {
                                    if (mListener != null) {
                                        mListener.onState(context, LoginResult.successOf(result));
                                    }
                                } else {
                                    if (mListener != null) {
                                        mListener.onState(context, LoginResult.failOf(LTGameError.make(result.getMsg())));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onState((Activity) context,
                                    LoginResult.failOf(ExceptionHelper.handleException(e)));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    /**
     * 登录
     *
     * @param phone     手机号
     * @param password  密码
     * @param mListener 接口回调
     */
    public static void login(final Activity context, String phone, String password,
                             final OnLoginStateListener mListener) {
        LTGameOptions options = LTGameSdk.options();
        if (!TextUtils.isEmpty(options.getLtAppId()) &&
                !TextUtils.isEmpty(options.getLtAppKey()) &&
                !TextUtils.isEmpty(options.getAdID()) &&
                !TextUtils.isEmpty(phone) &&
                !TextUtils.isEmpty(password)) {
            long LTTime = System.currentTimeMillis() / 1000L;
            String LTToken = MD5Util.md5Decode("POST" + options.getLtAppId() + LTTime + options.getLtAppKey());
            Map<String, Object> map = new WeakHashMap<>();
            map.put("phone", phone);
            map.put("password", password);
            map.put("adid", options.getAdID());
            map.put("platform", 2);
            map.put("gps_adid", options.getAdID());
            map.put("platform_id", AppUtil.getPackageName(context));

            String baseUrl = "";
            if (options.getISServerTest()) {
                baseUrl = Api.TEST_SERVER_URL;
            } else {
                baseUrl = Api.FORMAL_SERVER_URL;
            }

            Api.getInstance(context, baseUrl)
                    .login(options.getLtAppId(), LTToken, (int) LTTime, map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry<ResultModel>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry<ResultModel> result) {
                            if (result != null) {
                                if (result.getCode() == 200) {
                                    if (mListener != null) {
                                        mListener.onState(context, LoginResult.successOf(result));
                                    }
                                } else {
                                    if (mListener != null) {
                                        mListener.onState(context, LoginResult.failOf(LTGameError.make(result.getMsg())));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onState((Activity) context,
                                    LoginResult.failOf(ExceptionHelper.handleException(e)));
                        }

                        @Override
                        public void onComplete() {


                        }
                    });
        }
    }

    /**
     * 更新密码
     * <p>
     *
     * @param phone     手机号
     * @param authCode  验证码
     * @param password  密码
     * @param mListener 接口回调
     */
    public static void updatePassword(final Activity context, String phone, int authCode,
                                      String password,
                                      final OnLoginStateListener mListener) {
        LTGameOptions options = LTGameSdk.options();
        if (!TextUtils.isEmpty(options.getLtAppId()) &&
                !TextUtils.isEmpty(options.getLtAppKey()) &&
                !TextUtils.isEmpty(options.getAdID()) &&
                !TextUtils.isEmpty(phone) &&
                !TextUtils.isEmpty(password) &&
                authCode != 0) {
            long LTTime = System.currentTimeMillis() / 1000L;
            String LTToken = MD5Util.md5Decode("POST" + options.getLtAppId() + LTTime + options.getLtAppKey());
            Map<String, Object> map = new WeakHashMap<>();
            map.put("phone", phone);
            map.put("auth_code", authCode);
            map.put("password", password);
            map.put("adid", options.getAdID());
            map.put("platform", 2);
            map.put("gps_adid", options.getAdID());
            map.put("platform_id", AppUtil.getPackageName(context));

            String baseUrl = "";
            if (options.getISServerTest()) {
                baseUrl = Api.TEST_SERVER_URL;
            } else {
                baseUrl = Api.FORMAL_SERVER_URL;
            }

            Api.getInstance(context, baseUrl)
                    .updatePassword(options.getLtAppId(), LTToken, (int) LTTime, map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry<ResultModel>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry<ResultModel> result) {
                            if (result != null) {
                                if (result.getCode() == 200) {
                                    if (mListener != null) {
                                        mListener.onState(context, LoginResult.successOf(result));
                                    }
                                } else {
                                    if (mListener != null) {
                                        mListener.onState(context, LoginResult.failOf(LTGameError.make(result.getMsg())));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onState((Activity) context,
                                    LoginResult.failOf(ExceptionHelper.handleException(e)));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    /**
     * QQ登录
     * <p>
     *
     * @param accessToken token
     * @param openID      openID
     * @param adid        唯一标识
     * @param mListener   接口回调
     */
    public static void qqLogin(final Activity context, String accessToken, String openID,
                               String adid, final OnLoginStateListener mListener) {
        LTGameOptions options = LTGameSdk.options();
        if (!TextUtils.isEmpty(options.getLtAppId()) &&
                !TextUtils.isEmpty(options.getLtAppKey()) &&
                !TextUtils.isEmpty(options.getAdID()) &&
                !TextUtils.isEmpty(accessToken) &&
                !TextUtils.isEmpty(openID) &&
                !TextUtils.isEmpty(adid)) {
            long LTTime = System.currentTimeMillis() / 1000L;
            String LTToken = MD5Util.md5Decode("POST" + options.getLtAppId() + LTTime + options.getLtAppKey());
            Map<String, Object> map = new WeakHashMap<>();
            map.put("access_token", accessToken);
            map.put("open_id", openID);
            map.put("adid", adid);
            map.put("platform", 2);
            map.put("gps_adid", options.getAdID());
            map.put("platform_id",AppUtil.getPackageName(context));

            String baseUrl = "";
            if (options.getISServerTest()) {
                baseUrl = Api.TEST_SERVER_URL;
            } else {
                baseUrl = Api.FORMAL_SERVER_URL;
            }

            Api.getInstance(context, baseUrl)
                    .qqLogin(options.getLtAppId(), LTToken, (int) LTTime, map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry<ResultModel>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry<ResultModel> result) {
                            if (result != null) {
                                if (result.getCode() == 200) {
                                    if (mListener != null) {
                                        mListener.onState(context, LoginResult.successOf(result));
                                    }
                                } else {
                                    if (mListener != null) {
                                        mListener.onState(context, LoginResult.failOf(LTGameError.make(result.getMsg())));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onState((Activity) context,
                                    LoginResult.failOf(ExceptionHelper.handleException(e)));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    /**
     * 微信登录
     * <p>
     *
     * @param accessToken token
     * @param adid        唯一标识
     * @param mListener   接口回调
     */
    public static void weChatLogin(final Activity context, String accessToken,
                                   String adid, final OnLoginStateListener mListener) {
        LTGameOptions options = LTGameSdk.options();
        if (!TextUtils.isEmpty(options.getLtAppId()) &&
                !TextUtils.isEmpty(options.getLtAppKey()) &&
                !TextUtils.isEmpty(options.getAdID()) &&
                !TextUtils.isEmpty(accessToken) &&
                !TextUtils.isEmpty(adid)) {
            long LTTime = System.currentTimeMillis() / 1000L;
            String LTToken = MD5Util.md5Decode("POST" + options.getLtAppId() + LTTime + options.getLtAppKey());
            Map<String, Object> map = new WeakHashMap<>();
            map.put("access_token", accessToken);
            map.put("adid", adid);
            map.put("platform", 2);
            map.put("gps_adid", options.getAdID());
            map.put("platform_id", AppUtil.getPackageName(context));

            String baseUrl = "";
            if (options.getISServerTest()) {
                baseUrl = Api.TEST_SERVER_URL;
            } else {
                baseUrl = Api.FORMAL_SERVER_URL;
            }

            Api.getInstance(context, baseUrl)
                    .weChatLogin(options.getLtAppId(), LTToken, (int) LTTime, map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry<ResultModel>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry<ResultModel> result) {
                            if (result != null) {
                                if (result.getCode() == 200) {
                                    if (mListener != null) {
                                        mListener.onState(context, LoginResult.successOf(result));
                                    }
                                } else {
                                    if (mListener != null) {
                                        mListener.onState(context, LoginResult.failOf(LTGameError.make(result.getMsg())));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onState((Activity) context,
                                    LoginResult.failOf(ExceptionHelper.handleException(e)));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    /**
     * 获取微信AccessToken
     * <p>
     */
    public static void getWXAccessToken(Context context, String baseUrl, String appid,
                                        String secret, String code,
                                        final OnWeChatAccessTokenListener<WeChatAccessToken> mListener) {
        if (!TextUtils.isEmpty(baseUrl) &&
                !TextUtils.isEmpty(appid) &&
                !TextUtils.isEmpty(secret) &&
                !TextUtils.isEmpty(code)) {

            Api.getInstance((Activity) context, baseUrl)
                    .getWXAccessToken(appid, secret, code, "authorization_code")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<WeChatAccessToken>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(WeChatAccessToken result) {
                            if (result != null) {
                                if (result.isNoError()) {
                                    if (mListener != null) {
                                        mListener.onWeChatSuccess(result);
                                    }
                                } else {
                                    if (mListener != null) {
                                        mListener.onWeChatFailed("WeChat get AccessToken error");
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    /**
     * 刷新微信AccessToken
     * <p>
     */
    public static void refreshWXAccessToken(Context context, String baseUrl, String appid,
                                            String refresh_token,
                                            final OnWeChatAccessTokenListener<WeChatAccessToken> mListener) {
        if (!TextUtils.isEmpty(baseUrl) &&
                !TextUtils.isEmpty(appid) &&
                !TextUtils.isEmpty(refresh_token)) {
            Api.getInstance((Activity) context, baseUrl)
                    .refreshWXAccessToken(appid, "refresh_token", refresh_token)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<WeChatAccessToken>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(WeChatAccessToken result) {
                            if (result != null) {
                                if (result.isNoError()) {
                                    if (mListener != null) {
                                        mListener.onWeChatSuccess(result);
                                    }
                                } else {
                                    if (mListener != null) {
                                        mListener.onWeChatFailed("WeChat get AccessToken error");
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    /**
     * 验证微信AccessToken
     * <p>
     */
    public static void authAccessToken(Context context, String access_token,
                                       final OnWeChatAccessTokenListener<AuthWXModel> mListener) {
        if (!TextUtils.isEmpty(access_token)) {
            Api.getInstance((Activity) context, "https://api.weixin.qq.com/cgi-bin/getcallbackip")
                    .authToken(access_token)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<AuthWXModel>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(AuthWXModel result) {
                            if (result != null) {
                                if (result.getErrcode() == 40001) {
                                    if (mListener != null) {
                                        mListener.onWeChatSuccess(result);
                                    }
                                } else {
                                    if (mListener != null) {
                                        mListener.onWeChatFailed("WeChat Validation failed");
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }


    /**
     * 游客登录验证
     */
    public static void guestLogin(final Context context, final OnLoginStateListener mListener) {
        String baseUrl = "";
        LTGameOptions options = LTGameSdk.options();
        if (!TextUtils.isEmpty(options.getLtAppId()) &&
                !TextUtils.isEmpty(options.getLtAppKey()) &&
                !TextUtils.isEmpty(options.getAdID())) {
            long LTTime = System.currentTimeMillis() / 1000L;
            String LTToken = MD5Util.md5Decode("POST" + options.getLtAppId() + LTTime + options.getLtAppKey());
            if (options.getISServerTest()) {
                baseUrl = Api.TEST_SERVER_URL;
            } else {
                baseUrl = Api.FORMAL_SERVER_URL;
            }
            Map<String, Object> params = new WeakHashMap<>();
            params.put("platform", 2);
            params.put("adid", options.getAdID());
            params.put("gps_adid", options.getAdID());
            params.put("platform_id", AppUtil.getPackageName(context));


            Api.getInstance((Activity) context, baseUrl)
                    .guestLogin(options.getLtAppId(), LTToken, (int) LTTime, params)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry<ResultModel>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry<ResultModel> result) {
                            if (result != null) {
                                if (result.getCode() == 200) {
                                    if (result.getData() != null) {
                                        if (mListener != null) {
                                            int code = 0;
                                            BaseEntry<ResultModel> baseEntry = new BaseEntry<>();
                                            baseEntry.setResult(result.getResult());
                                            if (result.getData().getLt_type().equals("register")) {
                                                code = Constants.USER_REGISTER_GUEST_CODE;
                                            }
                                            baseEntry.setCode(code);
                                            baseEntry.setData(result.getData());
                                            baseEntry.setMsg(result.getMsg());
                                            mListener.onState((Activity) context, LoginResult.successOf(baseEntry));
                                        }
                                        if (!TextUtils.isEmpty(result.getData().getApi_token())) {
                                            PreferencesUtils.putString(context, Constants.USER_API_TOKEN,
                                                    result.getData().getApi_token());
                                        }
                                        if (!TextUtils.isEmpty(result.getData().getLt_uid())) {
                                            PreferencesUtils.putString(context, Constants.USER_LT_UID,
                                                    result.getData().getLt_uid());
                                        }
                                        if (!TextUtils.isEmpty(result.getData().getLt_uid_token())) {
                                            PreferencesUtils.putString(context, Constants.USER_LT_UID_TOKEN,
                                                    result.getData().getLt_uid_token());
                                        }
                                    }

                                } else {
                                    if (mListener != null) {
                                        mListener.onState((Activity) context,
                                                LoginResult.failOf(LTGameError.make(result.getMsg())));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mListener != null) {
                                mListener.onState((Activity) context,
                                        LoginResult.failOf(ExceptionHelper.handleException(e)));
                            }
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    /**
     * 绑定账户
     */
    public static void bingAccount(final Context context, String token, String type,
                                   final OnLoginStateListener mListener) {

        LTGameOptions options = LTGameSdk.options();
        if (!TextUtils.isEmpty(options.getLtAppId()) &&
                !TextUtils.isEmpty(options.getLtAppKey()) &&
                !TextUtils.isEmpty(options.getPackageID()) &&
                !TextUtils.isEmpty(options.getAdID())) {
            long LTTime = System.currentTimeMillis() / 1000L;
            String LTToken = MD5Util.md5Decode("POST" + options.getLtAppId() + LTTime + options.getLtAppKey());
            String baseUrl = "";
            if (options.getISServerTest()) {
                baseUrl = Api.TEST_SERVER_URL;
            } else {
                baseUrl = Api.FORMAL_SERVER_URL;
            }
            Map<String, Object> params = new WeakHashMap<>();
            params.put("access_token", token);
            params.put("platform", 2);
            params.put("adid", options.getAdID());
            params.put("gps_adid", options.getAdID());
            params.put("platform_id", options.getPackageID());
            params.put("type", type);

            Api.getInstance((Activity) context, baseUrl)
                    .bindAccount(options.getLtAppId(), LTToken, (int) LTTime, params)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry<ResultModel>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry<ResultModel> result) {
                            if (result != null) {
                                if (result.getCode() == 200) {
                                    if (result.getData() != null) {
                                        if (mListener != null) {
                                            mListener.onState((Activity) context, LoginResult.successOf(result));
                                        }
                                        if (!TextUtils.isEmpty(result.getData().getApi_token())) {
                                            PreferencesUtils.putString(context, Constants.USER_API_TOKEN,
                                                    result.getData().getApi_token());
                                        }
                                        if (!TextUtils.isEmpty(result.getData().getLt_uid())) {
                                            PreferencesUtils.putString(context, Constants.USER_LT_UID,
                                                    result.getData().getLt_uid());
                                        }
                                        if (!TextUtils.isEmpty(result.getData().getLt_uid_token())) {
                                            PreferencesUtils.putString(context, Constants.USER_LT_UID_TOKEN,
                                                    result.getData().getLt_uid_token());
                                        }
                                    }

                                } else if (result.getCode() == 431) {
                                    if (result.getData() != null) {
                                        if (mListener != null) {
                                            BaseEntry<ResultModel> baseEntry = new BaseEntry<>();
                                            baseEntry.setCode(Constants.USER_ALREADY_BIND_CODE);
                                            mListener.onState((Activity) context, LoginResult.successOf(baseEntry));
                                        }
                                    }
                                } else {
                                    if (mListener != null) {
                                        mListener.onState((Activity) context,
                                                LoginResult.failOf(LTGameError.make(result.getCode(), result.getMsg())));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mListener != null) {
                                mListener.onState((Activity) context,
                                        LoginResult.failOf(ExceptionHelper.handleException(e)));
                            }
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    /**
     * 解绑账户
     */
    public static void unBindAccount(final Context context, final OnLoginStateListener mListener) {

        LTGameOptions options = LTGameSdk.options();
        if (!TextUtils.isEmpty(options.getLtAppId()) &&
                !TextUtils.isEmpty(options.getLtAppKey()) &&
                !TextUtils.isEmpty(options.getPackageID()) &&
                !TextUtils.isEmpty(options.getAdID())) {
            long LTTime = System.currentTimeMillis() / 1000L;
            String LTToken = MD5Util.md5Decode("POST" + options.getLtAppId() + LTTime + options.getLtAppKey());
            String baseUrl = "";
            if (options.getISServerTest()) {
                baseUrl = Api.TEST_SERVER_URL;
            } else {
                baseUrl = Api.FORMAL_SERVER_URL;
            }
            Map<String, String> params = new WeakHashMap<>();
            params.put("adid", options.getAdID());
            Api.getInstance((Activity) context, baseUrl)
                    .unBindAccount(options.getLtAppId(), LTToken, (int) LTTime, params)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry<ResultModel>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry<ResultModel> result) {
                            if (result != null) {
                                if (result.getCode() == 200) {
                                    if (result.getData() != null) {
                                        if (mListener != null) {
                                            mListener.onState((Activity) context, LoginResult.successOf(result));
                                        }
                                        if (!TextUtils.isEmpty(result.getData().getApi_token())) {
                                            PreferencesUtils.putString(context, Constants.USER_API_TOKEN,
                                                    result.getData().getApi_token());
                                        }
                                        if (!TextUtils.isEmpty(result.getData().getLt_uid())) {
                                            PreferencesUtils.putString(context, Constants.USER_LT_UID,
                                                    result.getData().getLt_uid());
                                        }
                                        if (!TextUtils.isEmpty(result.getData().getLt_uid_token())) {
                                            PreferencesUtils.putString(context, Constants.USER_LT_UID_TOKEN,
                                                    result.getData().getLt_uid_token());
                                        }
                                    }

                                } else {
                                    if (mListener != null) {
                                        mListener.onState((Activity) context,
                                                LoginResult.failOf(LTGameError.make(result.getCode(), result.getMsg())));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mListener != null) {
                                mListener.onState((Activity) context,
                                        LoginResult.failOf(ExceptionHelper.handleException(e)));
                            }
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }


    /**
     * 绑定游戏角色
     */
    public static void bindRole(final Context context, String ltAppID, String ltAppKey, String adid,
                                String roleID, String roleName,
                                String roleSex, String roleUid, boolean isServerTest) {
        if (!TextUtils.isEmpty(adid) &&
                !TextUtils.isEmpty(ltAppID) &&
                !TextUtils.isEmpty(ltAppKey) &&
                !TextUtils.isEmpty(roleID) &&
                !TextUtils.isEmpty(roleName) &&
                !TextUtils.isEmpty(roleSex) &&
                !TextUtils.isEmpty(roleUid)) {
            long LTTime = System.currentTimeMillis() / 1000L;
            String LTToken = MD5Util.md5Decode("POST" + ltAppID + LTTime + ltAppKey);
            String baseUrl = "";
            if (isServerTest) {
                baseUrl = Api.TEST_SERVER_URL;
            } else {
                baseUrl = Api.FORMAL_SERVER_URL;
            }
            Map<String, Object> params = new WeakHashMap<>();
            params.put("platform", 2);
            params.put("adid", adid);
            params.put("gps_adid", adid);
            params.put("platform_id", AppUtil.getPackageName(context));
            params.put("r_id", roleID);
            params.put("r_name", roleName);
            params.put("r_sex", roleSex);
            params.put("r_uid", roleUid);
            Api.getInstance((Activity) context, baseUrl)
                    .bindRole(ltAppID, LTToken, (int) LTTime, params)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry result) {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    /**
     * 提交设备信息
     */
    public static void uploadDeviceInfo(final Context context, final OnUploadDeviceListener mListener) {

        LTGameOptions options = LTGameSdk.options();
        if (!TextUtils.isEmpty(options.getLtAppId()) &&
                !TextUtils.isEmpty(options.getLtAppKey()) &&
                !TextUtils.isEmpty(options.getAdID())) {
            long LTTime = System.currentTimeMillis() / 1000L;
            String LTToken = MD5Util.md5Decode("POST" + options.getLtAppId() + LTTime + options.getLtAppKey());
            String baseUrl = "";
            if (options.getISServerTest()) {
                baseUrl = Api.TEST_SERVER_URL;
            } else {
                baseUrl = Api.FORMAL_SERVER_URL;
            }
            Map<String, Object> params = new WeakHashMap<>();
            params.put("platform", 2);
            params.put("adid", options.getAdID());
            params.put("package_id", AppUtil.getPackageName(context));
            params.put("device", AppUtil.getManufacturer());
            params.put("system", AppUtil.getOSVersion());
            params.put("district", AppUtil.getLocation());
            params.put("lang", AppUtil.getLanguage());
            params.put("gps_adid", options.getAdID());
            Api.getInstance((Activity) context, baseUrl)
                    .uploadDevice(options.getLtAppId(), LTToken, (int) LTTime, params)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry result) {
                            if (result != null) {
                                if (result.getCode() == 200) {
                                    if (result.getData() != null) {
                                        if (mListener != null) {
                                            mListener.onSuccess();
                                        }
                                    }

                                } else {
                                    if (mListener != null) {
                                        mListener.onFailed(result.getMsg());
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mListener != null) {
                                mListener.onFailed(e.getMessage());
                            }
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }
}
