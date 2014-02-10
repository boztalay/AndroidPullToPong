package com.boztalay.pulltopongdemo;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by boztalay on 1/18/14.
 */
public class MainFragment extends Fragment implements OnRefreshListener {

    private PullToRefreshLayout mPullToRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ViewGroup container = (ViewGroup) view;

        mPullToRefreshLayout = new PullToRefreshLayout(container.getContext());
        ActionBarPullToRefresh.from(getActivity())
//                .options(Options.create()
//                        .scrollDistance(0.5f)
//                        .headerLayout(R.layout.pong_header)
//                        .headerTransformer(new PongHeaderTransformer())
//                        .build())
                .insertLayoutInto(container)
                .allChildrenArePullable()
                .listener(this)
                .setup(mPullToRefreshLayout);
    }

    public void onRefreshStarted(View view) {

    }
}