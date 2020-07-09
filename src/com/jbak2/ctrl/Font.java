package com.jbak2.ctrl;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Vector;

import com.jbak2.Dialog.Dlg;
import com.jbak2.Dialog.DlgPopupWnd;
import com.jbak2.JbakKeyboard.ClipbrdSyncAct;
import com.jbak2.JbakKeyboard.R;
import com.jbak2.JbakKeyboard.ServiceJbKbd;
import com.jbak2.JbakKeyboard.ShowTextAct;
import com.jbak2.JbakKeyboard.Templates;
import com.jbak2.JbakKeyboard.Translate;
import com.jbak2.JbakKeyboard.VibroThread;
import com.jbak2.JbakKeyboard.st;
import com.jbak2.JbakKeyboard.com_menu.MenuEntry;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Шрифт клавиатуры, для отображения на разных спец.клавишах <br>
 * (enter, shift, space и т.д.)
 */
public class Font {
	public static Typeface tf = null;
	public static final String TYPEFACE_ASSETS_FILENAME = "jbak2key.ttf";
	static DlgPopupWnd dpw = null;
	/** пустой код (для вставки в key.label), чтобы не менялся значёк из шрифта клавы */
	public static final char NULL_CODE= (char) 0x0000;

	/** массив констант с перечислением какие буквы 
	 *  какому символу соответствуют <br>
	 * НЕ ЗАБЫВАТЬ ДОБАВЛЯТЬ новые символы и в методе <br>
	createArraySymbolOfFont() */
	public static char[] ar_symbol;

	/** НЕ ЗАБЫВАТЬ ДОБАВЛЯТЬ новые символы в методе <br>
	createArraySymbolOfFont() */

	/** просто массив констант с перечислением какие буквы <br>
	 *  какому символу соответствуют */
	public static class FontArSymbol {
		public static final char TO_START = 'ɐ';
		public static final char TO_END = 'ɑ';
		public static final char HOME_STR = 'ɒ';
		public static final char END_STR = 'ɓ';
		public static final char BACKSPACE = 'ɔ';
		public static final char DELETE = 'ɕ';
		public static final char KEYBOARD = 'ɖ';
		public static final char KEYBOARD_DONE = 'ɗ';
		
		public static final char UNDO = 'ɘ';
		public static final char REDO = 'ə';
		public static final char HOME = 'ɚ';
		public static final char MENU = 'ɛ';
		public static final char ENTER = 'ɜ';
		public static final char SPACE = 'ɝ';
		public static final char SCREPKA = 'ɞ';
		public static final char SMILE_CIRCLE = 'ɟ';
		
		public static final char SMILE_ULIBKA_HORIZONTAL = 'ɠ';
		public static final char SHIFT = 'ɡ';
		public static final char COPY = 'ɢ';
		public static final char PASTE = 'ɣ';
		public static final char KARANDASH = 'ɤ';
		/** вертикальные ножницы */
		public static final char NOZNICI = 'ɥ';
		public static final char CALCULATOR = 'ɦ';
		public static final char GALOCHKA = 'ɧ';
		
		public static final char BIG_TEXT = 'ɨ';
		public static final char SAVE = 'ɩ';
		public static final char OPEN = 'ɪ';
		public static final char GLOBUS = 'ɫ';
		public static final char LANGUAGE = 'ɬ';
		public static final char TRANSLATE = 'ɭ';
		public static final char SETTING = 'ɮ';
		public static final char SEARCH = 'ɯ';
		
		public static final char SORT_A_Z = 'ɰ';
		public static final char SORT_Z_A = 'ɱ';
		public static final char POWER_OFF = 'ɲ';
		public static final char POWER_ON = 'ɳ';
		public static final char PALETTE = 'ɴ';
		public static final char EXIT = 'ɵ';
		public static final char PGUP = 'ɶ';
		public static final char PGDN = 'ɷ';
		
		public static final char LEFT_RIGHT = 'ɸ';
		public static final char SELECT = 'ɹ';
		public static final char SHARE1 = 'ɺ';
		public static final char SHARE2 = 'ɻ';
		public static final char KORZINA = 'ɼ';
		public static final char SKIN = 'ɽ';
		public static final char FLAG_DARK = 'ɾ';
		public static final char FLAG_LIGHT = 'ɿ';
		
		public static final char SIZE_TEXT_HEIGHT = 'ʀ';
		public static final char SIZE_TEXT_WIDTH = 'ʁ';
		public static final char MICROPHONE = 'ʂ';
		public static final char MICROPHONE_OFF = 'ʃ';
		public static final char VIDEO = 'ʄ';
		public static final char CAMERA = 'ʅ';
		public static final char HELP_CIRCLE = 'ʆ';
		public static final char EDIT = 'ʇ';
		public static final char GRID = 'ʈ';

		public static final char OK_ON = 'ʉ';
		public static final char OK_OFF = 'ʊ';
		public static final char CLOSE = 'ʋ';
		public static final char CLOSE_CIPCLE = 'ʌ';
		public static final char EYE_ON = 'ʍ';
		public static final char EYE_OFF = 'ʎ';
		public static final char PICT = 'ʏ';
		public static final char PHONE = 'ʐ';

		public static final char LOGIN = 'ʑ';
		public static final char LOGOUT = 'ʒ';
		public static final char BLOCK = 'ʓ';
		public static final char APP_MAXIMIZE = 'ʔ';
		public static final char APP_MINIMIZE = 'ʕ';
		public static final char ZOOM_IN = 'ʖ';
		public static final char ZOOM_OUT = 'ʗ';
		public static final char FONT_BOLD = 'ʘ';
/** Зачёркнутый */
		public static final char FONT_STRIKED = 'ʙ';
		public static final char FONT_UNDERLINED = 'ʚ';
		public static final char GRAFIK_VERTICAL_LINE = 'ʛ';
		public static final char GRAFIK_UP = 'ʜ';
		public static final char DATABASE = 'ʝ';
		public static final char ICON_MOVE = 'ʞ';
		public static final char ICON_RESIZE = 'ʟ';
		public static final char CODE = 'ʠ';

		public static final char CURSOR = 'ʡ';
		public static final char CLOUD = 'ʢ';
		public static final char SPIN1 = 'ʣ';
		public static final char SPIN2 = 'ʤ';
		public static final char SPIN3 = 'ʥ';
		public static final char SPIN4 = 'ʦ';

//		public static final char  = '';
/** НЕ ЗАБЫВАТЬ ДОБАВЛЯТЬ новые символы в методе <br>
		createArraySymbolOfFont() */
	}
	public static char[] createArraySymbolOfFont() {
		Vector<String> vch = new Vector<String>();
		vch.add(st.STR_NULL+FontArSymbol.TO_START);
		vch.add(st.STR_NULL+FontArSymbol.TO_END);
		vch.add(st.STR_NULL+FontArSymbol.HOME_STR);
		vch.add(st.STR_NULL+FontArSymbol.END_STR);
		vch.add(st.STR_NULL+FontArSymbol.BACKSPACE);
		vch.add(st.STR_NULL+FontArSymbol.DELETE);
		vch.add(st.STR_NULL+FontArSymbol.KEYBOARD);
		vch.add(st.STR_NULL+FontArSymbol.SETTING);
		vch.add(st.STR_NULL+FontArSymbol.UNDO);
		vch.add(st.STR_NULL+FontArSymbol.REDO);
		vch.add(st.STR_NULL+FontArSymbol.HOME);
		vch.add(st.STR_NULL+FontArSymbol.MENU);
		vch.add(st.STR_NULL+FontArSymbol.ENTER);
		vch.add(st.STR_NULL+FontArSymbol.SPACE);
		vch.add(st.STR_NULL+FontArSymbol.SCREPKA);
		vch.add(st.STR_NULL+FontArSymbol.SMILE_CIRCLE);
		vch.add(st.STR_NULL+FontArSymbol.SMILE_ULIBKA_HORIZONTAL);
		vch.add(st.STR_NULL+FontArSymbol.SHIFT);
		vch.add(st.STR_NULL+FontArSymbol.COPY);
		vch.add(st.STR_NULL+FontArSymbol.PASTE);
		vch.add(st.STR_NULL+FontArSymbol.KARANDASH);
		vch.add(st.STR_NULL+FontArSymbol.NOZNICI);
		vch.add(st.STR_NULL+FontArSymbol.CALCULATOR);
		vch.add(st.STR_NULL+FontArSymbol.GALOCHKA);
		vch.add(st.STR_NULL+FontArSymbol.BIG_TEXT);
		vch.add(st.STR_NULL+FontArSymbol.SAVE);
		vch.add(st.STR_NULL+FontArSymbol.OPEN);
		vch.add(st.STR_NULL+FontArSymbol.GLOBUS);
		vch.add(st.STR_NULL+FontArSymbol.LANGUAGE);
		vch.add(st.STR_NULL+FontArSymbol.TRANSLATE);
		vch.add(st.STR_NULL+FontArSymbol.KEYBOARD_DONE);
		vch.add(st.STR_NULL+FontArSymbol.SEARCH);
		vch.add(st.STR_NULL+FontArSymbol.SORT_A_Z);
		vch.add(st.STR_NULL+FontArSymbol.SORT_Z_A);
		vch.add(st.STR_NULL+FontArSymbol.POWER_OFF);
		vch.add(st.STR_NULL+FontArSymbol.POWER_ON);
		vch.add(st.STR_NULL+FontArSymbol.PALETTE);
		vch.add(st.STR_NULL+FontArSymbol.EXIT);
		vch.add(st.STR_NULL+FontArSymbol.PGUP);
		vch.add(st.STR_NULL+FontArSymbol.PGDN);
		vch.add(st.STR_NULL+FontArSymbol.LEFT_RIGHT);
		vch.add(st.STR_NULL+FontArSymbol.SELECT);
		vch.add(st.STR_NULL+FontArSymbol.SHARE1);
		vch.add(st.STR_NULL+FontArSymbol.SHARE2);
		vch.add(st.STR_NULL+FontArSymbol.KORZINA);
		vch.add(st.STR_NULL+FontArSymbol.SKIN);
		vch.add(st.STR_NULL+FontArSymbol.FLAG_DARK);
		vch.add(st.STR_NULL+FontArSymbol.FLAG_LIGHT);
		vch.add(st.STR_NULL+FontArSymbol.SIZE_TEXT_HEIGHT);
		vch.add(st.STR_NULL+FontArSymbol.SIZE_TEXT_WIDTH);
		vch.add(st.STR_NULL+FontArSymbol.MICROPHONE);

		vch.add(st.STR_NULL+FontArSymbol.MICROPHONE_OFF);
		vch.add(st.STR_NULL+FontArSymbol.VIDEO);
		vch.add(st.STR_NULL+FontArSymbol.CAMERA);
		vch.add(st.STR_NULL+FontArSymbol.HELP_CIRCLE);
		vch.add(st.STR_NULL+FontArSymbol.EDIT);
		vch.add(st.STR_NULL+FontArSymbol.GRID);
		vch.add(st.STR_NULL+FontArSymbol.OK_ON);
		vch.add(st.STR_NULL+FontArSymbol.OK_OFF);
		vch.add(st.STR_NULL+FontArSymbol.CLOSE);
		vch.add(st.STR_NULL+FontArSymbol.CLOSE_CIPCLE);
		vch.add(st.STR_NULL+FontArSymbol.EYE_ON);
		vch.add(st.STR_NULL+FontArSymbol.EYE_OFF);
		vch.add(st.STR_NULL+FontArSymbol.PICT);
		vch.add(st.STR_NULL+FontArSymbol.PHONE);
		vch.add(st.STR_NULL+FontArSymbol.LOGIN);
		vch.add(st.STR_NULL+FontArSymbol.LOGOUT);
		vch.add(st.STR_NULL+FontArSymbol.BLOCK);
		vch.add(st.STR_NULL+FontArSymbol.APP_MAXIMIZE);
		vch.add(st.STR_NULL+FontArSymbol.APP_MINIMIZE);
		vch.add(st.STR_NULL+FontArSymbol.ZOOM_IN);
		vch.add(st.STR_NULL+FontArSymbol.ZOOM_OUT);
		vch.add(st.STR_NULL+FontArSymbol.FONT_BOLD);
		vch.add(st.STR_NULL+FontArSymbol.FONT_STRIKED);
		vch.add(st.STR_NULL+FontArSymbol.FONT_UNDERLINED);
		vch.add(st.STR_NULL+FontArSymbol.GRAFIK_VERTICAL_LINE);
		vch.add(st.STR_NULL+FontArSymbol.GRAFIK_UP);
		vch.add(st.STR_NULL+FontArSymbol.DATABASE);
		vch.add(st.STR_NULL+FontArSymbol.ICON_MOVE);
		vch.add(st.STR_NULL+FontArSymbol.ICON_RESIZE);
		vch.add(st.STR_NULL+FontArSymbol.CODE);
		vch.add(st.STR_NULL+FontArSymbol.CURSOR);
		vch.add(st.STR_NULL+FontArSymbol.CLOUD);
		vch.add(st.STR_NULL+FontArSymbol.SPIN1);
		vch.add(st.STR_NULL+FontArSymbol.SPIN2);
		vch.add(st.STR_NULL+FontArSymbol.SPIN3);
		vch.add(st.STR_NULL+FontArSymbol.SPIN4);

		char[] ar = new char[vch.size()];
		for (int i = 0; i < vch.size(); i++) {
			ar[i] = vch.get(i).charAt(0);
		}
		Arrays.sort(ar);
		return ar;
	}

	public Font(Context con) {
		tf = Typeface.createFromAsset(con.getAssets(), TYPEFACE_ASSETS_FILENAME);
		ar_symbol = createArraySymbolOfFont();
	}

	static class FontGridAdapt extends ArrayAdapter<char[]> {
		Context m_c = null;
		String ch = null;
		st.UniObserver m_MenuObserver;

		public FontGridAdapt(Context context, st.UniObserver obs) {
			super(context, 0);
			m_c = context;
			m_MenuObserver = obs;
			;

		}

		@Override
		public int getCount() {
			return ar_symbol.length;
		};

		@SuppressLint("NewApi")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView != null) {
				convertView = newView(position, convertView);
			} else {
				convertView = newView(position, null);
			}
			return convertView;
		}

		View newView(int pos, View vv) {
			LinearLayout ll = null;
			// символ из основного шрифта
			TextView tvs = null;
			// символ из шрифта клавиатуры
			TextView tv = null;
			if (vv == null) {
				ll = new LinearLayout(m_c);
				ll.setOrientation(LinearLayout.HORIZONTAL);
				tvs = new TextView(m_c);
				tv = new TextView(m_c);
				// НЕ ЮЗАТЬ!!! Иначе вылет на андроидах < 5
				// tv.setLayoutParams(new
				// LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
				// LinearLayout.LayoutParams.WRAP_CONTENT
				// ));
			} else {
				ll = (LinearLayout) vv;
				tvs = (TextView) ll.getChildAt(0);
				tv = (TextView) ll.getChildAt(1);
				ll.removeAllViews();
			}
			tvs.setTextColor(Color.WHITE);
			tvs.setText(st.STR_NULL + ar_symbol[pos]+st.STR_POINT);
			ll.addView(tvs);
			tv.setId(pos);
			tv.setOnClickListener(m_listener);
			tv.setOnLongClickListener(m_longListener);
			tv.setTextSize(40);
			tv.setTypeface(tf);
			tv.setTextColor(Color.WHITE);
			tv.setText(st.STR_NULL + ar_symbol[pos]);
			ll.addView(tv);
			vv = ll;
			return vv;
		}

		/** Обработчик короткого нажатия кнопок меню */
		View.OnClickListener m_listener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				int pos = v.getId();
				switch (v.getId()) {
				case R.id.but_lang_and_layout: // просто для примера

					return;
				}
				if (m_MenuObserver != null) {
					try {
						int ret = m_MenuObserver.OnObserver(pos, new Boolean(false));
						if (ret==0&&dpw!=null) {
							dpw.dismiss();
							dpw = null;
						}
					} catch (Throwable e) {
					}
				}
				return;
			}
		};
		View.OnLongClickListener m_longListener = new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {

				int pos = v.getId();
				switch (v.getId()) {
				case R.id.but_lang_and_layout: // просто для примера
					
					return true;
				}
				if (m_MenuObserver != null) {
					try {
						m_MenuObserver.OnObserver(pos, new Boolean(true));
						if (dpw!=null) {
							dpw.dismiss();
							dpw = null;
						}
					} catch (Throwable e) {
					}
				}
				return true;
			}
		};
	}

	/** устанавливаем шрифт и букву символа ch, на view объект <br> */
	public static void setTextOnTypeface(View view, char ch) {
		if (tf == null)
			return;
		String str = st.STR_NULL + ch;
		if (view instanceof Button) {
			((Button) view).setTypeface(tf);
			((Button) view).setText(str);
		} else if (view instanceof TextView) {
			((TextView) view).setTypeface(tf);
			((TextView) view).setText(str);
		} else if (view instanceof CheckBox) {
			((CheckBox) view).setTypeface(tf);
			((CheckBox) view).setText(str);
		}
	}
	/** устанавливаем шрифт на объект tp <br>
	 * false - установка не удалась (шрифт клавиатуры не инициализирован) */
	public static boolean setTypeface(TextPaint tp) {
		if (tf == null)
			return false;
		tp.setTypeface(tf);
		return true;
	}

	/** диалог выбора символа из списка типа gridLayout */
	public static void showDialogGridSelectSymbolOfFont(Context con, st.UniObserver obs) {
		GridView grid = new GridView(con);
		int wid = 600;
		try {
			wid = st.getDisplayWidth(con);
			
		} catch (Throwable e) {
		}
		if (wid >= 480)
			wid = 5;
		else if (wid >= 240&&wid < 480)
			wid = 4;
		else
			wid = 3;
		grid.setNumColumns(wid);
		FontGridAdapt adapt = new FontGridAdapt(con, obs);
		grid.setAdapter(adapt);
		Dlg.customDialog(con, grid, con.getString(R.string.cancel), null, null, obs);
	}
	/** диалог поверх клавиатуры, выбор символа из списка типа gridLayout */
	public static void showDialogOnKeyboardGridSelectSymbolOfFont(Context con, st.UniObserver obs) {
		GridView grid = new GridView(con);
		grid.setBackgroundResource(android.R.drawable.dialog_frame);
		int wid = 600;
		try {
			wid = st.getDisplayWidth(con);
			
		} catch (Throwable e) {
		}
		if (wid >= 480)
			wid = 5;
		else if (wid >= 240&&wid < 480)
			wid = 4;
		else
			wid = 3;
		grid.setNumColumns(wid);
		FontGridAdapt adapt = new FontGridAdapt(con, obs);
		grid.setAdapter(adapt);
		
		dpw = new DlgPopupWnd(st.c());
		dpw.setObserver(obs);
		dpw.set(null, 0, R.string.mm_close);
		dpw.setWidthAndHeight(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		dpw.show(0, grid);
	}

}