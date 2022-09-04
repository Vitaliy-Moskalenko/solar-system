package com.gruebleens.framework.impl;

import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLSurfaceView;

public class GLGraphics {

    GLSurfaceView glView;
    GL10          gl;

    GLGraphics(GLSurfaceView glview) {
        this.glView = glview;
    }

    public GL10 getGl() { return gl; }

    void setGl(GL10 gl) { this.gl = gl; }

    public int getWidth() { return glView.getWidth(); }

    public int getHeight() { return glView.getHeight(); }
}