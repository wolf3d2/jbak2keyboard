package com.jbak2.JbakKeyboard;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.text.ClipboardManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/**Класс для визуального подбора номера цвета */
@SuppressLint("NewApi")
public class ColorPicker extends View implements View.OnTouchListener
{
//для закрытия окна диалога вставить в onBackPressed
//вызывающей активности проверку:
//
//	if (ColorPicker.inst!=null){
//		GlobDialog.inst.finish();
//		return;
//	}
	/** jjjjj */
	static boolean fl_color_picker = false;
	EditText m_et = null;
	SeekBar sb = null;
	TextView tv_example = null;
	TextView tv_example_col = null;
	int transparent = 255;
	int height = 0;
	// выбранный цвет из круга
	int rgb = 0xff000000;
	int lastrgb =0;
	int lastX = 0;
	int lastY = 0;
	int xxx = 0;
	int yyy = 0;
	
	// конечный цвет с альфа каналом
	int endrgb = 0xff000000;
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
		setOnTouchListener(this);
		setDrawingCacheEnabled(true);

//		setExampleText();
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
    	m_et = et;
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
		
        if (st.isLandscape(c))
			m_view = createLandscape(c);
		else
			m_view = createPortrait(c);
//		if (height != 0)
//			lp.height = height;
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
            case 1001:
            	finish();
                return;
            case 1007:
            	if(m_et!=null){
            		m_et.setText(st.STR_NULL+String.format(st.STR_16FORMAT,endrgb));
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
    public View createLandscape(Context c)
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
		bclose.setId(1001);
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
        rpcanvas.addRule(RelativeLayout.LEFT_OF, 1001);
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
		this.setId(1002);
		rl.addView(this);
		
        RelativeLayout.LayoutParams tvexamlecolpar = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.WRAP_CONTENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
        tvexamlecolpar.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        tvexamlecolpar.addRule(RelativeLayout.BELOW, 1002);
        tvexamlecolpar.setMargins(10, 10, 5, 5);
        tv_example_col = new TextView(m_c);
        tv_example_col.setBackgroundResource(R.drawable.textview__frame_style);
        tv_example_col.setTextColor(endrgb);
        tv_example_col.setId(1003);
        tv_example_col.setTextSize(30);
        tv_example_col.setLayoutParams(tvexamlecolpar);
        tv_example_col.setText(st.STR_SPACE+"▇"+st.STR_SPACE);
        rl.addView(tv_example_col);

        RelativeLayout.LayoutParams tvexamletxtpar = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.WRAP_CONTENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
        tvexamletxtpar.addRule(RelativeLayout.BELOW, 1002);
        tvexamletxtpar.addRule(RelativeLayout.RIGHT_OF, 1003);
        tvexamletxtpar.setMargins(10, 10, 5, 5);

        tv_example = new TextView(m_c);
        tv_example.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				st.copyText(m_c, ((TextView)v).getText().toString());
			}
		});
        tv_example.setTextSize(30);
//        tv_example.setText();
//        	tv_example.setTextColor(setExampleText());
        tv_example.setId(1004);
        tv_example.setLayoutParams(tvexamletxtpar);
        setExampleText();
        rl.addView(tv_example);

        RelativeLayout.LayoutParams tvsbpar = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.WRAP_CONTENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
        tvsbpar.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        tvsbpar.addRule(RelativeLayout.BELOW, 1003);
        tvsbpar.setMargins(10, 0, 5, 0);
        TextView tvseek = new TextView(m_c);
        tvseek.setText(m_c.getString(R.string.transparency));
        tvseek.setId(1005);
        tvseek.setLayoutParams(tvsbpar);
		rl.addView(tvseek);

        RelativeLayout.LayoutParams sbpar = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.MATCH_PARENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
        sbpar.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        sbpar.addRule(RelativeLayout.BELOW, 1005);
        sbpar.setMargins(10, 1, 5, 5);
        sb = new SeekBar(m_c);
        sb.setId(1006);
        sb.setMax(255);
        sb.setProgress(transparent);
        sb.setLayoutParams(sbpar);
        sb.setOnSeekBarChangeListener(m_seekbarCngListener);
        rl.addView(sb);
        
        int height = st.getDisplayHeight(null); 
        RelativeLayout.LayoutParams bselpar = null;
        if (height>=400){
            bselpar = new RelativeLayout.LayoutParams(
            		300,
            		RelativeLayout.LayoutParams.WRAP_CONTENT
            		);
            bselpar.addRule(RelativeLayout.CENTER_HORIZONTAL);
        	bselpar.addRule(RelativeLayout.BELOW, 1006);
            bselpar.setMargins(10, 10, 5, 5);
        } else {
            bselpar = new RelativeLayout.LayoutParams(
            		RelativeLayout.LayoutParams.WRAP_CONTENT,
            		RelativeLayout.LayoutParams.WRAP_CONTENT
            		);
            bselpar.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            bselpar.addRule(RelativeLayout.BELOW, 1001);
            bselpar.setMargins(5, 10, 5, 5);
        }

        Button bsel = new Button(m_c);
        bsel.setText(m_c.getString(R.string.selection));
        bsel.setLayoutParams(bselpar);
        bsel.setOnClickListener(m_clkListener);
        bsel.setId(1007);
        if (height<400)
        	bsel.setText("Ok");
        rl.addView(bsel);

        RelativeLayout.LayoutParams tvinfpar = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.MATCH_PARENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
        tvinfpar.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        if (height>=400)
        	tvinfpar.addRule(RelativeLayout.BELOW, 1007);
        else
        	tvinfpar.addRule(RelativeLayout.BELOW, 1006);
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
    public View createPortrait(Context c)
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
		bclose.setId(1001);
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
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
        rpcanvas.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        rpcanvas.addRule(RelativeLayout.LEFT_OF, 1001);
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
		this.setId(1002);
		rl.addView(this);
		
        RelativeLayout.LayoutParams tvexamlecolpar = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.WRAP_CONTENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
        tvexamlecolpar.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        tvexamlecolpar.addRule(RelativeLayout.BELOW, 1002);
        tvexamlecolpar.setMargins(10, 10, 5, 5);
        tv_example_col = new TextView(m_c);
        tv_example_col.setBackgroundResource(R.drawable.textview__frame_style);
        tv_example_col.setTextColor(endrgb);
        tv_example_col.setId(1003);
        tv_example_col.setTextSize(30);
        tv_example_col.setLayoutParams(tvexamlecolpar);
        tv_example_col.setText(st.STR_SPACE+"▇"+st.STR_SPACE);
        rl.addView(tv_example_col);

        RelativeLayout.LayoutParams tvexamletxtpar = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.WRAP_CONTENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
        tvexamletxtpar.addRule(RelativeLayout.BELOW, 1002);
        tvexamletxtpar.addRule(RelativeLayout.RIGHT_OF, 1003);
        tvexamletxtpar.setMargins(10, 10, 5, 5);

        tv_example = new TextView(m_c);
//        tv_example.setText();
//        	tv_example.setTextColor(setExampleText());
        tv_example.setId(1004);
        tv_example.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				st.copyText(m_c, ((TextView)v).getText().toString());
			}
		});
        tv_example.setTextSize(30);
        tv_example.setLayoutParams(tvexamletxtpar);
        setExampleText();
        rl.addView(tv_example);

        RelativeLayout.LayoutParams tvsbpar = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.WRAP_CONTENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
        tvsbpar.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        tvsbpar.addRule(RelativeLayout.BELOW, 1003);
        tvsbpar.setMargins(10, 0, 5, 0);
        TextView tvseek = new TextView(m_c);
        tvseek.setText(m_c.getString(R.string.transparency));
        tvseek.setId(1005);
        tvseek.setLayoutParams(tvsbpar);
		rl.addView(tvseek);

        RelativeLayout.LayoutParams sbpar = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.MATCH_PARENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
        sbpar.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        sbpar.addRule(RelativeLayout.BELOW, 1005);
        sbpar.setMargins(10, 1, 5, 5);
        sb = new SeekBar(m_c);
        sb.setId(1006);
        sb.setMax(255);
        sb.setProgress(transparent);
        sb.setLayoutParams(sbpar);
        sb.setOnSeekBarChangeListener(m_seekbarCngListener);
        rl.addView(sb);
        
        int height = st.getDisplayHeight(null); 
        RelativeLayout.LayoutParams bselpar = null;
        if (height>=400){
            bselpar = new RelativeLayout.LayoutParams(
            		300,
            		RelativeLayout.LayoutParams.WRAP_CONTENT
            		);
            bselpar.addRule(RelativeLayout.CENTER_HORIZONTAL);
        	bselpar.addRule(RelativeLayout.BELOW, 1006);
            bselpar.setMargins(10, 10, 5, 5);
        } else {
            bselpar = new RelativeLayout.LayoutParams(
            		RelativeLayout.LayoutParams.WRAP_CONTENT,
            		RelativeLayout.LayoutParams.WRAP_CONTENT
            		);
            bselpar.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            bselpar.addRule(RelativeLayout.BELOW, 1001);
            bselpar.setMargins(5, 10, 5, 5);
        }

        Button bsel = new Button(m_c);
        bsel.setText(m_c.getString(R.string.selection));
        bsel.setLayoutParams(bselpar);
        bsel.setOnClickListener(m_clkListener);
        bsel.setId(1007);
        if (height<400)
        	bsel.setText("Ok");
        rl.addView(bsel);

        RelativeLayout.LayoutParams tvinfpar = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.MATCH_PARENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
        tvinfpar.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        if (height>=400)
        	tvinfpar.addRule(RelativeLayout.BELOW, 1007);
        else
        	tvinfpar.addRule(RelativeLayout.BELOW, 1006);
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
    SeekBar.OnSeekBarChangeListener m_seekbarCngListener = new SeekBar.OnSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			setExampleText();
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			// TODO Auto-generated method stub
			setExampleText();
		}
	};
	public void setExampleText() 
	{
		String txt ="0x";
		if (sb!=null){
			transparent = sb.getProgress();
			if (transparent<16)
				txt=txt+st.STR_ZERO;
			txt += st.STR_NULL+Integer.toHexString(transparent);
		} else {
			if (transparent<16)
				txt=txt+st.STR_ZERO;
			txt += Integer.toHexString(transparent);
		}
//		txt += Integer.toHexString(rgb);
		if (rgb!=0){
			String tmp = st.STR_NULL;
			tmp = String.format(st.STR_16FORMAT,rgb);
			tmp=tmp.substring(4);
			txt+=tmp;
		}
		endrgb = st.str2hex(txt, 16);
		if (tv_example!=null)
			tv_example.setText(txt);
		if (tv_example_col!=null)
			tv_example_col.setTextColor(endrgb);
		
	}
}