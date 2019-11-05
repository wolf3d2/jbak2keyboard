package com.jbak2.JbakKeyboard;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.view.inputmethod.InputConnection;

public class Translate 
{
    public static Translate inst;
    File dir = null;
    public static com_menu menu = null;
    public String[] ar = null;
    public String FILENAME = "translate.txt";
    public static final String RECORD = "record";
    public static int mtype = 0;
    // переменная для хранения первоначальной записи при редактировании записи :)
    // если null, значит запись новая
    public static String old_record= null;
    
    /** type = 0 - перевести выделенное
        1 - перевести скопированное */
    Translate(int type)
    {
    	mtype = type;
        inst = this;
        String rd = st.getSettingsPath();
        dir = new File(rd);
        if(!dir.exists())
        {
            if(!dir.mkdirs())
                dir = null;
        }
    }
    void makeCommonMenu()
    {
        if(dir==null)
            return;
    	if (inst == null)
    		return;
        menu = new com_menu();
        if  (mtype == 0)
        	menu.setMenuname(ServiceJbKbd.inst.textMenuName(st.CMD_TRANSLATE_SELECTED));
        else if (mtype == 1)
        	menu.setMenuname(ServiceJbKbd.inst.textMenuName(st.CMD_TRANSLATE_COPIED));
        menu.m_state|=com_menu.STAT_TRANSLATE;
        ar = onReadFileAndSortArray();
        if (ar!=null)
            for (int i=0;i<ar.length;i++)
            	menu.add(ar[i], i);
        st.UniObserver obs = new st.UniObserver()
        {
            @SuppressLint("NewApi")
			@SuppressWarnings("deprecation")
			@Override
            public int OnObserver(Object param1, Object param2)
            {
                int pos = ((Integer)param1).intValue();
                boolean bLong = ((Boolean)param2).booleanValue();
                if (bLong){
                    ServiceJbKbd.inst.startActivity(Translate.getEditActIntent(ar[pos]));
                } else {
                	com_menu.close();
                	CharSequence sel = null;
                	switch (mtype)
                	{
                	case 0:
                        InputConnection ic = ServiceJbKbd.inst.getCurrentInputConnection();
                    	sel = ic.getSelectedText(0);
                		break;
                	case 1:
                    	sel = st.getClipboardCharSequence();
                		break;
                	}
                	if (sel == null)
                		return 0;
                	String ar1[] = ar[pos].split("-");
                	if (ar1.length<1)
                		return 0;
                	st.hidekbd();
                	String interface_lang= st.pref().getString(st.PREF_TRANSLATE_INTERFACE, st.getSystemLangApp(false));
//                	String txt = "https://translate.google.com/?hl="+interface_lang+"&tab=TT#"+ar1[0]+st.STR_SLASH+ar1[1]+st.STR_SLASH+sel;
                	String txt = "https://translate.google.com/?hl="+interface_lang+"&tab=TT#"+ar1[0]+st.STR_SLASH+ar1[1]+st.STR_SLASH+Uri.encode(sel.toString());
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(txt));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ServiceJbKbd.inst.startActivity(intent);
                    inst = null;
                }
                	
                return 0;
            }
        };
        menu.show(obs, false);
    }
    // интент для вызова TplEditorActivity
    public static Intent getEditActIntent(String record)
    {
        Intent in = new Intent(ServiceJbKbd.inst,TplEditorActivity.class)
                .putExtra(TplEditorActivity.EXTRA_CLIPBOARD_ENTRY, -2)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	if (record!=null)
    		in.putExtra(RECORD, record);
    	return in;
    }
    public String[] onReadFileAndSortArray()
    {
    	File path = new File(dir.getAbsolutePath()+st.STR_SLASH+FILENAME);
    	if (!path.exists())
    		return null;
		FileReader fr;
		ArrayList<String> obj = new ArrayList<String>();
		String line = st.STR_NULL;
		try {
			fr = new FileReader(path);
			Scanner sc = new Scanner(fr);
			sc.useLocale(Locale.US);
			while (sc.hasNext()) {
				if (sc.hasNextLine()) {
					line = sc.nextLine();
				}
				line = line.trim();
				if (line.length() > 0) {
					obj.add(line);
				}
			}
		} catch (FileNotFoundException e) {
		}
		fr=null;
		if (!obj.isEmpty()){
			String[] ar = new String[obj.size()];
			for (int i=0;i<obj.size();i++){
				ar[i]=obj.get(i).toString().toLowerCase();
			}
			Arrays.sort(ar);
			return ar;
		}
    	return null;
    }
    static void close()
    {
        inst = null;
    }
    /** Юзер отменил редактирование шаблона */
    public void onCloseEditor()
    {
        ServiceJbKbd.inst.showWindow(true);
        if (Translate.inst!=null) {
        	int type = inst.getType();
        	
        	close();
        	new Translate(type).makeCommonMenu();
        } else
        	makeCommonMenu();
    }
    public int getType()
    {
    	return mtype;
    }
    
    public void onDelete (String txt)
    {
    	onSave(txt,true);
    }
    // если old_record=null, то значит запись новая, 
    // иначе редактируемая и записываем весь массив
    // bDelete - если true, то данную запись нужно удалить
    public void onSave(String txt, boolean bDelete)
    {
		FileWriter wr;
		String path = st.getSettingsPath()+FILENAME;
		txt.toLowerCase().trim();
		try {
			// удаление записи
			if (bDelete){
				wr = new FileWriter(path, false);
				for (int i=0;i<ar.length;i++){
					if (ar[i].compareToIgnoreCase(old_record)==0)
						continue;
					wr.write(ar[i]+st.STR_LF);
				}
				wr.flush();
				wr.close();
				old_record = null;
				return;
			}
			// редактирование записи
			if (old_record!=null){
				wr = new FileWriter(path, false);
				for (int i=0;i<ar.length;i++){
					if (ar[i].compareToIgnoreCase(old_record)==0)
						ar[i] = txt;
					wr.write(ar[i]+st.STR_LF);
				}
				wr.flush();
				wr.close();
				old_record = null;
				return;
			}
			// добавление записи
			wr = new FileWriter(path, true);
			txt+=st.STR_LF;
			wr.write(txt.toLowerCase());
			wr.flush();
			wr.close();
		} catch (IOException e) {}
   	
    }
    
}