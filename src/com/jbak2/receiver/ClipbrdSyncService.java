package com.jbak2.receiver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

import com.jbak2.JbakKeyboard.R;
import com.jbak2.JbakKeyboard.st;
import com.jbak2.ctrl.SameThreadTimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
/** Сервис для синхронизации мультибуфера поо таймеру */
public class ClipbrdSyncService extends SameThreadTimer 
{
	FileWriter wr;
	String str;
	String fname;
	long time_last_rec = -1;
	long time = 0;
	String RECORD = "RECORD №";
	public static String SAVEDIR = "save_clipboard";
	int KILOBYTE = 1024;
	int cnt = 0;
	public static ClipbrdSyncService inst;
    Timer m_timer;

    public ClipbrdSyncService(Context c) {
		super(st.cs_dur*1000*60, st.cs_dur*1000*60);
// на время отладки		
//		super(st.cs_dur*1000, st.cs_dur*1000);
        inst = this;
        c.registerReceiver(m_recv, new IntentFilter());
        save();
        start();
	}
    public void delete(Context c)
    {
        inst = null;
        c.unregisterReceiver(m_recv);
        cancel();
    }
    void save()
    {
		int len = 0;
		try {
        	Cursor c = st.stor().getClipboardCursor();
            if(c==null)
              return;
            fname = st.getSettingsPath()+SAVEDIR;
			File file = new File(fname);
			if (!file.exists()&&!file.isDirectory()){
					if (!file.mkdir())
						return;
			}
			fname+= "/save";
			if (st.fl_sync_create_new_file){
    			try {
    				SimpleDateFormat sdf = new SimpleDateFormat("_dd-MM-yy_HH.mm.ss");
    				fname += sdf.format(new Date());
    			} catch(Throwable e) {
    			}
			}
			fname+=".txt";
			file = new File(fname);
			if (file.exists()&&!file.canWrite()){
				st.toast(R.string.cs_error_write);
				return;
			}
			time = c.getLong(2);
    		if (time==time_last_rec)
    			return;
    		else
    			time_last_rec = time;
            cnt = 1;
			wr = new FileWriter(fname, false);
            do
            {
           		str = c.getString(0)+st.STR_LF;
                if (str.isEmpty())
                	continue;
                if(cnt<=st.cs_cnt){
                	len = str.length();
                	if (len>st.cs_size*KILOBYTE)
                		len = st.cs_size*KILOBYTE;
                	str = str.substring(0, len)+st.STR_LF+st.STR_LF;
                	wr.write(RECORD+cnt+st.STR_LF);
                	wr.write(str);
                }else
                	break;
                cnt++;
            }while(c.moveToPrevious());
            c.close();
			wr.close();
			if (!st.fl_clipbrd_sync_msg)
				st.toast(R.string.cs_sync_msg);
		} catch (IOException e) {
		}
    }
    BroadcastReceiver m_recv = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
        	if (st.fl_sync){
        	}
//            String act = intent.getAction();
//            if(Intent.ACTION_SCREEN_ON.equals(act))
//            {
//                start();
//            }
//            if(Intent.ACTION_SCREEN_OFF.equals(act))
//            {
//                cancel();
//            }
        }
    };
	@Override
	public void onTimer(SameThreadTimer timer) {
		save();
	}
}
