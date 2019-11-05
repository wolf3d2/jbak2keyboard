package com.jbak2.words;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jbak2.JbakKeyboard.st;

public class VocabFile
{
    public static final String DEF_EXT = ".dic";
    public final String        REGEXP2  = "([A-z]{2})_v(\\d+).*?\\" + DEF_EXT;
    public final String        REGEXP3  = "([A-z]{3})_v(\\d+).*?\\" + DEF_EXT;
    Pattern                    m_pattern;
    Matcher                    m_matcher;
    int                        m_version;
    String                     m_filePath;
    int lengthLangSymbol = 0;

    public  VocabFile()
    {
    	createPattern();
    }

    public boolean match(String filename)
    {
        try
        {
            m_matcher = m_pattern.matcher(filename);
            boolean bRet = m_matcher.find();
            return bRet;
        }
        catch (Exception e)
        {
        }
        return false;
    }
    public void createPattern()
    {
    	if (lengthLangSymbol == 2)
    		m_pattern = Pattern.compile(REGEXP2, Pattern.CASE_INSENSITIVE);
    	else if (lengthLangSymbol == 3)
    		m_pattern = Pattern.compile(REGEXP3, Pattern.CASE_INSENSITIVE);
    }

    public String getLang()
    {
        return m_matcher.group(1);
    }

    public int getVersion()
    {
        try
        {
            return Integer.decode(m_matcher.group(2));
        }
        catch (Exception e)
        {
        }
        return 0;
    }
    public String processDir(String lang, File[] files)
    {
        m_filePath = null;
        m_version = -1;
        if (files == null)
            return m_filePath;
        String s = st.STR_NULL;
        int ii =0;
        try
        {
            for (File f : files)
            {
            	s = f.getName();
            	lengthLangSymbol = s.indexOf("_");
            	if (lengthLangSymbol == 0)
            		lengthLangSymbol = 2;
            	createPattern();
                if (match(f.getName()))
                {
                    String l = getLang();
                    if (l != null && l.equals(lang))
                    {
                        int v = getVersion();
                        if (v > m_version)
                        {
                            m_filePath = f.getAbsolutePath();
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return m_filePath;
    }
    public String processDir(String path, String lang)
    {
        return processDir(lang, st.getFilesByExt(new File(path), DEF_EXT));
    }
}
