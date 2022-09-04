package com.gruebleens.framework.impl;

import android.graphics.*;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class AndroidFastRenderView extends SurfaceView implements Runnable {

    AndroidGame   game;
    Bitmap        frameBuffer;
    Thread        renderThread = null;
    SurfaceHolder surfaceHolder;

    volatile boolean isRunning = false;


    public AndroidFastRenderView(AndroidGame game, Bitmap frameBuffer) {
        super(game);
        this.game = game;
        this.frameBuffer = frameBuffer;
        this.surfaceHolder = getHolder();
    }

    public void resume() {
        isRunning = true;
        renderThread = new Thread(this);
        renderThread.start();
    }

    public void pause() {
        isRunning = false;
        while(true) {
            try {
                renderThread.join();
                break;
            }
            catch (InterruptedException e) {
                // Retry
            }
        }
    }

    public void run() {
        Rect dstRect = new Rect();
        long startTime = System.nanoTime();

        while(isRunning) {
            if(!surfaceHolder.getSurface().isValid()) continue;

            float dT = (System.nanoTime() - startTime) / 1000000000.0f;
            startTime = System.nanoTime();

            game.getCurrentScreen().update(dT);
            game.getCurrentScreen().present(dT);

            Canvas canvas = surfaceHolder.lockCanvas();
            canvas.getClipBounds(dstRect);
            canvas.drawBitmap(frameBuffer, null, dstRect, null);
            surfaceHolder.unlockCanvasAndPost(canvas);

        }
    }
}