package com.jbak2.receiver;

import com.jbak2.JbakKeyboard.ServiceJbKbd;
import com.jbak2.ctrl.Notif;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

public class NotifReceiver extends BroadcastReceiver {
	static final String TAG = "jbKbd2";
    private ServiceJbKbd mIME;

	public NotifReceiver(ServiceJbKbd ime) {
	 	super();
    	mIME = ime;
		Log.i(TAG, "NotificationReceiver created, ime=" + mIME);
	}
	
    @Override
    public void onReceive(Context context, Intent intent) {

    	String act = intent.getAction();
    	if (act.compareTo(Notif.ACTION_SHOW)==0) {
    		InputMethodManager imm = (InputMethodManager)
    	        	context.getSystemService(Context.INPUT_METHOD_SERVICE);
   			if (imm != null) {
   				imm.showSoftInputFromInputMethod(mIME.mToken, InputMethodManager.SHOW_FORCED);
   			}
    	}
	}
}
