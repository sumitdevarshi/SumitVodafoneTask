package com.vodafone.Util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.vodafone.Model.DeviceDetails;


public class IOTUtil {
	public static String convertToDate(Long timeStampMili) {

		String date = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStampMili), ZoneId.systemDefault())
				.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

		return date;
	}

	public static String getStatus(DeviceDetails deviceInfo) {
		if (deviceInfo != null && deviceInfo.getAirplaneMode() != null && deviceInfo.getAirplaneMode().equals("OFF")) {

			return "Active";
		}
		return "Inactive";
	}

	public static String getStatus(List<DeviceDetails> dlist) {
		String status = "Inactive";
		int numberOfCoOrdinates = 0;

		// if average coordinates distance are more then 10 mitre apart then we will
		// consider it active else N/A
		// if zero then inactive. We are also considering that in near future a long
		// list of coordinates are going to be considered. Even if a device is moving in
		// a
		// big circle we will consider it to be moving

		Double distance = 0.0;

		Double longi_1 = 0.0;
		Double lat_1 = 0.0;

		Double longi_2 = 0.0;
		Double lat_2 = 0.0;

		for (DeviceDetails dd : dlist) {

			numberOfCoOrdinates++;

			if (numberOfCoOrdinates > 1) {

				longi_2 = dd.getLongitude();
				lat_2 = dd.getLatitude();

				if (longi_1 == null || lat_1 == null || longi_2 == null || lat_2 == null) {
					return status;
				}

				distance += getDistanceBetween(lat_1, longi_1, dd.getLatitude(), dd.getLongitude());

			}
			// we do not need to verify null
			// if first two coordinates are null this method will not call
			// it will generate a error message as per requirement in Part 2.
			longi_1 = dd.getLongitude();
			lat_1 = dd.getLatitude();
		}

		Double averageDistance = distance / numberOfCoOrdinates;

		if (numberOfCoOrdinates <= 2) {
			return "N/A";//not enough GPS reading
		} else if (averageDistance >= 10) {
			return "Active";
		} else if (averageDistance > 0) {
			return "N/A";
		}
		return status;
	}

	public static String getBatteryStatus(DeviceDetails deviceInfo) {

		if (deviceInfo != null && deviceInfo.getBattery() != null) {

			if (deviceInfo.getBattery() >= .98) {
				return "Full";
			} else if (deviceInfo.getBattery() >= .60) {
				return "High";
			} else if (deviceInfo.getBattery() >= .40) {
				return "Medium";
			} else if (deviceInfo.getBattery() >= .10) {
				return "Low";
			}

		}

		return "Critical";
	}

	/***
	 * @param deviceInfo
	 * @return device location
	 */
	public static String getDescription(DeviceDetails deviceInfo) {

		if (deviceInfo != null && deviceInfo.getAirplaneMode() != null && deviceInfo.getAirplaneMode().equals("OFF")) {

			return "SUCCESS: Location identified.";
		}

		return "SUCCESS: Location not available: Please turn off airplane mode";

	}

	public static String getLatitude(DeviceDetails deviceInfo) {

		if (deviceInfo != null && deviceInfo.getLatitude() != null && deviceInfo.getAirplaneMode().equals("OFF")) {

			return deviceInfo.getLatitude().toString();
		}

		return "";
	}

	public static String getLongitude(DeviceDetails deviceInfo) {

		if (deviceInfo != null && deviceInfo.getLongitude() != null && deviceInfo.getAirplaneMode().equals("OFF")) {

			return deviceInfo.getLongitude().toString();
		}

		return "";

	}

	public static float getDistanceBetween(double lat1, double lng1, double lat2, double lng2) {
		double earthRadius = 6371000; // meters
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		float dist = (float) (earthRadius * c);

		return dist;
	}

	public static boolean isValidDelimiter(Character delimiter) {

//		switch (delimiter) {
//	    case ',','|',':',';' ->{return true;}
//	    default -> {return false; }	        
//	    }
		
		if(delimiter == null) {
			return false;
		}

		switch (delimiter) {
			case ',':
				return true;
			case ';':
				return true;
			case '|':
				return true;
			case ':':
				return true;
			case '\t':
				return true;
			default:
				return false;
		}
	}

	

}
