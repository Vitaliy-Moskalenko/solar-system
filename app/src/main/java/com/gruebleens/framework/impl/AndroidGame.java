package com.gruebleens.framework.impl;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Window;
import android.view.WindowManager;

import com.gruebleens.framework.Audio;
import com.gruebleens.framework.FileIO;
import com.gruebleens.framework.Game;
import com.gruebleens.framework.Graphics;
import com.gruebleens.framework.Input;
import com.gruebleens.framework.Screen;


public abstract class AndroidGame extends Activity implements Game {

    AndroidFastRenderView renderView;
    Graphics graphics;
    Audio audio;
    Input input;
    FileIO fileIo;
    Screen screen;
    WakeLock wakeLock;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        int frameBufferWidth  = isLandscape ? 480 : 320;
        int frameBufferHeight = isLandscape ? 320 : 480;

        Bitmap frameBuffer = Bitmap.createBitmap(frameBufferWidth, frameBufferHeight, Config.RGB_565);

        float scaleX = (float) frameBufferWidth  / getWindowManager().getDefaultDisplay().getWidth();
        float scaleY = (float) frameBufferHeight / getWindowManager().getDefaultDisplay().getHeight();

        renderView = new AndroidFastRenderView(this, frameBuffer);
        graphics   = new AndroidGraphics(getAssets(), frameBuffer);
        fileIo     = new AndroidFileIO(getAssets());
        audio      = new AndroidAudio(this);
        input      = new AndroidInput(this, renderView, scaleX, scaleY);

        screen     = getStartScreen(); // This method must implement in particular Game

        setContentView(renderView);

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "GLGame");
    }

    @Override
    public void onResume() {
        super.onResume();
        wakeLock.acquire();
        screen.resume();
        renderView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        wakeLock.release();
        renderView.pause();
        screen.pause();

        if (isFinishing())
            screen.dispose();
    }

    public void setScreen(Screen screen) {
        if(screen == null)
            throw new IllegalArgumentException("Game: Screen undefined.");

        this.screen.pause();
        this.screen.dispose();

        screen.resume();
        screen.update(0);

        this.screen = screen;
    }

    public Screen getCurrentScreen() {
        return this.screen;
    }

    public Input getInput() {
        return input;
    }

    public FileIO getFileIO() {
        return fileIo;
    }

    public Graphics getGraphics() {
        return graphics;
    }

    public Audio getAudio() {
        return audio;
    }
}