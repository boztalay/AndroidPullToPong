package com.boztalay.pulltopongdemo;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.*;

import com.boztalay.pulltopong.PongHeaderTransformer;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class MainActivity extends Activity implements OnRefreshListener {

    private PullToRefreshLayout mPullToRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);
        ActionBarPullToRefresh.from(this)
                .options(Options.create()
                        .scrollDistance(0.5f)
                        .headerLayout(R.layout.pong_header)
                        .headerTransformer(new PongHeaderTransformer())
                        .build())
                .allChildrenArePullable()
                .listener(this)
                .setup(mPullToRefreshLayout);
    }

    public void onRefreshStarted(View view) {
        //Load some data!
    }

    public void stopRefreshingButtonPressed(View view) {
        mPullToRefreshLayout.setRefreshComplete();
    }
}