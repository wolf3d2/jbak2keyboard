package com.jbak2.JbakKeyboard;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.jbak2.ctrl.Font;
import com.jbak2.ctrl.IntEditor;
import com.jbak2.ctrl.IntEditor.OnChangeValue;

public class EditSetFontActivity extends Activity
{
    public static EditSetFontActivity inst;
    public static final String EXTRA_PREF_KEY = "pref_key";
    public static final String EXTRA_DEFAULT_EDIT_SET = "def_edit_set";
    EditText m_edit;
    EditSet m_es = new EditSet();
    String m_prefKey;
    String m_defaultEditSet;
    float m_defaultFontSize;
    OnCheckedChangeListener m_onCheckChange = new OnCheckedChangeListener()
    {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
        {
            switch (buttonView.getId())
            {
                case R.id.es_font_bold:
                    if(isChecked)
                        m_es.style|=Typeface.BOLD;
                    else
                        m_es.style = st.rem(m_es.style, Typeface.BOLD);
                break;
                
                case R.id.es_font_italic:
                    if(isChecked)
                        m_es.style|=Typeface.ITALIC;
                    else
                        m_es.style = st.rem(m_es.style, Typeface.ITALIC);
                break;
            }
            m_es.setToEditor(m_edit);
        }
    };
    OnItemSelectedListener m_OnSpinnerChange = new OnItemSelectedListener()
    {

        @Override
        public void onItemSelected(AdapterView<?> view, View selView, int pos, long id)
        {
            switch(view.getId())
            {
                case R.id.es_fonts:
                {
                    m_es.typeface = EditSet.intToTypeface(pos);
                }
                break;
            }
            m_es.setToEditor(m_edit);
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0)
        {
        }
        
    };
    @Override
    public void onCreate(android.os.Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        inst = this;
        m_prefKey = getIntent().getStringExtra(EXTRA_PREF_KEY);
        if(m_prefKey==null)
            m_prefKey = st.PREF_KEY_EDIT_SETTINGS;
        View v = getLayoutInflater().inflate(R.layout.edit_settings, null);
        m_defaultEditSet = getIntent().getStringExtra(EXTRA_DEFAULT_EDIT_SET);
        if(!m_es.load(m_prefKey)&&m_defaultEditSet!=null)
            m_es.fromString(m_defaultEditSet);
        m_edit = (EditText) v.findViewById(R.id.es_edit);
        m_defaultFontSize = m_edit.getTextSize();
        m_es.setToEditor(m_edit);
        CheckBox cb = (CheckBox)v.findViewById(R.id.es_font_bold);
        cb.setOnCheckedChangeListener(m_onCheckChange);
        cb.setChecked(st.has(m_es.style,Typeface.BOLD));
        cb = (CheckBox)v.findViewById(R.id.es_font_italic);
        cb.setOnCheckedChangeListener(m_onCheckChange);
        cb.setChecked(st.has(m_es.style,Typeface.ITALIC));
        Spinner s = (Spinner)v.findViewById(R.id.es_fonts); 
        s.setOnItemSelectedListener(m_OnSpinnerChange);
        s.setSelection(EditSet.typefaceToInt(m_es.typeface));
        IntEditor fs=(IntEditor)v.findViewById(R.id.es_font_size);
        if(m_es.fontSize==0)
            fs.setValue((int)m_defaultFontSize);
        else
            fs.setValue(m_es.fontSize);
        fs.setOnChangeValue(new OnChangeValue()
        {
            @Override
            public void onChangeIntValue(IntEditor edit)
            {
                m_es.fontSize = edit.getValue();
                m_es.setToEditor(m_edit);
                
            }
        });
        setContentView(v);
    };
    @Override
    protected void onDestroy() 
    {
        String s = m_es.toString();
        SharedPreferences p = st.pref();
        m_es.save(p,m_prefKey);
        inst = null;
        JbKbdView.inst = null;
        super.onDestroy();
    };
    public static class EditSet
    {
        public Typeface typeface = Typeface.DEFAULT;
        public int style = 0;
        public int fontSize = 0;
        public EditSet()
        {}
        final boolean isDefault()
        {
            return typeface==Typeface.DEFAULT&&fontSize==0&&style==0;
        }
        static final int typefaceToInt(Typeface tf)
        {
            if(tf==Typeface.SERIF)
                return 1;
            if(tf==Typeface.MONOSPACE)
                return 2;
            return 0;
        }
     // какой шрифт использовать
        static final Typeface intToTypeface(int tf)
        {
            if(tf==1)
                return Typeface.SERIF;
            if(tf==2)
                return Typeface.MONOSPACE;
            return Typeface.DEFAULT;
        }
        void setToEditor(TextView tv)
        {
            float df = tv.getTextSize();
            tv.setTypeface(typeface, style);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,fontSize>0?fontSize:df);
        }
        TextPaint getTextPaint()
        {
            return getTextPaint(false);
        }
        /** @param bKeyboardFont - true, значит использовать шрифт клавиатуры */
        TextPaint getTextPaint(boolean bOwnBoldAndItalic)
        {
            TextPaint tp = new TextPaint();
            tp.density = (float) 1.0;
            tp.setDither(true);
            tp.setAntiAlias(true);
            if(bOwnBoldAndItalic)
            {
                if(st.has(style, Typeface.BOLD))
                    tp.setFakeBoldText(true);
                if(st.has(style, Typeface.ITALIC))
                    tp.setTextSkewX((float) -0.25);
            }
            if (st.font_keyboard&Font.tf!=null)
                tp.setTypeface(Typeface.create(Font.tf, style));
            else
                tp.setTypeface(Typeface.create(typeface, style));
//        	if (!Font.setTypeface(tp)) 
//                tp.setTypeface(Typeface.create(typeface, style));
        	
//            if (bKeyboardFont) {
//            	if (!Font.setTypeface(tp)) {
//                    tp.setTypeface(Typeface.create(typeface, style));
//            	}
//
//            } else
//                tp.setTypeface(Typeface.create(typeface, style));

            if(fontSize!=0)
                tp.setTextSize(fontSize);
            return tp;
        }
        boolean load(String prefKey)
        {
            int ret = fromString(st.pref().getString(prefKey, st.STR_NULL));
            if(ret<0)
            {
                save(st.pref(), prefKey);
            }
            return ret!=0;
        }
        int fromString(String s)
        {
            if(s==null||s.indexOf(';')<0)
                return 0;
            String ar[] = s.split(";");
            if(ar.length<3)
                return 0;
            try
            {
                typeface = intToTypeface(Integer.valueOf(ar[0]));
                style = Integer.valueOf(ar[1]);
                float fs = Float.valueOf(ar[2]);
                if(fs<1&&fs>=0)
                {
                    fontSize = KeyboardPaints.getPercToPixel(st.c(),true, fs,false);
                    return 1;
                }
                else
                {
                    fontSize = (int)fs;
                    return -1;
                }
            }
            catch (Throwable e)
            {}
            return 0;
        }
        public String toString()
        {
            return new StringBuffer().append(typefaceToInt(typeface)).append(';')
                                       .append(style).append(';')
                                       .append(KeyboardPaints.getPixelToPerc(st.c(),true,fontSize)).toString();
        }
        void save(SharedPreferences pref,String prefKey)
        {
            pref.edit().putString(prefKey,toString()).commit();
        }
    }
}
