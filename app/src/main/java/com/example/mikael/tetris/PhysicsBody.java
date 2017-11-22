package com.example.mikael.tetris;

import android.graphics.Point;
import android.graphics.Rect;

import java.util.Vector;

/**
 * Created by mikael on 11/13/17.
 */

public class PhysicsBody {
    public Rect bounds;
    public Point pos;

    public PhysicsBody() {
        bounds = new Rect();
    }

    public void setBounds(Rect bounds) {
        this.bounds = bounds;
    }

}
