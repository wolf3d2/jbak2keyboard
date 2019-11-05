package com.jbak2.JbakKeyboard;

import com.jbak2.ctrl.IntEditor;
import com.jbak2.perm.Perm;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;

public class ClipbrdSyncAct extends Activity
{
	static ClipbrdSyncAct inst;
	Button start;
	// частота записи в минутах
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clipbrd_sync_act);
        inst = this;
        if (!Perm.checkPermission(inst)) {
   			finish();
   			st.runAct(Quick_setting_act.class,inst);
        }
        CheckBox cb = (CheckBox) findViewById(R.id.cs_cb1);
        cb.setChecked(st.fl_clipbrd_btn_sync_show);
        cb.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            	st.fl_clipbrd_btn_sync_show = ((CheckBox)v).isChecked();
            }
        });
        if(android.os.Build.VERSION.SDK_INT <= 17)
        	cb.setPadding(60,0,0,0);

        cb = (CheckBox) findViewById(R.id.cs_cb2);
        cb.setChecked(st.fl_clipbrd_sync_msg);
        cb.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            	st.fl_clipbrd_sync_msg = ((CheckBox)v).isChecked();
            }
        });
        if(android.os.Build.VERSION.SDK_INT <= 17)
        	cb.setPadding(60,0,0,0);

        cb = (CheckBox) findViewById(R.id.cs_cb3);
        cb.setChecked(st.fl_sync_create_new_file);
        cb.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            	st.fl_sync_create_new_file = ((CheckBox)v).isChecked();
            }
        });
        if(android.os.Build.VERSION.SDK_INT <= 17)
        	cb.setPadding(60,0,0,0);

        final IntEditor iedur = (IntEditor)findViewById(R.id.cs_dur);
        iedur.setValue(st.cs_dur);
        iedur.setSteps(new int[]{5,50,200});
        iedur.setMinAndMax(5, 4320);
        iedur.setOnChangeValue(new IntEditor.OnChangeValue()
        {
            @Override
            public void onChangeIntValue(IntEditor edit)
            {
            	st.cs_dur = edit.getValue();
            	stopServise();
            }
        });
        inst.findViewById(R.id.cs_def_dur).setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            	iedur.setValue(st.cs_dur_def);
            	stopServise();
            }
        });

        final IntEditor iesize = (IntEditor)findViewById(R.id.cs_size);
        iesize.setValue(st.cs_size);
        iesize.setSteps(new int[]{1,1,5});
        iesize.setMinAndMax(1, 100);
        iesize.setOnChangeValue(new IntEditor.OnChangeValue()
        {
            @Override
            public void onChangeIntValue(IntEditor edit)
            {
            	st.cs_size = edit.getValue();
            	stopServise();
            }
        });
        inst.findViewById(R.id.cs_def_size).setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            	iesize.setValue(st.cs_size_def);
            	stopServise();
            }
        });
        final IntEditor ierecsize = (IntEditor)findViewById(R.id.cs_cnt);
        ierecsize.setValue(st.cs_cnt);
        ierecsize.setSteps(new int[]{1,2,5});
        ierecsize.setMinAndMax(1, 400);
        ierecsize.setOnChangeValue(new IntEditor.OnChangeValue()
        {
            @Override
            public void onChangeIntValue(IntEditor edit)
            {
            	st.cs_cnt = edit.getValue();
            	stopServise();
            }
        });
        inst.findViewById(R.id.cs_def_cnt).setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            	ierecsize.setValue(st.cs_cnt_def);
            	stopServise();
            }
        });
        start = (Button) findViewById(R.id.cs_start);
   		start.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            	if (st.fl_sync)
            		st.stopSyncServise();
            	else {
            		if (ServiceJbKbd.inst==null) {
            			st.toast(inst,R.string.cs_start_msg);
            		}
            		st.startSyncServise();
            	}
        		setTextStartButton();
            	st.pref(inst).edit().putBoolean(st.PREF_KEY_CLIPBRD_SYNC, st.fl_sync).commit();
            }
        });
   		setTextStartButton();
        Ads.show(this, 10);
	}
    public void onClick(View view) 
    {
        switch (view.getId())
        {
        case R.id.desc_btn_sellang:
            return;
        }
    }
    @Override
    public void onBackPressed()
    {
    	st.pref(inst).edit().putBoolean(st.PREF_KEY_CLIPBRD_SYNC, st.fl_sync).commit();
    	st.pref(inst).edit().putInt(st.PREF_KEY_CLIPBRD_SYNC_SIZE, st.cs_size).commit();
    	st.pref(inst).edit().putInt(st.PREF_KEY_CLIPBRD_SYNC_CNT, st.cs_cnt).commit();
    	st.pref(inst).edit().putInt(st.PREF_KEY_CLIPBRD_SYNC_DUR, st.cs_dur).commit();
    	st.pref(inst).edit().putBoolean(st.PREF_KEY_CLIPBRD_BTN_SYNC_SHOW, st.fl_clipbrd_btn_sync_show).commit();
    	st.pref(inst).edit().putBoolean(st.PREF_KEY_CLIPBRD_SYNC_MSG_SHOW, st.fl_clipbrd_sync_msg).commit();
    	st.pref(inst).edit().putBoolean(st.PREF_KEY_CLIPBRD_SYNC_CREATE_FILE, st.fl_sync_create_new_file).commit();
    	if (com_menu.inst!=null)
    		com_menu.setButtonPointTopDrawable((Button)com_menu.m_MainView.findViewById(R.id.clipboard_sync), st.fl_sync);
    	
       	if (!st.fl_pref_act)
    		st.showkbd();
 		super.onBackPressed();
    }
    public void setTextStartButton()
    {
    	if (start==null)
    		return;
    	if (st.fl_sync){
    		start.setText(R.string.cs_stop);
    	} else {
    		start.setText(R.string.cs_start);

    	}
    }
    public void stopServise()
    {
    	st.stopSyncServise();
    	setTextStartButton();
    }
    @Override
    protected void onUserLeaveHint()
    {
    	finish();
    	inst = null;
        //onBackPressed();
    }    

}