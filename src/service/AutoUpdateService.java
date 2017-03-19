package service;

import receiver.AutoUpdateReceiver;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.LogUtil;
import util.Utility;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class AutoUpdateService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(new Runnable() {
			public void run() {
				updateWeather();
			}
		}).start();
		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int time = 1000 * 60 * 60 ; // 一小时的毫秒数
		long triggerAtTime = SystemClock.elapsedRealtime() + time;
		Intent intent2 = new Intent(this, AutoUpdateReceiver.class);
		PendingIntent PI = PendingIntent.getBroadcast(this, 0, intent2, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, PI);

		return super.onStartCommand(intent, flags, startId);
	}

	/** 
	 * 更新天气信息。
	 */
	private void updateWeather() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		String weatherCode = pref.getString("weather_code", "");
		String address = "http://www.weather.com.cn/data/cityinfo/"
				+ weatherCode + ".html";
		LogUtil.d("AutoUpdateReceiver", "服务启动成功,准备更新数据");
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				Utility.handleWeatherResponse(AutoUpdateService.this, response);
			}

			@Override
			public void onError(Exception e) {
				e.printStackTrace();
			}
		});

	}

}
