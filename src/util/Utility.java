package util;

import model.City;
import model.CoolWeatherDB;
import model.County;
import model.Province;
import android.text.TextUtils;

public class Utility {

	/**
	 * �����ʹ�����������ص�ʡ������
	 */
	public synchronized static boolean handleProvincesResponse(
			CoolWeatherDB coolWeatherDB, String response) {
		if (!TextUtils.isEmpty(response)) {
			String[] allProvinces = response.split(",");
			if (allProvinces != null && allProvinces.length > 0) {
				for (String ps : allProvinces) {
					String[] a = ps.split("\\|");
					Province province = new Province();
					province.setProvinceCode(a[0]);
					province.setProvinceName(a[1]);
					// ���������������ݴ洢��Province��
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * �����ʹ�����������ص��м�����
	 */
	public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,
			String response, int provinceId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCities = response.split(",");
			if (allCities != null && allCities.length > 0) {
				for (String cs : allCities) {
					String[] a = cs.split("\\|");
					City city = new City();
					city.setCityCode(a[0]);
					city.setCityName(a[1]);
					city.setProvinceId(provinceId);
					// ���������������ݴ洢��City��
					coolWeatherDB.saveCity(city);
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * �����ʹ�����������ص��ؼ�����
	 */
	public static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,
			String response, int cityId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCounties = response.split(",");
			if (allCounties != null && allCounties.length > 0) {
				for (String cos : allCounties) {
					String[] a = cos.split("\\|");
					County county = new County();
					county.setCountyCode(a[0]);
					county.setCountyName(a[1]);
					county.setCityId(cityId);
					// ���������������ݴ洢��County�� 
					coolWeatherDB.saveCounty(county);  
				}
				return true;
			}
		}
		return false;
	}

}
