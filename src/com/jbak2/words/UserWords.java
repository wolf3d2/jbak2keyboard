package com.jbak2.words;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import com.jbak2.JbakKeyboard.R;
import com.jbak2.JbakKeyboard.st;
import com.jbak2.ctrl.GlobDialog;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.widget.TextView;

public class UserWords {
//	/** временно останавливает запись в словарь.
//	 * Пока используется только в поиске редактора словаря пользователя */
//	public boolean stop_add_word = false;
	/** переменная хранения слова для addWord */
	public static String m_word = st.STR_NULL;
	/** лимит частоты слова, если больше, то устанавливается это */
	public static int WORD_FREQ_LIMIT = 30000;
	/** значение частоты по умолчанию для слов добавленных пользователем */
	public static int FREQ_USER_WORD = 20000;
	/** значение на сколько увеличивать частоту слова, <br>
	 * если включено расширенное обучение словаря */
	public static int FREQ_STUDENT_EXTENDED_VALUE = 5;
	public static long fr = 0;
	public static String delword = st.STR_NULL;
	public static final String C_WORD = "word";
	public static final String C_FREQ = "freq";
	public static String FILENAME = "user_vocab.cdb";
	public static final String CRT_TABLE = "CREATE TABLE %s (" + C_WORD + " text PRIMARY KEY," + C_FREQ + " tinyint)";
	static SQLiteDatabase m_db = null;;
	SQLiteStatement m_getWordsStat;
	public ArrayList<WordArray> arword = new ArrayList<WordArray>();
	public ArrayList<LangCount> arlang_value = new ArrayList<LangCount>();

	/** минимум слов в таблице до которой проверка
	 * на расширенное обучение словаря не производится*/
	public static int MIN_COUNT_WORD = 1000;
	/** лимит размера таблицы слов */
	public static int LIMIT_WORD = 3500;
	/** лимит добавления слов для проверки последней проверки */
	public static int LIMIT_TEST = 200;

	/**
	 * класс для хранения слов для редактирования пользовательского словаря
	 */
	public static class WordArray {
		public int id = -1;
		public String nameold = st.STR_NULL;
		public String namenew = st.STR_NULL;
		public long freqold = -1;
		public long freqnew = -1;
		public boolean del = false;

		public WordArray() {
			nameold = st.STR_NULL;
			namenew = st.STR_NULL;
			freqold = -1;
			freqnew = -1;
			del = false;
		}
	}

	/** текущее количество записей в таблицы */
	public class LangCount {
		public int val = 0;
		public String lang = null;

		public LangCount() {
			lang = null;
			val = 0;
		}

		public LangCount(String lng) {
			lang = lng;
			val = 0;
		}
	}
/** текущее число записей в таблицу lng */
	public int getLangCurInputValue(String lng) {
		int ret = 0;
		for (LangCount lc : arlang_value) {
			if (lc.lang != null && lc.lang.compareToIgnoreCase(lng) == 0) {
				ret = lc.val;
				break;
			}
		}
		return ret;
	}

	/** увеличиваем количество записей val в таблице lng, 
	 * если val = 0, то обнуляем счётчик */
	public void addLangCurInputValue(String lng, int val) {
		boolean fl = true;
		for (LangCount lc : arlang_value) {
			if (lc.lang != null && lc.lang.compareToIgnoreCase(lng) == 0) {
				if (val == 0)
					lc.val = 0;
				else
					lc.val += val;
				fl = false;
				break;
			}
		}
		if (fl) {
			LangCount lc = new LangCount();
			lc.lang = lng;
			lc.val = val;
			arlang_value.add(lc);
		}
	}

	// возвращает true если чтение прошло успешно
	// читает с конца.
	// прочитанные слова находятся в arword
	public boolean getAllWord(String lang) {
		if (m_db == null)
			return false;
		if (!setCurTable(lang))
			return false;
		arword.clear();
		// String sel = "%";
		// m_sel[0]=sel;
		// long fr1 = -1;
		char gg = '"';

		String req = "select word from 'ru'";
		String sel = "%";
		m_sel[0] = sel;
		try {
			Cursor c = m_db.query(lang, new String[] { "word", "freq" }, null, null, null, null, null);
			if (c.getCount() <= 0) {
				st.toast(R.string.empty);
				return false;
			}
			c.moveToLast();
			WordArray wa = null;
			do {
				wa = new WordArray();
				wa.nameold = c.getString(0);
				wa.namenew = wa.nameold;
				wa.freqold = c.getLong(1);
				wa.freqnew = wa.freqold;
				wa.del=false;
				arword.add(wa);
			} while (c.moveToPrevious());
		} catch (Throwable e) {
			return false;
		}

		return true;
	}

	public Vector<String> getTables() {
		if (m_db == null)
			return null;
		return getTables(m_db);

	}

	public static Vector<String> getTables(SQLiteDatabase db) {
		Vector<String> ar = new Vector<String>();
		try {
			Cursor c = db.rawQuery("select name from sqlite_master where type = 'table'", null);
			if (c.moveToFirst()) {
				do {
					ar.add(c.getString(0));
				} while (c.moveToNext());
			}
			c.close();
		} catch (Throwable e) {
		}
		return ar;
	}

	// SQLiteDatabase m_db;
	Vector<String> m_tables;
	String m_curTable;

	public boolean open(String path) {
		try {
			File f = new File(path);
			m_db = SQLiteDatabase.openOrCreateDatabase(f, null);
			m_tables = getTables(m_db);
			return true;
		} catch (Throwable e) {
		}
		return false;
	}

	public final boolean isTableExist(String name) {
		for (String t : m_tables) {
			if (t.equals(name))
				return true;
		}
		return false;
	}

	boolean addTable(String name) {
		try {
			String sql = String.format(CRT_TABLE, name);
			m_db.execSQL(sql);
			m_tables.add(name);
			return true;
		} catch (Throwable e) {
		}
		return false;
	}

	public boolean deleteTable(String name) {
		try {
			String sql = String.format("DROP TABLE IF EXISTS %s", name);
			m_db.execSQL(sql); 
			m_tables.remove(name);
			return true;
		} catch (Throwable e) {
		}
		return false;
	}

	boolean addWord(String word) {
		if (isTableOpen())
			return addWord(word, m_curTable);
		return false;
	}

	boolean addWord(String word, String lang) {
//		if (stop_add_word)
//			return false;
		if (word.length() == 1 && !Character.isLetterOrDigit(word.charAt(0)))
			return false;
		if (!isTableExist(lang) && !addTable(lang))
			return false;
		m_word = word.toLowerCase();
		fr = 0;
		fr = getFreqWord(m_word, lang);

		if (fr < 0)
			fr = 1;
		if (fr > WORD_FREQ_LIMIT - 1)
			fr = WORD_FREQ_LIMIT - 1;
		ContentValues cv = new ContentValues(2);
		cv.put(C_WORD, m_word);
		if (fr == 0)
			return true;
		// cv.put(C_FREQ, fr);
		else
			cv.put(C_FREQ, st.freq_dict + fr);
		st.freq_dict = 0;
		// проверки, если включено быстрое обучение
		if (st.student_dict & st.student_dict_ext) {
			long count = 0;
			count = cv.getAsLong(C_FREQ);
			// если у слова частота уже больше 2 и меньше (лимит частоты-10000),
			// то увеливаем её с порогом на 5
			if (count > 2&count < FREQ_USER_WORD-1000)
				cv.put(C_FREQ, count+FREQ_STUDENT_EXTENDED_VALUE);
			count = 0;
			try {
				count = DatabaseUtils.queryNumEntries(m_db, lang);
				if (count > MIN_COUNT_WORD || count >= LIMIT_WORD 
					||getLangCurInputValue(lang) > LIMIT_TEST) {
					m_db.execSQL("DELETE FROM " + lang + " WHERE " + C_FREQ + " = 2;");
					addLangCurInputValue(lang, 0);
				}
			} catch (Throwable e) {}
			if (count > LIMIT_WORD){
				long ri = m_db.insert(lang, null, cv);
				if (ri < 0) {
					ri = m_db.update(lang, cv, C_WORD + "=?", new String[] { m_word });
				}
				addLangCurInputValue(lang, 1);
				//m_db.update(lang, cv, C_WORD + "=?", new String[] { m_word });
				return true;
			}
		}
		if (!isTableExist(lang))
			addTable(lang);
		long ri = m_db.insert(lang, null, cv);
		if (ri < 0) {
			ri = m_db.update(lang, cv, C_WORD + "=?", new String[] { m_word });
			addLangCurInputValue(lang, 1);
		} else
			addLangCurInputValue(lang, 1);
		return true;
	}

	public void updateWord(String lang, WordArray wa) {
		if (!isTableExist(lang) && !addTable(lang))
			return;
		if (m_db == null)
			return;
		wa.namenew = wa.namenew.toLowerCase();
		ContentValues cv = new ContentValues(2);
		cv.put(C_WORD, wa.namenew);
		cv.put(C_FREQ, wa.freqnew);
		int ri = m_db.update(lang, cv, C_WORD + "=?", new String[] { wa.nameold });
		return;
	}

	public boolean setCurTable(String lang) {
		if (m_db == null)
			return false;
		if (m_tables == null)
			m_tables = getTables(m_db);
		if (!isTableExist(lang))
			addTable(lang);
		m_curTable = lang;
		// String sql = "SELECT * FROM "+lang+" WHERE "+C_WORD+" LIKE ?";
		// try
		// {
		// m_getWordsStat = m_db.compileStatement(sql);
		// return true;
		// }
		// catch (Throwable e)
		// {
		// e.printStackTrace();
		// }
		return true;
	}

	public void close() {
		if (m_db != null)
			m_db.close();
		m_curTable = null;
	}

	public boolean isTableOpen() {
		return m_curTable != null;
	}

	String m_sel[] = new String[1];

	public WordsReader getWordsReader(String word) {
		if (m_db == null) {
			open(WordsService.getVocabDir() + FILENAME);
		}
		String sel = "%";
		if (word.length() > 0)
			sel = word.charAt(0) + "%";
		m_sel[0] = sel;
		try {
			Cursor c = m_db.query(m_curTable, null, C_WORD + " LIKE ?", m_sel, null, null, null);
			return new WordsReader(c, word);
		} catch (Throwable e) {
		}
		return null;
	}

	public static class WordsReader extends IWords {
		public WordsReader(Cursor c, String word) {
			m_cursor = c;
			m_word = word;
		}

		final boolean next() {
			if (m_cursor == null) {
				m_bHasNext = false;
				return false;
			}
			m_bHasNext = m_cursor.isBeforeFirst() ? m_cursor.moveToFirst() : m_cursor.moveToNext();
			if (!m_bHasNext) {
				m_cursor.close();
				m_cursor = null;
				return false;
			}
			return true;
		}

		public Cursor m_cursor;
		public String m_word;

		@Override
		public WordEntry getNextWordEntry(int minFreq, boolean bFull) {
			if (!next())
				return null;
			String word = m_cursor.getString(0);
			int freq = m_cursor.getInt(1);
			if (freq < minFreq && !bFull)
				return null;
			int ct = TextTools.compare(m_word, word);
			if (ct == TextTools.COMPARE_TYPE_NONE)
				return null;
			if (st.student_dict == false)
				freq = freq * 100;
			if (freq > WORD_FREQ_LIMIT)
				freq = WORD_FREQ_LIMIT;
			WordEntry we = new WordEntry(word, freq, ct);
			we.flags += WordEntry.FLAG_FROM_USER_VOCAB;
			return we;
		}
	}

	// частота использования слова
	public long getFreqWord(String word, String lang) {
		if (m_db == null) {
			open(WordsService.getVocabDir() + FILENAME);
		}
		String sel = "%";
		if (word.length() > 0)
			sel = word.charAt(0) + "%";
		m_sel[0] = sel;
		long fr1 = -1;
		try {
			Cursor c = m_db.query(m_curTable, null, C_WORD + " LIKE ?", m_sel, null, null, null);
			c.moveToFirst();
			String user_word_bd = st.STR_NULL;
			do {
				user_word_bd = c.getString(0);
				if (user_word_bd.length() == 0)
					return 0;
				if (word.equalsIgnoreCase(user_word_bd)) {
					fr1 = c.getLong(1);
				}

			} while (c.moveToNext());
		} catch (Throwable e) {
		}
		if (fr1 >= WORD_FREQ_LIMIT)
			fr1 = WORD_FREQ_LIMIT;
		return fr1;
	}

	boolean delWord(String word) {
		if (isTableOpen())
			return deleteUserWord(word.toLowerCase(), m_curTable, false, null, new int[] {0,0,0}, true, null);
		return false;
	}

	/** удаляет слово из пользовательского словаря
	* tv - textview для изменения цвета текста при 'Да'
	* col - массив изменения цветов у tv:
	* col[0] - исходный цвет. Если = -1, то цвет не меняем
	* col[1] - цвет удаляемого слова до его удаления
	* col[2] - цвет удалённого слова
	* если col.length=0, != -1, то цвет меняется
	 */
	public boolean deleteUserWord(final String word2, final String lng1, 
			final boolean viewkbd, final TextView tv,
			final int[] col, boolean bNoQuery, final WordArray wa) {
		if (m_db == null) {
			if (!open(WordsService.getVocabDir() + FILENAME))
				return false;
		}
		String sel = "%";
		if (word2.length() > 0)
			sel = word2.charAt(0) + "%";
		m_sel[0] = sel;
		try {
			Cursor c = m_db.query(m_curTable, null, C_WORD + " LIKE ?", m_sel, null, null, null);
			if (c.getCount() <= 0) {
				st.toast(R.string.ac_del_word_notdict);
				return false;
			}
			c.moveToFirst();
			String user_word_bd = st.STR_NULL;
			boolean bflag = true;
			do {
				user_word_bd = c.getString(0);
				if (word2.equalsIgnoreCase(user_word_bd)) {
					delword = word2;
					if (bNoQuery) {
						if (tv!=null&col.length==2&col[1]!=0)
							tv.setTextColor(col[1]);
						GlobDialog gd = new GlobDialog(st.c());
						gd.set(st.c().getString(R.string.ac_del_word)+" ("+word2+")", 
								R.string.yes, R.string.no);
						gd.setObserver(new st.UniObserver() {
							@Override
							public int OnObserver(Object param1, Object param2) {
								if (tv!=null&col.length==3&col[0]!=0)
									tv.setTextColor(col[0]);
								if (((Integer) param1).intValue() == AlertDialog.BUTTON_POSITIVE) {
									m_db.execSQL("DELETE FROM " + lng1 + " WHERE " + C_WORD + " = \"" + word2 + "\";");
									if (tv!=null&col.length==3&col[2] != 0)
										tv.setTextColor(col[2]);
									if (viewkbd)
										st.showkbd();
									if (wa!=null)
										wa.del = true;
										
									st.toast(R.string.ac_del_word_ok);
								} else {
									if (viewkbd)
										st.showkbd();
								}
								return 0;
							}
						});
						st.hidekbd();
						gd.showAlert();
					} else {
						m_db.execSQL("DELETE FROM " + lng1 + " WHERE " + C_WORD + " = \"" + word2 + "\";");
						if (tv != null && col[2] != -1)
							tv.setTextColor(col[2]);
						if (viewkbd)
							st.showkbd();
						st.toast(R.string.ac_del_word_ok);

					}

					// fr1 = c.getLong(1);
					return true;
				}

			} while (c.moveToNext());
			if (bflag)
				st.toast(R.string.ac_del_word_notdict);
		} catch (Throwable e) {
		}
		return false;
	}
//	/** когда true, останавливает запись в словарь.*/
//	public void stopAddWord(boolean stop) {
//		stop_add_word = stop;
//	}

}
