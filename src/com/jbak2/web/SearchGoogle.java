package com.jbak2.web;

import java.net.URLEncoder;

import com.jbak2.JbakKeyboard.ServiceJbKbd;
import com.jbak2.JbakKeyboard.st;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.view.inputmethod.InputConnection;

public class SearchGoogle 
{
	public static String SEARCH_PREFICS_URL = "http://www.google.com/search?q=";
	/** поиск в гугле (с интентом)
	 * @param type
	 type = 0 - поиск выделенного <br>
	 type = 1 - поиск скопированного <br>
	 */
	@SuppressLint("NewApi")
	public static void search(int type){
    	CharSequence sel = null;
    	switch (type)
    	{
    	case 0:
            InputConnection ic = ServiceJbKbd.inst.getCurrentInputConnection();
        	sel = ic.getSelectedText(0);
    		break;
    	case 1:
        	sel = st.getClipboardCharSequence();
    		break;
    	}
    	if (sel == null)
    		return;
    	if (ServiceJbKbd.inst==null)
    		return;
    	String enc;
		try {
			enc = URLEncoder.encode(sel.toString(),st.STR_UTF8);
		} catch (Throwable e) {
			enc = null;
		}
    	if (enc == null)
    		return;
    	st.hidekbd();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(SEARCH_PREFICS_URL+enc));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ServiceJbKbd.inst.startActivity(intent);
	}
	/** возвращает поисковый url из строки text */
	public static String getSearchUrl(String text){
		try {
			text = URLEncoder.encode(text,st.STR_UTF8);
		} catch (Throwable e) {
    		return text;
		}
		return SEARCH_PREFICS_URL+text;
	}

}
	