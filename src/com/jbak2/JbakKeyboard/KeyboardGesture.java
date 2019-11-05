package com.jbak2.JbakKeyboard;

import android.inputmethodservice.Keyboard;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.jbak2.JbakKeyboard.JbKbd.LatinKey;


public class KeyboardGesture extends GestureDetector
{
    public KeyboardGesture(JbKbdView view)
    {
        super(view.getContext(), new KvListener().setKeyboardView(view));
    }
    static class KvListener extends SimpleOnGestureListener
    {
        JbKbdView m_kv;
//        int minGestSize = 100;
// минимальная длина жеста
        int minGestSize = st.gesture_min_length;
        float deltaDelim = (float) 1.5;
//скорость жеста
//        int minVelocity = 150;
        int minVelocity = st.gesture_velocity;
        public KvListener setKeyboardView(JbKbdView kv)
        {
            m_kv = kv;
            return this;
        }
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			boolean ret = super.onDoubleTap(e); 
			LatinKey lk = st.getKeyByPress((int)e.getX(),(int)e.getY());
	        JbKbd kbd = st.kv().getCurKeyboard();
	        if(kbd==null)
	            return ret;
	        LatinKey slk = kbd.getKeyByCode(Keyboard.KEYCODE_SHIFT);
			if (lk!=slk)
				return ret;
			st.fl_gest_double_click_shift = true;
			return ret;
		}
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
        	st.minGestSize = minGestSize;
        	int downX = (int)e1.getX();
            int downY = (int)e1.getY();
            float dx = e2.getX()-e1.getX();
            float dy = e2.getY()-e1.getY();
            float mdx = Math.abs(dx);
            float mdy = Math.abs(dy);
// определяем длину жеста
            if (mdx >= mdy) {
        		st.gesture_length = mdx;
            } else{
            	st.gesture_length = mdy;
            }
//            st.log("dx="+dx+"; dy="+dy+";vX="+velocityX+";vY="+velocityY+"|downX="+downX+"; downY="+downY);
            if(mdx>=st.minGestSize&&(mdy==0||mdx/mdy>=deltaDelim)&&Math.abs(velocityX)>minVelocity)
            {
                int type = velocityX>0?GestureInfo.RIGHT:GestureInfo.LEFT;
                m_kv.gesture(new GestureInfo(st.getKeyByPress(downX, m_kv.m_vertCorr+downY),type));
                return true;
            }
            if(mdy>=st.minGestSize&&(mdx==0||mdy/mdx>=deltaDelim)&&Math.abs(velocityY)>minVelocity&&(velocityX==0||Math.abs(velocityY/velocityX)>=deltaDelim))
            {
                int type = velocityY>0?GestureInfo.DOWN:GestureInfo.UP;
                m_kv.gesture(new GestureInfo(st.getKeyByPress(downX, m_kv.m_vertCorr+downY),type));
                return true;
            }
            if(mdx>=st.minGestSize||mdy>=st.minGestSize)
                return true;
            return false;
        }
    };
    public static class GestureInfo
    {
        public static final int LEFT    = 0;
        public static final int RIGHT   = 1;
        public static final int UP      = 2;
        public static final int DOWN    = 3;
        LatinKey downKey;
        int dir;
        public GestureInfo(LatinKey k,int dir)
        {
            downKey = k;
            this.dir = dir;
        }
        
    }
    public static class GestureHisList
    {
        int  keycode;
        int direction;
        int action;
        int id;
        public GestureHisList(int keycod, int dir, int act, int identification)
        {
        	keycode = keycod;
            direction = dir;
            action = act;
            id = identification;
        }
    }
//    public static class GestureHideKeyboardList
//    {
//        int  keycode;
//        int direction;
//        int action;
//        int id;
//        String namehide;
//        public GestureHideKeyboardList(int keycod, int dir, int act, int identification)
//        {
//        	keycode = keycod;
//            direction = dir;
//            action = act;
//            id = identification;
//        }
//    }
}
