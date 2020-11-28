package com.jbak2.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

import com.jbak2.JbakKeyboard.CustomKbdScroll;
import com.jbak2.JbakKeyboard.JbKbdPreference;
import com.jbak2.JbakKeyboard.R;
import com.jbak2.JbakKeyboard.st;
import com.jbak2.ctrl.IniFile;

import android.content.Context;
import android.os.Build;

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
    /** (2 месяца) последняя проверка была 3 месяца назад */
	public static final long LAST_CHECK_CHECK_3_MONTHS_BACK = 1000l*3600l*24l*60l;
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
	public static final String PAGE_ADD_STAT = "http://vhost-33881.cloudpark.tech";
	//public static final String PAGE_ADD_STAT = "http://m445438c.beget.tech";
	
	/** массив ссылок для скачивания проверочного файла наличия новой версии. <br>
	 * Нулевой индекс - прямая ссылка. ПОРЯДОК НЕ МЕНЯТЬ!*/
	public static final String[] AR_PAGE_UPDATE =  new String[]
			{
				SITE_KBD+PAGE_UPDATE,
				PAGE_ADD_STAT,
				"https://is.gd/63ta6l",
				"https://ur-l.ru/Jb2up"
					
			};
	public static final String PAGE_OTHER_APP= "/index/vse_programmy/0-16";
	/** дополнительные компоненты (jbak2layout, jbak2skin) */
	public static final String PAGE_ADDITIONA_COMPONENT = "/load/dopolnitelno/21";
	/** страница загрузки словарей */
	public static final String PAGE_DICT = "/load/slovari/14";
	/** страница отзывов */
	public static final String PAGE_REVIEW = "/gb";
	public static final String CHECK_VERSION_NAME = "ver";
	public static final String CHECK_MIN_SDK = "msd";

	public static final String PAGE_UPD_CLICK = "/upd/cl_kbd.htm";

	/** флаг, что проверка автоматическая (false), а не ручная (true) */
	public static boolean autocheck = false;
    static SiteKbd inst = null;
	static Context m_c = null;
	//static WebView ww = null;
	
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
		if (JbKbdPreference.rate_check)
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
		} else {
			ini.setParam(ini.LAST_CHECK_TIME, st.STR_NULL+(curtime-LAST_CHECK_CHECK_3_MONTHS_BACK-1000));
			return true;
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
		updateCheckPreferenceData(2, ini);

		// чекаем файлы параметров скролящихся раскладок
		CustomKbdScroll.checkFileSettingLayout();
		
//		final WebView ww = new WebView(m_c);
//		ww.getSettings().setAppCacheEnabled(false);
//		ww.getSettings().setJavaScriptEnabled(true);
//	    ww.setWebViewClient(new WebViewClient() {
//	             public void onPageFinished(WebView view, String url) {
//	                 st.test();
//	             }
//	         });
//
//		final String url = SITE_KBD+PAGE_UPD_CLICK;
//		//clickCheckCopy(SITE_KBD+PAGE_UPD_CLICK);
//		if (ww != null) {
//			ww.loadUrl(url);
//		
////		ww.post(new Runnable() {
////	          @Override
////	          public void run() {
////					ww.loadUrl(url);
////	          }
////	       });
//		try {
//			Thread.sleep(4000);
//		} catch (Throwable e) {
//		}
////		ww.destroy();
//		}
		
		if (st.debug_mode)
			st.toastLong("Проверка выхода новой версии");
		
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
//				if (!autocheck) {
//					
//					final WebView ww = new WebView(m_c);
//					ww.getSettings().setAppCacheEnabled(false);
//				    ww.setWebViewClient(new WebViewClient() {
//				             public void onPageFinished(WebView view, String url) {
//				                 st.test();
//				             }
//				         });
//
//					final String url = SITE_KBD+PAGE_UPD_CLICK;
//					//clickCheckCopy(SITE_KBD+PAGE_UPD_CLICK);
//					if (ww == null)
//						return;
//					
//					ww.post(new Runnable() {
//				          @Override
//				          public void run() {
//								ww.loadUrl(url);
//				          }
//				       });
//					try {
//						Thread.sleep(4000);
//					} catch (Throwable e) {
//					}
//					ww.destroy();

//						String str = SITE_KBD+PAGE_UPD_CLICK;
//					str += KEY_UPD_CHECK_KEYS+(st.STR_EQALLY+!autocheck).toUpperCase();
//					str += "&"+KEY_UPD_CHECK_VER+st.STR_EQALLY+st.getAppVersionCode(m_c);
//					str += "&"+KEY_UPD_CHECK_SUBMIT+st.STR_EQALLY+"Send";
//					sc =  new Scanner(new URL(str).openStream(), st.STR_UTF8);
//				}
				Scanner sc =  null;
				info = null;
				if (!autocheck) {
					//URL url = null
					String param = null;
					for (int i=1;i<AR_PAGE_UPDATE.length;i++) {
						param = null;
						try {
							if (AR_PAGE_UPDATE[i].compareTo(AR_PAGE_UPDATE[1]) == 0) {
								AR_PAGE_UPDATE[i] = AR_PAGE_UPDATE[i]+"/index.php?act=1&v="+st.getAppVersionCode(m_c)
								+"&s="+st.STR_NULL+android.os.Build.VERSION.SDK_INT;
							}
							info = readUrl(AR_PAGE_UPDATE[i], param);
//							} else {
//								sc =  new Scanner(new URL(AR_PAGE_UPDATE[i]).openStream(), st.STR_UTF8);
//								sc.useDelimiter("\\A");
//								info = sc.next();
//								sc.close();
//							}
						} catch (Throwable e) {
						}
						if (info != null&&info.startsWith(CHECK_KEY)) {
							break;
						}
						info = null;
						param = null;
					}
				}
				autocheck = false;
				if (info == null) {
					try {
						// прямая ссылка
						sc =  new Scanner(new URL(AR_PAGE_UPDATE[0]).openStream(), st.STR_UTF8);
						sc.useDelimiter("\\A");
						info = sc.next();
						sc.close();
					} catch (Throwable e) {
						st.log("kkk");
						st.logEx(e);
						bcheck_backgraund = false;
						updateCheckPreferenceData(1, ini);
					}
				}
				// если до сих пор info = null, то причин 3:
				// 1. нет инета
				// 2. разрешения на инет нету
				// 3.закрыли доступ к сайту через hosts
				// а значит просто возврат
				if (info == null) {
					return;
				}
				boolean fl = false;
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
								fl = true;
							}
							else if (param.compareToIgnoreCase(ini.MIN_SDK)==0) {
								ini.setParam(ini.MIN_SDK, param_value);
								fl = true;
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
				updateCheckPreferenceData(1, ini);
				bcheck_backgraund = false;
				m_c = null;
			}
		}).start();
		return false;
	}
	/** Устанавливаем значение у настройки "Проверить обновления", 
	 * если это возможно 
	 * @param type : <br>
	 * 0 - пустую строку <br> 
	 * 1 - есть/нет новая версия + дата последней проверки <br>
	 * 2 - строка Проверяю...*/
	public void updateCheckPreferenceData(int type, IniFile ini)
	{
		if (JbKbdPreference.inst!=null)
			JbKbdPreference.inst.setCheckEntry(JbKbdPreference.inst, type, ini);
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
			vapp = 0;
		}
		// чекаем мин сдк
		ver = ini.getParamValue(ini.MIN_SDK);
		int vlini = 0;
		if (ver == null)
			vlini = Build.VERSION.SDK_INT;
		else {
			try {
				vlini = Integer.parseInt(ver);
			} catch (NumberFormatException e) {
				return 0;
			}
		}
		if (Build.VERSION.SDK_INT < vlini)
			return 0;
		ver = ini.getParamValue(ini.LAST_CHECK_VERSION);
		vlini = 0;
		try {
			vlini = Integer.parseInt(ver);
		} catch (NumberFormatException e) {
			ini.setParam(ini.LAST_CHECK_VERSION, st.STR_NULL+(vapp-1));
			return 0;
		}
		long curtime = new Date().getTime();
		ver = ini.getParamValue(ini.LAST_TIME_TOAST_NOT_UPDATE);
		long tlasttoast = 0;
		try {
			tlasttoast = Long.parseLong(ver);
		} catch (NumberFormatException e) {
			ini.setParam(ini.LAST_TIME_TOAST_NOT_UPDATE, st.STR_NULL+curtime);
			return 0;
		}
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
		updateCheckPreferenceData(1, ini);

		return 0;
	}
	
	public String readUrl(String url, String param) {
		String str = null;
//		URL lurl = null;
		InputStream is = null;
		try {
			is = new URL(url).openStream();
			BufferedReader in = new BufferedReader(
			new InputStreamReader(is));
			char buf[] = new char[200];
			in.read(buf);
			in.close();
			str = new String(buf).trim();
			if (str!=null&&str.length() == 0)
				str = null;
			
		} catch (Throwable e) {
		} finally {
			if (is!=null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
// старый код
//		char buf[] = new char[200];
//		in.read(buf);
//	in.close();
//	str = new String(buf).trim();
//	if (str!=null&&str.length() == 0)
//		str = null;
//		} finally {
//		}
//		HttpURLConnection urlConnection = null;
//		try {
//			lurl = new URL(url);
//			urlConnection = (HttpURLConnection) lurl.openConnection();
//			urlConnection.setRequestMethod("GET");
//			urlConnection.setRequestProperty("Content-Type", "text/plain"); 
//			urlConnection.setRequestProperty("Accept-Charset", st.STR_UTF8);
//
//			BufferedReader in = new BufferedReader(
//					new InputStreamReader(urlConnection.getInputStream()));
//	   		char buf[] = new char[200];
//	   		in.read(buf);
//			in.close();
//			str = new String(buf).trim();
//			if (str!=null&&str.length() == 0)
//				str = null;
//
//		} catch (Throwable e) {
//			st.log("kkk");
//			st.logEx(e);
//		} finally {
//			urlConnection.disconnect();
//		}
		return str;
	}
	
}