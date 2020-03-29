package com.jbak2.JbakKeyboard;

import java.util.ArrayList;

import com.jbak2.Dialog.Dlg;
import com.jbak2.ctrl.Mainmenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

public class MainmenuAct extends Activity
{
	LinearLayout llview = null;
	Mainmenu mmenu = null;
//	ScrollView sv = null;
	CheckBox cb1 = null;
	static MainmenuAct inst = null;
	
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu);
        inst = this;
        mmenu = new Mainmenu();
        llview = (LinearLayout)findViewById(R.id.mainmenu_llmain);
//        sv = (ScrollView)this.findViewById(R.id.mainmenu_sv);
        cb1 = (CheckBox) findViewById(R.id.mainmenu_cb1);
        view();
        st.toastLong(R.string.mainmenu_toast);
        Ads.show(this, 6);
	}

	public void view() 
    {
    	if (llview == null)
    		return;
    	if (llview.getChildCount()>0)
    		llview.removeAllViews();
    	if (Mainmenu.arMenu.size()==0)
    		Mainmenu.arMenu = mmenu.getDefaultItem();
    	int pos = 0;
    	for (Mainmenu mm:Mainmenu.arMenu)
    	{
    		Button btn = new Button(this);
    		btn.setText(mm.name);
    		btn.setId(pos);
    		btn.setOnLongClickListener(m_LongClickListener);
    		llview.addView(btn);
    		pos++;
    	}
    }
    View.OnLongClickListener m_LongClickListener = new View.OnLongClickListener() 
    {
        @Override
        public boolean onLongClick(final View v)
        {
        	if (cb1 == null){
        		deleteItem(v);
        		return true;
        	}
        	if (cb1.isChecked()){
        		deleteItem(v);
        	} else {
        		Dlg.yesNoDialog(inst, inst.getString(R.string.delete_q), new st.UniObserver() {
					
					@Override
					public int OnObserver(Object param1, Object param2) {
                        if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
                        	deleteItem(v);
						return 0;
					}
				});
//                GlobDialog gd = new GlobDialog(inst);
//                gd.set(R.string.delete_q, R.string.yes, R.string.no);
//                gd.setObserver(new st.UniObserver()
//                {
//                    @Override
//                    public int OnObserver(Object param1, Object param2)
//                    {
//                        if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
//                        	deleteItem(v);
//                      	return 0;
//                    }
//                });
//                gd.showAlert();
        		
        	}
        	return true;
        }
    };
    public void deleteItem(View v)
    {
    	int pos = v.getId();
    	Mainmenu mm = mmenu.getItemByIndex(pos);
    	if (mm!=null){
    		Mainmenu.arMenu.remove(mm);
    		view();
    	}
    	
    }

    public void onClick(View view) 
    {
    	switch (view.getId())
    	{
    	case R.id.mainmenu_add_btn:
    		Mainmenu mm;
        	String ar[] = new String[mmenu.getAllItem().size()];
        	ArrayList<Mainmenu> arList= new ArrayList<Mainmenu>();
        	arList.addAll(mmenu.getAllItem());
        	for (int i=0;i<arList.size();i++){
        		mm = arList.get(i);
        		ar[i] = st.STR_NULL+mm.code+st.STR_SPACE+mm.name;
        	}
            final ArrayAdapter<String> adapt = new ArrayAdapter<String>(this, 
            		R.layout.tpl_instr_list,
                    ar);
                   
            Dlg.customMenu(this, adapt, this.getString(R.string.mainmenu_setting), new st.UniObserver()
            {
                @Override
                public int OnObserver(Object param1, Object param2)
                {
                    int which = ((Integer)param1).intValue();
                    if(which>=0)
                    {
                        String txt = adapt.getItem(which);
                        String name = st.STR_NULL;
                        int f = txt.indexOf(' ');
                        if(f>0){
                        	name = txt.substring(f+1);
                            txt = txt.substring(0,f);
                        }
                    	Mainmenu mm = new Mainmenu();
        				try{
        					mm.code = Integer.valueOf(txt);
        				} catch (Throwable e){
        					mm.code = 0;
        				}
        				mm.name = name.trim();
    					if (mm.code!=0&&!mm.name.isEmpty()){
        					Mainmenu.arMenu.add(mm);
        					view();
    					}
                    }
                    return 0;
                }
            });
    		break;
    	}
    }
    @Override
    public void onBackPressed()
    {
    	String str = st.STR_NULL;
    	for (Mainmenu mm:Mainmenu.arMenu){
    		str +=st.STR_NULL+mm.code+st.STR_COMMA;
    	}
    	st.pref().edit().putString(st.PREF_KEY_MAINMENU_NEW, str).commit();
 		super.onBackPressed();
    }

}
