package com.jbak2.JbakKeyboard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.util.Vector;

import com.jbak2.CustomGraphics.BitmapCachedGradBack;
import com.jbak2.CustomGraphics.GradBack;
import com.jbak2.JbakKeyboard.IKeyboard.KbdDesign;
import com.jbak2.JbakKeyboard.st.IntEntry;

import android.content.res.AssetManager;

public class CustomKbdDesign
{
	public static String ASSETS= "assets:";
	public static String FOLDER_ASSETS_SKIN= "SKIN/";
	public static String FOLDER_SKINS = "skins";
    public static String m_val;
    int errorLine = 0;
    String skinPath = st.STR_NULL;
    Vector<IntEntry> arValues = new Vector<IntEntry>();
    
    boolean load(String path)
    {
        skinPath = path;
        BufferedReader reader = null;
        try{
        	if (path.startsWith(ASSETS)){
        		AssetManager assetManager = st.c().getAssets();
        		String pth = path.substring(ASSETS.length()).trim();
                InputStreamReader istream = new InputStreamReader(assetManager.open(FOLDER_ASSETS_SKIN+pth));
                reader = new BufferedReader(istream);
        	} else
        		reader = new BufferedReader(new FileReader(path));
        }catch (Throwable e) {
        	st.logEx(e);
        }
        if(reader == null)
        {
            return false;
        }
        return load(reader);
    }
    boolean load(BufferedReader r)
    {
        int line = 1;
        try{
            String s;
            while((s = r.readLine())!=null)
            {
                int index = parseParam(s);
                if(index>-1)
                {
                    int dec = -1;
                    dec = processStringInt(m_val);
                    switch (index)
                    {
                    case IntEntry.KeyBackStartColor:
                    case IntEntry.KeyBackEndColor:
                    case IntEntry.KeyStrokeStartColor:
                    case IntEntry.KeyStrokeEndColor:
                    case IntEntry.SpecKeyBackStartColor:
                    case IntEntry.SpecKeyBackEndColor:
                    case IntEntry.SpecKeyStrokeStartColor:
                    case IntEntry.SpecKeyStrokeEndColor:
                    	dec = st.getSkinColorAlpha(dec);
                    }
                    arValues.add(new IntEntry(index,dec));
                }
                ++line;
            }
        }
        catch (Throwable e) {
            errorLine = line;
            return false;
        }
        return true;
    }
    final int getColor(int index)
    {
        return getIntValue(index, st.DEF_COLOR);
    }
    final int getIntValue(int index,int defVal)
    {
        for(IntEntry ie:arValues)
        {
            if(ie.index==index)
                return ie.value;
        }
        return defVal;
    }
    KbdDesign getDesign()
    {
        KbdDesign ret = new KbdDesign(0, 0, st.DEF_COLOR, 0, 0);
        ret.path = skinPath;
        int startColor,endColor,gradType;
        startColor = getIntValue(IntEntry.KeyBackStartColor, st.DEF_COLOR);
        endColor = getIntValue(IntEntry.KeyBackEndColor, st.DEF_COLOR);
        gradType = getIntValue(IntEntry.KeyBackGradientType, GradBack.GRADIENT_TYPE_LINEAR);
        int gap = getIntValue(IntEntry.KeyGapSize, GradBack.DEFAULT_GAP);
        int cornerX = getIntValue(IntEntry.KeyBackCornerX, GradBack.DEFAULT_CORNER_X);
        int cornerY = getIntValue(IntEntry.KeyBackCornerY, GradBack.DEFAULT_CORNER_Y);
        if(startColor!=st.DEF_COLOR)
        {
            ret.setKeysBackground(new BitmapCachedGradBack(true, startColor, endColor)
                                       .setGradType(gradType)
                                       .setGap(gap)
                                       .setCorners(cornerX, cornerY)
                                       .setGradType(getIntValue(IntEntry.KeyBackPressedGradientType, GradBack.GRADIENT_TYPE_LINEAR))
                                        ).setCopyItemColorsDesign();
        }
        startColor = getIntValue(IntEntry.KeyboardBackgroundStartColor, st.DEF_COLOR);
        endColor = getIntValue(IntEntry.KeyboardBackgroundEndColor, st.DEF_COLOR);
        gradType = getIntValue(IntEntry.KeyboardBackgroundGradientType, GradBack.GRADIENT_TYPE_LINEAR);
        if(startColor!=st.DEF_COLOR)
        {
            ret.setKbdBackground(new BitmapCachedGradBack(true, startColor, endColor)
                                       .setGradType(gradType)
                                       .setGap(0)
                                       .setCorners(0, 0)
                                       ).setCopyItemColorsDesign();
        }
        startColor = getIntValue(IntEntry.KeyStrokeStartColor, st.DEF_COLOR);
        endColor = getIntValue(IntEntry.KeyStrokeEndColor, st.DEF_COLOR);
        if(startColor!=st.DEF_COLOR&&ret.m_keyBackground!=null)
        {
            ret.m_keyBackground.setStroke(
                    new GradBack(startColor,endColor)
                        .setGap(gap-1)
                        .setCorners(cornerX, cornerY)
                            );
        }
        startColor = getColor(IntEntry.KeyBackPressedStartColor);
        endColor = getColor(IntEntry.KeyBackPressedEndColor);
        if(startColor!=st.DEF_COLOR&&ret.m_keyBackground!=null)
        {
            GradBack pressed = new GradBack(startColor,endColor)
                .setGap(gap)
                .setCorners(cornerX, cornerY)
                .setGradType(getIntValue(IntEntry.SpecKeyBackPressedGradientType, GradBack.GRADIENT_TYPE_LINEAR));
            startColor = getColor(IntEntry.KeyPressedStrokeStartColor);
            endColor = getColor(IntEntry.KeyPressedStrokeEndColor);
            if(startColor!=st.DEF_COLOR)
            {
                pressed.setStroke(
                        new GradBack(startColor,endColor)
                        .setGap(gap-1)
                        .setCorners(cornerX, cornerY)
                        );
            }
            ret.m_keyBackground.setPressedGradBack(pressed);
        }
        
        ret.textColor = getIntValue(IntEntry.KeyTextColor, st.DEF_COLOR);
        ret.addItemColor(IntEntry.KeyTextColor, ret.textColor);
        ret.setCopyItemColorsDesign();
        if(getIntValue(IntEntry.KeyTextBold, 0)==1)
            ret.flags|=st.DF_BOLD;
        startColor = getIntValue(IntEntry.SpecKeyBackStartColor, st.DEF_COLOR);
        endColor = getIntValue(IntEntry.SpecKeyBackEndColor, st.DEF_COLOR);
        // создаём дизайн для спецкнопок
        if(startColor!=st.DEF_COLOR)
        {
            int textColor = getIntValue(IntEntry.SpecKeyTextColor, st.DEF_COLOR);
            GradBack gb = new BitmapCachedGradBack(true, startColor, endColor)
                .setGradType(ret.m_kbdBackground.m_gradType)
                // смещение спецклавиш (было просто gap)
                .setGap(gap);
            gb.setCorners(cornerX, cornerY);
            startColor = getIntValue(IntEntry.SpecKeyStrokeStartColor, st.DEF_COLOR);
            endColor = getIntValue(IntEntry.SpecKeyStrokeEndColor, st.DEF_COLOR);
            if(startColor!=st.DEF_COLOR)
                gb.setStroke(new GradBack(startColor, endColor)
                		.setCorners(cornerX, cornerY)
                		.setGap(gap-1));
            ret.setFuncKeysDesign(new KbdDesign(0, 0, textColor, 0, 0).setKeysBackground(gb));
            ret.m_kbdFuncKeys.setColors(ret.m_kbdFuncKeys.textColor, getColor(IntEntry.SpecKeySymbolColor), getColor(IntEntry.SpecKeyTextPressedColor), getColor(IntEntry.SpecKeySymbolPressedColor));
            startColor = getColor(IntEntry.SpecKeyBackPressedStartColor);
            endColor = getColor(IntEntry.SpecKeyBackPressedEndColor);
            if(startColor!=st.DEF_COLOR&&ret.m_keyBackground!=null)
            {
                GradBack pressed = new GradBack(startColor,endColor)
                    .setGap(gap)
                    .setCorners(cornerX, cornerY);
                startColor = getColor(IntEntry.KeyPressedStrokeStartColor);
                endColor = getColor(IntEntry.KeyPressedStrokeEndColor);
                if(startColor!=st.DEF_COLOR)
                {
                    pressed.setStroke(
                            new GradBack(startColor,endColor)
                            .setGap(gap-1)
                            .setCorners(cornerX, cornerY)
                            );
                }
                ret.m_kbdFuncKeys.m_keyBackground.setPressedGradBack(pressed);
            }

        }
        ret.setColors(ret.textColor, getColor(IntEntry.KeySymbolColor), getColor(IntEntry.KeyTextPressedColor), getColor(IntEntry.KeySymbolPressedColor));
        return ret;
    }
    public static int parseParam(String s)
    {
        int index = -1;
        if(s==null)
            return index;
        s = s.trim();
        if(s.length()==0)
            return index;
        int f = s.indexOf('=');
        if(f<0)return index;
        String name = s.substring(0,f).trim();
        index = findName(name);
        if(index>-1)
        {
            m_val = s.substring(f+1);
        }
        return index;
    }
    public static int findName(String name)
    {
        int index = 0;
        for(String s:st.arDesignNames)
        {
            if(s.compareTo(name)==0)
                return index;
            index++;
        }
        return -1;
    }
    public static  int processStringInt(String s)
    {
        s = s.trim();
        if(s.indexOf('#')==0||s.startsWith("0x")) // 16-ричное значение
        {
            return st.parseInt(s.substring(1),16);
        }
        return Integer.valueOf(s);
    }
    String getErrString()
    {
        if(errorLine>0)
            return "Parse err: "+new File(skinPath).getName()+", line: "+errorLine+st.STR_LF;
        else
            return "Can't read: "+new File(skinPath).getName();

    }
    static String updateArraySkins()
    {
//    	if (IKeyboard.arDesign==null)
//    		IKeyboard.setDefaultDesign();
        String err = st.STR_NULL;
        try{
            String path = st.getSettingsPath()+FOLDER_SKINS;  
            File dir = new File(path);
            if(!dir.exists())
            {
                dir.mkdirs();
               return err;
            }
            File skins[] = dir.listFiles(new FilenameFilter()
            {
                @Override
                public boolean accept(File dir, String filename)
                {
                    int pos = filename.lastIndexOf('.');
                    if(pos<0)return false;
                    return filename.substring(pos+1).compareTo("skin")==0;
                }
            });
            if(skins!=null&skins.length==0)
                return err;
            Vector<KbdDesign> ar = new Vector<IKeyboard.KbdDesign>();
            for(File fs:skins)
            {
                ar.add(new KbdDesign(fs.getAbsolutePath()));
            }
            int pos = 0;
            for(KbdDesign kd:st.arDesign)
            {   
                if(kd.path!=null)
                	if(!kd.path.startsWith(ASSETS))
                		break;
                ++pos;
            }
            KbdDesign des[] = new KbdDesign[pos+ar.size()];
            System.arraycopy(st.arDesign, 0, des, 0, pos);
            for(KbdDesign kd:ar)
            {
                des[pos]=kd;
                pos++;
            }
            st.arDesign = des;
        }
        catch (Throwable e) {
        	st.logEx(e);
            return "System error";
        }
        return err;
    }
}
