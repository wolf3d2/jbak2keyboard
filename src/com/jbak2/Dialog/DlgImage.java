package com.jbak2.Dialog;

import com.jbak2.JbakKeyboard.st;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

/** показываем картинку в большом окне */
public class DlgImage {
	AlertDialog dlg = null;
	Context m_c;
	public static DlgImage inst = null;

	public DlgImage(Context c) {
		m_c = c;
		inst = this;
	}

	void show(Drawable dr) {
		ImageView iv = new ImageView(m_c);
		iv.setAdjustViewBounds(true);
		iv.setImageDrawable(dr);
		iv.setScaleType(ScaleType.CENTER_CROP);
		AlertDialog.Builder bd = Dlg.getDefaultAlertDialogBuilderTheme(m_c);
//        AlertDialog.Builder bd = new AlertDialog.Builder(m_c);

		dlg = bd.create();
		dlg.setView(iv);
		int width = (int)st.getDisplayWidth(m_c);
		int height = (int)st.getDisplayHeight(m_c)/2;

		dlg.show();
		dlg.getWindow().setLayout(width, height);
		// return dlg;

		// просто пример реализации на весь экран - у меня всё равно не пашет
		// https://overcoder.net/q/39001/%D0%BA%D0%B0%D0%BA-%D1%81%D0%B4%D0%B5%D0%BB%D0%B0%D1%82%D1%8C-%D1%82%D0%B0%D0%BA-%D1%87%D1%82%D0%BE%D0%B1%D1%8B-%D0%B4%D0%B8%D0%B0%D0%BB%D0%BE%D0%B3-%D0%BE%D0%BF%D0%BE%D0%B2%D0%B5%D1%89%D0%B5%D0%BD%D0%B8%D1%8F-%D0%B7%D0%B0%D0%BD%D0%B8%D0%BC%D0%B0%D0%BB-90-%D1%80%D0%B0%D0%B7%D0%BC%D0%B5%D1%80%D0%B0-%D1%8D%D0%BA%D1%80%D0%B0%D0%BD%D0%B0
//        AlertDialog.Builder adb = new AlertDialog.Builder(this);
//        Dialog d = adb.setView(new View(this)).create();
//        // (That new View is just there to have something inside the dialog that can grow big enough to cover the whole screen.)
//
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(d.getWindow().getAttributes());
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
//        d.show();
//        d.getWindow().setAttributes(lp);	
	}

	public void dismiss() {
		if (dlg != null)
			dlg.dismiss();
		inst = null;
	}

}
