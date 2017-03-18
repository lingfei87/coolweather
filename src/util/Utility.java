package util;

import model.City;
import model.CoolWeatherDB;
import model.County;
import model.Province;
import android.text.TextUtils;

public class Utility {

	/**
	 * 解析和处理服务器返回的省级数据
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
					// 将解析出来的数据存储到Province表
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 解析和处理服务器返回的市级数据
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
					// 将解析出来的数据存储到City表
					coolWeatherDB.saveCity(city);
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * 解析和处理服务器返回的县级数据
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
					// 将解析出来的数据存储到County表 
					coolWeatherDB.saveCounty(county);  
				}
				return true;
			}
		}
		return false;
	}

}
