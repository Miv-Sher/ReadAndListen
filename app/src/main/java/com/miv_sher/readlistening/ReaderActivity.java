package com.miv_sher.readlistening;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.accessibility.AccessibilityManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import company.JSonModifier;
import company.Shingles;
import company.WordsInfo;


public class ReaderActivity extends ActionBarActivity {

    BookView webView;
    TextView percent;
    ImageView playButton;
    SeekBar seekBar;
    boolean isPlaying = false;
    WordsInfo wordsInfo;
    MediaPlayer mediaPlayer;
    String audioFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + "/section";
    int currentSection = 0;

    String name;
    String path;
    int audio;
    String transcript="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reader);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        path = intent.getStringExtra("path");
        audio = intent.getIntExtra("audio", 0);
        transcript = intent.getStringExtra("json");

        setTitle(name);




        webView = (BookView)findViewById(R.id.bookview);
        webView.loadBook(path);
        webView.readerActivity = this;

        percent = (TextView)findViewById(R.id.percentage);
        playButton = (ImageView) findViewById(R.id.play_button);
        seekBar = (SeekBar) findViewById(R.id.progress_bar);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isReady) {
                    isPlaying = !isPlaying;
                    if (isPlaying) {
                        play();
                    } else {
                        pause();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Wait for loading to finish", Toast.LENGTH_LONG).show();
                }
            }
        });

        LoadingProcess task = new LoadingProcess();
        task.execute();

    }

    public boolean isReady = false;

    class LoadingProcess extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {

            String json = webView.readFile(transcript);
            webView.parsePlainText();
            wordsInfo = new JSonModifier().parseFromString(json);

            mediaPlayer = MediaPlayer.create(getApplicationContext(), audio);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    currentSection++;
                    openAudioFile();
                }
            });
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

            return  null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            isReady = true;
            playButton.setImageResource(R.drawable.ic_play_circle_filled_black_48dp);
        }
    }


    public void play() {

            isPlaying = true;
            playButton.setImageResource(R.drawable.ic_pause_circle_filled_black_48dp);
            mediaPlayer.start();
    }
    public void pause() {
            isPlaying = false;
            playButton.setImageResource(R.drawable.ic_play_circle_filled_black_48dp);
            mediaPlayer.pause();
            findInText();
    }

    public void findInAudio(String selected) {
        int time = (int)(Shingles.searchTime(wordsInfo, selected, Shingles.SHINGLE_SIZE) * 1000);
        if (time >= 0) {
            mediaPlayer.seekTo(time);
            play();
        } else      {
            Toast.makeText(this, "missing audio",Toast.LENGTH_LONG).show();
        }
    }

    private  void findInText() {
        double currentTime =  mediaPlayer.getCurrentPosition()/1000.0;
        String plainText = webView.getPlainText();
        int index = Shingles.searchFromTime(webView.getPlainText(), wordsInfo, currentTime, Shingles.SHINGLE_SIZE);
        if (index >= 0) {
            webView.findAllAsync(getPart(plainText, index));
            webView.findNext(true);
        }
        else {
            Toast.makeText(this, "missing text",Toast.LENGTH_LONG).show();
        }

    }

    private String getPart(String plainText, int index) {
        String[] array = plainText.split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i< 20; i++) {
            if (index + i >= array.length) break;
            sb.append(array[index + i] + " ");
        }
        return sb.toString();
    }
    public void openAudioFile()
    {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(audioFolder + currentSection + ".mp3");
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            mediaPlayer.start();
            String json = webView.readFile(transcript);
            wordsInfo = new JSonModifier().parseFromString(json);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reader, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            pause();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
