package com.jbak2.ctrl;

import android.app.ProgressDialog;
import android.content.Context;
import com.jbak2.JbakKeyboard.st;
import com.jbak2.JbakKeyboard.st.UniObserver;

/** Класс для вывода прогресс-бара в долгоиграющих операциях*/
public abstract class ProgressOperation extends st.SyncAsycOper
{
    /** Диалог прогресс-бара*/
    public ProgressDialog m_progress;
    public int m_position=0;
    public int m_total=0;
    public boolean m_bCancel = false;
    SameThreadTimer m_tt;
    public ProgressOperation(UniObserver obs,Context c)
    {
        super(obs);
        m_progress = new ProgressDialog(c);
        m_progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        m_progress.setMax(100);
        m_progress.setIndeterminate(false);
        m_progress.setTitle(st.STR_NULL);
        m_progress.setMessage(st.STR_NULL);
        m_tt = new SameThreadTimer(0,500)
        {
            @Override
            public void onTimer(SameThreadTimer timer)
            {
                if(m_progress==null)
                {
                    cancel();
                    return;
                }
                onProgress();
            }
        };
    }
    public void start()
    {
        startAsync();
        m_progress.show();
        m_tt.start();
    }
    public int getPercent()
    {
        if(m_total==0)
            return 0;
        long pc = m_position;
        return (int)pc*100/m_total;
    }
    @Override
    protected void onProgressUpdate(Void... values) 
    {
        if(m_progress!=null)
        {
            m_progress.dismiss();
            m_progress = null;
        }
        super.onProgressUpdate(values);
    };
/** Функция вызывается, когда необходимо перерисовать прогресс-бар (по таймеру)*/    
    public abstract void onProgress();
}
