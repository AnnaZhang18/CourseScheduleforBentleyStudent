package com.example.eleanor.coursescheduleforbentleystudent;

import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;
/*
This activity file aims to perform the loading function after user entered correct email address and password,
before entering the student menu activity.
 */
public class Animation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);

        /*
       Insert the animation xml file to the Image view
       Create a timer and a animation routine
        */
        ImageView img = (ImageView) findViewById(R.id.loading);
        img.setBackgroundResource(R.drawable.animation);

        AnimationRoutine1 task1 = new AnimationRoutine1();
  /*
    Define the animation start one second after it is called
     */
        Timer t = new Timer();
        t.schedule(task1, 1000);


    }

    class AnimationRoutine1 extends TimerTask {
        /*
        Start the animation.
         */
        @Override
        public void run() {
            ImageView img = (ImageView) findViewById(R.id.loading);
            AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();
            frameAnimation.start();
        }
    }

}
