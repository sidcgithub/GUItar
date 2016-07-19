package com.example.siddharth.libpd;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;


/**
 * Created by Avtopalm and Siddharth
 */

public class MainActivity extends Activity {
    private TouchScreen tScr;
    private View.OnTouchListener tListener;
    private GtSoundPlayer sPlayer;
    private Display display;
    private Point scrSize;

    private ArrayList<CuePoint> cuePoints;

    private boolean addNoteMode;

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

            IoUtils.extractZipResource(getResources().openRawResource(R.raw.trial7),dir,true);


        File pdPatch = new File(dir, "trial7.pd");

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
        setContentView(R.layout.activity_main);
        //tScr = new TouchScreen(this);
        display = getWindowManager().getDefaultDisplay();
        this.scrSize = new Point();
        display.getSize(scrSize);

        cuePoints = new ArrayList<CuePoint>();
        //tScr.setZOrderOnTop(true);
        //setContentView(tScr);

        addNoteMode = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        PdAudio.startAudio(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PdAudio.stopAudio();
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {

        CuePoint testPoint = new CuePoint(event.getX(), event.getY(), this.scrSize);
        // Mode "add note" is active
        if (addNoteMode) {
            CuePoint newPoint = new CuePoint(event.getX(), event.getY(), this.scrSize);
            cuePoints.add(newPoint);
            return true;
        }
        // Mode "add note" is not active
        else {
            if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN) {

                //boolean inMarkedArea = false;

                CuePoint currentPoint;
                if (cuePoints.size() == 0) {
                    currentPoint = new CuePoint(0, 0, this.scrSize);
                }

                // Check if touch is in the marked area
                for (int i = 0; i < cuePoints.size(); i++) {

                    // Touch in marked area
                    if (cuePoints.get(i).areaIncludes(event.getX(), event.getY())) {
                        //inMarkedArea = true;
                        currentPoint = cuePoints.get(i);
                        return inMarked(event, currentPoint);
                    }
                }

                return outOfMarked(event);

            }

            if (event.getAction() == MotionEvent.ACTION_UP) {
                PdBase.sendFloat("fr", 0);
                return true;
            }
            return false;
        }
    }

    private boolean inMarked(final MotionEvent event, CuePoint point) {

        PdBase.sendFloat("tmb", point.tmb);

        PdBase.sendFloat("fr", point.freq);

        Log.v("Sound", "Precise note is applied");

        return true;
    }

    private boolean outOfMarked(final MotionEvent event) {

        //Relative position of the touch
        float x = event.getX() / scrSize.x;
        int y = (int) (100*(event.getY() / scrSize.y));

        Log.v("Sound", x + " " + y);

//            PdBase.sendMessage("fr",Float.toString(1000*x+200));

//            String harmonics = "";

//            List<Object> harms = new ArrayList<Object>();
//            for(int i = 1; i<=y; i++)
//            {
//                harms.add((float)(1.000/(float)i));
////                harmonics = harmonics + Float.toString(((float)(1.000/(float)i)))+  " ";
//
//            }

//            String sinesum = "sinesum 32768 "+ harmonics+", normalize 1";

//            Log.v("Harmonics", harms.size() + "");

        PdBase.sendFloat("tmb", y/20); // y/20 computes the corresponding timbre value

//            OutputStreamWriter osw;
//            BufferedWriter out;


//
//            try {
//                osw = new OutputStreamWriter(timbre.getOutputStream());
//                out = new BufferedWriter(osw);
//                out.write(sinesum);
//                out.newLine();
//                out.flush();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }




        PdBase.sendFloat("fr", 1000 * x + 200); //Send frequency to pd patch





//            String sinesum = "sinesum 32768 "+ harmonics+", normalize 1";





        return true;
    }

    public void onCheckboxClick(View view) {

        CheckBox check = (CheckBox)view;

        if (check.isChecked()) {
            this.addNoteMode = true;
            Log.v("Checkbox", "Checkbox is checked");
        }
        else {
            this.addNoteMode = false;
            Log.v("Checkbox", "Checkbox is checked");
        }
    }



}