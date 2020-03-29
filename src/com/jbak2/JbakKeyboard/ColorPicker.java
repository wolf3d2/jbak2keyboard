package com.jbak2.JbakKeyboard;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

/**Класс для визуального подбора номера цвета <br> <br> 
 * 
 * для закрытия окна диалога вставить в onBackPressed  и в onCreate  <br>
 * (а то при смене ориентации прекратит вызываться, <br> 
 пока не закроешь вызывающую активность) <br>
вызывающей активности проверку: <br> <br>

	if (ColorPicker.inst!=null){
		GlobDialog.inst.finish();
		return;
	}

 * */
@SuppressLint("NewApi")
public class ColorPicker extends View implements View.OnTouchListener
{
	public static final int ID_BTN_CLOSE = 1001;
	public static final int ID_COLOR_PICKER = 1002;
	public static final int ID_TV_EXAMPLE_COLOR = 1003;
	public static final int ID_TV_COLOR_CODE_TEXT = 1004;
	/** надпись "прозрачность" */
	public static final int ID_TV_TRANSPARENCY = 1005;
	public static final int ID_SEEKBAR_ALPHA = 1006;
	public static final int ID_BTN_SELECT = 1007;
	public static final int ID_BTN_TYPE_PICKER = 1008;
	/** правый вертикальный лайот */
	public static final int ID_LL_RIGHT = 1009;
	public static final int ID_SEEKBAR_R = 1010;
	public static final int ID_SEEKBAR_G = 1011;
	public static final int ID_SEEKBAR_B = 1012;
	public static final int ID_TV_R = 1013;
	public static final int ID_TV_G = 1014;
	public static final int ID_TV_B = 1015;
	public static final int ID_SPINNER = 1016;
	// id 1009- заняты!

	Integer[] ar_color_example =
		{
				0xffffffff,
				0xff000000,
				0xffF08080,
				0xffFA8072,
				0xffFFA07A,
				0xffDC143C,
				0xffFF0000,
				0xff8B0000,
				0xffFFC0CB,
				0xffFF69B4,
				0xffFF1493,
				0xffDB7093,
				0xffFF7F50,
				0xffFF6347,
				0xffFFA500,
				0xffFFFF00,
				0xffF0E68C,
				0xff4B0082,
				0xff6A5ACD,
				0xff483D8B,
				0xffBC8F8F,
				0xffF4A460,
				0xffDAA520,
				0xffB8860B,
				0xffCD853F,
				0xffD2691E,
				0xff8B4513,
				0xffA0522D,
				0xff800000,
				0xffC0C0C0,
				0xffADFF2F,
				//0xff000000,
				0xff32CD32,
				0xff98FB98,
				0xff90EE90,
				0xff00FA9A,
				0xff2E8B57,
				0xff228B22,
				0xff006400,
				0xff6B8E23,
				0xff808000,
				0xff556B2F,
				0xff66CDAA,
				0xff20B2AA,
				0xff008B8B,
				0xff008080,
				0xff00FFFF,
				0xffAFEEEE,
				0xff7FFFD4,
				0xff7B68EE,
				0xff191970,
				0xff000080,
				0xffD3D3D3,
				0xff778899,
				0xff2F4F4F,
		};
	SharedPreferences pref = null;
	/** флаг, что меняли тип окна <br>
	 * если >1, то при смене окно нужно закрывать, иначе ошибка */
	static int type_picker_window = 0;
	static boolean fl_color_picker = false;
	static int col_a = 255;
	static int col_r = 0;
	static int col_g = 0;
	static int col_b = 0;
	EditText m_et = null;
	SeekBar sb_r = null;
	SeekBar sb_g = null;
	SeekBar sb_b = null;
	SeekBar sb_a = null;
	/** код цвета */
	TextView tv_example = null;
	/** сам цвет */
	TextView tv_example_col = null;
	int height = 0;
	/** выбранный цвет из круга */
	static int rgb = 0xffffffff;
	int lastrgb =0;
	int lastX = 0;
	int lastY = 0;
	int xxx = 0;
	int yyy = 0;
	
	View m_view;
	Context m_c;
	int[] mColors = new int[] {
				Color.YELLOW, 
				Color.BLACK, 
				Color.BLUE, 
				Color.CYAN, 
				Color.DKGRAY, 
				Color.GRAY, 
				Color.GREEN, 
				Color.LTGRAY, 
				Color.MAGENTA, 
				Color.RED, 
				Color.WHITE, 
				Color.YELLOW 
			};
	WindowManager wm;
	private float				rad_1 = 0;
	private float				cx;
	private float				cy;
	private int					size;
	static ColorPicker inst = null;
	private Paint p_color	= new Paint(Paint.ANTI_ALIAS_FLAG);
	
	public ColorPicker(Context context) {
		this(context, null);
	}

	public ColorPicker(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ColorPicker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) 
	{
		type_picker_window = 0;
		setOnTouchListener(this);
		setDrawingCacheEnabled(true);

//		setExampleText();
		decodeColor(rgb);
		inst = this;
	}
	

	@Override
	public boolean performClick() {
		return super.performClick();
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) 
	{
		lastrgb = rgb;
		switch (event.getAction()) {
		// Тут мы определяем, что сделал юзер
//		case MotionEvent.ACTION_UP:
//			v.performClick();
		case MotionEvent.ACTION_DOWN:
//			float a = Math.abs(event.getX() - cx);
//			float b = Math.abs(event.getY() - cy);
			rgb = getDrawingCache().getPixel((int) event.getX(), (int) event.getY());
            destroyDrawingCache();
            break;

		case MotionEvent.ACTION_MOVE:
//			if(event.getX()<this.getX()&&event.getX() > this.getX()+this.getWidth())
//				xxx = lastX;
//			if (event.getY()<this.getY()&&event.getY()>this.getY()+this.getHeight())
//				yyy = lastY;
			try 
			{
			rgb = getDrawingCache().getPixel((int) event.getX(), (int)event.getY());
            destroyDrawingCache();
			} catch(Throwable e)
	        {
				rgb = lastrgb;
	        }

			break;

		}
//		rgb = 0xff0000;
		decodeColor(rgb);
		if (sb_a!=null) {
			sb_a.setProgress(col_a);
		}
		setExampleText();
		invalidate();
		return true;
	}
	@SuppressLint("NewApi")
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int mWidth = measure(widthMeasureSpec);
		int mHeight = measure(heightMeasureSpec);
		size = Math.min(mWidth, mHeight);
		// размер холста
//		setMeasuredDimension(size, size);
		setMeasuredDimension(size, size);

		// Вычислили размер доступной области, определили что меньше
		// и установили размер нашей View в виде квадрата со стороной в 
		// высоту или ширину экрана в зависимости от ориентации.
		// Вместо Math.min как вариант можно использовать getConfiguration,
		// величину size можно умножать на какие-нибудь коэффициенты, 
		// задавая размер View относительно размера экрана. Например так:
//		switch (orient) {
//		case Configuration.ORIENTATION_PORTRAIT:
//			size = (int) (measureHeight * port);
//
//			break;
//		case Configuration.ORIENTATION_LANDSCAPE:
//			size = (int) (measureHeight * land);
//			break;
//		}
		
		calculateSizes();
		// И запустили метод для расчетов всяких наших размеров
	}

	private int measure(int measureSpec) {
		int result = 0;
		int specMoge = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
// задаём размер окна с картинками
		if (specMoge == MeasureSpec.UNSPECIFIED) 
			result = 180;
		else 
			result = specSize;
//		result = 120;
		return result;
	}

	private void calculateSizes() 
	{
		cx = size * 0.5f;
		cy = cx;
		// вычисляем радиус
		rad_1 = size * 0.44f;
//		rad_1 = size * 0.30f;
		// 0.44 – ну понравилось мне это число. Можно подобрать свое
		// Теперь кисть:
		p_color.setStrokeWidth(size * 0.08f);
		// То есть ставим толщину линии 0.08 от size
		// опять же можно экспериментировать
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas c) {
		super.onDraw(c);
		Shader s=null;
		if (st.isLandscape(m_c)){
//			s = new LinearGradient(cx, cy, mColors, null,
//					false, CycleMethod.REPEAT, new Stop(0f, Color.RED),new Stop(1f, Color.BLACK);
			c.drawColor(Color.BLUE);
			s = new LinearGradient(0,0, c.getWidth(), 50,
					mColors, null,
				      Shader.TileMode.REPEAT);
			if (s!=null)
				p_color.setShader(s);
			int w_canc = c.getWidth();
			c.drawRect(0, 0, c.getWidth(), 100, p_color);
		} else {
			c.drawColor(Color.BLUE);
			s = new SweepGradient(cx, cy, mColors, null);
			if (s!=null)
				p_color.setShader(s);
			c.drawCircle(cx, cy, rad_1, p_color);
		}
		if (s!=null)
			p_color.setShader(s);

// рисуем квадрат с заливкой
//		LinearGradient shader = new LinearGradient(0, 0, 100, 20,
//			      new int[] { Color.BLACK, Color.BLUE, Color.CYAN, Color.DKGRAY, Color.GRAY, Color.GREEN, Color.LTGRAY, Color.MAGENTA, Color.RED, Color.TRANSPARENT, Color.WHITE, Color.YELLOW }, null,
//			      Shader.TileMode.MIRROR);
//		LinearGradient shader = new LinearGradient(0, 0, 100, 20,
//				mColors, null, Shader.TileMode.MIRROR);
//		p_color.setShader(shader);
//		c.drawRect(10, 10, 50, 500, p_color);

		// Ну а тут будем рисовать
		// Для начала проверим, что все работает – нарисуем просто фон
		//invalidate() будем потом вызывать
	}
    public void show(Context c, EditText et)
    {
    	if (fl_color_picker)
    		return;
    	if (m_view!=null){
    		wm.removeView(m_view);
    		m_view = null;
    	}
    	m_et = et;
    	//rgb = 0;
    	if (m_et!=null)
    		rgb = st.str2hex(et.getText().toString(),16);
    	m_c = c;
    	st.hidekbd();
    	wm = (WindowManager) c.getSystemService(Service.WINDOW_SERVICE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.height = st.getDisplayHeight(null)-50;
        lp.width = st.getDisplayWidth(null)-50;
//        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        			|WindowManager.LayoutParams.FLAG_FULLSCREEN
                    |WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    |WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                    ;
        lp.gravity = Gravity.TOP;
        lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
        lp.x = 0;
//        lp.y = yPos;
        lp.y = (st.getDisplayHeight(null)-50)/2;
        sb_r = null;
        sb_g = null;
        sb_b = null;
        sb_a = null;
		if (!st.color_picker_type){
	        if (st.isLandscape(c))
				m_view = createCircleLandscape(c);
			else
				m_view = createCirclePortrait(c);
		} else
			m_view = createLinear(c);
// задаём высоту окна (разобраться)		
//		m_view.measure(0, 0);
//		int hh = m_view.getMeasuredHeight();
//      lp.height = hh;
		wm.addView(m_view, lp);
		fl_color_picker = true;
    }
    View.OnClickListener m_clkListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
            case ID_BTN_TYPE_PICKER:
            	st.color_picker_type = !st.color_picker_type;
                pref = st.pref(m_c);
            	if (pref!=null) {
            		pref.edit().putBoolean(st.PREF_KEY_TYPE_COLOR_PICKER, st.color_picker_type).commit();
            	}
            	fl_color_picker = false;
            	type_picker_window++;
            	if (type_picker_window > 1) {
            		st.toast(m_c, R.string.picker_close);
            		finish();
            		return;
            	}
            	show(m_c,m_et);
                return;
            case ID_BTN_CLOSE:
            	finish();
                return;
            case ID_TV_COLOR_CODE_TEXT:
				st.copyText(m_c, ((TextView)v).getText().toString());
                return;
            case ID_BTN_SELECT:
            	if(m_et!=null){
            		m_et.setText(st.STR_NULL+String.format(st.STR_16FORMAT,rgb));
            		finish();
            	}
                return;
            }
        }
    };
    public void finish()
    {
    	if (m_view!=null){
    		wm.removeView(m_view);
    		m_view = null;
    	}
    	fl_color_picker = false;
    	inst = null;
    }
    public View createCircleLandscape(Context c)
    {
        RelativeLayout rl = new RelativeLayout(m_c);
        rl.setBackgroundResource(android.R.drawable.dialog_frame);

        
//		ll.setOrientation(LinearLayout.VERTICAL);
        rl.setPadding(20, 20, 20, 20);

		Button bclose = new Button(m_c);
		bclose.setText(" X ");
		// не забыть поставить в вызывающей активити
		// в onbackPressed - st.fl_color_picker = false;
		bclose.setOnClickListener(m_clkListener);
		bclose.setId(ID_BTN_CLOSE);
        RelativeLayout.LayoutParams rlclose = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.WRAP_CONTENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
        rlclose.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		rlclose.setMargins(5, 10, 5, 5);
		bclose.setLayoutParams(rlclose);
		rl.addView(bclose);
        
        RelativeLayout.LayoutParams rpcanvas = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.WRAP_CONTENT,
        		100
        		)        		;
        rpcanvas.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        rpcanvas.addRule(RelativeLayout.LEFT_OF, ID_BTN_CLOSE);
//        rpcanvas.gravity = Gravity.LEFT;
        rpcanvas.setMargins(10, 10, 5, 5);

//        Canvas cc = new Canvas();
//		cc.drawCircle(cx, cy, rad_1, p_color);
//        
//		LinearGradient shader = new LinearGradient(0, 0, 100, 20,
//			      new int[] { Color.BLACK, Color.BLUE, Color.CYAN, 
//			      Color.DKGRAY, Color.GRAY, Color.GREEN, 
//			      Color.LTGRAY, Color.MAGENTA, Color.RED, 
//			      Color.WHITE, Color.YELLOW }, 
//			      null, Shader.TileMode.MIRROR);
//		p_color.setShader(shader);
		this.setLayoutParams(rpcanvas);
		this.setId(ID_COLOR_PICKER);
		rl.addView(this);
		
        RelativeLayout.LayoutParams tvexamlecolpar = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.WRAP_CONTENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
        tvexamlecolpar.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        tvexamlecolpar.addRule(RelativeLayout.BELOW, ID_COLOR_PICKER);
        tvexamlecolpar.setMargins(10, 10, 5, 5);
        tv_example_col = new TextView(m_c);
        tv_example_col.setBackgroundResource(R.drawable.textview__frame_style);
        tv_example_col.setTextColor(rgb);
        tv_example_col.setId(ID_TV_EXAMPLE_COLOR);
        tv_example_col.setTextSize(30);
        tv_example_col.setLayoutParams(tvexamlecolpar);
        tv_example_col.setText(st.STR_SPACE+"▇"+st.STR_SPACE);
        rl.addView(tv_example_col);

        RelativeLayout.LayoutParams tvexamletxtpar = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.WRAP_CONTENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
        tvexamletxtpar.addRule(RelativeLayout.BELOW, ID_COLOR_PICKER);
        tvexamletxtpar.addRule(RelativeLayout.RIGHT_OF, ID_TV_EXAMPLE_COLOR);
        tvexamletxtpar.setMargins(10, 10, 5, 5);

        tv_example = new TextView(m_c);
        tv_example.setOnClickListener(m_clkListener);
        tv_example.setTextSize(30);
//        tv_example.setText();
//        	tv_example.setTextColor(setExampleText());
        tv_example.setId(ID_TV_COLOR_CODE_TEXT);
        tv_example.setLayoutParams(tvexamletxtpar);
        setExampleText();
        rl.addView(tv_example);

        RelativeLayout.LayoutParams tvsbpar = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.WRAP_CONTENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
        tvsbpar.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        tvsbpar.addRule(RelativeLayout.BELOW, ID_TV_EXAMPLE_COLOR);
        tvsbpar.setMargins(10, 0, 5, 0);
        TextView tvseek = new TextView(m_c);
        tvseek.setText(m_c.getString(R.string.transparency));
        tvseek.setId(ID_TV_TRANSPARENCY);
        tvseek.setLayoutParams(tvsbpar);
		rl.addView(tvseek);

        RelativeLayout.LayoutParams sbpar = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.MATCH_PARENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
        sbpar.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        sbpar.addRule(RelativeLayout.BELOW, ID_TV_TRANSPARENCY);
        sbpar.setMargins(10, 1, 5, 5);
        sb_a = new SeekBar(m_c);
        sb_a.setId(ID_SEEKBAR_ALPHA);
        sb_a.setMax(255);
        sb_a.setProgress(col_a);
        sb_a.setLayoutParams(sbpar);
        sb_a.setOnSeekBarChangeListener(m_seekbarChangeListener);
        rl.addView(sb_a);
        
        int height = st.getDisplayHeight(null); 
        RelativeLayout.LayoutParams bselpar = null;
        if (height>=400){
            bselpar = new RelativeLayout.LayoutParams(
            		300,
            		RelativeLayout.LayoutParams.WRAP_CONTENT
            		);
            bselpar.addRule(RelativeLayout.CENTER_HORIZONTAL);
        	bselpar.addRule(RelativeLayout.BELOW, ID_SEEKBAR_ALPHA);
            bselpar.setMargins(10, 10, 5, 5);
        } else {
            bselpar = new RelativeLayout.LayoutParams(
            		RelativeLayout.LayoutParams.WRAP_CONTENT,
            		RelativeLayout.LayoutParams.WRAP_CONTENT
            		);
            bselpar.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            bselpar.addRule(RelativeLayout.BELOW, ID_BTN_CLOSE);
            bselpar.setMargins(5, 10, 5, 5);
        }

        Button bsel = new Button(m_c);
        bsel.setText(m_c.getString(R.string.selection));
        bsel.setLayoutParams(bselpar);
        bsel.setOnClickListener(m_clkListener);
        bsel.setId(ID_BTN_SELECT);
        if (height<400)
        	bsel.setText("Ok");
        if (m_et == null)
        	bsel.setVisibility(View.GONE);
        rl.addView(bsel);

        RelativeLayout.LayoutParams tvinfpar = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.MATCH_PARENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
        tvinfpar.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        if (height>=400)
        	tvinfpar.addRule(RelativeLayout.BELOW, ID_BTN_SELECT);
        else
        	tvinfpar.addRule(RelativeLayout.BELOW, ID_SEEKBAR_ALPHA);
        tvinfpar.setMargins(10, 10, 5, 5);

        TextView tvinf = new TextView(m_c);
        tvinf.setText(m_c.getString(R.string.sc_inf_land));
        tvinf.setLayoutParams(tvinfpar);
        tvinf.setOnClickListener(m_clkListener);
        tvinf.setTextSize(12);
        tvinf.setId(1008);
        rl.addView(tvinf);

        height = rl.getHeight();
        return rl;
    }
    public View createCirclePortrait(Context c)
    {
        RelativeLayout rl = new RelativeLayout(m_c);
        rl.setBackgroundResource(android.R.drawable.dialog_frame);

        
//		ll.setOrientation(LinearLayout.VERTICAL);
        rl.setPadding(20, 20, 20, 20);

		Button bclose = new Button(m_c);
		bclose.setText(" X ");
		// не забыть поставить в вызывающей активити
		// в onbackPressed - st.fl_color_picker = false;
		bclose.setOnClickListener(m_clkListener);
		bclose.setId(ID_BTN_CLOSE);
        RelativeLayout.LayoutParams rlclose = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.WRAP_CONTENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
        rlclose.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		rlclose.setMargins(5, 10, 5, 5);
		bclose.setLayoutParams(rlclose);
		rl.addView(bclose);
        
		Button btype = new Button(m_c);
		btype.setText(" ― ");
		// не забыть поставить в вызывающей активити
		btype.setOnClickListener(m_clkListener);
		btype.setId(ID_BTN_TYPE_PICKER);
        RelativeLayout.LayoutParams rltype = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.WRAP_CONTENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
        rltype.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rltype.addRule(RelativeLayout.BELOW, ID_BTN_CLOSE);
        rltype.setMargins(5, 10, 5, 5);
        btype.setLayoutParams(rltype);
		rl.addView(btype);

		RelativeLayout.LayoutParams rpcanvas = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.WRAP_CONTENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
        rpcanvas.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        rpcanvas.addRule(RelativeLayout.LEFT_OF, ID_BTN_CLOSE);
//        rpcanvas.gravity = Gravity.LEFT;
        rpcanvas.setMargins(10, 10, 5, 5);

//        Canvas cc = new Canvas();
//		cc.drawCircle(cx, cy, rad_1, p_color);
//        
//		LinearGradient shader = new LinearGradient(0, 0, 100, 20,
//			      new int[] { Color.BLACK, Color.BLUE, Color.CYAN, 
//			      Color.DKGRAY, Color.GRAY, Color.GREEN, 
//			      Color.LTGRAY, Color.MAGENTA, Color.RED, 
//			      Color.WHITE, Color.YELLOW }, 
//			      null, Shader.TileMode.MIRROR);
//		p_color.setShader(shader);
		this.setLayoutParams(rpcanvas);
		this.setId(ID_COLOR_PICKER);
		rl.addView(this);
		
        RelativeLayout.LayoutParams tvexamlecolpar = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.WRAP_CONTENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
        tvexamlecolpar.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        tvexamlecolpar.addRule(RelativeLayout.BELOW, ID_COLOR_PICKER);
        tvexamlecolpar.setMargins(10, 10, 5, 5);
        tv_example_col = new TextView(m_c);
        tv_example_col.setBackgroundResource(R.drawable.textview__frame_style);
        tv_example_col.setTextColor(rgb);
        tv_example_col.setId(ID_TV_EXAMPLE_COLOR);
        tv_example_col.setTextSize(30);
        tv_example_col.setLayoutParams(tvexamlecolpar);
        tv_example_col.setText(st.STR_SPACE+"▇"+st.STR_SPACE);
        rl.addView(tv_example_col);

        RelativeLayout.LayoutParams tvexamletxtpar = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.WRAP_CONTENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
        tvexamletxtpar.addRule(RelativeLayout.BELOW, ID_COLOR_PICKER);
        tvexamletxtpar.addRule(RelativeLayout.RIGHT_OF, ID_TV_EXAMPLE_COLOR);
        tvexamletxtpar.setMargins(10, 10, 5, 5);

        tv_example = new TextView(m_c);
//        tv_example.setText();
//        	tv_example.setTextColor(setExampleText());
        tv_example.setId(ID_TV_COLOR_CODE_TEXT);
        tv_example.setOnClickListener(m_clkListener);
        tv_example.setTextSize(30);
        tv_example.setLayoutParams(tvexamletxtpar);
        setExampleText();
        rl.addView(tv_example);

        RelativeLayout.LayoutParams tvsbpar = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.WRAP_CONTENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
        tvsbpar.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        tvsbpar.addRule(RelativeLayout.BELOW, ID_TV_EXAMPLE_COLOR);
        tvsbpar.setMargins(10, 0, 5, 0);
        TextView tvseek = new TextView(m_c);
        tvseek.setText(m_c.getString(R.string.transparency));
        tvseek.setId(ID_TV_TRANSPARENCY);
        tvseek.setLayoutParams(tvsbpar);
		rl.addView(tvseek);

        RelativeLayout.LayoutParams sbpar = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.MATCH_PARENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
        sbpar.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        sbpar.addRule(RelativeLayout.BELOW, ID_TV_TRANSPARENCY);
        sbpar.setMargins(10, 1, 5, 5);
        sb_a = new SeekBar(m_c);
        sb_a.setId(ID_SEEKBAR_ALPHA);
        sb_a.setMax(255);
        sb_a.setProgress(col_a);
        sb_a.setLayoutParams(sbpar);
        sb_a.setOnSeekBarChangeListener(m_seekbarChangeListener);
        rl.addView(sb_a);
        
        int height = st.getDisplayHeight(null); 
        RelativeLayout.LayoutParams bselpar = null;
        if (height>=400){
            bselpar = new RelativeLayout.LayoutParams(
            		300,
            		RelativeLayout.LayoutParams.WRAP_CONTENT
            		);
            bselpar.addRule(RelativeLayout.CENTER_HORIZONTAL);
        	bselpar.addRule(RelativeLayout.BELOW, ID_SEEKBAR_ALPHA);
            bselpar.setMargins(10, 10, 5, 5);
        } else {
            bselpar = new RelativeLayout.LayoutParams(
            		RelativeLayout.LayoutParams.WRAP_CONTENT,
            		RelativeLayout.LayoutParams.WRAP_CONTENT
            		);
            bselpar.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            bselpar.addRule(RelativeLayout.BELOW, ID_BTN_CLOSE);
            bselpar.setMargins(5, 10, 5, 5);
        }

        Button bsel = new Button(m_c);
        bsel.setText(m_c.getString(R.string.selection));
        bsel.setLayoutParams(bselpar);
        bsel.setOnClickListener(m_clkListener);
        bsel.setId(ID_BTN_SELECT);
        if (height<400)
        	bsel.setText("Ok");
        if (m_et == null)
        	bsel.setVisibility(View.GONE);
        rl.addView(bsel);

        RelativeLayout.LayoutParams tvinfpar = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.MATCH_PARENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
        tvinfpar.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        if (height>=400)
        	tvinfpar.addRule(RelativeLayout.BELOW, ID_BTN_SELECT);
        else
        	tvinfpar.addRule(RelativeLayout.BELOW, ID_SEEKBAR_ALPHA);
        tvinfpar.setMargins(10, 10, 5, 5);

        TextView tvinf = new TextView(m_c);
        tvinf.setText(m_c.getString(R.string.sc_inf_port));
        tvinf.setLayoutParams(tvinfpar);
        tvinf.setOnClickListener(m_clkListener);
        tvinf.setTextSize(12);
        tvinf.setId(1008);
        rl.addView(tvinf);

        height = rl.getHeight();
        return rl;
    }
    public View createLinear(Context c)
    {
        RelativeLayout.LayoutParams rlp = null;
        RelativeLayout rl = new RelativeLayout(m_c);
        rl.setBackgroundResource(android.R.drawable.dialog_frame);
//		ll.setOrientation(LinearLayout.VERTICAL);
        rl.setPadding(20, 20, 20, 20);
        
// ***************************************		
// правая сторона
		rlp = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.WRAP_CONTENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT
        		);
		rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		
        LinearLayout llr = new LinearLayout(m_c);
        llr.setId(ID_LL_RIGHT);
        llr.setOrientation(LinearLayout.VERTICAL);
        llr.setLayoutParams(rlp);
        
		Button bclose = new Button(m_c);
		bclose.setText(" X ");
		// не забыть поставить в вызывающей активити
		// в onbackPressed - st.fl_color_picker = false;
		bclose.setOnClickListener(m_clkListener);
		bclose.setId(ID_BTN_CLOSE);
		rlp = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.WRAP_CONTENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
		//rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		rlp.setMargins(5, 10, 5, 5);
		bclose.setLayoutParams(rlp);
		llr.addView(bclose);
        
		Button btype = new Button(m_c);
		btype.setText(" ○ ");
		// не забыть поставить в вызывающей активити
		btype.setOnClickListener(m_clkListener);
		btype.setId(ID_BTN_TYPE_PICKER);
		rlp.setMargins(5, 10, 5, 5);
		llr.addView(btype);

		rlp = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.MATCH_PARENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
		rlp.setMargins(5, 10, 5, 5);
		Spinner sp = new Spinner(m_c);
		Adapt adapter = new Adapt(m_c,ar_color_example);
		sp.setLayoutParams(rlp);
		//sp.setBackgroundColor(0xffffffff);
		sp.setAdapter(adapter);
		sp.setId(ID_SPINNER);
		sp.setOnItemSelectedListener(m_itemSelection);
		sp.setSelection(0);
		llr.addView(sp);

		rl.addView(llr);

// ***************************************		
// левая сторона
		rlp = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.WRAP_CONTENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
        rlp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        //rlp.addRule(RelativeLayout.LEFT_OF, ID_LL_RIGHT);
        rlp.setMargins(10, 30, 5, 5);
        
        tv_example_col = new TextView(m_c);
        tv_example_col.setBackgroundResource(R.drawable.textview__frame_style);
        tv_example_col.setTextColor(rgb);
        tv_example_col.setId(ID_TV_EXAMPLE_COLOR);
        tv_example_col.setTextSize(60);
        tv_example_col.setLayoutParams(rlp);
        tv_example_col.setText(st.STR_SPACE+"▇"+st.STR_SPACE);
        rl.addView(tv_example_col);

        rlp = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.WRAP_CONTENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
        rlp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rlp.addRule(RelativeLayout.BELOW, ID_TV_EXAMPLE_COLOR);
        rlp.setMargins(10, 10, 5, 5);

        tv_example = new TextView(m_c);
//        tv_example.setText();
//        	tv_example.setTextColor(setExampleText());
        tv_example.setId(ID_TV_COLOR_CODE_TEXT);
        tv_example.setOnClickListener(m_clkListener);
        tv_example.setTextSize(30);
        tv_example.setLayoutParams(rlp);
        setExampleText();
        rl.addView(tv_example);

        rlp = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.WRAP_CONTENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
        rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        rlp.addRule(RelativeLayout.LEFT_OF, ID_LL_RIGHT);
        rlp.addRule(RelativeLayout.BELOW, ID_TV_COLOR_CODE_TEXT);
        rlp.setMargins(10, 0, 5, 0);

        decodeColor(rgb);
        TextView tv = new TextView(m_c);
        tv.setText("R:");
        tv.setId(ID_TV_R);
        tv.setLayoutParams(rlp);
		rl.addView(tv);

        rlp = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.MATCH_PARENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
        rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        rlp.addRule(RelativeLayout.LEFT_OF, ID_LL_RIGHT);
        rlp.addRule(RelativeLayout.BELOW, ID_TV_R);
        rlp.setMargins(10, 1, 5, 5);

        sb_r = new SeekBar(m_c);
        sb_r.setId(ID_SEEKBAR_R);
        sb_r.setMax(255);
        sb_r.setProgress(col_r);
        sb_r.setLayoutParams(rlp);
        sb_r.setOnSeekBarChangeListener(m_seekbarChangeListener);
        rl.addView(sb_r);
        
        rlp = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.WRAP_CONTENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
        rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        rlp.addRule(RelativeLayout.LEFT_OF, ID_LL_RIGHT);
        rlp.addRule(RelativeLayout.BELOW, ID_SEEKBAR_R);
        rlp.setMargins(10, 0, 5, 0);
        
        tv = new TextView(m_c);
        tv.setText("G:");
        tv.setId(ID_TV_G);
        tv.setLayoutParams(rlp);
		rl.addView(tv);

        rlp = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.MATCH_PARENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
        rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        rlp.addRule(RelativeLayout.LEFT_OF, ID_LL_RIGHT);
        rlp.addRule(RelativeLayout.BELOW, ID_TV_G);
        rlp.setMargins(10, 1, 5, 5);

        sb_g = new SeekBar(m_c);
        sb_g.setId(ID_SEEKBAR_G);
        sb_g.setMax(255);
        sb_g.setProgress(col_g);
        sb_g.setLayoutParams(rlp);
        sb_g.setOnSeekBarChangeListener(m_seekbarChangeListener);
        rl.addView(sb_g);
        
        rlp = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.WRAP_CONTENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
        rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        rlp.addRule(RelativeLayout.LEFT_OF, ID_LL_RIGHT);
        rlp.addRule(RelativeLayout.BELOW, ID_SEEKBAR_G);
        rlp.setMargins(10, 0, 5, 0);
        
        tv = new TextView(m_c);
        tv.setText("B:");
        tv.setId(ID_TV_B);
        tv.setLayoutParams(rlp);
		rl.addView(tv);

        rlp = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.MATCH_PARENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
        rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        rlp.addRule(RelativeLayout.LEFT_OF, ID_LL_RIGHT);
        rlp.addRule(RelativeLayout.BELOW, ID_TV_B);
        rlp.setMargins(10, 1, 5, 5);

        sb_b = new SeekBar(m_c);
        sb_b.setId(ID_SEEKBAR_B);
        sb_b.setMax(255);
        sb_b.setProgress(col_b);
        sb_b.setLayoutParams(rlp);
        sb_b.setOnSeekBarChangeListener(m_seekbarChangeListener);
        rl.addView(sb_b);
        
        rlp = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.WRAP_CONTENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
        rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        rlp.addRule(RelativeLayout.LEFT_OF, ID_LL_RIGHT);
        rlp.addRule(RelativeLayout.BELOW, ID_SEEKBAR_B);
        rlp.setMargins(10, 0, 5, 0);
        
        tv = new TextView(m_c);
        tv.setText(m_c.getString(R.string.transparency));
        tv.setId(ID_TV_TRANSPARENCY);
        tv.setLayoutParams(rlp);
		rl.addView(tv);

        rlp = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.MATCH_PARENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
        rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        rlp.addRule(RelativeLayout.LEFT_OF, ID_LL_RIGHT);
        rlp.addRule(RelativeLayout.BELOW, ID_TV_TRANSPARENCY);
        rlp.setMargins(10, 1, 5, 5);

        sb_a = new SeekBar(m_c);
        sb_a.setId(ID_SEEKBAR_ALPHA);
        sb_a.setMax(255);
        sb_a.setProgress(col_a);
        sb_a.setLayoutParams(rlp);
        sb_a.setOnSeekBarChangeListener(m_seekbarChangeListener);
        rl.addView(sb_a);
        
        int height = st.getDisplayHeight(null); 
        RelativeLayout.LayoutParams bselpar = null;
        if (height>=400){
            bselpar = new RelativeLayout.LayoutParams(
            		300,
            		RelativeLayout.LayoutParams.WRAP_CONTENT
            		);
            bselpar.addRule(RelativeLayout.CENTER_HORIZONTAL);
        	bselpar.addRule(RelativeLayout.BELOW, ID_SEEKBAR_ALPHA);
            bselpar.setMargins(10, 10, 5, 5);
        } else {
            bselpar = new RelativeLayout.LayoutParams(
            		RelativeLayout.LayoutParams.WRAP_CONTENT,
            		RelativeLayout.LayoutParams.WRAP_CONTENT
            		);
            bselpar.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            bselpar.addRule(RelativeLayout.BELOW, ID_BTN_CLOSE);
            bselpar.setMargins(5, 10, 5, 5);
        }

        Button bsel = new Button(m_c);
        bsel.setText(m_c.getString(R.string.selection));
        bsel.setLayoutParams(bselpar);
        bsel.setOnClickListener(m_clkListener);
        bsel.setId(ID_BTN_SELECT);
        if (height<400)
        	bsel.setText("Ok");
        if (m_et == null)
        	bsel.setVisibility(View.GONE);
        rl.addView(bsel);

        String str = m_c.getString(R.string.sc_inf_port);
        int iii = str.indexOf(st.STR_POINT);
        if (iii > 0) {
            rlp = new RelativeLayout.LayoutParams(
            		RelativeLayout.LayoutParams.MATCH_PARENT,
            		RelativeLayout.LayoutParams.WRAP_CONTENT)
            		;
            rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            if (height>=400)
            	rlp.addRule(RelativeLayout.BELOW, ID_BTN_SELECT);
            else
            	rlp.addRule(RelativeLayout.BELOW, ID_SEEKBAR_ALPHA);
            rlp.setMargins(10, 10, 5, 5);
            tv = new TextView(m_c);
            str = str.substring(iii+1).trim();
            tv.setText(str);
            tv.setLayoutParams(rlp);
            tv.setOnClickListener(m_clkListener);
            tv.setTextSize(12);
            rl.addView(tv);
        	
        }

        height = rl.getHeight();
        return rl;
        	
    }
    SeekBar.OnSeekBarChangeListener m_seekbarChangeListener = new SeekBar.OnSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			setArgbColorOnSeekBar(seekBar);
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			// TODO Auto-generated method stub
			setArgbColorOnSeekBar(seekBar);
		}
	};
	public void setArgbColorOnSeekBar(SeekBar sb)
	{
		switch (sb.getId())
		{
		case ID_SEEKBAR_ALPHA:
			col_a = sb.getProgress();
			break;
		case ID_SEEKBAR_R:
			col_r = sb.getProgress();
			break;
		case ID_SEEKBAR_G:
			col_g = sb.getProgress();
			break;
		case ID_SEEKBAR_B:
			col_b = sb.getProgress();
			break;
		}
		rgb = col_a * 0x1000000 + col_r * 0x10000 + col_g * 0x100 + col_b;
		setExampleText();
	}
	public void setExampleText() 
	{
		//String col =st.STR_NULL;
		String txt ="0x";
		if (col_a < 9)
			txt +=st.STR_ZERO;
		txt += st.STR_NULL+Integer.toHexString(col_a);
		if (col_r < 9)
			txt +=st.STR_ZERO;
		txt += st.STR_NULL+Integer.toHexString(col_r);
		if (col_g < 9)
			txt +=st.STR_ZERO;
		txt += st.STR_NULL+Integer.toHexString(col_g);
		if (col_b < 9)
			txt +=st.STR_ZERO;
		txt += st.STR_NULL+Integer.toHexString(col_b);
			
		
		if (tv_example!=null)
			tv_example.setText(txt);
		if (tv_example_col!=null)
			tv_example_col.setTextColor(rgb);
		
	}
	/** декодирует цвет на составляющие <br> */
    public static void decodeColor(int color) {
    	col_a = (color >> 24) & 0xff; // or color >>> 24
    	col_r = (color >> 16) & 0xff;
    	col_g = (color >>  8) & 0xff;
    	col_b = (color      ) & 0xff;
    	     	
    }
	public class Adapt extends ArrayAdapter<Integer> {
		private Integer[] num;
		private TextView tv;
		private Drawable dr;
		
		public Adapt(Context context, Integer[] images) {
		    super(context, android.R.layout.simple_spinner_item, images);
		    this.num = images;
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			parent.setBackgroundColor(0xffffffff);
		    return getImageForPosition(convertView, position, true);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
		    return getImageForPosition(convertView, position, true);
		}
		private View getImageForPosition(View v,int position, boolean bigImageSize) {
			if (v==null) {
				tv = new TextView(getContext());
		        tv.setLayoutParams(new LinearLayout.LayoutParams(
		        		ViewGroup.LayoutParams.MATCH_PARENT, 
		        		ViewGroup.LayoutParams.MATCH_PARENT));
			} else 
				tv= (TextView)v;
//			if (bigImageSize) {
//				tv.setMinimumHeight(75);
//				tv.setMinimumWidth(75);
//			}
			
	        tv.setBackgroundColor(ar_color_example[position]);
	        tv.setText(st.STR_SPACE);
	        return tv;
		}
	}
    AdapterView.OnItemSelectedListener m_itemSelection = new AdapterView.OnItemSelectedListener()
    {
    	public void onItemSelected(AdapterView<?> parent,
    			View itemSelected, int pos, long selectedId) 
    	{
    		rgb = ar_color_example[pos];
    		decodeColor(rgb);
    		if (sb_a!=null)
    			sb_a.setProgress(col_a);
    		if (sb_r!=null)
    			sb_r.setProgress(col_r);
    		if (sb_g!=null)
    			sb_g.setProgress(col_g);
    		if (sb_b!=null)
    			sb_b.setProgress(col_b);
    		((Spinner)parent).setBackgroundResource(R.drawable.colors_table);;
    		setExampleText();
    	}
    	public void onNothingSelected(AdapterView<?> parent) {
    	}
    ;};		    

}