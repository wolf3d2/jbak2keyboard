//package com.jbak2.receiver;
//
//import java.util.Timer;
//
//import com.jbak2.JbakKeyboard.st;
//import com.jbak2.ctrl.SameThreadTimer;
//
//import android.app.Service;
//import android.content.BroadcastReceiver;
//import android.content.ClipData;
//import android.content.ClipboardManager;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//
//
//// СТАРЫЙ (ДО СДК 11) сервис для чекания системного буфера
//// новый находится в ctrl
///** Сервис для забора значений из системного буфера по таймеру */
//public class ClipbrdService extends SameThreadTimer 
//{
//    ClipData.Item m_item=null;
//    CharSequence cm_str = null;
//    String m_sLastClipStr;
///** Интервал взятия значений из буфера обмена в милисекундах */ 
//    public static final int CLIPBRD_INTERVAL = 5000;
//    ClipboardManager m_cm;
//    Timer m_timer;
//	public static ClipbrdService inst;
//	ClipData clip = null;
//	Context m_c = null;
//	
//    public ClipbrdService(Context c) {
//		super(CLIPBRD_INTERVAL, CLIPBRD_INTERVAL);
//        inst = this;
//        m_cm = (ClipboardManager)c.getSystemService(Service.CLIPBOARD_SERVICE);
//        IntentFilter filt = new IntentFilter();
//        filt.addAction(Intent.ACTION_SCREEN_ON);
//        filt.addAction(Intent.ACTION_SCREEN_OFF);
//        c.registerReceiver(m_recv, filt);
//        m_c = c;
//        start();
//	}
//    public void delete(Context c)
//    {
//        inst = null;
//        c.unregisterReceiver(m_recv);
//    }
//    public void checkClipboardString()
//    {
//    	if (m_cm == null)
//    		return;
//// проверяет изменился ли буфер, если нет то возврат
//        if(!m_cm.hasPrimaryClip())
//        {
//            return;
//        }
//        try {
//			clip = m_cm.getPrimaryClip();
//			if (clip==null)
//				return;
//	        if (clip.getItemCount()>0) {
//	        	m_item = clip.getItemAt(0);
//	        	if (m_item==null) 
//	        		return;
//	        	cm_str = m_item.getText();
//	        	if (cm_str==null)
//	        		return;
//	        	checkString(cm_str.toString());
//	        }
//
//		} catch (Throwable e) {
//			return;
//		}
//        	
//    }
//    void checkString(String str)
//    {
//    	if (str == null)
//    		return;
//    	if (str.isEmpty())
//    		return;
//    	cancel();
//        try{
//            if(str.equals(m_sLastClipStr))
//            {
//                return;
//            }
//            st.stor().checkClipboardString(str);
//            m_sLastClipStr = str;
//        }
//        catch(Throwable e)
//        {
//            
//        }
//        start();
//    }
//    BroadcastReceiver m_recv = new BroadcastReceiver()
//    {
//        @Override
//        public void onReceive(Context context, Intent intent)
//        {
//            String act = intent.getAction();
//            if(Intent.ACTION_SCREEN_ON.equals(act))
//            {
//                start();
//            }
//            if(Intent.ACTION_SCREEN_OFF.equals(act))
//            {
//                cancel();
//            }
//        }
//    };
//	@Override
//	public void onTimer(SameThreadTimer timer) {
//		checkClipboardString();
//	}
//}
