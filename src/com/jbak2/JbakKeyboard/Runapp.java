package com.jbak2.JbakKeyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.jbak2.JbakKeyboard.st;
import com.jbak2.ctrl.GlobDialog;
import com.jbak2.ctrl.th;
public class Runapp extends Activity
{
	boolean bsave = false;
//	  boolean bsave2 = false;
	LinearLayout llview_ll_favorite;
	LinearLayout llview_ll_all;
	ProgressBar load_progress = null;
	int wrapContent = LinearLayout.LayoutParams.WRAP_CONTENT;
	static Runapp inst = null;
	int text_color = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		setTheme(th.theme_interface);
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.run_app);
        inst = this;
		llview_ll_favorite = (LinearLayout) findViewById(R.id.runapp_ll_favorite);
		llview_ll_all = (LinearLayout) findViewById(R.id.runapp_ll_all);
        load_progress = (ProgressBar) findViewById(R.id.runapp_load_progress);
        load_progress.setVisibility(View.GONE);

        if (th.isDarkThemeApp())
        	text_color = Color.CYAN;
        else
        	text_color = Color.BLUE;
	    st.hidekbd();
	    createView();
        // показ рекламы
        Ads.show(this, 7);
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
        super.onDestroy();
    }
	public void createView() 
	{
		if (llview_ll_favorite== null)
			return;
		if (llview_ll_all== null)
			return;
		llview_ll_favorite.removeAllViews();
// избранное
        int pos = 0;
    	for (int i =0;i<st.runapp_favorite.size();i++){
    		if (st.runapp_favorite.get(i).compareToIgnoreCase(st.STR_NULL) == 0){
    			st.runapp_favorite.remove(i);
    		}
    	}
    	for (int i =0;i<st.runapp_favorite.size();i++){
            Spannable ftext = null;
            ftext = new SpannableString(st.runapp_favorite.get(i));
            ftext.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, st.runapp_favorite.get(i).indexOf(st.STR_LF), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ftext.setSpan(new  ForegroundColorSpan(Color.GREEN), st.runapp_favorite.get(i).indexOf(st.STR_LF)+1,st.runapp_favorite.get(i).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            RelativeLayout rl = new RelativeLayout(this);
// добавляем строку команды
// под команды зарезервировано 300 кодов            
            // создаём текст команды
    		if (i==0){
    			// первая строка
    			if (st.runapp_favorite.size()!=1){
    				rl.addView(createBtn(i+300,"∇", false, true,-1));
            		rl.addView(createBtn(i+900,"✖", false,false,i+300));
    			} else {
            		rl.addView(createBtn(i+900,"✖", false,true,i+300));
    				
    			}
        		// текст
        		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                		RelativeLayout.LayoutParams.WRAP_CONTENT,
                		RelativeLayout.LayoutParams.WRAP_CONTENT)
                		;
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                lp.addRule(RelativeLayout.LEFT_OF, i+900);
        		
                TextView tv = new TextView(this);
        		tv.setText(ftext);
        		tv.setTextSize(17);
        		tv.setOnClickListener(run_clkListener);
        		tv.setGravity(Gravity.LEFT|Gravity.CENTER_HORIZONTAL);
        		tv.setLayoutParams(lp);
        		rl.addView(tv);
    		}
    		else if (i>0&&pos > 0&&pos < st.runapp_favorite.size()-1){
    			// следующие строки
    			rl.addView(createBtn(i+300,"∇", false, true,-1));
    			// перед ней
    			rl.addView(createBtn(i+600,"∆", false, false,i+300));
    			// удалить
        		rl.addView(createBtn(i+900,"✖", false,false,i+600));
        		// текст
        		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                		RelativeLayout.LayoutParams.WRAP_CONTENT,
                		RelativeLayout.LayoutParams.WRAP_CONTENT)
                		;
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                lp.addRule(RelativeLayout.LEFT_OF, i+900);
        		
                TextView tv = new TextView(this);
        		tv.setText(ftext);
        		tv.setLayoutParams(lp);
        		tv.setTextSize(17);
        		tv.setGravity(Gravity.LEFT|Gravity.CENTER_HORIZONTAL);
        		tv.setOnClickListener(run_clkListener);
        		rl.addView(tv);
    		}
    		else if (i>0&&pos == st.runapp_favorite.size()-1){
    			// последняя строка
    			rl.addView(createBtn(i+600,"∆", false, true,-1));
    			// удалить
        		rl.addView(createBtn(i+900,"✖", false,false,i+600));
        		// текст
        		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                		RelativeLayout.LayoutParams.WRAP_CONTENT,
                		RelativeLayout.LayoutParams.WRAP_CONTENT)
                		;
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                lp.addRule(RelativeLayout.LEFT_OF, i+900);
        		
                TextView tv = new TextView(this);
        		tv.setText(ftext);
        		tv.setTextSize(17);
        		tv.setGravity(Gravity.LEFT|Gravity.CENTER_HORIZONTAL);
        		tv.setLayoutParams(lp);
        		tv.setOnClickListener(run_clkListener);
        		rl.addView(tv);
    		}

        	if (rl.getChildCount()> 0)
        		llview_ll_favorite.addView(rl);
    		pos++;
    	}
    	
// все приложения
	    for (int i=0; i< st.runapp_all.length;i++){
	    	if (st.runapp_all[i].length() > 0) {
	    		createAll(st.runapp_all[i],1500+i,2);
	    	}
	    }
	  }
    public Button createBtn(int id, String str, 
    		boolean left, boolean right, int id_leftof
    		) 
    {
    	Button btn = new Button(this);
    	btn.setText(str);
    	btn.setOnClickListener(m_clkListener);
    	RelativeLayout.LayoutParams lp = null;
    	if (right) {
            lp = new RelativeLayout.LayoutParams(
            		RelativeLayout.LayoutParams.WRAP_CONTENT,
            		RelativeLayout.LayoutParams.WRAP_CONTENT)
            		;
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    	}
    	else if (left){
            lp = new RelativeLayout.LayoutParams(
            		RelativeLayout.LayoutParams.WRAP_CONTENT,
            		RelativeLayout.LayoutParams.WRAP_CONTENT)
            		;
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp.addRule(RelativeLayout.LEFT_OF, id_leftof);
    	}
    	else if (!right&&!left){
            lp = new RelativeLayout.LayoutParams(
            		RelativeLayout.LayoutParams.WRAP_CONTENT,
            		RelativeLayout.LayoutParams.WRAP_CONTENT)
            		;
            lp.addRule(RelativeLayout.LEFT_OF, id_leftof);
    	}
    		
    		
    	btn.setLayoutParams(lp);
    	btn.setId(id);
    	return btn;
    }
//    public void ll_remove() 
//	{
//		llview_ll_favorite = (LinearLayout) findViewById(R.id.runapp_ll_favorite);
//		llview_ll_all = (LinearLayout) findViewById(R.id.runapp_ll_all);
//		llview_ll_favorite.removeAllViews();
//		llview_ll_all.removeAllViews();
//	}
	public void onClickSave(View view) 
	{
		save();
	    st.toast(getString(R.string.settings_saved));
			
		}
	public void onClickRescan(View view) 
	{
    	if (load_progress!=null)
  			load_progress.setVisibility(View.VISIBLE);
    	if (llview_ll_all!=null)
    		llview_ll_all.setVisibility(View.GONE);
	    new Handler().postDelayed(new Runnable() {
	    	public void run() {
	    		llview_ll_all = (LinearLayout) findViewById(R.id.runapp_ll_all);
	    		llview_ll_favorite.removeAllViews();
	    		llview_ll_all.removeAllViews();
	    		for (int i=0;i<st.runapp_all.length;i++){
	    			st.runapp_all[i]=st.STR_NULL;
	    		}
	    	    PackageManager pm = inst.getPackageManager();
	    		List<PackageInfo> pm1 = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES);
	    	    for (int i=0; i<pm1.size();i++) {
	    	    	String label = pm1.get(i).applicationInfo.packageName;
	        		CharSequence  name = pm1.get(i).applicationInfo.loadLabel(pm);
	        		st.runapp_all[i] = name+st.STR_LF+label;
	    	    }
	    	    Arrays.sort(st.runapp_all);
//	    	    rotate();
	    	    createView();
	        	if (load_progress!=null)
	      			load_progress.setVisibility(View.GONE);
	    		llview_ll_all.setVisibility(View.VISIBLE);
	        }
	    }, 1000);
    	
	}
	public void onBackPressed()
	{

//		if (bsave2 == false&&bsave == true){
		if (bsave == true){
            GlobDialog gd = new GlobDialog(st.c());
            gd.set(R.string.data_changed, R.string.yes, R.string.no);
            gd.setObserver(new st.UniObserver()
            {
                @Override
                public int OnObserver(Object param1, Object param2)
                {
                    if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
                    {
                    	save();
                   		finish();
                    	st.showkbd();
                    }
                    if(((Integer)param1).intValue()==AlertDialog.BUTTON_NEUTRAL)
                    {
            			st.prefReload();
            		    bsave = false;
            			finish();
                    	st.showkbd();
                    }
                    return 0;
                }
            });
            gd.showAlert();
		}
	    super.onBackPressed();
	    if (bsave==false)
	    	st.showkbd();
	}
	public void save()
	{
//		удаляем старые значения из настроек
		for (int i=0; i<300;i++){
			st.pref().edit().remove(st.PREF_KEY_RUNAPP+String.valueOf(i));
		}
		ArrayList<String> ar = new ArrayList<String>();
		for (int i=0;i<st.runapp_favorite.size();i++){
			ar.add(st.runapp_favorite.get(i));
		}
		int cnt=st.runapp_favorite.size();
		st.pref().edit().putInt(st.PREF_KEY_RUNAPP_COUNT, -5).commit();
// записываем новые		
		for (int i=0; i<ar.size();i++){
		    st.pref().edit().putString(st.PREF_KEY_RUNAPP+String.valueOf(i), ar.get(i)).commit();
		}
		st.pref().edit().putInt(st.PREF_KEY_RUNAPP_COUNT, cnt).commit();
	    bsave = false;
	}
	View.OnClickListener run_clkListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
        	try {
    			String run = ((TextView)v).getText().toString();
      			run = run.substring(run.indexOf(st.STR_LF)+1);
      			Intent intent1 = getPackageManager().getLaunchIntentForPackage(run);
      			finish();
      			startActivity(intent1);
			} catch (Throwable e) {
				st.toast(inst, R.string.runapp_error_start);
			}
        }
    };
	View.OnClickListener m_clkListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
        	if (st.runapp_favorite.size()==0)
        		return;
        	ArrayList<String> ar = new ArrayList<String>();
        	int id = v.getId();
        	// вниз
        	if (id>=300&&id<600){
        		if (st.runapp_favorite.size()==1)
        			return;
        		for (int i=0;i<st.runapp_favorite.size();i++){
        			if (id != i+300)
        				ar.add(st.runapp_favorite.get(i));
        			else {
        				ar.add(st.runapp_favorite.get(i+1));
        				ar.add(st.runapp_favorite.get(i));
        				st.runapp_favorite.remove(i+1);
        			}
        		}
        	}
        	// вверх
        	else if (id>=600&&id<900){
        		boolean fl = false;
        		for (int i=0;i<st.runapp_favorite.size();i++){
        			if (id != i+600)
        				ar.add(st.runapp_favorite.get(i));
        			else {
        				if (fl==false){
            				String rec = st.runapp_favorite.get(i);
            				if (ar.size()>0)
            					ar.remove(ar.size()-1);
            				ar.add(rec);
            				ar.add(st.runapp_favorite.get(i-1));
            				fl=true;
        				}
        			}
        		}
        	}
        	// удалить
        	else if (id>=900&&id<1200){
        		for (int i=0;i<st.runapp_favorite.size();i++){
        			if (id-900 == i)
        				continue;
        			ar.add(st.runapp_favorite.get(i));
        		}
        	    bsave = true;

        	}
        	st.runapp_favorite.clear();
        	for (int i=0;i<ar.size();i++){
        		st.runapp_favorite.add(ar.get(i));
        	}
        	createView();
  			st.toast(getString(R.string.runapp_favorite_cnt)+st.STR_SPACE+String.valueOf(st.runapp_favorite.size()));

        }
    };
	  public void createAll(String text, int id, int rejim) 
	  {
		  llview_ll_all = (LinearLayout) findViewById(R.id.runapp_ll_all);
	      @SuppressWarnings("deprecation")
	      LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(
	    		  LayoutParams.MATCH_PARENT , wrapContent);
	      lParams.gravity = Gravity.LEFT|Gravity.TOP;

	      Spannable stext = new SpannableString(text);
    	  stext.setSpan(new ForegroundColorSpan(text_color), 0, text.indexOf(st.STR_LF), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    	  stext.setSpan(new  ForegroundColorSpan(Color.GREEN), text.indexOf(st.STR_LF)+1,text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

	      TextView tv = new TextView(this);
	      tv.setId(id);
//	      tv.setTextColor(Color.GREEN);
	      tv.setText(stext);
	      tv.setContentDescription(text);
	      tv.setOnClickListener(run_clkListener);
	    	tv.setOnLongClickListener(new View.OnLongClickListener(){
	    	      @Override
	    	      public boolean onLongClick(View v) 
	    	      {
	    	      	final int id = v.getId();
	    	      	if (id < 1500) {
	    	              GlobDialog gd = new GlobDialog(st.c());
	    	              gd.set(R.string.delete, R.string.yes, R.string.no);
	    	              gd.setObserver(new st.UniObserver()
	    	              {
	    	                  @Override
	    	                  public int OnObserver(Object param1, Object param2)
	    	                  {
	    	                      if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
	    	                      {
	    	                  		st.runapp_favorite.remove(id);
	    	                  		createView();
	    	                  	    bsave=true;
	    	                      }
	    	                      return 0;
	    	                  }
	    	              });
	    	              gd.showAlert();

	    	      	}
	    	      	else if (id >= 1500) {
	    	      		boolean flag = false;
// в избранное можно добавлять до 300 приложений
	    	      		if (st.runapp_favorite.size()>299)
	    	      			return true;
	    	      		for (int i=0; i<st.runapp_favorite.size();i++){
	    	      			if (i == 0&&flag == false){
	    	      				st.runapp_favorite.add(((TextView)v).getText().toString());
	    	      				flag = true;
	    	      			}
	    	      		}
	    	      		if (st.runapp_favorite.size()==0){
	    	  				st.runapp_favorite.add(((TextView)v).getText().toString());
	    	  				flag = true;
	    	      		}
	    	      		if (flag){
	    	      			bsave = true;
	    	      			st.toast(getString(R.string.add)+" ("+String.valueOf(st.runapp_favorite.size())+")");
	    	      		}
	    	      	}
	    	      	createView();
	    	        return true;
	    	    }
	    	 }
	      );
// разделяющая линия	    	
	      TextView tv_str = new TextView(this);
	      tv_str.setGravity(Gravity.CENTER_HORIZONTAL);
	        if (th.isDarkThemeApp())
	  	      tv_str.setBackgroundColor(Color.WHITE);
	        else
	  	      tv_str.setBackgroundColor(Color.BLACK);
	      tv_str.setLayoutParams(new LinearLayout.LayoutParams(
  				LayoutParams.MATCH_PARENT, 2));
//		tv_str.setPadding(30, 10, 30, 10);
//	      tv_str.setText("---");
	      if (rejim == 1){
		  	  llview_ll_favorite.addView(tv, lParams);
		  	  llview_ll_favorite.addView(tv_str);
	      } 
	      else if (rejim == 2){
		  	  llview_ll_all.addView(tv, lParams);
		  	  llview_ll_all.addView(tv_str);
	      }
	}

}