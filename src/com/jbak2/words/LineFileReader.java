package com.jbak2.words;

import java.io.ByteArrayInputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CoderResult;
/** Класс для построчного чтения файла 
 * */
/** TODO: Некорректно работает со строками, если длина одной строки в байтах > m_blockSize*/
import java.nio.charset.CharsetDecoder;
/** TODO: Если разделитель \r\n и позиция следующего считанного блока начнется со \n - вернет пустую строку */
public class LineFileReader
{
/** Открытый файл */    
    RandomAccessFile m_file;
/** Файловый канал */    
    FileChannel m_fc;
/** Буфер для чтения */    
    ByteBuffer m_buf;
/** Размер файла */    
    long m_fileSize=0;
/** Текущая позиция в файле */    
    long m_filePos = 0;
/** Размер текущего буфера со считанными байтами */    
    int m_bufSize = 0;
/** Начало текущей строки */    
    int m_lineStart = -1;
/** Конец текущей строки */    
    int m_lineEnd = 0;
/** Размер блока для чтения */    
    public int m_blockSize = 10240;
    ByteArrayInputStream m_readStream;
    byte m_tmpArray[] = new byte[100];
    int m_savedPos = -1;
    int m_savedLimit = -1;
    CharsetDecoder m_decoder;
    CharBuffer m_decBuffer;
    public boolean open(String path,String accessType)
    {
        try{
            m_file = new RandomAccessFile(path, accessType);
            m_fc = m_file.getChannel();
            m_fileSize = m_file.length();
            m_decoder = Charset.forName("UTF-8").newDecoder();
            m_decBuffer = CharBuffer.allocate(100);
            return true;
        }
        catch (Throwable e) {
        }
        return false;
    }
    void setReadBlock(ByteBuffer buf)
    {
        m_buf = buf;
        m_blockSize = buf.capacity();
    }
    public final RandomAccessFile getFile()
    {
        return m_file;
    }
/** Считывает порцию файла в m_buf
*@param lastPartSize Размер последнего куска, где не найден разделитель. Если >0 - копируется в начало m_buf
*@return true - считали успешно, false - неудачно */
    final boolean read(int lastPartSize)
    {
        try
        {
            m_buf.position(0);
            if(lastPartSize!=0)
            {
                byte b[] = _barray(lastPartSize);
                m_buf.position(m_bufSize-lastPartSize);
                m_buf.get(b,0,lastPartSize);
                m_buf.position(0);
                m_buf.put(b,0,lastPartSize);
                m_buf.position(lastPartSize);
            }
            m_filePos = m_fc.position()-lastPartSize;
            m_bufSize = m_fc.read(m_buf);
            if(m_bufSize==0)
                return false;
            m_bufSize+=lastPartSize;
            m_buf.position(0);
            m_buf.limit(m_bufSize);
            return true;
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        return false;
    }
/** Проверка байта на принадлежность к концу строки, если надо - смещает позицию в буфере 
*@param b Байт для проверки
*@return Возвращает true, если байт подходящий */
    final boolean checkByte(byte b)
    {
        if(b=='\r')
        {
            m_lineEnd = m_buf.position()-1;
            b = m_buf.get();
            if(b!='\n')
                m_buf.position(m_buf.position()-1);
            return true;
        }
        else if(b=='\n')
        {
            m_lineEnd = m_buf.position()-1;
            return true;
        }
        return false;
    }
/** Выполняет поиск конца строки, возвращает true в случае успеха */    
    final boolean searchLine()
    {
        m_lineStart = m_buf.position();
        if(m_lineStart+m_filePos>=m_fileSize)
            return false;
        try{
        while (true)
        {
            if(checkByte(m_buf.get()))
                return true;
        }
        }
        catch (Throwable e) {
        }
        if(m_filePos+m_bufSize==m_fileSize)
        {
            // Если достигли конца файла
            m_lineEnd = m_buf.position();
            return true;
        }
        // Нужно считать следующую порцию из файла
        if(read(m_bufSize-m_lineStart))
            return searchLine();
        return false;
    }
/** Возвращает позицию начала текущей строки в файле (в байтах)*/    
    public final int getLineStart()
    {
        return m_lineStart;
    }
/** Возвращает позицию начала текущей строки в файле (в байтах)*/    
    public final long getLineFilePos()
    {
        return m_filePos+m_lineStart;
    }
/** Возвращает размер текущей строки в байтах 
*@return Размер текущей строки в байтах */
    public final int getLineSize()
    {
        return m_lineEnd-m_lineStart;
    }
    public final int searchByte(byte b,boolean bFirst)
    {
        selectLine();
        int lim = m_buf.limit();
        int pos = -1;
        while(m_buf.position()<lim)
        {
            if(m_buf.get()==b)
            {
                if(bFirst)
                {
                    restorePos();
                    return m_buf.position();
                }
                else 
                    pos = m_buf.position();
            }
        }
        restorePos();
        return pos;
    }
/** Возвращает данные в виде CharBuffer
*@return Массив символов текущей строки. position всегда 0, limit - размер сконвертированных символов*/
    public final CharBuffer getCharBuffer()
    {
        int sz = getLineSize();
        setPos(m_lineStart, m_lineEnd);
        CharBuffer cb = _cbuf(sz);
        m_decBuffer.position(0);
        m_decBuffer.limit(sz);
        CoderResult cr = m_decoder.decode(m_buf, cb, false);
        restorePos();
        int p = m_decBuffer.position();
        m_decBuffer.position(0);
        m_decBuffer.limit(p);
        return m_decBuffer;
    }
/** Возвращает текущую линию в виде строки */    
    public final String getString()
    {
        return getCharBuffer().toString();
    }
    public final int getLineBytes(byte b[])
    {
        int sz = getLineSize();
        int pos = m_buf.position();
        m_buf.position(m_lineStart);
        m_buf.get(b, 0, sz);
        m_buf.position(pos);
        return sz;
    }
    public final boolean nextLine()
    {
        if(m_buf==null)
        {
            m_buf = ByteBuffer.allocateDirect(m_blockSize);
        }
        if(m_lineStart<0)
        {
            if(!read(0))
                return false;
        }
        return searchLine();
    }
/** Позиционирует файл в позицию pos */    
    public final boolean seek(long pos)
    {
        try{
            m_fc = m_fc.position(pos);
            m_lineStart = -1;
            return true;
        }
        catch (Throwable e) {
        }
        return false;
    }
    final byte[] _barray(int size)
    {
        if(m_tmpArray.length<size)
            m_tmpArray = new byte[size];
        return m_tmpArray;
    }
    final CharBuffer _cbuf(int size)
    {
        if(m_decBuffer.capacity()<size)
            m_decBuffer = CharBuffer.allocate(size);
        return m_decBuffer;
    }
    public final void setPos(int pos,int limit)
    {
        m_savedPos = m_buf.position();
        m_savedLimit = m_buf.limit();
        m_buf.position(pos);
        m_buf.limit(limit);
    }
    public final boolean restorePos()
    {
        if(m_savedPos<0)
            return false;
        if(m_savedLimit>-1)
        {
            m_buf.limit(m_savedLimit);
        }
        m_buf.position(m_savedPos);
        m_savedPos = -1;
        m_savedLimit = -1;
        return true;
    }
    public final void selectLine()
    {
        setPos(m_lineStart, m_lineEnd);
    }
}
