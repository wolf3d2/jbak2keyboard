package com.jbak2.JbakKeyboard;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.util.Xml;
import android.widget.Toast;

import com.jbak2.JbakKeyboard.st.UniObserver;
import com.jbak2.ctrl.ProgressOperation;
import com.jbak2.words.VocabFile;
import com.jbak2.words.WordsIndex;
import com.jbak2.words.WordsService;
/** Класс для проверки и загрузки обновлений */
public class UpdateDownloader
{
    public static final String HOST = "http://jbak.ru";
    Vector<Version> m_arDownloadedUpdates;
/** Информация о версии объекта */    
    public static class Version
    {
/** Тип объекта, пока может быть только {@link UpdXMLDownload#TAG_VOCAB}*/        
        public String obj;
/** Версия */        
        public int ver;
/** Название (для словарей - язык)*/        
        public String name;
/** Размер в байтах*/        
        public int size;
/** Ссылка для загрузки */        
        public String link;
    }
/** Информация об обновлении (старая и новая версии)*/    
    public static class VersionDiff
    {
        public VersionDiff(Version o,Version n)
        {
            oldVer = o;
            newVer = n;
        }
/** Старая версия, если нет - null */        
        public Version oldVer;
/** Новая версия*/        
        public Version newVer;
    }
/** Выполняет проверку обновления словарей 
*@param callback Вызывается по окончании проверки. В param1 - Vector&lt;VersionDiff&gt;, содержащий список обновлений. Если param1==null, произошла ошибка при проверке обновлений.
*@param bLoadXML true - загружается xml со списком обновлений. false - если уже есть данные об обновлениях - xml не загружается */
    public void getVocabUpdates(final st.UniObserver callback,boolean bLoadXML)
    {
        if(bLoadXML||m_arDownloadedUpdates==null)
        {
            st.UniObserver dobs = new st.UniObserver()
            {
                @Override
                public int OnObserver(Object param1, Object param2)
                {
                    m_arDownloadedUpdates = (Vector<Version>)param1;
                    checkVocabVersions(m_arDownloadedUpdates, callback);
                    return 0;
                }
            };
            UpdXMLDownload dd = new UpdXMLDownload(dobs);
            dd.startAsync();
        }
        else
        {
            checkVocabVersions(m_arDownloadedUpdates,callback);
        }
    }
    private void checkVocabVersions(Vector <Version> ar,final st.UniObserver callback)
    {
        if(ar==null)
        {
            callback.OnObserver(ar, null);
            return;
        }
        VocabFile vf = new VocabFile();
        Vector<VersionDiff> arupd = new Vector<UpdateDownloader.VersionDiff>();
        for(Version nv:ar)
        {
            Version v = getVocabCurrentVersion(nv, vf);
            if(v!=null&&v.ver==nv.ver)
                continue;
            arupd.add(new VersionDiff(v, nv));
        }
        callback.OnObserver(arupd, null);
    }
/** Распаковка первого (единственного) файла из zip-архива в заданную папку */    
    public static void unzipFile(final Context c,final String zipPath,final String dir,final st.UniObserver callback)
    {
        final File z = new File(zipPath);
        final String fileName = z.getName();
        ProgressOperation po = new ProgressOperation(callback,c)
        {
            String m_fn = fileName;
            @Override
            public void makeOper(UniObserver obs)
            {
                try{
                    ZipFile zf = new ZipFile(z, ZipFile.OPEN_READ);
                    ZipEntry zent = null;
                    for(ZipEntry ze:Collections.list(zf.entries()))
                    {
                        if(ze.isDirectory())
                            continue;
                        zent = ze;
                        break;
                    }
                    InputStream in = new BufferedInputStream(zf.getInputStream(zent));
                    String path = dir;
                    if(!dir.endsWith(st.STR_SLASH))
                        path+='/';
                    path+=zent.getName();
                    m_total = (int) zent.getSize();
                    FileOutputStream fs = new FileOutputStream(path, false);
                    byte b[] = new byte[10000];
                    int read = 0;
                    while((read=in.read(b))>-1)
                    {
                        if(m_bCancel)
                            break;
                        fs.write(b,0,read);
                        m_position+=read;
                    }
                    in.close();
                    fs.close();
                    zf.close();
                    obs.m_param1 = path;
                }
                catch (Throwable e) {
                }
            }
            
            @Override
            public void onProgress()
            {
                String msg = m_fn+' ';
                if(m_total>0)
                {
                    msg+=fmtFileSize(c, m_position)+'/'+fmtFileSize(c, m_total);
                    m_progress.setProgress(getPercent());
                }
                m_progress.setMessage(msg);
            }
        };
        po.m_progress.setTitle(c.getString(R.string.upd_unzip));
        po.m_progress.setMessage(fileName);
        po.start();
    }
    public static void vocabIndex(final Context c,final String path,st.UniObserver callback)
    {
        final String fileName = new File(path).getName();
        ProgressOperation po = new ProgressOperation(callback,c)
        {
            WordsIndex wi;
            @Override
            public void makeOper(UniObserver obs)
            {
                try{
                    String indexPath = path+com.jbak2.words.Words.INDEX_EXT;
                    wi = new WordsIndex();
                    if(wi.makeIndexFromVocab(path))
                    {
                        wi.save(indexPath);
                    }
                    obs.m_param1 = indexPath;
                }
                catch (Throwable e) {
                }
            }
            @Override
            public void onProgress()
            {
                if(wi.m_curEnt!=null)
                {
                    long prog = wi.m_curEnt.filepos;
                    if(wi.m_filesize==0)
                        prog = 0;
                    else
                        prog=prog*100/wi.m_filesize;
                    m_progress.setProgress((int)prog);
                }
            }
        };
        po.m_progress.setTitle(c.getString(R.string.upd_index));
        po.m_progress.setMessage(fileName);
        po.start();
    }
    public static void downloadFile(final Context c,final String url,final String path,final st.UniObserver callback)
    {
        final String fileName = new File(path).getName();
        ProgressOperation po = new ProgressOperation(callback,c)
        {
            @Override
            public void makeOper(UniObserver obs)
            {
                try{
                    HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
                    urlConnection.connect();
                    int s = urlConnection.getResponseCode();
                    if(s!=200||m_bCancel)
                    {
                        return;
                    }
                    m_total = urlConnection.getContentLength();
                    
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    FileOutputStream fs = new FileOutputStream(path, false);
                    byte b[] = new byte[10000];
                    int read = 0;
                    while((read=in.read(b))>-1)
                    {
                        if(m_bCancel)
                            break;
                        fs.write(b,0,read);
                        m_position+=read;
                    }
                    in.close();
                    fs.close();
                    obs.m_param1 = path;
                }
                catch (Throwable e) {
                }
            }
            
            @Override
            public void onProgress()
            {
                if(m_progress==null)
                    return;
                String msg = fileName+' ';
                if(m_total>0)
                {
                    msg+=fmtFileSize(c, m_position)+'/'+fmtFileSize(c, m_total);
                    m_progress.setProgress(getPercent());
                }
                m_progress.setMessage(msg);
            }
        };
        po.m_progress.setTitle(c.getString(R.string.upd_download));
        po.start();
    }
    static final String fmtFileSize(Context c,long val)
    {
        return android.text.format.Formatter.formatFileSize(c,val);
    }
    Version getVocabCurrentVersion(Version newVer,VocabFile vf)
    {
        if(newVer.name==null)
            return null;
        String path = vf.processDir(WordsService.getVocabDir(), newVer.name);
        if(path == null)
            return null;
        Version v = new Version();
        v.link = path;
        v.ver = vf.getVersion();
        return v;
    }
/** Загрузка XML с сервера и ее разбор 
 * Обсервер передаёт в param1 - Vector&lt;Version&gt; в случае успеха и null - в случае неудачи
 */
    public static class UpdXMLDownload extends st.SyncAsycOper
    {
        public static final String REL_PATH = "/soft/jbakkeyboard/jkb_update.xml";
        public static final String TAG_VOCAB = "vocab";
        public static final String A_LINK = "link";
        public static final String A_NAME = "name";
        public static final String A_SIZE = "size";
        public static final String A_VERSION = "version";
        public UpdXMLDownload(UniObserver obs)
        {
            super(obs);
        }
        @Override
        public void makeOper(UniObserver obs)
        {
            String url = HOST+REL_PATH;
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) new URL(url).openConnection();
                urlConnection.connect();
                XmlPullParser xp = Xml.newPullParser();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                xp.setInput(in, null);
                int eventType = xp.getEventType();
                boolean done = false;
                Vector<Version>ar = new Vector<UpdateDownloader.Version>();
                while (eventType != XmlPullParser.END_DOCUMENT && !done)
                {
                    switch (eventType)
                    {
                        case XmlPullParser.START_DOCUMENT:
                            break;
                        case XmlPullParser.START_TAG:
                            String tag = xp.getName();
                            if(TAG_VOCAB.equals(tag))
                            {
                                Version v = processTag(xp);
                                if(v!=null)
                                    ar.add(v);
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            break;
                    }
                    eventType = xp.next();
                }
                obs.m_param1 = ar;
            }
            catch(Throwable e)
            {
            }
        }
        Version processTag(XmlPullParser xp)
        {
            try{
                Version v = new Version();
                v.obj = TAG_VOCAB;
                int c = xp.getAttributeCount();
                for(int i=0;i<c;i++)
                {
                    String att = xp.getAttributeName(i);
                    if(A_NAME.equals(att))
                        v.name = xp.getAttributeValue(i);
                    else if(A_VERSION.equals(att))
                        v.ver = Integer.decode(xp.getAttributeValue(i));
                    else if(A_LINK.equals(att))
                        v.link = xp.getAttributeValue(i);
                    else if(A_SIZE.equals(att))
                        v.size = Integer.decode(xp.getAttributeValue(i));
                }
                if(v.name!=null&&v.link!=null&&v.ver>0)
                    return v;
            }
            catch(Throwable e)
            {
            }
            return null;
        }
    }
/** Полная процедура обновления словаря. Выполняет загрузку zip-файла с сервера и в случае успеха - вызывает {@link UpdateDownloader#updateVocabUnzip(String, Context, UniObserver)}
*@param vd Сравнение версий, полученное через {@link UpdateDownloader#getVocabUpdates(UniObserver, boolean)}
*@param c Контекст
*@param endCallback Обработчик, который вызовется по окончании операции.
 */
    public static void updateVocab(final VersionDiff vd,final Context c,final st.UniObserver endCallback)
    {
        if(vd==null||vd.newVer==null)
            return;
        final String vocabZip = WordsService.getVocabDir()+"dict.zip";
        st.UniObserver cb = new st.UniObserver()
        {
            @Override
            public int OnObserver(Object param1, Object param2)
            {
                if(param1==null)
                {
                    endCallback.Observ();
                    Toast.makeText(c, "Download error", 700).show();
                    return 0;
                }
                updateVocabUnzip((String)param1, c,endCallback);
                return 0;
            }
        };
        downloadFile(c, vd.newVer.link, vocabZip, cb);
    }
/** Распаковка словаря из zipPath в папку со словарями. В случае успеха предаёт управление в {@link UpdateDownloader#updateVocabIndex(String, Context, UniObserver)}
 * @param zipPath Путь к zip-файлу
*@param c Контекст
*@param endCallback Обработчик, который вызовется по окончании операции*/
    public static void updateVocabUnzip(final String zipPath,final Context c,final st.UniObserver endCallback)
    {
        st.UniObserver cb= new st.UniObserver()
        {
            @Override
            public int OnObserver(Object param1, Object param2)
            {
                if(param1==null)
                {
                    Toast.makeText(c, "Unzip error", 700).show();
                    endCallback.Observ();
                    return 0;
                }
                new File(zipPath).delete();
                updateVocabIndex((String)param1, c,endCallback);
                return 0;
            }
        };
        unzipFile(c, zipPath, WordsService.getVocabDir(), cb);
    }
    public static void updateVocabIndex(final String path,final Context c,final st.UniObserver endCallback)
    {
        st.UniObserver cb= new st.UniObserver()
        {
            @Override
            public int OnObserver(Object param1, Object param2)
            {
                if(param1==null)
                {
                    Toast.makeText(c, "Index error", 700).show();
                    endCallback.Observ();
                    return 0;
                }
                Toast.makeText(c, "Dictionary installed", 700).show();
                endCallback.Observ();
                return 0;
            }
        };
        vocabIndex(c, path, cb);
    }
}
