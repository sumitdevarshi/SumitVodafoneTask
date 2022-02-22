package com.vodafone.Service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.vodafone.Constant.IOTConstant;
import com.vodafone.Model.DeviceDetails;
import com.vodafone.Repository.IOTRepository;
import com.vodafone.Response.IOTResponse;
import com.vodafone.Util.IOTUtil;

@Service
public class CSVFileService implements IOTFileService {
	
	@Autowired
	IOTRepository iotRepository;
	
	

	@Override
	public Optional<IOTResponse> loadCSVFile(String filepath, Character delimiter) {
		List<DeviceDetails> deviceDetailList = null;		
		
		if(IOTUtil.isValidDelimiter(delimiter) == false) {
			delimiter = ','; //very common delimiter of a csv file
		}
		
		IOTResponse resp = new IOTResponse();
		
		//file will be close automatically after use or in a exception
		try(Reader reader = new FileReader(filepath)){

			deviceDetailList = readAll(reader, delimiter);
			
			if(deviceDetailList == null || deviceDetailList.isEmpty()) {
				resp.setDescription(IOTConstant.ERROR_EMPTY_FILE);
				return Optional.of(resp);
			}	
		
		}catch (FileNotFoundException e) {
			resp.setDescription(IOTConstant.ERROR_FILE_NOT_FOUND);
			System.out.println("File - " + filepath + " not found - " + e.getMessage());
			return  Optional.of(resp);
		}catch(Exception e) {
			deviceDetailList = null;
			resp.setDescription(IOTConstant.ERROR_TECHNICAL_EXCEP + " - " + e.getMessage());
			System.out.println("Exception occured while processing file - " + filepath 
					+ " reason - " + e.getMessage());
			return Optional.of(resp);
		}
				
		if(iotRepository.saveOrUpdate(deviceDetailList) == 1) {
			resp.setDescription(IOTConstant.DATA_REFRESHED);
		}
		else {
			deviceDetailList = null;
			resp.setDescription("ERROR: A technical exception occurred - Failed to load DB"
					+ " - Check csv File and Delimiter");
			System.out.println("Exception occured while Loading records in DB - ");
			return Optional.of(resp);
		}
		
		
		return Optional.of(resp);
		
	}

	private List<DeviceDetails> readAll(Reader reader, char separator){
		
		HeaderColumnNameMappingStrategy<DeviceDetails> cpms = new HeaderColumnNameMappingStrategy<DeviceDetails>();
		cpms.setType(DeviceDetails.class);
		CsvToBean<DeviceDetails> csvToBean = new CsvToBeanBuilder<DeviceDetails>(reader)
				   .withSeparator(separator)
			       .withType(DeviceDetails.class)
			       .withMappingStrategy(cpms)
			       .build();
		return csvToBean.parse();
	}

}
