/*
 * Copyright (C) 2008-2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.jbak2.JbakKeyboard;

import java.lang.reflect.Field;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.jbak2.JbakKeyboard.IKeyboard.KbdDesign;
import com.jbak2.JbakKeyboard.IKeyboard.Keybrd;
import com.jbak2.JbakKeyboard.JbKbd.LatinKey;
import com.jbak2.JbakKeyboard.KeyboardGesture.GestureInfo;
import com.jbak2.JbakKeyboard.st.KbdGesture;
import com.jbak2.CustomGraphics.draw;

@SuppressLint("NewApi")
public class JbKbdView extends KeyboardView 
{
	/** флаг, что клавиша уже обработана по длинному нажатию */
	public static boolean processLongKey = false;
	/** итерация нажатой клавиши 
	 * (если есть массив codes[])*/
	public int key_iter = -1;
	/** текущая нажатая клавиша */
	public LatinKey lk_this = null;
	/** прошлая нажатая клавиша */
	public LatinKey lk_prev = null;
	public PopupKeyboard m_pk = null;
	String gstr = st.STR_NULL;
	boolean longpress = false;
	int thiskey_code = 0;
	int lastkey_code = 0;
	boolean temshift = false;
//неиспользуется	
//	static final int INTERVAL_REDRAW = 100;
    KeyDrw m_PreviewDrw = new KeyDrw();
    Drawable m_PreviewDrawable;
    static JbKbdView inst;
    /** Высота клавиш */    
    VibroThread m_vibro;
    int m_ac_place =0;
    int m_KeyHeight =0;
    int m_KeyTextSz =0;
    int step_shift =0;
    StateListDrawable m_KeyBackDrw;
    Drawable m_drwKeyBack;
    Drawable m_drwKeyPress;
    TextPaint m_tpPreview;
    int m_LabelTextSize = 0;
    int m_PreviewTextSize=0;
    PopupKeyWindow m_popup;
    
//!!! массив для жестов    
    KbdGesture m_gestures[]=new KbdGesture[8];
/** Состояние - клавиши в верхнем регистре на одну букву. После ввода любого символа - сбрасывается */    
    public static final int STATE_TEMP_SHIFT    = 0x0000001;
/** Состояние - включён CAPS_LOCK */    
    public static final int STATE_CAPS_LOCK     = 0x0000002;
/** Состояние - включены звуки при наборе текста*/    
    public static final int STATE_SOUNDS        = 0x0000004;
/** НЕ ИСПОЛЬЗУЕТСЯ!!! Состояние - включены жесты при наборе текста */    
    //public static final int STATE_GESTURES      = 0x0000008;
    int m_previewType = 1;
    int m_state = 0;
    int m_PreviewHeight=0;
    public static int defaultVertCorr = -15;
    KeyboardGesture m_gd;
//    static KbdDesign g_lastLoadedDesign=st.arDesign[st.KBD_DESIGN_STANDARD];
//    KbdDesign m_curDesign = g_lastLoadedDesign;
    static KbdDesign g_lastLoadedDesign= null;
    KbdDesign m_curDesign = null;
    static Field g_vertCorrectionField;
    
    public JbKbdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public JbKbdView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
/** Возвращает текущую клавиатуру типа {@link JbKbd}*/    
    public final JbKbd getCurKeyboard()
    {
        return (JbKbd) getKeyboard();
    }
/** Возвращает значение поля f типа int. В случае ошибки возвращает defVal */    
    static int getFieldInt(Field f,Object o,int defVal)
    {
        try{
            f.setAccessible(true);
            return(f.getInt(o));
        }
        catch(Throwable e)
        {
        }
        return defVal;
    }
    Drawable m_defDrawable;
    Drawable m_defBackground;
    String m_designPath;
    OwnKeyboardHandler m_handler;
    Drawable dr = null;
/** Инициализация. Берутся значения приватных переменных для задания размера шрифта */    
    void init()
    {
        inst = this;
        processLongKey = false;
        lk_this = null;
        lk_prev = null;
    	//if (m_pk==null)
    		m_pk = new PopupKeyboard(inst.getContext());
    	if (IKeyboard.arDesign==null)
    		IKeyboard.setDefaultDesign();
        g_lastLoadedDesign = st.arDesign[st.KBD_DESIGN_STANDARD];
        m_curDesign = g_lastLoadedDesign;

        m_vibro = VibroThread.getInstance(getContext());
        String path = st.pref().getString(st.PREF_KEY_KBD_SKIN_PATH, st.STR_NULL+st.KBD_DESIGN_STANDARD);
        KbdDesign d = st.getSkinByPath(path);
        if(d!=g_lastLoadedDesign)
        {
            g_lastLoadedDesign = d.getDesign();
            m_curDesign = g_lastLoadedDesign;
        }
        setPreferences();
        int clr = Color.WHITE;
        if(st.has(m_curDesign.flags, st.DF_BIG_GAP))
          KeyDrw.GAP = KeyDrw.BIG_GAP;
        else
          KeyDrw.GAP = KeyDrw.DEFAULT_GAP;
        if(m_curDesign.drawResId!=0)
            m_KeyBackDrw = (StateListDrawable)getResources().getDrawable(m_curDesign.drawResId);
        if(m_curDesign.m_keyBackground!=null)
        {
        	m_KeyBackDrw = m_curDesign.m_keyBackground.getStateDrawable(); 
        	KeyDrw.GAP = m_curDesign.m_keyBackground.m_gap+2;
        }
        if(m_curDesign.backDrawableRes!=0)
            setBackgroundResource(m_curDesign.backDrawableRes);
        else if(m_curDesign.m_kbdBackground!=null)
        	setBackgroundDrawable(m_curDesign.m_kbdBackground.getStateDrawable());
        else
        {
            if(m_defBackground==null)
                m_defBackground = getBackground();
            else
                setBackgroundDrawable(m_defBackground);
        }
        if(st.kbd_back_pict.length()>0) {
        	try {
        		//int bbb = Integer.valueOf("huk");
        		dr = Drawable.createFromPath(st.kbd_back_pict);
    			setBackground(dr);
			} catch (Throwable e) {
				st.kbd_back_pict=st.STR_NULL;
		    	if (ServiceJbKbd.inst!=null) {
		    		st.pref(ServiceJbKbd.inst).edit().putString(st.KBD_BACK_PICTURE,st.STR_NULL).commit();
		    		st.toastLong(R.string.set_kbd_background_error);
		    	}
			}
        }
        Field[] af = KeyboardView.class.getDeclaredFields();
        String txtCol="mKeyTextColor";  
        String txtSz = "mKeyTextSize";  
        String labSz="mLabelTextSize";  
        String prevText="mPreviewText";
        String prevTs = "mPreviewTextSizeLarge";
        String ph = "mPreviewHeight";
        String keyBack = "mKeyBackground";
        String shadowRadius = "mShadowRadius";
        String handler = "mHandler";
        //String gd = "mGestureDetector";
        String vertCorr = "mVerticalCorrection";
        m_gd = new KeyboardGesture(this);
        for(int i=0;i<af.length;i++)
        {
            Field f = af[i];
            if(f.getName().equals(shadowRadius))
            {
              try {
                f.setAccessible(true);
                f.setFloat(this, 0);
              } catch (Throwable e) {
              }
            }
/** Пытаемся подложить собственный хэндлер */            
            else if(f.getName().equals(handler))
            {
              try {
                f.setAccessible(true);
                m_handler= new OwnKeyboardHandler((Handler)f.get(this),this);
                if(m_handler.m_bSuccessInit)
                    f.set(this,m_handler);
              } catch (Throwable e) {
                  m_handler = null;
              }
            }
// Вертикальная коррекция            
            else if(f.getName().equals(vertCorr)&&g_vertCorrectionField==null)
            {
              try{
                  g_vertCorrectionField = f;
                  g_vertCorrectionField.setAccessible(true);
                  defaultVertCorr = g_vertCorrectionField.getInt(this);
                }
                catch(Throwable e)
                {
                }
            }
            else if(f.getName().equals(txtCol))
            {
              try{
                  f.setAccessible(true);
                  clr = f.getInt(this);
                }
                catch(Throwable e)
                {
                }
            }
            else if(f.getName().equals(txtSz)&&m_KeyTextSz==0)
            {
                m_KeyTextSz = getFieldInt(f, this, 20);
            }
            else if(f.getName().equals(prevTs))
            {
                m_PreviewTextSize = getFieldInt(f, this, 25);
            }
            else if(f.getName().equals(keyBack))
            {
                try
                {
                    f.setAccessible(true);
                    if(m_defDrawable==null)
                        m_defDrawable = (Drawable)f.get(this);
                    if(m_curDesign.drawResId==0&&m_curDesign.m_keyBackground==null)
                    {
                        f.set(this,m_defDrawable);
                        m_KeyBackDrw = (StateListDrawable) m_defDrawable;
                    }
                    else
                    {
                        f.set(this, m_KeyBackDrw);
                    }
                }
                catch(Throwable e)
                {
                    m_KeyBackDrw = null;
                }
            }
            else if(f.getName().equals(labSz)&&m_LabelTextSize==0)
            {
                m_LabelTextSize = getFieldInt(f, this, 12);
            }
//            else if(f.getName().equals(gd)&&st.has(m_state, STATE_GESTURES))
//            {
//                try{
//                    m_gd = new KeyboardGesture(this);
//                    f.setAccessible(true);
//                    f.set(this, m_gd);
//                }
//                catch (Throwable e) {
//                }
//            }
            else if(f.getName().equals(ph)&&m_PreviewHeight==0)
            {
                m_PreviewHeight = getFieldInt(f, this, 80);
            }
            else if(f.getName().equals(prevText))
            {
                try{
                f.setAccessible(true);
                m_tpPreview = ((TextView)f.get(this)).getPaint();
                }
                catch(Throwable e)
                {
                    m_tpPreview = null;
                }
            }
        }
        if(isDefaultDesign()&&m_defDrawable!=null)
        {
            // Дёргаем фон ненажатой кнопки
            try{
                StateListDrawable ds = (StateListDrawable)m_defDrawable;
                m_drwKeyBack = ds.getCurrent();
                int stat[] = m_KeyBackDrw.getState();
                // Дёргаем фон нажатой кнопки
                ds.setState(PRESSED_ENABLED_STATE_SET);

                m_drwKeyPress = ds.getCurrent();
                m_KeyBackDrw.setState(stat);
            }
            catch(Throwable e)
            {
            }
        }
        draw.paint().setDefault(m_curDesign, clr);
        draw.paint().createFromSettings();
        setVerticalCorrection(m_vertCorr);
        m_popup = new PopupKeyWindow(getContext(),m_PreviewHeight,m_PreviewHeight);
        m_popup.m_bShowUnderKey = m_previewType==1;
        if(m_tpPreview==null)
        {
            m_tpPreview = new TextPaint(draw.paint().main);
        }
        m_tpPreview.setTextSize(m_PreviewTextSize);
    }
    
    boolean processLongPress(LatinKey key)
    {
    	m_vibro.runVibro(VibroThread.VIBRO_LONG);
        if(key.popupCharacters!=null)
        {
            key.processed = false;
            invalidateKey(getKeyIndex(key));
            return false;
        }
        //m_pressed.setPress(key.codes[0], PressArray.TYPE_LONG);
//        key.pressed=false;
        if(key.longCode!=0)
        {
            if(isUserInput())
                ServiceJbKbd.inst.processKey(key.longCode);
            return true;
        }
// вызывает клавиатуру смайликов
        else if(key.codes[0] == 10)
        {
        	longpress = false;
            st.setSmilesKeyboard();
            invalidateAllKeys();
            return true;
        }
        else if(key.codes[0] == Keyboard.KEYCODE_SHIFT)
        {
// вызывает клавиатуру редактирования текста
        	st.setTextEditKeyboard();
            return true;
        }
        else 
        {
        	if(key.runSpecialInstructions(true))
        		return true;
            String t = key.getUpText();
            if(t!=null)
            {
                if(isUserInput()&&t.length()==1)
                    ServiceJbKbd.inst.processKey(t.charAt(0));
                else
                    m_actionListener.onText(t);
                if (key.isGoQwerty()){
// возврат на qwerty клавиатуру при долгом нажатии (если установлено)
                	ServiceJbKbd.inst.beep(97);
                	st.setQwertyKeyboard();
                }
                return true;
            }
        }
        return false;
    }
    @Override
    protected boolean onLongPress(Key key) {
    	lk_this = (LatinKey)key;
        key_iter = -1;
    	if (ServiceJbKbd.inst.isMacro(((LatinKey)key).longCode))
    		return false;
        if(m_previewType>0&&m_handler!=null&&hasKey(key))
        {
            longpress = true;
            if(((LatinKey)key).longCode==st.CMD_VOICE_RECOGNIZER)
            {
                closing();
            }
            else
            {
                m_handler.removeMessages(OwnKeyboardHandler.MSG_REMOVE_PREVIEW);
                if(m_previewType>0&&key.popupCharacters==null)
                {
                    m_PreviewDrw.set((LatinKey)key,true);
                    m_PreviewDrw.m_bLongPreview = true;
                    key.iconPreview = m_PreviewDrw.getDrawable();
                    m_popup.show(inst, (LatinKey)key, true);
                }
//                longpress = true;
            }
        }
        if(processLongPress((LatinKey)key))
        {
            longpress = false;
            return key.codes!=null&&key.codes[0]==Keyboard.KEYCODE_SHIFT;
        }
// вывод окна popupcharacter v2
        if (key.popupCharacters!=null&&key.popupCharacters.length()>0) {
        	if(m_pk!=null&&m_pk.showPopupKeyboard(key)){
        	//if (popV2_show(getContext(), (String)key.popupCharacters)) {
           		st.fl_popupcharacter2 = true;
                return true;
            }
        }
        if (st.fl_popupcharacter2)
        	return true;
        longpress = false;
        return super.onLongPress(key);
    }
//    public TextView popV2_createTextView(boolean fullView)
//    {
//    	LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
//    		    ViewGroup.LayoutParams.WRAP_CONTENT, 
//    		    ViewGroup.LayoutParams.WRAP_CONTENT
//    		);
//// отступ кнопки в лайоте
//    	param.setMargins(2, 0, 1, 0);
//    	TextView tv = new TextView(getContext());
//        tv.setLayoutParams(param);
//        
//// отступ текста от краёв кнопки
////        tv.setPadding(5, 0, 5, 0);
//        tv.setGravity(Gravity.CENTER);
//        tv.setBackgroundColor(st.ac_col_word_back);
//        tv.setTextColor(st.ac_col_word_text);
//        tv.setMinWidth(50);
////    	tv.setIncludeFontPadding(false);
////        tv.setOnClickListener(m_ClickListenerText);
////        tv.setOnLongClickListener(m_longClickListenerText);
////        if(m_es!=null)
////            m_es.setToEditor(tv);
//        return tv;
//    }
    void setTempShift(boolean bShift,boolean bInvalidate)
    {
        m_state = st.rem(m_state, STATE_CAPS_LOCK);
        if(bShift)
            m_state|=STATE_TEMP_SHIFT;
        else
            m_state = st.rem(m_state, STATE_TEMP_SHIFT);
        checkCapsLock();
    }
    void handleShift()
    {
        Keybrd kbd = getCurKeyboard().kbd;
        if(st.isQwertyKeyboard(kbd))
        {
            String s = st.pref().getString(st.PREF_KEY_SHIFT_STATE, st.STR_ZERO);
            int v = Integer.decode(s);
            //v=3;
            if (v==3) { //normal-shift (double click=CAPSLOCK)
            	if (st.fl_gest_double_click_shift) {
            		st.fl_gest_double_click_shift = false;
                    m_state = st.rem(m_state, STATE_TEMP_SHIFT);
                    m_state|=STATE_CAPS_LOCK;
                	//st.toast("shift CAPS");
            	} else {
        			if (!isUpperCase())
        				setTempShift(true, true);
        			else 
        				setTempShift(false, true);
            	}
        		checkCapsLock();
            	return;
            }
    		st.fl_gest_double_click_shift = false;
            if(v>0)
            {
               	if(isUpperCase())
                {
                    m_state = st.rem(m_state, STATE_CAPS_LOCK);
                    m_state = st.rem(m_state, STATE_TEMP_SHIFT);
                }
                else
                {
                    m_state|=v==1?STATE_TEMP_SHIFT:STATE_CAPS_LOCK;
                }
            }
            else
            {
                if(st.has(m_state, STATE_TEMP_SHIFT))
                {
                    m_state = st.rem(m_state, STATE_TEMP_SHIFT);
                    m_state|=STATE_CAPS_LOCK;
                }
                else if(st.has(m_state, STATE_CAPS_LOCK))
                {
                    m_state = st.rem(m_state, STATE_CAPS_LOCK);
                    m_state = st.rem(m_state, STATE_TEMP_SHIFT);
                }
                else
                {
                    JbKbdView.inst.m_state|=JbKbdView.STATE_TEMP_SHIFT;
                }
            }
            checkCapsLock();
        }
        else
        {
            st.setSymbolKeyboard(st.LANG_SYMBOL_KBD.equals(kbd.lang.name));
        }
    }
    private void checkCapsLock()
    {
        JbKbd kbd = getCurKeyboard();
        if(kbd==null)
            return;
        LatinKey lk = kbd.getKeyByCode(Keyboard.KEYCODE_SHIFT);
        if(lk!=null)
            lk.on = st.has(m_state, STATE_CAPS_LOCK);
        invalidateAllKeys();
    }

    boolean isUpperCase()
    {
        boolean bCaps = st.has(m_state, STATE_CAPS_LOCK);
        boolean bts = st.has(m_state, STATE_TEMP_SHIFT);
        if(bCaps&&bts)
            return false;
        return bCaps||bts;
    }
/** Выставляет настройки клавиатуры из {@link SharedPreferences}*/  
    void setPreferences()
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        if(pref==null)
            return;
        m_state = 0;
//        if(pref.getBoolean(st.PREF_KEY_VIBRO_SHORT_KEY, false))
//            m_state|=STATE_VIBRO_SHORT;

// не использовать! Сделано по другому (10.04.20)        
//        if(pref.getBoolean(st.PREF_KEY_USE_GESTURES, true)) {
//            //m_state|=STATE_GESTURES;
//            m_gd = new KeyboardGesture(this);
//        } else {
//        	m_gd = null;
//        }
        if(pref.getBoolean(st.PREF_KEY_SOUND, false))
            m_state|=STATE_SOUNDS;
//        boolean bp = pref.getBoolean(st.PREF_KEY_PREVIEW, true);
        m_previewType = Integer.decode(pref.getString(st.PREF_KEY_PREVIEW_TYPE, st.STR_ONE));
        if(m_popup!=null)
            m_popup.m_bShowUnderKey = m_previewType==1;
        boolean bPortrait = true;
        boolean bSet = false;
        m_gestures[GestureInfo.LEFT] = st.getGesture(st.PREF_KEY_GESTURE_LEFT, pref);
        m_gestures[GestureInfo.RIGHT] = st.getGesture(st.PREF_KEY_GESTURE_RIGHT, pref);
        m_gestures[GestureInfo.UP] = st.getGesture(st.PREF_KEY_GESTURE_UP, pref);
        m_gestures[GestureInfo.DOWN] = st.getGesture(st.PREF_KEY_GESTURE_DOWN, pref);
        m_gestures[4] = st.getGesture(st.PREF_KEY_GESTURE_SPACE_LEFT, pref);
        m_gestures[5] = st.getGesture(st.PREF_KEY_GESTURE_SPACE_RIGHT, pref);
        m_gestures[6] = st.getGesture(st.PREF_KEY_GESTURE_SPACE_UP, pref);
        m_gestures[7] = st.getGesture(st.PREF_KEY_GESTURE_SPACE_DOWN, pref);
        if(SetKbdActivity.inst!=null)
        {
            if(SetKbdActivity.inst.m_curAction==st.SET_KEY_HEIGHT_PORTRAIT)
            {
                bPortrait = true;
                bSet = true;
            }
            else if(SetKbdActivity.inst.m_curAction==st.SET_KEY_HEIGHT_LANDSCAPE)
            {
                bPortrait = false;
                bSet = true;
            }
        }
        if(!bSet&&st.isLandscape(getContext()))
            bPortrait = false;
        m_KeyHeight = KeyboardPaints.getValue(getContext(), pref, bPortrait?KeyboardPaints.VAL_KEY_HEIGHT_PORTRAIT:KeyboardPaints.VAL_KEY_HEIGHT_LANDSCAPE);
        m_ac_place = KeyboardPaints.getValue(getContext(), pref, bPortrait?KeyboardPaints.VAL_AC_PLACE_HEIGHT_PORTRAIT:KeyboardPaints.VAL_AC_PLACE_HEIGHT_LANDSCAPE);
        m_vertCorr = pref.getInt(st.isLandscape(getContext())?st.PREF_KEY_CORR_LANDSCAPE:st.PREF_KEY_CORR_PORTRAIT, defaultVertCorr);
    }
    int m_vertCorr = defaultVertCorr;
    @Override
    public void onDraw(android.graphics.Canvas canvas) 
    {
        m_bStopInvalidate = false;
        try{
            super.onDraw(canvas);
        }
        catch(Throwable e){}
    };
    @Override
    public void invalidateKey(int keyIndex)
    {
        if(com_menu.inst==null)
            super.invalidateKey(keyIndex);
    }
    public final boolean isDefaultDesign()
    {
        return m_curDesign==null||m_curDesign.m_keyBackground==null;
    }
    boolean m_bStopInvalidate = false;
    public void setLang(String newLang)
    {
        Keybrd k = st.getKeybrdForLangName(newLang);
        if(k==null)
        {
            Toast.makeText(getContext(), "No keyboards for lang "+newLang, Toast.LENGTH_LONG).show();
            return;
        }
        setKeyboard(st.loadKeyboard(k));
        st.saveCurLang();
        if(isUserInput())
        {
        	ServiceJbKbd.inst.getCandidates();
        }
    }
    /** 
     * Смена языка
     * @param canUseMenu Если false - меню выбора языков не выводится
     * @param nextLang 0 - следующий язык, 1 - предыдущий, 2- последние языки 
     */
    public void handleLangChange(boolean canUseMenu,int nextLang)
    {
    	if (ServiceJbKbd.inst!=null)
    		ServiceJbKbd.inst.selmode = false;
        String ls[]=st.getLangsArray(st.c());
        if(canUseMenu&&ls.length>3&&isUserInput())
        {
        	com_menu.showLangs(getContext());
        	return;
        }
        String cl = st.getCurLang();
        if(st.tempEnglishQwerty)
            cl = st.arLangs[0].name;
        st.tempEnglishQwerty = false;
        int ff = st.searchStr(cl, ls);
        String newLang=st.defKbd().lang.name;
        if (st.fl_qwerty_kbd){
            setLang(st.getCurLang());
        	st.fl_qwerty_kbd = false;
        	return;
        }
        switch (nextLang)
        {
        case 0:
	        if(ff==ls.length-1)
	            newLang = ls[0];
	        else if(ff<ls.length-1)
	            newLang = ls[ff+1];
        	break;
        case 1:
	        if(ff==0)
	            newLang = ls[ls.length-1];
	        else if(ff<=ls.length-1)
	            newLang = ls[ff-1];
        	break;
        case 2:
	        if(ff==0)
	            newLang = ls[ls.length-1];
	        else if(ff<ls.length-1)
	            newLang = ls[ff-1];
        	break;
        }
        setLang(newLang);
//        if(nextLang)
//        {
//	        if(f==ls.length-1)
//	            newLang = ls[0];
//	        else if(f<ls.length-1)
//	            newLang = ls[f+1];
//        }
//        else
//        {
//	        if(f==0)
//	            newLang = ls[ls.length-1];
//	        else if(f<ls.length-1)
//	            newLang = ls[f-1];
//        }
    }
    void reload()
    {
    	init();
    	setKeyboard(st.loadKeyboard(st.getCurQwertyKeybrd()));
    }
    void reloadSkin()
    {
        m_designPath = null;
        g_lastLoadedDesign = null;
        init();
        invalidateAllKeys();
    }
    public boolean isUserInput()
    {
        return m_extListener instanceof ServiceJbKbd;
    }
// нжатие в пределах клавиатуры (клавиатура получает фокус)
    char chh = 0;
    @Override
    public boolean onTouchEvent(MotionEvent me)
    {
        try{
// показываем автодополнение при получении фокуса клавиатурой     	
//        	if (st.ac1 != -1) {
//        		ServiceJbKbd.inst.m_acPlace = st.ac1;
//        		ServiceJbKbd.setTypeKbd();
//        		st.ac1 = -1;
//        	}

        	boolean ret = false;
        	
        	if (m_pk!=null&&m_pk.fl_popupcharacter_window){
            	switch (me.getAction())
            	{
            	case MotionEvent.ACTION_UP:
            		chh = m_pk.getTouchUpSymbol(me.getX(),me.getY());
            		if (chh != 0&&ServiceJbKbd.inst!=null){
            			ServiceJbKbd.inst.sendKeyChar(chh);
            			m_pk.close();
            			chh = 0;
            		}
            		//return true;
            	}
        		ret = true;
        	}
        	else if(m_gd!=null)
                ret = m_gd.onTouchEvent(me);
        	lk_this = st.getKeyByPress((int)me.getX(),(int)me.getY());
        	if (lk_this==null)
        		key_iter = -1;
        	else 
        		if (lk_this.codes.length>1) {
            		if (lk_this == lk_prev)
            			key_iter++;
            		else
            			key_iter=-1;
        		} else 
        			key_iter = 0;
            boolean sup = super.onTouchEvent(me);
            if(ret)
            {
                resetPressed();
                invalidateAllKeys();
            }
           	return sup;
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
        return true;
    }
    boolean gestureProcessed = false;
    public void gesture(GestureInfo gest)
    {
        if(!isUserInput())
            return;
        if(m_handler!=null)
        {
            m_handler.removeMessages(OwnKeyboardHandler.MSG_SHOW_PREVIEW);
            m_handler.removeMessages(OwnKeyboardHandler.MSG_MY_REPEAT);
            m_handler.removeMessages(OwnKeyboardHandler.MSG_MY_LONG_PRESS);
            m_handler.sendMessage(m_handler.obtainMessage(OwnKeyboardHandler.MSG_REMOVE_PREVIEW, null));
        }
//        resetPressed();
        gestureProcessed = true;
 //       invalidateAllKeys();
        KbdGesture g = null;
        boolean bg = true;
        if(gest.downKey!=null&&
           gest.downKey.codes[0]==32&&
           (gest.dir==GestureInfo.UP||
           gest.dir==GestureInfo.DOWN
           ))
        {
            g = m_gestures[gest.dir==GestureInfo.UP?6:7];
            bg = false;
        }
        else if(gest.downKey!=null&&
        		gest.downKey.codes[0]==32&&
        		(gest.dir==GestureInfo.LEFT||
        		gest.dir==GestureInfo.RIGHT
        		))
        {
            g = m_gestures[gest.dir==GestureInfo.LEFT?4:5];
            bg = false;
        }
        else if(gest.downKey!=null)
        {
			for (int i=0;i<st.gc.size();i++){
				if(gest.downKey!=null&&
		           gest.downKey.codes[0]!=32&&
		           gest.downKey.codes[0] == st.gc.get(i).keycode&&
		           gest.dir+1 == st.gc.get(i).direction
        		   ){
	                    st.kbdCommand(st.arGestures.get(st.gc.get(i).action).code);
	                    bg = false;
	                    return;
		        }
			}
//			return;
        }
        if (bg)
            g = m_gestures[gest.dir];
//        if(g.code!=0)
//            st.kbdCommand(g.code);
        if(g.code!=0){
        	// жесты - ввод символа в верхнем, нижнем регистрре и ввод текста по долготапу
        	if (gest.downKey!=null){
        		gstr = st.STR_NULL+(char)gest.downKey.codes[0];
                switch (g.code)
                {
                case st.CMD_SYMBOL_UP_CASE:
                    if (gest.downKey.codes[0] > 32) {
                    	ServiceJbKbd.inst.onText(gstr.toUpperCase());
                    	ServiceJbKbd.inst.processCaseAndCandidates();
                    }
                    return;
                case st.CMD_SYMBOL_LOWER_CASE:
                    if (gest.downKey.codes[0] > 32){
                    	ServiceJbKbd.inst.onText(gstr.toLowerCase());
                    	ServiceJbKbd.inst.processCaseAndCandidates();
                    }
                    return;
                case st.CMD_INPUT_LONG_GESTURE:
                	// работу жеста не менять - юзеры уже привыкли и реализуют 
                	// с его помощью третье действие на кнопке
                	ServiceJbKbd.inst.onText(gest.downKey.m_kd.txtSmall);
                	ServiceJbKbd.inst.processCaseAndCandidates();
                	return;
                case st.TXT_ED_SELECT:
                	LatinKey lk = getKeyByCode(st.TXT_ED_SELECT);
                	if (lk!=null){
                		if (lk.on)
                			lk.on=false;
                		else
                			lk.on = true;
                		st.kv().invalidateAllKeys();
                	}
                   	st.kbdCommand(g.code);
                	return;
                }
        	}
           	st.kbdCommand(g.code);
        }
    }
    @Override
    public boolean setShifted(boolean shifted)
    {
        return true;
    }
    public void trueInvalidateKey(int index)
    {
        super.invalidateKey(index);
    }
    LatinKey m_repeatedKey;
    public void onKeyRepeat(LatinKey lk)
    {
//        if(m_previewType>0)
//        {
//            m_popup.show(inst, lk, false);
//        }
    	key_iter = -1;
        m_vibro.runVibro(VibroThread.VIBRO_REPEAT);
//        m_pressed.setPress(lk.codes[0], PressArray.TYPE_REPEAT);
        m_repeatedKey = lk;
        if(m_repeatedKey == null||m_extListener==null)
            return;
        m_extListener.onKey(lk.codes[0], null);
    }
    public static int NOT_A_KEY = 0;
    OnKeyboardActionListener m_actionListener = new OnKeyboardActionListener()
    {
		@Override
        public void onText(CharSequence text)
        {
            m_extListener.onText(text);
        }
        @Override
        public void onRelease(int primaryCode)
        {
//        	if (m_pk.fl_popupcharacter_window){
//        		m_pk.close();
//        		return;
//        	}
            if(m_handler!=null)
            {
                m_handler.removeMessages(OwnKeyboardHandler.MSG_MY_REPEAT);
                m_handler.removeMessages(OwnKeyboardHandler.MSG_MY_LONG_PRESS);
            }
            LatinKey key = getKeyByCode(primaryCode);
            if(key==null)
                return;
            //Log.d("JKB", "release: "+key.getMainText());
            key.processed = false;
//            if(!key.processed)
//                onKey(primaryCode, null);
//            if(m_pressed.getPress(primaryCode)==0)
//            {
//            }
//            if(getKeyIndex(key)>-1)
//                invalidateKey(pos);
//            else
//                invalidateAllKeys();
        	longpress = false;
            m_extListener.onRelease(primaryCode);
        	lk_prev = lk_this;
            lk_this = null;
        }
        
        @Override
        public void onPress(int primaryCode)
        {
        		
            gestureProcessed = false;
            //LatinKey key = getCurKeyboard().getKeyByCode(primaryCode);
            if (lk_this==null)
            	lk_this = getCurKeyboard().getKeyByCode(primaryCode);
            if(lk_this==null)
                return;
            //Log.d("JKB", "press: "+key.getMainText());
            if(m_vibro.hasVibroOnPress())
                m_vibro.runVibro(VibroThread.VIBRO_SHORT);
            if(m_handler!=null)
            {
                if(lk_this.trueRepeat)
                {
                    m_repeatedKey = lk_this;
                    m_handler.sendRepeat(lk_this,true);
                }
                else if(lk_this.hasLongPress()){
//                    m_handler.removeMessages(OwnKeyboardHandler.MSG_MY_LONG_PRESS);

                    m_handler.sendLongPress(lk_this);
                }
            }
            if(m_previewType>0
            		&&lk_this.codes!=null
            		&&lk_this.codes.length<2
            		&&!st.fl_popupcharacter2)
            {
               m_PreviewDrw.set(lk_this,true);
               m_PreviewDrw.m_bLongPreview = false;
               lk_this.iconPreview = m_PreviewDrw.getDrawable();
               m_popup.show(inst, lk_this, false);
            }
            m_extListener.onPress(primaryCode);
        }
        @Override
        public void onKey(int primaryCode, int[] keyCodes)
        {
//            if(m_repeatedKey!=null&&m_repeatedKey.codes[0]==primaryCode||m_pressedCode!=primaryCode&&m_pressedCode2!=primaryCode)
//                return;
//            if(m_pressed.getPress(primaryCode)>0)
//                return;
            if(gestureProcessed||com_menu.inst!=null)
                return;
            LatinKey k = getKeyByCode(primaryCode);
            if(k!=null&&k.processed)
                return;
            if(m_previewType>0&&k!=null&&k.codes!=null&&k.codes.length>1&&!st.fl_popupcharacter2)
            {
               m_PreviewDrw.set(k,true,primaryCode);
               m_PreviewDrw.m_bLongPreview = false;
               k.iconPreview = m_PreviewDrw.getDrawable();
               m_popup.show(inst, k, false);
            }
            
            if(!m_vibro.hasVibroOnPress())
                m_vibro.runVibro(VibroThread.VIBRO_SHORT);
//           if(k.runSpecialInstructions(false))
//            	return;
            thiskey_code = primaryCode;
            if (longpress
            	&&k!=null
            	&&!k.hasLongPress()
            	&&primaryCode == Keyboard.KEYCODE_DELETE
            	)
            {
            } else {
            	if (!st.fl_popupcharacter2)
            		m_extListener.onKey(primaryCode,keyCodes);
            }
            lastkey_code = primaryCode;
        }
        @Override
        public void swipeUp()
        {}
        public void swipeRight()
        {}
        public void swipeLeft()
        {}
        public void swipeDown()
        {}
    };
    final boolean hasKey(Key k)
    {
        JbKbd kbd = getCurKeyboard();
        if(kbd==null)
            return false;
        return kbd.hasKey(k);
    }
    final int getKeyIndex(Key k)
    {
        JbKbd kbd = getCurKeyboard();
        if(kbd==null)
            return -1;
        return kbd.getKeyIndex(k);
    }
    final LatinKey getKeyByCode(int code)
    {
        JbKbd kbd = getCurKeyboard();
        if(kbd==null)
            return null;
        return kbd.getKeyByCode(code);
    }
    
    OnKeyboardActionListener m_extListener;
    @Override
    public void setOnKeyboardActionListener(OnKeyboardActionListener listener) 
    {
        m_extListener = listener;
        if(isUserInput())
        	inst = this;
        else if(inst==this&&listener==null)
        	inst = null;
        super.setOnKeyboardActionListener(m_actionListener);
    };
    @Override
    public void setKeyboard(Keyboard keyboard)
    {
        try{
// Восстанавливаем капслок для qwerty или сбрасываем для не-qwerty
            JbKbd kbd = (JbKbd)keyboard;
            boolean qwerty = st.isQwertyKeyboard(kbd.kbd);
            Key k = kbd.getKeyByCode(Keyboard.KEYCODE_SHIFT);
            if(k==null||!k.sticky)
            {
                qwerty = false;
            }
            if(qwerty)
            {
                k.on = st.has(m_state, STATE_CAPS_LOCK);
            }
            else
            {
                m_state = st.rem(m_state, STATE_CAPS_LOCK);
                m_state = st.rem(m_state, STATE_TEMP_SHIFT);
            }
        }
        catch (Throwable e) {
        }
        resetPressed();
		processLongKey = false;
        super.setKeyboard(keyboard);
        if(isUserInput())
        {
            ServiceJbKbd.inst.onKeyboardChanged();
        }
		if (ServiceJbKbd.inst!=null) {
			ServiceJbKbd.inst.setImeOptions();
		}
    }
    boolean resetPressed()
    {
        boolean ret = false;
        JbKbd kbd = getCurKeyboard();
        if(kbd!=null)
            ret = kbd.resetPressed();
        return ret;
    }
    @Override
    public void closing()
    {
// прячет список слов из автодополнения, если он на экране
    	if (st.fl_ac_list_view&&ServiceJbKbd.inst!=null) {
    		ServiceJbKbd.inst.m_candView.popupViewFullList();
    		return;
    	}

//        m_popup.close();

    	//        resetPressed();
//        invalidateAllKeys();
        super.closing();
    }
    @Override
    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
    }
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus)
    {
        if(isUserInput())
        {
            ServiceJbKbd.inst.onKeyboardWindowFocus(hasWindowFocus);
        }
        super.onWindowFocusChanged(hasWindowFocus);
    }
    void setTopSpace(int top)
    {
        setPadding(0, top, 0, 0);
    }
    public void setVerticalCorrection(int corr)
    {
        try{
            g_vertCorrectionField.setInt(this, corr);
        }
        catch (Throwable e) {
        }
    }
    /** ставит на Button или TextView, оформление текущего скина
     * @param view - TextView или Button
     * @param bspec - как оформлять, как спецклавиша, или обычная */
    public View getCurrentDesign(View view, boolean bspec)
    {
    	if (inst == null)
    		return view;
    	TextView tv = null;
    	try {
        	tv = (TextView)view;
			
		} catch (Throwable e) {
			return view;
		}
    	KbdDesign des = null;
    	if (!bspec)
    		des = inst.m_curDesign;
    	else
    		des = inst.m_curDesign.m_kbdFuncKeys;
    	if (des == null)
    		des = inst.m_curDesign;
    		
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(inst.m_LabelTextSize);
        tv.setHeight(m_KeyHeight);
        tv.setTextColor(des.textColor);
        if (des.m_keyBackground!=null)
        	tv.setBackground(des.m_keyBackground.getDrawable());
        else
        	tv.setBackground(inst.m_defDrawable);
    	
		if (view instanceof Button) {
			view = (Button) tv;
		} 
		else if (view instanceof TextView) {
			view = (TextView) tv;
		} 

    	return view;
    }
}
