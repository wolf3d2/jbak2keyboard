package com.jbak2.JbakKeyboard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jbak2.CustomGraphics.draw;
import com.jbak2.Dialog.Dlg;
import com.jbak2.Dialog.DlgFileExplorer;
import com.jbak2.JbakKeyboard.IKeyboard.Lang;
import com.jbak2.JbakKeyboard.UpdVocabActivity;
import com.jbak2.ctrl.Font;
import com.jbak2.ctrl.GlobDialog;
import com.jbak2.ctrl.IniFile;
import com.jbak2.ctrl.IntEditor;
import com.jbak2.perm.Perm;
import com.jbak2.web.Mail;
import com.jbak2.web.SiteKbd;
import com.jbak2.words.Words;
import com.jbak2.words.WordsService;

@SuppressLint("NewApi")
public class JbKbdPreference extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	// /** время момента нажатия кнопки Да для оценки приложения */
	// long rateStart = 0;
	// long rateLen = 0;
	// String rateapp = null;

/** время старта на оставление отзыва. <br>
 * Записываем значение что оценили  в onResume, если отсутствовали в программе <br>
 * более какого-то времени */
	long review_time_start = 0;
	File file_crash;
	private static final int MAX_STACK_STRING = 8192;
	private static final String CAUSED_BY = "caused by";
	public static final String SAVE_CRASH = "/save_crash.txt";
	// имя файла для сохранения строки дополнительных жестов
	String PREF_GESTURE_DOP_SYMB_FILENAME = "gesture_string.txt";
	// // ключ, для сохранения строки значений дополнительного жеста
	// String gest_dop_symb_save = "save_gesture_dop_symb_str";
	// // ключ, для загрузки строки значений дополнительного жеста
	// String gest_dop_symb_load = "load_gesture_dop_symb_str";
	boolean intent_share = false;
	boolean fl_temp_del_spase = false;
	public static final String DEF_SIZE_CLIPBRD = "20";
	public static final String DEF_SHORT_VIBRO = "30";
	public static final String DEF_LONG_VIBRO = "15";
	public static JbKbdPreference inst;
	/** Массив списков с целыми значениями */
	IntEntry arIntEntries[];
	// *****************************************
	// параметры par.ini (см. onCreate)
	// *****************************************

	// текущая версия
	String vers = null;
	// дата и время из par.ini
	long timeini = 0;
	// текущее время
	long cur_time = 0;
	// оценивалось приложение или нет
	int rate_app = 0;
	// первая установленная версия
	String rate_start_version = st.STR_ZERO;
	// String rate_start_time="1";
	// выводить ли историю версий
	boolean new_vers = false;
	IniFile ini = null;
	// путь и имяфайла
	public static String path = st.STR_NULL;
	private Thread.UncaughtExceptionHandler androidDefaultUEH;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (ColorPicker.inst != null) {
			ColorPicker.inst.finish();
		}
		st.fl_pref_act = true;
		inst = this;
		checkCrash();
		androidDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread thread, Throwable e) {
				saveCrash(e);
				androidDefaultUEH.uncaughtException(thread, e);
			}
		});
		// просто вызываем ошибку, чтобы сработал фидбек
		// int bbb = Integer.valueOf("huk");
		// для инфы мне
		// String sss = "brand:"+Build.DISPLAY.BRAND
		// +"\nCODENAME: " + Build.VERSION.CODENAME
		// +"\nINCREMENTAL: " + Build.VERSION.INCREMENTAL
		// +"\nRELEASE: " + Build.VERSION.RELEASE
		// +"\nSDK_INT: " + Build.VERSION.SDK_INT;
		// st.help(sss);

		if (Font.tf == null)
			new Font(inst);

		// проверяем был ли послан текст для записи в буфер
		checkStartIntent();
		cur_time = new Date().getTime();
		ini = null;
		// старое место
		// preOper();

		// основной конструктор
		// вывод установленных значений
		// inst = this;
		st.getGestureAll();
		// SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
		arIntEntries = new IntEntry[] {
				new IntEntry(st.PREF_KEY_USE_SHORT_VIBRO, R.string.set_key_short_vibro_desc, R.array.vibro_short_type,
						st.STR_ONE),
				new IntEntry(st.PREF_KEY_AC_PLACE, R.string.set_key_ac_place_desc, R.array.ac_place, st.STR_ZERO),
				new IntEntry(st.PREF_KEY_PORTRAIT_TYPE, R.string.set_key_portrait_input_type_desc,
						R.array.array_input_type, st.STR_ZERO),
				new IntEntry(st.PREF_KEY_LANSCAPE_TYPE, R.string.set_key_landscape_input_type_desc,
						R.array.array_input_type, st.STR_ZERO),
				new IntEntry(st.PREF_KEY_PREVIEW_TYPE, R.string.set_ch_keys_preview_desc, R.array.pv_place, st.STR_ONE),
				new IntEntry(st.PREF_KEY_USE_VOLUME_KEYS, R.string.set_key_use_volumeKeys_desc, R.array.vk_use,
						st.STR_ZERO),
				new IntEntry(st.PREF_KEY_SOUND_VOLUME, R.string.set_key_sounds_volume_desc, R.array.integer_vals, "5"),
				new IntEntry(st.PREF_KEY_PREVIEW_WINSIZE, R.string.preview_size_win_desc, R.array.popupwndsize, "2"),
				new IntEntry(st.PREF_KEY_MINI_KBD_BTN_SIZE, R.string.pref_mini_kbd_btnsize_desc,
						R.array.array_minikbd_btn_size, st.STR_ZERO),
				new IntEntry(st.PREF_KEY_MINI_KBD_BTN_TEXT_SIZE, R.string.pref_mini_kbd_textbtnsize_desc,
						R.array.array_minikbd_btn_text_size, st.STR_ZERO), };
		st.upgradeSettings(this);
		super.onCreate(savedInstanceState);
		if (!Perm.checkPermission(inst)) {
			finish();
			st.runAct(Quick_setting_act.class, inst);
		}
		setContentView(R.layout.pref_view);
		addPreferencesFromResource(R.xml.preferences);
		setShiftState();
		setACtype();
		setEnterPict();
		setDesignElemMinusPlus();
		// p = PreferenceManager.getDefaultSharedPreferences(this);

		// p = st.pref(this);
		preOper();
		SharedPreferences p = st.pref(st.c());
		PreferenceScreen ps = getPreferenceScreen();
		Preference pr = ps.findPreference(st.PREF_KEY_SAVE);
		pr.setSummary(pr.getSummary().toString() + '\n' + getBackupPath());
		pr = getPreferenceScreen().findPreference(st.PREF_KEY_LOAD);
		pr.setSummary(pr.getSummary().toString() + '\n' + getBackupPath());
		setSummary(st.KBD_BACK_ALPHA, R.string.set_kbd_background_alpha_desc,
				strVal(p.getString(st.KBD_BACK_ALPHA, st.STR_NULL + st.KBD_BACK_ALPHA_DEF)));
		setSummary(st.KBD_BACK_PICTURE, R.string.set_kbd_background_desc,
				strVal(p.getString(st.KBD_BACK_PICTURE, st.STR_NULL)));
		setSummary(st.PREF_KEY_CLIPBRD_SIZE, R.string.set_key_clipbrd_size_desc,
				strVal(p.getString(st.PREF_KEY_CLIPBRD_SIZE, DEF_SIZE_CLIPBRD)));
		setSummary(st.SET_STR_GESTURE_DOPSYMB, R.string.gesture_popupchar_str1_desc,
				strVal(p.getString(st.SET_STR_GESTURE_DOPSYMB, st.STR_NULL)));
		setSummary(st.SET_GESTURE_LENGTH, R.string.set_key_gesture_length_desc,
				strVal(p.getString(st.SET_GESTURE_LENGTH, "100")));
		setSummary(st.SET_GESTURE_VELOCITY, R.string.set_key_gesture_vel_desc,
				strVal(p.getString(st.SET_GESTURE_VELOCITY, "150")));
		setSummary(st.MM_BTN_OFF_SIZE, R.string.mm_btnoff_size_desc, strVal(p.getString(st.MM_BTN_OFF_SIZE, "8")));
		setSummary(st.AC_LIST_VALUE, R.string.ac_list_value_desc, strVal(p.getString(st.AC_LIST_VALUE, "40")));
		setSummary(st.PREF_AC_DEFKEY, R.string.set_ac_defkey_desc,
				strVal(p.getString(st.PREF_AC_DEFKEY, st.AC_DEF_WORD)));
		setSummary(st.PREF_AC_HEIGHT, R.string.set_key_ac_height_desc,
				strVal(p.getString(st.PREF_AC_HEIGHT, st.STR_ZERO)));
//		setSummary(st.PREF_KEY_IE_DESIGN, R.string.ie_design_desc,
//				strVal(p.getString(st.PREF_KEY_IE_DESIGN, st.STR_ZERO)));
		// setSummary(st.PREF_KEY_CHECK_UPD_APP, R.string.upd_check_desc,
		// strVal(p.getString(st.PREF_AC_HEIGHT,"bbb" )));

		// Последняя редакция Как пользоваться клавиатурой для выбранного перевода
		showLastEditDeskKbdText();

		// через обращение к апк как к архиву, дату создания файла
		// выдает временем компиляции апк
		// try {
		// String apkPatch = getApplicationInfo().publicSourceDir;
		// File apkFile = new File(apkPatch);
		// ZipFile zipFile = new ZipFile(apkFile);
		// ZipEntry zze = zipFile.getEntry("assets/_ru_desc_kbd.txt");
		// long ft = zze.getTime();
		// String val = inst.getString(R.string.ann_last_edit);
		// String scurtime = "dd.MM.yyyy";
		// Date dt = new Date();
		// dt.setTime(ft);
		// SimpleDateFormat sdf = new SimpleDateFormat(scurtime);
		// val+= st.STR_SPACE+sdf.format(dt);
		//
		// setSummary(st.HOW_TO_USE_KEYBOARD, R.string.ann_desc, strVal(val));
		// } catch (Throwable e) {
		// }

		CharSequence entries[] = st.getGestureEntries(this);
		CharSequence entValues[] = st.getGestureEntryValues();
		setGestureList(p, st.PREF_KEY_GESTURE_LEFT, entries, entValues);
		setGestureList(p, st.PREF_KEY_GESTURE_RIGHT, entries, entValues);
		setGestureList(p, st.PREF_KEY_GESTURE_UP, entries, entValues);
		setGestureList(p, st.PREF_KEY_GESTURE_DOWN, entries, entValues);
		setGestureList(p, st.PREF_KEY_GESTURE_SPACE_LEFT, entries, entValues);
		setGestureList(p, st.PREF_KEY_GESTURE_SPACE_RIGHT, entries, entValues);
		setGestureList(p, st.PREF_KEY_GESTURE_SPACE_UP, entries, entValues);
		setGestureList(p, st.PREF_KEY_GESTURE_SPACE_DOWN, entries, entValues);
		int index = 0;
		for (IntEntry ie : arIntEntries) {
			index = Integer.decode(p.getString(ie.key, ie.defValue));
			setSummary(ie.key, ie.descStringId, strVal(getResources().getStringArray(ie.arrayNames)[index]));
		}

		st.pref(this).registerOnSharedPreferenceChangeListener(this);

		// выводить экран истории версий, или "как пользоваться клавиатурой"
		postOper();

		Ads.count_failed_load = 0;
		Ads.show(this, 1);
	}

	/**
	 * Устанавливаем результат проверки на новую версию
	 * 
	 * @param type
	 *            - тип, что выводить <br>
	 *            0 - пустую строку <br>
	 *            1 - есть/нет новая версия + дата последней проверки <br>
	 *            2 - строка Проверяю...
	 * @param bcheck
	 *            - проверять-ли наличие параметра ппп в массиве для обновления
	 */
	public void setCheckEntry(Context con, int type, IniFile ini) {
		if (ini == null)
			return;
		String ver = st.getAppVersionCode(con);
		String vini = ini.getParamValue(ini.LAST_CHECK_VERSION);
		if (ver == null | vini == null)
			return;
		boolean check_new_version = false;
		switch (type) {
		case 1:
			boolean err = false;
			long time = 0;
			long verini = 0;
			try {
				verini = Long.parseLong(vini);
				time = Long.parseLong(ver);
			} catch (NumberFormatException e) {
				err = true;
			}
			if (!err && time > verini)
				ver = con.getString(R.string.upd_check_desc_no);
			else if (ver.compareToIgnoreCase(vini) == 0)
				ver = con.getString(R.string.upd_check_desc_no);
			else {
				ver = con.getString(R.string.upd_check_desc_yes);
				check_new_version = true;
			}
			vini = ini.getParamValue(ini.LAST_CHECK_TIME);
			if (vini != null) {
				try {
					time = Long.parseLong(vini);
				} catch (NumberFormatException e) {
				}
				if (time != 0) {
					vini = st.getDatetime(time, "1");
					ver += st.STR_LF + con.getString(R.string.upd_last_check) + st.STR_LF + vini;
				}
				// закоментить в релизе
				// ---
				vini = ini.getParamValue(ini.LAST_TIME_TOAST_NOT_UPDATE);
				if (vini != null) {
					time = 0;
					try {
						time = Long.parseLong(vini);
					} catch (NumberFormatException e) {
					}
				}
				// режим отладки включен и есть новая версия
				if (st.debug_mode&&check_new_version)
					ver += st.STR_LF + "След. напом.: " + st.getDatetime(time + SiteKbd.TOAST_NOT_UPDATE, "1");
				// ---
			}
			break;
		case 2:
			ver = con.getString(R.string.upd_checking);
			break;
		default:
			ver = st.STR_NULL;
		}
		try {
			// setValue(st.PREF_KEY_CHECK_UPD_APP, R.string.upd_check_desc, ver);
			setSummary(st.PREF_KEY_CHECK_UPD_APP, R.string.upd_check_desc, ver);
		} catch (Throwable e) {
		}
		// setSummary(st.PREF_KEY_CHECK_UPD_APP, R.string.upd_check_desc, ver);
	}

	/**
	 * показываем дату последнего редактирования файла <br>
	 * Как пользоваться клавиатурой <br>
	 * для выбранного языка
	 */
	public void showLastEditDeskKbdText() {
		String str = st.STR_UNDERSCORING + st.getLangDescKbd() + st.STA_FILENAME_DESC_KBD;
		str = st.readAssetsTextFilename(inst, str);
		int ind = str.indexOf(ShowTextAct.LAST_EDITED_DESC_KBD);
		if (str != null && ind > -1) {
			ind = str.indexOf(st.STR_LF);
			if (ind > -1)
				str = str.substring(0, ind);
			if (str != null) {
				ind = str.indexOf(st.STR_COLON);
				if (ind > -1) {
					str = str.substring(ind + 1).trim();
					String val = inst.getString(R.string.ann_last_edit);
					val += st.STR_SPACE + str;
					str = val;
				}
				setSummary(st.HOW_TO_USE_KEYBOARD, R.string.ann_desc, strVal(str));
			}
		}

	}

	// проверка текущей версии, если отличается от записанной
	// - выводим "историю версий"
	public void preOper() {
		vers = st.getAppVersionCode(inst);
		ini = new IniFile(inst);
		if (!ini.createMainIniFile()) {
			ini = null;
			return;
		}
		setCheckEntry(inst, 1, ini);
		new SiteKbd(inst).checkVersion(ini);
		String par = null;
		rate_app = 0;
		par = ini.getParamValue(ini.RATE_APP);
		if (par != null) {
			try {
				rate_app = Integer.parseInt(par);
			} catch (NumberFormatException e) {
				rate_app = 0;
			}
		}

		//// ini.setFilename(st.getSettingsPath()+ini.PAR_INI);
		//// if (!ini.isFileExist()){
		//// if (!ini.create(st.getSettingsPath(), ini.PAR_INI))
		//// return;
		//// }
		// new_vers = false;
		// String par = null;
		// par = ini.getParamValue(ini.VERSION_CODE);
		// if (par == null)
		// new_vers = false;
		// else if (par.compareToIgnoreCase(vers)!=0)
		// new_vers = true;
		// par = ini.getParamValue(ini.START_TIME);
		// if (par == null)
		// timeini = cur_time;
		// else {
		// try {
		// timeini=Long.parseLong(par);
		// } catch (NumberFormatException e){
		// timeini=cur_time;;
		// }
		// }
		// Quick_setting_act.readQuickSetting(ini);
		//// par = ini.getParamValue(ini.QUICK_SETTING);
		//// if (par != null) {
		//// String[] ar = par.split(st.STR_COMMA);
		//// int zn = 0;
		//// for (int i=0;i<st.qs_ar.length;i++){
		//// st.qs_ar[i]=0;
		//// try{
		//// zn = Integer.valueOf(ar[1]);
		//// st.qs_ar[i]=zn;
		//// } catch (Throwable e){
		//// zn = 0;
		//// st.qs_ar[i]=zn;
		//// }
		//// }
		//// }
		// rate_app = 0;
		// par = ini.getParamValue(ini.RATE_APP);
		// if (par != null) {
		// try {
		// rate_app = Integer.parseInt(par);
		// } catch (NumberFormatException e){
		// rate_app = 0;
		// }
		// }
		// if (!ini.isParamEmpty(ini.VERSION_CODE))
		// ini.setParam(ini.VERSION_CODE, vers);
		// if (!ini.isParamEmpty(ini.RATE_APP))
		// ini.setParam(ini.RATE_APP, st.STR_ZERO);
		// if (!ini.isParamEmpty(ini.START_TIME))
		// ini.setParam(ini.START_TIME, st.STR_NULL+cur_time+ini.RATE_FIRST_TIME);
	}

	public void postOper() {
		if (st.getRegisterKbd(inst) != 2) {
			if (Quick_setting_act.inst == null)
				st.runAct(Quick_setting_act.class, inst);
		}
		// просьба оценить приложение в маркете
		// else
		// rateCheck();
		// или проверка новой версии на сайте клавы
		else {
			// просьба оценить приложение в маркете
			rateCheckSiteKbd();
			st.checkUpdate(inst, ini, false);
		}
	}

	/** просьба оставить отзыв на сайте программы, если не оставляли */
	public boolean rateCheckSiteKbd() {
		// если оценивали - выходим
		if (rate_app != 0)
			return true;
		if (ini == null)
			return true;
		long curtime = new Date().getTime();
		String param = ini.getParamValue(ini.RATE_APP);
		if (param == null) {
			ini.setParam(ini.RATE_APP, st.STR_ZERO);
			return true;
		}


//		 String scurtime = "dd.MM.yyyy HH:mm:ss";
//		 Date dt = new Date();
//		 dt.setTime(curtime);
//		 SimpleDateFormat sdf = new SimpleDateFormat(scurtime);
//		 scurtime = sdf.format(dt);
//		 String spartime = "dd.MM.yyyy HH:mm:ss";
//		 dt = new Date();
//		 dt.setTime((long) (timeini));
//		 sdf = new SimpleDateFormat(spartime);
//		 scurtime = scurtime;
//		 spartime = sdf.format(dt);

		
		// curtime и initime сравнивать не нужно - мы только выводим тост
		if (rate_app == 0&&Quick_setting_act.inst == null) {
			st.toastLong(R.string.rate_toast);
		}
		return rate_app == 1;
	}

	public boolean rateCheck() {
		// если оценивали - выходим
		if (rate_app != 0)
			return true;
		if (ini == null)
			return true;
		// long curtime = new Date().getTime();
		// String param = ini.getParamValue(ini.RATE_APP);
		// if (param == null) {
		// ini.setParam(ini.RATE_APP, st.STR_ZERO);
		// return true;
		// }

		// long initime = 0;
		// param = ini.getParamValue(ini.START_TIME);
		// if (param == null) {
		// ini.setParam(ini.START_TIME, st.STR_NULL + (long) (curtime +
		// ini.RATE_FIRST_TIME));
		// initime = curtime;
		// } else {
		// try {
		// initime = Long.parseLong(param);
		// } catch (NumberFormatException e) {
		// ini.setParam(ini.START_TIME, st.STR_NULL + (long) (curtime +
		// ini.RATE_FIRST_TIME));
		// initime = curtime;
		// ;
		// }
		// }

		// String scurtime = "dd.MM.yyyy HH:mm:ss";
		// Date dt = new Date();
		// dt.setTime(curtime);
		// SimpleDateFormat sdf = new SimpleDateFormat(scurtime);
		// scurtime = sdf.format(dt);
		// String spartime = "dd.MM.yyyy HH:mm:ss";
		// dt = new Date();
		// dt.setTime((long) (timeini));
		// sdf = new SimpleDateFormat(spartime);
		// scurtime = scurtime;
		// spartime = sdf.format(dt);

		boolean gp_exist = st.isAppInstalled(inst, st.APP_PACKAGE_GOOGLE_PLAY);
		// curtime и initime сравнивать не нужно - мы только выводим тост
		if (gp_exist && Quick_setting_act.inst == null) {
			st.toastLong(R.string.rate_toast);
		}
		return rate_app == 1;
	}

	@Override
	public void onPause() {
		// Ads.pause();
		super.onPause();
	}

	@Override
	protected void onResume() {

		// st.fl_pref_act = true;
		super.onResume();
		if (review_time_start!=0) {
			long cur = new Date().getTime();
			// если вернулись в программу меньше, чем за 20сек., то НЕ записываем 
			// что оставили отзыв
			if (cur >= review_time_start + 20000) {
				if (ini!=null)
					ini.setParam(ini.RATE_APP, st.STR_ONE);
			} else {
				st.toastLong(R.string.rate_not_toast);
			}
			review_time_start = 0;
		}
		// if (rateStart!=0) {
		// st.toast("rateStart!=0\n");
		// try {
		// if (ini!=null) {
		// rateLen = new Date().getTime();
		// rateapp = ini.getParamValue(ini.RATE_APP);
		// if (rateapp == null) {
		// st.toast("rateapp==null");
		// ini.setParam(ini.RATE_APP, st.STR_ZERO);
		// rateStart = 0;
		// } else {
		// st.toast("rateapp!=null");
		// // если приложение уже оценивалось, то длительность времени
		// // проведенного в маркете не проверяем, а сразу пишем
		// if (rateapp.compareTo(st.STR_ONE)==0) {
		// ini.setParam(ini.RATE_APP, st.STR_ONE);
		// rateStart = 0;
		// }
		// // время потраченное на оценку - если больше текущего,
		// // то пишем в RATE_APP
		// else if ((long)(rateStart+15000) >= rateLen+15000) {
		// ini.setParam(ini.RATE_APP, st.STR_ONE);
		// rateStart = 0;
		// }
		// }
		//
		// }
		// } catch (Throwable e) {
		// rateStart = 0;
		// }
		// }

		Ads.show(this, 1);

		// основной код
		showHelper();
	}

	// копирование выделенного текста из системной кнопки share
	@SuppressLint("NewApi")
	public void checkStartIntent() {
		intent_share = false;
		Intent it = new Intent();
		it = getIntent();
		String type = it.getType();
		String action = it.getAction();
		if (Intent.ACTION_SEND.equals(action) && type != null) {
			if ("text/plain".equals(type)) {
				String txt = it.getStringExtra(Intent.EXTRA_TEXT);
				if (txt == null)
					return;
				st.stor().saveClipboardString(txt, 0);
				int currentapiVersion = android.os.Build.VERSION.SDK_INT;
				if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
					ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
					ClipData clip = ClipData.newPlainText("label", txt);
					clipboard.setPrimaryClip(clip);
				} else {
					ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
					clipboard.setText(txt);
				}
				st.toast(getString(R.string.menu_copy));
				finish();
				intent_share = true;
				st.toast(getString(R.string.menu_copy));
			}
		} else if (type != null && Intent.ACTION_VIEW.equals(action) || Intent.ACTION_VIEW.equals(action)) {
			// if (!"text/plain".equals(type))
			// return;
			path = null;
			Uri data = it.getData();
			if (data != null) {
				String scheme = data.getScheme();
				if (ContentResolver.SCHEME_FILE.equals(scheme)) {
					path = Uri.decode(data.getEncodedPath());
				} else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
					ContentResolver cr = getContentResolver();
					InputStream fi = null;
					int start = 0;
					byte buf[] = null;
					try {
						fi = cr.openInputStream(data);
						buf = new byte[fi.available()];
						fi.read(buf);
						if (buf.length > 3 && buf[0] == 0xef && buf[1] == 0xbb && buf[2] == 0xbf) {
							start = 3;
						}
						fi.close();
					} catch (IOException e) {
						st.log("error");
						st.logEx(e);
					}
					path = ShowTextAct.START_FILENAME_DESCRIPTOR + new String(buf, start, buf.length - start);
				} else {
					path = data.toString();
				}
			} else {
			}
			if (path != null) {
				st.runActShowText(inst, R.string.tpl_full_edit, path,
						ShowTextAct.FLAG_EXTERNAL_FILE_EDIT | ShowTextAct.FLAG_HIDE_BTN_LANG

				);
			}
		}
	}

	void showHelper() {
		Preference pr = getPreferenceScreen().findPreference("helper");
		if (pr == null)
			return;
		int step = st.getRegisterKbd(inst);
		if (step == 1) {

		}
		// Предлагаем включить клавиатуру в настройках
		if (step == 0) {
			pr.setTitle(R.string.helper_1);
			pr.setSummary(getString(R.string.helper_1_desc) + " \"" + getString(R.string.ime_name) + "\"");
		} else if (step == 1) {
			pr.setTitle(R.string.helper_2);
			pr.setSummary(getString(R.string.helper_1_desc) + " \"" + getString(R.string.ime_name) + "\"");
		}
		if (step == 2)
			getPreferenceScreen().removePreference(pr);
		else
			pr.setOnPreferenceClickListener(getHelperListener(step));
	}

	/** НЕ ИСПОЛЬЗУЕТСЯ! Но работает. Запуск выбора цвета */
	void showPicker() {
		ColorPicker m_colpic = null;
		m_colpic = (ColorPicker) getLayoutInflater().inflate(R.layout.picker, null);
		if (m_colpic != null) {
			m_colpic.show(inst, null);
		}
	}

	OnPreferenceClickListener getHelperListener(final int step) {
		return new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				if (step == 0)
					startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS));
				else if (step == 1) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
					imm.showInputMethodPicker();
				}
				return true;
			}
		};
	}

	void setGestureList(SharedPreferences p, final String set, CharSequence entries[], CharSequence entValues[]) {
		ListPreference lp = (ListPreference) getPreferenceScreen().findPreference(set);
		if (lp != null) {
			String def = st.getGestureDefault(set);
			String s = p.getString(set, def);
			String sss = st.STR_NULL;
			int index = st.getGestureIndexBySetting(s);
			if (entries == null || entries.length == 0) {
				if (set.compareTo(st.PREF_KEY_GESTURE_SPACE_DOWN) == 0) {
					sss = strVal(st.getGestureEntries(this)[index].toString()) + st.STR_LF;
					sss += inst.getString(R.string.gesture_space_down_desc);
					lp.setSummary(sss);
				} else
					lp.setSummary(strVal(st.getGestureEntries(this)[index].toString()));
				return;
			}
			lp.setEntries(entries);
			lp.setEntryValues(entValues);
			lp.setValueIndex(index);
			if (set.compareTo(st.PREF_KEY_GESTURE_SPACE_DOWN) == 0) {
				sss = strVal(entries[index].toString()) + st.STR_LF;
				sss += inst.getString(R.string.gesture_space_down_desc);
				lp.setSummary(sss);
			} else
				lp.setSummary(strVal(entries[index].toString()));
		}
	}

	final String strVal(String src) {
		if (src.length() < 1)
			return src;
		return "[ " + src + " ]";
	}

	@Override
	protected void onDestroy() {
		inst = null;
		st.pref(this).unregisterOnSharedPreferenceChangeListener(this);
		if (JbKbdView.inst != null)
			JbKbdView.inst.setPreferences();
		Ads.destroy();
		super.onDestroy();
		st.sleep(100);
	}

	// void runSetKbd(int action, Context c)
	// {
	// try{
	// if (st.getRegisterKbd(c) < 2) {
	// st.toast(c.getString(R.string.kbd_warning));
	// return;
	// }
	// Intent in = new Intent(Intent.ACTION_VIEW)
	// .setComponent(new ComponentName(this, SetKbdActivity.class))
	// .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
	// .putExtra(st.SET_INTENT_ACTION, action);
	// startActivity(in);
	// }
	// catch(Throwable e)
	// {
	// }
	//
	// }

	//
	@SuppressWarnings("deprecation")
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		inst = this;
		if (!Perm.checkPermission(inst)) {
			st.runAct(Quick_setting_act.class, inst);
			return true;
		}

		String k = preference.getKey();
		Context c = this;
		if ("pref_ac_height".equals(k)) {
			showAcHeight();
			return true;
		}
		// else if("calc_corr_ind".equals(k))
		// {
		// showCalcHeightCorrInd();
		// return true;
		// }
		else if ("gesture_clear".equals(k)) {
			showGestureClear();
			return true;
		} else if ("show_picker".equals(k)) {
			showPicker();
			return true;
		} else if ("mini_keyboard_help".equals(k)) {
			Dlg.helpDialog(inst, R.string.pref_mini_kbd_help);
			return true;
		} else if ("empty_dict".equals(k)) {
			showEmptyDict();
			return true;
		} else if ("system_setting_unknown_source".equals(k)) {
			try {
				Intent intent = new Intent();
				intent.setAction(Settings.ACTION_SECURITY_SETTINGS);
				startActivity(intent);
			} catch (Throwable e) {
			}
		} else if ("check_upd_app".equals(k)) {
			if (!SiteKbd.bcheck_backgraund) {
				Dlg.helpDialog(inst, inst.getString(R.string.upd_checking_before_dlg), new st.UniObserver() {

					@Override
					public int OnObserver(Object param1, Object param2) {
						SiteKbd.autocheck = true;
						st.checkUpdate(inst, ini, true);
						inst.recreate();
						return 0;
					}
				});
			}
		} else if ("review_application".equals(k)) {
			Intent in = new Intent(Intent.ACTION_VIEW);
			in.setData(Uri.parse(SiteKbd.SITE_KBD + SiteKbd.PAGE_REVIEW));
			startActivity(in);
			// записываем что отзыв оставил в onResume
			review_time_start = new Date().getTime();

		} else if ("go_on_site".equals(k)) {
			Intent in = new Intent(Intent.ACTION_VIEW);
			in.setData(Uri.parse(SiteKbd.SITE_KBD));
			startActivity(in);

		}
		// УЖЕ НЕ ИСПОЛЬЗУЕТСЯ
		// else if("pref_ac_sub_panel".equals(k))
		// {
		// Dlg.helpDialog(inst, inst.getString(R.string.qs_restart_app),new
		// st.UniObserver() {
		//
		// @Override
		// public int OnObserver(Object param1, Object param2) {
		// st.exitApp();
		// return 0;
		// }
		// });
		// }
		else if ("show_kbd_notif".equals(k)) {
			if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
				st.toast(inst, R.string.honeycomb);
				return false;
			}
		} else if ("kbd_background_alpha".equals(k)) {
			showAlpha();
			return true;
		} else if ("kbd_background_pict".equals(k)) {
			String txt = null;
			if (st.kbd_back_pict != null && st.kbd_back_pict.length() > 0)
				txt = inst.getString(R.string.cancel_kbd_background);
			new DlgFileExplorer(inst, inst.getString(R.string.set_kbd_background), DlgFileExplorer.PICTURE_EXT,
					DlgFileExplorer.TYPE_PICTURE, null, txt, DlgFileExplorer.SELECT_FILE) {
				@Override
				public void onSelected(File file) {
					if (file == null)
						st.kbd_back_pict = st.STR_NULL;
					else
						st.kbd_back_pict = file.getAbsolutePath();
					st.pref().edit().putString(st.KBD_BACK_PICTURE, st.kbd_back_pict).commit();
					if (st.kv() != null)
						st.kv().reloadSkin();
					SharedPreferences p = st.pref(st.c());
					setSummary(st.KBD_BACK_PICTURE, R.string.set_kbd_background_desc,
							strVal(st.pref(st.c()).getString(st.KBD_BACK_PICTURE, st.STR_NULL)));

				}
			}.show();
			return true;
		} else if ("rate_application".equals(k)) {
			rateDialog();
			return true;
		} else if ("clipbrd_sync".equals(k)) {
			st.runAct(ClipbrdSyncAct.class, c);
			return true;
		} else if ("clipboard_size".equals(k)) {
			showClipboardSize();
			return true;
		} else if ("ac_defkey".equals(k)) {
			showACDefaultWord();
			return true;
		} else if ("g_str_additional1".equals(k)) {
			showAdditionalString();
			return true;
		} else if ("ac_list_value".equals(k)) {
			showAcCountWord();
			return true;
		} else if ("quick_setting".equals(k)) {
			st.runAct(Quick_setting_act.class, c);
			return true;
		} else if ("skin_constructor".equals(k)) {
			st.runAct(SkinConstructorAct.class, c);
			return true;
		} else if ("set_sound".equals(k)) {
			st.runAct(SetSound.class, c);
		} else if ("ac_load_vocab".equals(k)) {
			st.runAct(UpdVocabActivity.class, c);
		} else if ("vibro_durations".equals(k)) {
			showVibroDuration();
		} else if ("intervals".equals(k)) {
			showIntervalsEditor();
		} else if ("pop_txt_color".equals(k)) {
			showTextColorPopupWindowValsEditor();
		} else if ("pop_back_color".equals(k)) {
			showBackColorPopupWindowValsEditor();
		} else if ("default_setting".equals(k)) {
			Dlg.yesNoDialog(inst, getString(R.string.are_you_sure), new st.UniObserver() {
				@Override
				public int OnObserver(Object param1, Object param2) {
					if (((Integer) param1).intValue() == AlertDialog.BUTTON_POSITIVE) {
						st.pref().edit().clear().commit();
						System.exit(0);
						st.toast(R.string.ok);
					}
					return 0;
				}
			});

			// GlobDialog gd = new GlobDialog(st.c());
			// gd.set(R.string.are_you_sure, R.string.yes, R.string.no);
			// gd.setObserver(new st.UniObserver()
			// {
			// @Override
			// public int OnObserver(Object param1, Object param2)
			// {
			// if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
			// {
			// st.pref().edit().clear().commit();
			// st.toast(R.string.ok);
			// }
			// return 0;
			// }
			// });
			// gd.showAlert();
		} else if (st.PREF_KEY_LOAD.equals(k)) {
			if (st.getRegisterKbd(inst) < 2)
				st.toast(getString(R.string.kbd_warning));
			else
				backup(inst, false);
		} else if (st.PREF_KEY_SAVE.equals(k)) {
			if (st.getRegisterKbd(inst) < 2)
				st.toast(getString(R.string.kbd_warning));
			else
				backup(inst, true);
		} else if ("set_skins".equals(k)) {
			String err = CustomKbdDesign.updateArraySkins();
			if (err.length() > 0) {
				Toast.makeText(this, err, 1000).show();
			}
			st.runSetKbd(inst, st.SET_SELECT_SKIN);
			return true;
		} else if ("pref_calib_portrait".equals(k)) {
			st.runSetKbd(inst, st.SET_KEY_CALIBRATE_PORTRAIT);
			return true;
		} else if ("pref_calib_landscape".equals(k)) {
			st.runSetKbd(inst, st.SET_KEY_CALIBRATE_LANDSCAPE);
			return true;
		} else if ("pref_port_key_height".equals(k)) {
			st.runSetKbd(inst, st.SET_KEY_HEIGHT_PORTRAIT);
			return true;
		} else if ("pref_land_key_height".equals(k)) {
			st.runSetKbd(inst, st.SET_KEY_HEIGHT_LANDSCAPE);
			return true;
		} else if ("save_pop2_str".equals(k)) {
			File f = new File(st.getSettingsPath() + "/gesture_string.txt");
			if (f.exists()) {
				GlobDialog gd = new GlobDialog(st.c());
				gd.set(R.string.pop2s_file_not_empty, R.string.yes, R.string.no);
				gd.setObserver(new st.UniObserver() {
					@Override
					public int OnObserver(Object param1, Object param2) {
						if (((Integer) param1).intValue() == AlertDialog.BUTTON_POSITIVE) {
							savePop2str();
						}
						return 0;
					}
				});
				gd.showAlert();
			} else
				savePop2str();
			return true;
		} else if ("load_pop2_str".equals(k)) {

			if (st.gesture_str.length() > 0) {
				GlobDialog gd = new GlobDialog(st.c());
				gd.set(R.string.pop2s_str_not_empty, R.string.yes, R.string.no);
				gd.setObserver(new st.UniObserver() {
					@Override
					public int OnObserver(Object param1, Object param2) {
						if (((Integer) param1).intValue() == AlertDialog.BUTTON_POSITIVE) {
							loadPop2str();
						}
						return 0;
					}
				});
				gd.showAlert();
			} else
				loadPop2str();
			return true;
		}
		// вставка пробела после символов
		else if ("space_sentence".equals(k)) {

			// GlobDialog gd = new GlobDialog(st.c());
			// gd.set(R.string.calc_load_prg_msg, R.string.yes, R.string.no);
			// gd.setObserver(new st.UniObserver()
			// {
			// @Override
			// public int OnObserver(Object param1, Object param2)
			// {
			// if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
			// {
			//
			// }
			// return 0;
			// }
			// });
			// gd.showAlert();
			// return true;
		}
		// else if("rate_app".equals(k))
		// {
		// Intent intent = new Intent(Intent.ACTION_VIEW);
		// intent.setData(Uri.parse("market://details?id=com.jbak2.JbakKeyboard"));
		// startActivity(intent);
		// }
		else if ("jbak2layout_app".equals(k)) {
			Intent in = new Intent(Intent.ACTION_VIEW);
			in.setData(Uri.parse(SiteKbd.SITE_KBD + SiteKbd.PAGE_ADDITIONA_COMPONENT));
			startActivity(in);
			// страничка в маркете
			// Intent intent = new Intent(Intent.ACTION_VIEW);
			// intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.jbak2.layout"));
			// startActivity(intent);
		} else if ("jbak2skin_app".equals(k)) {
			Intent in = new Intent(Intent.ACTION_VIEW);
			in.setData(Uri.parse(SiteKbd.SITE_KBD + SiteKbd.PAGE_ADDITIONA_COMPONENT));
			startActivity(in);

			// страничка в маркете
			// Intent intent = new Intent(Intent.ACTION_VIEW);
			// intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.jbak2.skin"));
			// startActivity(intent);
		} else if ("set_key_main_font".equals(k)) {
			c.startActivity(new Intent(c, EditSetActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
					.putExtra(EditSetActivity.EXTRA_PREF_KEY, st.PREF_KEY_MAIN_FONT)
					.putExtra(EditSetActivity.EXTRA_DEFAULT_EDIT_SET, draw.paint().getDefaultMain().toString()));

		} else if ("set_key_second_font".equals(k)) {
			c.startActivity(new Intent(c, EditSetActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
					.putExtra(EditSetActivity.EXTRA_PREF_KEY, st.PREF_KEY_SECOND_FONT)
					.putExtra(EditSetActivity.EXTRA_DEFAULT_EDIT_SET, draw.paint().getDefaultSecond().toString()));

		} else if ("set_key_label_font".equals(k)) {
			c.startActivity(new Intent(c, EditSetActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
					.putExtra(EditSetActivity.EXTRA_PREF_KEY, st.PREF_KEY_LABEL_FONT)
					.putExtra(EditSetActivity.EXTRA_DEFAULT_EDIT_SET, draw.paint().getDefaultLabel().toString()));

		} else if ("pref_languages".equals(k)) {
			return st.startSetLangActivity(inst);
			// старый код. Закоментил 04.10.19
			// if (!isKbdRegister()) {
			// return false;
			// }
			// st.runAct(LangSetActivity.class,c);
			// return true;
		} else if ("fs_editor_set".equals(k)) {
			getApplicationContext().startActivity(
					new Intent(getApplicationContext(), EditSetActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
							.putExtra(EditSetActivity.EXTRA_PREF_KEY, st.PREF_KEY_EDIT_SETTINGS));
			return true;
		} else if ("ac_font".equals(k)) {
			getApplicationContext().startActivity(new Intent(getApplicationContext(), EditSetActivity.class)
					.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
					.putExtra(EditSetActivity.EXTRA_PREF_KEY, st.PREF_KEY_FONT_PANEL_AUTOCOMPLETE)
					.putExtra(EditSetActivity.EXTRA_DEFAULT_EDIT_SET, CandView.getDefaultEditSet(this).toString()));
			return true;
		} else if ("edit_user_vocab".equals(k)) {
			vocabTest();
			st.runAct(EditUserVocab.class, c);
			return true;
		} else if ("annotation".equals(k)) {
			vocabTest();
			st.runActShowText(c, R.string.ann, st.STA_FILENAME_DESC_KBD, ShowTextAct.FLAG_MULTI_LANG);
			return true;
		} else if ("dict_app".equals(k)) {
			vocabTest();
			st.runApp(inst, st.APP_PACKAGE_DICTIONARY, SiteKbd.SITE_KBD + SiteKbd.PAGE_DICT);
			return true;
		}
		// моя старая Новая загрузка словарей
		// else if("dict_app".equals(k))
		// {
		// vocabTest();
		// st.runAct(NewDictionary.class,c);
		// return true;
		// }
		else if ("about_app".equals(k)) {
			vocabTest();
			st.runAct(AboutActivity.class, c);
			return true;
		} else if ("mainmenu_setting".equals(k)) {
			if (!isKbdRegister()) {
				return false;
			}
			vocabTest();
			st.runAct(MainmenuAct.class, c);
			return true;
		} else if ("gesture_create".equals(k)) {
			vocabTest();
			st.runAct(GestureCreateAct.class, c);
			return true;
		} else if ("ac_key_color".equals(k)) {
			if (!isKbdRegister()) {
				return false;
			}
			vocabTest();
			st.runAct(AcColorAct.class, c);
			return true;
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	void setSummary(String prefName, int summaryStr, String value) {
		Preference p = getPreferenceScreen().findPreference(prefName);
		if (p != null) {
			String summary;
			if (summaryStr == 0) {
				summary = value;
			} else {
				if (value != null && value.length() > 0) {
					summary = value + "\n" + getString(summaryStr);
				} else {
					summary = value + getString(summaryStr);
				}

			}

			p.setSummary(summary);
		}
	}

	void setDesignElemMinusPlus() {
		int v = Integer.decode(st.pref(this).getString(st.PREF_KEY_IE_DESIGN, st.STR_ZERO));
		setSummary(st.PREF_KEY_IE_DESIGN, R.string.ie_design_desc, getResources().getStringArray(R.array.ie_list)[v]);
	}

	void setShiftState() {
		int v = Integer.decode(st.pref(this).getString(st.PREF_KEY_SHIFT_STATE, st.STR_ZERO));
		setSummary(st.PREF_KEY_SHIFT_STATE, 0, getResources().getStringArray(R.array.array_shift_vars)[v]);
	}

	void setACtype() {
		st.type_ac_window = Integer
				.decode(st.pref(this).getString(st.PREF_AC_WINDOW_TYPE, st.STR_NULL + st.TYPE_AC_METHOD2));
		setSummary(st.PREF_AC_WINDOW_TYPE, R.string.set_key_ac_place_sub_panel_desc,
				strVal(getResources().getStringArray(R.array.array_type_ac_place)[st.type_ac_window]));
	}

	/** устанавливаем подпись под пунктом меню про изображение на ентере */
	void setEnterPict() {
		int v = Integer.decode(st.pref(this).getString(st.PREF_ENTER_PICT, st.STR_ZERO));
		setSummary(st.PREF_ENTER_PICT, 0, getResources().getStringArray(R.array.array_enter_pict)[v]);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (st.PREF_KEY_GESTURE_LEFT.equals(key) || st.PREF_KEY_GESTURE_RIGHT.equals(key)
				|| st.PREF_KEY_GESTURE_UP.equals(key) || st.PREF_KEY_GESTURE_DOWN.equals(key)
				|| st.PREF_KEY_GESTURE_SPACE_LEFT.equals(key) || st.PREF_KEY_GESTURE_SPACE_RIGHT.equals(key)
				|| st.PREF_KEY_GESTURE_SPACE_UP.equals(key) || st.PREF_KEY_GESTURE_SPACE_DOWN.equals(key)) {
			JbKbdView.inst = null;
			setGestureList(sharedPreferences, key, null, null);
		}
		if (st.PREF_KEY_USE_GESTURES.equals(key))
			JbKbdView.inst = null;
		if (st.PREF_KEY_IE_DESIGN.equals(key))
			setDesignElemMinusPlus();
		if (st.PREF_KEY_SHIFT_STATE.equals(key))
			setShiftState();
		if (st.PREF_AC_WINDOW_TYPE.equals(key)) {
			setACtype();
			Dlg.helpDialog(inst, inst.getString(R.string.qs_restart_app), new st.UniObserver() {

				@Override
				public int OnObserver(Object param1, Object param2) {
					st.exitApp();
					return 0;
				}
			});
		}
		if (st.PREF_KEY_DESC_LANG_KBD.equals(key))
			showLastEditDeskKbdText();
		else if (st.PREF_ENTER_PICT.equals(key))
			setEnterPict();
		for (IntEntry ie : arIntEntries) {
			if (ie.key.equals(key)) {
				if (st.PREF_KEY_AC_PLACE.equals(key)) {
					Dlg.helpDialog(inst, inst.getString(R.string.qs_restart_app), new st.UniObserver() {

						@Override
						public int OnObserver(Object param1, Object param2) {
							st.exitApp();
							return 0;
						}
					});
				}

				int index = Integer.decode(sharedPreferences.getString(key, ie.defValue));
				setSummary(key, ie.descStringId, strVal(getResources().getStringArray(ie.arrayNames)[index]));
				break;
			}
		}
		if (st.SET_GESTURE_LENGTH.equals(key)) {
			setSummary(key, R.string.set_kbd_background_desc, st.STR_NULL);
		}
		// вывод установленних значений под строкой настройки (не массивов!)
		if (st.PREF_KEY_CLIPBRD_SIZE.equals(key)) {
			if (checkIntValue(key, DEF_SIZE_CLIPBRD)) {
				setValue(key, R.string.set_key_clipbrd_size_desc, DEF_SIZE_CLIPBRD);

			}
		}
		if (st.KBD_BACK_ALPHA.equals(key)) {
			setValue(key, R.string.set_kbd_background_alpha_desc, st.STR_NULL + st.KBD_BACK_ALPHA_DEF);
		}
		if (st.PREF_AC_DEFKEY.equals(key)) {
			Preference p = getPreferenceScreen().findPreference(key);
			if (p.getSharedPreferences().getString(key, st.AC_DEF_WORD).trim().isEmpty())
				p.getEditor().putString(key, st.AC_DEF_WORD).commit();
			setValue(key, R.string.set_ac_defkey_desc, st.AC_DEF_WORD);
		}
		if (st.PREF_AC_HEIGHT.equals(key)) {
			setValue(key, R.string.set_key_ac_height_desc, st.STR_ZERO);
		}
		// setSummary(st.AC_HEIGHT, R.string.set_key_ac_height_desc,
		// strVal(p.getString(st.AC_HEIGHT,st.STR_ZERO )));

		if (st.SET_GESTURE_LENGTH.equals(key)) {
			setValue(key, R.string.set_key_gesture_length_desc, "100");
		}
		if (st.SET_GESTURE_VELOCITY.equals(key)) {
			setValue(key, R.string.set_key_gesture_vel_desc, "150");
		}
		if (st.AC_LIST_VALUE.equals(key)) {
			setValue(key, R.string.ac_list_value_desc, "20");
		}
		if (st.SET_STR_GESTURE_DOPSYMB.equals(key)) {
			setValue(key, R.string.gesture_popupchar_str1_desc, st.STR_NULL);
		}
		if (st.MM_BTN_SIZE.equals(key)) {
			setValue(key, R.string.mm_btn_size_desc, "15");
		}
		if (st.MM_BTN_OFF_SIZE.equals(key)) {
			setValue(key, R.string.mm_btnoff_size_desc, "8");
		}
	}

	// вывод текущего значения параметра НЕ МАССИВА, в виде [value]\ntext
	void setValue(String key, int id, String defValue) {
		try {
			setSummary(key, id, strVal(st.pref(this).getString(key, defValue)));
		} catch (Throwable e) {
		}
	}

	boolean checkIntValue(String key, String defValue) {
		String v = st.pref(this).getString(key, st.STR_ZERO);
		boolean bOk = true;
		for (int i = v.length() - 1; i >= 0; i--) {
			if (!Character.isDigit(v.charAt(i))) {
				bOk = false;
				break;
			}
		}
		if (!bOk) {
			Toast.makeText(this, "Incorrect integer value!", 700).show();
			st.pref(this).edit().putString(key, defValue).commit();
		}
		return bOk;
	}

	/** ��������� ���������� ������� */
	void showIntervalsEditor() {
		final View v = getLayoutInflater().inflate(R.layout.edit_intervals, null);
		int max = 5000, min = 50;
		// int steps[] = new int[]{50,100,100};
		int steps[] = new int[] { 10, 10, 10 };
		final SharedPreferences p = st.pref(this);

		String title = st.STR_SPACE;
		// ((TextView)v.findViewById(R.id.ei_title)).setText(R.string.set_key_intervals);

		IntEditor ie = null;
		ie = (IntEditor) v.findViewById(R.id.long_press);
		ie.setMinAndMax(min, max);
		ie.setValue(p.getInt(st.PREF_KEY_LONG_PRESS_INTERVAL, 500));
		ie.setSteps(steps);
		title += st.STR_NULL + 500 + st.STR_COMMA;

		ie = (IntEditor) v.findViewById(R.id.first_repeat);
		min = 50;
		ie.setMinAndMax(min, max);
		ie.setValue(p.getInt(st.PREF_KEY_REPEAT_FIRST_INTERVAL, 400));
		ie.setSteps(steps);
		title += st.STR_NULL + 400 + st.STR_COMMA;

		ie = (IntEditor) v.findViewById(R.id.next_repeat);
		min = 20;
		ie.setMinAndMax(min, max);
		ie.setValue(p.getInt(st.PREF_KEY_REPEAT_NEXT_INTERVAL, 50));
		ie.setSteps(steps);
		title += st.STR_NULL + 50 + st.STR_COMMA;

		((TextView) v.findViewById(R.id.interval4)).setVisibility(View.VISIBLE);
		ie = (IntEditor) v.findViewById(R.id.min_press);
		ie.setVisibility(View.VISIBLE);
		min = 1;
		max = 300;
		ie.setMinAndMax(min, max);
		ie.setValue(p.getInt(st.PREF_KEY_MINIMAL_PRESS_INTERVAL, min));
		ie.setSteps(new int[] { 1, 5, 20 });
		title += st.STR_NULL + 1;
		if (inst != null) {
			try {
				title = inst.getString(R.string.set_key_intervals) + st.STR_LF + "("
						+ inst.getString(R.string.intervals_keys_title_def_value) + title + ")" + st.STR_LF;
				((TextView) v.findViewById(R.id.ei_title)).setText(title);

			} catch (Exception e) {
				((TextView) v.findViewById(R.id.ei_title)).setText(R.string.set_key_intervals);
			}

		} else
			((TextView) v.findViewById(R.id.ei_title)).setText(R.string.set_key_intervals);

		st.UniObserver obs = new st.UniObserver() {
			@Override
			public int OnObserver(Object param1, Object param2) {
				if (((Integer) param1).intValue() == AlertDialog.BUTTON_POSITIVE) {
					IntEditor ie;
					Editor e = p.edit();
					ie = (IntEditor) v.findViewById(R.id.long_press);
					e.putInt(st.PREF_KEY_LONG_PRESS_INTERVAL, ie.getValue());
					ie = (IntEditor) v.findViewById(R.id.first_repeat);
					e.putInt(st.PREF_KEY_REPEAT_FIRST_INTERVAL, ie.getValue());
					ie = (IntEditor) v.findViewById(R.id.next_repeat);
					e.putInt(st.PREF_KEY_REPEAT_NEXT_INTERVAL, ie.getValue());
					ie = (IntEditor) v.findViewById(R.id.min_press);
					e.putInt(st.PREF_KEY_MINIMAL_PRESS_INTERVAL, ie.getValue());
					e.commit();
					if (OwnKeyboardHandler.inst != null)
						OwnKeyboardHandler.inst.loadFromSettings();
				}
				return 0;
			}
		};
		Dlg.customDialog(this, v, getString(R.string.ok), getString(R.string.cancel), null, obs);
	}

	void showTextColorPopupWindowValsEditor() {
		final View v = getLayoutInflater().inflate(R.layout.edit_intervals, null);
		int max = 255, min = 0;
		int steps[] = new int[] { 1, 5, 20 };
		final SharedPreferences p = st.pref(this);

		((TextView) v.findViewById(R.id.ei_title)).setText(R.string.pop_scr_txt);

		((TextView) v.findViewById(R.id.interval1)).setText(R.string.pop_scr_fon_r);
		((TextView) v.findViewById(R.id.interval2)).setText(R.string.pop_scr_fon_g);
		((TextView) v.findViewById(R.id.interval3)).setText(R.string.pop_scr_fon_b);

		final IntEditor ie = (IntEditor) v.findViewById(R.id.long_press);
		ie.setMinAndMax(min, max);
		ie.setValue(st.str2int(p.getString(st.POP_COLOR_TEXT_R, st.STR_ZERO), 0, 255, st.STR_ERROR));
		ie.setSteps(steps);

		final IntEditor ie1 = (IntEditor) v.findViewById(R.id.first_repeat);
		ie1.setMinAndMax(min, max);
		ie1.setValue(st.str2int(p.getString(st.POP_COLOR_TEXT_G, st.STR_ZERO), 0, 255, st.STR_ERROR));
		ie1.setSteps(steps);

		final IntEditor ie2 = (IntEditor) v.findViewById(R.id.next_repeat);
		ie2.setMinAndMax(min, max);
		ie2.setValue(st.str2int(p.getString(st.POP_COLOR_TEXT_B, st.STR_ZERO), 0, 255, st.STR_ERROR));
		ie2.setSteps(steps);
		st.UniObserver obs = new st.UniObserver() {
			@Override
			public int OnObserver(Object param1, Object param2) {
				if (((Integer) param1).intValue() == AlertDialog.BUTTON_POSITIVE) {
					IntEditor ie;
					Editor e = p.edit();
					ie = (IntEditor) v.findViewById(R.id.long_press);
					e.putString(st.POP_COLOR_TEXT_R, st.STR_NULL + ie.getValue());
					ie = (IntEditor) v.findViewById(R.id.first_repeat);
					e.putString(st.POP_COLOR_TEXT_G, st.STR_NULL + ie.getValue());
					ie = (IntEditor) v.findViewById(R.id.next_repeat);
					e.putString(st.POP_COLOR_TEXT_B, st.STR_NULL + ie.getValue());
					ie = (IntEditor) v.findViewById(R.id.next_repeat);
					e.commit();
					if (OwnKeyboardHandler.inst != null)
						OwnKeyboardHandler.inst.loadFromSettings();

				}
				return 0;
			}
		};
		final Button btn = (Button) v.findViewById(R.id.ei_btn_def);
		btn.setVisibility(View.VISIBLE);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ie.setValue(0);
				ie1.setValue(0);
				ie2.setValue(0);
				if (OwnKeyboardHandler.inst != null)
					OwnKeyboardHandler.inst.loadFromSettings();
			}
		});
		Dlg.customDialog(this, v, getString(R.string.ok), getString(R.string.cancel), null, obs);
	}

	void showBackColorPopupWindowValsEditor() {
		final View v = getLayoutInflater().inflate(R.layout.edit_intervals, null);
		int max = 255, min = 0;
		int steps[] = new int[] { 1, 5, 20 };
		final SharedPreferences p = st.pref(this);

		((TextView) v.findViewById(R.id.ei_title)).setText(R.string.pop_scr_fon);

		((TextView) v.findViewById(R.id.interval1)).setText(R.string.pop_scr_fon_r);
		final IntEditor ie = (IntEditor) v.findViewById(R.id.long_press);
		ie.setMinAndMax(min, max);
		ie.setValue(st.str2int(p.getString(st.POP_COLOR_R, st.STR_NULL + max), 0, 255, st.STR_ERROR));
		ie.setSteps(steps);

		((TextView) v.findViewById(R.id.interval2)).setText(R.string.pop_scr_fon_g);
		final IntEditor ie1 = (IntEditor) v.findViewById(R.id.first_repeat);
		ie1.setMinAndMax(min, max);
		ie1.setValue(st.str2int(p.getString(st.POP_COLOR_G, st.STR_NULL + max), 0, 255, st.STR_ERROR));
		ie1.setSteps(steps);

		((TextView) v.findViewById(R.id.interval3)).setText(R.string.pop_scr_fon_b);
		final IntEditor ie2 = (IntEditor) v.findViewById(R.id.next_repeat);
		ie2.setMinAndMax(min, max);
		ie2.setValue(st.str2int(p.getString(st.POP_COLOR_B, st.STR_NULL + max), 0, 255, st.STR_ERROR));
		ie2.setSteps(steps);
		st.UniObserver obs = new st.UniObserver() {
			@Override
			public int OnObserver(Object param1, Object param2) {
				if (((Integer) param1).intValue() == AlertDialog.BUTTON_POSITIVE) {
					IntEditor ie;
					Editor e = p.edit();
					ie = (IntEditor) v.findViewById(R.id.long_press);
					e.putString(st.POP_COLOR_R, st.STR_NULL + ie.getValue());
					ie = (IntEditor) v.findViewById(R.id.first_repeat);
					e.putString(st.POP_COLOR_G, st.STR_NULL + ie.getValue());
					ie = (IntEditor) v.findViewById(R.id.next_repeat);
					e.putString(st.POP_COLOR_B, st.STR_NULL + ie.getValue());
					ie = (IntEditor) v.findViewById(R.id.next_repeat);
					e.commit();
					if (OwnKeyboardHandler.inst != null)
						OwnKeyboardHandler.inst.loadFromSettings();
				}
				return 0;
			}
		};
		final Button btn = (Button) v.findViewById(R.id.ei_btn_def);
		btn.setVisibility(View.VISIBLE);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ie.setValue(255);
				ie1.setValue(255);
				ie2.setValue(255);
				if (OwnKeyboardHandler.inst != null)
					OwnKeyboardHandler.inst.loadFromSettings();
			}
		});
		Dlg.customDialog(this, v, getString(R.string.ok), getString(R.string.cancel), null, obs);
	}

	void showAcHeight() {
		final View v = inst.getLayoutInflater().inflate(R.layout.edit_intervals, null);
		int max = 20, min = 0;
		int steps[] = new int[] { 1, 2, 2 };
		final SharedPreferences p = st.pref(inst);

		((TextView) v.findViewById(R.id.ei_title)).setText(R.string.set_key_ac_height);

		((TextView) v.findViewById(R.id.interval1)).setVisibility(View.GONE);
		final IntEditor ie = (IntEditor) v.findViewById(R.id.long_press);
		ie.setMinAndMax(min, max);
		ie.setValue(st.str2int(p.getString(st.PREF_AC_HEIGHT, st.STR_ZERO), min, max, st.STR_ERROR));
		ie.setSteps(steps);

		((TextView) v.findViewById(R.id.interval2)).setVisibility(View.GONE);
		((IntEditor) v.findViewById(R.id.first_repeat)).setVisibility(View.GONE);
		((TextView) v.findViewById(R.id.interval3)).setVisibility(View.GONE);
		((IntEditor) v.findViewById(R.id.next_repeat)).setVisibility(View.GONE);

		st.UniObserver obs = new st.UniObserver() {
			@Override
			public int OnObserver(Object param1, Object param2) {
				if (((Integer) param1).intValue() == AlertDialog.BUTTON_POSITIVE) {
					IntEditor ie;
					Editor e = p.edit();
					ie = (IntEditor) v.findViewById(R.id.long_press);
					e.putString(st.PREF_AC_HEIGHT, st.STR_NULL + ie.getValue());
					e.commit();
					if (OwnKeyboardHandler.inst != null)
						OwnKeyboardHandler.inst.loadFromSettings();
				}
				return 0;
			}
		};
		final Button btn = (Button) v.findViewById(R.id.ei_btn_def);
		btn.setVisibility(View.VISIBLE);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ie.setValue(0);
				if (OwnKeyboardHandler.inst != null)
					OwnKeyboardHandler.inst.loadFromSettings();
			}
		});
		Dlg.customDialog(inst, v, inst.getString(R.string.ok), inst.getString(R.string.cancel), null, obs);
	}

	void showGestureClear() {
		String query = inst.getString(R.string.are_you_sure);
		query += st.STR_LF + st.STR_LF + inst.getString(R.string.gesture_clear_query);

		Dlg.yesNoDialog(inst, query, new st.UniObserver() {

			@Override
			public int OnObserver(Object param1, Object param2) {
				if (((Integer) param1).intValue() == AlertDialog.BUTTON_POSITIVE) {
					SharedPreferences sp = st.pref(inst);
					Editor e = sp.edit();
					e.putString(st.PREF_KEY_GESTURE_LEFT, st.STR_ZERO);
					e.putString(st.PREF_KEY_GESTURE_RIGHT, st.STR_ZERO);
					e.putString(st.PREF_KEY_GESTURE_UP, st.STR_ZERO);
					e.putString(st.PREF_KEY_GESTURE_DOWN, st.STR_ZERO);
					e.putString(st.PREF_KEY_GESTURE_SPACE_LEFT, st.STR_ZERO);
					e.putString(st.PREF_KEY_GESTURE_SPACE_RIGHT, st.STR_ZERO);
					e.putString(st.PREF_KEY_GESTURE_SPACE_UP, st.STR_ZERO);
					e.putString(st.PREF_KEY_GESTURE_SPACE_DOWN, st.STR_ZERO);

					e.putInt(st.PREF_KEY_GESTURE_CNT, 0);

					e.commit();
					inst.recreate();
				}

				return 0;
			}
		});

	}

	/** коррекция высоты индикатора над калькулятором */
	// void showCalcHeightCorrInd()
	// {
	// final View v = inst.getLayoutInflater().inflate(R.layout.edit_intervals,
	// null);
	// int max = 200,min = -200;
	// int steps[] = new int[]{1,5,10};
	// final SharedPreferences p = st.pref(inst);
	//
	// ((TextView)v.findViewById(R.id.ei_title)).setText(R.string.calc_corr_ind);
	//
	// ((TextView)v.findViewById(R.id.interval1)).setVisibility(View.GONE);
	// final IntEditor ie = (IntEditor)v.findViewById(R.id.long_press);
	// ie.setMinAndMax(min, max);
	// ie.setValue(st.str2int(p.getString(st.PREF_CALC_CORRECTION_IND,
	// st.STR_NULL+25),min,max,st.STR_ERROR));
	// ie.setSteps(steps);
	//
	// ((TextView)v.findViewById(R.id.interval2)).setVisibility(View.GONE);
	// ((IntEditor)v.findViewById(R.id.first_repeat)).setVisibility(View.GONE);
	// ((TextView)v.findViewById(R.id.interval3)).setVisibility(View.GONE);
	// ((IntEditor)v.findViewById(R.id.next_repeat)).setVisibility(View.GONE);
	//
	// st.UniObserver obs = new st.UniObserver()
	// {
	// @Override
	// public int OnObserver(Object param1, Object param2)
	// {
	// if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
	// {
	// IntEditor ie;
	// Editor e = p.edit();
	// ie = (IntEditor)v.findViewById(R.id.long_press);
	// e.putString(st.PREF_CALC_CORRECTION_IND, st.STR_NULL+ie.getValue());
	// e.commit();
	// if(OwnKeyboardHandler.inst!=null)
	// OwnKeyboardHandler.inst.loadFromSettings();
	// }
	// return 0;
	// }
	// };
	// final Button btn = (Button)v.findViewById(R.id.ei_btn_def);
	// btn.setVisibility(View.VISIBLE);
	// btn.setOnClickListener(new OnClickListener()
	// {
	// @Override
	// public void onClick(View v)
	// {
	// ie.setValue(25);
	// if(OwnKeyboardHandler.inst!=null)
	// OwnKeyboardHandler.inst.loadFromSettings();
	// }
	// });
	// Dlg.CustomDialog(inst, v, inst.getString(R.string.ok),
	// inst.getString(R.string.cancel), null, obs);
	// }
	void showAlpha() {
		final View v = inst.getLayoutInflater().inflate(R.layout.edit_intervals, null);
		int max = 10, min = 0;
		int steps[] = new int[] { 1, 1, 1 };
		final SharedPreferences p = st.pref(inst);

		((TextView) v.findViewById(R.id.ei_title)).setText(R.string.set_kbd_background_alpha);

		((TextView) v.findViewById(R.id.interval1)).setVisibility(View.GONE);
		final IntEditor ie = (IntEditor) v.findViewById(R.id.long_press);
		ie.setMinAndMax(min, max);
		ie.setValue(st.str2int(p.getString(st.KBD_BACK_ALPHA, st.STR_NULL + st.KBD_BACK_ALPHA_DEF), min, max,
				st.STR_ERROR));
		ie.setSteps(steps);

		((TextView) v.findViewById(R.id.interval2)).setVisibility(View.GONE);
		((IntEditor) v.findViewById(R.id.first_repeat)).setVisibility(View.GONE);
		((TextView) v.findViewById(R.id.interval3)).setVisibility(View.GONE);
		((IntEditor) v.findViewById(R.id.next_repeat)).setVisibility(View.GONE);

		st.UniObserver obs = new st.UniObserver() {
			@Override
			public int OnObserver(Object param1, Object param2) {
				if (((Integer) param1).intValue() == AlertDialog.BUTTON_POSITIVE) {
					IntEditor ie;
					Editor e = p.edit();
					ie = (IntEditor) v.findViewById(R.id.long_press);
					st.arDesign = null;
					e.putString(st.KBD_BACK_ALPHA, st.STR_NULL + ie.getValue());
					e.commit();
					if (OwnKeyboardHandler.inst != null)
						OwnKeyboardHandler.inst.loadFromSettings();
				}
				return 0;
			}
		};
		final Button btn = (Button) v.findViewById(R.id.ei_btn_def);
		btn.setVisibility(View.VISIBLE);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ie.setValue(st.KBD_BACK_ALPHA_DEF);
				st.arDesign = null;
				if (OwnKeyboardHandler.inst != null)
					OwnKeyboardHandler.inst.loadFromSettings();
			}
		});
		Dlg.customDialog(inst, v, inst.getString(R.string.ok), inst.getString(R.string.cancel), null, obs);
	}

	void showAcCountWord() {
		final View v = getLayoutInflater().inflate(R.layout.edit_intervals, null);
		int max = 1000, min = 1;
		int steps[] = new int[] { 1, 5, 20 };
		final SharedPreferences p = st.pref(this);

		((TextView) v.findViewById(R.id.ei_title)).setText(R.string.ac_list_value);

		((TextView) v.findViewById(R.id.interval1)).setVisibility(View.GONE);
		final IntEditor ie = (IntEditor) v.findViewById(R.id.long_press);
		ie.setMinAndMax(min, max);
		ie.setValue(st.str2int(p.getString(st.AC_LIST_VALUE, st.STR_NULL + 40), min, max, st.STR_ERROR));
		ie.setSteps(steps);

		((TextView) v.findViewById(R.id.interval2)).setVisibility(View.GONE);
		((IntEditor) v.findViewById(R.id.first_repeat)).setVisibility(View.GONE);
		((TextView) v.findViewById(R.id.interval3)).setVisibility(View.GONE);
		((IntEditor) v.findViewById(R.id.next_repeat)).setVisibility(View.GONE);

		st.UniObserver obs = new st.UniObserver() {
			@Override
			public int OnObserver(Object param1, Object param2) {
				if (((Integer) param1).intValue() == AlertDialog.BUTTON_POSITIVE) {
					IntEditor ie;
					Editor e = p.edit();
					ie = (IntEditor) v.findViewById(R.id.long_press);
					e.putString(st.AC_LIST_VALUE, st.STR_NULL + ie.getValue());
					e.commit();
					if (OwnKeyboardHandler.inst != null)
						OwnKeyboardHandler.inst.loadFromSettings();
				}
				return 0;
			}
		};
		final Button btn = (Button) v.findViewById(R.id.ei_btn_def);
		btn.setVisibility(View.VISIBLE);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ie.setValue(40);
				if (OwnKeyboardHandler.inst != null)
					OwnKeyboardHandler.inst.loadFromSettings();
			}
		});
		Dlg.customDialog(this, v, getString(R.string.ok), getString(R.string.cancel), null, obs);
	}

	/** строка имени файла для создания пустого словаря. Сделано для удобства */
	String empty_dict = st.STR_NULL;

	/** создаём пустой словарь */
	void showEmptyDict() {
		final View v = getLayoutInflater().inflate(R.layout.dialog_edit, null);
		((TextView) v.findViewById(R.id.eadw_title)).setText(R.string.dict_empty_dict);
		((Button) v.findViewById(R.id.eadw_plus_btn_button)).setVisibility(View.GONE);
		final EditText et = (EditText) v.findViewById(R.id.eadw_edit);
		et.setSingleLine();
		View.OnClickListener clickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.eadw_help:
					String txt = inst.getString(R.string.dict_empty_dict_help);
					Dlg.helpDialog(inst, txt);
					return;
				case R.id.eadw_plus_tpl_button:
					final String[] langs = Lang.getAlLocalelLang(2);
					ServiceJbKbd.inst.forceHide();
					int rlist = R.layout.tpl_instr_list;
					final ArrayAdapter<String> ar = new ArrayAdapter<String>(inst, rlist, langs);
					Dlg.customMenu(inst, ar, inst.getString(R.string.dict_languages), new st.UniObserver() {
						@Override
						public int OnObserver(Object param1, Object param2) {
							try {
								int pos = ((Integer) param1).intValue();
								String lng = langs[pos];
								int pp = lng.indexOf("-");
								if (pp > -1) {
									String[] str = lng.split("-");
									lng = str[str.length - 1].trim().toLowerCase();
									et.setText(lng);
								}

							} catch (Throwable e) {
							}
							// показываем клавиатуру
							ServiceJbKbd.inst.forceShow();
							return 0;
						}
					});
				}
				return;
			}
		};
		TextView tv = (TextView) v.findViewById(R.id.eadw_help);
		tv.setOnClickListener(clickListener);
		Button b = (Button) v.findViewById(R.id.eadw_plus_btn_button);
		b.setOnClickListener(clickListener);
		b = (Button) v.findViewById(R.id.eadw_plus_tpl_button);
		b.setText(R.string.dict_languages);
		b.setOnClickListener(clickListener);

		// String str = p.getString(st.PREF_EMPTY_DICT, st.STR_NULL);
		et.setText(empty_dict.toLowerCase());
		st.showkbd(et, false);

		st.UniObserver obs = new st.UniObserver() {
			@Override
			public int OnObserver(Object param1, Object param2) {
				st.hidekbd();
				if (((Integer) param1).intValue() == AlertDialog.BUTTON_POSITIVE) {
					empty_dict = et.getText().toString().trim();
					empty_dict = empty_dict.toLowerCase();
					if (empty_dict != null && (empty_dict.length() == 2 || empty_dict.length() == 3)) {
						boolean fl = true;
						char cc = 0;
						for (int i = 0; i < empty_dict.length(); i++) {
							if (cc < 48 && cc > 57 && cc < 97 && cc > 122) {
								fl = false;
							}
						}
						if (fl) {
							final String path = st.getSettingsPath() + WordsService.DEF_PATH + empty_dict + "_v0.dic";
							File ff = new File(path);
							if (ff.exists() && ff.isFile()) {
								Dlg.yesNoDialog(inst, inst.getString(R.string.rewrite_question), R.string.yes,
										R.string.no, new st.UniObserver() {

											@Override
											public int OnObserver(Object param1, Object param2) {
												if (((Integer) param1).intValue() == AlertDialog.BUTTON_POSITIVE) {
													File f_ind = new File(path + Words.INDEX_EXT);
													if (f_ind.exists() && f_ind.isFile()) {
														f_ind.delete();
													}
													saveEmptyDictionary(path);
												}
												return 0;
											}
										});
							} else
								saveEmptyDictionary(path);
						}
					}

					// Editor e = p.edit();
					// String text = et.getText().toString();
					// e.putString(st.PREF_EMPTY_DICT, text.toLowerCase());
					// e.commit();
					// if(OwnKeyboardHandler.inst!=null)
					// OwnKeyboardHandler.inst.loadFromSettings();
				}
				return 0;
			}
		};

		Dlg.customDialog(inst, v, getString(R.string.ok), getString(R.string.cancel), null, obs);
	}

	public static void saveEmptyDictionary(String path) {

		FileWriter wr;
		try {
			wr = new FileWriter(path, false);
			wr.write(st.STR_NULL);
			// wr.write("~ 1\n");
			wr.flush();
			wr.close();
			st.toast(R.string.create);
		} catch (IOException e) {
		}

	}

	void showClipboardSize() {
		final View v = getLayoutInflater().inflate(R.layout.edit_intervals, null);
		int max = 1000, min = 1;
		int steps[] = new int[] { 1, 10, 50 };
		final SharedPreferences p = st.pref(this);

		((TextView) v.findViewById(R.id.ei_title)).setText(R.string.set_key_clipbrd_size);

		((TextView) v.findViewById(R.id.interval1)).setVisibility(View.GONE);
		final IntEditor ie = (IntEditor) v.findViewById(R.id.long_press);
		ie.setMinAndMax(min, max);
		ie.setValue(st.str2int(p.getString(st.PREF_KEY_CLIPBRD_SIZE, st.STR_NULL + 20), min, max, st.STR_ERROR));
		ie.setSteps(steps);

		((TextView) v.findViewById(R.id.interval2)).setVisibility(View.GONE);
		((IntEditor) v.findViewById(R.id.first_repeat)).setVisibility(View.GONE);
		((TextView) v.findViewById(R.id.interval3)).setVisibility(View.GONE);
		((IntEditor) v.findViewById(R.id.next_repeat)).setVisibility(View.GONE);

		st.UniObserver obs = new st.UniObserver() {
			@Override
			public int OnObserver(Object param1, Object param2) {
				if (((Integer) param1).intValue() == AlertDialog.BUTTON_POSITIVE) {
					IntEditor ie;
					Editor e = p.edit();
					ie = (IntEditor) v.findViewById(R.id.long_press);
					e.putString(st.PREF_KEY_CLIPBRD_SIZE, st.STR_NULL + ie.getValue());
					e.commit();
					if (OwnKeyboardHandler.inst != null)
						OwnKeyboardHandler.inst.loadFromSettings();
				}
				return 0;
			}
		};
		final Button btn = (Button) v.findViewById(R.id.ei_btn_def);
		btn.setVisibility(View.VISIBLE);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ie.setValue(20);
				if (OwnKeyboardHandler.inst != null)
					OwnKeyboardHandler.inst.loadFromSettings();
			}
		});
		Dlg.customDialog(this, v, getString(R.string.ok), getString(R.string.cancel), null, obs);
	}

	/** слова в автодополнении по умолчанию */
	void showACDefaultWord() {
		final SharedPreferences p = st.pref(this);
		final View v = getLayoutInflater().inflate(R.layout.dialog_edit, null);
		final EditText et = (EditText) v.findViewById(R.id.eadw_edit);
		View.OnClickListener clickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.eadw_help:
					String txt = inst.getString(R.string.set_ac_defkey_help1);
					txt += inst.getString(R.string.set_ac_defkey_help_buttons);
					txt += inst.getString(R.string.set_ac_defkey_help_button_font);
					Dlg.helpDialog(inst, txt);
					return;
				case R.id.eadw_plus_btn_font:
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
									st.setInsertTextToCursorPosition(et, text);
									return 0;
								} else {
									st.copyText(inst, st.STR_NULL + Font.ar_symbol[pos]);
								}
							}
							return 0;
						}

					});
					return;
				case R.id.eadw_plus_btn_button:
					String text = st.STR_PREFIX + "0,t] ";
					st.setInsertTextToCursorPosition(et, text);
					int pos = et.getSelectionStart() - 4;
					if (pos < 0)
						pos = 0;
					et.setSelection(pos);
					return;
				case R.id.eadw_plus_tpl_button:
					final String foldroot = st.getSettingsPath() + Templates.FOLDER_TEMPLATES;
					File rd = new File(foldroot);
					new DlgFileExplorer(inst, inst.getString(R.string.fm_btn_plus_title), null,
							DlgFileExplorer.TYPE_ALL, rd, null, DlgFileExplorer.SELECT_FILE) {
						@Override
						public void onSelected(File file) {
							String text = file.getAbsolutePath();
							text = st.getSettingsPathShort(text, Templates.INT_FOLDER_TEMPLATES);
							text = st.STR_PREFIX + text + st.STR_COMMA + file.getName() + "] ";
							st.setInsertTextToCursorPosition(et, text);
							int pos = et.getSelectionStart() - 2;
							if (pos < 0)
								pos = 0;
							et.setSelection(pos);
							st.showkbd();
						}
					}.show();
					return;
				}
			}
		};
		TextView tv = (TextView) v.findViewById(R.id.eadw_help);
		tv.setOnClickListener(clickListener);
		Button b = (Button) v.findViewById(R.id.eadw_plus_btn_button);
		b.setOnClickListener(clickListener);
		b = (Button) v.findViewById(R.id.eadw_plus_tpl_button);
		b.setOnClickListener(clickListener);
		b = (Button) v.findViewById(R.id.eadw_plus_btn_font);
		b.setVisibility(View.VISIBLE);
		b.setOnClickListener(clickListener);

		String str = p.getString(st.PREF_AC_DEFKEY, st.AC_DEF_WORD);
		et.setText(str);
		st.showkbd(et, false);

		st.UniObserver obs = new st.UniObserver() {
			@Override
			public int OnObserver(Object param1, Object param2) {
				if (((Integer) param1).intValue() == AlertDialog.BUTTON_POSITIVE) {
					Editor e = p.edit();
					String text = et.getText().toString();
					e.putString(st.PREF_AC_DEFKEY, text);
					e.commit();
					if (OwnKeyboardHandler.inst != null)
						OwnKeyboardHandler.inst.loadFromSettings();
				}
				return 0;
			}
		};

		Dlg.customDialog(this, v, getString(R.string.ok), getString(R.string.cancel), null, obs);

	}

	/** строка для жеста Дополнительные символы */
	void showAdditionalString() {
		final SharedPreferences p = st.pref(this);
		final View v = getLayoutInflater().inflate(R.layout.dialog_edit, null);
		((TextView) v.findViewById(R.id.eadw_title)).setText(R.string.gesture_popupchar_str1);
		final EditText et = (EditText) v.findViewById(R.id.eadw_edit);
		View.OnClickListener clickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.eadw_help:
					String txt = inst.getString(R.string.gesture_popupchar_str1_help1);
					txt += inst.getString(R.string.set_ac_defkey_help_buttons);
					Dlg.helpDialog(inst, txt);
					return;
				case R.id.eadw_plus_btn_button:
					String text = st.STR_PREFIX + "0,t] ";
					st.setInsertTextToCursorPosition(et, text);
					int pos = et.getSelectionStart() - 4;
					if (pos < 0)
						pos = 0;
					et.setSelection(pos);
					return;
				case R.id.eadw_plus_tpl_button:
					final String foldroot = st.getSettingsPath() + Templates.FOLDER_TEMPLATES;
					File rd = new File(foldroot);
					new DlgFileExplorer(inst, inst.getString(R.string.fm_btn_plus_title), null,
							DlgFileExplorer.TYPE_ALL, rd, null, DlgFileExplorer.SELECT_FILE) {
						@Override
						public void onSelected(File file) {
							String text = file.getAbsolutePath();
							text = st.getSettingsPathShort(text, Templates.INT_FOLDER_TEMPLATES);
							text = st.STR_PREFIX + text + st.STR_COMMA + file.getName() + "] ";
							st.setInsertTextToCursorPosition(et, text);
							int pos = et.getSelectionStart() - 2;
							if (pos < 0)
								pos = 0;
							et.setSelection(pos);
							st.showkbd();
						}
					}.show();
					return;
				}
			}
		};
		TextView tv = (TextView) v.findViewById(R.id.eadw_help);
		tv.setOnClickListener(clickListener);
		Button b = (Button) v.findViewById(R.id.eadw_plus_btn_button);
		b.setOnClickListener(clickListener);
		b = (Button) v.findViewById(R.id.eadw_plus_tpl_button);
		b.setOnClickListener(clickListener);

		String str = p.getString(st.PREF_AC_DEFKEY, st.AC_DEF_WORD);
		et.setText(str);
		st.showkbd(et, false);

		st.UniObserver obs = new st.UniObserver() {
			@Override
			public int OnObserver(Object param1, Object param2) {
				if (((Integer) param1).intValue() == AlertDialog.BUTTON_POSITIVE) {
					Editor e = p.edit();
					String text = et.getText().toString();
					e.putString(st.SET_STR_GESTURE_DOPSYMB, text);
					e.commit();
					if (OwnKeyboardHandler.inst != null)
						OwnKeyboardHandler.inst.loadFromSettings();
				}
				return 0;
			}
		};

		Dlg.customDialog(this, v, getString(R.string.ok), getString(R.string.cancel), null, obs);

	}

	void showVibroDuration() {
		final View v = getLayoutInflater().inflate(R.layout.edit_intervals, null);
		int max = 5000, min = 0;
		((TextView) v.findViewById(R.id.interval1)).setText(R.string.set_key_short_vibro_duration);
		((TextView) v.findViewById(R.id.interval2)).setText(R.string.set_key_long_vibro_duration);
		((TextView) v.findViewById(R.id.interval3)).setText(R.string.set_key_repeat_vibro_duration);
		((TextView) v.findViewById(R.id.ei_title)).setText(R.string.set_key_vibro_durations);
		int steps[] = new int[] { 5, 10, 20 };
		final SharedPreferences p = st.pref(this);

		IntEditor ie = null;
		IntEditor.OnChangeValue cv = new IntEditor.OnChangeValue() {
			@Override
			public void onChangeIntValue(IntEditor edit) {
				VibroThread.getInstance(inst).runForce(edit.getValue());
			}
		};
		ie = (IntEditor) v.findViewById(R.id.long_press);
		ie.setMinAndMax(min, max);
		ie.setValue(Integer.decode(p.getString(st.PREF_KEY_VIBRO_SHORT_DURATION, DEF_LONG_VIBRO)));
		ie.setSteps(steps);
		ie.setOnChangeValue(cv);

		ie = (IntEditor) v.findViewById(R.id.first_repeat);
		ie.setMinAndMax(min, max);
		ie.setValue(Integer.decode(p.getString(st.PREF_KEY_VIBRO_LONG_DURATION, DEF_LONG_VIBRO)));
		ie.setSteps(steps);
		ie.setOnChangeValue(cv);

		ie = (IntEditor) v.findViewById(R.id.next_repeat);
		ie.setMinAndMax(min, max);
		ie.setValue(Integer.decode(p.getString(st.PREF_KEY_VIBRO_REPEAT_DURATION, DEF_LONG_VIBRO)));
		ie.setSteps(steps);
		ie.setOnChangeValue(cv);
		st.UniObserver obs = new st.UniObserver() {
			@Override
			public int OnObserver(Object param1, Object param2) {
				if (((Integer) param1).intValue() == AlertDialog.BUTTON_POSITIVE) {
					IntEditor ie;
					Editor e = p.edit();
					ie = (IntEditor) v.findViewById(R.id.long_press);
					e.putString(st.PREF_KEY_VIBRO_SHORT_DURATION, st.STR_NULL + ie.getValue());
					ie = (IntEditor) v.findViewById(R.id.first_repeat);
					e.putString(st.PREF_KEY_VIBRO_LONG_DURATION, st.STR_NULL + ie.getValue());
					ie = (IntEditor) v.findViewById(R.id.next_repeat);
					e.putString(st.PREF_KEY_VIBRO_REPEAT_DURATION, st.STR_NULL + ie.getValue());
					e.commit();
					if (VibroThread.inst != null)
						VibroThread.inst.readSettings();
				}
				return 0;
			}
		};
		Dlg.customDialog(this, v, getString(R.string.ok), getString(R.string.cancel), null, obs);
	}

	final static String getBackupPath() {
		return st.getSettingsPath() + st.SETTINGS_BACKUP_FILE;
	}

	// final String getGestureAdditionalPath()
	// {
	// String sss
	// =st.getSettingsPath()+st.STR_SLASH+PREF_GESTURE_DOP_SYMB_FILENAME;;
	// return st.getSettingsPath()+st.STR_SLASH+PREF_GESTURE_DOP_SYMB_FILENAME;
	// }
	public static void backup(final Context cont, final boolean bSave) {
		Dlg.yesNoDialog(cont, cont.getString(bSave ? R.string.set_key_save_pref : R.string.set_key_load_pref) + " ?",
				new st.UniObserver() {
					@Override
					public int OnObserver(Object param1, Object param2) {
						if (((Integer) param1).intValue() == AlertDialog.BUTTON_POSITIVE) {
							int ret = backupPref(cont, bSave);
							try {
								if (ret == 0)
									Toast.makeText(cont, st.STR_ERROR, 700).show();
								else if (ret == 1)
									if (!bSave) {
										st.exitApp();
										// Toast.makeText(getApplicationContext(), R.string.reboot,
										// Toast.LENGTH_LONG).show();
									} else {
										Toast.makeText(cont, R.string.ok, 700).show();
									}
							} catch (Throwable e) {
								st.toast("error save/load setting");
							}
						}
						return 0;
					}
				});
	}

	static int backupPref(Context cont, boolean bSave) {
		try {
			String appname = cont.getPackageName();
			String path = getBackupPath();
			String prefDir = cont.getFilesDir().getParent() + "/shared_prefs/";
			File ar[] = st.getFilesFromDir(new File(prefDir), st.EXT_XML);
			if (ar == null || ar.length == 0)
				return 0;
			File f = new File(path);
			FileInputStream in;
			FileOutputStream out = null;
			if (bSave) {

				in = new FileInputStream(ar[0]);
				f.delete();
				out = new FileOutputStream(f);
			} else {
				if (!f.exists()) {
					Toast.makeText(cont, "File not exist: " + path, 700).show();
					return -1;
				}
				for (int i = 0; i < ar.length; i++) {
					if (ar[i].toString().indexOf(appname + "_preferences.xml") >= 0) {
						out = new FileOutputStream(ar[i]);
					}
				}
				if (out == null)
					return -1;
				in = new FileInputStream(f);
			}

			byte b[] = new byte[in.available()];
			in.read(b);
			out.write(b);
			out.flush();
			in.close();
			out.close();
			if (!bSave) {
				if (JbKbdView.inst != null)
					JbKbdView.inst = null;
				if (ServiceJbKbd.inst != null) {
					ServiceJbKbd.inst.stopSelf();
				}
			}
			return 1;
		} catch (Throwable e) {
		}
		return 0;
	}

	public void vocabTest() {
		// Words w = new Words();
		// w.open("ru");
		// String test[] = new String[]{"��","��"};
		// long times []= new long[test.length];
		// for(int i=0;i<test.length;i++)
		// {
		// long time = System.currentTimeMillis();
		// String s[] = w.getWords(test[i]);
		// time = System.currentTimeMillis()-time;
		// times[i]=time;
		// }
		// long total = 0;
		// String log = "Test words: {";
		// for(int i=0;i<test.length;i++)
		// {
		// long time = times[i];
		// total+=time;
		// log+=test[i]+st.STR_COLON+time;
		// }
		// log+="} total:"+total;
		// Log.w("Words test", log);
	}

	public static class IntEntry {
		public IntEntry(String prefName, int descString, int names, String defaultValue) {
			key = prefName;
			descStringId = descString;
			defValue = defaultValue;
			arrayNames = names;
		}

		String key;
		int descStringId;
		int arrayNames;
		String defValue;
	}

	public void onStartService() {
		showHelper();
	}

	/** диалог оценить приложение */
	public void rateDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		alertDialogBuilder.setTitle(getString(R.string.rate_title)); // Set title

		alertDialogBuilder
				// Set dialog message
				.setMessage(getString(R.string.rate_about)).setCancelable(true)
				.setNegativeButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if (ini == null)
							return;
						// не делать оценку проведённого времени в маркете
						// гугл может это воспринять как повторное вымогательство
						// отзыва
						// try {
						// rateStart = new Date().getTime();
						// } catch (Throwable e) {
						// }

						ini.setParam(ini.RATE_APP, st.STR_ONE);

						// saveIniParam(st.INI_RATE_APP,st.STR_ONE);
						// saveIniParam("rate_start_time",st.STR_ZERO);

						Uri uri = Uri.parse(st.RUN_MARKET_STRING + getPackageName()); // Go to Android market
						Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
						if (goToMarket.resolveActivity(getPackageManager()) != null) {
							startActivity(goToMarket);
						}
						dialog.cancel();
					}
				}).setPositiveButton(getString(R.string.no), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// saveIniParam(START_TIME,st.STR_NULL+(long)(cur_time+TIME_NEGATIVE_MONTH));
						// saveIniParam(st.INI_RATE_APP,st.STR_ZERO);

						// rateStart = 0;
						dialog.cancel();
					}
				});
		AlertDialog alertDialog = alertDialogBuilder.create(); // Create alert dialog
		alertDialog.show(); // Show alert dialog
	}

	// если true - клавиатура зарегистрирована и включена в системе
	// и тогда читаются настройки программы
	public boolean isKbdRegister() {
		if (st.getRegisterKbd(inst) < 2) {
			st.toast(getString(R.string.kbd_warning));
			return false;
		}
		return true;
	}

	public void savePop2str() {
		String path = st.getSettingsPath();
		File f = new File(path);
		if (!f.isDirectory()) {
			st.toast(getString(R.string.kbd_warning));
			return;
		}
		path += st.STR_SLASH + PREF_GESTURE_DOP_SYMB_FILENAME;
		FileWriter writer;
		try {
			writer = new FileWriter(path, false);
			writer.write(st.gesture_str.trim());
			writer.close();
			st.toast(getString(R.string.ok));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void loadPop2str() {
		String path = st.getSettingsPath();
		File f = new File(path);
		if (!f.isDirectory()) {
			st.toast(getString(R.string.kbd_warning));
			return;
		}
		try {
			path += st.STR_SLASH + PREF_GESTURE_DOP_SYMB_FILENAME;
			FileReader fr = new FileReader(path);
			Scanner sc = new Scanner(fr);
			sc.useLocale(Locale.US);
			st.gesture_str = st.STR_NULL;
			while (sc.hasNextLine()) {
				st.gesture_str += sc.nextLine();
			}
			sc.close();
			st.pref(st.c()).edit().putString(st.SET_STR_GESTURE_DOPSYMB, st.gesture_str.trim()).commit();
			st.toast(getString(R.string.ok));
		} catch (IOException ex) {
		}
	}

	/** вызывается когда активность перекрывается другой */
	@Override
	public void onUserLeaveHint() {
		super.onUserLeaveHint();
	}

	@Override
	public void onBackPressed() {
		if (ColorPicker.inst != null) {
			ColorPicker.inst.finish();
			return;
		}
		st.fl_pref_act = false;
		super.onBackPressed();
	}

	public boolean strToFile(String s, File f) {
		// выводим дату последнего редактирования
		String dt = "dd.MM.yyyy HH:mm";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(dt);
			dt = sdf.format(new Date());
			dt = "Report created " + dt + st.STR_LF;
			dt += "in App: " + st.getAppNameAndVersion(inst) + st.STR_LF + st.STR_LF;
		} catch (Throwable e) {
			dt = null;
		}

		try {

			f.delete();
			FileOutputStream fout = new FileOutputStream(f);
			if (dt != null)
				fout.write(dt.getBytes());
			fout.write(s.getBytes());
			fout.close();
			return true;
		} catch (Throwable e) {
		}
		return false;
	}

	public String getStackString(Throwable e) {
		if (e == null)
			e = new Exception();
		StringBuffer msg = new StringBuffer(e.getClass().getName());
		if (!TextUtils.isEmpty(e.getMessage()))
			msg.append(' ').append(e.getMessage());
		msg.append('\n');
		StackTraceElement st[] = e.getStackTrace();
		for (StackTraceElement s : st)
			msg.append(s.toString()).append('\n');
		Throwable cause = e.getCause();
		if (cause != null && msg.length() < MAX_STACK_STRING)
			msg.append('\n').append(CAUSED_BY).append('\n').append(getStackString(cause));
		String ret = msg.toString();
		return ret;
	}

	public void saveCrash(Throwable e) {
		e.printStackTrace();
		strToFile(getStackString(e), new File(this.getFilesDir(), SAVE_CRASH));
	}

	public boolean checkCrash() {
		// проверяем, что ведётся разработка на эмуляторе
		// и отчёт о краше выводить не надо
		if (st.isDebugEmulator())
			return false;
		try {
			String path = getPackageManager().getPackageInfo(getPackageName(), 0).applicationInfo.dataDir;

			path += "/files" + SAVE_CRASH;
			file_crash = new File(path);
			if (!file_crash.exists())
				return false;
		} catch (Throwable e) {
		}
		Dlg.yesNoDialog(inst, inst.getString(R.string.crash_question), new st.UniObserver() {

			@Override
			public int OnObserver(Object param1, Object param2) {
				if (((Integer) param1).intValue() == AlertDialog.BUTTON_POSITIVE) {
					if (file_crash != null) {
						Mail.sendFeedback(inst, file_crash);
					}
				}
				if (file_crash != null) {
					file_crash.delete();
					file_crash = null;
				}
				return 0;
			}
		});
		return true;
	}

	public void setLangApp() {
		st.lang_pref = "ru";
		if (st.qs_ar[3] == 0)
			st.lang_pref = Locale.getDefault().getLanguage();
		else if (st.qs_ar[3] == 1)
			st.lang_pref = "ru";
		else if (st.qs_ar[3] == 2)
			st.lang_pref = "en";
		else if (st.qs_ar[3] == 3)
			st.lang_pref = "es";
		else if (st.qs_ar[3] == 4)
			st.lang_pref = "uk";

		if (!st.lang_pref.contains(Locale.getDefault().getCountry())) {
			Locale locale = new Locale(st.lang_pref);
			Locale.setDefault(locale);
			Configuration config = new Configuration();
			config.locale = locale;
			getBaseContext().getResources().updateConfiguration(config, null);
			inst.recreate();
		}
	}

}