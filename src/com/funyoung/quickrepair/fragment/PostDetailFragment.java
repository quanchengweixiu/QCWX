
package com.funyoung.quickrepair.fragment;

import android.app.ListFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.funyoung.qcwx.R;
import com.funyoung.quickrepair.MainActivity;
import com.funyoung.quickrepair.model.Post;
import com.funyoung.quickrepair.model.User;
import com.funyoung.quickrepair.transport.BillingClient;
import com.funyoung.quickrepair.utils.AsyncTaskUtils;
import com.funyoung.quickrepair.utils.PerformanceUtils;

import java.util.ArrayList;
import java.util.HashMap;

import baidumapsdk.demo.DemoApplication;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

public class PostDetailFragment extends BaseFragment {
    private static final String TAG = "PostDetailFragment";
//    private static final long SIMULATED_REFRESH_LENGTH = 5000;

    private Post mPost;

    private AsyncTask<Void, Void, String> mPreTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (null != args) {
            mPost = Post.fromBundle(args);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);
//        mPullToRefreshAttacher.addRefreshableView(getListView(), this);
        performPreTask();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
//        if (null == mPullToRefreshAttacher) {
//            mPullToRefreshAttacher = ((MainActivity) getActivity())
//                    .getPullToRefreshAttacher();
//            mPullToRefreshAttacher.addRefreshableView(getView(), this);
//        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mPreTask && mPreTask.getStatus() == AsyncTask.Status.RUNNING) {
            mPreTask.cancel(true);
        }
        mPreTask = null;
    }

    private void performPreTask() {
        if (null == mPreTask) {
            mPreTask = new AsyncTask<Void, Void, String>() {
                Post mResult;
                long startTime;
                @Override
                protected void onPreExecute() {
                    mResult = null;
                    startTime = System.currentTimeMillis();
                }

                @Override
                protected String doInBackground(Void... voids) {
                    try {
                        final HashMap<String, String> filter = null;
                        mResult = BillingClient.getBillInfo(getActivity(), mPost, filter);
                        return "getBillInfo succeed by " + mPost.postId;
                    } catch (Exception e) {
                        return "getBillInfo exception " + e.getMessage();
                    }
                }

                @Override
                protected void onPostExecute(String result) {
                    final long diff = PerformanceUtils.showTimeDiff(startTime, System.currentTimeMillis());
                    PerformanceUtils.showToast(getActivity(), result, diff);

                    if (mResult != null) {
                        Toast.makeText(getActivity(), R.string.list_posts_succeed, Toast.LENGTH_SHORT).show();
                        mPost = mResult;
                        refreshUi();
                    } else {
                        Toast.makeText(getActivity(), R.string.list_posts_fail, Toast.LENGTH_SHORT).show();
                    }
                }
            };
        }
        if (AsyncTaskUtils.isReadyToRun(mPreTask)) {
            mPreTask.execute();
        }
    }

    private String getContent(View hostView) {
        if (null != hostView) {
            TextView textView = (TextView)hostView.findViewById(R.id.tv_content);
            if (null != textView) {
                return textView.getText().toString();
            }
        }
        return "";
    }

    private void refreshUi() {
        if (null == mPost) {
            // fill in with debug data
        } else {
            // todo: inflat data to ui.
        }
    }

    private static String formatDate(Context context, long time) {
        return context.getString(R.string.post_item_time, DateUtils.formatDateTime(context, time,
                DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_TIME));
    }

//    private PullToRefreshAttacher mPullToRefreshAttacher;
//
//    @Override
//    public void onRefreshStarted(View view) {
//        /**
//         * Simulate Refresh with 4 seconds sleep
//         */
//        new AsyncTask<Void, Void, Void>() {
//
//            @Override
//            protected Void doInBackground(Void... params) {
//                try {
//                    Thread.sleep(SIMULATED_REFRESH_LENGTH);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void result) {
//                super.onPostExecute(result);
//
//                // Notify PullToRefreshAttacher that the refresh has finished
//                mPullToRefreshAttacher.setRefreshComplete();
//            }
//        }.execute();
//    }

    public void updateArguments(Bundle args) {
        mPost = Post.fromBundle(args);
        refreshUi();
    }
}