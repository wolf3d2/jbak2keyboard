package com.jbak2.web;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

import com.jbak2.JbakKeyboard.JbKbdPreference;
import com.jbak2.JbakKeyboard.R;
import com.jbak2.JbakKeyboard.st;
import com.jbak2.ctrl.IniFile;

import android.content.Context;

/** класс для проверки обновлений с сайта клавиатуры */
public class SiteKbd {
    /** флаг, что проверка в фоне активна */
	public static boolean bcheck_backgraund = false;
    /** (сутки) частота проверки */
	public static final long FREQ_UPDATE_TIME= 1000l*3600l*24l*1l;
	// для тестирования
//	public static final long FREQ_UPDATE_TIME = 1000l*120l;
    /** (1 сутки) через какой промежуток времени ныть о старой версии */
	public static final long TOAST_NOT_UPDATE = 1000l*3600l*24l*1l;
	// для тестирования
//	public static final long TOAST_NOT_UPDATE = 1000l*3600l*4l;
//	public static final long TOAST_NOT_UPDATE = 1000l*3600l*2l;//30000l;//30 сек
	// для тестирования
//	public static final long TOAST_NOT_UPDATE= 1000l*120l;
    /** (3 месяца) последняя проверка была 3 месяца назад */
	public static final long LAST_CHECK_CHECK_3_MONTHS_BACK = 1000l*3600l*24l*90l;
    /** (1 год) через сколько времени перестаём ныть */
	public static final long LAST_UPDATE_NOT_SHOW_TOAST = 1000l*3600l*24l*365l;
	// для тестирования
//	public static final long LAST_CHECK_CHECK_3_MONTHS_BACK = 10000l;
	
	// Ссылки на страницы в клавиатуре
	/** ключ, с чего должна начинаться чекнутая строка из инета <br>
	 * (первая строка в файле) */
	public static final String CHECK_KEY = ";cjbak2";
	public static final String SITE_KBD = "https://jbak2.ucoz.net";
	public static final String PAGE_UPDATE = "/upd/act_ver_kbd.htm";
	/** дополнительные компоненты (jbak2layout, jbak2skin) */
	public static final String PAGE_ADDITIONA_COMPONENT = "/load/dopolnitelno/21";
	/** урл для программы словарей */
	public static final String PAGE_DICT = "/load/slovari/14";
	/** страница отзывов */
	public static final String PAGE_REVIEW = "/gb";
	public static final String CHECK_VERSION_NAME = "ver";

    static SiteKbd inst = null;
	Context m_c = null;
	
	public SiteKbd(Context context)
	{
		inst = this;
		m_c = context;
	}
	
	/** чекаем на новую версию на сайте <br>
	 * и пишем инфу в par.ini, для последующей обработке в программе и в настройках <br>
	 * Если возвращает true, то проверка НЕ ВЫПОЛНЯЛАСЬ и можно проверять дальше в программе
	 * 
	 *   @param bcheck - true - принудительная проверка 
	 */
	public boolean checkUpdate(final IniFile ini, final boolean bcheck) {
//		ini = new IniFile(m_c);
//		if (!ini.createMainIniFile()) {
//			ini = null;
//			return;
//		}
		if (ini==null)
			return true;
		long curtime = new Date().getTime();
		long lastcheck = 0;
		// читаем и обрабатываем параметр - время последней проверки
		String par = ini.getParamValue(ini.LAST_CHECK_TIME);
		if (par != null) {
			try {
				lastcheck = Long.parseLong(par);
			} catch (NumberFormatException e) {
				lastcheck = 0;
				ini.setParam(ini.LAST_CHECK_TIME, st.STR_NULL+(curtime-LAST_CHECK_CHECK_3_MONTHS_BACK-1000));
			}
		}

//		String scurtime = "dd.MM.yyyy HH:mm:ss";
//		Date dt = new Date();
//		dt.setTime(curtime);
//		SimpleDateFormat sdf = new SimpleDateFormat(scurtime);
//		scurtime = sdf.format(dt);
//		String slastcheck = "dd.MM.yyyy HH:mm:ss";
//		dt = new Date();
//		dt.setTime((long) (lastcheck));
//		sdf = new SimpleDateFormat(slastcheck);
//		scurtime = scurtime;
//		slastcheck = sdf.format(dt);
		
		// проверяем время последней проверки
		// если текущее время меньше <посл.проверка>+<частота проверки>,
		// то дальше не проверяем
		if (!bcheck&&curtime < (lastcheck+SiteKbd.FREQ_UPDATE_TIME)) {
			checkVersion(ini);
			return true;
		}
		if (JbKbdPreference.inst!=null) {
			JbKbdPreference.inst.setCheckEntry(JbKbdPreference.inst, 2, ini);
		}
		// чекаем в фоне
		new Thread(new Runnable() {
			public void run() {
				if (ini == null)
					return;
				bcheck_backgraund = true;
				long curtime = new Date().getTime();
				long lastcheck = 0;
				String info = null;
				// читаем и обрабатываем параметр - время последней проверки
				info = ini.getParamValue(ini.LAST_CHECK_TIME);
				if (info == null) {
					ini.setParam(ini.LAST_CHECK_TIME, st.STR_NULL+(curtime-LAST_CHECK_CHECK_3_MONTHS_BACK-1000));
					lastcheck = curtime - FREQ_UPDATE_TIME-1000;
				} else {
					try {
						lastcheck = Long.parseLong(info);
					} catch (NumberFormatException e) {
						ini.setParam(ini.LAST_CHECK_TIME, st.STR_NULL+curtime);
						return;
					}
				}
				// проверяем время последней проверки
				// если оно = 0 (выше параметр уже запишется в ini, 
				// или текущее время меньше <посл.проверка>+<частота проверки>,
				// то дальше не проверяем
				
				if (!bcheck) {
					if (!bcheck&&lastcheck == 0||curtime < (lastcheck+SiteKbd.FREQ_UPDATE_TIME)) {
						return;
					}
				}
				// пытаемся чекнуть
				Scanner sc =  null;
				info = null;
				boolean fl = false;
				try {
					sc =  new Scanner(new URL(SITE_KBD+PAGE_UPDATE).openStream(), "UTF-8");
					sc.useDelimiter("\\A");
					info = sc.next();
					sc.close();
					fl = true;
				} catch (Throwable e) {
					// если словили исключение, то причин 3:
					// 1. нет инета
					// 2. разрешения на инет нету
					// 3.закрыли доступ к сайту через hosts
					return;
				}
				// обрабатываем результат
				if (info==null) {
					if (fl)
						ini.setParam(ini.LAST_CHECK_TIME, st.STR_NULL+curtime);
					return;
				}
				if (!info.startsWith(CHECK_KEY)) {
					return;
				}
				String par = null;
				String param = null;
				String param_value = null;
				sc = new Scanner(info);
				try {
					sc.useLocale(Locale.US);
					while (sc.hasNext()) {
						if (sc.hasNextLine()) {
							par = sc.nextLine();
						}
						if (par.length() != 0) {
							if (par.compareToIgnoreCase(CHECK_KEY)==0)
								continue;
							param = par.substring(0, par.indexOf("="));
							param_value = par.substring(par.indexOf("=") + 1);
							if (param.compareToIgnoreCase(CHECK_VERSION_NAME)==0) {
								ini.setParam(ini.LAST_CHECK_VERSION, param_value);
							}
						}
					}
				} catch (Throwable e) {
				}
				sc.close();

				if (fl){
					ini.setParam(ini.LAST_CHECK_TIME, st.STR_NULL+curtime);
// не откоменчивать, иначе тост не показывается					
//					ini.setParam(ini.LAST_TIME_TOAST_NOT_UPDATE, st.STR_NULL+(curtime-TOAST_NOT_UPDATE-1000));
				}
				if (JbKbdPreference.inst!=null)
					JbKbdPreference.inst.setCheckEntry(JbKbdPreference.inst, 1, ini);
				bcheck_backgraund = false;
				
			}
		}).start();
		return false;
	}
	/** чекаем на новую версию с выведением тоста если новая версия есть <br>
	 * ЗАПУСКАТЬ МЕТОД ТОЛЬКО В ГЛАВНОМ ПОТОКЕ! <br>
	 * Предварительно, перед этой функцией, должна отработать фукция checkUpdate, <br>
	 * чтобы в par.ini уже были готовы данные для обработки <br>
	 * Если возвращает:   <br>
	 * 0 - нет новой версии <br>
	 * 1 - есть новая версия <br>
	 * 2 - прошло 3 месяца, а прога не обновлена <br>
	 */
	public int checkVersion(IniFile ini) {
		if (ini == null||m_c==null)
			return 0;
		String ver = st.getAppVersionCode(m_c);
		int vapp = 0;
		try {
			vapp = Integer.parseInt(ver);
		} catch (NumberFormatException e) {
		}
		ver = ini.getParamValue(ini.LAST_CHECK_VERSION);
		int vlini = 0;
		try {
			vlini = Integer.parseInt(ver);
		} catch (NumberFormatException e) {
		}
		ver = ini.getParamValue(ini.LAST_TIME_TOAST_NOT_UPDATE);
		long tlasttoast = 0;
		try {
			tlasttoast = Long.parseLong(ver);
		} catch (NumberFormatException e) {
		}
		long curtime = new Date().getTime();
//		long tstart = 0;
//		ver = ini.getParamValue(ini.VERSION_APP_CODE_START);
//		if (ver == null) {
//			ini.setParam(ini.VERSION_APP_CODE_START, st.STR_NULL+curtime);
//		} else {
//			try {
//				tstart = Long.parseLong(ver);
//			} catch (NumberFormatException e) {
//			}
//		}
		
//		String scurtime = st.getDatetime(curtime, "1");
//		String ststart= st.getDatetime((long) (tstart+TOAST_NOT_UPDATE), "1");
//		String slasttoast = st.getDatetime((long) (tlasttoast+TOAST_NOT_UPDATE), "1");
//		String sTOAST_NOT_UPDATE = st.getDatetime((long) (TOAST_NOT_UPDATE), "1");

		if (vapp >= vlini+8) {
			st.toastLong(m_c, R.string.chk_wrong_version);
			return 1;
		}
		if (vapp >= vlini)
			return 0;
		// ставить именно в таком порядке - сперва проверять бОльшие промежутки!
		else if (tlasttoast+TOAST_NOT_UPDATE+LAST_UPDATE_NOT_SHOW_TOAST < curtime) {
			return 0;
		}
		else if (tlasttoast+TOAST_NOT_UPDATE+LAST_CHECK_CHECK_3_MONTHS_BACK < curtime) {
			ini.setParam(ini.LAST_TIME_TOAST_NOT_UPDATE, st.STR_NULL+curtime);
			st.toastLong(m_c, R.string.chk_old_version);
			return 2;
		}
		else if (vapp < vlini&&tlasttoast+TOAST_NOT_UPDATE < curtime) {
			ini.setParam(ini.LAST_TIME_TOAST_NOT_UPDATE, st.STR_NULL+curtime);
			st.toastLong(m_c, R.string.chk_new_version);
			return 1;
		}
//		else if (vapp < vlini) // должно быть последней проверкой! 
//		{
//			ini.setParam(ini.LAST_TIME_TOAST_NOT_UPDATE, st.STR_NULL+curtime);
//			st.toastLong(m_c, R.string.chk_new_version);
//			return 1;
//		}
		if (JbKbdPreference.inst!=null)
			JbKbdPreference.inst.setCheckEntry(JbKbdPreference.inst, 1, ini);

		return 0;
	}

}