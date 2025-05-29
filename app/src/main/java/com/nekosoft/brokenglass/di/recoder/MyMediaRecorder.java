package com.nekosoft.brokenglass.di.recoder;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class MyMediaRecorder {
    public File myRecAudioFile ;
    private MediaRecorder mMediaRecorder ;
    public boolean isRecording = false ;
    public String TAG = "xxxxx";

    public float getMaxAmplitude() {
        if (mMediaRecorder != null) {
            try {
                return (float) mMediaRecorder.getMaxAmplitude();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return (float) 0;
            }
        } else {
            return (float) 5;
        }
    }

    public File getMyRecAudioFile() {
        return myRecAudioFile;
    }

    public void setMyRecAudioFile(File myRecAudioFile) {
        this.myRecAudioFile = myRecAudioFile;
    }

    /**
     * Recording
     *
     * @return Whether to start recording successfully
     */
    public boolean startRecorder(){
        if (myRecAudioFile == null) {
            return false;
        }
        try {
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC );
            mMediaRecorder.setOutputFile(myRecAudioFile.getAbsolutePath());
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            isRecording = true;
            return true;
        } catch(IOException exception) {
            exception.printStackTrace();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            isRecording = false ;
        }catch(IllegalStateException e){
            e.printStackTrace();
            stopRecording();
            e.printStackTrace();
            isRecording = false ;
        }catch (Exception e){
            Log.d("tag123", "startRecorder_74:"+e.getMessage());
        }
        return false;
    }




    public void stopRecording() {
        if (mMediaRecorder != null){
            if(isRecording){
                try{
                    mMediaRecorder.stop();
                    mMediaRecorder.release();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            mMediaRecorder = null;
            isRecording = false ;
        }
    }




    public void delete() {
        stopRecording();
        if (myRecAudioFile != null) {
            myRecAudioFile.delete();
            myRecAudioFile = null;
        }
    }
}