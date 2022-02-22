package com.vodafone.Model;

import java.util.HashMap;
import java.util.Map;

public class ProductList {
	
	private static Map<String, String> productList = null;
	//I will load this list from DB or from a config file.
	//The reason for using this list is to avoid hard coding, so that if a new
	//device has been introduced in future, there will not be any impact in code.
	//Also considering that there would be thousand of different products
	static {
		
		productList = new HashMap<String,String>();
		productList.put("WG", "CyclePlusTracker");
		productList.put("69", "GeneralTracker");
	}
		
	
	public static String getProductType(String productId) {
		
		String key = productId.substring(0, 2);
		String deviceType = productList.get(key);
		
		if(deviceType == null || deviceType.isEmpty()) {
			return "Unknown";
		}
		
		return deviceType;
	}



}
