//package com.jbak2.ctrl;
//
//import java.io.BufferedInputStream;
//import java.io.IOException;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//
//import com.jbak2.JbakKeyboard.st;
//
//import android.R;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.drawable.Drawable;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.widget.Button;
//import android.widget.ImageView;
///** класс создания маленькой картинки в ImageView в отдельном потоке */
//public class ExtImage {
//	static int MAX_HEIGHT_IMAGE = 50;
//	static Drawable m_img = null;
//	static Handler handler = null;
//	static Thread thread = null;
//	/** Private constructor prevents instantiation from other classes */
//	private ExtImage() {
//	}
//
//	public static void createMiniImageHandlerFromPath(final String fpath, final Button btn) {
//		if (fpath == null || btn == null)
//			return;
////		if (handler==null) {
//			handler = new Handler() {
//				@Override
//				public void handleMessage(Message message) {
//					m_img = (Drawable) message.obj;
//					if (m_img==null) {
//						btn.setCompoundDrawables( null, null, null, null );
//						return;
//					}
//					m_img.setBounds( 0, 0, MAX_HEIGHT_IMAGE, MAX_HEIGHT_IMAGE );
//					btn.setCompoundDrawables( m_img, null, null, null);
//					m_img=null;
//					btn.setCompoundDrawablePadding(2);
//				}
//			};
//			
////		}
////		if (thread==null) {
//			thread = new Thread() {
//				@Override
//				public void run() {
//					Drawable image = readDrawableFromPath(fpath);
//					if (image != null) {
//						final Message message = handler.obtainMessage(1, image);
//						handler.sendMessage(message);
//					}
//				}
//			};
//			//btn.setImageResource(R.drawable.btn_minus);
//			thread.setPriority(3);
//			thread.start();
//			
////		}
//	}
//	public static Drawable readDrawableFromPath(String path) {
//		try {
//			return Drawable.createFromPath(path);
//		} catch (Throwable e) {
//			return null;
//		}
//// чтение картинки из url		
////		Bitmap bitmap = null;
////		HttpURLConnection conn = null;
////		BufferedInputStream buf_stream = null;
////		try {
////			Log.v(TAG, "Starting loading image by URL: " + path);
////			conn = (HttpURLConnection) new URL(path).openConnection();
////			conn.setDoInput(true);
////			conn.setRequestProperty("Connection", "Keep-Alive");
////			conn.connect();
////			buf_stream = new BufferedInputStream(conn.getInputStream(), 8192);
////			bitmap = BitmapFactory.decodeStream(buf_stream);
////			buf_stream.close();
////			conn.disconnect();
////			buf_stream = null;
////			conn = null;
////		} catch (MalformedURLException ex) {
////			Log.e(TAG, "Url parsing was failed: " + path);
////		} catch (IOException ex) {
////			Log.d(TAG, path + " does not exists");
////		} catch (OutOfMemoryError e) {
////			Log.w(TAG, "Out of memory!!!");
////			return null;
////		} finally {
////			if (buf_stream != null)
////				try {
////					buf_stream.close();
////				} catch (IOException ex) {
////				}
////			if (conn != null)
////				conn.disconnect();
////		}
////		return bitmap;
//	}
//	public static void close()
//	{
//		
//	}
//}