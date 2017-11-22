package com.example.mikael.tetris;

import android.graphics.Point;

import static com.example.mikael.tetris.UserEventCreator.UserEventEnum.DROP;
import static com.example.mikael.tetris.UserEventCreator.UserEventEnum.MOVE_LEFT;
import static com.example.mikael.tetris.UserEventCreator.UserEventEnum.MOVE_RIGHT;
import static com.example.mikael.tetris.UserEventCreator.UserEventEnum.ROTATE_LEFT;
import static com.example.mikael.tetris.UserEventCreator.UserEventEnum.ROTATE_RIGHT;
import static com.example.mikael.tetris.UserEventCreator.UserEventEnum.VERTICAL_SCROLL;

/**
 * Created by mikael on 11/15/17.
 */

public class UserEventCreator {
    private static UserEventCreator instance = null;

    private UserEventCreator() {
    }

    static public UserEventCreator getInstance() {
        if (instance == null) {
            instance = new UserEventCreator();
        }
        return instance;
    }

    public enum UserEventEnum {
        MOVE_LEFT, MOVE_RIGHT, ROTATE_LEFT, ROTATE_RIGHT, DROP, VERTICAL_SCROLL
    }

    class UserEvent { UserEventEnum type; }
    class ScrollEvent extends UserEvent {
        int distance;
    }
    class RotateLeftEvent extends UserEvent { }
    class RotateRightEvent extends UserEvent { }
    class MoveLeftEvent extends UserEvent { }
    class MoveRightEvent extends UserEvent { }
    class DropEvent extends UserEvent { Point point; }

    public ScrollEvent makeScrollEvent(int distance) {
        ScrollEvent event = new ScrollEvent();
        event.type = VERTICAL_SCROLL;
        event.distance = distance;
        return event;
    }

    public RotateLeftEvent makeRotateLeftEvent() {
        RotateLeftEvent event = new RotateLeftEvent();
        event.type = ROTATE_LEFT;
        return event;
    }

    public RotateRightEvent makeRotateRightEvent() {
        RotateRightEvent event = new RotateRightEvent();
        event.type = ROTATE_RIGHT;
        return event;
    }

    public MoveLeftEvent makeMoveLeftEvent() {
        MoveLeftEvent event = new MoveLeftEvent();
        event.type = MOVE_LEFT;
        return event;
    }

    public MoveRightEvent makeMoveRightEvent() {
        MoveRightEvent event = new MoveRightEvent();
        event.type = MOVE_RIGHT;
        return event;
    }

    public DropEvent makeDropEvent(int x, int y) {
        DropEvent event = new DropEvent();
        event.type = DROP;
        event.point = new Point(x, y);
        return event;
    }

}
