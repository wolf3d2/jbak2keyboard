package com.jbak2.JbakKeyboard;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;
import java.util.Vector;

import android.R.drawable;
import android.content.Context;
import android.graphics.Color;
import android.os.Environment;

import com.jbak2.CustomGraphics.BitmapCachedGradBack;
import com.jbak2.CustomGraphics.GradBack;
import com.jbak2.JbakKeyboard.st.IntEntry;

public class IKeyboard {
	// лучше всего описан скин Розовый

	// типы раскладок, для заголовков в списке языков в Языках и раскладках
	public static final int TYPE_LANG_NONE = -1;
	public static final int TYPE_LANG_MAIN = 0;
	public static final int TYPE_LANG_UNKNOWN = 1;
	public static final int TYPE_LANG_OTHER = 2;
	public static final int TYPE_LANG_VIRTUAL = 3;
	public static final int TYPE_LANG_SYMBOL2 = 4;

	public static final String LANG_SYMBOL_KBD = "symbol";
	public static final String LANG_SYMBOL2_KBD = "symbol2";
	public static final String LANG_EDITTEXT = "edittext";
	public static final String LANG_SMILE = "smile";
	public static final String LANG_NUMBER = "num";
	public static final String LANG_CALCULATOR = "calc";
	public static final String LANG_HIDE_LAYOUT = "hide";
	public static final String LANG_QWERTY = "qwerty";
	// --------------------------------------------------------------------------
	/** язык не определён (пустой язык) */
	public static final int LANG_EMPTY = 10000	;
	public static final int LANG_EN = 0;
	public static final int LANG_RU = 1;
	public static final int LANG_UK = 2;
	public static final int LANG_BE = 3;
	public static final int LANG_FR = 4;
	public static final int LANG_IT = 5;
	public static final int LANG_DE = 6;
	public static final int LANG_SV = 7;
	public static final int LANG_TT = 8;
	public static final int LANG_BA = 9;
	public static final int LANG_TR = 10;
	public static final int LANG_HE = 11;
	public static final int LANG_EL = 12;
	public static final int LANG_ES = 13;
	public static final int LANG_LV = 14;
	public static final int LANG_AR = 15;
	public static final int LANG_ID = 16;
	public static final int LANG_KA = 17;
	public static final int LANG_PL = 18;
	public static final int LANG_NO = 19;
	public static final int LANG_UZ = 20;
	public static final int LANG_AZ = 21;
	public static final int LANG_PT = 22;
	public static final int LANG_FI = 23;

	public static final int LANG_SYM = 1000;
	public static final int LANG_SYM1 = 1001;
	public static final int LANG_EDIT = 1002;
	public static final int LANG_SMIL = 1003;
	public static final int LANG_NUM = 1004;
	public static final int LANG_CALC = 1005;
	public static final int LANG_HIDE = 1006;
	// --------------------------------------------------------------------------
	public static Lang[] arLangs = null; 
//			new Lang(LANG_EN, "en"), 
//			new Lang(LANG_RU, "ru"), 
//			new Lang(LANG_UK, "uk"),
//			new Lang(LANG_BE, "be"), 
//			new Lang(LANG_FR, "fr"), 
//			new Lang(LANG_ES, "es"), 
//			new Lang(LANG_IT, "it"),
//			new Lang(LANG_DE, "de"), 
//			new Lang(LANG_LV, "lv"), 
//			new Lang(LANG_SV, "sv"), 
//			new Lang(LANG_TT, "tt"),
//			new Lang(LANG_BA, "ba"), 
//			new Lang(LANG_TR, "tr"), 
//			new Lang(LANG_HE, "he"), 
//			new Lang(LANG_EL, "el"),
//			new Lang(LANG_AR, "ar"), 
//			new Lang(LANG_ID, "id"), 
//			new Lang(LANG_KA, "ka"), 
//			new Lang(LANG_PL, "pl"),
//			new Lang(LANG_NO, "no"), 
//			new Lang(LANG_UZ, "uz"), 
//			new Lang(LANG_AZ, "az"), 
//			new Lang(LANG_PT, "pt"),
//			new Lang(LANG_FI, "fi"),
//
//			new Lang(LANG_SYM, LANG_SYM_KBD), 
//			new Lang(LANG_SYM1, LANG_SYM_KBD1), 
//			new Lang(LANG_EDIT, LANG_EDITTEXT),
//			new Lang(LANG_SMIL, LANG_SMILE), 
//			new Lang(LANG_NUM, LANG_NUMBER), 
//			new Lang(LANG_CALC, LANG_CALCULATOR),
//	};
	// --------------------------------------------------------------------------
	// Коды клавиатур
	public static final int KBD_QWERTY_EN = 0;
	public static final int KBD_QWERTY_RU = 1;
	public static final int KBD_QWERTY_BE = 2;
	public static final int KBD_QWERTY_UA = 3;
	public static final int KBD_QWERTY_RU_HALF = 4;
	public static final int KBD_QWERTY_EN_HALF = 5;
	public static final int KBD_SYM = 6;
	public static final int KBD_SYM1 = 7;
	public static final int KBD_EDITTEXT = 8;
	public static final int KBD_SMILE = 9;

	public static final int KBD_CUSTOM = -1;
	public static final int KBD_COMPILED = -2;

	public static String getSettingsPath() {
		return Environment.getExternalStorageDirectory().getAbsolutePath() + "/jbak2Keyboard/";
	}
	/** возвращает короткий путь из fullpath, с вырезанным путём от корня 
	 * @param typefolder - тип, что ещё вырезать  (одна из констант Templates.INT_FOLDER_*)?: <br>?: <br>
	 * 0 - ничего <br>
	 * 1 - "templates/" <br>
	 * 2 - "calc/" */
	public static String getSettingsPathShort(String fullpath, int typefolder) 
	{
    	if (fullpath.startsWith(st.getSettingsPath())) {
    		fullpath = fullpath.substring((st.getSettingsPath()).length());
    		if (typefolder == Templates.INT_FOLDER_TEMPLATES)
        		fullpath = fullpath.substring((Templates.FOLDER_TEMPLATES+st.STR_SLASH).length());
    		else if (typefolder == Templates.INT_FOLDER_CALC)
        		fullpath = fullpath.substring((Templates.FOLDER_CALC+st.STR_SLASH).length());
    	}
		return fullpath;
	}
	/** добавляет в path путь от корня 
	 * @param typefolder - тип, что ещё добавлять (одна из констант Templates.INT_FOLDER_*)?: <br>
	 * 0 - ничего <br>
	 * 1 - "templates/" <br>
	 * 2 - "calc/" */
	public static String getSettingsPathFull(String path, int typefolder) 
	{
		String add = st.getSettingsPath();
		if (typefolder == Templates.INT_FOLDER_TEMPLATES)
			add += Templates.FOLDER_TEMPLATES+st.STR_SLASH;
		else if (typefolder == Templates.INT_FOLDER_CALC)
			add += Templates.FOLDER_CALC+st.STR_SLASH;
		path = add+path;
		return path;
	}

	public static Keybrd[] arKbd = {
			new Keybrd("en_psevdoT9_MWcorp_v109", R.string.kbd_psevdo_t9_mwcorp),
	};
	// Флаги дизайна (Design Flags)
	/** Жирный шрифт */
	public static final int DF_BOLD = 0x0001;
	/** Большой отступ, {@link KeyDrw#BIG_GAP} */
	public static final int DF_BIG_GAP = 0x0002;

	public static final int DEF_COLOR = GradBack.DEFAULT_COLOR;
	public static final int KBD_DESIGN_STANDARD = 4;
	public static KbdDesign[] arDesign = {
			// Стандартный дизайн
			new KbdDesign(R.string.kbd_design_standard, 0, Color.WHITE, 0, 0),
	};

	// *****************************************************************
	/** Класс для хранения оформлений клавиатур */
	public static class KbdDesign {
		/** временная переменная для задания прозрачности в цвете */
		// String strcol;
		/** признак, что индекса данного цвета нет в дизайне */
//		public static final int NONE_COLOR_IN_DESIGN = -100000000;

		/** Id drawable-ресурса для рисования кнопок */
		public int drawResId;
		/** Id ресурса названия клавиатуры */
		public int nameResId;
		/** Цвет текста */
		public int textColor;
		public int secondColor = st.DEF_COLOR;
		public int textColorPressed = st.DEF_COLOR;
		public int secondColorPressed = st.DEF_COLOR;
		/** drawable-ресурс для рисования фона клавиатуры */
		public int backDrawableRes;
		/** Флаги */
		public int flags = 0;
		/** Путь к файлу скина, если скин не встроенный */
		String path = null;
		/** Фон клавиш */
		GradBack m_keyBackground = null;
		/** Фон клавиатуры */
		GradBack m_kbdBackground = null;
		/** Отдельный дизайн для функциональных клавиш (цвет текста, фон, обводка) */
		KbdDesign m_kbdFuncKeys = null;
		/** отдельные цвета дизайна */
	    public Vector<IntEntry> arItemColor = new Vector<IntEntry>();
		/** временный массив задания цветов дизайна <br>
		 * НЕ ЗАБЫВАТЬ В КОНЦЕ ЗАДАНИЯ ДИЗАЙНА? ЮЗАТЬ МЕТОД <br>
		 * copyItemColorDesign() */
	    public	static Vector<IntEntry> arItemColorTemp = new Vector<IntEntry>();

//	    /** временная переменная - начальный цвет */
//	    private int tcol_beg = 0;
//	    private int tcol_end = 0;
	    
		public KbdDesign(int name, int drawable, int textColor, int backDrawable, int flags) {
			clearArItemColorTemp();
			arItemColor.clear();
			nameResId = name;
			drawResId = drawable;
			this.textColor = textColor;
			addItemColor(IntEntry.KeyTextColor, textColor);
			this.backDrawableRes = backDrawable;
			this.flags = flags;
		}

		public KbdDesign(String path) {
			clearArItemColorTemp();
			arItemColor.clear();
			this.path = path;
		}

		/** скин из assets */
		public KbdDesign(int resNameId, String filename) {
			clearArItemColorTemp();
			arItemColor.clear();
			nameResId = resNameId;
			this.path = CustomKbdDesign.ASSETS + filename;
		}
		/** добавляемм цвета из временного массива в основной этого дизайна */
		public KbdDesign setCopyItemColorsDesign()
		{
			IntEntry ie = null;
			IntEntry ie1 = null;
			for (int i=0;i<arItemColorTemp.size();i++) {
				ie = arItemColorTemp.get(i);
				for (int i1=0;i1<arItemColor.size();i1++) {
					ie1 = arItemColor.get(i1);
					if (ie.index == ie1.index) {
						arItemColor.remove(i1);
					}
				}
			}
			this.arItemColor.addAll(arItemColorTemp);
			arItemColorTemp.clear();
			return this;
		}
		/** добавляем во времееный масив по ключу IntEntry.index, значение цвета value */
		public static void addItemColor(int index, int colorValue)
		{
			arItemColorTemp.add(new IntEntry(index, colorValue));
			
		}
		/** очищаем временный массив цветов, если он не пуст, <br> 
		 * при создании нового дизайна*/
		static public void clearArItemColorTemp()
		{
			if (KbdDesign.arItemColorTemp!=null&&KbdDesign.arItemColorTemp.size()>0)
				KbdDesign.arItemColorTemp.clear();
				
		}
		/** возвращает цвет из основного дизайна, или дефолтный цвет */
		public int getItemColor(int index)
		{
			for (IntEntry ie:arItemColor) {
				if (index == ie.index)
					return ie.value;
			}
			return st.DEF_COLOR;
		}
		/** возвращает цвет из дизайна спецклавиш, или дефолтный цвет */
		public int getItemColorSpec(int index)
		{
			for (IntEntry ie:arItemColor) {
				if (index == ie.index)
					return ie.value;
			}
			return st.DEF_COLOR;
		}
		KbdDesign setKeysBackground(GradBack bg) {
			
	    	KbdDesign.addItemColor(IntEntry.KeyBackStartColor, BitmapCachedGradBack.cols);
	    	KbdDesign.addItemColor(IntEntry.KeyBackEndColor, BitmapCachedGradBack.cole);
			m_keyBackground = bg;
			if (bg instanceof BitmapCachedGradBack)
				((BitmapCachedGradBack) bg).setCacheSize(20);
			return this;
		}
		KbdDesign setKbdBackground(GradBack bg) {
	    	KbdDesign.addItemColor(IntEntry.KeyboardBackgroundStartColor, BitmapCachedGradBack.cols);
	    	KbdDesign.addItemColor(IntEntry.KeyboardBackgroundEndColor, BitmapCachedGradBack.cole);
			m_kbdBackground = bg;
			if (bg instanceof BitmapCachedGradBack)
				((BitmapCachedGradBack) bg).setCacheSize(2);
			return this;
		}

		KbdDesign setColors(int text, int second, int textPressed, int secondPressed) {
			textColor = text;
			textColorPressed = textPressed;
			secondColor = second;
			secondColorPressed = secondPressed;
			return this;
		}

		KbdDesign setFuncKeysDesign(KbdDesign fc) {
			fc.setCopyItemColorsDesign();
			m_kbdFuncKeys = fc;
			return this;
		}

		public KbdDesign getDesign() {
			BitmapCachedGradBack.clearAllCache();
			if (path == null)
				return this;
			CustomKbdDesign d = new CustomKbdDesign();
			if (!d.load(path))
				return arDesign[0];
			return d.getDesign();
		}

		String getName(Context c) {
			try {
				if (nameResId != 0)
					return c.getString(nameResId);
				else if (path != null)
					return new File(path).getName();
			} catch (Throwable e) {
			}
			return "<bad name>";
		}
	}

	static GradBack skinIPhoneKey() {
		GradBack stroke = new BitmapCachedGradBack(st.getSkinColorAlpha(0xff8c929a), st.getSkinColorAlpha(0xff2c2f32))
				.setGap(4);
		return new BitmapCachedGradBack(true, st.getSkinColorAlpha(Color.WHITE), st.getSkinColorAlpha(0xffC1C1C1)).setGap(6)
				.setShadowColor(GradBack.DEFAULT_COLOR).setStroke(stroke);
	}

	static KbdDesign skinHTCDesign() {
		return new KbdDesign(R.string.kbd_design_htc, 0, 0xff000000, 0, DF_BOLD).setCopyItemColorsDesign()
				.setKeysBackground(
						new BitmapCachedGradBack(true,st.getSkinColorAlpha(0xfff8f8f8), st.getSkinColorAlpha(0xffd8d4d8))
								.setGap(3)
								.setStroke(new BitmapCachedGradBack(st.getSkinColorAlpha(0xff605960),
										st.getSkinColorAlpha(0xff101418)).setGap(2)))
				.setCopyItemColorsDesign()
				.setKbdBackground(new BitmapCachedGradBack(true, 0xffbdbebd, 0xff706e70).setCorners(0, 0).setGap(0))
				.setCopyItemColorsDesign()
				.setFuncKeysDesign(new KbdDesign(0, 0, Color.WHITE, 0, 0)
						// .setKeysBackground(new
						// BitmapCachedGradBack(st.setColorTransparency(0xff686868),
						// st.setColorTransparency(0xff101418))
						.setKeysBackground(new BitmapCachedGradBack(true,st.getSkinColorAlpha(0xff686868),
								st.getSkinColorAlpha(0xff404040)).setGap(3)
										.setStroke(new BitmapCachedGradBack(st.getSkinColorAlpha(0xff605960),
												st.getSkinColorAlpha(0xff101418)).setGap(2))));
				//.setCopyItemColorsDesign());
	}

	static KbdDesign skinMagentaDesign() {
		return
		// цвет квадрата фона клавиатуры
		// здесь же задаётся цвет текста простых кнопок
		new KbdDesign(R.string.kbd_design_pink, 0, Color.BLUE, 0, DF_BOLD).setCopyItemColorsDesign()
				// цвет обычных клавиш (с градиентом)
				.setKeysBackground(
						new BitmapCachedGradBack(true, st.getSkinColorAlpha(Color.WHITE), st.getSkinColorAlpha(Color.MAGENTA))
								// Установка отступа краёв фона от прямоугольника, на котором фон отрисовывается
								.setGap(2)
								// Устанавливает обводку stroke, в виде еще одного объекта GradBack
								// Необходимо правильно задать отступ (gap) для нового объекта, с учетом того,
								// что сверху будет нарисован текущий объект
								.setStroke(new BitmapCachedGradBack(st.getSkinColorAlpha(0xff605960),
										st.getSkinColorAlpha(0xff101418)).setGap(1)))
				.setCopyItemColorsDesign()
				.setKbdBackground(new BitmapCachedGradBack(true, st.getSkinColorAlpha(0xffaabbcc), 0xffabdccd)
						// радиус скругления углов
						.setCorners(0, 0)
						// Установка отступа краёв фона от прямоугольника, на котором фон отрисовывается
						.setGap(2)).setCopyItemColorsDesign()
				// функциональные клавиши
				.setFuncKeysDesign(new KbdDesign(0, 0, Color.WHITE, 0, 0).setKeysBackground(
						new BitmapCachedGradBack(true, st.getSkinColorAlpha(0xff686868), st.getSkinColorAlpha(0xff404040))
								.setGap(1).setStroke(new BitmapCachedGradBack(st.getSkinColorAlpha(0xff605960),
										st.getSkinColorAlpha(0xff101418)).setGap(2))));
	}

	static KbdDesign skinDarkDesign() {
		return
		// цвет квадрата фона клавиатуры
		// здесь же задаётся цвет текста простых кнопок
		new KbdDesign(R.string.kbd_design_dark, 0, Color.BLACK, drawable.dark_header, DF_BOLD).setCopyItemColorsDesign()
				.setKeysBackground(
						new BitmapCachedGradBack(true, st.getSkinColorAlpha(Color.WHITE), st.getSkinColorAlpha(0xff222222))

								// Установка отступа краёв фона от прямоугольника, на котором фон отрисовывается
								.setGap(3)
								// Устанавливает обводку stroke, в виде еще одного объекта GradBack
								// Необходимо правильно задать отступ (gap) для нового объекта, с учетом того,
								// что сверху будет нарисован текущий объект
								.setStroke(new BitmapCachedGradBack(st.getSkinColorAlpha(0xff605960),
										st.getSkinColorAlpha(0xff101418)).setGap(2)))
				.setCopyItemColorsDesign()
				.setKbdBackground(new BitmapCachedGradBack(true, 0xffeeeeee, 0xffffffff)
						// радиус скругления углов
						.setCorners(0, 0)
						// Установка отступа краёв фона от прямоугольника, на котором фон отрисовывается
						.setGap(0))
				.setCopyItemColorsDesign()
				// функциональные клавиши
				.setFuncKeysDesign(new KbdDesign(0, 0, Color.WHITE, 0, 0).setKeysBackground(
						new BitmapCachedGradBack(true, st.getSkinColorAlpha(0xff686868), st.getSkinColorAlpha(0xff404040))
								.setGap(3).setStroke(new BitmapCachedGradBack(st.getSkinColorAlpha(0xff605960),
										st.getSkinColorAlpha(0xff101418)).setGap(2))));
	}

	static KbdDesign skinSolidDesign() {
		return
		// цвет квадрата фона клавиатуры
		// здесь же задаётся цвет текста простых кнопок
		new KbdDesign(R.string.kbd_design_solid, 0, Color.BLACK, 0, DF_BOLD).setCopyItemColorsDesign()
				// цвет обычных клавиш (с градиентом)
				.setKeysBackground(
						new BitmapCachedGradBack(true, st.getSkinColorAlpha(Color.WHITE), st.getSkinColorAlpha(0xff999999))
								// Установка отступа между клавишами одного ряда
								.setGap(0)
								// Устанавливает обводку stroke, в виде еще одного объекта GradBack
								// Необходимо правильно задать отступ (gap) для нового объекта, с учетом того,
								// что сверху будет нарисован текущий объект
								.setStroke(new BitmapCachedGradBack(st.getSkinColorAlpha(0xff605960),
										st.getSkinColorAlpha(0xff101418)).setGap(0)))
				.setCopyItemColorsDesign()
				.setKbdBackground(new BitmapCachedGradBack(true, 0xffaabbcc, 0xffabdccd)
						// радиус скругления углов
						.setCorners(0, 0)
						// Установка отступа краёв фона от прямоугольника, на котором фон отрисовывается
						.setGap(0))
				.setCopyItemColorsDesign()
				// функциональные клавиши
				.setFuncKeysDesign(new KbdDesign(0, 0, Color.WHITE, 0, 0).setKeysBackground(
						new BitmapCachedGradBack(true, st.getSkinColorAlpha(0xff686868), st.getSkinColorAlpha(0xff404040))
								.setGap(0).setStroke(new BitmapCachedGradBack(st.getSkinColorAlpha(0xff605960),
										st.getSkinColorAlpha(0xff101418)).setGap(0))));
	}

	// *****************************************************************
	/** Класс для хранения сведений о языке */
	public static class Lang {
		
		public Lang(int lang, String name) {
			this.type = st.TYPE_LANG_NONE;
			this.lang = lang;
			this.name = name;
			switch (lang) {
			case LANG_EDIT:
				strId = R.string.lang_edittext;
				break;
			case LANG_SYM:
				strId = R.string.lang_symbol;
				break;
			case LANG_SYM1:
				strId = R.string.lang_symbol_shift;
				break;
			case LANG_SMIL:
				strId = R.string.lang_smiles;
				break;
			case LANG_NUM:
				strId = R.string.lang_numbers;
				break;
			case LANG_CALC:
				strId = R.string.lang_calc;
				break;
			default:
				strId = 0;
			}

		}

		/** Код языка, одна из констант LANG_ */
		public int lang;
		/** Тип языка - виртуальный, основной, неизвестный и тд*/
		public int type;
		/** Символьный код языка ("ru" - для русского, "en" - для английского и тд) */
		public String name;
		/** Строка с названием языка из ресурсов */
		public int strId;
		boolean bVirtual = false;
/** если в языке задан тип, отличный от -1, то это означает,
 * что нужно выводить заголовок группы языков ниже этого,
 * в активности выбора языков и раскладок 
 * @param typeLang - одна из констант TYPE_LANG в IKeyboard*/
		void setTypeLang(int typeLang) {
			type = typeLang;
		}
		/** Возвращает "человеческое" название языка lang,
		 *  с заглавной первой буквой */
		static String getLangDisplayName(String lang) {
			try {
				String ln = new Locale(lang).getDisplayName();
				if (ln.length() > 1) {
					return st.upFirstSymbol(ln);
				}
			} catch (Throwable e) {
			}
			return lang;
		}
		static String getLangShortName(int id) {
			String ret = st.STR_NULL;
			if (arLangs==null)
				setDefaultLang();
			for (int i = 0; i < arLangs.length; i++) {
				if (id == arLangs[i].lang)
					return arLangs[i].name;
			}
			return null;
		}

		String getName(Context c) {
			try {
				if (strId > 0) {
					return c.getString(strId);
				} else {
					return getLangDisplayName(name);
				}
			} catch (Throwable e) {
			}
			return name;
		}

		/**
		 * возвращает виртуальные языки (в программе они в конце списка и их невозможно
		 * выбрать)
		 */
		final boolean isVirtualLang() {
			return lang == LANG_SYM || lang == LANG_SMIL || lang == LANG_SYM1 
					|| lang == LANG_EDIT || lang == LANG_NUM || lang == LANG_CALC;
		}
	    /** возвращает сортированным массивом String список всех доступных в Locale языков 
	     * @param type - возвращаемый формат: <br>
	     * 0 - "ru - Русский" <br>
	     * 1 - "Русский" <br>
	     * 2 - "Русский - ru" <br>
	     * 3 - "ru" */
	    public static String[] getAlLocalelLang(int type) {
	    	String[] locales = Locale.getISOLanguages();
	    	Vector<String> o1 = new Vector<String>();
	    	Lang ln = null;
	    	String shortn = null;
	    	String dispn = null;
	    	for (int i=0; i<locales.length;i++) {
		    	try {
			    	ln = new Lang(IKeyboard.LANG_EMPTY, locales[i]);
			    	shortn = ln.name;
			    	if (shortn==null||shortn.length()<2||shortn.length()>2)
			    		continue;
			    	dispn = ln.getLangDisplayName(shortn);
			    	if (dispn==null||dispn.length()< 3)
			    		continue;
			        switch (type)
			        {
			        case 0:
			        	o1.add(shortn.toLowerCase() + " - "+ dispn);
			        	break;
			        case 1:
			        	o1.add(dispn);
			        	break;
			        case 2:
			        	o1.add(dispn + " - "+ shortn.toLowerCase());
			        	break;
			        case 3:
			        	o1.add(shortn.toLowerCase());
			        	break;
			        }
					
				} catch (Throwable e) {
				}
		    }
	    	String[] out = new String[o1.size()];
	    	for (int i=0;i<o1.size();i++)
	    	{
	    		out[i] = o1.get(i).toString();
	    	}
	        Arrays.sort(out);
			return out;
	    }
		
	}

	// *****************************************************************
	/** Класс для хранения сведений о конкретной клавиатуре */
	public static class Keybrd {
		/** Язык клавиатуры, один из элементов массива arLangs */
		public Lang lang;
		/** XML-ресурс клавиатуры (из R.xml) */
		public int resId;
		/** Код клавиатуры, одна из констант KBD_ */
		public int kbdCode;
		/** Строка из ресурсов с названием клавиатуры */
		public int resName;
		public String path = null;
		public boolean isWide = false;
		public static final String WIDE_SUFFIX = "_wide";
		public static final String TABLET_SUFFIX = "_tablet";
		public static final String TABLET_HALF_SUFFIX = "_tablet_half";

		/**
		 * Конструктор
		 * 
		 * @param kbdCode
		 *            Код клавиатуры, одна из констант KBD_
		 * @param lang
		 *            Язык клавиатуры, один из элементов массива arLangs
		 * @param resId
		 *            XML-ресурс клавиатуры (из R.xml)
		 * @param resName
		 *            Строка из ресурсов с названием клавиатуры
		 */
		Keybrd(String assetFileName, int resName) {
			this.kbdCode = KBD_COMPILED;
			this.lang = getLangByName(assetFileName.substring(0, assetFileName.indexOf('_')));
			this.resId = R.xml.kbd_empty;
			path = assetFileName;
			isWide = assetFileName.endsWith(WIDE_SUFFIX);
			this.resName = resName;
		}

		Keybrd(Keybrd k) {
			this.kbdCode = k.kbdCode;
			this.lang = k.lang;
			this.resId = k.resId;
			this.resName = k.resName;
			isWide = k.isWide;
			path = k.path;
		}

		Keybrd(int kbdCode, Lang lang, int resId, int resName) {
			this.kbdCode = kbdCode;
			this.lang = lang;
			this.resId = resId;
			this.resName = resName;
		}

		String getName(Context c) {
			if (resName != 0)
				return c.getString(resName);
			if (path != null) {
				return new File(path).getName();
			}
			return "<undef>";
		}

		final boolean isLang(String lng) {
			return lang.name.equals(lng);
		}
	}

	// -----------------------------------------------------------------------------
	public static Keybrd defKbd() {
		return arKbd[0];
	}

	// -----------------------------------------------------------------------------
	/** Возвращает язык по внутреннему коду, константы KBD_ */
	public static Keybrd kbdForCode(int kbd) {
		for (int i = 0; i < arKbd.length; i++) {
			Keybrd l = arKbd[i];
			if (kbd == l.kbdCode)
				return l;
		}
		return null;
	}

	// -----------------------------------------------------------------------------
	/** Возвращает клавиатуру по коду клавиатуры из ресурсов */
	static Lang getLangByName(String name) {
		if (arLangs==null)
			setDefaultLang();
		for (Lang l : arLangs) {
			if (l.name.equals(name))
				return l;
		}
		return addCustomLang(name);
	}

	static Lang addCustomLang(String name) {
		if (arLangs==null)
			setDefaultLang();
		Lang lng = new Lang(arLangs.length, name);
		Lang al[] = new Lang[arLangs.length + 1];
		int pos = arLangs.length;
		System.arraycopy(arLangs, 0, al, 0, pos);
		al[pos] = lng;
		arLangs = al;
		return lng;
	}
	public static void setDefaultLang() {
		arLangs = null;
		arLangs = new Lang[] {
				new Lang(LANG_EN, "en"), 
				new Lang(LANG_RU, "ru"), 
				new Lang(LANG_UK, "uk"),
				new Lang(LANG_BE, "be"), 
				new Lang(LANG_FR, "fr"), 
				new Lang(LANG_ES, "es"), 
				new Lang(LANG_IT, "it"),
				new Lang(LANG_DE, "de"), 
				new Lang(LANG_LV, "lv"), 
				new Lang(LANG_SV, "sv"), 
				new Lang(LANG_TT, "tt"),
				new Lang(LANG_BA, "ba"), 
				new Lang(LANG_TR, "tr"), 
				new Lang(LANG_HE, "he"), 
				new Lang(LANG_EL, "el"),
				new Lang(LANG_AR, "ar"), 
				new Lang(LANG_ID, "id"), 
				new Lang(LANG_KA, "ka"), 
				new Lang(LANG_PL, "pl"),
				new Lang(LANG_NO, "no"), 
				new Lang(LANG_UZ, "uz"), 
				new Lang(LANG_AZ, "az"), 
				new Lang(LANG_PT, "pt"),
				new Lang(LANG_FI, "fi"),
	
				new Lang(LANG_SYM, LANG_SYMBOL_KBD), 
				new Lang(LANG_SYM1, LANG_SYMBOL2_KBD), 
				new Lang(LANG_EDIT, LANG_EDITTEXT),
				new Lang(LANG_SMIL, LANG_SMILE), 
				new Lang(LANG_NUM, LANG_NUMBER), 
				new Lang(LANG_CALC, LANG_CALCULATOR),
		};
	}
////Рожденный в СССР
//new KbdDesign(R.string.kbd_design_ussr,
//0,
//Color.WHITE,
//0,
//0)
//.setKeysBackground(new BitmapCachedGradBack(0xff800000,
////0xffc00000).setGradType(GradBack.GRADIENT_TYPE_SWEEP))
//.setKbdBackground(new
////BitmapCachedGradBack(0xff000000,0xff00bbff).setCorners(0, 0).setGap(0))
//,
	public static void setDefaultDesign() {
		arDesign = null;
		arDesign = new KbdDesign[] {
				// Стандартный дизайн
				new KbdDesign(R.string.kbd_design_standard, 0, Color.WHITE, 0, 0).setCopyItemColorsDesign(),
				// iPhone
				new KbdDesign(R.string.kbd_design_iphone, 0, Color.BLACK, 0, DF_BOLD | DF_BIG_GAP).setCopyItemColorsDesign()
						.setKeysBackground(skinIPhoneKey()).setCopyItemColorsDesign()
						.setKbdBackground(new BitmapCachedGradBack(true, 0xff9199a3, 0xff444e5c).setCorners(0, 0).setGap(0))
						.setCopyItemColorsDesign(),
				// Украина
				new KbdDesign(R.string.kbd_design_ukraine, 0, Color.LTGRAY, 0, DF_BOLD).setCopyItemColorsDesign()
						.setKeysBackground(new BitmapCachedGradBack(true, st.getSkinColorAlpha(0xff060a6c),
								st.getSkinColorAlpha(0xff1199af)).setGradType(GradBack.GRADIENT_TYPE_SWEEP))
						.setCopyItemColorsDesign()
						.setKbdBackground(
								new BitmapCachedGradBack(true, Color.CYAN, Color.YELLOW).setGap(0).setCorners(0, 0))
						.setCopyItemColorsDesign(),
				// Старая Украина
				new KbdDesign(R.string.kbd_design_ukraine_old, 0, Color.YELLOW, 0, DF_BOLD).setCopyItemColorsDesign()
						.setKeysBackground(new BitmapCachedGradBack(true, st.getSkinColorAlpha(0xff060a6c),
								st.getSkinColorAlpha(0xff1199af)).setGradType(GradBack.GRADIENT_TYPE_SWEEP))
						.setCopyItemColorsDesign()
						.setKbdBackground(
								new BitmapCachedGradBack(true, Color.CYAN, Color.YELLOW).setGap(0).setCorners(0, 0))
						.setCopyItemColorsDesign(),
				// скины из функций
				skinHTCDesign(), 
				skinMagentaDesign(), 
				skinDarkDesign(), 
				skinSolidDesign(),
				// Шоколад
				new KbdDesign(R.string.kbd_design_chokolate, 0, 0xffffffc0, 0, DF_BOLD).setCopyItemColorsDesign()
						.setKeysBackground(new BitmapCachedGradBack(true, st.getSkinColorAlpha(0xff75412b),
								st.getSkinColorAlpha(0xffc16643)).setGradType(GradBack.GRADIENT_TYPE_SWEEP))
						.setCopyItemColorsDesign()
						.setKbdBackground(new BitmapCachedGradBack(true, 0xff400000, GradBack.DEFAULT_COLOR).setGap(0)
								.setCorners(0, 0)).setCopyItemColorsDesign(),
				// красно-синий
				new KbdDesign(R.string.kbd_design_red, 0, Color.WHITE, 0, 0).setCopyItemColorsDesign()
						.setKeysBackground(new BitmapCachedGradBack(true, st.getSkinColorAlpha(0xff800000),
								st.getSkinColorAlpha(0xffc00000)).setGradType(GradBack.GRADIENT_TYPE_SWEEP))
						.setCopyItemColorsDesign()
						.setKbdBackground(new BitmapCachedGradBack(true, 0xff000000, 0xff00bbff).setCorners(0, 0).setGap(0))
						.setCopyItemColorsDesign(),
				// из assets
				new KbdDesign(R.string.design_keylifes1, "blackandwhite.skin"),
				new KbdDesign(R.string.design_thepop3250_1, "thepop3250_1.skin"),
				new KbdDesign(R.string.design_thepop3250_2, "blue.skin"),
				new KbdDesign(R.string.design_thepop3250_3, "neon.skin"),
				new KbdDesign(R.string.design_rusrespect1, "Notebook-2_by_RusRespect.skin"),
				new KbdDesign(R.string.design_eliz1, "cream_2.skin"),
				new KbdDesign(R.string.design_eliz2, "apple.skin"),
				new KbdDesign(R.string.design_eliz3, "brown_1.skin"), new KbdDesign(R.string.design_eliz4, "gold.skin"),
				new KbdDesign(R.string.design_eliz5, "violet2.skin"), new KbdDesign(R.string.design_eliz6, "drop.skin"),
				new KbdDesign(R.string.design_eliz7, "cherry.skin"),
				new KbdDesign(R.string.design_eliz8, "grey_2.skin"),
				new KbdDesign(R.string.design_eliz9, "md_blue.skin"),
				new KbdDesign(R.string.design_eliz10, "black_robo.skin"),
				new KbdDesign(R.string.design_eliz11, "plum.skin"),
				new KbdDesign(R.string.design_schenee, "Chocolate_by_Schnee-Leopard.skin"),
				new KbdDesign(R.string.design_newportstyle, "NewPortStyle.skin"),
				new KbdDesign(R.string.design_glamor, "glamor.skin"),
				new KbdDesign(R.string.design_nad1, "Blue_and_gray.skin"),
				new KbdDesign(R.string.design_nad2, "Brown&pink&yellow.skin"),
				new KbdDesign(R.string.design_BOGDANPRIME1, "Gray_Soft_Keys_by_BOGDANPRIME.skin"), 
				new KbdDesign(R.string.design_Proctocrator1, "PaleOrangeAndTwilightGreen_v2.skin"), 
				new KbdDesign(R.string.design_Proctocrator2, "e-ink_light.skin"), 
				new KbdDesign(R.string.design_Proctocrator3, "e-ink_dark.skin"), 
				new KbdDesign(R.string.design_unknown1, "cherry2.skin"), 
			};
	}
	public static void setDefaultKeybrd() {
		arKbd = null;
		arKbd = new Keybrd[] {
				new Keybrd("en_psevdoT9_MWcorp_v109", R.string.kbd_psevdo_t9_mwcorp),
				new Keybrd("en_T9_MWcorp_v100", R.string.kbd_t9_mwcorp),
				new Keybrd("en_landscape_MWcorp_v100", R.string.kbd_landscape_mwcorp),
				new Keybrd("en_qwerty", R.string.kbd_name_qwerty), 
				new Keybrd("en_wide", R.string.kbd_name_wide),
				new Keybrd("en_qwerty_tablet", R.string.kbd_name_qwerty_tablet),
				new Keybrd("en_qwerty_Round", R.string.kbd_name_round), 
				new Keybrd("en_dvorak", R.string.kbd_name_dvorak),
				new Keybrd("en_qwerty_5_row", R.string.kbd_name_5_rows),
				new Keybrd("en_two_sides", R.string.kbd_name_2_sides),

				new Keybrd("ru_psevdoT9_MWcorp_v109", R.string.kbd_psevdo_t9_mwcorp),
				new Keybrd("ru_T9_MWcorp_v100", R.string.kbd_t9_mwcorp),
				new Keybrd("ru_landscape_MWcorp_v100", R.string.kbd_landscape_mwcorp),
				new Keybrd("ru_qwerty", R.string.kbd_name_qwerty), 
				new Keybrd("ru_wide", R.string.kbd_name_wide),
				new Keybrd("ru_qwerty_tablet", R.string.kbd_name_qwerty_tablet),
				new Keybrd("ru_qwerty_Round", R.string.kbd_name_round), 
				new Keybrd("ru_dvorak", R.string.kbd_name_dvorak),
				new Keybrd("ru_two_sides", R.string.kbd_name_2_sides),

				new Keybrd("fa_wide", R.string.kbd_name_wide),

				new Keybrd("hy_qwerty", R.string.kbd_name_qwerty),
				new Keybrd("hy_qwerty_Hayastan_64", R.string.kbd_name_qwerty_hyastan64),

				new Keybrd("lv_qwerty", R.string.kbd_name_qwerty_rusrespect),
				new Keybrd("lv_qwerty_tablet", R.string.kbd_name_qwerty_tablet),
				new Keybrd("lv_wide", R.string.kbd_name_wide),

				new Keybrd("be_qwerty", R.string.kbd_name_qwerty), 
				new Keybrd("be_wide", R.string.kbd_name_wide),

				new Keybrd("uk_qwerty", R.string.kbd_name_qwerty), 
				new Keybrd("uk_wide", R.string.kbd_name_wide),
				new Keybrd("uk_qwerty_Round", R.string.kbd_name_round),

				new Keybrd("fr_qwerty", R.string.kbd_name_qwerty), 
				new Keybrd("fr_wide", R.string.kbd_name_wide),

				new Keybrd("it_qwerty", R.string.kbd_name_qwerty), 
				new Keybrd("it_wide", R.string.kbd_name_wide),

				new Keybrd("de_qwerty", R.string.kbd_name_qwerty), 
				new Keybrd("de_wide", R.string.kbd_name_wide),

				new Keybrd("sv_qwerty", R.string.kbd_name_qwerty), 
				new Keybrd("sv_wide", R.string.kbd_name_wide),

				new Keybrd("es_wide", R.string.kbd_name_wide),
				new Keybrd("es_psevdoT9_MWcorp_v100", R.string.kbd_psevdo_t9_mwcorp),

				new Keybrd("eo_wide", R.string.kbd_name_wide),
				new Keybrd("eo_psevdoT9_MWcorp_v100", R.string.kbd_psevdo_t9_mwcorp),

				new Keybrd("et_wide", R.string.kbd_name_wide),
				new Keybrd("et_psevdoT9_MWcorp_v100", R.string.kbd_psevdo_t9_mwcorp),

				new Keybrd("sv_qwerty_tablet", R.string.kbd_name_qwerty_tablet),

				new Keybrd("tr_qwerty", R.string.kbd_name_qwerty), 
				new Keybrd("tr_wide", R.string.kbd_name_wide),

				new Keybrd("tt_qwerty", R.string.kbd_name_qwerty), 
				new Keybrd("tt_wide", R.string.kbd_name_wide),

				new Keybrd("ba_qwerty", R.string.kbd_name_qwerty), 
				new Keybrd("ba_wide", R.string.kbd_name_wide),

				new Keybrd("he_qwerty", R.string.kbd_name_qwerty), 
				new Keybrd("he_wide", R.string.kbd_name_wide),

				new Keybrd("el_qwerty", R.string.kbd_name_qwerty), 
				new Keybrd("el_wide", R.string.kbd_name_wide),

				new Keybrd("ar_psevdoT9_MWcorp_v101", R.string.kbd_psevdo_t9_mwcorp),

				new Keybrd("ka_psevdoT9_MWcorp_v100", R.string.kbd_psevdo_t9_mwcorp),
				new Keybrd("ka_wide", R.string.kbd_name_wide),

				new Keybrd("pl_psevdoT9_MWcorp_v100", R.string.kbd_psevdo_t9_mwcorp),
				new Keybrd("pl_wide", R.string.kbd_name_wide),

				new Keybrd("no_psevdoT9_MWcorp_v100", R.string.kbd_psevdo_t9_mwcorp),
				new Keybrd("no_wide", R.string.kbd_name_wide),

				new Keybrd("id_psevdoT9_MWcorp_v100", R.string.kbd_psevdo_t9_mwcorp),
				new Keybrd("id_qwerty", R.string.kbd_name_qwerty), 
				new Keybrd("id_wide", R.string.kbd_name_wide),
				new Keybrd("id_qwerty_tablet", R.string.kbd_name_qwerty_tablet),

				new Keybrd("uz_wide", R.string.kbd_name_wide),

				new Keybrd("pt_psevdoT9_MWcorp_v100", R.string.kbd_psevdo_t9_mwcorp),
				new Keybrd("pt_wide", R.string.kbd_name_wide),

				new Keybrd("fi_wide", R.string.kbd_name_wide),

				new Keybrd("kk_qwerty_from_MWcorp_v1", R.string.kbd_name_mwcorp),
				new Keybrd("kk_10qwerty", R.string.kbd_name_qwerty_hyastan64),
				new Keybrd("kk_7qwerty", R.string.kbd_name_wide),

				new Keybrd("az_wide", R.string.kbd_name_wide),
				new Keybrd("az_psevdoT9_MWcorp_v100", R.string.kbd_psevdo_t9_mwcorp),
				new Keybrd("az_qwerty", R.string.kbd_name_qwerty_eliz69),

				new Keybrd("symbol_standard", R.string.lang_symbol), 
				new Keybrd("symbol_Round", R.string.kbd_name_round),
				new Keybrd("symbol_edtext", R.string.kbd_name_sym_edit), 
				new Keybrd("symbol_wide", R.string.kbd_name_wide),

				new Keybrd("symbol2_standard", R.string.lang_symbol_shift),

				new Keybrd("edittext_MWcorp", R.string.lang_edittext2),
				new Keybrd("edittext_standard", R.string.lang_edittext),
				new Keybrd("edittext_Round", R.string.kbd_name_round),
				new Keybrd("edittext_4row_8inch_MWcorp", R.string.lang_edittext3),

				new Keybrd("smile_MaxQwertyKeys Emoji_my-3", R.string.smiles_MaxQwertyKeys),
				new Keybrd("smile_standard", R.string.lang_text_smiles), 
				new Keybrd("smile_MWcorp_v100", R.string.smiles_MWcorp),
				new Keybrd("smile_scroll_android7", R.string.smiles_android7),
//				new Keybrd("smile_scroll_example_andr6", R.string.about),
				
				new Keybrd("num_kbd", R.string.lang_numbers), 
				new Keybrd("num_5row", R.string.kbd_name_5_rows),
				new Keybrd("num_Round", R.string.kbd_name_round),

				new Keybrd("calc_from_MWcorp_v102", R.string.kbd_calc),
				new Keybrd("calc_scient_MWcorp_v100", R.string.kbd_calc_saent),
				new Keybrd("calc_prog_MWcorp_v100", R.string.kbd_calc_prog), 
		};
	}

}