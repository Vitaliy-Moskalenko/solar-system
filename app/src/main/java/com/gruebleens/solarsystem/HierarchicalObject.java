package com.gruebleens.solarsystem;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import com.gruebleens.framework.opengl.Vertices3;


public class HierarchicalObject {
    public boolean hasParent;
    public float x, y, z;
    public float scale = 1;
    public float rotationY, rotationParent;

    public final List<HierarchicalObject> children = new ArrayList<HierarchicalObject>();
    public final Vertices3 mesh;

    public HierarchicalObject(Vertices3 mesh, boolean hasParent) {
        this.mesh = mesh;
        this.hasParent = hasParent;
    }

    public void update(float dT) {
        rotationY += 45 * dT;
        rotationParent += 20 * dT;

        int len = children.size();
        for(int i=0; i<len; i++)
            children.get(i).update(dT);
    }

    public void render(GL10 gl) {
        gl.glPushMatrix();

        if(hasParent)
            gl.glRotatef(rotationParent, 0, 1, 0);

        gl.glTranslatef(x, y, z);
        gl.glPushMatrix();
        gl.glRotatef(rotationY, 0, 1, 0);
        gl.glScalef(scale, scale, scale);

        mesh.draw(GL10.GL_TRIANGLES, 0, 36);

        gl.glPopMatrix();

        int len = children.size();
        for(int i=0; i<len; i++)
            children.get(i).render(gl);

        gl.glPopMatrix();
    }
}