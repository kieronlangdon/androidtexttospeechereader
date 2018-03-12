package hughpearse.textToSpeechReader;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;

public class ReaderActivity extends AppCompatActivity {

    private static final String TAG = "Class-ReaderActivity";
    EditText textBox;
    ArrayList<String> sentences = new ArrayList<String>();
    MyTTSService tts;
    boolean isBound = false;
    Integer offsetStart = 0;
    Integer offsetFinish = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MyTTSService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        setContentView(R.layout.activity_reader);
        textBox = (EditText) findViewById(R.id.readerTextArea);
        Intent intentExtras = getIntent();
        Bundle extrasBundle = intentExtras.getExtras();
        sentences = extrasBundle.getStringArrayList("sentences");
        //textBox.setText(sentences.toString(), TextView.BufferType.NORMAL);
        for(String sentence : sentences){
            textBox.append(sentence);
        }
        textBox.setKeyListener(null);
        Log.d(TAG, "Binding TTS Service");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void updateUI(String sentence){
        offsetStart = offsetFinish;
        offsetFinish = offsetStart + sentence.length();
        if((offsetStart != offsetFinish) && textBox.requestFocus())
            textBox.setSelection(offsetStart, offsetFinish);
    }

    public void readSentences(){
        for(String sentence : sentences){
            //Log.d(TAG +"Sencence", sentence);
            while(!tts.isInit()){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Log.d(TAG, e.toString());
                }
            }
            tts.speak(sentence, this);
            updateUI(sentence);
            tts.waitToFinishSpeaking();
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyTTSService.MyBinder binder = (MyTTSService.MyBinder) service;
            tts = binder.getService();
            isBound = true;
            new Thread(new Runnable() {
                public void run() {
                    readSentences();
                }
            }).start();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };
}
