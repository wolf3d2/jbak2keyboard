package com.jbak2.Dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import com.jbak2.JbakKeyboard.R;
import com.jbak2.JbakKeyboard.st;
import com.jbak2.ctrl.SameThreadTimer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public abstract class DlgFileExplorer 
{
	public static int TYPE_ALL = 0; 
	public static int TYPE_PICTURE = 1; 
	int SIZE_BOUNDS_IMAGE = 100;
	static int cur_max_hw = 0;
	/** показываем картинку в большом окне */
	DlgImage di = null; 
	/** присваеваем время пока скроллится. Если 0, то читаем картинки */
	long scroll_time = 0;
	/** период миллисекунд, после чего считается что скроллинг остановился */
	int SCROLL_PERIOD = 100;
    static SameThreadTimer m_tm;
	TextView tvcurpath = null;
	ListView lv = null;
	Button bfilt = null;
	Button bhome = null;
	Button bclose = null;
	/** Сторонний обработчик, который был передан в функции {@link #show()}*/    
    st.UniObserver m_MenuObserver;
	Adapt m_adapter = null;
	String mtitle = null;
	static File root = null;
	File cur_dir = null;
	File last_dir = null;
	File parent_dir = null;
	ArrayList<FileInfo> aritems = new ArrayList<FileInfo>();
	public String dir = st.STR_NULL;
	public static String TEXT_FOLD_BEG = "📁["; 
	public static String TEXT_FOLD_END= "]"; 
	public static String TEXT_FOLD_PARENT = "📂[..]"; 
	
	public static int SELECT_FILE = 0; 
	public static int SELECT_FOLDER = 1; 

	public static Activity mact = null;
	public static DlgFileExplorer inst = null;
    static View m_mainview;
	public static String[] arext = null;
	public static String[] arexttmp = null;
	String oldpath = null;
	/** режим выбора - директория или файл */
	int sel = 0;
	/** маленькая картинка */
	Drawable m_draw = null;	
	public static final String[] PICTURE_EXT = new String[]
	{
		".png",
		".bmp",
		".jpg",
		".jpeg",
	};
	int mtype = 0;
	
	/**  Файл эксплорер.
	 * @param title- заголовок окна, если null, то "Файловый менеджер" 
	 * @param ext - массив отображаемых расширений файлов, если null, 
	 * то выводим весь список
	 * @param roottDir - если null, значит выводить домашнюю папку
	 * @param oldPath - если не null, то выводить в корневом меню кнопку с текстом
	 * указанны здесь при нажатии и на неё, посылать в onSelected не файл, а null
	 * @param type - тип файлов - какие дополнительные папки выводить в корневой папке   
	 * */
	public DlgFileExplorer(Activity c, String title, String[] ext, int type, File roottDir, 
			String oldPath, int selectDirOrFile){
		if (title==null)
			mtitle = c.getString(R.string.fi_title);
		else
			mtitle=title;
		mact = c;
		root = roottDir;
		sel = selectDirOrFile;
		arext = ext;
		oldpath = oldPath;
		mtype = type;
		inst = this;
		init();
        
	}
    @SuppressLint("NewApi")
	public void init()
    {
    	try {
        	cur_max_hw = Math.min(st.getDisplayHeight(mact), st.getDisplayWidth(mact));
			
		} catch (Throwable e) {
			cur_max_hw = 300;
		}
		m_mainview = mact.getLayoutInflater().inflate(R.layout.dialog_file_explorer, null);
		
		tvcurpath = (TextView) m_mainview.findViewById(R.id.fe_lastpath);
		tvcurpath.setVisibility(View.VISIBLE);
		
		bclose= (Button) m_mainview.findViewById(R.id.fe_close);
		bclose.setVisibility(View.VISIBLE);
		bclose.setOnClickListener(m_listener);
		
		bhome = (Button) m_mainview.findViewById(R.id.fe_up);
		bhome.setOnClickListener(m_listener);
		bhome = (Button) m_mainview.findViewById(R.id.fe_down);
		bhome.setOnClickListener(m_listener);

		bhome = (Button) m_mainview.findViewById(R.id.fe_home);
		bhome.setVisibility(View.VISIBLE);
		bhome.setOnClickListener(m_listener);
		
		bfilt = (Button) m_mainview.findViewById(R.id.fe_filter);
		bfilt.setVisibility(View.VISIBLE);
		bfilt.setOnClickListener(m_listener);
		bfilt.setOnLongClickListener(m_longListener);
	
		scroll_time = 0;
        if(m_tm!=null)
            m_tm.cancel();
        // длительность показа окна
        m_tm = new SameThreadTimer(4000,1000)
        {
            @Override
            public void onTimer(SameThreadTimer timer)
            {
                if(scroll_time == 0) {
                	return;
                }
                else if(scroll_time<=System.currentTimeMillis()-(long)SCROLL_PERIOD) {
                	scroll_time=0;
                	m_adapter.notifyDataSetChanged();
                    m_tm.cancel();
                	
                }
            }
        };
        m_tm.start();

		
		lv = (ListView) m_mainview.findViewById(R.id.fe_list);
//		lv.setOnScrollChangeListener(new OnScrollChangeListener() {
//			
//			@Override
//			public void onScrollChange(View arg0, int arg1, int arg2, int arg3, int arg4) {
//				scroll_time = System.currentTimeMillis();
////				if (m_tm!=null)
////					m_tm.start();
//			}
//		});
		lv.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				scroll_time = System.currentTimeMillis();
				
			}
		});
		if(arext!=null)
			setDrawableFilter(true);
    }
    public void show()
    {
    	st.hidekbd();
    	changeArrayList();
        m_MenuObserver = getClickItemObserver();
        lv = (ListView)m_mainview.findViewById(R.id.fe_list);
        m_adapter = new Adapt(mact, this);
        lv.setAdapter(m_adapter);
//        int rlist = R.layout.tpl_instr_list;
//        final ArrayAdapter<FileInfo> ar = new ArrayAdapter<String>(mact, 
//                                                    rlist,
//                                                    aritems
//                                                    );
        Dlg.customViewAndMenu(mact, m_mainview, m_adapter, mtitle, new st.UniObserver()
        {
            @Override
            public int OnObserver(Object param1, Object param2)
            {
                return 0;
            }
        });
    }
    public st.UniObserver getClickItemObserver()
    {
    	st.UniObserver obs = new st.UniObserver() 
    	{
			
			@Override
			public int OnObserver(Object param1, Object param2) {
				FileInfo fi = (FileInfo)param1;
				boolean longClick = ((Boolean)param2).booleanValue();
				if (!longClick) {
					// короткий клик
					if (fi.file!=null) {
						if (fi.file.isDirectory()) {
							last_dir = fi.file;
							if(fi.file.getAbsoluteFile().toString().compareTo("/")!=0)
								st.pref(mact).edit().putString(st.FILE_EXPLORER_LAST_DIR, last_dir.getAbsolutePath()).commit();
							cur_dir = fi.file;
							parent_dir = cur_dir.getParentFile();
							if (root!=null) {
								if (!parent_dir.getAbsolutePath().contains(root.getAbsolutePath()))
									cur_dir = null;
								
							}
							changeArrayList();
	//kk
						} else {
							if (sel == SELECT_FILE) {
								onSelected(fi.file);
								close();
								}
						}
						
					}
				} 
				// длинное нажатие
				else {
					if (fi.file != null)
						st.copyText(mact, fi.file.getAbsolutePath().toString());
				}
				return 0;
			}
		};
    	return obs;
    }
    static class Adapt extends ArrayAdapter<FileInfo>
    {
    	DlgFileExplorer m_menu; 
        public Adapt(Context context,DlgFileExplorer menu)
        {
            super(context,0);
            m_menu = menu;
        }
        @Override
        public int getCount() 
        {
            return m_menu.aritems.size();
        };
        @SuppressLint("NewApi")
		@Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
        	FileInfo fi = m_menu.aritems.get(position);
            if(convertView!=null)
            {
            	TextView b = (TextView)convertView;
                m_menu.getNewView(fi,b);
            }
            else
            {
                convertView = m_menu.getNewView(fi,null);
            }
//            arcounter++;
//            if (posY > 0&&arcounter >= getCount()) {
//               ListView lv = (ListView)m_MainView.findViewById(R.id.com_menu_container);
//                lv.smoothScrollToPosition(posY);
//                posY = 0;
//            }
            return convertView;
        }
    }
    /** Класс, хранящий информацию об элементе меню */  
    public static class FileInfo
    {
/** id элемента */      
        int id;
        String text;
        File file;
        
        public FileInfo(int pos, File fname)
        {
            id = pos;
            file = fname;
            text = null;
        }
        public FileInfo(int pos, File fname, String nameItem)
        {
            id = pos;
            file = fname;
            text = nameItem;
        }
        public FileInfo()
        {
            id = -1;
            file = null;
            text = null;
        }
    }
    /** Класс для сравнения двух файлов, используется в сортировке */   
    static class FilesComp implements Comparator<File>
    {
        @Override
        public int compare(File object1, File object2)
        {
            boolean bDir1 = object1.isDirectory(), bDir2 = object2.isDirectory(); 
            if(bDir1&&!bDir2)
                return -1;
            else if(!bDir1&&bDir2)
                return 1;
            return object1.getName().compareToIgnoreCase(object2.getName());
        }
    }
    
/** Возвращает массив отсортированных файлов из текущей папки */   
    ArrayList<File> getSortedCurrentDir()
    {
        ArrayList<File> ar = new ArrayList<File>();
        try{
            File af[] = cur_dir.listFiles();
            for(int i=0;i<af.length;i++)
            {
                ar.add(af[i]);
            }
            Collections.sort(ar, new FilesComp());
            return ar;
        }
        catch (Throwable e) {
        	st.toast(R.string.fi_not_open);
        }
        return null;
    }
//    File fi_file = null;
    public View getNewView(FileInfo fi, TextView tv) {
    	if (tv==null)
    		tv = new TextView(mact);
    	tv.setCompoundDrawables( null, null, null, null );
    	if (fi == null)
    		return tv;
    	m_draw = null;
    	if (scroll_time==0) {
    		try {
    			m_draw = readDrawable(fi.file.getAbsolutePath());
    		} catch (OutOfMemoryError  e) {
    			st.toast(mact, R.string.out_of_memory);
    			m_draw = null;
    		} catch (Throwable e) {
    			m_draw = null;
    		}
    		
    	}
		if (m_draw!=null) {
			
			m_draw.setBounds( 0, 0, SIZE_BOUNDS_IMAGE, SIZE_BOUNDS_IMAGE);
			tv.setCompoundDrawables( m_draw, null, null, null );
			tv.setCompoundDrawablePadding(2);
			m_draw = null;
		}
    	if (fi.text!=null)
    		//setText(tv,fi.text, fi.file);
    		tv.setText(fi.text);
    	else {
    		if (fi.file.isDirectory())
    			tv.setText(TEXT_FOLD_BEG+fi.file.getName().toUpperCase()+TEXT_FOLD_END);
    		else
        		//setText(tv,fi.file.getName(), fi.file);
    			tv.setText(fi.file.getName());
    	}
    	tv.setPadding(2, 2, 2, 2);
    	tv.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
    	tv.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent me) {
				TextView tv = (TextView)v;
				FileInfo fi = (FileInfo)tv.getTag();
	        	switch (me.getAction())
	        	{
	        	case MotionEvent.ACTION_DOWN:
	        		if (di!=null&di.inst!=null) {
	        			di.dismiss();
	        			di = null;
	        		}
	        		float xx = me.getX();
	        		float yy = me.getY();
	        		if (xx>SIZE_BOUNDS_IMAGE||yy>SIZE_BOUNDS_IMAGE)
	        			return false;
	        		Drawable[] ardr = tv.getCompoundDrawables();
	        		if (ardr[0]!=null) {
	        			Drawable dr = null;
	            		try {
	            			dr = readDrawable(fi.file.getAbsolutePath());
	            		} catch (OutOfMemoryError  e) {
	            			st.toast(mact, R.string.out_of_memory);
	            			dr = null;
	            		} catch (Throwable e) {
	            			dr = null;
	            		}
	        			if (dr!=null) {
	        				dr.setBounds(0, 0, cur_max_hw, cur_max_hw);
		        			di = new DlgImage(mact);
		        			di.show(dr);
		        			dr = null;
		        			scroll_time = 0;
		        			return true;
	        			}
	        		}
	        		break;
	        	case MotionEvent.ACTION_CANCEL:
	        	case MotionEvent.ACTION_UP:
	        		if (di!=null&di.inst!=null) {
	        			di.dismiss();
	        			di = null;
	    				scroll_time = System.currentTimeMillis();

	        		}
    				return false;
	        	}
				return false;
			}
		});
    	tv.setOnClickListener(m_listener);
    	//tv.setOnLongClickListener(m_longListener);
    	tv.setMinLines(2);
    	tv.setMaxLines(3);
    	tv.setTag(fi);
    	tv.setTransformationMethod(null);
    	m_draw=null;
    	return tv;
    }
	public Drawable readDrawable(String path) {
		try {
			return Drawable.createFromPath(path);
		} catch (Throwable e) {
			return null;
		}
	}
	/**  НЕ ИСПОЛЬЗУЕТСЯ!
	 * Создаём форматированный текст итема listview с инфой о файле */
	public void setText(TextView tv, String name, File file)
	{
		// инфа о картинке
		if (m_draw!=null) {
			name+=st.STR_LF+"(";
			int hw = m_draw.getIntrinsicWidth();
			name+=st.STR_NULL+hw+"x";
			hw= m_draw.getIntrinsicHeight();
			name+=st.STR_NULL+hw+"), "+st.getNumericPackOfString(file.getAbsoluteFile().length());
		}
		tv.setText(name);

	}
    /** читаем начальные папки */
	public static File[] getStorages()
	{
		Vector<File> ar = new Vector<File>();
//		String sd = Environment.getExternalStorageDirectory().getAbsolutePath().toString();
//		File stor = new File(sd);
//		if (stor!=null)
//			ar.add(stor);
		File storages = null;
		if (root!=null&&root.exists()) {
			File sdcards[] = root.listFiles();
			for (File ff:sdcards) {
				ar.add(ff);
			}
		} else {
			storages = new File("/storage");
			if(storages.exists())
			{
				File sdcards[] = storages.listFiles();
				for (File ff:sdcards) {
					ar.add(ff);
				}
			}
			storages = new File("/mnt");
			ar.add(storages);
//			if(storages.exists())
//			{
//				File sdcards[] = storages.listFiles();
//				for (File ff:sdcards) {
//					ar.add(ff);
//				}
//			}
			
		}
		if (ar.isEmpty())
			return null;
		File ret[] = new File[ar.size()];
		for (int i = 0; i<ar.size();i++)
			ret[i] = ar.get(i);
		return ret;
		
//		File storages = new File("/storage");
//		if(storages.exists())
//		{
//			File sdcards[] = storages.listFiles();
//			return sdcards;
//		}
//		return null;
	}
    /** читаем начальные папки */
//	public static File[] getStorages()
//	{
//		String sd = Environment.getExternalStorageDirectory().getAbsolutePath().toString();
//		File stor = new File(sd);
//		Vector<File> ar = new Vector<File>();
//		if (stor!=null)
//			ar.add(stor);
//		File storages = new File("/storage");
//		if(storages.exists())
//		{
//			File sdcards[] = storages.listFiles();
//			for (File ff:sdcards) {
//				ar.add(ff);
//			}
//		}
//		if (ar.isEmpty())
//			return null;
//		File ret[] = new File[ar.size()];
//		for (int i = 0; i<ar.size();i++)
//			ret[i] = ar.get(i);
//		return ret;
//	}

    /** Обработчик короткого нажатия кнопок меню */    
    View.OnClickListener m_listener = new View.OnClickListener()
    {
        
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
        	case R.id.fe_up:
        		if (lv!=null)
        			lv.setSelection(0);
        		return;
        	case R.id.fe_down:
        		if (lv!=null)
        			lv.setSelection(lv.getCount()-1);
        		return;
        	case R.id.fe_home:
        		cur_dir=null;
        		changeArrayList();
        		return;
        	case R.id.fe_filter:
        		String ss = st.STR_NULL;
        		ss = mact.getString(R.string.fi_filt1)
        				+st.STR_LF;
        		if (arext !=null) {
            		for (int i=0;i<arext.length;i++) {
            			ss += arext[i]+st.STR_SPACE; 
            		}
        		} else
        			ss += "<empty>";
      			ss+= st.STR_LF+st.STR_LF
        				+mact.getString(R.string.fi_filt2);
        	 
        		st.toastLong(ss);
        		return;
            }
            FileInfo fi = (FileInfo)v.getTag();
            if (fi==null)
            	return;
            if (fi.file==null) {
				onSelected(null);
				close();
            }
            if(m_MenuObserver!=null)
            {
                m_MenuObserver.OnObserver(fi, new Boolean(false));
            }
        }
    };
    /** пока не юзается */
    OnLongClickListener m_longListener = new OnLongClickListener()
    {
        @Override
        public boolean onLongClick(View v)
        {
            switch (v.getId())
            {
        	case R.id.fe_filter:
        		boolean fl = false;
        		if (arext!=null&&arexttmp==null) {
        			arexttmp = arext;
        			arext = null;
        			fl = false;
        		}
        		else if (arext==null&&arexttmp!=null) {
            		arext = arexttmp;
            		arexttmp = null;
            		fl = true;
        		}
        		setDrawableFilter(fl);
        		st.toast(mact,R.string.fi_done);
        		changeArrayList();
        		return true;
            
            }
            
            FileInfo fi = (FileInfo)v.getTag();
            if(m_MenuObserver!=null)
            {
                m_MenuObserver.OnObserver(fi, new Boolean(true));
            }
            return true;
        }
    };

	public static void close()
	{
		inst = null;
        if(m_tm!=null)
            m_tm.cancel();

		Dlg.dismiss();
	}
	/** обработка текущей папки (cur_dir) */
	public void changeArrayList()
	{
    	if (aritems!=null)
    		aritems.clear();
        ArrayList<File> arf = new ArrayList<File>();
        int pos = 0;
    	if (cur_dir== null) {
   			tvcurpath.setText(st.STR_NULL);
    		bhome.setVisibility(View.GONE);
    		if (oldpath!=null) {
        		aritems.add(new FileInfo(pos,null,oldpath));
    			pos++;
    		}
    		if (last_dir!=null) {
        		aritems.add(new FileInfo(pos,last_dir,st.STR_NULL+mact.getString(R.string.fi_folder_last)+st.STR_LF+last_dir.getAbsolutePath()));
    			pos++;
    			last_dir = null;
    		} else {
    			String lastfold = st.pref(mact).getString(st.FILE_EXPLORER_LAST_DIR, st.STR_NULL);
    			if (lastfold.length()>0){
    				last_dir = new File(lastfold);
            		aritems.add(new FileInfo(pos,last_dir,st.STR_NULL+mact.getString(R.string.fi_folder_last)+st.STR_LF+last_dir.getAbsolutePath()));
        			pos++;
        			last_dir = null;
    			}

    		}
    			
    		parent_dir = null;
    		if (root==null) {
        		aritems.add(new FileInfo(pos,new File("/"),TEXT_FOLD_BEG+mact.getString(R.string.fi_root)+TEXT_FOLD_END));
    			pos++;
        		aritems.add(new FileInfo(pos,new File(st.STR_NULL+Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)),TEXT_FOLD_BEG+mact.getString(R.string.fi_folder_dowload)+TEXT_FOLD_END));
    			pos++;
    		}
    		//aritems.add(new FileInfo(2,new File(st.STR_NULL+Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)),mact.getString(R.string.fi_folder_emulated)));
    		File sdcards[] = getStorages();
    		if(sdcards!=null&&sdcards.length>0)
    		{
    			//sortFilesByName(sdcards);
                FileInfo fi = null;
                String ext = st.STR_NULL;
    			for(File ff:sdcards) {
                	fi = new FileInfo();
                	if (ff.isDirectory()) {
                		aritems.add(new FileInfo(pos,ff.getAbsoluteFile()));
                	} else {
                		if (arext == null)
                    		aritems.add(new FileInfo(pos,ff.getAbsoluteFile()));
                		else {
                			ext = ff.getName().toUpperCase();
                			for (int i=0;i<arext.length;i++) {
                				if (ext.endsWith(arext[i].toUpperCase())) {
                            		aritems.add(new FileInfo(pos,ff.getAbsoluteFile()));
                            		pos++;
                            		break;
                				}
                			}
                			continue;
                		}
                	}
                	pos++;

    				//aritems.add(new FileInfo(pos,f));
    				//pos++;
    			}
    		}
    		if (mtype == TYPE_PICTURE)
    			aritems.add(new FileInfo(3,new File(st.STR_NULL+Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)),TEXT_FOLD_BEG+mact.getString(R.string.fi_folder_pict)+TEXT_FOLD_END));
    	} else {
    		if (parent_dir!=null&&parent_dir.getAbsoluteFile().toString().compareToIgnoreCase("/")!=0) {
        		aritems.add(new FileInfo(pos,parent_dir.getAbsoluteFile(),TEXT_FOLD_PARENT));
    			pos++;
    		}
    		tvcurpath.setText(cur_dir.getAbsolutePath());
    		bhome.setVisibility(View.VISIBLE);
    		arf = getSortedCurrentDir();
    		if (arf==null) {
    			cur_dir = null;
    			changeArrayList();
    			return;
    		}
            FileInfo fi = null;
            String ext = st.STR_NULL;
            for (File ff:arf) {
            	fi = new FileInfo();
            	if (ff.isDirectory()) {
            		aritems.add(new FileInfo(pos,ff.getAbsoluteFile()));
            	} else {
            		if (arext == null)
                		aritems.add(new FileInfo(pos,ff.getAbsoluteFile()));
            		else {
            			ext = ff.getName().toUpperCase();
            			for (int i=0;i<arext.length;i++) {
            				if (ext.endsWith(arext[i].toUpperCase())) {
                        		aritems.add(new FileInfo(pos,ff.getAbsoluteFile()));
                        		pos++;
                        		break;
            				}
            			}
            			continue;
            		}
            	}
            	pos++;
            }
    	}
    	if (m_adapter!=null) {
    		m_adapter.notifyDataSetChanged();
    		
    	}

	}
	/** выбранный файл или директория */
    public abstract void onSelected(File file);

    public void setDrawableFilter(boolean fl)
	{
    	Drawable top = null;
    	if (!fl)
    		top = mact.getResources().getDrawable(R.drawable.bullet_black);
    	else
    		top = mact.getResources().getDrawable(R.drawable.bullet_red);
		bfilt.setCompoundDrawablesWithIntrinsicBounds(null, top , null, null);
	}

}