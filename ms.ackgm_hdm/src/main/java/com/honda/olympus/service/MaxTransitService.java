package com.honda.olympus.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MaxTransitService {

	@Value("${service.urlmax}")
	private String maxTRansitURI;

	@Value("${service.name}")
	private String serviceName;

	@Value("${maxtransit.timewait}")
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

			ResponseEntity<List<MaxTransitResponseVO>> responseEntity = restTemplate.exchange(maxTRansitURI,
					HttpMethod.POST, requestEntity, new ParameterizedTypeReference<List<MaxTransitResponseVO>>() {
					});

			log.debug("Maxtransit request with Status Code: {}",responseEntity.getStatusCode());

			if (!responseEntity.getStatusCode().is2xxSuccessful()) {

				logEventService.sendLogEvent(new EventVO(serviceName, AckgmConstants.ZERO_STATUS,
						"La API de MAXTRANSIT retorno un error: " + responseEntity.getStatusCode(), ""));
				log.debug("Error calling MAXTRANSIT service");
			}

			return responseEntity.getBody();
		} catch (ResourceAccessException r) {

			logEventService.sendLogEvent(new EventVO(serviceName, AckgmConstants.ZERO_STATUS,
					"Tiempo de espera agotado en la consulta a la API MAXTRANSIT ubicada en: " + timeOut, ""));
			log.debug("Ackgm_hdm:: Error calling MAXTRANSIT service, Timeout");

			return maxTransitResponse;
		} catch (Exception e) {
			log.info("Ackgm_hdm:: Error calling MAXTRANSIT service");
			return maxTransitResponse;
		}

	}

	private SimpleClientHttpRequestFactory getClientHttpRequestFactory() {

		SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
		clientHttpRequestFactory.setConnectTimeout(timeOut);
		clientHttpRequestFactory.setReadTimeout(timeOut);
		return clientHttpRequestFactory;
	}

}
