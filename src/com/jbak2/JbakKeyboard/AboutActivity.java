package com.jbak2.JbakKeyboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jbak2.CustomGraphics.ColorsGradientBack;
import com.jbak2.Dialog.Dlg;
import com.jbak2.ctrl.th;
import com.jbak2.web.Mail;
import com.jbak2.web.SiteKbd;

public class AboutActivity extends Activity
{
	/** вкл/выкл режима отладки в клавиатуре <br>
	 * ДЛЯ ВКЛЮЧЕНИЯ, нужно непрерывно 10 раз нажимать на заголовок <br>
	 * Прочее в самом низу активности. <br>
	 * ДЛЯ ВЫКЛЮЧЕНИЯ - ТОЖЕ САМОЕ, НО 5 раз. <br>
	 * Любое нажатие на другую кнопку, сбрасывает счётчик нажатий */
	int debug_on = 0;
	
	static AboutActivity inst = null;
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
		// тут светлая тема не нужна
		//setTheme(ThemeApp.theme_interface);
        super.onCreate(savedInstanceState);
        inst = this;
        debug_on = 0;
        View v = getLayoutInflater().inflate(R.layout.about, null);
        v.setBackgroundDrawable(new ColorsGradientBack().setCorners(0, 0).setGap(0).getStateDrawable());
        try{
//            String vers = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            String vers = st.getAppVersionName(inst)+" ("+st.getAppVersionCode(inst)+")";
            String app = getString(R.string.about_version)+st.STR_SPACE+vers+st.STR_LF
                           +getString(R.string.about_web); 
            ((TextView)v.findViewById(R.id.version)).setText(app);
    		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    			int col = 0xff141414;
                ((TextView)v.findViewById(R.id.version)).setBackgroundColor(col);
                ((TextView)v.findViewById(R.id.about_author_desc)).setBackgroundColor(col);
                ((TextView)v.findViewById(R.id.about_item_desc)).setBackgroundColor(col);
    			
    		}

        }
        catch (Throwable e) {}
        setContentView(v);
    }
    public void onClick(View v) {
    	if (v.getId() != R.id.about_other)
    		debug_on = 0;
    	switch (v.getId())
    	{
    	case R.id.about_btn_colorpicker:
    		showPicker();
    		break;
    	case R.id.about_other:
        	debug_on++;
        	if (!st.debug_mode) {
            	if (debug_on >= 10) {
            		st.pref(inst).edit().putBoolean(st.PREF_KEY_DEBUG_MODE, true).commit();
            		debug_on = 0;
            		st.toast(inst, "Режим отладки включен");
        			if (JbKbdPreference.inst!=null)
        				JbKbdPreference.inst.recreate();
            	}
        	} else {
            	if (debug_on >= 5) {
            		st.pref(inst).edit().putBoolean(st.PREF_KEY_DEBUG_MODE, false).commit();
            		debug_on = 0;
            		st.toast(inst, "Режим отладки выключен");
        			if (JbKbdPreference.inst!=null)
        				JbKbdPreference.inst.recreate();
            	}
        	}
    		break;
    	case R.id.about_btn_copy_info:
    		final String info = st.STR_NULL+ st.getDeviceInfo(inst);
    		st.UniObserver obs = new st.UniObserver() {
				
				@Override
				public int OnObserver(Object param1, Object param2) {
					switch (((Integer)param1).intValue())
					{
					// share
					case AlertDialog.BUTTON_POSITIVE:
						st.sendShareTextIntent(inst, info);
						break;
					// copy
					case AlertDialog.BUTTON_NEUTRAL:
                    	st.copyText(inst, info);
						break;
					// ok
					case AlertDialog.BUTTON_NEGATIVE:
						break;
					}
					return 0;
				}
			};
    		String txt = inst.getString(R.string.device_info)+st.STR_LF+st.STR_LF;
    		txt += st.getDeviceInfo(inst).toString();
    		//Dlg.helpDialog(inst, info, R.string.gesture_copy, obs);
    		Dlg.yesNoCancelDialog(inst, txt, R.string.share, R.string.gesture_copy, R.string.ok, obs);
    		break;
        case R.id.about_btn_mail:
    		Mail.sendFeedback(inst);
    		break;
    	case R.id.about_btn_keycode:
        	st.runApp(inst,st.APP_PACKAGE_UNICODE, null);
    		break;
    	case R.id.about_btn_diary:
            st.runActShowText(inst, R.string.diary, st.STA_FILENAME_DIARY, 
            		ShowTextAct.FLAG_HIDE_BTN_LANG);//|ShowTextAct.FLAG_HIDE_BTN_SEARCH);
//        	st.desc_act_ini(2);
//        	st.runAct(ShowTextAct.class,inst);
    		break;
    	case R.id.about_btn_other_app:
    		String link =  SiteKbd.SITE_KBD+SiteKbd.PAGE_OTHER_APP;
    		try {

    	        Intent intent = new Intent(Intent.ACTION_VIEW);
    	        intent.setData(Uri.parse(link));
    	        inst.startActivity(intent);

    		} catch (Throwable e) {
    		}

// запуск маркета
//    		Intent intent = new Intent(Intent.ACTION_VIEW);
//        	intent.setData(Uri.parse(st.ALL_APP_INMARKET));
//        	startActivity(intent);
    		break;
    	}
    }
    /** запуск выбора цвета */
    void showPicker()
    {
    	ColorPicker m_colpic = null;
        m_colpic = (ColorPicker) getLayoutInflater().inflate(R.layout.picker, null);
        if (m_colpic != null){
   			m_colpic.show(inst, null);
        }
    }

}
