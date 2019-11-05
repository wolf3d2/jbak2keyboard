package com.jbak2.JbakKeyboard;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Parcel;

public class Stor extends SQLiteOpenHelper
{
    static Stor inst;
    public static final int DATABASE_VERSION = 2;
    // Таблицы    
/** Таблица буфера обммена*/    
    public static final String TABLE_CLIPBOARD = "tClipbrd";
    public static final String TABLE_KEYS = "tKeys";
// Столбцы таблицы буфера обмена     
    public static final String C_TEXT   = "txt";
    public static final String C_LENGTH = "len";
    public static final String C_DATE = "dat";

// Столбцы    
    public static final String C_ID = "_id";
    public static final String C_KEYCODE = "kc";
    public static final String C_CHAR = "chr";
    public static final String C_FLAGS = "flg";
    public static final String C_ACTION = "act";
    public static final String C_BINARY = "bin";

    public int CLIPBOARD_LIMIT = 20;
    public static final String DB_FILENAME="kbstor";
    private static final String KEYS_TABLE_CREATE = 
        "CREATE TABLE IF NOT EXISTS " + TABLE_KEYS + " (" +
        C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        C_KEYCODE + " INTEGER, " +
        C_CHAR + " INTEGER, " +
        C_FLAGS + " INTEGER, " +
        C_ACTION + " INTEGER, " +
        C_TEXT + " TEXT, " +
        C_BINARY + " BLOB);";
    private static final String CLIPBOARD_TABLE_CREATE = 
        "CREATE TABLE IF NOT EXISTS " + TABLE_CLIPBOARD + " (" +
        C_TEXT + " TEXT, " +
        C_LENGTH + " INTEGER, " +
        C_DATE + " INTEGER);";

    Stor(Context context) 
    {
        super(context, DB_FILENAME, null, DATABASE_VERSION);
        try{
            CLIPBOARD_LIMIT = Integer.decode(st.pref(context).getString(st.PREF_KEY_CLIPBRD_SIZE, "20"));
        }
        catch (Throwable e) {
            CLIPBOARD_LIMIT = 20;
        }
        inst = this;
        m_db = getWritableDatabase();
        try{m_db.execSQL(CLIPBOARD_TABLE_CREATE);}catch (Exception e){}
        try{m_db.execSQL(KEYS_TABLE_CREATE);}catch (Exception e){}
    }
    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2){
    	
    }
    @Override
    public void onCreate(SQLiteDatabase db){}
    void saveClipboardString(String s, long time )
    {
    	long date = 0;
    	if (time == 0 )
    		date = System.currentTimeMillis();
    	else
    		date = time;
        try{
        	if (time == 0 ) {
        		SQLiteStatement stat = m_db.compileStatement("INSERT INTO "+TABLE_CLIPBOARD+" VALUES(?,?,?)");
        		m_db.beginTransaction();
        		stat.bindString(1, s);
        		stat.bindLong(2, s.length());
        		stat.bindLong(3, date);
       			stat.executeInsert();
        		m_db.setTransactionSuccessful();
        		m_db.endTransaction();
        	} else {
        		SQLiteStatement stat = m_db.compileStatement("UPDATE "+TABLE_CLIPBOARD+" SET "
        				+C_TEXT+" = \""+s+"\", "+C_LENGTH+" =\""+s.length()+";"
        				+"\" WHERE "+C_DATE+" = "+date);
        		m_db.beginTransaction();
       			stat.executeUpdateDelete();
        		m_db.setTransactionSuccessful();
        		m_db.endTransaction();
        	}
        }
        catch (Throwable e) {
        }
    }
    // пока не работает
    void updateClipboardString(String s, long time )
    {
    	long date = 0;
    	SQLiteStatement stat;
        try{
        	if (time == 0 ) {
        		date = System.currentTimeMillis();
                stat = m_db.compileStatement("INSERT INTO "+TABLE_CLIPBOARD+" VALUES(?,?,?)");
        	}
        	else {
        		date = time;
                stat = m_db.compileStatement("INSERT INTO "+TABLE_CLIPBOARD+" VALUES(?,?,?)");
        	}
//            SQLiteStatement stat = m_db.compileStatement("UPDATE "+TABLE_CLIPBOARD+" SET (?,?,?)");
            m_db.beginTransaction();
            stat.bindString(1, s);
            stat.bindLong(2, s.length());
            stat.bindLong(3, date);
            stat.executeInsert();
            m_db.setTransactionSuccessful();
            m_db.endTransaction();
        }
        catch (Throwable e) {
        }
    }
/** Запускает запрос sql на выполнение, в случае ошибки возвращает false и пишет в лог */   
    boolean runSql(String sql)
    {
        try{
            m_db.execSQL(sql);
        }
        catch(Throwable e)
        {
            return false;
        }
        return true;
    }
    public Cursor getClipboardCursor()
    {
        try
        {
            Cursor cursor = m_db.query(TABLE_CLIPBOARD, null, null, null, null, null, null);
            if(cursor.moveToLast())
                return cursor;
            cursor.close();
        }
        catch (Throwable e) {
        }
        return null;
    }
/** Удаляет вхождения буфера обмена по датам. Если date2==0 - удаляет только по date*/  
    void removeClipboardByDate(long date,long date2)
    {
        String sql = "DELETE FROM "+TABLE_CLIPBOARD+" WHERE "+C_DATE+"="+date;
        if(date2>0)
        {
            sql+=" OR "+C_DATE+"<="+date2;
        }
        runSql(sql);
    }
    public boolean clearClipboard()
    {
        return runSql("DELETE FROM "+TABLE_CLIPBOARD);
    }
/** Удаляет строки, совпадающие с txt, проверяет */ 
    public boolean checkClipboardString(String txt)
    {
    	
        long date = 0;
        long date2=0;
        try
        {
            Cursor cursor = m_db.query(TABLE_CLIPBOARD, null, null, null, null, null, null);
            if(cursor==null)
                return false;
            if(!cursor.moveToLast())
            {
                cursor.close();
//                Cursor c = getClipboardCursor();
//                long ll = new Long(c.getLong(2));
//                saveClipboardString(txt, ll);
                saveClipboardString(txt, 0);
                return true;
            }
            int count = 1;
            do
            {
                long len = cursor.getLong(1); // C_LENGTH
                if(len==txt.length())
                {
                    String s = cursor.getString(0);
                    if(txt.equals(s))
                    {
                        date = cursor.getLong(2);// Нашли одинаковую строку, удаляем
                        --count;
                    }
                }
                if(count==CLIPBOARD_LIMIT)
                {
                    date2 = cursor.getLong(2);
                }
                ++count;
            }while(cursor.moveToPrevious());
            cursor.close();
            if(date>0||date2>0)
                removeClipboardByDate(date, date2);
            saveClipboardString(txt, 0);
        }
        catch (Throwable e) {
        }
        return true;
    }
    static byte[] getBytesFromIntent(Intent in)
    {
        if(in==null)
            return null;
        Parcel pars = Parcel.obtain();
        in.writeToParcel(pars, 0);
        return pars.marshall();
    }
    static Intent getIntentFromBytes(byte[] ar)
    {
        if(ar==null)
            return null;
        Parcel parc = Parcel.obtain();
        Intent in = new Intent();
        parc.unmarshall(ar, 0, ar.length);
        parc.setDataPosition(0);
        in.readFromParcel(parc);
        return in;
    }
    SQLiteDatabase m_db;
}
