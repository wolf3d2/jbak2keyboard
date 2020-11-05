package com.jbak2.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListAdapter;

import com.jbak2.JbakKeyboard.CandView;
import com.jbak2.JbakKeyboard.R;
import com.jbak2.JbakKeyboard.ServiceJbKbd;
import com.jbak2.JbakKeyboard.st;
import com.jbak2.JbakKeyboard.st.UniObserver;
import com.jbak2.ctrl.th;

/** Класс предоставляет функции для вывода различных диалогов */
public class Dlg {
	/** тип - окно help */
	public static final int TYPE_DEFAULT = 0;
	/** тип - окно замены в выделении */
	public static final int TYPE_REPLACE_IN_SELECTED_TEXT = 1;

	static AlertDialog dlg = null;
	// ПОКА НЕ ИСПОЛЬЗУЮТСЯ!
	static st.UniObserver obs = null;
	static int return_button = 0;

	/** Обработчик нажатия кнопок в диалоге */
	static class OnButtonListener implements DialogInterface.OnClickListener {
		/**
		 * Конструктор. Получает обработчик нажатия
		 * 
		 * @param call
		 *            Обработчик, вызываемый при нажатии кнопки в диалоге. Первый
		 *            параметр - код нажатой кнопки в виде Integer
		 */
		public OnButtonListener(UniObserver call) {
			callback = call;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (callback != null)
				callback.OnObserver(new Integer(which), callback.m_param2);
		}

		UniObserver callback;
	}

	public static AlertDialog.Builder getDefaultAlertDialogBuilderTheme(Context c)
	{
		AlertDialog.Builder bd = null;
		if (th.isDarkThemeApp())
			bd = new AlertDialog.Builder(c);
		else
			bd = new AlertDialog.Builder(c, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
		return bd;
	}
	/**
	 * Пользовательский диалог, содержащий заданное пользователем окно<br>
	 * По окончании вызова - вызовет callback.OnObserver(Integer buttonCode,
	 * callback.m_param2)
	 * 
	 * @param c
	 *            Контекст
	 * @param customView
	 *            Пользовательское окно
	 * @param but1
	 *            Текст кнопки BUTTON_POSITIVE или null, если кнопка не нужна
	 * @param but2
	 *            Текст кнопки BUTTON_NEGATIVE или null, если кнопка не нужна
	 * @param but3
	 *            Текст кнопки BUTTON_NEUTRAL или null, если кнопка не нужна
	 * @param callback
	 *            Обработчик нажатия кнопок. Конструкция вызова -
	 *            callback.OnObserver(Integer buttonCode, callback.m_param2)
	 * @return Возвращает созданный диалог
	 */
	public static AlertDialog customDialog(Context c, View customView, String but1, String but2, String but3,
			UniObserver obs) {
		AlertDialog.Builder bd = getDefaultAlertDialogBuilderTheme(c);
		dlg = bd.create();
		OnButtonListener cl = new OnButtonListener(obs);
		dlg.setView(customView);
		if (but1 != null)
			dlg.setButton(AlertDialog.BUTTON_POSITIVE, but1, cl);
		if (but2 != null)
			dlg.setButton(AlertDialog.BUTTON_NEGATIVE, but2, cl);
		if (but3 != null)
			dlg.setButton(AlertDialog.BUTTON_NEUTRAL, but3, cl);
		dlg.show();
		return dlg;
	}

	/**
	 * кастомный диалог. Если кнопка = null, то она не отображается
	 */
	public static AlertDialog customDialog(Context c, int layout, String yes, String no, String cancel,
			UniObserver obs) {
		View view = View.inflate(c, layout, null);
		// AlertDialog.Builder alert = new AlertDialog.Builder(c);
		// // Now set the dialog content
		// alert.setContentView(view);
		return customDialog(c, view, yes, no, cancel, obs);

	}

	public static AlertDialog showImageWindow(Context c, View customView, String but1, String but2, String but3,
			UniObserver obs) {
		AlertDialog.Builder bd = getDefaultAlertDialogBuilderTheme(c);
		dlg = bd.create();
		OnButtonListener cl = new OnButtonListener(obs);
		dlg.setView(customView);
		if (but1 != null)
			dlg.setButton(AlertDialog.BUTTON_POSITIVE, but1, cl);
		if (but2 != null)
			dlg.setButton(AlertDialog.BUTTON_NEGATIVE, but2, cl);
		if (but3 != null)
			dlg.setButton(AlertDialog.BUTTON_NEUTRAL, but3, cl);
		dlg.show();
		return dlg;
	}

	////
	public static AlertDialog customViewAndMenu(Context c, View customView, ListAdapter adapter, String title,
			UniObserver callback) {
		AlertDialog.Builder bd = getDefaultAlertDialogBuilderTheme(c);
		final UniObserver obs = callback;
		bd.setAdapter(adapter, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				obs.OnObserver(new Integer(which), obs);
			}
		});
		dlg = bd.create();
		if (title != null)
			dlg.setTitle(title);
		dlg.setView(customView);
		dlg.show();
		return dlg;

	}

	public static AlertDialog customMenu(Context c, ListAdapter adapter, String title, UniObserver callback) {
		AlertDialog.Builder bd = getDefaultAlertDialogBuilderTheme(c);
		final UniObserver obs = callback;
		bd.setAdapter(adapter, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				obs.OnObserver(new Integer(which), obs);
			}
		});
		dlg = bd.create();
		if (title != null)
			dlg.setTitle(title);
		dlg.show();
		return dlg;

	}

	/** */
	public static AlertDialog yesNoDialog(Context c, String query, UniObserver callback) {
		if (ServiceJbKbd.inst != null && ServiceJbKbd.inst.isInputViewShown())
			ServiceJbKbd.inst.acGone();

		AlertDialog.Builder bd = getDefaultAlertDialogBuilderTheme(c);
		OnButtonListener cl = new OnButtonListener(callback);
		bd.setPositiveButton(R.string.yes, cl);
		bd.setNegativeButton(R.string.no, cl);
		bd.setMessage(query);
		dlg = bd.create();
		dlg.show();
		return dlg;
	}

	public static AlertDialog yesNoCancelDialog(Context c, String query, int id_text_yes, int id_text_no,
			int id_text_cancel, UniObserver callback) {
		if (ServiceJbKbd.inst != null && ServiceJbKbd.inst.isInputViewShown())
			ServiceJbKbd.inst.acGone();
		AlertDialog.Builder bd = getDefaultAlertDialogBuilderTheme(c);
		OnButtonListener cl = new OnButtonListener(callback);
		bd.setPositiveButton(id_text_yes, cl);
		bd.setNegativeButton(id_text_cancel, cl);
		bd.setNeutralButton(id_text_no, cl);
		bd.setMessage(query);
		dlg = bd.create();
		dlg.show();
		return dlg;
	}

	public static AlertDialog yesNoDialog(Context c, String query, int id_text_yes, int id_text_no,
			UniObserver callback) {
		if (ServiceJbKbd.inst != null && ServiceJbKbd.inst.isInputViewShown())
			ServiceJbKbd.inst.acGone();
		AlertDialog.Builder bd = getDefaultAlertDialogBuilderTheme(c);
		OnButtonListener cl = new OnButtonListener(callback);
		bd.setPositiveButton(id_text_yes, cl);
		bd.setNegativeButton(id_text_no, cl);
		bd.setMessage(query);
		dlg = bd.create();
		dlg.show();
		return dlg;
	}

	public static abstract class RunOnYes {
		public RunOnYes(Context c, String query) {
			Dlg.yesNoDialog(c, query, mkObserver());
		}

		st.UniObserver mkObserver() {
			return new UniObserver() {
				@Override
				public int OnObserver(Object param1, Object param2) {
					if (ServiceJbKbd.inst != null && ServiceJbKbd.inst.isInputViewShown()
							&& ServiceJbKbd.inst.m_acPlace != CandView.AC_PLACE_NONE)
						ServiceJbKbd.inst.acVisible();
					if (((Integer) param1).intValue() == AlertDialog.BUTTON_POSITIVE)
						run();
					return 0;
				}
			};
		}

		public abstract void run();
	}

	public static AlertDialog helpDialog(Context c, String text, int ResIdTextButtonOrZero, UniObserver callback) {
		AlertDialog.Builder bd = getDefaultAlertDialogBuilderTheme(c);
		OnButtonListener cl = new OnButtonListener(callback);
		if (ResIdTextButtonOrZero == 0)
			bd.setPositiveButton(R.string.ok, cl);
		else
			bd.setPositiveButton(ResIdTextButtonOrZero, cl);
		bd.setMessage(text);
		dlg = bd.create();
		dlg.show();
		return dlg;
	}

	public static AlertDialog helpDialog(Context c, String text, UniObserver callback) {
		AlertDialog.Builder bd = getDefaultAlertDialogBuilderTheme(c);
		OnButtonListener cl = new OnButtonListener(callback);
		bd.setPositiveButton(R.string.ok, cl);
		bd.setMessage(text);
		dlg = bd.create();
		dlg.show();
		return dlg;
	}

	public static void helpDialog(Context c, int resIdText) {
		Dlg.helpDialog(c, c.getString(resIdText));
	}

	public static AlertDialog helpDialog(Context c, String text) {
		AlertDialog.Builder bd = getDefaultAlertDialogBuilderTheme(c);

		OnButtonListener cl = new OnButtonListener(new st.UniObserver() {
			@Override
			public int OnObserver(Object param1, Object param2) {
				return 0;
			}
		});
		bd.setPositiveButton(R.string.ok, cl);
		bd.setMessage(text);
		dlg = bd.create();
		dlg.show();
		return dlg;
	}

	public static void dismiss() {
		if (dlg != null)
			dlg.dismiss();
	}

	/**
	 * нажатие какой кнопки диалога возвращаем, если окно закрывается по кнопке
	 * Назад в диалоге должны быть: return_button =
	 * AlertDialog.название_кнопки_возврата, и dlg.setOnKeyListener(onKeyListener);
	 */
	static DialogInterface.OnKeyListener onKeyListener = new DialogInterface.OnKeyListener() {

		@Override
		public boolean onKey(DialogInterface arg0, int arg1, KeyEvent arg2) {
			if (obs != null)
				obs.OnObserver(new Integer(return_button), obs.m_param2);

			return false;
		}
	};

}