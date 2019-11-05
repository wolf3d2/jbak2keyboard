package com.jbak2.ctrl;

import com.jbak2.JbakKeyboard.st;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.ClipData.Item;

/** новый обработчик буфера обмена, без 5 секундой задержки */
public class ClipbrdService {
	// переменные для ClipboardManager
    String m_sLastClipStr;
	ClipData cm_clip = null;
   	Item cm_item = null;
   	CharSequence cm_str = null;
/** новый обработчик буфера обмена, без 5 секундой задержки */
    public ClipbrdService(final Context c)
    {
		final ClipboardManager m_cm = (ClipboardManager) c.getSystemService(Context.CLIPBOARD_SERVICE);
		m_cm.addPrimaryClipChangedListener( new ClipboardManager.OnPrimaryClipChangedListener() {
			
			@Override
			public void onPrimaryClipChanged() {
		        try {
		        	cm_clip = m_cm.getPrimaryClip();
					if (cm_clip==null)
						return;
			        if (cm_clip.getItemCount()>0) {
			        	cm_item = cm_clip.getItemAt(0);
			        	if (cm_item==null) 
			        		return;
			        	cm_str = cm_item.getText();
			        	if (cm_str==null||cm_str.length()==0)
			        		return;
			            try{
			                if(cm_str.equals(m_sLastClipStr))
			                {
			                    return;
			                }
			                m_sLastClipStr = cm_str.toString();
			                st.stor().checkClipboardString(m_sLastClipStr);
			                clearVariable();
//			                m_sLastClipStr = cm_str;
//			                Toast.makeText(c,"Copy:\n"+cm_str,Toast.LENGTH_LONG).show();
			            }
			            catch(Throwable e)
			            {
			                clearVariable();
			            }
			        	//checkString(cm_str.toString());
			        }

				} catch (Throwable e) {
					return;
				}
			}
		});
    	
    }
    public void clearVariable() {
    	cm_clip = null;
    	cm_item = null;
    	cm_str = null;
    }
}
