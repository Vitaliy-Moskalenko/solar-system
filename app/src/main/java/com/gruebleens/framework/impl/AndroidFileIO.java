package com.gruebleens.framework.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.res.AssetManager;
import android.os.Environment;
import com.gruebleens.framework.FileIO;


public class AndroidFileIO implements FileIO {

    AssetManager assManager;
    String extStoragePath;

    public AndroidFileIO(AssetManager assets) {
        this.assManager = assets;
        this.extStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    }

    public InputStream readAsset(String filename) throws IOException {
        return assManager.open(filename);
    }

    public InputStream readFile(String filename) throws IOException {
        return new FileInputStream(extStoragePath + filename);
    }

    public OutputStream writeFile(String filename) throws IOException {
        return new FileOutputStream(extStoragePath + filename);
    }
}
