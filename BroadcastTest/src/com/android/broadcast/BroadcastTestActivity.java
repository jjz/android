package com.android.broadcast;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;

public class BroadcastTestActivity extends Activity {
	NetCheckReceiver netCheckReceiver;
	SdCardReceiver sdCardReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		netCheckReceiver = new NetCheckReceiver();
		IntentFilter mFilter = new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION);

		registerReceiver(netCheckReceiver, mFilter);
		IntentFilter intentFilter = new IntentFilter(
				Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
		intentFilter.addAction(Intent.ACTION_MEDIA_SHARED);
		intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
		intentFilter.addDataScheme("file");
		sdCardReceiver=new SdCardReceiver();
		registerReceiver(sdCardReceiver, intentFilter);
	
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(netCheckReceiver);
		unregisterReceiver(sdCardReceiver);
		super.onDestroy();
	}
}