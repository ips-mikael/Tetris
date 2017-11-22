package com.example.mikael.tetris;

import android.graphics.Color;
import android.graphics.Rect;

/**
 * Created by mikael on 11/10/17.
 */

public class TetrisUtils {
//    static int MAP_NO_OF_ROWS = 4;
//    static int MAP_NO_OF_COLS = 4;
    static int TETRIS_NO_OF_ROWS = 40;
    static int TETRIS_NO_OF_COLS = 20;
    static int NO_OF_PIECES = 7;
    static int topMargin = 0;
    /*
    public enum UserEvent {
        MOVE_LEFT, MOVE_RIGHT, ROTATE_LEFT, ROTATE_RIGHT, SCROLL_DOWN, SCROLL_UP, VERTICAL_SCROLL, NONE
    }*/

    public static int colors[] = {Color.GREEN,
                                  Color.YELLOW,
                                  Color.BLUE,
                                  Color.RED,
                                  Color.CYAN,
                                  Color.MAGENTA,
                                  Color.LTGRAY};
    /*
    public static int block_map[][] =
            {{0, 1, 0, 0,
              0, 1, 0, 0,
              0, 1, 0, 0,
              0, 1, 0, 0},
             {0, 0, 1, 0,
              0, 0, 1, 0,
              0, 1, 1, 0,
              0, 0, 0, 0},
             {0, 0, 0, 0,
              0, 1, 1, 0,
              0, 1, 1, 0,
              0, 0, 0, 0},
             {0, 1, 0, 0,
              0, 1, 0, 0,
              0, 1, 1, 0,
              0, 0, 0, 0},
             {0, 0, 0, 0,
              0, 0, 1, 1,
              0, 1, 1, 0,
              0, 0, 0, 0},
             {0, 0, 0, 0,
              1, 1, 0, 0,
              0, 1, 1, 0,
              0, 0, 0, 0},
             {0, 0, 0, 0,
              0, 1, 0, 0,
              1, 1, 1, 0,
              0, 0, 0, 0}};
*/
    public static int rotateRightMap[] =
            {3, 7, 11, 15,  /*  0->3,  1->7,  2->11,  3->15 */
             2, 6, 10, 14,  /*  4->2,  5->6,  6->10,  7->14 */
             1, 5,  9, 13,  /*  8->1,  9->5, 10-> 9, 11->13 */
             0, 4,  8, 12}; /* 12->0, 13->4, 14-> 8, 15->12 */
    public static int rotateLeftMap[] =
            {12, 8,  4, 0,  /*  0->12,  1-> 8,  2->4,  3->0 */
             13, 9,  5, 1,  /*  4->13,  5-> 9,  6->5,  7->1 */
             14, 10, 6, 2,  /*  8->14,  9->10, 10->6, 11->2 */
             15, 13, 7, 3}; /* 12->15, 13->11, 14->7, 15->3 */

    private static TetrisUtils instance = null;
    private static int size = 0;
    private static int gameWidth = 0;
    private static int gameHeight = 0;
    private static Rect gameRect = null;
    private TetrisUtils() {}

    public static TetrisUtils getInstance() {
        if (instance == null) {
            instance = new TetrisUtils();
        }
        return instance;
    }

    public void setBlockSize(int size) {
        this.size = size;
    }

    public int getBlockSize() {
        return size;
    }

    public void setGameWidth(int width) {
        gameWidth = width;
    }

    public int getGameWidth() {
        return gameWidth;
    }

    public void setGameHeight(int height) {
        gameHeight = height;
    }

    public int getGameHeight() {
        return gameHeight;
    }

    public void setGameRect(Rect rect) {
        gameRect = new Rect(rect);
    }

    public Rect getGameRect() {
        return gameRect;
    }

    public void setTopMargin(int topMargin) { this.topMargin = topMargin; }

//    public int getTopMargin() { return topMargin; }

    public static int getColor(int id) {
        return colors[id];
    }

}
