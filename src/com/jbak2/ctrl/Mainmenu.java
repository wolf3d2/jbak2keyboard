package com.jbak2.ctrl;

import java.util.ArrayList;

import com.jbak2.JbakKeyboard.R;
import com.jbak2.JbakKeyboard.st;
// класс для хранения сведений о пунктах главного меню
public class Mainmenu {
	// массив пунктов главного меню
	public static ArrayList<Mainmenu> arMenu = new ArrayList<Mainmenu>();
    public String name;
    public int code;
    

    public Mainmenu(int nameId,int cod)
    {
        name = st.c().getString(nameId);
        code = cod;
    }
    public Mainmenu(String nameStr,int cod)
    {
        name = nameStr;
        code = cod;
    }
    public Mainmenu()
    {
    }
    public void createArMenu(String ars)
    {
    	ArrayList<Mainmenu> ar= new ArrayList<Mainmenu>();
    	ar.addAll(this.getAllItem());
    	String[] armenu = ars.split(st.STR_COMMA);
    	if (arMenu!=null)
    		arMenu.clear();
    	Mainmenu mm;
    	int code = 0;
    	for (int i=0;i<armenu.length;i++){
			try{
				code = Integer.valueOf(armenu[i]);
			} catch (Throwable e){
				code = 0;
			}
    		mm = getItemByCode(ar, code);
    		if (mm!=null)
    			arMenu.add(mm);
    	}
    	
    }
    public Mainmenu getItemByCode(ArrayList<Mainmenu> ar,int code)
    {
    	if (code == 0)
    		return null;
    	for (Mainmenu mm:ar){
    		if (code == mm.code)
    			return mm;
    	}
    	return null;
    }
    /** пункты меню по умолчанию */
    public ArrayList<Mainmenu> getDefaultItem()
    {
    	ArrayList<Mainmenu> ar= new ArrayList<Mainmenu>();
    	ar.add(new Mainmenu(R.string.mm_templates, st.CMD_TPL));
    	ar.add(new Mainmenu(R.string.mm_multiclipboard, st.CMD_CLIPBOARD));
    	ar.add(new Mainmenu(R.string.mm_settings, st.CMD_PREFERENCES));
    	ar.add(new Mainmenu(R.string.addit_layout_menuname, st.CMD_SHOW_ADDITIONAL_HIDE_LAYOUT));
    	ar.add(new Mainmenu(R.string.lang_calc, st.CMD_CALC));
    	ar.add(new Mainmenu(R.string.mainmenu_setting, st.CMD_RUN_MAINMENU_SETTING));
    	return ar;
    }
    /** возвращает все пункты главного меню */
    public ArrayList<Mainmenu> getAllItem()
    {
    	ArrayList<Mainmenu> ar= new ArrayList<Mainmenu>();
    	ar.add(new Mainmenu(R.string.mainmenu_setting, st.CMD_RUN_MAINMENU_SETTING));
    	ar.add(new Mainmenu(R.string.mm_templates, st.CMD_TPL));
    	ar.add(new Mainmenu(R.string.mm_multiclipboard, st.CMD_CLIPBOARD));
    	ar.add(new Mainmenu(R.string.mm_settings, st.CMD_PREFERENCES));
    	ar.add(new Mainmenu(R.string.lang_calc, st.CMD_CALC));
    	ar.add(new Mainmenu(R.string.mm_ac_hide0, st.CMD_AC_HIDE));
    	ar.add(new Mainmenu(R.string.mm_stop_dict0, st.CMD_TEMP_STOP_DICT));
    	ar.add(new Mainmenu(R.string.set_keyboard_height, st.CMD_HEIGHT_KEYBOARD));
//    	ar.add(new Mainmenu(R.string.lang_calc, st.CMD_INPUT_METHOD));
    	ar.add(new Mainmenu(R.string.set_input_keyboard, st.CMD_INPUT_KEYBOARD));
    	ar.add(new Mainmenu(R.string.set_select_layout, st.CMD_SELECT_KEYBOARD));
    	ar.add(new Mainmenu(R.string.menu_sel_layout, st.CMD_MENU_QUICK_SELECT_LAYOUT));
    	ar.add(new Mainmenu(R.string.set_languages, st.CMD_START_SET_LANG_ACTIVITY));
    	ar.add(new Mainmenu(R.string.set_key_skins, st.CMD_RUN_SELECT_SKIN));
    	ar.add(new Mainmenu(R.string.menu_sel_skin, st.CMD_MENU_QUICK_SELECT_SKIN));
    	ar.add(new Mainmenu(R.string.mm_runapp, st.CMD_RUN_APP));
    	ar.add(new Mainmenu(st.getStr(R.string.mm_reload_skin)+st.getStr(R.string.mm_reload_skin_desk), st.CMD_RELOAD_SKIN));
    	ar.add(new Mainmenu(R.string.set_key_landscape_input, st.CMD_FULL_DISPLAY_EDIT));
    	ar.add(new Mainmenu(R.string.gesture_select_share, st.CMD_SHARE_SELECTED));
    	ar.add(new Mainmenu(R.string.gesture_copy_share, st.CMD_SHARE_COPIED));
    	ar.add(new Mainmenu(R.string.mm_compil, st.CMD_COMPILE_KEYBOARDS));
    	ar.add(new Mainmenu(R.string.mm_decompil, st.CMD_DECOMPILE_KEYBOARDS));
    	ar.add(new Mainmenu(R.string.gesture_trans_sel, st.CMD_TRANSLATE_SELECTED));
    	ar.add(new Mainmenu(R.string.gesture_trans_copy, st.CMD_TRANSLATE_COPIED));
    	ar.add(new Mainmenu(R.string.gesture_search_sel, st.CMD_SEARCH_SELECTED));
    	ar.add(new Mainmenu(R.string.gesture_search_copy, st.CMD_SEARCH_COPYING));
    	ar.add(new Mainmenu(R.string.euv_actname, st.CMD_EDIT_USER_VOCAB));
    	ar.add(new Mainmenu(R.string.mm_copy_notation, st.CMD_SHOW_COPY_NUMBER_ANY_NOTATION));
    	ar.add(new Mainmenu(R.string.ss_name, st.CMD_INSERT_SPEC_SYMBOL));
    	ar.add(new Mainmenu(R.string.user_hide_layout_menuname, st.CMD_SHOW_USER_HIDE_LAYOUT));
    	ar.add(new Mainmenu(R.string.addit_layout_menuname, st.CMD_SHOW_ADDITIONAL_HIDE_LAYOUT));
    	ar.add(new Mainmenu(R.string.mm_font_kbd, st.CMD_SHOW_FONT_KEYBOARD_DIALOG));

    	return ar;
    }
    public Mainmenu getItemByIndex(int index)
    {
    	for (int i=0;i<arMenu.size();i++){
    		if (i == index)
    			return arMenu.get(i);
    	}
    	return null;
    }
}