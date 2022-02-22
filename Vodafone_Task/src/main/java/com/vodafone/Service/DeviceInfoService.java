package com.vodafone.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.vodafone.Constant.IOTConstant;
import com.vodafone.Exception.IOTException;
import com.vodafone.Model.DeviceDetails;
import com.vodafone.Model.ProductList;
import com.vodafone.Repository.IOTRepository;
import com.vodafone.Response.DeviceInfoResponse;
import com.vodafone.Response.IOTResponse;
import com.vodafone.Util.IOTUtil;


@Service
public class DeviceInfoService implements IOTDeviceInfoService {

	
	@Autowired
	IOTRepository iotRepository;
	

	/**
	 * Below method will generate a DeviceInfoResponse if a device found in the
	 * memory/db based on productId and timestamp for that device. If timestamp is not
	 * available, it will return the information of a device record which is immediate
	 * nearest in past i.e. near to timestamp provided by the caller. Appropriate error
	 * response will be send back to caller for any failure
	 * 
	 * @param productId
	 * @param tstmp
	 * @return
	 */
	@Override
	public Optional<?> getDeviceInfo(String productId, Long tstmp) {

		if (tstmp == null) {
			Instant instant = Instant.now();
			tstmp = instant.toEpochMilli();
		}

		List<DeviceDetails> deviceDetailList = null;

		try {
			deviceDetailList = iotRepository.getDeviceList(productId);
		} catch (IOTException e) {
			IOTResponse resp = new IOTResponse();
			resp.setDescription(IOTConstant.ERROR_DB_EMPTY);
			return Optional.of(resp);
		}

		if (deviceDetailList == null) {
			IOTResponse resp = new IOTResponse();
			resp.setDescription("ERROR: Id <" + productId + "> not found");
			return Optional.of(resp);
		}

		if (ProductList.getProductType(productId).equals("CyclePlusTracker")) {

			List<DeviceDetails> devList = getDeviceDetailList(deviceDetailList, tstmp);
			
			return generateIOTResponse(devList);

		}

		Optional<DeviceDetails> deviceDetails = getDeviceDetails(deviceDetailList, tstmp);

		return generateIOTResponse(deviceDetails);

	}

	public Optional<DeviceDetails> getDeviceDetails(List<DeviceDetails> deviceDetailList, Long t_stmp) {

		// if the timestamp provided is not a complete match then it should return the
		// data that is closest to it in the past. If not past timestamp found it will
		// send a error respose to caller
		return Optional.ofNullable(deviceDetailList.parallelStream().filter(dd -> dd.getDateTime() <= t_stmp)
				.min(Comparator.comparingLong(dd -> (Math.abs(dd.getDateTime() - t_stmp)))).orElse(null));
	}

	// this method will return a device information for three consecutive locations
	public List<DeviceDetails> getDeviceDetailList(List<DeviceDetails> deviceDetailList, Long tstmp) {
		// need list of devices in ordered way
		List<DeviceDetails> dList = new LinkedList<DeviceDetails>();
		Optional<DeviceDetails> deviceDetails = null;

		for (int i = 0; i < 3; i++) {
			deviceDetails = getDeviceDetails(deviceDetailList, tstmp);
			if (deviceDetails.isPresent()) {
				dList.add(deviceDetails.get());
				tstmp = deviceDetails.get().getDateTime() - (i + 1);// avoid duplicate
			} else {
				break;
			}
		}

		if (dList.isEmpty()) {
			dList = null;
		}

		return dList;
	}

	/**
	 * Below private method will generate a device detail response if successful
	 * else a appropriate error response in case there is any error.
	 * 
	 * @param deviceDetails
	 * @return
	 */
	private Optional<?> generateIOTResponse(Optional<DeviceDetails> deviceDetails) {

		if (deviceDetails==null) {
			IOTResponse resp = new IOTResponse();
			resp.setDescription(IOTConstant.ERROR_NO_DEVICE_IN_PAST);
			return Optional.of(resp);
		}

		DeviceDetails deviceInfo = deviceDetails.get();

		if (deviceInfo.getAirplaneMode().equals("OFF")
				&& (deviceInfo.getLatitude() == null || deviceInfo.getLongitude() == null)) {
			IOTResponse resp = new IOTResponse();
			resp.setDescription(IOTConstant.ERROR_DEVICE_NOT_LOCATED);
			return Optional.of(resp);

		}

		DeviceInfoResponse deviceInfoResponse = uploadAndGetDeviceInfo(deviceInfo);

		return Optional.of(deviceInfoResponse);

	}

	private Optional<?> generateIOTResponse(List<DeviceDetails> devList) {

		Optional<DeviceDetails> deviceDetails = Optional.empty();

		if (devList == null) {
			IOTResponse resp = new IOTResponse();
			resp.setDescription(IOTConstant.ERROR_NO_DEVICE_IN_PAST);
			return Optional.of(resp);
		}

		deviceDetails = Optional.of(devList.get(0));// get the first reading
		
		DeviceDetails deviceInfo = deviceDetails.get();
		
		//we will consider the latest device information here
		if (deviceInfo.getAirplaneMode().equals("OFF")
				&& (deviceInfo.getLatitude() == null || deviceInfo.getLongitude() == null)) {
			IOTResponse resp = new IOTResponse();
			resp.setDescription(IOTConstant.ERROR_DEVICE_NOT_LOCATED);
			return Optional.of(resp);
		}
		
		DeviceInfoResponse deviceInfoResponse = uploadAndGetDeviceInfo(deviceInfo);
		
		//if latest device's AirplaneMode status is ON we will not going to check its status
		//based on it's past coordinates
		if(deviceInfo.getAirplaneMode().equals("OFF")) {
			String status = IOTUtil.getStatus(devList);
			deviceInfoResponse.setStatus(status);
		}

		return Optional.of(deviceInfoResponse);

	}

	private DeviceInfoResponse uploadAndGetDeviceInfo(DeviceDetails deviceInfo) {

		DeviceInfoResponse deviceInfoResponse = new DeviceInfoResponse();

		deviceInfoResponse.setId(deviceInfo.getProductId());
		deviceInfoResponse.setName(ProductList.getProductType(deviceInfo.getProductId()));
		deviceInfoResponse.setDatetime(IOTUtil.convertToDate(deviceInfo.getDateTime()));
		deviceInfoResponse.setLongitude(IOTUtil.getLongitude(deviceInfo));
		deviceInfoResponse.setLatitude(IOTUtil.getLatitude(deviceInfo));
		deviceInfoResponse.setStatus(IOTUtil.getStatus(deviceInfo));
		deviceInfoResponse.setBattery(IOTUtil.getBatteryStatus(deviceInfo));
		deviceInfoResponse.setDescription(IOTUtil.getDescription(deviceInfo));

		return deviceInfoResponse;
	}

}
