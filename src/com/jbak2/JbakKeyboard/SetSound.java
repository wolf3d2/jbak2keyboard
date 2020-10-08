package com.jbak2.JbakKeyboard;

import com.jbak2.ctrl.th;

import android.app.Activity;
import android.graphics.Color;
import android.media.AudioManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.os.Bundle;

public class SetSound extends Activity
{
	LinearLayout ll = null;
	Spinner sp = null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
		setTheme(th.theme_interface);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setsound);
        ll = (LinearLayout) findViewById(R.id.ss_ll);
        
        ll.addView(createTextView(R.string.ssact_key_symb));
        ll.addView(createSpinner(getKeySoundSpinnerPos(st.kse[0]),1));
        
        ll.addView(createTextView(R.string.ssact_key_left));
        ll.addView(createSpinner(getKeySoundSpinnerPos(st.kse[1]),2));
        
        ll.addView(createTextView(R.string.ssact_key_right));
        ll.addView(createSpinner(getKeySoundSpinnerPos(st.kse[2]),3));
        
        ll.addView(createTextView(R.string.ssact_key_up));
        ll.addView(createSpinner(getKeySoundSpinnerPos(st.kse[3]),4));
        
        ll.addView(createTextView(R.string.ssact_key_down));
        ll.addView(createSpinner(getKeySoundSpinnerPos(st.kse[4]),5));
        
        ll.addView(createTextView(R.string.ssact_key_space));
        ll.addView(createSpinner(getKeySoundSpinnerPos(st.kse[5]),6));
        
        ll.addView(createTextView(R.string.ssact_key_enter));
        ll.addView(createSpinner(getKeySoundSpinnerPos(st.kse[6]),7));
        
        ll.addView(createTextView(R.string.ssact_key_backspace));
        ll.addView(createSpinner(getKeySoundSpinnerPos(st.kse[7]),8));
         
        ll.addView(createTextView(R.string.ssact_key_delete));
        ll.addView(createSpinner(getKeySoundSpinnerPos(st.kse[8]),9));
        
        ll.addView(createTextView(R.string.ssact_key_shift));
        ll.addView(createSpinner(getKeySoundSpinnerPos(st.kse[9]),10));
        
        // показ рекламы
        Ads.show(this, 9);
	}
    
	public TextView createTextView(int str)
	{
		TextView tv = new TextView(this);
		tv.setTextColor(Color.GREEN);
		tv.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, 
				LinearLayout.LayoutParams.WRAP_CONTENT
				));
		tv.setTextSize(20);
		tv.setText(str);
		return tv; 
	}
	public Spinner createSpinner(int pos, int id)
	{
		Spinner sp = new Spinner(this);
		sp.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, 
				LinearLayout.LayoutParams.WRAP_CONTENT
				));
		ArrayAdapter<?> adapter = 
		ArrayAdapter.createFromResource(this, R.array.setsound, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp.setAdapter(adapter);
		sp.setSelection(pos);
		sp.setId(id);
		sp.setOnItemSelectedListener(m_itemSelection);
		return sp; 
	}
	public void onBackPressed()
	{
		if (ll!=null){
			String out = st.STR_NULL;
			for (int i=1;i<= ll.getChildCount();i++){
				sp = null;
				sp = (Spinner)ll.findViewById(i);
				if (sp!=null)
					out +=st.STR_NULL+(i-1)+"="+getArraySoundEffect(sp.getSelectedItemPosition())+";";
//					out +=st.STR_NULL+i+"="+setArraySoundEffect(i)+";";
					
			}
			st.pref().edit().putString(st.PREF_KEY_SOUND_EFFECT, out).commit();
		}

		close();
	}
    public void close() 
    {
		finish();
    }
    public int getArraySoundEffect(int spinner_pos){
    	int effect = AudioManager.FX_KEY_CLICK;
    	switch (spinner_pos)
    	{
    	case 1:effect = AudioManager.FX_FOCUS_NAVIGATION_LEFT;break; 
    	case 2:effect = AudioManager.FX_FOCUS_NAVIGATION_RIGHT;break; 
    	case 3:effect = AudioManager.FX_FOCUS_NAVIGATION_UP;break; 
    	case 4:effect = AudioManager.FX_FOCUS_NAVIGATION_DOWN;break; 
    	case 5:effect = AudioManager.FX_KEYPRESS_SPACEBAR;break; 
    	case 6:effect = AudioManager.FX_KEYPRESS_RETURN;break; 
    	case 7:effect = AudioManager.FX_KEYPRESS_DELETE;break;
    	}
    	return effect;
    }
    AdapterView.OnItemSelectedListener m_itemSelection = new AdapterView.OnItemSelectedListener()
    {
    	public void onItemSelected(AdapterView<?> parent,
    			View itemSelected, int selectedItemPosition, long selectedId) 
    	{
    		// пока пусто
    	}
    	public void onNothingSelected(AdapterView<?> parent) {
    	}
    ;};		    
    // позиция еффекта в спинере
    public int getKeySoundSpinnerPos(int effect){
    	switch (effect)
    	{
    	case AudioManager.FX_KEY_CLICK:return 0;
    	case AudioManager.FX_FOCUS_NAVIGATION_LEFT:return 1;
    	case AudioManager.FX_FOCUS_NAVIGATION_RIGHT:return 2;
    	case AudioManager.FX_FOCUS_NAVIGATION_UP:return 3;
    	case AudioManager.FX_FOCUS_NAVIGATION_DOWN:return 4;
    	case AudioManager.FX_KEYPRESS_SPACEBAR:return 5;
    	case AudioManager.FX_KEYPRESS_RETURN:return 6;
    	case AudioManager.FX_KEYPRESS_DELETE:return 7;
    	default: return 0;
    	}
    }

}