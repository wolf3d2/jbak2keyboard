package com.jbak2.words;

import java.io.File;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.jbak2.JbakKeyboard.st;
import com.jbak2.JbakKeyboard.st.UniObserver;

public class WordsService extends Service
{
    public static WordsService inst;
    public static Handler g_serviceHandler;
    public static final String EXTRA_CMD="cmd";
    public static final String EXTRA_STR1 = "str1";
    public static final String EXTRA_STR2 = "str2";
/** Команда открытия словаря. В EXTRA_STR1 должно быть название языка */    
    public static final int CMD_OPEN_VOCAB = 1;
/** Команда получения слова. В EXTRA_STR1 должно быть исходное слово. Результат возвращается в {@link #g_serviceHandler}*/    
    public static final int CMD_GET_WORDS = 2;
/** Команда сохранения слова в пользовательском словаре. В EXTRA_STR1 должно быть слово */    
    public static final int CMD_SAVE_WORD = 3;
/** Отменяет операцию получения слов, если она существует. Экстра не требуется */    
    public static final int CMD_CANCEL_VOCAB = 4;
/** Закрывает словарь, если открыт. Экстра не требуется */    
    public static final int CMD_CLOSE_VOCAB = 5;
/** Удаляет слово из пользовательского словаря */    
    public static final int CMD_DELETE_VOCAB = 6;
/** Команда сохранения слова в пользовательском словаре при
 *  расширенном обучении словаря. 
 * В EXTRA_STR1 должно быть слово */    
    public static final int CMD_EXTENDED_SAVE_WORD = 7;
    
    public static final String DEF_PATH = "vocab/";
    Words m_words;
    String m_curWord;
    String m_newWord = null;
    boolean m_bRun = false;
    @Override
    public void onCreate()
    {
        inst = this;
        new File(getVocabDir()).mkdirs();
        m_words = new Words(getVocabDir());
    }
    @Override
    public void onDestroy() 
    {
        m_words.close();
        inst = null;
    };
    public static void start(Context c)
    {
        c.startService(new Intent(c,WordsService.class));
    }
    public static void command(int cmd, String param,Context c)
    {
        Intent in = new Intent(c,WordsService.class)
            .putExtra(EXTRA_CMD, cmd)
            .putExtra(EXTRA_STR1, param);
        c.startService(in);
    }
    public static final boolean hasAnyVocab()
    {
        return st.getFilesFromDir(new File(getVocabDir()), VocabFile.DEF_EXT)!=null;
    }
    public boolean hasVocabForLang(String lang)
    {
        return m_words.m_vocabFile.processDir(m_words.m_vocabDir, lang)!=null;
    }
    public boolean canGiveWords()
    {
        return m_words.canGiveWords();
    }
    public static final String getVocabDir()
    {
        return st.getSettingsPath()+DEF_PATH;
    }
    public static boolean isSelectNow()
    {
        return inst!=null&&inst.m_bRun;
    }
    void asyncOper(final String param)
    {
        st.SyncAsycOper op = new st.SyncAsycOper(null)
        {
            @Override
            public void makeOper(UniObserver obs)
            {
                while(m_newWord!=null)
                {
                    m_words.cancelSync(false);
                    m_bRun = true;
                    String s = m_newWord;
                    m_newWord = null;
                    m_words.getWordsSync(s, g_serviceHandler);
                    m_bRun = false;
                }
            }
        };
        op.startAsync();
    }
    void cancelSelect()
    {
        if(m_bRun)
        {
        	// закоментил 16.05.18 - возможно автодоп пропадал из-за неё
        	//Words.m_word = null;
            m_newWord = null;
            m_words.cancelSync(true);
        }
        
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if(intent==null)
            return 0;
        int cmd = intent.getIntExtra(EXTRA_CMD, 0);
        final String param = intent.getStringExtra(EXTRA_STR1);
        if(cmd==CMD_OPEN_VOCAB)
        {
            cancelSelect();
            m_words.open(param);
        }
        else if(cmd==CMD_CANCEL_VOCAB)
        {
            cancelSelect();
        }
        else if(cmd==CMD_CLOSE_VOCAB)
        {
            cancelSelect();
            m_words.close();
        }
        else if(cmd==CMD_GET_WORDS)
        {
            m_newWord = param;
            if(m_bRun)
            {
                m_words.cancelSync(true);
            }
            else 
                asyncOper(param);
        }
        else if(cmd==CMD_SAVE_WORD)
        {
            m_words.getUserWords().addWord(param);
        }
        else if(cmd==CMD_EXTENDED_SAVE_WORD)
        {
        	if (m_words.isWordExist(param))
        		m_words.getUserWords().addWord(param);
        }
        else if(cmd==CMD_DELETE_VOCAB)
        {
            m_words.getUserWords().delWord(param);
        }
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
