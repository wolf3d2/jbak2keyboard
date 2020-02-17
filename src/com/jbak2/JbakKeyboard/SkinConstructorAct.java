package com.jbak2.JbakKeyboard;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;

import com.jbak2.Dialog.Dlg;
import com.jbak2.JbakKeyboard.st.IntEntry;
import com.jbak2.ctrl.GlobDialog;

public class SkinConstructorAct extends Activity {
	// сколько прибавлять к id поля et для кнопки пикера
	int ID_BTN = 100;
	static int value = 0;
	int counter_id = -1;
	static DataOutputStream fout = null;
	public static String COMM = "//";
	public static ArrayList<String> arskinname = new ArrayList<String>();
	public static Vector<IntEntry> arval = new Vector<IntEntry>();
	String feditname = st.STR_NULL;
	Button upd;
	Button help;
	LinearLayout llskin = null;
	TextView tv_name = null;
	String skinname = st.STR_NULL;
	boolean fl_changed = false;
	static SkinConstructorAct inst;
	TextWatcher tw = new TextWatcher() {
		@Override
		public void afterTextChanged(Editable s) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			fl_changed = true;
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// fl_changed = true;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.skinconstructor);
		inst = this;
		if (ColorPicker.inst != null) {
			ColorPicker.inst.finish();
		}
		tv_name = (TextView) findViewById(R.id.sc_skinname);
		upd = (Button) findViewById(R.id.sc_upd);
		help = (Button) findViewById(R.id.sc_help);
		help.setText(" ? ");

		// st.kv().reloadSkin();
	}

	public void loadSkin() {
		loadDefault();
		load(skinname);
		createEditLayout();
	}

	String getDesignPath(String name) {
		return st.getSettingsPath() + "skins/" + name;
	}

	boolean load(String name) {
		BufferedReader reader = null;
		try {
			name = getDesignPath(name);
			reader = new BufferedReader(new FileReader(name));
		} catch (Throwable e) {
		}
		if (reader == null) {
			return false;
		}
		return load(reader);
	}

	boolean load(BufferedReader r) {
		int line = 1;
		counter_id = 1;
		try {
			IntEntry ie = null;
			String s;
			while ((s = r.readLine()) != null) {
				int index = parseParam(s);
				if (index > -1) {
					int iii = getIndexIntEntryByIndex(index);

					if (iii == -1) {
						arval.add(new IntEntry(index, getValueIntEntryByIndex(index), getValueIntEntryByIndex(index),
								counter_id));
						counter_id++;
					} else {
						ie = arval.get(index);
						ie.value = value;
						arval.set(iii, ie);
					}
				}
				++line;
			}
		} catch (Throwable e) {
			st.toast("Error line" + line);
			return false;
		}
		return true;
	}

	public static int parseParam(String s) {
		int index = -1;
		if (s == null)
			return index;
		s = s.trim();
		if (s.length() == 0)
			return index;
		int fff = s.indexOf(COMM);
		if (fff >= 0) {
			s = s.substring(0, fff).trim();
		} else {
			s = s.substring(0).trim();
		}
		if (s.length() == 0)
			return index;
		fff = s.indexOf('=');

		if (fff < 0)
			return index;
		String name = s.substring(0, fff).trim();
		// value = Integer.valueOf(name.substring(f+1));
		index = getIndexIntEntryByKey(name);
		if (index > -1) {
			value = CustomKbdDesign.processStringInt(s.substring(fff + 1));
		}
		return index;
	}

	public boolean VisibleEditSkin() {
		llskin = null;
		llskin = (LinearLayout) findViewById(R.id.sсll_skin);
		if (llskin != null) {
			llskin.setVisibility(View.VISIBLE);
			return true;
		}
		return false;
	}

	/**
	 * нажатие на enter или на Ок в диалоге редактирования имени скина <br>
	 * здесь-же закрываем диалог
	 */
	public void setEditNameSkin(EditText et, int action) {
		String text = et.getText().toString().trim();
		if (text.length() > 0) {
			if (!text.endsWith(st.STR_POINT + st.EXT_SKIN)) {
				text += st.STR_POINT + st.EXT_SKIN;
			}
			setNameSkin(text);
			if (VisibleEditSkin()) {
				if (action == 0) {
					save();
					// loadDefault();
					loadSkin();
					// createEditLayout();
				} else if (action == 1) {
					save();
					loadSkin();
					// loadDefault();
					// createEditLayout();
				} else if (action > 1) {
					loadSkin();
				}
			}
		}
		st.hidekbd();

		// search_txt = et.getText().toString().trim().toLowerCase();
		// if (search_txt.isEmpty()) {
		// createWordLayout();
		// } else {
		// userword.getAllWord(curlang);
		// m_adapter.getFilter().filter(search_txt);
		// }
		Dlg.dismiss();
	}

	public void editNameSkin(String txt, final int action) {
		final View v = getLayoutInflater().inflate(R.layout.dialog_edit, null);
		((TextView) v.findViewById(R.id.eadw_title)).setText(R.string.sc_skin_name);
		((TextView) v.findViewById(R.id.eadw_help)).setVisibility(View.GONE);
		;
		((Button) v.findViewById(R.id.eadw_plus_btn_button)).setVisibility(View.GONE);
		((Button) v.findViewById(R.id.eadw_plus_tpl_button)).setVisibility(View.GONE);
		final EditText et = (EditText) v.findViewById(R.id.eadw_edit);
		et.setSingleLine();
		et.setText(txt);
		et.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
					setEditNameSkin(et, action);
				}
				return false;
			}
		});
		st.showkbd(et, true);

		st.UniObserver obs = new st.UniObserver() {
			@Override
			public int OnObserver(Object param1, Object param2) {
				if (((Integer) param1).intValue() == AlertDialog.BUTTON_POSITIVE) {
					setEditNameSkin(et, action);
				}
				return 0;
			}
		};

		Dlg.customDialog(this, v, getString(R.string.ok), getString(R.string.cancel), null, obs);

		// final GlobDialog gd = new GlobDialog(this);
		// gd.ret_edittext_text = txt;
		// gd.set(this.getString(R.string.sc_skin_name), R.string.ok, R.string.cancel);
		// gd.setObserver(new st.UniObserver()
		// {
		// @Override
		// public int OnObserver(Object param1, Object param2)
		// {
		// if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
		// {
		// if (gd.ret_edittext_text.length() > 0){
		// if (!gd.ret_edittext_text.endsWith(st.STR_POINT+st.EXT_SKIN)){
		// gd.ret_edittext_text+=st.STR_POINT+st.EXT_SKIN;
		// }
		// setNameSkin(gd.ret_edittext_text);
		// if (VisibleEditSkin()) {
		// if (action == 0){
		// save();
		// //loadDefault();
		// loadSkin();
		// //createEditLayout();
		// }
		// else if (action == 1){
		// save();
		// loadSkin();
		//// loadDefault();
		//// createEditLayout();
		// }
		// else if (action > 1){
		// loadSkin();
		// }
		// }
		// }
		// st.hidekbd();
		// }
		// return 0;
		// }
		// });
		// gd.showEdit(txt,0);
	}

	public void query_changed(final int action) {
		st.UniObserver obs = new st.UniObserver() {
			
			@Override
			public int OnObserver(Object param1, Object param2) {
				if (((Integer) param1).intValue() == AlertDialog.BUTTON_POSITIVE) {
					// запись перед загрузкой нового скина
					if (action == 0) {
						save();
					} else if (action == 1) {

						save();
						fl_changed = false;
						setNameSkin(arskinname.get(action));
						loadSkin();
					}
					if (action > 1) {
						save();
					}
				}
				if (((Integer) param1).intValue() == AlertDialog.BUTTON_NEGATIVE) {
					// запись перед загрузкой нового скина
					if (action == 0) {
						loadDefault();
						createEditLayout();
					} else if (action == 1) {
						setNameSkin(skinname);

					} else if (action > 1) {

						setNameSkin(arskinname.get(action));
						loadSkin();
					}
				}
				return 0;
			}
		};
		Dlg.yesNoCancelDialog(inst, inst.getString(R.string.data_changed), 
				R.string.ok, R.string.no, R.string.cancel, obs);
		
//		final GlobDialog gd = new GlobDialog(this);
//		gd.set(R.string.data_changed, R.string.ok, R.string.no, R.string.cancel);
//		gd.setObserver(new st.UniObserver() {
//			@Override
//			public int OnObserver(Object param1, Object param2) {
//				if (((Integer) param1).intValue() == AlertDialog.BUTTON_POSITIVE) {
//					// запись перед загрузкой нового скина
//					if (action == 0) {
//						save();
//					} else if (action == 1) {
//
//						save();
//						fl_changed = false;
//						setNameSkin(arskinname.get(action));
//						loadSkin();
//					}
//					if (action > 1) {
//						save();
//					}
//				}
//				if (((Integer) param1).intValue() == AlertDialog.BUTTON_NEGATIVE) {
//					// запись перед загрузкой нового скина
//					if (action == 0) {
//						loadDefault();
//						createEditLayout();
//					} else if (action == 1) {
//						setNameSkin(skinname);
//
//					} else if (action > 1) {
//
//						setNameSkin(arskinname.get(action));
//						loadSkin();
//					}
//				}
//				return 0;
//			}
//		});
//		gd.showAlert();
	}

	public void onClick(View view) {
		CustomKbdDesign.updateArraySkins();
		switch (view.getId()) {
		case R.id.sc_upd:
			save();
			// CustomKbdDesign.loadCustomSkins();
			st.pref().edit().putString(st.PREF_KEY_KBD_SKIN_PATH, st.getSettingsPath() + "skins/" + skinname).commit();
			try {
				st.kv().m_curDesign.path = getDesignPath(skinname);
				st.kv().reloadSkin();
			} catch (Exception e) {

			}
			return;
		case R.id.sc_edit:
			st.hidekbd();
			if (arskinname != null)
				arskinname.clear();
			arskinname.add(this.getString(R.string.sc_create_new));
			arskinname.add(this.getString(R.string.sc_skin_change));
			File myFolder = new File(st.getSettingsPath() + "/skins");
			if (myFolder.exists() == false) {
				myFolder.mkdir();
			}
			if (myFolder.isDirectory() != false) {
				String[] file = myFolder.list();
				for (int i = 0; i < file.length; i++) {
					if (file[i].endsWith(st.STR_POINT + st.EXT_SKIN)) {
						arskinname.add(file[i]);
					}
				}
			}
			String[] ars = new String[arskinname.size()];
			for (int i = 0; i < arskinname.size(); i++) {
				ars[i] = arskinname.get(i);
			}
			ArrayAdapter<String> ar = new ArrayAdapter<String>(this, R.layout.tpl_instr_list, ars);

			Dlg.customMenu(this, ar, inst.getString(R.string.selection), new st.UniObserver() {
				@Override
				public int OnObserver(Object param1, Object param2) {
					int pos = (((Integer) param1).intValue());
					if (pos == 0) {
						if (!fl_changed) {
							editNameSkin(st.STR_NULL, 0);
						} else {
							query_changed(0);
						}

					} else if (pos == 1) {
						if (skinname.length() > 0) {
							editNameSkin(skinname, 1);
						} else {
							st.toast(R.string.sc_notskin);
						}
					} else if (pos > 1) {
						if (fl_changed) {
							query_changed(pos);
						} else {
							setNameSkin(arskinname.get(pos));
							loadSkin();
						}
					}
					pos = -1;
					return 0;
				}
			});

			return;
		case R.id.sc_help:
			String out = this.getString(R.string.sc_help) + st.STR_LF + st.STR_LF
					+ this.getString(R.string.pc2act_color_desc);
			Dlg.helpDialog(inst, out);
			// st.help(out);
		}
	}

	public void setNameSkin(String name) {
		if (tv_name != null) {
			skinname = name;
			tv_name.setText(name.trim());
		}
	}

	public void loadDefault() {
		if (llskin != null)
			llskin.removeAllViews();
		arval.clear();
		arval.add(new IntEntry(IntEntry.KeyBackStartColor, 0xfffafafa, 0xfffafafa, 1));
		arval.add(new IntEntry(IntEntry.KeyBackEndColor, 0xffd0ffff, 0xffd0ffff, 2));
		arval.add(new IntEntry(IntEntry.KeyBackGradientType, 0, 0, 3));
		arval.add(new IntEntry(IntEntry.KeyTextColor, 0xff002020, 0xff002020, 4));
		arval.add(new IntEntry(IntEntry.KeyTextBold, 1, 1, 5));
		arval.add(new IntEntry(IntEntry.KeyGapSize, 1, 1, 6));
		arval.add(new IntEntry(IntEntry.KeyStrokeStartColor, 0xff005050, 0xff005050, 7));
		arval.add(new IntEntry(IntEntry.KeyStrokeEndColor, 0xff005050, 0xff005050, 8));
		arval.add(new IntEntry(IntEntry.KeyboardBackgroundStartColor, 0xffffffee, 0xffffffee, 9));
		arval.add(new IntEntry(IntEntry.KeyboardBackgroundEndColor, 0xffaaaaff, 0xffaaaaff, 10));
		arval.add(new IntEntry(IntEntry.KeyboardBackgroundGradientType, 1, 1, 11));
		arval.add(new IntEntry(IntEntry.SpecKeyBackStartColor, 0xff686868, 0xff686868, 12));
		arval.add(new IntEntry(IntEntry.SpecKeyBackEndColor, 0xff686868, 0xff686868, 13));
		arval.add(new IntEntry(IntEntry.SpecKeyStrokeStartColor, 0xff005050, 0xff005050, 14));
		arval.add(new IntEntry(IntEntry.SpecKeyStrokeEndColor, 0xff005050, 0xff005050, 15));
		arval.add(new IntEntry(IntEntry.SpecKeyTextColor, 0xffffffff, 0xffffffff, 16));
		arval.add(new IntEntry(IntEntry.KeyBackCornerX, 9, 9, 17));
		arval.add(new IntEntry(IntEntry.KeyBackCornerY, 9, 9, 18));
		arval.add(new IntEntry(IntEntry.KeySymbolColor, 0xffcc0000, 0xffcc0000, 19));
		arval.add(new IntEntry(IntEntry.KeyTextPressedColor, 0xff00cc00, 0xff00cc00, 20));
		arval.add(new IntEntry(IntEntry.KeySymbolPressedColor, 0xff00cc00, 0xff00cc00, 21));
		arval.add(new IntEntry(IntEntry.SpecKeySymbolColor, 0xffffffff, 0xffffffff, 22));
		arval.add(new IntEntry(IntEntry.SpecKeyTextPressedColor, 0xffffffff, 0xffffffff, 23));
		arval.add(new IntEntry(IntEntry.SpecKeySymbolPressedColor, 0xffff0000, 0xffff0000, 24));
		arval.add(new IntEntry(IntEntry.KeyBackPressedStartColor, 0xff686868, 0xff686868, 25));
		arval.add(new IntEntry(IntEntry.KeyBackPressedEndColor, 0xff686868, 0xff686868, 26));
		arval.add(new IntEntry(IntEntry.KeyBackPressedGradientType, 1, 1, 27));
		arval.add(new IntEntry(IntEntry.KeyPressedStrokeStartColor, 0xff005050, 0xff005050, 28));
		arval.add(new IntEntry(IntEntry.KeyPressedStrokeEndColor, 0xff005050, 0xff005050, 29));
		arval.add(new IntEntry(IntEntry.SpecKeyBackPressedStartColor, 0xffff0000, 0xffff0000, 30));
		arval.add(new IntEntry(IntEntry.SpecKeyBackPressedEndColor, 0xff002020, 0xff002020, 31));
		arval.add(new IntEntry(IntEntry.SpecKeyBackPressedGradientType, 1, 1, 32));
		arval.add(new IntEntry(IntEntry.SpecKeyPressedStrokeStartColor, 0xff005050, 0xff005050, 33));
		arval.add(new IntEntry(IntEntry.SpecKeyPressedStrokeEndColor, 0xff005050, 0xff005050, 34));
		fl_changed = false;
	}

	public void save() {
		try {
			File f = new File(getDesignPath(skinname.trim()));
			f.delete();
			// f.createNewFile();
			// fout = new DataOutputStream(new FileOutputStream(f));
			FileWriter fout = new FileWriter(getDesignPath(skinname.trim()), false);

			String out3 = "// Created Skin constructor v1.02\n// by MWcorp\n";
			out3 += getSaveLine(0, -1, 0, R.string.sc_cat1);
			out3 += getSaveLine(R.string.sc_msg1, getIndexIntEntryByIndex(IntEntry.KeyBackStartColor), 16, 0);
			out3 += getSaveLine(R.string.sc_msg2, getIndexIntEntryByIndex(IntEntry.KeyBackEndColor), 16, 0);
			out3 += getSaveLine(R.string.sc_msg3, getIndexIntEntryByIndex(IntEntry.KeyBackPressedGradientType), 10, 0);
			out3 += getSaveLine(R.string.sc_msg4, getIndexIntEntryByIndex(IntEntry.KeyBackPressedStartColor), 16, 0);
			out3 += getSaveLine(R.string.sc_msg5, getIndexIntEntryByIndex(IntEntry.KeyBackPressedEndColor), 16, 0);
			out3 += getSaveLine(R.string.sc_msg32, getIndexIntEntryByIndex(IntEntry.KeyBackGradientType), 10, 0);
			fout.write(out3);
			out3 = getSaveLine(0, -1, 0, R.string.sc_cat2);
			out3 += getSaveLine(R.string.sc_msg6, getIndexIntEntryByIndex(IntEntry.KeyTextColor), 16, 0);
			out3 += getSaveLine(R.string.sc_msg7, getIndexIntEntryByIndex(IntEntry.KeyTextBold), 10, 0);
			out3 += getSaveLine(R.string.sc_msg8, getIndexIntEntryByIndex(IntEntry.KeySymbolColor), 16, 0);
			out3 += getSaveLine(R.string.sc_msg9, getIndexIntEntryByIndex(IntEntry.KeyTextPressedColor), 16, 0);
			out3 += getSaveLine(R.string.sc_msg10, getIndexIntEntryByIndex(IntEntry.KeySymbolPressedColor), 16, 0);
			fout.write(out3);
			out3 = getSaveLine(0, -1, 0, R.string.sc_cat3);
			out3 += getSaveLine(R.string.sc_msg12, getIndexIntEntryByIndex(IntEntry.KeyStrokeStartColor), 16, 0);
			out3 += getSaveLine(R.string.sc_msg13, getIndexIntEntryByIndex(IntEntry.KeyStrokeEndColor), 16, 0);
			out3 += getSaveLine(R.string.sc_msg14, getIndexIntEntryByIndex(IntEntry.KeyPressedStrokeStartColor), 16, 0);
			out3 += getSaveLine(R.string.sc_msg15, getIndexIntEntryByIndex(IntEntry.KeyPressedStrokeEndColor), 16, 0);
			fout.write(out3);
			out3 = getSaveLine(0, -1, 0, R.string.sc_cat4);
			out3 += getSaveLine(R.string.sc_msg16, getIndexIntEntryByIndex(IntEntry.KeyboardBackgroundStartColor), 16,
					0);
			out3 += getSaveLine(R.string.sc_msg17, getIndexIntEntryByIndex(IntEntry.KeyboardBackgroundEndColor), 16, 0);
			out3 += getSaveLine(R.string.sc_msg18, getIndexIntEntryByIndex(IntEntry.KeyboardBackgroundGradientType), 10,
					0);
			fout.write(out3);
			out3 = getSaveLine(0, -1, 0, R.string.sc_cat5);
			out3 += getSaveLine(R.string.sc_msg19, getIndexIntEntryByIndex(IntEntry.SpecKeyTextColor), 16, 0);
			out3 += getSaveLine(R.string.sc_msg20, getIndexIntEntryByIndex(IntEntry.SpecKeySymbolColor), 16, 0);
			out3 += getSaveLine(R.string.sc_msg21, getIndexIntEntryByIndex(IntEntry.SpecKeyTextPressedColor), 16, 0);
			out3 += getSaveLine(R.string.sc_msg22, getIndexIntEntryByIndex(IntEntry.SpecKeySymbolPressedColor), 16, 0);
			out3 += getSaveLine(R.string.sc_msg23, getIndexIntEntryByIndex(IntEntry.SpecKeyBackStartColor), 16, 0);
			out3 += getSaveLine(R.string.sc_msg24, getIndexIntEntryByIndex(IntEntry.SpecKeyBackEndColor), 16, 0);
			out3 += getSaveLine(R.string.sc_msg25, getIndexIntEntryByIndex(IntEntry.SpecKeyStrokeStartColor), 16, 0);
			out3 += getSaveLine(R.string.sc_msg26, getIndexIntEntryByIndex(IntEntry.SpecKeyStrokeEndColor), 16, 0);
			out3 += getSaveLine(R.string.sc_msg27, getIndexIntEntryByIndex(IntEntry.SpecKeyBackPressedStartColor), 16,
					0);
			out3 += getSaveLine(R.string.sc_msg28, getIndexIntEntryByIndex(IntEntry.SpecKeyBackPressedEndColor), 16, 0);
			out3 += getSaveLine(R.string.sc_msg29, getIndexIntEntryByIndex(IntEntry.SpecKeyBackPressedGradientType), 10,
					0);
			out3 += getSaveLine(R.string.sc_msg14, getIndexIntEntryByIndex(IntEntry.SpecKeyPressedStrokeStartColor), 16,
					0);
			out3 += getSaveLine(R.string.sc_msg15, getIndexIntEntryByIndex(IntEntry.SpecKeyPressedStrokeEndColor), 16,
					0);
			fout.write(out3);
			out3 = getSaveLine(0, -1, 0, R.string.sc_cat6);
			out3 += getSaveLine(R.string.sc_msg11, getIndexIntEntryByIndex(IntEntry.KeyGapSize), 10, 0);
			out3 += getSaveLine(R.string.sc_msg30, getIndexIntEntryByIndex(IntEntry.KeyBackCornerX), 10, 0);
			out3 += getSaveLine(R.string.sc_msg31, getIndexIntEntryByIndex(IntEntry.KeyBackCornerY), 10, 0);
			fout.write(out3);
			fout.flush();
			fout.close();
			fl_changed = false;
		} catch (Throwable e) {
			st.toast(inst, "error save");
		}

	}

	public void createEditLayout() {
		if (upd != null)
			upd.setVisibility(View.VISIBLE);
		if (help != null)
			help.setVisibility(View.VISIBLE);
		createEdit(0, -1, 0, R.string.sc_cat1);
		createEdit(R.string.sc_msg1, getIndexIntEntryByIndex(IntEntry.KeyBackStartColor), 16, 0);
		createEdit(R.string.sc_msg2, getIndexIntEntryByIndex(IntEntry.KeyBackEndColor), 16, 0);
		createEdit(R.string.sc_msg3, getIndexIntEntryByIndex(IntEntry.KeyBackPressedGradientType), 10, 0);
		createEdit(R.string.sc_msg4, getIndexIntEntryByIndex(IntEntry.KeyBackPressedStartColor), 16, 0);
		createEdit(R.string.sc_msg5, getIndexIntEntryByIndex(IntEntry.KeyBackPressedEndColor), 16, 0);
		createEdit(R.string.sc_msg32, getIndexIntEntryByIndex(IntEntry.KeyBackGradientType), 10, 0);

		createEdit(0, -1, 0, R.string.sc_cat2);
		createEdit(R.string.sc_msg6, getIndexIntEntryByIndex(IntEntry.KeyTextColor), 16, 0);
		createEdit(R.string.sc_msg7, getIndexIntEntryByIndex(IntEntry.KeyTextBold), 10, 0);
		createEdit(R.string.sc_msg8, getIndexIntEntryByIndex(IntEntry.KeySymbolColor), 16, 0);
		createEdit(R.string.sc_msg9, getIndexIntEntryByIndex(IntEntry.KeyTextPressedColor), 16, 0);
		createEdit(R.string.sc_msg10, getIndexIntEntryByIndex(IntEntry.KeySymbolPressedColor), 16, 0);

		createEdit(0, -1, 0, R.string.sc_cat3);
		createEdit(R.string.sc_msg12, getIndexIntEntryByIndex(IntEntry.KeyStrokeStartColor), 16, 0);
		createEdit(R.string.sc_msg13, getIndexIntEntryByIndex(IntEntry.KeyStrokeEndColor), 16, 0);
		createEdit(R.string.sc_msg14, getIndexIntEntryByIndex(IntEntry.KeyPressedStrokeStartColor), 16, 0);
		createEdit(R.string.sc_msg15, getIndexIntEntryByIndex(IntEntry.KeyPressedStrokeEndColor), 16, 0);

		createEdit(0, -1, 0, R.string.sc_cat4);
		createEdit(R.string.sc_msg16, getIndexIntEntryByIndex(IntEntry.KeyboardBackgroundStartColor), 16, 0);
		createEdit(R.string.sc_msg17, getIndexIntEntryByIndex(IntEntry.KeyboardBackgroundEndColor), 16, 0);
		createEdit(R.string.sc_msg18, getIndexIntEntryByIndex(IntEntry.KeyboardBackgroundGradientType), 10, 0);

		createEdit(0, -1, 0, R.string.sc_cat5);
		createEdit(R.string.sc_msg19, getIndexIntEntryByIndex(IntEntry.SpecKeyTextColor), 16, 0);
		createEdit(R.string.sc_msg20, getIndexIntEntryByIndex(IntEntry.SpecKeySymbolColor), 16, 0);
		createEdit(R.string.sc_msg21, getIndexIntEntryByIndex(IntEntry.SpecKeyTextPressedColor), 16, 0);
		createEdit(R.string.sc_msg22, getIndexIntEntryByIndex(IntEntry.SpecKeySymbolPressedColor), 16, 0);
		createEdit(R.string.sc_msg23, getIndexIntEntryByIndex(IntEntry.SpecKeyBackStartColor), 16, 0);
		createEdit(R.string.sc_msg24, getIndexIntEntryByIndex(IntEntry.SpecKeyBackEndColor), 16, 0);
		createEdit(R.string.sc_msg25, getIndexIntEntryByIndex(IntEntry.SpecKeyStrokeStartColor), 16, 0);
		createEdit(R.string.sc_msg26, getIndexIntEntryByIndex(IntEntry.SpecKeyStrokeEndColor), 16, 0);
		createEdit(R.string.sc_msg27, getIndexIntEntryByIndex(IntEntry.SpecKeyBackPressedStartColor), 16, 0);
		createEdit(R.string.sc_msg28, getIndexIntEntryByIndex(IntEntry.SpecKeyBackPressedEndColor), 16, 0);
		createEdit(R.string.sc_msg29, getIndexIntEntryByIndex(IntEntry.SpecKeyBackPressedGradientType), 10, 0);
		createEdit(R.string.sc_msg14, getIndexIntEntryByIndex(IntEntry.SpecKeyPressedStrokeStartColor), 16, 0);
		createEdit(R.string.sc_msg15, getIndexIntEntryByIndex(IntEntry.SpecKeyPressedStrokeEndColor), 16, 0);

		createEdit(0, -1, 0, R.string.sc_cat6);
		createEdit(R.string.sc_msg11, getIndexIntEntryByIndex(IntEntry.KeyGapSize), 10, 0);
		createEdit(R.string.sc_msg30, getIndexIntEntryByIndex(IntEntry.KeyBackCornerX), 10, 0);
		createEdit(R.string.sc_msg31, getIndexIntEntryByIndex(IntEntry.KeyBackCornerY), 10, 0);
	}

	// создаём пару описание-ввод цвета
	// тут же можно создать категорию
	public void createEdit(int id_tv_str, int arIndex, int radix, int id_res_category) {
		// создаём имя категории
		// если id_res_category не равно 0, значит создаём, иначе пропускаем
		if (id_res_category != 0) {
			TextView cn = new TextView(this);
			cn.setTextColor(Color.GREEN);
			cn.setTextSize(20);
			cn.setText(st.STR_LF + this.getString(id_res_category));
			if (llskin == null)
				VisibleEditSkin();
			llskin.addView(cn);
			return;
		}
		TextView tv = new TextView(this);
		tv.setTextColor(Color.WHITE);
		tv.setTextSize(14);
		tv.setText(id_tv_str);
		llskin.addView(tv);
		EditText et = new EditText(this);
		// et.setBackgroundColor(Color.WHITE);
		et.setTextSize(18);
		et.setText(st.STR_ZERO);
		if (arIndex > -1) {
			IntEntry ie = getIntEntry(getIndexIntEntryByKey(st.arDesignNames[arIndex]));
			// if (arval.get(arIndex).resId_et >-1){
			if (ie != null) {
				et.setId(ie.resId_et);
				et.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
				et.setKeyListener(DigitsKeyListener.getInstance(st.STR_16INPUT_DIGIT));
				et.setOnKeyListener(number_keyListener);
				if (radix == 16)
					et.setText(String.format(st.STR_16FORMAT, ie.value));
				else
					et.setText(String.valueOf(ie.value));
				et.addTextChangedListener(tw);
			}
		}
		if (radix == 16) {
			RelativeLayout rl = new RelativeLayout(this);
			rl.setId(et.getId() + 1000);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			Button btn = new Button(this);
			btn.setText(R.string.selection);
			btn.setId(et.getId() + ID_BTN);
			btn.setOnClickListener(m_clkListener);
			btn.setLayoutParams(lp);
			rl.addView(btn);

			RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			lp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			lp1.addRule(RelativeLayout.LEFT_OF, btn.getId());
			et.setLayoutParams(lp1);
			rl.addView(et);

			llskin.addView(rl);
		} else
			llskin.addView(et);
	}

	View.OnClickListener m_clkListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			onClickButton(v.getId());
		}
	};

	public void onClickButton(int id) {
		ColorPicker m_colpic = null;
		m_colpic = (ColorPicker) getLayoutInflater().inflate(R.layout.picker, null);
		if (m_colpic != null) {
			EditText et = null;
			et = (EditText) findViewById(id - ID_BTN);
    		if (et!=null)
    			m_colpic.show(inst, et);
		}

	}

	String getSaveLine(int id_text, int arIndex, int radix, int id_res_category) {
		// создаём имя категории
		// если id_res_category не равно 0, значит создаём, иначе пропускаем
		String out2 = st.STR_NULL;
		if (id_res_category != 0) {
			out2 += st.STR_LF + "//----------\n";
			out2 += COMM + getString(id_res_category) + st.STR_LF;
			out2 += "//----------\n";
			return out2;
		}
		out2 += COMM + getString(id_text) + st.STR_LF;
		int cnt = llskin.getChildCount();
		int ind = -1;
		String key = st.STR_NULL;
		String val = st.STR_NULL;
		EditText ettmp = null;
		int id = -1;
		boolean fl = false;
		int child_rlcnt = -1;
		for (int i = 0; i < cnt; i++) {
			id = llskin.getChildAt(i).getId();
			if (id < 0)
				continue;
			else if (id >= 1000) {
				RelativeLayout rl = (RelativeLayout) llskin.getChildAt(i);
				child_rlcnt = rl.getChildCount();
				for (int i1 = 0; i1 < rl.getChildCount(); i1++) {
					int aaa = rl.getChildAt(i1).getId();
					int bbb = arval.get(arIndex).index;
					if (rl.getChildAt(i1).getId() != arval.get(arIndex).resId_et)
						continue;
					ettmp = (EditText) rl.getChildAt(i1);
					key = st.STR_NULL + st.arDesignNames[arIndex] + "=";
					val = ettmp.getText().toString().trim();
					fl = true;
					break;
				}
			} else {
				if (id - 1 == arval.get(arIndex).index) {
					ettmp = (EditText) llskin.getChildAt(i);
					key = st.STR_NULL + st.arDesignNames[arIndex] + "=";
					val = ettmp.getText().toString().trim();
					break;
				}
			}
			// if (fl)
			// break;
		}
		if (radix == 16 && !st.checkHexValue(val, radix)) {
			val = String.format(st.STR_16FORMAT, arval.get(arIndex).defvalue);
			if (ettmp != null) {
				ettmp.setBackgroundColor(Color.RED);

			}
		}
		if (val.length() > 0) {
			if (radix == 16) {
				out2 += key + val;
			} else {
				out2 += key + Integer.valueOf(val);
			}
		}
		out2 += st.STR_LF + st.STR_LF;

		return out2;
	}

	public static int getValueIntEntryByIndex(int key) {
		for (IntEntry ie : arval) {
			if (ie.index == key) {
				return ie.value;
			}
		}
		return -1;
	}

	public static int getIndexIntEntryByIndex(int key) {
		int iii = 0;
		for (IntEntry ie : arval) {
			if (ie.index == key) {
				return ie.index;
			}
			iii++;
		}
		return -1;
	}

	public static IntEntry getIntEntry(int index) {
		for (IntEntry ie : arval) {
			if (ie.index == index) {
				return ie;
			}
		}
		return null;
	}

	public static int getIndexIntEntryByKey(String s) {
		for (int i = 0; i <= st.arDesignNames.length; i++) {
			if (st.arDesignNames[i].equals(s)) {
				for (IntEntry ie : arval) {
					if (ie.index == i) {
						return ie.index;
					}

				}
			}
		}
		return -1;
	}

	@Override
	public void onBackPressed() {
		if (GlobDialog.gbshow) {
			GlobDialog.inst.finish();
			return;
		}
		if (ColorPicker.inst != null) {
			ColorPicker.inst.finish();
			return;
		}

		if (fl_changed) {
			GlobDialog gd = new GlobDialog(st.c());
			gd.set(R.string.data_changed, R.string.yes, R.string.no);
			gd.setObserver(new st.UniObserver() {
				@Override
				public int OnObserver(Object param1, Object param2) {
					if (((Integer) param1).intValue() == AlertDialog.BUTTON_POSITIVE) {
						st.toast("save");
						save();
						finish();
					}
					return 0;
				}
			});
			gd.showAlert();
		} else
			super.onBackPressed();
		finish();

	};

	View.OnKeyListener number_keyListener = new View.OnKeyListener() {
		@SuppressLint("NewApi")
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				if (!st.isHoneycomb() && event.isCtrlPressed() && keyCode == KeyEvent.KEYCODE_A) {

					EditText et = (EditText) v;
					if (et != null) {
						et.selectAll();
						return true;
					}
				}
			} else {
				v.setBackgroundColor(Color.WHITE);
			}
			return false;
		}
	};

}