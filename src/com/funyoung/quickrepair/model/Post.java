package com.funyoung.quickrepair.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by yangfeng on 13-8-18.
 * "uid":"XXX" //当前登录用户的uid
 category: xxx  // 大分类id
 sub_category:xxx // 小分类id
 description：”XXX” // 描述信息
 “latitude”:”XXXX” //维度
 “longitude”XXXX” //经度
 address：详细地址
 area：区域
 brand:品牌
 version:型号
 createyear:年份
 photo[]: “XXX” // 图片URL列表， 图片文件需先调接口生成URL
 */
public class Post implements Parcelable {
    private static final String TAG = "Post";
    public String postId;
    public long uid;
    public int category;
    public int subCategory;
    public long latitude;
    public long longitude;

    public String description;
    public String address;
    public String area;
    public String brand;
    public String contact;
    public String mobile;

    public String model;
    public String createYear;
    public String[] photos;

    // remote attribute
    public long createTime;
    public long updateTime;
    public String price;
    public int status;

    public static ArrayList<Post> parseListResult(String result) {
        ArrayList<Post> postList = new ArrayList<Post>();
        if (TextUtils.isEmpty(result)) {
            return postList;
        }

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray posts = jsonObject.optJSONArray("posts");
            if (null == posts || posts.length() == 0) {
                Log.i(TAG, "parseListResult, got empty list");
            } else {
                final int len = posts.length();
                JSONObject obj;
                for (int i = 0; i < len; i++) {
                    obj = posts.getJSONObject(i);
                    postList.add(fromJson(obj));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return postList;
    }
    public static Post parseResult(String str) {
        try {
            JSONObject jsonObject = new JSONObject(str);
            return fromJson(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Post fromJson(JSONObject obj) {
        Post post = new Post();
        if (null != obj) {
            post.postId = obj.optString("post_id");
            post.uid = obj.optLong("uid");
            post.category = obj.optInt("categoryid");
            post.subCategory = obj.optInt("sub_categoryid");
            post.description = obj.optString("description");
            post.status = obj.optInt("status");
            post.createTime = obj.optLong("dateline");
        }
        return post;
    }

    // todo: orgnaize text.
    public String getPrice() {
        return TextUtils.isEmpty(price) ? "未知" : price;
    }

    public String getStatus() {
        return "未被抢";
    }

    public static final Parcelable.Creator<Post> CREATOR = new Creator<Post>() {
        public Post createFromParcel(Parcel source) {
            Post post = new Post();
            post.postId = source.readString();
            post.uid = source.readLong();
            post.category = source.readInt();
            post.subCategory = source.readInt();
            post.latitude = source.readLong();
            post.longitude = source.readLong();

            post.description = source.readString();
            post.address = source.readString();
            post.area = source.readString();
            post.brand = source.readString();
            post.contact = source.readString();
            post.mobile = source.readString();

            post.model = source.readString();
            post.createYear = source.readString();
            source.readStringArray(post.photos);
            post.createTime = source.readLong();
            post.updateTime = source.readLong();
            post.price = source.readString();
            post.status = source.readInt();
            return post;
        }
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(postId);
        parcel.writeLong(uid);  // source.readLong();
        parcel.writeInt(category);  // source.readInt();
        parcel.writeInt(subCategory);  // source.readInt();
        parcel.writeLong(latitude);  // source.readLong();
        parcel.writeLong(longitude);  // source.readLong();

        parcel.writeString(description);  // source.readString();
        parcel.writeString(address);  // source.readString();
        parcel.writeString(area);  // source.readString();
        parcel.writeString(brand);  // source.readString();
        parcel.writeString(contact);  // source.readString();
        parcel.writeString(mobile);  // source.readString();

        parcel.writeString(model);  // source.readString();
        parcel.writeString(createYear);  // source.readString();
        parcel.writeStringArray(photos);
        parcel.writeLong(createTime);  // source.readLong();
        parcel.writeLong(updateTime);  // source.readLong();
        parcel.writeString(price);  // source.readString();
        parcel.writeInt(status);  // source.readInt();
    }

    public Bundle toBundle() {
        Bundle args = new Bundle();
        args.putParcelable("POST", this);
        return args;
    }
    public static Post fromBundle(Bundle args) {
        if (null == args) {
            return new Post();
        }
        Post post = args.getParcelable("POST");
        if (null == post) {
            return new Post();
        } else {
            return post;
        }
    }
}
