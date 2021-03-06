package com.jbak2.JbakKeyboard;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Vector;

import com.jbak2.Dialog.Dlg;
import com.jbak2.JbakKeyboard.JbKbd.LatinKey;
import com.jbak2.ctrl.Font;
import com.jbak2.ctrl.Font.FontArSymbol;
import com.jbak2.ctrl.th;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/** активность для вывода текстов из файлов в assets */
public class ShowTextAct extends Activity {
	/** для открытия файла на редактирование, через интент */
	public static String START_FILENAME_DESCRIPTOR = "URI_TEXT_FILE://" + st.STR_LF;

	public int flags = 0;
	/** имя файла для вывода */
	public String fn = st.STR_NULL;
	public int title = 0;
	/** текст был изменён и нужно выдавать запрос на сохранение */
	public boolean changed = false;

	public boolean multilang = false;
	/** Цвета редактора - если true - белый фон, чёрные буквы. Иначе наоборот */
	public boolean et_color = true;
	// ключи для интента
	/** ключ, перечисление флагов во входящем интенте */
	public static final String FLAGS = "flags";
	/**
	 * ключ, имя текстового файла для вывода <br>
	 * БЕЗ ПРИСТАВКИ "_ru" для мультиязычных файлов!
	 */
	public static final String FILENAME = "fn";
	/** ключ, заголовок */
	public static final String TITLE = "title";

	// флаги
	/**
	 * флаг, что выводимый файл имеет несколько переводов<br>
	 * в формате: _ru(en, etc)_<название файла с расширением>
	 */
	public static final int FLAG_MULTI_LANG = 0x0000001;
	public static final int FLAG_HIDE_BTN_SEARCH = 0x0000002;
	public static final int FLAG_HIDE_BTN_LANG = 0x0000004;
	/**
	 * флаг, что текст брать из переменной st.help <br>
	 * Если она null, то берём текст из клавиши с кодом -514
	 */
	public static final int FLAG_TEXT_IN_HELP_VARIABLE = 0x0000008;
	/**
	 * флаг, что текст нужно редактировать и его брать брать из переменной st.help
	 * <br>
	 * По окончанию редактирования (onBackPressed), выдавать запрос на сохранение
	 */
	public static final int FLAG_EDIT_TEXT = 0x0000010;
	/**
	 * флаг - откуда была вызвана активность для редактирования текста, <br>
	 * чтобы обновить текст в вызывающей активности <br>
	 * ЭТО ПОЛЕ m_edText из TplEditorActivity
	 */
	public static final int FLAG_CALL_IN_TPL_NAME_FIELD = 0x00000100;
	/** флаг, что редактируется внешний файл */
	public static final int FLAG_EXTERNAL_FILE_EDIT = 0x000001000;

	private Thread.UncaughtExceptionHandler androidDefaultUEH;
	static ShowTextAct inst;
	LinearLayout llcont = null;
	FrameLayout.LayoutParams llcont_lp = null;
	/** гравитация панели инструментов */
	int llcont_gr = Gravity.LEFT;
	boolean big_size = false;
	boolean searchviewpanel = false;
	Button def_btn = null;
	EditText et = null;
	EditText et_search = null;
	EditText et_replace = null;
	TextView tv_search = null;
	RelativeLayout searchpanel;
	RelativeLayout searchpanel_replace;
	ProgressBar load_progress = null;
	ArrayList<Integer> arpos_search = new ArrayList<Integer>();
	int pos_search = -1;
	/**
	 * Дата последнего редактиролвания, для "Как пользоваться клавиатурой"<br>
	 * ДЛЯ ВЫРЕЗАНИЯ ЭТОЙ СТРОКИ, чтобы пользователь не видел!
	 */
	public static String LAST_EDITED_DESC_KBD = "Last edited: ";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(th.theme_interface);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_text_act);
		inst = this;
		androidDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread thread, Throwable e) {
				st.toast("error - long reading.");
				androidDefaultUEH.uncaughtException(thread, e);
			}
		});
		fn = st.STR_NULL;
		multilang = false;
		title = 0;
		flags = 0;
		Intent in = getIntent();
		if (in != null) {
			flags = in.getIntExtra(FLAGS, 0);
			title = in.getIntExtra(TITLE, 0);
			fn = in.getStringExtra(FILENAME);
			if (st.has(flags, FLAG_MULTI_LANG))
				multilang = true;
		}

		tv_search = null;
		et = null;
		et_search = null;
		searchpanel = null;
		searchpanel_replace = null;
		llcont = (LinearLayout) findViewById(R.id.desc_llcontrol);
		llcont_gr = st.pref(inst).getInt(st.STA_TOOLBAR_LEFTRIGHT_TOOLBAR, Gravity.LEFT);
		setToolbarGravity(false);

		searchpanel = (RelativeLayout) findViewById(R.id.desc_rlsearch_panel);
		searchpanel_replace = (RelativeLayout) findViewById(R.id.desc_rlsearch_replace_panel);

		load_progress = (ProgressBar) findViewById(R.id.desc_load_progress);
		load_progress.setVisibility(View.GONE);
		et = (EditText) findViewById(R.id.desc_et1);
		setViewText();
		et.addTextChangedListener(tw);
		// автолинк из xml атрибутов не работает, хоть текст и выделяет
		Linkify.addLinks(et, Linkify.ALL);
		et.setFocusableInTouchMode(true);

		// настраиваем текст на кнопках
		setDefaultTextButton();
		def_btn = (Button) llcont.findViewById(R.id.desc_btn_search);
		def_btn.setVisibility(View.VISIBLE);
		def_btn = (Button) llcont.findViewById(R.id.desc_btn_sellang);
		def_btn.setVisibility(View.VISIBLE);
		def_btn = (Button) llcont.findViewById(R.id.desc_btn_save);
		def_btn.setVisibility(View.GONE);

		// настраиваем активность
		if (st.has(flags, FLAG_EXTERNAL_FILE_EDIT)) {
			def_btn = (Button) llcont.findViewById(R.id.desc_btn_save);
			def_btn.setVisibility(View.VISIBLE);
			setTitle(fn);
		} else if (title != 0)
			setTitle(getString(title));
		else
			setTitle(st.STR_NULL);

		if (st.has(flags, FLAG_HIDE_BTN_SEARCH)) {
			def_btn = (Button) llcont.findViewById(R.id.desc_btn_search);
			def_btn.setVisibility(View.GONE);
		}
		if (st.has(flags, FLAG_HIDE_BTN_LANG)) {
			def_btn = (Button) llcont.findViewById(R.id.desc_btn_sellang);
			def_btn.setVisibility(View.GONE);
		}
		et_color = st.pref(inst).getBoolean(st.STA_COLOR_EDITTEXT, true);
		big_size = st.pref(inst).getBoolean(st.STA_BIG_TEXT, false);
		setColorsEditor();
		searchPanelHide();
		setButtonHeightAndSizeText();
		st.hidekbd();
		Ads.show(this, 3);
	}

	TextWatcher tw = new TextWatcher() {
		@Override
		public void afterTextChanged(Editable s) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			changed = true;
			if (searchviewpanel) {
				ettxt = et.getText().toString().toLowerCase();
			}
		}
	};

	public void setToolbarGravity(boolean bchange) {
		if (llcont == null)
			return;
		llcont_lp = (FrameLayout.LayoutParams) llcont.getLayoutParams();
		if (llcont_lp == null)
			return;
		if (bchange) {
			if (llcont_gr == Gravity.LEFT)
				llcont_gr = Gravity.RIGHT;
			else
				llcont_gr = Gravity.LEFT;
			st.pref(inst).edit().putInt(st.STA_TOOLBAR_LEFTRIGHT_TOOLBAR, llcont_gr).commit();
		}
		llcont_lp.gravity = llcont_gr;
		llcont.setLayoutParams(llcont_lp);
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.desc_btn_save:
			if (st.has(flags, FLAG_EXTERNAL_FILE_EDIT)) {
				String str = et.getText().toString();
				if (st.savefile(fn, str)) {
					st.toast(R.string.saved);
					changed = false;
				} else
					st.toast(R.string.not_save);
			}
			return;
		case R.id.desc_btn_show_search_replace:
			if (searchpanel_replace.getVisibility() == View.GONE) {
				searchpanel_replace.setVisibility(View.VISIBLE);
				et_replace.requestFocus();
				et_replace.selectAll();

			} else {
				searchpanel_replace.setVisibility(View.GONE);
			}
			return;
		case R.id.desc_btn_replace:
			if (setTextOnEditor())
				search(1);
			return;
		case R.id.desc_replace_all:
			int count = 0;
			boolean ret = search(0);
			et.requestFocus();
			while (ret) {
				if (setTextOnEditor())
					count++;
				ret = search(1);
			}
			st.toast(inst.getString(R.string.relace_all_toast)+": "+count);
			return;
		case R.id.desc_btn_left_right:
			setToolbarGravity(true);
			return;
		case R.id.desc_btn_set_editor_color:
			et_color = !et_color;
			st.pref(inst).edit().putBoolean(st.STA_COLOR_EDITTEXT, et_color).commit();
			setColorsEditor();
			return;
		case R.id.desc_btn_sellang:
			final String[] lng = getArrayLangByPreFilename();
			if (lng == null || lng.length == 0)
				return;
			Arrays.sort(lng);
			String[] ars = new String[lng.length];
			for (int i = 0; i < lng.length; i++) {
				ars[i] = new Locale(lng[i]).getDisplayName();
				ars[i] = st.getUpFirstSymbol(ars[i]);
			}
			int lvl = R.layout.tpl_instr_list_dark;
			if (!th.isDarkThemeApp())
				lvl = R.layout.tpl_instr_list_light;
			ArrayAdapter<String> ar = new ArrayAdapter<String>(this, lvl, ars);
			Dlg.customMenu(inst, ar, inst.getString(R.string.euv_lang_text), new st.UniObserver() {
				@Override
				public int OnObserver(Object param1, Object param2) {
					int pos = ((Integer) param1).intValue();
//                	if ((pos == 0&&st.lang_desckbd.contains("en"))
//                		||(pos == 1&&st.lang_desckbd.contains("ru"))	
//                		)
//                		return 0;
					if (fn.contains(st.STA_FILENAME_DESC_KBD)) {
						st.lang_desckbd = lng[pos];
						st.pref(inst).edit().putString(st.PREF_KEY_DESC_LANG_KBD, st.lang_desckbd).commit();
					} else if (fn.contains(st.STA_FILENAME_HELP_SPECINSTRUCTION)) {
						st.lang_help_specinstruction = lng[pos];
						st.pref(inst).edit()
								.putString(st.PREF_KEY_LANG_HELP_SPECINSTRUCTION, st.lang_help_specinstruction)
								.commit();
					}

					setViewText();
					st.hidekbd();
					return 0;
				}
			});

			return;
		case R.id.desc_btn_size:
			big_size = !big_size;
			st.pref(inst).edit().putBoolean(st.STA_BIG_TEXT, big_size).commit();
			setButtonHeightAndSizeText();
			return;
		case R.id.desc_btn_search:
			if (searchpanel == null)
				return;
			if (searchpanel.getVisibility() == View.GONE)
				searchPanelShow();
			else
				searchPanelHide();
			return;
		case R.id.desc_search_close:
			if (searchpanel != null)
				searchpanel.setVisibility(View.GONE);
			if (et != null)
				et.setEnabled(true);
			searchPanelHide();
			return;
		case R.id.desc_search_down:
			search(1);
			// searchSpanViewPos(1);
			return;
		case R.id.desc_search_up:
			search(-1);
			// searchSpanViewPos(-1);
			return;
//        case R.id.desc_CheckBox1:
//            CheckBox cb = (CheckBox) view.findViewById(R.id.desc_CheckBox1);
//            st.desc_fl_not_input = cb.isChecked();
//        	String path = st.getSettingsPath();
//       		path =path.substring(0,path.length()-1);
//       		File file2 = new File(path);
//       		if (file2!=null&&file2.isDirectory()) {
//       			if (st.desc_fl_not_input)
//       				JbKbdPreference.saveIniParam("desc_begin", st.STR_ONE);
//       		}
//            return;
		case R.id.desc_btn_start:
			et.setSelection(0);
			return;
		case R.id.desc_btn_end:
			et.setSelection(et.getText().toString().length());
			return;
		case R.id.desc_btn_pgdn:
			et.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_MULTIPLE, KeyEvent.KEYCODE_PAGE_DOWN));
			return;
		case R.id.desc_btn_pgup:
			et.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_MULTIPLE, KeyEvent.KEYCODE_PAGE_UP));
			return;
		}
	}

	public void searchPanelShow() {
		if (searchviewpanel)
			return;
		if (searchpanel == null)
			return;
		Button btn = (Button) searchpanel.findViewById(R.id.desc_btn_show_search_replace);
		Font.setTextOnTypeface(btn, Font.FontArSymbol.SEARCH_REPLACE);
		searchpanel.setVisibility(View.VISIBLE);
		tv_search = (TextView) searchpanel.findViewById(R.id.desc_search_result);
		// для spannable закоментить
		tv_search.setVisibility(View.GONE);
		et_search = (EditText) searchpanel.findViewById(R.id.desc_search_edit);
		et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView tv, int action, KeyEvent tvent) {
				if (action == EditorInfo.IME_ACTION_SEARCH) {
					if (isReplacePanelVisible()) {
						setTextOnEditor();
					}
					search(0);
					// для span
					// searchSpan();
					// st.hidekbd();
					return true;
				}
				return false;
			}
		});
		et_search.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				try {
					if (hasFocus) {
						et_search.setBackgroundResource(R.drawable.edittext_back_focus_style);
					} else
						et_search.setBackgroundResource(R.drawable.edittext_back_notfocus_style);
				} catch (Throwable e) {
				}

			}
		});

		et_replace = (EditText) searchpanel_replace.findViewById(R.id.desc_replace_edit);
		et_replace.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView tv, int action, KeyEvent tvent) {
				if (action == EditorInfo.IME_ACTION_SEARCH) {
					setTextOnEditor();
					search(1);
					return true;
				}
				return false;
			}
		});
		et_replace.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				try {
					if (hasFocus) {
						et_replace.setBackgroundResource(R.drawable.edittext_back_focus_style);
					} else
						et_replace.setBackgroundResource(R.drawable.edittext_back_notfocus_style);
				} catch (Throwable e) {
				}

			}
		});
		btn = null;

		ettxt = null;
		st.showkbd(et_search, true);
		if (et_search.getText().toString().length() > 0) {
			search(0);
			// searchSpan();
		}
		searchviewpanel = true;
	}
	public boolean isReplacePanelVisible()
	{
		return searchpanel_replace.getVisibility() == View.VISIBLE;
	}
	public boolean setTextOnEditor() {
		if (et.getSelectionStart() == et.getSelectionEnd()) {
			return false;
		}
		et.requestFocus();
		String txt = et_replace.getText().toString();
		if (txt == null||txt.length()==0)
			return false;
		st.insertTextOnCursorPosition(et, txt);
		//ServiceJbKbd.inst.onText(txt);
		return true;
	}

	public void searchPanelHide() {
		int pos = et.getSelectionStart();
		et.setText(et.getText().toString());
		et.setSelection(pos);
		et.setEnabled(true);
		if (searchpanel == null)
			return;
		searchpanel.setVisibility(View.GONE);
		searchpanel_replace.setVisibility(View.GONE);
		et_search = null;
		ettxt = null;
		et_replace = null;
		searchviewpanel = false;
	}

	String search_str = null;
	String ettxt = null;
	int cursor_pos = 0;
	int search_index = -1;

	/**
	 * поиск
	 * 
	 * @param pos - направление поиска: <br>
	 *            -1 - вверх от курсора <br>
	 *            -1 - с начала текста<br>
	 *            1 - вниз от курсора <br>
	 */
	public boolean search(int pos) {
		if (et.isSelected())
			et.setSelection(et.getSelectionStart());
		search_str = et_search.getText().toString().toLowerCase().trim();
		if (search_str.length() == 0)
			return false;
		if (ettxt == null)
			ettxt = et.getText().toString().toLowerCase();
		// текущая позиция курсора
		if (pos >= 0)
			cursor_pos = et.getSelectionEnd();
		else
			cursor_pos = et.getSelectionStart();
		if (pos == 0)
			cursor_pos = 0;

		// сам поиск
		search_index = -1;
		// вперёд от курсора
		if (pos > -1) {
			search_index = ettxt.indexOf(search_str, cursor_pos);
		}
		// назад от курсора
		else {
			cursor_pos = cursor_pos - 1;
			if (cursor_pos < 0)
				cursor_pos = 0;
			search_index = ettxt.lastIndexOf(search_str, cursor_pos);
			if (search_index < 0) {
				cursor_pos = ettxt.length() - 1;
				if (cursor_pos < 0)
					cursor_pos = 0;
				search_index = ettxt.lastIndexOf(search_str, cursor_pos);

			}
		}
		if (pos >= 0 & search_index < 0) {
//			if (!search(0))
//				return false;
//    		if (!repeatSearch)
//    			if (!search(0,true))
//    				return false;
		}
//    	else if (!repeatSearch)
//			if (!search(-1,true))
//				return false;
		if (search_index > -1) {
			et.requestFocus();
			et.setSelection(search_index);
			et.setSelection(search_index, search_index + search_str.length());
			return true;
		}
		return false;
	}

	public void searchSpan() {
		if (et.isSelected())
			st.toast("selected");
		String search_str = et_search.getText().toString().toLowerCase().trim();
		String ettxt = et.getText().toString().toLowerCase();
		String subtxt = ettxt;
		arpos_search.clear();
		;
		if (search_str.length() == 0) {
			et.setText(ettxt);
			searchSpanViewPos(0);
			return;
		}
		int pos = -1;
		int pos1 = 0;
		boolean fl = true;
		while (fl) {
			pos = subtxt.indexOf(search_str);
			if (pos != -1) {
				pos = pos + pos1;
				arpos_search.add(pos);
				if (pos <= ettxt.length()) {
					int bbb = pos + search_str.length();
					subtxt = ettxt.substring(pos + search_str.length());
				} else {
					break;
				}
				if (pos1 == 0)
					pos1 = pos;
				else
					pos1 = pos + search_str.length();
				continue;
			}
			fl = false;
		}
//!!!
		if (arpos_search.size() > 1) {
			arpos_search.remove(1);
		}
		if (arpos_search.size() > 0) {
			Spannable text = new SpannableString(et.getText().toString());
			for (int i = 0; i < arpos_search.size(); i++) {
				text.setSpan(new BackgroundColorSpan(0x88ff8c00), arpos_search.get(i),
						arpos_search.get(i) + search_str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			et.setText(text);
			et.requestFocus();
			et.setSelection(arpos_search.get(0).intValue());
			pos_search = 0;

		} else {
			pos = et.getSelectionStart();
			et.setText(et.getText().toString());
			et.setSelection(pos);

			et.requestFocus();
			pos_search = -1;
		}
		if (searchviewpanel) {
			// et.setEnabled(false);
			setColorsEditor();
//    		if (th.isDarkThemeApp()) {
//        		et.setBackgroundColor(Color.WHITE);
//        		et.setTextColor(Color.BLACK);
//    		} else {
//        		et.setBackgroundColor(Color.BLACK);
//        		et.setTextColor(Color.WHITE);
//    		}
		} else
			et.setEnabled(true);

		et.setCursorVisible(true);
		searchSpanViewPos(0);
	}

	public void searchSpanViewPos(int pos) {
		if (tv_search == null)
			return;
		if (arpos_search.size() == 0) {
			tv_search.setText("[0/0]");
			return;
		}
		if (pos == 0) {
			pos_search = 0;
		} else if (pos == 1) {
			pos_search++;
			if (pos_search >= arpos_search.size())
				pos_search = 0;
//			pos_search = arpos.size()-1;
		} else if (pos == -1) {
			pos_search--;
			if (pos_search < 0)
				pos_search = arpos_search.size() - 1;
//			pos_search = 0;
		}
		tv_search.setText("[" + (pos_search + 1) + st.STR_SLASH + arpos_search.size() + "]");
		et.requestFocus();
		et.setSelection(arpos_search.get(pos_search).intValue());
	}

	@Override
	public void onBackPressed() {
		if (searchviewpanel) {
			searchPanelHide();
			return;
		}
		if (st.has(flags, FLAG_EDIT_TEXT) | st.has(flags, FLAG_EXTERNAL_FILE_EDIT) && changed) {
			Dlg.yesNoCancelDialog(inst, inst.getString(R.string.data_changed), R.string.yes, R.string.no,
					R.string.cancel, new st.UniObserver() {

						@Override
						public int OnObserver(Object param1, Object param2) {
							int an = ((Integer) param1).intValue();
							int cursor_pos = et.getSelectionStart();
							String str = et.getText().toString();
							// yes
							if (an == AlertDialog.BUTTON_POSITIVE) {
								if (st.has(flags, FLAG_EXTERNAL_FILE_EDIT)) {
									st.savefile(fn, str);
									closePreferenceActivity();
								} else if (st.has(flags, FLAG_CALL_IN_TPL_NAME_FIELD)) {
									if (TplEditorActivity.inst != null) {
										TplEditorActivity.inst.m_edText.setText(str);
										TplEditorActivity.inst.m_edText.setSelection(cursor_pos);
									}
								}
								finish();
							}
							// no
							else if (an == AlertDialog.BUTTON_NEUTRAL) {
								finish();
								closePreferenceActivity();
							}
							// cancel
							else if (an == AlertDialog.BUTTON_NEGATIVE) {

							}

							return 0;
						}
					});
		} else {
			closePreferenceActivity();
			if (TplEditorActivity.inst != null)
				TplEditorActivity.inst.help_specinstruction = false;
			super.onBackPressed();
		}
	}

	/**
	 * проверяем запуск активности для редактирования внешнего файла <br>
	 * и если это так, то закрываем активность настроек
	 */
	public void closePreferenceActivity() {
		if (!st.has(flags, FLAG_EXTERNAL_FILE_EDIT))
			return;
		if (JbKbdPreference.inst != null)
			JbKbdPreference.inst.finish();
	}

	public void setButtonHeightAndSizeText() {
		if (llcont == null)
			return;
		Button btn;
		int size = 1;
		for (int i = 0; i < llcont.getChildCount(); i++) {
			btn = null;
			btn = (Button) llcont.getChildAt(i);
			if (btn == null)
				continue;
			btn.measure(0, 0);
			size = btn.getMeasuredHeight();
			if (big_size) {
				size += 15;
			} else {
				size = LinearLayout.LayoutParams.WRAP_CONTENT;
			}
// c 2.33.13 не используется
//    		if (btn.getId() == R.id.desc_btn_size){
//        		if (big_size)
//        			btn.setText(R.string.ann_btn_small);
//        		else
//        			btn.setText(R.string.ann_btn_big);
//    		}
			btn.setHeight(size);
		}
		float ts = 17;
		if (et != null) {
			if (big_size)
				ts += 5;
			et.setTextSize(ts);

		}

	}

	/** загружаем текст в фоне для ускорения на слабых устройствах */
	public void setViewText() {
		if (load_progress != null) {
			load_progress.setVisibility(View.VISIBLE);
		}
		new Handler().postDelayed(new Runnable() {
			@SuppressWarnings("unused")
			public void run() {
				String fname = fn;
				if (fname == null)
					fname = st.STR_NULL;
				String str = null;
				;
				if (st.has(flags, FLAG_EXTERNAL_FILE_EDIT)) {
					File ff = new File(fname);
					str = st.readFileString(ff);
				} else if (st.has(flags, FLAG_EDIT_TEXT)) {
					str = fn;
				} else if (fname.contains(st.STA_FILENAME_DESC_KBD)) {
					fname = st.STR_UNDERSCORING + st.getLangDescKbd() + st.STA_FILENAME_DESC_KBD;
					str = st.readAssetsTextFilename(inst, fname);
				} else if (fname.contains(st.STA_FILENAME_HELP_SPECINSTRUCTION)) {
					fname = st.STR_UNDERSCORING + getLangHelpDescFilename() + st.STA_FILENAME_HELP_SPECINSTRUCTION;
					str = st.readAssetsTextFilename(inst, fname);
				} else if (fname.contains(st.STA_FILENAME_DIARY)) {
					str = st.readAssetsTextFilename(inst, fname);
				} else if (fname != null && fname.length() > 0) {
					str = fname;
				} else if (st.has(flags, FLAG_TEXT_IN_HELP_VARIABLE)) {
					if (st.help != null && st.help.length() > 0) {
						str = st.help;
					} else {
						JbKbd curkbd = st.curKbd();
						if (curkbd != null) {
							LatinKey key = curkbd.getKeyByCode(st.CMD_HELP);
							if (key != null)
								str = key.help;
							else
								key = curkbd.getKeyByLongCode(st.CMD_HELP);
							if (key != null)
								str = key.help;
//	                	} else {
//	                		str = fname;
						}
//		                if (str==null)
//	                		if (fname.length() > 0)
//	                			str=fname;
					}
					st.help = null;
				}
				setColorsEditor();
				if (!str.isEmpty()) {
					if (fname.contains(st.STA_FILENAME_DESC_KBD)) {
						int ind = str.indexOf(LAST_EDITED_DESC_KBD);
						if (ind > -1) {
							ind = str.indexOf(st.STR_LF);
							if (ind > -1)
								str = str.substring(ind + 1);
						}
					}
					if (str == null)
						str = st.STR_NULL;
					et.setText(str);
					changed = false;
					;
				}
				if (searchviewpanel)
					searchPanelHide();
				if (load_progress != null)
					load_progress.setVisibility(View.GONE);
				et.clearFocus();
				st.hidekbd();
			}
			// если времени указать мало, то load_progress не крутится
		}, 1000);
	}

	/**
	 * не используется!!! Смотрите setViewText()<br>
	 * Оставлено для примера использования handler
	 */
	public void setTextBackground(final String txt) {
		if (load_progress != null)
			load_progress.setVisibility(View.VISIBLE);
		new Handler().postDelayed(new Runnable() {
			public void run() {
				et.setText(txt);
				if (load_progress != null)
					load_progress.setVisibility(View.GONE);
			}
			// если времени указать мало, то load_progress не крутится
		}, 1000);

	}

	public String[] getArrayLangByPreFilename() {
		if (fn == null)
			return null;
		String[] in = null;
		String[] out = null;
		int pos = 0;
		try {
			in = inst.getAssets().list(st.STR_NULL);
			if (in == null || in.length < 1)
				return null;
			out = new String[in.length];
			pos = 0;
			for (int i = 0; i < in.length; i++) {
				out[i] = null;
				if (in[i].contains(fn)) {
					out[pos] = in[i];
					pos++;
				}
			}
			pos = 0;
			in = out;
			out = null;
			for (int i = 0; i < in.length; i++) {
				if (in[i] != null)
					pos++;
			}
			out = new String[pos];
			pos = 0;
			for (int i1 = 0; i1 < in.length; i1++) {
				if (in[i1] != null) {
					out[pos] = in[i1];
					pos++;
				}
			}
		} catch (IOException e) {
			return null;
		}
		in = new String[out.length];
		pos = 0;
		String lng = null;
		int ind = 0;
		for (int i = 0; i < out.length; i++) {
			lng = out[i];
			if (lng == null)
				continue;
			if (lng.startsWith(st.STR_UNDERSCORING))
				lng = lng.substring(1);
			else
				lng = out[i];
			ind = lng.indexOf(st.STR_UNDERSCORING);
			if (ind > -1) {
				in[pos] = lng.substring(0, ind);
				pos++;
			}
		}
		return in;
	}

	/** возвращает язык выводимого desc_kbd.txt */
	public static String getLangHelpDescFilename() {
		String out = Locale.getDefault().getLanguage();
		if (st.lang_help_specinstruction.contains(st.STR_3TIRE)) {
			if (out.contains("ru") || out.contains("az") || out.contains("hy") || out.contains("ba")
					|| out.contains("be") || out.contains("ka") || out.contains("kk") || out.contains("ky")
					|| out.contains("kv") || out.contains("lv") || out.contains("lt") || out.contains("tg")
					|| out.contains("tt") || out.contains("uz") || out.contains("uk") || out.contains("cv")
					|| out.contains("et"))
				out = "ru";
			else if (out.contains("es")
//     	           ||out.contains("az")
			)
				out = "es";
			else
				out = "en";
		} else
			out = st.lang_help_specinstruction;
		return out;
	}

	public void setColorsEditor() {
		if (et_color) {
			et.setTextColor(Color.BLACK);
			et.setBackgroundColor(Color.WHITE);
			load_progress.setBackgroundColor(Color.WHITE);
		} else {
			et.setTextColor(Color.WHITE);
			et.setBackgroundColor(Color.BLACK);
			load_progress.setBackgroundColor(Color.BLACK);
		}
	}
	public void setDefaultTextButton() {
		llcont = (LinearLayout)inst.findViewById(R.id.desc_llcontrol);
		if (llcont!=null&&Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			def_btn = (Button) llcont.findViewById(R.id.desc_btn_set_editor_color);
			Font.setTextOnTypeface(def_btn, Font.FontArSymbol.BLACK_WHITE_CIRCLE);
			def_btn = (Button) llcont.findViewById(R.id.desc_btn_sellang);
			Font.setTextOnTypeface(def_btn, Font.FontArSymbol.TRANSLATE);
			def_btn = (Button) llcont.findViewById(R.id.desc_btn_start);
			Font.setTextOnTypeface(def_btn, Font.FontArSymbol.TO_START);
			def_btn = (Button) llcont.findViewById(R.id.desc_btn_end);
			Font.setTextOnTypeface(def_btn, Font.FontArSymbol.TO_END);
			def_btn = (Button) llcont.findViewById(R.id.desc_btn_search);
			Font.setTextOnTypeface(def_btn, Font.FontArSymbol.SEARCH);
			def_btn = (Button) llcont.findViewById(R.id.desc_btn_pgup);
			Font.setTextOnTypeface(def_btn, Font.FontArSymbol.PGUP);
			def_btn = (Button) llcont.findViewById(R.id.desc_btn_pgdn);
			Font.setTextOnTypeface(def_btn, Font.FontArSymbol.PGDN);
			def_btn = (Button) llcont.findViewById(R.id.desc_btn_size);
			Font.setTextOnTypeface(def_btn, Font.FontArSymbol.BIG_TEXT);
			def_btn = (Button) llcont.findViewById(R.id.desc_btn_save);
			Font.setTextOnTypeface(def_btn, Font.FontArSymbol.SAVE);
			def_btn = (Button) llcont.findViewById(R.id.desc_btn_left_right);
			Font.setTextOnTypeface(def_btn, Font.FontArSymbol.LEFT_RIGHT);
		}
		
	}

}