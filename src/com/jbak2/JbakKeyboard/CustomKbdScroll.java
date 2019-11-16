package com.jbak2.JbakKeyboard;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.RectShape;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jbak2.CustomGraphics.GradBack;
import com.jbak2.CustomGraphics.draw;
import com.jbak2.JbakKeyboard.IKeyboard.Lang;
import com.jbak2.ctrl.GlobDialog;

/** Раскладка с вертикальным скролом */
public class CustomKbdScroll
{
	static boolean close_menu = false; 
	static int arcounter =0;
	static int posY =0;
	int btn_mark_color = Color.BLACK;
	String btn_mark_text = st.STR_NULL;
    JbCandView                 m_candView;
    /** Элемент-разделитель. Не нажимается, имеет оформление, отличное от основного списка*/
    public static final int ID_DELIMETER            =-2;
/** Окно меню */    
    static View m_MainView;
    ArrayList<MenuEntry> m_arItems = new ArrayList<MenuEntry>();
    int m_state = 0;
    public static final int STAT_TEMPLATES = 0x000001;
    public static final int STAT_CLIPBOARD = 0x000002;
    public static final int STAT_CALC_HISTORY = 0x000003;
    public static final int STAT_TRANSLATE = 0x000004;
    public static final int STAT_KEYCODE_NOTATION = 0x000005;

    static CustomKbdScroll inst; 
    protected static final int[] PRESSED_STATE_SET = {android.R.attr.state_pressed};
    protected static final int[] EMPTY_STATE_SET = {};
/** Класс, хранящий информацию об элементе меню */  
    public static class MenuEntry
    {
/**
 * Конструктор      
 * @param t Текст элемента
 * @param i id элемента. Может быть одной из констант ID_ . Если  = ID_DELIMETER - выводит ненажимаемый элемент с другим оформлением
 */
        public MenuEntry(String t,int i)
        {
            text = t;
            id = i;
            date = -1;
        }
        public MenuEntry(String t,int i,long dat)
        {
            text = t;
            id = i;
            date = dat;
        }
/** Текст элемента */       
        String text;
/** id элемента */      
        int id;
        // дата для поиска и удаления записей мультибуфера
        long date;
    }
/** Конструктор 
 * @param act Контекст */   
    CustomKbdScroll()
    {
        inst = this;
        m_MainView = ServiceJbKbd.inst.getLayoutInflater().inflate(R.layout.com_menu, null);
    }
/** Устанавливает фон ненажатой кнопки, соответствующий текущему оформлению клавиатуры*/    
//    void setButtonKeyboardBackground(View btn)
//    {
//        if(st.kv().m_KeyBackDrw!=null)
//        {
//            btn.setBackgroundDrawable(st.kv().m_drwKeyBack);
////            btn.setOnTouchListener(m_btnListener);
//        }
//    }
/** Создаёт новую кнопку элемента меню */   
    View newView(MenuEntry ent)
    {
        Button btn = new Button(st.c());
        int pad = (int) st.floatDp(10, st.c());
        if(st.kv().isDefaultDesign())
        {
            btn.setBackgroundDrawable(st.kv().m_drwKeyBack.mutate());
        }
        else
        {
        	try{
        	RectShape clon = st.kv().m_curDesign.m_keyBackground.clone();
            btn.setBackgroundDrawable(((GradBack)clon).getStateDrawable());
        	}
        	catch (Throwable e) {
				// TODO: handle exception
			}
        }
//        setButtonKeyboardBackground(btn);
        btn.setHeight(st.kv().m_KeyHeight);
        btn.setTextColor(draw.paint().mainColor);
        btn.setTextSize(st.mm_btn_size);
        if(st.has(m_state, STAT_TEMPLATES)||st.has(m_state, STAT_CLIPBOARD))
        {
            btn.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
            btn.setLongClickable(true);
            btn.setOnLongClickListener(m_longListener);
        }
        else if(st.has(m_state, STAT_TRANSLATE))
        {
            btn.setGravity(Gravity.CENTER_VERTICAL);
            btn.setLongClickable(true);
            btn.setOnLongClickListener(m_longListener);
        }
        else if(st.has(m_state, STAT_KEYCODE_NOTATION))
        {
            btn.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
            btn.setLongClickable(true);
            btn.setOnLongClickListener(m_longListener);
        }
        btn.setMaxLines(2);
        btn.setEllipsize(TruncateAt.MARQUEE);
        btn.setPadding(pad, pad, pad, pad);
        btn.setTag(ent);
        btn.setText(ent.text);
        btn.setTransformationMethod(null);
        btn.setOnClickListener(m_listener);
        return btn;
    }
/** Обработчик нажатия кнопки меню */  
    st.UniObserver m_lvObserver = new st.UniObserver()
    {
        @Override
        public int OnObserver(Object param1, Object param2)
        {
            if(m_MenuObserver==null)return 0;
            m_MenuObserver.m_param1 = param1;
            m_MenuObserver.Observ();
            return 0;
        }
    };
    int m_longClicked=-1;
/** Обработчик длинного нажатия элемента меню */
    OnLongClickListener m_longListener = new OnLongClickListener()
    {
        @Override
        public boolean onLongClick(View v)
        {
            VibroThread vt = VibroThread.getInstance(st.c());
            vt.runVibro(VibroThread.VIBRO_LONG);
            if(!st.has(m_state, STAT_CLIPBOARD))
                close();
            MenuEntry me = (MenuEntry)v.getTag();
            m_longClicked = me.id;
            if(m_MenuObserver!=null)
            {
                m_MenuObserver.OnObserver(new Integer(me.id),new Boolean(true));
            }
            return true;
        }
    };
/** Сторонний обработчик, который был передан в функции {@link #show(com.jbak2.JbakTaskMan.st.UniObserver)}*/    
    st.UniObserver m_MenuObserver;
/** Добавляет в меню элемент с текстом text и идентификатором id */ 
    void add(String text,int id)
    {
        m_arItems.add(new MenuEntry(text, id));
    }
/** Добавляет в меню элемент со id строки tid, которая берётся из ресурсов и идентификатором id */  
    void add(int tid,int id)
    {
        add(st.c().getString(tid),id);
    }
    // конструктор меню для записей мультибуфера
    void add(String text,int id, long date)
    {
        m_arItems.add(new MenuEntry(text, id, date));
    }
    void add(int tid,int id, long date)
    {
        add(st.c().getString(tid),id, date);
    }
    static void closeTplAndTrans()
    {
    	if (Templates.inst!=null)
    		Templates.inst = null;
    	if (Translate.inst!=null)
    		Translate.inst = null;
    }

    static void close()
    {
    	if (st.fl_fiks_clip)
    		return;
        inst = null;
        close_menu = false;
        if(ServiceJbKbd.inst!=null)
        {
            try{
                st.kv().setKeyboard(st.curKbd());
                ServiceJbKbd.inst.processTextEditKey(-20);
            }
            catch (Throwable e) {
            }
        }
    }
/** Обработчик короткого нажатия кнопок меню */    
    View.OnClickListener m_listener = new View.OnClickListener()
    {
        
        @Override
        public void onClick(View v)
        {
            VibroThread vt = VibroThread.getInstance(st.c());
            vt.runVibro(VibroThread.VIBRO_SHORT);
            switch (v.getId())
            {
            	case R.id.clipboard_sync:
            		st.hidekbd();
            		st.runAct(ClipbrdSyncAct.class,st.c());
            		setButtonPointTopDrawable((Button)m_MainView.findViewById(R.id.clipboard_sync), st.fl_sync);
            		return;
            	case R.id.trans_lang: 
            		//close();
                	final GlobDialog gd1 = new GlobDialog(ServiceJbKbd.inst);
                	String txt = st.pref().getString(st.PREF_TRANSLATE_INTERFACE, st.getSystemLangApp(false));
               		gd1.ret_edittext_text = txt;
                    gd1.set(ServiceJbKbd.inst.getString(R.string.mm_translate_interfase_browser), R.string.ok, R.string.cancel);
                    gd1.setObserver(new st.UniObserver()
                      {
                          @Override
                          public int OnObserver(Object param1, Object param2)
                          {
                              if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
                              {
                            	  gd1.ret_edittext_text = gd1.ret_edittext_text.trim().toLowerCase();
                            	  if (gd1.ret_edittext_text.length()!=2)
                            		  gd1.ret_edittext_text = st.getSystemLangApp(false);
                            	  st.pref().edit().putString(st.PREF_TRANSLATE_INTERFACE, gd1.ret_edittext_text).commit();
                        		  //st.showkbd();
                        		  if (Translate.inst!=null){
                        			  Translate.inst.onCloseEditor();
                        		  }
                              }
                              return 0;
                          }
                      });
                      gd1.showEdit(txt,0);
            		return;
            	case R.id.but_new_template_folder: 
            		st.kbdCommand(st.CMD_TPL_NEW_FOLDER); 
            		close();
            		return;
                case R.id.but_new_template: 
                	if (Translate.inst!=null){
                        ServiceJbKbd.inst.startActivity(Translate.getEditActIntent(null));
                        //close();
                	} else
                		st.kbdCommand(st.CMD_TPL_EDITOR);
                    close();
                	return;
                case R.id.clipboard_del_record:
                	if (close_menu){
                		close_menu = false;
                		
                	} else {
                		close_menu = true;
                	}
                	setButtonPointTopDrawable((Button)m_MainView.findViewById(R.id.clipboard_del_record),close_menu);
                	return;
                case R.id.clipboard_enter_end:
                	if (st.fl_enter_key){
                		st.fl_enter_key = false;
                	} else {
                		st.fl_enter_key = true;
                	}
                	st.pref().edit().putBoolean(st.PREF_KEY_CLIPBRD_ENTER_AFTER_PASTE, st.fl_enter_key);
                	setButtonPointTopDrawable((Button)m_MainView.findViewById(R.id.clipboard_enter_end), st.fl_enter_key);
                	return;
                case R.id.clear:
                    close();
                    st.hidekbd();
                    GlobDialog gd = new GlobDialog(st.c());
                    gd.set(R.string.clipboard_clear, R.string.yes, R.string.no);
                    gd.setObserver(new st.UniObserver()
                    {
                        @Override
                        public int OnObserver(Object param1, Object param2)
                        {
                            if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
                            {
                                st.stor().clearClipboard();
                            } 
                            st.showkbd();
                            return 0;
                        }
                    });
                    gd.showAlert();
                    return;
                case R.id.close:
                    close();
                	return;
                case R.id.help:
               		st.help += st.c().getString(R.string.mm_info);
                    close_menu=false;
                	return;
                case R.id.home:
                	close();
                	if (Templates.inst!=null) {
                		int r = Templates.inst.rejim;
                		int t = Templates.inst.type;
                    	new Templates(r,t).makeCommonMenu();
                	}
                	return;
                case R.id.fiks_tpl:
            		if(Templates.inst!=null){
            			if (Templates.inst.rejim == 1) {
            				if (st.fiks_tpl.length() > 0) {
            					st.fiks_tpl=st.STR_NULL;
            				} else {
            					st.fiks_tpl = Templates.inst.m_curDir.getPath();
            				}
            			}
               			if (Templates.inst.rejim == 2) {
               				if (st.fiks_calc.length() > 0) {
               					st.fiks_calc=st.STR_NULL;
               				} else {
               					st.fiks_calc = Templates.inst.m_curDir.getPath();
               				}
               			}
               			
                        st.UniObserver obs1 =  Templates.inst.UniObserver();
                        show(obs1,false);

            		}
            		return;
                case R.id.fiks_clip:
            		if(st.fl_fiks_clip){
            			st.fl_fiks_clip = false;
           			} else {
           				st.fl_fiks_clip = true;
           			}
                	((Button)v).setText(st.returnZamok(st.fl_fiks_clip));
               			
            		return;
            }
            MenuEntry me = (MenuEntry)v.getTag();
            if(m_MenuObserver!=null)
            {
                m_MenuObserver.OnObserver(new Integer(me.id), new Boolean(false));
//                if (close_menu)
//                	close();
            }
        }
    };
    Adapt m_adapter;
    AdapterView.OnItemLongClickListener m_itemLongClickListener = new AdapterView.OnItemLongClickListener()
    {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View clickedView, int pos, long id)
        {
            return true;
        }
        
    };
    void textMenuButton(String txt)
    {
    	TextView mm = ((TextView)m_MainView.findViewById(R.id.menuname));
   		mm.setText(txt);
        
    }
    
/** Показывает меню
 * @param observer Обработчик нажатия */    
    
	@SuppressLint("NewApi")
	void show(st.UniObserver observer, boolean fl_closemenu)
    {
		if (!fl_closemenu)
			close_menu = false;
    	sizeOfficialButton();
        Button fiks = (Button)m_MainView.findViewById(R.id.fiks_tpl);
        if (Templates.inst!=null) {
        	if (Templates.inst.rejim == 1) {
        		if (st.fiks_tpl.length() >0)
        			fiks.setText(st.returnZamok(true));
        		else
        			fiks.setText(st.returnZamok(false));
        	}
        	else if (Templates.inst.rejim == 2) {
        		if (st.fiks_calc.length() >0)
        			fiks.setText(st.returnZamok(true));
        		else
        			fiks.setText(st.returnZamok(false));
        	}
    }

    	TextView mm = ((TextView)m_MainView.findViewById(R.id.menuname));
        mm.setTextColor(draw.paint().mainColor);
        m_MenuObserver = observer;
        ListView lv = (ListView)m_MainView.findViewById(R.id.com_menu_container);
        m_adapter = new Adapt(st.c(), this);
        
        lv.setAdapter(m_adapter);
//        LinearLayout ll = (LinearLayout)m_MainView.findViewById(R.id.com_menu_container);
//        for(MenuEntry me:m_arItems)
//        {
//            ll.addView(newView(me));
//        }
        lv.setOnItemLongClickListener(m_itemLongClickListener);
//        if (posY > 0) {
//        	lv.smoothScrollToPosition(5);
//        	posY = 0;
//        }
        m_MainView.setBackgroundDrawable(st.kv().getBackground());
        View bClose = m_MainView.findViewById(R.id.close);
        if(bClose!=null)
        {
            bClose.setOnClickListener(m_listener);
        }
        hideOfficialButton();
        LinearLayout bl = (LinearLayout)m_MainView.findViewById(R.id.com_menu_buttons);
        int cnt = bl.getChildCount();
// вывод служебных кнопок меню 
        for(int i=cnt-1;i>=0;i--)
        {
            View v = bl.getChildAt(i);
            int id = v.getId();
            boolean bUse = false;
            switch (m_state)
            {
            case 0:
// меню калькулятора
                if(id==R.id.close)
                    bUse = true;
        		if(id==R.id.help)
        			bUse = true;
            	break;
            case 1:
// шаблоны
            	if (Templates.inst.rejim == 1) {
            		if(id==R.id.close)
            			bUse = true;
            		else if(id==R.id.but_new_template)
            			bUse = true;
            		else if(id==R.id.but_new_template_folder)
            			bUse = true;
            		else if(id==R.id.home)
            			bUse = true;
            		else if(id==R.id.tire)
            			bUse = true;
            		else if(id==R.id.fiks_tpl)
            			bUse = true;
            		else if(id==R.id.help)
            			bUse = true;
            	}
// меню калькулятора. Сохранить
            	else if (Templates.inst.rejim == 2&&Templates.inst.type == 1){
            		if(id==R.id.close)
            			bUse = true;
            		else if(id==R.id.but_new_template)
            			bUse = true;
            		else if(id==R.id.but_new_template_folder)
            			bUse = true;
            		else if(id==R.id.home)
            			bUse = true;
            		else if(id==R.id.tire)
            			bUse = true;
            		else if(id==R.id.help)
            			bUse = true;
            		else if(id==R.id.fiks_tpl)
            			bUse = true;
            	}
// меню калькулятора. Загрузить
            	else if (Templates.inst.rejim == 2&&Templates.inst.type == 2){
            		if(id==R.id.close)
            			bUse = true;
            		else if(id==R.id.home)
            			bUse = true;
            		else if(id==R.id.tire)
            			bUse = true;
            		else if(id==R.id.help)
            			bUse = true;
            		else if(id==R.id.fiks_tpl)
            			bUse = true;
            	}
            	break;
            case 2: 
// буфер обмена
                if(id==R.id.clear)
                    bUse = true;
// кнопка режима удаления (пока не понял как добавляются
// кнопки не служебные (как-то через MenuEntry
//                else if(id==R.id.seldel)
//        			bUse = true;
                else if(id==R.id.close)
                    bUse = true;
                else if(id==R.id.clipboard_sync){
                	if (st.fl_clipbrd_btn_sync_show){
                    	setButtonPointTopDrawable((Button)m_MainView.findViewById(R.id.clipboard_sync), st.fl_sync);
            			bUse = true;
                	}
                }
                else if(id==R.id.clipboard_del_record)
        			bUse = true;
                else if(id==R.id.clipboard_enter_end)
        			bUse = true;
        		else if(id==R.id.tire)
        			bUse = true;
                else if(id==R.id.help)
        			bUse = true;
        		else if(id==R.id.fiks_clip){
        			Button bb = (Button) v.findViewById(R.id.fiks_clip);
        			if (bb!=null)
        				bb.setText(st.returnZamok(st.fl_fiks_clip));
        			bUse = true;
        		}
                break;
            case 3:
// главное меню
                if(id==R.id.close)
                    bUse = true;
                else if(id==R.id.help)
        			bUse = true;
                break;
            case 4:
// переводчик            	
            	if (Translate.inst!=null) {
            		if(id==R.id.close)
            			bUse = true;
            		else if(id==R.id.but_new_template)
            			bUse = true;
            		else if(id==R.id.trans_lang)
            			bUse = true;
            	}
            	break;
            case 5:
// системы счисления
                if(id==R.id.close)
                    bUse = true;
                else if(id==R.id.help)
        			bUse = true;
                break;
            }

             if(bUse) {
                 v.setOnClickListener(m_listener);
                 v.setVisibility(View.VISIBLE);
        } else 
                 v.setVisibility(View.GONE);
        }
        ServiceJbKbd.inst.showCandView(false);
        ServiceJbKbd.inst.setInputView(m_MainView);
        ViewGroup.LayoutParams lp = m_MainView.getLayoutParams();
        lp.width= st.kv().getWidth();
        lp.height = st.kv().getHeight();
        m_MainView.setLayoutParams(lp);
    }
    public boolean has(int iii)
    {
    	if (m_state == iii)
    		return true;
    	return false;
    }
    public static void showCopy(final Context c)
        {
    	st.help = st.STR_NULL;
    	final CurInput ci = new CurInput();
    	if(!ci.init(ServiceJbKbd.inst.getCurrentInputConnection()))
    		return;
    	final com_menu menu = new com_menu();
    	menu.add(R.string.menu_copy_all, R.string.menu_copy_all);
    	if(ci.hasCurParagraph)
    		menu.add(R.string.menu_copy_paragraph, R.string.menu_copy_paragraph);
    	String word = ci.getTextWord(); 
    	if(!TextUtils.isEmpty(word))
    		menu.add(R.string.menu_copy_word, R.string.menu_copy_word);
    	menu.show(new st.UniObserver() {
			
			@Override
			public int OnObserver(Object param1, Object param2) {
//				ClipboardManager cm = (ClipboardManager)c.getSystemService(Service.CLIPBOARD_SERVICE);
				switch((Integer)param1)
				{
					case R.string.menu_copy_all:
						ServiceJbKbd.inst.processTextEditKey(st.TXT_ED_COPY_ALL);
                		st.messageCopyClipboard();
						break;
					case R.string.menu_copy_word:
						st.copyText(c, ci.getTextWord());
						break;
					case R.string.menu_copy_paragraph:
						st.copyText(c, ci.getTextParagraph());
						break;
				}
				menu.close();
				return 0;
			}
		}, false);
    }
    public static void showLangs(Context с)
    {
    	st.help = st.STR_NULL;
    	final String lang[] = st.getLangsArray(с);
    	final com_menu menu = new com_menu();
    	for(int i=0;i<lang.length;i++)
    	{
    		menu.add(Lang.getLangDisplayName(lang[i]),i);
    	}
    	menu.show(new st.UniObserver() {
			
			@Override
			public int OnObserver(Object param1, Object param2) {
				st.kv().setLang(lang[(Integer)param1]);
            	com_menu.close();
				return 0;
			}
		}, false);
    }
    /** показывает введённый код символа в разных системах счисления */
    public static void showNotationNumber(final Context c, String num)
    {
    	st.help = c.getString(R.string.set_longtap_keycode_help);
    	final String str[] = new String[4];
    	str[0]= " 2x: "+st.num2str(num, 2);
    	str[1]= " 8x: "+st.num2str(num, 8);
    	str[2]= "10x: "+st.num2str(num, 10);
    	str[3]= "16x: "+st.num2str(num, 16);
    	final com_menu menu = new com_menu();
        menu.m_state = STAT_KEYCODE_NOTATION;
    	menu.setMenuname(st.getStr(R.string.mm_notation));
    	for(int i=0;i<str.length;i++)
    	{
    		menu.add(str[i],i);
    	}
    	menu.show(new st.UniObserver() {
			
			@Override
			public int OnObserver(Object param1, Object param2) {
				// длинное нажатие на элемент
                if(((Boolean)param2).booleanValue())
                {
                    int pos = ((Integer)param1).intValue();
                    if (pos > -1){
                    	try {
//                    		ClipboardManager cm = (ClipboardManager)c.getSystemService(Service.CLIPBOARD_SERVICE);
//                    		String s = menu.m_arItems.get(pos).text;
//                    		s = s.substring(s.indexOf(st.STR_COLON)+1).trim();
//                    		cm.setText(s);
//                    		st.messageCopyClipboard();
                    		String s = menu.m_arItems.get(pos).text;
                    		s = s.substring(s.indexOf(st.STR_COLON)+1).trim();
                    		st.copyText(c, s);
                    	} catch (Throwable e) {}

                    }
    				com_menu.close();
                    return 0;
                }

				String out = str[(Integer)param1];
				out = out.substring(out.indexOf(st.STR_COLON)+1).trim();
        		if (ServiceJbKbd.inst!=null){
            		ServiceJbKbd.inst.onText(out);
            		ServiceJbKbd.inst.processCaseAndCandidates();
        		}

				com_menu.close();
				return 0;
			}
		}, false);
    }/** Функция создаёт меню для мультибуфера обмена */    
    public static boolean showClipboard(boolean fl_closemenu)
    {
    	Cursor c = st.stor().getClipboardCursor();
        if(c==null) {
        	st.toast((R.string.clipboard_empty));
            return false;
        }
    	st.help = st.STR_NULL;
        final CustomKbdScroll menu = new CustomKbdScroll();
    	menu.textMenuButton(ServiceJbKbd.inst.textMenuName(st.CMD_CLIPBOARD));
        menu.m_state = STAT_CLIPBOARD;
        int pos = 0;
        long date = -1;
        do
        {
            String s = c.getString(0);
// отображаем размер записи
            s = "("+st.getLengthOfString((long)s.length())+") "+s.trim();
            if(s.length()>50)
                s = s.substring(0, 50)+"...";
            s.replace('\n', ' ');
            date = c.getLong(2);
            
            menu.add(s,pos, date);
            ++pos;
        }while(c.moveToPrevious());
        c.close();
        st.UniObserver obs = new st.UniObserver()
        {
            @SuppressLint("NewApi")
			@Override
            public int OnObserver(Object param1, Object param2)
            {
                int id = ((Integer)param1).intValue();
                int pos = -1;
                MenuEntry me = null;
                for(int i=menu.m_arItems.size()-1;i>=0;i--)
                {
                	
                    if(menu.m_arItems.get(i).id==id)
                    {
                        pos = i;
                        me = menu.m_arItems.get(i);
                        break;
                    }
                }
                if(pos<0)
                    return 0;
                if(((Boolean)param2).booleanValue())
                {
                    if (Translate.inst!=null){
                        Intent in = new Intent(ServiceJbKbd.inst,TplEditorActivity.class)
                                .putExtra(TplEditorActivity.EXTRA_CLIPBOARD_ENTRY, -2)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ServiceJbKbd.inst.startActivity(in);
                        close();
                        return 0;
                    }
                	posY = pos;
                    arcounter = 0;
                    Intent in = new Intent(ServiceJbKbd.inst,TplEditorActivity.class)
                        .putExtra(TplEditorActivity.EXTRA_CLIPBOARD_ENTRY, pos)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ServiceJbKbd.inst.startActivity(in);
//                    ServiceJbKbd.inst.m_candView.remove();
                    close();
                    return 0;
                }
                Cursor c = st.stor().getClipboardCursor();
                if(c==null)
                    return 0;
                try{
                    c.move(0-id);
                    String cp = c.getString(0);
                    ServiceJbKbd.inst.onText(cp);
                    if (st.fl_enter_key)
                        ServiceJbKbd.inst.sendKeyChar(st.STR_LF.charAt(0));
// чекаем системный буфер перед показом мультибуфера чтобы 
// отображалось все скопированные тексты (без 5 секундной задержки
// если откоментить, то в буфере записи отображаются неверно                    
//                  	if(ClipbrdService.inst!=null)
//                      	ClipbrdService.inst.checkString(cp);
                }
                catch (Throwable e) {
                	st.logEx(e);
                }
                if (close_menu) {
                	if (me!=null){
                    	delete(me.date);
                    	menu.showClipboard(true);                    
                	}
                } else
                	menu.close();
                return 0;
            }
        };
        if (fl_closemenu)
            menu.show(obs,true);
        else
        	menu.show(obs,false);
    	setButtonPointTopDrawable((Button)m_MainView.findViewById(R.id.clipboard_del_record),close_menu);
    	setButtonPointTopDrawable((Button)m_MainView.findViewById(R.id.clipboard_enter_end), st.fl_enter_key);
    	setButtonPointTopDrawable((Button)m_MainView.findViewById(R.id.clipboard_sync), st.fl_sync);
        return true;
    }
    public static boolean showCalcHistory()
    {
    	st.help = st.STR_NULL;
        com_menu menu = new com_menu();
    	menu.setMenuname(ServiceJbKbd.inst.textMenuName(st.CMD_CALC_HISTORY));
        menu.m_state = STAT_CALC_HISTORY;
        int pos = 0;
        boolean bbb = false;
        if (st.calc_history[0] !=null&&st.calc_history[0].length() > 0)
        	bbb = true;
        while (bbb)
        {
            String s = st.calc_history[pos];
            if (s.length() >0&&pos<=9){
            	s = s.trim();
            	if(s.length()>50)
            		s = s.substring(0, 50)+"...";
//            s.replace('\n', ' ');
            	menu.add(s,pos);
            	++pos;
            } else 
            	bbb = false;
        }
        st.UniObserver obs = new st.UniObserver()
        {
            @Override
            public int OnObserver(Object param1, Object param2)
            {
                int pos = ((Integer)param1).intValue();
                boolean bLong = ((Boolean)param2).booleanValue();
                if (bLong == false){
                	ServiceJbKbd.inst.setWord(st.calc_history[pos],false);
                	com_menu.close();
                }
                return 0;
            }
        };
        menu.show(obs,false);
        return true;
    }
    public static boolean showCalcList()
    {
    	st.help = st.STR_NULL;
    	boolean fl = false;
    	String out = st.STR_LF;
		if (st.isCalcPrg() == false){
			st.toast(st.c().getString(R.string.calc_prog_empty));
			return true;
		}
		fl = false;
    	int count = 0;
    	String zero =st.STR_NULL;
    	for (int i=0;i < st.calc_prog.length;i++) {
    		if (count >= 4) {
    			count = 0;
    			out+=st.STR_LF;
    		}
    		if (st.calc_prog[i] < 0&&fl==false){
   				out+=st.STR_LF;
   	    		count = 0;
   				fl=true;
   			}
    		if (st.calc_prog[i]>=0&&fl == true)
    			fl=false;
    		if (st.calc_prog[i] >=0&&fl==false) {
    			zero = st.STR_NULL+i;
    			while (zero.length() < 4) {
    				zero=st.STR_ZERO+zero;
    			}
   				out += zero+st.STR_POINT+st.getCalcCommandText(st.calc_prog[i])+st.STR_SPACE;
   	    		count++;
   				fl = false;
    		}
    	}
       	ServiceJbKbd.inst.setWord(out,false);
        return true;
    }
    static class Adapt extends ArrayAdapter<MenuEntry>
    {
    	CustomKbdScroll m_menu; 
        public Adapt(Context context,CustomKbdScroll menu)
        {
            super(context,0);
            m_menu = menu;
        }
        @Override
        public int getCount() 
        {
            return m_menu.m_arItems.size();
        };
        @SuppressLint("NewApi")
		@Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            MenuEntry me = m_menu.m_arItems.get(position);
            if(convertView!=null)
            {
                Button b = (Button)convertView;
                b.setTag(me);
                b.setId(me.id);
                b.setText(me.text);
                b.setTransformationMethod(null);
            }
            else
            {
                convertView = m_menu.newView(me);
            }
            arcounter++;
            if (posY > 0&&arcounter >= getCount()) {
               ListView lv = (ListView)m_MainView.findViewById(R.id.com_menu_container);
                lv.smoothScrollToPosition(posY);
                posY = 0;
            }
            return convertView;
        }
    }
    void removeLastLongClicked()
    {
    	MenuEntry me = null;
        for(int i=m_arItems.size()-1;i>=0;i--)
        {
            me = m_arItems.get(i);
            if(me.id==m_longClicked)
            {
                m_arItems.remove(i);
                break;
            }
        }
        if(m_arItems.size()==0)
            close();
        else
            m_adapter.notifyDataSetChanged();
    }
    void hideOfficialButton()
    {
        Button btn = (Button) m_MainView.findViewById(R.id.close);
        btn.setVisibility(View.GONE);
        btn = (Button) m_MainView.findViewById(R.id.clear);
        btn.setVisibility(View.GONE);
        btn = (Button) m_MainView.findViewById(R.id.but_new_template);
        btn.setVisibility(View.GONE);
        btn = (Button) m_MainView.findViewById(R.id.but_new_template_folder);
        btn.setVisibility(View.GONE);
        btn = (Button) m_MainView.findViewById(R.id.home);
        btn.setVisibility(View.GONE);
        TextView tv = (TextView) m_MainView.findViewById(R.id.tire);
        tv.setVisibility(View.GONE);
        btn = (Button) m_MainView.findViewById(R.id.fiks_tpl);
        btn.setVisibility(View.GONE);
        btn = (Button) m_MainView.findViewById(R.id.fiks_clip);
        btn.setVisibility(View.GONE);
        btn = (Button) m_MainView.findViewById(R.id.help);
        btn.setVisibility(View.GONE);
        btn = (Button) m_MainView.findViewById(R.id.clipboard_sync);
        btn.setVisibility(View.GONE);
        btn = (Button) m_MainView.findViewById(R.id.clipboard_del_record);
        btn.setVisibility(View.GONE);
        btn = (Button) m_MainView.findViewById(R.id.clipboard_enter_end);
        btn.setVisibility(View.GONE);
        btn = (Button) m_MainView.findViewById(R.id.trans_lang);
        btn.setVisibility(View.GONE);
    }
    void sizeOfficialButton()
    {
    	Button btn = ((Button)m_MainView.findViewById(R.id.close));
    	btn.setTextSize(st.mm_btn_off_size);
    	btn = ((Button)m_MainView.findViewById(R.id.but_new_template));
    	btn.setTextSize(st.mm_btn_off_size);
    	btn = ((Button)m_MainView.findViewById(R.id.but_new_template_folder));
    	btn.setTextSize(st.mm_btn_off_size);
    	btn = ((Button)m_MainView.findViewById(R.id.clear));
    	btn.setTextSize(st.mm_btn_off_size);
    	btn = ((Button)m_MainView.findViewById(R.id.home));
    	btn.setTextSize(st.mm_btn_off_size);
    	btn = ((Button)m_MainView.findViewById(R.id.clipboard_sync));
    	btn.setTextSize(st.mm_btn_off_size);
    	btn = ((Button)m_MainView.findViewById(R.id.clipboard_del_record));
    	btn.setTextSize(st.mm_btn_off_size);
    	btn = ((Button)m_MainView.findViewById(R.id.clipboard_enter_end));
    	btn.setTextSize(st.mm_btn_off_size);
    	btn = ((Button)m_MainView.findViewById(R.id.trans_lang));
    	btn.setTextSize(st.mm_btn_off_size);
    	btn.setBackgroundResource(android.R.drawable.btn_default);
    	
    	TextView tv = ((TextView)m_MainView.findViewById(R.id.tire));
    	
    	btn = ((Button)m_MainView.findViewById(R.id.help));
    	btn.setTextSize(st.mm_btn_off_size);
    	btn = ((Button)m_MainView.findViewById(R.id.fiks_tpl));
    	btn.setTextSize(st.mm_btn_off_size+3);
    	btn = ((Button)m_MainView.findViewById(R.id.fiks_clip));
    	btn.setTextSize(st.mm_btn_off_size+3);
    }
    public static void setButtonPointTopDrawable(Button btn, boolean fl)
    {
    	if (btn==null)
    		return;
    	Drawable top = null;
    	if (!fl)
    		top = st.c().getResources().getDrawable(R.drawable.bullet_black);
    	else
    		top = st.c().getResources().getDrawable(R.drawable.bullet_red);
		btn.setCompoundDrawablesWithIntrinsicBounds(null, top , null, null);
    }
    public static void delete(long date)
    {
    	st.stor().removeClipboardByDate(date, 0);
    }    
}