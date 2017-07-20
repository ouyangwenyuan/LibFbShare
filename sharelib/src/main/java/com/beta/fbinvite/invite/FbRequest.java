package com.beta.fbinvite.invite;

import android.app.Activity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;

import java.util.List;

/**
 * Created by ouyangwenyuan on 2017/7/20.
 */

public interface FbRequest {

    /**
     * 发送应用邀请回调
     */
    interface FbInviteCallback {
        /**
         * 成功返回
         *
         * @param keys
         */
        void onSuccess(String keys);

        /**
         * 错误返回
         *
         * @param error
         */
        void onFail(String error);
    }


    /**
     * 获取好友列表回调
     */
    interface FbFetchFriendCallback {
        /**
         * 好友列表
         *
         * @param model
         */
        void onFinish(List<FBFriendsModel.PayloadBean> model);

        /**
         * 单个好友信息
         *
         * @param modelPayload
         */
        void onFetchItem(FBFriendsModel.PayloadBean modelPayload);

        /**
         * 错误信息
         *
         * @param error
         */
        void onFail(String error);
    }

    /**
     * 获取好友列表
     *
     * @param requestCallback 发送应用邀请回调
     */
    void fetchFriends(FbFetchFriendCallback requestCallback);

    /**
     * 发送邀请给好友
     *
     * @param ids             好友列表
     * @param requestCallback 获取好友列表回调
     */
    void sendInvites(String ids, FbInviteCallback requestCallback);


    void shareApp(Activity context, String url, CallbackManager callBackManager, FacebookCallback callback);
}
