package com.jbak2.JbakKeyboard;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import com.jbak2.CustomGraphics.BitmapCachedGradBack;
import com.jbak2.CustomGraphics.GradBack;
import com.jbak2.CustomGraphics.draw;
import com.jbak2.Dialog.Dlg;
import com.jbak2.JbakKeyboard.IKeyboard.KbdDesign;
import com.jbak2.JbakKeyboard.IKeyboard.Keybrd;
import com.jbak2.JbakKeyboard.JbKbd.LatinKey;
import com.jbak2.ctrl.IntEditor;
import com.jbak2.ctrl.IntEditor.OnChangeValue;
import com.jbak2.perm.Perm;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

// зависоны при переключении скинов начались с версии 2.31.12 - чёт нахимичил
/**
 * Класс для настроек различных значений клавиатуры, требующих просмотра
 * qwerty-слоя
 */
public class SetKbdActivity extends Activity {
	static boolean show_kbd_land= false;
	/** Текущий экземпляр класса */
	static SetKbdActivity inst;
	int m_curAction;
	String m_LangName;
	int m_curKbd = -1;
	View m_MainView = null;
	JbKbdView m_kbd;
	int m_curSkin;
	Button btn_back = null;
	Button decompil_skin = null;
	boolean m_calibrateAuto = true;
	ImageButton ibnext = null;
	ImageButton ibprev = null;
	TextView tv_name = null;
	/**
	 * Текущий тип экрана, для которого выбирается клава. 0- оба типа, 1 - портрет,
	 * 2 - ландшафт
	 */
	int m_screenType = -1;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		inst = this;
        if (!Perm.checkPermission(inst)) {
   			finish();
   			st.runAct(Quick_setting_act.class,inst);
        }
        
		m_curAction = getIntent().getIntExtra(st.SET_INTENT_ACTION, st.SET_KEY_HEIGHT_PORTRAIT);
		if (m_curAction == st.SET_KEY_CALIBRATE_PORTRAIT || m_curAction == st.SET_KEY_CALIBRATE_LANDSCAPE) {
			initCalibrate();
			return;
		}
		try {
			m_MainView = getLayoutInflater().inflate(R.layout.kbd_set, null);
		} catch (Throwable e) {
			st.toast(inst, R.string.kbdact_error_open);
			finish();
			return;
		}
		if (m_MainView == null)
			finish();
		tv_name = (TextView) m_MainView.findViewById(R.id.keyboard_name);
		setBackColor();
		// m_MainView.setBackgroundDrawable(st.getBack());
		SharedPreferences pref = st.pref();
		m_kbd = (JbKbdView) m_MainView.findViewById(R.id.keyboard);
		m_MainView.findViewById(R.id.back_col).setOnClickListener(m_clkListener);
		decompil_skin = (Button) m_MainView.findViewById(R.id.decompile_skin);
		if (decompil_skin != null) {
			decompil_skin.setOnClickListener(m_clkListener);
			decompil_skin.setVisibility(View.GONE);
		}
		ibnext = (ImageButton) m_MainView.findViewById(R.id.next);
		ibprev = (ImageButton) m_MainView.findViewById(R.id.prew);
		ibnext.setOnClickListener(m_clkNextPrevListener);
		ibprev.setOnClickListener(m_clkNextPrevListener);
		ibnext.setOnLongClickListener(m_clkLongListener);
		ibprev.setOnLongClickListener(m_clkLongListener);
		show_kbd_land= false;
		if (m_curAction == st.SET_KEY_HEIGHT_LANDSCAPE) {
			st.qs_ar[4] = 1;
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			setTitle(R.string.set_key_height_landscape);
			show_kbd_land= true;
			st.setQwertyKeyboard(true);
		} else if (m_curAction == st.SET_KEY_HEIGHT_PORTRAIT) {
			st.qs_ar[4] = 1;
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			setTitle(R.string.set_key_height_portrait);
			st.setQwertyKeyboard(true);
		} else if (m_curAction == st.SET_SELECT_SKIN) {
			setTitle(R.string.set_key_skins);
			CustomKbdDesign.updateArraySkins();
			st.qs_ar[1] = 1;
			String path = st.pref().getString(st.PREF_KEY_KBD_SKIN_PATH, st.STR_NULL + st.KBD_DESIGN_STANDARD);
			int pos = 0;
			for (KbdDesign kd : st.arDesign) {
				if (st.getSkinPath(kd).equals(path)) {
					m_curKbd = pos;
					break;
				}
				++pos;
			}
			m_curSkin = m_curKbd;
			m_MainView.findViewById(R.id.set_height).setVisibility(View.GONE);
			m_MainView.findViewById(R.id.select_kbd).setVisibility(View.VISIBLE);
			m_MainView.findViewById(R.id.screen_type).setVisibility(View.GONE);
			if (m_curSkin < 0) {
				m_curSkin = st.KBD_DESIGN_STANDARD;
				st.toast(R.string.skin_not_found);
			}
			if (m_kbd.m_curDesign.path != null && m_kbd.m_curDesign.path.startsWith(CustomKbdDesign.ASSETS)
					&& decompil_skin != null)
				decompil_skin.setVisibility(View.VISIBLE);

			String name = st.arDesign[m_curSkin].getName(this);
			tv_name.setText(name);
			m_MainView.findViewById(R.id.save).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						m_curSkin = m_curKbd;
						// Toast.makeText(inst, R.string.settings_saved, 700).show();
						st.toast(getString(R.string.settings_saved));
						onBackPressed();
					} catch (Throwable e) {
					}
				}
			});
			m_kbd.reload();
			st.toast(R.string.kbdact_toast1);
		} else if (m_curAction == st.SET_SELECT_KEYBOARD) {
			CustomKeyboard.updateArrayKeyboards(false);
			m_MainView.findViewById(R.id.set_height).setVisibility(View.GONE);
			m_MainView.findViewById(R.id.select_kbd).setVisibility(View.VISIBLE);
			m_MainView.findViewById(R.id.save).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					st.qs_ar[0] = 1;
					setCurKeyboard();
					onBackPressed();
					Toast.makeText(inst, R.string.settings_saved, 700).show();
					finish();
				}
			});
			m_MainView.findViewById(R.id.screen_type).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showScreenTypes();
				}
			});

			setTitle(R.string.set_select_layout);
			m_LangName = getIntent().getStringExtra(st.SET_INTENT_LANG_NAME);
			m_curKbd = -1;
			m_screenType = getIntent().getIntExtra(st.SET_SCREEN_TYPE, -1);
			if (m_screenType > -1)
				setScreenType(m_screenType);
			changeKbd(1);
			st.toast(R.string.kbdact_toast1);
		}
		int flags = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		getWindow().addFlags(flags);
		m_kbd.setOnKeyboardActionListener(m_kbdListener);
		if (m_curAction == st.SET_KEY_HEIGHT_PORTRAIT || m_curAction == st.SET_KEY_HEIGHT_LANDSCAPE) {
			// высота клавиш
			((TextView) m_MainView.findViewById(R.id.key_height_txt)).setVisibility(View.VISIBLE);
			final IntEditor sb = (IntEditor) m_MainView.findViewById(R.id.key_height);
			sb.setSteps(new int[] { 2, 4, 8 });
			sb.setMinAndMax(20, 200);
			int val = KeyboardPaints.getValue(this, pref,
					m_curAction == st.SET_KEY_HEIGHT_PORTRAIT ? KeyboardPaints.VAL_KEY_HEIGHT_PORTRAIT
							: KeyboardPaints.VAL_KEY_HEIGHT_LANDSCAPE);
			sb.setOnChangeValue(new IntEditor.OnChangeValue() {
				@Override
				public void onChangeIntValue(IntEditor edit) {
					changeKeyHeight(edit.getValue());
				}
			});
			sb.setValue(val);
			((Button) m_MainView.findViewById(R.id.default_size)).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					float def = KeyboardPaints.getDefValue(
							m_curAction == st.SET_KEY_HEIGHT_PORTRAIT ? KeyboardPaints.VAL_KEY_HEIGHT_PORTRAIT
									: KeyboardPaints.VAL_KEY_HEIGHT_LANDSCAPE);
					sb.setValue(KeyboardPaints.getPercToPixel(inst, true, def, true));
				}
			});
			// горизонтальное положение клавиатуры
			((TextView) m_MainView.findViewById(R.id.kbd_pos_txt)).setVisibility(View.VISIBLE);
			((LinearLayout) m_MainView.findViewById(R.id.set_kbd_pos)).setVisibility(View.VISIBLE);

			final IntEditor kp = (IntEditor) m_MainView.findViewById(R.id.kbd_pos);
			kp.setSteps(new int[] { 1, 5, 10 });
			kp.setMinAndMax(-400, 400);
			int val1 = m_curAction == st.SET_KEY_HEIGHT_PORTRAIT ? st.kbd_horiz_port
							: st.kbd_horiz_land;
			kp.setOnChangeValue(new IntEditor.OnChangeValue() {
				@Override
				public void onChangeIntValue(IntEditor edit) {
					boolean bPort = m_curAction == st.SET_KEY_HEIGHT_PORTRAIT;
					String pname = bPort ? st.PREF_KEYBOARD_POS_PORT : st.PREF_KEYBOARD_POS_LAND;
					st.pref().edit().putInt(pname, edit.getValue()).commit();
					m_kbd.setX(edit.getValue());
					m_kbd.reload();
				}
			});
			kp.setValue(val1);
			((Button) m_MainView.findViewById(R.id.default_kbd_pos)).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
//					float def = KeyboardPaints.getDefValue(
//							m_curAction == st.SET_KEY_HEIGHT_PORTRAIT ? KeyboardPaints.VAL_KEY_HEIGHT_PORTRAIT
//									: KeyboardPaints.VAL_KEY_HEIGHT_LANDSCAPE);
					kp.setValue(0);
				}
			});
		}
		setContentView(m_MainView);
		setContentView(m_MainView);
	}

	void showScreenTypes() {
		st.UniObserver obs = new st.UniObserver() {
			@Override
			public int OnObserver(Object param1, Object param2) {
				setScreenType(((Integer) param1).intValue());
				return 0;
			}
		};
		ArrayAdapter<String> adapt = new ArrayAdapter<String>(inst, android.R.layout.select_dialog_item,
				getResources().getStringArray(R.array.screen_type_vars));
		Dlg.customMenu(inst, adapt, null, obs);
	}

	void setScreenType(int sel) {
		m_screenType = sel;
		((TextView) m_MainView.findViewById(R.id.screen_type))
				.setText(getResources().getStringArray(R.array.screen_type_vars)[sel]);
//		if (m_screenType > -1)
//			st.pref().edit().putInt(st.SET_SCREEN_TYPE, m_screenType).commit();
	}

	void changeKeyHeight(int height) {
		boolean bPort = m_curAction == st.SET_KEY_HEIGHT_PORTRAIT;
		String pname = bPort ? st.PREF_KEY_HEIGHT_PORTRAIT_PERC : st.PREF_KEY_HEIGHT_LANDSCAPE_PERC;
		st.pref().edit().putFloat(pname, KeyboardPaints.getPixelToPerc(this, bPort, height)).commit();
		m_kbd.m_KeyHeight = height;
		m_kbd.reload();

	}

	void setCurKeyboard() {
		if (m_curAction == st.SET_SELECT_KEYBOARD) {
			String path = st.getKeybrdArrayByLang(m_LangName).elementAt(m_curKbd).path;
			if (m_screenType == 0 || m_screenType == 1)
				st.pref().edit().putString(st.PREF_KEY_LANG_KBD_PORTRAIT + m_LangName, path).commit();
			if (m_screenType == 0 || m_screenType == 2)
				st.pref().edit().putString(st.PREF_KEY_LANG_KBD_LANDSCAPE + m_LangName, path).commit();

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		if (m_curAction == st.SET_SELECT_SKIN) {
			if (m_curSkin < 0) {
				m_curSkin = st.KBD_DESIGN_STANDARD;
			}
			st.pref().edit().putString(st.PREF_KEY_KBD_SKIN_PATH, st.getSkinPath(st.arDesign[m_curSkin])).commit();
		}
		st.pref().edit().putInt(st.SET_KBD_BACK_COL, st.set_kbdact_backcol).commit();
		m_kbd.setOnKeyboardActionListener(null);
		if (ServiceJbKbd.inst != null)
			ServiceJbKbd.inst.reinitKeyboardView();
		BitmapCachedGradBack.clearAllCache();
		show_kbd_land= false;
		super.onBackPressed();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
			if (Quick_setting_act.inst!=null) {
				Quick_setting_act.saveQuickSetting();
			} else {
				st.exitApp();
			}
		if (!st.fl_pref_act)
			st.showkbd();
	}

	/** */
	void changeSkin(boolean bNext) {
		if (m_curKbd < 0) {
			m_curKbd = 0;
		} else if (bNext) {

			m_curKbd++;
			if (m_curKbd >= st.arDesign.length)
				m_curKbd = 0;
		} else {
			--m_curKbd;
			if (m_curKbd < 0) {
				m_curKbd = st.arDesign.length - 1;
			}
		}
		setCurSkin();

	}

	void setCurSkin() {
		KbdDesign kd = st.arDesign[m_curKbd];
		tv_name.setText(kd.getName(inst));
		st.pref().edit().putString(st.PREF_KEY_KBD_SKIN_PATH, st.getSkinPath(kd)).commit();
		decompil_skin.setVisibility(View.GONE);
		if (kd.path != null && kd.path.startsWith(CustomKbdDesign.ASSETS))
			decompil_skin.setVisibility(View.VISIBLE);
		m_kbd.reload();
		m_kbd.reloadSkin();
	}

	/**
	 * Устанавливает в просмотр следующую клавиатуру в массиве
	 * {@link IKeyboard#arKbd}
	 * bNext - 0-пред. клава, 1 - след., 2- из m_curKbd
	 */
	void changeKbd(int bNext) {
		Vector<Keybrd> ar = st.getKeybrdArrayByLang(m_LangName);
		if (m_curKbd == -1) {
			boolean bLandscape = st.isLandscape(this);
			SharedPreferences pref = st.pref();
			String pv = pref.getString(st.PREF_KEY_LANG_KBD_PORTRAIT + m_LangName, st.STR_NULL);
			String lv = pref.getString(st.PREF_KEY_LANG_KBD_LANDSCAPE + m_LangName, st.STR_NULL);
			String t = bLandscape ? lv : pv;
			int pos = 0;
			for (Keybrd k : ar) {
				if (t.length() == 0 || t.equals(k.path)) {
					m_curKbd = pos;
					break;
				}
				pos++;
			}
			if (m_curKbd < 0) {
				m_curKbd = 0;
			}
			if (m_screenType < 0) {
				int sel = 0;
				if (!pv.equals(lv))
					sel = bLandscape ? 2 : 1;
				setScreenType(sel);
			}
		} else {
			if (bNext == 0){
				if (m_curKbd == 0)
					m_curKbd = ar.size() - 1;
				else
					--m_curKbd;
			}
			else if (bNext == 1) {
				++m_curKbd;
				if (m_curKbd >= ar.size())
					m_curKbd = 0;
			} 
			else if (bNext == 2) {
				
			}
		}
		setKeyboard(ar.elementAt(m_curKbd));

	}

	/** Устанавливает клавиатуру kbd текущей в просмотре */
	void setKeyboard(Keybrd kbd) {
		m_kbd.setKeyboard(st.loadKeyboard(kbd));
		tv_name.setText(kbd.getName(this));
	}

	OnKeyboardActionListener m_kbdListener = new OnKeyboardActionListener() {
		@Override
		public void swipeUp() {
		}

		@Override
		public void swipeRight() {
		}

		@Override
		public void swipeLeft() {
		}

		@Override
		public void swipeDown() {
		}

		@Override
		public void onText(CharSequence text) {
		}

		@Override
		public void onRelease(int primaryCode) {
		}

		@Override
		public void onPress(int primaryCode) {
		}

		@Override
		public void onKey(int primaryCode, int[] keyCodes) {
			if (st.kv() == null)
				return;
			if (primaryCode == Keyboard.KEYCODE_SHIFT) {
				st.kv().handleShift();
			}
			if (primaryCode == st.CMD_LANG_CHANGE && m_curAction != st.SET_LANGUAGES_SELECTION) {
				st.kv().handleLangChange(true, 0);
			}
		}
	};
	View.OnClickListener m_clkNextPrevListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			boolean bNext = v.getId() == R.id.next;
//			CustomKeyboard.updateArrayKeyboards(false);
//			CustomKbdDesign.updateArraySkins();
			switch (m_curAction) {
			case st.SET_SELECT_KEYBOARD:
				CustomKeyboard.updateArrayKeyboards(false);
				if (bNext)
					changeKbd(1);
				else
					changeKbd(0);
				break;
			case st.SET_SELECT_SKIN:
				CustomKbdDesign.updateArraySkins();
				changeSkin(bNext);
				break;
			}
		}
	};

	void setCalibrate(boolean auto) {
		m_calibrateAuto = auto;
		if (auto) {
			m_calibrToggle.setText(R.string.calibr_toggle_manual);
			m_MainView.findViewById(R.id.calibr_input).setVisibility(View.VISIBLE);
			m_MainView.findViewById(R.id.calibr_test).setVisibility(View.VISIBLE);
			m_MainView.findViewById(R.id.calibr_edit_test).setVisibility(View.GONE);
			m_MainView.findViewById(R.id.calibr_size).setVisibility(View.GONE);
		} else {
			m_calibrToggle.setText(R.string.calibr_toggle_auto);
			m_MainView.findViewById(R.id.calibr_input).setVisibility(View.GONE);
			m_MainView.findViewById(R.id.calibr_test).setVisibility(View.GONE);
			m_MainView.findViewById(R.id.calibr_edit_test).setVisibility(View.VISIBLE);
			m_MainView.findViewById(R.id.calibr_size).setVisibility(View.VISIBLE);
		}
	}

	EditText m_calibrateEdit;
	TextView m_calibrateTest;
	Button m_calibrToggle;
	IntEditor m_calibrManual;

	void setCalibrateListeners() {
		m_calibrManual.setMinAndMax(-100, 100);
		String calibrSet = m_curAction == st.SET_KEY_CALIBRATE_PORTRAIT ? st.PREF_KEY_CORR_PORTRAIT
				: st.PREF_KEY_CORR_LANDSCAPE;
		int val = st.pref(inst).getInt(calibrSet, JbKbdView.defaultVertCorr);
		m_calibrManual.setValue(val);
		m_MainView.findViewById(R.id.save).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!m_calibrateAuto)
					calibrateSave(true, m_calibrManual.getValue());
				else
					calibrateSave(true, m_autoY);
			}
		});
		m_calibrManual.setOnChangeValue(new OnChangeValue() {

			@Override
			public void onChangeIntValue(IntEditor edit) {
				m_kbd.setVerticalCorrection(edit.getValue());
			}
		});
		OnKeyboardActionListener calibrateListener = new OnKeyboardActionListener() {

			@Override
			public void swipeUp() {
			}

			@Override
			public void swipeRight() {
			}

			@Override
			public void swipeLeft() {
			}

			@Override
			public void swipeDown() {
			}

			@Override
			public void onText(CharSequence text) {
			}

			@Override
			public void onRelease(int primaryCode) {
			}

			@Override
			public void onPress(int primaryCode) {
			}

			@Override
			public void onKey(int primaryCode, int[] keyCodes) {
				LatinKey lk = m_kbd.getKeyByCode(primaryCode);
				onCalibrationKey(lk, primaryCode);

			}
		};
		OnTouchListener touchListener = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					m_lastY = (int) event.getY();
				return false;
			}
		};
		m_kbd.setOnKeyboardActionListener(calibrateListener);
		m_kbd.setOnTouchListener(touchListener);
	}

	int m_lastY = -1;
	int m_autoY = 0;

	void onCalibrationKey(LatinKey k, int primaryCode) {
		String t = k.getMainText();
		if ((t == null||t.length()>1) && k.codes.length > 0) {
			// добавил 09.05.19
			// алгоритм вероятно пашет не совсем верно на раскладках т9
			// посмотрим на реакцию юзеров
			int code = st.getSearchValueInIntArray(k.codes, primaryCode);
			if (code == -1)
				t = st.STR_NULL + (char) k.codes[0];
			else
				t = st.STR_NULL + (char) k.codes[code];
		}
		if (!m_calibrateAuto) {
			if (t != null)
				m_calibrateEdit.setText(m_calibrateEdit.getText().toString() + t);
		} else {

			if (m_calibrPos >= m_calibrTest.length() || TextUtils.isEmpty(t) || t.length() > 1)
				return;
			char ch = Character.toLowerCase(m_calibrTest.charAt(m_calibrPos));
			if (Character.toLowerCase(t.charAt(0)) != ch)
				return;
			int yc = k.y + k.height / 2 + m_kbd.getPaddingTop();
			m_autoY = (yc - m_lastY + m_autoY) / 2;
			++m_calibrPos;
			m_calibrTest.removeSpan(m_autoSpan);
			if (ch == ' ')
				m_calibrTest.setSpan(m_backSpan, m_calibrPos - 1, m_calibrPos, 0);
			else
				m_calibrTest.removeSpan(m_backSpan);
			m_calibrTest.removeSpan(m_autoSpan);

			m_calibrTest.setSpan(m_autoSpan, 0, m_calibrPos, 0);
			m_calibrateTest.setText(m_calibrTest);
			if (m_calibrPos == m_calibrTest.length()) {
				calibrateSave(true, m_autoY);
			}
		}
	}

	public void onConfigurationChanged(Configuration newConfig) {
		// добавил 03.07.17. Если будут жалобы - удалить здесь и onConfigurationChanged
		// у активности в манифесте
		if (android.os.Build.VERSION.SDK_INT > 17
				|| android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.HONEYCOMB)
			super.onConfigurationChanged(newConfig);
	};

	SpannableString m_calibrTest;
	int m_calibrPos = 0;
	ForegroundColorSpan m_autoSpan;
	BackgroundColorSpan m_backSpan;
	SpannableString m_testSpan;

	void initCalibrate() {
		setRequestedOrientation(m_curAction == st.SET_KEY_CALIBRATE_PORTRAIT ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
				: ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setTitle(R.string.calibr_label);
		m_MainView = getLayoutInflater().inflate(R.layout.kbd_calibrate, null);
		m_calibrateEdit = (EditText) m_MainView.findViewById(R.id.calibr_edit_test);
		m_calibrateTest = (TextView) m_MainView.findViewById(R.id.calibr_test);
		m_calibrTest = new SpannableString(m_calibrateTest.getText().toString());
		m_calibrToggle = (Button) m_MainView.findViewById(R.id.toggle_calibr);
		m_calibrManual = (IntEditor) m_MainView.findViewById(R.id.calibr_size);
		m_MainView.setBackgroundDrawable(draw.getBack());
		m_kbd = (JbKbdView) m_MainView.findViewById(R.id.keyboard);

		setCalibrateListeners();
		st.setTempEnglishQwerty();
		m_autoSpan = new ForegroundColorSpan(0xff00ff00);
		m_backSpan = new BackgroundColorSpan(0xff00ff00);
		m_calibrToggle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setCalibrate(!m_calibrateAuto);
			}
		});
		setCalibrate(true);
		setContentView(m_MainView);
	}

	void calibrateSave(boolean confirm, final int value) {
		if (!confirm) {
			if (m_curAction == st.SET_KEY_CALIBRATE_PORTRAIT)
				st.pref().edit().putInt(st.PREF_KEY_CORR_PORTRAIT, value).commit();
			else
				st.pref().edit().putInt(st.PREF_KEY_CORR_LANDSCAPE, value).commit();
			onBackPressed();
			return;
		}
		String alert = String.format(getString(R.string.calibr_save), value, JbKbdView.defaultVertCorr);
		Dlg.yesNoDialog(inst, alert, new st.UniObserver() {

			@Override
			public int OnObserver(Object param1, Object param2) {
				if (((Integer) param1).intValue() == DialogInterface.BUTTON_POSITIVE) {
					calibrateSave(false, value);
				} else {
					if (m_calibrateAuto)
						resetCalibrate();
					else
						onBackPressed();
				}
				return 0;
			}
		});
	}

	void resetCalibrate() {
		if (m_calibrateAuto) {
			m_calibrTest.removeSpan(m_autoSpan);
			m_calibrTest.removeSpan(m_backSpan);
			m_calibrPos = 0;
			m_calibrateTest.setText(m_calibrTest);
		}
	}

	void setBackColor() {
		if (m_MainView == null)
			return;
		tv_name.setTextColor(Color.WHITE);
		// при изменении не забыть в листенере
		// кнопки указания фона менять максимальное значение
		// этого свича
		switch (st.set_kbdact_backcol) {
		case 0:
			m_MainView.setBackgroundDrawable(draw.getBack());
			break;
		case 1:
			m_MainView.setBackgroundColor(Color.BLACK);
			break;
		case 2:
			m_MainView.setBackgroundColor(Color.MAGENTA);
			break;
		case 3:
			m_MainView.setBackgroundColor(Color.RED);
			break;
		case 4:
			m_MainView.setBackgroundColor(Color.LTGRAY);
			tv_name.setTextColor(Color.BLACK);
			break;
		case 5:
			m_MainView.setBackgroundColor(Color.DKGRAY);
			break;
		case 6:
			m_MainView.setBackgroundDrawable(new GradBack(0xffFF8C00, 0xffaa0000).setCorners(0, 0).setGap(0)
					.setDrawPressedBackground(false).getStateDrawable());
			tv_name.setTextColor(Color.BLACK);
			break;
		case 7:
			m_MainView.setBackgroundDrawable(new GradBack(0xff00ffee, 0xffaa0000).setCorners(0, 0).setGap(0)
					.setDrawPressedBackground(false).getStateDrawable());
			tv_name.setTextColor(Color.BLACK);
			break;
		case 8:
			m_MainView.setBackgroundDrawable(new GradBack(0xff8B008B, 0xffaa0000).setCorners(0, 0).setGap(0)
					.setDrawPressedBackground(false).getStateDrawable());
			break;
		default:
			m_MainView.setBackgroundDrawable(draw.getBack());
			break;

		}

	}

	View.OnClickListener m_clkListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back_col:
				st.set_kbdact_backcol++;
				if (st.set_kbdact_backcol > 8)
					st.set_kbdact_backcol = 0;
				setBackColor();
				break;
			case R.id.decompile_skin:
				saveSkinOnDisk();
				break;
			}
		}
	};
	View.OnLongClickListener m_clkLongListener = new View.OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			switch (v.getId()) {
			case R.id.next:
			case R.id.prew:
				viewListMenu();
				return true;
			}
			return false;
		}
	};

	void viewListMenu() {
		String tit = null;
		String[] arn = null;
		switch (m_curAction) {
		case st.SET_SELECT_KEYBOARD:
			tit = inst.getString(R.string.set_select_layout);
			CustomKeyboard.updateArrayKeyboards(false);
			Vector<Keybrd> ar = st.getKeybrdArrayByLang(m_LangName);
			arn = new String[ar.size()];
			for (int i = 0; i < ar.size(); i++) {
				arn[i] = ar.get(i).getName(inst);
			}
			break;
		case st.SET_SELECT_SKIN:
			tit = inst.getString(R.string.set_key_skins);
			CustomKbdDesign.updateArraySkins();
			arn = new String[st.arDesign.length];
			for (int i = 0; i < st.arDesign.length; i++) {
				arn[i] = st.arDesign[i].getName(inst);
			}
			break;
		}
		if (arn==null)
			return;
		int rlist = R.layout.tpl_instr_list;
		final ArrayAdapter<String> ad = new ArrayAdapter<String>(this, rlist, arn);
		Dlg.customMenu(this, ad, tit, new st.UniObserver() {
			@Override
			public int OnObserver(Object param1, Object param2) {
				int pos = ((Integer) param1).intValue();
				if (pos >= 0) {
					switch (m_curAction) {
					case st.SET_SELECT_KEYBOARD:
						m_curKbd = pos;
						changeKbd(2);
						break;
					case st.SET_SELECT_SKIN:
						m_curKbd = pos;
						setCurSkin();
						break;
					}
				}
				return 0;
			}
		});
	}

	void saveSkinOnDisk() {
		KbdDesign kd = st.arDesign[m_curKbd];
		if (kd != null && !kd.path.startsWith(CustomKbdDesign.ASSETS))
			return;
		byte[] buffer = null;
		InputStream is;
		String path = kd.path.substring(CustomKbdDesign.ASSETS.length());
		try {
			is = getAssets().open(CustomKbdDesign.FOLDER_ASSETS_SKIN + path);
			int size = is.available();
			buffer = new byte[size];
			is.read(buffer);
			is.close();
			String name = st.getSettingsPath() + CustomKbdDesign.FOLDER_SKINS + st.STR_SLASH + path;
			FileOutputStream fos = new FileOutputStream(name);
			fos.write(buffer);
			fos.flush();
			fos.close();

			st.toastLong("Saved to:\n" + name);
		} catch (IOException e) {
			st.logEx(e);
		}
	}

	public static class IntEntry {

		int index = -1;
		int value = -1;
		int defvalue = -1;
		int resId_et = -1;

		public IntEntry() {
		}

		public IntEntry(int ind, int val) {
			index = ind;
			value = val;
			resId_et = -1;
			defvalue = -1;
		}

		public IntEntry(int ind, int val, int res_id_et) {
			index = ind;
			value = val;
			defvalue = -1;
			resId_et = res_id_et;
		}

		public IntEntry(int ind, int val, int defval, int res_id_et) {
			index = ind;
			value = val;
			defvalue = defval;
			resId_et = res_id_et;
		}
	}

}