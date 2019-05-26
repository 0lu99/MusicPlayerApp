package com.example.musicplayerapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MusicPlayer extends AppCompatActivity {

    Button btn_next, btn_previous, btn_pause, btn_shuffle, btn_repeat;
    TextView songTextLabel;
    SeekBar positionBar;

    static MediaPlayer mp;
    int position;
    int r;
    int s;
    String sname;

    ArrayList<File> Songs;
    Thread updateseekBar;

    Random random = new Random();


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        //initialising variables
        btn_next = (Button) findViewById(R.id.next);
        btn_previous = (Button) findViewById(R.id.previous);
        btn_pause = (Button) findViewById(R.id.pause);
        btn_shuffle = (Button) findViewById(R.id.shuffle);
        btn_repeat = (Button) findViewById(R.id.repeat);
        songTextLabel = (TextView) findViewById(R.id.songLabel);
        positionBar = (SeekBar) findViewById(R.id.seekBar);

        //elapsedTimeLabel = (TextView) findViewById(R.id.elapsedTimeLabel);
        //remainingTimeLabel = (TextView) findViewById(R.id.remainingTimeLabel);



        //Name of the page
        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        updateseekBar = new Thread(){
            @Override
            public void run() {

                int totalDuration = mp.getDuration();
                int currentPosition = 0;

                while (currentPosition<totalDuration) {
                    try {
                        sleep(500);
                        currentPosition = mp.getCurrentPosition();
                        positionBar.setProgress(currentPosition);


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };



        if (mp!=null){
            mp.pause();
        }

        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        Songs = (ArrayList) bundle.getParcelableArrayList("songs");

        //Song Name
        sname = Songs.get(position).getName().toString().replace(".mp3","");

        String songName = i.getStringExtra("songname");

        songTextLabel.setText(songName);
        songTextLabel.setSelected(true);

        position = bundle.getInt("pos",0);

        Uri u = Uri.parse(Songs.get(position).toString());

        //MediaPlayer
        mp = MediaPlayer.create(getApplicationContext(),u);
        mp.seekTo(0);
        mp.start();
        positionBar.setMax(mp.getDuration());

        updateseekBar.start();

        positionBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        positionBar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.SRC_IN);

        //updating position of SeekBar
        positionBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.seekTo(seekBar.getProgress());
            }
        });

        //pause button function
        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mp.isPlaying()){
                    btn_pause.setBackgroundResource(R.drawable.icon_play);
                    mp.pause();
                }else{
                    btn_pause.setBackgroundResource(R.drawable.icon_pause);
                    mp.start();
                }

            }
        });

        //next button function
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mp.stop();
                mp.release();
                position = ((position+1)%Songs.size());

                Uri u = Uri.parse(Songs.get(position).toString());

                mp = MediaPlayer.create(getApplicationContext(),u);

                sname = Songs.get(position).getName().toString().replace(".mp3","");
                songTextLabel.setText(sname);

                mp.start();

            }
        });

        //previous button function
        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mp.stop();
                mp.release();

                position = ((position-1)<0)?(Songs.size()-1):(position-1);
                Uri u = Uri.parse(Songs.get(position).toString());
                mp = MediaPlayer.create(getApplicationContext(),u);

                sname = Songs.get(position).getName().toString().replace(".mp3","");
                songTextLabel.setText(sname);

                mp.start();
            }
        });

        //shuffle button function
        btn_shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(s%2==0){
                    btn_shuffle.setBackgroundResource(R.drawable.icon_shuffle);
                    ++s;
                }else{

                    Collections.shuffle(Songs);
                    btn_shuffle.setBackgroundResource(R.drawable.icon_shuffle_clicked);
                    ++s;
                }

            }
        });

        //repeat button function
        btn_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(r%2==0){
                    mp.setLooping(false);
                    btn_repeat.setBackgroundResource(R.drawable.icon_repeat);
                    ++r;
                }else{
                    btn_repeat.setBackgroundResource(R.drawable.icon_repeat_clicked);
                    mp.setLooping(true);
                    ++r;
                }
            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);

    }
}
