package com.jbak2.JbakKeyboard;

import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.Keyboard.Key;
import android.view.MotionEvent;

import com.jbak2.JbakKeyboard.JbKbd.LatinKey;

public class PressArray
{
    JbKbdView m_kv;

    public static final int TYPE_LONG = 1;
    public static final int TYPE_REPEAT = 2;
    public static final int TYPE_CANCEL = 3;
    PressInfo m_ar[] = new PressInfo[]{null,null,null,null,null,null,null,null,null,null};
    public static class PressInfo
    {
        
        public PressInfo(LatinKey lk,int ptId)
        {
            key = lk;
            pointerId = ptId;
        }
        public boolean isMoved(int x,int y)
        {
            if(!key.isInside(x, y))
                return true;
            return false;
        }
        int type=0;
        int pointerId = 0;
        LatinKey key;
        int index = -1;
    }
    public PressArray(JbKbdView kv)
    {
        m_kv = kv;
    }
    public boolean add(PressInfo pi)
    {
        for(int i = m_ar.length-1;i>=0;i--)
        {
            if(m_ar[i]==null)
            {
                m_ar[i]=pi;
                return true;
            }
        }
        return false;
    }
    public PressInfo get(int primaryCode)
    {
        for(PressInfo pi:m_ar)
        {
            if(pi!=null&&pi.key.codes[0]==primaryCode)
                return pi;
        }
        return null;
    }
    public PressInfo getByPointer(int pointerId)
    {
        for(PressInfo pi:m_ar)
        {
            if(pi!=null&&pi.pointerId==pointerId)
                return pi;
        }
        return null;
    }
    public boolean remove(int primaryCode)
    {
        boolean ret = false;
        for(int i = m_ar.length-1;i>=0;i--)
        {
            PressInfo pi = m_ar[i];
            if(pi!=null&&pi.key.codes[0]==primaryCode)
            {
                ret = true;
                m_ar[i]=null;
            }
        }
        return ret;
    }
    public PressInfo pointerOver(int x,int y)
    {
        for(PressInfo pi:m_ar)
        {
            if(pi!=null&&pi.key.isInside(x, y))
                return pi;
        }
        return null;
    }
    public boolean resetPress(int x,int y)
    {
        boolean bRet = false;
        for(PressInfo pi:m_ar)
        {
            if(pi!=null&&!pi.key.isInside(x, y))
            {
                pi.key.pressed = false;
                bRet = true;
            }
        }
        return bRet;
    }
    public boolean setPress(int primaryCode,int type)
    {
        PressInfo pi = get(primaryCode);
        if(pi==null)
            return false;
        pi.type = type;
        return true;
    }
    public int getPress(int primaryCode)
    {
        PressInfo pi = get(primaryCode);
        if(pi==null)
            return -1;
        return pi.type;
    }
    static final String TAG = "jbKbdPress";
    @SuppressWarnings("deprecation")
    public boolean onTouchEvent(MotionEvent me,JbKbd kbd,KeyboardView.OnKeyboardActionListener listener)
    {
//        Log.d(TAG, "act:"+me.getAction()+", x="+me.getX()+", y="+me.getY()+", pc="+me.getPointerCount());
        int action = me.getAction();
        if(action==MotionEvent.ACTION_MOVE)
        {
            checkMove(me, listener);
            return true;
        }
        if(action!=MotionEvent.ACTION_DOWN
           &&action!=MotionEvent.ACTION_POINTER_1_DOWN
           &&action!=MotionEvent.ACTION_POINTER_2_DOWN
           &&action!=MotionEvent.ACTION_POINTER_3_DOWN
           &&action!=MotionEvent.ACTION_UP
           &&action!=MotionEvent.ACTION_POINTER_1_UP
           &&action!=MotionEvent.ACTION_POINTER_2_UP
           &&action!=MotionEvent.ACTION_POINTER_3_UP
        )
            return false;
        int ptindex = 0;
        int ptcnt = me.getPointerCount();
        if(ptcnt>1&&(action==MotionEvent.ACTION_POINTER_2_DOWN||action==MotionEvent.ACTION_POINTER_2_UP))
            ptindex = 1;
        else if(ptcnt>2&&(action==MotionEvent.ACTION_POINTER_3_DOWN||action==MotionEvent.ACTION_POINTER_3_UP))
            ptindex = 2;
        int pointerId = ptindex;//me.getPointerId(ptindex);
        int x = (int) me.getX(ptindex);
        int y = (int) me.getY(ptindex);
        LatinKey lk = null;
        for(Key k:kbd.getKeys())
        {
            if(k.isInside(x, y))
            {
                lk=(LatinKey) k;
                break;
            }
        }
        boolean bDown = (action==MotionEvent.ACTION_DOWN||action==MotionEvent.ACTION_POINTER_1_DOWN||action==MotionEvent.ACTION_POINTER_2_DOWN||action==MotionEvent.ACTION_POINTER_3_DOWN);
        if(lk!=null)
        {
            if(bDown)
            {
                remove(lk.codes[0]);
                add(new PressInfo(lk, pointerId));
                listener.onPress(lk.codes[0]);
            }
            else
            {
                PressInfo pi = getByPointer(pointerId);
                if(pi!=null)
                {
  //                  Log.d(TAG, "release " +pi.key.getMainText());
                    listener.onRelease(pi.key.codes[0]);
                    remove(pi.key.codes[0]);
                }
            }
        }
        return true;
    }
    final void checkMove(MotionEvent me,KeyboardView.OnKeyboardActionListener listener)
    {
        for(int i = me.getPointerCount()-1;i>=0;i--)
        {
            PressInfo pi = getByPointer(me.getPointerId(i));
            if(pi!=null&&pi.isMoved((int)me.getX(i), (int)me.getY(i)))
            {
//                Log.d(TAG, "release move " +pi.key.getMainText());
                setPress(pi.key.codes[0], TYPE_CANCEL);
                listener.onRelease(pi.key.codes[0]);
                remove(pi.key.codes[0]);
            }
        }
    }
    void reset()
    {
        for(int i=m_ar.length-1;i>=0;i--)
        {
            m_ar[i] = null;
        }
    }
}
