package com.jbak2.ctrl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/** класс для работы с регулярными выражениями */
public class SearchRegex {
	
/** возвращает изменённую строку
 * @param text - входная строка
 * @param searchRegex - регулярное выраженитя для поиска
 * @param repl - строка замены
 * @param bcase - если true, то искать точное соответствие, с учётом регистра
 *  */
	public static String getReplaceALL(String text, String searchRegex, String repl,
			boolean bcase
			)
	{
		String ret = text;
		Pattern r;
        try{
            if ( bcase ){
                r = Pattern.compile(searchRegex, Pattern.CASE_INSENSITIVE
                		|Pattern.UNICODE_CASE
                		|Pattern.MULTILINE );
            }else{
                r = Pattern.compile(searchRegex);
            }
   		 Matcher m = r.matcher(ret);
 		m.replaceAll(repl);
 		if (m.find())
 			return ret;
        }
        catch( PatternSyntaxException e ){
            
        }
		return null;
	}
}
