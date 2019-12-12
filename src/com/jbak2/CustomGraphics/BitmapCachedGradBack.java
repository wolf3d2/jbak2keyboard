package com.jbak2.CustomGraphics;

import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.shapes.RectShape;

public class BitmapCachedGradBack extends GradBack
{
	/** стартовый цвет для чтения из нужных мест, для добавления в массив цветов*/
	public static int cols = 0;
	/** конечный цвет для чтения из нужных мест, для добавления в массив цветов*/
	public static int cole = 0;
    int m_cacheSize = 20;
    static Vector<Vector<BmpCacheEntry>> caches = new Vector<Vector<BmpCacheEntry>>();
    Vector<BmpCacheEntry> m_cache = new Vector<BitmapCachedGradBack.BmpCacheEntry>();
    BmpCacheEntry m_curEntry;
    public BitmapCachedGradBack()
    {
        super();
    }
    public BitmapCachedGradBack(Vector<BmpCacheEntry> cache)
    {
        super();
        m_cache = cache;
    }
    public BitmapCachedGradBack(int startColor, int endColor)
    {
        super(startColor, endColor);
    	
    }
    public BitmapCachedGradBack(boolean saveColor, int startColor, int endColor)
    {
        super(startColor, endColor);
    	if (saveColor) {
            cols = startColor;
        	cole = endColor;
    	}
    	
    }
    @Override
    public RectShape clone() throws CloneNotSupportedException 
    {
    	return copyProperties(new BitmapCachedGradBack(m_cache));
    }
    @Override
    protected void onResize(float width, float height) 
    {
        int w = (int)width,h = (int)height;
        m_curEntry = searchEntry((int)width, (int)height);
        if(m_curEntry!=null)
        {
            if(m_curEntry.isValid())
                return;
            m_cache.remove(m_curEntry);
        }
        super.onResize((int)width, (int)height);
        if(m_cache.size()==0)
            caches.add(m_cache);
        m_curEntry = new BmpCacheEntry();
        m_curEntry.w = w;
        m_curEntry.h = h;
        m_curEntry.bmpNormal = Bitmap.createBitmap(w, h,Config.ARGB_8888);
        boolean press = m_bPressed;
        boolean ch = m_bCheckable;
        m_bCheckable = false;
        m_bPressed = false;
        super.draw(new Canvas(m_curEntry.bmpNormal), null);
        m_bPressed = true;
        m_curEntry.bmpPress = Bitmap.createBitmap(w, h,Config.ARGB_8888);
        super.draw(new Canvas(m_curEntry.bmpPress), null);
        if(m_cache.size()==m_cacheSize)
            m_cache.remove(0);
        m_cache.add(m_curEntry);
        m_bPressed = press;
        m_bCheckable = ch;
//        try {
//        	File f = new File(Environment.getExternalStorageDirectory()+"/test"+System.currentTimeMillis()+".jpg");
//        	f.createNewFile();
//			boolean ok = m_curEntry.bmpNormal.compress(CompressFormat.JPEG, 100, new FileOutputStream(f));
//			if(!ok||!f.exists())
//			{
//				long aa = 1;
//				long bb = aa;
//			}
//		} catch (Throwable e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    }
    @Override
    public void draw(Canvas canvas, Paint paint) 
    {
    	if(m_curEntry==null)
    		return;
        if(!m_curEntry.isValid())
        {
            onResize(m_curEntry.w, m_curEntry.h);
        }
        Bitmap bmp = m_bPressed?m_curEntry.bmpPress:m_curEntry.bmpNormal;
        canvas.drawBitmap(bmp,0,0,null);
        if(m_bCheckable||m_bChecked)
        	setIndicatorDrawKey(canvas,m_bChecked, m_rect);
    };
    public BmpCacheEntry searchEntry(int width,int height)
    {
        for(BmpCacheEntry ce:m_cache)
        {
            if(ce.w==width&&ce.h==height)
                return ce;
        }
        return null;
    }
    public BitmapCachedGradBack setCacheSize(int size)
    {
        m_cacheSize = size;
        return this;
    }
    public void recycle()
    {
        for(BmpCacheEntry ce:m_cache)
        {
            ce.recycle();
        }
        m_cache.clear();
        caches.remove(m_cache);
    }
    public static class BmpCacheEntry
    {
        int w;
        int h;
        Bitmap bmpNormal;
        Bitmap bmpPress;
        void recycle()
        {
            if(bmpNormal!=null&&!bmpNormal.isRecycled())
                bmpNormal.recycle();
            if(bmpPress!=null&&!bmpPress.isRecycled())
                bmpPress.recycle();
        }
        boolean isValid()
        {
            return bmpNormal!=null&&!bmpNormal.isRecycled()&&bmpPress!=null&&!bmpPress.isRecycled();
        }
    }
    public static void clearAllCache()
    {
        for(Vector<BmpCacheEntry> cache:caches)
        {
            for(BmpCacheEntry be:cache)
            {
                be.recycle();
            }
            cache.clear();
        }
        caches.clear();
        System.gc();
    }
}
