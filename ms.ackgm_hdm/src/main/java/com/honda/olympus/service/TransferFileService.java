package com.honda.olympus.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.honda.olympus.vo.TransferFileVO;

@Service
public class TransferFileService {

	@Value("${transfer.file.service.url}")
	private String transferFileURI;

	public void sendTransferFileEvent(TransferFileVO message) {
		try {
			System.out.println("Calling transferFile service");
			System.out.println(message.toString());
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			RestTemplate restTemplate = new RestTemplate();

			HttpEntity<TransferFileVO> requestEntity = new HttpEntity<>(message, headers);

			ResponseEntity<String> responseEntity = restTemplate.postForEntity(transferFileURI, requestEntity,
					String.class);

			System.out.println("TRnasfer file called with Status Code: " + responseEntity.getStatusCode());
			System.out.println("Message: " + responseEntity.getBody());
		} catch (Exception e) {
			System.out.println("Error calling transferFile service " + e.getLocalizedMessage());
		}

	}

}
