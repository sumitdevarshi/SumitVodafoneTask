package com.vodafone.Repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vodafone.DataBase.IOTDataBase;
import com.vodafone.Exception.IOTException;
import com.vodafone.Model.DeviceDetails;

@Service
public class IOTRepository {
	
	@Autowired
	IOTDataBase db;

	public int saveOrUpdate(List<DeviceDetails> deviceDetailList) {
		// TODO Auto-generated method stub
		return db.add(deviceDetailList);
	}
public List<DeviceDetails> getDeviceList(String productId) throws IOTException {
		
		return db.get(productId);
	}

}
