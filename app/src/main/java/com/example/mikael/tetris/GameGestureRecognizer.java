package com.example.mikael.tetris;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import java.util.Vector;

/**
 * Created by mikael on 11/14/17.
 */

public class GameGestureRecognizer extends GestureDetector.SimpleOnGestureListener {
    GameScene scene;
    float accumulatedDistanceX = 0;
    float accumulatedDistanceY = 0;
    float distanceSinceLastScrollX = 0;
    float distanceSinceLastScrollY = 0;

    public GameGestureRecognizer(GameScene scene) {
        this.scene = scene;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        //Log.d("GameGestureRecognizer", "onDown");

        accumulatedDistanceX = 0;
        accumulatedDistanceY = 0;
        distanceSinceLastScrollX = 0;
        distanceSinceLastScrollY = 0;
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        //Log.d("GameGestureRecognizer", "onFling");
        return true;
    }

        @Override
    public boolean onSingleTapUp(MotionEvent e) {
        //Log.d("GameGestureRecognizer", "onSingleTapUp");

        UserEventCreator.UserEvent event = null;
        if (e.getX() < TetrisUtils.getInstance().getGameWidth() / 2) {
            event = UserEventCreator.getInstance().makeRotateLeftEvent();
        }
        else {
            event = UserEventCreator.getInstance().makeRotateRightEvent();
        }

        if (event != null) {
            scene.userEvents.add(event);
        }

        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        accumulatedDistanceX += distanceX;
        accumulatedDistanceY += distanceY;
        distanceSinceLastScrollX += distanceX;
        distanceSinceLastScrollY += distanceY;
        UserEventCreator.UserEvent event = null;
        if (Math.abs(accumulatedDistanceX) > Math.abs(accumulatedDistanceY)) {
            if (Math.abs(distanceSinceLastScrollX) >= TetrisUtils.getInstance().getBlockSize()) {
                if (distanceSinceLastScrollX > 0) {
                    event = UserEventCreator.getInstance().makeMoveLeftEvent();
                } else {
                    event = UserEventCreator.getInstance().makeMoveRightEvent();
                }
                distanceSinceLastScrollX = 0;
            }
        }
        else {
            if (Math.abs(distanceSinceLastScrollY) >= TetrisUtils.getInstance().getBlockSize()) {
                event = UserEventCreator.getInstance().makeScrollEvent((int)distanceY);
                distanceSinceLastScrollY = 0;
            }
        }
        if (event != null) {
            scene.userEvents.add(event);
            if (scene.userEvents.size() > 10) {
                int index = 0;
                scene.userEvents.poll();
            }
        }

        return true;
    }

    public boolean onScroll2(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.d("GameGestureRecognizer", "onScroll");

        boolean isHorizontalScroll = false;
        float totalDistanceX = Math.abs(e1.getX() - e2.getX());
        float totalDistanceY = Math.abs(e1.getY() - e2.getY());
        if (e1.getX() > e1.getY()) {
            isHorizontalScroll = true;
        }
        UserEventCreator.UserEvent event = null;
        accumulatedDistanceX += distanceX;
        accumulatedDistanceY += distanceY;

        if (isHorizontalScroll) {
            if (accumulatedDistanceX > TetrisUtils.getInstance().getBlockSize()) {
                event = UserEventCreator.getInstance().makeMoveLeftEvent();
                accumulatedDistanceX = 0;
            } else if (accumulatedDistanceX < -TetrisUtils.getInstance().getBlockSize()) {
                event = UserEventCreator.getInstance().makeMoveRightEvent();
                accumulatedDistanceX = 0;
                Log.d("GameGestureRecognizer", "makeMoveRightEvent");
            }
        }
        else {
            event = UserEventCreator.getInstance().makeScrollEvent((int)distanceY);
            scene.userEvents.add(event);
        }

        if (event != null) {
            scene.userEvents.add(event);
        }

        return true;
    }

    @Override
    public boolean onDoubleTapEvent (MotionEvent e) {
        Log.d("GameGestureRecognizer", "onDoubleTapEvent");

        return true;
    }

    @Override
    public boolean onContextClick (MotionEvent e) {
        Log.d("GameGestureRecognizer", "onContextClick");
        return true;
    }
}
