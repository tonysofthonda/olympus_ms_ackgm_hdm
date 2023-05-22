package com.honda.olympus.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.honda.olympus.utils.AckgmConstants;
import com.honda.olympus.vo.EventVO;
import com.honda.olympus.vo.MaxTransitCallVO;
import com.honda.olympus.vo.MaxTransitResponseVO;
import com.honda.olympus.vo.ResponseVO;

@Service
public class MaxTransitService {
	
	@Value("${max.transit.service.url}")
	private String maxTRansitURI;
	
	@Value("${service.name}")
	private String serviceName;
	
	@Value("${maxtransit.time.wait}")
	private Integer timeOut;
	
	@Autowired
	LogEventService logEventService;
	
	public List<MaxTransitResponseVO> generateCallMaxtransit(MaxTransitCallVO message) {

		List<MaxTransitResponseVO> maxTransitResponse = new ArrayList<>();
		
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());

			HttpEntity<MaxTransitCallVO> requestEntity = new HttpEntity<>(message, headers);

			ResponseEntity<List<MaxTransitResponseVO>> responseEntity = restTemplate.postForEntity(maxTRansitURI, requestEntity,
					List<MaxTransitResponseVO>.class);

			System.out.println("Notification sent with Status Code: " + responseEntity.getStatusCode());
			
			
			if(!responseEntity.getStatusCode().is2xxSuccessful()) {
				
				logEventService.sendLogEvent(
						new EventVO(serviceName, AckgmConstants.ZERO_STATUS, "La API de max transit retorno un error: "+responseEntity.getStatusCode(), ""));
				System.out.println("Error calling service, Timeout");
			}
			
			
			
			
			return maxTransitResponse;
		} catch(ResourceAccessException r) {
			
			logEventService.sendLogEvent(
					new EventVO(serviceName, AckgmConstants.ZERO_STATUS, "Tiempo de espera agotado en la consulta a la API MAX TRANSIT ubicada en: "+timeOut, ""));
			System.out.println("Error calling service, Timeout");
			maxTransitResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			
			return maxTransitResponse;
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error calling MaxTransit service");
			maxTransitResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			return maxTransitResponse;
		} 

	}
	
	
	
	private SimpleClientHttpRequestFactory getClientHttpRequestFactory() {

	    SimpleClientHttpRequestFactory clientHttpRequestFactory  = new SimpleClientHttpRequestFactory();
	    clientHttpRequestFactory.setConnectTimeout(timeOut);
	    clientHttpRequestFactory.setReadTimeout(timeOut);
	    return clientHttpRequestFactory;
	}

}