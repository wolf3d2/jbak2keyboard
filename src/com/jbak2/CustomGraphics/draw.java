package com.jbak2.CustomGraphics;

import com.jbak2.JbakKeyboard.IKbdSettings;
import com.jbak2.JbakKeyboard.IKeyboard;
import com.jbak2.JbakKeyboard.KeyboardPaints;
import com.jbak2.JbakKeyboard.R;
import com.jbak2.JbakKeyboard.st;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

/** Класс содержит полезные графические методы*/
public class draw extends IKeyboard implements IKbdSettings {

	/** Возвращает id иконки по команде*/    
	public static Bitmap getBitmapByCmd(int cmd)
    {
        int bid = 0;
        switch (cmd)
        {
            case CMD_VOICE_RECOGNIZER: bid = R.drawable.vr_small_white;
        }
        if(bid!=0)
            return BitmapFactory.decodeResource(st.c().getResources(), bid);
        return null;
    }
    public static Drawable getBack()
    {
        return new GradBack(0xff000088, 0xff008800).setCorners(0, 0).setGap(0).setDrawPressedBackground(false).getStateDrawable();
    }
    public static final KeyboardPaints paint()
    {
        if(KeyboardPaints.inst==null)
            return new KeyboardPaints();
        return KeyboardPaints.inst;
    }

}