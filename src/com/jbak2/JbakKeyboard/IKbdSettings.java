package com.jbak2.JbakKeyboard;

/** Константы для настроек клавиатуры */
public interface IKbdSettings
{
	/** Ключ, позиция кнопки Выгрузить из памяти */
	public static final String PREF_KEY_UNLOAD_BUTTON_POS = "unload_button_pos";
	/** Ключ, прозрачность кнопки Выгрузить из памяти */
	public static final String PREF_KEY_UNLOAD_BUTTON_ALPHA = "unload_button_alpha";
	/** Ключ, цвет кнопки Выгрузить из памяти */
	public static final String PREF_KEY_UNLOAD_BUTTON_COLOR = "unload_button_color";
	/** Ключ, тема интерфейса программы*/
	public static final String PREF_KEY_THEME_INTERFACE_APPLICATION = "theme_interface_application";
	/** Ключ, (основная настройка) включить шрифт клавиатуры на клавишах*/
	public static final String PREF_KEY_FONT_KBD_ON_KEY = "set_font_kbd";
	/** Ключ что показывать в настройках, на настройке Шрифт клавиатуры*/
	public static final String PREF_KEY_VIEW_FONT_KBD_ON_KEY = "pref_set_font_kbd";
	/** Ключ, дизайн элемента минус/плюс */
	public static final String PREF_KEY_IE_DESIGN = "ie_design";
	/** Ключ, тип пикера - круг или линейный */
	public static final String PREF_KEY_TYPE_COLOR_PICKER = "type_color_picker";
	/** Ключ, режим отладки */
	public static final String PREF_KEY_DEBUG_MODE= "debug_mode";
	/** Ключ, проверка обновлений */
	public static final String PREF_KEY_CHECK_UPD_APP = "check_upd_app";
	/** Под какую руку выводить кнопки тулбара в активности для вывода текстов */
	public static final String STA_TOOLBAR_LEFTRIGHT_TOOLBAR = "lh_rh_showact_toolbar";
	/** Цвета редактора в активности для вывода текстов */
	public static final String STA_COLOR_EDITTEXT = "sta_color_edittext";
	/** Пиктограмма на ентере */
	public static final String PREF_ENTER_PICT = "enter_pict";
	//
	public static final String PREF_AC_REPLACE_SEPARATOR_SYMBOL= "ac_replace_separator_symbol";
	// последняя папка для файлового менеджера
	public static final String FILE_EXPLORER_LAST_DIR= "fe_last_dir";
	// прозрачность скина клавиатуры
	public static final String KBD_BACK_ALPHA= "kbd_background_alpha";
	// фоновая картинка клавиатуры
	public static final String KBD_BACK_PICTURE= "kbd_background_pict";
	// какой язык выводить по умолчанию в редактированиипользовательского 
	// словаря
	public static final String PREF_EUV_LANG_DEF= "euv_lang_def";

	// вызов клавиатуры через шторку
	public static final String PREF_SHOW_KBD_NOTIF = "show_kbd_notif";
	// маленькая клавиатура 
	public static final String PREF_MINI_KBD_ITS = "mini_kbd_its";
	// сообщение о копировании
	public static final String PREF_COPY_TOAST= "copy_toast";
	// язык интерфейса сайта транслятора
	public static final String PREF_TRANSLATE_INTERFACE= "translate_interfase";
    /** Ключ, String,  размер кнопок маленькой клавиатуры*/
    public static final String PREF_KEY_MINI_KBD_BTN_SIZE= "minikbd_btn_size";
    public static final String PREF_KEY_MINI_KBD_BTN_TEXT_SIZE= "minikbd_btn_text_size";
	
	public static final String PREF_KEY_MAINMENU_NEW = "mainmenu_new";
	public static final String PREF_KEY_LANG_APP = "lang_app";
	public static final String PREF_KEY_DESC_LANG_KBD = "lang_desckbd";
	public static final String PREF_KEY_LANG_HELP_SPECINSTRUCTION = "lang_help_specinstruction";
	public static final String PREF_KEY_SOUND_EFFECT = "key_sound_effect";
// другие приложения в маркете
	public static final String ALL_APP_INMARKET = "https://play.google.com/store/apps/developer?id=Михаил+Вязенкин";
//-------------------------------------------------------------------    
// Ключи для SharedPreferences
//-------------------------------------------------------------------    
// ключ установки цвета фона активности setKbdAct
	public static final String SET_KBD_BACK_COL = "kbd_act_back_col";
	/** тип разметки для выбора цветов элементов автодополнения */
	public static final String AC_COL_TYPE_LAYOUT= "ac_col_type_layout";
	
// (пока не используются!)ключи для настроек цветов автодополнения 
//	public static final String AC_COL_MAIN_START_BG = "ac_color_main_start_grad_bg";
//	public static final String AC_COL_MAIN_END_BG = "ac_color_main_end_grad_bg";
	public static final String AC_COL_MAIN_BG = "ac_color_main_bg";
	public static final String AC_COL_ADD_BG = "ac_color_addvocab_bg";
	public static final String AC_COL_ADD_T = "ac_color_addvocab_t";
	public static final String AC_COL_KEYCODE_BG = "ac_color_keycode_bg";
	public static final String AC_COL_KEYCODE_T = "ac_color_keycode_t";
	public static final String AC_COL_COUNTER_BG = "ac_color_counter_bg";
	public static final String AC_COL_COUNTER_T = "ac_color_counter_t";
	public static final String AC_COL_FORCIBLY_BG = "ac_color_forcibly_bg";
	public static final String AC_COL_FORCIBLY_T = "ac_color_forcibly_t";
	public static final String AC_COL_WORD_BG = "ac_color_word_bg";
	public static final String AC_COL_WORD_T = "ac_color_word_t";
	public static final String AC_COL_ARROWDOWN_BG = "ac_color_arrowdown_bg";
	public static final String AC_COL_ARROWDOWN_T = "ac_color_arrowdown_t";
	public static final String AC_COL_CALCMENU_BG = "ac_color_calcmenu_bg";
	public static final String AC_COL_CALCMENU_T = "ac_color_calcmenu_t";
	public static final String AC_COL_CALCIND_BG = "ac_color_calcind_bg";
	public static final String AC_COL_CALCIND_T = "ac_color_calcind_t";
	
	public static final int AC_COLDEF_MAIN_BG = 0x00000000;
	public static final int AC_COLDEF_ADD_BG = 0xfff7c5b4;
	public static final int AC_COLDEF_ADD_T = 0xff000000;
	public static final int AC_COLDEF_KEYCODE_BG = 0xff00ff00;
	public static final int AC_COLDEF_KEYCODE_T = 0xff000000;
	public static final int AC_COLDEF_COUNTER_BG = 0xffff0000;
	public static final int AC_COLDEF_COUNTER_T = 0xff000000;
	public static final int AC_COLDEF_FORCIBLY_BG = 0xff00ffff;
	public static final int AC_COLDEF_FORCIBLY_T = 0xff000000;
	public static final int AC_COLDEF_WORD_BG = 0xfff6ffff;
	public static final int AC_COLDEF_WORD_T = 0xff000000;
	public static final int AC_COLDEF_ARROWDOWN_BG = 0xfff7c5b4;
	public static final int AC_COLDEF_ARROWDOWN_T = 0xff000000; 
//			0xff00ff00;
	public static final int AC_COLDEF_CALCMENU_BG = 0xff0000ff;
	public static final int AC_COLDEF_CALCMENU_T = 0xffffffff;
	public static final int AC_COLDEF_CALCIND_BG = 0xff000000;
	public static final int AC_COLDEF_CALCIND_T = 0xff00ff00;
	
// ключ, включения обучаемого словаря
		public static final String STUDENT_DICT = "ac_student_dict";
/** ключ, вывод подсказок в сокращённом виде */
		public static final String AC_ABBREVIATED_DICT = "ac_abbreviated_dict";
// ключ, расширенного обучения словаря
		public static final String EXTENDED_STUDENT_DICT = "ac_student_ext_dict";
// количество слов в выпадающем списке из автодополнения
		public static final String AC_LIST_VALUE = "ac_list_value";
// высота окна автодополнения
		public static final String PREF_AC_HEIGHT = "pref_ac_height";
///** @deprecated рисовать окно автодополнения как SUB_PANEL */
//		public static final String PREF_AC_SUB_PANEL = "pref_ac_sub_panel";
/** тип отрисовки окна автодополнения <br>
 *  Заменяет константу PREF_AC_SUB_PANEL */		
		public static final String PREF_AC_WINDOW_TYPE = "pref_ac_window_type";
// сортировка выпадающего списка автодопа
		public static final String AC_SORT_DROPDOWNLIST_ALPHABETICALLY = "ac_list_dropdown";
// имя файла для вывода по умолчанию
	   public static final String PREF_VIEW_DESC = "desc_view";
	/** ключ, Как пользоваться клавиатурой */    
	   public static final String HOW_TO_USE_KEYBOARD = "annotation";

	/** ключ, для arrow_down из автодополнения */    
	   public static final String SET_AC_PLACE_ARROW_DOWN = "ac_place_arrow_down";
	/** поведение стрелок */    
	   public static final String SET_ARROW_KEY = "set_arrow_key";
	/** ключи для строк для жестов "дополнительные символы" */    
	   public static final String SET_STR_GESTURE_DOPSYMB = "g_str_additional1";
	// длина жеста
	   public static final String SET_GESTURE_LENGTH = "g_length";
	// скорость жеста
	   public static final String SET_GESTURE_VELOCITY = "g_velocity";
		/** цвет фона окошка нажатых клавиш*/    
	   public static final String POP_COLOR_R = "pop_color_r";
	   public static final String POP_COLOR_G = "pop_color_g";
	   public static final String POP_COLOR_B = "pop_color_b";
	   /** цвет символов в окне нажатых клавиш*/    
	   public static final String POP_COLOR_TEXT_R = "pop_txt_color_r";
	   public static final String POP_COLOR_TEXT_G = "pop_txt_color_g";
	   public static final String POP_COLOR_TEXT_B = "pop_txt_color_b";
	   /** ключ размер текста кнопок меню*/    
	   public static final String MM_BTN_SIZE = "mm_textsize_btn";
	   /** ключ размер текста служебных кнопок меню*/    
	   public static final String MM_BTN_OFF_SIZE = "mm_textsize_btn_off";
	   
//  Ключ String, хранящий путь к папке для горячих клавишей
    public static final String PREF_KEY_HOT_DIR = "hot_dir";
/** @deprecated Ключ, boolean, хранящий значение "включить/отключить просмотр клавиш" */    
    public static final String PREF_KEY_PREVIEW = "ch_preview";

    public static final String PREF_KEY_RUNAPP_COUNT = "runapp_count";
    public static final String PREF_KEY_RUNAPP = "runapp";
/** Ключ, boolean, показывать или нет счетчик нажатий клавиш */
    public static final String PREF_KEY_USE_COUNTER = "counter";
//  Ключ, boolean, удалять ли пробел     
    public static final String PREF_KEY_DEL_SPACE = "del_space";
//  Символы перед которыми удаляется пробел (если он есть)
    public static final String PREF_KEY_DEL_SPACE_SYMBOL = "del_space_symbols";
//  Ключ, boolean, показывания кода нажатой клавиши    
    public static final String PREF_KEYCODE = "keycode";
//Ключ, int, хранящий время задержки перед вводом символа    
//    public static final String PREF_DELAY_SYMB = "delay_symb";
//Ключ, boolean, указывающий показывать ли путь к текущей папке в шаблонах,     
    public static final String PREF_TEMPLATE_PATH= "set_tpl_path";
	// ивертировать кнопки в редакторе шаблонов
	public static final String PREF_TEMPLATE_INVERT_BUTTON_TOOLBAR= "tpl_invert_button_toolbar";
	// прижать вправо кнопки в панели кнопок в редакторе шаблонов
	public static final String PREF_TEMPLATE_RIGHT_BUTTON_TOOLBAR= "tpl_right_button_toolbar";
/** Ключ, boolean, вставлять ли пробел после вставки слова из автодополнения" */    
    public static final String PREF_AC_SPACE = "ac_space";
    /** Ключ, String, хранящий слова по умолчанию*/    
    public static final String PREF_AC_DEFKEY = "ac_defkey";
    /** Ключ, String, хранящий тип показа предпросмотра клавиши (0-нет, 1- над клавишами, 2 - над клавиатурой)*/    
    public static final String PREF_KEY_PREVIEW_TYPE = "key_preview";
    /** Ключ, String, хранящий размер окна нажатых клавиш*/    
    public static final String PREF_KEY_PREVIEW_WINSIZE = "popup_win_size";
/** Ключ, int ,хранящий код последней используемой клавиатуры */    
    public static final String PREF_KEY_LAST_LANG = "lastLng";
//    /** @deprecated Ключ, int, хранящий высоту клавиш в портретном режиме */    
//    public static final String PREF_KEY_HEIGHT_PORTRAIT = "kh";
//    /** @deprecated Ключ, int, хранящий высоту клавиш в ландшафтном режиме */    
//    public static final String PREF_KEY_HEIGHT_LANDSCAPE = "khl";
    /** Ключ, int, хранящий положение клавиатуры в портретном режиме */    
    public static final String PREF_KEYBOARD_POS_PORT = "kbd_pos_p";
    /** Ключ, int, хранящий положение клавиатуры в ландшафтном режиме */    
    public static final String PREF_KEYBOARD_POS_LAND = "kbd_pos_l";
    /** Ключ, float, хранящий высоту клавиш в портретном режиме */    
    public static final String PREF_KEY_HEIGHT_PORTRAIT_PERC = "kh_p";
    public static final String PREF_KEY_HEIGHT_LANDSCAPE_PERC = "kh_l";
/** Ключ, int, хранящий высоту клавиш по умолчанию, на всякий случай */    
    public static final String PREF_KEY_DEF_HEIGHT = "dh";
    /** Ключ, String, хранящий порядок переключения языков */    
    public static final String PREF_KEY_LANGS = "langs";
///** @deprecated Ключ, boolean, хранящий настройку вибро при коротком нажатии 
// *  Заменено на PREF_KEY_VIBRO_SHORT_TYPE */
//    public static final String PREF_KEY_VIBRO_SHORT_KEY = "vs";
/** Ключ, String, тип вибро при коротком нажатии. "0" - нет, "1" - при отпускании, "2" - при нажатии*/    
    public static final String PREF_KEY_USE_SHORT_VIBRO = "vibro_short";
    /** Ключ, boolean, хранящий настройку вибро при коротком нажатии */    
    public static final String PREF_KEY_USE_LONG_VIBRO = "vl";
    /** Ключ, String, интервал вибро для повтора клавиши */
    public static final String PREF_KEY_USE_REPEAT_VIBRO = "vibro_repeat";
/** Ключ, String, интервал вибро для короткого нажатия*/
    public static final String PREF_KEY_VIBRO_SHORT_DURATION = "vibro_short_duration";
    /** Ключ, String, интервал вибро для удержания*/
    public static final String PREF_KEY_VIBRO_LONG_DURATION = "vibro_long_duration";
    /** Ключ, String, интервал вибро для повтора клавиши */
    public static final String PREF_KEY_VIBRO_REPEAT_DURATION = "vibro_repeat_duration";
    /** Ключ, boolean, вибрация клавиш в тихом режиме */
    public static final String PREF_KEY_VIBRO_IN_SILENT_MODE="vibro_silent";

/** Ключ, boolean, хранящий настройку проигрывания звуков */    
    public static final String PREF_KEY_SOUND = "sound";
    
/** Ключ, int, хранящий настройку громкости звуков */    
    public static final String PREF_KEY_SOUND_VOLUME = "sound_volume";

/** Ключ, String, хранящий путь к клавиатуре для выбраного языка в портрете
 *  Полный ключ выглядит как PREF_KEY_LANG_KBD+"en".*/    
    public static final String PREF_KEY_LANG_KBD_PORTRAIT = "kp_path_";
/** Ключ, String, хранящий путь к клавиатуре для выбраного языка в ландшафте
 *  Полный ключ выглядит как PREF_KEY_LANG_KBD+"en".*/
    public static final String PREF_KEY_LANG_KBD_LANDSCAPE = "kl_path_";
///**@deprecated
// *  Ключ, int, хранящий индекс текущего скина
// *  Уже не юзается. Но его надо выпилить со старых версий */    
//    public static final String PREF_KEY_KBD_SKIN = "kbd_skin";
/** Ключ, String. Для сторонних скинов - путь к скину, для встроенных - индекс в виде строки?*/    
    public static final String PREF_KEY_KBD_SKIN_PATH = "kbd_skin_path";

// смена регистра
/** Ключ, boolean - глобальный ключ, включающий/отключающий автоматическую смену регистра */    
    public static final String PREF_KEY_AUTO_CASE = "up_sentence";
/** Ключ, boolean - включает/отключает смену регистра после набора символов */    
    public static final String PREF_KEY_UP_AFTER_SYMBOLS = "up_after_symbols";
/** Ключ, String, список символов для перехода в верхний регистр */    
    public static final String PREF_KEY_SENTENCE_ENDS = "sentence_ends";
/** Ключ, boolean - смена регистра только после символа из PREF_KEY_SENTENCE_ENDS и следующего за ним пробела*/    
    public static final String PREF_KEY_UPERCASE_AFTER_SPACE = "space_after_sentence";
/** Ключ, boolean - добавление пробела после конца предложения */    
    public static final String PREF_KEY_SENTENCE_SPACE = "space_sentence";
/** Ключ, boolean - переход в верхний регистр в пустом поле */    
    public static final String PREF_KEY_EMPTY_UPPERCASE = "up_empty";
    /** Ключ, String - набор символов, после которых вставляется пробел*/    
    public static final String PREF_KEY_ADD_SPACE_SYMBOLS = "space_symbols";
    /** Ключ, String - добавлять ли пробел перед некоторыми символами */    
    public static final String PREF_KEY_ADD_SPACE_BEFORE_SENTENCE = "add_space_before_sentence";
    /** Ключ, String - набор символов, перед которыми вставляется пробел*/    
    public static final String PREF_KEY_ADD_SPACE_BEFORE_SYMBOLS = "add_space_before_symbols";
    /** Ключ, String, тип ландшафтного редактора.  Одна из констант PREF_VAL_EDIT_TYPE_ , в видк строки */
    public static final String PREF_KEY_LANSCAPE_TYPE = "landscape_type";
    /** Ключ, String, тип портретного редактора.  Одна из констант PREF_VAL_EDIT_TYPE_ , в видк строки */
    public static final String PREF_KEY_PORTRAIT_TYPE = "portrait_type";
    /** Ключ, String, настройки редактора ExtractedText*/
    public static final String PREF_KEY_EDIT_SETTINGS = "edit_set";
    /** Ключ, String, настройка основного шрифта */
    public static final String PREF_KEY_MAIN_FONT = "pMainFont";
    /** Ключ, String, настройка шрифта дополнительных символов */
    public static final String PREF_KEY_SECOND_FONT = "pSecondFont";
    /** Ключ, String, настройка шрифта меток */
    public static final String PREF_KEY_LABEL_FONT = "pLabelFont";
    /** Ключ, String, настройка шрифта панели автодополнения*/
    public static final String PREF_KEY_FONT_PANEL_AUTOCOMPLETE = "pAcFont";
    /** Ключ, String, варианты клавиши Shift, строка со значениями:0 - 3-позиц., 1 - normal/shift, 2 - normal/capslock */
    public static final String PREF_KEY_SHIFT_STATE = "shift_state";
	public static final String ENTER_STATE = "enter_state";
    /** Ключ, String, задержка нажатий клавиш в ms (хранится в виде строки, по умолчанию - 0)*/
    public static final String PREF_KEY_REPEAT_DELAY = "key_repeat";
    /** Ключ, String, синхронизация записей из мультибуфера */
    public static final String PREF_KEY_CLIPBRD_SYNC = "clipboard_sync";
    /** Ключ, String, период синхронизации записей из мультибуфера */
    public static final String PREF_KEY_CLIPBRD_SYNC_DUR = "clipboard_sync_dur";
    /** Ключ, String, макс. размер сохраняемой записи для синхронизации из мультибуфера */
    public static final String PREF_KEY_CLIPBRD_SYNC_SIZE = "clipboard_sync_size";
    /** Ключ, String, количество сохраняемых записей в синхронизации из мультибуфера */
    public static final String PREF_KEY_CLIPBRD_SYNC_CNT = "clipboard_sync_cnt";
    /** Ключ, String, показывать ли кнопку синхронизации в мультибуфере */
    public static final String PREF_KEY_CLIPBRD_BTN_SYNC_SHOW = "clipboard_btn_sync_show";
    /** Ключ, String, перезаписывать файл*/
    public static final String PREF_KEY_CLIPBRD_SYNC_CREATE_FILE = "clipboard_sync_create";
    /** Ключ, String, подавлять сообщение о синхронизации*/
    public static final String PREF_KEY_CLIPBRD_SYNC_MSG_SHOW = "clipboard_sync_msg_show";
    /** Ключ, String, вставка ентера после вставки записи из мультибуфера */
    public static final String PREF_KEY_CLIPBRD_ENTER_AFTER_PASTE = "clipboard_clipboard_enter_after_paste";
    /** Ключ, String, количество записей в буфере обмена (преобразуется в int)*/
    public static final String PREF_KEY_CLIPBRD_SIZE = "clipboard_size";
    /** Ключ, String, показывать размер каждой записи в мультибуфере */
    public static final String PREF_KEY_SHOW_CLIPBOARD_SIZE = "show_size_record_clipboard";
    /** Ключ, int, интервал длинного нажатия в ms*/
    public static final String PREF_KEY_LONG_PRESS_INTERVAL = "int_long_press";
    /** Ключ, int, интервал первого повтора в ms*/
    public static final String PREF_KEY_REPEAT_FIRST_INTERVAL = "int_first_repeat";
    /** Ключ, int, интервал следующих повторов в ms*/
    public static final String PREF_KEY_REPEAT_NEXT_INTERVAL = "int_next_repeat";
    /** Ключ, int, минимальный интервал между нажатиями клавиш в ms*/
    public static final String PREF_KEY_MINIMAL_PRESS_INTERVAL = "int_min_press";
    
    /** Ключ, none, ключ для пункта сохранения настроек*/
    public static final String PREF_KEY_SAVE = "save";
    /** Ключ, none, ключ для пункта загрузки настроек*/
    public static final String PREF_KEY_LOAD = "load";
    
// ЖЕСТЫ

    /** Ключ, boolean, true - включает использование жестов */
    public static final String PREF_KEY_USE_GESTURES = "use_gestures";
    /** Ключ, String, количество своих жестов*/
    public static final String PREF_KEY_GESTURE_CNT = "gesture_count";
    public static final String PREF_KEY_GESTURE_KEY = "gesture_key";
    public static final String PREF_KEY_GESTURE_DIR = "gesture_dir";
    public static final String PREF_KEY_GESTURE_ID = "gesture_id";
    public static final String PREF_KEY_GESTURE_ACT = "gesture_act";
    /** Ключ, String, команда для жеста влево (код команды)*/
    public static final String PREF_KEY_GESTURE_LEFT = "g_left";
    /** Ключ, String, команда для жеста вправо (код команды)*/
    public static final String PREF_KEY_GESTURE_RIGHT = "g_right";
    /** Ключ, String, команда для жеста вверх (код команды)*/
    public static final String PREF_KEY_GESTURE_UP = "g_up";
    /** Ключ, String, команда для жеста вниз (код команды)*/
    public static final String PREF_KEY_GESTURE_DOWN = "g_down";
    /** Ключ, String, команда для жеста влево от пробела (код команды)*/
    public static final String PREF_KEY_GESTURE_SPACE_LEFT = "g_space_left";
    /** Ключ, String, команда для жеста влево от пробела (код команды)*/
    public static final String PREF_KEY_GESTURE_SPACE_RIGHT = "g_space_right";
    /** Ключ, String, команда для жеста влево от пробела (код команды)*/
    public static final String PREF_KEY_GESTURE_SPACE_UP = "g_space_up";
    /** Ключ, String, команда для жеста влево от пробела (код команды)*/
    public static final String PREF_KEY_GESTURE_SPACE_DOWN = "g_space_down";
    
    // ключ, включен или выключен показ автодополнения
    public static final String PREF_KEY_VIEW_AC_PLACE = "acplace_view";
    /** Ключ, String, где показывать автодополнения */
    public static final String PREF_KEY_AC_PLACE = "ac_place";
    /** Ключ, boolean, true - использовать автоисправление */
    public static final String PREF_KEY_AC_AUTOCORRECT = "ac_autocorrect";
    /** Ключ, int, использование клавиш громкости для управления курсором. 0:нет, 1:+ влево, - вправо, 2: - влево, + вправо*/
    public static final String PREF_KEY_USE_VOLUME_KEYS = "use_volume_keys";
    /** Ключ, int, вертикальная коррекция в портретном режиме */
    public static final String PREF_KEY_CORR_PORTRAIT = "pref_vertcorr_p";
    /** Ключ, int, вертикальная коррекция в ландшафтном режиме */
    public static final String PREF_KEY_CORR_LANDSCAPE = "pref_vertcorr_l";
    
    // сокращения для уменьшения занимаемой памяти
    //public static final String DOT_SRING = ".";
    //public static final String COLON_SRING = ":";
    public static final String EXT_XML = "xml";
    public static final String EXT_SKIN = "skin";
    public static final String SETTINGS_BACKUP_FILE= "settings_backup"+'.'+EXT_XML;
    /** Строковый префикс для кнопки, где метка для длинного нажатия является иконкой */
    public static final String DRW_PREFIX = "d_"; 
/** Тэг для записи в logcat*/
    public static final String TAG = "JBK2";
// строковые константы для уменьшения объёма занимаемой памяти
    public static final String STR_UTF8 = "UTF-8";
    public static final String STR_NULL = "";
    public static final String STR_SPACE = " ";
    public static final String STR_ZERO = "0";
    public static final String STR_ONE = "1";
    public static final String STR_LF = "\n";
    public static final String STR_16FORMAT = "0x%08x";
    public static final String STR_10INPUT_DIGIT = "0123456789";
    public static final String STR_16INPUT_DIGIT = "0123456789#xabcdefABCDEF";
	public static final String STR_3TIRE = "---";
	public static final String STR_UNDERSCORING = "_";
    public static final String STR_EQALLY = "=";
    public static final String STR_ERROR = "error";
    public static final String STR_PREFIX = "$[";
    public static final String STR_PREFIX_LINE = "$l#";
    public static final String STR_PREFIX_END_LINE = "$el#";
    public static final String STR_PREFIX_FONT = "$f#";
    public static final String STR_PREFIX_SELECTED_TEXT = "$st#";
    public static final String STR_PREFIX_COPY_TEXT = "$ct#";
    public static final String STR_COMMA = ",";
    public static final String STR_POINT = ".";
    public static final String STR_SLASH= "/";
    public static final String STR_COLON= ":";
	public static final String STR_COMMENT = "//";
    /** символы конца предложения */
    public static final String STR_END_SENTENCE= ".!?";

    
    public static final int PREF_VAL_EDIT_TYPE_DEFAULT = 0;
    public static final int PREF_VAL_EDIT_TYPE_FULLSCREEN = 1;
    public static final int PREF_VAL_EDIT_TYPE_NOT_FULLSCREEN = 2;
	//-------------------------------------------------------------------    
	/// параметры для popupcharacter v2
	//-------------------------------------------------------------------    
    
    // ключ, цвет фона окна
    public static final String PREF_KEY_PC2_WIN_BG = "pc2win_bg";
    public static final String PREF_KEY_PC2_WIN_BG_DEF = "#cc000000";

    public static final String PREF_KEY_PC2_WIN_FIX = "pc2win_fix";
    /** позиционирование под левую/правую руку */
    public static final String PREF_KEY_PC2_LR = "pc2win_lr";

    public static final String PREF_KEY_PC2_BTN_SIZE = "pc2btn_size";
    public static final String PREF_KEY_PC2_BTN_SIZE_DEF = "20";
    public static final String PREF_KEY_PC2_BTN_BG = "pc2btn_bg";
    public static final String PREF_KEY_PC2_BTN_BG_DEF = "#ff000000";
    public static final String PREF_KEY_PC2_BTN_TCOL = "pc2btn_textcolor";
    public static final String PREF_KEY_PC2_BTN_TCOL_DEF = "#ffffffff";

    public static final String PREF_KEY_PC2_BTNOFF_BG = "pc2btnoff_bg";
    public static final String PREF_KEY_PC2_BTNOFF_BG_DEF = "#ff444444";
    public static final String PREF_KEY_PC2_BTNOFF_SIZE = "pc2btnoff_size";
    public static final String PREF_KEY_PC2_BTNOFF_SIZE_DEF = "20";
    public static final String PREF_KEY_PC2_BTNOFF_TCOL = "pc2btnoff_textcolor";
    public static final String PREF_KEY_PC2_BTNOFF_TCOL_DEF = "#ffffffff";
    
	//-------------------------------------------------------------------    
	/// Константы для запуска настроечных активностей
	//-------------------------------------------------------------------    
    /** Значение для запуска {@link SetKbdActivity} - настройка высоты клавиш в портретном режиме */    
    public static final int SET_KEY_HEIGHT_PORTRAIT = 1;
/** Значение для запуска {@link SetKbdActivity} - настройка высоты клавиш в ландшафтном режиме */    
    public static final int SET_KEY_HEIGHT_LANDSCAPE =2;
/** Вызывает настройку переключения языков */    
    public static final int SET_LANGUAGES_SELECTION =3;
    // НИГДЕ НЕ ИСПОЛЬЗУЕТСЯ!!!
/** Вызывает настройку клавиш*/    
    public static final int SET_KEYS =4;
/** Вызывает настройку вида клавиатуры (обычный, для планешета...)*/    
    public static final int SET_SELECT_KEYBOARD = 5;
/** Вызывает настройку внешнего вида клавиатуры (стандартный, айфон..)*/    
    public static final int SET_SELECT_SKIN= 6;
    
    public static final int SET_KEY_CALIBRATE_PORTRAIT = 7;
    public static final int SET_KEY_CALIBRATE_LANDSCAPE = 8;

    public static final int SET_KEY_SKIN_CONSTRUKTOR = 9;

    //-------------------------------------------------------------------    
    /// Список клавиш редактирования текста
    //-------------------------------------------------------------------    
    public static final int TXT_ED_FIRST = -300;
/** Команда текстовой клавиатуры - переход в начало текста */    
    public static final int TXT_ED_START = -303;
/** Команда текстовой клавиатуры - переход в конец текста */    
    public static final int TXT_ED_FINISH = -304;
/** Команда текстовой клавиатуры - переход в начало абзаца */    
    public static final int TXT_ED_HOME = -305;
/** Команда текстовой клавиатуры - переход в конец абзаца */    
    public static final int TXT_ED_END = -306;
    /** Команда текстовой клавиатуры - включение/отключение режима горячих клавиш */    
    public static final int TXT_HOT = -307;
    /** Команда текстовой клавиатуры - включение/отключение режима ALT (left) */    
    public static final int TXT_LALT = -308;
    /** Команда текстовой клавиатуры - включение/отключение режима ALT (right) */    
    public static final int TXT_RALT = -311;
    /** Команда текстовой клавиатуры - включение/отключение режима CTRL (left) */    
    public static final int TXT_CTRL = -309;
    /** Команда текстовой клавиатуры - включение/отключение режима выделения */    
    public static final int TXT_ED_SELECT = -310;
/** Команда текстовой клавиатуры - копирование выбранного текста */    
    public static final int TXT_ED_COPY = -320;
/** Команда текстовой клавиатуры - вставка */
    public static final int TXT_ED_PASTE = -321;
/** Команда текстовой клавиатуры - вырезать */
    public static final int TXT_ED_CUT = -322;
    /** Команда текстовой клавиатуры - выделить всё*/
    public static final int TXT_ED_SELECT_ALL = -323;
    public static final int TXT_ED_COPY_ALL = -324;
 // size selected
    public static final int TXT_ED_SIZE_SELECTED = -325;
    public static final int TXT_ED_LAST = -399;
    /** Команда текстовой клавиатуры - клавиша delete (удаляет символ справа от курсора*/
    public static final int TXT_ED_DEL = -601;
    /** Команда текстовой клавиатуры - клавиша delete (удаляет слово слева от курсора*/
    public static final int TXT_ED_DEL_WORD = -602;
    /** Команда текстовой клавиатуры - страница вверх*/
    public static final int TXT_ED_PG_UP = -326;
    /** Команда текстовой клавиатуры - страница вниз*/
    public static final int TXT_ED_PG_DOWN = -327;
    /** Команда текстовой клавиатуры - в начало строки*/
    public static final int TXT_ED_HOME_STR = -328;
    /** Команда текстовой клавиатуры - в конец строки*/
    public static final int TXT_ED_END_STR = -329;
    /** Команды макросов 1 и 2*/
    public static final int REC_MACRO1 = -330;
    public static final int RUN_MACRO1 = -331;
    public static final int CLR_MACRO1 = -332;
    public static final int REC_MACRO2 = -333;
    public static final int RUN_MACRO2 = -334;
    public static final int CLR_MACRO2 = -335;
    public static final int TXT_ED_UNDO = -336;
    public static final int TXT_ED_REDO = -337;

    public static final int TXT_SELECT_FUNCTION = -338;
    public static final int TXT_SELECT_PARAGRAPF = -339;
    public static final int TXT_SELECT_LINE = -340;
    public static final int TXT_SELECT_SENTENCE = -341;
    public static final int TXT_SELECT_WORD = -342;

    /**-------------------------------------------------------------------    
	/// Список команд 
	//-------------------------------------------------------------------    

/** команда калькулятора - запись программы*/   
    public static final int CMD_CALC_SAVE = -498;
/** команда калькулятора - загрузка программы*/   
    public static final int CMD_CALC_LOAD = -499;
/** Внутреняя команда - открывает главное меню*/   
    public static final int CMD_MAIN_MENU = -500;
 /** Внутреняя команда - голосовой ввод */   
    public static final int CMD_VOICE_RECOGNIZER = -501;
/** Внутреняя команда - показ шаблонов на клавиатуре */ 
    public static final int CMD_TPL = -502;
/** Внутреняя команда - запуск настроек */  
    public static final int CMD_PREFERENCES = -503;
/** Внутреняя команда - запуск мультибуфера обмена */   
    public static final int CMD_CLIPBOARD = -504;
/** Внутреняя команда - создание папки шаблонов */  
    public static final int CMD_TPL_NEW_FOLDER = -505;
    /** Внутреняя команда - запуск редактора шаблонов */    
    public static final int CMD_TPL_EDITOR = -506;
    /** Внутреняя команда - запуск внешних приложений */    
    public static final int CMD_RUN_APP = -507;
    /** Выбор раскладки для текущей ориентации экрана */    
    public static final int CMD_SELECT_KEYBOARD = -508;
    /** Внутреняя команда - показ горячих клавиш на клавиатуре */ 
    public static final int CMD_HOTKEY = -510;
    /** Внутреняя команда - запуск активности настройки отображения пунктов главного меню*/ 
    public static final int CMD_RUN_MAINMENU_SETTING = -511;
    /** Внутреняя команда - запуск смены скина*/ 
    public static final int CMD_RUN_SELECT_SKIN = -512;
    /** команда "Выбор метода ввода" */    
    public static final int CMD_INPUT_KEYBOARD = -513;
    /** команда запускающая активность помощи */    
    public static final int CMD_HELP = -514;
    /** Внутреняя команда - выбор метода ввода*/   
    public static final int CMD_INPUT_METHOD = -515;
    /** команда калькулятора - история вычислений */    
    public static final int CMD_CALC_HISTORY = -516;
    /** внутренняя команда - запуск калькулятора */    
    public static final int CMD_CALC = -517;
    /** внутренняя команда - листинг программы калькулятора */    
    public static final int CMD_CALC_LIST = -518;
    /** внутренняя команда - скрывает/показывает окно автодополнения если оно включено над клавиатурой */    
    public static final int CMD_AC_HIDE = -519;
    /** внутренняя команда - запускает активность для выбора высоты клавиатуры */    
    public static final int CMD_HEIGHT_KEYBOARD = -603;
    /** внутренняя команда - ввести символ в верхнем регистре */    
    public static final int CMD_SYMBOL_UP_CASE = -604;
    /** внутренняя команда - ввести символ в нижнем регистре */    
    public static final int CMD_SYMBOL_LOWER_CASE = -605;
    /** внутренняя команда - ввод текста по долготапу */    
    public static final int CMD_INPUT_LONG_GESTURE = -606;
    /** внутренняя команда - полноэкранный режим*/    
    public static final int CMD_FULL_DISPLAY_EDIT = -607;
    /** внутренняя команда - запуск переводчика*/    
    public static final int CMD_TRANSLATE_SELECTED = -608;
    /** внутренняя команда - запуск переводчика*/    
    public static final int CMD_TRANSLATE_COPIED = -609;
    /** внутренняя команда - вызывает клавиатуру смайликов */    
    public static final int CMD_RUN_KBD_SMILE = -610;
    /** внутренняя команда - вызывает клавиатуру редактирования */    
    public static final int CMD_RUN_KBD_EDIT = -611;
    /** внутренняя команда - вызывает клавиатуру редактирования */    
    public static final int CMD_RUN_KBD_NUM = -612;
    /** внутренняя команда - поделиться выделенным */    
    public static final int CMD_SHARE_SELECTED = -613;
    /** внутренняя команда - искать выделенное в браузере */    
    public static final int CMD_SEARCH_SELECTED = -614;
    /** внутренняя команда - искать скопированное в браузере */    
    public static final int CMD_SEARCH_COPYING = -615;
    /** внутренняя команда - запуск редактора словаря пользователя */    
    public static final int CMD_EDIT_USER_VOCAB = -616;
    /** внутренняя команда - показать скопированное число в разных системах счисления */    
    public static final int CMD_SHOW_COPY_NUMBER_ANY_NOTATION = -617;
    
    /** переключатели раскладок для жестов */    
    public static final int GESTURE_SELECTOR_CALC_QWERTY = -618;
    public static final int GESTURE_SELECTOR_EDITTEXT_QWERTY = -619;
    public static final int GESTURE_SELECTOR_SMILE_QWERTY = -620;
    public static final int GESTURE_SELECTOR_NUM_QWERTY = -621;

    /** внутренняя команда - временное отключение словаря */    
    public static final int CMD_TEMP_STOP_DICT = -622;
    /** быстрая смена раскладки текущего языка (менюшка) */
    public static final int CMD_MENU_QUICK_SELECT_LAYOUT = -623;
    /** внутренняя команда - поделиться скопированным */    
    public static final int CMD_SHARE_COPIED = -624;
    /** внутренняя команда - ввод спецсимволов*/    
    public static final int CMD_INSERT_SPEC_SYMBOL = -625;
    /** быстрая смена скина без окрытия активности */
    public static final int CMD_MENU_QUICK_SELECT_SKIN = -626;
    /** внутренняя команда - старт активности Языки и раскладки*/    
    public static final int CMD_START_SET_LANG_ACTIVITY = -627;
    /** внутренняя команда - выводит список пользовательских раскладок типа hide <br> 
     * из папки keyboards*/    
    public static final int CMD_SHOW_USER_HIDE_LAYOUT = -628;
    /** внутренняя команда - выводит меню дополнительных раскладок встроенных в клавиатуру <br>
     *  типа hide*/    
    public static final int CMD_SHOW_ADDITIONAL_HIDE_LAYOUT = -629;
    /** внутренняя команда - выводит меню дополнительных раскладок встроенных в клавиатуру <br>
     *  типа hide*/    
    public static final int CMD_SHOW_FONT_KEYBOARD_DIALOG = -630;

    /** стартовое число для посылки кейкодов клавиш по формуле 0-KEYCODE_CODE-посылаемый_код 
     * (занимает промежуток кодов -5000 - -7000) */
    public static final int KEYCODE_CODE = -5000;
 /** использована только для жеста! */ 
    public static final int CMD_RUN_KBD_SYMBOL = -2;
    
    /** выводит окно popupcharacter для жеста "дополнительные символы1" */    
    public static final int GESTURE_ADDITIONAL_SYMBOL1 = -100001;
    /** выпадающий список слов из автодополнения */    
    public static final int GESTURE_AC_PLACE_LIST = -100002;
    /** количество пробелов зависит от длины жеста */    
    public static final int GESTURE_SPACE_QUANTITY = -100003;

//-------------------------------------------------------------------    
/// Калькулятор
//-------------------------------------------------------------------    
    
    // формат файла программы калькулятора    
    public static final String CALC_PRG_VERSION = "VERSION = 2";
    public static final String CALC_PRG_DESC_WORD = "DESCRIPTION:";
    public static final String CALC_PROGRAM_WORD = "PROGRAM:";
    // коррекция высоты индикатора над клавиатурой если автодополнение отключено    
    //public static final String PREF_CALC_CORRECTION_IND = "calc_corr_ind";
    // цифра калькулятора 0    
    public static final int SET_KEY_CALC_NUMBER0 = -540;
    // цифра калькулятора 1    
    public static final int SET_KEY_CALC_NUMBER1 = -541;
    // цифра калькулятора 2    
    public static final int SET_KEY_CALC_NUMBER2 = -542;
    // цифра калькулятора 3    
    public static final int SET_KEY_CALC_NUMBER3 = -543;
    // цифра калькулятора 4    
    public static final int SET_KEY_CALC_NUMBER4 = -544;
    // цифра калькулятора 5    
    public static final int SET_KEY_CALC_NUMBER5 = -545;
    // цифра калькулятора 6    
    public static final int SET_KEY_CALC_NUMBER6 = -546;
    // цифра калькулятора 7    
    public static final int SET_KEY_CALC_NUMBER7 = -547;
    // цифра калькулятора 8    
    public static final int SET_KEY_CALC_NUMBER8 = -548;
    // цифра калькулятора 9    
    public static final int SET_KEY_CALC_NUMBER9 = -549;
/**  никакой операции (подавляет вывод на экран)*/    
    public static final int SET_KEY_CALC_NOP = -550;
    // клавиша умножить    
    public static final int SET_KEY_CALC_MULTIPLY = -552;
    // клавиша делить    
    public static final int SET_KEY_CALC_DIVIDE = -553;
    // клавиша плюс    
    public static final int SET_KEY_CALC_PLUS = -554;
    // клавиша минус    
    public static final int SET_KEY_CALC_MINUS = -555;
    // клавиша очистки содержимого регистра х (Cx)
    public static final int SET_KEY_CALC_CX = -556;
    // клавиша копирования содержимого регистра x в y
    public static final int SET_KEY_CALC_B_UP = -557;
    // клавиша обмена содержимого регистров x и y
    public static final int SET_KEY_CALC_XY = -558;
    // клавиша минуса по модулю
    public static final int SET_KEY_CALC_MODUL_MINUS = -559;
    // клавиша "точка" - означает что после её нажатия будет вводиться дробная часть
    public static final int SET_KEY_CALC_ZERO = -560;
    // клавиша x^2
    public static final int SET_KEY_CALC_X_KVADRAT = -563;
    // клавиша sqrt(x)
    public static final int SET_KEY_CALC_X_SQRT = -564;
    // клавиша %
    public static final int SET_KEY_CALC_PROC = -567;
    // клавиша "M"
    public static final int SET_KEY_CALC_MEMORY = -568;
    // клавиша "MR"
    public static final int SET_KEY_CALC_MR = -569;
    // клавиша "MR"
    public static final int SET_KEY_CALC_MC = -570;
    // клавиша "ПХ"
    public static final int SET_KEY_CALC_PX = -571;
    // клавиша "ИПХ"
    public static final int SET_KEY_CALC_IPX = -572;
    // клавиша "clear" очистка массива программы калькулятора
    public static final int SET_KEY_CALC_CLR = -577;

  //-------------------------------------------------------------------    
  /// функции калькулятора
  //-------------------------------------------------------------------    
    // круговорот t-z-y-x-(t)    
    public static final int SET_KEY_CALC_TTOX = -536;
    // вставка число факториал    
    public static final int SET_KEY_CALC_FACTORIAL = -537;
    // вставка число еx    
    public static final int SET_KEY_CALC_EX = -538;
    // константа Е   
    public static final int SET_KEY_CALC_E = -539;
    // вставка числа Пи    
    public static final int SET_KEY_CALC_PI = -585;
    // синус числа    
    public static final int SET_KEY_CALC_SIN = -586;
    // косинус числа    
    public static final int SET_KEY_CALC_COS = -587;
    // тангенс числа     
    public static final int SET_KEY_CALC_TAN = -588;
    // арксинус числа    
    public static final int SET_KEY_CALC_ASIN = -589;
    // арккосинус числа    
    public static final int SET_KEY_CALC_ACOS = -590;
    // арктангенс числа     
    public static final int SET_KEY_CALC_ATAN = -591;
    // 1/x     
    public static final int SET_KEY_CALC_1X = -592;
    // X^Y     
    public static final int SET_KEY_CALC_XSTY = -593;
    // LOG     
    public static final int SET_KEY_CALC_LOG = -594;
    // random     
    public static final int SET_KEY_CALC_RND = -595;
    // целая часть числа     
    public static final int SET_KEY_CALC_INT = -596;
    // LOG10
    public static final int SET_KEY_CALC_LOG10 = -597;
    // градусы в радианы
    public static final int SET_KEY_CALC_GTOR = -598;
    // радианы в градусы
    public static final int SET_KEY_CALC_RTOG = -599;
    // радианы в градусы
    public static final int SET_KEY_CALC_ROUND = -600;
    // озведение числа в степени 10     
    public static final int SET_KEY_CALC_VP = -535;

 //-------------------------------------------------------------------    
 // условные команды калькулятора
 //-------------------------------------------------------------------    
    // клавиша больше или равно НОЛЮ
    public static final int SET_KEY_CALC_BOLSHERAVNO_ZERO = -521;
    // клавиша меньше НОЛЮ
    public static final int SET_KEY_CALC_MENSHE_ZERO = -522;
    // клавиша равно НОЛЮ
    public static final int SET_KEY_CALC_PAVNO_ZERO = -523;
    // клавиша неравно НОЛЮ
    public static final int SET_KEY_CALC_NERAVNO_ZERO = -524;
    // клавиша больше или равно y
    public static final int SET_KEY_CALC_BOLSHERAVNO_Y = -525;
    // клавиша меньше y
    public static final int SET_KEY_CALC_MENSHE_Y = -526;
    // клавиша равно y
    public static final int SET_KEY_CALC_RAVNO_Y = -527;
    // клавиша неравно y
    public static final int SET_KEY_CALC_HERAVNO_Y = -528;
  //-------------------------------------------------------------------    
  // команды калькулятора
  //-------------------------------------------------------------------    
    /** клавиша окончания работы с калькулятором. Аналог клавиши (-2) */
    public static final int SET_KEY_CALC_CLOSE = -561;
    // клавиша включения индикатора калькулятора
    public static final int SET_KEY_CALC_ON = -562;
    // клавиша "шаг вперед"
    public static final int SET_KEY_CALC_UP = -565;
    // клавиша "шаг назад"
    public static final int SET_KEY_CALC_DOWN = -566;
    // клавиша "в/о"
    public static final int SET_KEY_CALC_VO = -573;
    // клавиша "с/п"
    public static final int SET_KEY_CALC_SP = -574;
    // клавиша "AUTO"
    public static final int SET_KEY_CALC_AUTO = -575;
    // клавиша "PRG"
    public static final int SET_KEY_CALC_PRG = -576;
    // клавиша "БП" - безусловный переход
    public static final int SET_KEY_CALC_BP = -578;
    // клавиша "ПП" - подпрограмма
    public static final int SET_KEY_CALC_PP = -520;
      
    
    
/** Внутреняя команда - переключение языка. Если языков больше 3 - показываем меню*/    
    public static final int CMD_LANG_CHANGE = -20;
    /** Внутреняя команда - выбор языка из меню */    
    public static final int CMD_LANG_MENU = -21;
    /** Внутреняя команда - переключает между последними двумя языками */    
    public static final int CMD_LANG_CHANGE_TWO_LANG = -22;
    /** Внутреняя команда - переключение языка без вывода меню, переход к следующему языку*/    
    public static final int CMD_LANG_CHANGE_NEXT_LANG = -23; 
    /** Внутреняя команда - переключение языка без вывода меню, переход к предыдущему языку*/    
    public static final int CMD_LANG_CHANGE_PREV_LANG = -24; 
    /** Внутреняя команда - компиляция клавиатур */    
    public static final int CMD_COMPILE_KEYBOARDS = -10000;
    /** Внутреняя команда - декомпиляция клавиатур */    
    public static final int CMD_DECOMPILE_KEYBOARDS = -11;
    public static final int CMD_RELOAD_SKIN = -10;
    
  //-------------------------------------------------------------------    
  /// Прочие строковые значения
  //-------------------------------------------------------------------    
    
/** Значение для запуска {@link SetKbdActivity}. С этим ключом передаётся параметр типа int<br>
 *  Параметр int - одно из значений SET_*/    
    public static final String SET_INTENT_ACTION = "sa";
 /**  Параметр String - название языка, для которого производится выбор клавиатуры */    
    public static final String SET_INTENT_LANG_NAME = "sl";
/**  Параметр int - ориентация экрана, для которой выбирается раскладка 0- оба типа, 1 - портрет, 2 - ландшафт*/    
    public static final String SET_SCREEN_TYPE = "screen";
    

}
