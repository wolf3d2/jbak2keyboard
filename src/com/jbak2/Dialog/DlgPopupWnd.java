package com.jbak2.Dialog;

import java.util.ArrayList;

import com.jbak2.JbakKeyboard.JbCandView;
import com.jbak2.JbakKeyboard.ServiceJbKbd;
import com.jbak2.JbakKeyboard.st;
import android.R.drawable;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

/** диалог на клавиатуре */
public class DlgPopupWnd
{
	public static int MARGIN = 10;
	int yoff = 0;
	PopupWindow pw = null;
    public static DlgPopupWnd inst;
	public static boolean fl_volume_key = false;
	String m_text;
    int m_text_gravity = Gravity.CENTER;
    String m_ok;
    String m_no;
    String m_cancel;
    Context m_c;
    View m_view;
    st.UniObserver m_obs;
    public static final int NO_FINISH = 1;
    float weight = 0;
    
    /** диалог на клавиатуре */
    public DlgPopupWnd(Context c)
    {
        m_c = c;
        inst = this;
    }
    View.OnClickListener m_clkListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
        	try {
                if(m_obs!=null&&m_obs.OnObserver(Integer.valueOf(v.getId()), this)!=NO_FINISH){
                	dismiss();
                }
			} catch (Throwable e) {
            	dismiss();
			}

        }
    };
//    @Override
//    public void onDestroy() 
//    {
//    	dismiss();
//   		super.onDestroy();
//    }
    public View createView()
    {
        LinearLayout ll = new LinearLayout(m_c);
        ll.setBackgroundResource(android.R.drawable.dialog_frame);
//        ll.setOnKeyListener(m_keyListener);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        	
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setLayoutParams(lp);
        ll.setPadding(MARGIN, MARGIN, MARGIN, MARGIN);
        // вес кнопок
        weight = 0;
        if(m_ok!=null)
        	weight++;
        if(m_no!=null)
        	weight++;
        if(m_cancel!=null)
        	weight++;
        weight = 1/weight;
        Button btn = makeButton("bb",0);
        btn.measure(0, 0);
        int bh = btn.getMeasuredHeight();
        if(m_text!=null)
        {
            TextView tv = new TextView(m_c);
            tv.setPadding(MARGIN*4, MARGIN, MARGIN, 2);
            tv.setTextColor(Color.WHITE);
            tv.setText(m_text);
            tv.setMinWidth(200);
            if (m_text_gravity == 0)
            	tv.setGravity(Gravity.CENTER);
            else
            	tv.setGravity(m_text_gravity);
            m_text_gravity = 0;
            tv.setMovementMethod(new ScrollingMovementMethod());
            if (Math.abs(yoff)>=bh)
            	tv.setMaxHeight(Math.abs(yoff)-bh-(MARGIN*4));
//            	tv.setMaxLines(10);
            else {
                tv.setPadding(2, 1, 1, 2);
            	tv.setMaxLines(1);
            }
//                tv.setMaxLines(5);
            ll.addView(tv);
        }
        LinearLayout.LayoutParams lp1 
        = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        lp1.setMargins(MARGIN, MARGIN, MARGIN, MARGIN);
        LinearLayout butLayout = new LinearLayout(m_c);
        butLayout.setLayoutParams(lp1);
        butLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        butLayout.setOrientation(LinearLayout.HORIZONTAL);
        butLayout.setPadding(MARGIN*4, MARGIN, MARGIN*4, MARGIN);
        if(m_ok!=null)
        {
            butLayout.addView(makeButton(m_ok, AlertDialog.BUTTON_POSITIVE));
        }
        if(m_no!=null)
        {
            butLayout.addView(makeButton(m_no, AlertDialog.BUTTON_NEGATIVE));
        }
        if(m_cancel!=null)
        {
            butLayout.addView(makeButton(m_cancel,AlertDialog.BUTTON_NEUTRAL));
        }
        ll.addView(butLayout);
        return ll;
    }
    Button makeButton(String text,int id)
    {
        Button b = new Button(m_c);
        b.setMinWidth(80);
//        b.setBackgroundColor(Color.WHITE);
        b.setText(text);
        b.setId(id);
        b.setBackgroundResource(drawable.btn_default);
        b.setTextColor(0xff000000);
        b.setOnClickListener(m_clkListener);
        b.setLayoutParams(new LinearLayout.LayoutParams(
        		LayoutParams.WRAP_CONTENT, 
        		LayoutParams.WRAP_CONTENT, 
        		weight
        		));
        return b;
    }
    /** гравитация текста окна */
    public void setGravityText(int gravity)
    {
    	m_text_gravity = gravity;
    }
    /** Позиция окна - если wnd_gravity = 0, то по умолчанию = Gravity.CENTER */
	public void show(int wnd_gravity)
    {
        // высота клавы, если ошибка, то возврат
		// НЕ УДАЛЯТЬ! - значение yoff используется в createView
		try {
	        yoff = 0-st.kv().getCurKeyboard().getHeight();
		} catch (Throwable e) {
			dismiss();
			return;
		}
        int cvh = 0;
        if (ServiceJbKbd.inst!=null
        		&ServiceJbKbd.inst.m_acPlace == JbCandView.AC_PLACE_KEYBOARD
        		)
        	cvh = ServiceJbKbd.inst.m_candView.getHeight();
        yoff += cvh;
        if (wnd_gravity==0)
        	wnd_gravity = Gravity.CENTER; 
    	pw = new PopupWindow(createView(),
    			LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        pw.setBackgroundDrawable(new BitmapDrawable());

        pw.setTouchable(true);
        pw.setFocusable(false);
        //pw.setOutsideTouchable(false);

        pw.setOnDismissListener(new PopupWindow.OnDismissListener() {
			
			@Override
			public void onDismiss() {
				dismiss();
			}
		});
        try {
            //pw.showAsDropDown(st.kv(), 0, yoff);
            pw.showAtLocation(st.kv(), wnd_gravity, 0, 0);    
		} catch (Throwable e) {
			dismiss();
		}

    }
    public void dismiss()
    {
    	//st.toast("bb");
    	if (pw!=null){
    		try {
        		pw.dismiss();
			} catch (Throwable e) {
			}
    		pw=null;

    	}
    	inst = null;
    }
    public void set(String title,String ok,String cancel)
    {
        m_text = title;
        m_ok = ok;
        m_cancel = cancel;
    }
    public void set(int title,int ok, int no, int cancel)
    {
        set(m_c.getString(title), ok==0?null:m_c.getString(ok),no==0?null:m_c.getString(no), cancel==0?null:m_c.getString(cancel));
    }
    public void set(String title,String ok,String no, String cancel)
    {
        m_text = title;
        m_ok = ok;
        m_no = no;
        m_cancel = cancel;
    }
    public void set(int title,int ok,int cancel)
    {
        set(m_c.getString(title), ok==0?null:m_c.getString(ok), cancel==0?null:m_c.getString(cancel));
    }
    public void set(String title,int ok,int cancel)
    {
        set(title, ok==0?null:m_c.getString(ok), cancel==0?null:m_c.getString(cancel));
    }
    public void setObserver(st.UniObserver obs)
    {
        m_obs = obs;
    }
 
}