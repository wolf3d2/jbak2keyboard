package com.jbak2.ctrl;

import android.os.Handler;

/** Таймер, который запускается всегда в том же потоке, в котором он был создан*/
public abstract class SameThreadTimer
{
    public int m_delay;
    public int m_period;
    Handler m_h = new Handler()
    {
        @Override
        public void handleMessage(android.os.Message msg) 
        {
            callTimer();
        };
    };
    final void callTimer()
    {
        onTimer(this);
        if(m_period!=0)
        {
            m_h.sendMessageDelayed(m_h.obtainMessage(1), m_period);
        }
    }
    public SameThreadTimer(int delay,int period)
    {
        m_delay = delay;
        m_period = period;
    }
    public void start()
    {
        m_h.sendMessageDelayed(m_h.obtainMessage(1), m_delay);
    }
    public void cancel()
    {
        m_h.removeMessages(1);
    }
    public abstract void onTimer(SameThreadTimer timer);
}
