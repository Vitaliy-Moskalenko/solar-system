package com.gruebleens.framework.impl;

import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.Paint.Style;

import com.gruebleens.framework.Graphics;
import com.gruebleens.framework.Pixmap;


public class AndroidGraphics implements Graphics {

    AssetManager assManager;
    Bitmap       frameBuffer;
    Canvas       canvas;
    Paint        paint;
    Rect         srcRect = new Rect();
    Rect         dstRect = new Rect();

    public AndroidGraphics(AssetManager assMan, Bitmap frameBuffer) {
        this.assManager  = assMan;
        this.frameBuffer = frameBuffer;
        this.canvas      = new Canvas(frameBuffer);
        this.paint       = new Paint();
    }

    public Pixmap newPixmap(String fileName, PixmapFormat format) {
        Config config = null;

        if(format == PixmapFormat.RGB565)
            config = Config.RGB_565;
        else if(format == PixmapFormat.ARGB4444)
            config = Config.ARGB_4444;
        else
            config = Config.ARGB_8888;

        Options options = new Options();
        options.inPreferredConfig = config;

        InputStream in = null;
        Bitmap bitmap = null;

        try {
            in = assManager.open(fileName);
            bitmap = BitmapFactory.decodeStream(in);

            if(bitmap == null)
                throw new RuntimeException("Faiied loading bitmap from sream " + fileName);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed loading bitmap from asset " + fileName);
        }
        finally {
            try {
                if (in != null) in.close();
            } catch (IOException e) {}
        }

        if(bitmap.getConfig() == Config.RGB_565)
            format = PixmapFormat.RGB565;
        else if (bitmap.getConfig() == Config.ARGB_4444)
            format = PixmapFormat.ARGB4444;
        else
            format = PixmapFormat.ARGB8888;

        return new AndroidPixmap(bitmap, format);
    }

    // Extracts r, g, b components from 32-bit ARGB color param and clear buffer with this color
    public void clear(int color) {
        canvas.drawRGB((color & 0xff0000) >> 16, (color & 0xff00) >> 8, (color & 0xff));
    }

    public void drawPixel(int x, int y, int color) {
        paint.setColor(color);
        canvas.drawPoint(x, y, paint);
    }

    public void drawLine(int x, int y, int x2, int y2, int color) {
        paint.setColor(color);
        canvas.drawLine(x, y, x2, y2, paint);
    }

    public void drawRect(int x, int y, int width, int height, int color) {
        paint.setColor(color);
        paint.setStyle(Style.FILL);
        canvas.drawRect(x, y, x + width - 1, y + height - 1, paint);
    }

    public void drawPixmap(Pixmap pixmap, int x, int y, int srcX, int srcY, int srcWidth, int srcHeight) {
        srcRect.left   = srcX;
        srcRect.top    = srcY;
        srcRect.right  = srcX + srcWidth - 1;
        srcRect.bottom = srcY + srcHeight - 1;

        dstRect.left   = x;
        dstRect.top    = y;
        dstRect.right  = x + srcWidth - 1;
        dstRect.bottom = y + srcHeight - 1;

        canvas.drawBitmap(((AndroidPixmap)pixmap).bitmap, srcRect, dstRect, null);
    }

    public void drawPixmap(Pixmap pixmap, int x, int y) {
        canvas.drawBitmap(((AndroidPixmap)pixmap).bitmap, x, y, paint);
    }

    public int getWidth() {
        return frameBuffer.getWidth();
    }

    public int getHeight() {
        return frameBuffer.getHeight();
    }
}