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

    private static int mapZ[][] = {{0, 0, 0,
                                    0, 1, 1,
                                    1, 1, 0},
                                   {1, 0, 0,
                                    1, 1, 0,
                                    0, 1, 0}};
    private static int mapS[][] = {{0, 0, 0,
                                    1, 1, 0,
                                    0, 1, 1},
                                   {0, 1, 0,
                                    1, 1, 0,
                                    1, 0, 0}};
    private static int mapJ[][] = {{0, 1, 0,
                                    0, 1, 0,
                                    1, 1, 0},
                                   {1, 0, 0,
                                    1, 1, 1,
                                    0, 0, 0},
                                   {1, 1, 0,
                                    1, 0, 0,
                                    1, 0, 0},
                                   {1, 1, 1,
                                    0, 0, 1,
                                    0, 0, 0}};
    private static int mapL[][] = {{0, 1, 0,
                                    0, 1, 0,
                                    0, 1, 1},
                                   {1, 1, 1,
                                    1, 0, 0,
                                    0, 0, 0},
                                   {0, 1, 1,
                                    0, 0, 1,
                                    0, 0, 1},
                                   {0, 0, 1,
                                    1, 1, 1,
                                    0, 0, 0}};
    private static int mapT[][] = {{0, 0, 0,
                                    0, 1, 0,
                                    1, 1, 1},
                                   {0, 1, 0,
                                    0, 1, 1,
                                    0, 1, 0},
                                   {0, 0, 0,
                                    1, 1, 1,
                                    0, 1, 0},
                                   {0, 0, 1,
                                    0, 1, 1,
                                    0, 0, 1}};
    private static int mapO[][] = {{1, 1,
                                    1, 1}};
    private static int mapI[][] = {{0, 1, 0, 0,
                                    0, 1, 0, 0,
                                    0, 1, 0, 0,
                                    0, 1, 0, 0},
                                   {0, 0, 0, 0,
                                    1, 1, 1, 1,
                                    0, 0, 0, 0,
                                    0, 0, 0, 0}};

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
        Rect bounds;
        Paint paint;

        public TetrisComponent(int index, int color) {
            bounds = new Rect();
            paint = new Paint();
            paint.setColor(color);
        }
        public TetrisComponent(TetrisComponent src) {
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
        int rotationMap[][];
        int rotationIndex = 0;
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
            rotationMap = src.rotationMap;
            rotationIndex = src.rotationIndex;
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
                tetrisObject.rotationMap = new int[1][1];
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
            rotationIndex--;
            if (rotationIndex < 0) {
                rotationIndex = rotationMap.length - 1;
            }
            rotate(rotationMap[rotationIndex]);
        }
        public void rotateRight() {
            rotationIndex = (rotationIndex + 1) % rotationMap.length;
            rotate(rotationMap[rotationIndex]);
        }
        private void rotate(int rotateMap[]) {
            Rect newBounds = new Rect();
            Iterator it = tetrisComponents.iterator();
            for (int i = 0; i < rotateMap.length; i++) {
                if (rotateMap[i] == 1) {
                    int r = i / size;
                    int c = i % size;
                    int y = r * TetrisUtils.getInstance().getBlockSize() + origin.y;
                    int x = c * TetrisUtils.getInstance().getBlockSize() + origin.x;
                    TetrisComponent tetrisComponent = (TetrisComponent)it.next();
                    tetrisComponent.bounds.offsetTo(x, y);
                    newBounds.union(tetrisComponent.bounds);
                }
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
        tetrisObject.rotationMap = mapZ;
        addComponents(tetrisObject, mapZ[0], Color.RED);
        return tetrisObject;
    }

    private TetrisObject makeTetrisObjectS() {
        TetrisObject tetrisObject = new TetrisObject();
        tetrisObject.size = 3;
        tetrisObject.rotationMap = mapS;
        addComponents(tetrisObject, mapS[0], Color.GREEN);
        return tetrisObject;
    }

    private TetrisObject makeTetrisObjectJ() {
        TetrisObject tetrisObject = new TetrisObject();
        tetrisObject.size = 3;
        tetrisObject.rotationMap = mapJ;
        addComponents(tetrisObject, mapJ[0], Color.BLUE);
        return tetrisObject;
    }

    private TetrisObject makeTetrisObjectL() {
        TetrisObject tetrisObject = new TetrisObject();
        tetrisObject.size = 3;
        tetrisObject.rotationMap = mapL;
        addComponents(tetrisObject, mapL[0], 0xFFFFA500); // Orange
        return tetrisObject;
    }

    private TetrisObject makeTetrisObjectO() {
        TetrisObject tetrisObject = new TetrisObject();
        tetrisObject.size = 2;
        tetrisObject.rotationMap = mapO;
        addComponents(tetrisObject, mapO[0], Color.YELLOW);
        return tetrisObject;
    }

    private TetrisObject makeTetrisObjectT() {
        TetrisObject tetrisObject = new TetrisObject();
        tetrisObject.size = 3;
        tetrisObject.rotationMap = mapT;
        addComponents(tetrisObject, mapT[0], 0xFFA020F0); // Purple
        return tetrisObject;
    }

    private TetrisObject makeTetrisObjectI() {
        TetrisObject tetrisObject = new TetrisObject();
        tetrisObject.size = 4;
        tetrisObject.rotationMap = mapI;
        addComponents(tetrisObject, mapI[0], Color.CYAN);
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
