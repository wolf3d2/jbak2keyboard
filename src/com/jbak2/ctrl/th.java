package com.jbak2.ctrl;

import com.jbak2.JbakKeyboard.R;

import android.graphics.Color;

/** тема приложения. По умолчанию тёмная*/
public class th {

	public static int DEF_THEME_APP_DARK = R.style.AppTheme;
	public static int theme_interface = DEF_THEME_APP_DARK;

	public static void setThemeApplication(int app_theme)
	{
		switch (app_theme)
		{
		case 0:
		case R.style.AppThemeLight:
			theme_interface = R.style.AppThemeLight;
			break;
		case 1:
		case R.style.AppTheme:
			theme_interface = DEF_THEME_APP_DARK;
			break;
		default:
			theme_interface = DEF_THEME_APP_DARK;
		}
	}
	public static boolean isDarkThemeApp()
	{
		return theme_interface == th.DEF_THEME_APP_DARK; 
	}

}
