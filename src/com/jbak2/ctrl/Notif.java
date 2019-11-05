package com.jbak2.ctrl;

import com.jbak2.JbakKeyboard.JbKbdPreference;
import com.jbak2.JbakKeyboard.R;
import com.jbak2.JbakKeyboard.ServiceJbKbd;
import com.jbak2.receiver.NotifReceiver;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;

// уведомление в шторке
public class Notif {
	public static final String ACTION_CHANNEL_ID = "com.jbak2.JbakKeyboard.CHANNEL_JBAK2KEYBOARD";
	public static final String ANDROID_CHANNEL_NAME = "JBAK2KEYBOARD CHANNEL";
	public static final String ACTION_SHOW = "com.jbak2.JbakKeyboard.SHOW_KEYBOARD";
	public static ServiceJbKbd mact;
	final public static int NOTIFY_ALL = 0;
	final public static int NOTIFY_ID = 1;
	NotificationManager notificationManager = null;
	NotifReceiver recv = null;

	public Notif(ServiceJbKbd act) {
		mact = act;
		notificationManager = (NotificationManager) mact.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	@SuppressLint("NewApi")
	public void createNotif() {

		recv = new NotifReceiver(mact);
		final IntentFilter pFilter = new IntentFilter(ACTION_SHOW);
		mact.registerReceiver(recv, pFilter);

		Intent settingIntent = new Intent(mact, JbKbdPreference.class);
		PendingIntent settingPendingIntent = PendingIntent.getActivity(mact, 0, settingIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		Intent notificationIntent = new Intent(ACTION_SHOW);
		PendingIntent contentIntent = PendingIntent.getBroadcast(mact.getApplicationContext(), 1, notificationIntent,
				0);
		// PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
		// notificationIntent, 0);
		Resources res = mact.getResources();
		CharSequence text = res.getString(R.string.ime_name);
		long when = System.currentTimeMillis();

		Notification notification= null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

			NotificationChannel channel = new NotificationChannel(ACTION_CHANNEL_ID, ANDROID_CHANNEL_NAME,
					NotificationManager.IMPORTANCE_HIGH);
			channel.setDescription(mact.getString(R.string.ime_name));
			channel.enableLights(true);
			channel.setLightColor(Color.WHITE);
			channel.enableVibration(false);
			notificationManager.createNotificationChannel(channel);
			notification = new Notification.Builder(mact.getApplicationContext())
					.setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.icon)).setAutoCancel(false) 
					.setTicker(text).setContentTitle(mact.getString(R.string.set_show_kbd_notif_title))
					.setContentText(mact.getString(R.string.set_show_kbd_notif_body)).setWhen(when)
					.setSmallIcon(R.drawable.sym_keyboard_done).setContentIntent(contentIntent)
					.setChannelId(ACTION_CHANNEL_ID)
					// .addAction(R.drawable.icon, "show kbd", showPendingIntent)
					.addAction(R.drawable.icon, res.getString(R.string.ime_settings), settingPendingIntent)

					// должен быть последней строкой билдера
					.getNotification();
		} else {
			notification = new Notification.Builder(mact.getApplicationContext())
					.setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.icon)).setAutoCancel(false) 
					.setTicker(text).setContentTitle(mact.getString(R.string.set_show_kbd_notif_title))
					.setContentText(mact.getString(R.string.set_show_kbd_notif_body)).setWhen(when)
					.setSmallIcon(R.drawable.sym_keyboard_done).setContentIntent(contentIntent)
					// .addAction(R.drawable.icon, "show kbd", showPendingIntent)
					.addAction(R.drawable.icon, res.getString(R.string.ime_settings), settingPendingIntent)

					// должен быть последней строкой билдера
					.getNotification();
		}

		notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;

		notificationManager.notify(NOTIFY_ID, notification);

		// Intent settingIntent = new Intent(mact, JbKbdPreference.class);
		// PendingIntent settingPendingIntent = PendingIntent.getActivity(mact,
		// 0, settingIntent,
		// PendingIntent.FLAG_CANCEL_CURRENT);
		//
		// Intent showIntent = new Intent(mact, ServiceJbKbd.class);
		// PendingIntent showPendingIntent = PendingIntent.getActivity(mact,
		// 0, showIntent,
		// PendingIntent.FLAG_CANCEL_CURRENT);
		//
		// Intent mainIntent = new Intent();
		// mainIntent.setAction(ACTION_SHOW);
		// mainIntent.putExtra("com.jbak2.JbakKeyboard.broadcast.Message", "bbb");
		// mainIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
		// mact.sendBroadcast(mainIntent);
		// PendingIntent contentIntent = PendingIntent.getActivity(mact,
		// 0, mainIntent,
		// PendingIntent.FLAG_CANCEL_CURRENT);
		//
		// Resources res = mact.getResources();
		//
		// // до версии Android 8.0 API 26
		// Notification.Builder builder = new Notification.Builder(mact);
		//
		// builder.setContentIntent(contentIntent)
		// // обязательные настройки
		// .setSmallIcon(R.drawable.icon)
		// //.setContentTitle(res.getString(R.string.notifytitle)) // Заголовок
		// уведомления
		// .setContentTitle(res.getString(R.string.ime_name))
		// //.setContentText(res.getString(R.string.notifytext))
		// .setContentText("Show keyboard") // Текст уведомления
		// // необязательные настройки
		// // большая картинка
		// .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.icon))
		// // текст в строке состояния
		// .setTicker(res.getString(R.string.ime_name))
		// //.setTicker("Последнее китайское предупреждение!")
		// .setWhen(System.currentTimeMillis())
		// .addAction(R.drawable.save, res.getString(R.string.ime_settings),
		// settingPendingIntent)
		// .addAction(R.drawable.icon, "show kbd",
		// showPendingIntent)
		//
		// //.setAutoCancel(true);
		// .setAutoCancel(false); // автоматически закрыть уведомление после нажатия

	}

	public void dismiss(int id) {
		if (recv != null)
			mact.unregisterReceiver(recv);

		if (notificationManager == null)
			return;
		// если id = 0, то удаляем все уведомления программы
		switch (id) {
		case NOTIFY_ALL:
			notificationManager.cancelAll();
			break;
		case NOTIFY_ID:
			notificationManager.cancel(NOTIFY_ID);
			break;
		}
		mact = null;
	}
}
