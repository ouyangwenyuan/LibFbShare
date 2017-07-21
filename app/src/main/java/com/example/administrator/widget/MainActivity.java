package com.example.administrator.widget;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.beta.fbinvite.invite.FBFriendsModel;
import com.beta.fbinvite.invite.FBUtils;
import com.beta.fbinvite.invite.FbRequest;
import com.beta.fbinvite.invite.MyLog;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.widget.LoginButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


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
    private List<FBFriendsModel.PayloadBean> model = null;

    private Handler handler = new Handler();
    private Map<FBFriendsModel.PayloadBean, Boolean> selectFriends = new HashMap<>();

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
        findViewById(R.id.bt_select_friend).setOnClickListener(this);

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
                MyLog.i("accessToken=" + accessToken);
                if (accessToken != null) {
//                    mFbUtils.getList();
                    mFbUtils.fetchFriends(new FbRequest.FbFetchFriendCallback() {
                        @Override
                        public void onFinish(List<FBFriendsModel.PayloadBean> model1) {
                            model = model1;

                        }

                        @Override
                        public void onFail(String error) {
                            Log.i("friendlist", "onFail=" + error);
                        }

                        @Override
                        public void onFetchItem(FBFriendsModel.PayloadBean modelPayload) {
                            MyLog.i("modelPayload=" + modelPayload.getText() + ",uid=" + modelPayload.getUid());
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
                    StringBuilder ids = new StringBuilder();
                    for (int i = 0; i < size; i++) {
                        ids.append(model.get(i).getUid());
                        ids.append(",");
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
                            sendInvite(ids);
                            ids.delete(0, ids.length());
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
            case R.id.bt_select_friend: {
                if (model != null) {
                    int size = model.size();
                    StringBuilder ids = new StringBuilder();
                    final CharSequence[] items = new CharSequence[size];
                    for (int i = 0; i < size; i++) {
//                        ids += model.get(i).getUid() + ",";
                        ids.append(model.get(i).getUid());
                        ids.append(",");
                        items[i] = model.get(i).getText();
                    }
                    MyLog.i("friendlist=" + ids);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    selectFriends.put(model.get(which), isChecked);
                                }
                            }).setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Set<FBFriendsModel.PayloadBean> sequenceSet = selectFriends.keySet();
                                    StringBuilder ids = new StringBuilder();
                                    for (FBFriendsModel.PayloadBean key : sequenceSet) {
                                        ids.append(key.getUid());
                                        ids.append(",");
                                    }
                                    sendInvite(ids);
                                    MyLog.i("friendlist=" + ids);
                                }
                            }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "facebook好友列表为空，请重新获取好友", Toast.LENGTH_SHORT).show();
                }
            }
            break;

        }
    }

    private void sendInvite(StringBuilder ids) {
        String substring = ids.substring(0, ids.length() - 1);
        mFbUtils.sendInvites(substring, new FbRequest.FbInviteCallback() {
            @Override
            public void onSuccess(String keys) {
                MyLog.i("发送邀请成功 ,uid=" + keys);
            }

            @Override
            public void onFail(String error) {
                MyLog.i("error=" + error);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fbcallbackMgr.onActivityResult(requestCode, resultCode, data);
    }

    private class FriendsAdapter extends BaseAdapter {
        List<FBFriendsModel.PayloadBean> model1;

        public FriendsAdapter(List<FBFriendsModel.PayloadBean> model1) {
            this.model1 = model1;
        }

        public void setModel1(List<FBFriendsModel.PayloadBean> model1) {
            this.model1 = model1;
        }

        @Override
        public int getCount() {
            return model1 == null ? 0 : model1.size();
        }

        @Override
        public Object getItem(int position) {
            return model1.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }
}