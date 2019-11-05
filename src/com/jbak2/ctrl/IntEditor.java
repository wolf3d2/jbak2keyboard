package com.jbak2.ctrl;

import android.app.Service;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jbak2.JbakKeyboard.R;
import com.jbak2.JbakKeyboard.st;

public class IntEditor extends LinearLayout
{
    View m_mainView;
    int m_minValue = 0;
    int m_maxValue = 200;
    int m_curValue = 0;
    boolean m_bFromUser = false;
    TextView m_edit;
    OnChangeValue m_listener = null;
    int steps[]=new int[]{1,3,5};
    public IntEditor(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        LayoutInflater li = (LayoutInflater)context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        m_mainView = li.inflate(R.layout.int_editor, this);
        m_edit = (TextView)m_mainView.findViewById(R.id.int_value);
        m_mainView.findViewById(R.id.plus).setOnTouchListener(m_cl);
        m_mainView.findViewById(R.id.minus).setOnTouchListener(m_cl);
    }
    Handler m_handler = new Handler()
    {
      @Override
      public void handleMessage(android.os.Message msg) 
      {
          if(m_downTime!=0)
          {
              boolean inc = msg.arg1==1;
              long ct = System.currentTimeMillis();
              long dt = ct-m_downTime;
              m_interval = 200;
              if(dt>1000)
                  changeValue(inc,steps[2]);
              else if(dt>500)
                  changeValue(inc,steps[1]);
              else
                  changeValue(inc,steps[0]);
              sendPressMessage(inc);
          }
      };
    };
    int m_interval = 400;
    long m_downTime = 0;
    void sendPressMessage(boolean bInc)
    {
        m_handler.sendMessageDelayed(m_handler.obtainMessage(0,bInc?1:0,0),m_interval);
    }
    View.OnTouchListener m_cl = new View.OnTouchListener()
    {
        
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            int act = event.getAction();
            boolean bInc = v.getId()==R.id.plus;
            if(act==MotionEvent.ACTION_DOWN)
            {
                changeValue(bInc,steps[0]);
                m_downTime = System.currentTimeMillis();
                m_interval = 400;
                sendPressMessage(bInc);
            }
            if(act==MotionEvent.ACTION_UP||act==MotionEvent.ACTION_CANCEL)
            {
                m_handler.removeMessages(0);
                m_downTime = 0;
                m_interval = 400;
            }
            
            return false;
        }
    };
    public void setMinAndMax(int min,int max)
    {
        m_minValue = min;
        m_maxValue = max;
    }
    public void changeValue(boolean bIncrement,int step)
    {
        if(bIncrement)
        {
            if(m_curValue+step<=m_maxValue)
                setValue(m_curValue+step);
            else if(m_curValue!=m_maxValue)
                setValue(m_maxValue);
        }
        else
        {
            if(m_curValue-step>=m_minValue)
                setValue(m_curValue-step);
            else if(m_curValue!=m_minValue)
                setValue(m_minValue);
        }
    }
    public void setValue(int value)
    {
        m_curValue = value;
        m_edit.setText(st.STR_NULL+value);
        if(m_listener!=null)
            m_listener.onChangeIntValue(this);
        m_bFromUser = false;
    }
    public final boolean isFromUser()
    {
        return m_bFromUser;
    }
    public int getValue()
    {
        return m_curValue;
    }
    public void setOnChangeValue(OnChangeValue listener)
    {
        m_listener = listener;
    }
/** Устанавливает массив шагов */    
    public void setSteps(int steps[])
    {
        this.steps = steps;
    }
    public static interface OnChangeValue
    {
        void onChangeIntValue(IntEditor edit);
    }
}
