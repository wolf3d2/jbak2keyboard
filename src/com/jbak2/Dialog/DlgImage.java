package com.jbak2.Dialog;

import com.jbak2.JbakKeyboard.st;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/** показываем картинку в большом окне */
public class DlgImage 
{
	AlertDialog dlg = null;
    Context m_c;
	public static DlgImage inst = null;
	
    public DlgImage(Context c)
    {
        m_c = c;
        inst = this;
    }
	void show(Drawable dr)
	{
		ImageView iv = new ImageView(m_c);
		iv.setImageDrawable(dr);
        AlertDialog.Builder bd = new AlertDialog.Builder(m_c);
        dlg = bd.create();
        dlg.setView(iv);
        dlg.show();
        //return dlg;

	}
	   public void dismiss()
	   {
		   if (dlg!=null)
			   dlg.dismiss();
		   inst = null;
	   }

}
