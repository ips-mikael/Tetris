package com.example.mikael.tetris;

import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

/**
 * Created by mikael on 11/15/17.
 */

public class SceneNavigator {
    private Rect map;
    private Rect scene;
    static private SceneNavigator instance = null;

    private SceneNavigator() {
        instance = this;
    }

    static public SceneNavigator getInstance() {
        if (instance == null) {
            instance = new SceneNavigator();
        }
        return instance;
    }

    public void setMap(Rect map) {
        this.map = map;
    }

    public void setScene(Rect scene) { this.scene = new Rect(scene); }
    public Rect getScene() { return scene; }

    public void move(int dx, int dy) {
        if (dy > 0) { // Scroll Down
            if (dy > map.bottom - scene.bottom) {
                dy = map.bottom - scene.bottom;
            }
        }
        else if (dy < 0) {
            if (dy < map.top - scene.top) {
                dy = map.top - scene.top;
            }
        }
        scene.offset(dx, dy);
    }

    public String toString() {
        String str = "scene(" + scene.left + "," + scene.top + "," + scene.right + "," + scene.bottom + ") " + map.toString();
        return str;
    }
}
