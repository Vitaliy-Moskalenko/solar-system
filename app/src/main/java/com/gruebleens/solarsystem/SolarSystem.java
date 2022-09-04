package com.gruebleens.solarsystem;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.gruebleens.framework.Screen;
import com.gruebleens.framework.impl.GLGame;

public class SolarSystem extends GLGame {
    boolean firstTimecreate = true;

    @Override
    public Screen getStartScreen() {
        return new SolarScreen(this);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);

        if(firstTimecreate) {
            Settings.load(getFileIO());
            Assets.load(this);
            firstTimecreate = false;
        }
        else {
            Assets.reload();
        }
    }

}
