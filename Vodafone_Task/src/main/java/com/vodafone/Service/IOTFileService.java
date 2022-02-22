package com.vodafone.Service;

import java.util.Optional;

import com.vodafone.Response.IOTResponse;

public interface IOTFileService {

	Optional<IOTResponse> loadCSVFile(String filepath, Character delimiter);

}
