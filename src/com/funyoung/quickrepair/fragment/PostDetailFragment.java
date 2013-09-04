
package com.funyoung.quickrepair.fragment;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Config;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.funyoung.qcwx.R;
import com.funyoung.quickrepair.DeployConfig;
import com.funyoung.quickrepair.MainActivity;
import com.funyoung.quickrepair.model.Post;
import com.funyoung.quickrepair.model.User;
import com.funyoung.quickrepair.transport.BillingClient;
import com.funyoung.quickrepair.utils.AsyncTaskUtils;
import com.funyoung.quickrepair.utils.DialogUtils;
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
    private AsyncTask<Void, Void, String> mTakeTask;

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
        initViews(view);
        performPreTask();
        return view;
    }

    private void initViews(View view) {
        if (null == view) {
            return;
        }
        initTakePostView(view);
        initCommentView(view);
    }
    private void initCommentView(View view) {
        if (null == view) {
            return;
        }

        View subView = view.findViewById(R.id.comment_l);
        if (null != subView) {
            subView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addComment();
                }
            });
        }
    }

    private void addComment() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText editText = new EditText(getActivity());
        editText.setHint(R.string.post_comment_hint);
        builder.setTitle(R.string.qp_post_write_comment);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String comment = editText.getText().toString();
                if (TextUtils.isEmpty(comment)) {
                    // todo : show too short text toas
                } else {
                    performCommentTask(comment);
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // todo: show add comment ui.
    private void performCommentTask(String comment) {
        DialogUtils.showConfirmDialog(getActivity(), "Comment", comment, null);
    }

    private void initTakePostView(View view) {
        if (null == view) {
            return;
        }

        View subView = view.findViewById(R.id.tv_order);
        if (null != subView) {
            if (DeployConfig.DEBUG_BOTH_USER_TYPE || isServiceProvider()) {
                subView.setVisibility(View.VISIBLE);
                subView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        takeThePost();
                    }
                });
            } else {
                subView.setVisibility(View.GONE);
                subView.setOnClickListener(null);
            }

        }
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
        if (null != mTakeTask && mTakeTask.getStatus() == AsyncTask.Status.RUNNING) {
            mTakeTask.cancel(true);
        }
        mPreTask = null;
        mTakeTask = null;
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
                        Toast.makeText(getActivity(), R.string.get_post_succeed, Toast.LENGTH_SHORT).show();
                        mPost = mResult;
                        refreshUi();
                    } else {
                        Toast.makeText(getActivity(), R.string.get_post_fail, Toast.LENGTH_SHORT).show();
                    }
                }
            };
        }
        if (AsyncTaskUtils.isReadyToRun(mPreTask)) {
            mPreTask.execute();
        }
    }

    private void performTakeTask() {
        if (null == mTakeTask) {
            mTakeTask = new AsyncTask<Void, Void, String>() {
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
//                        final HashMap<String, String> filter = null;
                        mResult = BillingClient.scrambleBill(getActivity(), mPost);
                        return "scrambleBill succeed by " + mPost.postId;
                    } catch (Exception e) {
                        return "scrambleBill exception " + e.getMessage();
                    }
                }

                @Override
                protected void onPostExecute(String result) {
                    final long diff = PerformanceUtils.showTimeDiff(startTime, System.currentTimeMillis());
                    PerformanceUtils.showToast(getActivity(), result, diff);

                    if (mResult != null) {
                        Toast.makeText(getActivity(), R.string.take_post_succeed, Toast.LENGTH_SHORT).show();
                        mPost = mResult;
                        refreshUi();
                    } else {
                        Toast.makeText(getActivity(), R.string.take_post_fail, Toast.LENGTH_SHORT).show();
                    }
                }
            };
        }
        if (AsyncTaskUtils.isReadyToRun(mTakeTask)) {
            mTakeTask.execute();
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

    private void takeThePost() {
        if (null == mPost) {
            Log.e(TAG, "takeThePost, skip null post");
            return;
        }
        performTakeTask();
    }
}