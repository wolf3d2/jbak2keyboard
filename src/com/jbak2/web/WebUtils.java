package com.jbak2.web;

import com.jbak2.JbakKeyboard.st;

import android.text.TextUtils;

public class WebUtils 
{
	public static final String HTTP = "http://";
	public static final String HTTPS = "https://";

	/** Проверяем text и если надо, создаём поисковый url */    
	public static String createUrl(String text)
	{
		if(TextUtils.isEmpty(text)) {
			text = st.STR_NULL;
			return text;
		}
		if(!isWebAddr(text))
			return  SearchGoogle.getSearchUrl(text);
		else if(hasSchemeHTTP(text))
		{
			return  text;
		}
//		else if(hasSchemeHTTP(text))
//		{
//			return  text;
//		}
		if(!text.startsWith(HTTP)&&!text.startsWith(HTTPS))
			text=HTTP+text;
		
		return text;
	}
	/** Проверяет url - адрес это, или нет */    
	public static boolean isWebAddr(String text)
	{
		if(TextUtils.isEmpty(text))
			return false;
		if(hasSchemeHTTP(text))
			return  true;
		boolean bbb = text.indexOf(' ')<0&&text.indexOf('.')>-1;
		return text.indexOf(' ')<0&&text.indexOf('.')>-1;
	}
	/** Проверяет url  */    
	public static boolean hasSchemeHTTP(String url)
	{
		int index = url.indexOf(':');
		if(index>-1&&index<30)
		{
			String s = url.substring(0,index);
			boolean isEng = true;
			for(int i=0;i<s.length();i++)
			{
				char c = s.charAt(i);
				if(!('a'<=c&&c<='z'||'A'<=c&&c<='Z'))
				{
					isEng = false;
					break;
				}
				if(isEng)
					return true;
			}
		}
		return false;
	}

}
