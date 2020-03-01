package com.jbak2.JbakKeyboard;

import java.io.File;

import com.jbak2.JbakKeyboard.st.ArrayFuncAddSymbolsGest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

/** класс для маленьких клавиатур */
public class PopupKeyboard 
{
	int MIN_WIDTH_OFFICIAL_BUTTON = 30;
	String V2 = "v2";
	// координаты вызывающей кнопки
	int keyXpos = -1;
	int keyYpos = -1;
	/** kvh - общая высота клавиатуры */
	int kvh = -1;
	static PopupWindow pw = null;
	// координаты окна
	int wm_up_x = 0;
	int wm_up_y = 0;
	public static Context m_c = null;
	/** главная для полноэкранной миниклавы */
	static RelativeLayout llmain=null;
	// правая для полноэкранной миниклавы
	LinearLayout llright = null;
	// левая для миниклавы
	LinearLayout llleft = null;
	WindowManager m_wm = null;
	// параметры для full window
	// временная переменная 
	public static ArrayFuncAddSymbolsGest ar = new ArrayFuncAddSymbolsGest();
	public static boolean fl_popupcharacter_window = false;
	final int DEFAULT_Y = 20;
	final int DEFAULT_X = 5;
	int MARGIN = 10;
	/** если true - окно не закрывам */
	boolean pc2_block = false;
	TextView close = null; 
	TextView set = null; 
	TextView back = null; 
	TextView block = null; 
	
	public PopupKeyboard(Context c){
		m_c = c;
    	m_wm = (WindowManager) c.getSystemService(Service.WINDOW_SERVICE);
	}
	public boolean showPopupKeyboard(Key key)
	{
		if (key == null)
			return false;
		if (key.popupCharacters.toString().startsWith(V2)
			||key.popupCharacters.toString().startsWith(V2))
			return createFullPopupWindow((key.popupCharacters.toString()));
		else 
			return createMiniPopupKbd(key);
		//return false;
	}
	public boolean showPopupKeyboard(String str)
	{
		if (str.startsWith(V2)
		  ||str.startsWith(V2))
			return createFullPopupWindow(str);
		return false;
	}
	@SuppressLint("NewApi")
	boolean createMiniPopupKbd(final Key key)
    {
    	fl_popupcharacter_window = false;
//    	int ii=0;
//		if (ii==0)
//			return false;
    	if (!st.fl_mini_kbd_its)
    		return false;
    	if (fl_popupcharacter_window)
    		return false;
        View v = ServiceJbKbd.inst.getLayoutInflater().inflate(R.layout.popup_kbd_mini_static, null);
        llmain = ((RelativeLayout) v.findViewById(R.id.pk_mini_static));
        llmain.setBackgroundResource(android.R.drawable.dialog_frame);
        
        llleft = (LinearLayout)v.findViewById(R.id.pk_mini_llleft);

        ImageView close = (ImageView) v.findViewById(R.id.pk_mini_close);
        close.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				//pw.dismiss();
				close();
			}
		});
        close.measure(0, 0);
    	int clw = close.getMeasuredWidth();
    	LinearLayout.LayoutParams btnpar = null;
    	// размер кнопок
    	switch (st.mini_kbd_btn_size)
    	{
    	case 1:
        	btnpar= new LinearLayout.LayoutParams(
        			50,50);
    		break;
    	case 2:
        	btnpar= new LinearLayout.LayoutParams(
        			64,64);
    		break;
    	case 3:
        	btnpar= new LinearLayout.LayoutParams(
        			key.width,key.height);
    		break;
    	case 4:
        	btnpar= new LinearLayout.LayoutParams(
        			64,50);
    		break;
    	default:
        	btnpar= new LinearLayout.LayoutParams(
        			LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        	break;
    	}
    	btnpar.setMargins(MARGIN, MARGIN,MARGIN,MARGIN);
//    	btnpar.leftMargin = 0;
//    	btnpar.rightMargin = MARGIN;
    	int tsize = 0;
    	switch (st.mini_kbd_btn_text_size)
    	{
    	case 1:tsize = 15;break;
    	case 2:tsize = 20;break;
    	case 3:tsize = 25;break;
    	}
    	int btn_w = 0;
    	LinearLayout ll = new LinearLayout(m_c);
        ll.setOrientation(LinearLayout.HORIZONTAL);
    	int llw = 0;
    	
// ОСНОВНОЙ ЦИКЛ СОЗДАНИЯ КНОПОК
		for (int i=0;i<key.popupCharacters.length();i++){
			// TextView
//			Button btn = new Button(m_c);
			TextView btn = new TextView(m_c);
        	btn.setGravity(Gravity.CENTER);
        	
        	btn.setMaxLines(1);
    		btn.setBackgroundColor(Color.WHITE);
    		btn.setTextColor(Color.BLACK);
    		btn.setText(st.STR_NULL+key.popupCharacters.charAt(i));
    		if (tsize!=0)
    			btn.setTextSize(tsize);
    		btn.setMinimumWidth(30);
//    		btn.setId(i+1);
//    		btn.setRight(0);
//    		btn.setLeft(0);
    		btn.setOnTouchListener(new View.OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
		        	switch (event.getAction())
		        	{
		        	case MotionEvent.ACTION_DOWN:
		        		v.setBackgroundColor(Color.GRAY);
		        		break;
		        	case MotionEvent.ACTION_CANCEL:
		        	case MotionEvent.ACTION_UP:
		        		v.setBackgroundColor(Color.WHITE);
		        		break;
		        	}
					return false;
				}
			});
    		btn.setTransformationMethod(null);
        	btn.setOnClickListener(new OnClickListener() {
            	@Override
            		public void onClick(View v) 
            		{
            		if (ServiceJbKbd.inst!=null){
                		ServiceJbKbd.inst.processKey(((TextView)v).getText().toString().charAt(0));
                		ServiceJbKbd.inst.processCaseAndCandidates();
            		}
            		close();
// отменяет фон нажатой клавиши, но глючит - разобраться            		 
//                    key.pressed = false;
//                    ((LatinKey)key).processed = true;
//            		JbKbdView.inst.invalidateKey(JbKbdView.inst.getKeyIndex(key));

            		}
        		});
        	btn.setLayoutParams(btnpar);
    		btn.measure(0, 0);
        	ll.measure(0, 0);
            llw = ll.getMeasuredWidth();//+ll.getPaddingLeft()+ll.getPaddingRight();
            int bw = btn.getMeasuredWidth();
            int yy = st.getDisplayWidth(null);
            btn_w = btn.getMeasuredWidth();
//            int len = llw+clw+btn_h+btn.getPaddingLeft()+btn.getPaddingRight();
            int len = llw+clw+btn_w+MARGIN;
            if (len>=st.getDisplayWidth(null)){
//            if (llw+clw+btn.getMeasuredWidth()>=st.getWidthDisplay()){
            	llleft.addView(ll);
                ll = new LinearLayout(m_c);
                ll.setOrientation(LinearLayout.HORIZONTAL);
        	}
            ll.addView(btn);
    	}
    	if (ll.getChildCount()>0)
    		ll.measure(0, 0);
    		int ww = ll.getMeasuredWidth();
    		ll.setMinimumWidth(ww+MARGIN+MARGIN+MARGIN);
    		llleft.addView(ll);
//    	llmain.addView(llleft);
    	
//    	//pw.setContentView(llmain);
//		pw.setFocusable(false);
//
//        pw.setBackgroundDrawable(new BitmapDrawable());
//        //pw.setTouchable(true);
//        //pw.setSplitTouchEnabled(false);
//        pw.setOutsideTouchable(true);
//        //pw.showAsDropDown(st.kv(), 500, 200);
//
//        pw.showAtLocation(st.kv(), Gravity.TOP, key.x, key.y-key.height);
////        pw.setFocusable(true);
////        pw.update();		
        //pw.showAsDropDown(llmain, 50, -30);    	
    	
    	pw = new PopupWindow(llmain,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        pw.setBackgroundDrawable(new BitmapDrawable());

        pw.setTouchable(true);
        pw.setFocusable(false);
        //pw.setOutsideTouchable(false);

        pw.setOnDismissListener(new PopupWindow.OnDismissListener() {
			
			@Override
			public void onDismiss() {
				close();
			}
		});
        llleft.measure(0, 0);
// для showAtLocation        
//        int xx = 0-key.x;
//
//        int ph = key.y-llleft.getMeasuredHeight();
//        int key_y_pos = key.y-kvh;
//        int yy = kvh-key_y_pos;
//        if (yy < kvh){
//        	yy = 100;
//        }
        // если над вызывающей кнопкой нет рядов, то рисуем окно под кнопкой
//        int iii = key.y-ServiceJbKbd.inst.m_candView.getHeight();
//        if (iii<10){
//        	yy =0-st.kv().getHeight()-ServiceJbKbd.inst.m_candView.getHeight()+key.y+key.height; 
//        }
//        pw.setHeight(ph);
//        pw.showAtLocation(st.kv(), Gravity.NO_GRAVITY, xx, yy);
        
// для showAsDropDown - у меня на планшете глючит определение позиции
        // определяем координату y окна, ВЫШЕ вызывающей кнопки
        // причём макс. высота миниклавы, не выше высоты основной клавы, иначе окно обрезается
        // и включается скроллинг
        
        // высота автодопа
        int cvh = 0;
        if (ServiceJbKbd.inst!=null
        		&ServiceJbKbd.inst.m_candView.m_place == JbCandView.AC_PLACE_KEYBOARD
        		)
        	cvh = ServiceJbKbd.inst.m_candView.getHeight();
        kvh = st.kv().getHeight()-cvh;
    	keyXpos = key.x;
    	keyYpos = key.y;
    	int llh = llleft.getMeasuredHeight();
    	int pwh = key.y- cvh;
    	if (llh>pwh)
    		llh = kvh-key.y;
//    	else
//    		llh = LayoutParams.WRAP_CONTENT;
        int yoff = 0;
        yoff = kvh - key.y+key.height-cvh;
        yoff = yoff+llh;
        if (yoff>kvh){
        	if (key.y-cvh<key.height)
        		yoff=kvh-key.height+cvh;
        	else {
        		yoff =kvh;
        		llh = key.height+cvh;
            	pw.setHeight(llh);
        	}			
        }
        yoff = 0-yoff;
        //st.toast("yoff="+yoff);
        //yoff = 0- key.y-llleft.getMeasuredHeight()+key.height;
        //yoff = 0-kvh+pwh;
        // если над вызывающей кнопкой нет рядов, то рисуем окно под кнопкой
//        int iii = key.y-ServiceJbKbd.inst.m_candView.getHeight();
//        if (iii<10){
//        	yoff =0-st.kv().getHeight()-ServiceJbKbd.inst.m_candView.getHeight()+key.y+key.height; 
//        }
        // определяем координату x окна
        //llmain.measure(0, 0);
        int xoff = 0-llleft.getMeasuredWidth()-clw;
        xoff =key.x + xoff; 
        //xoff = 30;
        pw.setWidth(llleft.getMeasuredWidth()+clw+btn_w);

        pw.showAsDropDown(st.kv(), xoff, yoff);
        wm_up_x = xoff;
        wm_up_y = yoff;
        fl_popupcharacter_window = true;
    	return true;
    }
	// статическая маленькая клавиатурка
//    @SuppressLint("NewApi")
//	boolean createMiniPopupWindowStatic(final Key key)
//    {
//    	if (!st.fl_mini_kbd_its)
//    		return false;
//
//        View v = ServiceJbKbd.inst.getLayoutInflater().inflate(R.layout.popup_kbd_mini_static, null);
//        llmain = ((RelativeLayout) v.findViewById(R.id.pk_mini_static));
//        llmain.setBackgroundResource(android.R.drawable.dialog_frame);
//        llleft = (LinearLayout)v.findViewById(R.id.pk_mini_llleft);
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//		lp.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
//        lp.format = PixelFormat.TRANSLUCENT;
//        lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//        		
//    			|WindowManager.LayoutParams.FLAG_SPLIT_TOUCH
//    			|WindowManager.LayoutParams.FLAG_FULLSCREEN
//                |WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
//                |WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
//                |WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                ;
//        lp.dimAmount = (float) 0.2;
//        
//		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//		lp.width = 20;
////      int dpi = m_c.getResources().getDisplayMetrics().densityDpi;
////      int ypos = m_c.getResources().getDisplayMetrics().heightPixels;
////      int xpos = m_c.getResources().getDisplayMetrics().widthPixels;
////        lp.y = ypos;
////        lp.x = DEFAULT_X-DEFAULT_X;
////        lp.height =  key.height;
////        lp.width = xpos-DEFAULT_X;
//
//		ImageView close = (ImageView)v.findViewById(R.id.pk_mini_close);
//    	close.setOnClickListener(new OnClickListener() {
//    	@Override
//    		public void onClick(View v) 
//    		{
//    		close();
//    		}
//		});
//    	close.measure(0, 0);
//
//    	
////    	RelativeLayout.LayoutParams llpar = new RelativeLayout.LayoutParams(
////    			LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
////    	llpar.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
////        llpar.addRule(RelativeLayout.LEFT_OF, R.id.pk_mini_close);
//
//    	LinearLayout.LayoutParams btnpar = new LinearLayout.LayoutParams(
//    			LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
////    	btnpar.setMargins(1, 1,1,1);
////    	btnpar.leftMargin = 0;
////    	btnpar.rightMargin = 0;
//
//    	int cpl = close.getPaddingLeft();
//    	int cpr = close.getPaddingRight();
//    	int clw = close.getMeasuredWidth();
//    	LinearLayout ll = new LinearLayout(m_c);
//        ll.setOrientation(LinearLayout.HORIZONTAL);
//    	int llw = 0;
//
//    	for (int i=0;i<key.popupCharacters.length();i++){
//    		Button btn = new Button(m_c);
//        	btn.setGravity(Gravity.CENTER);
//        	btn.setMaxLines(1);
//    		btn.setTextColor(Color.BLACK);
//    		btn.setText(st.STR_NULL+key.popupCharacters.charAt(i));
//    		btn.setTextSize(20);
//    		btn.setId(i+1);
//    		btn.setRight(0);
//    		btn.setLeft(0);
//    		btn.setTransformationMethod(null);
//        	btn.setOnClickListener(new OnClickListener() {
//            	@Override
//            		public void onClick(View v) 
//            		{
//            		ServiceJbKbd.inst.sendKeyChar(((Button)v).getText().toString().charAt(0));
//            		close();
//// отменяет фон нажатой клавиши, но глючит - разобраться            		 
////                    key.pressed = false;
////                    ((LatinKey)key).processed = true;
////            		JbKbdView.inst.invalidateKey(JbKbdView.inst.getKeyIndex(key));
//
//            		}
//        		});
//        	btn.setLayoutParams(btnpar);
//    		btn.measure(0, 0);
//        	ll.measure(0, 0);
//            llw = ll.getMeasuredWidth();//+ll.getPaddingLeft()+ll.getPaddingRight();
//            int bw = btn.getMeasuredWidth();
//            int yy = st.getWidthDisplay();
//            int len = llw+clw+btn.getMeasuredWidth()+btn.getPaddingLeft()+btn.getPaddingRight();
//            if (len>=st.getWidthDisplay()){
////            if (llw+clw+btn.getMeasuredWidth()>=st.getWidthDisplay()){
//            	llleft.addView(ll);
//                ll = new LinearLayout(m_c);
//                ll.setOrientation(LinearLayout.HORIZONTAL);
//        	}
//            ll.addView(btn);
//    	}
//    	if (ll.getChildCount()>0)
//    		llleft.addView(ll);
//
//    	lp.gravity = Gravity.NO_GRAVITY;
//    	lp.y = key.y-key.height;
//		lp.x = key.x;
//    	llleft.measure(0, 0);
//    	int tmp = llleft.getMeasuredWidth()+clw;
//    	int tmp1 = llleft.getMeasuredWidthAndState()+clw+close.getPaddingLeft()+close.getPaddingRight();
//    		
//    	lp.width = tmp;
//    	tmp = key.x -lp.width;
////    	if (tmp>key.x)
////    		tmp = key.x - lp.width;
//    	if (tmp<0)
//    		tmp=0;
//    	lp.x = tmp;
//    	
//		CustomKeyboard kbd = (CustomKeyboard)st.kv().getCurKeyboard();
//		int ypos = kbd.getHeight();
//		int hei = llleft.getMeasuredHeight();
//		int lpy = lp.y;
//		tmp = Math.abs(lp.y - (llleft.getMeasuredHeight()+key.y));
//		
//		if (tmp>=ypos){
//			//lp.y = 2;
//			lp.y = kbd.getHeight()-key.y;
//	        lp.height =  kbd.getHeight()-ServiceJbKbd.inst.m_candView.getHeight();
//			//lp.height = Math.abs(key.y - lp.y);
//		}
//		wm_x = lp.x;
//		wm_y = lp.y;
//    	m_wm.addView(v,lp);
//    	fl_popupcharacter_window = true;
//    	return true;
//    }
	/** создаем полноразмерную миниклавиатуру */
    boolean createFullPopupWindow(String popupstr)
    {
    	kvh = -1;
    	if (st.ar_asg.size()>0)
    		st.ar_asg.clear();
    	popupstr = popupstr.substring(3, popupstr.length()).trim();
//    	String[] txt = popupstr.split(st.STR_SPACE);
    	
    	String[] txt = ArrayFuncAddSymbolsGest.getSplitArrayElements(popupstr);
    	View v = null;
    	if (!st.pc2_lr)
    		v = ServiceJbKbd.inst.getLayoutInflater().inflate(R.layout.popup_kbd_full_right, null);
    	else
    		v = ServiceJbKbd.inst.getLayoutInflater().inflate(R.layout.popup_kbd_full_left, null);
    		
        if (v==null)
        	return false;
        llmain = ((RelativeLayout) v.findViewById(R.id.popup2main));
        llright = ((LinearLayout) v.findViewById(R.id.buttons_official));
        final LinearLayout llrow = ((LinearLayout) v.findViewById(R.id.pc2_llrow));
        LinearLayout ll = new LinearLayout(m_c);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		v.setBackgroundColor(st.win_bg);
//        pop2.setBackgroundResource(android.R.drawable.dialog_frame);
// устанавливает цвет фона окна, если не используем  цвет системы строчкой выше        
//		pop2.setBackgroundColor(Color.RED);

		//wm = (WindowManager) c.getSystemService(Service.WINDOW_SERVICE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

		LinearLayout.LayoutParams llrowpar = new LinearLayout.LayoutParams(
	    		  LayoutParams.WRAP_CONTENT , LayoutParams.WRAP_CONTENT);
		llrowpar.bottomMargin = MARGIN;
		LinearLayout.LayoutParams llpar = new LinearLayout.LayoutParams(
	    		  LayoutParams.WRAP_CONTENT , LayoutParams.WRAP_CONTENT);
//		llpar.setMargins(5, 5, 5, 5);
		llpar.gravity = Gravity.LEFT;
		llpar.rightMargin=MARGIN;
// параметры кнопки
    	RelativeLayout.LayoutParams btnpar = new RelativeLayout.LayoutParams(
	    		  LayoutParams.WRAP_CONTENT , LayoutParams.WRAP_CONTENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
		} else {
			lp.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		}
		//lp.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
    	CustomKeyboard kbd = (CustomKeyboard)st.kv().getCurKeyboard();
        int ypos = m_c.getResources().getDisplayMetrics().heightPixels;
        int xpos = m_c.getResources().getDisplayMetrics().widthPixels;
        ypos -= kbd.getHeight();
        lp.y = ypos;
        lp.x = DEFAULT_X-DEFAULT_X;
        lp.height =  kbd.getHeight()-ServiceJbKbd.inst.m_candView.getHeight();
        lp.width = xpos-DEFAULT_X;

        
        lp.format = PixelFormat.TRANSLUCENT;
        lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
    			|WindowManager.LayoutParams.FLAG_FULLSCREEN
                |WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                |WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                |WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                ;
        lp.dimAmount = (float) 0.2;
		pc2_block = false;

		close = ((TextView) v.findViewById(R.id.pc2close));
        close.setTextSize(st.btnoff_size);
    	close.setBackgroundColor(st.btnoff_bg);
    	close.setTextColor(st.btnoff_tc);
    	close.setOnClickListener(new OnClickListener() {
    	@Override
    		public void onClick(View v) 
    		{
    		close();
    		}
		});
    	set = ((TextView) v.findViewById(R.id.pc2setting));
        set.setTextSize(st.btnoff_size);
    	set.setBackgroundColor(st.btnoff_bg);
    	set.setTextColor(st.btnoff_tc);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        	set.setText("⚙");
        }
    	set.setOnClickListener(new OnClickListener() {
    	@Override
    		public void onClick(View v) 
    		{
    		st.runAct(Popup2act.class);
    		}
		});
    	
    	block = ((TextView) v.findViewById(R.id.pc2block));
    	block.setTextSize(st.btnoff_size);
    	block.setBackgroundColor(st.btnoff_bg);
    	block.setTextColor(st.btnoff_tc);
    	block.setText(st.returnZamok(false));
    	if(st.win_fix){
    		pc2_block = true;
    		block.setText(st.returnZamok(true));
    	}
    	block.setOnClickListener(new OnClickListener() {
    	@Override
    		public void onClick(View v) 
    		{
    			if (pc2_block) {
    				pc2_block = false;
    	    		((TextView)v).setText(st.returnZamok(false));
    			} else {
    				pc2_block = true;
    	    		((TextView)v).setText(st.returnZamok(true));
    			}
    		}
		});
    	back = ((TextView) v.findViewById(R.id.pc2back));
    	back.setTextSize(st.btnoff_size);
    	back.setBackgroundColor(st.btnoff_bg);
    	back.setTextColor(st.btnoff_tc);
        if (Build.VERSION.SDK_INT < 23) {
        	back.setText("❮❎");
    	} else {
        	back.setText("⌫");
    	}
		back.setOnClickListener(new OnClickListener() {
    	@Override
    		public void onClick(View v) 
    		{
			if (ServiceJbKbd.inst!=null)
				ServiceJbKbd.inst.processKey(Keyboard.KEYCODE_DELETE);
    		}
		});
    	// устанавливаем одинаковую ширину служебных кнопок
    	int wid = MIN_WIDTH_OFFICIAL_BUTTON;
    	int hw = 0;
    	close.measure(0, 0);
    	hw = close.getMeasuredWidth();
        if (wid < hw)
        	wid = hw;
    	set.measure(0, 0);
    	hw = set.getMeasuredWidth();
        if (wid < hw)
        	wid = hw;
        back.measure(0, 0);
    	hw = back.getMeasuredWidth();
        if (wid < hw)
        	wid = hw;
        block.measure(0, 0);
    	hw = block.getMeasuredWidth();
        if (wid < hw)
        	wid = hw;
        close.setMinWidth(wid);
        set.setMinWidth(wid);
        back.setMinWidth(wid);
        block.setMinWidth(wid);

        int ll_btn_width = lp.width-MARGIN-MARGIN-wid;

        int id =1;
        
// ОСНОВНОЙ ЦИКЛ СОЗДАНИЯ КНОПОК
        for (int i=0;i<txt.length;i++) {
        	if (txt[i].trim().length() == 0){
        		TextView tv = new TextView(m_c);
        		tv.setBackgroundColor(st.win_bg);
        		tv.setTextColor(st.win_bg);
        		tv.setText(st.STR_SPACE+st.STR_SPACE);
        		tv.setId(id);
        		id++;
            	tv.measure(0, 0);
            	ll.addView(tv, llpar);
            	continue;
        	}
        	
        	TextView tv = new TextView(m_c);
        	tv.setTextSize(st.btn_size);
        	tv.setBackgroundColor(st.btn_bg);
        	tv.setTextColor(st.btn_tc);
        	tv.setId(id);
        	st.setElementSpecFormatAddSymbol(st.ar_asg,txt[i],id);
        	ar = st.getElementSpecFormatSymbol(st.ar_asg, id);
        	if (ar!=null){
        		tv.setText(ar.visibleText+st.STR_SPACE);
        		Drawable img = m_c.getResources().getDrawable( R.drawable.bullet_red);
        		img.setBounds( 0, 0, 15, 15 );
        		tv.setCompoundDrawables( img, null, null, null );
        		tv.setCompoundDrawablePadding(2);
        	} else
        		tv.setText(st.STR_SPACE+st.STR_SPACE+txt[i]+st.STR_SPACE+st.STR_SPACE);
        	id++;
        	tv.setOnClickListener(new OnClickListener() {
        	@Override
        		public void onClick(View v) 
        		{
        		String txt = ((TextView)v).getText().toString().trim();
				ArrayFuncAddSymbolsGest el = st.getElementSpecFormatSymbol(st.ar_asg, v.getId());
    			if (!pc2_block) 
    				close();
				if (el!=null){
					if (el.tpl_name!=null) {
						ArrayFuncAddSymbolsGest.processTemplateClick(m_c, el);
//						File ff = null;
//						if (el.tpl_name.startsWith(st.STR_SLASH))
//					   		ff = new File(el.tpl_name);
//						else
//				   		 	ff = new File(st.getSettingsPathFull(el.tpl_name, Templates.INT_FOLDER_TEMPLATES));
//				   		if (ff.exists()&&ff.isFile()) {
//			        		new Templates(1,0,null).processTemplate(st.readFileString(ff));
//			        		Templates.destroy();
//				   		}
//				   		else if (ff.exists()&&ff.isDirectory()) {
//			            	new Templates(1,0,el.tpl_name).makeCommonMenu();
//				   		} else
//				   			st.toast("Template not found: "+ el.tpl_name);
					} else
						if (ServiceJbKbd.inst!=null)
							ServiceJbKbd.inst.processKey(el.code);
    			} 
				else if (txt.length()== 1) {
					if (ServiceJbKbd.inst!=null)
						ServiceJbKbd.inst.processKey(txt.charAt(0));
				} else
    				new Templates(1,0,null).processTemplate(txt);
        		}
    		});
        	tv.setOnTouchListener(new View.OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
		        	switch (event.getAction())
		        	{
		        	case MotionEvent.ACTION_DOWN:
		        		v.setBackgroundColor(Color.GRAY);
		        		break;
		        	case MotionEvent.ACTION_CANCEL:
		        	case MotionEvent.ACTION_UP:
		        		v.setBackgroundColor(st.btn_bg);
		        		break;
		        	}
					return false;
				}
			});
        	tv.setGravity(Gravity.CENTER);
        	tv.setMaxLines(1);
        	tv.setTransformationMethod(null);
        	tv.setLayoutParams(btnpar);
        	
        	tv.measure(0, 0);
            close.measure(0, 0);
            ll.measure(0, 0);
            int llcnt = ll.getMeasuredWidth();
            if (llcnt+tv.getMeasuredWidth()+MARGIN>ll_btn_width){
        		llrow.addView(ll, llrowpar);
        		int ww = llrow.getWidth();
        		llrow.setMinimumWidth(ww+100);
                ll = new LinearLayout(m_c);
                ll.setOrientation(LinearLayout.HORIZONTAL);
        	}
        	ll.addView(tv, llpar);
        }
        if (ll.getChildCount() > 0)
    		llrow.addView(ll, llrowpar);
        llmain = (RelativeLayout)v;
    	m_wm.addView(llmain, lp);
    	fl_popupcharacter_window = true;
    	return true;
    }
    public void setBackgroundOnTouch(View v)
    {
    	
    }
    public void close()
    {
    	if (pw!=null){
    		pw.dismiss();
    		pw=null;

    	}
    	else if (m_wm != null){
    		if (llmain == null)
    			return;
            try{
            	m_wm.removeViewImmediate(llmain);
           		llleft=null;
           		llright=null;
           		llmain = null;
            } catch(Throwable e){}
    	}
// не используется    	
//		if (mkey!=null){
//			mkeyPressed = mkey.pressed;
//			mkey.pressed = false;
//			st.kv().invalidateKey(st.kv().getKeyIndex(mkey));//.invalidateAllKeys();
//			mkey.pressed = mkeyPressed;
//			mkey = null;
//		}
    	fl_popupcharacter_window = false;
    	st.fl_popupcharacter2 = false;
    }
    // определяет кнопку по точке отрыва 
    // и возвращает прописанный на ней символ
    // иначе 0
     @SuppressLint("NewApi")
	char getTouchUpSymbol(float x, float y)
    {
//    	 int hh=0;
//    	 if (hh==0)
//    		 return 0;
    	 if (!fl_popupcharacter_window)
    		 return 0;
    	 if (llleft==null)
    		 return 0;
    	 if (kvh == -1)
    		 return 0;
    	 LinearLayout ll=null;
    	 TextView b = null;
    	for (int i=0;i<llleft.getChildCount();i++){
    		try {
        		ll = (LinearLayout)llleft.getChildAt(i);
    		}
            catch (Throwable e) {
            	continue;
            }
    		for (int i1=0;i1<ll.getChildCount();i1++){
        		try {
            		b = (TextView)ll.getChildAt(i1);
        		}
                catch (Throwable e) {
                	continue;
                }
        		if (b == null)
        			continue;
        		if (llleft== null)
        			return 0;
//        		int llx = llleft.getLeft();
//        		int lly = llleft.getTop();
//        		float xb = wm_x+llx+b.getLeft();
//        		float xe = wm_x+llx+b.getRight();
//        		float yb = wm_y+b.getTop();
//        		float ye = wm_y+b.getBottom();
        		//b.measure(0, 0);
        		int llx = llleft.getLeft();
        		int lly = llleft.getTop();
        		int xxb = b.getLeft();
        		int xxe = b.getRight();
        		float xb = wm_up_x+llx+b.getLeft();
        		float xe = wm_up_x+llx+b.getRight();
        		float yb = kvh-Math.abs(wm_up_y)+b.getTop();
        		float ye = kvh-Math.abs(wm_up_y)+b.getBottom();
        		if (x>=xb
        		  &&x<=xe
        		  &&y>=yb
        		  &&y<=ye)
        			return b.getText().charAt(0);
    			
    		}
    	}
    	return 0;
    }

}