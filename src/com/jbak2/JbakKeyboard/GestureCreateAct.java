package com.jbak2.JbakKeyboard;

import java.util.ArrayList;

import com.jbak2.Dialog.Dlg;
import com.jbak2.JbakKeyboard.KeyboardGesture.GestureHisList;
import com.jbak2.JbakKeyboard.st.KbdGesture;
import com.jbak2.ctrl.GlobDialog;
import com.jbak2.ctrl.th;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class GestureCreateAct extends Activity
{
	LinearLayout llview_view;
	LinearLayout llview_create;
	int wrapContent = LinearLayout.LayoutParams.WRAP_CONTENT;
	Button btnCreate;
	boolean bcreate = false;
    int gc_type = 0;
    int gc_action = 0;
    EditText et = null;
	Spinner sp;
	Spinner sp2;
// флаг что есть изменения	
	boolean fl_changed = false;
	int g_id = -1;
	public static  GestureCreateAct inst = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		setTheme(th.theme_interface);
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.gesture_create);
	    inst = this;
	    createView();
        // показ рекламы
        Ads.show(this, 4);
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
	public void createView() 
	{
		arrayZeroDelete();
		if (st.gc.size() == 0&&st.gc.size() < 1)
			return;
	      LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(
	    		  LayoutParams.MATCH_PARENT , wrapContent);
	      lParams.gravity = Gravity.LEFT|Gravity.TOP;
		llview_view = (LinearLayout) findViewById(R.id.gc_ll_view);
		if (llview_view.getChildCount()>0)
			llview_view.removeAllViews();
		for (int i=0;i<st.gc.size();i++){
// клавиша
			st.tmps = getString(R.string.gesture_tv2)
					+st.STR_SPACE+st.gc.get(i).keycode;

			TextView tv = new TextView(this);
			st.tmps = getString(R.string.gesture_tv2)
					+st.STR_SPACE
					+getTextKey(st.gc.get(i).keycode);
			tv.setText(st.tmps);
		    llview_view.addView(tv, lParams);
// надпись направление жеста
//		    st.tmps = getString(R.string.gesture_tv2)
//					+st.STR_SPACE+st.gc.get(i).keycode;
		    TextView tv1 = new TextView(this);
		    tv1.setText(getString(R.string.gc_type)+st.STR_SPACE
		    		+getResources().getTextArray(R.array.gc_create)
		    		[st.gc.get(i).direction]
		    		);
		    llview_view.addView(tv1, lParams);
// надпись действие
		    TextView tv_act_txt = new TextView(this);
		    tv_act_txt.setText(getString(R.string.gc_action)
		    	+st.STR_SPACE
		    	+st.getGestureEntries(this)[st.gc.get(i).action]
		    	);
		    llview_view.addView(tv_act_txt, lParams);

// клавиша Удалить
		    Button btn_del = new Button(this);
		    btn_del.setGravity(Gravity.CENTER);
		    btn_del.setText(getText(R.string.delete));
		    btn_del.setBackgroundColor(Color.YELLOW);
		    btn_del.setId(i);
		    btn_del.setOnClickListener(new OnClickListener() {
		        @Override
				public void onClick(View v)
	    			{
	    				g_id = v.getId();
	                    GlobDialog gd = new GlobDialog(st.c());
	                    gd.set(R.string.delete_q, R.string.yes, R.string.no);
	                    gd.setObserver(new st.UniObserver()
	                    {
	                        @Override
	                        public int OnObserver(Object param1, Object param2)
	                        {
	                            if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
	                            {
	                        		llview_view = (LinearLayout) findViewById(R.id.gc_ll_view);
	                        		llview_view.removeAllViews();
	                        		st.gc.remove(g_id);
	                        		fl_changed=true;
//	                        		arrayZeroDelete();
	                        		createView();
	                            }
	                            return 0;
	                        }
	                    });
	                    gd.showAlert();
	    				
	    				
//	    				st.toast(st.STR_NULL+g_id);
	    			}});
		    llview_view.addView(btn_del, lParams);

		    TextView tv_polosa = new TextView(this);
		    tv_polosa.setGravity(Gravity.CENTER);
		    tv_polosa.setText("------");
		    llview_view.addView(tv_polosa, lParams);
		}
	}
	public void arrayZeroDelete()
	{
		boolean flag = false;
		for (int i=0;i<st.gc.size();i++){
			flag = false;
			if(st.gc.get(i).keycode != 0)
				flag = true;
			else if(st.gc.get(i).direction != 0)
				flag = true;
			else if(st.gc.get(i).action != 0)
				flag = true;
			if (flag == false) {
				st.gc.remove(i);
			}
		}
	}
	public void onBackPressed()
	{
		if (fl_changed) {
			save();
			st.toast(getString(R.string.settings_saved));
		}
	    super.onBackPressed();
	}
	public void save()
	{
		ArrayList<GestureHisList> ar = new ArrayList<GestureHisList>();
		for (int i=0;i<st.gc.size();i++){
			ar.add(st.gc.get(i));
		}
		int cnt=st.gc.size();
		st.pref().edit().putInt(st.PREF_KEY_GESTURE_CNT, -5).commit();
// записываем новые		
		for (int i=0;i<ar.size();i++)
		{
			st.pref().edit().putInt(st.PREF_KEY_GESTURE_KEY+String.valueOf(i), ar.get(i).keycode).commit();
			st.pref().edit().putInt(st.PREF_KEY_GESTURE_DIR+String.valueOf(i), ar.get(i).direction).commit();
			st.pref().edit().putInt(st.PREF_KEY_GESTURE_ID+String.valueOf(i), ar.get(i).id).commit();
			st.pref().edit().putInt(st.PREF_KEY_GESTURE_ACT+String.valueOf(i), ar.get(i).action).commit();
		}
//		for (int i=0; i<ar.size();i++){
//		    st.pref().edit().putString(st.PREF_KEY_RUNAPP+String.valueOf(i), ar.get(i)).commit();
//		}
		st.pref().edit().putInt(st.PREF_KEY_GESTURE_CNT, cnt).commit();
	}
	public void onClickAdd(View view)
	{
		llview_create = (LinearLayout) findViewById(R.id.gc_ll_all);
		btnCreate = (Button) findViewById(R.id.gc_btn_create);
	      LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(
	    		  LayoutParams.FILL_PARENT , wrapContent);
	      lParams.gravity = Gravity.LEFT|Gravity.TOP;
		if (bcreate)
			bcreate=false;
		else
			bcreate=true;
		if (bcreate) {
// нажали кнопку Добавить			
			btnCreate.setText(R.string.gesture_save);
			btnCreate.setBackgroundColor(Color.MAGENTA);
// button help
			Button btn = new Button(this);
			btn.setText("?");
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
		    	  200 , wrapContent);
		    lp.gravity = Gravity.CENTER_HORIZONTAL;
			btn.setLayoutParams(lp);
			btn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Dlg.helpDialog(inst, inst.getString(R.string.gesture_tv1));
					//st.help(R.string.gesture_tv1);
				}
			});
		    llview_create.addView(btn);

		    TextView tv1 = new TextView(this);
		    tv1.setText(R.string.gc_type);
		    llview_create.addView(tv1, lParams);
		    gc_type = 0;
		    gc_action = 0;
		    sp = new Spinner(this);
		 // Настраиваем адаптер
		    ArrayAdapter<?> adapter = 
		    	ArrayAdapter.createFromResource(this, R.array.gc_create, android.R.layout.simple_spinner_item);
		    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		    sp.setAdapter(adapter);
		    sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    	public void onItemSelected(AdapterView<?> parent,
		    			View itemSelected, int selectedItemPosition, long selectedId) {
		    		 gc_type = selectedItemPosition; 
		    	}
		    	public void onNothingSelected(AdapterView<?> parent) {
		    	}
		    });		    
		    llview_create.addView(sp, lParams);
		    TextView tv2 = new TextView(this);
		    tv2.setText(R.string.gc_action);
		    llview_create.addView(tv2, lParams);
		    
		    sp2 = new Spinner(this);
	    	CharSequence[] tmp = st.getGestureEntries(st.c());
	    	String[] ret = new String[tmp.length];
		    for (int i=0; i<tmp.length;i++) {
		    	ret[i] = (String) tmp[i];
		    }
	        // адаптер
	        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ret);
	        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        
	        sp2.setAdapter(adapter2);
	        // заголовок
	        sp2.setPrompt(getString(R.string.gc_action));
	        // выделяем элемент 
	        sp2.setSelection(0);
	        // устанавливаем обработчик нажатия
	        sp2.setOnItemSelectedListener(new OnItemSelectedListener() {
	      @Override
	      public void onItemSelected(AdapterView<?> parent, View view,
	          int position, long id) {
	        // показываем позиция нажатого элемента
	    	  gc_action = position;
//	        Toast.makeText(getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
	      }
	      @Override
	      public void onNothingSelected(AdapterView<?> arg0) {
	      }
	    });
		    llview_create.addView(sp2, lParams);
		    TextView tv = new TextView(this);
		    tv.setText(R.string.gesture_tv2);
		    llview_create.addView(tv, lParams);
			et = new EditText(this);
		    et.setMaxLines(1);
		    et.requestFocus();
		    st.showkbd();
		    llview_create.addView(et, lParams);

		} else {
//запись
			GestureHisList gc1 =new GestureHisList(0,0,0,-1);
			String sss = st.STR_NULL;
			int keycode = 0;
			if (et!=null)
				sss = et.getText().toString();
			if (sss.length()>0) {
				try
				{
					keycode = Integer.parseInt(sss);
//					keycode = sss.charAt(0);
//					if (sss.charAt(0) == '-'&&sss.length()>1)
//						keycode = st.str2int(sss,-500000,200000,"Create gesture");
				} catch(Throwable e)
		        {
					keycode=0;
					st.toast("Error. Field keycode not a number");
					return;
		        }

			}
			gc1.keycode = keycode;
			gc1.direction = gc_type;
			gc1.action=gc_action;
			gc1.id = st.gc.size()+1;
			try
			{
				st.gc.add(gc1);
        	}catch (java.lang.NullPointerException e) {
        		st.toast("\nError format");
        	}  
			llview_create.removeAllViews();
			btnCreate.setText(R.string.gesture_create_add);
			btnCreate.setBackgroundColor(Color.WHITE);
			fl_changed = true;
			arrayZeroDelete();
			createView();
		}
	}
	String getTextKey(int key)
	{
//		String ret = st.STR_NULL+key;
		String ret = st.STR_NULL;
		if (key < 0){
			for (int i=0;i<st.arGestures.size();i++){
				KbdGesture kg = st.arGestures.get(i);
//			for (KbdGesture kg:st.arGestures){
				if (kg.code == key){
					ret = this.getString(kg.nameId); 
					break;
				}
			}
			if (ret.length()== 0){
		        switch (key)
		        {
		    	case st.TXT_ED_START:ret = "to Start";break;
		    	case st.TXT_ED_FINISH:ret = "to End";break;
		    	case st.TXT_ED_HOME:ret = "Home paragrapf";break;
		    	case st.TXT_ED_END:ret = "End paragrapf";break;
		    	case st.CMD_HOTKEY:
		    	case st.TXT_HOT:ret = "HotKey";break;
		    	case st.TXT_LALT:ret = "lALT";break;
		    	case st.TXT_RALT:ret = "rALT";break;
		    	case st.TXT_CTRL:ret = "CTRL";break;
		    	case st.TXT_ED_SELECT:ret = "Select";break;
		    	case st.TXT_ED_COPY:ret = "Copy";break;
		    	case st.TXT_ED_PASTE:ret = "Paste";break;
		    	case st.TXT_ED_CUT:ret = "Cut";break;
		    	case st.TXT_ED_SELECT_ALL:ret = "Select all";break;
		    	case st.TXT_ED_COPY_ALL:ret = "Copy all";break;
		    	case st.TXT_ED_SIZE_SELECTED:ret = "Size selected";break;
		    	case st.TXT_ED_DEL:ret = "Delete";break;
		    	case st.TXT_ED_DEL_WORD:ret = "Delete word";break;
		    	case st.TXT_ED_PG_UP:ret = "Page Up";break;
		    	case st.TXT_ED_PG_DOWN:ret = "Page Down";break;
		    	case st.TXT_ED_HOME_STR:ret = "Home";break;
		    	case st.TXT_ED_END_STR:ret = "End";break;
		    	case st.REC_MACRO1:ret = "Record macro 1";break;
		    	case st.RUN_MACRO1:ret = "Run macro 1";break;
		    	case st.CLR_MACRO1:ret = "Clear macro 1";break;
		    	case st.REC_MACRO2:ret = "Record macro 2";break;
		    	case st.RUN_MACRO2:ret = "Run macro 2";break;
		    	case st.CLR_MACRO2:ret = "Clear macro 2";break;
		    	case st.CMD_CALC_SAVE:ret = "Calc: save";break;
		    	case st.CMD_CALC_LOAD:ret = "Calc: load";break;
		    	case st.CMD_MAIN_MENU:ret = "Main menu";break;
		    	case st.CMD_VOICE_RECOGNIZER:ret = "Voice recognizer";break;
		    	case st.CMD_TPL:ret = this.getString(R.string.mm_templates);break;
		    	case st.CMD_PREFERENCES:ret = this.getString(R.string.mm_settings);break;
		    	case st.CMD_CLIPBOARD:ret = this.getString(R.string.mm_multiclipboard);break;
		        }

			}
		} else {
			ret = st.STR_NULL+((char) key);
			ret.toUpperCase();
		}
		return st.STR_NULL+key+" ("+ret+")";
	}

}
