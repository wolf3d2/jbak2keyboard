package com.jbak2.JbakKeyboard;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.jbak2.JbakKeyboard.JbKbd.LatinKey;

import android.content.SharedPreferences;
import android.inputmethodservice.KeyboardView;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OwnKeyboardHandler extends Handler
{
	static String key_popup2=st.STR_NULL;
	  LinearLayout llMain;
    String opup2txt=st.STR_NULL;
    int repeatInterval = 0;
    int longPressInterval = 500;
    int deltaLongPress = 0;
    int deltaRepeatStart = 0;
    int firstRepeatInterval = 400;
    public static final int MSG_SHOW_PREVIEW = 1;
    public static final int MSG_REMOVE_PREVIEW = 2;
    public static final int MSG_REPEAT = 3;
    public static final int MSG_LONGPRESS = 4;
    public static final int MSG_INVALIDATE = 5;
    public static final int MSG_MY_REPEAT = 6;
    public static final int MSG_MY_LONG_PRESS = 7;
    Handler m_existHandler;
    TextView m_PreviewText;
    Method m_showKey;
    Method m_repeatKey;
    Method m_openPopupIfRequired;
    JbKbdView m_kv;
    public boolean m_bSuccessInit;
    public static OwnKeyboardHandler inst;
    public OwnKeyboardHandler(Handler exist,JbKbdView kv)
    {
        super();
        inst = this;
        m_kv = kv;
        m_existHandler = exist;
        m_bSuccessInit = init();
        loadFromSettings();
    }
    void loadFromSettings()
    {
        SharedPreferences p = st.pref(m_kv.getContext());
        longPressInterval = p.getInt(st.PREF_KEY_LONG_PRESS_INTERVAL, 500);
        deltaLongPress = longPressInterval>=500?longPressInterval-500:0;
        firstRepeatInterval = p.getInt(st.PREF_KEY_REPEAT_FIRST_INTERVAL, 400);
        deltaRepeatStart = firstRepeatInterval>=400?firstRepeatInterval-400:0;
        repeatInterval =  p.getInt(st.PREF_KEY_REPEAT_NEXT_INTERVAL, 50);
    }
    boolean init()
    {
        try{
            m_showKey = KeyboardView.class.getDeclaredMethod("showKey", int.class);
            m_repeatKey = KeyboardView.class.getDeclaredMethod("repeatKey");
            m_openPopupIfRequired = KeyboardView.class.getDeclaredMethod("openPopupIfRequired",MotionEvent.class);
            m_openPopupIfRequired.setAccessible(true);
            m_repeatKey.setAccessible(true);
            m_showKey.setAccessible(true);
            Field f = KeyboardView.class.getDeclaredField("mPreviewText");
            f.setAccessible(true);
            m_PreviewText = (TextView) f.get(m_kv);
            return true;
        }
        catch (Throwable e) {
        }
        return false;
    }
    void invokeShowKey(int key)
    {
        try{
            if(m_showKey!=null)
            {
                m_showKey.invoke(m_kv, key);
            }
        }
        catch(Throwable e)
        {
        }
    }
    @Override
    public void handleMessage(Message msg)
    {
        try{
            switch (msg.what) 
            {
                case MSG_INVALIDATE:
                    m_kv.trueInvalidateKey(msg.arg1);
                    break;
//                case MSG_SHOW_PREVIEW:
//                    invokeShowKey(msg.arg1);
//                    break;
//                case MSG_REMOVE_PREVIEW:
//                    if(m_PreviewText!=null)
//                        m_PreviewText.setVisibility(View.INVISIBLE);
//                    break;
                case MSG_REPEAT:
                    break;
                case MSG_MY_REPEAT:
                    {
                        LatinKey lk = (LatinKey)msg.obj;
                        if(lk==null||!lk.pressed||!m_kv.getCurKeyboard().hasKey(lk))
                            return;
                        lk.processed = true;
                        m_kv.onKeyRepeat(lk);
                        sendRepeat(lk, false);
                    }
                    break;
                case MSG_LONGPRESS:
//                    if(deltaLongPress==0&&msg.arg1==0)
//                    {
//                        sendMessageDelayed(obtainMessage(MSG_LONGPRESS,1,1, msg.obj),deltaLongPress);
//                        return;
//                    }
//                    if(m_openPopupIfRequired!=null)
//                    {
//                        m_openPopupIfRequired.invoke(m_kv, (MotionEvent) msg.obj);
//                    }
                    break;
                case MSG_MY_LONG_PRESS:
                    {
                        LatinKey lk = (LatinKey)msg.obj;
                        if(lk!=null&&lk.pressed)
                        {
                            lk.processed = true;
// вывод окошка маленькой клавиатурки первой версии  
                            st.fl_popupcharacter2 = false;
                            m_kv.longpress = true;

                            m_kv.onLongPress(lk);
                        }
                    }
                    break;
            }
        }
        catch (Throwable e) {
        	st.logEx(e);
        }
    }
    public final void sendRepeat(LatinKey k,boolean bFirst)
    {
        sendMessageDelayed(obtainMessage(MSG_MY_REPEAT, k), bFirst?firstRepeatInterval:repeatInterval);
    }
    public final void sendLongPress(LatinKey k)
    {
        sendMessageDelayed(obtainMessage(MSG_MY_LONG_PRESS, k),longPressInterval);
    }
}
