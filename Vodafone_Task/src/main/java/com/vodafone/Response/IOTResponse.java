package com.vodafone.Response;

import lombok.Data;

@Data
public class IOTResponse {
	String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	

}
