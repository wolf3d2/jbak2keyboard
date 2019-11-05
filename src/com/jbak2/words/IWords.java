package com.jbak2.words;

import java.nio.CharBuffer;

import com.jbak2.words.WordsIndex.IndexEntry;

/** Интерфейс для получения слов */
public abstract class IWords
{
    /** Класс, содержащий информацию об одном слове */
    public static class WordEntry
    {
        public static final int FLAG_FROM_USER_VOCAB = 0x001;
        public WordEntry()
        {}
        public WordEntry(String w,int f,int ct)
        {
            word = w;
            freq = f;
            compareType = ct;
            bword = false;
        }
/** Текст слова */        
        public String word;
/** Частота */        
        public int freq;
/** Тип совпадения с исходным словом*/        
        public int compareType;
        public int flags=0;
/** флаг, что это текущий ввод*/        
    	public boolean bword = false;
    }
    
    public boolean m_bHasNext = true;
/** Функция возвращает следующее вхождение слова или null */
    public abstract WordEntry getNextWordEntry(int minFreq,boolean bFull);
    public static class TextFileWords extends IWords
    {
        LineFileReader m_file;
        IndexEntry m_ie;
        String m_word;
        public void open(LineFileReader file,IndexEntry ie,String word)
        {
            m_word = word;
            m_file = file;
            m_ie = ie;
            m_file.seek(m_ie.filepos);
        }
        WordEntry processLine(CharBuffer line,int minFreq)
        {
            int windex = charbufIndexOf(line, ' ', true);
            if(windex<0)
                return null;
            int index = charbufIndexOf(line, ' ', false);
            CharSequence b = line.subSequence(index,line.limit());
            int f = 0;
            try {
        		f = Integer.parseInt(b.toString().trim());
        	} catch (Throwable e) { 
        		f = 0;
        		e.printStackTrace();
            }
            if(f<minFreq)
                return null;
            WordEntry we = new WordEntry();
            we.word = line.subSequence(0, windex-1).toString();
            we.freq = f;
            return we;
        }
        final int charbufIndexOf(CharBuffer cb,int chr,boolean bFirst)
        {
            int pos = -1;
            int p = cb.position();
            int lim = cb.length();
            while(cb.position()<lim)
            {
                if(cb.get()==chr)
                {
                    pos = cb.position();
                    if(bFirst)
                        break;
                }
            }
            cb.position(p);
            return pos;
        }
        @Override
        public WordEntry getNextWordEntry(int minFreq,boolean bFull)
        {
            if(!m_file.nextLine()||m_file.getLineFilePos()>=m_ie.endpos)
            {
                m_bHasNext = false;
                return null;
            }
            CharBuffer line = m_file.getCharBuffer();
            int ct = TextTools.compare(m_word, line);
            if(ct==TextTools.COMPARE_TYPE_NONE||bFull&&ct==TextTools.COMPARE_TYPE_CORRECTION)
                return null;
            WordEntry we = processLine(line,minFreq);
            if(we==null||we.freq<=minFreq&&bFull)
                return null;
            we.compareType = ct;
            return we;
        }
    }

}
