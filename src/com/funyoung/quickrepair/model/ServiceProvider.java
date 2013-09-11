package com.funyoung.quickrepair.model;

import android.os.Bundle;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by yangfeng on 13-8-11.
 */
public class ServiceProvider extends User {
    public boolean isProviderType() {
        return true;
    }

    public ServiceProvider(long uid, String nickName, String avatarUrl, String address, String mobile) {
        super(uid, nickName, avatarUrl, address, mobile);
    }

    public static ServiceProvider parseFromJson(String jstr) throws Exception {
        try {
            JSONObject jsonObject = new JSONObject(jstr);
            if (null == jsonObject) {
                throw new Exception("Invalid json to convert as User " + jstr);
            }
            long uid = jsonObject.optLong(KEY_UID);
            if (uid > 0) {
                String nickName= jsonObject.optString(KEY_NAME);
                if (TextUtils.isEmpty(nickName)) {
                    nickName= jsonObject.optString(KEY_NICK_NAME);
                }

                String avatarUrl = jsonObject.optString(KEY_AVATAR);
                String address = jsonObject.optString(KEY_ADDRESS);
                String mobile = jsonObject.optString(KEY_USER_MOBILE);
                return new ServiceProvider(uid, nickName, avatarUrl, address, mobile);
            } else {
                throw new Exception("Invalid uid in " + jstr);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    public Bundle toBundle() {
        Bundle bundle = super.toBundle();
        return bundle;
    }

    public static ArrayList<ServiceProvider> parseListResult(String result) {
        return null;
    }
}
