package com.jbak2.JbakKeyboard;

import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.os.Vibrator;
import android.provider.Settings;

public class VibroThread extends ContentObserver
{
    Vibrator m_vibro;
    private int m_shortVibro = 30;
    private int m_longVibro = 10;
    private int m_repeatVibro = 10;
    public static final int VIBRO_SHORT = 1;
    public static final int VIBRO_LONG = 2;
    public static final int VIBRO_REPEAT = 3;
    boolean m_bLongVibro = false;
    boolean m_bRepeatVibro = false;
    int m_shortType = 0;
    boolean m_bSilent;
    boolean m_bSilentVibro = false;
    Context m_c;
    public static VibroThread inst = null;
    public VibroThread(Context c)
    {
        super(null);
        m_c = c;
        m_c.getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.MODE_RINGER), false, this);
        m_vibro = (Vibrator) c.getSystemService(Service.VIBRATOR_SERVICE);
        readSettings();
    }
    public static VibroThread getInstance(Context c)
    {
        if(inst==null)
            inst = new VibroThread(c);
        return inst;
    }
    void destroy()
    {
        m_c.getContentResolver().unregisterContentObserver(this);
        m_c = null;
        inst = null;
    }
    @Override
    public void onChange(boolean selfChange) 
    {
        m_bSilent = isSilent();
    };
    public void readSettings()
    {
        SharedPreferences p = st.pref();
        try{
            m_shortType = Integer.decode(p.getString(st.PREF_KEY_USE_SHORT_VIBRO, st.STR_ONE));
            m_bLongVibro = p.getBoolean(st.PREF_KEY_USE_LONG_VIBRO, true);
            m_shortVibro = Integer.decode(p.getString(st.PREF_KEY_VIBRO_SHORT_DURATION, JbKbdPreference.DEF_SHORT_VIBRO));
            m_longVibro = Integer.decode(p.getString(st.PREF_KEY_VIBRO_LONG_DURATION, JbKbdPreference.DEF_LONG_VIBRO));
            m_bRepeatVibro = p.getBoolean(st.PREF_KEY_USE_REPEAT_VIBRO, m_bLongVibro);
            m_repeatVibro = Integer.decode(p.getString(st.PREF_KEY_VIBRO_REPEAT_DURATION, JbKbdPreference.DEF_LONG_VIBRO));
            m_bSilentVibro = p.getBoolean(st.PREF_KEY_VIBRO_IN_SILENT_MODE, false);
        }
        catch (Throwable e) {
        }
        m_bSilent = isSilent();
    }
    public final boolean hasVibroOnPress()
    {
        if(m_bSilent&&!m_bSilentVibro)return false;
        return m_shortType==2;
    }
    final void runVibro(final int vibroType)
    {
        if(m_bSilent&&!m_bSilentVibro)
            return;
        Runnable r = null;
        if(vibroType==VIBRO_SHORT&&m_shortType!=0)
            r = m_runShort;
        else if(vibroType==VIBRO_REPEAT&&m_bRepeatVibro)
            r = m_runRepeat;
        else if(vibroType==VIBRO_LONG&&m_bLongVibro){
            r = m_runLong;
        }
        if(r!=null)
            new Thread(r).run();
    }
    void runForce(final int interval)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                m_vibro.vibrate(interval);
            }
        }).run();
    }
    final boolean isSilent()
    {
        int set = Settings.System.getInt(m_c.getContentResolver(),Settings.System.MODE_RINGER,-1 );
        return set==0;
    }
    Runnable m_runShort = new Runnable()
    {
        @Override
        public void run()
        {
            m_vibro.vibrate(m_shortVibro);
        }
    };
    Runnable m_runLong = new Runnable()
    {
        @Override
        public void run()
        {
            m_vibro.vibrate(m_longVibro);
        }
    };
    Runnable m_runRepeat = new Runnable()
    {
        @Override
        public void run()
        {
            m_vibro.vibrate(m_repeatVibro);
        }
    };
}
