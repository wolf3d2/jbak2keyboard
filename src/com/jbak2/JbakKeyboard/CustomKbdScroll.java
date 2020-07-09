package com.jbak2.JbakKeyboard;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.xmlpull.v1.XmlPullParser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jbak2.CustomGraphics.draw;
import com.jbak2.JbakKeyboard.EditSetFontActivity.EditSet;
import com.jbak2.JbakKeyboard.IKeyboard.Keybrd;
import com.jbak2.ctrl.Font;

/** Раскладка с вертикальным скролом */
public class CustomKbdScroll 
{
	public static final int ID_TV_FAVORITE = 1001;
	public static final int ID_TV_ALL = 1002;
	public static final int ID_TV_ABC = 1003;
	public static final int ID_TV_BACKSPACE = 1004;
	public static final int ID_TV_DELETE = 1005;
	public static final int ID_TV_ENTER = 1006;
	public static final int ID_TV_ARROW_LEFT = 1007;
	public static final int ID_TV_ARROW_RIGHT = 1008;
	public static final int ID_TV_VOSKLICATELNY_ZNAK = 1009;
	public static final int ID_TV_VOPROSITELNY_ZNAK = 1010;
	public static final int ID_TV_COMMA = 1011;
	public static final int ID_TV_POINT = 1012;
	public static final int ID_TV_SPACE= 1013;
	public static final int ID_TV_LEFT_TOP= 1014;
	public static final int ID_TV_LEFT_BOTTOM= 1015;
	public static final int ID_TV_RIGHT_TOP= 1016;
	public static final int ID_TV_RIGHT_BOTTOM= 1017;
	public static final int ID_TV_POPUP_KEYBOARD = 1018;
	
	/** Опорное значение ID для элементов якорей левого меню */
	public static final int ID_TV_MAIN_VALUE_ANHCOR = 1100;
	/** якорей может быть не более тысячи */
	public static final int ID_TV_MAX_VALUE_ANHCOR = 2100;
	
/** папка для хранения настроек скроллящихся раскладок */
	public static final String FOLDER_LAYOUT_SETTING = "layout_setting";
	/** имя файла, если не определили название файла раскладки */
	public static final String NOT_NAME_FILENAME = "_notName";
	/** время до удаления файла параметров раскладки */
	public static final long TIME_3MONTH = 3600l*24l*90l*1000l;
	Keybrd mkbd = null;
	LayoutSetting ls = null;
	
	EditSet es_main = null;
	EditSet es_sec = null;
	LinearLayout lll = null; 
	LinearLayout llr = null; 
	JbKbdView jkbview = null;
	/** ширина в 12% служебных кнопок */
	int max_tv_width = 0;
	/** размер текста */
	float font_size = 10;
	/** ширина левого лайота */
	int lll_w = 0;
	/** ширина правого лайота */
	int llr_w = 0;
	GridView gv = null;
	
	String[] arKeys = null;
	/** массив для избранного */
	String[] arFav = null;
	String[] arAnhcor = null;
	/** строка пользовательской popupCharacters v2 */
	String arPopCh= null; 

	static CustomKeyboard ck = null;
	Adapt m_adapter;
	static CustomKbdScroll inst;
	static View m_MainView;
	
	/** Раскладка с вертикальным скролом. <br>
	 * Сперва делаем переменную с нужным конструктором <br>
	 *  и потом вызываем соответствующую loadKeyboard  <br>
	 *  (XmlPullParser или DataInputStream)
	 * @param custom_keyboard - CustomKeyboard
	 * @param xmlparser - уже открытый XmlPullParser
	 */
	CustomKbdScroll(CustomKeyboard custom_keyboard) {
		ck = custom_keyboard;
		init();
	}
	public void init()
	{
		inst = this;
		m_MainView = ServiceJbKbd.inst.getLayoutInflater().inflate(R.layout.keyboard_scroll, null);
		arKeys = null;
		arFav = null;
		arAnhcor = null;
		arPopCh = null;
		jkbview = st.kv();
		try {
			max_tv_width = ck.getSize("12%p",ck.m_displayWidth,50,ck.B_keyWidth);
		} catch (IOException e) {
		}
        es_main = new EditSet();
        es_main.load(st.PREF_KEY_MAIN_FONT);
        es_sec = new EditSet();
        es_sec.load(st.PREF_KEY_SECOND_FONT);

	}
	/** Создаёт и показывает раскладку */
	void loadKeyboard(XmlPullParser parser, Keybrd kbd) 
	{
		if (inst == null)
			init();
		mkbd = kbd;
		try {
			parseKeys(parser);
		} catch (IOException e) {
		}
        if(ck.m_os==null)
        	show();
	}
	/** Создаёт и показывает раскладку */
	final byte loadKeyboard(DataInputStream is,byte b, Keybrd kbd) 
	{
		if (inst == null)
			init();
		mkbd = kbd;
		try {
			b = parseKeys(is, b);
		} catch (IOException e) {
		}
        if(ck.m_os==null)
        	show();
        return b;

	}
    final boolean parseKeys(XmlPullParser p) throws IOException
    {
        if(ck.m_os!=null)
            ck.m_os.writeByte(ck.BA_KEYS);

    	String str = null;
        int cnt = p.getAttributeCount();
        String name = st.STR_NULL;
        for(int i=0;i<cnt;i++)
        {
            name = ck.attName(p, i);
            if(name.equals(ck.A_arrayKeys))
            {
            	str = p.getAttributeValue(i);
            	arKeys = str.split(st.STR_COMMA); 
                if(ck.m_os!=null)
                {
                    ck.m_os.writeByte(ck.B_arrayKeys);
                    ck.m_os.writeUTF(str);//.write(str.getBytes());
                }
            }
            else if(name.equals(ck.A_arrayAnchors)) {
            	str = p.getAttributeValue(i);
            	arAnhcor = str.split(st.STR_COMMA); 
                if(ck.m_os!=null)
                {
                    ck.m_os.writeByte(ck.B_arrayAnchors);
                    ck.m_os.writeUTF(str);//.write(str.getBytes());
                }
            }
            else if(name.equals(ck.A_popupCharacters)) {
            	arPopCh = p.getAttributeValue(i);
                if(ck.m_os!=null)
                {
                    ck.m_os.writeByte(ck.B_popupCharacters);
                    ck.m_os.writeUTF(arPopCh);//.write(str.getBytes());
                }
            }
        }
        //ck.processKey(k);
        //m_keys.add(k);
        return true;
    }
    final byte parseKeys(DataInputStream is, byte b) throws IOException
    {
        String str = null;
//        byte b = 0;
        do{
            if (b == ck.B_arrayKeys) {
            	str = is.readUTF();
            	arKeys = str.split(st.STR_COMMA); 
                if(ck.m_os!=null)
                {
                    ck.m_os.writeByte(ck.B_arrayKeys);
                    ck.m_os.writeUTF(str);//.write(str.getBytes());
                }
            }
            else if (b == ck.B_arrayAnchors) {
            	str = is.readUTF();
            	arAnhcor = str.split(st.STR_COMMA); 
                if(ck.m_os!=null)
                {
                    ck.m_os.writeByte(ck.B_arrayAnchors);
                    ck.m_os.writeUTF(str);//.write(str.getBytes());
                }
            }
            else if (b == ck.B_popupCharacters) {
            	arPopCh = is.readUTF();
                if(ck.m_os!=null)
                {
                    ck.m_os.writeByte(ck.B_popupCharacters);
                    ck.m_os.writeUTF(arPopCh);//.write(str.getBytes());
                }
            }
            b = is.readByte();
        } while(b<ck.BA_KEYS);
        return b;
    }
	/** Обработчик длинного нажатия элемента меню */
	OnLongClickListener m_longListener=new OnLongClickListener(){
		@Override 
		public boolean onLongClick(View v)
		{
        	ScrollView sv = null;
			int id = v.getId();
        	switch (id)
        	{
            case R.id.ks_tv_favorite_key:
            	sv = (ScrollView)m_MainView.findViewById(R.id.ks_scroll_left);
            	sv.scrollTo(0, sv.getHeight()+10000);
            	return true;
            case R.id.ks_tv_abc_key:
            	sv = (ScrollView)m_MainView.findViewById(R.id.ks_scroll_right);
            	sv.scrollTo(0, sv.getHeight()+10000);
            	return true;
        	case ID_TV_FAVORITE:
        	case ID_TV_ALL:
        	case ID_TV_ABC:
        	case ID_TV_BACKSPACE:
        	case ID_TV_DELETE:
        	case ID_TV_ENTER:
        	case ID_TV_ARROW_LEFT:
        	case ID_TV_ARROW_RIGHT:
        	case ID_TV_VOSKLICATELNY_ZNAK:
        	case ID_TV_VOPROSITELNY_ZNAK:
        	case ID_TV_COMMA:
        	case ID_TV_POINT:
        	case ID_TV_SPACE:
        	case ID_TV_LEFT_TOP:
        	case ID_TV_LEFT_BOTTOM:
        	case ID_TV_RIGHT_TOP:
        	case ID_TV_RIGHT_BOTTOM:
        		return false;
        	}
        	if (id>-1&&id < ID_TV_MAX_VALUE_ANHCOR)
        		return false;
        	updateFavoriteArray(((TextView)v).getText().toString());
			return true;
		}
	};

	static void close() 
	{
		if (inst.ls!=null)
			inst.ls.saveLayoutSetting();
		inst = null;
        if(ServiceJbKbd.inst!=null)
        {
            try{
        		Keybrd kb = st.getCurQwertyKeybrd();
	        	st.kv().setKeyboard(st.loadKeyboard(kb));
                ServiceJbKbd.inst.setInputView(st.kv());
                st.showAcPlace();
            }
            catch (Throwable e) {
            }
        }
	}

/** Обработчик короткого нажатия кнопок меню */    
    View.OnClickListener m_listener = new View.OnClickListener()
    {
        
        @Override
        public void onClick(View v)
        {
            VibroThread vt = VibroThread.getInstance(st.c());
            vt.runVibro(VibroThread.VIBRO_SHORT);
        	ScrollView sv = null;
        	int id = v.getId();
            switch (id)
            {
            // левый лайот
            case R.id.ks_tv_favorite_key:
            case ID_TV_FAVORITE:
            	ls.favorite = !ls.favorite;
        		typeAdapter(ls.favorite);
//            	if (gv!=null&&gv.getCount()>0) {
//            		int iii = gv.getCount();
//            		typeAdapter(ls.favorite);
//            	}
        		setTextFavoriteButton((TextView) v);

            	return;
            case ID_TV_ALL: 
            	st.toast(R.string.in_developing);
            	return;
            case ID_TV_LEFT_TOP:
            	sv = (ScrollView)m_MainView.findViewById(R.id.ks_scroll_left);
            	sv.scrollTo(0, 0);
            	return;
            case ID_TV_LEFT_BOTTOM: 
            	sv = (ScrollView)m_MainView.findViewById(R.id.ks_scroll_left);
            	sv.scrollTo(0, sv.getHeight()+10000);
            	return;
            case ID_TV_RIGHT_TOP:
            	sv = (ScrollView)m_MainView.findViewById(R.id.ks_scroll_right);
            	sv.scrollTo(0, 0);
            	return;
            case ID_TV_RIGHT_BOTTOM: 
            	sv = (ScrollView)m_MainView.findViewById(R.id.ks_scroll_right);
            	sv.scrollTo(0, sv.getHeight()+10000);
            	return;

            // правый лайот
           	case R.id.ks_tv_abc_key: 
           	case ID_TV_ABC: 
           		close();
           		return;
            case ID_TV_POPUP_KEYBOARD:
            	if (ServiceJbKbd.inst == null)
            		return;
            	int hh = m_MainView.getHeight();
            	String pop = "v2 $[-303,$f#ɐ] $[-304,$f#ɑ] $[-328,$f#ɒ] $[-329,$f#ɓ] $[19,↑] "
            			+ "$[20,↓] $[21,←] $[22,→] $[-323,sAll] $[-320,$f#ɢ] $[-321,$f#ɣ] "
            			+ "$[-601,$f#ɕ] $[-5,$f#ɔ] $[10,$f#ɜ] $[32,$f#ɝ] ! ? , .";
            	if (arPopCh!=null) {
            		if (arPopCh.startsWith("v2 ")||arPopCh.startsWith("V2 "))
            			arPopCh = arPopCh.substring(3);
            		pop += st.STR_SPACE+st.STR_PREFIX_LINE+st.STR_SPACE+arPopCh;
            	}
            	PopupKeyboard pk = new PopupKeyboard(m_MainView.getContext());
            	pk.createFullPopupWindow(pop, hh,true, true);
            	return;
//           	case ID_TV_DELETE:
//           		Templates.template_processing= true;
//                ServiceJbKbd.inst.processKey(st.TXT_ED_DEL);
//           		Templates.template_processing= false;
//           		return;
           	case ID_TV_BACKSPACE:
           		Templates.template_processing= true;
                ServiceJbKbd.inst.processKey(Keyboard.KEYCODE_DELETE);
           		Templates.template_processing= false;
           		return;
           	case ID_TV_ENTER:
           		Templates.template_processing= true;
                ServiceJbKbd.inst.processKey('\n');
           		Templates.template_processing= false;
           		return;
           	case ID_TV_SPACE:
           		Templates.template_processing= true;
                ServiceJbKbd.inst.processKey(' ');
           		Templates.template_processing= false;
           		return;
//           	case ID_TV_ARROW_LEFT:
//           		Templates.template_processing= true;
//                ServiceJbKbd.inst.processKey(21);
//           		Templates.template_processing= false;
//           		return;
//           	case ID_TV_ARROW_RIGHT:
//           		Templates.template_processing= true;
//                ServiceJbKbd.inst.processKey(22);
//           		Templates.template_processing= false;
//           		return;
//           	case ID_TV_VOSKLICATELNY_ZNAK:
//           		Templates.template_processing= true;
//                ServiceJbKbd.inst.processKey('!');
//           		Templates.template_processing= false;
//           		return;
//           	case ID_TV_VOPROSITELNY_ZNAK:
//           		Templates.template_processing= true;
//                ServiceJbKbd.inst.processKey('?');
//           		Templates.template_processing= false;
//           		return;
//           	case ID_TV_COMMA:
//           		Templates.template_processing= true;
//                ServiceJbKbd.inst.processKey(',');
//           		Templates.template_processing= false;
//           		return;
//           	case ID_TV_POINT:
//           		Templates.template_processing= true;
//                ServiceJbKbd.inst.processKey('.');
//           		Templates.template_processing= false;
//           		return;
            }
            // обработка якорей
            if (id >= ID_TV_MAIN_VALUE_ANHCOR) {
            	gv.setSelection(id-ID_TV_MAIN_VALUE_ANHCOR);
            	return;
            }
            ServiceJbKbd.inst.onText(((TextView)v).getText());
        }
    };
    TextView newTextView(boolean bspec)
    {
    	return newTextView(bspec, null);
    }
    /** создаёт новую кнопку. <br>
     *  Если tv != null, то применяем оформление к указанному tv.  
     * @param bspec - true = оформление спецклавиши */
    TextView newTextView(boolean bspec, TextView tv)
    {
    	if (tv == null)
    		tv = new TextView(ServiceJbKbd.inst);
        tv.setOnClickListener(m_listener);
        tv.setOnLongClickListener(m_longListener);
        tv = (TextView) jkbview.getCurrentKeyDesign(tv, bspec);
    	tv.setMinWidth(50);
    	if (max_tv_width!= 0)
        	tv.setMinWidth(max_tv_width);
    	if (bspec) {
    		if (es_sec.fontSize== 0)
    			tv.setTextSize(JbKbdView.DEF_MAIN_FONT_SIZE);
    		else
    			es_sec.setToEditor(tv);
    	} else {
    		if (es_main.fontSize== 0)
    			tv.setTextSize(JbKbdView.DEF_MAIN_FONT_SIZE);
    		else
    			es_main.setToEditor(tv);
    	}
//    	es_main.setToEditor(tv);
    	return tv;
    }
    void setTextFavoriteButton(TextView tv)
    {
        if (ls.favorite)
            tv.setText("All");
        else
            tv.setText("★");
    	
    }
    void createLeftButton()
    {
        lll = (LinearLayout)m_MainView.findViewById(R.id.ks_ll_left);
        TextView tv = null;

        tv = (TextView)m_MainView.findViewById(R.id.ks_tv_favorite_key);
        tv = newTextView(true, tv);
        setTextFavoriteButton(tv);
        tv.measure(0, 0);
        int ww = tv.getMeasuredWidth();
        font_size = tv.getTextSize();
        
//        tv = newTextView(true);
//        tv.setId(ID_TV_FAVORITE);
//        tv.setOnClickListener(m_listener);
//        setTexFavoriteButton(tv);
//        lll.addView(tv);
//        font_size = tv.getTextSize();
        
        Vector<Integer> ar = new Vector<Integer>();
        int ind = -1;
    	setIndexElementOnArray(ar);

//        if (ar.size() > 5) {
//            tv = newTextView(true);
//            tv.setId(inst.ID_TV_LEFT_BOTTOM);
//            Font.setTextOnTypeface(tv, Font.FontArSymbol.PGDN);
//            lll.addView(tv);
//        }
        for (int i=0;i<ar.size();i++) {
        	ind = ar.get(i);
            tv = newTextView(true);
            tv.setId(ID_TV_MAIN_VALUE_ANHCOR+ind);
            tv.setText(arKeys[ind]);
            lll.addView(tv);
        }
        if (ar.size() > 5) {
            tv = newTextView(true);
            tv.setId(inst.ID_TV_LEFT_TOP);
            Font.setTextOnTypeface(tv, Font.FontArSymbol.PGUP);
            lll.addView(tv);
        }
        lll.measure(0, 0);
        lll_w = lll.getMeasuredWidth();
        if (ww > lll_w)
        	lll_w = ww;
    }
    /** устанавливает массив доступных якорей в массиве arKeys */
    void setIndexElementOnArray(Vector<Integer> arIndex) {
    	if (arKeys == null)
    		return;
    	if (arAnhcor == null)
    		return;
// просто для примера, если глиф символа не отображаетя (width = 0)
// https://stackoverflow.com/questions/11815458/check-if-custom-font-can-display-character/41100873#41100873    	
//    	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//    	Rect rtt = new Rect();
//    	paint.getTextBounds( "♫", 0, 1, rtt );
//    	    if( rtt.width() == 0 ){
//    	}
    	int i=0;
    	int ind =0;
		for (i=0;i<arAnhcor.length;i++) {
			if(arAnhcor[i].length()>3 ) {
				arAnhcor[i] = st.STR_NULL;
				continue;
			}
			ind = Arrays.asList(arKeys).indexOf(arAnhcor[i]);
			if (ind != -1) {
				arIndex.add(ind);
			}
		}
    }
    void createRightButton()
    {
        llr = (LinearLayout)m_MainView.findViewById(R.id.ks_ll_right);
        TextView tv = null;
        tv = (TextView)m_MainView.findViewById(R.id.ks_tv_abc_key);
        tv = newTextView(true, tv);
        tv.measure(0, 0);
        int ww = tv.getMeasuredWidth();
        
//        tv = newTextView(true);
//        tv.setId(ID_TV_ABC);
//        tv.setOnClickListener(m_listener);
//        tv.setText("ABC");
//        llr.addView(tv);
        
//        tv = newTextView(true);
//        tv.setId(inst.ID_TV_RIGHT_BOTTOM);
//        Font.setTextOnTypeface(tv, Font.FontArSymbol.PGDN);
//        llr.addView(tv);
        
        tv = newTextView(true);
        tv.setId(inst.ID_TV_POPUP_KEYBOARD);
        tv.setSoundEffectsEnabled(false);
        Font.setTextOnTypeface(tv, Font.FontArSymbol.KEYBOARD);
        llr.addView(tv);
        
//        tv = newTextView(true);
//        tv.setId(inst.ID_TV_DELETE);
//        tv.setSoundEffectsEnabled(false);
//        tv.setText("del");
//        llr.addView(tv);
        
        tv = newTextView(true);
        tv.setId(inst.ID_TV_BACKSPACE);
        tv.setSoundEffectsEnabled(false);
        Font.setTextOnTypeface(tv, Font.FontArSymbol.BACKSPACE);
        llr.addView(tv);
        
        tv = newTextView(true);
        tv.setId(inst.ID_TV_ENTER);
        tv.setSoundEffectsEnabled(false);
        Font.setTextOnTypeface(tv, Font.FontArSymbol.ENTER);
        llr.addView(tv);
        
        tv = newTextView(true);
        tv.setId(inst.ID_TV_SPACE);
        tv.setSoundEffectsEnabled(false);
        Font.setTextOnTypeface(tv, Font.FontArSymbol.SPACE);
        llr.addView(tv);
        
//        tv = newTextView(true);
//        tv.setId(inst.ID_TV_ARROW_LEFT);
//        tv.setSoundEffectsEnabled(false);
//        tv.setText("←");
//        llr.addView(tv);
//        
//        tv = newTextView(true);
//        tv.setId(inst.ID_TV_ARROW_RIGHT);
//        tv.setSoundEffectsEnabled(false);
//        tv.setText("→");
//        llr.addView(tv);
//        
//        tv = newTextView(true);
//        tv.setId(inst.ID_TV_VOSKLICATELNY_ZNAK);
//        tv.setSoundEffectsEnabled(false);
//        tv.setText("!");
//        llr.addView(tv);
//        
//        tv = newTextView(true);
//        tv.setId(inst.ID_TV_VOPROSITELNY_ZNAK);
//        tv.setSoundEffectsEnabled(false);
//        tv.setText("?");
//        llr.addView(tv);
//        
//        tv = newTextView(true);
//        tv.setId(inst.ID_TV_COMMA);
//        tv.setSoundEffectsEnabled(false);
//        tv.setText(st.STR_COMMA);
//        llr.addView(tv);
//        
//        tv = newTextView(true);
//        tv.setId(inst.ID_TV_POINT);
//        tv.setSoundEffectsEnabled(false);
//        tv.setText(st.STR_POINT);
//        llr.addView(tv);
        
        tv = newTextView(true);
        tv.setId(inst.ID_TV_RIGHT_TOP);
        Font.setTextOnTypeface(tv, Font.FontArSymbol.PGUP);
        llr.addView(tv);
        
        llr.measure(0, 0);
        llr_w = llr.getMeasuredWidth();
        if (ww > lll_w)
        	lll_w = ww;
    }
/** Показывает раскладку */    
	@SuppressLint("NewApi")
	void show()
    {
		if (ls == null)
			ls = new LayoutSetting();
		ls.readLayoutSetting();
        createLeftButton();
        createRightButton();
        int colu = Math.max(lll_w,llr_w);
        int i = 0;
        try {
        	((TextView)m_MainView.findViewById(R.id.ks_tv_favorite_key)).setMinimumWidth(colu);
        	((TextView)m_MainView.findViewById(R.id.ks_tv_abc_key)).setMinimumWidth(colu);
            for(i=0;i<lll.getChildCount();i++)
            {
            	((TextView)lll.getChildAt(i)).setMinimumWidth(colu);
            }
            for(i=0;i<llr.getChildCount();i++)
            {
            	((TextView)llr.getChildAt(i)).setMinimumWidth(colu);
            }
		} catch (Throwable e) {
		}
        gv = (GridView)m_MainView.findViewById(R.id.ks_grid);
        colu = ck.m_displayWidth-(colu*2);
        colu = colu/(int)(font_size+50);
        colu-=1;
        //colu = colu/120;
        if (colu < 1)
        	colu = 1;
        gv.setNumColumns(colu);
        typeAdapter(ls.favorite);
//        setVisibleGridView();
        //m_adapter = new Adapt(st.c(), arKeys);
        //gv.setAdapter(m_adapter);
        m_MainView.setBackground(jkbview.getBackground());
        ServiceJbKbd.inst.showCandView(false);
        ServiceJbKbd.inst.setInputView(m_MainView);
        ViewGroup.LayoutParams lp = m_MainView.getLayoutParams();
        lp.width= jkbview.getWidth();
        lp.height = jkbview.getHeight();
        m_MainView.setLayoutParams(lp);
    }
	/** устанавливаем какой грид выводить - избранное или общее <br>
	 * и тут-же устанавливает адаптер в grid*/
	public void typeAdapter(boolean favorite)
	{
//		if (m_adapter!=null)
//			m_adapter.clear();
		if (!favorite)
			if (arKeys != null) {
				m_adapter = new Adapt(st.c(), arKeys);
			} else {
				m_adapter = null;
			}
		else {
			if (arFav != null) {
				m_adapter = new Adapt(st.c(), arFav);
			} else {
				m_adapter = null;
			}
		}
		setVisibleOficialButton(ls.favorite);
		TextView tv = (TextView)m_MainView.findViewById(R.id.ks_tv_favorite_null_text);
		tv.setMovementMethod(new ScrollingMovementMethod());
		if (m_adapter == null) {
    		gv.setVisibility(View.GONE);
			if (ls.favorite) {
	    		tv.setVisibility(View.GONE);
	        	if (arFav == null) {
	        		tv.setVisibility(View.VISIBLE);
//	        		gv.setVisibility(View.GONE);
	        	}				
			}
			return;
		}
		else if (m_adapter != null) {
    		gv.setVisibility(View.VISIBLE);
			if (!ls.favorite) {
	    		tv.setVisibility(View.GONE);
			}
		}

		if (gv != null)
			gv.setAdapter(m_adapter);
		
	}

	static class Adapt extends ArrayAdapter<List<Key>>
    {
		String[] arKeys; 
        public Adapt(Context context, String[] arKey)
        {
            super(context,0);
            arKeys = arKey;
        }
        @Override
        public int getCount() 
        {
            return arKeys.length;
        };
        @SuppressLint("NewApi")
		@Override
        public View getView(int pos, View convertView, ViewGroup parent)
        {
            if(convertView!=null)
            {
                convertView = newView(pos, (View) convertView);
            }
            else
            {
                convertView = newView(pos, null);
            }
            return convertView;
        }
        
		View newView(int pos, View vv) {
			TextView tv = null;
			if (vv == null) {
		        tv = inst.newTextView(false);
			} else {
				tv = (TextView)vv;
			}
			tv.setText(arKeys[pos]);
			return tv;
		}
    }
	String getFilenameFromKeybrd()
	{
		if (mkbd == null) {
			return NOT_NAME_FILENAME;
		}
    	int ind = mkbd.path.lastIndexOf(st.STR_SLASH);
    	if (ind == -1) {
    		return mkbd.path;
    	} else {
    		return mkbd.path.substring(ind+1);
    	}
	}
	
	/** класс, для хранения настроек текущей скроллящейся раскладки */
    public static class LayoutSetting
    {
    	/** время последнего запуска этой раскладки*/
    	long lastStartTime = 0;
    	/** какую панель открывать - избранное или общую */
    	boolean favorite = false;
    	
        public LayoutSetting()
        {
        	lastStartTime = 0;
        	favorite = false;
        }
    	/** записываем параметры текущей раскладки */
    	public void saveLayoutSetting()
    	{
        	String pt = inst.getFilenameFromKeybrd();
        	if (pt.compareTo(NOT_NAME_FILENAME)==0)
        		return;
        	pt = st.getSettingsPath()+CustomKbdScroll.FOLDER_LAYOUT_SETTING+st.STR_SLASH+pt;
        	String delim = ";";
    		lastStartTime = new Date().getTime();
        	String out = lastStartTime+delim;
        	if (favorite)
        		out+= st.STR_ONE;
        	else
        		out+= st.STR_ZERO;
        	out += delim;
        	if (inst.arFav !=null) {
            	for (int i=0;i<inst.arFav.length;i++) {
            		out += inst.arFav[i]+st.STR_COMMA;
            	}
        	}
        	st.savefile(pt, out);

    	}
    	/** загружаем параметры текущей раскладки */
    	public boolean readLayoutSetting()
    	{
        	String pt = st.getSettingsPath()+CustomKbdScroll.FOLDER_LAYOUT_SETTING;
            File ff = new File(pt);
            if(!ff.exists())
            {
                ff.mkdirs();
                inst.setDefaultSettingLayout();
                return true;
            }
            pt += st.STR_SLASH+inst.getFilenameFromKeybrd();            
            ff = new File(pt);
            if(!ff.exists())
            {
                inst.setDefaultSettingLayout();
                return true;
            }
            pt = st.readFileString(pt);
            if (pt == null) {
                inst.setDefaultSettingLayout();
            	return true;
            }
            boolean ret = true;
            String[] ar = pt.split(";");
            try {
        		lastStartTime = Long.parseLong(ar[0]);
				
			} catch (Throwable e) {
				ret = false;
	    		lastStartTime = new Date().getTime();
			}
    		favorite = ar[1].compareTo(st.STR_ZERO)!=0;
    		if (ar.length>=3&&ar[2]!=null&&ar[2].length()>0) {
    			inst.arFav = ar[2].split(st.STR_COMMA);
    		}
    		return ret;
    	}
    }
	/** устанавливаем параметры текущей раскладки по умолчанию*/
	public void setDefaultSettingLayout()
	{
		if (inst.mkbd == null)
			return;
		ls = new LayoutSetting();
		ls.lastStartTime = new Date().getTime();
	}
	public void setVisibleOficialButton(boolean favorite)
	{
		int vis = favorite?View.GONE:View.VISIBLE;
        TextView tv = null;
        // левая панель кнопок
        for (int i=0;i<lll.getChildCount();i++)
        {
        	try {
				tv = (TextView)lll.getChildAt(i);
			} catch (Throwable e) {
				continue;
			}
        	switch (tv.getId())
        	{
        	case ID_TV_LEFT_TOP:
        	case ID_TV_LEFT_BOTTOM:
        		tv.setVisibility(vis);
        		break;
        	default:
        		if (tv.getId() >= ID_TV_MAIN_VALUE_ANHCOR) {
            		tv.setVisibility(vis);
        			//gv.setSelection(tv.getId()-ID_TV_MAIN_VALUE_ANHCOR);
        		}
        	}
        }
	}
	public void updateFavoriteArray(String text)
	{
		String[] art = null;
		int i = 0;
		int pos = 0;
		// если активна панель избранного, значит элемент удаляем
		if (ls.favorite) {
			art = new String[arFav.length-1];
			pos = 0;
			if (art.length>0) {
				for (i=0;i<arFav.length;i++) {
					if (text.compareTo(arFav[i])==0)
						continue;
					art[pos]=arFav[i];
					pos++;
				}
			}
		} else {
			// если панель избранного НЕ АКТИВНА, значит элемент добавляем
			if (arFav!=null) {
				for (i=0;i<arFav.length;i++) {
					if (arFav[i].compareTo(text) == 0) {
						st.toast(R.string.already_exist);
						return;
					}
				}
			}
			if (arFav==null) {
				pos = 1;
				art = new String[pos];
			} else {
				for (i=0;i<arFav.length;i++) {
					
				}
				pos = arFav.length+1;
				art = new String[pos];
		        System.arraycopy(arFav, 0, art, 0, pos-1);
			}
	        art[pos-1] = text;
	        st.toast(R.string.add);
	        
		}
		if (art.length < 1)
			arFav = null;
		else
			arFav = new String[art.length];
		for (i =0;i<art.length;i++)
			arFav[i]=art[i];
        if (ls.favorite)
        	typeAdapter(ls.favorite);
        ls.saveLayoutSetting();
	}
	/** удаляем файлы параметров раскладок, 
	 * если они не запускались больше определённого периода */
	public static void checkFileSettingLayout()
	{
	    new Handler().postDelayed(new Runnable() {
			public void run() {
				long time = 0;
		    	String pt = st.getSettingsPath()+CustomKbdScroll.FOLDER_LAYOUT_SETTING;
		        File ff = new File(pt);
		        if(!ff.exists())
		        {
		            ff.mkdirs();
		            return;
		        }
		        String[] ar = null;
		        File[] arf = st.getFilesFromDir(ff, null);
		        for (int i=0;i<arf.length;i++)
		        {
		        	if (ServiceJbKbd.inst!=null&&ServiceJbKbd.inst.isInputViewShown())
		        		return;
		        	pt = st.readFileString(arf[i]);
		        	ar = pt.split(";");
		        	try {
						time = Long.parseLong(ar[0]);
					} catch (Throwable e) {
						arf[i].delete();
						continue;
					}
		        	if (time > TIME_3MONTH)
		        		arf[i].delete();
		        }
				
	        }
	    }, 50);
		
	}

}