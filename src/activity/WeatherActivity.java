package activity;

import service.AutoUpdateService;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.LogUtil;
import util.Utility;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coolweather.app.R;

public class WeatherActivity extends Activity implements OnClickListener {
	/**
	 * cityNameText ������ʾ������ , publishText ������ʾ����ʱ�� , weatherDespText ������ʾ����������Ϣ
	 * temp1Text temp2Text ������ʾ����1 ����2, currentDateText ������ʾ��ǰ���� switchCity
	 * �л����а�ť , refreshWeather ����������ť
	 */
	private LinearLayout weatherInfoLayout;
	private TextView cityNameText;
	private TextView weatherDespText;
	private TextView publishText;
	private TextView temp1Text;
	private TextView temp2Text;
	private TextView currentDateText;
	private Button switchCity;
	private Button refreshWeather;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		// ��ʼ���ؼ�
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		switchCity = (Button) findViewById(R.id.switch_city);
		refreshWeather = (Button) findViewById(R.id.refresh_weather);

		String countyCode = getIntent().getStringExtra("county_code");
		if (!TextUtils.isEmpty(countyCode)) {
			// ���ؼ����ž�ȥ��ѯ����
			publishText.setText("ͬ����...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		} else {
			// û���ؼ�����ʱ��ֱ����ʾ��������
			showWeather();
		}
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.switch_city:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishText.setText("ͬ����...");
			SharedPreferences pref = PreferenceManager
					.getDefaultSharedPreferences(this);
			String weatherCode = pref.getString("weather_code", "");
			if (!TextUtils.isEmpty(weatherCode)) {
				queryWeatherInfo(weatherCode);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * ��ѯ�ؼ���������Ӧ���������š�
	 */
	private void queryWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn/data/list3/city"
				+ countyCode + ".xml";
		LogUtil.d("WeatherActivity", "�Ѿ��õ��ؼ�����");
		queryFromServer(address, "countyCode");
	}

	/**
	 * ��ѯ������������Ӧ��������
	 */
	private void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/"
				+ weatherCode + ".html";
		LogUtil.d("WeatherActivity", "�Ѿ��õ���������");
		queryFromServer(address, "weatherCode");

	}

	/**
	 * ���ݴ���ĵ�ַ������ȥ���������ѯ�������Ż���������Ϣ��
	 */
	private void queryFromServer(final String address, final String type) {
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(final String response) {
				if ("countyCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						// �ӷ��������ص������н�������������
						String[] a = response.split("\\|");
						if (a != null && a.length == 2) {
							String weatherCode = a[1];
							queryWeatherInfo(weatherCode);
							// LogUtil.d("WeatherActivity", weatherCode+"**");
						}
						LogUtil.d("WeatherActivity", "��ȡ�����������ص�����");
					}
				} else if ("weatherCode".equals(type)) {
					// ������������ص�������Ϣ
					Utility.handleWeatherResponse(WeatherActivity.this,
							response);
					runOnUiThread(new Runnable() {
						public void run() {
							showWeather();
						}
					});
				}
			}

			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					public void run() {
						publishText.setText("ͬ��ʧ��");
					}
				});
			}
		});
	}

	/**
	 * ��SharedPreferences�ļ��ж�ȡ�洢��������Ϣ������ʾ�������ϡ�
	 */
	private void showWeather() {

		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		cityNameText.setText(pref.getString("city_name", ""));
		temp1Text.setText(pref.getString("temp1", ""));
		temp2Text.setText(pref.getString("temp2", ""));
		weatherDespText.setText(pref.getString("weather_desp", ""));
		publishText.setText("����" + pref.getString("publish_time", "") + "����");
		currentDateText.setText(pref.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);

		Intent intent = new Intent(this, AutoUpdateService.class);
		startService(intent);

	}

}
