package com.jbak2.JbakKeyboard;

import java.util.Locale;

import com.jbak2.ctrl.th;
import com.jbak2.web.SiteKbd;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.preference.PreferenceManager;

public class App extends Application {
	private SharedPreferences preferences;
	private Locale locale;
	private String lang;
	public static String DEF = "default";
	public static String PREF_SYSTEM_LANG = "system_lang";

	@Override
	public void onCreate() {
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		th.theme_interface = Integer.decode(preferences.getString(st.PREF_KEY_THEME_INTERFACE_APPLICATION, st.STR_NULL+th.DEF_THEME_APP_DARK));
		th.setThemeApplication(th.theme_interface);
		lang=getResources().getConfiguration().locale.getLanguage();//.getCountry();
		preferences.edit().putString(PREF_SYSTEM_LANG, lang).commit();
		lang = preferences.getString(st.PREF_KEY_LANG_APP, DEF);	
		if (lang.equals(DEF)) {
			lang=getResources().getConfiguration().locale.getLanguage();//.getCountry();
		}
		if (lang.isEmpty())
			lang = st.getSystemLangApp(false);
		locale = new Locale(lang);
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config, null);

	}
	
	@Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		lang = preferences.getString(st.PREF_KEY_LANG_APP, DEF);	
		if (lang.equals(DEF)) {
			lang=getResources().getConfiguration().locale.getLanguage();
		}
		if (lang.isEmpty())
			lang = st.getSystemLangApp(false);
        locale = new Locale(lang);
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config, null);     
    }
		
}