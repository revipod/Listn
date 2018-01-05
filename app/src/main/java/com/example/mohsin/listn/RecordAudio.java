package com.example.mohsin.listn;

import android.media.MediaPlayer;
import android.media.MediaRecorder;

import java.io.IOException;

/**
 * Created by Mohsin on 11/26/2017.
 */

class RecordAudio {

    private boolean isRecording = false;
    private static String audioFilePath;

    private static MediaRecorder mediaRecorder;


    public RecordAudio(String path) {
         audioFilePath = path;
     }

     private void readyRecording()
     {
         mediaRecorder = new MediaRecorder();
         mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
         mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
         mediaRecorder.setOutputFile(audioFilePath);
         mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
     }

     public void startRecord() throws IOException {
         readyRecording();
         mediaRecorder.prepare();
         mediaRecorder.start();
         isRecording = true;
     }

     public void stopRecord()
     {
         mediaRecorder.stop();
         mediaRecorder.release();
         mediaRecorder = null;
         isRecording = false;
     }

     public void playAudio() throws IOException {
         MediaPlayer mediaPlayer = new MediaPlayer();
         mediaPlayer.setDataSource(audioFilePath);
         mediaPlayer.prepare();
         mediaPlayer.start();
     }
}
