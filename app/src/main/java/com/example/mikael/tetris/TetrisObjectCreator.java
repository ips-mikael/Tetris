package com.example.mikael.tetris;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

/**
 * Created by mikael on 11/16/17.
 */

public class TetrisObjectCreator {

    private static int mapZ[] = {0, 0, 0,
                                 0, 1, 1,
                                 1, 1, 0};
    private static int mapS[] = {0, 0, 0,
                                 1, 1, 0,
                                 0, 1, 1};
    private static int mapJ[] = {0, 1, 0,
                                 0, 1, 0,
                                 1, 1, 0};
    private static int mapL[] = {0, 1, 0,
                                 0, 1, 0,
                                 0, 1, 1};
    private static int mapT[] = {0, 0, 0,
                                 0, 1, 0,
                                 1, 1, 1};
    private static int mapO[] = {1, 1,
                                 1, 1};
    private static int mapI[] = {0, 1, 0, 0,
                                 0, 1, 0, 0,
                                 0, 1, 0, 0,
                                 0, 1, 0, 0};

    private static int rotateRightMap2x2[] = {1, 3,
                                              0, 2};
    private static int rotateLeftMap2x2[] = {2, 0,
                                             3, 1};
    private static int rotateRightMap3x3[] = {2, 5, 8,
                                              1, 4, 7,
                                              0, 3, 6};
    private static int rotateLeftMap3x3[] = {6, 3, 0,
                                             7, 4, 1,
                                             8, 5, 2};
    private static int rotateRightMap4x4[] = {3, 7, 11, 15,
                                              2, 6, 10, 14,
                                              1, 5,  9, 13,
                                              0, 4,  8, 12};
    private static int rotateLeftMap4x4[] = {12,  8, 4, 0,
                                             13,  9, 5, 1,
                                             14, 10, 6, 2,
                                             15, 11, 7, 3};
    private static TetrisObjectCreator instance = null;

    private TetrisObjectCreator() {
    }

    static public TetrisObjectCreator getInstance() {
        if (instance == null) {
            instance = new TetrisObjectCreator();
        }
        return instance;
    }

    class TetrisComponent {
        int index;
        Rect bounds;
        Paint paint;

        public TetrisComponent(int index, int color) {
            this.index = index;
            bounds = new Rect();
            paint = new Paint();
            paint.setColor(color);
        }
        public TetrisComponent(TetrisComponent src) {
            index = src.index;
            bounds = new Rect(src.bounds);
            paint = new Paint(src.paint);
        }
        public void draw(Canvas canvas) {
            Rect drawRect = new Rect(bounds);
            Rect scene = SceneNavigator.getInstance().getScene();
            drawRect.offset(0, -scene.top);
            canvas.drawRect(drawRect, paint);
        }
    }

    class TetrisObject {
        Vector<TetrisComponent> tetrisComponents;
        int rotateLeftMap[];
        int rotateRightMap[];
        int size;
        int velocity;
        Point origin;
        Rect bounds;
        Paint paint;
        boolean needsRedraw = false;

        public TetrisObject() {
            tetrisComponents = new Vector<TetrisComponent>();
            origin = new Point();
            bounds = new Rect();
            velocity = 10;
            paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
        }
        public TetrisObject(TetrisObject src) {
            tetrisComponents = new Vector<TetrisComponent>();
            Iterator it = src.tetrisComponents.iterator();
            while (it.hasNext()) {
                TetrisComponent tetrisComponent = (TetrisComponent)it.next();
                TetrisComponent newTetrisComponent = new TetrisComponent(tetrisComponent);
                tetrisComponents.add(newTetrisComponent);
            }
            rotateLeftMap = src.rotateLeftMap;
            rotateRightMap = src.rotateRightMap;
            size = src.size;
            velocity = src.velocity;
            origin = new Point(src.origin);
            bounds = new Rect(src.bounds);
        }
        public void addComponent(TetrisComponent tetrisComponent) {
            tetrisComponents.add(tetrisComponent);
            bounds.union(tetrisComponent.bounds);
        }
        public TetrisObject[] split() {
            Vector<TetrisObject> splitVector = new Vector<TetrisObject>();
            Iterator it = tetrisComponents.iterator();
            while (it.hasNext()) {
                TetrisComponent tetrisComponent = (TetrisComponent) it.next();
                TetrisObject tetrisObject = new TetrisObject();
                tetrisObject.addComponent(tetrisComponent);
                tetrisObject.bounds = new Rect(tetrisComponent.bounds);
                tetrisObject.origin = new Point(bounds.left, bounds.top);
                tetrisObject.size = 1;
                tetrisObject.rotateLeftMap = new int[1];
                tetrisObject.rotateRightMap = new int[1];
                splitVector.add(tetrisObject);
            }
            TetrisObject[] a = new TetrisObject[splitVector.size()];
            return splitVector.toArray(a);
        }
        public void join(TetrisObject other) {
            Iterator it = other.tetrisComponents.iterator();
            while (it.hasNext()) {
                TetrisComponent tetrisComponent = (TetrisComponent) it.next();
                tetrisComponents.add(tetrisComponent);
                bounds.union(tetrisComponent.bounds);
            }
        }
        public void moveLeft() {
            offset(-TetrisUtils.getInstance().getBlockSize(), 0);
        }
        public void moveRight() {
            offset(TetrisUtils.getInstance().getBlockSize(), 0);
        }
        public void rotateLeft() {
            rotate(rotateLeftMap);
        }
        public void rotateRight() {
            rotate(rotateRightMap);
        }
        private void rotate(int rotateMap[]) {
            Rect newBounds = new Rect();
            Iterator it = tetrisComponents.iterator();
            while (it.hasNext()) {
                TetrisComponent tetrisComponent = (TetrisComponent)it.next();
                int newIndex = rotateMap[tetrisComponent.index];
                int r = newIndex / size;
                int c = newIndex % size;
                int y = r * TetrisUtils.getInstance().getBlockSize() + origin.y;
                int x = c * TetrisUtils.getInstance().getBlockSize() + origin.x;

                tetrisComponent.bounds.offsetTo(x, y);
                tetrisComponent.index = newIndex;
                newBounds.union(tetrisComponent.bounds);
            }
            bounds = newBounds;
        }
        public void offset(int dx, int dy) {
            needsRedraw = true;
            origin.offset(dx, dy);
            bounds.offset(dx, dy);
            Iterator it = tetrisComponents.iterator();
            while (it.hasNext()) {
                TetrisComponent tetrisComponent = (TetrisComponent) it.next();
                tetrisComponent.bounds.offset(dx, dy);
            }
        }
        public void moveTo(int x, int y) {
            needsRedraw = true;
            int dx = x - origin.x;
            int dy = y - origin.y;
            origin.set(x, y);
            bounds.offset(dx, dy);
            Iterator it = tetrisComponents.iterator();
            while (it.hasNext()) {
                TetrisComponent tetrisComponent = (TetrisComponent)it.next();
                //dx = x - tetrisComponent.bounds.left;
                //dy = y - tetrisComponent.bounds.top;
                tetrisComponent.bounds.offset(dx, dy);
            }

        }
        public Rect intersect(TetrisObject other) {
            Rect intersect = new Rect();
            Iterator itThis = tetrisComponents.iterator();
            while (itThis.hasNext()) {
                TetrisComponent thisTetrisComponent = (TetrisComponent)itThis.next();
                Rect thisTetrisComponentBounds = new Rect(thisTetrisComponent.bounds);
                Iterator itOther = other.tetrisComponents.iterator();
                while (itOther.hasNext()) {
                    TetrisComponent otherTetrisComponent = (TetrisComponent)itOther.next();
                    if (thisTetrisComponentBounds.intersect(otherTetrisComponent.bounds)) {
                        // thisTetrisComponentBounds is modified to the intersection
                        intersect.union(thisTetrisComponentBounds);
                        return intersect;
                    }
                }
            }
            return intersect;
        }
        public void draw(Canvas canvas) {
            needsRedraw = false;
            if (Rect.intersects(SceneNavigator.getInstance().getScene(),
                                bounds)) {
                Iterator it = tetrisComponents.iterator();
                while (it.hasNext()) {
                    TetrisComponent tetrisComponent = (TetrisComponent) it.next();
                    tetrisComponent.draw(canvas);
                }
                Rect drawRect = new Rect(bounds);
                Rect scene = SceneNavigator.getInstance().getScene();
                drawRect.offset(0, -scene.top);
                canvas.drawRect(drawRect, paint);
            }
        }
    }

    public TetrisObject makeClone(TetrisObject src) {
        return new TetrisObject(src);
    }

    public TetrisObject makeTetrisObject() {
        TetrisObject tetrisObject = null;
        Random random = new Random();
        switch (random.nextInt(7)) {
            case 0:
                tetrisObject = makeTetrisObjectZ();
                break;
            case 1:
                tetrisObject = makeTetrisObjectJ();
                break;
            case 2:
                tetrisObject = makeTetrisObjectL();
                break;
            case 3:
                tetrisObject = makeTetrisObjectS();
                break;
            case 4:
                tetrisObject = makeTetrisObjectO();
                break;
            case 5:
                tetrisObject = makeTetrisObjectT();
                break;
            case 6:
                tetrisObject = makeTetrisObjectI();
                break;
        }
        return tetrisObject;
    }

    private TetrisObject makeTetrisObjectZ() {
        TetrisObject tetrisObject = new TetrisObject();
        tetrisObject.size = 3;
        tetrisObject.rotateLeftMap = rotateLeftMap3x3;
        tetrisObject.rotateRightMap = rotateRightMap3x3;
        addComponents(tetrisObject, mapZ, Color.RED);
        return tetrisObject;
    }

    private TetrisObject makeTetrisObjectS() {
        TetrisObject tetrisObject = new TetrisObject();
        tetrisObject.size = 3;
        tetrisObject.rotateLeftMap = rotateLeftMap3x3;
        tetrisObject.rotateRightMap = rotateRightMap3x3;
        addComponents(tetrisObject, mapS, Color.GREEN);
        return tetrisObject;
    }

    private TetrisObject makeTetrisObjectJ() {
        TetrisObject tetrisObject = new TetrisObject();
        tetrisObject.size = 3;
        tetrisObject.rotateLeftMap = rotateLeftMap3x3;
        tetrisObject.rotateRightMap = rotateRightMap3x3;
        addComponents(tetrisObject, mapJ, Color.BLUE);
        return tetrisObject;
    }

    private TetrisObject makeTetrisObjectL() {
        TetrisObject tetrisObject = new TetrisObject();
        tetrisObject.size = 3;
        tetrisObject.rotateLeftMap = rotateLeftMap3x3;
        tetrisObject.rotateRightMap = rotateRightMap3x3;
        addComponents(tetrisObject, mapL, 0xFFFFA500); // Orange
        return tetrisObject;
    }

    private TetrisObject makeTetrisObjectO() {
        TetrisObject tetrisObject = new TetrisObject();
        tetrisObject.size = 2;
        tetrisObject.rotateLeftMap = rotateLeftMap2x2;
        tetrisObject.rotateRightMap = rotateRightMap2x2;
        addComponents(tetrisObject, mapO, Color.YELLOW);
        return tetrisObject;
    }

    private TetrisObject makeTetrisObjectT() {
        TetrisObject tetrisObject = new TetrisObject();
        tetrisObject.size = 3;
        tetrisObject.rotateLeftMap = rotateLeftMap3x3;
        tetrisObject.rotateRightMap = rotateRightMap3x3;
        addComponents(tetrisObject, mapT, 0xFFA020F0); // Purple
        return tetrisObject;
    }

    private TetrisObject makeTetrisObjectI() {
        TetrisObject tetrisObject = new TetrisObject();
        tetrisObject.size = 4;
        tetrisObject.rotateLeftMap = rotateLeftMap4x4;
        tetrisObject.rotateRightMap = rotateRightMap4x4;
        addComponents(tetrisObject, mapI, Color.CYAN);
        return tetrisObject;
    }

    private void addComponents(TetrisObject tetrisObject, int map[], int color) {
        for (int r = 0; r < tetrisObject.size; r++) {
            int y = r * TetrisUtils.getInstance().getBlockSize() + tetrisObject.origin.y;
            for (int c = 0; c < tetrisObject.size; c++) {
                int x = c * TetrisUtils.getInstance().getBlockSize() + tetrisObject.origin.x;
                int index = r * tetrisObject.size + c;
                if (map[index] == 1) {
                    TetrisComponent tetrisComponent = new TetrisComponent(index, color);
                    tetrisComponent.bounds.set(x,
                                               y,
                                         x + TetrisUtils.getInstance().getBlockSize(),
                                       y + TetrisUtils.getInstance().getBlockSize());
                    tetrisObject.addComponent(tetrisComponent);
                }
            }
        }

    }
}
