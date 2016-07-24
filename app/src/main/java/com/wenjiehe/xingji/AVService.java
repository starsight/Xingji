package com.wenjiehe.xingji;

/**
 * Created by wenjie on 16/07/22.
 */

import android.util.Log;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.FunctionCallback;
import com.avos.avoscloud.RequestPasswordResetCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SignUpCallback;

import java.util.HashMap;
import java.util.Map;

public class AVService {


    //Use callFunctionMethod
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void getAchievement(String userObjectId) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("userObjectId", userObjectId);
        AVCloud.callFunctionInBackground("hello", parameters,
                new FunctionCallback() {
                    @Override
                    public void done(Object object, AVException e) {
                        if (e == null) {
                            Log.e("at", object.toString());// processResponse(object);
                        } else {
                            // handleError();
                        }
                    }
                });
    }


    public static void requestPasswordReset(String email, RequestPasswordResetCallback callback) {
        AVUser.requestPasswordResetInBackground(email, callback);
    }

    public static void findDoingListGroup(FindCallback<AVObject> findCallback) {
        AVQuery<AVObject> query = new AVQuery<AVObject>("DoingListGroup");
        query.orderByAscending("Index");
        query.findInBackground(findCallback);
    }

    public static void findChildrenList(String groupObjectId, FindCallback<AVObject> findCallback) {
        AVQuery<AVObject> query = new AVQuery<AVObject>("DoingListChild");
        query.orderByAscending("Index");
        query.whereEqualTo("parentObjectId", groupObjectId);
        query.findInBackground(findCallback);
    }

    /*public static void initPushService(Context ctx) {
        PushService.setDefaultPushCallback(ctx, ChooseLoginRegActivity.class);
        PushService.subscribe(ctx, "public", ChooseLoginRegActivity.class);
        AVInstallation.getCurrentInstallation().saveInBackground();
    }*/

    public static void signUp(String username, String password, String email,
                              SignUpCallback signUpCallback) {//String phonenumber,
        AVUser user = new AVUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        //user.setMobilePhoneNumber(phonenumber);
        user.signUpInBackground(signUpCallback);
    }

    public static void logout() {
        AVUser.logOut();
    }

    public static void createAdvice(String userId, String advice, SaveCallback saveCallback) {
        AVObject doing = new AVObject("SuggestionByUser");
        doing.put("UserObjectId", userId);
        doing.put("UserSuggestion", advice);
        doing.saveInBackground(saveCallback);
    }
}
