package com.funyoung.quickrepair.fragment;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.funyoung.qcwx.R;
import com.funyoung.quickrepair.MainActivity;
import com.funyoung.view.OnTextFragmentAnimationEndListener;

import java.util.ArrayList;
import java.util.Arrays;

public class TextFragment extends Fragment {
    public static final String ARG_CAT_ID = "ARG_CAT_ID";
    public static final String ARG_CAT_LABEL = "ARG_CAT_LABEL";
    private static final String DUMB_LABEL = "NA";

    View.OnClickListener clickListener;
    OnTextFragmentAnimationEndListener mListener;

    private View rootView;
    private ArrayList<String> mSubCategory = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    private int mMainId = 0;
    private String mMainLabel;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.text_fragment, container, false);
        rootView.setOnClickListener(clickListener);

        Bundle bundle = getArguments();
        if (null != bundle) {
            mMainId = bundle.getInt(ARG_CAT_ID, 0);
            mMainLabel = bundle.getString(ARG_CAT_LABEL, DUMB_LABEL);
        }
        initSubCategory();
        return rootView;
    }

    public void setClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim)
    {
        int id = enter ? R.animator.slide_fragment_in : R.animator.slide_fragment_out;
        final Animator anim = AnimatorInflater.loadAnimator(getActivity(), id);
        if (enter) {
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mListener.onAnimationEnd();
                }
            });
        }
        return anim;
    }

    public void setOnTextFragmentAnimationEnd(OnTextFragmentAnimationEndListener listener)
    {
        mListener = listener;
    }

    private void initSubCategory() {
        String[] subLabels = getResources().getStringArray(MainActivity.subLabelArray[mMainId]);
        mSubCategory.clear();
        mSubCategory.addAll(Arrays.asList(subLabels));

        GridView gridView = (GridView) rootView.findViewById(R.id.ptr_gridview_sub);
        adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.gridview_item_category_sub,
                mSubCategory);

//        SimpleAdapter adapter = new SimpleAdapter(getActivity(),
//                itemData,
//                R.layout.gridview_item_category,
//                new String[] { "img", "label" },
//                new int[] { R.id.img, R.id.label });

        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int[] subIds = getResources().getIntArray(MainActivity.subIdArray[mMainId]);
                ((MainActivity)getActivity()).startPost(mMainId + 1, subIds[i],
                        mMainLabel, mSubCategory.get(i));
            }
        });
    }

    public void setArguments(int id, String label) {
        Bundle args = new Bundle();
        args.putInt(ARG_CAT_ID, id);
        args.putString(ARG_CAT_LABEL, null == label ? DUMB_LABEL : label);
        setArguments(args);
    }

    public void updateArgs(int id, String label) {
        mMainId = id;
        mMainLabel = null == label ? DUMB_LABEL : label;
        String[] subLabels = getResources().getStringArray(MainActivity.subLabelArray[mMainId]);
        mSubCategory.clear();
        mSubCategory.addAll(Arrays.asList(subLabels));
        adapter.notifyDataSetChanged();
    }
}
