package com.example.mikael.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Created by mikael on 11/14/17.
 */

public class GameLoop implements Runnable {
    private boolean isRunning = false;
    private Thread gameThread;
    private SurfaceHolder holder;
    private final static int MAX_FPS = 30; //desired fps
    private final static int FRAME_PERIOD = 1000 / MAX_FPS;
    private GameScene scene;
    private int maxSleepTime = FRAME_PERIOD;
    private int minSleepTime = 0;

    public GameLoop(GameScene scene) {
        this.scene = scene;
    }

    public void resume() {
        isRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * Pause the game loop
     */
    public void pause() {
        isRunning = false;
        boolean retry = true;
        while (retry) {
            try {
                gameThread.join();
                retry = false;
            } catch (InterruptedException e) {
                // try again shutting down the thread
            }
        }
    }

    public void run() {
        while(isRunning) {

            long started = System.currentTimeMillis();

            // update
            scene.update();
            // draw
            scene.draw();

            float deltaTime = (System.currentTimeMillis() - started);
            int sleepTime = (int) (FRAME_PERIOD - deltaTime);

            if (minSleepTime > sleepTime){
                minSleepTime = sleepTime;
            }
            if (maxSleepTime < sleepTime) {
                maxSleepTime = sleepTime;
            }

            if (sleepTime > 0) {
                try {
                    gameThread.sleep(sleepTime);
                }
                catch (InterruptedException e) {
                }
            }
            //Log.d("SLEEPTIME", "" + sleepTime);
        }
    }

}
