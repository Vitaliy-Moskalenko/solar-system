package com.gruebleens.framework.impl;

import java.io.IOException;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import com.gruebleens.framework.Music;

public class AndroidMusic implements Music, OnCompletionListener {

    MediaPlayer mediaPlayer;
    boolean     isPrepared = false;

    public AndroidMusic(AssetFileDescriptor assFileDescriptor) {
        try {
            mediaPlayer.setDataSource(
                    assFileDescriptor.getFileDescriptor(),
                    assFileDescriptor.getStartOffset(),
                    assFileDescriptor.getLength());

            mediaPlayer.prepare();
            isPrepared = true;

            mediaPlayer.setOnCompletionListener(this);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to load Music.");
        }
    }

    @Override
    public void play() {
        if(mediaPlayer.isPlaying()) return;
        try {
            synchronized (this) {
                if(!isPrepared)
                    mediaPlayer.prepare();

                mediaPlayer.start();
            }
        }
        catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        mediaPlayer.stop();
        synchronized (this) {
            isPrepared = false;
        }
    }

    @Override
    public void pause() {
        if(mediaPlayer.isPlaying())
            mediaPlayer.pause();
    }

    @Override
    public void setLooping(boolean looping) {
        mediaPlayer.setLooping(looping);
    }

    @Override
    public void setVolume(float volume) {
        mediaPlayer.setVolume(volume, volume);
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public boolean isStopped() {
        return !isPrepared;
    }

    @Override
    public boolean isLooping() {
        return mediaPlayer.isLooping();
    }

    @Override
    public void dispose() {
        if(mediaPlayer.isPlaying())
            mediaPlayer.stop();

        mediaPlayer.release();
    }

    public void onCompletion(MediaPlayer player) {
        synchronized (this) {
            isPrepared = false;
        }
    }
}
