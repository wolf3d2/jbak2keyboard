package com.jbak2.perm;

import java.util.ArrayList;

import com.jbak2.Dialog.Dlg;
import com.jbak2.JbakKeyboard.R;
import com.jbak2.JbakKeyboard.st;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

public class Perm {

	public static String PERM_READ_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE; 
	public static String PERM_WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE; 
	public static String PERM_SYSTEM_ALERT = Manifest.permission.SYSTEM_ALERT_WINDOW; 

	public static int RPC = 111; 
	public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;

	@TargetApi(Build.VERSION_CODES.M)
	public static boolean requestPermission(Activity act, String[] perms, int rpc) {
		int cp = -500;
		ArrayList<String> al = new ArrayList<String>(perms.length);
		for (int i = 0; i < perms.length; i++) {
			try {
				cp = act.checkPermission(perms[i], android.os.Process.myPid(), android.os.Process.myUid());
				if (cp != PackageManager.PERMISSION_GRANTED)
					al.add(perms[i]);
			} catch (Throwable e) {
			}
		}
		if (al.size() > 0) {
			String[] aaa = new String[al.size()];
			int pos = 0;
			for (int i = 0; i < al.size(); i++) {
				aaa[pos] = al.get(i).toString();
				pos++;
			}
			act.requestPermissions(aaa, rpc);
			return false;
		}
		return true;
	}

	@TargetApi(Build.VERSION_CODES.M)
	public static boolean checkPermission(final Context act) {
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M)
			return true;
		String[] ss = getPermissionStartArray();
		int res = -1;
		for (int i=0;i<ss.length;i++) {
			res = act.checkCallingOrSelfPermission(ss[i]); 
			if (res!=PackageManager.PERMISSION_GRANTED)
				if (ss[i].contains(PERM_SYSTEM_ALERT)) {
					if (Settings.canDrawOverlays(act)) {
						continue;
					}
				return false;
				} else
					return false;
		}
		return true;
	}
	@TargetApi(Build.VERSION_CODES.M)
	public static void postRequestPermission(final Activity act, String[] perm) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (perm==null)
				return;
			for (int i=0;i< perm.length;i++) {
				if (perm[i].contains(Perm.PERM_SYSTEM_ALERT)
						&&!Settings.canDrawOverlays(act)) {
						Dlg.helpDialog(act, act.getString(R.string.perm_expl2), new st.UniObserver() {
							
							@Override
							public int OnObserver(Object param1, Object param2) {
								Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
										Uri.parse("package:" + act.getPackageName()));
								act.startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
								//System.exit(0);
								return 0;
							}
						});
					}
//				else if (perm[i].contains(Perm.PERM_READ_STORAGE)){
//					requestPermission(act, new String[] {Perm.PERM_READ_STORAGE}, Perm.RPC);
//				}
//				else if (perm[i].contains(Perm.PERM_WRITE_STORAGE)){
//					requestPermission(act, new String[] {Perm.PERM_WRITE_STORAGE}, Perm.RPC);
//				}
			}
		}
	}
	/** необходимые программе разрешения 
	 * (также не забывать указываать их в манифесте) */
	public static String[] getPermissionStartArray() {
		return new String[] {
				Perm.PERM_READ_STORAGE,
				Perm.PERM_WRITE_STORAGE,
				Perm.PERM_SYSTEM_ALERT
		};
	}

}