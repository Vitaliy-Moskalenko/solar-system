package com.gruebleens.framework.impl;

import java.util.ArrayList;
import java.util.List;
import android.view.MotionEvent;
import android.view.View;

import com.gruebleens.framework.Input.TouchEvent;
import com.gruebleens.framework.impl.Pool.PoolObjectFactory;


public class MultiTouchHandler implements TouchHandler {

    public static final int MAX_TOUCHPONTS = 10;

    boolean[] isTouched = new boolean[MAX_TOUCHPONTS];
    int[]     touchX    = new int[MAX_TOUCHPONTS];
    int[]     touchY    = new int[MAX_TOUCHPONTS];
    int[]     ids       = new int[MAX_TOUCHPONTS];

    Pool<TouchEvent> touchEventsPool;
    List<TouchEvent> touchEvents       = new ArrayList<TouchEvent>();
    List<TouchEvent> touchEventsBuffer = new ArrayList<TouchEvent>();

    float scaleX;
    float scaleY;

    public MultiTouchHandler(View view, float sclX, float sclY) {
        PoolObjectFactory<TouchEvent> touchEventsFactory = new PoolObjectFactory<TouchEvent>() {
            @Override
            public TouchEvent createObject() {
                return new TouchEvent();
            }
        };

        touchEventsPool = new Pool<TouchEvent>(touchEventsFactory, 100);

        view.setOnTouchListener(this);

        this.scaleX = sclX;
        this.scaleY = sclY;
    }

    public boolean onTouch(View view, MotionEvent event) {
        synchronized (this) {
            int action       = event.getAction() & MotionEvent.ACTION_MASK;
            int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;
            int pointerCount = event.getPointerCount();

            TouchEvent touchEvent;

            for(int i = 0; i < MAX_TOUCHPONTS; ++i) {
                if(i >= pointerCount) {
                    isTouched[i] = false;
                    ids[i] = -1;
                    continue;
                }

                int pointerId = event.getPointerId(i);

                // If it's up/down/cancel/out event, mask id to see if we should process it for this touch
                if(event.getAction() != MotionEvent.ACTION_MOVE  && i != pointerIndex)
                    continue;

                switch(action) {
                    case MotionEvent.ACTION_DOWN :
                    case MotionEvent.ACTION_POINTER_DOWN :
                        touchEvent         = touchEventsPool.createNewObject();
                        touchEvent.type    = TouchEvent.TOUCH_DOWN;
                        touchEvent.pointer = pointerId;
                        touchEvent.x = touchX[i] = (int)(event.getX(i) * scaleX);
                        touchEvent.y = touchY[i] = (int)(event.getY(i) * scaleY);

                        isTouched[i] = true;
                        ids[i]       = pointerId;

                        touchEventsBuffer.add(touchEvent);
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                    case MotionEvent.ACTION_CANCEL:
                        touchEvent         = touchEventsPool.createNewObject();
                        touchEvent.type    = TouchEvent.TOUCH_UP;
                        touchEvent.pointer = pointerId;
                        touchEvent.x = touchX[i] = (int)(event.getX(i) * scaleX);
                        touchEvent.y = touchY[i] = (int)(event.getY(i) * scaleY);

                        isTouched[i] = false;
                        ids[i]       = -1;

                        touchEventsBuffer.add(touchEvent);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        touchEvent         = touchEventsPool.createNewObject();
                        touchEvent.type    = TouchEvent.TOUCH_DRAGGED;
                        touchEvent.pointer = pointerId;
                        touchEvent.x = touchX[i] = (int)(event.getX(i) * scaleX);
                        touchEvent.y = touchY[i] = (int)(event.getY(i) * scaleY);

                        isTouched[i] = true;
                        ids[i]       = pointerId;

                        touchEventsBuffer.add(touchEvent);
                        break;
                }
            }

            return true;
        }

    }

    @Override
    public boolean isTouchDown(int pointer) {
        synchronized (this) {
            int idx = getIndex(pointer);
            if (idx < 0 || idx >= MAX_TOUCHPONTS)
                return false;
            else
                return isTouched[idx];
        }
    }

    @Override
    public int getTouchX(int pointer) {
        synchronized (this) {
            int idx = getIndex(pointer);
            if (idx < 0 || idx >= MAX_TOUCHPONTS)
                return 0;
            else
                return touchX[idx];
        }
    }

    @Override
    public int getTouchY(int pointer) {
        synchronized (this) {
            int idx = getIndex(pointer);
            if (idx < 0 || idx >= MAX_TOUCHPONTS)
                return 0;
            else
                return touchY[idx];
        }
    }

    @Override
    public List<TouchEvent> getTouchEvents() {
        synchronized (this) {
            int len = touchEvents.size();
            for(int i=0; i<len; ++i)
                touchEventsPool.free(touchEvents.get(i));

            touchEvents.clear();
            touchEvents.addAll(touchEventsBuffer);
            touchEventsBuffer.clear();

            return touchEvents;
        }
    }

    // Returns the index for a given pointerId or -1 if no index
    private int getIndex(int pointerId) {
        for(int i=0; i<MAX_TOUCHPONTS; ++i)
            if(ids[i] == pointerId)
                return i;

        return -1;
    }
}