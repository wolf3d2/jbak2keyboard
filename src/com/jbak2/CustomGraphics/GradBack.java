package com.jbak2.CustomGraphics;

import java.util.Vector;

import com.jbak2.JbakKeyboard.IKeyboard.KbdDesign;
import com.jbak2.JbakKeyboard.st.IntEntry;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

/** Класс для рисования прямоугольников со скругленными углами, залитых градиентом или цветом */
public class GradBack extends RectShape
{
	public static final int DEFAULT_CORNER_X = 4;
	public static final int DEFAULT_CORNER_Y = 4;
	public static final int DEFAULT_GAP = 2;
	public static final int DEFAULT_COLOR = 12345678;
	public static final int GRADIENT_TYPE_LINEAR = 0;
	public static final int GRADIENT_TYPE_SWEEP = 1;
	RectF m_rect = new RectF();
	Paint m_ptFill;
	Paint m_ptFillPressed;
	/** цвет выключенной точки для тега isSticky */
	Paint m_ptFillCheckable;
	/** цвет включенной точки для тега isSticky */
	Paint m_ptFillChecked;
	GradBack m_dependentBack;
/** Цвет начала градиента */	
	int m_clrStart;
/** Цвет конца градиента */	
	int m_clrEnd;
/** Отступ краёв фона от прямоугольника, на котором фон отрисовывается */	
	public int m_gap = DEFAULT_GAP;
/** Радиус скругления прямоугольника в пикселях по X*/	
	public int m_cornerX = DEFAULT_CORNER_X;
/** Радиус скругления прямоугольника в пикселях по Y*/	
	public int m_cornerY = DEFAULT_CORNER_Y;
	boolean m_bDrawPressedBack = true;
/** Тип градиента, одна из констант GRADIENT_TYPE */	
	public int m_gradType = GRADIENT_TYPE_LINEAR;
    public int shadowColor = 0x88888888;
/** Фон обводки. Если не null - рисуется под основным фоном, и в этом случае будет рисоваться его тень*/    
    GradBack m_stroke=null;
    GradBack m_pressed = null;
/** Толщина обводки*/    
    int m_strokeSize=1;
    boolean m_bUseCache = true;
    boolean m_bCheckable = false;
    boolean m_bPressed = false;
    boolean m_bChecked = false;
    public static ColorFilter m_pressFilter = new PorterDuffColorFilter(0xff888888, PorterDuff.Mode.MULTIPLY);
    public static class PaintEntry
    {
        public PaintEntry(float width,float height,Paint paint)
        {
            w = width;
            h = height;
            pt = paint;
        }
        float w;
        float h;
        Paint pt;
    }
    Vector<PaintEntry>m_arCachePaints = new Vector<GradBack.PaintEntry>();
/** Пустой конструктор*/	
	public GradBack()
	{
		m_ptFillPressed = newColorPaint(0x44ffffff);
		m_ptFillChecked = newColorPaint(Color.GREEN);
		m_ptFillCheckable = newColorPaint(Color.DKGRAY);
	}
	protected GradBack copyProperties(GradBack gb)
	{
		gb.m_clrStart = m_clrStart;
		gb.m_clrEnd = m_clrEnd;
	    gb.setCorners(m_cornerX, m_cornerY);
	    gb.m_gap = m_gap;
	    gb.m_gradType = m_gradType;
        gb.m_bDrawPressedBack = m_bDrawPressedBack;
        try{
	    if(m_stroke!=null)
	        gb.m_stroke = (GradBack)m_stroke.clone();
	    if(m_pressed!=null)
	        gb.m_pressed = (GradBack)m_pressed.clone();
        }
        catch (Throwable e) {
		}
	    return gb;
	}
	@Override
	public RectShape clone() throws CloneNotSupportedException {
	    GradBack gb = new GradBack(m_clrStart, m_clrEnd);
	    return copyProperties(gb);
	}
/** Конструктор, задающий цвета градиента
 * @param startColor Начальный цвет градиента
 * @param endColor Конечный цвет градиента */
	public GradBack (int startColor,int endColor)
	{
		this();
		set(startColor,endColor);
	}
/** Установка начального и конечного цветов градиента  
 * @param startColor Начальный цвет градиента
 * @param endColor Конечный цвет градиента. Если равен {@link #DEFAULT_COLOR} 
 * 		  - то фон заполняется цветом startColor*/
	public GradBack set(int startColor,int endColor)
	{
		m_clrStart = startColor;
		m_clrEnd = endColor;
		return this;
	}
/** Установка отступа краёв фона от прямоугольника, на котором фон отрисовывается
 * @param gap Значение отступа в пикселях
 * @return Возвращает текущий объект */	
	public GradBack setGap(int gap)
	{
		m_gap = gap;
		return this;
	}
/** Установка типа градиента 
*@param gradType Тип градиента, одна из констант GRADIENT_TYPE
*@return Возвращает текущий объект */
	public GradBack setGradType(int gradType)
	{
		this.m_gradType = gradType;
		return this;
	}
	public GradBack setDrawPressedBackground(boolean bDrawPressed)
	{
	    m_bDrawPressedBack = bDrawPressed;
	    return this;
	}
    public GradBack setPressedGradBack(GradBack pressed)
    {
        m_pressed = pressed;
        return this;
    }
/** Устанавливает радиус скругления углов 
*@param cx Радиус скругления по оси X
*@param cy Радиус скругления по оси Y
*@return Возвращает текущий объект */
	public GradBack setCorners(int cx,int cy)
	{
		m_cornerX = cx;
		m_cornerY = cy;
		return this;
	}
/** Возвращает новый объект для отрисовки с предустановленными параметрами */
	protected Paint newPaint()
	{
		Paint ret = new Paint();
		ret.setDither(true);
		ret.setAntiAlias(true);
		return ret;
	}
/** Возвращает новый объект для заливки цветом */
	protected Paint newColorPaint(int color)
	{
		Paint ret = newPaint();
        ret.setStyle(Style.FILL);
		ret.setColor(color);
		return ret;
	}
/** Возвращает объект {@link Drawable}, который содержит текущий объект*/	
	public Drawable getDrawable()
	{
		return new ShapeDrawable(this);
	}
/** Возвращает объект {@link CustomButtonDrawable}, который содержит текущий объект*/   
	public CustomButtonDrawable getStateDrawable()
	{
		return new CustomButtonDrawable(this);
	}
	protected Paint makeBackground(float width, float height)
	{
        if(m_clrEnd==DEFAULT_COLOR)
        {
            m_clrEnd = m_clrStart;
            //return newColorPaint(m_clrStart);
        }
        Paint pt = newPaint();
        if(m_gradType==GRADIENT_TYPE_SWEEP)
        {
            pt.setShader(new SweepGradient(width, height, m_clrStart, m_clrEnd));
        }
        else
        {
            pt.setShader(new LinearGradient(0, 0, 0, height, m_clrStart, m_clrEnd, TileMode.CLAMP));
        }
        // просто для примера радиального градиента. Как по мне, так не ахти
        //pt.setShader(new RadialGradient(width/2, height/2, height, new int[] { m_clrStart, m_clrEnd}, null, TileMode.CLAMP));
        pt.setStyle(Style.FILL);
        if(shadowColor!=DEFAULT_COLOR&&m_stroke==null)
            pt.setShadowLayer(2, 2, 2, shadowColor);
        return pt;
	}
/** Действия по установке размеров текущего объекта */	
	@Override
	protected void onResize(float width, float height) 
	{
	    if(m_dependentBack!=null)
	        m_dependentBack.resize(width, height);
        if(m_stroke!=null)
            m_stroke.resize(width, height);
        if(m_pressed!=null)
            m_pressed.resize(width, height);
	    if(m_ptFill!=null&&width==m_rect.width()+m_gap*2&&height==m_rect.height()+m_gap*2)
	        return;
        m_rect.set(m_gap, m_gap, width-m_gap, height-m_gap);
        m_ptFill = null;
	    if(m_bUseCache)
	    {
    	    for(PaintEntry pe:m_arCachePaints)
    	    {
    	        if(pe.w==width&&pe.h==height)
    	        {
    	            m_ptFill = pe.pt;
    	            break;
    	        }
    	    }
	    }
	    if(m_ptFill==null)
	    {
	        m_ptFill = makeBackground(width, height);
	        if(m_bUseCache)
	        {
	            m_arCachePaints.add(new PaintEntry(width, height, m_ptFill));
	        }
	    }
		super.onResize(width, height);
	};
/** Отрисовка */	
	@Override
	public void draw(Canvas canvas, Paint paint)
	{
	    if(m_stroke!=null)
	    {
	        m_stroke.draw(canvas, null);
	    }
	    if(m_ptFill!=null)
	    {
	        if(m_bPressed&&m_pressed!=null)
	        {
	            m_pressed.draw(canvas, null);
	        }
	        else
	        {
    	        m_ptFill.setColorFilter(m_bDrawPressedBack&&m_bPressed?m_pressFilter:null);
    	        canvas.drawRoundRect(m_rect, m_cornerX, m_cornerY, m_ptFill);
	        }
	    }
//		if(m_bDrawPressedBack&&hasState(android.R.attr.state_pressed))
//			canvas.drawRoundRect(m_rect, m_cornerX, m_cornerY, m_ptFillPressed);
		if(m_bCheckable)
			setDrawKeyIndicator(canvas,m_bChecked, m_rect);
	}
/** Функция отрисовки точки на клавише с тегом isSticky, для состояний checked и checkable.
 * Вызывается только при наличии этих состояний
*@param canvas Canvas для отрисовки 
*@param bCheck true - помечено, false - нет 
*@param rect Прямоугольник, на котором производится отрисовка */
	public void setDrawKeyIndicator(Canvas canvas,boolean bCheck,RectF rect)
	{
        RectF rr = new RectF(m_rect.left+4, m_rect.top+4, m_rect.left+16, m_rect.top+16);
        canvas.drawArc(rr, 0, 360, false, bCheck?m_ptFillChecked:m_ptFillCheckable);
	}
/** Обработчик изменения состояния */	
	public void changeState(int []states)
	{
	    if(m_dependentBack!=null)
	        m_dependentBack.changeState(states);
        m_bCheckable = hasState(android.R.attr.state_checkable, states);
	    m_bChecked = hasState(android.R.attr.state_checked, states);
        m_bPressed = hasState(android.R.attr.state_pressed, states);
	}
    public GradBack setShadowColor(int shadow)
    {
        shadowColor = shadow;
        return this;
    }
/** Устанавливает обводку stroke, в виде еще одного объекта {@link GradBack}
 * Другими словами, это первоначальный фон клавиши
 * Необходимо правильно задать отступ (gap) для нового объекта, с учетом того, что сверху будет нарисован текущий объект
*@param stroke Объект для рисования обводки 
*@return Текущий объект
 */
    public GradBack setStroke(GradBack stroke)
    {
        m_stroke = stroke;
        return this;
    }
    public void setDependentback(GradBack gb)
    {
        m_dependentBack = gb;
    }
/** Проверяет наличие статуса s в массиве текущих статусов 
*@param s Проверяемый статус, одна из констант <b>android.R.attr.state_</b>
*@return true - статус есть, false - нет */
	public boolean hasState(int s,int []states)
	{
		for(int stat:states)
		{
			if(stat==s)
				return true;
		}
		return false;
	}
}
