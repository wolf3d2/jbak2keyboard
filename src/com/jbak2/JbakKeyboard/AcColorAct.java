package com.jbak2.JbakKeyboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.jbak2.Dialog.DlgPopupWnd;
import com.jbak2.JbakKeyboard.st;
import com.jbak2.ctrl.GlobDialog;

/** класс установок цветов кнопок для автодополнения */
public class AcColorAct extends Activity
{
	/** число на которое увеличиваем ил элемета edittext */
	public static final int ID_KEY = 100;
	/** число элементов массива edittext'ов */
	public static final int AR_COUNT_INDEX = 17;
	/** массив edittext'ов для задания цветов автодопа */
	public static EditText et[] = null;
	
	/** разметка "свои цвета" */
	public static LinearLayout ll_user = null;
	
    public static AcColorAct inst = null;
	boolean fl_changed = false;
	TextWatcher tw = new TextWatcher()
	{
        @Override
        public void afterTextChanged(Editable s) 
        {
//        	fl_changed = true;
        }
         
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) 
        {
//        	fl_changed = true;
        }
     
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) 
        {
        	fl_changed = true;
        }
	};
	/** индексы для задания полей цветов */
 	public static class ColId
    {
	    public static final byte MainBackColor              = 0;

	    public static final byte KeyCodeBackColor           = 1;
	    public static final byte KeyCodeTextColor           = 2;

	    public static final byte CounterBackColor           = 3;
	    public static final byte CounterTextColor           = 4;
	    
	    public static final byte ForciblyBackColor          = 5;
	    public static final byte ForciblyTextColor          = 6;

	    public static final byte AddVocabBackColor          = 7;
	    public static final byte AddVocabTextColor          = 8;

	    public static final byte WordBackColor              = 9;
	    public static final byte WordTextColor              = 10;

	    public static final byte ArrowDownBackColor         = 11;
	    public static final byte ArrowDownTextColor         = 12;

	    public static final byte CalcMenuBackColor          = 13;
	    public static final byte CalcMenuTextColor          = 14;

	    public static final byte CalcIndicatorBackColor     = 15;
	    public static final byte CalcIndicatorTextColor     = 16;

  	}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.accoloract);
	    inst = this;
	    
    	if (ColorPicker.inst!=null) {
    		ColorPicker.inst.finish();
    	}

		Spinner sp = (Spinner)findViewById(R.id.accolact_spinner_type);
		ArrayAdapter<?> adapter = 
		ArrayAdapter.createFromResource(this, R.array.ac_color_type, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp.setAdapter(adapter);
		sp.setOnItemSelectedListener(m_itemSelection);
		sp.setSelection(st.ac_col_type_layout);

		ll_user = (LinearLayout)findViewById(R.id.accolact_ll);
		createUserLayout(0);
        fl_changed = false;
//	    super.onCreate(savedInstanceState);
        //et_back.requestFocusFromTouch();
       	Ads.count_failed_load = 0;
        Ads.show(this, 2);
    }
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        
    }
    @Override
    public void onBackPressed() 
    {
    	if (GlobDialog.gbshow){
    		GlobDialog.inst.finish();
    		return;
    	}
    	if (ColorPicker.inst!=null){
    		ColorPicker.inst.finish();
    		return;
    	}
    	if (fl_changed) {
    		if (st.fl_pref_act) {
    			save();
    			st.toast(R.string.settings_saved);
    			finish();
    		} else {
        		final DlgPopupWnd dpw = new DlgPopupWnd(st.c());
                //gd.setPositionOnKeyboard(true);
        		dpw.set(R.string.data_changed, R.string.yes, R.string.no);
        		dpw.setObserver(new st.UniObserver()
                {
                    @Override
                    public int OnObserver(Object param1, Object param2)
                    {
                        if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
                        {
                        	save();
                            dpw.dismiss();
                            finish();
                        }
                        return 0;
                    }
                });
        		dpw.show(0, null);
        		st.showkbd();
                //gd.showAlert();
    			
    		}
    	} else
    		super.onBackPressed();
    	finish();
    }
    View.OnKeyListener m_keyListener = new View.OnKeyListener()
    {
        @SuppressLint("NewApi")
		@Override
        public boolean onKey(View v, int keyCode, KeyEvent event)
        {
    	    if(event.getAction() == KeyEvent.ACTION_DOWN){ 
        	    if(!st.isHoneycomb()&&event.isCtrlPressed()&&keyCode == KeyEvent.KEYCODE_A){
 	    		
        	    	EditText et = (EditText)v;
        	    	if (et !=null){
        	    		et.selectAll();
        	    		return true;
        	    	}
        	    }
    	    }
            return false;
        }
    };
    public void save()
    {
        SharedPreferences p = st.pref(inst);
        Editor e = p.edit();
    	switch (st.ac_col_type_layout)
    	{
    	case 2:
    		e.putInt(st.AC_COL_TYPE_LAYOUT, 2);
    		break;
    	case 1:
    		e.putInt(st.AC_COL_TYPE_LAYOUT, 1);
    		break;
		default: // свои цвета и оно же 0 значение
	    	if (et!=null&&et.length==AR_COUNT_INDEX) {
	    		e.putInt(st.AC_COL_TYPE_LAYOUT, 0);
	            checkSave(e,st.AC_COL_MAIN_BG,ColId.MainBackColor,st.AC_COLDEF_MAIN_BG);
	            checkSave(e,st.AC_COL_KEYCODE_BG,ColId.KeyCodeBackColor,st.AC_COLDEF_KEYCODE_BG);
	            checkSave(e,st.AC_COL_KEYCODE_T,ColId.KeyCodeTextColor,st.AC_COLDEF_KEYCODE_T);
	            checkSave(e,st.AC_COL_COUNTER_BG,ColId.CounterBackColor,st.AC_COLDEF_COUNTER_BG);
	            checkSave(e,st.AC_COL_COUNTER_T,ColId.CounterTextColor,st.AC_COLDEF_COUNTER_T);
	            checkSave(e,st.AC_COL_FORCIBLY_BG,ColId.ForciblyBackColor,st.AC_COLDEF_FORCIBLY_BG);
	            checkSave(e,st.AC_COL_FORCIBLY_T,ColId.ForciblyTextColor,st.AC_COLDEF_FORCIBLY_T);
	            checkSave(e,st.AC_COL_ADD_BG,ColId.AddVocabBackColor,st.AC_COLDEF_ADD_BG);
	            checkSave(e,st.AC_COL_ADD_T,ColId.AddVocabTextColor,st.AC_COLDEF_ADD_T);
	            checkSave(e,st.AC_COL_WORD_BG,ColId.WordBackColor,st.AC_COLDEF_WORD_BG);
	            checkSave(e,st.AC_COL_WORD_T,ColId.WordTextColor,st.AC_COLDEF_WORD_T);
	            checkSave(e,st.AC_COL_ARROWDOWN_BG,ColId.ArrowDownBackColor,st.AC_COLDEF_ARROWDOWN_BG);
	            checkSave(e,st.AC_COL_ARROWDOWN_T,ColId.ArrowDownTextColor,st.AC_COLDEF_ARROWDOWN_T);
	            checkSave(e,st.AC_COL_CALCMENU_BG,ColId.CalcMenuBackColor,st.AC_COLDEF_CALCMENU_BG);
	            checkSave(e,st.AC_COL_CALCMENU_T,ColId.CalcMenuTextColor,st.AC_COLDEF_CALCMENU_T);
	            checkSave(e,st.AC_COL_CALCIND_BG,ColId.CalcIndicatorBackColor,st.AC_COLDEF_CALCIND_BG);
	            checkSave(e,st.AC_COL_CALCIND_T,ColId.CalcIndicatorTextColor,st.AC_COLDEF_CALCIND_T);
	    	}
			break;
    	}
		e.commit();
    }
    String mcol = null;
    int icol = 0;
    public void checkSave(Editor e, String prefkey, int index, int defcolor)
    {
    	mcol = et[index].getEditableText().toString().trim();
       	icol = st.str2hex(mcol, 16);
       	if (icol==-1) {
			mcol = String.format(st.STR_16FORMAT,defcolor);
			et[index].setText(mcol);
       	}
        e.putString(prefkey, mcol);
    }
    AdapterView.OnItemSelectedListener m_itemSelection = new AdapterView.OnItemSelectedListener()
    {
    	public void onItemSelected(AdapterView<?> parent,
    			View itemSelected, int selectedItemPosition, long selectedId) 
    	{
    		st.ac_col_type_layout = selectedItemPosition;
    		st.pref(inst).edit().putInt(st.AC_COL_TYPE_LAYOUT, selectedItemPosition).commit();
    		setShowLayout(selectedItemPosition);
    	}
    	public void onNothingSelected(AdapterView<?> parent) {
    	}
    ;};		    
    public void setShowLayout(int type)
    {
    	ll_user.setVisibility(View.GONE);
    	switch (type)
    	{
    	case 2:
    	case 1:
    		fl_changed = true;
    		//ll_user.setVerticalScrollbarPosition(0);
    		break;
    	default:
    		ll_user.setVisibility(View.VISIBLE);
    		//TextView tv = (TextView)inst.findViewById(R.id.accolact_spinner_type);
    		ll_user.setVerticalScrollbarPosition(0);
    		
    	}
    }
    public void getUserLayoutDefaultElements()
    {
    	// описание задания цветов
    	TextView tv = new TextView(inst);
    	tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
    			LinearLayout.LayoutParams.WRAP_CONTENT));
    	tv.setTextSize(15);
    	tv.setPadding(5, 2, 5, 2);
    	tv.setTextColor(Color.GREEN);
    	tv.setText(R.string.pc2act_color_desc);
    	ll_user.addView(tv);

    	tv = new TextView(inst);
    	tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
    			LinearLayout.LayoutParams.WRAP_CONTENT));
    	tv.setGravity(Gravity.CENTER_HORIZONTAL);
    	tv.setTextSize(15);
    	tv.setText(st.STR_3TIRE);
    	ll_user.addView(tv);
    }
    public void createUserLayout(int type)
    {
    	et = new EditText[AR_COUNT_INDEX];
    	for (int i=0;i<AR_COUNT_INDEX;i++) {
        	et[i] = new EditText(inst);
    	}
    	if (ll_user.getChildCount()>0)
    		ll_user.removeAllViews();
    	getUserLayoutDefaultElements();
        SharedPreferences p = st.pref(inst);
        st.readACColor(p);
    	ll_user.addView(getTitleLineUserLayout(R.string.ac_color_mainback,true));
    	createUserLineLayout(ColId.MainBackColor, 0, st.ac_col_main_back, st.AC_COLDEF_MAIN_BG);

    	ll_user.addView(getTitleLineUserLayout(R.string.ac_color_keycode,true));
    	createUserLineLayout(ColId.KeyCodeBackColor, R.string.ac_color_back, st.ac_col_keycode_back, st.AC_COLDEF_KEYCODE_BG);
    	createUserLineLayout(ColId.KeyCodeTextColor, R.string.ac_color_text, st.ac_col_keycode_text, st.AC_COLDEF_KEYCODE_T);
    	
    	ll_user.addView(getTitleLineUserLayout(R.string.set_counter,true));
    	createUserLineLayout(ColId.CounterBackColor, R.string.ac_color_back, st.ac_col_counter_back, st.AC_COLDEF_COUNTER_BG);
    	createUserLineLayout(ColId.CounterTextColor, R.string.ac_color_text, st.ac_col_counter_text, st.AC_COLDEF_COUNTER_T);
    	
    	ll_user.addView(getTitleLineUserLayout(R.string.ac_color_forcibly,true));
    	createUserLineLayout(ColId.ForciblyBackColor, R.string.ac_color_back, st.ac_col_forcibly_back, st.AC_COLDEF_FORCIBLY_BG);
    	createUserLineLayout(ColId.ForciblyTextColor, R.string.ac_color_text, st.ac_col_forcibly_text, st.AC_COLDEF_FORCIBLY_T);
    	
    	ll_user.addView(getTitleLineUserLayout(R.string.ac_color_addvocab,true));
    	createUserLineLayout(ColId.AddVocabBackColor, R.string.ac_color_back, st.ac_col_addvocab_back, st.AC_COLDEF_ADD_BG);
    	createUserLineLayout(ColId.AddVocabTextColor, R.string.ac_color_text, st.ac_col_addvocab_text, st.AC_COLDEF_ADD_T);
    	
    	ll_user.addView(getTitleLineUserLayout(R.string.ac_color_word,true));
    	createUserLineLayout(ColId.WordBackColor, R.string.ac_color_back, st.ac_col_word_back, st.AC_COLDEF_WORD_BG);
    	createUserLineLayout(ColId.WordTextColor, R.string.ac_color_text, st.ac_col_word_text, st.AC_COLDEF_WORD_T);
    	
    	ll_user.addView(getTitleLineUserLayout(R.string.ac_color_arrowdown,true));
    	createUserLineLayout(ColId.ArrowDownBackColor, R.string.ac_color_back, st.ac_col_arrow_down_back, st.AC_COLDEF_ARROWDOWN_BG);
    	createUserLineLayout(ColId.ArrowDownTextColor, R.string.ac_color_text, st.ac_col_arrow_down_text, st.AC_COLDEF_ARROWDOWN_T);
    	
    	ll_user.addView(getTitleLineUserLayout(R.string.ac_color_calcmenu,true));
    	createUserLineLayout(ColId.CalcMenuBackColor, R.string.ac_color_back, st.ac_col_calcmenu_back, st.AC_COLDEF_CALCMENU_BG);
    	createUserLineLayout(ColId.CalcMenuTextColor, R.string.ac_color_text, st.ac_col_calcmenu_text, st.AC_COLDEF_CALCMENU_T);
    	
    	ll_user.addView(getTitleLineUserLayout(R.string.ac_color_calcind,true));
    	createUserLineLayout(ColId.CalcIndicatorBackColor, R.string.ac_color_back, st.ac_col_calcind_back, st.AC_COLDEF_CALCIND_BG);
    	createUserLineLayout(ColId.CalcIndicatorTextColor, R.string.ac_color_text, st.ac_col_calcind_text, st.AC_COLDEF_CALCIND_T);
    	
    }
    /** строка горизонтальной разметки для зания одного цвета для каждого элемента автодопа 
     * @param id - ид горизонтальной разметки
     * @param resIdTitleLayout - ид текста заголовка горизонтальной разметки
     * @param twoLine - одна строка или две
     *  */
    public void createUserLineLayout(int id, int resIdTitleLayout, int color, int hint_color)
    {
    	// заголовок горизонтальной разметки
    	if (resIdTitleLayout!=0)
    		ll_user.addView(getTitleLineUserLayout(resIdTitleLayout,false));
    	// цвет кнопки или фона автодопа
		TextView tv = new TextView(inst);
    	tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
    			LinearLayout.LayoutParams.WRAP_CONTENT));
    	tv.setText(R.string.ac_color_back);

    	RelativeLayout rl = new RelativeLayout(inst);
    	rl.setLayoutParams(new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
    			LinearLayout.LayoutParams.WRAP_CONTENT));
    	rl.setId(id);

    	RelativeLayout.LayoutParams lprr = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.WRAP_CONTENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
    	lprr.setMargins(2, 2, 2, 2);
        lprr.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        Button btn = new Button(inst);
        btn.setText(R.string.selection);
        btn.setId(ID_KEY+(id*2)+2);
        btn.setOnClickListener(m_clkListener);
        btn.setLayoutParams(lprr);
        rl.addView(btn);
        
    	RelativeLayout.LayoutParams lprl = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.WRAP_CONTENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT)
        		;
    	//lprl.setMargins(2, 2, 2, 2);
        lprl.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lprl.addRule(RelativeLayout.LEFT_OF, btn.getId());
        
		et[id].setTextSize(18);
		// если откомментить, то глючит с масшабированием при закрытии окна пикера 
		// кнопкой Назад
		//et[id].setPadding(5, 2, 5, 2);
        et[id].setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        et[id].setKeyListener(DigitsKeyListener.getInstance(st.STR_16INPUT_DIGIT));
        et[id].setId(ID_KEY+(id*2)+1);
		et[id].setOnKeyListener(number_keyListener);
        et[id].setLayoutParams(lprl);
        et[id].addTextChangedListener(tw);
        et[id].setText(String.format(st.STR_16FORMAT,color));
        et[id].setHint(inst.getString(R.string.by_def)+st.STR_COLON+st.STR_SPACE+String.format(st.STR_16FORMAT,hint_color));
        
        rl.addView(et[id]);
        
    	ll_user.addView(rl);
    	return;
    }
    /** возвращает текст заголовка горизонтальной разметки */
    public TextView getTitleLineUserLayout(int resIdText, boolean bold)
    {
    	TextView tv = new TextView(inst);
    	tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
    			LinearLayout.LayoutParams.WRAP_CONTENT));
    	tv.setTextSize(15);
    	if (bold) {
        	tv.setTextSize(20);
        	tv.setTypeface(null, Typeface.BOLD);
    	}
    	tv.setText(resIdText);
    	return tv;
    }
    View.OnKeyListener number_keyListener = new View.OnKeyListener()
    {
        @SuppressLint("NewApi")
		@Override
        public boolean onKey(View v, int keyCode, KeyEvent event)
        {
    	    if(event.getAction() == KeyEvent.ACTION_DOWN){ 
        	    if(!st.isHoneycomb()&&event.isCtrlPressed()&&keyCode == KeyEvent.KEYCODE_A){
 	    		
        	    	EditText et = (EditText)v;
        	    	if (et !=null){
        	    		et.selectAll();
        	    		return true;
        	    	}
        	    }
    	    } else {
    	    	v.setBackgroundColor(Color.WHITE);
    	    }
            return false;
        }
    };
    View.OnClickListener m_clkListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
        	onClickButton(v.getId());
        }
    };
    public void onClickButton(int id){
    	ColorPicker m_colpic = null;
        m_colpic = (ColorPicker) getLayoutInflater().inflate(R.layout.picker, null);
        if (m_colpic != null){
    		EditText et = null;
    		et = (EditText) findViewById(id-1);
    		if (et!=null)
    			m_colpic.show(inst, et);
        }
    	
    }
    
}