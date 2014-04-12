package com.boztalay.pulltopong;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import uk.co.senab.actionbarpulltorefresh.library.HeaderTransformer;

/**
 * Created by boztalay on 1/14/14.
 */
public class PongHeaderTransformer extends HeaderTransformer {
    private static final long FADE_IN_OUT_DURATION = 150;

    private View mHeaderView;
    private PongPlayer mPongPlayer;

    @Override
    public void onViewCreated(Activity activity, View headerView) {
        mHeaderView = headerView;
        mPongPlayer = new PongPlayer(mHeaderView);
    }

    @Override
    public void onPulled(float percentagePulled) {
        mPongPlayer.onPulled(percentagePulled);
    }

    @Override
    public void onRefreshStarted() {
        mPongPlayer.startPlaying();
    }

    @Override
    public boolean showHeaderView() {
        final boolean changeVis = mHeaderView.getVisibility() != View.VISIBLE;
        if (changeVis) {
            mPongPlayer.resetGamePieces();
            mHeaderView.setVisibility(View.VISIBLE);
            ObjectAnimator.ofFloat(mHeaderView, "alpha", 0f, 1f).setDuration(FADE_IN_OUT_DURATION).start();
        }

        return changeVis;
    }

    @Override
    public boolean hideHeaderView() {
        final boolean changeVis = mHeaderView.getVisibility() == View.VISIBLE;
        if (changeVis) {
            Animator animator = ObjectAnimator.ofFloat(mHeaderView, "alpha", 1f, 0f);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mHeaderView.setVisibility(View.GONE);
                    mPongPlayer.stopPlaying();
                }
            });
            animator.setDuration(FADE_IN_OUT_DURATION).start();
        }

        return changeVis;
    }
}