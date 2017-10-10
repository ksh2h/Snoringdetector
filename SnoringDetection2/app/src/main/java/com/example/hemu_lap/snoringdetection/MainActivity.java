package com.example.hemu_lap.snoringdetection;

import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity  {

    private static final String TAG = "Mainactivity";
    Button button1,button2;
    Thread runner;
    private static double mEMA=0.0;
    static final private int Threshold = 1500;
    public static final int RequestPermissionCode = 1;
    String SavePath = null;

    MediaRecorder mediaRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1 = (Button)findViewById(R.id.button1);
        button2 = (Button)findViewById(R.id.button2);

        //button1.setEnabled(false);
        button2.setEnabled(false);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CheckPermission()) {
                    SavePath = "AudioRecord.3gpp";
                    MediaRecorderReady();

                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();

                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    button1.setEnabled(false);
                    button2.setEnabled(true);


                    Toast.makeText(MainActivity.this, "Recording", Toast.LENGTH_SHORT).show();

                    final Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        public void run() {
                            int amp = mediaRecorder.getMaxAmplitude();
                            if(amp>Threshold)Toast.makeText(MainActivity.this,"Threshold crossed",Toast.LENGTH_SHORT).show();
                            handler.postDelayed(this,1000);
                        }

                    };

                }else{
                    RequestPermission();
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaRecorder.stop();
                button2.setEnabled(false);
                button1.setEnabled(true);

                Toast.makeText(MainActivity.this, "Recorded" , Toast.LENGTH_SHORT).show();

            };
        });
    }

    public void MediaRecorderReady(){
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        //mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //mediaRecorder.setOutputFile(SavePath);
    }


    private void RequestPermission(){
        ActivityCompat.requestPermissions(MainActivity.this , new String[]{WRITE_EXTERNAL_STORAGE , RECORD_AUDIO} , RequestPermissionCode);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case RequestPermissionCode:
                if(grantResults.length>0){
                    boolean StoragePermission = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if( RecordPermission){
                        Toast.makeText(this,"Permission is Granted" , Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(this,"Permission Denied" , Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    public boolean CheckPermission(){
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

}
