package com.gruebleens.framework.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import com.gruebleens.framework.impl.GLGraphics;


public class Vertices3 {
    final boolean hasColor;
    final boolean hasTextCoords;
    final int     vertexSize;
    final int[]   tmpBuffer;
    final ShortBuffer indices;
    final IntBuffer   vertices;
    final GLGraphics  glGraphics;

    public Vertices3(GLGraphics glGraphics, int maxVertices, int maxIndices,
                     boolean hasColor, boolean hasTextCoords) {
        this.glGraphics    = glGraphics;
        this.hasColor      = hasColor;
        this.hasTextCoords = hasTextCoords;
        this.vertexSize    = (3 + (hasColor ? 4 : 0) + (hasTextCoords ? 2 : 0)) * 4;
        this.tmpBuffer     = new int[maxVertices * vertexSize / 4];

        ByteBuffer buffer = ByteBuffer.allocateDirect(maxVertices * vertexSize);
        buffer.order(ByteOrder.nativeOrder());
        vertices = buffer.asIntBuffer();

        if(maxIndices > 0) {
            buffer = ByteBuffer.allocateDirect(maxIndices * Short.SIZE / 8);
            buffer.order(ByteOrder.nativeOrder());
            indices = buffer.asShortBuffer();
        }
        else
            indices = null;
    }

    public void setVertices(float[] vertices, int offset, int length) {
        this.vertices.clear();

        int len = offset + length;
        for(int i=offset, j=0; i<len; i++, j++)
            tmpBuffer[j] = Float.floatToIntBits(vertices[i]);

        this.vertices.put(tmpBuffer, 0, length);
        this.vertices.flip();
    }

    public void setIndices(short[] indices, int offset, int length) {
        this.indices.clear();
        this.indices.put(indices, offset, length);
        this.indices.flip();
    }

    public void bind() {
        GL10 gl = glGraphics.getGl();

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        vertices.position(0);
        gl.glVertexPointer(3, GL10.GL_FLOAT, vertexSize, vertices);

        if(hasColor) {
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
            vertices.position(3);
            gl.glColorPointer(4, GL10.GL_FLOAT, vertexSize, vertices);
        }

        if(hasTextCoords) {
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            vertices.position(hasColor ? 7 : 3);
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, vertexSize, vertices);
        }
    }

    public void draw(int primitiveType, int offset, int numVertices) {
        GL10 gl = glGraphics.getGl();

        if(indices != null) {
            indices.position(offset);
            gl.glDrawElements(primitiveType, numVertices, GL10.GL_UNSIGNED_SHORT, indices);
        }
        else {
            gl.glDrawArrays(primitiveType, offset, numVertices);
        }
    }

    public void unbind() {
        GL10 gl = glGraphics.getGl();
        if(hasTextCoords)
            gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        if(hasColor)
            gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
    }
}