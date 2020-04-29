/*
 * Copyright (C) 2008-2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.jbak2.JbakKeyboard;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.inputmethodservice.ExtractEditText;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import com.google.android.voiceime.VoiceRecognitionTrigger;
import com.jbak2.JbakKeyboard.com_menu;
import com.jbak2.JbakKeyboard.st;
import com.jbak2.JbakKeyboard.st.IntEntry;
import com.jbak2.CustomGraphics.BitmapCachedGradBack;
import com.jbak2.Dialog.DlgPopupWnd;
import com.jbak2.JbakKeyboard.EditSetActivity.EditSet;
import com.jbak2.JbakKeyboard.IKeyboard.KbdDesign;
import com.jbak2.JbakKeyboard.IKeyboard.Keybrd;
import com.jbak2.JbakKeyboard.JbKbd.LatinKey;
import com.jbak2.JbakKeyboard.JbKbd.Replacement;
import com.jbak2.JbakKeyboard.KeyboardGesture.GestureHisList;
import com.jbak2.ctrl.ClipbrdService;
import com.jbak2.ctrl.Font;
import com.jbak2.ctrl.GlobDialog;
import com.jbak2.ctrl.IniFile;
import com.jbak2.ctrl.Mainmenu;
import com.jbak2.ctrl.Notif;
import com.jbak2.ctrl.SameThreadTimer;
import com.jbak2.perm.Perm;
import com.jbak2.receiver.ClipbrdSyncService;
import com.jbak2.words.Words;
import com.jbak2.words.WordsService;
import com.jbak2.words.IWords.WordEntry;

/** Основной сервис клавиатуры */
@SuppressLint("NewApi")
public class ServiceJbKbd extends InputMethodService
		implements KeyboardView.OnKeyboardActionListener, OnSharedPreferenceChangeListener {
	/** флаг, что режим выделения включён, 
	 * если на раскладке отсутствует кнопка TXT_ED_SELECT. 
	 * Применяется в CurInput для КОПИРОВАТЬ СТРОКУ в менюшке копирования без выделения*/
	boolean selmode = false;
	int m_SelStart;
	int m_SelEnd;
	StringBuffer m_textBeforeCursor;
	CharSequence m_textAfterCursor;
	/** наличие на устройстве гугл маркета */
	boolean googleplayexist = false;
	/** слово до срабатывания компаратора */
	String old_word = null;
	Notif notif = null;
	InputConnection mIc = null;
	Mainmenu mmenu = null;
	EditorInfo kbd_show_ei = null;
	/** НЕ УДАЛЯТЬ! <br> */
	boolean fl_newvers = false;
	IniFile ini;
	long curtime = 0;
	long initime = 0;

	// читались ли уже настройки
	boolean fl_read_pref = false;
	boolean fl_word_separator = false;
	String cur_lang = st.STR_NULL;
	/** предотвращает повторное нажатие */
	long last_time = 0;
	long cur_time = 0;
	// флаг что applyCorrection уже сработал
	boolean fl_applyCorrection = false;
	// флаг что нажат ентер (для обработки)
	boolean fl_enter = false;
	// переменные для вывода подсчитанного размера текста в текущем поле ввода
	boolean length_fl = false;
	int length_int = 0;
	String length_str = st.STR_NULL;
	String length_str1 = null;
	// флаг что клавиатура запущена в самой программе и вставлять пробел не нужно
	boolean fl_text = true;
	int g_count = 0;

	int m_popup_color = 0xeeffffff;
	int m_popup_color_text = 0xee000000;
	/** поведение стрелок */
	boolean m_arrow_key = false;
	// коррекция высоты окна индикатора над клавиатурой
	// int calc_corr_ind =25;
	// массив горячих клавиш
	String[] m_hot_str = new String[100];
	// массив шаблонов горячих клавиш
	String[] m_hot_tpl = new String[100];
	// сколько всего элементов массивов для горячих клавиш
	static int m_hot_count = 0;
	// выбранная директория шаблонов для горячих клавиш
	String m_hotkey_dir = st.STR_NULL;
	// метод ввода цифр и символов
	int input_method = 1;
	/** временная строка */
	String s1 = st.STR_NULL;
	/** временная строка */
	int in1 = 0;
	/** временная строка */
	int in2 = 0;
	/** временная строка */
	int in3 = 0;
	/** временная строка */
	int in4 = 0;
	// символы удаления пробела
	String del_space_symbol = ".,!?;:";
	/** символы добавления пробела перед одного из этих символов */
	String add_space_before_symbol = "—№";

	// задержка после записи par.ini
	// static int m_par_delay = 10;
	/** слова по умолчанию в автодополнении */
	String m_ac_defkey;
	boolean m_ac_space = true;
	AudioManager m_audio;
	// String m_defaultWords = ".,?!@/\"-";
	//static final boolean PROCESS_HARD_KEYS = true;
	boolean PROCESS_VOLUME_KEYS = true;
	/** Автодополнение отсутствует */
	public static final int SUGGEST_NONE = 0;
	/** Автодополнение из словаря */
	public static final int SUGGEST_VOCAB = 1;
	/** Автодополнение из программы, которой принадлежит текущий ввод */
	public static final int SUGGEST_OWN = 2;
	/** запрет на показ слов автодополнения в полях пароля */
	public static final int SUGGEST_NOT_DICT = 3;
	/** Тип дополнений, одна из констант SUGGEST_ */
	int m_suggestType = SUGGEST_NONE;
	public boolean m_bComplete = true;
	public static final String PID = "a14ef033de91702";
	public boolean m_acAutocorrect = false;
	public int m_lastInput = 0;
	public int lastkey_index = -1;
	/** Если первый символ на клавише, выводится в верхнем регистре */
	public boolean firstkey_upper = false;
	public boolean firstsymb = false;
	public LatinKey thiskey;

	/** Место, в котором показано окно автодополнения */
	public int m_acPlace = CandView.AC_PLACE_TITLE;
	PopupKeyboard pk;
	/** Текущий просмотр кандидатов */
	public CandView m_candView = null;
	/** сохранение параметров для калькулятора */
	CandView m_candView1;
	/** Просмотр кандидатов, прикрепленный к клавиатуре */
	//CandView m_kbdCandView;
	WindowManager m_wm;
	public static ServiceJbKbd inst;
	/** Символы концов предложений */
	String m_SentenceEnds = st.STR_NULL;
	String m_SpaceSymbols = st.STR_NULL;
	boolean m_bForceShow = false;
	int m_state = 0;
	int m_PortraitEditType = st.PREF_VAL_EDIT_TYPE_DEFAULT;
	int m_LandscapeEditType = st.PREF_VAL_EDIT_TYPE_DEFAULT;
	/** Разрешена автоматическая смена регистра */
	/** true - hазрешена правила автоматической смены регистра */
	public static boolean state_auto_case = true;
	//public static final int STATE_AUTO_CASE = 0x00000001;
	/** Статус - вставка пробела после конца предложения */
	public static final int STATE_SENTENCE_SPACE = 0x0000002;
	/** Статус - верхний регистр в пустом поле */
	public static final int STATE_EMPTY_UP = 0x0000004;
	/** Статус - верхний регистр только после пробела */
	public static final int STATE_SPACE_SENTENCE_UP = 0x0000008;
	/**
	 * Статус - предложения с большой буквы после символов из строки
	 * {@link #m_SentenceEnds}
	 */
	public static final int STATE_UP_AFTER_SYMBOLS = 0x0000010;
	public static final int STATE_GO_END = 0x00001000;
	boolean m_bBackProcessed = false;
	Rect m_cursorRect;
	EditSet m_es = new EditSet();
	boolean m_bCanAutoInput = false;
	/**
	 * Обработка клавиш громкости. 0:нет, 1:+ влево, - вправо, 2: - влево, + вправо
	 */
	int m_volumeKeys = 0;
	float m_soundVolume = 5;
	final static int MSG_SHOW_PANEL = 0x11;
	VoiceRecognitionTrigger m_voice;
	Handler m_autoCompleteHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == Words.MSG_GET_WORDS) {
				onWords((Vector<WordEntry>) msg.obj);
			} else if (msg.what == MSG_SHOW_PANEL) {
				if (isInputViewShown() && com_menu.inst == null)
					m_candView.show(st.kv(), m_acPlace);
			}
		};
	};
	public View mRootView;
	@Override
	public void onCreate() {
		super.onCreate();
		if (st.runapp_favorite.size() > 0)
			st.runapp_favorite.clear();
		for (int i = 0; i < st.runapp_all.length; i++) {
			st.runapp_all[i] = st.STR_NULL;
		}
		st.getGestureAll();
		if (JbKbdPreference.inst != null)
			JbKbdPreference.inst.onStartService();
		if (Quick_setting_act.inst != null)
			Quick_setting_act.inst.showHintButton();
//		 старая проверка буфера обмена через ресивер каждые 5 секунд
		new ClipbrdService(this);
		
		if (st.fl_sync && ClipbrdSyncService.inst == null) {
			st.startSyncServise();
		}
		m_ac_defkey = st.AC_DEF_WORD;
		m_voice = new VoiceRecognitionTrigger(this);
		m_audio = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
		inst = this;
		m_wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		WordsService.g_serviceHandler = m_autoCompleteHandler;
		WordsService.start(this);
		st.upgradeSettings(inst);
		m_candView = null;
		m_candView = createNewCandView();
		// SharedPreferences pref = st.pref();
		// m_es.load(st.PREF_KEY_EDIT_SETTINGS);
		// pref.registerOnSharedPreferenceChangeListener(this);
		// onSharedPreferenceChanged(pref, null);
		if (Font.tf == null)
			new Font(inst);
		readPreferences();
		
	}
//	private void showSystemUi(boolean visible) {
//	    int flag = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//	            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//	            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//	    if (!visible) {
//	    // We used the deprecated "STATUS_BAR_HIDDEN" for unbundling
//	        flag |= View.SYSTEM_UI_FLAG_FULLSCREEN
//	                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
//	    }
//	    mRootView.setSystemUiVisibility(flag);
//		st.showAcPlace();
//		//st.toast("bb");
//	}
//
//	private void setOnSystemUiVisibilityChangeListener() {
//		if (mRootView == null)
//			return;
//	    mRootView.setOnSystemUiVisibilityChangeListener(
//	            new View.OnSystemUiVisibilityChangeListener() {
//	                @Override
//	                public void onSystemUiVisibilityChange(final int visibility) {
//	                    new Handler().postDelayed(new Runnable() {
//	                        @Override
//	                        public void run() {
//	                            //showSystemUi(false);
//	                            showSystemUi(st.has(visibility, View.SYSTEM_UI_FLAG_HIDE_NAVIGATION));
//	                        }
//	                    }, 2000);
//	                }
//	    });
//	}
	private void readPreferences() {
		SharedPreferences pref = st.pref();
		m_es.load(st.PREF_KEY_EDIT_SETTINGS);
		pref.registerOnSharedPreferenceChangeListener(this);
		onSharedPreferenceChanged(pref, null);
	}

	/** Завершение работы сервиса */
	@Override
	public void onDestroy() {
		// JbKbdView.inst = null;
		removeCandView();
// старый		
//		if (ClipbrdService.inst != null)
//			ClipbrdService.inst.delete(inst);
		st.pref().unregisterOnSharedPreferenceChangeListener(this);
		KeyboardPaints.inst = null;
		if (VibroThread.inst != null)
			VibroThread.inst.destroy();
		if (st.kv() != null) {
			st.kv().setOnKeyboardActionListener(null);
		}
		inst = null;
		if (JbKbdPreference.inst != null)
			JbKbdPreference.inst.onStartService();
		if (Quick_setting_act.inst != null)
			Quick_setting_act.inst.showHintButton();
		st.fl_show_kbd_notif = false;
		setShowNotification();
		super.onDestroy();
		st.exitApp();
	}

	/** Стартует ввод */
	@Override
	public View onCreateInputView() {
		getLayoutInflater().inflate(R.layout.input, null);
		JbKbdView.inst.setOnKeyboardActionListener(this);
		st.setQwertyKeyboard();
		return JbKbdView.inst;
	}

	/** Должен вернуть просмотр кандидатов или null */
	@Override
	public View onCreateCandidatesView() {
		// m_kbdCandView = createNewCandView();
		// return m_kbdCandView;
		return null;
	}

	CandView createNewCandView() {
		// if (m_candView!=null)
		// return m_candView;
		return (CandView) getLayoutInflater().inflate(R.layout.candidates, null);
	}

	void forceFullScreen(EditorInfo attribute) {
		int set = st.isLandscape(this) ? m_LandscapeEditType : m_PortraitEditType;
		if (set == st.PREF_VAL_EDIT_TYPE_FULLSCREEN) {
			attribute.imeOptions = st.rem(attribute.imeOptions, EditorInfo.IME_FLAG_NO_EXTRACT_UI);
			attribute.imeOptions = st.rem(attribute.imeOptions, 0x02000000/* EditorInfo.IME_FLAG_NO_FULLSCREEN */);
		}
	}
	/** клавиша select */
	LatinKey selkey = null;
	@Override
	public void onStartInputView(EditorInfo attribute, boolean restarting) {
		if (com_menu.inst != null) {
			com_menu.close();
		}
		if (CustomKbdScroll.inst != null) {
			CustomKbdScroll.close();
		}
		if (!Perm.checkPermission(inst)) {
			st.runAct(Quick_setting_act.class, inst);
			return;
		}
		JbKbdView kv = st.kv();
		if (kv != null) {
			kv.reloadSkin();
			kv.setX(st.getKbdHorizontalBias());
		}
		// if (TplEditorActivity.inst==null)
		// com_menu.closeTplAndTrans();
		// if (isNewVersion())
		// return;
		// else
		super.onStartInputView(attribute, restarting);
		isNewVersion();
//		try {
//		    mRootView = getWindow().getWindow().getDecorView();
//		    setOnSystemUiVisibilityChangeListener();
//		    showSystemUi(false);
//		} catch (Exception e) {
//		}
		//JbKbd.setEnterDrawable(inst.getResources());
		if (!fl_read_pref) {
			readPreferences();
		}
		if (st.fl_ac_list_view) {
			m_candView.popupViewFullList();
		}
		if (st.fl_sync)
			st.startSyncServise();
		// else
		// st.stopSyncServise();

		//m_candView.setInflatePopupPanelButton();
		m_candView.setVisible(m_candView.m_counter, st.fl_counter);
		setCountTextValue();
		m_candView.setVisible(m_candView.m_keycode, st.fl_keycode);

		m_voice.onStartInputView();
		m_candView.setDefaultWords(m_ac_defkey);
		// m_candView.setInd(false);

		if (!restarting) {
			m_SelStart = attribute.initialSelStart;
			m_SelEnd = attribute.initialSelEnd;
		}
		m_textAfterCursor = null;
		m_textBeforeCursor = null;
		if (JbKbdView.inst == null)
			reinitKeyboardView();
		int var = attribute.inputType & EditorInfo.TYPE_MASK_VARIATION;
		checkSuggestType(attribute);
// старое положение		
//		if (restarting)
//			return;
		// выбор какую раскладку выводить
		switch (attribute.inputType & EditorInfo.TYPE_MASK_CLASS) {
		case EditorInfo.TYPE_CLASS_NUMBER:
		case EditorInfo.TYPE_CLASS_DATETIME:
		case EditorInfo.TYPE_CLASS_PHONE:
			m_bCanAutoInput = false;
			st.setNumberKeyboard();
			break;
			
		default:
			// отключаем автосмену регистра в полях ввода пароля и емейла
			switch (var)
			{
			case EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS:
			case EditorInfo.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS:
			case EditorInfo.TYPE_TEXT_VARIATION_EMAIL_SUBJECT:
			case EditorInfo.TYPE_TEXT_VARIATION_PASSWORD:
			case EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD:
			case EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD:
				m_bCanAutoInput = false;
				changeCase(false);
				break;
			default:
				m_bCanAutoInput = canAutoInput(attribute);
				if (m_bCanAutoInput)
					changeCase(false);
				else
					st.kv().setTempShift(false, false);
				// выводить английский, если поле ввода пароль
				// if (attribute.inputType == EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD)
				// {
				// st.tempEnglishQwerty = true;
				// st.setQwertyKeyboard(false);
				// }
				// else
				// st.type_kbd = 1;
				setTypeKbd();
				// st.setQwertyKeyboard();
				break;
			}
		}
		// новое положение (добавил 14.08.19)
		if (restarting)
			return;

		selkey = st.curKbd().getKeyByCode(st.TXT_ED_SELECT);
		if (selkey != null) {
			if (selkey.on) 
				selmode = true;
			else
				selmode = false;
		}
		kbd_show_ei = attribute;
		setImeOptions();
		updateFullscreenMode();
		EditorInfo ei;
		makeExtractingText(false);
		createNewCandView();
		m_candView.setACBackground();
		st.showAcPlace();
	}
/** счётчик заходов в isNewVersion и если версия новая, 
 * то выводить change log со второго раза */
	int cnt_isnewversion = 0;
	String param = st.STR_NULL;

	/** если версия новая, то выводит change log */
	public void isNewVersion() {
		if (ini == null) {
			ini = new IniFile(inst);
			if (!ini.createMainIniFile()) {
				ini = null;
				return;
			}
		}
//		ini.setFilename(st.getSettingsPath() + ini.PAR_INI);
//		if (!ini.isFileExist())
//			ini.create(st.getSettingsPath(), ini.PAR_INI);
//		if (!ini.isFileExist())
//			return false;
		fl_newvers = false;
		param = ini.getParamValue(ini.VERSION_CODE);
		if (param == null) {
			ini.setParam(ini.VERSION_CODE, st.getAppVersionCode(inst));
			return;
		}
		if (st.getAppVersionCode(inst).compareToIgnoreCase(param) != 0) {
			DlgPopupWnd dpw = new DlgPopupWnd(inst);
			dpw.setGravityText(Gravity.LEFT | Gravity.TOP);
			String str = inst.getString(R.string.nv_upload) + st.STR_LF + st.STR_LF + getTextNewVersion();
			dpw.set(str, R.string.no, R.string.unload);
			dpw.setObserver(new st.UniObserver() {
				@Override
				public int OnObserver(Object param1, Object param2) {
					cnt_isnewversion = 0;
					if (((Integer) param1).intValue() == AlertDialog.BUTTON_NEUTRAL) {
						st.exitApp();
					}
					// dpw.dismiss();
					return 0;
				}
			});
			dpw.show(0);
			cnt_isnewversion++;
			if (cnt_isnewversion < 2)
				return;
			ini.setParam(ini.VERSION_CODE, st.getAppVersionCode(inst));
			fl_newvers = true;
			//cnt_isnewversion = -1;
			return;
		} 
		else {
			// оценивалось ли приложение?
			//rateCheck();
			
			// проверка обновлений
   			st.checkUpdate(inst, ini, false);

			return;
		}
	}

	public void rateCheck() {
		rate_dialog = false;
		if (ini == null)
			return;
		curtime = new Date().getTime();
		param = ini.getParamValue(ini.RATE_APP);
		if (param == null) {
			ini.setParam(ini.RATE_APP, st.STR_ZERO);
			return;
		}
		if (param.compareToIgnoreCase(st.STR_ZERO) == 0) {
			initime = 0;
			param = ini.getParamValue(ini.START_TIME);
			if (param == null) {
				ini.setParam(ini.START_TIME, st.STR_NULL + (long) (curtime + ini.RATE_FIRST_TIME));
				initime = curtime;
			} else {
				try {
					initime = Long.parseLong(param);
				} catch (NumberFormatException e) {
					initime = curtime;
					;
				}
			}
//			String scurtime = "dd.MM.yyyy HH:mm:ss";
//			Date dt = new Date();
//			dt.setTime(curtime);
//			SimpleDateFormat sdf = new SimpleDateFormat(scurtime);
//			scurtime = sdf.format(dt);
//			String spartime = "dd.MM.yyyy HH:mm:ss";
//			dt = new Date();
//			dt.setTime((long) (initime));
//			sdf = new SimpleDateFormat(spartime);
//			scurtime = scurtime;
//			spartime = sdf.format(dt);
			if ((initime) <= curtime) {
				if (googleplayexist) {
					rateDialog(ini);
					// st.toastLong(R.string.rate_toast);
					// ini.setParam(ini.START_TIME, st.STR_NULL + (long) (curtime +
					// ini.RATE_NEGATIVE_TIME));

				}
			}
		}
	}
	/** флаг, что диалог оценки на экране */
	boolean rate_dialog = false;
	public void rateDialog(final IniFile inifile) {
		rate_dialog = true;
		DlgPopupWnd dpw = new DlgPopupWnd(inst);
		dpw.setGravityText(Gravity.LEFT | Gravity.TOP);
		String str = inst.getString(R.string.rate_query);
		dpw.set(str, R.string.later, R.string.rate_btn);
		dpw.setObserver(new st.UniObserver() {
			@Override
			public int OnObserver(Object param1, Object param2) {
				if (inifile == null)
					return 0;
				// yes
				if (((Integer) param1).intValue() == AlertDialog.BUTTON_NEUTRAL) {
					if (!inifile.setParam(inifile.RATE_APP, st.STR_ONE)) {
						st.toast(R.string.cs_error_write);
						return 0;
					}
					try {
						Uri uri = Uri.parse(st.RUN_MARKET_STRING + getPackageName()); // Go to Android market
						Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
						if (goToMarket.resolveActivity(getPackageManager()) != null) {
							startActivity(goToMarket);
						}
					} catch (Throwable e) {
					}
				} else {
					// no
					if (!inifile.setParam(ini.START_TIME, st.STR_NULL + (long) (curtime + inifile.RATE_NEGATIVE_TIME))) {
						st.toast(R.string.cs_error_write);
						return 0;
					}
					inifile.setParam(ini.START_TIME, st.STR_NULL + (long) (curtime + inifile.RATE_NEGATIVE_TIME));
				}
				// dpw.dismiss();
				return 0;
			}
		});
		dpw.show(0);
	}

	// показывает автодополнение
	public void acVisible() {
		if (m_candView != null)
			m_candView.setVisibility(View.VISIBLE);
	}

	// скрывает автодополнение
	public void acGone() {
		if (m_candView != null)
			m_candView.setVisibility(View.GONE);
	}

	/** читает из diary.txt текст только последней версии */
	public String getTextNewVersion() {
		String out = st.STR_NULL;
		String line = st.STR_NULL;
		InputStream is;
		// строка с номером каждой версии должна начинаться с *
		// тогда, когда cnt = 2, то считаем что описалово версии полное,
		// а потому возврат 
		int cnt = 0;
		try {
			is = getAssets().open(st.STA_FILENAME_DIARY);

			Scanner sc = new Scanner(is);
			sc.useLocale(Locale.US);
			while (sc.hasNext()) {
				if (sc.hasNextLine()) {
					line = sc.nextLine();
					line = line.trim();
				}
				if (line.startsWith("*"))
					cnt += 1;
				if (cnt == 2)
					break;
				out += line + st.STR_LF;
			}
			sc.close();
		} catch (IOException e) {
		}

		return out;
	}

	int m_StatusBarHeight = 0;

	/** Проверяет тип дополнений, запоминает в {@link #m_suggestType} */
	public final void checkSuggestType(EditorInfo ei) {
		m_suggestType = getSuggestType(ei, isFullscreenMode());
		if (st.fl_suggest_dict)
			m_suggestType = SUGGEST_VOCAB;
		// if(m_suggestType==SUGGEST_NONE)
		// {
		// m_suggestType=SUGGEST_VOCAB;
		// openWords();
		// showCandView(true);
		// getCandidates();
		// }
		if (m_suggestType == SUGGEST_VOCAB) {
			openWords();
			showCandView(true);
			getCandidates();
		} else if (m_suggestType == SUGGEST_OWN)
			showCandView(true);
		else
			showCandView(true);
	}

	final void showCandView(boolean bShow) {
		if (bShow) {
			// if (m_acPlace!=JbCandView.AC_PLACE_NONE) {
			// m_candView.show(st.kv(), m_acPlace);
			// return;
			// }

			if (com_menu.inst != null)
				return;

			if (m_acPlace != CandView.AC_PLACE_NONE) {
				m_candView.show(st.kv(), m_acPlace);
				return;
			}
			// ||m_acPlace==JbCandView.AC_PLACE_NONE
			if (m_suggestType == SUGGEST_NONE || m_acPlace == CandView.AC_PLACE_NONE)
				return;
			m_candView.show(st.kv(), m_acPlace);
		} else {
			removeCandView();
		}

	}

	void removeCandView() {
		m_candView.remove();
	}

	public final String getCurQwertyLang() {
		JbKbd ck = st.curKbd();
		if (ck == null)
			return null;
		return st.isQwertyKeyboard(ck.kbd) ? ck.kbd.lang.name : st.getCurLang();
	}

	final void openWords() {
		if (!m_bComplete || WordsService.inst == null)
			return;
		String lang = getCurQwertyLang();
		if (lang == null)
			return;
		WordsService.command(WordsService.CMD_OPEN_VOCAB, lang, inst);
	}

	@Override
	public void onStartInput(EditorInfo attribute, boolean restarting) {
		googleplayexist = st.isAppInstalled(inst, st.APP_PACKAGE_GOOGLE_PLAY);
		// st.toast(inst, "gp= "+googleplayexist);
		if (m_candView != null)
			showCandView(true);
		if (attribute.initialSelStart < 0 && attribute.initialSelEnd < 0 && attribute.imeOptions == 0) {
			requestHideSelf(0);
			BitmapCachedGradBack.clearAllCache();
		}
		// проверяем папку установки программы клавиатуры
		// НЕ ЗАБЫТЬ ПОСЛЕ ПЕРЕВОДА ДОБАВИТЬ СТРОКУ ТОСТА (и заменить r.string.about)
		// String insfolder = st.STR_NULL;
		// try {
		// insfolder = getPackageManager().getPackageInfo(getPackageName(),
		// 0).applicationInfo.dataDir.toLowerCase();
		// } catch (NameNotFoundException e) {
		// e.printStackTrace();
		// }
		// if (insfolder.contains("sdcard")){
		// st.toast(R.string.about);
		// }
		super.onStartInput(attribute, restarting);
	}

	@Override
	public void onFinishInputView(boolean finishingInput) {
		if (fl_newvers & GlobDialog.gbshow) {
			GlobDialog.inst.fl_back_key = false;
			GlobDialog.inst.finish();
			fl_newvers = false;
		}
		st.fl_suggest_dict = false;
		m_candView.m_forcibly.setVisibility(View.GONE);
		if (JbKbdView.inst != null && JbKbdView.inst.m_pk.fl_popupcharacter_window) {
			JbKbdView.inst.m_pk.close();
		}
		st.fl_fiks_tpl = true;
		st.fl_fiks_calc = true;
		if (st.type_kbd == 3)
			st.type_kbd = 1;
		removeCandView();
		JbKbdView kv = st.kv();
		if (kv != null)
			kv.resetPressed();
		fl_enter = false;
		kbd_show_ei = null;
		super.onFinishInputView(finishingInput);
	};

	@Override
	public void onFinishCandidatesView(boolean finishingInput) {
		super.onFinishCandidatesView(finishingInput);
	};

	@Override
	public void onBindInput() {
		super.onBindInput();
	};

	@Override
	public void onUnbindInput() {
		super.onUnbindInput();
	};

	/** Закрытие поля ввода */
	@Override
	public void onFinishInput() {
		super.onFinishInput();
		// Clear current composing text and candidates.
		// We only hide the candidates window when finishing input on
		// a particular editor, to avoid popping the underlying application
		// up and down if the user is entering text into the bottom of
		// its window.
		if (JbKbdView.inst != null) {
			removeCandView();
			JbKbdView.inst.closing();
		}
		kbd_show_ei = null;
		if (JbKbdPreference.inst != null)
			JbKbdPreference.inst.onStartService();
		if (Quick_setting_act.inst != null)
			Quick_setting_act.inst.showHintButton();
	}


	/** Изменение выделения в редакторе */
	@Override
	public void onUpdateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int candidatesStart,
			int candidatesEnd) {

		// super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd,
		// candidatesStart, candidatesEnd);
		final ExtractEditText eet = m_extraText;
		if (eet != null && isFullscreenMode() && mExtractedText != null) {
			final int off = mExtractedText.startOffset;
			eet.startInternalChanges();
			newSelStart -= off;
			newSelEnd -= off;
			final int len = eet.getText().length();
			if (newSelStart < 0)
				newSelStart = 0;
			else if (newSelStart > len)
				newSelStart = len;
			if (newSelEnd < 0)
				newSelEnd = 0;
			else if (newSelEnd > len)
				newSelEnd = len;
			eet.setSelection(newSelStart, newSelEnd);
			eet.finishInternalChanges();
		}

		// st.log("onUpdateSelection " + m_SelStart + st.STR_SPACE + m_SelEnd);
		if (st.has(m_state, STATE_GO_END)) {
			m_state = st.rem(m_state, STATE_GO_END);
			mIc = getCurrentInputConnection();
			if (mIc != null)
				mIc.setSelection(isSelMode() ? m_SelEnd : newSelEnd, newSelEnd);
			return;
		}
		m_SelStart = newSelStart;
		m_SelEnd = newSelEnd;
		if (m_SelStart == m_SelEnd) {
			// Буферы не заполняются в том случае, если введена одна буква - сильно ускоряет
			// на тормознутых редакторах при быстром вводе

			// стрелка влево
			if (oldSelStart == oldSelEnd && m_SelStart - oldSelStart == 1) {
				if (m_lastInput != 0) {
					boolean separator = isWordSeparator(m_lastInput);
					processReplacements();
					if (separator) {
						handleWordSeparator(m_lastInput);
					}
					processCaseAndCandidates();
					m_lastInput = 0;
				}
				if (m_suggestType != SUGGEST_NONE && m_acPlace == CandView.AC_PLACE_CURSOR_POS)
					m_candView.show(st.kv(), m_acPlace);
			} else {
				getTextBeforeCursor();
				if (m_bCanAutoInput && m_suggestType == SUGGEST_VOCAB)
					try {
						mIc = getCurrentInputConnection();
						if (mIc != null)
							m_textAfterCursor = mIc.getTextAfterCursor(40, 0);
					} catch (Throwable e) {
						m_textAfterCursor = st.STR_NULL;
					}
				processCaseAndCandidates();
			}
			// стрелка вправо
			if (oldSelStart == oldSelEnd && m_SelEnd - oldSelEnd == -1) {
				if (m_lastInput != 0) {
					boolean separator = isWordSeparator(m_lastInput);
					processReplacements();
					if (separator) {
						handleWordSeparator(m_lastInput);
					}
					processCaseAndCandidates();
					m_lastInput = 0;
				}
				if (m_suggestType != SUGGEST_NONE && m_acPlace == CandView.AC_PLACE_CURSOR_POS)
					m_candView.show(st.kv(), m_acPlace);
			} else {
				getTextBeforeCursor();
				if (m_bCanAutoInput && m_suggestType == SUGGEST_VOCAB)
					try {
						mIc = getCurrentInputConnection();
						if (mIc != null)
							m_textAfterCursor = mIc.getTextAfterCursor(40, 0);
					} catch (Throwable e) {
						m_textAfterCursor = st.STR_NULL;
					}
				processCaseAndCandidates();
			}
		}
		// Log.d(PressArray.TAG, "sendKey "+ms);
	}

	boolean processReplacements() {
		JbKbd kbd = st.curKbd();
		if (kbd == null || !kbd.hasReplacements())
			return false;
		if (m_textBeforeCursor == null)
			getTextBeforeCursor();
		ArrayList<Replacement> ar = kbd.getReplacements(m_textBeforeCursor.toString());
		if (ar.size() < 1)
			return false;
		Replacement r = ar.get(0);
		InputConnection ic = getCurrentInputConnection();
		if (ic == null)
			return false;
		ic.beginBatchEdit();
		ic.deleteSurroundingText(r.from.length(), 0);
		ic.commitText(r.to, 1);
		ic.endBatchEdit();
		getTextBeforeCursor();
		return true;
	}

	void processCaseAndCandidates() {
		if (m_bCanAutoInput)
			changeCase(true);
		else
			changeCase(false);
		if (st.fl_suggest_dict)
			m_suggestType = SUGGEST_VOCAB;
		if (m_suggestType == SUGGEST_VOCAB)
			getCandidates();
	}

	public void getCandidates() {
		if (st.fl_suggest_dict) {
			m_suggestType = SUGGEST_VOCAB;
		}

		if (m_suggestType != SUGGEST_VOCAB)
			return;
		try {
			if (m_acPlace == CandView.AC_PLACE_NONE)
				return;
			// закоментил 30.11.18
			//if (m_textBeforeCursor == null)
				getTextBeforeCursor();
			if (m_textAfterCursor == null) {
				mIc = getCurrentInputConnection();
				if (mIc != null)
					m_textAfterCursor = mIc.getTextAfterCursor(40, 0);
			}
			String wstart = Templates.getCurWordStart(m_textBeforeCursor, false);
			String wend = Templates.getCurWordEnd(m_textAfterCursor, false);
			st.this_word = null;
			if (wstart != null && wend != null) {
				// String word = wstart + wend;
				st.this_word = wstart + wend;

				if (st.this_word.length() < 1) {
					if (st.student_dict & st.student_dict_ext) {
						st.freq_dict = 1;
						WordsService.command(WordsService.CMD_EXTENDED_SAVE_WORD, old_word, inst);
					}
					old_word = null;
					WordsService.command(WordsService.CMD_CANCEL_VOCAB, null, inst);
					onWords(null);
				} else {
					WordsService.command(WordsService.CMD_GET_WORDS, st.this_word, inst);
					old_word = st.this_word;
				}
			}
		} catch (Throwable e) {
		}
	}

	/**
	 * Предлагает юзеру набор автодополнений. В браузере - показывает какой-то набор
	 * из закладок и посещенных ссылок
	 */
	@Override
	public void onDisplayCompletions(CompletionInfo[] completions) {

		if (m_candView == null)
			return;
		m_candView.setCompletions(completions);
	}

	/** Обаботка нажатия BACK */
	public boolean handleBackPress() {
		if (isInputViewShown()) {

			if (com_menu.inst != null) {
				com_menu.inst.close();
				com_menu.inst.closeTplAndTrans();
				return true;
			}
			forceHide();
			return true;
		}
		return false;
	}

	@Override
	public boolean onKeyMultiple(int keyCode, int count, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			processTextEditKey(KeyEvent.KEYCODE_DPAD_LEFT);
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			processTextEditKey(KeyEvent.KEYCODE_DPAD_RIGHT);
			return true;
		}
		return super.onKeyMultiple(keyCode, count, event);
	}

	/** key down */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (GlobDialog.gbshow) {
			
			if (fl_newvers) {
				if (GlobDialog.inst != null) {
					GlobDialog.inst.finish();
					return true;
				}
				fl_newvers = false;
				if (JbKbdView.inst != null)
					reinitKeyboardView();
				processCaseAndCandidates();
				return true;
			} 
			else if (GlobDialog.fl_back_key)
				return true;
			else if (GlobDialog.inst != null&&GlobDialog.gbshowEdit) {
				return true;
			}
			else if (GlobDialog.inst != null) {
				GlobDialog.inst.finish();
				return true;
			}
			return false;
		} 
		else if (DlgPopupWnd.inst != null) {
			DlgPopupWnd.inst.dismiss();
			if (rate_dialog) {
				rate_dialog = false;
				if (ini!=null) {
//					long ln = ini.RATE_NEGATIVE_TIME;
//					ln += curtime;
					ini.setParam(ini.START_TIME, st.STR_NULL + (curtime+ini.RATE_NEGATIVE_TIME));
				}
			}

			return true;
		}
     	if (ColorPicker.inst!=null){
    		ColorPicker.inst.finish();
    		return true;
    	}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (JbKbdView.inst != null && JbKbdView.inst.m_pk.fl_popupcharacter_window) {
				JbKbdView.inst.m_pk.close();
				return true;
			}
			// else if ()
			if (event.getRepeatCount() == 0 && handleBackPress()) {
				m_bBackProcessed = true;
			}
			return m_bBackProcessed;
		}
		if (isInputViewShown() && m_volumeKeys > 0
				&& (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {
			processVolumeKey(keyCode, true);
			return true;
		}
		// return super.onKeyDown(keyCode, event);
		return super.onKeyDown(keyCode, event);
	}

	/** Ловим keyUp */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (GlobDialog.gbshow) {
			return false;
		}
		// обработка клавиши Назад (onBackPressed)
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (JbKbdView.inst != null && JbKbdView.inst.m_pk.fl_popupcharacter_window) {
				JbKbdView.inst.m_pk.close();
				return true;
			}
			if (GlobDialog.gbshow) {
				GlobDialog.inst.finish();
				st.showkbd();
				// m_bBackProcessed = false;
				return true;
			} else {
				st.fl_fiks_tpl = true;
				st.fl_fiks_calc = true;
			}
			boolean ret = m_bBackProcessed;
			m_bBackProcessed = false;
			return ret;
		}
		if ((isInputViewShown() || m_volumeKeyTimer != null) && m_volumeKeys > 0
				&& (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {
			processVolumeKey(keyCode, false);
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	/** Helper to send a key down / key up pair to the current editor. */
	private void keyDownUp(int keyEventCode) {
		mIc = getCurrentInputConnection();
		mIc.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
		mIc.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
	}

	final void processChar(char ch) {
		if (m_textBeforeCursor == null)
			m_textBeforeCursor = new StringBuffer();
		m_textBeforeCursor.append(ch);
		if (m_textBeforeCursor.length() > 100)
			m_textBeforeCursor.deleteCharAt(0);
	}

	/** Helper to send a character to the editor as raw key events. */
	void sendKey(int keyCode) {
		processChar((char) keyCode);
		// задержка перед вводом в систему символа
		// закомментил - 16.12.16, посмотрим на реакцию юзеров
		// st.sleep(m_delay_symb);
		
		// задержка между нажатием клавиш, для предотвращения случайных нажатий
		 cur_time = System.currentTimeMillis();
		 if(cur_time - st.min_interval_press_key <= last_time)
			 return;
		 last_time = cur_time;
		if (!st.fl_pref_act) {
			setDelSymb(keyCode);
			setAddSpaceBeforeSymbol(keyCode);
		}
		// добавил 4 строки 20.03.17
		// long TekTime= System.currentTimeMillis(); // добавить
		// if(TekTime - LastTime <= m_delay_symb)
		// return; // добавить
		// LastTime = TekTime;
		st.fl_delsymb = false;
		st.fl_add_space_before_symb = false;
		// присваеваем key клавишу hot
		LatinKey key = st.curKbd().getKeyByCode(st.TXT_HOT);
		String k = st.STR_NULL;
		String txt = st.STR_NULL;
		k += (char) keyCode;
		boolean hot = false;
		for (int i = 0; i < m_hot_count; i++) {
			if (key != null && key.on) {
				if (m_hot_str[i].contains(k.toUpperCase())) {
					txt = m_hot_tpl[i];
					hot = true;
					break;
				}
			}
		}
		if (hot) {
			if (Templates.inst == null) {
				new Templates(1, 0,null);
				Templates.inst.processTemplate(txt);
				Templates.inst = null;
			} else
				Templates.inst.processTemplate(txt);

			if (key != null) {
				key.on = false;
				st.kv().invalidateAllKeys();
			}

			// st.setQwertyKeyboard();

		} else {
			if (st.fl_keycode)
				m_candView.setKeycode(keyCode);
			if (keyCode == '\n') {
				if (!fl_enter)
					sendKeyChar((char) keyCode);
				else
					keyDownUp(KeyEvent.KEYCODE_ENTER);
			} else {
				// sendKeyChar((char) keyCode);
				getCurrentInputConnection().commitText(String.valueOf((char) keyCode), 1);
				// setTextAfterSetText(keyCode);
			}
		}
		// если не будет жалоб,
		// то не забыть выпилить setTextAfterSetText и setDelSymbol

		processCaseAndCandidates();

		// switch (keyCode)
		// {
		// case '\n':
		////// старая отправка ентера
		//// keyDownUp(KeyEvent.KEYCODE_ENTER);
		// sendKeyChar((char) keyCode);
		// break;
		// default:
		//
		// if (input_method==1) {
		// if (keyCode >= '0' && keyCode <= '9')
		// {
		// sendKeyChar((char) keyCode);
		// }
		// else {
		// setText(keyCode);
		// }
		// } else {
		// setText(keyCode);
		// }

		// старый ввод
		// if (input_method==1) {
		// if (keyCode >= '0' && keyCode <= '9')
		// {
		// if (input_method==1) {
		// st.sleep(m_delay_symb);
		// keyDownUp(keyCode - '0' + KeyEvent.KEYCODE_0);
		// }
		// }
		// else {
		// setText(keyCode);
		// }
		// } else {
		// setText(keyCode);
		// }

		// break;
		// }
	}

	public final KeyEvent generateHardwareEvent(int action, int code, int meta) {
		return new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), action, code, 0, meta, 0, 0,
				KeyEvent.FLAG_KEEP_TOUCH_MODE | KeyEvent.FLAG_VIRTUAL_HARD_KEY);
	}

	public int getModifier(int code) {
		if (KeyEvent.isModifierKey(code)) {
			switch (code) {
			case KeyEvent.KEYCODE_ALT_LEFT:
			case KeyEvent.KEYCODE_ALT_RIGHT:
				return KeyEvent.META_ALT_ON;
			case KeyEvent.KEYCODE_CTRL_LEFT:
			case KeyEvent.KEYCODE_CTRL_RIGHT:
				return KeyEvent.META_CTRL_ON;
			case KeyEvent.KEYCODE_SHIFT_LEFT:
			case KeyEvent.KEYCODE_SHIFT_RIGHT:
				return KeyEvent.META_SHIFT_ON;

			}
		}
		return 0;
	}

	public final void sendHardwareSequence(InputConnection ic, Integer... vals) {
		int meta = 0;
		for (int i = 0; i < vals.length; i++) {
			int code = vals[i];
			ic.sendKeyEvent(generateHardwareEvent(KeyEvent.ACTION_DOWN, code, meta));
			meta |= getModifier(code);
		}
		for (int i = vals.length - 1; i >= 0; i--) {
			int code = vals[i];
			ic.sendKeyEvent(generateHardwareEvent(KeyEvent.ACTION_UP, code, meta));
			meta = st.rem(meta, getModifier(code));
		}
	}

	int combocode = 0;
	int maincode = 0;

	/** реализация нажатия комбинации клавиш одной клавишей (ctrl-c, shift-a и тд */
	public boolean processComboKey(LatinKey lk, int primaryCode, InputConnection ic) {
		if (lk == null)
			return false;
		if (JbKbdView.inst == null)
			return false;
		combocode = 0;
		maincode = primaryCode;
		if (!JbKbdView.inst.longpress) {
			if (lk.comboKeyCodes == null)
				return false;
			if (lk.comboKeyCodes.length < JbKbdView.inst.key_iter)
				return false;
			if (JbKbdView.inst.key_iter < 0)
				return false;
			if (primaryCode == Keyboard.KEYCODE_DELETE)
				return false;
			combocode = lk.comboKeyCodes[JbKbdView.inst.key_iter];
		} else {
			if (lk.longComboKeyCode == 0)
				return false;
			combocode = lk.longComboKeyCode;
		}
		if (combocode == 0)
			return false;

		if (maincode > st.KEYCODE_CODE || maincode < st.KEYCODE_CODE - 2000)
			return false;
		ic.beginBatchEdit();
		if (maincode <= st.KEYCODE_CODE && maincode >= st.KEYCODE_CODE - 2000 && combocode <= st.KEYCODE_CODE
				&& combocode >= st.KEYCODE_CODE - 2000) {
			sendHardwareSequence(ic, getKeycode(maincode), getKeycode(combocode));
		} else if (maincode <= st.KEYCODE_CODE && maincode >= st.KEYCODE_CODE - 2000 && combocode > st.KEYCODE_CODE
				&& combocode < st.KEYCODE_CODE - 2000) {
			sendHardwareSequence(ic, getKeycode(maincode), combocode);
		} else if (maincode > st.KEYCODE_CODE && maincode < st.KEYCODE_CODE - 2000 && combocode <= st.KEYCODE_CODE
				&& combocode >= st.KEYCODE_CODE - 2000) {
			sendHardwareSequence(ic, maincode, getKeycode(combocode));
		} else
			sendHardwareSequence(ic, maincode, combocode);

		ic.endBatchEdit();
		return true;
	}

	public final void processKey(int primaryCode) {
		thiskey = null;
		if (!Templates.template_processing) {
			if (st.kv() != null) {
				thiskey = st.kv().lk_this;
				if (st.kv().longpress) {
					if (JbKbdView.processLongKey)
						return;
					else
						JbKbdView.processLongKey = true;
				}

			} else
				thiskey = st.curKbd().getKeyByCode(primaryCode);
			if (thiskey == null)
				thiskey = st.curKbd().getKeyByCode(primaryCode);
				
//			lastkey_index = st.kv().getKeyIndex(thiskey);

			if (thiskey != null && thiskey.mainText != null && thiskey.mainText.length() > 0) {
				if (st.kv() != null) {
					if (st.kv().longpress) {
						if (thiskey.runSpecialInstructions(true))
							return;
					} else {
						if (thiskey.runSpecialInstructions(false))
							return;
					}

				}
			}
			if (thiskey != null && JbKbdView.inst != null && !JbKbdView.inst.longpress
					&& thiskey.shortPopupCharacters.length() > 0) {
				if (!JbKbdView.inst.m_pk.fl_popupcharacter_window) {
					JbKbdView.inst.m_pk.showPopupKeyboard("v2 " + thiskey.shortPopupCharacters.trim());
					return;
				}
			}
			if (st.fl_keycode)
				m_candView.setKeycode(primaryCode);
		}
		beep(primaryCode);
		InputConnection ic = getCurrentInputConnection();
		// обработка комбинаций клавиш
		if (processComboKey(thiskey, primaryCode, ic))
			return;
		// альт или ctrl нажаты
		if (primaryCode > 0 || (primaryCode <= st.KEYCODE_CODE && primaryCode >= st.KEYCODE_CODE - 2000)) {
			if (st.fl_lalt && st.fl_ctrl) {
				ic.beginBatchEdit();
				sendHardwareSequence(ic, KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.KEYCODE_CTRL_LEFT,
						getKeycode(primaryCode));
				ic.endBatchEdit();
				st.fl_lalt = false;
				st.fl_ctrl = false;
				stickyOff(st.TXT_CTRL);
				stickyOff(st.TXT_LALT);
				return;
			}
			if (st.fl_lalt && primaryCode != st.TXT_LALT) {
				sendHardwareSequence(ic, KeyEvent.KEYCODE_ALT_LEFT, getKeycode(primaryCode));
				st.fl_lalt = false;
				stickyOff(st.TXT_LALT);
				return;
			}
			if (st.fl_ralt && st.fl_ctrl) {
				ic.beginBatchEdit();
				sendHardwareSequence(ic, KeyEvent.KEYCODE_ALT_RIGHT, KeyEvent.KEYCODE_CTRL_LEFT,
						getKeycode(primaryCode));
				ic.endBatchEdit();
				st.fl_ralt = false;
				st.fl_ctrl = false;
				stickyOff(st.TXT_CTRL);
				stickyOff(st.TXT_RALT);
				return;
			}
			if (st.fl_ralt && primaryCode != st.TXT_RALT) {
				sendHardwareSequence(ic, KeyEvent.KEYCODE_ALT_RIGHT, getKeycode(primaryCode));
				st.fl_ralt = false;
				stickyOff(st.TXT_RALT);
				return;
			}
			if (st.fl_ctrl && primaryCode != st.TXT_CTRL) {
				ic.beginBatchEdit();
				sendHardwareSequence(ic, KeyEvent.KEYCODE_CTRL_LEFT, getKeycode(primaryCode));
				ic.endBatchEdit();
				st.fl_ctrl = false;
				stickyOff(st.TXT_CTRL);
				return;
			}
		}
		// обработка кейкодов
		if (primaryCode <= st.KEYCODE_CODE && primaryCode >= st.KEYCODE_CODE - 2000) {
			ic.beginBatchEdit();
			sendHardwareSequence(ic, (st.KEYCODE_CODE - primaryCode));
			ic.endBatchEdit();
			return;
		}
		
// основные проверки
		if (primaryCode < -200 && primaryCode > -300) {
			// Смайлики или текстовая метка
			// m_candView.setInd(false);
			LatinKey k = st.curKbd().getKeyByCode(primaryCode);
			if (k != null) {
				onText(k.getMainText());
			}
		} else if (primaryCode < -300 && primaryCode > -398
				|| primaryCode >= KeyEvent.KEYCODE_DPAD_UP && primaryCode <= KeyEvent.KEYCODE_DPAD_RIGHT) {
			// Текстовая клавиатура
			processTextEditKey(primaryCode);

		}
		// код клавиши окончания работы с калькулятором
		else if (primaryCode == st.SET_KEY_CALC_CLOSE && JbKbdView.inst != null) {
			m_candView.setInd(false);
			m_candView.restoreAc_place();
			if (m_acPlace == CandView.AC_PLACE_NONE)
				removeCandView();
			// m_candView.remove();

			if (st.isQwertyKeyboard(st.curKbd().kbd)) {
				st.setSymbolKeyboard(false);
			} else {
				st.setQwertyKeyboard();
			}
		} else if (primaryCode <= -520 && primaryCode >= -600
				|| primaryCode >= KeyEvent.KEYCODE_DPAD_UP && primaryCode <= KeyEvent.KEYCODE_DPAD_RIGHT) {
			// Текстовая клавиатура
			processTextEditKey(primaryCode);

		} else if (primaryCode == Keyboard.KEYCODE_DELETE) {
			handleBackspace();
			selOff();
		}
		// удаление символа справа от курсора
		else if (primaryCode == st.TXT_ED_DEL) {
			handleDelete();
			selOff();
		}
		// удаление слова слева от курсора
		else if (primaryCode == st.TXT_ED_DEL_WORD) {
			ic.beginBatchEdit();
			int symbol = 0;
			CharSequence cs = ic.getTextBeforeCursor(200, 0);
			int len = 0;
			for (int i = cs.length(); i > 0; i--) {
				symbol = cs.charAt(i-1);
				if (isWordSeparator(symbol)) {
					break;
				}
				len++;
			}
			if (len == 0)
				len++;
			ic.deleteSurroundingText(len, 0);
			ic.endBatchEdit();
			processCaseAndCandidates();
// старый код. Закоментил 04.10.19			
//			ic.beginBatchEdit();
//			String symbol = st.STR_NULL;
//			CharSequence cs = ic.getTextBeforeCursor(200, 0);
//			int len = 0;
//			boolean fl = false;
//			for (int i = cs.length(); i > 0; i--) {
//				symbol = cs.subSequence(i - 1, i).toString();
//				if (fl == false)
//					if (symbolDelWord(symbol) == false)
//						len++;
//					else
//						fl = true;
//			}
//			len++;
//			ic.deleteSurroundingText(len, 0);
//			ic.endBatchEdit();
//			processCaseAndCandidates();
		} else if (primaryCode <= -500 && primaryCode >= -519) {
			st.kbdCommand(primaryCode);
		} else if (primaryCode <= -603 && primaryCode >= -700) {
			st.kbdCommand(primaryCode);
		} else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
			if (JbKbdView.inst != null)
				JbKbdView.inst.handleShift();
		} else if (primaryCode == Keyboard.KEYCODE_CANCEL) {
			// прячет список слов из автодополнения, если он на экране
			// if (st.fl_ac_list_view) {
			// m_candView.ViewCandList();
			// return;
			// }

			handleClose();
			return;
		}
		// else if(primaryCode==10)
		// {
		// getCurrentInputConnection().performEditorAction(getCurrentInputEditorInfo().actionId);
		// }
		else if (primaryCode == st.CMD_LANG_CHANGE) {
			st.kv().handleLangChange(true, 0);

		} else if (primaryCode == st.CMD_LANG_CHANGE_NEXT_LANG) {
			st.kv().handleLangChange(false, 0);
		} else if (primaryCode == st.CMD_LANG_CHANGE_PREV_LANG) {
			st.kv().handleLangChange(false, 1);
		} else if (primaryCode == st.CMD_LANG_CHANGE_TWO_LANG) {
			st.kv().handleLangChange(false, 2);
		}
		// else if (primaryCode == st.CMD_LANG_CHANGE)
		// {
		// st.kv().handleLangChange(true,true);
		// }
		else if (primaryCode == st.CMD_LANG_MENU) {
			selmode = false;
			com_menu.showLangs(inst);
		}
		// какую раскладку выводить, hide, цмфровую или qwerty
		else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE && JbKbdView.inst != null) {
			selmode = false;
			JbKbd kb = st.curKbd();
			String name = kb.kbd.lang.name;
			if (name.startsWith(IKeyboard.LANG_HIDE_LAYOUT)) {
//			if (st.curKbd().kbd.path.startsWith("hide_") == true) {
				st.setQwertyKeyboard();
			} else if (st.isQwertyKeyboard(st.curKbd().kbd) == true) {
				st.setSymbolKeyboard(false);
			} else if (st.isQwertyKeyboard(st.curKbd().kbd) == false) {
				st.setQwertyKeyboard();
			}

			// if (st.isQwertyKeyboard(st.curKbd().kbd))
			// {
			// st.setSymbolKeyboard(false);
			// }
			// else
			// {
			// st.setQwertyKeyboard();
			// }
		} else if (primaryCode == 0) {

		} else {
			m_lastInput = primaryCode;
			firstsymb = false;
			if (lastkey_index > -1 && lastkey_index == st.kv().getKeyIndex(thiskey)) {
//			LatinKey lastkey = st.kv().lk_prev;
//			if(lastkey_index > -1 && thiskey == st.kv().lk_prev) {
				//int iii = isLastCase(thiskey, primaryCode);
				if (isLastCase(thiskey, primaryCode) > 0) {
					firstsymb = true;//JbKbdView.inst.isUpperCase();
				}
			} else
				firstsymb = false;
			//st.toast(""+firstkey_upper+" "+firstsymb);
			if (firstkey_upper&&firstsymb) {
				primaryCode = Character.toUpperCase(primaryCode);
				try {
					st.kv().setTempShift(true, false);
				} catch (Exception e) {
				}
			}
			if (isWordSeparator(primaryCode) && m_acAutocorrect) {

				if (handleWordSeparator(primaryCode))
					handleCharacter(primaryCode);

			} else
				handleCharacter(primaryCode);
		}
		setImeOptions();
		checkGoQwerty(primaryCode);
		lastkey_index = st.kv().getKeyIndex(thiskey);
		
		setCountTextValue();
	}
/** Определяем что thiskey есть в клавише key, для установки 
 * регистра вывода символа. <br>
 * Если есть, то возвращаем индекс символа в клавише, иначе -1
 * */
	public int isLastCase(LatinKey key, int thiskey) {
		if (thiskey == 0)
			return -1;
		for (int i = 0; i < key.codes.length; i++) {
			if (key.codes[i] == thiskey)
				return i;
		}
		return -1;
	}

	/**
	 * Проверяет, нужно ли выполнить переход к qwerty-клавиатуре по нажатию клавиши.
	 * Если нужно - выполняет переход
	 * 
	 * @param primaryCode
	 *            код нажатой клавиши
	 * @return true - клавиатура закрыта, false - не закрыта
	 */
	public final boolean checkGoQwerty(int primaryCode) {
		JbKbd kbd = st.curKbd();
		if (kbd == null || st.isQwertyKeyboard(kbd.kbd))
			return false;
		LatinKey k = kbd.getKeyByCode(primaryCode);
		if (k == null || st.has(k.flags, LatinKey.FLAG_NOT_GO_QWERTY))
			return false;
		if (kbd.kbd.lang.lang == st.LANG_SMIL || st.has(k.flags, LatinKey.FLAG_GO_QWERTY)) {
			st.setQwertyKeyboard(true);
			return true;
		}
		return false;
	}

	public void onKey(int primaryCode, int[] keyCodes) {
		// кейэвенты неподдерживаемые ниже андроид 3
		if (st.isHoneycomb()) {
			switch (primaryCode) {
			case st.TXT_CTRL:
				// KeyEvent.KEYCODE_CTRL_LEFT и KeyEvent.KEYCODE_CTRL_RIGHT
			case -5113:
			case -5114:
				st.toast(R.string.honeycomb);
				stickyOff(primaryCode);
				return;

			}
		}
		if (st.fl_popupcharacter2) {
			st.fl_popupcharacter2 = false;
			return;
		}
		if (!isInputViewShown())
			return;
		if (isMacro(primaryCode))
			return;
		processKey(primaryCode);

		// ServiceJbKbd.inst.createNewCandView();
		// ServiceJbKbd.inst.showCandView(true);
		// ServiceJbKbd.inst.getCandidates();
		// if(isInputViewShown())
		// st.kv().invalidateAllKeys();

	}

	/**
	 * Проверка, можно ли использовать автосмену регистра и вставку пробелов
	 * 
	 * @param ei
	 *            Информация о редакторе
	 * @return true - используется автоввод, false - не используется
	 */
	final boolean canAutoInput(EditorInfo ei) {
//		if (!st.has(m_state, STATE_AUTO_CASE))
		if (!state_auto_case)
			return false;
		try {
			int var = ei.inputType & EditorInfo.TYPE_MASK_VARIATION;
			int type = ei.inputType & EditorInfo.TYPE_MASK_CLASS;
			return type == EditorInfo.TYPE_CLASS_TEXT && ei != null
					&& var != EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS && var != EditorInfo.TYPE_TEXT_VARIATION_URI
					&& var != EditorInfo.TYPE_TEXT_VARIATION_PASSWORD
					&& var != EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
					&& var != EditorInfo.TYPE_TEXT_VARIATION_FILTER;
		} catch (Throwable e) {
		}
		return false;
	}

	private final boolean handleWordSeparator(int primaryCode) {
		boolean ret = false;
		if (!(m_acAutocorrect && primaryCode != '\'' && m_candView != null
				&& m_candView.applyCorrection(primaryCode))) {
			ret = true;
		}
		if (m_bCanAutoInput) {
			if (st.has(m_state, STATE_SENTENCE_SPACE) && m_SpaceSymbols.indexOf(primaryCode) > -1) {
				// if (fl_text) {
				if (st.fl_pref_act == false) {
					sendKey(' ');
					ret = true;
				}
			}
		}
		return ret;
	}

	public void onText(CharSequence text) {
		if (text == null)
			return;
		if (text.length() == 1) {
			if (st.fl_keycode)
				m_candView.setKeycode(text.charAt(0));
			int pc = (int) text.charAt(0);
			if (isWordSeparator(pc)) {
				handleWordSeparator(pc);
			}
		}
		InputConnection ic = getCurrentInputConnection();
		if (ic == null)
			return;
		int newpos = 0;
		if (ic.getSelectedText(0) != null) {
			newpos = 1;
		}
		// st.sleep(m_delay_symb);

		ic.beginBatchEdit();
		// откоментил 01.06.18
		// до этого была строчка ниже
		ic.commitText(text, text.length() > 0 ? 1 : 0);
		// ic.commitText(text, newpos);
		ic.endBatchEdit();
		processCaseAndCandidates();
	}

	private void handleBackspace() {
		keyDownUp(KeyEvent.KEYCODE_DEL);
		processCaseAndCandidates();
		selOff();
	}

	boolean m_bsel = false;

	private void handleDelete() {
		InputConnection ic = getCurrentInputConnection();
		boolean bbb = false;
		CharSequence cs = ic.getSelectedText(0);

		if (cs != null && cs.length() > 0)
			bbb = true;
		s1 = st.STR_NULL + ic.getTextAfterCursor(1, 0);
		if (bbb == false && s1.length() > 0) {
			if (android.os.Build.VERSION.SDK_INT < 11) {
				sendHardwareSequence(ic, KeyEvent.KEYCODE_DPAD_RIGHT);
				sendHardwareSequence(ic, KeyEvent.KEYCODE_DEL);
			} else {
				sendHardwareSequence(ic, KeyEvent.KEYCODE_FORWARD_DEL);
			}
		}
		if (bbb)
			handleBackspace();
		m_textAfterCursor = null;
		m_textBeforeCursor = null;
		processCaseAndCandidates();
		selOff();
	}

	public void handleCharacter(int primaryCode) {
		if (isInputViewShown()) {
			if (JbKbdView.inst.isUpperCase()) {
				primaryCode = Character.toUpperCase(primaryCode);
				firstkey_upper = true;
			} else
				firstkey_upper = false;
		}
		sendKey(primaryCode);
		if (st.has(JbKbdView.inst.m_state, JbKbdView.STATE_TEMP_SHIFT)) {
			JbKbdView.inst.m_state = st.rem(JbKbdView.inst.m_state, JbKbdView.STATE_TEMP_SHIFT);
			JbKbdView.inst.setShifted(st.has(JbKbdView.inst.m_state, JbKbdView.STATE_CAPS_LOCK));
			JbKbdView.inst.invalidateAllKeys();
		}
	}

	public void handleClose() {
		JbKbdView.inst.closing();
		forceHide();
	}

	public boolean isWordSeparator(int code) {
		fl_word_separator = !Character.isLetterOrDigit(code);
		// if (fl_word_separator)
		// old_word = null;

		// cur_lang = Lang.getLangShortName(IKeyboard.LANG_UZ);
		// if (cur_lang!=null&&st.getCurLang().compareToIgnoreCase(cur_lang)==0){
		// switch (code)
		// {
		// case 8216:
		// case 8217:
		// fl_word_separator = true;
		// }
		// }
		return fl_word_separator;
	}

	public void setCompletionInfo(CompletionInfo ci) {
		InputConnection ic = getCurrentInputConnection();
		if (ic != null)
			ic.commitCompletion(ci);
	}

	public void setWord(String word, boolean autoCorrect) {
		CurInput ci = new CurInput();
		InputConnection ic = getCurrentInputConnection();
		ic.beginBatchEdit();
		if (autoCorrect)
			ic.deleteSurroundingText(1, 0);
		if (ci.init(ic)) {
			String sss = null;
			try {
				sss = ic.getSelectedText(0).toString();
			} catch (Throwable e) {
				sss = null;
			}
			if (sss!=null) {
				handleBackspace();
				sss = st.STR_SPACE;
			} else {
				sss = (String) ic.getTextBeforeCursor(1, 0);
				if (sss==null)
					sss=st.STR_NULL;
				if (st.fl_delsymb) {
					if (st.fl_ac_word)
						sss = st.STR_SPACE;
					else
						sss = "tgd";
					st.fl_delsymb = false;
					st.fl_ac_word = false;
				}
			}
			st.fl_add_space_before_symb = false;
			if (sss.compareTo(st.STR_SPACE) == 0)
				ic.commitText(word, 1);
			else
				ci.replaceCurWord(ic, word);
		}
		ic.endBatchEdit();
		processCaseAndCandidates();
	}

	public void swipeRight() {
	}

	public void swipeLeft() {
	}

	public void swipeDown() {
	}

	public void swipeUp() {
	}

	public void onPress(int primaryCode) {
	}

	public void onRelease(int primaryCode) {
		JbKbdView.processLongKey = false;
		// вроде не нужна...
		// if (st.kv()!=null)
		// st.kv().longpress=false;
	}

	public void onOptions() {
		final com_menu menu = new com_menu();
		menu.close_menu = false;
		menu.setMenuname(textMenuName(st.CMD_MAIN_MENU));
		st.UniObserver onMenu = new st.UniObserver() {
			public int OnObserver(Object param1, Object param2) {
				int id = ((Integer) param1).intValue();
				switch (id) {
				case st.CMD_RELOAD_SKIN:
					st.kv().reloadSkin();
					menu.close();
					break;
				case st.CMD_DECOMPILE_KEYBOARDS:
					compiledKbdToXML();
					menu.close();
					break;
				default:
					st.kbdCommand(id);
				}
				return 0;
			}
		};
		Mainmenu mmenu = new Mainmenu();
		if (Mainmenu.arMenu == null || Mainmenu.arMenu.isEmpty())
			Mainmenu.arMenu.addAll(mmenu.getDefaultItem());
		for (Mainmenu mm : Mainmenu.arMenu) {
			switch (mm.code) {
			case st.CMD_AC_HIDE:
				if (st.ac1 == 1) {
					if (m_acPlace == 1)
						menu.add(R.string.mm_ac_hide1, st.CMD_AC_HIDE);
				} else {
					if (m_acPlace < 2)
						menu.add(R.string.mm_ac_hide2, st.CMD_AC_HIDE);
				}
				break;
			case st.CMD_TEMP_STOP_DICT:
				if (st.fl_temp_stop_dict)
					menu.add(R.string.mm_stop_dict2, st.CMD_TEMP_STOP_DICT);
				else
					menu.add(R.string.mm_stop_dict1, st.CMD_TEMP_STOP_DICT);
				break;
			case st.CMD_RELOAD_SKIN:
				String path = st.kv().m_curDesign.path;
				if (path != null && !path.startsWith(CustomKbdDesign.ASSETS))
					menu.add(getString(R.string.mm_reload_skin), st.CMD_RELOAD_SKIN);
				break;
			default:
				menu.add(mm.name, mm.code);

			}
		}

		menu.show(onMenu, false);
	}

	public void onCalcMenu() {
		com_menu menu = new com_menu();
		menu.setMenuname(textMenuName(st.CMD_CALC));
		st.UniObserver onMenu = new st.UniObserver() {
			public int OnObserver(Object param1, Object param2) {
				int id = ((Integer) param1).intValue();
				st.kbdCommand(id);
				return 0;
			}
		};

		String[] sss;
		LatinKey key = st.curKbd().getKeyByCode(st.SET_KEY_CALC_CX);
		if (key.calc_menu != null &&key.calc_menu!=null&& !key.calc_menu.isEmpty()) {
			sss = key.calc_menu.split(st.STR_SPACE);
		} else {
			String ss1 = "history";
			sss = ss1.split(st.STR_SPACE);
		}
		for (int i = 0; i < sss.length; i++) {
			if (sss[i].contains("history")) {
				menu.add(R.string.calc_menu_history, st.CMD_CALC_HISTORY);
			}
			if (sss[i].contains("list")) {
				menu.add(R.string.calc_menu_list, st.CMD_CALC_LIST);
			}
			if (sss[i].contains("save")) {
				menu.add(R.string.calc_save, st.CMD_CALC_SAVE);
			}
			if (sss[i].contains("load")) {
				menu.add(R.string.calc_load, st.CMD_CALC_LOAD);
			}
		}
		menu.show(onMenu, false);
	}

	public void onVoiceRecognition(final ArrayList<String> ar) {
		if (ar == null) {
			forceShow();
			return;
		}
		com_menu menu = new com_menu();
		for (int i = 0; i < ar.size(); i++) {
			menu.add(ar.get(i), i);
		}
		st.UniObserver obs = new st.UniObserver() {
			@Override
			public int OnObserver(Object param1, Object param2) {
				int index = ((Integer) param1).intValue();
				onText(ar.get(index));
				return 0;
			}
		};
		menu.show(obs, false);
		if (!isInputViewShown()) {
			forceShow();
		}
	}

	/**
	 * Возвращает true, если находимся в режиме выделения текста, иначе возвращает
	 * false
	 */
	final boolean isSelMode() {
		LatinKey key = st.curKbd().getKeyByCode(st.TXT_ED_SELECT);
		if (key != null && key.on) {
			return true;
		}
		if (selmode)
			return true;
		return false;
	}
	/** Обработка клавиш с клавиатуры для текстовых операций */
	void processTextEditKey(int code) {
		InputConnection ic = getCurrentInputConnection();
		if (code == st.TXT_ED_SELECT) {
			if (selmode) {
				ic.performContextMenuAction(android.R.id.stopSelectingText);
				selmode = false;
			} else {
				ic.performContextMenuAction(android.R.id.startSelectingText);
				selmode = true;
			}
			return;
		}
		switch (code) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
		case KeyEvent.KEYCODE_DPAD_RIGHT:
		case KeyEvent.KEYCODE_DPAD_UP: // Up
		case KeyEvent.KEYCODE_DPAD_DOWN: // Down
			if (code == KeyEvent.KEYCODE_DPAD_RIGHT || code == KeyEvent.KEYCODE_DPAD_DOWN) {
				CharSequence s = ic.getTextAfterCursor(1, 0);
				if (s == null || s.length() == 0) {
					if (code == KeyEvent.KEYCODE_DPAD_RIGHT||code == KeyEvent.KEYCODE_DPAD_DOWN)
						if (m_arrow_key) {
							processTextEditKey(st.TXT_ED_START);
						} else {
							if (isSelMode() == false && getSelectSize() > 0) {
								keyDownUp(KeyEvent.KEYCODE_DPAD_RIGHT);
							} else {
								if (isSelMode()) {
									sendHardwareSequence(ic, KeyEvent.KEYCODE_SHIFT_LEFT, code);
									return;
								}
							}
						}
					return;
				}
			}
			if (code == KeyEvent.KEYCODE_DPAD_LEFT || code == KeyEvent.KEYCODE_DPAD_UP) {
				CharSequence s = ic.getTextBeforeCursor(1, 0);
				if (s == null || s.length() == 0) {
					if (code == KeyEvent.KEYCODE_DPAD_LEFT||code == KeyEvent.KEYCODE_DPAD_UP)
						if (m_arrow_key) {
							processTextEditKey(st.TXT_ED_FINISH);
						} else {
							if (isSelMode() == false && getSelectSize() > 0) {
								keyDownUp(KeyEvent.KEYCODE_DPAD_LEFT);
							} else {
								if (isSelMode()) {
									sendHardwareSequence(ic, KeyEvent.KEYCODE_SHIFT_LEFT, code);
									return;
								}
							}
						}
					return;
				}
			}
			boolean sel = isSelMode();
			if (sel)
				sendHardwareSequence(ic, KeyEvent.KEYCODE_SHIFT_LEFT, code);
			else
				sendHardwareSequence(ic, code);
			break;
		case st.TXT_LALT: // alt (left)
			if (st.fl_lalt) {
				st.fl_lalt = false;
			} else {
				st.fl_lalt = true;
			}
			return;
		case st.TXT_RALT: // alt (right)
			if (st.fl_ralt) {
				st.fl_ralt = false;
			} else {
				st.fl_ralt = true;
			}
			return;
		case st.TXT_CTRL: // ctrl (left)
			if (st.fl_ctrl) {
				st.fl_ctrl = false;
			} else {
				st.fl_ctrl = true;
			}
			return;
		case st.CLR_MACRO1:
			GlobDialog gd = new GlobDialog(st.c());
			gd.set(R.string.clear_question, R.string.yes, R.string.no);
			gd.setObserver(new st.UniObserver() {
				@Override
				public int OnObserver(Object param1, Object param2) {
					if (((Integer) param1).intValue() == AlertDialog.BUTTON_POSITIVE) {
						st.macro1.clear();
						st.pref().edit().putString("macro1", st.STR_NULL).commit();
					}
					return 0;
				}
			});
			gd.showAlert();
			return;
		case st.CLR_MACRO2:
			GlobDialog gd2 = new GlobDialog(st.c());
			gd2.set(R.string.clear_question, R.string.yes, R.string.no);
			gd2.setObserver(new st.UniObserver() {
				@Override
				public int OnObserver(Object param1, Object param2) {
					if (((Integer) param1).intValue() == AlertDialog.BUTTON_POSITIVE) {
						st.pref().edit().putString("macro2", st.STR_NULL).commit();
						st.macro2.clear();
					}
					return 0;
				}
			});
			gd2.showAlert();
			return;
		case st.TXT_ED_START:
			boolean ret = ic.setSelection(isSelMode() ? m_SelEnd : 0, 0);
			if (!ret) {
				ic.setSelection(0, 0);
			}
			break;
		case st.TXT_ED_FINISH:
			m_state |= STATE_GO_END;
			ic.performContextMenuAction(android.R.id.selectAll);
			break;
		case st.TXT_ED_UNDO:
			sendHardwareSequence(ic, KeyEvent.KEYCODE_CTRL_LEFT, KeyEvent.KEYCODE_Z);
			break;
		case st.TXT_ED_REDO:
			sendHardwareSequence(ic, KeyEvent.KEYCODE_CTRL_LEFT, KeyEvent.KEYCODE_Y);
			break;
		case st.TXT_ED_HOME: // Home paragraph
			handleHomeParagrapf(isSelMode());
			break;
		case st.TXT_ED_END: // End paragraph
			handleEndParagrapf(isSelMode());
			break;
		case st.TXT_ED_PG_UP: // pg_up
			if (isSelMode())
				sendHardwareSequence(ic, KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_PAGE_UP);
			else
				sendHardwareSequence(ic, KeyEvent.KEYCODE_PAGE_UP);
			break;
		case st.TXT_ED_PG_DOWN: // pg_down
			if (isSelMode())
				sendHardwareSequence(ic, KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_PAGE_DOWN);
			else
				sendHardwareSequence(ic, KeyEvent.KEYCODE_PAGE_DOWN);
			break;
		case st.TXT_ED_HOME_STR: // home string
			if (isSelMode())
				sendHardwareSequence(ic, KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_MOVE_HOME);
			else
				sendHardwareSequence(ic, KeyEvent.KEYCODE_MOVE_HOME);
			break;
		case st.TXT_ED_END_STR: // end string
			if (isSelMode())
				sendHardwareSequence(ic, KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_MOVE_END);
			else
				sendHardwareSequence(ic, KeyEvent.KEYCODE_MOVE_END);
			break;
		case st.TXT_ED_SELECT_ALL: // Select all
//			закоментил 14.07.19
//			посмотрим на реакцию юзеров
//			if (!st.isHoneycomb())
//				sendHardwareSequence(ic, KeyEvent.KEYCODE_CTRL_LEFT, KeyEvent.KEYCODE_A);
//			else
				ic.performContextMenuAction(android.R.id.selectAll);
			processCaseAndCandidates();
			selOff();
			break;
		case st.TXT_SELECT_PARAGRAPF: // команды выделения
		case st.TXT_SELECT_LINE: 
		case st.TXT_SELECT_SENTENCE:
		case st.TXT_SELECT_WORD:
			st.kbdCommand(code);
			break;
		case st.TXT_SELECT_FUNCTION: // функции выделения
			com_menu.showFuncSelect(inst);
			break;
		case st.TXT_ED_CUT: // Cut
			ic.performContextMenuAction(android.R.id.cut);
			selOff();
			break;
		case st.TXT_ED_COPY: // Copy
			if (m_SelStart == m_SelEnd) {
				com_menu.showFuncCopy(inst);
			} else {
				int iii = getSelectSize();
				if (iii > 0) {
					ic.performContextMenuAction(android.R.id.copy);
					st.messageCopyClipboard();
				}
			}
			selOff();
			break;
		case st.TXT_ED_PASTE: // Paste
			CharSequence str = st.getClipboardCharSequence();
			int pos = Math.min(m_SelStart, m_SelEnd) + (str.length());
			onText(str);
			ic.setSelection(pos, pos);
			// ic.performContextMenuAction(android.R.id.paste);
			selOff();
			break;
		case st.TXT_ED_COPY_ALL: // Copy all
			ic.beginBatchEdit();
			ic.performContextMenuAction(android.R.id.selectAll);
			ic.performContextMenuAction(android.R.id.copy);
			ic.setSelection(m_SelStart, m_SelEnd);
			ic.endBatchEdit();
			st.messageCopyClipboard();
			selOff();
			break;
		case st.TXT_ED_SIZE_SELECTED: // Size selected
			st.toast("Size selected: " + getSelectSize() + " bytes");
			break;
		// калькулятор
		case st.CMD_CALC: // запуск калькулятора
			st.setCalcKeyboard();
			break;
		case st.SET_KEY_CALC_NUMBER0: // клавиша 0
			m_candView.setCalcInd(0, code);
			break;
		case st.SET_KEY_CALC_NUMBER1: // клавиша 1
			m_candView.setCalcInd(1, code);
			break;
		case st.SET_KEY_CALC_NUMBER2: // клавиша 2
			m_candView.setCalcInd(2, code);
			break;
		case st.SET_KEY_CALC_NUMBER3: // клавиша 3
			m_candView.setCalcInd(3, code);
			break;
		case st.SET_KEY_CALC_NUMBER4: // клавиша 4
			m_candView.setCalcInd(4, code);
			break;
		case st.SET_KEY_CALC_NUMBER5: // клавиша 5
			m_candView.setCalcInd(5, code);
			break;
		case st.SET_KEY_CALC_NUMBER6: // клавиша 6
			m_candView.setCalcInd(6, code);
			break;
		case st.SET_KEY_CALC_NUMBER7: // клавиша 7
			m_candView.setCalcInd(7, code);
			break;
		case st.SET_KEY_CALC_NUMBER8: // клавиша 8
			m_candView.setCalcInd(8, code);
			break;
		case st.SET_KEY_CALC_NUMBER9: // клавиша 9
			m_candView.setCalcInd(9, code);
			break;
		case st.SET_KEY_CALC_MULTIPLY: // клавиша умножить
			m_candView.setCalcInd(10, code);
			break;
		case st.SET_KEY_CALC_DIVIDE: // клавиша делить
			m_candView.setCalcInd(11, code);
			break;
		case st.SET_KEY_CALC_PLUS: // клавиша плюс
			m_candView.setCalcInd(12, code);
			break;
		case st.SET_KEY_CALC_MINUS: // клавиша минус
			m_candView.setCalcInd(13, code);
			break;
		case st.SET_KEY_CALC_CX: // клавиша Сх
			m_candView.setCalcInd(14, code);
			break;
		case st.SET_KEY_CALC_B_UP: // клавиша B стрелка вверх
			m_candView.setCalcInd(15, code);
			break;
		case st.SET_KEY_CALC_XY: // клавиша обмен регистров x и y
			m_candView.setCalcInd(16, code);
			break;
		case st.SET_KEY_CALC_MODUL_MINUS: // клавиша "минус по модулю"
			m_candView.setCalcInd(17, code);
			break;
		case st.SET_KEY_CALC_ZERO: // клавиша "точка"
			m_candView.setCalcInd(18, code);
			break;
		case st.SET_KEY_CALC_ON: // клавиша включения калькулятора
			m_candView.setCalcInd(19, code);
			break;
		case st.SET_KEY_CALC_X_KVADRAT: // клавиша x^2
			m_candView.setCalcInd(20, code);
			break;
		case st.SET_KEY_CALC_X_SQRT: // клавиша sqrt(x)
			m_candView.setCalcInd(21, code);
			break;
		case st.SET_KEY_CALC_UP: // клавиша "шаг вперед"
			m_candView.setCalcInd(22, code);
			break;
		case st.SET_KEY_CALC_DOWN: // клавиша "шаг назад"
			m_candView.setCalcInd(23, code);
			break;
		case st.SET_KEY_CALC_PROC: // клавиша %
			m_candView.setCalcInd(24, code);
			break;
		case st.SET_KEY_CALC_MEMORY: // клавиша M
			m_candView.setCalcInd(25, code);
			break;
		case st.SET_KEY_CALC_MR: // клавиша MR
			m_candView.setCalcInd(26, code);
			break;
		case st.SET_KEY_CALC_MC: // клавиша MC
			m_candView.setCalcInd(27, code);
			break;
		case st.SET_KEY_CALC_PX: // клавиша ПХ
			m_candView.setCalcInd(28, code);
			break;
		case st.SET_KEY_CALC_IPX: // клавиша ИПХ
			m_candView.setCalcInd(29, code);
			break;
		case st.SET_KEY_CALC_VO: // клавиша в/о
			m_candView.setCalcInd(30, code);
			break;
		case st.SET_KEY_CALC_AUTO: // клавиша режима автомат
			m_candView.setCalcInd(31, code);
			break;
		case st.SET_KEY_CALC_PRG: // клавиша режима прг
			m_candView.setCalcInd(32, code);
			break;
		case st.SET_KEY_CALC_CLR: // клавиша clr
			m_candView.setCalcInd(33, code);
			break;
		case st.SET_KEY_CALC_SP: // клавиша с/п
			m_candView.setCalcInd(34, code);
			break;
		case st.SET_KEY_CALC_BP: // клавиша БП
			m_candView.setCalcInd(35, code);
			break;
		case st.SET_KEY_CALC_PI: // число ПИ
			m_candView.setCalcInd(42, code);
			break;
		case st.SET_KEY_CALC_SIN: // синус
			m_candView.setCalcInd(43, code);
			break;
		case st.SET_KEY_CALC_COS: // косинус
			m_candView.setCalcInd(44, code);
			break;
		case st.SET_KEY_CALC_TAN: // тангенс
			m_candView.setCalcInd(45, code);
			break;
		case st.SET_KEY_CALC_ASIN: // арксинус
			m_candView.setCalcInd(46, code);
			break;
		case st.SET_KEY_CALC_ACOS: // арккосинус
			m_candView.setCalcInd(47, code);
			break;
		case st.SET_KEY_CALC_ATAN: // арктангенс
			m_candView.setCalcInd(48, code);
			break;
		case st.SET_KEY_CALC_1X: // 1/x
			m_candView.setCalcInd(49, code);
			break;
		case st.SET_KEY_CALC_XSTY: // x^y
			m_candView.setCalcInd(50, code);
			break;
		case st.SET_KEY_CALC_LOG: // LOG
			m_candView.setCalcInd(51, code);
			break;
		case st.SET_KEY_CALC_RND: // random
			m_candView.setCalcInd(52, code);
			break;
		case st.SET_KEY_CALC_INT: // целая часть числа
			m_candView.setCalcInd(53, code);
			break;
		case st.SET_KEY_CALC_LOG10: // LOG10
			m_candView.setCalcInd(54, code);
			break;
		case st.SET_KEY_CALC_GTOR: // градусы в радианы
			m_candView.setCalcInd(55, code);
			break;
		case st.SET_KEY_CALC_RTOG: // радианы в градусы
			m_candView.setCalcInd(56, code);
			break;
		case st.SET_KEY_CALC_ROUND: // округленние (нажал round, нажал цифру количество знаков после запятой
			m_candView.setCalcInd(57, code);
			break;
		case st.SET_KEY_CALC_E: // константа Е
			m_candView.setCalcInd(58, code);
			break;
		case st.SET_KEY_CALC_EX: // Ех
			m_candView.setCalcInd(59, code);
			break;
		case st.SET_KEY_CALC_FACTORIAL: // факториал
			m_candView.setCalcInd(60, code);
			break;

		case st.SET_KEY_CALC_PP: // подпрограмма
			m_candView.setCalcInd(61, code);
			break;
		case st.SET_KEY_CALC_BOLSHERAVNO_ZERO: // х>=0
			m_candView.setCalcInd(62, code);
			break;
		case st.SET_KEY_CALC_MENSHE_ZERO: // х<0
			m_candView.setCalcInd(63, code);
			break;
		case st.SET_KEY_CALC_PAVNO_ZERO: // х=0
			m_candView.setCalcInd(64, code);
			break;
		case st.SET_KEY_CALC_NERAVNO_ZERO: // х!=0
			m_candView.setCalcInd(65, code);
			break;
		case st.SET_KEY_CALC_BOLSHERAVNO_Y: // х>=y
			m_candView.setCalcInd(66, code);
			break;
		case st.SET_KEY_CALC_MENSHE_Y: // х<y
			m_candView.setCalcInd(67, code);
			break;
		case st.SET_KEY_CALC_RAVNO_Y: // х=y
			m_candView.setCalcInd(68, code);
			break;
		case st.SET_KEY_CALC_HERAVNO_Y: // х!=y
			m_candView.setCalcInd(69, code);
			break;
		case st.SET_KEY_CALC_TTOX: // reg t to x
			m_candView.setCalcInd(70, code);
			break;
		case st.SET_KEY_CALC_VP: // ВП (х в степени 10)
			m_candView.setCalcInd(71, code);
			break;
		}
	}

	/**
	 * Форсированно показывает окно клавиатуры, запоминает статус в
	 * {@link #m_bForceShow}
	 */
	void forceShow() {
		showWindow(true);
		m_bForceShow = true;
	}

	public void forceHide() {
		if (st.fl_ac_list_view) {
			m_candView.popupViewFullList();
			return;
		}
		hideWindow();
		if (m_bForceShow) {
			m_bForceShow = false;
		}
		// requestHideSelf(0);
	}

	void handleHomeParagrapf(boolean bSel) {
		try {
			InputConnection ic = getCurrentInputConnection();
			String s = ic.getTextBeforeCursor(4000, 0).toString();
			int pos = Templates.chkPos(s.lastIndexOf('\n'), s.lastIndexOf('\r'), true, s.length());
			if (pos == -1)
				return;
			int cp = m_SelStart > m_SelEnd ? m_SelEnd : m_SelStart;
			cp = cp - (s.length() - pos);
			ic.setSelection(bSel ? m_SelStart : cp, cp);
		} catch (Throwable e) {
		}
	}

	@Override
	public void onAppPrivateCommand(String action, android.os.Bundle data) {
		super.onAppPrivateCommand(action, data);
	};

	@Override
	public boolean onExtractTextContextMenuItem(int id) {
		return super.onExtractTextContextMenuItem(id);
	};

	void handleEndParagrapf(boolean bSel) {
		try {
			InputConnection ic = getCurrentInputConnection();
			String s = ic.getTextAfterCursor(4000, 0).toString();
			int pos = Templates.chkPos(s.indexOf('\n'), s.indexOf('\r'), false, s.length());
			if (pos < 0)
				return;
			int cp = m_SelStart > m_SelEnd ? m_SelStart : m_SelEnd;
			cp = cp + pos;
			ic.setSelection(bSel ? m_SelStart : cp, cp);
		} catch (Throwable e) {
		}
	}

	// удаление старых настроек из файла настроек
	public void removeSharedPreferences() {
		st.pref().edit().remove("ac_view");
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		removeSharedPreferences();
		VibroThread.getInstance(this).readSettings();
		setShowIconLauncher(true);
		if (st.KBD_BACK_ALPHA.equals(key) || key == null) {
			st.kbd_back_alpha = st.str2int(
					sharedPreferences.getString(st.KBD_BACK_ALPHA, st.STR_NULL + st.KBD_BACK_ALPHA_DEF), 0,
					st.KBD_BACK_ALPHA_DEF, "Error read. Set default value");
			st.setDefaultDesign();
		}
		st.debug_mode= sharedPreferences.getBoolean(st.PREF_KEY_DEBUG_MODE, false);
		st.color_picker_type= sharedPreferences.getBoolean(st.PREF_KEY_TYPE_COLOR_PICKER, false);
		//st.ac_sub_panel = sharedPreferences.getBoolean(st.PREF_AC_SUB_PANEL, false);
		if (st.PREF_AC_WINDOW_TYPE.equals(key) || key == null) {
			st.type_ac_window = Integer.decode(sharedPreferences.getString(st.PREF_AC_WINDOW_TYPE, st.STR_NULL+st.TYPE_AC_METHOD2));
			//sharedPreferences.getInt(st.PREF_AC_WINDOW_TYPE, st.TYPE_AC_METHOD2);
			m_candView = null;
			m_candView = createNewCandView();
		}
		try {
	        st.enter_pict = Integer.decode(st.pref(this).getString(st.PREF_ENTER_PICT, st.STR_ZERO));
			
		} catch (Throwable e) {
			st.enter_pict = 0;
		}
        st.min_interval_press_key =  (long)sharedPreferences.getInt(st.PREF_KEY_MINIMAL_PRESS_INTERVAL, 1);

		if (st.PREF_KEYBOARD_POS_PORT.equals(key) || key == null) {
			st.kbd_horiz_port = sharedPreferences.getInt(st.PREF_KEYBOARD_POS_PORT, 0);
		}
		if (st.PREF_KEYBOARD_POS_PORT.equals(key) || key == null) {
			st.kbd_horiz_port = sharedPreferences.getInt(st.PREF_KEYBOARD_POS_PORT, 0);
		}
		if (st.PREF_KEYBOARD_POS_LAND.equals(key) || key == null) {
			st.kbd_horiz_land = sharedPreferences.getInt(st.PREF_KEYBOARD_POS_LAND, 0);
		}
		if (st.KBD_BACK_PICTURE.equals(key) || key == null) {
			st.kbd_back_pict = sharedPreferences.getString(st.KBD_BACK_PICTURE, st.STR_NULL);
		}
		if (key == null || st.PREF_KEY_SHOW_CLIPBOARD_SIZE.equals(key)) {
			st.show_size_record_clipboard = sharedPreferences.getBoolean(st.PREF_KEY_SHOW_CLIPBOARD_SIZE, true);
		}
		if (key == null || st.PREF_AC_REPLACE_SEPARATOR_SYMBOL.equals(key)) {
			st.fl_ac_separator_symbol = sharedPreferences.getBoolean(st.PREF_AC_REPLACE_SEPARATOR_SYMBOL, false);
		}
		if (key == null || st.AC_ABBREVIATED_DICT.equals(key)) {
			st.abbreviated_dict = sharedPreferences.getBoolean(st.AC_ABBREVIATED_DICT, false);
		}
		if (key == null || st.PREF_KEY_AC_AUTOCORRECT.equals(key)) {
			m_acAutocorrect = sharedPreferences.getBoolean(st.PREF_KEY_AC_AUTOCORRECT, false);
		}
		if (st.PREF_KEY_EDIT_SETTINGS.equals(key)) {
			m_es.load(st.PREF_KEY_EDIT_SETTINGS);
			if (m_extraText != null) {
				try {
					m_es.setToEditor(m_extraText);
				} catch (Throwable e) {
				}
			}
		}
		if (st.PREF_KEY_FONT_PANEL_AUTOCOMPLETE.equals(key) && m_candView != null)
			m_candView = createNewCandView();
		if (st.PREF_KEY_SENTENCE_ENDS.equals(key) || key == null) {
			m_SentenceEnds = sharedPreferences.getString(st.PREF_KEY_SENTENCE_ENDS, "?!.");
			m_SentenceEnds = st.getDelSpace(m_SentenceEnds, st.STR_SPACE);
		}
		if (st.PREF_AC_HEIGHT.equals(key) || key == null) {
			st.ac_height = Integer.valueOf(sharedPreferences.getString(st.PREF_AC_HEIGHT, st.STR_ZERO));
			m_candView = createNewCandView();
		}
		if (st.PREF_KEY_USE_VOLUME_KEYS.equals(key) || key == null)
			m_volumeKeys = Integer.valueOf(sharedPreferences.getString(st.PREF_KEY_USE_VOLUME_KEYS, st.STR_ZERO));
		if (st.PREF_KEY_AC_PLACE.equals(key) || key == null) {
			m_acPlace = Integer.valueOf(sharedPreferences.getString(st.PREF_KEY_AC_PLACE, st.STR_ONE));
		}
		if (st.PREF_KEY_ADD_SPACE_SYMBOLS.equals(key) || key == null) {
			m_SpaceSymbols = sharedPreferences.getString(st.PREF_KEY_ADD_SPACE_SYMBOLS, ",?!.");
			m_SpaceSymbols = st.getDelSpace(m_SpaceSymbols, st.STR_SPACE);
		}
		st.add_space_before_symbols = sharedPreferences.getBoolean(st.PREF_KEY_ADD_SPACE_BEFORE_SENTENCE, false);
		if (st.PREF_KEY_ADD_SPACE_BEFORE_SYMBOLS.equals(key) || key == null) {
			add_space_before_symbol = sharedPreferences.getString(st.PREF_KEY_ADD_SPACE_BEFORE_SYMBOLS, "—№");
			add_space_before_symbol = st.getDelSpace(add_space_before_symbol, st.STR_SPACE);
		}
		if (st.PREF_KEY_DEL_SPACE_SYMBOL.equals(key) || key == null) {
			del_space_symbol = sharedPreferences.getString(st.PREF_KEY_DEL_SPACE_SYMBOL, ".,!?;:");
			del_space_symbol = st.getDelSpace(del_space_symbol, st.STR_SPACE);
		}
		setKeySoundEffect(
				sharedPreferences.getString(st.PREF_KEY_SOUND_EFFECT, "0=0;1=3;2=4;3=1;4=2;5=6;6=8;7=0;8=7;9=0"));
		m_soundVolume = Integer.valueOf(sharedPreferences.getString(st.PREF_KEY_SOUND_VOLUME, "5"));
		m_soundVolume /= 10f;
		m_LandscapeEditType = Integer.valueOf(sharedPreferences.getString(st.PREF_KEY_LANSCAPE_TYPE, st.STR_ZERO));
		m_PortraitEditType = Integer.valueOf(sharedPreferences.getString(st.PREF_KEY_PORTRAIT_TYPE, st.STR_ZERO));
		state_auto_case = sharedPreferences.getBoolean(st.PREF_KEY_AUTO_CASE, true);
//		if (sharedPreferences.getBoolean(st.PREF_KEY_AUTO_CASE, true) || key == null)
//			m_state |= STATE_AUTO_CASE;
//		else
//			m_state = st.rem(m_state, STATE_AUTO_CASE);
		if (sharedPreferences.getBoolean(st.PREF_KEY_UP_AFTER_SYMBOLS, true))
			m_state |= STATE_UP_AFTER_SYMBOLS;
		else
			m_state = st.rem(m_state, STATE_UP_AFTER_SYMBOLS);
		if (sharedPreferences.getBoolean(st.PREF_AC_SPACE, true))
			m_ac_space = true;
		else
			m_ac_space = false;
		
		// задаём цвета автодополлнения
		st.ac_col_type_layout = sharedPreferences.getInt(st.AC_COL_TYPE_LAYOUT, 0);
		setACPrefColors(sharedPreferences, st.ac_col_type_layout, null);
		
		st.lang_desckbd = sharedPreferences.getString(st.PREF_KEY_DESC_LANG_KBD, st.STR_3TIRE);
		st.lang_help_specinstruction = sharedPreferences.getString(st.PREF_KEY_LANG_HELP_SPECINSTRUCTION, st.STR_3TIRE);


		st.fl_copy_toast = sharedPreferences.getBoolean(st.PREF_COPY_TOAST, false);
		m_arrow_key = sharedPreferences.getBoolean(st.SET_ARROW_KEY, false);
		st.ac_place_arrow_down = sharedPreferences.getBoolean(st.SET_AC_PLACE_ARROW_DOWN, false);
		st.student_dict = sharedPreferences.getBoolean(st.STUDENT_DICT, false);
		st.student_dict_ext = sharedPreferences.getBoolean(st.EXTENDED_STUDENT_DICT, false);
		st.ac_list_value = st.str2int(sharedPreferences.getString(st.AC_LIST_VALUE, "40"), 0, 255, "Arrow down");
		st.fl_alphabetically = sharedPreferences.getBoolean(st.AC_SORT_DROPDOWNLIST_ALPHABETICALLY, false);
		// окно показа нажатых клавиш
		in1 = st.str2int(sharedPreferences.getString(st.POP_COLOR_R, "255"), 0, 255, "Arrow down");
		in2 = st.str2int(sharedPreferences.getString(st.POP_COLOR_G, "255"), 0, 255, "Arrow down");
		in3 = st.str2int(sharedPreferences.getString(st.POP_COLOR_B, "255"), 0, 255, "Arrow down");
		int old_m_popup_color = m_popup_color;
		boolean bbb = false;
		m_popup_color = Color.argb(238, in1, in2, in3);
		if (old_m_popup_color != m_popup_color) {
			bbb = true;
		}
		in1 = st.str2int(sharedPreferences.getString(st.POP_COLOR_TEXT_R, st.STR_ZERO), 0, 255, "Arrow down");
		in2 = st.str2int(sharedPreferences.getString(st.POP_COLOR_TEXT_G, st.STR_ZERO), 0, 255, "Arrow down");
		in3 = st.str2int(sharedPreferences.getString(st.POP_COLOR_TEXT_B, st.STR_ZERO), 0, 255, "Arrow down");
		old_m_popup_color = m_popup_color_text;
		m_popup_color_text = Color.argb(238, in1, in2, in3);
		if (old_m_popup_color != m_popup_color_text) {
			bbb = true;
		}
		if (bbb) {
			reinitKeyboardView();
			processCaseAndCandidates();
		}

		// calc_corr_ind =
		// st.str2int(sharedPreferences.getString(st.PREF_CALC_CORRECTION_IND,
		// st.STR_NULL),0,255,"Calc correction inddicator");
		st.set_kbdact_backcol = sharedPreferences.getInt(st.SET_KBD_BACK_COL, 0);
		st.win_fix = sharedPreferences.getBoolean(st.PREF_KEY_PC2_WIN_FIX, false);
		st.pc2_lr = sharedPreferences.getBoolean(st.PREF_KEY_PC2_LR, false);
		st.btn_size = st.str2hex(sharedPreferences.getString(st.PREF_KEY_PC2_BTN_SIZE, st.PREF_KEY_PC2_BTN_SIZE_DEF),
				10);
		st.btnoff_size = st
				.str2hex(sharedPreferences.getString(st.PREF_KEY_PC2_BTNOFF_SIZE, st.PREF_KEY_PC2_BTNOFF_SIZE_DEF), 10);
		st.win_bg = st.str2hex(sharedPreferences.getString(st.PREF_KEY_PC2_WIN_BG, st.PREF_KEY_PC2_WIN_BG_DEF), 16);
		st.btn_bg = st.str2hex(sharedPreferences.getString(st.PREF_KEY_PC2_BTN_BG, st.PREF_KEY_PC2_BTN_BG_DEF), 16);
		st.btn_tc = st.str2hex(sharedPreferences.getString(st.PREF_KEY_PC2_BTN_TCOL, st.PREF_KEY_PC2_BTN_TCOL_DEF), 16);
		st.btnoff_bg = st.str2hex(sharedPreferences.getString(st.PREF_KEY_PC2_BTNOFF_BG, st.PREF_KEY_PC2_BTNOFF_BG_DEF),
				16);
		st.btnoff_tc = st
				.str2hex(sharedPreferences.getString(st.PREF_KEY_PC2_BTNOFF_TCOL, st.PREF_KEY_PC2_BTNOFF_TCOL_DEF), 16);

		st.gesture_str = sharedPreferences.getString(st.SET_STR_GESTURE_DOPSYMB, st.STR_NULL);
		// читаем массив запуска приложений. Избранное
		if (st.runapp_favorite.size() > 0)
			st.runapp_favorite.clear();
		st.tmpi = sharedPreferences.getInt(st.PREF_KEY_RUNAPP_COUNT, -1);
		if (st.tmpi == -1) {
			for (int i = 0; i <= 300; i++) {
				st.tmps = sharedPreferences.getString(st.PREF_KEY_RUNAPP + i, st.STR_NULL);
				if (st.tmps.compareToIgnoreCase(st.STR_NULL) == 0) {
					break;
				} else {
					st.runapp_favorite.add(st.tmps);
				}
			}
		} else if (st.tmpi == -5) {
		} else if (st.tmpi > 0) {
			for (int i = 0; i < st.tmpi; i++) {
				st.tmps = sharedPreferences.getString(st.PREF_KEY_RUNAPP + i, st.STR_NULL);
				if (st.tmps.length() > 0)
					st.runapp_favorite.add(st.tmps);
			}
		}

		st.mm_btn_size = st.str2int(sharedPreferences.getString(st.MM_BTN_SIZE, "15"), 0, 100, st.STR_NULL);
		st.mm_btn_off_size = st.str2int(sharedPreferences.getString(st.MM_BTN_OFF_SIZE, "8"), 0, 100, st.STR_NULL);
		st.tmps = st.STR_NULL;
		st.tmps = sharedPreferences.getString("macro1", st.STR_NULL);
		if (st.tmps.length() > 0) {
			st.macro1.clear();
			String[] ar = st.tmps.split(st.STR_COMMA);
			for (int i = 0; i < ar.length; i++) {
				st.macro1.add(Integer.valueOf(ar[i]));
			}
		}
		st.tmps = st.STR_NULL;
		st.tmps = sharedPreferences.getString("macro2", st.STR_NULL);
		if (st.tmps.length() > 0) {
			st.macro2.clear();
			String[] ar = st.tmps.split(st.STR_COMMA);
			for (int i = 0; i < ar.length; i++) {
				st.macro2.add(Integer.valueOf(ar[i]));
			}
		}
		if (st.PREF_KEY_MAINMENU_NEW.equals(key) || key == null) {
			String ars = sharedPreferences.getString(st.PREF_KEY_MAINMENU_NEW, st.STR_NULL);
			ars = ars.trim();
			mmenu = new Mainmenu();
			if (ars == null || ars.isEmpty()) {
				for (Mainmenu mm : mmenu.getDefaultItem()) {
					ars += st.STR_NULL + mm.code + st.STR_COMMA;
				}
				ars = ars.substring(0, ars.length() - 1);
			}
			mmenu.createArMenu(ars);
		}

		if (st.PREF_MINI_KBD_ITS.equals(key) || key == null)
			st.fl_mini_kbd_its = sharedPreferences.getBoolean(st.PREF_MINI_KBD_ITS, false);
		// if (st.PREF_KEY_HOT_DIR.equals(key) || key == null){
		m_hotkey_dir = sharedPreferences.getString(st.PREF_KEY_HOT_DIR, st.STR_NULL);
		if (m_hotkey_dir.isEmpty())
			m_hot_count = -1;
		// }
		if (st.PREF_KEY_CLIPBRD_SYNC.equals(key) || key == null)
			st.fl_sync = sharedPreferences.getBoolean(st.PREF_KEY_CLIPBRD_SYNC, false);
		if (st.PREF_KEY_CLIPBRD_SYNC_CREATE_FILE.equals(key) || key == null)
			st.fl_sync_create_new_file = sharedPreferences.getBoolean(st.PREF_KEY_CLIPBRD_SYNC_CREATE_FILE, false);
		if (st.PREF_KEY_CLIPBRD_BTN_SYNC_SHOW.equals(key) || key == null)
			st.fl_clipbrd_btn_sync_show = sharedPreferences.getBoolean(st.PREF_KEY_CLIPBRD_BTN_SYNC_SHOW, true);
		if (st.PREF_KEY_CLIPBRD_SYNC_MSG_SHOW.equals(key) || key == null)
			st.fl_clipbrd_sync_msg = sharedPreferences.getBoolean(st.PREF_KEY_CLIPBRD_SYNC_MSG_SHOW, true);
		if (st.PREF_KEY_CLIPBRD_SYNC_DUR.equals(key) || key == null)
			st.cs_dur = sharedPreferences.getInt(st.PREF_KEY_CLIPBRD_SYNC_DUR, st.cs_dur_def);
		if (st.PREF_KEY_CLIPBRD_SYNC_CNT.equals(key) || key == null)
			st.cs_cnt = sharedPreferences.getInt(st.PREF_KEY_CLIPBRD_SYNC_CNT, st.cs_cnt_def);
		if (st.PREF_KEY_CLIPBRD_SYNC_SIZE.equals(key) || key == null)
			st.cs_size = sharedPreferences.getInt(st.PREF_KEY_CLIPBRD_SYNC_SIZE, st.cs_size_def);
		if (st.fl_sync)
			st.startSyncServise();

		st.desc_fl_not_input = sharedPreferences.getBoolean(st.PREF_VIEW_DESC, false);

		st.fl_show_kbd_notif = sharedPreferences.getBoolean(st.PREF_SHOW_KBD_NOTIF, false);
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			st.fl_show_kbd_notif = false;
		}
		// !!!
		setShowNotification();
		// if (st.fl_show_kbd_notif){
		// if (notif!=null&&notif.mact!= null){
		// notif.dismiss(Notif.NOTIFY_ID);
		// notif = null;
		// }
		// notif = new Notif(inst);
		// notif.createNotif();
		// } else {
		// if (notif.mact!= null){
		// notif.dismiss(Notif.NOTIFY_ID);
		// notif = null;
		// }
		// }
		st.gesture_min_length = st.str2int(sharedPreferences.getString(st.SET_GESTURE_LENGTH, "100"), 1, 1000,
				"Gesture length");
		st.gesture_velocity = st.str2int(sharedPreferences.getString(st.SET_GESTURE_VELOCITY, "150"), 1, 1000,
				"Gesture velocity");
		// читаем свои жесты
		if (st.gc.size() > 0)
			st.gc.clear();
		int tmp = 0;
		GestureHisList gh;
		st.tmpi = sharedPreferences.getInt(st.PREF_KEY_GESTURE_CNT, -1);
		if (st.tmpi == -1) {
			for (int i = 0; i <= 500; i++) {
				gh = new GestureHisList(0, 0, 0, 0);
				tmp = sharedPreferences.getInt(st.PREF_KEY_GESTURE_KEY + i, 0);
				if (tmp == 0) {
					break;
				} else {
					gh.keycode = tmp;
				}
				tmp = sharedPreferences.getInt(st.PREF_KEY_GESTURE_DIR + i, 0);
				if (tmp == 0) {
					break;
				} else {
					gh.direction = tmp;
				}
				tmp = sharedPreferences.getInt(st.PREF_KEY_GESTURE_ID + i, 0);
				if (tmp == 0) {
					break;
				} else {
					gh.id = tmp;
				}
				tmp = sharedPreferences.getInt(st.PREF_KEY_GESTURE_ACT + i, 0);
				if (tmp == 0) {
					break;
				} else {
					gh.action = tmp;
				}
				st.gc.add(gh);
			}
		} else if (st.tmpi == -5) {
		} else if (st.tmpi > 0) {
			for (int i = 0; i < st.tmpi; i++) {
				gh = new GestureHisList(0, 0, 0, 0);
				gh.keycode = sharedPreferences.getInt(st.PREF_KEY_GESTURE_KEY + i, 0);
				gh.direction = sharedPreferences.getInt(st.PREF_KEY_GESTURE_DIR + i, 0);
				gh.id = sharedPreferences.getInt(st.PREF_KEY_GESTURE_ID + i, 0);
				gh.action = sharedPreferences.getInt(st.PREF_KEY_GESTURE_ACT + i, 0);
				if (gh.keycode != 0 && gh.direction != 0 && gh.id != 0 && gh.action != 0)
					st.gc.add(gh);
			}
		}

		st.fl_enter_state = sharedPreferences.getBoolean(st.ENTER_STATE, false);

		m_ac_defkey = sharedPreferences.getString(st.PREF_AC_DEFKEY, st.AC_DEF_WORD);
		if (m_ac_defkey != null && m_ac_defkey.trim().isEmpty())
			m_ac_defkey = st.AC_DEF_WORD;
		// setParSleepValue(Integer.valueOf(sharedPreferences.getString(st.PREF_PAR_DELAY,
		// st.STR_ZERO)));
		st.fl_tpl_path = sharedPreferences.getBoolean(st.PREF_TEMPLATE_PATH, true);
		// setDelaySymbValue(Integer.valueOf(sharedPreferences.getString(st.PREF_DELAY_SYMB,
		// "1")));
		st.fl_counter = sharedPreferences.getBoolean(st.PREF_KEY_USE_COUNTER, false);
		st.fl_keycode = sharedPreferences.getBoolean(st.PREF_KEYCODE, false);
//		if (m_candView != null) {
//			m_candView.setVisible(m_candView.m_counter, st.fl_counter);
//			m_candView.setVisible(m_candView.m_keycode, st.fl_keycode);
//
//		}
		st.fl_enter_key = sharedPreferences.getBoolean(st.PREF_KEY_CLIPBRD_ENTER_AFTER_PASTE, false);
		st.del_space = sharedPreferences.getBoolean(st.PREF_KEY_DEL_SPACE, false);
		boolean bSpac = sharedPreferences.getBoolean(st.PREF_KEY_SENTENCE_SPACE, false);
		if (bSpac)
			m_state |= STATE_SENTENCE_SPACE;
		else
			m_state = st.rem(m_state, STATE_SENTENCE_SPACE);
		boolean bEmptyUp = sharedPreferences.getBoolean(st.PREF_KEY_EMPTY_UPPERCASE, true);
		if (bEmptyUp)
			m_state |= STATE_EMPTY_UP;
		else
			m_state = st.rem(m_state, STATE_EMPTY_UP);
		boolean bSpaceUp = sharedPreferences.getBoolean(st.PREF_KEY_UPERCASE_AFTER_SPACE, false);
		if (bSpaceUp)
			m_state |= STATE_SPACE_SENTENCE_UP;
		else
			m_state = st.rem(m_state, STATE_SPACE_SENTENCE_UP);
		if (st.PREF_KEY_VIEW_AC_PLACE.equals(key) || key == null)
			st.ac1 = sharedPreferences.getInt(st.PREF_KEY_VIEW_AC_PLACE, 1);

		st.mini_kbd_btn_size = Integer.valueOf(sharedPreferences.getString(st.PREF_KEY_MINI_KBD_BTN_SIZE, st.STR_ZERO));
		st.mini_kbd_btn_text_size = Integer
				.valueOf(sharedPreferences.getString(st.PREF_KEY_MINI_KBD_BTN_TEXT_SIZE, st.STR_ZERO));

		setPopupWndSize(Integer.valueOf(sharedPreferences.getString(st.PREF_KEY_PREVIEW_WINSIZE, "2")));
		checkAvtocorrectAndAddSpace();
		fl_read_pref = true;
	}
	/** предварительное задание цветов в автодополнении */
	public void setACPrefColors(SharedPreferences p, int typeAcLayout, KbdDesign kd) 
	{
		/** цвет фона для служебных кнопок */
		int spcol = 0;
		int ecol = 0;
        String path = null;
        /** дизайн спецклавиш */
        KbdDesign kds = null;
		switch (typeAcLayout)
		{
		case 2: // конечный градиент
	        if (kd==null) {
		        path = p.getString(st.PREF_KEY_KBD_SKIN_PATH, st.STR_NULL+st.KBD_DESIGN_STANDARD);
		        kd = st.getSkinByPath(path);
		        if (kd==null) {
		        	setACPrefColors(p,0, kd);
		        	return;
		        }
	        }
	        
	        kds = kd.m_kbdFuncKeys;
	        if (kds==null) {
	        	kds=kd;
	        	spcol = kd.getItemColor(IntEntry.KeyboardBackgroundEndColor);
	        } else
				spcol = kds.getItemColorSpec(IntEntry.KeyBackEndColor);

			st.ac_col_main_back = kd.getItemColor(IntEntry.KeyboardBackgroundEndColor);

			st.ac_col_keycode_back = spcol;
			st.ac_col_keycode_text = kds.getItemColorSpec(IntEntry.KeyTextColor);
			
			st.ac_col_counter_back = spcol;
			st.ac_col_counter_text = kds.getItemColorSpec(IntEntry.KeyTextColor);
			
			st.ac_col_forcibly_back = spcol;
			st.ac_col_forcibly_text = kds.getItemColorSpec(IntEntry.KeyTextColor);
			
			st.ac_col_addvocab_back = spcol;
			st.ac_col_addvocab_text = kds.getItemColorSpec(IntEntry.KeyTextColor);
			
			st.ac_col_arrow_down_back = spcol;
			st.ac_col_arrow_down_text = kds.getItemColorSpec(IntEntry.KeyTextColor);
			
			st.ac_col_calcmenu_back = spcol;
			st.ac_col_calcmenu_text = kds.getItemColorSpec(IntEntry.KeyTextColor);
			
			st.ac_col_calcind_back = spcol;
			st.ac_col_calcind_text = kds.getItemColorSpec(IntEntry.KeyTextColor);

			st.ac_col_word_back = kd.getItemColorSpec(IntEntry.KeyBackEndColor);
			st.ac_col_word_text = kd.getItemColorSpec(IntEntry.KeyTextColor);
			break;
		case 1: // начальный градиент
	        if (kd==null) {
		        path = p.getString(st.PREF_KEY_KBD_SKIN_PATH, st.STR_NULL+st.KBD_DESIGN_STANDARD);
		        kd = st.getSkinByPath(path);
		        if (kd==null) {
		        	setACPrefColors(p,0, kd);
		        	return;
		        }
	        }
	        
	        kds = kd.m_kbdFuncKeys;
	        if (kds==null) {
	        	kds=kd;
	        	spcol = kd.getItemColor(IntEntry.KeyboardBackgroundStartColor);
	        } else
				spcol = kds.getItemColorSpec(IntEntry.KeyBackStartColor);

			st.ac_col_main_back = kd.getItemColor(IntEntry.KeyboardBackgroundStartColor);

			st.ac_col_keycode_back = spcol;
			st.ac_col_keycode_text = kds.getItemColorSpec(IntEntry.KeyTextColor);
			
			st.ac_col_counter_back = spcol;
			st.ac_col_counter_text = kds.getItemColorSpec(IntEntry.KeyTextColor);
			
			st.ac_col_forcibly_back = spcol;
			st.ac_col_forcibly_text = kds.getItemColorSpec(IntEntry.KeyTextColor);
			
			st.ac_col_addvocab_back = spcol;
			st.ac_col_addvocab_text = kds.getItemColorSpec(IntEntry.KeyTextColor);
			
			st.ac_col_arrow_down_back = spcol;
			st.ac_col_arrow_down_text = kds.getItemColorSpec(IntEntry.KeyTextColor);
			
			st.ac_col_calcmenu_back = spcol;
			st.ac_col_calcmenu_text = kds.getItemColorSpec(IntEntry.KeyTextColor);
			
			st.ac_col_calcind_back = spcol;
			st.ac_col_calcind_text = kds.getItemColorSpec(IntEntry.KeyTextColor);

			st.ac_col_word_back = kd.getItemColorSpec(IntEntry.KeyBackStartColor);
			st.ac_col_word_text = kd.getItemColorSpec(IntEntry.KeyTextColor);

			break;
		default: // свои цвета
			st.readACColor(p);
			break;
		}
//		if (m_candView.m_ll != null) {
//			TextView tv = null;
//			for (int i = 0; i < m_candView.m_ll.getChildCount(); i++) {
//				tv = (TextView) m_candView.m_ll.getChildAt(i);
//				if (tv != null) {
//					tv.setBackgroundColor(st.ac_col_word_back);
//					tv.setTextColor(st.ac_col_word_text);
//				}
//			}
//		}
		if (m_candView!=null)
			m_candView.setACColors();
	}
	// если включены оба параметра,
	// то выдача предупреждения что будет работать неверно
	public void checkAvtocorrectAndAddSpace() {
		if (!st.fl_pref_act)
			return;
		if (m_acAutocorrect && st.has(m_state, STATE_SENTENCE_SPACE))
			st.help(R.string.msg_avtocorrect_and_addspace);
	}

	ExtractEditText m_extraText = null;

	@Override
	public View onCreateExtractTextView() {
		View v = super.onCreateExtractTextView();
		if (v instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup) v;
			if (vg.getChildCount() > 0) {
				View ve = vg.getChildAt(0);
				if (ve instanceof ExtractEditText) {
					m_extraText = (ExtractEditText) ve;
					m_es.setToEditor(m_extraText);
				}
			}
		}
		return v;
	}

	@Override
	public boolean onEvaluateFullscreenMode() {
		int set = st.isLandscape(this) ? m_LandscapeEditType : m_PortraitEditType;
		boolean b = super.onEvaluateFullscreenMode();
		if (set == st.PREF_VAL_EDIT_TYPE_FULLSCREEN)
			b = true;
		else if (set == st.PREF_VAL_EDIT_TYPE_NOT_FULLSCREEN)
			b = false;
		return b;
	}

	void compiledKbdToXML() {
		try {
			String path = st.getSettingsPath() + "keyboards/res/";
			new File(path).mkdirs();
			for (Keybrd kbd : st.arKbd) {
				if (kbd.path == null || kbd.path.startsWith(st.STR_SLASH))
					continue;
				File f = new File(path + kbd.path + ".xml");
				f.delete();
				f.createNewFile();
				CustomKeyboard.m_os = new DataOutputStream(new FileOutputStream(f));
				new CustomKeyboard(this, kbd);
				CustomKeyboard.m_os.flush();
				CustomKeyboard.m_os.close();
				CustomKeyboard.m_os = null;
			}
			InputStream is;
			AssetManager myAssetManager = getApplicationContext().getAssets();
			String[] files = { "bbbbb" };
			try {
				files = myAssetManager.list(st.STR_NULL); // массив имен файлов
			} catch (IOException e) {
			}
			for (int i = 0; i < files.length; i++) {
				if (files[i].startsWith("hide_") != true)
					continue;
				Keybrd tmp = new Keybrd(files[i], 0);
				// new Keybrd("en_from_MWcorp", R.string.kbd_name_mwcorp),
				File f = new File(path + tmp.path + ".xml");
				f.delete();
				f.createNewFile();
				CustomKeyboard.m_os = new DataOutputStream(new FileOutputStream(f));
				new CustomKeyboard(this, tmp);
				CustomKeyboard.m_os.flush();
				CustomKeyboard.m_os.close();
				CustomKeyboard.m_os = null;

			}
			st.toast("All keybooard decompile in folder\n" + st.getSettingsPath() + "keyboards/res");
		} catch (Throwable e) {
		}
	}

	/**
	 * Определяет текущий регистр на основе позиции курсора и настроек в
	 * {@link #m_state}
	 * 
	 * @return -1, для нижнего регистра, 1 - для верхнего, 0 - не делать никаких
	 *         действий
	 */
	final int getCase() {
//		if (!m_bCanAutoInput || m_SelStart != m_SelEnd || !st.has(m_state, STATE_AUTO_CASE))
		if (!m_bCanAutoInput || m_SelStart != m_SelEnd || !state_auto_case)
			return 0;
		try {
			if (st.has(st.kv().m_state, JbKbdView.STATE_CAPS_LOCK))
				return 0;
			// !!!если будет глючить - убрать строку
			InputConnection ic = getCurrentInputConnection();
			m_textBeforeCursor = null;
			if (m_textBeforeCursor == null) {
				if (ic != null)
					m_textBeforeCursor = new StringBuffer(ic.getTextBeforeCursor(40, 0));
			}
			if (m_textBeforeCursor == null || m_textBeforeCursor.length() == 0 && st.has(m_state, STATE_EMPTY_UP))
				return 1;
			boolean bUpperCase = false;
			boolean bHasSpace = false;
			boolean bAfterSpace = st.has(m_state, STATE_SPACE_SENTENCE_UP);
			char ch = 0;
			for (int i = m_textBeforeCursor.length() - 1; i >= 0; i--) {
				ch = m_textBeforeCursor.charAt(i);
				// enter
				if (ch == st.STR_LF.charAt(0)&m_SentenceEnds.indexOf((int) ch) > -1)
					bUpperCase = true;
				else if (Character.isWhitespace(ch))
					bHasSpace = true;
				else if (m_SentenceEnds.indexOf((int) ch) > -1) {
					bUpperCase = bHasSpace && bAfterSpace || !bAfterSpace;
					break;
				} else
					break;
			}
			return bUpperCase ? 1 : -1;
		} catch (Throwable e) {
		}
		return 0;

	}

	final void changeCase(boolean bInvalidate) {
		if (st.kv() == null)
			return;
		if (!m_bCanAutoInput)
			return;
		int c = getCase();
		JbKbdView kv = st.kv();
		if (kv == null)
			return;
		boolean bUpperCase = kv.isUpperCase();
		if (bUpperCase && c < 0)
			kv.setTempShift(false, bInvalidate);
		else if (!bUpperCase && c > 0)
			kv.setTempShift(true, bInvalidate);
	}

	void onWords(Vector<WordEntry> ar) {
		if (m_candView != null)
			m_candView.setTexts(ar);
	}

	public final int getSuggestType(EditorInfo ei, boolean bFullscreen) {
		if (st.fl_suggest_dict == false)
			st.fl_suggest_dict = false;
		if (ei == null)
			return SUGGEST_NONE;
		int var = ei.inputType & EditorInfo.TYPE_MASK_VARIATION;
		int type = ei.inputType & EditorInfo.TYPE_MASK_CLASS;
		int flags = ei.inputType & EditorInfo.TYPE_MASK_FLAGS;

		if (type != EditorInfo.TYPE_CLASS_TEXT)
			return SUGGEST_NONE;
		if ((flags & EditorInfo.TYPE_TEXT_FLAG_AUTO_COMPLETE) > 0) {
			m_candView.m_forcibly.setVisibility(View.VISIBLE);
			return bFullscreen ? SUGGEST_OWN : SUGGEST_NONE;
		}

		if (var == EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
				// || var == EditorInfo.TYPE_TEXT_VARIATION_URI
				|| var == EditorInfo.TYPE_TEXT_VARIATION_PASSWORD
				|| var == EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD 
				|| var == EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD)
			return SUGGEST_NONE;

		// if (var == EditorInfo.TYPE_TEXT_VARIATION_URI)
		// return SUGGEST_NONE;
		// if (var == EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
		// return SUGGEST_NONE;
		// if (var == 224) // чёрт его знает что за код - в полях пароля в браузере
		// return SUGGEST_NOT_DICT;
		// if (var == EditorInfo.TYPE_TEXT_VARIATION_PASSWORD)
		// return SUGGEST_NOT_DICT;
		// if (var == EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
		// return SUGGEST_NOT_DICT;
		// String lang = getCurQwertyLang();
		// if(WordsService.inst==null||lang==null||!WordsService.inst.hasVocabForLang(lang))
		// return SUGGEST_NONE;
		if (var == 0 && type == 1 && flags == 180224)
			fl_enter = true;
		return SUGGEST_VOCAB;
	}

	public void saveUserWord(String word) {
		WordsService.command(WordsService.CMD_SAVE_WORD, word, inst);
		getCandidates();
	}

	@Override
	public void onComputeInsets(Insets outInsets) {
		// TODO Auto-generated method stub
		super.onComputeInsets(outInsets);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		try {
			super.onConfigurationChanged(newConfig);
		} catch (Throwable e) {

		}
	}

	public final boolean canGiveVocabWords() {
		return WordsService.inst != null && WordsService.inst.canGiveWords();
	}

	@Override
	public void setInputView(View view) {
		if (view instanceof JbKbdView) {
		} else {
			removeCandView();
		}
		super.setInputView(view);
	}

	@Override
	public void onUpdateCursor(Rect newCursor) {
		m_cursorRect = newCursor;
		// закоментил 3.04.18
		// EditorInfo ei = getCurrentInputEditorInfo();
		// CharSequence seq = st.STR_NULL;
		// InputConnection ic = getCurrentInputConnection();
		// if (ic!=null)
		// seq = ic.getTextBeforeCursor(5, 0);
		if (isFullscreenMode() && m_extraText != null) {
			// int h = m_extraText.getHeight();
			// Path p = new Path();
			// RectF r = new RectF();
			// m_extraText.getLayout().getCursorPath(m_extraText.getSelectionEnd(), p,
			// null);
			// p.computeBounds(r, true);
			//
			// m_extraText.getGlobalVisibleRect(newCursor);
			// m_cursorRect = new Rect((int)r.left,(int)r.top,(int)r.right,(int)r.bottom);
			// m_cursorRect.offset(newCursor.left,newCursor.top);
		}
		super.onUpdateCursor(newCursor);
	}

	@Override
	public void onUpdateExtractedText(int token, ExtractedText text) {
		if (token == mExtractedToken) {
			mExtractedText = text;
			// добавил 13.04.19
			if (m_extraText!=null)
				m_extraText.setExtractedText(text);
		}
		super.onUpdateExtractedText(token, text);
	}

	ExtractedText mExtractedText;
	int mExtractedToken = 1;

	void makeExtractingText(boolean inputChanged) {
		final ExtractEditText eet = m_extraText;
		if (eet != null && getCurrentInputStarted() && isFullscreenMode()) {
			mExtractedToken++;
			ExtractedTextRequest req = new ExtractedTextRequest();
			req.token = mExtractedToken;
			req.flags = InputConnection.GET_TEXT_WITH_STYLES;
			req.hintMaxLines = 10;
			req.hintMaxChars = 10000;
			InputConnection ic = getCurrentInputConnection();
			mExtractedText = ic == null ? null : ic.getExtractedText(req, InputConnection.GET_EXTRACTED_TEXT_MONITOR);
			if (mExtractedText == null || ic == null) {
			}
			final EditorInfo ei = getCurrentInputEditorInfo();

			try {
				eet.startInternalChanges();
				onUpdateExtractingVisibility(ei);
				onUpdateExtractingViews(ei);
				int inputType = ei.inputType;
				if ((inputType & EditorInfo.TYPE_MASK_CLASS) == EditorInfo.TYPE_CLASS_TEXT) {
					inputType |= EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE;
					// if ((inputType&EditorInfo.TYPE_TEXT_FLAG_IME_MULTI_LINE) != 0) {
					// }
				}
				eet.setInputType(inputType);
				eet.setHint(ei.hintText);
				if (mExtractedText != null) {
					eet.setEnabled(true);
					eet.setExtractedText(mExtractedText);
				} else {
					eet.setEnabled(false);
					eet.setText(st.STR_NULL);
				}
			} finally {
				eet.finishInternalChanges();
			}

			if (inputChanged) {
				onExtractingInputChanged(ei);
			}
		}
	}

	final void onKeyboardChanged() {
		// m_candView.saveAc_place();
		checkSuggestType(getCurrentInputEditorInfo());

	}

	// нажатие на поле ввода
	public void onViewClicked(boolean focusChanged) {
		// if (!focusChanged){
		// removeCandView();
		// if(isInputViewShown())
		// st.kv().invalidateAllKeys();
		// }
		if (!focusChanged && st.fl_ac_list_view && m_candView != null) {
			m_candView.popupViewFullList();
		}
	}

	/** вызывается при перекрытии раскладки клавиатуры чем-то другим */
	@Override
	public void onWindowHidden() {
		super.onWindowHidden();
		forceHide();
	}
	@Override
	public void onWindowShown() {
		super.onWindowShown();
	}
	
	public void onKeyboardWindowFocus(boolean bFocus) {
		showCandView(bFocus);
	}

	SameThreadTimer m_volumeKeyTimer;

	void processVolumeKey(int code, boolean down) {
		if (m_volumeKeyTimer != null) {
			m_volumeKeyTimer.cancel();
			m_volumeKeyTimer = null;
		}
		if (down) {
			boolean left = m_volumeKeys == 1 && code == KeyEvent.KEYCODE_VOLUME_UP
					|| m_volumeKeys == 2 && code == KeyEvent.KEYCODE_VOLUME_DOWN;
			final int key = left ? KeyEvent.KEYCODE_DPAD_LEFT : KeyEvent.KEYCODE_DPAD_RIGHT;
			m_volumeKeyTimer = new SameThreadTimer(0, 500) {
				@Override
				public void onTimer(SameThreadTimer timer) {
					processKey(key);
				}
			};
			m_volumeKeyTimer.start();
		}
	}

	void getTextBeforeCursor() {
		CharSequence seq = null;
		try {
			seq = getCurrentInputConnection().getTextBeforeCursor(100, 0);
		} catch (Throwable e) {
			seq = null;
		}
		if (seq == null)
			seq = st.STR_NULL;
		// старая строка
		// m_textBeforeCursor = new StringBuffer(seq==null?st.STR_NULL:seq);
		m_textBeforeCursor = new StringBuffer(seq);
	}

	void getTextAfterCursor() {
		CharSequence seq = null;
		try {
			seq = getCurrentInputConnection().getTextBeforeCursor(100, 0);
		} catch (Throwable e) {
			seq = null;
		}
		if (seq == null)
			seq = st.STR_NULL;
		// старая строка
		// m_textBeforeCursor = new StringBuffer(seq==null?st.STR_NULL:seq);
		m_textAfterCursor = new StringBuffer(seq);
	}

	public void reinitKeyboardView() {
		setInputView(onCreateInputView());
	}

	// public void setParSleepValue(int v)
	// {
	// int par=0;
	// switch (v)
	// {
	// case 0: par=10;break;
	// case 1: par=500;break;
	// case 2: par=1000;break;
	// case 3: par=5000;break;
	// case 4: par=10000;break;
	// default:
	// par=10;
	// break;
	// }
	// m_par_delay=par;
	// }
	// public void setDelaySymbValue(int v)
	// {
	// int par=0;
	// switch (v)
	// {
	// case 0: par=1;break;
	// case 1: par=10;break;
	// case 2: par=25;break;
	// case 3: par=50;break;
	// case 4: par=75;break;
	// case 5: par=100;break;
	// case 6: par=150;break;
	// default:
	// par=11;
	// break;
	// }
	// m_delay_symb=par;
	// }
	public void setPopupWndSize(int v) {
		switch (v) {
		case 0:
			st.popup_win_size = 0.5;
			break;
		case 1:
			st.popup_win_size = 0.75;
			break;
		case 2:
			st.popup_win_size = 1;
			break;
		case 3:
			st.popup_win_size = 1.5;
			break;
		case 4:
			st.popup_win_size = 2;
			break;
		default:
			st.popup_win_size = 1;
			break;
		}
		if (JbKbdView.inst != null) {
			JbKbdView.inst.m_popup = new PopupKeyWindow(this, JbKbdView.inst.m_PreviewHeight,
					JbKbdView.inst.m_PreviewHeight);
			JbKbdView.inst.m_popup.m_bShowUnderKey = JbKbdView.inst.m_previewType == 1;

		}
	}

	// нигде не используется. Закоментил 12.09.18
	// public void setText(int keyCode)
	// {
	// setDelSymb(keyCode);
	// st.fl_delsymb = false;
	//// присваеваем key клавишу hot
	// LatinKey key = st.curKbd().getKeyByCode(st.TXT_HOT);
	// String k=st.STR_NULL;
	// String txt = st.STR_NULL;
	// k+= (char) keyCode;
	// boolean hot= false;
	// for (int i = 0; i < m_hot_count; i++) {
	// if(key!=null&&key.on) {
	// if (m_hot_str[i].contains(k.toUpperCase())){
	// txt=m_hot_tpl[i];
	// hot=true;
	// }
	// }
	// }
	// if (hot){
	// Templates.inst.processTemplate(txt);
	// if (key!=null)
	// key.on=false;
	// st.setQwertyKeyboard();
	//
	// } else
	// getCurrentInputConnection().commitText(String.valueOf((char) keyCode), 1);
	//// setTextAfterSetText(keyCode);
	// processCaseAndCandidates();
	// }
	//// уже не используется
	//// public void setTextAfterSetText(int keycode)
	//// {
	////// st.sleep(m_delay_symb);
	//// getCurrentInputConnection().commitText(String.valueOf((char) keycode), 1);
	//// }
	public void setDelSymb(int keyCode) {
		if (st.del_space && !st.fl_pref_act) {
			s1 = st.STR_NULL + getCurrentInputConnection().getTextBeforeCursor(1, 0);
			if (s1.length() > 0 && s1.compareToIgnoreCase(st.STR_SPACE) == 0) {
				char c = 0;
				boolean b = false;
				for (int i = 0; i < del_space_symbol.length(); i++) {
					c = del_space_symbol.charAt(i);
					if (c == keyCode) {
						b = true;
						break;
					}
				}
				if (b) {
					JbKbdView.processLongKey = false;
					processKey(Keyboard.KEYCODE_DELETE);
//					st.sleep(100);
//					keyDownUp(KeyEvent.KEYCODE_DEL);
					st.fl_delsymb = true;
				}
			}
		}
	}

	/** добавляет пробел перед перечисленными в настройках символами */
	public void setAddSpaceBeforeSymbol(int keyCode) {
		if (st.fl_add_space_before_symb)
			return;
		if (st.add_space_before_symbols && !st.fl_pref_act) {
			if (add_space_before_symbol != null && add_space_before_symbol.length() > 0) {
				char c = (char) keyCode;
				char c1 = 0;
				for (int i = 0; i < add_space_before_symbol.length(); i++) {
					c1 = add_space_before_symbol.charAt(i);
					if (c == c1) {
						getCurrentInputConnection().commitText(st.STR_SPACE, 1);
						st.fl_add_space_before_symb = true;
						break;
					}
				}
			}
		}
	}

	public void setTplCount(int num) {
		m_hot_count = num;
	}

	/** гасит индикатор на кнопке select если она есть на текущей раскладке */
	public void selOff() {
		stickyOff(st.TXT_ED_SELECT);
		selmode = false;

		// LatinKey key = st.curKbd().getKeyByCode(st.TXT_ED_SELECT);
		// if(key!=null&&key.on) {
		// if(key.m_kd!=null){
		// key.m_kd.m_bPressed = false;
		// }
		// key.on = false;
		//
		// if (st.kv() == null)
		// return;
		// int c = getCase();
		// JbKbdView kv = st.kv();
		// if (kv == null)
		// return;
		// // перерисовывает всю клаву, тушит индикатор на кнопке
		// // в зависимости от состояния key.on)
		// kv.invalidateAllKeys();
		// }
	}

	/** гасит индикатор на кнопке с кодом codekey если он включен */
	public void stickyOff(int codekey) {
		LatinKey key = st.curKbd().getKeyByCode(codekey);
		if (key != null && key.on) {
			key.on = false;
			JbKbdView kv = st.kv();
			if (kv == null)
				return;
			kv.invalidateKey(kv.getKeyIndex(key));
			// kv.invalidateAllKeys();
		}
	}

	/** какую раскладку показать. <br>
	 * Устанавливает за счёт значения в st.type_kbd <br>
	 * Значения в этой переменной: <br>
		1 - qwerty <br>
		2 - num <br>
		3 - edit <br>
		4 - symbol1 <br>
		5 - symdol2 <br>
		6 - smile <br>
		7 - calc <br>
	 */
	public static void setTypeKbd() {
		switch (st.type_kbd) {
		case 1:
			st.setQwertyKeyboard();
			st.type_kbd = 1;
			break;
		case 2:
			st.setNumberKeyboard();
			st.type_kbd = 1;
			break;
		case 3:
			st.setTextEditKeyboard();
			// st.type_kbd = 1;
			break;
		case 4:
			st.setSymbolKeyboard(false);
			st.type_kbd = 1;
			break;
		case 5:
			st.setSymbolKeyboard(true);
			st.type_kbd = 1;
			break;
		case 6:
			st.setSmilesKeyboard();
			st.type_kbd = 1;
			break;
		case 7:
			st.setCalcKeyboard();
			break;
		default:
			// st.setQwertyKeyboard();
			// st.type_kbd = 1;
			break;
		}
	}

	/** выдаёт длину выделения или ноль */
	public int getSelectSize() {
		CharSequence cs = getCurrentInputConnection().getSelectedText(0);
		if (cs != null)
			return cs.length();
		return 0;
	}

	// НЕ ИСПОЛЬЗУЕТСЯ.удаляет слово перед курсором
	// Закоментил 04.10.19
//	public boolean symbolDelWord(String label) {
//		if (label == null)
//			return false;
//		boolean bret = false;
//		for (int i = 0; i < label.length(); i++) {
//			char c = label.charAt(i);
//			switch (c) {
//			case '\n':
//				bret = true;
//				break;
//			case '\t':
//				bret = true;
//				break;
//			case '.':
//				bret = true;
//				break;
//			case ',':
//				bret = true;
//				break;
//			case ';':
//				bret = true;
//				break;
//			case ':':
//				bret = true;
//				break;
//			case '!':
//				bret = true;
//				break;
//			case '(':
//				bret = true;
//				break;
//			case ')':
//				bret = true;
//				break;
//			case ' ':
//				bret = true;
//				break;
//			}
//		}
//		return bret;
//	}

	// обработка макросов
	public boolean isMacro(int primaryCode) {
		if (primaryCode == st.REC_MACRO1) {
			if (st.fl_macro1) {
				st.fl_macro1 = false;
				st.tmps = st.intToStr(st.macro1);
				st.pref().edit().putString("macro1", st.tmps).commit();
				st.toast("MACROS1 = OFF");
			} else {
				st.fl_macro1 = true;
				st.toast("MACROS1 = ON");
			}
			return true;
		} else if (st.fl_macro1) {
			st.macro1.add(primaryCode);
			if (primaryCode == st.RUN_MACRO2)
				return true;
		} else if (primaryCode == st.RUN_MACRO1 && st.fl_macro1 == false && st.fl_macro2 == false) {
			for (int i = 0; i < st.macro1.size(); i++) {
				if (st.macro1.get(i) == st.RUN_MACRO2) {
					for (int i2 = 0; i2 < st.macro2.size(); i2++) {
						processKey(st.macro2.get(i2));
					}
				} else {
					processKey(st.macro1.get(i));
				}
			}
			return true;
		}
		if (primaryCode == st.REC_MACRO2) {
			if (st.fl_macro2) {
				st.fl_macro2 = false;
				st.tmps = st.intToStr(st.macro2);
				st.pref().edit().putString("macro2", st.tmps).commit();
				st.toast("MACROS2 = OFF");
			} else {
				st.fl_macro2 = true;
				st.toast("MACROS2 = ON");
			}
			return true;
		} else if (st.fl_macro2) {
			st.macro2.add(primaryCode);
			if (primaryCode == st.RUN_MACRO1)
				return true;
		} else if (primaryCode == st.RUN_MACRO2 && st.fl_macro1 == false && st.fl_macro2 == false) {
			for (int i = 0; i < st.macro2.size(); i++) {
				if (st.macro2.get(i) == st.RUN_MACRO1) {
					for (int i2 = 0; i2 < st.macro1.size(); i2++) {
						processKey(st.macro1.get(i2));
					}
				} else {
					processKey(st.macro2.get(i));
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * возвращает строку названия меню по коду если index =0 - название не выдаётся
	 */
	public String textMenuName(int index) {
		String txt = st.STR_NULL;
		switch (index) {
		case st.CMD_MAIN_MENU:
			txt = getString(R.string.mm);
			break;
		case st.CMD_TPL:
			txt = textMenuNameAddPath(getString(R.string.mm_templates), st.CMD_TPL);
			break;
		case st.CMD_CLIPBOARD:
			txt = getString(R.string.mm_multiclipboard);
			break;
		case st.CMD_CALC:
			txt = getString(R.string.lang_calc);
			break;
		case st.CMD_CALC_HISTORY:
			txt = getString(R.string.lang_calc) + ". " + getString(R.string.calc_menu_history);
			break;
		case st.CMD_CALC_LOAD:
			txt = textMenuNameAddPath(getString(R.string.lang_calc) + ". " + getString(R.string.calc_load),
					st.CMD_CALC_LOAD);
			break;
		case st.CMD_CALC_SAVE:
			txt = textMenuNameAddPath(getString(R.string.lang_calc) + ". " + getString(R.string.calc_save),
					st.CMD_CALC_SAVE);
			break;
		case st.CMD_TRANSLATE_SELECTED:
			txt = getString(R.string.gesture_trans_sel);
			break;
		case st.CMD_TRANSLATE_COPIED:
			txt = getString(R.string.gesture_trans_copy);
			break;
		}
		return txt;
	}

	public String textMenuNameAddPath(String txt, int action) {
		if (st.fl_tpl_path) {
			txt += st.STR_LF + getString(R.string.mm_path) + st.STR_SPACE;
			switch (action) {
			case st.CMD_CALC_LOAD:
			case st.CMD_CALC_SAVE:
			case st.CMD_TPL:
				if (Templates.inst != null) {
					String pat = Templates.inst.m_curDir.getPath()
							.substring(Templates.inst.m_rootDir.getPath().length());
					if (pat.isEmpty())
						pat = st.STR_SLASH;
					txt += pat;
				}
				break;
			}

		}
		return txt;
	}

	// выдаёт звук (для простого звука подать на вход значение 10 (звук ентера))
	public void beep(int primaryCode) {
		if (st.fl_sound && st.has(st.kv().m_state, JbKbdView.STATE_SOUNDS)) {
			int sound = st.kse[0];
			switch (primaryCode) {
			case KeyEvent.KEYCODE_DPAD_LEFT:
				sound = st.kse[1];
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				sound = st.kse[2];
				break;
			case KeyEvent.KEYCODE_DPAD_UP:
				sound = st.kse[3];
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				sound = st.kse[4];
				break;
			case KeyEvent.KEYCODE_SPACE:
				sound = st.kse[5];
				break;
			case 10:
				sound = st.kse[6];
				break;
			case Keyboard.KEYCODE_DELETE:
				sound = st.kse[7];
				break;
			case st.TXT_ED_DEL:
				sound = st.kse[8];
				break;
			case Keyboard.KEYCODE_SHIFT:
				sound = st.kse[9];
				break;
			}
			try {
				m_audio.playSoundEffect(sound, m_soundVolume);
			} catch (Throwable e) {
			}
		}
	}

	public void setKeySoundEffect(String in) {
		String[] ar = in.split(";");
		if (ar == null && ar.length < 1)
			return;
		int ind = 0;
		String str = st.STR_NULL;
		int arpos = -1;
		int eff = 0;
		try {
			for (int i = 0; i < ar.length; i++) {
				ind = ar[i].indexOf("=");
				if (ind == 0)
					continue;
				str = ar[i].substring(0, ind);
				arpos = -1;
				try {
					arpos = Integer.parseInt(str);
				} catch (Throwable e) {
					continue;
				}
				if (arpos < 0 && arpos >= st.kse.length)
					continue;
				try {
					eff = Integer.parseInt(ar[i].substring(ind + 1));
				} catch (Throwable e) {
					eff = getKeySoundEffectDefault(arpos);
				}
				st.kse[arpos] = eff;
			}
		} catch (Throwable e) {
		}
	}

	public int getKeySoundEffectDefault(int pos) {
		switch (pos) {
		case 1:
			return AudioManager.FX_FOCUS_NAVIGATION_LEFT;
		case 2:
			return AudioManager.FX_FOCUS_NAVIGATION_RIGHT;
		case 3:
			return AudioManager.FX_FOCUS_NAVIGATION_UP;
		case 4:
			return AudioManager.FX_FOCUS_NAVIGATION_DOWN;
		case 5:
			return AudioManager.FX_KEYPRESS_SPACEBAR;
		case 6:
			return AudioManager.FX_KEYPRESS_RETURN;
		case 8:
			return AudioManager.FX_KEYPRESS_DELETE;
		default:
			return AudioManager.FX_KEY_CLICK;
		}
	}

	// старый beep (до установки звуков)
	// public void beep(int primaryCode)
	// {
	// if (st.fl_sound&&st.has(st.kv().m_state, JbKbdView.STATE_SOUNDS))
	// {
	// int sound = AudioManager.FX_KEY_CLICK;
	// switch(primaryCode)
	// {
	// case KeyEvent.KEYCODE_DPAD_LEFT:sound =
	// AudioManager.FX_FOCUS_NAVIGATION_LEFT; break;
	// case KeyEvent.KEYCODE_DPAD_RIGHT:sound =
	// AudioManager.FX_FOCUS_NAVIGATION_RIGHT; break;
	// case KeyEvent.KEYCODE_DPAD_UP:sound = AudioManager.FX_FOCUS_NAVIGATION_UP;
	// break;
	// case KeyEvent.KEYCODE_DPAD_DOWN:sound =
	// AudioManager.FX_FOCUS_NAVIGATION_DOWN; break;
	// case KeyEvent.KEYCODE_SPACE:sound = AudioManager.FX_KEYPRESS_SPACEBAR; break;
	// case st.TXT_ED_DEL:sound = AudioManager.FX_KEYPRESS_DELETE; break;
	// case 10:sound = AudioManager.FX_KEYPRESS_RETURN; break;
	// }
	// try{
	// m_audio.playSoundEffect(sound, m_soundVolume);
	// }
	// catch(Throwable e)
	// {}
	// }
	// }
	public void setCountTextValue() {
		if (st.fl_counter == false)
			return;
		if (length_fl == false) {
			length_fl = true;
			return;
		}
		if (this.isInputViewShown()) {
			InputConnection ic = getCurrentInputConnection();
			if (ic != null) {
				try {
					length_str = null;
					length_str = ic.getTextBeforeCursor(51000, 0).toString();

					if (length_str == null)
						length_str = st.STR_NULL;
					length_str1 = null;
					length_str1 = ic.getTextAfterCursor(51000, 0).toString();
					if (length_str1 == null)
						length_str1 = st.STR_NULL;
					length_str += length_str1;
					length_int = 0;
					if (!length_str.isEmpty() && length_str.length() > 0)
						length_int = length_str.length();
					else
						length_str = " 0 ";
					if (length_int >= 50000)
						length_str = " MAX ";
					else
						length_str = "  " + String.valueOf(length_int) + "  ";
					m_candView.setVisible(m_candView.m_counter, true);
					m_candView.setCounter(length_str);
					if (st.type_keyboard.compareToIgnoreCase("calculator") == 0) {
						m_candView.setVisible(m_candView.m_counter, false);
						m_candView.setVisible(m_candView.m_forcibly, false);
					}
				} catch (Throwable e) {
				}
			}
		}
	}

	// возвращает кейкод символа для ctrl и alt
	public int getKeycode(int label) {
		if (label > 32) {
			String sss = String.valueOf((char) label);
			sss = sss.toUpperCase();
			label = Integer.valueOf(sss.charAt(0));
		}
		switch (label) {
		case '0':
			label = KeyEvent.KEYCODE_0;
			break;
		case '1':
			label = KeyEvent.KEYCODE_1;
			break;
		case '2':
			label = KeyEvent.KEYCODE_2;
			break;
		case '3':
			label = KeyEvent.KEYCODE_3;
			break;
		case '4':
			label = KeyEvent.KEYCODE_4;
			break;
		case '5':
			label = KeyEvent.KEYCODE_5;
			break;
		case '6':
			label = KeyEvent.KEYCODE_6;
			break;
		case '7':
			label = KeyEvent.KEYCODE_7;
			break;
		case '8':
			label = KeyEvent.KEYCODE_8;
			break;
		case '9':
			label = KeyEvent.KEYCODE_9;
			break;
		case 'A':
			label = KeyEvent.KEYCODE_A;
			break;
		case 'B':
			label = KeyEvent.KEYCODE_B;
			break;
		case 'C':
			label = KeyEvent.KEYCODE_C;
			break;
		case 'D':
			label = KeyEvent.KEYCODE_D;
			break;
		case 'E':
			label = KeyEvent.KEYCODE_E;
			break;
		case 10:
			label = KeyEvent.KEYCODE_ENTER;
			break;
		case 'F':
			label = KeyEvent.KEYCODE_F;
			break;
		case 'G':
			label = KeyEvent.KEYCODE_G;
			break;
		case 'H':
			label = KeyEvent.KEYCODE_H;
			break;
		case 'I':
			label = KeyEvent.KEYCODE_I;
			break;
		case 'J':
			label = KeyEvent.KEYCODE_J;
			break;
		case 'K':
			label = KeyEvent.KEYCODE_K;
			break;
		case 'L':
			label = KeyEvent.KEYCODE_L;
			break;
		case 'M':
			label = KeyEvent.KEYCODE_M;
			break;
		case 'N':
			label = KeyEvent.KEYCODE_N;
			break;
		case 'O':
			label = KeyEvent.KEYCODE_O;
			break;
		case 'P':
			label = KeyEvent.KEYCODE_P;
			break;
		case 'Q':
			label = KeyEvent.KEYCODE_Q;
			break;
		case 'R':
			label = KeyEvent.KEYCODE_R;
			break;
		case 'S':
			label = KeyEvent.KEYCODE_S;
			break;
		case 'T':
			label = KeyEvent.KEYCODE_T;
			break;
		case 32:
			label = KeyEvent.KEYCODE_SPACE;
			break;
		case 9:
			label = KeyEvent.KEYCODE_TAB;
			break;
		case 'U':
			label = KeyEvent.KEYCODE_U;
			break;
		case 'V':
			label = KeyEvent.KEYCODE_V;
			break;
		case 'W':
			label = KeyEvent.KEYCODE_W;
			break;
		case 'X':
			label = KeyEvent.KEYCODE_X;
			break;
		case 'Y':
			label = KeyEvent.KEYCODE_Y;
			break;
		case 'Z':
			label = KeyEvent.KEYCODE_Z;
			break;
		case 19:
			label = KeyEvent.KEYCODE_DPAD_UP;
			break;
		case 20:
			label = KeyEvent.KEYCODE_DPAD_DOWN;
			break;
		case 21:
			label = KeyEvent.KEYCODE_DPAD_LEFT;
			break;
		case 22:
			label = KeyEvent.KEYCODE_DPAD_RIGHT;
			break;
		case st.TXT_ED_HOME_STR:
			label = KeyEvent.KEYCODE_MOVE_HOME;
			break;
		case st.TXT_ED_END_STR:
			label = KeyEvent.KEYCODE_MOVE_END;
			break;
		case st.TXT_ED_PG_UP:
			label = KeyEvent.KEYCODE_PAGE_UP;
			break;
		case st.TXT_ED_PG_DOWN:
			label = KeyEvent.KEYCODE_PAGE_DOWN;
			break;
		case st.TXT_ED_DEL:
			label = KeyEvent.KEYCODE_FORWARD_DEL;
			break;
		}
		if (label <= st.KEYCODE_CODE && label >= st.KEYCODE_CODE - 2000)
			label = (st.KEYCODE_CODE - label);

		return label;
	}

	public void viewAcPlace() {
       CustomKeyboard kbd = null;
       try {
    	   kbd = (CustomKeyboard)st.kv().getCurKeyboard();
       } catch (Exception e) {
       }
       if (st.ac1 == 1) {
    	   ServiceJbKbd.inst.removeCandView();
    	   st.ac1 = 2;
    	   ServiceJbKbd.inst.m_acPlace = CandView.AC_PLACE_NONE;
    	   ServiceJbKbd.setTypeKbd();
       } else {
    	   st.ac1 = 1;
    	   ServiceJbKbd.inst.m_acPlace = 1;
    	   if (st.temp_ac_hide >-1) {
				//st.ac1 = st.temp_ac_hide;
				ServiceJbKbd.inst.m_acPlace = st.temp_ac_hide;
        		st.temp_ac_hide = -1;
			}
			ServiceJbKbd.setTypeKbd();
			createNewCandView();
       }
       st.pref().edit().putInt(st.PREF_KEY_VIEW_AC_PLACE, st.ac1);
	}

	// для контекстного ентера
	public void setImeOptions() {
		if (kbd_show_ei != null)
			st.curKbd().setImeOptions(inst.getResources(), kbd_show_ei.imeOptions);
	}

	@Override
	public AbstractInputMethodImpl onCreateInputMethodInterface() {
		return new MyInputMethodImpl();
	}

	public IBinder mToken;

	public class MyInputMethodImpl extends InputMethodImpl {
		@Override
		public void attachToken(IBinder token) {
			super.attachToken(token);
			if (mToken == null) {
				mToken = token;
			}
		}
	}

	/** устанавливаем уведомление для вызова клавы из шторки */
	public void setShowNotification() {
		if (st.fl_show_kbd_notif) {
			if (notif != null && notif.mact != null) {
				notif.dismiss(Notif.NOTIFY_ID);
				notif = null;
			}
			notif = new Notif(inst);
			notif.createNotif();
		} else {
			if (notif.mact != null) {
				notif.dismiss(Notif.NOTIFY_ID);
				notif = null;
			}
		}
	}

	/** устанавливаем отображение иконки в лаунчере */
	public void setShowIconLauncher(boolean visible) {
		// пока нормально не работает
		// если скрытие иконки делать в JbKbdPreference, то иконка скрывается,
		// но и активность не запускается (через системные "язык и ввод"
		// настройки клавы тоже не запускаются

		// PackageManager pkg = getPackageManager();
		//// visible = false;
		// if (!visible){
		// pkg.setComponentEnabledSetting(new ComponentName(this,JbKbdPreference.class),
		// PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
		// PackageManager.DONT_KILL_APP);
		// } else {
		// PackageManager p = getPackageManager();
		// ComponentName componentName = new ComponentName(this,JbKbdPreference.class);
		// p.setComponentEnabledSetting(componentName,
		// PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
		// PackageManager.DONT_KILL_APP);
		//// pkg.setComponentEnabledSetting(new
		// ComponentName(inst,JbKbdPreference.class),
		//// PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
		//// PackageManager.DONT_KILL_APP);
		//
		// }
	}

}