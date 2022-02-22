package com.vodafone.Service;

import java.util.Optional;

public interface IOTDeviceInfoService {

	Optional<?> getDeviceInfo(String productId, Long tstmp);

}
