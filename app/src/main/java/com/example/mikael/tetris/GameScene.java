package com.example.mikael.tetris;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import static junit.framework.Assert.assertEquals;


/**
 * Created by mikael on 11/13/17.
 */

public class GameScene extends SurfaceView implements TetrisPhysicsWorld.ContactDelegate {
    private SurfaceHolder holder;
    private MotionEvent playerEvent = null;
    private Paint paint;
    private Rect scene;
    private GestureDetector gestureDetector;
    private Bitmap gridBitmap = null;
    Queue<UserEventCreator.UserEvent> userEvents;
    private TetrisObjectCreator.TetrisObject user;
    private TetrisPhysicsWorld world = new TetrisPhysicsWorld();
    private enum GameStates {
        INTERACTIVE, CONTACT_STARTED, USER_TOUCH_DOWN
    }
    private GameStates gameState = GameStates.INTERACTIVE;
    private int score = 0;
    private int maxNoOfUserEvents = 0;

    public GameScene(Context context) {
        super(context);
        init();
    }

    public GameScene(Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameScene(Context context, android.util.AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        holder = getHolder();
        //this.setFocusableInTouchMode(true);
        GameGestureRecognizer listener = new GameGestureRecognizer(this);
        gestureDetector = new GestureDetector(getContext(), listener);
        paint = new Paint();
        userEvents = new LinkedList<>();
    }

    @Override
    public void onLayout(boolean changed,
                         int left,
                         int top,
                         int right,
                         int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int maxColWidth = getWidth() / TetrisUtils.TETRIS_NO_OF_COLS;
        int maxRowHeight = getHeight() / TetrisUtils.TETRIS_NO_OF_ROWS;
        int maxSize = maxColWidth; // Math.min(maxColWidth, maxRowHeight);
        TetrisUtils.getInstance().setBlockSize(maxSize);
        TetrisUtils.getInstance().setTopMargin(maxSize);
        TetrisUtils.getInstance().setGameWidth(maxSize * TetrisUtils.TETRIS_NO_OF_COLS);
        TetrisUtils.getInstance().setGameHeight(maxSize * TetrisUtils.TETRIS_NO_OF_ROWS);

        int leftMargin = (getWidth() - TetrisUtils.getInstance().getGameWidth()) / 2;
        scene = new Rect(leftMargin,
                         0,
                         TetrisUtils.getInstance().getGameWidth() + leftMargin,
                          getHeight());
        Rect gameBoundary = new Rect(scene.left,
                                     scene.top /*TetrisUtils.getInstance().getTopMargin()*/,
                                     scene.right,
                                     TetrisUtils.getInstance().getGameHeight());
        TetrisUtils.getInstance().setGameRect(gameBoundary);
        world.addBorder(gameBoundary);
        SceneNavigator.getInstance().setMap(gameBoundary);
        SceneNavigator.getInstance().setScene(scene);
        makeGridBitmap();

        makeUser();

        world.contactDelegate = this;

        //assertEquals (true, TetrisUtils.getInstance().getGameWidth() == SceneNavigator.getInstance().getScene().width());
    }

    public void update() {
        handleUserEvent();
        handleGameState();
        world.update();
    }

    private void makeUser() {
        user = TetrisObjectCreator.getInstance().makeTetrisObject();
        user.velocity = 10;
        int col = (-user.size + TetrisUtils.TETRIS_NO_OF_COLS) / 2;
        int x = col * TetrisUtils.getInstance().getBlockSize() + TetrisUtils.getInstance().getGameRect().left;
        user.moveTo(x, 0);
        world.addChild(user);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Log.d("GameScene", "onTouchEvent");
        gestureDetector.onTouchEvent(event);

        return true;
    }

    public void handleUserEvent() {
        boolean userChanged = true;
        if (maxNoOfUserEvents < userEvents.size()) {
            maxNoOfUserEvents = userEvents.size();
            Log.d("maxNoOfUserEvents", "" + maxNoOfUserEvents);
        }
        UserEventCreator.UserEvent e = userEvents.poll();
        if (e != null) {
            switch (e.type) {
                case VERTICAL_SCROLL:
                    UserEventCreator.ScrollEvent scroll = (UserEventCreator.ScrollEvent) e;
                    SceneNavigator.getInstance().move(0, (int) scroll.distance);
                    break;
                case MOVE_LEFT:
                    user.moveLeft();
                    if (world.isCollision(user)) {
                        user.moveRight();
                    }
                    break;
                case MOVE_RIGHT:
                    user.moveRight();
                    if (world.isCollision(user)) {
                        user.moveLeft();
                    }
                    break;
                case ROTATE_LEFT:
                    user.rotateLeft();
                    if (world.isCollision(user)) {
                        user.rotateRight();
                    }
                    break;
                case ROTATE_RIGHT:
                    user.rotateRight();
                    if (world.isCollision(user)) {
                        user.rotateLeft();
                    }
                    break;
                case DROP:
                    UserEventCreator.DropEvent drop = (UserEventCreator.DropEvent)e;
                    if (user.bounds.contains(drop.point.x, drop.point.y)) {
                        user.velocity = 30;
                    }
                    break;
            }
        }
    }

    public void draw()
    {
        if (!holder.getSurface().isValid()) {
            return;
        }

        Canvas canvas = holder.lockCanvas();

        if (canvas != null) {
            // canvas.draw(...);
            paint.setColor(Color.BLACK);
            Rect fullRect = new Rect(0, 0, getWidth(), getHeight());
            //canvas.drawRect(fullRect, paint);
            //canvas.drawRect(scene, paint);
            drawGrid(canvas);

            Iterator it = world.children.iterator();
            while (it.hasNext()) {
                TetrisObjectCreator.TetrisObject tetrisObject =
                        (TetrisObjectCreator.TetrisObject) it.next();
                tetrisObject.draw(canvas);
            }

            Paint boarderPaint = new Paint();
            boarderPaint.setColor(Color.GRAY);
            boarderPaint.setStyle(Paint.Style.STROKE);
            Rect boarder = new Rect(world.boarder);
            for (int i = 0; i < 1; i++) {
                canvas.drawRect(boarder, boarderPaint);
                boarder.inset(1, 1);
            }

            Paint textPaint = new Paint();
            textPaint.setColor(Color.LTGRAY);
            textPaint.setTextSize(TetrisUtils.getInstance().getBlockSize());
            canvas.drawText("Score: " + score, 0, TetrisUtils.getInstance().getBlockSize(), textPaint);

            holder.unlockCanvasAndPost(canvas);
        }

    }

    private void drawGrid(Canvas canvas) {
        Rect src = new Rect(0,
                             SceneNavigator.getInstance().getScene().top,
                             SceneNavigator.getInstance().getScene().width(),
                             SceneNavigator.getInstance().getScene().bottom);
        Rect dst = new Rect(SceneNavigator.getInstance().getScene());
        dst.offsetTo(dst.left, 0);
        Paint p = new Paint();
        canvas.drawBitmap(gridBitmap, src, dst, p);
    }

    public void makeGridBitmap() {
        Rect rect = new Rect(0,
                             0,
                              TetrisUtils.getInstance().getGameWidth(),
                              TetrisUtils.getInstance().getGameHeight());

        gridBitmap = Bitmap.createBitmap(rect.width(),
                                         rect.height(),
                                         Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(gridBitmap);
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        canvas.drawRect(rect, p);
        p.setColor(Color.LTGRAY);
        p.setStyle(Paint.Style.STROKE);
        Paint textPaint = new Paint();
        textPaint.setColor(Color.LTGRAY);
        textPaint.setTextSize(TetrisUtils.getInstance().getBlockSize());
        for (int r = 0; r < TetrisUtils.TETRIS_NO_OF_ROWS; r++) {
            int y = r * TetrisUtils.getInstance().getBlockSize()/* + TetrisUtils.getInstance().getGameRect().top*/;
            for (int c = 0; c < TetrisUtils.TETRIS_NO_OF_COLS; c++) {
                int x = c * TetrisUtils.getInstance().getBlockSize()/* + TetrisUtils.getInstance().getGameRect().left*/;
                rect.set(x,
                         y,
                        x + TetrisUtils.getInstance().getBlockSize(),
                        y + TetrisUtils.getInstance().getBlockSize());
                canvas.drawRect(rect, p);
            }
            //canvas.drawText("" + r, 0, y + TetrisUtils.getInstance().getBlockSize(), textPaint);
        }

    }

    public void handleGameState() {
        switch (gameState) {
            case USER_TOUCH_DOWN:
                handleUserTouchDown();
                break;
        }
    }

    public void handleUserTouchDown() {
        world.removeChild(user);
        Vector<TetrisObjectCreator.TetrisObject> objectsToRemove =
                new Vector<TetrisObjectCreator.TetrisObject>();
        TetrisObjectCreator.TetrisObject split[] = user.split();
        for (int i = 0; i < split.length; i++) {
            boolean isJoin = false;
            Iterator it = world.children.iterator();
            while (it.hasNext()) {
                TetrisObjectCreator.TetrisObject tetrisObject =
                        (TetrisObjectCreator.TetrisObject) it.next();
                if (tetrisObject.bounds.top == split[i].bounds.top) {
                    tetrisObject.join(split[i]);
                    isJoin = true;
                    if (tetrisObject.tetrisComponents.size() == TetrisUtils.TETRIS_NO_OF_COLS) {
                        // Remove full row
                        objectsToRemove.add(tetrisObject);
                    }
                }
            }
            if (!isJoin) {
                world.addChild(split[i]);
            }
        }
        Iterator it = objectsToRemove.iterator();
        int lineCount = 0;
        while(it.hasNext()) {
            TetrisObjectCreator.TetrisObject tetrisObject =
                    (TetrisObjectCreator.TetrisObject) it.next();
            world.removeChild(tetrisObject);
            lineCount++;
            score += lineCount * 100;
        }
        makeUser();
        gameState = GameStates.INTERACTIVE;
    }

    public void contactStarted(TetrisObjectCreator.TetrisObject tetrisObject) {
        if (user == tetrisObject) {

            if (gameState == GameStates.CONTACT_STARTED) {
                gameState = GameStates.USER_TOUCH_DOWN;
            }
            else {
                gameState = GameStates.CONTACT_STARTED;
            }
        }
    }
}
