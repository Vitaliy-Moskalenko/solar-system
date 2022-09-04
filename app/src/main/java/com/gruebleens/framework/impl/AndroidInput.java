package com.gruebleens.framework.impl;

import java.util.List;
import android.content.Context;
import android.os.Build.VERSION;
import android.view.View;
import com.gruebleens.framework.Input;


public class AndroidInput implements Input {

    AccelerometerHandler accelerometerHandler;
    KeyboardHandler      keyboardHandler;
    TouchHandler         touchHandler;

    public AndroidInput(Context context, View view, float sclX, float sxlY) {
        accelerometerHandler = new AccelerometerHandler(context);
        keyboardHandler      = new KeyboardHandler(view);

        if(VERSION.SDK_INT < 5)
            touchHandler = new SingleTouchHandler(view, sclX, sxlY);
        else
            touchHandler = new MultiTouchHandler(view, sclX, sxlY);


    }

    public boolean isKeyPressed(int keyCode) {
        return keyboardHandler.isKeyPressed(keyCode);
    }

    public boolean isTouchDown(int pointer) {
        return touchHandler.isTouchDown(pointer);
    }

    public int getTouchX(int pointer) {
        return touchHandler.getTouchX(pointer);
    }

    public int getTouchY(int pointer) {
        return getTouchY(pointer);
    }

    public float getAccelX() {
        return accelerometerHandler.getAccelX();
    }

    public float getAccelY() {
        return accelerometerHandler.getAccelY();
    }

    public float getAccelZ() {
        return accelerometerHandler.getAccelZ();
    }

    public List<TouchEvent> getTouchEvents() {
        return touchHandler.getTouchEvents();
    }

    public List<KeyEvent> getKeyEvents() {
        return keyboardHandler.getKeyEvents();
    }
}
