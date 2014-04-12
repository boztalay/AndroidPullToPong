package com.boztalay.pulltopong;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;

import com.boztalay.pulltopongdemo.R;

import java.util.Random;

/**
 * Created by boztalay on 1/15/14.
 */
public class PongPlayer {
    private final static int DEFAULT_GAME_COLOR = Color.WHITE;
    private final static int DEFAULT_HORIZONTAL_TRAVEL_TIME = 350;

    private boolean isReadyToPlay;
    private AnimatorSet currentAnimator;
    private Random rand;

    private View parentView;
    private PongGamePieceView leftPaddleView;
    private PongGamePieceView rightPaddleView;
    private PongGamePieceView ballView;

    private PointF ballOrigin;
    private PointF ballDestination;
    private PointF ballDirection;

    private PointF leftPaddleDestination;
    private PointF rightPaddleDestination;

    public PongPlayer(View parentView) {
        isReadyToPlay = false;
        rand = new Random();

        this.parentView = parentView;

        leftPaddleView = (PongGamePieceView)parentView.findViewById(R.id.pong_left_paddle);
        leftPaddleView.setBackgroundColor(DEFAULT_GAME_COLOR);

        rightPaddleView = (PongGamePieceView)parentView.findViewById(R.id.pong_right_paddle);
        rightPaddleView.setBackgroundColor(DEFAULT_GAME_COLOR);

        ballView = (PongGamePieceView)parentView.findViewById(R.id.pong_ball);
        ballView.setBackgroundColor(DEFAULT_GAME_COLOR);

        ballOrigin = new PointF();
        ballDestination = new PointF();
        ballDirection = new PointF();

        leftPaddleDestination = new PointF();
        rightPaddleDestination = new PointF();
    }

    public void onPulled(float percentagePulled) {
        float amountToRotate = (float)(180.0 * percentagePulled);
        leftPaddleView.setRotation(amountToRotate);
        rightPaddleView.setRotation(-amountToRotate);
    }

    public void resetGamePieces() {
        Log.d("PongPlayer", "resetGamePieces");

        ballView.setPositionByCenter(getGameFieldCenterX(), getGameFieldCenterY());

        int paddleHeight = parentView.getHeight() / 3;
        float leftPaddleX = ((float)parentView.getWidth() * 0.25f);
        leftPaddleView.setHeight(paddleHeight);
        leftPaddleView.setPositionByCenter(leftPaddleX, getGameFieldCenterY());
        leftPaddleView.setRotation(0.0f);
        leftPaddleDestination.set(leftPaddleX, getGameFieldCenterY());

        float rightPaddleX = ((float)parentView.getWidth() * 0.75f);
        rightPaddleView.setHeight(paddleHeight);
        rightPaddleView.setPositionByCenter(rightPaddleX, getGameFieldCenterY());
        rightPaddleView.setRotation(0.0f);
        rightPaddleDestination.set(rightPaddleX, getGameFieldCenterY());

        if(!isReadyToPlay) {
            isReadyToPlay = true;
        }
    }

    public void stopPlaying() {
        if(currentAnimator != null) {
            currentAnimator.removeAllListeners();
            currentAnimator.cancel();
        }

        leftPaddleView.clearAnimation();
        rightPaddleView.clearAnimation();
        ballView.clearAnimation();
    }

    public void startPlaying() {
        Log.d("PongPlayer", "startPlaying");

        if(isReadyToPlay) {
            resetPaddleRotations();

            ballOrigin.set(getGameFieldCenterX(), getGameFieldCenterY());

            pickRandomStartingBallDestination();
            determineNextPaddleDestinations();
            animateBallAndPaddlesToDestinations();
        } else {
            parentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < 16) {
                        parentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        parentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }

                    resetGamePieces();
                    startPlaying();
                }
            });
        }
    }

    private void resetPaddleRotations() {
        leftPaddleView.setRotation(0.0f);
        rightPaddleView.setRotation(0.0f);
    }

    //BALL

    private void pickRandomStartingBallDestination() {
        float destinationX = ballView.centerXWhenContactingRightSideOf(leftPaddleView);
        if(rand.nextBoolean()) {
            destinationX = ballView.centerXWhenContactingLeftSideOf(rightPaddleView);
        }

        float destinationY = randomFloatBetween(0.0f, parentView.getHeight() - 30.0f) + 15.0f;

        ballDestination.set(destinationX, destinationY);
        ballDirection.set((ballDestination.x - ballOrigin.x), (ballDestination.y - ballOrigin.y));
        normalizeVector(ballDirection);
    }

    private void determineNextBallDestination() {
        float newBallDestinationX;
        float newBallDestinationY;

        reflectBallDirection();

        float verticalDistanceToNextWall = calculateVerticalDistanceFromBallToNextWall();
        float distanceToNextWall = verticalDistanceToNextWall / ballDirection.y;
        float horizontalDistanceToNextWall = distanceToNextWall * ballDirection.x;

        float horizontalDistanceToNextPaddle = calculateHorizontalDistanceFromBallToNextPaddle();

        if(Math.abs(horizontalDistanceToNextPaddle) < Math.abs(horizontalDistanceToNextWall)) {
            newBallDestinationX = ballDestination.x + horizontalDistanceToNextPaddle;

            float verticalDistanceToNextPaddle = Math.abs(horizontalDistanceToNextPaddle) * ballDirection.y;
            newBallDestinationY = ballDestination.y + verticalDistanceToNextPaddle;
        } else {
            newBallDestinationX = ballDestination.x + horizontalDistanceToNextWall;
            newBallDestinationY = ballDestination.y + verticalDistanceToNextWall;
        }

        ballOrigin.set(ballDestination);
        ballDestination.set(newBallDestinationX, newBallDestinationY);
    }

    private void reflectBallDirection() {
        if(didBallHitWall()) {
            ballDirection.set(ballDirection.x, -ballDirection.y);
        } else if(didBallHitPaddle()) {
            ballDirection.set(-ballDirection.x, ballDirection.y);
        }
    }

    private boolean didBallHitWall() {
        return (areFloatsEqual(ballDestination.y, ballView.centerYWhenContactingCeiling(parentView)) ||
                areFloatsEqual(ballDestination.y, ballView.centerYWhenContactingFloor(parentView)));
    }

    private boolean didBallHitPaddle() {
        return (areFloatsEqual(ballDestination.x, ballView.centerXWhenContactingRightSideOf(leftPaddleView)) ||
                areFloatsEqual(ballDestination.x, ballView.centerXWhenContactingLeftSideOf(rightPaddleView)));
    }

    private float calculateVerticalDistanceFromBallToNextWall() {
        if(ballDirection.y > 0.0f) {
            return (ballView.centerYWhenContactingFloor(parentView) - ballDestination.y);
        } else {
            return (ballView.centerYWhenContactingCeiling(parentView) - ballDestination.y);
        }
    }

    private float calculateHorizontalDistanceFromBallToNextPaddle() {
        if(ballDirection.x < 0.0f) {
            return (ballView.centerXWhenContactingRightSideOf(leftPaddleView) - ballDestination.x);
        } else {
            return (ballView.centerXWhenContactingLeftSideOf(rightPaddleView) - ballDestination.x);
        }
    }

    //PADDLES

    private void determineNextPaddleDestinations() {
        float lazySpeedFactor = 0.25f;
        float normalSpeedFactor = 0.5f;
        float holyCrapSpeedFactor = 1.0f;

        float leftPaddleVerticalDistanceToBallDestination = ballDestination.y - leftPaddleView.getCenterY();
        float rightPaddleVerticalDistanceToBallDestination = ballDestination.y - rightPaddleView.getCenterY();

        float leftPaddleOffset;
        float rightPaddleOffset;

        //Determining how far each paddle will mode
        if(ballDirection.x < 0.0f) {
            //Ball is going toward the left paddle

            if(isBallDestinationIsTheLeftPaddle()) {
                leftPaddleOffset = (leftPaddleVerticalDistanceToBallDestination * holyCrapSpeedFactor);
                rightPaddleOffset = (rightPaddleVerticalDistanceToBallDestination * lazySpeedFactor);
            } else {
                //Destination is a wall
                leftPaddleOffset = (leftPaddleVerticalDistanceToBallDestination * normalSpeedFactor);
                rightPaddleOffset = -(rightPaddleVerticalDistanceToBallDestination * normalSpeedFactor);
            }
        } else {
            //Ball is going toward the right paddle

            if(isBallDestinationIsTheRightPaddle()) {
                leftPaddleOffset = (leftPaddleVerticalDistanceToBallDestination * lazySpeedFactor);
                rightPaddleOffset = (rightPaddleVerticalDistanceToBallDestination * holyCrapSpeedFactor);
            } else {
                //Destination is a wall
                leftPaddleOffset = -(leftPaddleVerticalDistanceToBallDestination * normalSpeedFactor);
                rightPaddleOffset = (rightPaddleVerticalDistanceToBallDestination * normalSpeedFactor);
            }
        }

        leftPaddleDestination.set(leftPaddleDestination.x, leftPaddleView.getCenterY() + leftPaddleOffset);
        rightPaddleDestination.set(rightPaddleDestination.x, rightPaddleView.getCenterY() + rightPaddleOffset);

        capPaddleDestinationsToWalls();
    }

    private boolean isBallDestinationIsTheLeftPaddle() {
        return areFloatsEqual(ballDestination.x, ballView.centerXWhenContactingRightSideOf(leftPaddleView));
    }

    private boolean isBallDestinationIsTheRightPaddle() {
        return areFloatsEqual(ballDestination.x, ballView.centerXWhenContactingLeftSideOf(rightPaddleView));
    }

    private void capPaddleDestinationsToWalls() {
        if(leftPaddleDestination.y < leftPaddleView.centerYWhenContactingCeiling(parentView)) {
            leftPaddleDestination.y = leftPaddleView.centerYWhenContactingCeiling(parentView);
        } else if(leftPaddleDestination.y > leftPaddleView.centerYWhenContactingFloor(parentView)) {
            leftPaddleDestination.y = leftPaddleView.centerYWhenContactingFloor(parentView);
        }

        if(rightPaddleDestination.y < rightPaddleView.centerYWhenContactingCeiling(parentView)) {
            rightPaddleDestination.y = rightPaddleView.centerYWhenContactingCeiling(parentView);
        } else if(rightPaddleDestination.y > rightPaddleView.centerYWhenContactingFloor(parentView)) {
            rightPaddleDestination.y = rightPaddleView.centerYWhenContactingFloor(parentView);
        }
    }

    //ANIMATING

    private void animateBallAndPaddlesToDestinations() {
        currentAnimator = new AnimatorSet();
        currentAnimator.playTogether(leftPaddleView.createAnimationsToSetPositionByCenter(leftPaddleDestination),
                rightPaddleView.createAnimationsToSetPositionByCenter(rightPaddleDestination),
                ballView.createAnimationsToSetPositionByCenter(ballDestination));

        float endToEndDistance = ballView.centerXWhenContactingLeftSideOf(rightPaddleView) - ballView.centerXWhenContactingRightSideOf(leftPaddleView);
        float horizontalDistanceBallWillTravel = Math.abs(ballDestination.x - ballOrigin.x);
        float proportionOfEndToEndDistanceBallWillTravel = horizontalDistanceBallWillTravel / endToEndDistance;
        long animationDuration = (long)((float)DEFAULT_HORIZONTAL_TRAVEL_TIME * proportionOfEndToEndDistanceBallWillTravel);
        currentAnimator.setDuration(animationDuration);
        currentAnimator.setInterpolator(new LinearInterpolator());

        currentAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                determineNextBallDestination();
                determineNextPaddleDestinations();
                animateBallAndPaddlesToDestinations();
            }

            @Override
            public void onAnimationStart(Animator animator) {}
            @Override
            public void onAnimationCancel(Animator animator) {}
            @Override
            public void onAnimationRepeat(Animator animator) {}
        });

        currentAnimator.start();
    }

    private float getGameFieldCenterX() {
        return ((float)parentView.getWidth() / 2.0f);
    }

    private float getGameFieldCenterY() {
        return ((float)parentView.getHeight() / 2.0f);
    }

    //Etc

    private float randomFloatBetween(float min, float max) {
        return ((rand.nextFloat() * (max - min)) + min);
    }

    private void normalizeVector(PointF vector) {
        float magnitude = (float)Math.sqrt((double)(vector.x * vector.x + vector.y * vector.y));
        vector.set(vector.x / magnitude, vector.y / magnitude);
    }

    private boolean areFloatsEqual(float num1, float num2) {
        float ellipsis = 0.01f;
        return (Math.abs(num1 - num2) < ellipsis);
    }
}
