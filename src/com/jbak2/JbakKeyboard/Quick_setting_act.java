package com.jbak2.JbakKeyboard;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import com.jbak2.Dialog.Dlg;
import com.jbak2.ctrl.GlobDialog;
import com.jbak2.ctrl.IniFile;
import com.jbak2.perm.Perm;
import com.jbak2.web.SiteKbd;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ArrayAdapter;
import android.widget.Button;

public class Quick_setting_act extends Activity
{
    File fset = null;
	public static String EXXTRA_HELP = "qs_exxtra_help";
	boolean bperm = false;
	static Quick_setting_act inst;
	static IniFile ini = null;
	int step = 0;
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quick_setting_act);
        inst = this;
        //st.fl_pref_act = true;
        Button btn = (Button) inst.findViewById(R.id.qs_desc_kbd);
        btn.setText(getString(R.string.ann)+getString(R.string.ann_info));
		btn = (Button)inst.findViewById(R.id.qs_ann_perm);
//		btn.setVisibility(View.GONE);
        GlobDialog.gbshow = false;
		ini = new IniFile(inst);
		if (!ini.createMainIniFile()) {
			ini = null;
			return;
		}
		if(ini!=null)
			readQuickSetting(ini);
        showHintButton();
//		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
//			btn.setVisibility(View.VISIBLE);
        if (!Perm.checkPermission(inst)) {
    		String str = inst.getString(R.string.perm_expl1);
    		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
    			str+=st.STR_LF+st.STR_LF+inst.getString(R.string.perm_expl3);
        	Dlg.helpDialog(inst, str, new st.UniObserver() {
				
				@Override
				public int OnObserver(Object param1, Object param2) {
		            String[] perms = Perm.getPermissionStartArray();
		            Perm.requestPermission(inst, perms, Perm.RPC);
					return 0;
				}
			});
        }
	}
    @Override
    protected void onDestroy()
    {
        inst = null;
        super.onDestroy();
    }
	@TargetApi(Build.VERSION_CODES.M)
	@Override
    public void onRequestPermissionsResult(int requestCode, String[] perm, int[] grantResults ) {
    	super.onRequestPermissionsResult(requestCode, perm, grantResults);
        //Log.d( TAG, "Permissions granted: " + permissions.toString() + " " + grantResults.toString() );
		ArrayList<String> al = new ArrayList<String>();
    	for (int i=0;i<grantResults.length;i++) {
    		if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
    			al.add(perm[i]);
    	}
    	if (al.size()>0) {
        	String[] ss = new String[al.size()];
        	ss =al.toArray(ss);
        	Perm.postRequestPermission(inst,ss);
    	}
	}
    public void onClick(View view) 
    {
    	if (GlobDialog.gbshow){
    		GlobDialog.inst.finish();
    		return;
    	}
    	int id = view.getId();
    	if (id == R.id.qs_ann_perm) {
    		String str = inst.getString(R.string.perm_expl1);
    		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
    			str+=st.STR_LF+st.STR_LF+inst.getString(R.string.perm_expl3);
    		
            st.runActShowText(inst, R.string.qs_ann_perm, str, 
            		ShowTextAct.FLAG_TEXT_IN_HELP_VARIABLE
            		|ShowTextAct.FLAG_HIDE_BTN_LANG
            		);
        	//Dlg.helpDialog(inst, str);
    		return;
    	}
    	else if (id == R.id.qs_desc_kbd) {
            st.runActShowText(inst, R.string.ann, st.STA_FILENAME_DESC_KBD, ShowTextAct.FLAG_MULTI_LANG);
    		return;
    	}
    	
    	if (!Perm.checkPermission(inst)) {
            if( !Perm.requestPermission(inst, Perm.getPermissionStartArray(), Perm.RPC)) {
            	return;
            }
    	}
    	switch (id)
        {
        case R.id.qs_about: // кроме прочего, включение режима отладки
            break;
        case R.id.qs_load_pref:
    		JbKbdPreference.backup(inst,false);
//        	if (JbKbdPreference.inst!=null) {
//        		JbKbdPreference.backup(inst,false);
//        	}
            break;
        case R.id.qs_enable_kbd:
            startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS));
            break;
        case R.id.qs_activate_kbd:
            InputMethodManager imm = (InputMethodManager)getSystemService(Service.INPUT_METHOD_SERVICE);
            imm.showInputMethodPicker();
            break;
        case R.id.qs_sel_lang:
        	// языки должны быть строго в таком порядке!
        	String[] ars1 = new String[5];
           	ars1[0] = inst.getString(R.string.qs_sel_lang_def);
           	ars1[1] = st.upFirstSymbol(new Locale("ru").getDisplayName());
           	ars1[2] = st.upFirstSymbol(new Locale("en").getDisplayName());
           	ars1[3] = st.upFirstSymbol(new Locale("es").getDisplayName());
           	ars1[4] = st.upFirstSymbol(new Locale("uk").getDisplayName());
           	
           	ArrayAdapter<String> ar1 = new ArrayAdapter<String>(this, 
           			R.layout.tpl_instr_list,
                    ars1
                    );
            

            Dlg.customMenu(inst, ar1, 
            		inst.getString(R.string.qs_sel_lang), 
            		new st.UniObserver()
            {
                @SuppressLint("NewApi")
				@Override
                public int OnObserver(Object param1, Object param2)
                {
                	int pos = ((Integer)param1).intValue();
                	String lang = App.DEF;
            		st.qs_ar[3]=pos;
                	switch (pos)
                	{
                	case 1:lang = "ru";break;
                	case 2:lang = "en";break;
                	case 3:lang = "es";break;
                	case 4:lang = "uk";break;
                	default:
                		st.qs_ar[3]=0;
                		break;
                	}
                	saveQuickSetting();
        			if (lang.equals(App.DEF)) {
        				// старый код, неправильный
//        				Locale locale = getResources().getConfiguration().locale;
//        				lang = locale.getLanguage(); 
        				lang = st.getSystemLangApp(true);
        				}
        			Locale locale = new Locale(lang);
        			Locale.setDefault(locale);
        			Configuration config = new Configuration();
        			config.locale = locale;
        			getBaseContext().getResources().updateConfiguration(config, null);
        			if (JbKbdPreference.inst!=null){
        				JbKbdPreference.inst.onConfigurationChanged(config);
            			if(!st.isHoneycomb())
            				JbKbdPreference.inst.recreate();
        				
        			}
        			st.pref().edit().putString(st.PREF_KEY_LANG_APP, lang).commit();
        			if(!st.isHoneycomb())
        				inst.recreate();
        			else
        				st.toastLong(R.string.qs_restart_app);
//        			 System.exit(0); 
                	return 0;
                }
            });
        	break;
        case R.id.qs_sel_layout:
//        	st.help(R.string.qs_btn3_help, inst);
            st.runAct(LangSetActivity.class,inst,EXXTRA_HELP,1);
            break;
        case R.id.qs_height_kbd:
        	if (st.getDisplayOrientation(inst)==Configuration.ORIENTATION_PORTRAIT)
        		st.runSetKbd(inst,st.SET_KEY_HEIGHT_PORTRAIT);
        	else
            	st.runSetKbd(inst,st.SET_KEY_HEIGHT_LANDSCAPE);
            break;
        case R.id.qs_sel_skin:
            CustomKbdDesign.updateArraySkins();
            runSetKbd(st.SET_SELECT_SKIN);
            break;
        case R.id.qs_where_ac_show:
        	String[] ars = new String[3];
        	ars[0] = inst.getString(R.string.set_key_ac_place_0);
        	ars[1] = inst.getString(R.string.set_key_ac_place_1);
        	ars[2] = inst.getString(R.string.set_key_ac_place_2);
           	ArrayAdapter<String> ar = new ArrayAdapter<String>(this, 
           			R.layout.tpl_instr_list,
                    ars
                    );
            

            Dlg.customMenu(inst, ar, 
            		inst.getString(R.string.set_key_ac_place_desc), 
            		new st.UniObserver()
            {
                @Override
                public int OnObserver(Object param1, Object param2)
                {
                	int pos = ((Integer)param1).intValue(); 
                	st.pref(inst).edit().putString(st.PREF_KEY_AC_PLACE, st.STR_NULL+pos).commit();
                	st.qs_ar[2] = 1;
                    if (pos > 0){
                    	Dlg.yesNoDialog(inst, inst.getString(R.string.qs_btn5_help), 
                    			new st.UniObserver() {
									
									@Override
									public int OnObserver(Object param1, Object param2) {
		                                if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
		                                {
		                                	st.runApp(inst,st.APP_PACKAGE_DICTIONARY, SiteKbd.SITE_KBD+SiteKbd.PAGE_DICT);
		                                }
		                            	
		                                return 0;
									}
								});
//                        GlobDialog gd1 = new GlobDialog(inst);
//                        gd1.setGravityText(Gravity.LEFT|Gravity.TOP);
//                        gd1.set(R.string.qs_btn5_help, R.string.no, R.string.yes);
//                        gd1.setObserver(new st.UniObserver()
//                        {
//                            @Override
//                            public int OnObserver(Object param1, Object param2)
//                            {
//                                if(((Integer)param1).intValue()==AlertDialog.BUTTON_NEUTRAL)
//                                {
//                                	st.runApp(inst,st.APP_PACKAGE_DICTIONARY, SiteKbd.SITE_KBD+SiteKbd.PAGE_DICT);
//                                }
//                            	
//                                return 0;
//                            }
//                        });
//                        gd1.showAlert();
                    	
                    }
    			return 0;
                }
            });
        	break;
        case R.id.qs_save:
        	endMsg();
        	saveQuickSetting();
//        	if (JbKbdPreference.inst!=null){
//        		if (JbKbdPreference.path.isEmpty())
//        			JbKbdPreference.path=st.getSettingsPath()+st.INI_PAR_INI;
//        		JbKbdPreference.saveIniParam(st.INI_QUICK_SETTING, 
//        				st.STR_NULL+st.qs_ar[0]+","+st.qs_ar[1]
//        						+","+st.qs_ar[2]+","+st.qs_ar[3]);
//        	}
        	break;
        }
        showHintButton();
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        showHintButton();
    }
    public void showHintButton() 
    {
		step = st.getRegisterKbd(inst);
		if (step > 0){
            st.createDefaultFolderApp();
    		if (ini!=null){
    			if (!ini.isFileExist()){
        			ini.create(st.getSettingsPath(), ini.PAR_INI);
    			}
    		}

		}
    	Button btn = null;
    	btn = (Button) findViewById(R.id.qs_activate_kbd);
    	if (btn!=null){
    		if (step <1){
    			btn.setText(st.STR_NULL);
    			btn.setHint(R.string.qs_btn2);
    			btn.setClickable(false);
    		} else {
    			btn.setText(R.string.qs_btn2);
    			btn.setHint(st.STR_NULL);
    			btn.setClickable(true);
    			
    		}
    	}
    	if (step<2){
    		st.qs_ar[0]=0;
    		st.qs_ar[1]=0;
    		st.qs_ar[2]=0;
    	}
    	btn = (Button) findViewById(R.id.qs_sel_lang);
    	if (btn!=null){
    		if (step <2){
    			btn.setText(st.STR_NULL);
    			btn.setHint(R.string.qs_sel_lang);
    			btn.setClickable(false);
    		} else {
    			btn.setText(R.string.qs_sel_lang);
    			btn.setHint(st.STR_NULL);
    			btn.setClickable(true);
    		}
    	}
    	btn = (Button) findViewById(R.id.qs_load_pref);
    	if (btn!=null){
    		if (step >=2){
    			String pt = st.getSettingsPath();
//    			if (JbKbdPreference.inst!=null) {
    	            //String path = JbKbdPreference.getBackupPath();
    	            fset = new File(JbKbdPreference.getBackupPath());
    	            if (fset!=null&&fset.isFile()) {
            			btn.setVisibility(View.VISIBLE);
            			btn.setClickable(true);
    	            }
//    			}
    		}
    	}
    	btn = (Button) findViewById(R.id.qs_sel_layout);
    	if (btn!=null){
    		if (step <2){
    			btn.setText(st.STR_NULL);
    			btn.setHint(R.string.qs_btn3);
    			btn.setClickable(false);
    		} else {
    			btn.setText(R.string.qs_btn3);
    			btn.setHint(st.STR_NULL);
    			btn.setClickable(true);
    		}
    	}
    	btn = (Button) findViewById(R.id.qs_height_kbd);
    	if (btn!=null){
    		if (step <2||st.qs_ar[0]!=1){
    			btn.setText(st.STR_NULL);
    			btn.setHint(R.string.qs_btn3_1);
    			btn.setClickable(false);
    		} else {
    			btn.setText(R.string.qs_btn3_1);
    			btn.setHint(st.STR_NULL);
    			btn.setClickable(true);
    		}
    	}
    	btn = (Button) findViewById(R.id.qs_sel_skin);
    	if (btn!=null){
    		if (step <2||st.qs_ar[4]!=1){
    			btn.setText(st.STR_NULL);
    			btn.setHint(R.string.qs_btn4);
    			btn.setClickable(false);
    		} else {
    			btn.setText(R.string.qs_btn4);
    			btn.setHint(st.STR_NULL);
    			btn.setClickable(true);
    		}
    	}
    	btn = (Button) findViewById(R.id.qs_where_ac_show);
    	if (btn!=null){
    		if (step <2||st.qs_ar[1]!=1){
    			btn.setText(st.STR_NULL);
    			btn.setHint(R.string.set_key_ac_place_desc);
    			btn.setClickable(false);
    		} else {
    			btn.setText(R.string.set_key_ac_place_desc);
    			btn.setHint(st.STR_NULL);
    			btn.setClickable(true);
    		}
    	}
    	btn = (Button) findViewById(R.id.qs_save);
    	if (btn!=null){
    		if (step !=2){
    			btn.setText(st.STR_NULL);
    			btn.setHint(R.string.save);
    			btn.setClickable(false);
    		} else {
    			btn.setText(R.string.save);
    			btn.setHint(st.STR_NULL);
    			btn.setClickable(true);
    		}
    	}
    }
    View.OnKeyListener etsearch_onKeyListener = new View.OnKeyListener() {
		
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) 
		{
    	    if(event.getAction() == KeyEvent.ACTION_DOWN && 
        	    	(keyCode == KeyEvent.KEYCODE_ENTER))
           		{
       				return true;
       			}
       		return false;
		}
	};
    @Override
    public void onBackPressed()
    {
//    	super.onBackPressed();
        if(JbKbdPreference.inst!=null&&st.getRegisterKbd(inst)!=2){
            JbKbdPreference.inst.finish();
            finish();
        } else{
        	if (st.getRegisterKbd(inst)==2
        			&&st.qs_ar[0]==1
        			&&st.qs_ar[1]==1
        			&&st.qs_ar[2]==1
        			){
        		endMsg();
        	} else
                finish();
        	saveQuickSetting();

        }
    }
      void runSetKbd(int action)
      {
          try{
          	if (st.getRegisterKbd(inst) < 2) {
          		st.toast(getString(R.string.kbd_warning));
          		return;
          	}
              Intent in = new Intent(Intent.ACTION_VIEW)
              .setComponent(new ComponentName(this, SetKbdActivity.class))
              .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
              .putExtra(st.SET_INTENT_ACTION, action);
              startActivity(in);
          }
          catch(Throwable e)
          {
          }
      }
      public void endMsg()
      {
      	if (GlobDialog.gbshow){
    		return;
    	}
//      	st.UniObserver obs = new st.UniObserver() {
//			
//			@Override
//			public int OnObserver(Object param1, Object param2) {
//               	finish();
//                if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
//                {
//                   	st.desc_act_ini(1);
//                  	st.runAct(Desc_act.class, inst);
//                }
//				return 0;
//			}
//		};
      	Dlg.yesNoDialog(inst, inst.getString(R.string.qs_end_msg), new st.UniObserver() {
			
			@Override
			public int OnObserver(Object param1, Object param2) {
               	finish();
                if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
                {
                    st.runActShowText(inst, R.string.ann, st.STA_FILENAME_DESC_KBD, ShowTextAct.FLAG_MULTI_LANG);
                }
				return 0;
			}
		});
//	        GlobDialog gd1 = new GlobDialog(inst);
//	        gd1.setGravityText(Gravity.LEFT|Gravity.TOP);
//	        gd1.set(R.string.qs_end_msg, R.string.no, R.string.yes);
//	        gd1.setObserver(new st.UniObserver()
//	        {
//	            @Override
//	            public int OnObserver(Object param1, Object param2)
//	            {
//	               	finish();
//	                if(((Integer)param1).intValue()==AlertDialog.BUTTON_NEUTRAL)
//	                {
//	                   	st.desc_act_ini(1);
//	                  	st.runAct(Desc_act.class, inst);
//	                }
//	                return 0;
//	            }
//	        });
//	        gd1.showAlert();
      }
      public static void saveQuickSetting()
      {
      	if (ini==null)
    		ini = new IniFile(inst);
		if (!ini.createMainIniFile()) {
			ini = null;
			return;
		}
		ini.setParam(ini.QUICK_SETTING, st.STR_NULL+st.qs_ar[0]
				+st.STR_COMMA+st.qs_ar[1]
				+st.STR_COMMA+st.qs_ar[2]
				+st.STR_COMMA+st.qs_ar[3]
				+st.STR_COMMA+st.qs_ar[4]
				);
     }
      public static void readQuickSetting(IniFile ini)
      {
    	  if (ini==null)
    		  return;
  		String par = ini.getParamValue(ini.QUICK_SETTING);
  		if (par != null) {
  			String[] ar = par.split(st.STR_COMMA);
  			int zn = 0;
  			for (int i=0;i<st.qs_ar.length;i++){
  				st.qs_ar[i]=0;
  				try{
  					zn = Integer.valueOf(ar[i]);
  					st.qs_ar[i]=zn;
  				} catch (Throwable e){
  					zn = 0;
  					st.qs_ar[i]=zn;
  				}
  			}
  		}
    	  
      }
}