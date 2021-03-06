package com.funyoung.quickrepair.fragment;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.funyoung.qcwx.R;
import com.funyoung.quickrepair.MainActivity;
import com.funyoung.quickrepair.model.User;

import baidumapsdk.demo.DemoApplication;

/**
 * Created by yangfeng on 13-7-2.
 */
public class BaseFragment extends Fragment {
    public static class FragmentSession {
        public static final int CATEGORY = 0;
        public static final int SELECTION = 1;
        public static final int LOGIN = 2;
        public static final int SETTINGS = 3;
        public static final int POSTED = 4;
        public static final int PROFILE = 5;
        public static final int FRAGMENT_COUNT = SETTINGS + 1;

        public int mState;
        public FragmentSession(Context context, int state) {
            mState = state;
        }
    }

    protected void invokeSession(int state, Exception exception) {
        Context context = getActivity();
        FragmentSession session = new FragmentSession(context, state);
        MainActivity.invoke(context, session, null, exception);
    }

    protected User getCurrentUser() {
        return DemoApplication.getInstance().getLoginUser();
    }
    protected boolean isMySelf(long uid) {
        User user = getCurrentUser();
        if (null != user && user.getUid() == uid) {
            return true;
        }
        return false;
    }
    protected boolean isServiceProvider() {
        User user = getCurrentUser();
        if (null != user) {
            if (user.isProviderType()) {
                return true;
            }
        }
        return false;
    }
}

abstract class UserFragment extends BaseFragment {
    protected View mRootView;
    protected EditText mNameView;
    protected EditText mPasswordView;
    protected TextView mLoginButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(getLayoutRes(), container, false);

        mLoginButton = (TextView)mRootView.findViewById(R.id.login_button);
        if (null != mLoginButton) {
            View.OnClickListener listener = getClickListener();
            if (null != listener) {
                mLoginButton.setOnClickListener(listener);
            }
        }

        mNameView = (EditText)mRootView.findViewById(R.id.user_name);
        mPasswordView = (EditText)mRootView.findViewById(R.id.user_password);

        onCreateViewFinish(mRootView);
        return mRootView;
    }
    abstract protected int getLayoutRes();
    protected View.OnClickListener getClickListener() {
        return null;
    }
    protected void onCreateViewFinish(View rootView) {
    }
    protected String validateUserName() {
        String name = null == mNameView ? null : mNameView.getText().toString();
        if (TextUtils.isEmpty(name)) {
            mNameView.requestFocus();
            return null;
        }
        return name;
    }

    protected String validatePassword() {
        String passwd = null == mPasswordView ? null : mPasswordView.getText().toString();
        if (TextUtils.isEmpty(passwd)) {
            mPasswordView.requestFocus();
            return null;
        }
        return passwd;
    }

    protected void setNameViewLabel(String name) {
        if (null != mNameView) {
            mNameView.setText(name);
            mNameView.selectAll();
            mNameView.requestFocus();
        }
    }
}
