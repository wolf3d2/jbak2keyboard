package com.jbak2.ctrl;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Vector;

import com.jbak2.Dialog.Dlg;
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
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
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
	public static final String TYPEFACE_DEFAULT_ASSETS_FILENAME = "jbak2key.ttf";
	public static char[] ar_symbol;

	/**
	 * просто массив констант с перечислением какие буквы <br>
	 * какому символу соответствуют
	 */
	public static class FontArSymbol {
		public static final char TO_START = 'A';
		public static final char TO_END = 'B';
		public static final char HOME_STR = 'C';
		public static final char END_STR = 'D';
		public static final char BACKSPACE = 'E';
		public static final char DELETE = 'F';
		public static final char KEYBOARD = 'G';
		public static final char SETTING = 'H';
		public static final char UNDO = 'I';
		public static final char REDO = 'J';
		public static final char HOME = 'K';
		public static final char MENU = 'L';
		public static final char ENTER = 'M';
		public static final char SPACE = 'N';
		public static final char SCREPKA = 'O';
		public static final char SMILE_CIRCLE = 'P';
		public static final char SMILE_ULIBKA_HORIZONTAL = 'Q';
		public static final char SHIFT = 'R';
		public static final char COPY = 'S';
		public static final char PASTE = 'T';
		public static final char KARANDASH = 'U';
		/** вертикальные ножницы */
		public static final char NOZNICI = 'V';
		public static final char CALCULATOR = 'W';
		public static final char GALOCHKA = 'X';
		public static final char BIG_TEXT = 'Y';
		public static final char SAVE = 'Z';
		public static final char OPEN = 'a';
		public static final char GLOBUS = 'b';
		public static final char TRANSLATE = 'c';
		public static final char KEYBOARD_DONE = 'd';
		public static final char SEARCH = 'e';
		public static final char SORT_A_Z = 'f';
		public static final char SORT_Z_A = 'g';
		public static final char POWER_OFF = 'h';
		public static final char POWER_ON = 'i';
		public static final char PALETTE = 'j';
		public static final char EXIT = 'k';
		public static final char PGUP = 'l';
		public static final char PGDN = 'm';
		public static final char LEFT_RIGHT = 'n';
		public static final char SELECT = 'o';
		public static final char SHARE1 = 'p';
		public static final char SHARE2 = 'q';
	}

	public Font(Context con, String filenameAssets) {
		if (filenameAssets != null)
			tf = Typeface.createFromAsset(con.getAssets(), filenameAssets);
		else if (tf == null)
			tf = Typeface.createFromAsset(con.getAssets(), TYPEFACE_DEFAULT_ASSETS_FILENAME);
		ar_symbol = createArraySymbolOfFont();
	}

	static class FontGridAdapt extends ArrayAdapter<char[]> {
		Context m_c = null;
		String ch = null;
		st.UniObserver m_MenuObserver;

		public FontGridAdapt(Context context, st.UniObserver obs) {
			super(context, 0);
			m_c = context;
			ar_symbol = createArraySymbolOfFont();
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
				// Button b = (Button)convertView;
				// b.setTag(me);
				// b.setId(me.id);
				// b.setText(me.text);
				// b.setTransformationMethod(null);
			} else {
				convertView = newView(position, null);
			}
			return convertView;
		}

		View newView(int pos, View vv) {
			TextView tv = null;
			if (vv == null) {
				tv = new TextView(m_c);
				// НЕ ЮЗАТЬ!!! Иначе вылет на андроидах < 5
				// tv.setLayoutParams(new
				// LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
				// LinearLayout.LayoutParams.WRAP_CONTENT
				// ));
			} else
				tv = (TextView) vv;
			tv.setId(pos);
			tv.setOnClickListener(m_listener);
			tv.setOnLongClickListener(m_longListener);
			tv.setTextSize(40);
			tv.setTypeface(tf);
			// ch = st.STR_NULL+ar[pos];
			tv.setText(st.STR_NULL + ar_symbol[pos]);
			vv = tv;
			return vv;
		}

//		st.UniObserver gridObserver = new st.UniObserver() {
//			@Override
//			public int OnObserver(Object param1, Object param2) {
//				if (m_MenuObserver == null)
//					return 0;
//				m_MenuObserver.m_param1 = param1;
//				m_MenuObserver.Observ();
//				return 0;
//			}
//		};
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
						m_MenuObserver.OnObserver(pos, new Boolean(false));
						// if (close_menu)
						// close();

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
						// if (close_menu)
						// close();

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

	public static char[] createArraySymbolOfFont() {
		Field[] ff = Font.FontArSymbol.class.getDeclaredFields();
		String nn = null;
		Field fff = null;
		Vector<String> vch = new Vector<String>();
		for (int i = 0; i < ff.length; i++) {
			fff = ff[i];
			nn = fff.getType().toString();// .getName();
			if (nn.compareToIgnoreCase("char") == 0) {
				try {
					vch.add("" + fff.getChar(i));
				} catch (IllegalArgumentException | IllegalAccessException e1) {
				}
			}
		}
		char[] ar = new char[vch.size()];
		for (int i = 0; i < vch.size(); i++) {
			ar[i] = vch.get(i).charAt(0);
		}
		Arrays.sort(ar);
		return ar;
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
		else
			wid = 3;
		grid.setNumColumns(wid);
		FontGridAdapt adapt = new FontGridAdapt(con, obs);
		grid.setAdapter(adapt);
		Dlg.customDialog(con, grid, con.getString(R.string.cancel), null, null, obs);
	}

}
