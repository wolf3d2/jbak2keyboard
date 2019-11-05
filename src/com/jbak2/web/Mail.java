package com.jbak2.web;

import java.io.File;
import java.io.FileInputStream;
import java.util.Locale;

import com.jbak2.JbakKeyboard.R;
import com.jbak2.JbakKeyboard.st;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class Mail 
{
	public static final String MAIL = "mwsoft@tut.by";

	public static void sendFeedback(Context c) {
		sendFeedback(c, null);
	}
	public static void sendFeedback(Context c,File crash) {
		StringBuilder info = st.getDeviceInfo(c);
		if(crash!=null)
			info.append(c.getString(R.string.crash_desc));
		info.append("===\n");
		if(crash!=null)
		{
			info.append(strFile(crash));
			info.append("===\n");
		}
		final Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setType("text/message");
		emailIntent.putExtra(Intent.EXTRA_EMAIL,
				new String[] {MAIL});

		String subj = null;
		if(crash==null)
			subj = "О "+c.getString(R.string.ime_name);
		else
			subj = "Crash report "+st.getAppNameAndVersion(c);
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, subj);
		emailIntent.putExtra(Intent.EXTRA_TEXT, info.toString());
		c.startActivity(Intent.createChooser(emailIntent, "Crash report "+st.getAppNameAndVersion(c)));
	}
    public static String strFile(File f)
    {
    	String s= null;
		try{
			FileInputStream fin = new FileInputStream(f);
			byte buf[] = new byte[(int) f.length()];
			fin.read(buf);
			fin.close();
			s = new String(buf);
		}
		catch(Throwable e)
		{
		}
		return s;
    }

}