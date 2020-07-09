package com.jbak2.JbakKeyboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;

import com.jbak2.Dialog.Dlg;
import com.jbak2.Dialog.DlgFileExplorer;
import com.jbak2.Dialog.DlgPopupWnd;
import com.jbak2.JbakKeyboard.st;
import com.jbak2.ctrl.Font;

/** класс для вывода активности настроек маленькой клавиатуры второй версии */
public class Popup2act extends Activity
	{
    ColorPicker m_colpic = null;
    static Popup2act inst;
    Button btn_unicode_app = null;
    CheckBox cb1 = null;
    CheckBox cb2 = null;
	/** число на которое увеличиваем ил элемета edittext */
	public static final int ID_KEY = 100;
	/** число элементов массива edittext'ов */
	public static final int AR_COUNT_INDEX = 5;
	/** массив edittext'ов для задания цветов миниклавы v2 */
	public static EditText et[] = null;
	EditText str_add = null;
	EditText btn_size = null;
	EditText btnoff_size = null;

	public static LinearLayout ll_color = null;

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

	/** индексы для задания полей цветов миниклавиатуры */
 	public static class ColorId
    {
	    public static final byte MainBackColor              = 0;

	    public static final byte KeyBackColor           = 1;
	    public static final byte KeyTextColor           = 2;

	    public static final byte OfficialBackColor           = 3;
	    public static final byte OfficialTextColor           = 4;
  	}
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.popup2act);
	    inst = this;
    	if (ColorPicker.inst!=null) {
    		ColorPicker.inst.finish();
    	}

    	et = new EditText[AR_COUNT_INDEX];
    	for (int i=0;i<AR_COUNT_INDEX;i++) {
        	et[i] = new EditText(inst);
    	}

        cb1 = (CheckBox)findViewById(R.id.pc2act_cb1);
        cb1.setChecked(st.win_fix);
        cb1.setOnClickListener(m_ClickListener);
        
        TextView tv = (TextView)findViewById(R.id.pc2act_help);
        tv.setOnClickListener(m_ClickListener);
        
        cb2 = (CheckBox)findViewById(R.id.pc2act_cb2);
        cb2.setChecked(st.pc2_lr);
        cb2.setOnClickListener(m_ClickListener);
        // задаём круглым кнопкам листенер
        btn_unicode_app = (Button)findViewById(R.id.pc2act_plus_btn_button);
        btn_unicode_app.setOnClickListener(m_ClickListener);
        btn_unicode_app = (Button)findViewById(R.id.pc2act_plus_tpl_button);
        btn_unicode_app.setOnClickListener(m_ClickListener);
        btn_unicode_app = (Button)findViewById(R.id.pc2act_plus_btn_font);
        btn_unicode_app.setOnClickListener(m_ClickListener);

        btn_unicode_app = (Button)findViewById(R.id.pc2act_unicode_app);
        btn_unicode_app.setOnClickListener(m_ClickListener);
        str_add = (EditText)findViewById(R.id.pc2act_et_addsymb);
	    str_add.setText(st.gesture_str);
	    str_add.addTextChangedListener(tw);
        btn_size = (EditText)findViewById(R.id.pc2act_et_btn_size);
        btn_size.setText((st.STR_NULL+st.btn_size).trim());
        btn_size.addTextChangedListener(tw);
        btnoff_size = (EditText)findViewById(R.id.pc2act_et_btnoff_size);
        btnoff_size.setText((st.STR_NULL+st.btnoff_size).trim());
        btnoff_size.addTextChangedListener(tw);
		ll_color = (LinearLayout)findViewById(R.id.pc2act_ll_color);
        createColorLayout();
        str_add.requestFocusFromTouch();
        fl_changed = false;
    }
    @Override
    public void onBackPressed() 
    {
    	if (ColorPicker.inst!=null) {
    		ColorPicker.inst.finish();
    		return;
    	}
    	if (fl_changed) {
        	final DlgPopupWnd dpw = new DlgPopupWnd(st.c());
        	dpw.setGravityText(Gravity.CENTER_HORIZONTAL);
        	dpw.set(R.string.data_changed, R.string.yes, R.string.no);
        	dpw.setObserver(new st.UniObserver()
            {
                @Override
                public int OnObserver(Object param1, Object param2)
                {
                    if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
                    {
                		st.pref().edit().putString(st.SET_STR_GESTURE_DOPSYMB, str_add.getText().toString().trim()).commit();
                		st.pref().edit().putBoolean(st.PREF_KEY_PC2_WIN_FIX, cb1.isChecked()).commit();
                		st.pref().edit().putBoolean(st.PREF_KEY_PC2_LR, cb2.isChecked()).commit();
                   		st.pref().edit().putString(st.PREF_KEY_PC2_BTN_SIZE, btn_size.getText().toString().trim()).commit();
                   		st.pref().edit().putString(st.PREF_KEY_PC2_BTNOFF_SIZE, btnoff_size.getText().toString().trim()).commit();
                    	saveColor();
                   		finish();
                    }
//                   		showKbdAndAdditionalSymbol();
//                    } else
//                   		showKbdAndAdditionalSymbol();
                	dpw.dismiss();
                    return 0;
                }
            });
        	dpw.show(0, null);

    	} else {
    		super.onBackPressed();
    		st.showkbd();
    	}
    	ColorPicker.inst.fl_color_picker = false;
    	finish();
     }
    public void showKbdAndAdditionalSymbol() {
        if (ServiceJbKbd.inst!=null&&ServiceJbKbd.inst.isInputViewShown()) {
        	st.showkbd();
        	st.popupAdditional(1);
        }
    }
    public void saveColor()
    {
        SharedPreferences p = st.pref(inst);
        Editor e = p.edit();
        checkSave(e,st.PREF_KEY_PC2_WIN_BG,ColorId.MainBackColor,st.PREF_KEY_PC2_WIN_BG_DEF);
        checkSave(e,st.PREF_KEY_PC2_BTN_BG,ColorId.KeyBackColor,st.PREF_KEY_PC2_BTN_BG_DEF);
        checkSave(e,st.PREF_KEY_PC2_BTN_TCOL,ColorId.KeyTextColor,st.PREF_KEY_PC2_BTN_TCOL_DEF);
        checkSave(e,st.PREF_KEY_PC2_BTNOFF_BG,ColorId.OfficialBackColor,st.PREF_KEY_PC2_BTNOFF_BG_DEF);
        checkSave(e,st.PREF_KEY_PC2_BTNOFF_TCOL,ColorId.OfficialTextColor,st.PREF_KEY_PC2_BTNOFF_TCOL_DEF);
        e.commit();

    }
    String mcol = null;
    int icol = 0;
    public void checkSave(Editor e, String key, int index, String defcolor)
    {
    	mcol = et[index].getEditableText().toString().trim();
       	icol = st.str2hex(mcol, 16);
       	if (icol==-1) {
			mcol = defcolor;
			//et[index].setText(mcol);
       	}
        e.putString(key, mcol);
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
    	    }
            return false;
        }
    };
    View.OnClickListener m_ClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
            case R.id.pc2act_help:
            	String txt = inst.getString(R.string.pc2act_str_help1); 
            	txt += inst.getString(R.string.set_ac_defkey_help_buttons);
            	txt += inst.getString(R.string.set_ac_defkey_help_button_font);
            	txt += inst.getString(R.string.set_ac_defkey_help_buttons2);
				Dlg.helpDialog(inst, txt);
            	return;
            case R.id.pc2act_plus_btn_font:
            	Font.showDialogGridSelectSymbolOfFont(inst, new st.UniObserver() {
    				
    				@Override
    				public int OnObserver(Object param1, Object param2) {
						int pos = AlertDialog.BUTTON_NEGATIVE;
						if (param1 != null)
							pos = (Integer) param1;
						boolean blong = false;
						if (param2 != null)
							blong = ((Boolean) param2).booleanValue();
						if (pos > -1) {
							if (!blong) {
								Dlg.dismiss();
								String text = st.STR_PREFIX_FONT + Font.ar_symbol[pos];
								st.setInsertTextToCursorPosition(str_add, text);
								return 0;
							} else {
								st.copyText(inst, st.STR_NULL + Font.ar_symbol[pos]);
							}
						}
						return 0;
    				}
    			});
                return;
            case R.id.pc2act_plus_btn_button:
            	String text = st.STR_PREFIX+"0,t] ";
            	st.setInsertTextToCursorPosition(str_add, text);
            	int pos = str_add.getSelectionStart()-4;
            	if (pos < 0)
            		pos = 0;
            	str_add.setSelection(pos);
                return;
            case R.id.pc2act_plus_tpl_button:
            	final String foldroot = st.getSettingsPath()+Templates.FOLDER_TEMPLATES;
            	File rd = new File(foldroot);
                new DlgFileExplorer(inst, 
                		inst.getString(R.string.fm_btn_plus_title),
                		null,
                		DlgFileExplorer.TYPE_ALL,
                		rd,
                		null,
                		DlgFileExplorer.SELECT_FILE) {
                    @Override
                    public void onSelected(File file)
                    {
                    	String text = file.getAbsolutePath();
                    	text = st.getSettingsPathShort(text, Templates.INT_FOLDER_TEMPLATES);
                    	text = st.STR_PREFIX+text+st.STR_COMMA+file.getName()+"] ";
                    	st.setInsertTextToCursorPosition(str_add, text);
                    	int pos = str_add.getSelectionStart()-2;
                    	if (pos < 0)
                    		pos = 0;
                    	str_add.setSelection(pos);
                    	st.showkbd();

                    }
                }
                .show();
                return;
            case R.id.pc2act_cb1:
            case R.id.pc2act_cb2:
            	fl_changed = true;
                return;
            case R.id.pc2act_unicode_app:
            	ServiceJbKbd.inst.forceHide();
            	st.runApp(inst, st.APP_PACKAGE_UNICODE, null);
                return;
            case R.id.cand_left:
                return;
            }
        }
    };
    View.OnClickListener m_clkListenerColor = new View.OnClickListener()
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
    public void createColorLayout()
    {
    	if (ll_color.getChildCount()>0)
    		ll_color.removeAllViews();
    	ll_color.addView(getTitleLineUserLayout(R.string.pc2act_wind_color_back));
    	createColorLineLayout(ColorId.MainBackColor, st.win_bg);

    	ll_color.addView(getTitleLineUserLayout(R.string.pc2act_btn_color_back));
    	createColorLineLayout(ColorId.KeyBackColor, st.btn_bg);

    	ll_color.addView(getTitleLineUserLayout(R.string.pc2act_btn_color_txt));
    	createColorLineLayout(ColorId.KeyTextColor, st.btn_tc);

    	ll_color.addView(getTitleLineUserLayout(R.string.pc2act_btn_off_color_back));
    	createColorLineLayout(ColorId.OfficialBackColor, st.btnoff_bg);

    	ll_color.addView(getTitleLineUserLayout(R.string.pc2act_btn_off_color_txt));
    	createColorLineLayout(ColorId.OfficialTextColor, st.btnoff_tc);
}
    /** строка горизонтальной разметки для зания одного цвета для каждого элемента автодопа 
     * @param id - ид горизонтальной разметки
     *  */
    public void createColorLineLayout(int id, int color)
    {
    	RelativeLayout rl = new RelativeLayout(inst);
    	rl.setLayoutParams(new RelativeLayout.LayoutParams(
    			LinearLayout.LayoutParams.MATCH_PARENT,
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
        btn.setOnClickListener(m_clkListenerColor);
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
        //et[id].setHint(inst.getString(R.string.by_def)+st.STR_COLON+st.STR_SPACE+String.format(st.STR_16FORMAT,hint_color));
        
        rl.addView(et[id]);
        
    	ll_color.addView(rl);
    	return;
    }
    /** возвращает текст заголовка горизонтальной разметки */
    public TextView getTitleLineUserLayout(int resIdText)
    {
    	TextView tv = new TextView(inst);
    	tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
    			LinearLayout.LayoutParams.WRAP_CONTENT));
    	tv.setTextSize(15);
    	String ss = inst.getString(resIdText);
    	tv.setText(resIdText);
    	return tv;
    }

}