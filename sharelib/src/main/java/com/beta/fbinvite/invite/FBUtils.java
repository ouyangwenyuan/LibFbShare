package com.beta.fbinvite.invite;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by hedong on 2017/7/5.
 */

public class FBUtils implements FbRequest {


    public static final String REQUEST_URL = "https://m.facebook.com/v2.2/dialog/apprequests";
    public static final String REQUEST_LIST_URL = "https://m.facebook.com/ds/first_degree.php";
    public static final String REQUEST_SEND_URL = "https://m.facebook.com/v2.2/dialog/app_requests/submit";
    public static final String REQUEST_BIG_IMAGE = "https://graph.facebook.com/";
    public static final String REQUEST_PARAMS_A = "Accept";
    public static final String REQUEST_PARAMS_AL = "Accept-Language";
    public static final String REQUEST_PARAMS_UA = "User-Agent";
    public static final String REQUEST_PARAMS_COOKIE = "Cookie";
    public static final String REQUEST_PARAMS_TYPE = "X-Requested-With";
    public static final String REQUEST_PARAMS_A_VALUE = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
    public static final String REQUEST_PARAMS_AL_VALUE = "en-US";
    public static final String REQUEST_PARAMS_UA_VALUE = "Mozilla/5.0 (iPad; CPU OS 10_2 like Mac OS X) AppleWebKit/602.3.12 (KHTML, like Gecko) Mobile/14C89";
    public static final String REQUEST_PARAMS_COOKIE_VALUE = "undefined";
    public static final String REQUEST_PARAMS_TOKEN = "token";
    public static final String REQUEST_PARAMS_TOKEN_VALUE = "v7";
    public static final String REQUEST_PARAMS_TYPE_VALUE = "XMLHttpRequest";

    public static final String REQUEST_PARAMS_FILTER = "filter[0]";
    public static final String REQUEST_PARAMS_FILTER_VALUE = "user";
    public static final String REQUEST_PARAMS_OPTION_1 = "options[0]";
    public static final String REQUEST_PARAMS_OPTION_1_VALUE = "friends_only";
    public static final String REQUEST_PARAMS_OPTION_2 = "options[1]";
    public static final String REQUEST_PARAMS_OPTION_2_VALUE = "nm";
    public static final String REQUEST_PARAMS_OPTION_3 = "options[2]";
    public static final String REQUEST_PARAMS_OPTION_3_VALUE = "skip_family";
    public static final String REQUEST_PARAMS_INCLUDE_VIEWER = "include_viewer";
    public static final String REQUEST_PARAMS_INCLUDE_VIEWER_VALUE = "0";
    public static final String REQUEST_PARAMS_VIEWER = "viewer";
    public static final String REQUEST_PARAMS_VIEWER_VALUE = "AfON0WzqcP_Oj35d";
    public static final String REQUEST_PARAMS_KEY = "session_key";
    public static final String REQUEST_PARAMS_APP_ID = "app_id";
    public static final String REQUEST_PARAMS_ACCESS_TOKEN = "access_token";
    public static final String REQUEST_PARAMS_DISPLAY = "display";
    public static final String REQUEST_PARAMS_DISPLAY_VALUE = "touch";
    public static final String REQUEST_PARAMS_MESSAGE = "message";
    public static final String REQUEST_PARAMS_MESSAGE_VALUE = "Come Here";
    public static final String REQUEST_PARAMS_REDIRECT = "redirect_uri";
    public static final String REQUEST_PARAMS_REDIRECT_VALUE = "fbconnect://success";
    public static final String REQUEST_PARAMS_REDIRECT_LSD = "lsd";
    public static final String REQUEST_PARAMS_REDIRECT_DATA = "data";
    public static final String REQUEST_PARAMS_REDIRECT_POST = "from_post";
    public static final String REQUEST_PARAMS_REDIRECT_POST_VALUE = "1";
    public static final String REQUEST_PARAMS_REDIRECT_TEXT = "text_[]";
    public static final String REQUEST_PARAMS_REDIRECT_TO = "to";
    public static final String SIZE = "picture.width(%d).height(%d)";

//    private CompositeSubscription mSubscriptions;
    private Context context;
    private String app_id;

    public FBUtils(String app_id) {
//        this.context = context;
        this.app_id = app_id;
    }


    public void fetchFriends(final FbFetchFriendCallback callback) {

        fetchSessionKeys(new FbInviteCallback() {
            @Override
            public void onSuccess(String keys) {
                HashMap<String, String> friendmaps = new HashMap<>(5);
                friendmaps.put(REQUEST_PARAMS_KEY, keys);
                friendmaps.put(REQUEST_PARAMS_TOKEN, REQUEST_PARAMS_TOKEN_VALUE);
                friendmaps.put(REQUEST_PARAMS_FILTER, REQUEST_PARAMS_FILTER_VALUE);
                friendmaps.put(REQUEST_PARAMS_OPTION_1, REQUEST_PARAMS_OPTION_1_VALUE);
                friendmaps.put(REQUEST_PARAMS_OPTION_2, REQUEST_PARAMS_OPTION_2_VALUE);
                friendmaps.put(REQUEST_PARAMS_OPTION_3, REQUEST_PARAMS_OPTION_3_VALUE);
                friendmaps.put(REQUEST_PARAMS_INCLUDE_VIEWER, REQUEST_PARAMS_INCLUDE_VIEWER_VALUE);
                friendmaps.put(REQUEST_PARAMS_VIEWER, REQUEST_PARAMS_VIEWER_VALUE);
                friendmaps.put(REQUEST_PARAMS_APP_ID, app_id);

                StringBuilder tempParams = new StringBuilder();
                try {
                    int pos = 0;
                    for (String key : friendmaps.keySet()) {
                        if (pos > 0) {
                            tempParams.append("&");
                        }
                        tempParams.append(String.format("%s=%s", key, URLEncoder.encode(friendmaps.get(key), "utf-8")));

                        pos++;
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String requestUrl = String.format("%s?%s", REQUEST_LIST_URL, tempParams.toString());

                Request request = new Request.Builder()
                        .addHeader(REQUEST_PARAMS_COOKIE, REQUEST_PARAMS_COOKIE_VALUE)
                        .addHeader(REQUEST_PARAMS_A, REQUEST_PARAMS_A_VALUE)
                        .addHeader(REQUEST_PARAMS_AL, REQUEST_PARAMS_AL_VALUE)
                        .addHeader(REQUEST_PARAMS_UA, REQUEST_PARAMS_UA_VALUE)
                        .addHeader(REQUEST_PARAMS_TYPE, REQUEST_PARAMS_TYPE_VALUE)
                        .url(requestUrl)
                        .build();

                OkHttpClient okHttpClient = new OkHttpClient();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        callback.onFail(call + ",exception=" + e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) {
                        if (response != null && response.body() != null) {
                            String responeString = null;
                            try {
                                responeString = response.body().string();
                                int index = responeString.indexOf("{");
                                String json = responeString.substring(index, responeString.length());
                                Log.i("friendlist", "json=" + json);
                                Gson jsonData = new Gson();
                                FBFriendsModel model = jsonData.fromJson(json, FBFriendsModel.class);
                                final List<FBFriendsModel.PayloadBean> modelPayload = model.getPayload();

                                callback.onFinish(modelPayload);

//                                getUserDetail(modelPayload, callback);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });

            }

            @Override
            public void onFail(String error) {
                callback.onFail(error);
            }
        });
    }

//    private void getUserDetail(final List<FBFriendsModel.PayloadBean> modelPayload, final FbFetchFriendCallback callback) {
//        addSubscription(Observable.from(modelPayload)
//                .concatMap(new Func1<FBFriendsModel.PayloadBean, Observable<UrlModel>>() {
//                    @Override
//                    public Observable<UrlModel> call(FBFriendsModel.PayloadBean payloadBean) {
//                        LoginAPIService server = RetrofitManager.getInstance(REQUEST_BIG_IMAGE).create(LoginAPIService.class);
//                        return server.getUrl(payloadBean.getUid() + "", String.format(SIZE, 148, 148));
//                    }
//                }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<UrlModel>() {
//                    @Override
//                    public void call(UrlModel urlModel) {
//                        for (FBFriendsModel.PayloadBean item : modelPayload) {
//                            if ((item.getUid() + "").equals(urlModel.getId())) {
//                                String path = urlModel.getPicture().getData().getUrl();
//                                item.setPhoto(path);
//                                callback.onFetchItem(item);
//                            }
//                        }
//                    }
//                }, new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable throwable) {
//
//                    }
//                }));
//    }
//
//    private void addSubscription(Subscription subscription) {
//        if (mSubscriptions == null) {
//            mSubscriptions = new CompositeSubscription();
//        }
//        mSubscriptions.add(subscription);
//    }

    /**
     *
     */
    private void fetchSessionKeys(final FbInviteCallback fetchSessionFinsh) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        String token = accessToken != null ? accessToken.getToken() : "";
        HashMap<String, String> maps = new HashMap<>(5);
        maps.put(REQUEST_PARAMS_ACCESS_TOKEN, token);
        maps.put(REQUEST_PARAMS_APP_ID, app_id);
        maps.put(REQUEST_PARAMS_DISPLAY, REQUEST_PARAMS_DISPLAY_VALUE);
        maps.put(REQUEST_PARAMS_MESSAGE, REQUEST_PARAMS_MESSAGE_VALUE);
        maps.put(REQUEST_PARAMS_REDIRECT, REQUEST_PARAMS_REDIRECT_VALUE);

        StringBuilder tempParams = new StringBuilder();
        int pos = 0;
        try {
            Set<String> set = maps.keySet();
            for (String key : set) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(maps.get(key), "utf-8")));
                pos++;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            fetchSessionFinsh.onFail(e.getMessage());
        }
        String requestUrl = String.format("%s?%s", REQUEST_URL, tempParams.toString());

        Request request = new Request.Builder()
                .addHeader(REQUEST_PARAMS_A, REQUEST_PARAMS_A_VALUE)
                .addHeader(REQUEST_PARAMS_AL, REQUEST_PARAMS_AL_VALUE)
                .addHeader(REQUEST_PARAMS_UA, REQUEST_PARAMS_UA_VALUE)
                .url(requestUrl)
                .build();

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                fetchSessionFinsh.onFail(call + ",exception=" + e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response != null && response.body() != null) {
                    String responeString = null;
                    try {
                        responeString = response.body().string();
//                        onWork(responeString, REQUEST_TYPE_INVITE);
                        String str = "session_key=";
                        int position = responeString.indexOf(str);
                        String substring = responeString.substring(position + str.length(), responeString.length());
                        int index = substring.indexOf("&");
                        String sKey = substring.substring(0, index);
                        fetchSessionFinsh.onSuccess(sKey);

                    } catch (IOException e) {
                        e.printStackTrace();
                        fetchSessionFinsh.onFail(e.getMessage());
                    }

                }
            }
        });
    }

    @Override
    public void sendInvites(String ids, final FbInviteCallback requestCallback) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        String token = accessToken != null ? accessToken.getToken() : "";
        Log.i("friendlist", "token=" + token);

        HashMap<String, String> postmaps = new HashMap<>(5);
        postmaps.put(REQUEST_PARAMS_ACCESS_TOKEN, token);
        postmaps.put(REQUEST_PARAMS_APP_ID, app_id);
        postmaps.put(REQUEST_PARAMS_DISPLAY, REQUEST_PARAMS_DISPLAY_VALUE);
        postmaps.put(REQUEST_PARAMS_REDIRECT_LSD, "");
        postmaps.put(REQUEST_PARAMS_REDIRECT_DATA, "");
        postmaps.put(REQUEST_PARAMS_REDIRECT_POST, REQUEST_PARAMS_REDIRECT_POST_VALUE);
        postmaps.put(REQUEST_PARAMS_REDIRECT, REQUEST_PARAMS_REDIRECT_VALUE);
        postmaps.put(REQUEST_PARAMS_MESSAGE, REQUEST_PARAMS_MESSAGE_VALUE);
        postmaps.put(REQUEST_PARAMS_REDIRECT_TEXT, "ouyang");
        postmaps.put(REQUEST_PARAMS_REDIRECT_TO, ids);

        MultipartBody.Builder builder = new MultipartBody.Builder();

        int pos = 0;
        for (String key : postmaps.keySet()) {
            if (pos > 0) {
                builder.addFormDataPart(key, postmaps.get(key));
            }
            pos++;
        }
        RequestBody formBody = builder.setType(MultipartBody.FORM).build();


        Request request = new Request.Builder()
                .addHeader(REQUEST_PARAMS_COOKIE, REQUEST_PARAMS_COOKIE_VALUE)
                .addHeader(REQUEST_PARAMS_A, REQUEST_PARAMS_A_VALUE)
                .addHeader(REQUEST_PARAMS_AL, REQUEST_PARAMS_AL_VALUE)
                .addHeader(REQUEST_PARAMS_UA, REQUEST_PARAMS_UA_VALUE)
                .addHeader(REQUEST_PARAMS_TYPE, REQUEST_PARAMS_TYPE_VALUE)
                .url(REQUEST_SEND_URL)
                .post(formBody)
                .build();

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                requestCallback.onFail(call + ",exception= " + e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                requestCallback.onSuccess(response.toString());
            }
        });

    }

    @Override
    public void shareApp(Activity context, String url, CallbackManager callBackManager, FacebookCallback callback) {
        ShareDialog shareDialog = new ShareDialog(context);
        shareDialog.setShouldFailOnDataError(true);
        //注册分享状态监听回调接口
        shareDialog.registerCallback(callBackManager, callback);
        ShareLinkContent.Builder shareLinkContentBuilder = new ShareLinkContent.Builder();
        shareLinkContentBuilder.setContentUrl(Uri.parse(url));
        ShareLinkContent shareLinkContent = shareLinkContentBuilder.build();
        if (shareDialog.canShow(ShareLinkContent.class)) {
            shareDialog.show(context, shareLinkContent);
        }
    }
}
