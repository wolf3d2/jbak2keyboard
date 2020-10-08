package com.jbak2.ctrl;

import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jbak2.Dialog.Dlg;
import com.jbak2.JbakKeyboard.R;
import com.jbak2.JbakKeyboard.st;

public class IntEditor extends LinearLayout
{
	LinearLayout m_mainView;
    LinearLayout.LayoutParams m_lp;
    int m_minValue = 0;
    int m_maxValue = 200;
    int m_curValue = 0;
    boolean m_bFromUser = false;
    TextView m_edit = null;
    TextView m_help;
    Button m_minus = null;
    Button m_plus = null;
    Context m_c = null;
    OnChangeValue m_listener = null;
    int steps[]=new int[]{1,3,5};
    public IntEditor(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        m_c = context;
        LayoutInflater li = (LayoutInflater)context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        LinearLayout ll = (LinearLayout)li.inflate(R.layout.int_editor, this);
        m_mainView = (LinearLayout)ll.findViewById(R.id.mainll);
        m_help = (TextView)m_mainView.findViewById(R.id.help);
        m_help.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Dlg.helpDialog(m_c, R.string.ie_help);
			}
		});
        m_edit = (TextView)m_mainView.findViewById(R.id.int_value);
        m_minus = (Button)m_mainView.findViewById(R.id.minus);
        m_minus.setOnTouchListener(m_cl);
        m_plus = (Button)m_mainView.findViewById(R.id.plus);
        m_plus.setOnTouchListener(m_cl);
        m_lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        setDesignElement();
    }
    void setDesignElement()
    {
    	LinearLayout ll = (LinearLayout) m_mainView;
    	ll.removeAllViews();
		SharedPreferences p = st.pref(m_c);
		int val = Integer.decode(p.getString(st.PREF_KEY_IE_DESIGN, st.STR_ZERO));
    	switch (val)
    	{
    	case 1: // ?left
    		ll.addView(m_help);
    		ll.addView(m_minus);
    		ll.addView(m_plus);
    		ll.addView(m_edit);
    		break;
    	case 2: // ?right
    		ll.addView(m_help);
    		ll.addView(m_edit);
    		ll.addView(m_minus);
    		ll.addView(m_plus);
    		break;
    	case 3: // center?
    		ll.addView(m_minus);
    		ll.addView(m_edit);
    		ll.addView(m_plus);
    		ll.addView(m_help);
    		break;
    	case 4: // left?
    		ll.addView(m_minus);
    		ll.addView(m_plus);
    		ll.addView(m_edit);
    		ll.addView(m_help);
    		break;
    	case 5: // right?
    		ll.addView(m_edit);
    		ll.addView(m_minus);
    		ll.addView(m_plus);
    		ll.addView(m_help);
    		break;
    	default: // ?center
    		ll.addView(m_help);
    		ll.addView(m_minus);
    		ll.addView(m_edit);
    		ll.addView(m_plus);
    		break;
    	}
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
