package com.example.musicplayerapplication;

import android.Manifest;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    ListView ListofSongs;
    SearchView songSearch;
    String[] items;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //screen layout will show what's been designed in the activity_main xml file

        ListofSongs = (ListView) findViewById(R.id.songList); //assigning variables to the id of the objects on screen
        songSearch = (SearchView) findViewById(R.id.searchBar);


        runtimePermission();

    }

    public void runtimePermission(){

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        display();

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    public ArrayList<File> findSong(File file){

        ArrayList<File> arrayList = new ArrayList<>();

        File[] files = file.listFiles();

        for(File singleFile: files){

            if (singleFile.isDirectory() && !singleFile.isHidden()){

                arrayList.addAll(findSong(singleFile));
            } else {
                if (singleFile.getName().endsWith(".mp3")){
                    arrayList.add(singleFile);
                }
            }
        }

        return arrayList;
    }

    void display(){

        final ArrayList<File> Songs = findSong(Environment.getExternalStorageDirectory());

        items = new String[Songs.size()];

        for (int i=0; i<Songs.size(); i++){

            items[i] = Songs.get(i).getName().toString().replace(".mp3","");

        }

        final ArrayAdapter<String> Adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items);
        ListofSongs.setAdapter(Adapter);

        ListofSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                String songName = ListofSongs.getItemAtPosition(i).toString();

                startActivity(new Intent(getApplicationContext(), MusicPlayer.class)
                .putExtra("songs",Songs).putExtra("songname",songName)
                .putExtra("pos",i));

            }
        });

        songSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {

                Adapter.getFilter().filter(text);
                return false;
            }
        });



    }
}
