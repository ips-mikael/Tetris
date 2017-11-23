package com.example.mikael.tetris;

import android.graphics.Rect;

import java.util.Iterator;
import java.util.Vector;

/**
 * Created by mikael on 11/16/17.
 */

public class TetrisPhysicsWorld {
    Vector<TetrisObjectCreator.TetrisObject> children =
            new Vector<>();
    Rect boarder = null;
    ContactDelegate contactDelegate = null;

    public void addChild(TetrisObjectCreator.TetrisObject tetrisObject) {
        children.add(tetrisObject);
    }

    public void removeChild(TetrisObjectCreator.TetrisObject child) {
        children.remove(child);
    }

    public void update() {
        Iterator it = children.iterator();
        while (it.hasNext()) {
            TetrisObjectCreator.TetrisObject tetrisObject =
                    (TetrisObjectCreator.TetrisObject) it.next();
            int dy = tetrisObject.velocity;
            TetrisObjectCreator.TetrisObject clone =
                TetrisObjectCreator.getInstance().makeClone(tetrisObject);
            clone.offset(0, dy);
            boolean isContact = false;
            if (clone.bounds.bottom > boarder.bottom) {
                clone.offset(0, boarder.bottom - clone.bounds.bottom);
                //contactDelegate.contactStarted(tetrisObject);
                isContact = true;
            }

            Rect intersect = getIntersect(clone, tetrisObject);
            if (!intersect.isEmpty()) {
                clone.offset(0, -intersect.height());
                //contactDelegate.contactStarted(tetrisObject);
                isContact = true;
            }

            if (isContact) {
                tetrisObject.moveTo(clone.origin.x, clone.origin.y);
                contactDelegate.contactStarted(tetrisObject);
            }
            else if (dy != 0) {
                tetrisObject.offset(0, dy);
            }
        }
    }

    public boolean isCollision(TetrisObjectCreator.TetrisObject tetrisObject) {
        if (boarder != null && !boarder.contains(tetrisObject.bounds)) {
            return true;
        }
        Iterator it = children.iterator();
        while (it.hasNext()) {
            TetrisObjectCreator.TetrisObject tmp =
                    (TetrisObjectCreator.TetrisObject) it.next();
            if (tetrisObject != tmp) {
                Rect intersect = tetrisObject.intersect(tmp);
                if (!intersect.isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }

    public Rect getIntersect(TetrisObjectCreator.TetrisObject tetrisObject,
                             TetrisObjectCreator.TetrisObject ignore) {
        Rect intersect = new Rect();

        Iterator it = children.iterator();
        while (it.hasNext()) {
            TetrisObjectCreator.TetrisObject tmp =
                    (TetrisObjectCreator.TetrisObject) it.next();
            if (tmp != ignore) {
                Rect intersect2 = tetrisObject.intersect(tmp);
                intersect.union(intersect2);
            }
            if (!intersect.isEmpty()) {
                return intersect;
            }
        }
        return intersect;
    }

    public void addBorder(Rect boarder) {
        this.boarder = boarder;
    }

    public interface ContactDelegate {
        public void contactStarted(TetrisObjectCreator.TetrisObject tetrisObject);
    }
}
