package com.vodafone.Controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vodafone.Constant.IOTConstant;
import com.vodafone.Model.CSVDetail;
import com.vodafone.Response.DeviceInfoResponse;
import com.vodafone.Response.IOTResponse;
import com.vodafone.Service.IOTDeviceInfoService;
import com.vodafone.Service.IOTFileService;




@RestController
@RequestMapping("/v1")
public class HomeController {
	@Autowired
	IOTFileService dataService;
	@Autowired
	IOTDeviceInfoService deviceInfoService;
	@PostMapping(path="/event",consumes = "application/json", produces = "application/json")
	public ResponseEntity<IOTResponse> loadCSVFile(@RequestBody CSVDetail csvDetail)
	{
		Optional<IOTResponse> resp = dataService.loadCSVFile(csvDetail.getFilepath(),csvDetail.getDelimiter());
		IOTResponse iotResponse=resp.get();
		
		if(iotResponse.getDescription().equals(IOTConstant.DATA_REFRESHED)) {
			return ResponseEntity.ok(iotResponse);
		}
		else if(iotResponse.getDescription().equals(IOTConstant.ERROR_EMPTY_FILE)) {
			return ResponseEntity.badRequest().body(iotResponse);
		}
		else if(iotResponse.getDescription().contains(IOTConstant.ERROR_TECHNICAL_EXCEP)) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(iotResponse);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(iotResponse);
	
	}

	@GetMapping(path="/event", produces = "application/json")
	public ResponseEntity<?> getDeviceInfo(@RequestParam(value="ProductId",required = true) String ProductId, @RequestParam(value="tstmp",required = false) Long tstmp )
	{
		Optional<?> resp = deviceInfoService.getDeviceInfo("WG11155638", 1580000000000L);
		
		if (resp.get() instanceof DeviceInfoResponse ){
			return ResponseEntity.ok(resp.get());
		}
		//if it is not a DeviceInfoResponse definitely it is IOTResponse
		
		IOTResponse iotResponse = (IOTResponse)resp.get();
		
		HttpStatus httpstatus = HttpStatus.NOT_FOUND;
		
		if(iotResponse.getDescription().equals(IOTConstant.ERROR_DEVICE_NOT_LOCATED)) {
			httpstatus = HttpStatus.BAD_REQUEST;
		}
		else if(iotResponse.getDescription().equals(IOTConstant.ERROR_DB_EMPTY)) {
			httpstatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		
		return ResponseEntity.status(httpstatus).body(iotResponse);

	}	

		
	}



