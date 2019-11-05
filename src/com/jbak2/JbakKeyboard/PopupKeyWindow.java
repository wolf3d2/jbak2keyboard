package com.jbak2.JbakKeyboard;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Build;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.jbak2.JbakKeyboard.JbKbd.LatinKey;
import com.jbak2.ctrl.SameThreadTimer;
import com.jbak2.CustomGraphics.draw;

/** показывает окно нажатой клавиши */
public class PopupKeyWindow
{
    SameThreadTimer m_tm;
    OwnView m_view;
    public static int m_w = 100;
    public static int m_h = 80;
    boolean m_bShowUnderKey = true;
    WindowManager m_wm;
    boolean m_bShow = false;
    public PopupKeyWindow(Context c,int w,int h)
    {
// задаёт размеры окна нажатых клавиш
//      int bbb = (int) (100 * 0.5);
        m_w = (int) (w * st.popup_win_size);
        m_h = (int) (h * st.popup_win_size);

        m_view = new OwnView(c);
        m_view.setKey(null, false);
        m_wm = (WindowManager)c.getSystemService(Service.WINDOW_SERVICE);
    }
    public void close()
    {
        if(!m_bShow)
            return;
        try{
            m_wm.removeViewImmediate(m_view);
        }
        catch (Throwable e) {
            
        }
    }
    public void addView(int x,int y)
    {


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = m_w;
        lp.height = m_h;
        lp.format = PixelFormat.TRANSLUCENT;
        lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                |WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                |WindowManager.LayoutParams.FLAG_FULLSCREEN
                |WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                |WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                ;
        lp.gravity = Gravity.LEFT|Gravity.TOP;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
		} else {
			lp.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
		}
        //lp.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        lp.x = x;
        lp.y = y;
//        if (!Perm.checkPermission(m_c)) {
//        	st.toastLong(R.string.perm_not_all_perm);
//        	return;
//        }
        m_wm.addView(m_view, lp);
    }
    public void show(JbKbdView v,LatinKey key,boolean bLong)
    {
    	m_view.setKey(key, bLong);
        int sh = m_view.getResources().getDisplayMetrics().heightPixels;
        int kbdTop = sh-v.getKeyboard().getHeight();
        int xoff = v.getWidth()/2-m_w/2;
        int yoff = kbdTop-m_h-4;
        if(m_bShowUnderKey)
        {
            xoff = Math.min(v.getWidth()-m_w-4, key.x);
            yoff = Math.max(yoff,kbdTop+key.y-m_h-40);
        }
        close();
        m_bShow = true;
        addView(xoff, yoff);
        if(m_tm!=null)
            m_tm.cancel();
        // длительность показа окна
        m_tm = new SameThreadTimer(300,0)
        {
            @Override
            public void onTimer(SameThreadTimer timer)
            {
                close();
            }
        };
        m_tm.start();
    }
    public void hide()
    {
        m_view.setKey(null, false);
//        if(isShowing())
//            dismiss();
    }
    public static class OwnView extends View
    {
    	/** нигде не используется, но если убрать,
    	 * то кайма на окне рисуется неправильно */
    	Paint kaima = null;
        LatinKey key;
        boolean bLong=false;
        TextPaint m_pt;
        Paint m_bgPaint;
        RectF m_bgRf;
        RectF kaimaRf;
        public OwnView(Context context)
        {
            super(context);
            m_pt = new TextPaint();
            m_pt.setColor(Color.BLACK);
            m_pt.setAntiAlias(true);
            m_bgRf = new RectF(0, 0, m_w-1, m_h-1);
            kaimaRf = new RectF(1, 1, m_w-2, m_h-2);
            m_bgPaint = new Paint();
// цвет фона окна показа нажатой клавиши
//            m_bgPaint.setColor(0xeeffffff);
             m_bgPaint.setColor(ServiceJbKbd.inst.m_popup_color);
             
        }
        public void setKey(LatinKey k,boolean longPress)
        {
            key = k;
            bLong = longPress;
            invalidate();
        }
      @Override
      protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
      {
          setMeasuredDimension(m_w, m_h);
      };
        @Override
        public void draw(Canvas canvas)
        {
            //canvas.drawColor(0x99ffffff);
            if(key==null)
                return;
            canvas.drawRoundRect(m_bgRf, 8, 8, m_bgPaint);
            draw.paint().bitmapPreview.setColor(Color.BLACK);
            draw.paint().bitmapPreview.setStyle(Style.STROKE);
            kaima = draw.paint().bitmapPreview;
            // задаём размер кисти (каёмка вокруг окна)
            kaima.setStrokeWidth(2);
            canvas.drawRoundRect(kaimaRf, 8, 8,draw.paint().bitmapPreview);
            draw.paint().bitmapPreview.setStyle(Style.FILL);
            
//            canvas.translate(0, 0-m_h/2);
            st.kv().m_PreviewDrw.draw(canvas, draw.paint().bitmapPreview);
//            key.m_kd.draw(canvas, m_pt);
        }
    }
}