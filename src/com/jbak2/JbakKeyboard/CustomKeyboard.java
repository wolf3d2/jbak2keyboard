package com.jbak2.JbakKeyboard;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.xmlpull.v1.XmlPullParser;

import com.jbak2.CustomGraphics.draw;
import com.jbak2.JbakKeyboard.IKeyboard.Keybrd;
import com.jbak2.JbakKeyboard.IKeyboard.Lang;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.util.DisplayMetrics;
import android.util.Xml;

public class CustomKeyboard extends JbKbd
{
// переменная указывающая нужно ли записывать при компиляции значение %p, px
// перед вызовом getsize() нужно устанавливать её значение
	// 0 - не писать
	// 1 - %p
	// 2 - px
	public static int compil_metric = 0;
    public static final String KEYBOARD_FOLDER = "keyboards";
    public static final String A_ANDROID = "android:";
    public static final String TYPE_LAYOUT_CALC = "calculator";
    public static final String TYPE_LAYOUT_SCROLL = "scroll";
    public static final String TAG_KEYBOARD = "Keyboard";
    public static final String VAL_PERCENT = "%p";
    public static final String VAL_PIXELS = "px";
    public static final String TAG_ROW = "Row";
    public static final String TAG_KEY = "Key";
    public static final String TAG_REPLACE = "Replace";
    public static final String DRW_PREFIX = "@drawable/sym_keyboard_";
    
    public static final String A_keyWidth = "keyWidth";
    public static final String A_keyHeight = "keyHeight";
    public static final String A_verticalGap = "verticalGap";
    public static final String A_horizontalGap = "horizontalGap";
    public static final String A_typeKeyboard = "typeKeyboard";

    // теги для скроллящейся раскладки
    public static final String TAG_KEYS = "Keys";
    public static final String A_arrayKeys = "arrayKeys";
    public static final String A_arrayAnchors = "arrayAnchors";

    public static final String A_codes="codes";
    // реализация комбинаций клавиш по нажатию одной клавиши
    public static final String A_comboKeyCodes="comboKeyCodes";     
    public static final String A_longComboKeyCode="longComboKeyCode";     
    public static final String A_iconPreview="iconPreview"; 
    public static final String A_isRepeatable="isRepeatable";   
    public static final String A_isSticky="isSticky";       
    public static final String A_keyIcon="keyIcon";     
    public static final String A_keyLabel="keyLabel";       
    public static final String A_keyOutputText="keyOutputText"; 
    public static final String A_longKeyOutputText="longKeyOutputText"; 
    public static final String A_popupCharacters="popupCharacters"; 
// popupCharacter по короткому нажатию
    public static final String A_shortPopupCharacters="shortPopupCharacters";
// команда выводящая активность с помощью
    public static final String A_help="help";
// номер регистра (для удобства) калькулятора (от 0 и выше) (calcReg="14")
// какой клавише назначен регистр, нужно указывать в окне help
    public static final String A_calcReg="calcReg";
// ключ указывающий что это отдельная раскладка калькулятора
//    public static final String A_calcKeyboard="calcKeyboard";
// строка какие пункты меню калькулятора выводить (через пробел)
    public static final String A_calcMenu="calcMenu";
/** Текстовая метка, рисующаяся мелким шрифтом по центру клавиши с разбиением на строки */    
    public static final String A_smallLabel="smallLabel";
/** Код, срабатывающий по удержанию клавиши */    
    public static final String A_upCode="longCode";   
/** Аттрибут, bool. Если true - на заднем плане клавиши рисуется фон 2, иначе - 1 */    
    public static final String A_specKey="specKey";   
/** Аттрибут, bool. Если true - иконка не окрашивается в цвет текста скина */    
    public static final String A_noColor="noColor";   
/** Аттрибут, bool. Если true - по нажатию клавиши происходит переход к qwerty-клавиатуре, false - переход не происходит*/
    public static final String A_goQwerty="goQwerty";   
    public static final String A_From="from";   
    public static final String A_To="to";   
    public static final String A_Template="template";   
    public static final String A_TemplateLong="longTemplate";   
    public static final String A_Keyboard="keyboard";   
    public static final String A_KeyboardLong="longKeyboard";   

    public static final  byte B_keyWidth   = 1;
    public static final  byte B_keyHeight  = 2;
    public static final  byte B_verticalGap  = 3;
    public static final  byte B_horizontalGap = 4;
    public static final  byte B_codes=5;     
    public static final  byte B_iconPreview=6; 
    public static final  byte B_isRepeatable=8;   
    public static final  byte B_isSticky=9;       
    public static final  byte B_keyIcon=11;     
    public static final  byte B_keyLabel=12;       
    public static final  byte B_keyOutputText=13; 
    public static final  byte B_popupCharacters=14; 
    public static final  byte B_upCode=16;   
    public static final  byte B_specKey=17;
    public static final  byte B_smallLabel=18;
    public static final  byte B_noColor=19;
    public static final  byte B_longKeyOutputText=20;
    public static final  byte B_goQwerty=21;
    public static final  byte B_from=22;
    public static final  byte B_to=23;
    public static final  byte B_keyboard=24;
    public static final  byte B_keyboardLong=25;
    public static final  byte B_template=26;
    public static final  byte B_templateLong=27;
    public static final  byte B_help=28;
    public static final  byte B_calcReg=29;
    public static final  byte B_calcMenu=30;
    public static final  byte B_shortPopupCharacters=31;
    public static final  byte B_typeKeyboard=32;
    public static final  byte B_comboKeyCodes=33;
    public static final  byte B_longComboKeyCode=34;

    // Для скроллящейся раскладки
    public static final byte B_arrayKeys = 35;
    public static final byte B_arrayAnchors = 36;
    // ---
    public static final  byte BA_KBD=(byte)'|';
    public static final  byte BA_ROW=58;//(byte)':'
    public static final  byte BA_KEY=(byte)'k';
    public static final  byte BA_KEYS=(byte)'s';

    static CustomKbdScroll kbdscroll = null;
    String old_kbd = st.STR_NULL;
// номер парсируемой клавиши
    int number_key = 0;
    int m_displayWidth;
    int m_displayHeight;
    int m_x = 0;
    int m_y = 0;
    int m_rowHeight;
    float m_fraction = 0f;
    float m_globalFraction = 0f;
    List<Key> m_keys;
    Row m_row = null;
    Context m_context;
    boolean m_bBrokenLoad = false;
    DisplayMetrics m_dm;
    Field m_totalHeight;
    static DataOutputStream m_os = null;
    
    public CustomKeyboard(Context context,Keybrd kbd)
    {
        super(context, kbd);
        try{
            String totalHeight = "mTotalHeight";
            m_totalHeight = Keyboard.class.getDeclaredField(totalHeight);
            m_totalHeight.setAccessible(true);
        }
        catch (Throwable e) {
            m_totalHeight = null;
        }
        m_context = context;
        m_dm = context.getResources().getDisplayMetrics();
        m_displayWidth = m_dm.widthPixels;
        m_displayHeight = m_dm.heightPixels;
        m_keys = getKeys();

        try{
            
            if(kbd.kbdCode==st.KBD_COMPILED)
            {
                AssetManager am = context.getResources().getAssets();
                makeCompiledKeyboard(new DataInputStream(new BufferedInputStream(am.open(kbd.path))), kbd);
                
            }
            else
            {
                XmlPullParser xp = Xml.newPullParser();
                FileInputStream fs = new FileInputStream(kbd.path);
                xp.setInput(new BufferedInputStream(fs), null);
                makeKeyboard(xp, kbd);
            }
        }
        catch (Throwable e) 
        {
            m_bBrokenLoad = true;
        }
    }
    void makeCompiledKeyboard(DataInputStream is, Keybrd kbd)
    {
        try{
            byte b = is.readByte();
            boolean bConvert = m_os!=null;
            if(bConvert)
            {
                m_os.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n".getBytes());
            }
            b = bConvert?parseKeyboardWithConvert(is):parseKeyboard(is);
            do
            {
                switch (b)
                {
                    case BA_ROW:
                        if(m_row!=null){
                            m_y+=m_row.defaultHeight+m_row.verticalGap;
                        }
                        if(bConvert)
                        {
                            if(m_row!=null)
                                m_os.write(" </Row>\n".getBytes());
                            m_os.write(" <Row>\n".getBytes());
                            m_row = new Row(this);

                            b = is.readByte();
                        }
                        else
                        {
                            parseRow(null);
                            if (st.type_keyboard.contains(TYPE_LAYOUT_SCROLL)) {
                                b = is.readByte();
                            	continue;
                            }
                            float ff = 0;
                            String dim = st.STR_NULL;
                            byte bb = -1;
                            do
                            {
                            	dim = st.STR_NULL;
                            	bb = -1;
                                b = is.readByte();
                                if(b == B_keyHeight)
                                {
                                    ff = is.readFloat();
                                    bb = is.readByte();
                                    if (bb>-1&&bb==1)
                                    	dim = "%p";
                                    else if (bb>-1&&bb==2)
                                    	dim = "px";
                                    m_row.defaultHeight = getSize(String.valueOf(ff)+dim, m_row.defaultHeight, m_row.defaultHeight, B_keyHeight);
                                }
                                else if(b == B_keyWidth)
                                {
                                    ff = is.readFloat();
                                    bb = is.readByte();
                                    if (bb>-1&&bb==1)
                                    	dim = "%p";
                                    else if (bb>-1&&bb==2)
                                    	dim = "px";
                                    m_row.defaultWidth = getSize(String.valueOf(ff)+dim, m_row.defaultWidth, m_row.defaultWidth, B_keyWidth);
                                }
                                else if(b == B_verticalGap)
                                {
                                    ff = is.readFloat();
                                    bb = is.readByte();
                                    if (bb>-1&&bb==1)
                                    	dim = "%p";
                                    else if (bb>-1&&bb==2)
                                    	dim = "px";
                                    m_row.verticalGap = getSize(String.valueOf(ff)+dim, m_displayHeight, m_row.verticalGap, B_verticalGap);
                                }
                            }
                            while (b!=BA_KEY&&b!=BA_KEYS&&b!=BA_ROW&&b!=BA_KBD);
                        }   
                    break;
                    case BA_KEY:
                        b = bConvert?parseKeyWithConvert(is):parseKey(is);
                        break;
                    case BA_KEYS:
                    	if (kbdscroll == null)
                            kbdscroll = new CustomKbdScroll(this);
                        b = bConvert?parseKeysWithConvert(is):kbdscroll.loadKeyboard(is, b);
                        b = is.readByte();

                    	break;
                    default:
                        b = is.readByte();
                    break;
                }
            }
            while(b!=BA_KBD);
            if(bConvert)
            {
                m_os.write(" </Row>\n".getBytes());
                m_os.write("</Keyboard>\n".getBytes());

            }
            m_y+=m_row.defaultHeight+m_row.verticalGap;
        }
        catch (Throwable e) {
            m_bBrokenLoad = true;
        }
        postProcessKeyboard();
    }
    void makeKeyboard(XmlPullParser parser, Keybrd kbd )
    {
        try
        {
            int eventType = parser.getEventType();
            boolean done = false;
            List <Key> keys = getKeys();
            if(m_os!=null)
                m_os.writeByte(BA_KBD);
            while (eventType != XmlPullParser.END_DOCUMENT && !done)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if(name.equals(TAG_KEYBOARD)){
                            parseKeyboard(parser);
                        }
                        else if(name.equals(TAG_ROW)){
                            parseRow(parser);
                        }
                        else if(name.equals(TAG_KEY)){
                            parseKey(parser, keys);
                        }
                        else if(name.equals(TAG_KEYS)){
                        	if (kbdscroll == null)
                                kbdscroll = new CustomKbdScroll(this);
                        	kbdscroll.loadKeyboard(parser);
                        	//kbdscroll.inst = null;
                        }
                        else if(name.equals(TAG_REPLACE)){
                            parseReplace(parser);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if(name.equals(TAG_KEYBOARD))
                        {
                            if(m_os!=null)
                                m_os.writeByte(BA_KBD);
                        }
                        if(name.equals(TAG_KEYS))
                        {
                            if(m_os!=null) {
                                m_os.writeByte(BA_KEYS);
//                                m_os.writeByte(BA_ROW);
                            }
                        }
                        if(name.equals(TAG_ROW))
                        {
                            m_y+=m_row.defaultHeight+m_row.verticalGap;
                        }
                        break;
                }
                eventType = parser.next();
            }
        }
        catch (Throwable e) 
        {
        }
        try
        {
            postProcessKeyboard();
            if(m_os!=null)
            {
                m_os.flush();
                m_os.close();
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        
    }
    void postProcessKeyboard()
    {
        try{
            String totalWidth = "mTotalWidth";
            String modKeys = "mModifierKeys";
            Field f;
            f = Keyboard.class.getDeclaredField(totalWidth);
            f.setAccessible(true);
            f.set(this, m_displayWidth);
//            if (st.type_keyboard.contains(TYPE_LAYOUT_SCROLL)){
//            	m_y = 300;
//            }
            m_totalHeight.set(this, m_y-getVerticalGap());
            f = Keyboard.class.getDeclaredField(modKeys);
            f.setAccessible(true);
            if (st.type_keyboard.contains(TYPE_LAYOUT_CALC)){
            	if (st.calc_fl_ind==false) {
            		ServiceJbKbd.inst.m_candView.setCalcInd(-10,0);
//            		st.calc_fl_ind = true;
            	}
            }
            if (old_kbd.contains(TYPE_LAYOUT_CALC)){
        		ServiceJbKbd.inst.m_candView.restoreAc_place();
        		st.calc_fl_ind = false;
            }
            	
            old_kbd = st.type_keyboard;
            
        }
        catch(Throwable e)
        {
        }
    }
    final byte parseKeyboardWithConvert(DataInputStream is)throws IOException
    {
    	number_key=0;
        String out = "<"+TAG_KEYBOARD+" xmlns:android=\"http://schemas.android.com/apk/res/android\" ";
        byte b = 0;
        do
        {
            b = is.readByte();
            if(b==BA_ROW)
                break;
            switch (b)
            {
            case B_keyWidth:
                out+=A_ANDROID+A_keyWidth+"=\""+is.readFloat()+"%p\" ";
                break;
            case B_typeKeyboard:
                out+=A_ANDROID+A_typeKeyboard+"=\""+is.readUTF()+"\" ";
                break;
                default:
                    is.readFloat();
                break;
            }
        }
        while (b<BA_ROW);
        out+=">\n";
        m_os.write(out.getBytes());
        return b;
    }
// вызод встроенной клавиатуры    
    final byte parseKeyboard(DataInputStream is)throws IOException
    {
    	old_kbd = st.type_keyboard;
    	st.type_keyboard=st.STR_NULL;
        byte b = 0;
        do
        {
            b = is.readByte();
            if(b==BA_ROW)
                break;
            switch (b)
            {
                case B_keyWidth:
                	float w = getPercentSizeFloat(is);
                    setKeyWidth(floatToInt(w));
                    m_globalFraction = floatToIntFraction(w);
                    break;
                case B_typeKeyboard:
                	st.type_keyboard = is.readUTF();
                    break;
                default:
                    is.readFloat();
                break;
            }
        }
        while (b<BA_ROW);
// закоментил 26.10.18        
//        if (st.type_keyboard.contains(TYPE_LAYOUT_CALC)){
//        	ServiceJbKbd.inst.m_candView.restoreAc_place();
//        }
        return b;
    }
    final boolean parseKeyboard(XmlPullParser p) throws IOException
    {
    	old_kbd = st.type_keyboard;
    	st.type_keyboard=st.STR_NULL;
        int cnt = p.getAttributeCount();
        for(int i=0;i<cnt;i++)
        {
            String name = attName(p, i);
            
            if(name.equals(A_keyWidth))
            {
                float sz = getSizeFloat(p.getAttributeValue(i),m_displayWidth,m_displayWidth/10,B_keyWidth);
                setKeyWidth(floatToInt(sz));
                m_globalFraction = floatToIntFraction(sz);
            }
            else if(name.equals(A_keyHeight))
                setKeyWidth(getSize(p.getAttributeValue(i),m_displayHeight,50,B_keyHeight));
            else if(name.equals(A_verticalGap))
                setVerticalGap(getSize(p.getAttributeValue(i),m_displayHeight,0,B_verticalGap));
            else if(name.equals(A_horizontalGap))
                setHorizontalGap(getSize(p.getAttributeValue(i),m_displayWidth,0,B_horizontalGap));
            else if(name.equals(A_typeKeyboard)){
                st.type_keyboard =p.getAttributeValue(i);
                if (st.type_keyboard.contains(TYPE_LAYOUT_CALC)){
                    if(m_os!=null)
                    {
                        m_os.writeByte(B_typeKeyboard);
                        m_os.writeUTF(TYPE_LAYOUT_CALC);
                    }
                }
                if (st.type_keyboard.contains(TYPE_LAYOUT_SCROLL)){
                    if(m_os!=null)
                    {
                        m_os.writeByte(B_typeKeyboard);
                        m_os.writeUTF(TYPE_LAYOUT_SCROLL);
                    }
                }
            }
        }
        return true;
    }
    final boolean parseRow(XmlPullParser p) throws IOException
    {
        m_x = 0;
        m_fraction = 0f;
        if(m_os!=null)
        {
            m_os.writeByte(BA_ROW);
        }
        m_row = new Row(this);
        m_row.defaultWidth = getKeyWidth();
        m_row.defaultHorizontalGap = 0;
        m_row.verticalGap = 0;
        m_row.defaultHeight = getKeyHeight();
        if(p!=null)
        {
            for(int i=p.getAttributeCount()-1;i>=0;i--)
            {
                String name = p.getAttributeName(i);
                String metric = p.getAttributeValue(i);
                compil_metric = 0;
                if (metric.endsWith("%p"))
                	compil_metric = 1;
                else if (metric.endsWith("px"))
                	compil_metric = 2;
                
                if(A_keyHeight.equals(name))
                {
                	m_row.defaultHeight = getSize(p.getAttributeValue(i), m_row.defaultHeight, m_row.defaultHeight, B_keyHeight);
                }
                if(A_keyWidth.equals(name))
                {
                	m_row.defaultWidth = getSize(p.getAttributeValue(i), m_row.defaultWidth, m_row.defaultWidth, B_keyWidth);
                }
                if(A_verticalGap.equals(name))
                {
                    m_row.verticalGap = getSize(p.getAttributeValue(i), m_displayHeight, m_row.verticalGap, B_verticalGap);
                }
            }
        }
        return true;
    }
    LatinKey newKey()
    {
        LatinKey k = new LatinKey(m_row);
        if (m_row.defaultWidth!=0)
            k.width = m_row.defaultWidth;
        else
            k.width = getKeyWidth();
        k.height = m_row.defaultHeight;
        k.gap = getHorizontalGap();
        k.x = m_x;
        k.y = m_y+m_row.verticalGap;
        k.pressed = false;
        k.on = false;
        return k;
    }
    final void processKey(LatinKey k)
    {
        k.x+=k.gap;
        try{
        k.init(m_row);
        }
        catch(Throwable e)
        {
            
        }
        if(k.codes!=null&&k.codes.length>0&&k.codes[0]==KEYCODE_SHIFT)
            setShiftKey(m_keys.size(), k);
        m_keys.add(k);
        m_x+=k.gap+k.width;
    }
    final byte parseKeyWithConvert(DataInputStream is) throws IOException
    {
        String out = "  <Key ";
        LatinKey k = newKey();
        byte b = 0;
        do{
            b = is.readByte();
            switch(b)
            {
            	case B_keyWidth:
            		out+=A_ANDROID+A_keyWidth+"=\""+is.readFloat()+"%p\" ";
//                k.width = 10;//getPercentSize(is);
            		break;
            	case B_keyHeight:
            		out+=A_ANDROID+A_keyHeight+"=\""+is.readFloat()+"%p\" ";
            		break;
                case B_codes:
                    out+=A_ANDROID+A_codes+"=\""+is.readUTF()+"\" ";
                    break;
                case B_comboKeyCodes:
                    out+=A_ANDROID+A_comboKeyCodes+"=\""+is.readUTF()+"\" ";
                    break;
                case B_longComboKeyCode:
                    out+=A_ANDROID+A_longComboKeyCode+"=\""+is.readUTF()+"\" ";
                    break;
                case B_upCode:
                    out+=A_ANDROID+A_upCode+"=\""+is.readInt()+"\" ";
                    break;
                case B_keyLabel:
                    out+=A_ANDROID+A_keyLabel+"=\""+st.decompileText(is.readUTF())+"\" ";
                    break;
                case B_smallLabel:
                    out+=A_ANDROID+A_smallLabel+"=\""+(is.readBoolean()?"true":"false")+"\" ";
                    break;
                case B_keyIcon:
                	String f = is.readUTF();
                	f=getStringDraw(f);
                    out+=A_ANDROID+A_keyIcon+"=\""+f+"\" ";
                    break;
                case B_isSticky:
                    out+=A_ANDROID+A_isSticky+"=\""+(is.readBoolean()?"true":"false")+"\" ";
                    break;
                case B_isRepeatable:
                    out+=A_ANDROID+A_isRepeatable+"=\""+(is.readBoolean()?"true":"false")+"\" ";
                    break;
                case B_specKey:    
                    out+=A_ANDROID+A_specKey+"=\""+(is.readBoolean()?"true":"false")+"\" ";
                    break;
                case B_popupCharacters:
                    out+=A_ANDROID+A_popupCharacters+"=\""+getPopupString(is.readUTF())+"\" ";
                    break;
                case B_shortPopupCharacters:
                    out+=A_ANDROID+A_shortPopupCharacters+"=\""+getPopupString(is.readUTF())+"\" ";
                    break;
                case B_horizontalGap:    
                    out+=A_ANDROID+A_horizontalGap+"=\""+is.readFloat()+"%p\" ";
                    break;
                case B_verticalGap:   
                    out+=A_ANDROID+A_verticalGap+"=\""+is.readFloat()+"%p\" ";
	                break;
                case B_help:
                    out+=A_ANDROID+A_help+"=\""+is.readUTF()+"\" ";
                    break;
                case B_calcReg:
                    out+=A_ANDROID+A_calcReg+"=\""+is.readUTF()+"\" ";
                    break;
                case B_calcMenu:
                    out+=A_ANDROID+A_calcMenu+"=\""+is.readUTF().trim()+"\" ";
                    break;
                case B_keyboard:   
                    out+=A_ANDROID+A_Keyboard+"=\""+is.readUTF()+"\" ";
	                break;
                case B_keyboardLong:   
                    out+=A_ANDROID+A_KeyboardLong+"=\""+is.readUTF()+"\" ";
	                break;
            }
        }
        while(b<BA_ROW);
        out+="/>\n";
        processKey(k);
        m_os.write(out.getBytes());
        return b;
    }
    final byte parseKeysWithConvert(DataInputStream is) throws IOException
    {
        String out = "  <Keys\n       ";
        byte b = 0;
        try {
            do{
                b = is.readByte();
                switch(b)
                {
            	case B_arrayKeys:
            		out+=A_ANDROID+A_arrayKeys+"=\""+is.readUTF()+"\"\n       ";
            		break;
            	case B_arrayAnchors:
            		out+=A_ANDROID+this.A_arrayAnchors+"=\""+is.readUTF()+"\"\n       ";
            		break;
                }
            }
            while(b<BA_ROW);
			
		} catch (Throwable e) {
		}
        out+="\n  />\n";//</Row>\n";
//        processKey(k);
        m_os.write(out.getBytes());
        return b;
    }
    final byte parseKey(DataInputStream is) throws IOException
    {
        LatinKey k = newKey();
        boolean setWidth = false;
        byte b = 0;
        do{
            b = is.readByte();
            switch(b)
            {
                case B_keyWidth:
                	float w = getPercentSizeFloat(is);
                    k.width = floatToInt(w);
                    m_fraction+=floatToIntFraction(w);
                    setWidth = true;
                    break;
                case B_keyHeight:
                	float h = getPercentSizeFloat(is);
                    k.height = floatToInt(h);
                    m_fraction+=floatToIntFraction(h);
                    break;
                case B_codes:
                    k.codes = parseCodes(is.readUTF(), B_codes);
                    break;
                case B_template:
                    k.mainText = is.readUTF();
                    k.flags|=LatinKey.FLAG_USER_TEMPLATE;
                    break;
                case B_templateLong:   
	                k.longText = is.readUTF();
	                k.flags|=LatinKey.FLAG_USER_TEMPLATE_LONG;
	                break;
                case B_keyboard:
                    k.mainText = is.readUTF();
                    k.flags|=LatinKey.FLAG_USER_KEYBOARD;
                    break;
                case B_keyboardLong:   
	                k.longText = is.readUTF();
	                k.flags|=LatinKey.FLAG_USER_KEYBOARD_LONG;
	                break;
                case B_upCode:
                    k.longCode = is.readInt();
                    break;
                case B_keyLabel:
                    k.label = is.readUTF();

                    break;
                case B_smallLabel:
                    k.smallLabel = is.readBoolean();
                    break;
                case B_goQwerty:
                    if(is.readBoolean())
                        k.flags|=LatinKey.FLAG_GO_QWERTY;
                    else    
                        k.flags|=LatinKey.FLAG_NOT_GO_QWERTY;
                    break;
                case B_keyIcon:
                    k.icon = getDrawable(is.readUTF(),true, k);
                    break;
                case B_isSticky:
                    k.sticky = is.readBoolean();
                    break;
                case B_isRepeatable:
                    k.repeatable = is.readBoolean();
                    break;
                case B_specKey:    
                    k.specKey = is.readBoolean()?1:0;
                    break;
                case B_horizontalGap:    
                	float g = getPercentSizeFloat(is);
                	k.gap  = floatToInt(g);
                    m_fraction+=floatToIntFraction(g);
                    if(m_fraction>1f)
                    {
                    	k.gap++;
                    	m_fraction-=1f;
                    }
                    break;
                case B_keyOutputText:    
                    k.text = is.readUTF();
                    break;
                case B_longKeyOutputText:    
                    k.longText = is.readUTF();
                    break;
                case B_popupCharacters:    
                    k.popupCharacters = is.readUTF();
                    k.popupResId = R.xml.kbd_empty;
                    break;
                case B_shortPopupCharacters:    
                    k.shortPopupCharacters = is.readUTF();
                    break;
                case B_help:    
                    k.help = st.compileText(is.readUTF());
                    break;
                case B_calcMenu:    
                    k.calc_menu = st.compileText(is.readUTF());
                    break;
                case B_calcReg:    
                    k.calc_reg_fl = st.str2int(is.readUTF(),0,99, "Keyboard create");
                    break;
            }
        }
        while(b<BA_ROW);
        if(!setWidth)
        	m_fraction+=m_globalFraction;
        if(m_fraction>1f)
        {
        	k.width++;
        	m_fraction-=1;
        }
        processKey(k);
        return b;
    }
    final boolean parseReplace(XmlPullParser p)
    {
        int cnt = p.getAttributeCount();
        String from=null,to=null;
        for(int i=0;i<cnt;i++)
        {
            String name = attName(p, i);
            if(name.equals(A_From))
            {
                from = p.getAttributeValue(i);
            }
            else if(name.equals(A_To))
            {
                to = p.getAttributeValue(i);
            }
            
        }
        if(from==null||to==null)
            return false;
        arReplacement.add(new Replacement(from, to));
        return true;
        
    }
    final void processKeyPercentWidth(float width,LatinKey k)
    {
    	k.width = floatToInt(width);
    	m_fraction+=floatToIntFraction(width);
    	if(m_fraction>=1f)
    	{
    		k.width++;
    		m_fraction-=1f;
    	}
    }
    final boolean parseKey(XmlPullParser p, List<Key> keys) throws IOException
    {
    	if (st.type_keyboard.compareTo(TYPE_LAYOUT_SCROLL) == 0) {
    		if (com_menu.inst!=null)
    			com_menu.close();
    		new com_menu().showCalcHistory();//show(null, false);
    		return true;
    	}
        LatinKey k = newKey();
        if(m_os!=null)
            m_os.writeByte(BA_KEY);
        boolean setWidth = false;
        int cnt = p.getAttributeCount();
        boolean flag_teg = false;
        String name = st.STR_NULL;
        number_key++;
        for(int i=0;i<cnt;i++)
        {
        	flag_teg = false;
            name = attName(p, i);
            if(name.equals(A_keyWidth))
            {
            	setWidth = true;
            	float sz = getSizeFloat(p.getAttributeValue(i), m_displayWidth, getKeyWidth(),B_keyWidth); 
                k.width = floatToInt(sz);
                m_fraction+=floatToIntFraction(sz);
                flag_teg = true;
            }
            else if(name.equals(A_keyHeight)) {
                	k.height = getSize(p.getAttributeValue(i), m_displayHeight, getKeyWidth(),B_keyHeight);
                    flag_teg = true;
            }
            else if(name.equals(A_codes)) {
                	k.codes = parseCodes(p.getAttributeValue(i),B_codes);
                    flag_teg = true;
            }
            else if(name.equals(A_upCode))
            {
                k.longCode = Integer.decode(p.getAttributeValue(i));
                if(m_os!=null)
                {
                    m_os.writeByte(B_upCode);
                    m_os.writeInt(k.longCode);
                }
                flag_teg = true;
}
            else if(name.equals(A_keyLabel)){
                k.label = processLabel(p.getAttributeValue(i));
                flag_teg = true;
            }
            else if(name.equals(A_comboKeyCodes)) {
            	k.comboKeyCodes = parseCodes(p.getAttributeValue(i),B_comboKeyCodes);
                flag_teg = true;
            }
            else if(name.equals(A_longComboKeyCode)) {
            	k.longComboKeyCode = Integer.decode(p.getAttributeValue(i));
                flag_teg = true;
            }
            else if(name.equals(A_noColor)){
                	k.noColorIcon = getBoolean(p.getAttributeValue(i), B_noColor);
                    flag_teg = true;
            }
            else if(name.equals(A_horizontalGap))
            {
            	float g = getSizeFloat(p.getAttributeValue(i), m_displayWidth, getHorizontalGap(),B_horizontalGap); 
                k.gap = floatToInt(g);
                m_fraction+=floatToIntFraction(g);
                if(m_fraction>=1f)
                {
                	k.gap++;
                	m_fraction-=1f;
                }
                flag_teg = true;
            }
            else if(name.equals(A_keyIcon)) {
                k.icon = getDrawable(p.getAttributeValue(i),false, k);
                flag_teg = true;
            }
            else if(name.equals(A_isSticky)) {
                k.sticky = getBoolean(p.getAttributeValue(i),B_isSticky);
                flag_teg = true;
            }
            else if(name.equals(A_smallLabel)) {
                k.smallLabel = getBoolean(p.getAttributeValue(i),B_smallLabel);
                flag_teg = true;
            }
            else if(name.equals(A_isRepeatable)) {
                k.repeatable = getBoolean(p.getAttributeValue(i),B_isRepeatable);
                flag_teg = true;
            }
            else if(name.equals(A_specKey)) {
                k.specKey = getBoolean(p.getAttributeValue(i),B_specKey)?1:0;
                flag_teg = true;
            }
            else if(name.equals(A_popupCharacters))
            {
                k.popupResId = R.xml.kbd_empty;
                k.popupCharacters =getString(p.getAttributeValue(i),B_popupCharacters);
                flag_teg = true;
            } 
            else if(name.equals(A_shortPopupCharacters))
            {
                k.shortPopupCharacters =getString(p.getAttributeValue(i),B_shortPopupCharacters);
                flag_teg = true;
            } 
            else if(name.equals(A_help))
            {
                k.help =st.compileText(getString(p.getAttributeValue(i),B_help));
                flag_teg = true;
            } 
            else if(name.equals(A_calcReg))
            {
                k.calc_reg_fl = st.str2int(getString(p.getAttributeValue(i),B_calcReg),0,99,"Decompile keyboard");
                flag_teg = true;
            } 
            else if(name.equals(A_calcMenu))
            {
                k.calc_menu =st.compileText(getString(p.getAttributeValue(i),B_calcMenu));
                flag_teg = true;
            } 
            else if(name.equals(A_Keyboard))
            {
            	k.mainText = getString(p.getAttributeValue(i),B_keyboard);
            	k.flags|=LatinKey.FLAG_USER_KEYBOARD;
                flag_teg = true;
            }
            else if(name.equals(A_KeyboardLong))
            {
            	k.longText = getString(p.getAttributeValue(i),B_keyboardLong);
            	k.flags|=LatinKey.FLAG_USER_KEYBOARD_LONG;
                flag_teg = true;
            }
            else if(name.equals(A_Template))
            {
            	k.mainText = getString(p.getAttributeValue(i),B_template);
            	if (k.mainText.startsWith(Templates.TPL_SPEC_CHAR+Templates.SPEC_INSTR_PROGRAM))
            	{
            		k.mainText = st.compileText(k.mainText);
            	}
            	k.flags|=LatinKey.FLAG_USER_TEMPLATE;
                flag_teg = true;
            }
            else if(name.equals(A_TemplateLong))
            {
            	k.longText = getString(p.getAttributeValue(i),B_templateLong);
            	if (k.longText.startsWith(Templates.TPL_SPEC_CHAR+Templates.SPEC_INSTR_PROGRAM))
            	{
            		k.longText = st.compileText(k.longText);
            	}
            	k.flags|=LatinKey.FLAG_USER_TEMPLATE_LONG;
                flag_teg = true;
            }
            else if(name.equals(A_keyOutputText)) {
                k.mainText = getString(p.getAttributeValue(i),B_keyOutputText);
                flag_teg = true;
            }
            else if(name.equals(A_longKeyOutputText)) {
                k.longText = getString(p.getAttributeValue(i),B_longKeyOutputText);
                flag_teg = true;
            }
            else if(name.equals(A_goQwerty)) {
                k.setGoQwerty(getBoolean(p.getAttributeValue(i),B_goQwerty));
                flag_teg = true;
            }

            if (flag_teg == false)
            	if (name.toUpperCase().startsWith("XXX") == false)
            		st.toast("Error parsing "+number_key+" key:\nUnknown tag ("+name+")");
        }
        if(!setWidth)
        	m_fraction+=m_globalFraction;
        if(m_fraction>1f)
        {
        	k.width++;
        	m_fraction-=1f;
        }
        processKey(k);
        return true;
    }
    public String processLabel(String label) throws IOException
    {
    	label = st.compileText(label);
        if(m_os!=null)
        {
            m_os.writeByte(B_keyLabel);
            m_os.writeUTF(label);
        }
        return label;
    }
    /** возвращает массив int с кодами клавиш.
     * @param attributeValue - строка для преобразования в int 
     * @param save_code - имя параметра для декомпиляции (B_ коды) */
    private int[] parseCodes(String attributeValue, byte save_code) throws IOException
    {
        attributeValue.trim();
        if(m_os!=null)
        {
            //m_os.writeByte(B_codes);
            m_os.writeByte(save_code);
            m_os.writeUTF(attributeValue);
        }
        String a[] = attributeValue.split(st.STR_COMMA);
        int cd[] = new int[a.length];
        for(int i=0;i<a.length;i++)
            cd[i]=Integer.decode(a[i]);
        return cd;
    }
    final String attName(XmlPullParser p,int index)
    {
        String name = p.getAttributeName(index);
        if(name.startsWith(A_ANDROID))
            return name.substring(A_ANDROID.length());
        return name;
    }
    final int floatToInt(float val)
    {
        return (int)val;
    }
    final float floatToIntFraction(float val)
    {
    	return val - floatToInt(val);
    }
    float getSizeFloat(String att,float percentBase,int defValue,byte type)throws IOException
    {
        float ret = defValue;
        if(att.endsWith(VAL_PERCENT))
        {
            String v = att.substring(0,att.length()-VAL_PERCENT.length());
            if(m_os!=null)
            {
                m_os.writeByte(type);
                m_os.writeFloat(Float.valueOf(v));
                if (compil_metric == 1){
                	compil_metric = 0;
                    m_os.writeByte(1);
                }
            }
            ret = (float) (Float.valueOf(v)*percentBase/100f);
        }
        else if(att.endsWith(VAL_PIXELS))
        {
            String v = att.substring(0,att.length()-VAL_PIXELS.length());
            if(m_os!=null)
            {
                m_os.writeByte(type);
                m_os.writeFloat(Float.valueOf(v));
                if (compil_metric == 2){
                	compil_metric = 0;
                    m_os.writeByte(2);
                }
            }
            ret = Float.valueOf(v);
        }
        return ret;
    }
    int getSize(String att,float percentBase,int defValue,byte type) throws IOException
    {
        return floatToInt(getSizeFloat(att, percentBase, defValue, type));
    }
    final boolean getBoolean(String att,byte type) throws IOException
    {
        boolean b = (att.compareToIgnoreCase("true")==0);
        if(m_os!=null)
        {
            m_os.writeByte(type);
            m_os.writeBoolean(b);
        }
        return b;
    }
    final String getString(String att,byte type) throws IOException
    {
        if(m_os!=null)
        {
            m_os.writeByte(type);
            m_os.writeUTF(att);
        }
        return att;
    }
    final Drawable getDrawable(String att,boolean bCompiled, LatinKey key) throws IOException
    {
        if(att.startsWith(DRW_PREFIX))
            att = att.substring(DRW_PREFIX.length());
        else if(!bCompiled)
            return loadFileDrawable(att);
        if(m_os!=null)
        {
            m_os.writeByte(B_keyIcon);
            m_os.writeUTF(att);
        }
        if(att.equals("delete")) return draw.paint().getBitmap(R.drawable.sym_keyboard_delete);
        if(att.equals("forward_del")) return draw.paint().getBitmap(R.drawable.sym_keyboard_forward_del);
        if(att.equals("done")) return draw.paint().getBitmap(R.drawable.sym_keyboard_done);
        if(att.equals("return")) {
        	if (key!=null)
        		key.iconRes = R.drawable.sym_keyboard_return;
        	return draw.paint().getBitmap(R.drawable.sym_keyboard_return);
        }
        if(att.equals("shift")) return draw.paint().getBitmap(R.drawable.sym_keyboard_shift);
        if(att.equals("space")) return draw.paint().getBitmap(R.drawable.sym_keyboard_space);    
        if(att.equals("search")) return draw.paint().getBitmap(R.drawable.sym_keyboard_search);    
        return null;
    }
    final String getStringDraw(String att) throws IOException
    {
        if(att.equals("delete")) return DRW_PREFIX + att;
        if(att.equals("done")) return DRW_PREFIX + att;
        if(att.equals("return")) return DRW_PREFIX + att;
        if(att.equals("shift")) return DRW_PREFIX + att;
        if(att.equals("space")) return DRW_PREFIX + att;    
        if(att.equals("search")) return DRW_PREFIX + att;    
        return st.STR_NULL;
    }
    private Drawable loadFileDrawable(String att)
    {
        String path = att;
        if(!att.startsWith(st.STR_SLASH))
        {
            path = st.getSettingsPath()+KEYBOARD_FOLDER+st.STR_SLASH+att;
        }
        return draw.paint().getBitmap(path);
    }
    void setShiftKey(int index,Key key)
    {
        try{
            Field f;
            f = Keyboard.class.getDeclaredField("mShiftKey");
            f.setAccessible(true);
            f.set(this, key);
            f = Keyboard.class.getDeclaredField("mShiftKeyIndex");
            f.setAccessible(true);
            f.set(this, index);
        }
        catch (Throwable e) {
        }
    }
    public static String updateArrayKeyboards(boolean bCompile)
    {
        try{
        	st.setDefaultKeybrd();
            String path = st.getSettingsPath()+KEYBOARD_FOLDER;
            Vector<Keybrd> arKb = new Vector<Keybrd>();
            File f = new File(path);
            if(!f.exists())
            {
                f.mkdirs();
                return null;
            }
            File keyboards[] = st.getFilesByExt(f, st.EXT_XML);
            Arrays.sort(keyboards);
            if(keyboards==null||keyboards.length==0)
                return null;
            for(File kf:keyboards)
            {
                Keybrd k = processCustomKeyboardFile(kf,bCompile);
                if(k!=null)
                    arKb.add(k);
            }
            if(arKb.size()==0)
                return null;
            int pos = 0;
            for(Keybrd k:st.arKbd)
            {
                if(k.kbdCode==st.KBD_CUSTOM)
                    break;
                ++pos;
            }
            Keybrd ak []=new Keybrd[pos+arKb.size()];
            System.arraycopy(st.arKbd, 0, ak, 0, pos);
            for(Keybrd k:arKb)
            {
                ak[pos]=k;
                ++pos;
            }
            st.arKbd = ak;
        }
        catch(Throwable e)
        {
        }
        return null;
    }
    public final float getPercentSizeFloat(DataInputStream is) throws IOException
    {
        float f = is.readFloat();
        return f*(float)m_displayWidth/100f;
    }
    public int getPercentSize(DataInputStream is) throws IOException
    {
        return floatToInt(getPercentSizeFloat(is));
    }
    public static Keybrd processCustomKeyboardFile(File kf,boolean bCompile)
    {
        if(!kf.exists()||!kf.canRead())
            return null;
        String name = kf.getName();
        int f = name.indexOf('_');
        if(f<0)
            return null;
        name = name.substring(0,f);
        Lang lng = null;
        if (st.arLangs.length>0)
        	st.arLangs.clone();
        for(Lang l:st.arLangs)
        {
            if(l.name.equals(name))
            {
                lng = l;
                break;
            }
        }
        if(lng==null)
            lng = st.addCustomLang(name);
        Keybrd kb = new Keybrd(st.KBD_CUSTOM, lng, R.xml.kbd_empty, 0);
        kb.path = kf.getAbsolutePath();
        if(bCompile)
            compileKeyboard(kf, kb);
        return kb;
    }
    static boolean compileKeyboard(File kf,Keybrd kb)
    {
        String path = kf.getParentFile().getAbsolutePath()+"/compiled";
        new File(path).mkdirs();
        path+=st.STR_SLASH+kf.getName().substring(0,kf.getName().length()-4);
        return convertToCompiledFormat(st.c(), kb, path);

    }
    static boolean convertToCompiledFormat(Context c,Keybrd kbd,String xmlPath)
    {
        try{
            File f = new File(xmlPath);
            f.delete();
            f.createNewFile();
            m_os = new DataOutputStream(new FileOutputStream(f));
            new CustomKeyboard(c, kbd);
            m_os = null;
            return true;
        }
        catch (Throwable e) {
        }
        return false;
    }
    /** смещает клавиши клавиатуры по вертикали
     * @param space - величина смещения
     * @param updown - если true - клавиши смещаются наверх
     *  */
    boolean setTopSpace(int space, boolean updown)
    {
        int diff = space-m_gap;
        if(diff==0)
            return false;
        m_gap = space;
        try{
            m_totalHeight.setInt(this, getHeight()+diff);
            for(Key k:getKeys())
            	if (!updown)
                    k.y+=diff;
            return true;
        }
        catch (Throwable e) {
        }
        return false;
    }
    int m_gap=0;

    String getPopupString(String in)
    {
    	char ch;
    	String out=st.STR_NULL;
    	for(int i=0;i<in.length();i++){
    		ch = in.charAt(i);
            switch (ch)
            {
            case '&': out+="&amp;";break;
            case '<': out+="&lt;";break;
            case '>': out+="&gt;";break;
            case '\"': out+="&quot;";break;
            default:
            	out += ch;
            	break;
            }
    	}
    	return out;
    }
}