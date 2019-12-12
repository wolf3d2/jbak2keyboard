package com.jbak2.JbakKeyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

import com.jbak2.Dialog.Dlg;
import com.jbak2.JbakKeyboard.IKeyboard.Keybrd;
import com.jbak2.JbakKeyboard.IKeyboard.Lang;
import com.jbak2.ctrl.GlobDialog;

public class LangSetActivity extends Activity
{
	/** массив языков вверху списка */
	public static String[] ar_main_lang = new String[]
			{
				"en",
				"ru",
				"es",
				"uk",
		    };
    static LangSetActivity inst;
    LangAdapter m_adapt;
    ListView m_list;
    /** текущие доступные языки en, ru, ... */
	public static ArrayList<LangInfo> arLang = new ArrayList<LangInfo>();

	public static class LangInfo {
		String displayName;
		/** короткое название (ru, en и тд) */
		String langName;

		public LangInfo() {
			displayName = st.STR_NULL;
			langName = st.STR_NULL;
		}
	}
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        inst = this;
        st.setDefaultLang();
        CustomKeyboard.updateArrayKeyboards(false);
        sortArrayKeyboard();
        m_adapt = new LangAdapter(this, R.layout.lang_list_item);
        for(Lang l:st.arLangs) {
        	if (l.name.compareToIgnoreCase(st.LANG_HIDE_LAYOUT)== 0) 
        		continue;
        	m_adapt.add(l);
        }
        
        View v = getLayoutInflater().inflate(R.layout.pref_view, null);
        m_list = (ListView)v.findViewById(android.R.id.list);
        m_list.setPadding(2, 10, 2, 0);
        m_list.setAdapter(m_adapt);
// кнопка "старая загрузка словарей" в языках и раскладках
//        View topView = v.findViewById(R.id.top_item);
//        topView.setVisibility(View.VISIBLE);
//        TextView tw = (TextView)topView.findViewById(R.id.text);
//        tw.setText(R.string.set_key_ac_load_vocab);
//        tw.setTextColor(0xff0000ff);
//        tw.setBackgroundResource(android.R.drawable.btn_default);
//        ((TextView)topView.findViewById(R.id.desc)).setText(st.STR_SPACE);
//        topView.setOnClickListener(new OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                st.runAct(UpdVocabActivity.class);
//            }
//        });
        setContentView(v);
        Ads.show(this,1);
        super.onCreate(savedInstanceState);
        Intent in = getIntent();
        if (in!=null) {
        	int help = in.getIntExtra(Quick_setting_act.EXXTRA_HELP, 0);
        	if (help == 1)
        		Dlg.helpDialog(inst, inst.getString(R.string.qs_btn3_help));
            	//st.help(R.string.qs_btn3_help, inst);
        }
        
	}
    public void sortArrayKeyboard() 
    {
    	/** виртуальные языки */
        Vector<Keybrd> arv = new Vector<Keybrd>();
    	/** первые языки */
        Vector<Keybrd> arf = new Vector<Keybrd>();
    	/** остальные языки */
        Vector<Keybrd> aro = new Vector<Keybrd>();
        // заполняем массив доступными языками (en, ru...)
        if (arLang==null)
        	arLang = new ArrayList<LangInfo>(); 
        if (arLang!=null)
        	arLang.clear();
        LangInfo li = null;
        for (Lang ll:st.arLangs) {
//        	if (ll.name.compareToIgnoreCase("uk") == 0)
//        		st.toast(inst,"bbb");
        	li = new LangInfo();
        	li.displayName = ll.getLangDisplayName(ll.name);
        	li.langName = ll.name;
			arLang.add(li);
        }
//        for (int i=0; i<st.arLangs.length;i++) {
//        	kk = st.arKbd[i];
//        	li = new LangInfo();
//			li.displayName = kk.lang.getLangDisplayName(kk.lang.getName(this));
////			li.displayName = li.displayName.substring(0, 1).toUpperCase()
////					+ li.displayName.substring(1).toLowerCase();
//			li.langName= kk.lang.name;
//			ind = arLang.indexOf(li);
//			if (ind<0)
//				arLang.add(li);
//        }
		String[] ar = new String[arLang.size()];
		for (int i = 0; i < arLang.size(); i++) {
			ar[i] = arLang.get(i).displayName;
		}
		Arrays.sort(ar);
		
		// массив основных языков
		Vector<Lang> armain = new Vector<Lang>();
		// массив неизвестных языков
		Vector<Lang> arunk = new Vector<Lang>();
		// массив виртуальных языков
		Vector<Lang> arvirt = new Vector<Lang>();
		// массив известных языков
		Vector<Lang> aroth= new Vector<Lang>();
		String nl = null;
		String shortname = null;
		for (String name:ar) {
			shortname = getSearchLangShortName(name);
			for (Lang ll:st.arLangs) {
				nl = ll.name;
				if (shortname.compareToIgnoreCase(nl)== 0) {
					// скрываем раскладки типа hide
		        	if (nl.compareToIgnoreCase(st.LANG_HIDE_LAYOUT)== 0) 
		        		continue;
					if (nl.compareTo(st.LANG_SYMBOL2_KBD) == 0) {
						ll.setTypeLang(st.TYPE_LANG_SYMBOL2);
					} 
					if (ll.isVirtualLang()) {
						if (arvirt.size()==0)
							ll.setTypeLang(st.TYPE_LANG_VIRTUAL);
						arvirt.add(ll);
						break;
					} 
					else if (isMainLang(shortname)) {
						if (armain.size()==0)
							ll.setTypeLang(st.TYPE_LANG_MAIN);
						armain.add(ll);
						break;
					}
					else if (getSearchLang(nl)) {
						if (arunk.size()==0)
							ll.setTypeLang(st.TYPE_LANG_UNKNOWN);
						arunk.add(ll);
						break;
					}
					else {
						if (aroth.size()==0)
							ll.setTypeLang(st.TYPE_LANG_OTHER);
						aroth.add(ll);
						break;
					}
				}
			}
		}
		// собираем
		Vector <Lang> copy = new Vector<Lang>();
		if (arvirt.size()>0) {
			for (Lang ll:arvirt) {
				copy.add(ll);
			}
		}
		if (armain.size()>0) {
			for (Lang ll:armain) {
				copy.add(ll);
			}
		}
		if (arunk.size()>0) {
			for (Lang ll:arunk) {
				copy.add(ll);
			}
		}
		if (aroth.size()>0) {
			for (Lang ll:aroth) {
				copy.add(ll);
			}
		}
		if (copy.size()>0) {
			st.arLangs = new Lang[copy.size()];
			String sss = null;
			for (int i=0; i<copy.size();i++) {
				st.arLangs[i] = copy.get(i);
				sss +=st.arLangs[i].name+", ";
			}
		}
    }
    @Override
    public void onBackPressed() {
    	if (GlobDialog.gbshow){
    		GlobDialog.inst.finish();
    		return;
    	}
        String langs = m_adapt.getLangString();
        st.pref().edit().putString(st.PREF_KEY_LANGS, langs).commit();

     	super.onBackPressed();
    }
    @Override
    protected void onDestroy()
    {
        inst = null;
        super.onDestroy();
    }
    View.OnClickListener m_butListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Lang l = (Lang)v.getTag();
//        	st.show_kbd1 = true;
            try{
                Intent in = new Intent(Intent.ACTION_VIEW)
                .setComponent(new ComponentName(v.getContext(), SetKbdActivity.class))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(st.SET_INTENT_ACTION, st.SET_SELECT_KEYBOARD)
                .putExtra(st.SET_INTENT_LANG_NAME, l.name);
                // добавил строчку 2.07.19
                // на завтра закоментил,
                // убрал, потому как вызовет больше непоняток у юзеров -
                // по дефолту показывает не Портрет и ландшафт, 
                //а текущую ориентацию экрана
                //.putExtra(st.SET_SCREEN_TYPE, st.isLandscape(inst)?2:1);
                startActivity(in);
            }
            catch(Throwable e)
            {
            }

            
        }
    };
    class LangAdapter extends ArrayAdapter<IKeyboard.Lang>
    {
        String getLangString()
        {
        	Lang ll = null;
            String ret=st.STR_NULL;
            for(String s:m_arLangs)
            {
            	ll = getLangAtByName(s);
            	if (ll==null)
            		continue;
            	if (ll.isVirtualLang())
            		continue;
                if(ret.length()>0)
                    ret+=st.STR_COMMA;
                ret+=s;
            }
            return ret;
        }
        Lang getLangAtByName(String lang)
        {
            for (Lang ll:st.arLangs){
                if(lang.equals(ll.name))
                    return ll;
            }
            return null;
        }
        int searchLang(String lang)
        {
            int pos = 0;
            for(String lng:m_arLangs)
            {
                if(lang.equals(lng))
                    return pos;
                pos++;
            }
            return -1;
        }
        OnCheckedChangeListener m_chkListener = new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                Lang l = (Lang)buttonView.getTag();
                if(isChecked)
                {
                    int f = searchLang(l.name);
                    if(f<0)
                   		m_arLangs.add(l.name);
                }
                else
                {
                    int f = searchLang(l.name);
                    if(f>-1)
                        m_arLangs.remove(f);
                }
            }
        };
        public LangAdapter(Context context, int textViewResourceId)
        {
            super(context, textViewResourceId);
            rId = textViewResourceId;
            //st.setDefaultLang();
            boolean canAdd = false;
            m_arLangs = new Vector<String>();
            for(String l:st.getLangsArray(context))
            {
                canAdd = false;
                if(searchLang(l)==-1)
                {
                	
                    for(Lang lang:st.arLangs)
                    {
                        if(lang.name.equals(l))
                        {
                            canAdd = true;
                            break;
                        }
                    }
                }
                if(canAdd)
                    m_arLangs.add(l);
            }
        }
        /** Возвращает язык для позиции pos*/        
        Lang getLangAtPosNew(int pos)
        {
        	if (pos>= st.arLangs.length)
        		return null;
            return st.arLangs[pos];
        }
        /** Возвращает язык для позиции pos, так, чтобы виртуальные языки были снизу*/        
        Lang getLangAtPos(int pos)
        {
        	if (pos>= st.arLangs.length)
        		return null;
            int cp = 0;
            int vp = -1;
            for(Lang l:st.arLangs)
            {
                if(l.isVirtualLang())
                {
                    if(vp<0)
                    	vp = cp;
                }
                else
                {
                    if(cp==pos)
                        return l; 
                    ++cp;
                }
            }
            return st.arLangs[vp+pos-cp];
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if(convertView==null)
                convertView = getLayoutInflater().inflate(rId, null);
            Lang l = getLangAtPosNew(position);
   			TextView tv = (TextView)convertView.findViewById(R.id.head);
   			tv.setVisibility(View.GONE);
        // старая строка, закоментил 08.06.19
        //if (!l.name.startsWith("hide")) {
       	if (!l.name.startsWith(IKeyboard.LANG_HIDE_LAYOUT+st.STR_UNDERSCORING)) {
       		if (l.type!=st.TYPE_LANG_NONE) {
       			int res = 0;
       			switch (l.type)
       			{
       			case st.TYPE_LANG_MAIN:
       				res = R.string.type_lang_main;
       				break;
       			case st.TYPE_LANG_SYMBOL2:
       				res = R.string.type_lang_symbol2;
       				tv.setTextColor(0xffFFFF00);
       				break;
       			case st.TYPE_LANG_OTHER:
       				res = R.string.type_lang_all;
       				break;
       			case st.TYPE_LANG_UNKNOWN:
       				res = R.string.type_lang_unknown;
       				break;
       			case st.TYPE_LANG_VIRTUAL:
       				res = R.string.type_lang_virtual;
       				break;
       			}
       			if (res!=0) {
           			tv.setText(res);
           			tv.setVisibility(View.VISIBLE);
       			}
       		}
            CheckBox cb = (CheckBox)convertView.findViewById(R.id.checkbox);
            cb.setText(l.getName(inst));
            cb.setTag(l);
            cb.setEnabled(true);
            if (l.isVirtualLang()){
            	cb.setEnabled(false);
            	cb.setChecked(true);
            } else {
                boolean bCheck = false;
                for(String s:m_arLangs)
                {
                    if(s.equals(l.name))
                    {
                    	// старая строка, закоментил 08.06.19
                    	//if (l.name.contains("hide") == false) {
                    	if (!l.name.startsWith(IKeyboard.LANG_HIDE_LAYOUT+st.STR_UNDERSCORING)) {
                    		bCheck=true;
                    		break;
                    	}
                    }
                }
                cb.setChecked(bCheck);
            }
            cb.setOnCheckedChangeListener(m_chkListener);
            Button b = (Button)convertView.findViewById(R.id.button);
            if(st.getKeybrdArrayByLang(l.name).size()>1)
            {
                b.setOnClickListener(m_butListener);
                b.setVisibility(View.VISIBLE);
            }
            else
            {
                b.setVisibility(View.GONE);
                //b.getLayoutParams().width = 0;
            }
            b.setTag(l);
        }
            return convertView;
        
        }
        int rId;
        Vector<String> m_arLangs;
        TextView tv_head;
    }
    /** возвращает true, если языка name, нет в массиве LangInfo arLang 
     * и его displayName и langName одинаковые, то есть, язык неизвестен
     * */
    boolean getSearchLang(String name)
    {
        for(LangInfo li:arLang)
        {
        	if (li.langName.compareTo(name)== 0) {
                if(li.displayName.compareToIgnoreCase(li.langName)==0) {
                    return true;
                }
                break;
            }
        }
        return false;
    }
    /** true, если это один из главных языков */
    boolean isMainLang(String name)
    {
        for(String ss:ar_main_lang)
        {
        	if (ss.compareToIgnoreCase(name)== 0) 
                return true;
        }
        return false;
    }
    String getSearchLangShortName(String name)
    {
        for(LangInfo li:arLang)
        {
        	if (li.displayName.compareTo(name)== 0) {
                return li.langName;
            }
        }
        return st.STR_NULL;
    }

}