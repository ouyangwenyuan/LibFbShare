package com.example.administrator.widget;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.beta.fbinvite.invite.FBFriendsModel;
import com.beta.fbinvite.invite.FBUtils;
import com.beta.fbinvite.invite.FbRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginBehavior;
import com.facebook.login.widget.LoginButton;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int login_type = 0;
    private static final int share_type = 1;

    private class MyFackbookCallback implements FacebookCallback {
        private int type;

        public MyFackbookCallback(int type) {
            this.type = type;
        }

        @Override
        public void onSuccess(Object o) {
            Toast.makeText(getApplicationContext(), type == login_type ? "登陆成功" : "分享成功", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(getApplicationContext(), type == login_type ? "登陆取消" : "分享取消", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(FacebookException error) {
            Toast.makeText(getApplicationContext(), type == login_type ? "登陆失败" : "分享失败" + error.toString(), Toast.LENGTH_LONG).show();
        }
    }


    private CallbackManager fbcallbackMgr = CallbackManager.Factory.create();

    public FBUtils mFbUtils;
    List<FBFriendsModel.PayloadBean> model = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // TODO: 2017/7/19   login and init
        LoginButton loginFbBtn = (LoginButton) findViewById(R.id.bt_login);
        loginFbBtn.setReadPermissions("email");
        loginFbBtn.registerCallback(fbcallbackMgr, new MyFackbookCallback(login_type));


        findViewById(R.id.bt_friendlist).setOnClickListener(this);
        findViewById(R.id.bt_invited).setOnClickListener(this);
        findViewById(R.id.bt_shared).setOnClickListener(this);

//        String app_id = getResources().getString(R.string.facebook_app_id);
        mFbUtils = new FBUtils(getString(R.string.facebook_app_id));

//        event log
//        AppEventsLogger logger = AppEventsLogger.newLogger(this);
//        logger.logPurchase(BigDecimal.valueOf(4.32), Currency.getInstance("USD"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login: {

            }
            break;
            case R.id.bt_friendlist: {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                Log.i("friendlist", "accessToken=" + accessToken);
                if (accessToken != null) {
//                    mFbUtils.getList();
                    mFbUtils.fetchFriends(new FbRequest.FbFetchFriendCallback() {
                        @Override
                        public void onFinish(List<FBFriendsModel.PayloadBean> model1) {
                            model = model1;

                            int size = model.size();
                            String ids = "";
                            for (int i = 0; i < size; i++) {
                                ids += model.get(i).getUid() + ",";
                            }
                            Log.i("friendlist", "friendlist=" + ids);
                        }

                        @Override
                        public void onFail(String error) {
                            Log.i("friendlist", "onFail=" + error);
                        }

                        @Override
                        public void onFetchItem(FBFriendsModel.PayloadBean modelPayload) {
                            Log.i("friendlist", "modelPayload=" + modelPayload.getText() + ",uid=" + modelPayload.getUid());
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "你还没有登陆facebook，请点击登录按钮登录", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case R.id.bt_invited: {

                if (model != null) {
                    int size = model.size();
                    String ids = "";
                    for (int i = 0; i < size; i++) {
                        ids += model.get(i).getUid() + ",";
                        if (((i + 1) % 50 == 0 || i == size - 1)) {
//                            int index = 0;
//                            if (i > 49) {
//                                index = i - 49;
//                            }
//                            FBFriendsModel.PayloadBean payloadBean = model.get(index);
//                            String display = payloadBean.getDisplay();
//                            if (TextUtils.isEmpty(display)) {
//                                display = payloadBean.getText();
//                            }
                            String substring = ids.substring(0, ids.length() - 1);
                            mFbUtils.sendInvites(substring, new FbRequest.FbInviteCallback() {
                                @Override
                                public void onSuccess(String keys) {
                                    Log.i("friendlist", "发送邀请成功 ,uid=" + keys);
                                }

                                @Override
                                public void onFail(String error) {
                                    Log.i("friendlist", "error=" + error);
                                }
                            });
                            ids = "";
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "facebook好友列表为空，请重新获取好友", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case R.id.bt_shared: {

                String url = "https://play.google.com/store/apps/details?id=com.tbs.piano&referrer=utm_source%old_piano";

                mFbUtils.shareApp(this, url, fbcallbackMgr, new MyFackbookCallback(share_type));
            }
            break;

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fbcallbackMgr.onActivityResult(requestCode, resultCode, data);
    }
}