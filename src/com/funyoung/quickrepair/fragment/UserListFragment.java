
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

import com.baidu.mapapi.map.LocationData;
import com.funyoung.qcwx.R;
import com.funyoung.quickrepair.MainActivity;
import com.funyoung.quickrepair.model.ServiceProvider;
import com.funyoung.quickrepair.model.User;
import com.funyoung.quickrepair.transport.UsersClient;
import com.funyoung.quickrepair.utils.AsyncTaskUtils;
import com.funyoung.quickrepair.utils.PerformanceUtils;

import java.util.ArrayList;
import java.util.HashMap;

import baidumapsdk.demo.DemoApplication;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

public class UserListFragment extends ListFragment implements
        PullToRefreshAttacher.OnRefreshListener {
    private static final String TAG = "UserListFragment";
    private static final long SIMULATED_REFRESH_LENGTH = 5000;

    private long mDisSlop = 1000;     // a kilometer ?
    private LocationData mLocation;
    SimpleAdapter adapter;
    private ArrayList<ServiceProvider> mUserList = new ArrayList<ServiceProvider>();

    private AsyncTask<Void, Void, String> mPreTask;
    ArrayList<HashMap<String, Object>> itemData = new ArrayList<HashMap<String, Object>>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocation = ((DemoApplication)getActivity().getApplication()).getLocation();
        /**
         * Get ListView and give it an adapter to display the sample items
         */
//        ListAdapter adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
//                ITEMS);

        adapter = new SimpleAdapter(getActivity(),
                itemData,
                R.layout.list_item_user,
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
                ArrayList<ServiceProvider> mResult;
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
                                          mResult = UsersClient.getDisUsers(getActivity(),
                                                  mLocation, mDisSlop, filter);
                        return "listBill succeed by " + mLocation.toString();
                    } catch (Exception e) {
                        return "listBill exception " + e.getMessage();
                    }
                }

                @Override
                protected void onPostExecute(String result) {
                    final long diff = PerformanceUtils.showTimeDiff(startTime, System.currentTimeMillis());
                    PerformanceUtils.showToast(getActivity(), result, diff);

                    mUserList.clear();
                    if (mResult != null && !mResult.isEmpty()) {
                        Toast.makeText(getActivity(), R.string.list_posts_succeed, Toast.LENGTH_SHORT).show();
                        mUserList.addAll(mResult);
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

    // new String[] { "img", "label", "time", "description", "price" },
    private void refreshUi() {
        itemData.clear();
        if (null == mUserList || mUserList.isEmpty()) {
            // fill in with debug data
        } else {
            HashMap<String, Object> item;
            for (ServiceProvider user : mUserList) {
                item = new HashMap<String, Object>();
                item.put("img", user.getAvatarUrl());
                item.put("label", user.getNickName());
                item.put("time", user.getGender());
                item.put("description", user.getMobile());
                item.put("price", user.getUid());
                item.put("status", user.getMobile());
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
        if (mUserList.isEmpty()) {
            Log.e(TAG, "onListItemClick, error with empty post list");
            return;
        } else if (position < 0 || position >= mUserList.size()) {
            Log.e(TAG, "onListItemClick, error with invalid position " + position);
        } else {
            ServiceProvider post = mUserList.get(position);
            FragmentFactory.getInstance(getActivity()).gotoPostDetailFragment(post.toBundle());
        }
    }
}