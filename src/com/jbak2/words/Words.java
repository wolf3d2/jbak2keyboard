package com.jbak2.words;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import android.os.Handler;

import com.jbak2.JbakKeyboard.st;
import com.jbak2.words.IWords.WordEntry;
import com.jbak2.words.WordsIndex.IndexEntry;

/** Класс для поиска слов в словаре */
public class Words
{
	/** слова в текущей выборке из основного словаря */
	public Vector<WordEntry> arword = new Vector<WordEntry>();
    public String m_name;
    public static String m_word = null;
//    public int m_wordsLimit = 20;
    
    public static final String INDEX_EXT               = ".index";
/** Сообщение, которое получает Handler. В этом сообщении объект - Vector&lt;WordEntry&gt;*/    
    public static final int MSG_GET_WORDS    = 0xfeded;
/** Словарный индекс */    
    WordsIndex m_index;
/** Файл словаря*/    
    LineFileReader m_file;
/** Обработчик, получающий сообщения о процессе обработки */    
    Handler m_handler;
    UserWords m_userWords;
    VocabFile m_vocabFile;
    public String m_vocabDir;
    public Words(String vocabDir)
    {
        m_vocabDir = vocabDir;
        m_vocabFile = new VocabFile();
        m_userWords = new UserWords();
        m_userWords.open(m_vocabDir+UserWords.FILENAME);
    }
    UserWords getUserWords()
    {
        return m_userWords;
    }
    public boolean open(String name)
    {
        if(name.equals(m_name)&&canGiveWords())
            return true;
        m_userWords.setCurTable(name);
        m_name = name;
        String path = m_vocabFile.processDir(m_vocabDir, name);
        String indexPath = path+INDEX_EXT;
        if(path==null)
            return false;
        m_index = new WordsIndex();
        int ret = m_index.openByFile(indexPath, path);
        if(ret==0)
            return false;
        if(ret<0)
        {
            if(!m_index.makeIndexFromVocab(path))
            {
                m_index = null;
                return false;
            }
            m_index.save(indexPath);
        }
        try{
            m_file = new LineFileReader();
            m_file.open(path, "r");
        }
        catch (Throwable e) {
            m_file = null;
            return false;
        }
        return canGiveWords();
    }
    public void close()
    {
        m_name = null;
        m_index = null;
    }
/** Возвращает true, если возможно получить слова (словарь открыт на чтение и есть индекс) */
    public final boolean canGiveWords()
    {
        return m_index!=null;
    }
    IndexEntry m_ie = new IndexEntry();
    /** Основная функция для получения набора слов из словаря 
    *@param word Часть слова для поиска 
    *@param handler Обработчик, в который прийдет набор слов в случае успешного выполнения. Тип сообщения - {@link #MSG_GET_WORDS}
     */
    public void getWordsSync(String word,Handler handler)
    {
        if(word==null||word.length()<1||!canGiveWords())
            return;
        m_handler = handler;
        m_word = word;
        m_ie.first = Character.toLowerCase(m_word.charAt(0));
        m_ie.second = word.length()>1?Character.toLowerCase(m_word.charAt(1)):0;
        arword = null;
        arword = readWords(m_word);
        if(arword==null)
            return;
        m_handler.removeMessages(MSG_GET_WORDS);
        m_handler.sendMessage(m_handler.obtainMessage(MSG_GET_WORDS, arword));
    }
/** Отмена синхронной обработки */    
    void cancelSync(boolean bCancel)
    {
//        arword = null;
        m_bCancel = bCancel;
    }
/** Если true - текущая задача прервется, как только */    
    boolean m_bCancel = false;
/** Сравнение слов по частоте для сортировки */    
    Comparator<WordEntry> m_wordsComparator = new Comparator<WordEntry>()
    {
        @Override
        public int compare(WordEntry object1, WordEntry object2)
        {
            if(object1.compareType>object2.compareType)return 1;
            if(object1.compareType<object2.compareType)return -1;
            if(object1.freq<object2.freq)return 1;
            if(object1.freq>object2.freq)return -1;
            return 0;
        }
        
    };
    final int getMinFreq(Vector<WordEntry> ar)
    {
        int ret = 10000000;
        for(WordEntry we:ar)
        {
            if(we.freq<ret)
                ret = we.freq;
        }
        return ret==10000000?1:ret;
    }
/** Возвращает позицию элемента с минимальной частотой в массиве ar 
*@param ar Массив слов
*@return Возвращает позицию элемента с минимальной частотой */
    final int getMinFreqPos(Vector<WordEntry> ar)
    {
        int ret = 10000000;
        int pos = -1;
        int i = 0;
        for(WordEntry we:ar)
        {
            if(we.freq<ret)
            {
                ret = we.freq;
                pos = i;
            }
            ++i;
        }
        return pos;
    }
/** Устанавливает слово we в позицию с минимальной частотой 
*@param ar Массив слов
*@param we Новое слово
*@return Возвращает текущую минимальную частоту в массиве */
    final int setAtMinFreq(Vector<WordEntry>ar,WordEntry we)
    {
        int pos = getMinFreqPos(ar);
    	WordEntry w1 = null;
        if(pos>-1)
       		ar.set(pos, we);
        return getMinFreq(ar);
    }
    String m_ret[] = new String[st.ac_list_value];
/** Основная функция для считывания из словаря 
*@param word Пользовательское слово
*@return Массив подходящих слов в словаре */
    private Vector<WordEntry> readWords(String word)
    {
        try{
            int cs = TextTools.getTextCase(word);
            String lc = word.toLowerCase();
            Vector<WordEntry> ar = new Vector<WordEntry>(st.ac_list_value); 
            int minF = 0;
            boolean bFull = false;
            for(int i=0;i<2;i++)
            {
                IWords wi = null;
                if(i==0)
                {
               		wi = m_userWords.getWordsReader(lc);
                }
                else
                {
                    if(/*m_ie.second==0||*/!m_index.getIndexes(m_ie))
                        break;
                    IWords.TextFileWords tfv = new IWords.TextFileWords();
                    tfv.open(m_file, m_ie, lc);                    
                    wi = tfv;
                }
                if(wi==null&&i==0)
                    continue;
                    
                while(!m_bCancel)
                {
                    WordEntry we = wi.getNextWordEntry(minF, bFull);
                    if(we==null)
                    {
                        if(wi.m_bHasNext)
                            continue;
                        else
                            break;
                    }
                    if(!bFull)
                    {
                   		ar.add(we);
                        bFull = ar.size()==st.ac_list_value;
                        if(bFull)
                            minF = getMinFreq(ar);
                    }
                    else if(we.freq>minF)
                    {
                        setAtMinFreq(ar, we);
                    }
                }
                if(m_bCancel)
                    return null;
                if(bFull)
                    break;
            }
            return postProcessWords(ar,cs);
        }
        catch (Throwable e) 
        {
            e.printStackTrace();
        }
        return null;
    }
/** Сортировка элементов, выставление регистра cs 
*@param ar Массив для обработки
*@param cs Изначальный регистр слова
*@return Возвращает обработанный массив */
    Vector<WordEntry> postProcessWords(Vector<WordEntry>ar,int cs)
    {
        Collections.sort(ar, m_wordsComparator);
        if(ar.size()<1||ar.get(0).compareType!=TextTools.COMPARE_TYPE_EQUAL)
        {
                WordEntry we = new WordEntry();
                we.word = m_word;
                we.freq = 1;
                we.compareType = TextTools.COMPARE_TYPE_NONE;
                we.bword = true;
                ar.add(0,we);
        	}
        for(WordEntry we1:ar)
            we1.word = TextTools.changeCase(we1.word, cs);
// возвращает список слов
        return ar;
    }
    int arpos = 0;
    int arlen = 0;
	WordEntry mwe = null;
    /** проверяет, есть ли слово в текущей выборке в словаре*/
    boolean isWordExist(String word){
    	if (word==null)
    		return false;
    	if (arword==null)
    		return false;
    	arlen = arword.size();
    	if (arlen>0){
    		arpos = 0;
    		while (arpos<arlen) {
    	    	if (arword==null)
    	    		return false;
    	    	try {
        	    	arlen = arword.size();
        		} catch (Throwable e) {
                    return false;
                }
    	    	if (arpos>=arlen)
    	    		return false;
    	    	try {
        	    	mwe = arword.get(arpos);
        		} catch (Throwable e) {
                    return false;
                }
    			if (mwe.word.compareToIgnoreCase(word) == 0&&!mwe.bword)
    				return true;
    			arpos++;
    		}
//    		try {
//        		for (WordEntry we:arword){
//        			if (we.word.compareToIgnoreCase(word) == 0&&!we.bword)
//        				return true;
//        		}
//    		} catch (Throwable e) {
//                return false;
//            }
    	}
    	return false;
    }
}