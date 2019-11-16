package com.jbak2.JbakKeyboard;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Vector;

import com.jbak2.Dialog.Dlg;
import com.jbak2.JbakKeyboard.st.UniObserver;
import com.jbak2.ctrl.GlobDialog;
import com.jbak2.ctrl.ProgressOperation;
import com.jbak2.words.UserWords;
import com.jbak2.words.WordsService;
import com.jbak2.words.UserWords.WordArray;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public class EditUserVocab extends Activity
{
	int freq_w = 0;
	ListView lv = null;
	public static  EditUserVocab inst = null;
	String titleEditBox = st.STR_NULL;
	 // множитель для ид textview'ов слова и частоты
	static int FACTOR = 2;
	// цвета
	int TEXT_COLOR_BASE = Color.WHITE;
	int TEXT_COLOR_EDIT= Color.YELLOW;
	int TEXT_COLOR_DEL = Color.RED;
	int PADDING_LEFT = 5;
	int PADDING_RIGHT = 3;
    Vector<String> lang = null;
    String deflang = st.STR_NULL;
    String curlang = st.STR_NULL;
    public static String search_txt = st.STR_NULL;
    static String EXCLUDE_SQL_PREFIX = "android_metadata";
	Button btn_sellang = null;
	Button btn_search = null;
	TextView tv_cnt_word = null;
	CheckBox cb_deflang = null;
	CheckBox cb_noquery= null;
	boolean fl_changed = false;
	RelativeLayout rlcap=null;
	
	UserWords userword = null;
    EuvAdapt m_adapter = null;
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        inst = this;
        setContentView(R.layout.edit_user_vocab);

    	cb_deflang = (CheckBox)findViewById(R.id.euv_cb1);
    	cb_deflang.setVisibility(View.GONE);
    	cb_noquery = (CheckBox)findViewById(R.id.euv_cb2);
    	cb_noquery.setVisibility(View.GONE);
        btn_sellang =(Button)findViewById(R.id.euv_sellang);
        btn_search =(Button)findViewById(R.id.euv_search);
        btn_search.setVisibility(View.GONE);
        ((TextView)findViewById(R.id.euv_txtlang)).setText(getString(R.string.euv_lang_text)+st.STR_COLON);
        tv_cnt_word =(TextView)findViewById(R.id.euv_allword);
        tv_cnt_word.setVisibility(View.GONE);

		TextView tv1 = new TextView(this);
		tv1.setPadding(PADDING_LEFT, 0, PADDING_RIGHT, 0);
		tv1.setTextSize(18);
		tv1.setText("90000000");
		tv1.measure(0, 0);
		freq_w = tv1.getMeasuredWidthAndState()+PADDING_LEFT+PADDING_RIGHT;

        rlcap =(RelativeLayout)findViewById(R.id.euv_rlcap);
        rlcap.setVisibility(View.GONE);
        userword = new UserWords();
        boolean bopen = userword.open(st.getSettingsPath()+WordsService.DEF_PATH
        		+UserWords.FILENAME);
        if (!bopen){
        	st.toast(R.string.empty);
        	finish();
        }
        lang = userword.getTables();
        setCountWord(0);
        lv =(ListView)findViewById(R.id.euv_list);

        fl_changed = false;

		// выводим окно выбранного словаря
		deflang = st.pref(inst).getString(st.PREF_EUV_LANG_DEF, st.STR_NULL);
        if (deflang.length() > 0){
        	curlang = deflang;
        	cb_deflang.setChecked(true);
            btn_sellang.setText(getLangName(curlang));
            createWordLayout();
            initList();
        }

       	Ads.count_failed_load = 0;
        Ads.show(this, 8);
        
	}
	public void setCountWord(int cnt) {
		if (tv_cnt_word==null)
			return;
		tv_cnt_word.setText(getText(R.string.euv_all_word_text)+st.STR_SPACE+cnt);
	}
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
    	inst=null;
        super.onDestroy();
    }

    public void onClickXml(View view) 
    {
    	if (GlobDialog.gbshow)
    		return;
        switch (view.getId())
        {
        case R.id.euv_search:
            final GlobDialog gd = new GlobDialog(st.c());
            gd.set(R.string.search, R.string.ok, R.string.cancel);
            gd.setObserver(new st.UniObserver()
              {
                  @Override
                  public int OnObserver(Object param1, Object param2)
                  {
                      if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
                      {
                    	  search_txt = gd.ret_edittext_text.trim().toLowerCase();
                    	  if (search_txt.isEmpty()) {
                    		  createWordLayout();
                    	  } else {
                    		  userword.getAllWord(curlang);
                    		  m_adapter.getFilter().filter(search_txt);
                    	  }
                      }
                  	//userword.stopAddWord(false);
                      return 0;
                  }
              });
           	gd.showEdit(search_txt,0);
        	return;
        case R.id.euv_cb2:
        	if (cb_noquery!=null){
        		
        	}
        	return;
        case R.id.euv_cb1:
        	if (cb_deflang!=null){
        		if (cb_deflang.isChecked())
                	st.pref(inst).edit().putString(st.PREF_EUV_LANG_DEF, curlang).commit();
        		else
                	st.pref(inst).edit().putString(st.PREF_EUV_LANG_DEF, st.STR_NULL).commit();
        	}
        	return;
        case R.id.euv_sellang:
        	// не забыть сделать запрос на сохранение 
        	// если спаисок не пуст и изменён
        	int pos = 0;
        	for (int p=0;p<lang.size();p++){
        		if (lang.get(p).length()<=3){
        			pos++;
        		}
        	}
        	final String[] ars = new String[pos];
//        	String[] ars = new String[arskinname.size()-1];
        	pos = 0;
        	String lng_in = st.STR_NULL;
        	String lng_out = st.STR_NULL;
        	for (int i=0;i<lang.size();i++){
        		lng_in = lang.get(i);
        		lng_out = getLangName(lng_in);
        		if (lng_out!=null&&lng_in.length()<=3){
        			ars[pos]= lng_out;
        			pos++;
        		}
        	}
        	if (ars.length == 0){
        		st.toast(R.string.empty);
            	if (rlcap!=null)
                    rlcap.setVisibility(View.GONE);
            	if (cb_noquery!=null)
            		cb_noquery.setVisibility(View.GONE);
            	if (cb_deflang!=null)
            		cb_deflang.setVisibility(View.GONE);
            	if (btn_search!=null)
            		btn_search.setVisibility(View.GONE);
        		return;
        	}
           	ArrayAdapter<String> ar = new ArrayAdapter<String>(this, 
           			R.layout.tpl_instr_list,
                    ars
                    );
            
            Dlg.customMenu(this, ar, 
            		ServiceJbKbd.inst.getString(R.string.selection), 
            		new st.UniObserver()
            {
                @Override
                public int OnObserver(Object param1, Object param2)
                {
                    int pos = (((Integer)param1).intValue());
                    
                    curlang = ars[pos].substring(0, ars[pos].indexOf("-")).trim();
                    if (deflang.compareToIgnoreCase(curlang)!=0){
                    	cb_deflang.setChecked(false);
                    } else
                    	cb_deflang.setChecked(true);
                    btn_sellang.setText(ars[pos]);
                    createWordLayout();
                    initList();
                    return 0;
                }
            });
            return;
        case R.id.euv_help:
        	Dlg.helpDialog(inst, inst.getString(R.string.euv_help));
        	//st.help(R.string.euv_help);
        	return;
        }
    }
    public static String getLangName(String lng)
    {
    	String out = st.STR_NULL;
    	out = st.upFirstSymbol(new Locale(lng).getDisplayName());
    	
    	if (out.compareToIgnoreCase(EXCLUDE_SQL_PREFIX)==0)
    		return null;
    	return lng+" - "+out;
    }
    public void createWordLayout()
    {
//    	if (ll==null)
//    		return;
    	if (curlang==null)
    		return;
    	cb_deflang.setVisibility(View.VISIBLE);
    	if (cb_noquery!=null)
    		cb_noquery.setVisibility(View.VISIBLE);
    	if (cb_deflang!=null)
    		cb_deflang.setVisibility(View.VISIBLE);

       	lv.removeAllViewsInLayout();
    	if (!userword.getAllWord(curlang)){
    		setCountWord(0);
    		if (tv_cnt_word!=null)
    			tv_cnt_word.setVisibility(View.GONE);
        	if (rlcap!=null)
                rlcap.setVisibility(View.GONE);
        	if (btn_search!=null)
        		btn_search.setVisibility(View.GONE);
    		return;
    	} else {
    		if (tv_cnt_word!=null)
    			tv_cnt_word.setVisibility(View.VISIBLE);
    		setCountWord(userword.arword.size());
    	}
        fl_changed = false;
    }
    public RelativeLayout createrl(int i)
    {
    	if (rlcap!=null)
            rlcap.setVisibility(View.VISIBLE);
    	if (btn_search!=null)
    		btn_search.setVisibility(View.VISIBLE);
    	RelativeLayout rl = new RelativeLayout(this);
//    	rl.setId(RL_FACTOR+i);
		rl.addView(createtv(i,false));
		rl.addView(createtv(i,true));
    	rl.setPadding(5, 0, 5, 2);
    	return rl;
    }
    /** 		
     * fl_item = 
     * true - частота
	 * false - слово

     * @param i
     * @param fl_item
     * @return
     */
    @SuppressLint("NewApi")
	public TextView createtv(int i, boolean fl_item)
    {
    	WordArray wa = null;
		wa = userword.arword.get(i);
		
		TextView tv = new TextView(this);
		tv.setPadding(PADDING_LEFT, 0, PADDING_RIGHT, 0);
		tv.setTextSize(18);
		tv.setOnClickListener(m_clkListener);
		tv.setTextColor(Color.WHITE);
		// true - частота
		// false - слово
		if (!fl_item){
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT
					);
	        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	        lp.addRule(RelativeLayout.LEFT_OF, i*FACTOR+1);
			tv.setLayoutParams(lp);
			tv.setOnLongClickListener(m_longClickListener);
			tv.setBackgroundColor(Color.DKGRAY);
			tv.setId(i*FACTOR);
			wa.id=i*FACTOR;
			tv.setGravity(Gravity.LEFT);
			tv.setText(wa.namenew);
		} else {
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					freq_w,
					RelativeLayout.LayoutParams.WRAP_CONTENT
					);
	        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			tv.setLayoutParams(lp);
			tv.setBackgroundColor(Color.BLUE);
			tv.setText(st.STR_NULL+wa.freqnew);
			tv.setId(i*FACTOR+1);
			tv.setGravity(Gravity.RIGHT);
			
		}
			
    	return tv;
    }
    View.OnClickListener m_clkListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
        	if (GlobDialog.gbshow){
        		return;
        	}
        	final TextView tv = (TextView)v;
        	int id = tv.getId();
        	final boolean even = st.isEven(id);
        	if (!even)
        		id--;
//        	TextView tv1 = (TextView) ll.findViewById(id);
//        	if (tv1==null)
//        		return;
//        	int col = tv1.getCurrentTextColor();
//        	if (col == TEXTCOLOR){
//        		st.toast(R.string.ac_del_word_ok);
//        		return;
//        	}
            final GlobDialog gd = new GlobDialog(st.c());
            gd.set(even?R.string.ac_color_word:R.string.euv_freq, R.string.ok, R.string.cancel);
            gd.setObserver(new st.UniObserver()
              {
                  @Override
                  public int OnObserver(Object param1, Object param2)
                  {
                      if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
                      {
//                		  tv.setText(gd.ret_edittext_text.trim());
                		  setWordAndFreq(tv,gd.ret_edittext_text.trim().toLowerCase());
                      }
                      return 0;
                  }
              });
            if (even)
            	gd.showEdit(tv.getText().toString().trim(),0);
            else
            	gd.showEdit(tv.getText().toString().trim(),1);

        }
    };
    View.OnLongClickListener m_longClickListener = new View.OnLongClickListener() 
    {
        @Override
        public boolean onLongClick(View v)
        {
        	if (GlobDialog.gbshow){
        		return true;
        	}
        	((TextView)v).setTextColor(TEXT_COLOR_EDIT);
        	WordArray wa = null;
        	if (st.isEven(v.getId()))
    			wa = st.getWordArrayElementById(v.getId(), userword.arword);
        	else
    			wa = st.getWordArrayElementById(v.getId()-1, userword.arword);
        	
        	// не спрашивать
        	boolean fl = true;
        	if (cb_noquery!=null&&cb_noquery.isChecked()){
        		fl = false;
        	}
        	TextView tt = (TextView)v;
        	userword.deleteUserWord(((TextView)v).getText().toString(), curlang,false,(TextView)v,new int[] {TEXT_COLOR_BASE,TEXT_COLOR_EDIT,TEXT_COLOR_DEL}, fl,wa);
        	return true;
        }
    };
    public void setWordAndFreq(TextView tv, String str){
    	String out = st.STR_NULL;
		WordArray wa = getWordArray(tv.getId());
		if (wa==null)
			return;
    	if (st.isEven(tv.getId())){
    		if (str.isEmpty())
    			str=wa.nameold;
    		wa.namenew = str;
    		out=str;
    		if (wa.nameold.compareTo(wa.namenew)!=0)
    			fl_changed = true;
    	} else {
    		try {
    			if (str.isEmpty())
    				str = st.STR_NULL+wa.freqold;
    			wa.freqnew = (long)st.parseInt(str, 10);
    		}catch (NumberFormatException e) {
    			wa.freqnew = wa.freqold;
        	}
    		if (wa.freqnew<0)
    			wa.freqnew = 0;
    		if (wa.freqnew>userword.WORD_FREQ_LIMIT)
    			wa.freqnew = userword.WORD_FREQ_LIMIT;
    		out =st.STR_NULL+wa.freqnew;
    		if (wa.freqold!=wa.freqnew)
    			fl_changed = true;
    	}
		tv.setText(out);
    }
    public WordArray getWordArray(int id){
    	for (WordArray wa:userword.arword){
    		if (wa.id==id||wa.id+1==id)
    			return wa;
    	}
    	return null;
    }
    @Override
    public void onBackPressed() 
    {
    	if (GlobDialog.gbshow){
    		GlobDialog.inst.finish();
    		return;
    	}
    	if (fl_changed) {
    		saveProgress(true);
    	} else
    		super.onBackPressed();
    }
    void save(String lang, WordArray wa)
    {
    	boolean flag = false;
    	if (wa.namenew!=null&&wa.namenew.isEmpty())
    		return;
    	
    	if (wa.nameold.compareToIgnoreCase(wa.namenew)!=0)
    		flag=true;
    	else if (wa.freqold!=wa.freqnew)
    		flag=true;
    	if (flag)
    		userword.updateWord(lang,wa);
    }
    void saveProgress(final boolean bclose)
    {
        GlobDialog gd = new GlobDialog(st.c());
        gd.set(R.string.data_changed, R.string.yes, R.string.no);
        gd.setObserver(new st.UniObserver()
        {
            @Override
            public int OnObserver(Object param1, Object param2)
            {
                if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
                {
                    final st.UniObserver cb= new st.UniObserver()
                    {
                        @Override
                        public int OnObserver(Object param1, Object param2)
                        {
                            return 0;
                        }
                    };

                	ProgressOperation po = new ProgressOperation(cb,inst)
                    {
                        String m_fn = st.STR_NULL;
                        @Override
                        public void makeOper(UniObserver obs)
                        {
                        	int pos = 0;
                            try{
                                m_total = userword.arword.size();
                                for (WordArray wa:userword.arword){
                                	save(curlang,wa);
                                	m_position+=pos;
                                	pos++;
                                }
                                obs.m_param1 = curlang;
                                finish();
                                fl_changed = false;
                                if (bclose)
                                	close();
                            }
                            catch (Throwable e) {
                            	// toast не пашет!!!
                                if (bclose)
                                	close();
                            }
                        }
                        
                        @Override
                        public void onProgress()
                        {
                            if(m_total>0)
                            {
                                m_progress.setProgress(getPercent());
                            }
                            m_progress.setMessage(inst.getText(R.string.euv_save));
                        }
                    };
                    po.m_progress.setTitle(getString(R.string.euv_actname));
                    po.m_progress.setMessage("begin");
                    po.start();
                }	
                if(((Integer)param1).intValue()==AlertDialog.BUTTON_NEUTRAL)
                	finish(); 
              	return 0;
                }
        });
        gd.showAlert();
   }
    public void close()
    {
    	finish();
    }
    public void setTextViewColor(TextView tv, boolean col) {
    	if (!col)
    		tv.setTextColor(Color.WHITE);
    	else
    		tv.setTextColor(TEXT_COLOR_DEL);
    }
//    public void search(String search){
//    	search=search.trim().toLowerCase();
//    	if (search.isEmpty()) {
//    		initList();
//    		return;
//    	}
//    	RelativeLayout rl = null;
//    	TextView tv = null;
//    	String ttv = null;
//    	WordArray wa = null;
//    	int pos = 0;
//    	int size = lv.getChildCount();
//    	for (int i=0;i<lv.getChildCount();i++) {
//    		rl = (RelativeLayout) lv.getChildAt(pos);
//    		if (rl!=null){
//    			tv = (TextView) rl.getChildAt(0);
//    			if (tv==null)
//    				continue;
//    			wa = userword.arword.get(i);
//    			if (!wa.namenew.startsWith(search)) {
//    				userword.arword.remove(wa);
//    			}
//    		}
//    	}
//    	//initList();
//        m_adapter.notifyDataSetChanged();
//    }
    public void initList(){
        m_adapter = new EuvAdapt(inst,userword.arword);
        lv.setAdapter(m_adapter);
   }
    static class EuvAdapt extends ArrayAdapter<WordArray> implements Filterable
    {
    	private ArrayList<WordArray> filteredModelItemsArray;
    	private Activity context;
    	private ModelFilter filter;
    	private LayoutInflater inflater;
    	
    	ArrayList<WordArray> mwa = null;
        public EuvAdapt(Context context, ArrayList<WordArray> arword)
        {
            super(context,0);
            mwa = arword;

//            this.mwa = new ArrayList<WordArray>();
//            mwa.addAll(arword);
            this.filteredModelItemsArray = new ArrayList<WordArray>();
            filteredModelItemsArray.addAll(mwa);
            //inflater = context.getLayoutInflater();
            getFilter();        
        }
        @Override
        public Filter getFilter() {
            if (filter == null){
                filter  = new ModelFilter();
            }
            return filter;
        }
        @Override
        public int getCount() 
        {
//        	if (inst == null||inst.userword==null||inst.userword.arword==null)
//        		return 0;
            return mwa.size();
        };
		@Override
        public View getView(int pos, View convertView, ViewGroup parent)
        {
           	WordArray wa = mwa.get(pos);
            if(convertView!=null)
            {
            	convertView = getCurView(convertView, pos, wa);
            }
            else
            {
                convertView = inst.createrl(pos);
            }
//            arcounter++;
//            if (posY > 0&&arcounter >= getCount()) {
//            	if (lv == null)
//            		lv = (ListView)inst.findViewById(R.id.euv_list);
//                lv.smoothScrollToPosition(posY);
//                posY = 0;
//            }
            return convertView;
        }
		public View getCurView(View v, int pos, WordArray wa) {
        	RelativeLayout rl = (RelativeLayout)v;
        	if (wa.id == -1)
        		wa.id = pos*FACTOR;

			RelativeLayout.LayoutParams lpr = new RelativeLayout.LayoutParams(
					inst.freq_w, RelativeLayout.LayoutParams.WRAP_CONTENT
					);
	        lpr.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	        
			RelativeLayout.LayoutParams lpl = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT
					);
	        lpl.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	        lpl.addRule(RelativeLayout.LEFT_OF, wa.id+1);

	        TextView tv = (TextView)rl.getChildAt(1);
	        tv.setLayoutParams(lpr);
            tv.setTag(wa);
            tv.setId(wa.id+1);
            tv.setText(st.STR_NULL+wa.freqnew);
            tv.setTransformationMethod(null);
            
            tv = (TextView)rl.getChildAt(0);
            tv.setLayoutParams(lpl);
            tv.setTag(wa);
            tv.setId(wa.id);
            tv.setText(wa.namenew);
            tv.setTransformationMethod(null);
            inst.setTextViewColor(tv, wa.del);

			return rl;
		}
    
   private class ModelFilter extends Filter
    {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if(constraint.toString().length() > 0)
            {
                ArrayList<WordArray> filteredItems = new ArrayList<WordArray>();
                WordArray wa = null;
                String m = null;
                for(int i = 0; i< mwa.size(); i++)
                {
                	wa = mwa.get(i);
                    m = mwa.get(i).namenew;
                    if(m.toLowerCase().startsWith(constraint.toString()))
                        filteredItems.add(wa);
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            }
            else
            {
                synchronized(this)
                {
                    result.values = mwa;
                    result.count = mwa.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            filteredModelItemsArray = (ArrayList<WordArray>)results.values;
            notifyDataSetChanged();
            mwa.clear();
            int siz = filteredModelItemsArray.size();
            WordArray wa = null;
            for(int i = 0; i< siz; i++) {
                wa = filteredModelItemsArray.get(i);
                mwa.add(wa);
            }
      	    inst.setCountWord(mwa.size());
            notifyDataSetInvalidated();
        }
      }
    }

}