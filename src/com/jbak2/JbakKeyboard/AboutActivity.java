package com.jbak2.JbakKeyboard;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jbak2.CustomGraphics.ColorsGradientBack;
import com.jbak2.web.Mail;

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
        	st.copyText(inst, st.getDeviceInfo(inst).toString());
    		break;
        case R.id.about_btn_mail:
    		Mail.sendFeedback(inst);
    		break;
    	case R.id.about_btn_keycode:
        	st.runApp(inst,st.UNICODE_APP);
    		break;
    	case R.id.about_btn_diary:
            st.runActShowText(inst, R.string.diary, st.STA_FILENAME_DIARY, 
            		ShowTextAct.FLAG_HIDE_BTN_LANG|ShowTextAct.FLAG_HIDE_BTN_SEARCH);
//        	st.desc_act_ini(2);
//        	st.runAct(ShowTextAct.class,inst);
    		break;
    	case R.id.about_btn_other_app:
        	Intent intent = new Intent(Intent.ACTION_VIEW);
        	intent.setData(Uri.parse(st.ALL_APP_INMARKET));
        	startActivity(intent);
    		break;
    		}
    	}
}
