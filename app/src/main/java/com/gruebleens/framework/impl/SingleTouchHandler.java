package com.gruebleens.framework.impl;

import java.util.ArrayList;
import java.util.List;
import android.view.MotionEvent;
import android.view.View;

import com.gruebleens.framework.Input.TouchEvent;
import com.gruebleens.framework.impl.Pool.PoolObjectFactory;


public class SingleTouchHandler implements TouchHandler {

    boolean isTouched;
    int touchX;
    int touchY;

    float scaleX;
    float scaleY;

    Pool<TouchEvent> touchEventsPool;
    List<TouchEvent> touchEvents       = new ArrayList<TouchEvent>();
    List<TouchEvent> touchEventsBuffer = new ArrayList<TouchEvent>();

    public SingleTouchHandler(View view, float scaleX, float scaleY) {
        PoolObjectFactory<TouchEvent> touchEventsFactory = new PoolObjectFactory<TouchEvent>() {
            @Override
            public TouchEvent createObject() {
                return new TouchEvent();
            }
        };

        touchEventsPool = new Pool<TouchEvent>(touchEventsFactory, 100);

        view.setOnTouchListener(this);

        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public boolean onTouch(View view, MotionEvent event) {

        TouchEvent touchEvent = touchEventsPool.createNewObject();

        synchronized (this) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchEvent.type = TouchEvent.TOUCH_DOWN;
                    isTouched = true;
                    break;

                case MotionEvent.ACTION_MOVE:
                    touchEvent.type = TouchEvent.TOUCH_DRAGGED;
                    isTouched = true;
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    touchEvent.type = TouchEvent.TOUCH_UP;
                    isTouched = false;
                    break;
            }
        }

        touchEvent.x = touchX = (int)(event.getX() + scaleX);
        touchEvent.y = touchY = (int)(event.getY() + scaleY);

        touchEventsBuffer.add(touchEvent);

        return true;
    }


    @Override
    public boolean isTouchDown(int pointer) {
        synchronized (this) {
            if(pointer == 0)
                return isTouched;
            else
                return false;
        }
    }

    @Override
    public int getTouchX(int pointer) {
        synchronized (this) {
            return touchX;
        }
    }

    @Override
    public int getTouchY(int pointer) {
        synchronized (this) {
            return touchY;
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
}