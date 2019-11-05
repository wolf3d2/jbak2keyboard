//package com.jbak2.receiver;
//
//import android.content.BroadcastReceiver;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//
//public class JbKbdReceiver extends BroadcastReceiver
//{
//    @Override
//    public void onReceive(Context c, Intent in)
//    {
//        if(Intent.ACTION_BOOT_COMPLETED.equals(in.getAction()))
//        {
//            c.startService(new Intent(c,ClipbrdService.class));
//        }
//    }
//    static Intent getClipbrdServiceIntent(Context c)
//    {
//        ComponentName cn = new ComponentName(JbKbdReceiver.class.getPackage().getName(), ClipbrdService.class.getName());
//        Intent in = new Intent().setComponent(cn);
//        return in;
//    }
//}
