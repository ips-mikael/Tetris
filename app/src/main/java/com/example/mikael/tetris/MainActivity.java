package com.example.mikael.tetris;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.graphics.Point;
import android.view.GestureDetector;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
//    GameEngine gameEngine;
    GameScene gameScene;
    GameLoop gameLoop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the pixel dimensions of the screen
        Display display = getWindowManager().getDefaultDisplay();

        // Initialize the result into a Point object
        Point size = new Point();
        display.getSize(size);

        // Create a new instance of the SnakeEngine class
        //gameEngine = new GameEngine(this, size);
        gameScene = (GameScene) findViewById(R.id.surfaceView);
        //gameScene = new GameScene(this);
        gameLoop = new GameLoop(gameScene);
        // Make snakeEngine the view of the Activity
        //setContentView(gameEngine);
        //gameScene.setOnTouchListener(listener);
        // setContentView(surface);
        //setContentView(gameScene);
        Log.d("MainActivity", "onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //gameEngine.resume();
        //gameSurfaceView.resume();
        gameLoop.resume();
        Log.d("MainActivity", "onResume");

    }

    @Override
    protected void onPause() {
        super.onPause();
        gameLoop.pause();
        //gameEngine.pause();
    }

    public void pauseButtonAction(View wiew) {
        final Button button = findViewById(R.id.button);

        if (button.getText().equals("II")) {
            button.setText(">");
            gameLoop.pause();
        }
        else {
            button.setText("II");
            gameLoop.resume();
        }
    }
}
