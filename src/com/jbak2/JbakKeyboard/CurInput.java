package com.jbak2.JbakKeyboard;

import com.jbak2.JbakKeyboard.ServiceJbKbd;
import com.jbak2.JbakKeyboard.Templates;
import com.jbak2.JbakKeyboard.st;

import android.view.KeyEvent;
import android.view.inputmethod.InputConnection;

/**
 * Класс для установки выделения и получения информации о текущем выделении, 
 * текущем слове и текущей строке. А также для операций с ними.
 */
public class CurInput {
	InputConnection ic = null;
	String wordStart;
	String wordEnd;
	String lineStart;
	String lineEnd;
	String sel = st.STR_NULL;
	boolean bInited = false;
	public boolean hasCurParagraph = false;

	boolean isInited() {
		return bInited;
	}

	String getTextParagraph() {
		if (lineStart == null || lineEnd == null)
			return null;
		return lineStart + lineEnd;
	}

	String getTextLine() {
		if (ServiceJbKbd.inst == null)
			return null;
		if (lineStart == null || lineEnd == null)
			return null;
		String out = null;
		int start = 0;
		int end = 0;
		/** стартовая позиция курсора */
		int spos = 0;
		try {
			ic = ServiceJbKbd.inst.getCurrentInputConnection();
			if (ic==null)
				return st.STR_NULL;
			spos = ServiceJbKbd.inst.m_SelStart;
			ServiceJbKbd.inst.sendHardwareSequence(ic, KeyEvent.KEYCODE_MOVE_HOME);
			ServiceJbKbd.inst.sendHardwareSequence(ic, 
					KeyEvent.KEYCODE_SHIFT_LEFT,
					KeyEvent.KEYCODE_MOVE_END);
			st.sleep(100);
			out = ic.getSelectedText(0).toString();
			ic.setSelection(spos, spos);

		} catch (Throwable e) {
		}
		if (out==null)
			out=st.STR_NULL;
		return out;
	}

	/** возвращает предложение */
	String getTextSentence() {
		if (ServiceJbKbd.inst == null)
			return null;
		if (lineStart == null || lineEnd == null)
			return null;
		CharSequence txt = st.STR_NULL;
		String ret = st.STR_NULL;
		String tmp = st.STR_NULL;
		try {
			ic = ServiceJbKbd.inst.getCurrentInputConnection();
			if (ic==null)
				return st.STR_NULL;
			txt = ic.getTextBeforeCursor(4000, 0);
			if (txt != null && txt.length() > 0) {
				for (int i = txt.length() - 1; i > -1; i--) {
					tmp = st.STR_NULL + txt.charAt(i);
					if (st.STR_END_SENTENCE.contains(tmp))
						break;
					ret = tmp + ret;
				}
			}
			// строка ПОСЛЕ курсора
			txt = ic.getTextAfterCursor(4000, 0);
			if (txt != null && txt.length() > 0) {
				for (int i = 0; i < txt.length(); i++) {
					tmp = st.STR_NULL + txt.charAt(i);
					if (st.STR_END_SENTENCE.contains(tmp)) {
						ret += tmp;
						break;
					}
					ret += tmp;
				}
			}

		} catch (Throwable e) {
		}
		return ret;
	}

	String getTextWord() {
		if (wordStart == null || wordEnd == null)
			return null;
		return wordStart + wordEnd;
	}
	boolean replaceCurWord(InputConnection ic, String repl) {
		if (!deleteCurWord(ic))
			return false;
		ic.commitText(repl, 1);
		return true;
	}

	boolean replaceCurParagraph(InputConnection ic, String repl) {
		if (!deleteCurParagraph(ic))
			return false;
		ic.commitText(repl, 1);
		return true;
	}

	boolean deleteCurParagraph(InputConnection ic) {
		if (lineStart == null || lineEnd == null)
			return false;
		ic.deleteSurroundingText(lineStart.length(), lineEnd.length());
		return true;
	}

	boolean deleteCurWord(InputConnection ic) {
		if (wordStart == null || wordEnd == null)
			return false;
		ic.deleteSurroundingText(wordStart.length(), wordEnd.length());
		return true;
	}

	/**
	 * Функция для получения текстов из редактора
	 * 
	 * @param positions
	 * @return true - инициализация удалась, false - нет
	 */
	boolean init(InputConnection ic) {
		bInited = true;
		try {
			if (ServiceJbKbd.inst == null)
				return false;
			if (ServiceJbKbd.inst.m_SelStart < 0 || ServiceJbKbd.inst.m_SelEnd < 0)
				return false;
			int ss = ServiceJbKbd.inst.m_SelStart, se = ServiceJbKbd.inst.m_SelEnd;
			// ss - реальная позиция курсора, может быть меньше, чем ss
			int cnt = se > ss ? se - ss : ss - se;
			int cp = se > ss ? ss : se;
			if (cnt > 0) {
				// НЕ МЕНЯТЬ! Получаем выделенный фрагмент
				ic.setSelection(cp, cp);
				sel = ic.getTextAfterCursor(cnt, 0).toString();
			}
			cp = se;
			if (cnt > 0)
				ic.setSelection(cp, cp);
			CharSequence sec1 = ic.getTextBeforeCursor(4000, 0);
			CharSequence sec2 = ic.getTextAfterCursor(4000, 0);
			String bef = sec1.toString();
			String aft = sec2.toString();
			int s = Templates.chkPos(bef.lastIndexOf('\n'), bef.lastIndexOf('\r'), true, bef.length());
			int e = Templates.chkPos(aft.indexOf('\n'), aft.indexOf('\r'), false, aft.length());
			if (s != -1 && e != -1) {
				lineStart = bef.substring(s);
				lineEnd = aft.substring(0, e);
				hasCurParagraph = s > 0 && e < aft.length();
			}
			wordStart = Templates.getCurWordStart(sec1, sec1.length() == 4000);
			wordEnd = Templates.getCurWordEnd(sec2, sec2.length() == 4000);
			if (cnt > 0)
				ic.setSelection(ss, se);
			return true;
		} catch (Throwable e) {
		}
		return false;
	}
	/** выделение части строки от курсора
	 * @param bhome - если true - выделяем до начала строки, иначе до конца
	 * */
	public void setSelectLine(boolean bhome) {
		if (ServiceJbKbd.inst == null)
			return;
		try {
			ic = ServiceJbKbd.inst.getCurrentInputConnection();
			ServiceJbKbd.inst.processTextEditKey(st.TXT_ED_SELECT);
			if (bhome)
				ServiceJbKbd.inst.processTextEditKey(st.TXT_ED_HOME_STR);
			else
				ServiceJbKbd.inst.processTextEditKey(st.TXT_ED_END_STR);
			ServiceJbKbd.inst.processTextEditKey(st.TXT_ED_SELECT);
		} catch (Throwable e) {
		}
		return;
	}
	/** выделяем строку целиком*/
	public void setSelectLine() {
		if (ServiceJbKbd.inst == null)
			return;
		try {
			ic = ServiceJbKbd.inst.getCurrentInputConnection();

			// строка ДО курсора
			ServiceJbKbd.inst.processTextEditKey(st.TXT_ED_HOME_STR);
			ServiceJbKbd.inst.processTextEditKey(st.TXT_ED_SELECT);
			ServiceJbKbd.inst.processTextEditKey(st.TXT_ED_END_STR);
			ServiceJbKbd.inst.processTextEditKey(st.TXT_ED_SELECT);
		} catch (Throwable e) {
		}
		return;
	}
	public void setSelectWord() {
		if (ServiceJbKbd.inst == null)
			return;
		try {
			ic = ServiceJbKbd.inst.getCurrentInputConnection();
			if (ic==null)
				return;
			int start = 0;
			int end = 0;
			CharSequence cs = ic.getTextBeforeCursor(300, 0);
			char ch=0;
			// начало слова
			if (cs.length()>0) {
				for (int i = cs.length()-1;i>-1;i--) {
					ch = cs.charAt(i);
					if (!Character.isLetterOrDigit(ch)) {
						break;
					}
					start++;
				}
			}
			// конец слова
			cs = ic.getTextAfterCursor(300, 0);
			if (cs.length()>0) {
				for (int i = 0;i<cs.length();i++) {
					ch = cs.charAt(i);
					if (!Character.isLetterOrDigit(ch)) {
						break;
					}
					end++;
				}
			}
			start = ServiceJbKbd.inst.m_SelStart-start;
			end = ServiceJbKbd.inst.m_SelEnd+end;
			ic.setSelection(start, end);
			
		} catch (Throwable e) {
		}
	}
	public void setSelectParagraph() {
		if (ServiceJbKbd.inst == null)
			return;
		try {
			ic = ServiceJbKbd.inst.getCurrentInputConnection();
			if (ic==null)
				return;
			int start = 0;
			int end = 0;
			CharSequence cs = ic.getTextBeforeCursor(4000, 0);
			char ch=0;
			// начало абзаца
			if (cs.length()>0) {
				for (int i = cs.length()-1;i>-1;i--) {
					ch = cs.charAt(i);
					if (ch=='\n'||ch=='\r') {
						break;
					}
					start++;
				}
			}
			// конец абзаца
			cs = ic.getTextAfterCursor(4000, 0);
			if (cs.length()>0) {
				for (int i = 0;i<cs.length();i++) {
					ch = cs.charAt(i);
					if (ch=='\n'||ch=='\r') {
						break;
					}
					end++;
				}
			}
			start = ServiceJbKbd.inst.m_SelStart-start;
			end = ServiceJbKbd.inst.m_SelEnd+end;
			ic.setSelection(start, end);
			
		} catch (Throwable e) {
		}
	}
	public void setSelectSentence() {
		if (ServiceJbKbd.inst == null)
			return;
		try {
			ic = ServiceJbKbd.inst.getCurrentInputConnection();
			if (ic==null)
				return;
			int start = 0;
			int end = 0;
			CharSequence cs = ic.getTextBeforeCursor(4000, 0);
			String ss=st.STR_NULL;
			// начало предложения
			if (cs.length()>0) {
				for (int i = cs.length()-1;i>-1;i--) {
					ss = st.STR_NULL+cs.charAt(i);
					if (st.STR_END_SENTENCE.contains(ss)) {
						// убираем лишние символы (ентер и тд) в начале предложения
						int ii = 1;
						boolean fl = true;
						try {
							while (fl) {
								ss = st.STR_NULL+cs.charAt(i+ii);
								if (!Character.isLetterOrDigit(ss.charAt(0))) {
									ii++;
									start--;
									continue;
								}
								fl = false;
							}
							
						} catch (Throwable e) {
						}
						break;
					}
					start++;
				}
			}
			// конец предложения
			cs = ic.getTextAfterCursor(4000, 0);
			if (cs.length()>0) {
				for (int i = 0;i<cs.length();i++) {
					ss = st.STR_NULL+cs.charAt(i);
					if (st.STR_END_SENTENCE.contains(ss)) {
						end++;
						break;
					}
					end++;
				}
			}
			start = ServiceJbKbd.inst.m_SelStart-start;
			end = ServiceJbKbd.inst.m_SelEnd+end;
			ic.setSelection(start, end);
			
		} catch (Throwable e) {
		}
	}

}
