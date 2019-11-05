package com.jbak2.receiver;

import java.util.ArrayList;

import com.jbak2.JbakKeyboard.R;
import com.jbak2.JbakKeyboard.ServiceJbKbd;
import com.jbak2.JbakKeyboard.st;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.speech.RecognizerIntent;

public class VRTest
{
    BroadcastReceiver m_recv = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            ArrayList<String> matches = intent.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            if(ServiceJbKbd.inst!=null)
            {
                ServiceJbKbd.inst.onVoiceRecognition(matches);
            }
        }
    };
    String bcastAction="Jbtm.VOICE_RECOGNIZER_RESULTS";
    void startVoice()
    {
        ServiceJbKbd.inst.forceHide();
        ServiceJbKbd.inst.registerReceiver(m_recv, new IntentFilter(bcastAction));
        Intent activityIntent = new Intent(ServiceJbKbd.inst, ServiceJbKbd.class);
        // this intent wraps results activity intent
        PendingIntent resultsPendingIntent = PendingIntent.getBroadcast(ServiceJbKbd.inst, 0, new Intent(bcastAction), 0);
        PendingIntent.getService(ServiceJbKbd.inst, 0, activityIntent, 0);
        // this intent calls the speech recognition
        Intent voiceIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        voiceIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, st.c().getString(R.string.ime_name));
        voiceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                |Intent.FLAG_ACTIVITY_NO_USER_ACTION
                                |Intent.FLAG_ACTIVITY_NO_HISTORY
                                |Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY
                                |Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                                |Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP
                                );
        voiceIntent.putExtra(RecognizerIntent.EXTRA_RESULTS_PENDINGINTENT, resultsPendingIntent);
        voiceIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        // this intent wraps voice recognition intent
        PendingIntent pendingIntent = PendingIntent.getActivity(ServiceJbKbd.inst, 0, voiceIntent, 0);
        try
        {
            pendingIntent.send(0, m_fin, null);
        } catch (Throwable e)
        {
        }
    }
    PendingIntent.OnFinished m_fin = new PendingIntent.OnFinished()
    {
        @Override
        public void onSendFinished(PendingIntent pendingIntent, Intent intent,int resultCode, String resultData, Bundle resultExtras)
        {
            
        }
    };
}
