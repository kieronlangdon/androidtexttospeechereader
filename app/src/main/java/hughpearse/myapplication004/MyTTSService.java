package hughpearse.myapplication004;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;

public class MyTTSService extends Service {

    private static final String TAG = "Class-MyTTSService";
    private TextToSpeech tts;
    private boolean isInit = false;
    private final IBinder myBinder = new MyBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Creating TTS Service");
        Context context = this.getApplicationContext();
        this.tts = new TextToSpeech(context, onInitListener);
        this.tts.setOnUtteranceProgressListener(utteranceProgressListener);
        Log.d(TAG, "TTS Service Created");
    }

    private TextToSpeech.OnInitListener onInitListener =  new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "This Language is not supported");
                } else {
                    //init success
                    isInit = true;
                    Log.d(TAG, "TTS Initialized.");
                }
            } else {
                isInit = false;
                Log.e("TTS", "Initilization Failed!");
            }
        }
    };

    public boolean isSpeaking(){
        return tts.isSpeaking();
    }

    public boolean isInit(){
        return isInit;
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        isInit = false;
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    public void waitToFinishSpeaking() {
        while (tts.isSpeaking()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Log.d(TAG, e.toString());
            }
        }
    }

    public void speak(String text, AppCompatActivity appCompatActivity) {
        //Log.d(TAG, "Speak" + text);
        appCompatActivity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            HashMap<String, String> param = new HashMap<>();
            param.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_MUSIC));
            tts.speak(text, TextToSpeech.QUEUE_ADD, null);
        } else {
            String utteranceId=this.hashCode() + "";
            Bundle bundle = new Bundle();
            bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC);
            tts.speak(text, TextToSpeech.QUEUE_ADD, null, null);
        }
    }

    private UtteranceProgressListener utteranceProgressListener = new UtteranceProgressListener() {
        @Override
        public void onStart(String utteranceId) {
        }

        @Override
        public void onDone(String utteranceId) {
        }

        @Override
        public void onError(String utteranceId) {
            Log.e(TAG, "Error while trying to synthesize sample text");
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Binding TTS Service");
        return myBinder;
    }

    public class MyBinder extends Binder {
        MyTTSService getService() {
            return MyTTSService.this;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }
}
