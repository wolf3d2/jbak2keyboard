package com.jbak2.words;

import java.nio.CharBuffer;

/** Утилиты для операций с текстом - сравнение, преобразование регистра*/
public class TextTools
{
/** Регистр - все буквы в нижнем регистре */    
    public static final int CASE_LOWER      =   1;
/** Регистр - слово начинается с большой буквы */    
    public static final int CASE_UPPER      =   2;
/** Регистр - все буквы большие */    
    public static final int CASE_CAPS_LOCK     = 3;
/** Значение compare - текст в исходной строке полностью совпадает с буфером для сравнения */
    public static final int COMPARE_TYPE_EQUAL = 1;
/** Значение compare - начало исходного текста совпадает с началом строки для сравнения */    
    public static final int COMPARE_TYPE_COMPLETION = 2;
/** Значение compare - текст в исходной строке отличается от буфера не более, чем на CORRECT_SIZE букв*/
    public static final int COMPARE_TYPE_CORRECTION = 3;
/** Значение compare - тексты не совпадают */    
    public static final int COMPARE_TYPE_NONE       = 4;
    
/** Максимальное количество несовпадений для типа {@link #COMPARE_TYPE_CORRECTION}*/    
    public static int CORRECT_SIZE = 1;
    public static final int getTextCase(String text)
    {
        if(text.length()<1)
        	return CASE_LOWER;
        int ret = CASE_LOWER;
        if(Character.isUpperCase(text.charAt(0)))
        {
            ret = CASE_UPPER;
            if(text.length()>1&&Character.isUpperCase(text.charAt(1)))
            {
                ret = CASE_CAPS_LOCK;
            }
        }
        return ret;
    }
    public static final String changeCase(String text,int cs)
    {
        if(text.length()==0)return text;
        int c = getTextCase(text);
        if(cs==c)
            return text;
        switch (cs)
        {
            case CASE_LOWER:
                return text.toLowerCase();
            case CASE_CAPS_LOCK:
                return text.toUpperCase();
            default:
                char ar[] = text.toLowerCase().toCharArray();
                ar[0]=Character.toUpperCase(ar[0]);
                return new String(ar);
        }
    }
/** Сравнивает символ c1 в нижнем регистре с символом c2 - в верхнем регистре
*@param c1 Первый символ в нижнем регистре
*@param c2 Второй символ, в любом регистре
*@return true, если символы совпадают, иначе false */
    public static final boolean isCharEqual(char c1,char c2)
    {
        if(c1=='е'&&c2=='ё'||c2=='е'&&c1=='ё')
            return true;
        return c2==c1;
    }
/** Сравнивает строку str c буфером cb
*@param str Исходная строка для сравнения в нижнем регистре
*@param cb Буфер для сравнения из словаря, после слова идет пробельный символ
*@return Возвращает одно из значений COMPARE_TYPE
**/
    public static final int compare(String str,CharBuffer cb)
    {
        int p = cb.position();
        int slen = str.length();
        int lim = cb.length();
        int pos = 0;
        int corr = 0;
        int wordLen = 0; // Длина слова, неполная, если строка короче (но по крайней мере, больше на 1)
        while(cb.position()<lim)
        {
            char ch = cb.get();
            if(ch<=0x20)
                break;
            ++wordLen;
            if(pos>=slen)
                break;
            ch = Character.toLowerCase(ch);
            if(!isCharEqual(str.charAt(pos++),ch))
            {
                corr++;
                if(corr>CORRECT_SIZE)
                    break;
            }
        }
        cb.position(p);
        int ds = wordLen-slen; //<0, если слово в словаре короче
        if(corr>CORRECT_SIZE||Math.abs(ds)>CORRECT_SIZE)
            return COMPARE_TYPE_NONE;
        if(ds==0)
        {
            return corr==0?COMPARE_TYPE_EQUAL:COMPARE_TYPE_CORRECTION;
        }
        else if(corr==0&&ds<0)// исходное слово длинее, чем в словаре, хотя все буквы совпали
        {
            if(Math.abs(ds)>CORRECT_SIZE)return COMPARE_TYPE_NONE;
            return COMPARE_TYPE_CORRECTION;
        }
        else if(corr==0&&ds>0)
            return COMPARE_TYPE_COMPLETION;
        return COMPARE_TYPE_NONE;
    }
    /** Сравнивает строку str c буфером str1.
    *@param str Исходная строка для сравнения в нижнем регистре
    *@param str1 Слово для сравнения
    *@return Возвращает одно из значений COMPARE_TYPE
    */
    public static final int compare(String str,String str1)
    {
        int len = str.length();
        int len1 = str1.length();
        int corr = 0;
        for(int i=0;i<len&&i<len1;i++)
        {
            char c = str.charAt(i);
            char c1 = Character.toLowerCase(str1.charAt(i));
            if(!isCharEqual(c, c1))
            {
                corr++;
                if(corr>CORRECT_SIZE)
                {
                    return COMPARE_TYPE_NONE;
                }
            }
        }
        if(corr==0)
        {
            if(len1==len) return COMPARE_TYPE_EQUAL;
            else if(len1>len) return COMPARE_TYPE_COMPLETION;
        }
        if(len-len1<=CORRECT_SIZE&&corr<=CORRECT_SIZE) return COMPARE_TYPE_CORRECTION;
        return COMPARE_TYPE_NONE;
    }
}
