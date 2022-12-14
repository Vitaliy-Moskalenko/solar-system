package com.gruebleens.framework.impl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.gruebleens.framework.Audio;
import com.gruebleens.framework.FileIO;
import com.gruebleens.framework.Game;
import com.gruebleens.framework.Graphics;
import com.gruebleens.framework.Input;
import com.gruebleens.framework.Screen;


public abstract class GLGame extends Activity implements Game, GLSurfaceView.Renderer {
    enum GLGameState {
        Initialized,
        Running,
        Paused,
        Finished,
        Idle
    }

    GLSurfaceView glView;
    GLGraphics    glGraphics;
    Audio         audio;
    Input         input;
    FileIO        fileIO;
    Screen        screen;
    GLGameState   state = GLGameState.Initialized;
    Object        stateChanged = new Object();
    long          startTime = System.nanoTime();

    @Override
    protected void onCreate(Bundle savedInstancestate) {
        super.onCreate(savedInstancestate);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        glView = new GLSurfaceView(this);
        glView.setRenderer(this);
        setContentView(glView);

        glGraphics = new GLGraphics(glView);
        fileIO     = new AndroidFileIO(getAssets());
        audio      = new AndroidAudio(this);
        input      = new AndroidInput(this, glView, 1, 1);
    }

    @Override
    public void onResume() {
        super.onResume();
        glView.onResume();
    }

    @Override
    public void onPause() {
        synchronized (stateChanged) {
            if(isFinishing())
                state = GLGameState.Finished;
            else
                state = GLGameState.Paused;

            while(true) {
                try {
                    stateChanged.wait();
                    break;
                } catch (InterruptedException e) {}
            }
        }

        glView.onPause();
        super.onPause();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glGraphics.setGl(gl);

        synchronized (stateChanged) {
            if(state == GLGameState.Initialized)
                screen = getStartScreen();
            state = GLGameState.Running;
            screen.resume();
            startTime = System.nanoTime();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {}

    public void onDrawFrame(GL10 gl) {
        GLGameState state = null;

        synchronized (stateChanged) {
            state = this.state;
        }

        if(state == GLGameState.Running) {
            float deltaTime = (System.nanoTime() - startTime) / 1000000000.0f;
            startTime = System.nanoTime();

            screen.update(deltaTime);
            screen.present(deltaTime);
        }

        if(state == GLGameState.Paused) {
            screen.pause();
            synchronized (stateChanged) {
                this.state = GLGameState.Idle;
                stateChanged.notifyAll();
            }
        }

        if(state == GLGameState.Finished) {
            screen.pause();
            screen.dispose();
            synchronized (stateChanged) {
                this.state = GLGameState.Idle;
                stateChanged.notifyAll();
            }
        }
    }

    public GLGraphics getGlGraphics() { return glGraphics; }

    public Input getInput() { return input; }

    public FileIO getFileIO() { return fileIO; }

    public Graphics getGraphics() { throw new IllegalStateException("Game is using OpenGL!"); }

    public Audio getAudio() { return audio; }

    public void setScreen(Screen newScreen) {
        if(newScreen == null)
            throw new IllegalArgumentException("Screen must not be null!");

        this.screen.pause();
        this.screen.dispose();
        newScreen.resume();
        newScreen.update(0);
        this.screen = newScreen;
    }

    public Screen getCurrentScreen() { return screen; }

    public Screen getStartScreen() { return null; }

}
