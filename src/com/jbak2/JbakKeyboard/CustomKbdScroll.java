package com.jbak2.JbakKeyboard;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.xmlpull.v1.XmlPullParser;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.RectShape;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.Keyboard.Row;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jbak2.CustomGraphics.GradBack;
import com.jbak2.CustomGraphics.draw;
import com.jbak2.JbakKeyboard.EditSetActivity.EditSet;
import com.jbak2.JbakKeyboard.IKeyboard.Keybrd;
import com.jbak2.JbakKeyboard.IKeyboard.Lang;
import com.jbak2.JbakKeyboard.JbKbd.LatinKey;
import com.jbak2.JbakKeyboard.com_menu.MenuEntry;
import com.jbak2.ctrl.Font;
import com.jbak2.ctrl.GlobDialog;

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
	/** Опорное значение ID для элементов якорей левого меню */
	public static final int ID_TV_MAIN_VALUE_ANHCOR = 1100;

    EditSet m_es = null;;
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
	String[] arAnhcor = null; 
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
		arAnhcor = null;
		jkbview = st.kv();
		try {
			max_tv_width = ck.getSize("12%p",ck.m_displayWidth,50,ck.B_keyWidth);
		} catch (IOException e) {
		}
        m_es = new EditSet();
        m_es.load(st.PREF_KEY_MAIN_FONT);

	}
	/** Создаёт и показывает раскладку */
	void loadKeyboard(XmlPullParser parser) 
	{
		if (inst == null)
			init();
		try {
			parseKeys(parser);
		} catch (IOException e) {
		}
        if(ck.m_os==null)
        	show();
	}
	/** Создаёт и показывает раскладку */
	final byte loadKeyboard(DataInputStream is,byte b) 
	{
		if (inst == null)
			init();
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
            b = is.readByte();
        } while(b<ck.BA_KEYS);
        return b;
    }
	/** Обработчик длинного нажатия элемента меню */
	OnLongClickListener m_longListener=new OnLongClickListener(){
		@Override 
		public boolean onLongClick(View v)
		{
        	st.toast(R.string.in_developing);
			return true;
		}
	};

	static void close() 
	{
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
        	ScrollView svl = null;
        	int id = v.getId();
            switch (id)
            {
            // левый лайот
            case ID_TV_FAVORITE: 
            	st.toast(R.string.in_developing);
            	return;
            case ID_TV_ALL: 
            	st.toast(R.string.in_developing);
            	return;
            case ID_TV_LEFT_TOP:
            	svl = (ScrollView)m_MainView.findViewById(R.id.ks_scroll_left);
            	svl.scrollTo(0, 0);
            	return;
            case ID_TV_LEFT_BOTTOM: 
            	svl = (ScrollView)m_MainView.findViewById(R.id.ks_scroll_left);
            	svl.scrollTo(0, svl.getHeight()+10000);
            	return;
            case ID_TV_RIGHT_TOP:
            	svl = (ScrollView)m_MainView.findViewById(R.id.ks_scroll_right);
            	svl.scrollTo(0, 0);
            	return;
            case ID_TV_RIGHT_BOTTOM: 
            	svl = (ScrollView)m_MainView.findViewById(R.id.ks_scroll_right);
            	svl.scrollTo(0, svl.getHeight()+10000);
            	return;

            // правый лайот
           	case ID_TV_ABC: 
           		close();
           		return;
           	case ID_TV_DELETE:
           		Templates.template_processing= true;
                ServiceJbKbd.inst.processKey(st.TXT_ED_DEL);
           		Templates.template_processing= false;
           		return;
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
           	case ID_TV_ARROW_LEFT:
           		Templates.template_processing= true;
                ServiceJbKbd.inst.processKey(21);
           		Templates.template_processing= false;
           		return;
           	case ID_TV_ARROW_RIGHT:
           		Templates.template_processing= true;
                ServiceJbKbd.inst.processKey(22);
           		Templates.template_processing= false;
           		return;
           	case ID_TV_VOSKLICATELNY_ZNAK:
           		Templates.template_processing= true;
                ServiceJbKbd.inst.processKey('!');
           		Templates.template_processing= false;
           		return;
           	case ID_TV_VOPROSITELNY_ZNAK:
           		Templates.template_processing= true;
                ServiceJbKbd.inst.processKey('?');
           		Templates.template_processing= false;
           		return;
           	case ID_TV_COMMA:
           		Templates.template_processing= true;
                ServiceJbKbd.inst.processKey(',');
           		Templates.template_processing= false;
           		return;
           	case ID_TV_POINT:
           		Templates.template_processing= true;
                ServiceJbKbd.inst.processKey('.');
           		Templates.template_processing= false;
           		return;
            }
            // обработка якорей
            if (id >= ID_TV_MAIN_VALUE_ANHCOR) {
            	gv.setSelection(id-ID_TV_MAIN_VALUE_ANHCOR);
            	return;
            }
            ServiceJbKbd.inst.onText(((TextView)v).getText());
        }
    };
    /** @param bspec - true = оформление спецклавиши */
    TextView newTextView(boolean bspec)
    {
    	TextView tv = new TextView(ServiceJbKbd.inst);
        tv.setOnClickListener(m_listener);
        tv.setOnLongClickListener(m_longListener);
        tv = (TextView) jkbview.getCurrentDesign(tv, bspec);
    	tv.setMinWidth(70);
    	if (max_tv_width!= 0)
        	tv.setMinWidth(max_tv_width);
        if(m_es!=null) {
            m_es.setToEditor(tv);
        }
    	
    	return tv;
    }
    void createLeftButton()
    {
        lll = (LinearLayout)m_MainView.findViewById(R.id.ks_ll_left);
        TextView tv = null;
        tv = newTextView(true);
        tv.setId(ID_TV_FAVORITE);
        tv.setOnClickListener(m_listener);
        tv.setText("★");
        lll.addView(tv);
        font_size = tv.getTextSize();
        
//        tv = newTextView(true);
//        tv.setId(ID_TV_ALL);
//        tv.setOnClickListener(m_listener);
//        tv.setText("All");
//        lll.addView(tv);

        Vector<Integer> ar = new Vector<Integer>();
        int ind = -1;
    	setIndexElementOnArray(ar);

        if (ar.size() > 5) {
            tv = newTextView(true);
            tv.setId(inst.ID_TV_LEFT_BOTTOM);
            tv.setOnClickListener(m_listener);
            Font.setTextOnTypeface(tv, Font.FontArSymbol.PGDN);
            lll.addView(tv);
        }
        for (int i=0;i<ar.size();i++) {
        	ind = ar.get(i);
            tv = newTextView(true);
            tv.setId(ID_TV_MAIN_VALUE_ANHCOR+ind);
            tv.setOnClickListener(m_listener);
            tv.setText(arAnhcor[i]);
            lll.addView(tv);
        }
        if (ar.size() > 5) {
            tv = newTextView(true);
            tv.setId(inst.ID_TV_LEFT_TOP);
            tv.setOnClickListener(m_listener);
            Font.setTextOnTypeface(tv, Font.FontArSymbol.PGUP);
            lll.addView(tv);
        }
        lll.measure(0, 0);
        lll_w = lll.getMeasuredWidth();
    }
    /** устанавливает массив доступных якорей в массиве arKeys */
    void setIndexElementOnArray(Vector<Integer> arIndex) {
    	if (arKeys == null)
    		return;
    	if (arAnhcor == null)
    		return;
// просто для примера, если глиф символа не отображаетя (width = 0
// https://stackoverflow.com/questions/11815458/check-if-custom-font-can-display-character/41100873#41100873    	
//    	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//    	Rect rtt = new Rect();
//    	paint.getTextBounds( "♫", 0, 1, rtt );
//    	    if( rtt.width() == 0 ){
//    	}
    	int i=0;
    	int j =0;
    	int pos = 0;
    	for (i=0;i< arKeys.length;i++) {
    		if (arKeys[i].length() < 1)
    			continue;
    		for (j = 0;j<arAnhcor.length;j++) {
    			if(arAnhcor[j].length()>0 && arKeys[i].compareToIgnoreCase(arAnhcor[j])==0) {
    				arIndex.add(i);
    				pos++;
    				break;
    			}
    		}
    		if (pos-1 == arAnhcor.length)
    			break;
    	}
    }
    void createRightButton()
    {
        llr = (LinearLayout)m_MainView.findViewById(R.id.ks_ll_right);
        TextView tv = null;
        tv = newTextView(true);
        tv.setId(ID_TV_ABC);
//        Font.setTextOnTypeface(tv, Font.FontArSymbol);
        tv.setOnClickListener(m_listener);
        tv.setText("ABC");
        llr.addView(tv);
        
        tv = newTextView(true);
        tv.setId(inst.ID_TV_RIGHT_BOTTOM);
        tv.setOnClickListener(m_listener);
        Font.setTextOnTypeface(tv, Font.FontArSymbol.PGDN);
        llr.addView(tv);
        
        tv = newTextView(true);
        tv.setId(inst.ID_TV_DELETE);
        tv.setOnClickListener(m_listener);
        tv.setText("del");
        llr.addView(tv);
        
        tv = newTextView(true);
        tv.setId(inst.ID_TV_BACKSPACE);
        tv.setOnClickListener(m_listener);
        Font.setTextOnTypeface(tv, Font.FontArSymbol.BACKSPACE);
        llr.addView(tv);
        
        tv = newTextView(true);
        tv.setId(inst.ID_TV_ENTER);
        tv.setOnClickListener(m_listener);
        Font.setTextOnTypeface(tv, Font.FontArSymbol.ENTER);
        llr.addView(tv);
        
        tv = newTextView(true);
        tv.setId(inst.ID_TV_SPACE);
        tv.setOnClickListener(m_listener);
        Font.setTextOnTypeface(tv, Font.FontArSymbol.SPACE);
        llr.addView(tv);
        
        tv = newTextView(true);
        tv.setId(inst.ID_TV_ARROW_LEFT);
        tv.setOnClickListener(m_listener);
        tv.setText("←");
        llr.addView(tv);
        
        tv = newTextView(true);
        tv.setId(inst.ID_TV_ARROW_RIGHT);
        tv.setOnClickListener(m_listener);
        tv.setText("→");
        llr.addView(tv);
        
        tv = newTextView(true);
        tv.setId(inst.ID_TV_VOSKLICATELNY_ZNAK);
        tv.setOnClickListener(m_listener);
        tv.setText("!");
        llr.addView(tv);
        
        tv = newTextView(true);
        tv.setId(inst.ID_TV_VOPROSITELNY_ZNAK);
        tv.setOnClickListener(m_listener);
        tv.setText("?");
        llr.addView(tv);
        
        tv = newTextView(true);
        tv.setId(inst.ID_TV_COMMA);
        tv.setOnClickListener(m_listener);
        tv.setText(st.STR_COMMA);
        llr.addView(tv);
        
        tv = newTextView(true);
        tv.setId(inst.ID_TV_POINT);
        tv.setOnClickListener(m_listener);
        tv.setText(st.STR_POINT);
        llr.addView(tv);
        
        tv = newTextView(true);
        tv.setId(inst.ID_TV_RIGHT_TOP);
        tv.setOnClickListener(m_listener);
        Font.setTextOnTypeface(tv, Font.FontArSymbol.PGUP);
        llr.addView(tv);
        
        llr.measure(0, 0);
        llr_w = llr.getMeasuredWidth();
    }
/** Показывает раскладку */    
	@SuppressLint("NewApi")
	void show()
    {
        createLeftButton();
        createRightButton();
        int colu = Math.max(lll_w,llr_w);
        int i = 0;
        try {
            for(i=0;i<lll.getChildCount();i++)
            {
            	((TextView)lll.getChildAt(i)).setMinimumWidth(colu);
            }
            for(i=0;i<llr.getChildCount();i++)
            {
            	((TextView)lll.getChildAt(i)).setMinimumWidth(colu);
            }
		} catch (Throwable e) {
		}
        gv = (GridView)m_MainView.findViewById(R.id.ks_grid);
        m_adapter = new Adapt(st.c(), arKeys);

        colu = ck.m_displayWidth-(colu*2);
        colu = colu/(int)(font_size+30);
        colu-=1;
        //colu = colu/120;
        if (colu < 1)
        	colu = 1;
        gv.setNumColumns(colu);
        gv.setAdapter(m_adapter);
        m_MainView.setBackground(jkbview.getBackground());
        ServiceJbKbd.inst.showCandView(false);
        ServiceJbKbd.inst.setInputView(m_MainView);
        ViewGroup.LayoutParams lp = m_MainView.getLayoutParams();
        lp.width= jkbview.getWidth();
        lp.height = jkbview.getHeight();
        m_MainView.setLayoutParams(lp);
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

}