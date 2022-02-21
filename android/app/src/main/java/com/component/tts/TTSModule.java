package com.component.tts;


import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import java.util.Locale;

public class TTSModule extends ReactContextBaseJavaModule {

    public TTSModule(ReactApplicationContext context) {
        super(context);
    }

    private TextToSpeech tts;

    @NonNull
    @Override
    public String getName() {
        return "TTSModule";
    }

    @ReactMethod
    public void speak(ReadableMap map, Callback callback) {
        tts = new TextToSpeech(getCurrentActivity(), new TTSListener(map.getString("text")));
    }

    @ReactMethod
    public void stop() {
        if (tts != null) {
            tts.shutdown();
            tts.stop();
            tts = null;
        }
    }

    private class TTSListener implements TextToSpeech.OnInitListener {

        private String text;

        public TTSListener(String text) {
            super();
            this.text = text;
        }

        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                //设置播放语言
                int result = tts.setLanguage(Locale.CHINESE);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(getCurrentActivity(), "不支持", Toast.LENGTH_SHORT).show();
                } else if (result == TextToSpeech.LANG_AVAILABLE) {
                    //初始化成功之后才可以播放文字
                    //否则会提示“speak failed: not bound to tts engine
                    //TextToSpeech.QUEUE_ADD会将加入队列的待播报文字按顺序播放
                    //TextToSpeech.QUEUE_FLUSH会替换原有文字
                    tts.speak(this.text, TextToSpeech.QUEUE_ADD, null, null);
                }

            } else {
                Log.e("TAG", "初始化失败");
            }
        }
    }
}
