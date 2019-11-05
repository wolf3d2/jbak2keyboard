package com.jbak2.words;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Vector;

import android.util.Log;
import com.jbak2.JbakKeyboard.st;

public class WordsIndex
{
    public static final int BUF_HEADER_SIZE = 18;
    public static final byte INDEX_VERSION = 1;    
    int                m_startBytes = 0;
    byte               m_delimSize  = 0;
    public IndexEntry m_curEnt = new IndexEntry();
    ByteBuffer m_index;
    public long m_filesize;
    long m_lastModified;
    final boolean testFile(String path)
    {
        try
        {
            int tl = 100;
            FileInputStream in = new FileInputStream(path);
            byte buf[] = new byte[tl];
            in.read(buf);
            if (buf.length < tl)
                return false;
            if (buf[0]==0xef&&buf[1]==0xbb&&buf[2]==0xbf)
                m_startBytes = 3;
            if (buf[0]==-17&&buf[1]==-69&&buf[2]==-65)
                m_startBytes = 3;
            for (int i = m_startBytes; i < tl; i++)
            {
                if (buf[i] == '\r')
                {
                    if (buf[i + 1] == '\n')
                        m_delimSize = 2;
                    else
                        m_delimSize = 1;
                    break;
                }
                else if (buf[i] == '\n')
                    m_delimSize = 0;
            }
//            Context c = st.c();
//            Toast.makeText(c, "Indexing completed.", 700).show();
            //st.toast("Indexing completed.");
            return true;
        }
        catch (Throwable e)
        {
        }
        return false;
    }
    final boolean processLine(CharBuffer cb)
    {
        if(cb.limit()<2)
            return false;
        char c1 = Character.toLowerCase(cb.get());
        char c2 = Character.toLowerCase(cb.get());
        if(c1<'A')
            return false;
        if(c1!=m_curEnt.first)
        {
            m_curEnt.first = c1;
            m_curEnt.second=c2<'A'?0:c2;
            return true;
        }
        else if(c1==m_curEnt.first&&c2>='A'&&c2!=m_curEnt.second)
        {
            m_curEnt.second = c2;
            return true;
        }
        return false;
    }
    final boolean processLine(String line)
    {
        if(line.length()<2)
            return false;
        char c1 = line.charAt(0);
        char c2 = line.charAt(1);
        if(c1<'A')
            return false;
        if(c1!=m_curEnt.first)
        {
            m_curEnt.first = c1;
            m_curEnt.second=c2<'A'?0:c2;
            return true;
        }
        else if(c1==m_curEnt.first&&c2>='A'&&c2!=m_curEnt.second)
        {
            m_curEnt.second = c2;
            return true;
        }
        return false;
    }
    public boolean makeIndexFromVocab(String path)
    {
        if(!testFile(path))
            return false;
        try
        {
            long time = System.currentTimeMillis();
            m_lastModified = new File(path).lastModified();
            LineFileReader fr = new LineFileReader();
            fr.open(path, "r");
            m_filesize = fr.m_fileSize;
            fr.seek(m_startBytes);
            Vector<IndexEntry> entries = new Vector<IndexEntry>();
            while (fr.nextLine())
            {
                CharBuffer cb =fr.getCharBuffer(); 
                if(processLine(cb))
                {
                    m_curEnt.filepos = (int) fr.getLineFilePos();
                    entries.add(new IndexEntry(m_curEnt));
                }
            }
            m_index = getBytes(entries);
            time = System.currentTimeMillis()-time;
            Log.w("JbakKeyboard", "Index takes: "+time+" milliseconds");
            st.toast("Index create.");
            return true;
        }
        catch (Throwable e)
        {
        }
        return false;
    }
    boolean getIndexes(IndexEntry e)
    {
        int pos = BUF_HEADER_SIZE;
        int len = m_index.capacity();
        int cnt = 0;
        e.filepos = -1;
        e.endpos = -1;
        while(pos<len)
        {
            m_index.position(pos);
            char ch = m_index.getChar();
            char ch2 = m_index.getChar();
            if(ch==e.first)
            {
                if(e.second==0)
                {
                    if(cnt>3)
                        return true;
                    setSizes(e);
                    ++cnt;
                }
                else if(ch2==e.second)
                {
                    setSizes(e);
                    return true;
                }
            }
            else
            {
                if(cnt>0)
                    return true;
            }
                
            pos = m_index.position()+4;
        }
        return false;
    }
    final void setSizes(IndexEntry e)
    {
        int p = m_index.position();
        if(e.second!=0||e.filepos<0)
            e.filepos = m_index.getInt();
        else
            m_index.getInt();
        
        int pos = m_index.position();
        if(m_index.capacity()-pos<IndexEntry.sz)
            e.endpos = (int)getFileSize();
        else
        {
            m_index.position(pos+4);
            e.endpos = m_index.getInt();
        }
        m_index.position(p);
    }
    final byte getDelimSize()
    {
        return m_index.get(8);
    }
    final long getFileLastModified()
    {
        return m_index.getLong(10);
    }
    final long getFileSize()
    {
        return m_index.getLong(0);
    }
    final byte getFileVersion()
    {
        return m_index.get(9);
    }
    void writeHeader(ByteBuffer ret)
    {
        ret.putLong(m_filesize);
        ret.put(m_delimSize);
        ret.put(INDEX_VERSION);
        ret.putLong(m_lastModified);
    }
    ByteBuffer getBytes(Vector<IndexEntry> ent)
    {
        int sz = IndexEntry.sz*ent.size()+BUF_HEADER_SIZE;
        ByteBuffer ret = ByteBuffer.allocate(sz);
        writeHeader(ret);
        for(IndexEntry e:ent)
        {
            ret.putChar(e.first);
            ret.putChar(e.second);
            ret.putInt(e.filepos);
        }
        return ret;
    }
/** Возвращает 1, если загрузка успешна, -1 - если необходимо перестроить индекс и 0 - в случае неудачи (без перестроения индекса)*/    
    final int openByFile(String indexPath,String vocabPath)
    {
        File fi = new File(indexPath);
        File fv = new File(vocabPath);
        if(!fv.exists())
            return 0;
        if(!fi.exists())
            return -1;
        boolean bLoad = load(fi);
        if(bLoad)
        {
            long fs = getFileSize();
            long lm = getFileLastModified();
            byte v = getFileVersion();
            if(fv.length()!=fs||fv.lastModified()!=lm||v!=INDEX_VERSION)
                bLoad = false;
        }
        if(bLoad)
            return 1;
        if(fi.delete())
            return -1;
        return 0;
    }
    private final boolean load(File f)
    {
        try{
            FileInputStream is = new FileInputStream(f);
            byte buf[] = new byte[(int) f.length()];
            is.read(buf);
            m_index = ByteBuffer.wrap(buf);
            return true;
        }
        catch (Throwable e) {
        }
        return false;
    }
    public final boolean save(String filePath)
    {
        try{
            FileOutputStream fs = new FileOutputStream(filePath);
            fs.write(m_index.array());
            fs.close();
            return true;
        }
        catch(Throwable e)
        {
        }
        return false;
    }
    public static class IndexEntry
    {
        public IndexEntry()
        {
            first = 0;
            second = 0;
            filepos = 0;
        }
        public IndexEntry(IndexEntry e)
        {
            first = e.first;
            second = e.second;
            filepos = e.filepos;
            endpos = e.endpos;
        }
        public IndexEntry(char f,char s,int fp)
        {
            first = f;
            second = s;
            filepos = fp;
        }
        public static final int sz = 8;
/** Первый символ*/        
        public char first;
/** Второй символ */        
        public char second;
/** Начальная позиция в файле */        
        public int  filepos;
/** Конечная позиция в файле */        
        public int endpos;
    }
}
