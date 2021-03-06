
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

public class PostListFragment extends ListFragment implements
        PullToRefreshAttacher.OnRefreshListener {
    private static final String TAG = "PostListFragment";
    private static final long SIMULATED_REFRESH_LENGTH = 5000;

    private User mUser;
    SimpleAdapter adapter;
    private ArrayList<Post> mPostList = new ArrayList<Post>();

    private AsyncTask<Void, Void, String> mPreTask;
    ArrayList<HashMap<String, Object>> itemData = new ArrayList<HashMap<String, Object>>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUser = ((DemoApplication)getActivity().getApplication()).getLoginUser();
        /**
         * Get ListView and give it an adapter to display the sample items
         */
//        ListAdapter adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
//                ITEMS);

        adapter = new SimpleAdapter(getActivity(),
                itemData,
                R.layout.list_item_post,
                new String[] { "img", "label", "time", "description", "price", "status" },
                new int[] { R.id.img, R.id.label, R.id.time, R.id.description, R.id.price, R.id.status });
        setListAdapter(adapter);

        /**
         * Here we create a PullToRefreshAttacher manually without an Options instance.
         * PullToRefreshAttacher will manually create one using default values.
         */
//            mPullToRefreshAttacher = PullToRefreshAttacher.get(getActivity());
//        mPullToRefreshAttacher = ((MainActivity) getActivity())
//                .getPullToRefreshAttacher();
        // Set the Refreshable View to be the ListView and the refresh listener to be this.
//        mPullToRefreshAttacher.addRefreshableView(getListView(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
//        mPullToRefreshAttacher.addRefreshableView(getListView(), this);
        performPreTask();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (null == mPullToRefreshAttacher) {
            mPullToRefreshAttacher = ((MainActivity) getActivity())
                    .getPullToRefreshAttacher();
            mPullToRefreshAttacher.addRefreshableView(getListView(), this);
        }
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
                ArrayList<Post> mResult;
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
                        mResult = BillingClient.listBill(getActivity(), mUser, filter);
                        return "listBill succeed by " + getCurrentUserName();
                    } catch (Exception e) {
                        return "listBill exception " + e.getMessage();
                    }
                }

                @Override
                protected void onPostExecute(String result) {
                    final long diff = PerformanceUtils.showTimeDiff(startTime, System.currentTimeMillis());
                    PerformanceUtils.showToast(getActivity(), result, diff);

                    mPostList.clear();
                    if (mResult != null && !mResult.isEmpty()) {
                        Toast.makeText(getActivity(), R.string.list_posts_succeed, Toast.LENGTH_SHORT).show();
                        mPostList.addAll(mResult);
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

    private String getCurrentUserName() {
        if (null == mUser) return "null";
        return mUser.getNickName();
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

    // new String[] { "img", "label", "time", "description", "price" },
    private void refreshUi() {
        itemData.clear();
        if (null == mPostList || mPostList.isEmpty()) {
            // fill in with debug data
        } else {
            HashMap<String, Object> item;
            for (Post post : mPostList) {
                item = new HashMap<String, Object>();
                item.put("img", MainActivity.images[post.category]);
                item.put("label", post.area);
                item.put("time", formatDate(getActivity(), post.createTime));
                item.put("description", post.description);
                item.put("price", getString(R.string.post_item_price, post.getPrice()));
                item.put("status", getString(R.string.post_item_status, post.getStatus()));
                itemData.add(item);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private static String formatDate(Context context, long time) {
        return context.getString(R.string.post_item_time, DateUtils.formatDateTime(context, time,
                DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_TIME));
    }

    private PullToRefreshAttacher mPullToRefreshAttacher;

    @Override
    public void onRefreshStarted(View view) {
        /**
         * Simulate Refresh with 4 seconds sleep
         */
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(SIMULATED_REFRESH_LENGTH);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);

                // Notify PullToRefreshAttacher that the refresh has finished
                mPullToRefreshAttacher.setRefreshComplete();
            }
        }.execute();
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        if (mPostList.isEmpty()) {
            Log.e(TAG, "onListItemClick, error with empty post list");
            return;
        } else if (position < 0 || position >= mPostList.size()) {
            Log.e(TAG, "onListItemClick, error with invalid position " + position);
        } else {
            Post post = mPostList.get(position);
            FragmentFactory.getInstance(getActivity()).gotoPostDetailFragment(post.toBundle());
        }
    }
}