package com.boztalay.pulltopong;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by boztalay on 1/15/14.
 */
public class PongGamePieceView extends View {
    public PongGamePieceView(Context context) {
        this(context, null);
    }

    public PongGamePieceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PongGamePieceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setWidth(int width) {
        getLayoutParams().width = width;
    }

    public void setHeight(int height) {
        getLayoutParams().height = height;
    }

    public void setPositionByCenter(float centerX, float centerY) {
        float newX = centerX - (getWidthInPixels() / 2.0f);
        float newY = centerY - (getHeightInPixels() / 2.0f);

        setX(newX);
        setY(newY);
    }

    public AnimatorSet createAnimationsToSetPositionByCenter(PointF newCenter) {
        float newX = newCenter.x - getHalfWidth();
        float newY = newCenter.y - getHalfHeight();

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(this, "x", getX(), newX),
                ObjectAnimator.ofFloat(this, "y", getY(), newY));
        return animatorSet;
    }

    public float getCenterX() {
        return (getX() + getHalfWidth());
    }

    public float getCenterY() {
        return (getY() + getHalfHeight());
    }

    public float centerYWhenContactingCeiling(View parentView) {
        return getHalfHeight();
    }

    public float centerYWhenContactingFloor(View parentView) {
        return ((float)parentView.getHeight() - getHalfHeight());
    }

    public float centerXWhenContactingRightSideOf(PongGamePieceView otherView) {
        return (otherView.getX() + otherView.getWidthInPixels() + getHalfWidth());
    }

    public float centerXWhenContactingLeftSideOf(PongGamePieceView otherView) {
        return (otherView.getX() - getHalfWidth());
    }

    public float getHalfWidth() {
        return (getWidthInPixels() / 2.0f);
    }

    public float getHalfHeight() {
        return (getHeightInPixels() / 2.0f);
    }

    //These are here so I can easily change how I'm getting the width/height

    public float getWidthInPixels() {
        return getLayoutParams().width;
    }

    public float getHeightInPixels() {
        return getLayoutParams().height;
    }
}