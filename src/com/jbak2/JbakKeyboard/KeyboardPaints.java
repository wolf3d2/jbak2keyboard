package com.jbak2.JbakKeyboard;

import java.util.Vector;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.DisplayMetrics;

import com.jbak2.CustomGraphics.CustomButtonDrawable;
import com.jbak2.JbakKeyboard.EditSetFontActivity.EditSet;
import com.jbak2.JbakKeyboard.IKeyboard.KbdDesign;
import com.jbak2.ctrl.Font;

public class KeyboardPaints
{
    public static final int VAL_AC_PLACE_HEIGHT_PORTRAIT =1;
    public static final int VAL_AC_PLACE_HEIGHT_LANDSCAPE =2;
    public static final int VAL_KEY_HEIGHT_PORTRAIT =1;
    public static final int VAL_KEY_HEIGHT_LANDSCAPE =2;
    public static final int VAL_TEXT_SIZE_MAIN =3;
    public static final int VAL_TEXT_SIZE_SYMBOL =4;
    public static final int VAL_TEXT_SIZE_LABEL =5;
    public static KeyboardPaints inst;
/** Шрифт клавиатуры для основных символов */    
    public Paint main;
/** Шрифт клавиатуры для дополнительных символов */    
    public Paint second;
/** Шрифт клавиатуры для меток */    
    public Paint label;
    public Paint halfLabel;
    Paint mainBitmapPaints[]=new Paint[]{null,null,null,null};
    Paint funcBitmapPaints[]=new Paint[]{null,null,null,null};
/** Цвета текстов. 0 - основной текст, 1 - текст доп символов, 2 - основной текст в нажатом состоянии, 3 - доп. символы в нажатом состоянии*/    
    int mainColors[]=new int[]{Color.WHITE,st.DEF_COLOR,st.DEF_COLOR,st.DEF_COLOR};
/** Цвета текстов. 0 - основной текст, 1 - текст доп символов, 2 - основной текст в нажатом состоянии, 3 - доп. символы в нажатом состоянии*/    
    int funcColors[]=new int[]{st.DEF_COLOR,st.DEF_COLOR,st.DEF_COLOR,st.DEF_COLOR};
/** Основной дизайн - цвет основного текста */
    public int mainColor = Color.WHITE;
/** Основной дизайн - цвет основного текста в нажатом состоянии*/
    public int mainColorPressed = Color.WHITE;
/** Основной дизайн - цвет текста символов*/
    public int mainSecondColor = Color.WHITE;
/** Основной дизайн - цвет текста символов в нажатом состоянии*/
    public int mainSecondColorPressed = Color.WHITE;
    
    public PorterDuffColorFilter bmpColorFilter = null;
    boolean m_bMainBold = true;
    Vector <BitmapCache> m_arBitmaps = new Vector<BitmapCache>();
//    int BitmapCacheSize = 120;
    int BitmapCacheSize = 120;
    StateListDrawable funcBackDrawable;
    Paint bitmapPreview;
    Paint bitmapNoColor;
    Rect padding = new Rect();
    public KeyboardPaints()
    {
        inst = this;
    }
    final int clr(int c,int def)
    {
        return c==st.DEF_COLOR?def:c;
    }
    void setDefault(KbdDesign design,int defColor)
    {
        mainColor = design.textColor==st.DEF_COLOR?defColor:design.textColor;
        mainBitmapPaints =  new Paint[]{null,null,null,null};
        funcBitmapPaints =  new Paint[]{null,null,null,null};
        mainColors=new int[]{Color.WHITE,st.DEF_COLOR,st.DEF_COLOR,st.DEF_COLOR};
        funcColors=new int[]{st.DEF_COLOR,st.DEF_COLOR,st.DEF_COLOR,st.DEF_COLOR};
        
        mainColors[0] = mainColor;
        mainColors[1] = clr(design.secondColor,mainColor);
        mainColors[2] = clr(design.textColorPressed,mainColor);
        mainColors[3] = clr(design.secondColorPressed,mainColors[1]);
        
        if(design.m_kbdFuncKeys!=null)
        {
            funcColors[0] = clr(design.m_kbdFuncKeys.textColor,mainColor);
            funcColors[1] = clr(design.m_kbdFuncKeys.secondColor,funcColors[0]);
            funcColors[2] = clr(design.m_kbdFuncKeys.textColorPressed,funcColors[0]);
            funcColors[3] = clr(design.m_kbdFuncKeys.secondColorPressed,funcColors[1]);
        }
        else
            funcColors = mainColors;
        m_bMainBold = st.has(design.flags,st.DF_BOLD);
//        if(design.m_keyBackground!=null)
//        {
//            previewBack = design.m_keyBackground.clone().getStateDrawable();
//        }
//        else
//        {
//            previewBack = st.kv().m_drwKeyBack;
//        }
        if(design.m_kbdFuncKeys!=null&&design.m_kbdFuncKeys.m_keyBackground!=null)
        {
            funcBackDrawable = design.m_kbdFuncKeys.m_keyBackground.getStateDrawable();
            if(st.kv().m_KeyBackDrw instanceof CustomButtonDrawable)
            {
//                design.m_keyBackground.setDependentback(design.m_kbdFuncKeys.m_keyBackground);
                ((CustomButtonDrawable)st.kv().m_KeyBackDrw).setDependentDrawable(funcBackDrawable);
            }
        }
        else
        {
            funcBackDrawable = null;
        }
        bitmapNoColor = new Paint();
        bitmapPreview = new Paint();
// рисуем цвет для картинки в показе нажатых клавиш 
//        bitmapPreview.setColorFilter(new PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP));
        bitmapPreview.setColorFilter(new PorterDuffColorFilter(ServiceJbKbd.inst.m_popup_color_text, PorterDuff.Mode.SRC_ATOP));
        st.kv().m_KeyBackDrw.getPadding(padding);
        padding.offset(2,2);
    }
    final int getIndex(KeyDrw d,boolean second)
    {
        int index = second?1:0;
        if(d.m_bPressed)
            index+=2;
        return index;
    }
    public final int getColor(KeyDrw d,boolean second)
    {
        int index = getIndex(d, second);
        return d.m_bFunc?funcColors[index]:mainColors[index];
    }
    public final Paint getBitmapPaint(KeyDrw d,boolean second)
    {
        if(d.m_bNoColorIcon)
            return bitmapNoColor;
        if(!d.m_bFunc)
        {
            int dd = 10;
            int d1 = dd;
        }
        if(d.m_bPreview)
            return bitmapPreview;
        int index = getIndex(d,second);
        Paint paints[]=d.m_bFunc?funcBitmapPaints:mainBitmapPaints;
        Paint pt = paints[index];
        if(pt!=null)
            return pt;
        if(pt==null)
        {
            pt = new Paint();
            int color = getColor(d, second);
            pt.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP));
            paints[index]=pt;
        }
        return pt;
    }
    public final Paint getBitmapPaint(KeyDrw d)
    {
        return getBitmapPaint(d,false);
    }
    final EditSet getDefaultMain()
    {
        EditSet es = new EditSet();
        es.fontSize = getValue(st.c(), null, VAL_TEXT_SIZE_MAIN);
        es.style = 0;
        if(m_bMainBold)
            es.style|=Typeface.BOLD;
        return es;
    }
    final EditSet getDefaultSecond()
    {
        EditSet es = new EditSet();
     // создаем second        
        es.fontSize = getValue(st.c(), null, VAL_TEXT_SIZE_SYMBOL);
         return es;
    }
    final EditSet getDefaultLabel()
    {
        EditSet es = new EditSet();
        es.fontSize = getValue(st.c(), null, VAL_TEXT_SIZE_LABEL);
         es.style=Typeface.BOLD;
         return es;
    }
    final void createFontFromSettings()
    {
        EditSet es = new EditSet();
        if(!es.load(st.PREF_KEY_MAIN_FONT)||es.isDefault())
            main = getDefaultMain().getTextPaint();
        else
            main = es.getTextPaint();
        if(!es.load(st.PREF_KEY_SECOND_FONT)||es.isDefault())
            second = getDefaultSecond().getTextPaint();
        else
        {
            second = es.getTextPaint(true);
            
        }
        if(!es.load(st.PREF_KEY_LABEL_FONT)||es.isDefault())
            label = getDefaultLabel().getTextPaint();
        else
            label = es.getTextPaint();
        halfLabel = new Paint(label);

        halfLabel.setTextSize(halfLabel.getTextSize()/2);
    }
    void addBitmap(BitmapCache c)
    {
        if(m_arBitmaps.size()==BitmapCacheSize)
            m_arBitmaps.remove(0);
        m_arBitmaps.add(c);
    }
    BitmapDrawable getBitmap(String path)
    {
        for(BitmapCache bc:m_arBitmaps)
        {
            if(path.equals(bc.path))
                return bc.bd;
        }
        try{
            BitmapDrawable bd = (BitmapDrawable)BitmapDrawable.createFromPath(path);
            if(bd!=null)
                addBitmap(new BitmapCache(path, bd));
            return bd;
        }
        catch (Throwable e) {
        }
        return null;
    }
    BitmapDrawable getBitmap(int id)
    {
        for(BitmapCache bc:m_arBitmaps)
        {
            if(bc.resId==id)
                return bc.bd;
        }
        BitmapDrawable bd =(BitmapDrawable)st.c().getResources().getDrawable(id);
        addBitmap(new BitmapCache(id, bd));
        return bd;
    }
    public static class BitmapCache
    {
        public BitmapCache(String path,BitmapDrawable b)
        {
            this.path = path;
            bd = b;
        }
        public BitmapCache(int id,BitmapDrawable b)
        {
            resId = id;
            bd = b;
        }
        int resId = 0;
        String path;
        BitmapDrawable bd;
    }
    public static final int getScreen(Context c,boolean bPortrait)
    {
        DisplayMetrics dm = c.getResources().getDisplayMetrics(); 
        if(bPortrait)
            return st.max(dm.widthPixels,dm.heightPixels);
        return st.min(dm.widthPixels,dm.heightPixels);
    }
/** Перевод процентов от размера экрана в пиксели
*@param c Контекст 
*@param bPortrait - true - для расчёта используется большее значение ширины/высоты, false - меньшее 
*@param val Значение для перевода
*@return
 */
    public static final float getPixelToPerc(Context c,boolean bPortrait,float val)
    {
        float sh = getScreen(c, bPortrait);
        return val/sh;
    }
    /** Преобразует значение в процентах от высоты экрана в пиксели
    *@param c Контекст
    *@param val Значение а процентах от высоты экрана
    *@param bPortrait - true - для расчёта используется большее значение ширины/высоты, false - меньшее 
    *@param bEven true - вернуть четное значение
    *@return Размер в пикселях
     */
        public static int getPercToPixel(Context c,boolean bPortrait,float val,boolean bEven)
        {
            float sh = getScreen(c, bPortrait);
            float ret = val*sh;
            if(!bEven)
                return (int)ret;
            int r = (int)ret;
            if(r%2>0)
                return r+1;
            return r;
        }
    public static float getDefValue(int type)
    {
        switch(type)
        {
            case VAL_KEY_HEIGHT_PORTRAIT:
                return (float) 0.1;
            case VAL_KEY_HEIGHT_LANDSCAPE:
                return (float) 0.12;
            case VAL_TEXT_SIZE_MAIN:
                return (float) 0.042;
            case VAL_TEXT_SIZE_SYMBOL:
                return (float) 0.025;
            case VAL_TEXT_SIZE_LABEL:
                return (float) 0.03;
        }
        return 0;
    }
    public static int getValue(Context c,SharedPreferences p,int type)
    {
        switch(type)
        {
            case VAL_KEY_HEIGHT_PORTRAIT:
                return getPercToPixel(c, true,p.getFloat(st.PREF_KEY_HEIGHT_PORTRAIT_PERC, getDefValue(type)),true);
            case VAL_KEY_HEIGHT_LANDSCAPE:
                return getPercToPixel(c, false,p.getFloat(st.PREF_KEY_HEIGHT_LANDSCAPE_PERC, getDefValue(type)),true);
            case VAL_TEXT_SIZE_MAIN:
            case VAL_TEXT_SIZE_SYMBOL:
            case VAL_TEXT_SIZE_LABEL:
                return getPercToPixel(c, true,getDefValue(type),false);
        }
        return 0;
    }
}
