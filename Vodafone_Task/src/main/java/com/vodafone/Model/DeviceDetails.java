package com.vodafone.Model;

import com.opencsv.bean.CsvBindByName;

import lombok.Data;
@Data
public class DeviceDetails {
	
	@CsvBindByName(column = "DateTime")
	private Long dateTime;
	
	@CsvBindByName(column = "EventId")
	private Long eventId; 
	
	@CsvBindByName(column = "ProductId")
	private String productId; 
	
	@CsvBindByName(column = "Latitude")
	private Double latitude; 
	
	@CsvBindByName(column = "Longitude")
	private Double longitude; 
	
	@CsvBindByName(column = "Battery")
	private Double battery;
	
	@CsvBindByName(column = "Light")
	private String light;
	
	@CsvBindByName(column = "AirplaneMode")
	private String airplaneMode;

	

	
	

}
