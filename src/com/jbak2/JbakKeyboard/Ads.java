package com.jbak2.JbakKeyboard;

//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdSize;
//import com.google.android.gms.ads.AdView;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

// класс управления рекламой
final class Ads {
	// мобила
	public static String ID_DEVICE = "591798f9";
	// public static AdView m_ads= null;
	public static int count_failed_load = 0;
	public static LinearLayout llad = null;

//	 для восстановления рекламы, это удалить и раскоментить закоменченное
//	 и не забыть вернуть в манифест пермишены:
//	<uses-permission android:name="android.permission.INTERNET"/>
//	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>

	public static void show(Context c, final int idAct) {

	}

	public static void destroy() {
	}

	public static void pause() {
	}

	public static void resume() {

	}

}

	// public static void createAds(Context c){
	// m_ads = new AdView(c);
	// m_ads.setAdSize(AdSize.BANNER);
	// LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
	// LinearLayout.LayoutParams.MATCH_PARENT,
	// LinearLayout.LayoutParams.WRAP_CONTENT
	// );
	// lp.gravity = Gravity.CENTER_HORIZONTAL;
	// m_ads.setLayoutParams(lp);
	// m_ads.setAdUnitId("ca-app-pub-6058046135647426/5798684192");
	// m_ads.setAdListener(new AdListener(){
	// @Override
	// public void onAdLoaded()
	// {
	// count_failed_load = 0;
	// llad.setVisibility(View.VISIBLE);
	// m_ads.setVisibility(View.VISIBLE);
	//// st.toast("load ads");
	// }
	// @Override
	// public void onAdClosed()
	// {
	// m_ads.resume();
	// }
	// @Override
	// public void onAdLeftApplication ()
	// {
	// m_ads.pause();
	// }
	// @Override
	// public void onAdFailedToLoad(int errorCode)
	// {
	//
	//// st.toast("failed ads ("+count_failed_load+")");
	//
	// if (count_failed_load>30)
	// return;
	// loadAds(m_ads);
	// count_failed_load++;
	// }
	// });
	//
	// m_ads.setVisibility(View.GONE);
	// loadAds(m_ads);
	// }
	// public static void show(Context c, final int idAct){
	// if (llad!=null){
	// llad.removeView(m_ads);
	// llad=null;
	//// llad.removeAllViews();
	// }
	// llad = (LinearLayout)getLinearLayout(c,idAct);
	// if (m_ads == null){
	// createAds(c);
	// count_failed_load = 0;
	// }
	// llad.addView(m_ads);
	//// llad.setVisibility(View.GONE);
	//
	// }
	// public static void loadAds(AdView ads){
	// if (Build.VERSION.SDK_INT>19
	// &&st.isDebugEmulator()
	// )
	// return;
	// AdRequest adRequest = new AdRequest.Builder()
	// .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
	// .addTestDevice(ID_DEVICE)
	// .build();
	// ads.loadAd(adRequest);
	//// или так
	//// AdRequest.Builder builder = new AdRequest.Builder();
	//// if(BuildConfig.DEBUG) {
	//// builder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
	//// }
	//// ads.loadAd(builder.build());
	//
	// }
	// public static LinearLayout getLinearLayout(Context c, int numAds){
	// LinearLayout ll= null;
	// switch (numAds)
	// {
	// case 1:
	// ll = (LinearLayout) ((Activity) c).findViewById(R.id.llad_pref);
	// break;
	// case 2:
	// ll = (LinearLayout) ((Activity) c).findViewById(R.id.llad_acolact);
	// break;
	// case 3:
	// ll = (LinearLayout) ((Activity) c).findViewById(R.id.llad_desc);
	// break;
	// case 4:
	// ll = (LinearLayout) ((Activity) c).findViewById(R.id.llad_gc);
	// break;
	// case 5:
	// ll = (LinearLayout) ((Activity) c).findViewById(R.id.llad_help);
	// break;
	// case 6:
	// ll = (LinearLayout) ((Activity) c).findViewById(R.id.llad_mm);
	// break;
	// case 7:
	// ll = (LinearLayout) ((Activity) c).findViewById(R.id.llad_runapp);
	// break;
	// case 8:
	// ll = (LinearLayout) ((Activity) c).findViewById(R.id.llad_euv);
	// break;
	// case 9:
	// ll = (LinearLayout) ((Activity) c).findViewById(R.id.llad_ss);
	// break;
	// case 10:
	// ll = (LinearLayout) ((Activity) c).findViewById(R.id.llad_clipsync);
	// break;
	// }
	// return ll;
	// }
	// public static void destroy(){
	//// if (m_ads != null)
	//// m_ads.destroy();
	//// if (llad!=null)
	//// llad.removeAllViews();
	// }
	// public static void pause(){
	//// if (m_ads != null)
	//// m_ads.pause();
	//
	//// loadAds(m_ads);
	// }
	// public static void resume(){
	// if (m_ads != null) {
	// m_ads.resume();
	//// m_ads.setVisibility(View.GONE);
	// loadAds(m_ads);
	// }
	// }
//
//}
