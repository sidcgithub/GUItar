package com.example.siddharth.libpd;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;


/**
 * Created by Avtopalm and Siddharth
 */

public class MainActivity extends Activity {
    private TouchScreen tScr;
    private View.OnTouchListener tListener;
    private GtSoundPlayer sPlayer;
    private Display display;
    private Point scrSize;

    public static Socket timbre ;

    private PdUiDispatcher dispatcher;


    private void initPD() throws IOException{
        int sampleRate = AudioParameters.suggestSampleRate();
        PdAudio.initAudio(sampleRate,0,2,8,true);
        dispatcher = new PdUiDispatcher();
        PdBase.setReceiver(dispatcher);
    }

    private void loadPDPatch() throws IOException
    {
        File dir = getFilesDir();

        IoUtils.extractZipResource(getResources().openRawResource(R.raw.trial8), dir, true);


        File pdPatch = new File(dir, "trial8.pd");

        PdBase.openPatch(pdPatch.getAbsolutePath());




    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            initPD();
            loadPDPatch();


        }catch (IOException e)
        {

        }

//        new Thread()
//        {
//            @Override
//            public void run() {
//                super.run();
//                try {
//                    timbre = new Socket("localhost",4000);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//
//        }.start();

        //LayoutInflater linf = new getLayoutInflater();
        //setContentView(R.layout.main);
        tScr = new TouchScreen(this);
        display = getWindowManager().getDefaultDisplay();
        scrSize = new Point();
        display.getSize(scrSize);
        //tScr.setZOrderOnTop(true);
        setContentView(tScr);

        //addContentView(tScr, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
    }

    @Override
    protected void onResume() {
        super.onResume();


        PdBase.release();
        dispatcher.release();



        try {
            initPD();
            loadPDPatch();
        } catch (IOException e) {
            e.printStackTrace();
        }



        if(PdAudio.isRunning())
        {

            PdAudio.stopAudio();
        }
        PdAudio.startAudio(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PdAudio.stopAudio();

    }

    protected void onStop()
    {
        super.onStop();
        PdAudio.stopAudio();
        PdBase.release();
        dispatcher.release();
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {

        event.getActionIndex();
        if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN
                ||event.getAction() == MotionEvent.ACTION_POINTER_DOWN) {
            if(!PdAudio.isRunning())
            {
                PdAudio.startAudio(this);
            }

            float x = event.getX() / scrSize.x;
            int y = (int) (100*(event.getY() / scrSize.y));//


            PdBase.sendFloat("tmb", (y/5)+1);// y/20 computes the corresponding timbre value




            PdBase.sendFloat("fr", 500 * x + 200);//Send frequency to pd patch







            return true;
        }
        if(event.getAction()==MotionEvent.ACTION_UP)
        {

            PdBase.sendFloat("fr", 0);


            if(PdAudio.isRunning())
            {
                PdAudio.stopAudio();
            }
//            PdBase.sendFloat("fr", 0);

            return true;
        }
        return false;
    }



}