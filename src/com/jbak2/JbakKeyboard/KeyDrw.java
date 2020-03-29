package com.jbak2.JbakKeyboard;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.inputmethodservice.Keyboard.Key;

import com.jbak2.JbakKeyboard.JbKbd.LatinKey;
import com.jbak2.CustomGraphics.draw;

/** Собственная картинка для клавиш. Генерируется на основе {@link Key#label}*/    
class KeyDrw extends RectShape
{
    public static final String DRW_PREFIX = "d_"; 
    Bitmap bmp;
    Bitmap bmpSmall;
    String txtMain;
    String txtSmall;
    boolean m_bPreview = false;
    boolean m_bLongPreview = false;
    boolean m_bSmallLabel = false;
    boolean m_bPressed = false;
/** true, если на клавише несколько кодов, которые перебираются последовательно (codes.length>1)*/    
    boolean m_bMultiCode = false;
    Rect rb;
    boolean m_bFunc = false;
    boolean m_bNoColorIcon = false;
    public static int GAP = 5;
    public static final int DELIM = 2;
    public static final int DEFAULT_GAP = 5;
    public static final int BIG_GAP = 8;
    DrwCache m_c;
/** Конструктор
* @param key Клавиша из которой создаётся рисовалка */
    public KeyDrw(LatinKey key)
    {
        set(key, false);
    }
/** Пустой конструктор */    
    public KeyDrw()
    {
    }
    public Drawable getDrawable()
    {
        ShapeDrawable drw = new ShapeDrawable(this);
        drw.setBounds(rb);
        return drw;
    }
    public final void setSmallLabel(boolean small)
    {
        m_bSmallLabel = small;
    }
    public final void setFuncKey(boolean func)
    {
        m_bFunc = func;
    }
    final void useTextColor(Paint pt,boolean second)
    {
        int c = draw.paint().getColor(this, second);
        if(c!=st.DEF_COLOR)
            pt.setColor(c);
    }
//    @Override
//    protected void onResize(float width, float height) 
//    {
//        super.onResize(width, height);
//        if(m_bPreview&&st.paint().previewBack!=null)
//        {
//            st.paint().previewBack.setBounds(new Rect(0,0,(int)width,(int)height));
//        }
//    };
    final void set(LatinKey key, boolean bPreview)
    {
        set(key,bPreview,0);
    }
    void set(LatinKey key, boolean bPreview,int primaryCode)
    {
        m_c = null;
        m_bPreview = bPreview;
        m_bPressed = key.pressed;
        m_bNoColorIcon = key.noColorIcon;
        m_bMultiCode = (key.codes!=null&&key.codes.length>1);
        if(key.icon!=null)
        {
            if(key.icon instanceof BitmapDrawable)
            {
                this.bmp = ((BitmapDrawable)key.icon).getBitmap();
            }
            else if(key.icon instanceof ShapeDrawable)
            {
                KeyDrw kd = ((LatinKey)key).m_kd;
                txtMain = primaryCode==0?kd.txtMain:st.STR_NULL+(char)primaryCode;
                txtSmall = kd.txtSmall;
                bmp = kd.bmp;
            }
        }
        else
        {
            bmp = null;
        }
        if(key.label!=null)
        {
            CharSequence lab = key.label;
            int f = lab.toString().indexOf('\n');
            if(f>-1)
            {
                txtSmall = lab.subSequence(0, f).toString();
                bmpSmall = draw.getBitmapByCmd(st.getCmdByLabel(txtSmall));
                txtMain = lab.subSequence(f+1, lab.length()).toString();
            }
            else
            {
                txtMain = lab.toString();
                txtSmall = null;
            }
            if(primaryCode!=0)
                txtMain = st.STR_NULL+(char)primaryCode;
        }
        if(m_bPreview)
            rb = new Rect(0, 0, /*st.kv().m_PreviewHeight*/PopupKeyWindow.m_w, /*st.kv().m_PreviewHeight*/PopupKeyWindow.m_h);
        else    
            rb = new Rect(0,0,key.width,key.height);
    }
    final void drawFuncBackground(Canvas canvas)
    {
        if(m_bFunc&&draw.paint().funcBackDrawable!=null)
        	draw.paint().funcBackDrawable.draw(canvas);
    }
    static class DrwCache
    {
        String mainLower;
        String mainUpper;
        float m_xMainLower;
        float m_yMainLower;
        float m_xMainUpper;
        float m_yMainUpper;
        float m_xSmall;
        float m_ySmall;
        boolean isHalfSmallLabel=false;
    };
    final int horzX(int minX,int preferX,int mainTextWidth,int secondTextPos)
    {
        if(preferX+mainTextWidth+DELIM<=secondTextPos)
            return preferX;
        return st.max(minX, secondTextPos-mainTextWidth-DELIM);
    }
    final void buildCache()
    {
        if(txtMain==null&&bmp==null||JbKbdView.inst==null)
            return;
        m_c = new DrwCache();
//      Rect rb = canvas.getClipBounds();
        Paint p1 = draw.paint().main;
        if(bmp!=null)
        {
// Рисуем просто картинку, без текста (по центру)
            if(m_bPreview)
            {
                m_c.m_xMainLower = getWidth()/2-bmp.getWidth()/2;
                m_c.m_yMainLower = getHeight()/2-bmp.getHeight()/2;
            }
            else
            {
                m_c.m_xMainLower = rb.width()/2-bmp.getWidth()/2;
                m_c.m_yMainLower = rb.height()/2-bmp.getHeight()/2;
            }
            return;
        }
        if(m_bPreview)
        {
//Рисуем просмотр     
            if(txtMain!=null)
            {
                p1 = JbKbdView.inst.m_tpPreview;
                float mw = p1.measureText(txtMain);
                m_c.m_yMainLower = getHeight()/2+(0-p1.ascent())/2;
                m_c.m_xMainLower = getWidth()/2-mw/2;
            }
            return;
        }
        if(m_bSmallLabel)
        {
            p1 = draw.paint().second;
        }
//Если текст длинее 1 символа - считаем меткой и рисуем с помощью JbKbdView.m_tpLabel 
        else if(txtMain.length()>1)
        {
            p1 = draw.paint().label;
        }
//canvas сдвинут к середине, вернём его в позицию 0,0           
        Paint p2 = draw.paint().second;
        if(txtMain.length()==1||m_bMultiCode)
        {
            m_c.mainLower = txtMain.toLowerCase();
            m_c.mainUpper = txtMain.toUpperCase();
        }
        else
        {
            m_c.mainLower = txtMain;
            m_c.mainUpper = txtMain;
        }
        int a1 = (int) (0-p1.ascent());
        int a2 = (int) (0-p2.ascent());
        int d1 = (int) p1.descent();
        int d2 = (int) p2.descent();
        int w1,h1=d1+a1,w2,h2;
        if(bmpSmall!=null)
        {
            h2 = bmpSmall.getHeight();
            w2 = bmpSmall.getWidth();
        }
        else if(txtSmall!=null)
        {
            h2 = a2+d2;
            w2 = (int) p2.measureText(txtSmall);
        }
        else
        {
            h2 = w2 = 0;
        }
        w1 = (int) p1.measureText(m_c.mainLower);
        if(w1>rb.width()-4&&m_c.mainLower.length()>1)
        {
            m_c.isHalfSmallLabel = true;
            p1 = draw.paint().halfLabel;
            w1 = (int) p1.measureText(m_c.mainLower);
        }
        int fh = draw.paint().padding.top+draw.paint().padding.left+h1+h2+DELIM; // Полная высота
        if(fh>rb.height())
        {
            // Изображение основного текста и доп. символов не умещается в высоту
            int x2 = rb.width()-draw.paint().padding.right-w2;
            if(bmpSmall!=null)
            {
                m_c.m_xSmall = x2;
                m_c.m_ySmall = GAP+3;
            }
            else if(txtSmall!=null)
            {
                m_c.m_xSmall = x2;
                m_c.m_ySmall = GAP+a2;
            }
            if(txtMain!=null)
            {
                m_c.m_xMainLower = horzX(draw.paint().padding.left,rb.width()/2-w1/2,w1,rb.width()-draw.paint().padding.right-w2);
                m_c.m_yMainLower = rb.height()-draw.paint().padding.bottom-d1;
                if(m_c.mainUpper.compareTo(m_c.mainLower)==0)
                {
                    m_c.m_xMainUpper = m_c.m_xMainLower;
                }
                else
                {
                    w1 = (int) p1.measureText(m_c.mainUpper);
                    m_c.m_xMainUpper = horzX(draw.paint().padding.left,rb.width()/2-w1/2,w1,rb.width()-draw.paint().padding.right-w2);
                }
                m_c.m_yMainUpper = m_c.m_yMainLower;
            }
        }
        else
        {
            // Всё изображение умещается
            if(bmpSmall!=null)
            {
                m_c.m_xSmall = rb.width()/2-w2/2;
                m_c.m_ySmall = draw.paint().padding.top;
            }
            else if(txtSmall!=null)
            {
                m_c.m_xSmall = rb.width()/2-w2/2;
                m_c.m_ySmall = draw.paint().padding.top+a2;
            }
            int y = h2+DELIM+a1;
            int dy = (rb.height()-draw.paint().padding.bottom-y)/2;
            if(dy<4)
                dy = 0;
            m_c.m_xMainLower = rb.width()/2-w1/2;
            m_c.m_yMainLower = y+dy;
            m_c.m_yMainUpper = m_c.m_yMainLower;
            if(m_c.mainUpper.compareToIgnoreCase(m_c.mainLower)!=0)
            {
                m_c.m_xMainUpper = rb.width()/2-p1.measureText(m_c.mainUpper)/2;
            }
            else
            {
                m_c.m_xMainUpper = m_c.m_xMainLower;
            }
        }
    }
    @Override
    public void draw(Canvas canvas, Paint paint)
    {
        if(m_c==null&&!m_bPreview)
            buildCache();
        if(m_c==null&&!m_bPreview||txtMain==null&&bmp==null||JbKbdView.inst==null)
            return;
        Paint p1 = draw.paint().main;
        if(bmp!=null)
        {
// Рисуем просто картинку, без текста (по центру)
            if(m_bPreview)
                canvas.drawBitmap(bmp, getWidth()/2-bmp.getWidth()/2,getHeight()/2-bmp.getHeight()/2, draw.paint().getBitmapPaint(this));
            else
            {
                canvas.translate(0-rb.width()/2, 0-rb.height()/2);
                drawFuncBackground(canvas);
                canvas.drawBitmap(bmp, m_c.m_xMainLower,m_c.m_yMainLower, draw.paint().getBitmapPaint(this));
            }
            return;
        }
        if(m_bPreview)
        {
            String text = m_bLongPreview?txtSmall:txtMain;
//            st.paint().previewBack.draw(canvas);
            if(text!=null)
            {
                p1 = JbKbdView.inst.m_tpPreview;
                p1.setColor(ServiceJbKbd.inst.m_popup_color_text);
                if(text.length()>1)
                {
//                    p1.setTextSize(st.kv().m_PreviewTextSize/2);
                    p1.setTextSize((int) (st.kv().m_PreviewTextSize / 2 * st.popup_win_size));
                }
                else
                {
//                  p1.setTextSize(st.kv().m_PreviewTextSize);
                  p1.setTextSize((int) (st.kv().m_PreviewTextSize * st.popup_win_size));
                }
                Rect bb = new Rect();
                p1.getTextBounds(text, 0, text.length(), bb);
                float x = getWidth()/2-bb.width()/2;
                float y = getHeight()/2-bb.height()/2;
// рисует text в окне нажатых клавиш
                canvas.drawText(text, x, y+bb.height(), p1);
            }
            return;
        }
//Если текст длинее 1 символа - считаем меткой и рисуем с помощью JbKbdView.m_tpLabel 
        boolean bUp = JbKbdView.inst.isUpperCase(); 
        if(m_c.isHalfSmallLabel)
            p1 = draw.paint().halfLabel;
        if(m_bSmallLabel)
        {
            bUp = false;
            if(!m_c.isHalfSmallLabel)
                p1 = draw.paint().second;
        }
        else if(txtMain.length()>1)
        {
            if(!m_bMultiCode)
                bUp = false;
            if(!m_c.isHalfSmallLabel)
                p1 = draw.paint().label;
        }
        useTextColor(p1,false);
//canvas сдвинут к середине, вернём его в позицию 0,0           
        canvas.translate(0-rb.width()/2, 0-rb.height()/2);
// !!!
        drawFuncBackground(canvas);
        Paint p2 = draw.paint().second;
        useTextColor(p2,true);
        if(bmp!=null)
        {
            canvas.drawBitmap(bmp, m_c.m_xMainLower, m_c.m_yMainLower, draw.paint().getBitmapPaint(this));
        }
        if(bmpSmall!=null)
            canvas.drawBitmap(bmpSmall, m_c.m_xSmall, m_c.m_ySmall, draw.paint().getBitmapPaint(this,true));
        else if(txtSmall!=null)
        {
            canvas.drawText(txtSmall,m_c.m_xSmall, m_c.m_ySmall, p2);
        }
        if(bUp)
            canvas.drawText(m_c.mainUpper, m_c.m_xMainUpper, m_c.m_yMainUpper, p1);
        else
            canvas.drawText(m_c.mainLower, m_c.m_xMainLower, m_c.m_yMainLower, p1);
    }
}

