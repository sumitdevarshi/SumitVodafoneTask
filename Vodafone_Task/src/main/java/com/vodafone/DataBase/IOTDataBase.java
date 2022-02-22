package com.vodafone.DataBase;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.vodafone.Constant.IOTConstant;
import com.vodafone.Exception.IOTException;
import com.vodafone.Model.DeviceDetails;


@Repository
public class IOTDataBase {
	ReadWriteLock rwLock = new ReentrantReadWriteLock();
	Lock readLock = rwLock.readLock();
	Lock writeLock = rwLock.writeLock();

	// I am storing device information based on its productId
	// it will help us to retrieve the record very quickly
	private Map<String, List<DeviceDetails>> deviceDetails = null;

	public int add(List<DeviceDetails> deviceDetailList) {
		
		int status = -1;
		
		if(deviceDetailList.isEmpty()) {
			return status;
		}
		//ReentrantReadWriteLock - write lock will stop all in coming read operation (read thread)
		//but wait for all the read thread already acquired the read lock and
		//allow them to complete their read operation first before modifying the shared resource
		writeLock.lock();
		
		try {
			
			deviceDetails = deviceDetailList.parallelStream().collect(Collectors.groupingBy(DeviceDetails::getProductId));
			status = 1;
			
		}catch(Throwable t) {
			
			status = -1;
			
		}finally{
			
			writeLock.unlock();
		}	
		
		return status;
	}
	
	
	public List<DeviceDetails> get(String productId) throws IOTException{
		
		//ReentrantReadWriteLock - read lock allow parallel access to all read operation
		//on a shared resource but will lock all read operation when write lock is locked
		readLock.lock();
		
		if(deviceDetails == null) {
			
			readLock.unlock();// we must unlock before throwing exception to avoid deadlock
			
			throw new IOTException(IOTConstant.ERROR_DB_EMPTY);
		}
		
		readLock.unlock();
		
		return deviceDetails.get(productId);
	}


}
