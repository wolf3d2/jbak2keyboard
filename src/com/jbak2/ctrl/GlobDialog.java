package com.jbak2.ctrl;

import java.util.ArrayList;

import com.jbak2.JbakKeyboard.R;
import com.jbak2.JbakKeyboard.ServiceJbKbd;
import com.jbak2.JbakKeyboard.st;
import com.jbak2.perm.Perm;

import android.R.drawable;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Build;
import android.text.method.DigitsKeyListener;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

//если в set(int) указать вместо параметров R.id.* ноли вместо кнопок, 
//то эти кнопки не выводятся

//если используется edittext,
//то его значение возвращается в кнопке ok, в переменной ret_edittext_text

//для закрытия окна диалога вставить в onBackPressed вызывающей
//активности проверку:
//
//if (GlobDialog.gbshow){
//	GlobDialog.inst.finish();
//	return;
//}

public class GlobDialog  extends Activity 
{
	// флаг что окно диалога по нажатию back закрывать не нужно	
	public static boolean fl_back_key = false;
	// флаг что окно помощи в GlobDialog выведено на экран	
	public static boolean fl_help = false;
    public static GlobDialog inst;
    // флаг, что поле ввода запущено не из настроек и нужно
    // установить pref_act в true чтобы не было зависания
	public static boolean fl_is_prefact = false;
	public static boolean gbshow = false;
	public static boolean fl_volume_key = false;
	private Paint p_color	= new Paint(Paint.ANTI_ALIAS_FLAG);
	public static String ret_edittext_text = st.STR_NULL;
	// позиция окна на некоторых show
	public static int pos_y = -1;
	public static int[] list;
	public static ArrayList<String> slist;
	/** для  */
	RelativeLayout m_rl = null;
	String m_text;
    int m_text_gravity = Gravity.CENTER;
    String m_ok;
    String m_no;
    String m_cancel;
    Context m_c;
    public static EditText et;
    View m_view;
    st.UniObserver m_obs;
    public static final int NO_FINISH = 1;

    public GlobDialog(Context c)
    {
        m_c = c;
        m_rl=null;
        inst = this;
        pos_y=-1;
    }
    View.OnClickListener m_clkListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if(m_obs!=null&&m_obs.OnObserver(Integer.valueOf(v.getId()), this)!=NO_FINISH){
                fl_back_key = false;
                finish();
            }
        }
    };
    View.OnClickListener m_clkListenerEdit = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
        	ret_edittext_text = et.getText().toString();
            st.hidekbd();
            if(m_obs!=null&&m_obs.OnObserver(Integer.valueOf(v.getId()), this)!=NO_FINISH){
                fl_back_key = false;
                finish();
            }
        }
    };
//    View.OnKeyListener m_keyListener = new View.OnKeyListener()
//    {
//        @Override
//        public boolean onKey(View v, int keyCode, KeyEvent event)
//        {
//    	    if(keyCode == KeyEvent.KEYCODE_BACK)  
//    	    {
//    	    	st.hidekbd();
//    	    	finish();
//        	    return true;  
//    	    }  
//            return false;
//        }
//    };
    public View createView()
    {
        LinearLayout ll = new LinearLayout(m_c);
        ll.setBackgroundResource(android.R.drawable.dialog_frame);
//        ll.setOnKeyListener(m_keyListener);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        	
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setLayoutParams(lp);
        ll.setPadding(20, 20, 20, 20);
        if(m_text!=null)
        {
            TextView tv = new TextView(m_c);
            tv.setPadding(20, 20, 20, 20);
            tv.setTextColor(Color.WHITE);
            tv.setText(m_text);
            tv.setMinWidth(200);
            if (m_text_gravity == 0)
            	tv.setGravity(Gravity.CENTER);
            else
            	tv.setGravity(m_text_gravity);
            m_text_gravity = 0;
            tv.setMovementMethod(new ScrollingMovementMethod());
            int h = st.getDisplayHeight(null);
            if (h<=320)
            	tv.setMaxLines(10);
            else
                tv.setMaxLines(15);
            ll.addView(tv);
        }
        LinearLayout.LayoutParams lp1 
        = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        lp1.setMargins(20, 20, 20, 20);
        LinearLayout butLayout = new LinearLayout(m_c);
        butLayout.setLayoutParams(lp1);
        butLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        butLayout.setOrientation(LinearLayout.HORIZONTAL);
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
    public View createRelativeLayout()
    {

        m_rl.setBackgroundResource(android.R.drawable.dialog_frame);
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
        		LinearLayout.LayoutParams.MATCH_PARENT,
        		LinearLayout.LayoutParams.WRAP_CONTENT);
        lp1.setMargins(20, 20, 20, 20);
        lp1.gravity = Gravity.CENTER_HORIZONTAL;
        LinearLayout butLayout = new LinearLayout(m_c);
        butLayout.setLayoutParams(lp1);
        butLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        butLayout.setOrientation(LinearLayout.HORIZONTAL);
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
        int last_child = m_rl.getChildCount();
        last_child = m_rl.getChildAt(last_child-1).getId();
        RelativeLayout.LayoutParams rlp= new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.WRAP_CONTENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
        rlp.addRule(RelativeLayout.BELOW, last_child);
        rlp.addRule(RelativeLayout.CENTER_HORIZONTAL);

        m_rl.addView(butLayout,rlp);
        return m_rl;
    }
//  RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
//	RelativeLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
//lp.gravity = Gravity.CENTER_HORIZONTAL;
//ll.setOrientation(LinearLayout.VERTICAL);
//ll.setLayoutParams(lp);
//
//if(m_text!=null)
//{
//TextView tv = new TextView(m_c);
//tv.setPadding(20, 20, 20, 20);
//tv.setTextColor(Color.WHITE);
//tv.setText(m_text);
//tv.setMinWidth(200);
//if (m_text_gravity == 0)
//	tv.setGravity(Gravity.CENTER);
//else
//	tv.setGravity(m_text_gravity);
//m_text_gravity = 0;
//tv.setMovementMethod(new ScrollingMovementMethod());
//int h = st.getHeightDisplay(null);
//if (h<=320)
//	tv.setMaxLines(10);
//else
//    tv.setMaxLines(15);
//view.addView(tv);
//}

    
    // режим поля ввода
    // hex =
    // 0 - text
    // 1 - decimal
    // 2 - hex
    public View createViewEdit(String txt, int hex)
    {
        LinearLayout ll = new LinearLayout(m_c);
        ll.setBackgroundResource(android.R.drawable.dialog_frame);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setLayoutParams(lp);
        ll.setPadding(20, 20, 20, 20);
        if(m_text!=null)
        {
            TextView tv = new TextView(m_c);
            tv.setTextColor(Color.WHITE);
            tv.setText(m_text);
            tv.setMinWidth(200);
            tv.setGravity(Gravity.CENTER);
            ll.addView(tv);
        }
        LinearLayout.LayoutParams lpet = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        lpet.gravity = Gravity.LEFT;
        lpet.leftMargin = 10;
        lpet.rightMargin = 10;
        et = new EditText(m_c);
        et.setBackgroundColor(Color.LTGRAY);
        et.setTextColor(Color.BLACK);
        et.setMinLines(1);
        et.setMaxLines(2);
        et.setPadding(5, 0, 5, 0);
        et.setId(AlertDialog.BUTTON_POSITIVE);
        switch (hex)
        {
        	//text
        	case 0:
        		break;
        	//decimal
            case 1:
            	et.setKeyListener(DigitsKeyListener.getInstance(st.STR_10INPUT_DIGIT));
            	break;
            //hex
            case 2:
            	et.setKeyListener(DigitsKeyListener.getInstance(st.STR_16INPUT_DIGIT));
            	break;
        }
        et.setOnKeyListener(et_onKeyListener);
//        et.setOnClickListener(m_clkListenerEdit);
        et.setLayoutParams(lpet);
		fl_volume_key=false;
        st.showkbd();//.showKbd(et);
        if (txt!=null&&txt.length()>0) {
        	et.setText(txt);
        	et.setSelection(et.getText().toString().length());
        }
        
        ll.addView(et);

        LinearLayout.LayoutParams lp1 
        = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        lp1.setMargins(20, 20, 20, 20);
        LinearLayout butLayout = new LinearLayout(m_c);
        butLayout.setLayoutParams(lp1);
        butLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        butLayout.setOrientation(LinearLayout.HORIZONTAL);
        if(m_ok!=null)
        {
            butLayout.addView(makeButtonOkEdit(m_ok, AlertDialog.BUTTON_POSITIVE));
        }
        if(m_no!=null)
        {
            butLayout.addView(makeButtonOkEdit(m_no, AlertDialog.BUTTON_NEGATIVE));
        }
        if(m_cancel!=null)
        {
            butLayout.addView(makeButtonOkEdit(m_cancel,AlertDialog.BUTTON_NEUTRAL));
        }
        ll.addView(butLayout);
        return ll;
    }
//    public View createViewList()
//    {
//        LinearLayout ll = new LinearLayout(m_c);
//        ll.setBackgroundResource(android.R.drawable.dialog_frame);
//        ll.setOnKeyListener(m_keyListener);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
//        lp.gravity = Gravity.CENTER_HORIZONTAL;
//        ll.setOrientation(LinearLayout.VERTICAL);
//        ll.setLayoutParams(lp);
//        ll.setPadding(5, 5, 5, 5);
//        
//        if(m_text!=null)
//        {
//            TextView tv = new TextView(m_c);
//            tv.setTextColor(Color.WHITE);
//            tv.setText(m_text);
//            tv.setMinWidth(200);
//            tv.setGravity(Gravity.CENTER);
//            tv.setPadding(0, 20, 0, 0);
//            ll.addView(tv);
//        }
//        ScrollView sv = new ScrollView(m_c);
//        sv.setPadding(20, 5, 15, 0);
//        sv.setBackgroundColor(android.R.drawable.dialog_frame);
//        // задаёт размер или по стандарту, или в пикселях
//        switch (st.getOrientation(m_c))
//        {
//        case Configuration.ORIENTATION_PORTRAIT:
//            if (list.length>6)
//            	sv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 450));
//            else
//            	sv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//            break;
//        default:
//            if (list.length>4)
//            	sv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 450));
//            else
//            	sv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//            break;
//        }        
////        sv.setPadding(20, 150, 20, 150);
//        
//        if (list.length >0){
//            LinearLayout ll1 = new LinearLayout(m_c);
////            ll1.setBackgroundResource(android.R.drawable.dialog_frame);
//            ll1.setOnKeyListener(m_keyListener);
//            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
//            lp1.gravity = Gravity.CENTER_HORIZONTAL;
//            ll1.setOrientation(LinearLayout.VERTICAL);
//            ll1.setLayoutParams(lp);
////            ll1.setPadding(20, 20, 20, 20);
//        	
//        	for (int i = 0;i<list.length;i++){
//        		RadioButton rb = new RadioButton(m_c);
//        		rb.setId(i);
//        		rb.setTextSize(14);
//        		rb.setText(list[i]);
//        		rb.setTextColor(Color.WHITE);
//        		rb.setOnClickListener(m_clkListenerList);
//        		ll1.addView(rb);
//        	}
//        	sv.addView(ll1);
//        }
//        
//
//        LinearLayout.LayoutParams lp1 
//        = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
//        lp1.setMargins(20, 20, 20, 20);
//        LinearLayout butLayout = new LinearLayout(m_c);
//        butLayout.setLayoutParams(lp1);
//        butLayout.setGravity(Gravity.CENTER_HORIZONTAL);
//        butLayout.setOrientation(LinearLayout.HORIZONTAL);
//        if(m_ok!=null)
//        {
//            butLayout.addView(makeButtonOkEdit(m_ok, AlertDialog.BUTTON_POSITIVE));
//        }
//        if(m_no!=null)
//        {
//            butLayout.addView(makeButtonNoList(m_no, AlertDialog.BUTTON_NEGATIVE));
//        }
//        if(m_cancel!=null)
//        {
//            butLayout.addView(makeButton(m_cancel,AlertDialog.BUTTON_NEUTRAL));
//        }
//      	ll.addView(sv);
//        ll.addView(butLayout);
//        return ll;
//    }
//	@SuppressLint("ResourceAsColor")
//	public View createViewListArray()
//    {
//        LinearLayout ll = new LinearLayout(m_c);
//        ll.setBackgroundResource(android.R.drawable.dialog_frame);
//        ll.setOnKeyListener(m_keyListener);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
//        lp.gravity = Gravity.CENTER_HORIZONTAL;
//        ll.setOrientation(LinearLayout.VERTICAL);
//        ll.setLayoutParams(lp);
//        ll.setPadding(5, 5, 5, 5);
//        
//        if(m_text!=null)
//        {
//            TextView tv = new TextView(m_c);
//            tv.setTextColor(Color.WHITE);
//            tv.setText(m_text);
//            tv.setMinWidth(200);
//            tv.setGravity(Gravity.CENTER);
//            tv.setPadding(0, 15, 0, 0);
//            ll.addView(tv);
//        }
//        ScrollView sv = new ScrollView(m_c);
//        sv.setPadding(20, 5, 15, 0);
//        sv.setBackgroundColor(android.R.drawable.dialog_frame);
//        // задаёт размер или по стандарту, или в пикселях
//        switch (st.getOrientation(m_c))
//        {
//        case Configuration.ORIENTATION_PORTRAIT:
//            if (slist.size()>6)
//            	sv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 450));
//            else
//            	sv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//            break;
//        default:
//            if (slist.size()>4)
//            	sv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 450));
//            else
//            	sv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//            break;
//        }        
//        
//        if (st.getOrientation(m_c) == Configuration.ORIENTATION_PORTRAIT){
//            if (slist.size()>6)
//            	sv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 450));
//            else
//            	sv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//        }
//        if (st.getOrientation(m_c) == Configuration.ORIENTATION_LANDSCAPE){
//            if (slist.size()>4)
//            	sv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 450));
//            else
//            	sv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//        }
//        
//        if (slist.size() >0){
//            LinearLayout ll1 = new LinearLayout(m_c);
////            ll1.setBackgroundResource(android.R.drawable.dialog_frame);
//            ll1.setOnKeyListener(m_keyListener);
//            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
//            lp1.gravity = Gravity.CENTER_HORIZONTAL;
//            ll1.setOrientation(LinearLayout.VERTICAL);
//            ll1.setLayoutParams(lp);
////            ll1.setPadding(20, 20, 20, 20);
//        	
//        	for (int i = 0;i<slist.size();i++){
//        		RadioButton rb = new RadioButton(m_c);
//        		rb.setId(i);
//        		rb.setTextSize(14);
//        		rb.setText(slist.get(i));
//        		rb.setTextColor(Color.WHITE);
//        		rb.setOnClickListener(m_clkListenerList);
//        		rb.setPadding(0, 0, 5, 0);
//                if(android.os.Build.VERSION.SDK_INT <= 17){
//                	rb.setPadding(60,0,5,0);
//                }
//        		ll1.addView(rb);
//        	}
//        	sv.addView(ll1);
//        }
//        
//
//        LinearLayout.LayoutParams lp1 
//        = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
//        lp1.setMargins(20, 5, 20, 20);
//        LinearLayout butLayout = new LinearLayout(m_c);
//        butLayout.setLayoutParams(lp1);
//        butLayout.setGravity(Gravity.CENTER_HORIZONTAL);
//        butLayout.setOrientation(LinearLayout.HORIZONTAL);
//        if(m_ok!=null)
//        {
//            butLayout.addView(makeButtonOkEdit(m_ok, AlertDialog.BUTTON_POSITIVE));
//        }
//        if(m_no!=null)
//        {
//            butLayout.addView(makeButtonNoList(m_no, AlertDialog.BUTTON_NEGATIVE));
//        }
//        if(m_cancel!=null)
//        {
//            butLayout.addView(makeButton(m_cancel,AlertDialog.BUTTON_NEUTRAL));
//        }
//      	ll.addView(sv);
//        ll.addView(butLayout);
//        return ll;
//    }
    Button makeButton(String text,int id)
    {
        Button b = new Button(m_c);
        b.setMinWidth(100);
//        b.setBackgroundColor(Color.WHITE);
        b.setText(text);
        b.setId(id);
        b.setBackgroundResource(drawable.btn_default);
        b.setTextColor(0xff000000);
        b.setOnClickListener(m_clkListener);
        return b;
    }
    // создание кнопки ок при использовании edittext
    Button makeButtonOkEdit(String text,int id)
    {
        Button b = new Button(m_c);
        b.setMinWidth(100);
//        b.setBackgroundColor(Color.WHITE);
        b.setText(text);
        b.setId(id);
        b.setBackgroundResource(drawable.btn_default);
        b.setTextColor(0xff000000);
        b.setOnClickListener(m_clkListenerEdit);
        return b;
    }
    public void Layout(WindowManager.LayoutParams lp)
    {}
    public void setGravityText(int gravity)
    {
    	m_text_gravity = gravity;
    }
    // если gravity = -1, то указывается
    // Gravity.CENTER_HORIZONTAL
    // иначе то, что в gravity
    public void showAlert()
    {
        WindowManager wm = (WindowManager)m_c.getSystemService(Service.WINDOW_SERVICE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
     // тип окна не менять!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
		} else {
			lp.type = WindowManager.LayoutParams.TYPE_PHONE;
		}
        lp.gravity = Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.format = PixelFormat.TRANSLUCENT;
        //lp.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN|
        lp.flags = 
        		WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
    			|WindowManager.LayoutParams.FLAG_FULLSCREEN
                |WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                |WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                |WindowManager.LayoutParams.FLAG_DIM_BEHIND
//                WindowManager.LayoutParams.FLAG_DIM_BEHIND
        ;
        lp.dimAmount = (float) 0.2;
        if (pos_y!=-1)
        	lp.y = pos_y;
        Layout(lp);
        m_view = createView();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
    		if (!Perm.checkPermission(m_c)) {
    			st.toastLong(R.string.perm_not_all_perm);
    			return;
    		}
        }
        gbshow = true;
        wm.addView(m_view, lp);
    }
    /** ввод осуществляется одним из трёх видов
     * hex=
     * 0 - text
     * 1 - decimal
     * 2 - hex */
    public void showEdit(String txt, int hex)
    {
        WindowManager wm = (WindowManager)m_c.getSystemService(Service.WINDOW_SERVICE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        // тип окна не менять!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
		} else {
			lp.type = WindowManager.LayoutParams.TYPE_PHONE;
		}
        lp.gravity = Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.format = PixelFormat.TRANSLUCENT;
        lp.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN|
        WindowManager.LayoutParams.FLAG_DIM_BEHIND
        ;
        lp.dimAmount = (float) 0.2;
        Layout(lp);
        m_view = createViewEdit(txt,hex);
        if (!Perm.checkPermission(m_c)) {
        	st.toastLong(R.string.perm_not_all_perm);
        	return;
        }
        wm.addView(m_view, lp);
        gbshow = true;
        if (st.fl_pref_act==false){
        	st.fl_pref_act = true;
        	fl_is_prefact = true;
        }
    }
    public void showRelativeLayout()
    {
        WindowManager wm = (WindowManager)m_c.getSystemService(Service.WINDOW_SERVICE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        // тип окна не менять!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
		} else {
			lp.type = WindowManager.LayoutParams.TYPE_PHONE;
		}
        lp.gravity = Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.format = PixelFormat.TRANSLUCENT;
        lp.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN|
        WindowManager.LayoutParams.FLAG_DIM_BEHIND
        ;
        lp.dimAmount = (float) 0.2;
        Layout(lp);
        m_view = createRelativeLayout();
        if (!Perm.checkPermission(m_c)) {
        	st.toastLong(R.string.perm_not_all_perm);
        	return;
        }
        wm.addView(m_view, lp);
        gbshow = true;
        if (st.fl_pref_act==false){
        	st.fl_pref_act = true;
        	fl_is_prefact = true;
        }
    }
    public void finish()
    {
    	if (fl_back_key)
    		return;
       	WindowManager wm = (WindowManager)m_c.getSystemService(Service.WINDOW_SERVICE);
       	wm.removeView(m_view);
        gbshow = false;
		fl_volume_key=false;
		if (fl_is_prefact){
			fl_is_prefact = false;
			st.fl_pref_act = false;
		}
		fl_help = false;
        fl_back_key = false;
        inst = null;
        if(ServiceJbKbd.inst!=null)
        	ServiceJbKbd.inst.acVisible();
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
    public void setList(int title,int ok, int no, int cancel, int ... val)
    {
//    	list = new int[val.length];
    	list = val;
        set(m_c.getString(title), ok==0?null:m_c.getString(ok),no==0?null:m_c.getString(no), cancel==0?null:m_c.getString(cancel));
    }
    public void setListArray(int title,int ok, int no, int cancel, ArrayList<String> ar)
    {
//    	list = new int[val.length];
    	slist = ar;
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
    public void setRelativeLayout(RelativeLayout rl)
    {
        m_rl = rl;
    }
    public void setObserver(st.UniObserver obs)
    {
        m_obs = obs;
    }
 // нажатие ентера для edittext
    View.OnKeyListener et_onKeyListener = new View.OnKeyListener() {
		
		@SuppressLint("NewApi")
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) 
		{
			int act = event.getAction();
    	    if(keyCode == KeyEvent.KEYCODE_BACK)  
    	    {
    	    	
            	et = null;
                st.hidekbd();
    	    	finish();
        	    return true;  
    	    }
    	    else if(!st.isHoneycomb()&&event.isCtrlPressed()&&keyCode == KeyEvent.KEYCODE_A){
 	    		
    	    	EditText et = (EditText)v;
    	    	if (et !=null){
    	    		et.selectAll();
    	    		return true;
    	    	}
    	    }
    	    else if(act == KeyEvent.ACTION_DOWN){ 
    	    	if (keyCode == KeyEvent.KEYCODE_ENTER){
                	ret_edittext_text = et.getText().toString();
                	et = null;
                	st.hidekbd();
                    if(m_obs!=null&&m_obs.OnObserver(Integer.valueOf(v.getId()), this)!=NO_FINISH){
                    	finish();
                    }
           			return true;
           		}
    	    	else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
        	    	st.processVolumeKey(keyCode, true);
        	    	fl_volume_key = true;
           			return true;
    	    	}
    	    	else if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
        	    	st.processVolumeKey(keyCode, true);
        	    	fl_volume_key = true;
           			return true;
    	    	}
   			}
    	    else if(act == KeyEvent.ACTION_UP&&fl_volume_key){
    	    	st.processVolumeKey(keyCode, false);
    	    	fl_volume_key = false;
   			}
       		return false;
		}
	};
	public void setPositionOnKeyboard(boolean onKeyboard)
    {
		if (onKeyboard) {
			if (st.kv()!=null)
				pos_y = st.kv().getHeight();
			else
				pos_y=-1;
		}
    }
 
}