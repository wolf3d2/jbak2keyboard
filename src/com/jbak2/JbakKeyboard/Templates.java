package com.jbak2.JbakKeyboard;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

import com.jbak2.JbakKeyboard.st.ArrayFuncAddSymbolsGest;
import com.jbak2.ctrl.GlobDialog;
import com.jbak2.ctrl.SearchRegex;

import android.app.AlertDialog;
import android.view.inputmethod.InputConnection;

/** Класс для операций с шаблонами - создание, листинг, обработка */
public class Templates
{
	/** строка выделенного текста для специнструкций */
	static String mrepl= st.STR_NULL;
/** Статический объект для доступа к экземпляру класса */    
    static Templates inst;
    public static com_menu menu = null;
    File m_editFile=null;
    
/** Обнуляет текущий объект */    
    static void destroy()
    {
        if(inst!=null)
            inst = null;
    }
/** Конструктор. Инициализирует rootDir 
 * @param rej - режим какую папку открывать <br>
 * 1 - templates
 * 2 - calc
 * @param typ - тип обработки для обработок папки calc <br>
 * @param curDir - или null, или устанавливает текущую папку
 * */  
    Templates(int rej, int typ, String curDir)
    {
        inst = this;
        rejim = rej;
        type = typ;
        setDir(rejim,type);
        String rd = st.getSettingsPath()+template_path;
        m_rootDir = new File(rd);
        if(!m_rootDir.exists())
        {
            if(!m_rootDir.mkdirs())
                m_rootDir = null;
        }
        m_curDir = m_rootDir;
        // должна стоять первой проверкой
        if (rejim == 1&&curDir!=null) {
        	if (!curDir.startsWith(st.STR_SLASH))
        		curDir = rd+st.STR_SLASH+curDir;
        	m_curDir = new File(curDir);
        	//st.fl_fiks_tpl = false;
        }
        else if (rejim == 1&&st.fiks_tpl.length()>0) {
        	m_curDir = new File(st.fiks_tpl);
        	st.fl_fiks_tpl = false;
        }
        else if (rejim == 2&&st.fiks_calc.length()>0) {
        	m_curDir = new File(st.fiks_calc);
        }
        if(!m_curDir.exists())
        {
            if(!m_curDir.mkdirs())
            	m_curDir = m_rootDir;
        }
    }
/** Устанавливает редактирования папки шаблонов - для запуска {@link TplEditorActivity}*/    
    void setEditFolder(boolean bSet)
    {
        if(bSet)
        {
            m_state|=STAT_EDIT_FOLDER;
        }
        else
        {
            m_state = st.rem(m_state, STAT_EDIT_FOLDER);
        }
    }
    boolean openFolder(File f)
    {
        m_curDir = f.getAbsoluteFile();
        makeCommonMenu();
        return true;
    }
    boolean isEditFolder()
    {
        return st.has(m_state, STAT_EDIT_FOLDER);
    }
/** Юзер отменил редактирование шаблона */
    void onCloseEditor()
    {
        setEditFolder(false);
        setEditTpl(null);
// !!! ПОКАЗЫВАЕТ ОКНО КЛАВИАТУРЫ        
        ServiceJbKbd.inst.showWindow(true);
        Templates.inst.makeCommonMenu();
    }
    void onDelete()
    {
        if(m_editFile==null)return;
        if(isEditFolder())
            deleteDir(m_editFile);
        else
            m_editFile.delete();
    }
/** Если m_editFile!=null - переименовывает эту папку в name.<br>
 *  Иначе создаёт новую папку с именем name */  
    void saveFolder(String name)
    {
        String fpath = m_curDir.getAbsolutePath()+File.separator+name;
        File f = new File(fpath);
        if(m_editFile!=null)
        {
            m_editFile.renameTo(f);
        }
        else
        {
            f.mkdirs();
        }
    }
/** Сохраняет шаблон с названием name и текстом text */ 
    void saveTemplate(String name,String text)
    {
        fpath = m_curDir.getAbsolutePath()+File.separator+name;
        try{
            if(m_editFile!=null&&rejim != 2)
            {
                if(!m_editFile.delete())
                {
                    return;
                }
            }
            if (rejim == 1) {
                File f = new File(fpath);
                FileOutputStream os = new FileOutputStream(f);
            	os.write(text.getBytes());
                os.close();
            }
            else if (rejim == 2) {
        		if (st.isCalcPrg() == false){
        			st.toast(st.c().getString(R.string.calc_prog_empty));
        		} else {
                    addCalc();
                    File f = new File(fpath);
	        		st.calc_prg_desc = text;
    	        	if (f.exists()) {
    	        		calcSavePrgQuery();
    	        	} else {
    	        		calcSavePrg();
    	        	}
        		}
            }
        }
        catch(Throwable e)
        {
        }
    }
    void calcSavePrg()
    {
     	try {
        	addCalc();
            File f = new File(fpath);
            if (f.exists())
            	f.delete();
        	FileWriter writer = new FileWriter(fpath, false);
        	writer.write(st.CALC_PRG_VERSION+st.STR_LF);
        	writer.write(st.CALC_PRG_DESC_WORD+st.STR_LF);
        	writer.write(st.calc_prg_desc+st.STR_LF);
        	writer.write(st.CALC_PROGRAM_WORD+st.STR_LF);
            String out = st.STR_NULL;
        	for (int i=0;i< st.calc_prog.length;i++){
        		out += String.valueOf(st.calc_prog[i])+",";
        	}
        	writer.write(out+st.STR_LF);
        	writer.close();
//        	int f = ServiceJbKbd.inst.getParSleepValue();
//        	st.sleep(f);
     	}
     	catch(IOException ex){
     	}
    }
/** Устанавливает файл шаблона для редактирования в редакторе шаблона.<br>
 *  null - для нового шаблона*/  
    void setEditTpl(File f)
    {
        m_editFile = f;
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
    
/** Возвращает массив отсортированных файлов из текущей папки шаблонов */   
    ArrayList<File> getSortedFiles()
    {
        ArrayList<File> ar = new ArrayList<File>();
        try{
            File af[] = m_curDir.listFiles();
            for(int i=0;i<af.length;i++)
            {
                ar.add(af[i]);
            }
            Collections.sort(ar, new FilesComp());
            return ar;
        }
        catch (Throwable e) {
        }
        return null;
    }
// возвращает описание программы калькулятора
    String getCalcPrgDesc(File f)
    {
     	try {
     		String fs = m_curDir + st.STR_SLASH + f.getName();
        	FileReader fr= new FileReader(fs);
         	Scanner sc = new Scanner(fr);
         	sc.useLocale(Locale.US);
            st.calc_prg_desc = st.STR_NULL;
        	String str = st.STR_NULL;
        	String out = st.STR_NULL;
       		str = sc.nextLine();
       		if (str.trim().equals(st.CALC_PRG_VERSION) == false) {
           		sc.close();
       			return st.STR_NULL;
         	}
       		boolean fl = false;
// алгоритм должен быть именно такой!
       		while (sc.hasNextLine()) {
          		str = sc.nextLine();
         		if (str.equals(st.CALC_PROGRAM_WORD)) {
         			fl = false;
         		}
          		if (fl){
          			if (str.length()>0)
          				out+=str;
          			if (out.length()>0&&out.endsWith(st.STR_SPACE) == false)
          				out+=st.STR_SPACE;
          		}
         		if (str.equals(st.CALC_PRG_DESC_WORD)) {
         			fl = true;
         		}
       		}
       		sc.close();
       		return out;
     	}
     	catch(IOException ex){
     	}
        return st.STR_NULL;
    }
    /** Общий InputConnection исключительно для использования в шаблонах */
    InputConnection mic = null;
    /** Общий CurInput исключительно для использования в шаблонах */
    CurInput mci = new CurInput();

/** Выполняет шаблон s в текущем сервисе 
 * (тут же обрабатываются и специнструкции) */ 
    @SuppressWarnings("deprecation")
	void processTemplate(String mstr)
    {
        if(mstr==null)
            return;
        int del = 0;
        int pos = 0;
        int len = mstr.length();
        mci = new CurInput();
        mic = ServiceJbKbd.inst.getCurrentInputConnection();
        String ss = st.STR_NULL;
    	String strformat = st.STR_NULL;
    	String out = st.STR_NULL;
    	boolean fl = false;
    	String sy = st.STR_NULL;
    	char ch = 0;
    	String r1 = st.STR_NULL;
        int ff = -1; 
        boolean bFound = false;
        while(true)
        {
            int f = mstr.indexOf(TPL_SPEC_CHAR, pos);
            if(f<0||f==len-1)
                break;
            if(mstr.charAt(f+1)==TPL_SPEC_CHAR)
            {
                pos = f+2;
                continue;
            }
            // обработка специнструкций
            bFound = false;
            for(int i=0;i<Instructions.length;i++)
            {
                ss = Instructions[i];
                ff = mstr.indexOf(ss, f+1); 
                if(ff==f+1)
                {
                    bFound = true;
                    if(!mci.isInited())
                    {
                        mci.init(mic);
                        mic.beginBatchEdit();
                    }
                	r1 = st.STR_NULL;
                	mrepl = mci.sel;
                	if (mrepl.isEmpty()) {
                		try {
                    		mrepl = mic.getSelectedText(0).toString();
						} catch (Throwable e) {
						}
                	}
                	if (mrepl == null)
                		mrepl = st.STR_NULL;
                	strformat = st.STR_NULL;
                	out = st.STR_NULL;
                	fl = false;
                	sy = st.STR_NULL;
                	ch = 0;
                    switch(i)
                    {
                        case 0:  break; // select
                        case 1: // selword
                            if(mrepl.length()==0)
                            {
                                if(del==0)
                                    del=IB_WORD;
                                mrepl = mci.getTextWord();
                            }
                            break;
                        case 2: // selline
                        	if(mrepl.length()==0)
                            {
                                del = IB_LINE;
                                mrepl = mci.getTextParagraph(); break;
                            }
                        	break;
                        case 3: // datetime
                        	String repl2 = st.STR_NULL;
                        	String sub = mstr.substring(mstr.indexOf(Instructions[i])+Instructions[i].length());
                        	if (sub.startsWith("[")){
                            	int pos_skob = sub.indexOf("]");
                        		if (pos_skob>1){
                        			try {
                        				strformat = sub.substring(1, pos_skob);
                        				SimpleDateFormat sdf = new SimpleDateFormat(strformat);
                        				repl2 = sdf.format(new Date());
                        			} catch(Throwable e) {
                        				st.toast("format error");
                        	        	mic.endBatchEdit();
                        				return;
                        			}
                        			int pp = mstr.indexOf("["+strformat+"]");
                        			mstr=mstr.substring(0, pp)+ mstr.substring(pp+("["+strformat+"]").length(), mstr.length());
                        		}else
                        			repl2 = java.util.Calendar.getInstance().getTime().toLocaleString().toString();
                        	} else {
                        		repl2 = java.util.Calendar.getInstance().getTime().toLocaleString().toString();
                        	}
                        	// закоменчено и исправлено 08/08/19
                        	// посмотрим что скажут юзеры
                    		mrepl = repl2;
//                        	if (mrepl.length()!=0)
//                        		mrepl = repl2;
//                        	else
//                        		ServiceJbKbd.inst.setWord(repl2,false);
                		break;
                        case 4: // sellowercase
                        	mrepl =mrepl.toLowerCase();
                		break;
                        case 5: // selupcase
                        	mrepl =mrepl.toUpperCase();
                		break;
                        case 6: // selinsertword
                        	if (mrepl!=null&&mrepl.length()>0){
                        		
                        		String str = st.rowInParentheses(mstr);
                        		if (str.isEmpty()) {
                                	mic.endBatchEdit();
                        			return;
                        		}
                        		String repl1 = st.STR_NULL;
                        		for (int i1=0;i1<mrepl.length();i1++) {
                        			repl1 += mrepl.charAt(i1)+ str;
                        		}
                        		mrepl = repl1.substring(0, repl1.length() - str.length());
                        	}
                		break;
                        case 7: // selDeleteWord
                        	if (mrepl!=null&&mrepl.length()>0){
                        		String str = st.rowInParentheses(mstr);
                        		if (str.isEmpty()) {
                                	mic.endBatchEdit();
                        			return;
                        		}
                        		int ind = -1;
                        		do
                        		{
                        			ind = mrepl.indexOf(str);
                        			if (ind>-1)
                        				mrepl = mrepl.substring(0,ind)+mrepl.substring(ind+str.length());
                                }while(ind>-1);

                        	}
                		break;
                        case 8: // seltranslit
                        	mrepl = Translit.toTranslit(mrepl);
                		break;
                        case 9: // selVerseMode режим стихов
                        	out = st.STR_NULL;
                        	fl = false;
                        	sy = st.STR_NULL;
                        	ch = 0;
                        	for (i=0;i<mrepl.length();i++){
                        		sy = mrepl.substring(i,i+1);
                        		if (i==0){
                        			fl = true;
                        		}
                        		ch = sy.charAt(0);
                        		if (ch>32&fl==true){
                        			sy = sy.toUpperCase();
                        			fl=false;
                        			out +=sy;
                        			continue;
                        		}
                        		else if (ch == '\n')
                        			fl = true;
                        		out+=sy;
                        	}
                        	mrepl = out;
                		break;
                		// selAsInTheSentences как в предложениях
                        case 10: 
                        	if (ServiceJbKbd.inst==null) {
                            	mic.endBatchEdit();
                        		return;
                        	}
                        	out = st.STR_NULL;
                        	fl = false;
                        	sy = st.STR_NULL;
                        	ch = 0;
                        	for (i=0;i<mrepl.length();i++){
                        		sy = mrepl.substring(i,i+1);
                        		if (i==0){
                        			fl = true;
                        		}
                        		if (ServiceJbKbd.inst.m_SentenceEnds.contains(sy)){
                        			fl=true;
                        			out+=sy;
                        			continue;
                        		}
                        		ch = sy.charAt(0);
                        		
                        		if (ch>32&fl==true){
                        			sy = sy.toUpperCase();
                        			fl=false;
                        			out += sy;
                        			continue;
                        		}
                        		out += sy;
                        	}
                        	mrepl = out;
                		break;
                      case 11: //paste - специнструкция удалена
                  		if (ServiceJbKbd.inst==null) { 
                        	mic.endBatchEdit();
                  			return;
                  		}
              			mrepl = st.getClipboardCharSequence().toString();
                      	break;
                        case 12: //selReplace
                    		if (ServiceJbKbd.inst==null) { 
                            	mic.endBatchEdit();
                    			return;
                    		}
                    		if (mrepl==null) {
                            	mic.endBatchEdit();
                    			return;
                    		}
                    		if (mrepl.length()<1) {
                            	mic.endBatchEdit();
                    			return;
                    		}
                        	String[] ar = getDecodeSelReplaceInstruction(mstr);
                        	if (ar == null) {
                            	mic.endBatchEdit();
                        		return;
                        	}
                        	boolean bcase = false;
                        	boolean bregular = false;
                        	// для будущей реализации поиска и замены через регулярки
//                        	if (ar[0]!=null) {
//                        		if (ar[0].contains("R"))
//                        			bregular = true;
//                        		if (ar[0].contains("C"))
//                        			bcase = true;
//                        	}
                        	if (ar[1]==null) {
                            	mic.endBatchEdit();
                        		return;
                        	}
                        	if (ar[1].isEmpty()) {
                            	mic.endBatchEdit();
                        		return;
                        	}
                        	if (ar[2]==null) {
                            	mic.endBatchEdit();
                        		return;
                        	}
                        	if (ar[2].isEmpty()) {
                            	mic.endBatchEdit();
                        		return;
                        	}
                        	if (ar[2].compareToIgnoreCase("@[DEL]")==0)
                        		ar[2] = st.STR_NULL;

                        	if (bregular) {
                            	String repl = SearchRegex.getReplaceALL(mrepl, ar[1], ar[2], bcase);
                            	if (repl!=null) {
                            		mrepl = repl;
                            	}
                        	} else {
                            	mrepl= mrepl.replace(ar[1], ar[2]);
                        	}
                        	mstr = "$"+Instructions[12];
                        		
                        	break;
                        case 13: //selToPos
                        	mic.endBatchEdit();
                    		if (ServiceJbKbd.inst==null) { 
                    			return;
                    		}
// формат инструкции:
// selToPos[0,0,0,.]
// где:
// 1 параметр - число, позиция с какой выделять.
//   Если >=0, то выделяем с начала текста с указанной позиции        
//   Если отрицательная, то считаем позицию выделения после текущего положения курсора
// 2 параметр - как искать.
//   Если >=0, то ищем до самого малого значения первого вхождения символов перечисленных 
//   в 4 параметре
//   Если <0, то ищем всю строку целиком, указанную в 4 параметре                    		
// 3 параметр - откуда искать, с начала или конца текста (число + или -).
//   (НЕ ЗАБЫТЬ УКАЗАТЬ ЧТО. ИСХОДНЫЙ ТЕКСТ, ДОЛЖЕН БЫТЬ НЕ БОЛЕЕ 200000 символов!)
// 4 параметр - строка символов, по чему искать. 
//   ОБЯЗАТЕЛЬНО ДОЛЖЕН БЫТЬ ПОСЛЕДНИМ, ЧТОБЫ МОЖНО БЫЛО ИСПОЛЬЗОВАТЬ СИМВОЛ ЗАПЯТОЙ!                    		
                    		int selst = -1;
                    		int selend = -1;
                    		String[] ar1 = getDecodeSelToPosInstruction(mstr);
                    		if (ar1==null) {
                            	mic.endBatchEdit();
                    			return;
                    		}
                    		// есть ли чего искать?
                    		if (ar1[3]==null|ar1[3].length()==0) {
                            	mic.endBatchEdit();
                    			return;
                    		}
                    		int[] ari = new int[3];
                    		try {
                    			ari[0] = Integer.valueOf(ar1[0]);
							} catch (Throwable e) {
							}
                    		try {
                    			ari[1] = Integer.valueOf(ar1[1]);
							} catch (Throwable e) {
							}
                    		try {
                    			ari[2] = Integer.valueOf(ar1[2]);
							} catch (Throwable e) {
							}
                    		int MAXVALUE = 100000;
                    		// получаем текст
                    		String txt = st.STR_NULL;
                    		String txttemp = st.STR_NULL;
                    		try {
								txt = mic.getTextBeforeCursor(MAXVALUE, 0).toString();
								txttemp = mic.getTextBeforeCursor(MAXVALUE+1, 0).toString();
							} catch (Throwable e) {
							}
                    		try {
								txt+=mic.getTextAfterCursor(MAXVALUE, 0).toString();
								txttemp+=mic.getTextAfterCursor(MAXVALUE+1, 0).toString();
							} catch (Throwable e) {
							}
                    		if (txt==null) {
                            	mic.endBatchEdit();
                    			return;
                    		}
                    		if (txt.length() < 1) {
                            	mic.endBatchEdit();
                    			return;
                    		}
                    		if (txt.compareTo(txttemp)!=0) {
                    			st.toast(R.string.tpl_seltopos_toast);
                            	mic.endBatchEdit();
                    			return;
                    		}
            				int ps = MAXVALUE+MAXVALUE;
            				int pst = -1;
                			// ищем с начала текста
                    		if (ari[2]>=0) {
                				selst = ari[0];
                				if (selst > 0)
                					selst--;
                				// искать по символам
                    			if (ari[1]>-1) {
                    				ps = MAXVALUE+MAXVALUE;
                    				pst = -1;
                    				for (int ii=0;ii<ar1[3].length();ii++) {
                    					pst = txt.indexOf(ar1[3].charAt(ii));
                    					if (pst>-1&pst<ps)
                    						ps = pst;
                    					if (pst == -1)
                    						break;
                    				}
                    				if (ps != MAXVALUE+MAXVALUE)
                    					selend = ps;
                    			} else {
                    				// искать по словам
                					pst = txt.indexOf(ar1[3]);
                					if (pst>-1)
                						selend = pst;
                    			}
                    		} else {
                    			// ищем с конца текста
                				selst = ari[0];
                				if (selst > 0)
                					selst--;
                				// искать по символам
                    			if (ari[1]>-1) {
                    				ps = MAXVALUE+MAXVALUE;
                    				pst = -1;
                    				for (int ii=0;ii<ar1[3].length();ii++) {
                    					pst = txt.lastIndexOf(ar1[3].charAt(ii));
                    					if (pst>-1&pst<ps)
                    						ps = pst;
                    					if (pst == -1)
                    						break;
                    				}
                    				if (ps != MAXVALUE+MAXVALUE)
                    					selend = ps;
                    			} else {
                    				// искать по словам
                					pst = txt.lastIndexOf(ar1[3]);
                					if (pst>-1)
                						selend = pst;
                    			}
                    			
                    		}
                    		if (selst>-1&selend>-1)
                    			mic.setSelection(selst, selend);
                    		return;
                        case 14: //program
                    		if (ServiceJbKbd.inst==null) { 
                            	mic.endBatchEdit();
                    			return;
                    		}
                    		// флаг, что слово $program встретилось первый раз
                    		boolean first_program = true;
                    		// последняя позиция
                    		int poz = 0;
                    		// текущая позиция
                    		int tp = 0;
                    		// текущая строка до '\n'
                    		String curstr = null;
                    		while (poz > -1)
                    		{
                    			poz = mstr.indexOf(st.STR_LF, tp);
                    			if (poz > -1) {
                    				curstr = mstr.substring(tp, poz);
                    				if (first_program&&curstr.compareToIgnoreCase("$program")==0) {
                    					first_program = false;
                        				tp = poz+1;
                    					continue;
                    				}
                    				if (curstr.length()>0) {
                    					curstr = getDecodeStringProgramInstruction(curstr);
                        				processTemplate(curstr);
                    				}
                    				tp = poz+1;
                    			} else {
                    				curstr = mstr.substring(tp);
                    				if (curstr.length()>0) {
                    					curstr = getDecodeStringProgramInstruction(curstr);
                        				processTemplate(curstr);
                    				}
                    			}
                    		}
//                        	mic.endBatchEdit();
                    		return;
                        case 15: //codes
                        	mic.endBatchEdit();
                    		if (ServiceJbKbd.inst==null) { 
                    			return;
                    		}
                    		int pb = mstr.indexOf("[");
                    		int pe = mstr.indexOf("]");
                    		out = null;
                    		if (pb>-1&&pe>-1)
                    			out = mstr.substring(pb+1, pe);
                    		if (out !=null) {
                    			String[] ar3 = out.split(st.STR_COMMA);
                    			String[]ar4 = null;
                    			Integer[] code = null;
                    			int cod = 0;
                    			for (int ii=0;ii<ar3.length;ii++) {
                    				if (ar3[ii]!=null&&ar3[ii].trim().length() < 1)
                    					continue;
                    				pb = ar3[ii].indexOf("+");
                    				if (pb > -1) {
                        				// "+" ОБЯЗАТЕЛЬНО ДОЛЖЕН БЫТЬ ЭКРАНИРОЛВАННЫМ В SPLIT,
                        				// так как это один из управляющих символов в regex!
                    					ar4 = ar3[ii].split("\\+");
                    					code = new Integer[ar4.length];
                    					for (int iii=0;iii<ar4.length;iii++) {
                    						try {
												code[iii] = Integer.parseInt(ar4[iii]);
												if (code[iii] <= st.KEYCODE_CODE 
														&& code[iii] >= st.KEYCODE_CODE - 2000) {
													code[iii] = st.KEYCODE_CODE - code[iii];
												}

											} catch (Exception e) {
											}
                    					}
                    					ServiceJbKbd.inst.sendHardwareSequence(mic, code);
                    				} else {
                    					try {
											cod = Integer.parseInt(ar3[ii]);
										} catch (Exception e) {
										}
                    					if (cod>0)
                    						ServiceJbKbd.inst.onKey(cod, null);
                    					else if (cod>st.KEYCODE_CODE&&cod<st.KEYCODE_CODE-2000)
                        					ServiceJbKbd.inst.sendHardwareSequence(mic, 
                        							st.KEYCODE_CODE - cod);
                    					else
                    						ServiceJbKbd.inst.onKey(cod, null);
                    				}
                    			}
                    		}
                    		//st.toast(mstr);
//                        	mic.endBatchEdit();
                    		return;
                    }
                    if(mrepl==null)
                    {
                        pos = mstr.length()-1;
                        break;
                    }
//                    s = s.substring(0,f)+mrepl+s.substring(f+ss.length()+1);
                    String sss = st.STR_NULL;
                    int l=0;
                    if (mstr.indexOf(ss)!=0){
                    	sss = mstr.substring(f+ss.length()+1);
                    	if (sss.startsWith("="))
                    		if (sss.indexOf("]")>0){
                    			l = sss.indexOf("]")+1;
                    		}
                    }
                    		
                    mstr = mstr.substring(0,f)+mrepl+mstr.substring(f+ss.length()+1+l);
                    pos = f+mrepl.length();
                    break;
                }
            }
            if(!bFound)
                pos++;
        }
        if(del==IB_WORD)
            mci.replaceCurWord(mic, mstr);
        else if(del==IB_LINE)
            mci.replaceCurParagraph(mic, mstr);
        else {
            ServiceJbKbd.inst.onText(mstr);
        }
        if(mci.isInited())
        	mic.endBatchEdit();
		if (ServiceJbKbd.inst!=null&&ServiceJbKbd.inst.isSelMode())
			ServiceJbKbd.inst.stickyOff(-310);
		mic = null;
		mci = null;
    }
    /** обрабатываем и возвращаем массив значений параметров 
     * для специнструкции program:
     *  */
    String getDecodeStringProgramInstruction(String in)
    {
    	int pos = in.indexOf(STR_TPL);
    	if (in.length()<3)
    		return in;
    	else if (pos > -1) {
        	String foldroot = st.getSettingsPath()+Templates.FOLDER_TEMPLATES+st.STR_SLASH;
    		String fn = in.substring(pos+STR_TPL.length());
    		fn = foldroot + fn.trim();
    		File ff = new File(fn);
    		if (ff.exists()&ff.isFile()) {
    			in = st.readFileString(ff);
    			return in;
    		} else {
    			st.toast("not found template: "+fn);
    			return st.STR_NULL;
    		}
    	}
    	return in;
    }
    /** обрабатываем и возвращаем массив значений параметров 
     * для специнструкции selToPos:
     *  */
    String[] getDecodeSelToPosInstruction(String instruction)
    {
    	if (instruction==null)
    		return null;
    	if (instruction.isEmpty())
    		return null;
    	int ind = 0;
    	ind = instruction.indexOf("[");
    	String repl = instruction;
    	if (ind>-1)
    		repl = repl.substring(ind+1);
    	// selToPos[0,0,0,.]
    	// где:
    	// 1 элемент - число, позиция с какой выделять.
    	// 2 элемент - как искать.
    	// 3 элемент - откуда искать, с начала или конца текста.
    	// 4 элемент - строка символов, по чему искать
    	String[] ar = new String[4];
    	for (int i=0;i<4;i++)
    		ar[i]=st.STR_NULL;
    	// номер считаемого параметра (разделитель ",")
    	ind = 0;
    	char ch = ' ';
    	String out = st.STR_NULL;
    	int lp = 0;
    	for (int i=0;i<repl.length();i++) {
    		ch = repl.charAt(i);
    		if (ch == ',') {
    			switch (ind)
    			{
    			case 0:
    				ar[0] = out;
    				break;
    			case 1:
    				ar[1] = out;
    				break;
    			case 2:
    				ar[2] = out;
    				lp = i;
    				break;
    			}
    			out = st.STR_NULL;
    			ind++;
    			continue;
    		}
    		out += ch;
    	}
    	ind = repl.indexOf("]");
    	if (ind == -1)
    		return null;
    	ar[3] = repl.substring(lp+1, ind);
    	for (int i1=0;i1<4;i1++){
    		if (ar[i1].length()==0)
    			ar[i1] = null;
    	}
    	return ar;
    }
    /** обрабатываем и возвращаем массив значений параметров 
     * для специнструкции selReplace:<br>
     * 1 элемент - значение из selReplace,<br>
     * 2 элемент - значение из @SEARCH,<br>
     * 3 элемент - значение из @REPLACE
     *  */
    String[] getDecodeSelReplaceInstruction(String repl)
    {
    	// массив значений параметров:
    	// 1 элемент - $selReplace
    	// 2 элемент - @SEARCH
    	// 3 элемент - @REPLACE
    	String[] ar = new String[3];
    	for (int i=0;i<3;i++)
    		ar[i]=null;
    	// позиции с которых начинаются строки параметров
    	int sel = repl.indexOf(Templates.Instructions[12]);
    	int sea = repl.indexOf(Templates.STR_SEARCH);
    	int rep = repl.indexOf(Templates.STR_REPLACE);
    	// значение следующего тега
    	int ninl = -1;
    	String val = null;
    	if (sel>-1) {
    		if (sea<0)
    			return null;
    		val = repl.substring(sel+Templates.Instructions[12].length(),sea);
    		if (val.length()>0) {
        		val= val.toUpperCase().trim();
        		if (val.length()>0)
        			ar[0]=val;
    		}
    	}
    	// @SEARCH
    	if (sea>-1) {
    		if (rep<0)
    			return null;
    		val = repl.substring(sea+Templates.STR_SEARCH.length(),rep);
    		if (val.length()>0) {
    			int in1 = val.lastIndexOf(st.STR_LF);
    			if (in1>-1) {
    				val = val.substring(0, in1);
    			}
    			if (val.length()>0)
        			ar[1]=val;
    		}
    	}
    	// @REPLACE
    	if (rep>-1) {
    		val = repl.substring(rep+Templates.STR_REPLACE.length());
    		if (val.length()>0) {
    			if (val.length()>0)
        			ar[2]=val;
    		}
    	}
    	
    	return ar;
    }
/** Обрабатывает щелчок по элементу шаблона */  
    void processTemplateClick(int index, boolean bLong)
    {
        if(index<0)
        {
            openFolder(m_curDir.getParentFile());
            return;
        }
        if(index>m_arFiles.size())
            return;
        file = m_arFiles.get(index);
        if(file.isDirectory())
        {
            if(bLong)
            {
                setEditTpl(file);
                setEditFolder(true);
                st.kbdCommand(st.CMD_TPL_EDITOR);
            }
            else
            {
                openFolder(file);
            }
        }
        else
        {
            if(bLong)
            {
           		setEditTpl(file);
           		st.kbdCommand(st.CMD_TPL_EDITOR);
            }
            else
            {
            	if (rejim == 2&&type == 1) {
            		if (st.isCalcPrg()) {
            			fpath = m_curDir+st.STR_SLASH+file.getName().trim();
            			addCalc();
            			File f1 = new File(fpath);
            			if (f1.exists()) {
            				calcSavePrgQuery();
            			} else {
            				calcSavePrg();
            			}
            		} else
            			st.toast(st.c().getString(R.string.calc_prog_empty));
            	} else {
            		if (rejim == 2&&type == 2) {
            			if (st.isCalcPrg()) {
                			calcLoadPrgQuery();
            			} else {
            				calcLoadPrg();
            			}
           			} else {
       		        	processTemplate(st.readFileString(file));
           			}
            	}
          	  menu.close();

            }
        }
    }
    void calcLoadPrgQuery()
    {

        GlobDialog gd = new GlobDialog(st.c());
        gd.set(R.string.calc_load_prg_msg, R.string.yes, R.string.no);
        gd.setObserver(new st.UniObserver()
        {
            @Override
            public int OnObserver(Object param1, Object param2)
            {
                if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
                {
                	calcLoadPrg();
                }
                return 0;
            }
        });
        gd.showAlert();
    	
    }
// загрузка программы калькулятора    
    void calcLoadPrg()
    {
     	try {
     		String fs = m_curDir + st.STR_SLASH + file1.getName();
        	FileReader fr= new FileReader(fs);
         	Scanner sc = new Scanner(fr);
         	sc.useLocale(Locale.US);
        	String str = st.STR_NULL;
        	String out = st.STR_NULL;
       		boolean fl = false;
// алгоритм должен быть именно такой!
       		while (sc.hasNextLine()) {
          		str = sc.nextLine();
          		if (fl){
          			out+=str;
          		}
         		if (str.equals(st.CALC_PROGRAM_WORD)) {
         			fl = true;
         		}
       		}
       		sc.close();
       		String[] arprg = null;
       		arprg = out.split(",");
       		if (arprg != null) {
       			for (int i=0;i<arprg.length;i++){
       				st.calc_prog[i]=Integer.valueOf(arprg[i]);
       			} 
       		}  else {
   				st.toast("Not loading./\nError format");
       		}
     	}
     	catch(IOException ex){
     	}
    }
/** Основная функция для вывода шаблонов в CommonMenu*/
    void makeCommonMenu()
    {
        if(m_curDir==null)
            return;
        menu = new com_menu();
    	if (Templates.inst == null)
    		return;
        if (rejim == 1) {
        	menu.setMenuname(ServiceJbKbd.inst.textMenuName(st.CMD_TPL));
        }
        else if (rejim == 2&&type == 1)
        	menu.setMenuname(ServiceJbKbd.inst.textMenuName(st.CMD_CALC_SAVE));
        else if (rejim == 2&&type == 2)
        	menu.setMenuname(ServiceJbKbd.inst.textMenuName(st.CMD_CALC_LOAD));
        if (rejim == 1&&st.fiks_tpl.length()>0&&st.fl_fiks_tpl) {
        	m_curDir = new File(st.fiks_tpl);
        	st.fl_fiks_tpl = false;
        }
        if (rejim == 2&&st.fiks_calc.length()>0&&st.fl_fiks_calc) {
        	m_curDir = new File(st.fiks_calc);
        	st.fl_fiks_calc = false;
        }
        menu.m_state|=com_menu.STAT_TEMPLATES;
        m_arFiles = getSortedFiles();
        if(m_arFiles==null)
            return;
        if(!m_curDir.getAbsolutePath().equals(m_rootDir.getAbsolutePath()))
        {
            menu.add("[..]",-1);
        }
        String str = st.STR_NULL;
        int pos = 0;
        for(File f:m_arFiles)
        {
            if(f.isDirectory())
            {
// вывод папок шаблонов
            	dir = m_curDir+st.STR_SLASH+f.getName().trim();
            	
            		if ((ServiceJbKbd.inst.m_hotkey_dir).length()>0&&
           				dir.contains(ServiceJbKbd.inst.m_hotkey_dir)&&
           				(ServiceJbKbd.inst.m_hotkey_dir).length()==dir.length()){
            			initHot(dir);
                    	menu.add(str+"[(sel) "+f.getName()+"]",pos);
            		}
            		else { 
           				menu.add(str+"["+f.getName()+"]",pos);
            		}
            	}
            else
            {
// вывод простых шаблонов
            	if (rejim == 1) {
            		menu.add(str+f.getName(),pos);
            	}
            	else if (rejim == 2) {
            		st.tmps = f.getName();
            		if (st.tmps.endsWith(".calc"))
            			menu.add(str+st.tmps.substring(0, st.tmps.length()-5),pos);
            	}
            }
            pos++;
        }
//        TextView tv_path = (TextView) menu.m_MainView.findViewById(R.id.path);
//        tv_path.setVisibility(View.VISIBLE);
//        tv_path.setText(st.c().getString(R.string.mm_path)+":\n" + m_curDir.getPath());
        
//      st.UniObserver obs = new st.UniObserver()
		st.help = st.c().getString(R.string.mm_path)+st.STR_LF + Templates.inst.m_curDir.getPath()+st.STR_LF+st.STR_LF;
        st.UniObserver obs =  UniObserver();
//        {
//            @Override
//            public int OnObserver(Object param1, Object param2)
//            {
//                int pos = ((Integer)param1).intValue();
//                boolean bLong = ((Boolean)param2).booleanValue();
//                if (pos > -1)
//                	file1 = m_arFiles.get(pos);
//               	processTemplateClick(pos,bLong);
//                return 0;
//            }
//        };
        menu.show(obs, false);
    }
/** Функция для поиска конца строки в тексте, выбранном из едитора.
*@param f1 Позиция символа \r . для bLast=true вычисляется через lastIndexOf, для bLast =false - через indexOf 
*@param f2 Позиция символа \n. Или наоборот, пофигу
*@param bLast true - поиск ведется вверх от курсора, false - вниз от курсора
*@param len Текущая длина строки. Если len<4000 и ни одного переноса строки не найдено - для bLast = true вернет 0, для bLast=false - len
*@return Возвращает позицию конца или начала строки или -1, если не найдено (текст >=4000 символов и в нём нет ни одного переноса)*/
    static int chkPos(int f1,int f2,boolean bLast,int len)
    {
        int s = 0;
        if(f1>-1&&f2==-1)
            s = bLast?f1+1:f1;
        else if(f2>-1&&f1==-1)
            s = bLast?f2+1:f2;
        else if(f1==-1&&f2==-1)
        {
            if(len<4000)
            {
                if(bLast)
                    s = 0;
                else
                    s=len;
            }
            else 
                s = -1;
        }
        else 
        {
            if(bLast)
                s = f1>f2?f1:f2;
            else
                s = f1>f2?f2:f1;
        }
        return s;
    }
/** Возвращает текст начала слова вверх от курсора
*@param seq Текст, взятый функцией {@link InputConnection#getTextBeforeCursor(int, int)}. Может быть null
*@return Текст начала слова под курсором **/
    static String getCurWordStart(CharSequence seq,boolean bRetEmptyIfNotDelimiter)
    {
        if(seq==null)
        {
            seq = ServiceJbKbd.inst.getCurrentInputConnection().getTextBeforeCursor(40, 0);
        }
        if(seq==null)
            return null;
        int apostr = -1;
        for(int i=seq.length()-1;i>=0;i--)
        {
            char ch = seq.charAt(i);
            if(ch=='\'')
            {
                apostr=i;
                continue;
            }
            if(!Character.isLetterOrDigit(ch))
//            if(!ServiceJbKbd.inst.isWordSeparator(ch))
            {
                return seq.subSequence(apostr>-1&&i==apostr-1?apostr+1:i+1, seq.length()).toString();
            }
        }
        if(bRetEmptyIfNotDelimiter)
            return null;
        return seq.toString();
    }
/** Возвращает текст конца слова вниз от курсора
*@param seq Текст, взятый функцией {@link InputConnection#getTextBeforeCursor(int, int)}. Может быть null
*@param bRetEmptyIfNotDelimiter - true - если не найден конец слова, вернёт пустую строку. false - вернёт строку seq
*@return Текст конца слова под курсором **/
    static String getCurWordEnd(CharSequence seq,boolean bRetEmptyIfNotDelimiter)
    {
        if(seq==null)
        {
            seq=ServiceJbKbd.inst.getCurrentInputConnection().getTextAfterCursor(40, 0);
        }
        if(seq==null)
            return bRetEmptyIfNotDelimiter?null:st.STR_NULL;
        int apostr = -1;
        for(int i=0;i<seq.length();i++)
        {
            char ch = seq.charAt(i);
            if(ch=='\'')
            {
                apostr = i;
                continue;
            }
            if(!Character.isLetterOrDigit(ch))
//            if(!ServiceJbKbd.inst.isWordSeparator(ch))
            {
                return seq.subSequence(0, apostr>-1&&i==apostr+1?apostr:i).toString();
            }
        }
        if(bRetEmptyIfNotDelimiter)
            return null;
        return seq.toString();
    }
    public static boolean deleteDir(File dir)
    {
        if(!dir.isDirectory())
            return false;
        String[] children = dir.list();
        for (String p:children) 
        {
           File temp =  new File(dir, p);
           if(temp.isDirectory())
           {
               if(!deleteDir(temp))
                   return false;
           }
           else
           {
               if(!temp.delete())
                   return false;
           }
        }
        dir.delete();
        return true;
    }
    void setHotDir(String dir)
    {
    	st.pref().edit().putString(st.PREF_KEY_HOT_DIR, m_curDir.getAbsolutePath()+st.STR_SLASH+dir.trim()).commit();
    	//ServiceJbKbd.inst.m_hotkey_dir=m_curDir.getAbsolutePath()+st.STR_SLASH+dir.trim();
    }
    void initHot(String dir)
    {
    	File myFolder = new File(dir);
    	String[] fn=myFolder.list();
    	int ii=0;
    	for (int i = 0; i < fn.length; i++) {
			File f = new File(dir+st.STR_SLASH+fn[i]);
			if (f.isFile()){
				if (fn[i].startsWith("#[")){
					fn[i]=fn[i].toUpperCase();
					if (fn[i].contains("]")){
						ServiceJbKbd.inst.m_hot_str[ii]=fn[i].substring(2, fn[i].indexOf("]"));
						ServiceJbKbd.inst.m_hot_tpl[ii]=st.readFileString(f);
						ii++;
					} else
						st.toast("Error format (not \"]\") in\n"+fn[i]);
				}
    		}
    	}
		if (ii>100)
			ii=100;
		ServiceJbKbd ss = new ServiceJbKbd();
		if (ii>0)
        	ss.setTplCount(ii); 
		else
        	ss.setTplCount(0); 
    	ServiceJbKbd.inst.m_hotkey_dir = dir.trim();
    }
    void setDir(int dir, int typ)
    {
    	if (dir == 1) {
    		template_path = FOLDER_TEMPLATES;
    		rejim = 1;
    		type = 0;
    	}
    	else if (dir == 2) {
    		template_path = FOLDER_CALC;
    		rejim = 2;
    		type = typ;
    	}
    }
    void rootDir()
    {
        setDir(rejim,type);
        String rd = st.getSettingsPath()+template_path;
        m_rootDir = new File(rd);
        if(!m_rootDir.exists())
        {
            if(!m_rootDir.mkdirs())
                m_rootDir = null;
        }
        m_curDir = m_rootDir;
    }
    void addCalc()
    {
    	if (fpath.endsWith(".calc") == false) {
    		fpath+=".calc";
    	}
    }
    st.UniObserver UniObserver()
    {
      st.UniObserver obs = new st.UniObserver()
      {
          @Override
          public int OnObserver(Object param1, Object param2)
          {
              int pos = ((Integer)param1).intValue();
              boolean bLong = ((Boolean)param2).booleanValue();
              if (pos > -1) {
              	file1 = m_arFiles.get(pos);
              }
              processTemplateClick(pos,bLong);
              return 0;
          }
      };
      return obs;
    }
    void calcSavePrgQuery()
    {
        GlobDialog gd = new GlobDialog(st.c());
        gd.set(R.string.rewrite_question, R.string.yes, R.string.no);
        gd.setObserver(new st.UniObserver()
        {
            @Override
            public int OnObserver(Object param1, Object param2)
            {
                if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
                {
                	calcSavePrg();
                }
                return 0;
            }
        });
        gd.showAlert();
    }
    
    /** Текущая папка */    
    File m_cd;
 // режим использования 
    // 1 - шаблоны
    // 2 - калькулятор
    public static int rejim = 1;
    public static int INT_FOLDER_TEMPLATES = 1;
    public static int INT_FOLDER_CALC = 2;
    public static String FOLDER_TEMPLATES = "templates";
    public static String FOLDER_CALC = "calc";
 /** тип<br>
    * 0 - шаблоны<br>
    * 1 - запись программы калькулятора<br>
    * 2 - загрузить программу калькулятора*/
    public static int type = 0;
    public static String template_path = FOLDER_TEMPLATES;
    File file1;
    File m_rootDir;
    File m_curDir;
    int m_state=0;
    String dir_hotkey=st.STR_NULL;
    String m_hot_format=st.STR_NULL;
    String cur_dir=st.STR_NULL;
    String dir=st.STR_NULL;
    String fpath = st.STR_NULL;
/** Состояние - редактирование папки */ 
    public static final int STAT_EDIT_FOLDER = 0x00001;
    public static final int IB_SEL = 0;
    public static final int IB_WORD = 1;
    public static final int IB_LINE = 2;
    public static final char TPL_SPEC_CHAR = '$';
    /** массв слов специнструкций. ПОРЯДОК НЕ МЕНЯТЬ! (используются в других местах) */
    public static final String[] Instructions = 
    	{
    	"select",
    	"selword",
    	"selline",
    	"datetime",
    	"sellowercase",
    	"selupcase",
    	"selinsertword",
    	"selDeleteWord",
    	"seltranslit",
    	"selVerseMode",
    	"selAsInTheSentences",
    	"paste",
    	"selReplaсe",
    	"selToPos",
    	"program",
    	"codes"
    	};
    /** константа для специнструкции selReplace */
    public static final String STR_SEARCH = "@SEARCH:"; 
    /** константа для специнструкции selReplace */
    public static final String STR_REPLACE = "@REPLACE:"; 
    /** константа для специнструкции program */
    public static final String STR_TPL= "@TPL:"; 
    File file;
    ArrayList<File> m_arFiles;
}
